/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.extern.aws;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.aws.SignatureV4;
import org.adamalang.common.*;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.extern.Email;
import org.adamalang.web.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/** amazon simple email service */
public class SES implements Email {
  private static final Logger LOGGER = LoggerFactory.getLogger(SES.class);
  private final WebClientBase base;
  private final AWSConfig config;
  private final AWSMetrics metrics;

  public SES(WebClientBase base, AWSConfig config, AWSMetrics metrics) {
    this.base = base;
    this.config = config;
    this.metrics = metrics;
  }

  private boolean send(String email, String txtSubject, String textbody, String htmlbody) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.send_email.start();
    try {
      CountDownLatch latch = new CountDownLatch(1);
      {
        String url = "https://email." + config.region + ".amazonaws.com/v2/email/outbound-emails";
        HashMap<String, String> headers = new HashMap<>();
        final byte[] postBody;
        {
          ObjectNode request = Json.newJsonObject();
          request.put("FromEmailAddress", config.fromEmailAddressForInit);
          request.putArray("ReplyToAddresses").add(config.fromEmailAddressForInit);
          request.putObject("Destination").putArray("ToAddresses").add(email);
          ObjectNode content = request.putObject("Content").putObject("Simple");
          ObjectNode subject = content.putObject("Subject");
          subject.put("Data", txtSubject);
          subject.put("Charset", "UTF-8");
          ObjectNode body = content.putObject("Body");
          ObjectNode textBody = body.putObject("Text");
          textBody.put("Data", textbody);
          textBody.put("Charset", "UTF-8");
          ObjectNode htmlBody = body.putObject("Html");
          htmlBody.put("Data", htmlbody);
          htmlBody.put("Charset", "UTF-8");
          postBody = request.toString().getBytes(StandardCharsets.UTF_8);
        }
        String sha256 = Hex.of(Hashing.sha256().digest(postBody));

        new SignatureV4(config.credential, config.region, "ses", "POST", "email.us-east-2.amazonaws.com", "/v2/email/outbound-emails") //
            .withHeader("Content-Type", "application/json") //
            .withHeader("Content-Length", postBody.length + "") //
            .withContentHashSha256(sha256) //
            .signIntoHeaders(headers);

        base.execute(new SimpleHttpRequest("POST", url, headers, SimpleHttpRequestBody.WRAP(postBody)), new SimpleHttpResponder() {
          boolean responded = false;

          @Override
          public void start(SimpleHttpResponseHeader header) {
            if (!responded) {
              responded = true;
              if (header.status == 200) {
                instance.success();
              } else {
                instance.failure(ErrorCodes.AWS_EMAIL_SEND_FAILURE_NOT_200);
              }
              latch.countDown();
            }
          }

          @Override
          public void bodyStart(long size) {
          }

          @Override
          public void bodyFragment(byte[] chunk, int offset, int len) {
          }

          @Override
          public void bodyEnd() {
          }

          @Override
          public void failure(ErrorCodeException ex) {
            if (!responded) {
              responded = true;
              LOGGER.error("failed-sending-code", ex);
              instance.failure(ErrorCodes.AWS_EMAIL_SEND_FAILURE_EXCEPTION);
              latch.countDown();
            }
          }
        });
      }
      boolean result = AwaitHelper.block(latch, 5000);
      if (!result) {
        metrics.alarm_send_failures.up();
      }
      return result;
    } catch (Exception ex) {
      LOGGER.error("failed-sending-code-hard", ex);
      instance.failure(ErrorCodes.AWS_EMAIL_SEND_FAILURE_HARD_EXCEPTION);
      return false;
    }
  }

  @Override
  public boolean sendCode(String email, String code) {
    String bodyText = "You have requested access to Adama, and this requires a confirmation via this email. Copy and paste this code into the developer tooling:\n" + code;
    String bodyHtml = "<!doctype html><html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\"><head><title>Hello world</title><!--[if !mso]><!--><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><!--<![endif]--><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><style type=\"text/css\">#outlook a { padding:0; }\n" + "          body { margin:0;padding:0;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%; }\n" + "          table, td { border-collapse:collapse;mso-table-lspace:0pt;mso-table-rspace:0pt; }\n" + "          img { border:0;height:auto;line-height:100%; outline:none;text-decoration:none;-ms-interpolation-mode:bicubic; }\n" + "          p { display:block;margin:13px 0; }</style><!--[if mso]>\n" + "        <noscript>\n" + "        <xml>\n" + "        <o:OfficeDocumentSettings>\n" + "          <o:AllowPNG/>\n" + "          <o:PixelsPerInch>96</o:PixelsPerInch>\n" + "        </o:OfficeDocumentSettings>\n" + "        </xml>\n" + "        </noscript>\n" + "        <![endif]--><!--[if lte mso 11]>\n" + "        <style type=\"text/css\">\n" + "          .mj-outlook-group-fix { width:100% !important; }\n" + "        </style>\n" + "        <![endif]--><!--[if !mso]><!--><link href=\"https://fonts.googleapis.com/css?family=Roboto:300,500\" rel=\"stylesheet\" type=\"text/css\"><style type=\"text/css\">@import url(https://fonts.googleapis.com/css?family=Roboto:300,500);</style><!--<![endif]--><style type=\"text/css\">@media only screen and (min-width:480px) {\n" + "        .mj-column-per-40 { width:40% !important; max-width: 40%; }\n" + ".mj-column-per-100 { width:100% !important; max-width: 100%; }\n" + ".mj-column-per-50 { width:50% !important; max-width: 50%; }\n" + "      }</style><style media=\"screen and (min-width:480px)\">.moz-text-html .mj-column-per-40 { width:40% !important; max-width: 40%; }\n" + ".moz-text-html .mj-column-per-100 { width:100% !important; max-width: 100%; }\n" + ".moz-text-html .mj-column-per-50 { width:50% !important; max-width: 50%; }</style><style type=\"text/css\">@media only screen and (max-width:480px) {\n" + "      table.mj-full-width-mobile { width: 100% !important; }\n" + "      td.mj-full-width-mobile { width: auto !important; }\n" + "    }</style></head><body style=\"word-spacing:normal;\"><div><!--[if mso | IE]><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\" ><tr><td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\"><![endif]--><div style=\"margin:0px auto;max-width:600px;\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\"><tbody><tr><td style=\"direction:ltr;font-size:0px;padding:0px;text-align:center;\"><!--[if mso | IE]><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"\" style=\"vertical-align:top;width:240px;\" ><![endif]--><div class=\"mj-column-per-40 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\"><tbody><tr><td align=\"center\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"border-collapse:collapse;border-spacing:0px;\"><tbody><tr><td style=\"width:190px;\"><a href=\"https://www.adama-platform.com/\" target=\"_blank\"><img height=\"auto\" src=\"https://www.adama-platform.com/i/adama-moderate.png\" style=\"border:0;display:block;outline:none;text-decoration:none;height:auto;width:100%;font-size:13px;\" width=\"190\"></a></td></tr></tbody></table></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\" ><tr><td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\"><![endif]--><div style=\"margin:0px auto;max-width:600px;\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\"><tbody><tr><td style=\"direction:ltr;font-size:0px;padding:0px;text-align:center;\"><!--[if mso | IE]><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"\" style=\"vertical-align:top;width:600px;\" ><![endif]--><div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\"><tbody><tr><td align=\"center\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\"><div style=\"font-family:Roboto, Helvetica, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:center;color:#616161;\">Building a brighter future with Engineered Wealth</div></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\" ><tr><td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\"><![endif]--><div style=\"margin:0px auto;max-width:600px;\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\"><tbody><tr><td style=\"direction:ltr;font-size:0px;padding:0px;text-align:center;\"><!--[if mso | IE]><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"\" style=\"vertical-align:top;width:300px;\" ><![endif]--><div class=\"mj-column-per-50 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\"><tbody><tr><td align=\"center\" style=\"font-size:0px;padding:0px;word-break:break-word;\"><div style=\"font-family:Roboto, Helvetica, sans-serif;font-size:18px;font-weight:500;line-height:24px;text-align:center;color:#616161;\">Your access code for Adama Platform</div></td></tr><tr><td align=\"center\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\"><p style=\"border-top:solid 2px #616161;font-size:1px;margin:0px auto;width:100%;\"></p><!--[if mso | IE]><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-top:solid 2px #616161;font-size:1px;margin:0px auto;width:250px;\" role=\"presentation\" width=\"250px\" ><tr><td style=\"height:0;line-height:0;\"> &nbsp;\n" + "</td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\" ><tr><td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\"><![endif]--><div style=\"margin:0px auto;max-width:600px;\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\"><tbody><tr><td style=\"direction:ltr;font-size:0px;padding:0px;padding-top:30px;text-align:center;\"><!--[if mso | IE]><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"\" style=\"vertical-align:top;width:600px;\" ><![endif]--><div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\"><tbody><tr><td align=\"left\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\"><div style=\"font-family:Roboto, Helvetica, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#616161;\"><p>Hello there!</p><p>You have requested access to Adama, and this requires a confirmation via this email. Copy and paste this code into the developer tooling:</p></div></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\" ><tr><td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\"><![endif]--><div style=\"margin:0px auto;max-width:600px;\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\"><tbody><tr><td style=\"direction:ltr;font-size:0px;padding:0px;text-align:center;\"><!--[if mso | IE]><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"\" style=\"vertical-align:top;width:300px;\" ><![endif]--><div class=\"mj-column-per-50 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\"><tbody><tr><td align=\"center\" style=\"font-size:0px;padding:0px;word-break:break-word;\"><div style=\"font-family:Roboto, Helvetica, sans-serif;font-size:24px;font-weight:500;line-height:24px;text-align:center;color:#616161;\">" + code + "</div></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\" ><tr><td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\"><![endif]--><div style=\"margin:0px auto;max-width:600px;\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\"><tbody><tr><td style=\"direction:ltr;font-size:0px;padding:0px;padding-top:30px;text-align:center;\"><!--[if mso | IE]><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"\" style=\"vertical-align:top;width:600px;\" ><![endif]--><div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\"><tbody><tr><td align=\"left\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\"><div style=\"font-family:Roboto, Helvetica, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#616161;\"><p>Thank you again for using Adama Platform.</p></div></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></div></body></html>";
    return send(email, "Access code for Adama Platform", bodyText, bodyHtml);
  }

  @Override
  public boolean sendWelcome(String email) {
    String bodyHtml = "<!doctype html><html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\"><head><title>Hello world</title><!--[if !mso]><!--><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><!--<![endif]--><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><style type=\"text/css\">#outlook a { padding:0; }\n" + "          body { margin:0;padding:0;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%; }\n" + "          table, td { border-collapse:collapse;mso-table-lspace:0pt;mso-table-rspace:0pt; }\n" + "          img { border:0;height:auto;line-height:100%; outline:none;text-decoration:none;-ms-interpolation-mode:bicubic; }\n" + "          p { display:block;margin:13px 0; }</style><!--[if mso]>\n" + "        <noscript>\n" + "        <xml>\n" + "        <o:OfficeDocumentSettings>\n" + "          <o:AllowPNG/>\n" + "          <o:PixelsPerInch>96</o:PixelsPerInch>\n" + "        </o:OfficeDocumentSettings>\n" + "        </xml>\n" + "        </noscript>\n" + "        <![endif]--><!--[if lte mso 11]>\n" + "        <style type=\"text/css\">\n" + "          .mj-outlook-group-fix { width:100% !important; }\n" + "        </style>\n" + "        <![endif]--><!--[if !mso]><!--><link href=\"https://fonts.googleapis.com/css?family=Roboto:300,500\" rel=\"stylesheet\" type=\"text/css\"><style type=\"text/css\">@import url(https://fonts.googleapis.com/css?family=Roboto:300,500);</style><!--<![endif]--><style type=\"text/css\">@media only screen and (min-width:480px) {\n" + "        .mj-column-per-40 { width:40% !important; max-width: 40%; }\n" + ".mj-column-per-100 { width:100% !important; max-width: 100%; }\n" + ".mj-column-per-50 { width:50% !important; max-width: 50%; }\n" + "      }</style><style media=\"screen and (min-width:480px)\">.moz-text-html .mj-column-per-40 { width:40% !important; max-width: 40%; }\n" + ".moz-text-html .mj-column-per-100 { width:100% !important; max-width: 100%; }\n" + ".moz-text-html .mj-column-per-50 { width:50% !important; max-width: 50%; }</style><style type=\"text/css\">@media only screen and (max-width:480px) {\n" + "      table.mj-full-width-mobile { width: 100% !important; }\n" + "      td.mj-full-width-mobile { width: auto !important; }\n" + "    }</style></head><body style=\"word-spacing:normal;\"><div><!--[if mso | IE]><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\" ><tr><td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\"><![endif]--><div style=\"margin:0px auto;max-width:600px;\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\"><tbody><tr><td style=\"direction:ltr;font-size:0px;padding:0px;text-align:center;\"><!--[if mso | IE]><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"\" style=\"vertical-align:top;width:240px;\" ><![endif]--><div class=\"mj-column-per-40 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\"><tbody><tr><td align=\"center\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"border-collapse:collapse;border-spacing:0px;\"><tbody><tr><td style=\"width:190px;\"><a href=\"https://www.adama-platform.com/\" target=\"_blank\"><img height=\"auto\" src=\"https://www.adama-platform.com/i/adama-moderate.png\" style=\"border:0;display:block;outline:none;text-decoration:none;height:auto;width:100%;font-size:13px;\" width=\"190\"></a></td></tr></tbody></table></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\" ><tr><td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\"><![endif]--><div style=\"margin:0px auto;max-width:600px;\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\"><tbody><tr><td style=\"direction:ltr;font-size:0px;padding:0px;text-align:center;\"><!--[if mso | IE]><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"\" style=\"vertical-align:top;width:600px;\" ><![endif]--><div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\"><tbody><tr><td align=\"center\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\"><div style=\"font-family:Roboto, Helvetica, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:center;color:#616161;\">Building a brighter future with Engineered Wealth</div></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\" ><tr><td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\"><![endif]--><div style=\"margin:0px auto;max-width:600px;\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\"><tbody><tr><td style=\"direction:ltr;font-size:0px;padding:0px;text-align:center;\"><!--[if mso | IE]><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"\" style=\"vertical-align:top;width:300px;\" ><![endif]--><div class=\"mj-column-per-50 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\"><tbody><tr><td align=\"center\" style=\"font-size:0px;padding:0px;word-break:break-word;\"><div style=\"font-family:Roboto, Helvetica, sans-serif;font-size:18px;font-weight:500;line-height:24px;text-align:center;color:#616161;\">Welcome to the Adama Platform!</div></td></tr><tr><td align=\"center\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\"><p style=\"border-top:solid 2px #616161;font-size:1px;margin:0px auto;width:100%;\"></p><!--[if mso | IE]><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-top:solid 2px #616161;font-size:1px;margin:0px auto;width:250px;\" role=\"presentation\" width=\"250px\" ><tr><td style=\"height:0;line-height:0;\"> &nbsp;\n" + "</td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"\" style=\"width:600px;\" width=\"600\" ><tr><td style=\"line-height:0px;font-size:0px;mso-line-height-rule:exactly;\"><![endif]--><div style=\"margin:0px auto;max-width:600px;\"><table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"width:100%;\"><tbody><tr><td style=\"direction:ltr;font-size:0px;padding:0px;padding-top:30px;text-align:center;\"><!--[if mso | IE]><table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td class=\"\" style=\"vertical-align:top;width:600px;\" ><![endif]--><div class=\"mj-column-per-100 mj-outlook-group-fix\" style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"vertical-align:top;\" width=\"100%\"><tbody><tr><td align=\"left\" style=\"font-size:0px;padding:10px 25px;word-break:break-word;\"><div style=\"font-family:Roboto, Helvetica, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#616161;\"><p>Hello there!</p><p>I'm an automated email from the Adama Platform to welcome you to the platform. First, Thank you for signing up for the Adama Platform. Second, this is a research platform as a service for building Engineered Wealth such that your applications can last forever at a low cost. Now that you have given the system your email, please consult the book at: <a href=\"https://book.adama-platform.com/\" style=\"color: #3498DB;\">https://book.adama-platform.com/</a></p><p>And finally, if you don't feel welcome or view this transactional email as spam, then we are sorry. You can use the developer tooling to delete your account.</p></div></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></td></tr></tbody></table></div><!--[if mso | IE]></td></tr></table><![endif]--></div></body></html>";
    String bodyText = "Welcome to the Adama Platform!\n\nHello there!\nI'm an automated email from the Adama Platform to welcome you to the platform. First, Thank you for signing up for the Adama Platform. Second, this is a research platform as a service for building Engineered Wealth such that your applications can last forever at a low cost. Now that you have given the system your email, please consult the book at: https://book.adama-platform.com/";
    return send(email, "Welcome to the Adama Platform!", bodyText, bodyHtml);
  }
}

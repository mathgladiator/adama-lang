/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.extern.aws;

import org.adamalang.ErrorCodes;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.adamalang.extern.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

public class SES implements Email {
  private static final Logger LOGGER = LoggerFactory.getLogger(SES.class);
  private final AWSConfig config;
  private final AWSMetrics metrics;
  private final SesClient client;

  public SES(AWSConfig config, AWSMetrics metrics) {
    this.config = config;
    this.metrics = metrics;
    this.client = SesClient.builder().credentialsProvider(config).region(Region.of(config.region)).build();
  }

  @Override
  public boolean sendCode(String email, String code) {
    RequestResponseMonitor.RequestResponseMonitorInstance instance = metrics.send_email.start();
    try {
      SendEmailRequest.Builder builder = SendEmailRequest.builder();
      builder.source(config.fromEmailAddressForInit);
      builder.replyToAddresses(config.replyToEmailAddressForInit);
      builder.destination(Destination.builder().toAddresses(email).build());
      Content subject = Content.builder().data("Your Super Secret Code for Adama Platform").charset("UTF-8").build();
      Content text = Content.builder().data("Your code is " + code).charset("UTF-8").build();
      Body body = Body.builder().text(text).build();
      builder.message(Message.builder().subject(subject).body(body).build());
      client.sendEmail(builder.build());
      instance.success();
      return true;
    } catch (Exception ex) {
      LOGGER.error("failed-sending-code", ex);
      instance.failure(ErrorCodes.AWS_EMAIL_SEND_FAILURE);
      return false;
    }
  }
}

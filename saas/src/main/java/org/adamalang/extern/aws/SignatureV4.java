/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.extern.aws;

import org.adamalang.common.HMACSHA256;
import org.adamalang.common.Hashing;
import org.adamalang.common.Hex;
import org.adamalang.common.URL;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class SignatureV4 {
  // for thread safety, these must be local to signature
  private final AWSConfig config;
  private final String method;
  private final Instant now;
  private final TreeMap<String, String> headers;
  private final TreeMap<String, String> parameters;
  private final DateTimeFormatter date;
  private final DateTimeFormatter iso8601;
  private final String service;
  private String contentHashSha256;
  private final String path;

  public SignatureV4(AWSConfig config, String service, String method, String host, String path) {
    this.config = config;
    this.service = service;
    this.method = method;
    this.path = path;
    this.now = Instant.now();
    this.headers = new TreeMap<>();
    this.parameters = new TreeMap<>();
    this.date = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(java.time.ZoneOffset.UTC);
    this.iso8601 = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(java.time.ZoneOffset.UTC);
    this.contentHashSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
    headers.put("Host", host);
  }

  public void signIntoHeaders(Map<String, String> writeTo) {
    headers.put("X-Amz-Date", iso8601.format(now));
    writeTo.putAll(this.headers);
    Authorization authorization = new Authorization(false);
    writeTo.put("Authorization", "AWS4-HMAC-SHA256 Credential=" + config.accessKeyId + "/" + authorization.scope + ", SignedHeaders=" + authorization.signedHeaders + ", Signature=" + authorization.signature);
  }

  private class Authorization {
    public final String scope;
    public final String signedHeaders;
    public final String signature;

    public Authorization(boolean asQueryString) {
      this.scope = date.format(now) + "/" + config.region + "/" + service + "/aws4_request";
      { // get a list of the headers signed
        StringBuilder sb = new StringBuilder();
        boolean notFirst = false;
        for (String name : headers.keySet()) {
          if (notFirst) {
            sb.append(";");
          }
          notFirst = true;
          sb.append(name.toLowerCase(Locale.ENGLISH));
        }
        this.signedHeaders = sb.toString();
      }
      if (asQueryString) {
        parameters.put("X-Amz-Algorithm", "AWS4-HMAC-SHA256");
        parameters.put("X-Amz-Credential", config.accessKeyId + "/" + scope);
        parameters.put("X-Amz-Date", iso8601.format(now));
        parameters.put("X-Amz-Expires", "30");
        parameters.put("X-Amz-SignedHeaders", signedHeaders);
      }
      final String canonicalHeaders;
      {
        TreeMap<String, String> result = new TreeMap<>();
        Pattern nukeSpaces = Pattern.compile("\\s+");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
          String name = entry.getKey().toLowerCase(Locale.ENGLISH);
          result.put(name, name + ":" + nukeSpaces.matcher(entry.getValue()).replaceAll(" ") + "\n");
        }
        StringBuilder sb = new StringBuilder();
        for (String value : result.values()) {
          sb.append(value);
        }
        canonicalHeaders = sb.toString();
      }
      final String canonicalQuery;
      {
        StringBuilder sb = new StringBuilder();
        boolean notFirst = false;
        for (String name : parameters.keySet()) {
          if (notFirst) {
            sb.append("&");
          } else {
            notFirst = true;
          }
          sb.append(name).append("=").append(URL.encode(parameters.get(name), false));
        }
        canonicalQuery = sb.toString();
      }
      String canonicalResource = URL.encode(path, true);
      String canonicalRequest = method + "\n" + canonicalResource + "\n" + canonicalQuery + "\n" + canonicalHeaders + "\n" + signedHeaders + "\n" + contentHashSha256;
      final String canonicalRequestSha256;
      {
        canonicalRequestSha256 = Hex.of(Hashing.sha256().digest(canonicalRequest.getBytes(StandardCharsets.UTF_8)));
      }
      final String toSign = "AWS4-HMAC-SHA256" + "\n" + iso8601.format(now) + "\n" + scope + "\n" + canonicalRequestSha256;
      final byte[] kSecret = ("AWS4" + config.secretKey).getBytes(StandardCharsets.UTF_8);
      final byte[] kDate = HMACSHA256.of(kSecret, date.format(now));
      final byte[] kRegion = HMACSHA256.of(kDate, config.region);
      final byte[] kService = HMACSHA256.of(kRegion, service);
      final byte[] kSigning = HMACSHA256.of(kService, "aws4_request");
      this.signature = Hex.of(HMACSHA256.of(kSigning, toSign));
      if (asQueryString) {
        parameters.put("X-Amz-Signature", signature);
      }
    }
  }

  public String signedQuery() {
    Authorization authorization = new Authorization(true);
    return URL.parameters(parameters);
  }

  public SignatureV4 withHeader(String header, String value) {
    this.headers.put(header, value);
    return this;
  }

  public SignatureV4 withParameter(String key, String value) {
    this.parameters.put(key, value);
    return this;
  }

  public SignatureV4 withParameters(Map<String, String> parameters) {
    this.parameters.putAll(parameters);
    return this;
  }

  public SignatureV4 withEmptyBody() {
    return withContentHashSha256("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
  }

  public SignatureV4 withContentHashSha256(String contentHashSha256) {
    this.contentHashSha256 = contentHashSha256;
    headers.put("X-Amz-Content-Sha256", contentHashSha256);
    return this;
  }
}

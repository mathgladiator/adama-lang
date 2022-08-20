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

import org.adamalang.common.Hashing;
import org.adamalang.common.Hex;
import org.adamalang.web.client.FileReaderHttpRequestBody;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpRequestBody;

import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class S3SimpleHttpRequestBuilder {
  private final AWSConfig config;
  private final String host;
  private final String method;
  private final String s3key;
  private final TreeMap<String, String> headers;

  public S3SimpleHttpRequestBuilder(AWSConfig config, String method, String s3key) {
    this.config = config;
    this.host = "s3." + config.region + ".amazonaws.com";
    this.headers = new TreeMap<>();
    this.method = method;
    this.s3key = s3key;
  }

  public S3SimpleHttpRequestBuilder withContentType(String contentType) {
    this.headers.put("Content-Type", contentType);
    return this;
  }

  public SimpleHttpRequest buildWithEmptyBody() {
    startSigning().withEmptyBody().signInto(headers);
    return new SimpleHttpRequest(method, "https://" + host + "/" + config.bucket + "/" + s3key.replaceAll(Pattern.quote("#"), "%23"), headers, SimpleHttpRequestBody.EMPTY);
  }

  private SignatureV4 startSigning() {
    SignatureV4 v4 = new SignatureV4(config, "s3", method, host, "/" + config.bucket + "/" + s3key);
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      v4.withHeader(entry.getKey(), entry.getValue());
    }
    return v4;
  }

  public SimpleHttpRequest buildWithFileAsBody(FileReaderHttpRequestBody body) {
    startSigning().withContentHashSha256(body.sha256).withHeader("Content-Length", body.size + "").signInto(headers);
    return new SimpleHttpRequest(method, "https://" + host + "/" + config.bucket + "/" + s3key.replaceAll(Pattern.quote("#"), "%23"), headers, body);
  }

  public SimpleHttpRequest buildWithBytesAsBody(byte[] body) {
    startSigning().withContentHashSha256(Hex.of(Hashing.sha256().digest(body))).withHeader("Content-Length", body.length + "").signInto(headers);
    return new SimpleHttpRequest(method, "https://" + host + "/" + config.bucket + "/" + s3key.replaceAll(Pattern.quote("#"), "%23"), headers, SimpleHttpRequestBody.WRAP(body));
  }
}

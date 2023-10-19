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

import org.adamalang.aws.SignatureV4;
import org.adamalang.common.Hashing;
import org.adamalang.common.Hex;
import org.adamalang.common.URL;
import org.adamalang.web.client.FileReaderHttpRequestBody;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpRequestBody;

import java.util.Map;
import java.util.TreeMap;

/** a builder for talking to amazon S3 */
public class S3SimpleHttpRequestBuilder {
  private final AWSConfig config;
  private final String host;
  private final String method;
  private final String s3key;
  private final TreeMap<String, String> headers;
  private final TreeMap<String, String> parameters;
  private final String bucket;

  public S3SimpleHttpRequestBuilder(AWSConfig config, String bucket, String method, String s3key, TreeMap<String, String> parameters) {
    this.config = config;
    this.bucket = bucket;
    this.host = "s3." + config.region + ".amazonaws.com";
    this.headers = new TreeMap<>();
    this.method = method;
    this.s3key = s3key;
    this.parameters = parameters;
  }

  public S3SimpleHttpRequestBuilder withContentType(String contentType) {
    this.headers.put("Content-Type", contentType);
    return this;
  }

  public S3SimpleHttpRequestBuilder withContentMD5(String md5) {
    this.headers.put("Content-MD5", md5);
    this.headers.put("x-amz-meta-md5", md5);
    return this;
  }

  private SignatureV4 startSigning() {
    SignatureV4 v4 = new SignatureV4(config.credential, config.region, "s3", method, host, "/" + bucket + "/" + s3key);
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      v4.withHeader(entry.getKey(), entry.getValue());
    }
    if (parameters != null) {
      for (Map.Entry<String, String> param : parameters.entrySet()) {
        v4.withParameter(param.getKey(), param.getValue());
      }
    }
    return v4;
  }

  private String url() {
    return "https://" + host + "/" + bucket + "/" + URL.encode(s3key, true) + URL.parameters(parameters);
  }

  public SimpleHttpRequest buildWithEmptyBody() {
    startSigning().withEmptyBody().signIntoHeaders(headers);
    return new SimpleHttpRequest(method, url(), headers, SimpleHttpRequestBody.EMPTY);
  }

  public SimpleHttpRequest buildWithFileAsBody(FileReaderHttpRequestBody body) {
    startSigning().withContentHashSha256(body.sha256).withHeader("Content-Length", body.size + "").signIntoHeaders(headers);
    return new SimpleHttpRequest(method, url(), headers, body);
  }

  public SimpleHttpRequest buildWithBytesAsBody(byte[] body) {
    startSigning().withContentHashSha256(Hex.of(Hashing.sha256().digest(body))).withHeader("Content-Length", body.length + "").signIntoHeaders(headers);
    return new SimpleHttpRequest(method, url(), headers, SimpleHttpRequestBody.WRAP(body));
  }
}

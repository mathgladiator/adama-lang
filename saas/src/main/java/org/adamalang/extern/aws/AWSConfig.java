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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

public class AWSConfig implements AwsCredentialsProvider, AwsCredentials {
  private final String accessKeyId;
  private final String secretKey;
  public final String fromEmailAddressForInit;
  public final String replyToEmailAddressForInit;
  public final String region;
  public final String bucketForAssets;

  private String extractStr(ObjectNode root, String field, String message) throws Exception {
    JsonNode node = root.get(field);
    if (node == null || node.isNull() || !node.isTextual()) {
      throw new Exception(message);
    }
    return node.textValue();
  }

  public AWSConfig(ObjectNode node) throws Exception {
    this.accessKeyId = extractStr(node, "access_key", "AWS Access Key not found");
    this.secretKey = extractStr(node, "secret_key", "AWS Secret Key not found");
    this.region = extractStr(node, "region", "AWS Region");
    this.fromEmailAddressForInit = extractStr(node, "init_from_email", "No sender email address set");
    this.replyToEmailAddressForInit = extractStr(node, "init_replay_email", "No reply email address set");
    this.bucketForAssets = extractStr(node, "bucket", "No bucket for assets");
  }

  @Override
  public String accessKeyId() {
    return accessKeyId;
  }

  @Override
  public String secretAccessKey() {
    return secretKey;
  }

  @Override
  public AwsCredentials resolveCredentials() {
    return this;
  }
}

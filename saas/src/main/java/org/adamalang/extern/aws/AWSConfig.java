package org.adamalang.extern.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AWSConfig implements AWSCredentials, AWSCredentialsProvider {
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
  public String getAWSAccessKeyId() {
    return accessKeyId;
  }

  @Override
  public String getAWSSecretKey() {
    return secretKey;
  }

  @Override
  public AWSCredentials getCredentials() {
    return this;
  }

  @Override
  public void refresh() {

  }
}

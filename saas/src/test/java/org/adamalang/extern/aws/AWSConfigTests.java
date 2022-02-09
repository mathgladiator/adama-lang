package org.adamalang.extern.aws;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

public class AWSConfigTests {
  @Test
  public void coverage() throws Exception {
    ObjectNode aws = Json.newJsonObject();
    aws.put("access_key", "key");
    aws.put("secret_key", "secret");
    aws.put("init_from_email", "x@x.com");
    aws.put("init_reply_email", "x@x.com");
    aws.put("bucket", "bucket");
    aws.put("region", "region");
    AWSConfig config = new AWSConfig(new ConfigObject(aws));
    Assert.assertEquals("secret", config.secretAccessKey());
    Assert.assertEquals("key", config.accessKeyId());
    Assert.assertEquals(config, config.resolveCredentials());
  }
}

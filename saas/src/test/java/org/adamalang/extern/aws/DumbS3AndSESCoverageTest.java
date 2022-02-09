package org.adamalang.extern.aws;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class DumbS3AndSESCoverageTest {
  @Test
  public void coverage() throws Exception {
    ObjectNode aws = Json.newJsonObject();
    aws.put("access_key", "key");
    aws.put("secret_key", "secret");
    aws.put("init_from_email", "x@x.com");
    aws.put("init_reply_email", "x@x.com");
    aws.put("bucket", "bucket");
    aws.put("region", "us-west-2");
    AWSConfig config = new AWSConfig(new ConfigObject(aws));
    SES ses = new SES(config, new AWSMetrics(new NoOpMetricsFactory()));
    Assert.assertFalse(ses.sendCode("x@x.com", "123"));
    S3 s3 = new S3(config, new AWSMetrics(new NoOpMetricsFactory()));
    s3.upload(new Key("space", "key"), NtAsset.NOTHING, File.createTempFile("xxxxx", "y1234"), new Callback<Void>() {
      @Override
      public void success(Void value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.assertEquals(950322, ex.code);
      }
    });
  }
}

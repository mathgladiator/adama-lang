package org.adamalang.extern.aws;

import org.junit.Assert;
import org.junit.Test;

public class S3LogRegexTest {
  @Test
  public void validate() {
    Assert.assertTrue(S3.shouldConsiderForUpload("error.2022-07-24.0.log"));
    Assert.assertFalse(S3.shouldConsiderForUpload("error!2022-07-24!0!log"));
    Assert.assertTrue(S3.shouldConsiderForUpload("error.2022-07-25.10.log"));
    Assert.assertTrue(S3.shouldConsiderForUpload("access.2022-407-224.120.log"));
    Assert.assertFalse(S3.shouldConsiderForUpload("error.log"));
    Assert.assertFalse(S3.shouldConsiderForUpload("access.log"));
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
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

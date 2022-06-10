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
    aws.put("archive", "archive");
    AWSConfig config = new AWSConfig(new ConfigObject(aws));
    Assert.assertEquals("secret", config.secretAccessKey());
    Assert.assertEquals("key", config.accessKeyId());
    Assert.assertEquals(config, config.resolveCredentials());
  }
}

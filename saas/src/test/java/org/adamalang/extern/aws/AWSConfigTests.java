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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

public class AWSConfigTests {
  @Test
  public void coverage() throws Exception {
    ObjectNode aws = Json.newJsonObject();
    aws.put("access-key", "key");
    aws.put("secret-key", "secret");
    aws.put("init-from-email", "x@x.com");
    aws.put("init-reply-email", "x@x.com");
    aws.put("bucket", "bucket");
    aws.put("region", "region");
    aws.put("archive", "archive");
    aws.put("queue", "queue");
    AWSConfig config = new AWSConfig(new ConfigObject(aws));
    Assert.assertEquals("secret", config.credential.secretKey);
    Assert.assertEquals("key", config.credential.accessKeyId);
  }
}

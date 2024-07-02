/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

import org.junit.Assert;
import org.junit.Test;

public class S3XmlParsingTests {

  @Test
  public void listing() throws Exception {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<ListBucketResult xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\"><Name>bucket-name</Name><Prefix>assets/ide/test-sni-01/</Prefix><Marker></Marker><MaxKeys>1000</MaxKeys><IsTruncated>false</IsTruncated><Contents><Key>the-key</Key><LastModified>2022-09-06T01:55:29.000Z</LastModified><ETag>&quot;0e3a0c4b37f939cc5d74b83b83a0d933&quot;</ETag><Size>290577</Size><Owner><ID>d01ac567222c6b07202ce9103a0334fb61109b0a8072ab4bae248b6f32469a06</ID></Owner><StorageClass>STANDARD</StorageClass></Contents></ListBucketResult>";
    S3XmlParsing.ListResult result = S3XmlParsing.listResultOf(xml);
    Assert.assertEquals(1, result.keys.length);
    Assert.assertEquals("the-key", result.keys[0]);
    Assert.assertFalse(result.truncated);
    Assert.assertEquals("the-key", result.last());
  }
}

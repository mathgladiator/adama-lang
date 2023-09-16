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
package org.adamalang.web.service;

import org.junit.Assert;
import org.junit.Test;

public class SpaceKeyRequestTests {
  @Test
  public void flow() {
    SpaceKeyRequest req;
    Assert.assertNull(SpaceKeyRequest.parse("/xyz"));
    req = SpaceKeyRequest.parse("/xyz/key-1234/uri/tail-wtf");
    Assert.assertEquals("xyz", req.space);
    Assert.assertEquals("key-1234", req.key);
    Assert.assertEquals("/uri/tail-wtf", req.uri);
    req = SpaceKeyRequest.parse("/xyz/da-key");// implicit root
    Assert.assertEquals("xyz", req.space);
    Assert.assertEquals("da-key", req.key);
    Assert.assertEquals("/", req.uri);
  }
}

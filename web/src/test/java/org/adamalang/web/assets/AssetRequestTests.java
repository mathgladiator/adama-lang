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
package org.adamalang.web.assets;

import org.adamalang.runtime.delta.secure.SecureAssetUtil;
import org.junit.Assert;
import org.junit.Test;

public class AssetRequestTests {

  @Test
  public void flow() throws Exception {
    String assetKey = SecureAssetUtil.makeAssetKeyHeader();
    String uri = "space/key/id=" + SecureAssetUtil.encryptToBase64(SecureAssetUtil.secretKeyOf(assetKey), "my-id");
    AssetRequest ar = AssetRequest.parse(uri, assetKey);
    Assert.assertEquals("space", ar.space);
    Assert.assertEquals("key", ar.key);
    Assert.assertEquals("my-id", ar.id);
  }

  @Test
  public void flowWithSlash() throws Exception {
    String assetKey = SecureAssetUtil.makeAssetKeyHeader();
    String uri = "space/key/path/path2/id=" + SecureAssetUtil.encryptToBase64(SecureAssetUtil.secretKeyOf(assetKey), "my-id");
    AssetRequest ar = AssetRequest.parse(uri, assetKey);
    Assert.assertEquals("space", ar.space);
    Assert.assertEquals("key/path/path2", ar.key);
    Assert.assertEquals("my-id", ar.id);
  }

}

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
package org.adamalang.runtime.delta.secure;

import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

public class TestKey {
  public static final String COMMON_KEY = "DS9srRiyRay6yBJE8ONlT3XenV97g2GS";

  public static final AssetIdEncoder ENCODER = MAKE();

  private static AssetIdEncoder MAKE() {
    try {
      return new AssetIdEncoder(COMMON_KEY);
    } catch (ErrorCodeException ece) {
      throw new UnsupportedOperationException();
    }
  }

  @Test
  public void sanity() throws Exception {
    String header = SecureAssetUtil.makeAssetKeyHeader();
    AssetIdEncoder encoder = new AssetIdEncoder(header);
    Assert.assertEquals("123", SecureAssetUtil.decryptFromBase64(SecureAssetUtil.secretKeyOf(header), encoder.encrypt("123")));
  }
}

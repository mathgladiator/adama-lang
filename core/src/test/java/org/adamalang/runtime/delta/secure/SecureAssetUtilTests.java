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

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SecureAssetUtilTests {
  @Test
  public void assets() throws Exception {
    String str = SecureAssetUtil.makeAssetKeyHeader();
    SecretKey key = SecureAssetUtil.secretKeyOf(str);
    key.getEncoded();
    key.getAlgorithm();
    key.getFormat();
  }

  @Test
  public void coverage() {
    try {
      SecureAssetUtil.encryptToBase64(null, "x");
      Assert.fail();
    } catch (RuntimeException re) {

    }
    try {
      SecureAssetUtil.decryptFromBase64(null, "x");
      Assert.fail();
    } catch (RuntimeException re) {

    }
    try {
      SecureAssetUtil.decryptFromBase64(null, Base64.getEncoder().encodeToString("XYZ".getBytes(StandardCharsets.UTF_8)));
      Assert.fail();
    } catch (RuntimeException re) {

    }
    try {
      SecureAssetUtil.secretKeyOf(Base64.getEncoder().encodeToString("XYZ".getBytes(StandardCharsets.UTF_8)));
      Assert.fail();
    } catch (ErrorCodeException re) {
      Assert.assertEquals(144583, re.code);
    }
    try {
      SecureAssetUtil.getKeyGenerator("X");
      Assert.fail();
    } catch (RuntimeException re) {

    }
  }
}

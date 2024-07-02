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
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class HashingTests {
  @Test
  public void okMD5() {
    MessageDigest digest = Hashing.md5();
    digest.update("X".getBytes(StandardCharsets.UTF_8));
    Assert.assertEquals("AhKbuGEGHRoFLFkuLcazgw==", Hashing.finishAndEncode(digest));
  }

  @Test
  public void okSHA256() {
    MessageDigest digest = Hashing.sha256();
    digest.update("X".getBytes(StandardCharsets.UTF_8));
    Assert.assertEquals("S2irOEf+2n1sYsH7y+6/o16rc1HtXnj03a3qXfZLgBU=", Hashing.finishAndEncode(digest));
  }

  @Test
  public void okSHA256EmptyHex() {
    Assert.assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", Hex.of(Hashing.sha256().digest()));
  }

  @Test
  public void okSHA384() {
    MessageDigest digest = Hashing.sha384();
    digest.update("X".getBytes(StandardCharsets.UTF_8));
    Assert.assertEquals("dU/pvqqRu3rpi+5VFo4Wx7HzxapUzPg8KNszhGM8rOSGOb7ujNAF4+u2uV3UPJW3", Hashing.finishAndEncode(digest));
  }

  @Test
  public void fail() {
    try {
      Hashing.forKnownAlgorithm("SHA9000!");
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertEquals("java.security.NoSuchAlgorithmException: SHA9000! MessageDigest not available", ex.getMessage());
    }
  }
}

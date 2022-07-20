/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
      Assert.assertEquals(
          "java.security.NoSuchAlgorithmException: SHA9000! MessageDigest not available",
          ex.getMessage());
    }
  }
}

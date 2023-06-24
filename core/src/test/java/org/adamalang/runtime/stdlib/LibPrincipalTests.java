/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class LibPrincipalTests {
  @Test
  public void is() {
    NtPrincipal a = new NtPrincipal("jeff", "adama");
    Assert.assertTrue(LibPrincipal.isAdamaDeveloper(a));
    Assert.assertFalse(LibPrincipal.isAdamaDeveloper(NtPrincipal.NO_ONE));
    Assert.assertTrue(LibPrincipal.fromAuthority(a, "adama"));
    Assert.assertFalse(LibPrincipal.fromAuthority(a, "x"));
    Assert.assertTrue(LibPrincipal.isAnonymous(new NtPrincipal("agent", "anonymous")));
    Assert.assertFalse(LibPrincipal.isAnonymous(new NtPrincipal("agent", "adama")));
  }
}

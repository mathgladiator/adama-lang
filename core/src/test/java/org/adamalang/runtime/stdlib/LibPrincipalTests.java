/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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

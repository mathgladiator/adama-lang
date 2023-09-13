/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.stdlib;

import org.junit.Assert;
import org.junit.Test;

public class LibTemplatesTests {
  @Test
  public void eval_accountRecovery() {
    String result = LibTemplates.accountRecoveryDefaultEmail("MyNewPassword", "HTTP-XYZ", "HTTP-ABC");
    Assert.assertTrue(result.contains("MyNewPassword"));
    Assert.assertTrue(result.contains("HTTP-XYZ"));
    Assert.assertTrue(result.contains("HTTP-ABC"));
  }

  @Test
  public void eval_multilineEmailWithButton() {
    System.out.println(LibTemplates.multilineEmailWithButton("Reset your password", "An action was taken on your account. Here is a temporary password to access your account", "123223", "This code can be used to validate your identity", "/reset-password", "Use it", "This is part of the Adama Platform."));
  }
}

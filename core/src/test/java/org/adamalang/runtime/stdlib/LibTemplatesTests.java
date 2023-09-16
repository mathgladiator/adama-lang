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

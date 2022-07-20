/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.model;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.mysql.data.Role;
import org.junit.Assert;
import org.junit.Test;

public class RoleTests {
  @Test
  public void coverage() throws Exception {
    Assert.assertEquals(Role.Developer, Role.from("developer"));
    Assert.assertEquals(Role.None, Role.from("none"));
    try {
      Role.from("ninja-cake-master");
    } catch (ErrorCodeException ex) {
      Assert.assertEquals(688141, ex.code);
    }
  }
}

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

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
package org.adamalang.validators;

import org.adamalang.EndToEnd_SpaceInfoTests;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

public class ValidatePlanTests {
  @Test
  public void coverage() throws Exception {
    try {
      ValidatePlan.validate("space", Json.newJsonObject());
      Assert.fail();
    } catch (ErrorCodeException ece) {
      Assert.assertEquals(115788, ece.code);
    }
    ValidatePlan.validate("space", Json.parseJsonObject(EndToEnd_SpaceInfoTests.planFor("@static {}")));
  }
}

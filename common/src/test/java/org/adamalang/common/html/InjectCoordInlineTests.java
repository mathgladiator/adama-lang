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
package org.adamalang.common.html;

import org.junit.Assert;
import org.junit.Test;

public class InjectCoordInlineTests {
  @Test
  public void foo() {
    Assert.assertEquals("<element ln:ch=\"0;0;0;9;name\">", InjectCoordInline.execute("<element>", "name"));
    Assert.assertEquals("<element ln:ch=\"0;0;0;10;name\" />", InjectCoordInline.execute("<element/>", "name"));
    Assert.assertEquals("<element ln:ch=\"0;0;0;18;name\">", InjectCoordInline.execute("<element         >", "name"));
  }
}

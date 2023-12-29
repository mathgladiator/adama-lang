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
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class ColorUtilToolsTests {
  @Test
  public void coverage() {
    ColorUtilTools.lowerNoColor();
    Assert.assertEquals("\u001B[31mXYZ\u001B[0m", ColorUtilTools.prefix("XYZ", ANSI.Red));
    Assert.assertEquals("\u001B[1m\u001B[31mXYZ\u001B[0m", ColorUtilTools.prefixBold("XYZ", ANSI.Red));
    ColorUtilTools.setNoColor();
    Assert.assertEquals("XYZ", ColorUtilTools.prefix("XYZ", ANSI.Red));
    Assert.assertEquals("XYZ", ColorUtilTools.prefixBold("XYZ", ANSI.Red));
    Assert.assertEquals("xyz           ", ColorUtilTools.justifyLeft("xyz", 14));
    Assert.assertEquals("           xyz", ColorUtilTools.justifyRight("xyz", 14));
  }
}

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
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedGraphTests extends GeneratedBase {
  private String cached_Dupes_1 = null;
  private String get_Dupes_1() {
    if (cached_Dupes_1 != null) {
      return cached_Dupes_1;
    }
    cached_Dupes_1 = generateTestOutput(false, "Dupes_1", "./test_code/Graph_Dupes_failure.a");
    return cached_Dupes_1;
  }

  @Test
  public void testDupesFailure() {
    assertLiveFail(get_Dupes_1());
  }

  @Test
  public void testDupesNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_Dupes_1());
  }

  @Test
  public void testDupesExceptionFree() {
    assertExceptionFree(get_Dupes_1());
  }

  @Test
  public void testDupesTODOFree() {
    assertTODOFree(get_Dupes_1());
  }

  @Test
  public void stable_Dupes_1() {
    String live = get_Dupes_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Graph_Dupes_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":0,\"byte\":9},\"end\":{\"line\":1,\"character\":8,\"byte\":17}},\"severity\":1,\"source\":\"error\",\"message\":\"Assoc 'x' was already defined.\",\"file\":\"./test_code/Graph_Dupes_failure.a\"},{\"range\":{\"start\":{\"line\":4,\"character\":0,\"byte\":28},\"end\":{\"line\":4,\"character\":8,\"byte\":36}},\"severity\":1,\"source\":\"error\",\"message\":\"Graph 'g' was already defined.\",\"file\":\"./test_code/Graph_Dupes_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

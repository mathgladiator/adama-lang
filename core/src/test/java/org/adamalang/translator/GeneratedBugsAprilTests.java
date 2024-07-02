/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

public class GeneratedBugsAprilTests extends GeneratedBase {
  private String cached_Issues_1 = null;
  private String get_Issues_1() {
    if (cached_Issues_1 != null) {
      return cached_Issues_1;
    }
    cached_Issues_1 = generateTestOutput(false, "Issues_1", "./test_code/BugsApril_Issues_failure.a");
    return cached_Issues_1;
  }

  @Test
  public void testIssuesFailure() {
    assertLiveFail(get_Issues_1());
  }

  @Test
  public void testIssuesNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_Issues_1());
  }

  @Test
  public void testIssuesExceptionFree() {
    assertExceptionFree(get_Issues_1());
  }

  @Test
  public void testIssuesTODOFree() {
    assertTODOFree(get_Issues_1());
  }

  @Test
  public void stable_Issues_1() {
    String live = get_Issues_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BugsApril_Issues_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":3,\"character\":6,\"byte\":21},\"end\":{\"line\":3,\"character\":14,\"byte\":29}},\"severity\":1,\"source\":\"error\",\"message\":\"Expression expected to be computed, rather than assigned to\",\"file\":\"./test_code/BugsApril_Issues_failure.a\"},{\"range\":{\"start\":{\"line\":3,\"character\":6,\"byte\":21},\"end\":{\"line\":3,\"character\":14,\"byte\":29}},\"severity\":1,\"source\":\"error\",\"message\":\"Could not find a meaning for 'bool' + 'int'\",\"file\":\"./test_code/BugsApril_Issues_failure.a\"},{\"range\":{\"start\":{\"line\":4,\"character\":3,\"byte\":35},\"end\":{\"line\":4,\"character\":11,\"byte\":43}},\"severity\":1,\"source\":\"error\",\"message\":\"Expression expected to be computed, rather than assigned to\",\"file\":\"./test_code/BugsApril_Issues_failure.a\"},{\"range\":{\"start\":{\"line\":4,\"character\":3,\"byte\":35},\"end\":{\"line\":4,\"character\":11,\"byte\":43}},\"severity\":1,\"source\":\"error\",\"message\":\"Could not find a meaning for 'bool' + 'int'\",\"file\":\"./test_code/BugsApril_Issues_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

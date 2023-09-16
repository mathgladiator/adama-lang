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

public class GeneratedCrashReportTests extends GeneratedBase {
  private String cached_March21_1 = null;
  private String get_March21_1() {
    if (cached_March21_1 != null) {
      return cached_March21_1;
    }
    cached_March21_1 = generateTestOutput(false, "March21_1", "./test_code/CrashReport_March21_failure.a");
    return cached_March21_1;
  }

  @Test
  public void testMarch21Failure() {
    assertLiveFail(get_March21_1());
  }

  @Test
  public void testMarch21NotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_March21_1());
  }

  @Test
  public void testMarch21ExceptionFree() {
    assertExceptionFree(get_March21_1());
  }

  @Test
  public void testMarch21TODOFree() {
    assertTODOFree(get_March21_1());
  }

  @Test
  public void stable_March21_1() {
    String live = get_March21_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:CrashReport_March21_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":3,\"character\":2,\"byte\":74},\"end\":{\"line\":3,\"character\":14,\"byte\":86}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'maybe<String>' is using a type that was not found.\",\"file\":\"./test_code/CrashReport_March21_failure.a\"},{\"range\":{\"start\":{\"line\":2,\"character\":0,\"byte\":58},\"end\":{\"line\":3,\"character\":27,\"byte\":99}},\"severity\":1,\"source\":\"error\",\"message\":\"The field 'bound_type' has no type\",\"file\":\"./test_code/CrashReport_March21_failure.a\"},{\"range\":{\"start\":{\"line\":2,\"character\":0,\"byte\":58},\"end\":{\"line\":3,\"character\":27,\"byte\":99}},\"severity\":1,\"source\":\"error\",\"message\":\"Variable 'bound_type' has no backing type\",\"file\":\"./test_code/CrashReport_March21_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

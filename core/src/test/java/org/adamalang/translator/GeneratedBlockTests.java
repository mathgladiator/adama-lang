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

public class GeneratedBlockTests extends GeneratedBase {
  private String cached_DeadCode_1 = null;
  private String get_DeadCode_1() {
    if (cached_DeadCode_1 != null) {
      return cached_DeadCode_1;
    }
    cached_DeadCode_1 = generateTestOutput(false, "DeadCode_1", "./test_code/Block_DeadCode_failure.a");
    return cached_DeadCode_1;
  }

  @Test
  public void testDeadCodeFailure() {
    assertLiveFail(get_DeadCode_1());
  }

  @Test
  public void testDeadCodeNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DeadCode_1());
  }

  @Test
  public void testDeadCodeExceptionFree() {
    assertExceptionFree(get_DeadCode_1());
  }

  @Test
  public void testDeadCodeTODOFree() {
    assertTODOFree(get_DeadCode_1());
  }

  @Test
  public void stable_DeadCode_1() {
    String live = get_DeadCode_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Block_DeadCode_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":2,\"character\":2,\"byte\":25},\"end\":{\"line\":2,\"character\":14,\"byte\":37}},\"severity\":1,\"source\":\"error\",\"message\":\"This code is unreachable.\",\"file\":\"./test_code/Block_DeadCode_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DuplicateVariable_2 = null;
  private String get_DuplicateVariable_2() {
    if (cached_DuplicateVariable_2 != null) {
      return cached_DuplicateVariable_2;
    }
    cached_DuplicateVariable_2 = generateTestOutput(false, "DuplicateVariable_2", "./test_code/Block_DuplicateVariable_failure.a");
    return cached_DuplicateVariable_2;
  }

  @Test
  public void testDuplicateVariableFailure() {
    assertLiveFail(get_DuplicateVariable_2());
  }

  @Test
  public void testDuplicateVariableNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DuplicateVariable_2());
  }

  @Test
  public void testDuplicateVariableExceptionFree() {
    assertExceptionFree(get_DuplicateVariable_2());
  }

  @Test
  public void testDuplicateVariableTODOFree() {
    assertTODOFree(get_DuplicateVariable_2());
  }

  @Test
  public void stable_DuplicateVariable_2() {
    String live = get_DuplicateVariable_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Block_DuplicateVariable_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":2,\"character\":2,\"byte\":24},\"end\":{\"line\":2,\"character\":8,\"byte\":30}},\"severity\":1,\"source\":\"error\",\"message\":\"Variable 'x' was already defined\",\"file\":\"./test_code/Block_DuplicateVariable_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

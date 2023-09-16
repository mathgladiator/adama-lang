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

public class GeneratedMessagesTests extends GeneratedBase {
  private String cached_CantHaveRecords_1 = null;
  private String get_CantHaveRecords_1() {
    if (cached_CantHaveRecords_1 != null) {
      return cached_CantHaveRecords_1;
    }
    cached_CantHaveRecords_1 = generateTestOutput(false, "CantHaveRecords_1", "./test_code/Messages_CantHaveRecords_failure.a");
    return cached_CantHaveRecords_1;
  }

  @Test
  public void testCantHaveRecordsFailure() {
    assertLiveFail(get_CantHaveRecords_1());
  }

  @Test
  public void testCantHaveRecordsNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_CantHaveRecords_1());
  }

  @Test
  public void testCantHaveRecordsExceptionFree() {
    assertExceptionFree(get_CantHaveRecords_1());
  }

  @Test
  public void testCantHaveRecordsTODOFree() {
    assertTODOFree(get_CantHaveRecords_1());
  }

  @Test
  public void stable_CantHaveRecords_1() {
    String live = get_CantHaveRecords_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Messages_CantHaveRecords_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":4,\"character\":0,\"byte\":23},\"end\":{\"line\":6,\"character\":1,\"byte\":43}},\"severity\":1,\"source\":\"error\",\"message\":\"Messages can't have a field type of 'R'\",\"file\":\"./test_code/Messages_CantHaveRecords_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_CantHaveTables_2 = null;
  private String get_CantHaveTables_2() {
    if (cached_CantHaveTables_2 != null) {
      return cached_CantHaveTables_2;
    }
    cached_CantHaveTables_2 = generateTestOutput(false, "CantHaveTables_2", "./test_code/Messages_CantHaveTables_failure.a");
    return cached_CantHaveTables_2;
  }

  @Test
  public void testCantHaveTablesFailure() {
    assertLiveFail(get_CantHaveTables_2());
  }

  @Test
  public void testCantHaveTablesNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_CantHaveTables_2());
  }

  @Test
  public void testCantHaveTablesExceptionFree() {
    assertExceptionFree(get_CantHaveTables_2());
  }

  @Test
  public void testCantHaveTablesTODOFree() {
    assertTODOFree(get_CantHaveTables_2());
  }

  @Test
  public void stable_CantHaveTables_2() {
    String live = get_CantHaveTables_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Messages_CantHaveTables_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":2,\"character\":1,\"byte\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'message', but got a type of 'R'.\",\"file\":\"./test_code/Messages_CantHaveTables_failure.a\"},{\"range\":{\"start\":{\"line\":8,\"character\":0,\"byte\":47},\"end\":{\"line\":11,\"character\":1,\"byte\":94}},\"severity\":1,\"source\":\"error\",\"message\":\"Messages can't have a field type of 'table<R>'\",\"file\":\"./test_code/Messages_CantHaveTables_failure.a\"},{\"range\":{\"start\":{\"line\":8,\"character\":0,\"byte\":47},\"end\":{\"line\":11,\"character\":1,\"byte\":94}},\"severity\":1,\"source\":\"error\",\"message\":\"Messages can't have a field type of 'table<T>'\",\"file\":\"./test_code/Messages_CantHaveTables_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

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

public class GeneratedReadOnlyTests extends GeneratedBase {
  private String cached_FutureResultReadOnly_1 = null;
  private String get_FutureResultReadOnly_1() {
    if (cached_FutureResultReadOnly_1 != null) {
      return cached_FutureResultReadOnly_1;
    }
    cached_FutureResultReadOnly_1 = generateTestOutput(false, "FutureResultReadOnly_1", "./test_code/ReadOnly_FutureResultReadOnly_failure.a");
    return cached_FutureResultReadOnly_1;
  }

  @Test
  public void testFutureResultReadOnlyFailure() {
    assertLiveFail(get_FutureResultReadOnly_1());
  }

  @Test
  public void testFutureResultReadOnlyNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_FutureResultReadOnly_1());
  }

  @Test
  public void testFutureResultReadOnlyExceptionFree() {
    assertExceptionFree(get_FutureResultReadOnly_1());
  }

  @Test
  public void testFutureResultReadOnlyTODOFree() {
    assertTODOFree(get_FutureResultReadOnly_1());
  }

  @Test
  public void stable_FutureResultReadOnly_1() {
    String live = get_FutureResultReadOnly_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_FutureResultReadOnly_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":8,\"character\":2,\"byte\":88},\"end\":{\"line\":8,\"character\":3,\"byte\":89}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'x' is readonly\",\"file\":\"./test_code/ReadOnly_FutureResultReadOnly_failure.a\"},{\"range\":{\"start\":{\"line\":8,\"character\":2,\"byte\":88},\"end\":{\"line\":8,\"character\":5,\"byte\":91}},\"severity\":1,\"source\":\"error\",\"message\":\"The field 'x' is on a readonly message\",\"file\":\"./test_code/ReadOnly_FutureResultReadOnly_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_ProcedureArgs_2 = null;
  private String get_ProcedureArgs_2() {
    if (cached_ProcedureArgs_2 != null) {
      return cached_ProcedureArgs_2;
    }
    cached_ProcedureArgs_2 = generateTestOutput(false, "ProcedureArgs_2", "./test_code/ReadOnly_ProcedureArgs_failure.a");
    return cached_ProcedureArgs_2;
  }

  @Test
  public void testProcedureArgsFailure() {
    assertLiveFail(get_ProcedureArgs_2());
  }

  @Test
  public void testProcedureArgsNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_ProcedureArgs_2());
  }

  @Test
  public void testProcedureArgsExceptionFree() {
    assertExceptionFree(get_ProcedureArgs_2());
  }

  @Test
  public void testProcedureArgsTODOFree() {
    assertTODOFree(get_ProcedureArgs_2());
  }

  @Test
  public void stable_ProcedureArgs_2() {
    String live = get_ProcedureArgs_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_ProcedureArgs_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":5,\"character\":2,\"byte\":47},\"end\":{\"line\":5,\"character\":3,\"byte\":48}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'x' is readonly\",\"file\":\"./test_code/ReadOnly_ProcedureArgs_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_RecordID_3 = null;
  private String get_RecordID_3() {
    if (cached_RecordID_3 != null) {
      return cached_RecordID_3;
    }
    cached_RecordID_3 = generateTestOutput(false, "RecordID_3", "./test_code/ReadOnly_RecordID_failure.a");
    return cached_RecordID_3;
  }

  @Test
  public void testRecordIDFailure() {
    assertLiveFail(get_RecordID_3());
  }

  @Test
  public void testRecordIDNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_RecordID_3());
  }

  @Test
  public void testRecordIDExceptionFree() {
    assertExceptionFree(get_RecordID_3());
  }

  @Test
  public void testRecordIDTODOFree() {
    assertTODOFree(get_RecordID_3());
  }

  @Test
  public void stable_RecordID_3() {
    String live = get_RecordID_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_RecordID_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":7,\"character\":2,\"byte\":45},\"end\":{\"line\":7,\"character\":6,\"byte\":49}},\"severity\":1,\"source\":\"error\",\"message\":\"'id' is a special readonly field\",\"file\":\"./test_code/ReadOnly_RecordID_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_RootFieldWrite_4 = null;
  private String get_RootFieldWrite_4() {
    if (cached_RootFieldWrite_4 != null) {
      return cached_RootFieldWrite_4;
    }
    cached_RootFieldWrite_4 = generateTestOutput(false, "RootFieldWrite_4", "./test_code/ReadOnly_RootFieldWrite_failure.a");
    return cached_RootFieldWrite_4;
  }

  @Test
  public void testRootFieldWriteFailure() {
    assertLiveFail(get_RootFieldWrite_4());
  }

  @Test
  public void testRootFieldWriteNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_RootFieldWrite_4());
  }

  @Test
  public void testRootFieldWriteExceptionFree() {
    assertExceptionFree(get_RootFieldWrite_4());
  }

  @Test
  public void testRootFieldWriteTODOFree() {
    assertTODOFree(get_RootFieldWrite_4());
  }

  @Test
  public void stable_RootFieldWrite_4() {
    String live = get_RootFieldWrite_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_RootFieldWrite_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":16,\"byte\":16},\"end\":{\"line\":3,\"character\":17,\"byte\":60}},\"severity\":1,\"source\":\"error\",\"message\":\"'r<string>' is unable to set 'string' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_RootFieldWrite_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_SimpleBool_5 = null;
  private String get_SimpleBool_5() {
    if (cached_SimpleBool_5 != null) {
      return cached_SimpleBool_5;
    }
    cached_SimpleBool_5 = generateTestOutput(false, "SimpleBool_5", "./test_code/ReadOnly_SimpleBool_failure.a");
    return cached_SimpleBool_5;
  }

  @Test
  public void testSimpleBoolFailure() {
    assertLiveFail(get_SimpleBool_5());
  }

  @Test
  public void testSimpleBoolNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_SimpleBool_5());
  }

  @Test
  public void testSimpleBoolExceptionFree() {
    assertExceptionFree(get_SimpleBool_5());
  }

  @Test
  public void testSimpleBoolTODOFree() {
    assertTODOFree(get_SimpleBool_5());
  }

  @Test
  public void stable_SimpleBool_5() {
    String live = get_SimpleBool_5();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_SimpleBool_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":4,\"character\":2,\"byte\":57},\"end\":{\"line\":4,\"character\":3,\"byte\":58}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'y' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleBool_failure.a\"},{\"range\":{\"start\":{\"line\":3,\"character\":11,\"byte\":42},\"end\":{\"line\":4,\"character\":10,\"byte\":65}},\"severity\":1,\"source\":\"error\",\"message\":\"'bool' is unable to set 'bool' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleBool_failure.a\"},{\"range\":{\"start\":{\"line\":6,\"character\":2,\"byte\":85},\"end\":{\"line\":6,\"character\":3,\"byte\":86}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'z' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleBool_failure.a\"},{\"range\":{\"start\":{\"line\":5,\"character\":2,\"byte\":69},\"end\":{\"line\":6,\"character\":11,\"byte\":94}},\"severity\":1,\"source\":\"error\",\"message\":\"'bool' is unable to set 'bool' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleBool_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_SimpleClient_6 = null;
  private String get_SimpleClient_6() {
    if (cached_SimpleClient_6 != null) {
      return cached_SimpleClient_6;
    }
    cached_SimpleClient_6 = generateTestOutput(false, "SimpleClient_6", "./test_code/ReadOnly_SimpleClient_failure.a");
    return cached_SimpleClient_6;
  }

  @Test
  public void testSimpleClientFailure() {
    assertLiveFail(get_SimpleClient_6());
  }

  @Test
  public void testSimpleClientNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_SimpleClient_6());
  }

  @Test
  public void testSimpleClientExceptionFree() {
    assertExceptionFree(get_SimpleClient_6());
  }

  @Test
  public void testSimpleClientTODOFree() {
    assertTODOFree(get_SimpleClient_6());
  }

  @Test
  public void stable_SimpleClient_6() {
    String live = get_SimpleClient_6();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_SimpleClient_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":3,\"character\":2,\"byte\":50},\"end\":{\"line\":3,\"character\":3,\"byte\":51}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'y' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleClient_failure.a\"},{\"range\":{\"start\":{\"line\":2,\"character\":11,\"byte\":25},\"end\":{\"line\":3,\"character\":13,\"byte\":61}},\"severity\":1,\"source\":\"error\",\"message\":\"'principal' is unable to set 'principal' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleClient_failure.a\"},{\"range\":{\"start\":{\"line\":5,\"character\":2,\"byte\":84},\"end\":{\"line\":5,\"character\":3,\"byte\":85}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'z' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleClient_failure.a\"},{\"range\":{\"start\":{\"line\":4,\"character\":2,\"byte\":65},\"end\":{\"line\":5,\"character\":13,\"byte\":95}},\"severity\":1,\"source\":\"error\",\"message\":\"'principal' is unable to set 'principal' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleClient_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_SimpleDouble_7 = null;
  private String get_SimpleDouble_7() {
    if (cached_SimpleDouble_7 != null) {
      return cached_SimpleDouble_7;
    }
    cached_SimpleDouble_7 = generateTestOutput(false, "SimpleDouble_7", "./test_code/ReadOnly_SimpleDouble_failure.a");
    return cached_SimpleDouble_7;
  }

  @Test
  public void testSimpleDoubleFailure() {
    assertLiveFail(get_SimpleDouble_7());
  }

  @Test
  public void testSimpleDoubleNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_SimpleDouble_7());
  }

  @Test
  public void testSimpleDoubleExceptionFree() {
    assertExceptionFree(get_SimpleDouble_7());
  }

  @Test
  public void testSimpleDoubleTODOFree() {
    assertTODOFree(get_SimpleDouble_7());
  }

  @Test
  public void stable_SimpleDouble_7() {
    String live = get_SimpleDouble_7();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_SimpleDouble_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":3,\"character\":2,\"byte\":44},\"end\":{\"line\":3,\"character\":3,\"byte\":45}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'y' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleDouble_failure.a\"},{\"range\":{\"start\":{\"line\":2,\"character\":11,\"byte\":25},\"end\":{\"line\":3,\"character\":10,\"byte\":52}},\"severity\":1,\"source\":\"error\",\"message\":\"'double' is unable to set 'double' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleDouble_failure.a\"},{\"range\":{\"start\":{\"line\":5,\"character\":2,\"byte\":72},\"end\":{\"line\":5,\"character\":3,\"byte\":73}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'z' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleDouble_failure.a\"},{\"range\":{\"start\":{\"line\":4,\"character\":2,\"byte\":56},\"end\":{\"line\":5,\"character\":10,\"byte\":80}},\"severity\":1,\"source\":\"error\",\"message\":\"'double' is unable to set 'double' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleDouble_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_SimpleEnum_8 = null;
  private String get_SimpleEnum_8() {
    if (cached_SimpleEnum_8 != null) {
      return cached_SimpleEnum_8;
    }
    cached_SimpleEnum_8 = generateTestOutput(false, "SimpleEnum_8", "./test_code/ReadOnly_SimpleEnum_failure.a");
    return cached_SimpleEnum_8;
  }

  @Test
  public void testSimpleEnumFailure() {
    assertLiveFail(get_SimpleEnum_8());
  }

  @Test
  public void testSimpleEnumNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_SimpleEnum_8());
  }

  @Test
  public void testSimpleEnumExceptionFree() {
    assertExceptionFree(get_SimpleEnum_8());
  }

  @Test
  public void testSimpleEnumTODOFree() {
    assertTODOFree(get_SimpleEnum_8());
  }

  @Test
  public void stable_SimpleEnum_8() {
    String live = get_SimpleEnum_8();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_SimpleEnum_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":4,\"character\":2,\"byte\":64},\"end\":{\"line\":4,\"character\":3,\"byte\":65}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'y' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleEnum_failure.a\"},{\"range\":{\"start\":{\"line\":3,\"character\":11,\"byte\":50},\"end\":{\"line\":4,\"character\":10,\"byte\":72}},\"severity\":1,\"source\":\"error\",\"message\":\"'E' is unable to set 'E' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleEnum_failure.a\"},{\"range\":{\"start\":{\"line\":6,\"character\":2,\"byte\":92},\"end\":{\"line\":6,\"character\":3,\"byte\":93}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'z' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleEnum_failure.a\"},{\"range\":{\"start\":{\"line\":5,\"character\":2,\"byte\":76},\"end\":{\"line\":6,\"character\":10,\"byte\":100}},\"severity\":1,\"source\":\"error\",\"message\":\"'E' is unable to set 'E' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleEnum_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_SimpleInteger_9 = null;
  private String get_SimpleInteger_9() {
    if (cached_SimpleInteger_9 != null) {
      return cached_SimpleInteger_9;
    }
    cached_SimpleInteger_9 = generateTestOutput(false, "SimpleInteger_9", "./test_code/ReadOnly_SimpleInteger_failure.a");
    return cached_SimpleInteger_9;
  }

  @Test
  public void testSimpleIntegerFailure() {
    assertLiveFail(get_SimpleInteger_9());
  }

  @Test
  public void testSimpleIntegerNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_SimpleInteger_9());
  }

  @Test
  public void testSimpleIntegerExceptionFree() {
    assertExceptionFree(get_SimpleInteger_9());
  }

  @Test
  public void testSimpleIntegerTODOFree() {
    assertTODOFree(get_SimpleInteger_9());
  }

  @Test
  public void stable_SimpleInteger_9() {
    String live = get_SimpleInteger_9();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_SimpleInteger_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":3,\"character\":2,\"byte\":38},\"end\":{\"line\":3,\"character\":3,\"byte\":39}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'y' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleInteger_failure.a\"},{\"range\":{\"start\":{\"line\":2,\"character\":11,\"byte\":25},\"end\":{\"line\":3,\"character\":7,\"byte\":43}},\"severity\":1,\"source\":\"error\",\"message\":\"'int' is unable to set 'int' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleInteger_failure.a\"},{\"range\":{\"start\":{\"line\":5,\"character\":2,\"byte\":60},\"end\":{\"line\":5,\"character\":3,\"byte\":61}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'z' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleInteger_failure.a\"},{\"range\":{\"start\":{\"line\":4,\"character\":2,\"byte\":47},\"end\":{\"line\":5,\"character\":7,\"byte\":65}},\"severity\":1,\"source\":\"error\",\"message\":\"'int' is unable to set 'int' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleInteger_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_SimpleLong_10 = null;
  private String get_SimpleLong_10() {
    if (cached_SimpleLong_10 != null) {
      return cached_SimpleLong_10;
    }
    cached_SimpleLong_10 = generateTestOutput(false, "SimpleLong_10", "./test_code/ReadOnly_SimpleLong_failure.a");
    return cached_SimpleLong_10;
  }

  @Test
  public void testSimpleLongFailure() {
    assertLiveFail(get_SimpleLong_10());
  }

  @Test
  public void testSimpleLongNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_SimpleLong_10());
  }

  @Test
  public void testSimpleLongExceptionFree() {
    assertExceptionFree(get_SimpleLong_10());
  }

  @Test
  public void testSimpleLongTODOFree() {
    assertTODOFree(get_SimpleLong_10());
  }

  @Test
  public void stable_SimpleLong_10() {
    String live = get_SimpleLong_10();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_SimpleLong_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":3,\"character\":2,\"byte\":39},\"end\":{\"line\":3,\"character\":3,\"byte\":40}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'y' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleLong_failure.a\"},{\"range\":{\"start\":{\"line\":2,\"character\":11,\"byte\":25},\"end\":{\"line\":3,\"character\":7,\"byte\":44}},\"severity\":1,\"source\":\"error\",\"message\":\"'long' is unable to set 'int' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleLong_failure.a\"},{\"range\":{\"start\":{\"line\":5,\"character\":2,\"byte\":62},\"end\":{\"line\":5,\"character\":3,\"byte\":63}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'z' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleLong_failure.a\"},{\"range\":{\"start\":{\"line\":4,\"character\":2,\"byte\":48},\"end\":{\"line\":5,\"character\":7,\"byte\":67}},\"severity\":1,\"source\":\"error\",\"message\":\"'long' is unable to set 'int' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleLong_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_SimpleMaybe_11 = null;
  private String get_SimpleMaybe_11() {
    if (cached_SimpleMaybe_11 != null) {
      return cached_SimpleMaybe_11;
    }
    cached_SimpleMaybe_11 = generateTestOutput(false, "SimpleMaybe_11", "./test_code/ReadOnly_SimpleMaybe_failure.a");
    return cached_SimpleMaybe_11;
  }

  @Test
  public void testSimpleMaybeFailure() {
    assertLiveFail(get_SimpleMaybe_11());
  }

  @Test
  public void testSimpleMaybeNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_SimpleMaybe_11());
  }

  @Test
  public void testSimpleMaybeExceptionFree() {
    assertExceptionFree(get_SimpleMaybe_11());
  }

  @Test
  public void testSimpleMaybeTODOFree() {
    assertTODOFree(get_SimpleMaybe_11());
  }

  @Test
  public void stable_SimpleMaybe_11() {
    String live = get_SimpleMaybe_11();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_SimpleMaybe_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":3,\"character\":2,\"byte\":47},\"end\":{\"line\":3,\"character\":3,\"byte\":48}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'x' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleMaybe_failure.a\"},{\"range\":{\"start\":{\"line\":2,\"character\":11,\"byte\":25},\"end\":{\"line\":3,\"character\":8,\"byte\":53}},\"severity\":1,\"source\":\"error\",\"message\":\"'maybe<int>' is unable to set 'int' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleMaybe_failure.a\"},{\"range\":{\"start\":{\"line\":5,\"character\":2,\"byte\":80},\"end\":{\"line\":5,\"character\":3,\"byte\":81}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'y' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleMaybe_failure.a\"},{\"range\":{\"start\":{\"line\":4,\"character\":2,\"byte\":57},\"end\":{\"line\":5,\"character\":8,\"byte\":86}},\"severity\":1,\"source\":\"error\",\"message\":\"'maybe<int>' is unable to set 'int' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleMaybe_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_SimpleString_12 = null;
  private String get_SimpleString_12() {
    if (cached_SimpleString_12 != null) {
      return cached_SimpleString_12;
    }
    cached_SimpleString_12 = generateTestOutput(false, "SimpleString_12", "./test_code/ReadOnly_SimpleString_failure.a");
    return cached_SimpleString_12;
  }

  @Test
  public void testSimpleStringFailure() {
    assertLiveFail(get_SimpleString_12());
  }

  @Test
  public void testSimpleStringNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_SimpleString_12());
  }

  @Test
  public void testSimpleStringExceptionFree() {
    assertExceptionFree(get_SimpleString_12());
  }

  @Test
  public void testSimpleStringTODOFree() {
    assertTODOFree(get_SimpleString_12());
  }

  @Test
  public void stable_SimpleString_12() {
    String live = get_SimpleString_12();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_SimpleString_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":3,\"character\":2,\"byte\":43},\"end\":{\"line\":3,\"character\":3,\"byte\":44}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'y' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleString_failure.a\"},{\"range\":{\"start\":{\"line\":2,\"character\":11,\"byte\":25},\"end\":{\"line\":3,\"character\":9,\"byte\":50}},\"severity\":1,\"source\":\"error\",\"message\":\"'string' is unable to set 'string' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleString_failure.a\"},{\"range\":{\"start\":{\"line\":5,\"character\":2,\"byte\":69},\"end\":{\"line\":5,\"character\":3,\"byte\":70}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'z' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleString_failure.a\"},{\"range\":{\"start\":{\"line\":4,\"character\":2,\"byte\":54},\"end\":{\"line\":5,\"character\":9,\"byte\":76}},\"severity\":1,\"source\":\"error\",\"message\":\"'string' is unable to set 'string' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleString_failure.a\"},{\"range\":{\"start\":{\"line\":7,\"character\":2,\"byte\":105},\"end\":{\"line\":7,\"character\":3,\"byte\":106}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'k' is readonly\",\"file\":\"./test_code/ReadOnly_SimpleString_failure.a\"},{\"range\":{\"start\":{\"line\":2,\"character\":11,\"byte\":25},\"end\":{\"line\":6,\"character\":24,\"byte\":102}},\"severity\":1,\"source\":\"error\",\"message\":\"'string' is unable to set 'string' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_SimpleString_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_Simples_13 = null;
  private String get_Simples_13() {
    if (cached_Simples_13 != null) {
      return cached_Simples_13;
    }
    cached_Simples_13 = generateTestOutput(true, "Simples_13", "./test_code/ReadOnly_Simples_success.a");
    return cached_Simples_13;
  }

  @Test
  public void testSimplesEmission() {
    assertEmissionGood(get_Simples_13());
  }

  @Test
  public void testSimplesSuccess() {
    assertLivePass(get_Simples_13());
  }

  @Test
  public void testSimplesNoFormatException() {
    assertNoFormatException(get_Simples_13());
  }

  @Test
  public void testSimplesGoodWillHappy() {
    assertGoodWillHappy(get_Simples_13());
  }

  @Test
  public void testSimplesExceptionFree() {
    assertExceptionFree(get_Simples_13());
  }

  @Test
  public void testSimplesTODOFree() {
    assertTODOFree(get_Simples_13());
  }

  @Test
  public void stable_Simples_13() {
    String live = get_Simples_13();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_Simples_success.a");
    gold.append("\n--EMISSION-----------------------------------------");
    gold.append("\nEmission Success, Yay");
    gold.append("\n=FORMAT===================================================");
    gold.append("\nmessage M {");
    gold.append("\n  int xyz = 123;");
    gold.append("\n}");
    gold.append("\nchannel<M> foo;");
    gold.append("\n@construct {");
    gold.append("\n  readonly int r0 = 1;");
    gold.append("\n  readonly bool r1 = true;");
    gold.append("\n  readonly long r2 = 3L;");
    gold.append("\n  readonly double r3 = 4.42;");
    gold.append("\n  readonly string r4 = \"\";");
    gold.append("\n  readonly list<int> r5;");
    gold.append("\n  readonly list<principal> r6;");
    gold.append("\n  readonly principal r7 = @no_one;");
    gold.append("\n  readonly maybe<int> r8 = 100;");
    gold.append("\n  table<M> p;");
    gold.append("\n  readonly table<M> r9 = p;");
    gold.append("\n  readonly channel<M> r10 = foo;");
    gold.append("\n  readonly table<M> r11;");
    gold.append("\n  readonly label r12;");
    gold.append("\n}");
    gold.append("\n#fooz {");
    gold.append("\n  readonly future<M> fut = foo.fetch(@no_one);");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n==========================================================");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[]\"--JAVA---------------------------------------------");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.delta.*;");
    gold.append("\nimport org.adamalang.runtime.delta.secure.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.graph.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.json.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.algo.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.tables.*;");
    gold.append("\nimport org.adamalang.runtime.remote.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport org.adamalang.runtime.sys.*;");
    gold.append("\nimport org.adamalang.runtime.sys.cron.*;");
    gold.append("\nimport org.adamalang.runtime.sys.web.*;");
    gold.append("\nimport org.adamalang.runtime.text.*;");
    gold.append("\nimport java.time.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.util.HashMap;");
    gold.append("\nimport java.util.HashSet;");
    gold.append("\nimport java.util.Map;");
    gold.append("\nimport java.util.Set;");
    gold.append("\npublic class Simples_13 extends LivingDocument {");
    gold.append("\n  @Override");
    gold.append("\n  public long __memory() {");
    gold.append("\n    long __sum = super.__memory();");
    gold.append("\n    return __sum;");
    gold.append("\n  }");
    gold.append("\n  public Simples_13(DocumentMonitor __monitor) {");
    gold.append("\n    super(__monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __settle(Set<Integer> __viewers) {");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __insert(JsonStreamReader __reader) {");
    gold.append("\n    if (__reader.startObject()) {");
    gold.append("\n      while(__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"__state\":");
    gold.append("\n            __state.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__constructed\":");
    gold.append("\n            __constructed.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__next_time\":");
    gold.append("\n            __next_time.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__last_expire_time\":");
    gold.append("\n            __last_expire_time.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__blocked\":");
    gold.append("\n            __blocked.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__seq\":");
    gold.append("\n            __seq.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__entropy\":");
    gold.append("\n            __entropy.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_future_id\":");
    gold.append("\n            __auto_future_id.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__connection_id\":");
    gold.append("\n            __connection_id.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__message_id\":");
    gold.append("\n            __message_id.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__time\":");
    gold.append("\n            __time.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__timezone\":");
    gold.append("\n            __timezone.__insert(__reader);");
    gold.append("\n            __timezoneCachedZoneId = ZoneId.of(__timezone.get());");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_table_row_id\":");
    gold.append("\n            __auto_table_row_id.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_gen\":");
    gold.append("\n            __auto_gen.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_cache_id\":");
    gold.append("\n            __auto_cache_id.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__cache\":");
    gold.append("\n            __cache.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__webTaskId\":");
    gold.append("\n            __webTaskId.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__dedupe\":");
    gold.append("\n            __hydrateDeduper(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__clients\":");
    gold.append("\n            __hydrateClients(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__messages\":");
    gold.append("\n            __hydrateMessages(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__enqueued\":");
    gold.append("\n            __hydrateEnqueuedTaskManager(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__webqueue\":");
    gold.append("\n            __hydrateWebQueue(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__timeouts\":");
    gold.append("\n            __hydrateTimeouts(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__replication\":");
    gold.append("\n            __hydrateReplicationEngine(__reader);");
    gold.append("\n            break;");
    gold.append("\n          default:");
    gold.append("\n            __reader.skipValue();");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __patch(JsonStreamReader __reader) {");
    gold.append("\n    if (__reader.startObject()) {");
    gold.append("\n      while(__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"__state\":");
    gold.append("\n            __state.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__constructed\":");
    gold.append("\n            __constructed.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__next_time\":");
    gold.append("\n            __next_time.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__last_expire_time\":");
    gold.append("\n            __last_expire_time.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__blocked\":");
    gold.append("\n            __blocked.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__seq\":");
    gold.append("\n            __seq.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__entropy\":");
    gold.append("\n            __entropy.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_future_id\":");
    gold.append("\n            __auto_future_id.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__connection_id\":");
    gold.append("\n            __connection_id.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__message_id\":");
    gold.append("\n            __message_id.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__time\":");
    gold.append("\n            __time.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__timezone\":");
    gold.append("\n            __timezone.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_table_row_id\":");
    gold.append("\n            __auto_table_row_id.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_gen\":");
    gold.append("\n            __auto_gen.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_cache_id\":");
    gold.append("\n            __auto_cache_id.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__cache\":");
    gold.append("\n            __cache.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__webTaskId\":");
    gold.append("\n            __webTaskId.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__dedupe\":");
    gold.append("\n            __hydrateDeduper(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__clients\":");
    gold.append("\n            __hydrateClients(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__messages\":");
    gold.append("\n            __hydrateMessages(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__enqueued\":");
    gold.append("\n            __hydrateEnqueuedTaskManager(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__webqueue\":");
    gold.append("\n            __hydrateWebQueue(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__timeouts\":");
    gold.append("\n            __hydrateTimeouts(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__replication\":");
    gold.append("\n            __hydrateReplicationEngine(__reader);");
    gold.append("\n            break;");
    gold.append("\n          default:");
    gold.append("\n            __reader.skipValue();");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __dump(JsonStreamWriter __writer) {");
    gold.append("\n    __writer.beginObject();");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__state\");");
    gold.append("\n    __state.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__constructed\");");
    gold.append("\n    __constructed.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__next_time\");");
    gold.append("\n    __next_time.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__last_expire_time\");");
    gold.append("\n    __last_expire_time.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__blocked\");");
    gold.append("\n    __blocked.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__seq\");");
    gold.append("\n    __seq.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__entropy\");");
    gold.append("\n    __entropy.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_future_id\");");
    gold.append("\n    __auto_future_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__connection_id\");");
    gold.append("\n    __connection_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__message_id\");");
    gold.append("\n    __message_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__time\");");
    gold.append("\n    __time.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__timezone\");");
    gold.append("\n    __timezone.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_table_row_id\");");
    gold.append("\n    __auto_table_row_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_gen\");");
    gold.append("\n    __auto_gen.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_cache_id\");");
    gold.append("\n    __auto_cache_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__cache\");");
    gold.append("\n    __cache.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__webTaskId\");");
    gold.append("\n    __webTaskId.__dump(__writer);");
    gold.append("\n    __dumpDeduper(__writer);");
    gold.append("\n    __dumpClients(__writer);");
    gold.append("\n    __dumpMessages(__writer);");
    gold.append("\n    __dumpEnqueuedTaskManager(__writer);");
    gold.append("\n    __dumpTimeouts(__writer);");
    gold.append("\n    __dumpWebQueue(__writer);");
    gold.append("\n    __dumpReplicationEngine(__writer);");
    gold.append("\n    __writer.endObject();");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, JsonStreamWriter __forward, JsonStreamWriter __reverse) {");
    gold.append("\n    __state.__commit(\"__state\", __forward, __reverse);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __forward, __reverse);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __forward, __reverse);");
    gold.append("\n    __last_expire_time.__commit(\"__last_expire_time\", __forward, __reverse);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __forward, __reverse);");
    gold.append("\n    __seq.__commit(\"__seq\", __forward, __reverse);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __forward, __reverse);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __forward, __reverse);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __forward, __reverse);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __forward, __reverse);");
    gold.append("\n    __time.__commit(\"__time\", __forward, __reverse);");
    gold.append("\n    __timezone.__commit(\"__timezone\", __forward, __reverse);");
    gold.append("\n    __auto_table_row_id.__commit(\"__auto_table_row_id\", __forward, __reverse);");
    gold.append("\n    __auto_gen.__commit(\"__auto_gen\", __forward, __reverse);");
    gold.append("\n    __auto_cache_id.__commit(\"__auto_cache_id\", __forward, __reverse);");
    gold.append("\n    __cache.__commit(\"__cache\", __forward, __reverse);");
    gold.append("\n    __webTaskId.__commit(\"__webTaskId\", __forward, __reverse);");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __revert() {");
    gold.append("\n    __state.__revert();");
    gold.append("\n    __constructed.__revert();");
    gold.append("\n    __next_time.__revert();");
    gold.append("\n    __last_expire_time.__revert();");
    gold.append("\n    __blocked.__revert();");
    gold.append("\n    __seq.__revert();");
    gold.append("\n    __entropy.__revert();");
    gold.append("\n    __auto_future_id.__revert();");
    gold.append("\n    __connection_id.__revert();");
    gold.append("\n    __message_id.__revert();");
    gold.append("\n    __time.__revert();");
    gold.append("\n    __timezone.__revert();");
    gold.append("\n    __auto_table_row_id.__revert();");
    gold.append("\n    __webTaskId.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  public class DeltaPrivacyCache {");
    gold.append("\n    public DeltaPrivacyCache(NtPrincipal __who) {}");
    gold.append("\n  }");
    gold.append("\n  private class DeltaSimples_13 implements DeltaNode {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaSimples_13() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public boolean show(Simples_13 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      DeltaPrivacyCache __policy_cache = new DeltaPrivacyCache(__writer.who);");
    gold.append("\n      __writer.setCacheObject(__policy_cache);");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __obj.manifest();");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n      return true;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __code_cost += 0;");
    gold.append("\n    }");
    gold.append("\n    public void hide(PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      if (__emitted) {");
    gold.append("\n        clear();");
    gold.append("\n        __emitted = false;");
    gold.append("\n        __writer.writeNull();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public Set<String> __get_intern_strings() {");
    gold.append("\n    HashSet<String> __interns = new HashSet<>();");
    gold.append("\n    __interns.add(\"\");");
    gold.append("\n    __interns.add(\"?\");");
    gold.append("\n    return __interns;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public PrivateView __createPrivateView(NtPrincipal __who, Perspective ___perspective, AssetIdEncoder __encoder) {");
    gold.append("\n    Simples_13 __self = this;");
    gold.append("\n    DeltaSimples_13 __state = new DeltaSimples_13();");
    gold.append("\n    RTx__ViewerType __viewerState = new RTx__ViewerType();");
    gold.append("\n    int __viewId = __genViewId();");
    gold.append("\n    return new PrivateView(__viewId, __who, ___perspective, __encoder) {");
    gold.append("\n      @Override");
    gold.append("\n      public long memory() {");
    gold.append("\n        return __state.__memory();");
    gold.append("\n      }");
    gold.append("\n      @Override");
    gold.append("\n      public void dumpViewer(JsonStreamWriter __writer) {");
    gold.append("\n        __viewerState.__writeOut(__writer);");
    gold.append("\n      }");
    gold.append("\n      @Override");
    gold.append("\n      public void ingest(JsonStreamReader __reader) {");
    gold.append("\n        __viewerState.__ingest(__reader);");
    gold.append("\n      }");
    gold.append("\n      @Override");
    gold.append("\n      public void update(JsonStreamWriter __writer) {");
    gold.append("\n        __state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer, __viewerState, __encoder, __viewId));");
    gold.append("\n      }");
    gold.append("\n    };");
    gold.append("\n  }");
    gold.append("\n  private static class RTx__ViewerType extends NtMessageBase {");
    gold.append("\n    private final RTx__ViewerType __this;");
    gold.append("\n    public void __hash(HashBuilder __hash) {");
    gold.append("\n      __hash.hashString(\"anonymous\");");
    gold.append("\n    }");
    gold.append("\n    private static String[] __INDEX_COLUMNS___ViewerType = new String[] {};");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS___ViewerType;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int[] __getIndexValues() {");
    gold.append("\n      return new int[] {};");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType(JsonStreamReader __reader) {");
    gold.append("\n      __this = this;");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      __reader.mustSkipObject();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType() { __this = this; }");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTx__ViewerType implements DeltaNode {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTx__ViewerType() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public void show(RTx__ViewerType __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __code_cost += 0;");
    gold.append("\n    }");
    gold.append("\n    public void hide(PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      if (__emitted) {");
    gold.append("\n        clear();");
    gold.append("\n        __emitted = false;");
    gold.append("\n        __writer.writeNull();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static class RTxM extends NtMessageBase {");
    gold.append("\n    private final RTxM __this;");
    gold.append("\n    private int xyz = 123;");
    gold.append("\n    public void __hash(HashBuilder __hash) {");
    gold.append("\n      __hash.hashString(\"xyz\");");
    gold.append("\n      __hash.hashInteger(this.xyz);");
    gold.append("\n      __hash.hashString(\"M\");");
    gold.append("\n    }");
    gold.append("\n    private static String[] __INDEX_COLUMNS_M = new String[] {};");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS_M;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int[] __getIndexValues() {");
    gold.append("\n      return new int[] {};");
    gold.append("\n    }");
    gold.append("\n    private RTxM(JsonStreamReader __reader) {");
    gold.append("\n      __this = this;");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      __reader.mustStartObject();");
    gold.append("\n      while (__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"xyz\":");
    gold.append("\n            this.xyz = __reader.readInteger();");
    gold.append("\n            break;");
    gold.append("\n          default:");
    gold.append("\n            __reader.skipValue();");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.writeObjectFieldIntro(\"xyz\");");
    gold.append("\n      __writer.writeInteger(xyz);");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTxM() { __this = this; }");
    gold.append("\n    private RTxM(int xyz) {");
    gold.append("\n      this.__this = this;");
    gold.append("\n      this.xyz = xyz;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTxM implements DeltaNode {");
    gold.append("\n    private DInt32 __dxyz;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTxM() {");
    gold.append("\n      __dxyz = new DInt32();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      __sum += __dxyz.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public void show(RTxM __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __dxyz.show(__item.xyz, __obj.planField(\"xyz\"));");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __dxyz.clear();");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n    }");
    gold.append("\n    public void hide(PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      if (__emitted) {");
    gold.append("\n        clear();");
    gold.append("\n        __emitted = false;");
    gold.append("\n        __writer.writeNull();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  public static HashMap<String, HashMap<String, Object>> __services() {");
    gold.append("\n    HashMap<String, HashMap<String, Object>> __map = new HashMap<>();");
    gold.append("\n    return __map;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __link(ServiceRegistry __registry) {}");
    gold.append("\n  @Override");
    gold.append("\n  public void __executeServiceCalls(boolean cancel) {}");
    gold.append("\n  @Override");
    gold.append("\n  public String __getViewStateFilter() {");
    gold.append("\n    return \"[]\";");
    gold.append("\n  }");
    gold.append("\n  private final Sink<RTxM> __queue_foo = new Sink<>(\"foo\");");
    gold.append("\n  private final NtChannel<RTxM> foo = new NtChannel<>(__futures, __queue_foo);");
    gold.append("\n  @Override");
    gold.append("\n  protected boolean __is_direct_channel(String channel) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __handle_direct(CoreRequestContext context, String channel, Object __message) throws AbortMessageException {");
    gold.append("\n    return;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask __task) {");
    gold.append("\n    switch (__task.channel) {");
    gold.append("\n      case \"foo\":");
    gold.append("\n        __queue_foo.enqueue(__task, (RTxM) __task.message);");
    gold.append("\n        return;");
    gold.append("\n      default:");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected Object __parse_message(String __channel, JsonStreamReader __reader) {");
    gold.append("\n    switch (__channel) {");
    gold.append("\n      case \"foo\":");
    gold.append("\n        return new RTxM(__reader);");
    gold.append("\n      default:");
    gold.append("\n        __reader.skipValue();");
    gold.append("\n        return NtMessageBase.NULL;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n    __queue_foo.clear();");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __bindReplication() {}");
    gold.append("\n  @Override");
    gold.append("\n  public String __metrics() { return \"{}\"; }");
    gold.append("\n  @Override");
    gold.append("\n  public void __debug(JsonStreamWriter __writer) {}");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __open_channel(String name) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String __auth(CoreRequestContext __context, String username, String password) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  public AuthResponse __authpipe(CoreRequestContext __context, String __message) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __password(CoreRequestContext __context, String __pw) {}");
    gold.append("\n  @Override");
    gold.append("\n  public void __make_cron_progress() {}");
    gold.append("\n  @Override");
    gold.append("\n  protected WebResponse __get_internal(CoreRequestContext __context, WebGet __request) throws AbortMessageException {");
    gold.append("\n    WebPath __path = new WebPath(__request.uri);");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected WebResponse __put_internal(CoreRequestContext __context, WebPut __request) throws AbortMessageException {");
    gold.append("\n    WebPath __path = new WebPath(__request.uri);");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected WebResponse __delete_internal(CoreRequestContext __context, WebDelete __request) throws AbortMessageException {");
    gold.append("\n    WebPath __path = new WebPath(__request.uri);");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public WebResponse __options(CoreRequestContext __context, WebGet __request) {");
    gold.append("\n    WebPath __path = new WebPath(__request.uri);");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  private void __step_fooz() {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    final SimpleFuture<RTxM> fut = foo.fetchItem(NtPrincipal.NO_ONE);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"fooz\":");
    gold.append("\n        __step_fooz();");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  public static boolean __onCanCreate(CoreRequestContext __context) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  public static boolean __onCanInvent(CoreRequestContext __context) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  public static boolean __onCanSendWhileDisconnected(CoreRequestContext __context) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onLoad() {}");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onCanAssetAttached(CoreRequestContext __cvalue) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onAssetAttached(CoreRequestContext __cvalue, NtAsset __pvalue) {}");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __delete(CoreRequestContext __cvalue) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onConnected(CoreRequestContext __cvalue) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onDisconnected(CoreRequestContext __cvalue) {}");
    gold.append("\n  public static HashMap<String, Object> __config() {");
    gold.append("\n    HashMap<String, Object> __map = new HashMap<>();");
    gold.append("\n    return __map;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {}");
    gold.append("\n  private void __construct_0(CoreRequestContext __context, NtPrincipal __who, NtMessageBase __object) {");
    gold.append("\n    __code_cost += 15;");
    gold.append("\n    __track(1);");
    gold.append("\n    final int r0 = 1;");
    gold.append("\n    __track(2);");
    gold.append("\n    final boolean r1 = true;");
    gold.append("\n    __track(3);");
    gold.append("\n    final long r2 = 3L;");
    gold.append("\n    __track(4);");
    gold.append("\n    final double r3 = 4.42;");
    gold.append("\n    __track(5);");
    gold.append("\n    final String r4 = \"\";");
    gold.append("\n    __track(6);");
    gold.append("\n    final NtList<Integer> r5 = new EmptyNtList<Integer>();");
    gold.append("\n    __track(7);");
    gold.append("\n    final NtList<NtPrincipal> r6 = new EmptyNtList<NtPrincipal>();");
    gold.append("\n    __track(8);");
    gold.append("\n    final NtPrincipal r7 = NtPrincipal.NO_ONE;");
    gold.append("\n    __track(9);");
    gold.append("\n    final NtMaybe<Integer> r8 = new NtMaybe<Integer>(100);");
    gold.append("\n    __track(10);");
    gold.append("\n    NtTable<RTxM> p = new NtTable<RTxM>(() -> new RTxM());");
    gold.append("\n    __track(11);");
    gold.append("\n    final NtTable<RTxM> r9 = new NtTable<RTxM>(p);");
    gold.append("\n    __track(12);");
    gold.append("\n    final NtChannel<RTxM> r10 = foo;");
    gold.append("\n    __track(13);");
    gold.append("\n    final NtTable<RTxM> r11 = new NtTable<RTxM>(() -> new RTxM());");
    gold.append("\n    __track(14);");
    gold.append("\n    final String r12 = \"\";");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(CoreRequestContext __context, NtMessageBase __object) {");
    gold.append("\n    __construct_0(__context, __context.who, __object);");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--=[LivingDocumentFactory COMPILING]=---");
    gold.append("\n--=[LivingDocumentFactory MADE]=---");
    gold.append("\n--REFLECTION RESULTS-------------------------------------");
    gold.append("\n{\"types\":{\"__Root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}},\"M\":{\"nature\":\"native_message\",\"name\":\"M\",\"anonymous\":false,\"fields\":{\"xyz\":{\"type\":{\"nature\":\"native_value\",\"type\":\"int\"},\"privacy\":\"public\"}}}},\"channels\":{\"foo\":\"M\"},\"channels-privacy\":{\"foo\":{\"open\":false,\"privacy\":[]}},\"constructors\":[],\"labels\":[\"fooz\"]}");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\",\"key\":\"0\",\"origin\":\"origin\",\"ip\":\"ip\"}-->{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__messages\":null,\"__seq\":1} need:false in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"25\"} need:false in:0");
    gold.append("\nCPU:15");
    gold.append("\nMEMORY:384");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seq\":3,\"__entropy\":\"-1034601897293430941\",\"__time\":\"50\"} need:false in:0");
    gold.append("\nNO_ONE: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":3}");
    gold.append("\nNO_ONE|FAILURE:184333");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"75\"}-->{\"__messages\":null,\"__seq\":4,\"__entropy\":\"7848011421992302230\",\"__time\":\"75\"} need:false in:0");
    gold.append("\nRANDO: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":4}");
    gold.append("\n+ RANDO DELTA:{\"seq\":4}");
    gold.append("\nRANDO|FAILURE:184333");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__time\":\"100\"} need:false in:0");
    gold.append("\nRANDO|SUCCESS:5");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":5}");
    gold.append("\n+ RANDO DELTA:{\"seq\":5}");
    gold.append("\nMEMORY:502");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{}");
    gold.append("\n--METRIC RESULTS-----------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_TableIngest_14 = null;
  private String get_TableIngest_14() {
    if (cached_TableIngest_14 != null) {
      return cached_TableIngest_14;
    }
    cached_TableIngest_14 = generateTestOutput(false, "TableIngest_14", "./test_code/ReadOnly_TableIngest_failure.a");
    return cached_TableIngest_14;
  }

  @Test
  public void testTableIngestFailure() {
    assertLiveFail(get_TableIngest_14());
  }

  @Test
  public void testTableIngestNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_TableIngest_14());
  }

  @Test
  public void testTableIngestExceptionFree() {
    assertExceptionFree(get_TableIngest_14());
  }

  @Test
  public void testTableIngestTODOFree() {
    assertTODOFree(get_TableIngest_14());
  }

  @Test
  public void stable_TableIngest_14() {
    String live = get_TableIngest_14();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:ReadOnly_TableIngest_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":3,\"character\":9,\"byte\":37},\"end\":{\"line\":6,\"character\":25,\"byte\":91}},\"severity\":1,\"source\":\"error\",\"message\":\"'table<R>' is unable to ingest '_AnonObjConvert_0' due to readonly left reference.\",\"file\":\"./test_code/ReadOnly_TableIngest_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

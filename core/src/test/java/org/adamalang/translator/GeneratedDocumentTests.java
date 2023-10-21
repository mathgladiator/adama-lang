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

public class GeneratedDocumentTests extends GeneratedBase {
  private String cached_CantMixChannelAndFunctions_1 = null;
  private String get_CantMixChannelAndFunctions_1() {
    if (cached_CantMixChannelAndFunctions_1 != null) {
      return cached_CantMixChannelAndFunctions_1;
    }
    cached_CantMixChannelAndFunctions_1 = generateTestOutput(false, "CantMixChannelAndFunctions_1", "./test_code/Document_CantMixChannelAndFunctions_failure.a");
    return cached_CantMixChannelAndFunctions_1;
  }

  @Test
  public void testCantMixChannelAndFunctionsFailure() {
    assertLiveFail(get_CantMixChannelAndFunctions_1());
  }

  @Test
  public void testCantMixChannelAndFunctionsNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_CantMixChannelAndFunctions_1());
  }

  @Test
  public void testCantMixChannelAndFunctionsExceptionFree() {
    assertExceptionFree(get_CantMixChannelAndFunctions_1());
  }

  @Test
  public void testCantMixChannelAndFunctionsTODOFree() {
    assertTODOFree(get_CantMixChannelAndFunctions_1());
  }

  @Test
  public void stable_CantMixChannelAndFunctions_1() {
    String live = get_CantMixChannelAndFunctions_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Document_CantMixChannelAndFunctions_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":5,\"character\":0,\"byte\":32},\"end\":{\"line\":7,\"character\":1,\"byte\":69}},\"severity\":1,\"source\":\"error\",\"message\":\"The function 'foo' was already defined.\",\"file\":\"./test_code/Document_CantMixChannelAndFunctions_failure.a\"},{\"range\":{\"start\":{\"line\":13,\"character\":0,\"byte\":110},\"end\":{\"line\":13,\"character\":15,\"byte\":125}},\"severity\":1,\"source\":\"error\",\"message\":\"Handler 'goo' was already defined.\",\"file\":\"./test_code/Document_CantMixChannelAndFunctions_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DuplicateComputeFields_2 = null;
  private String get_DuplicateComputeFields_2() {
    if (cached_DuplicateComputeFields_2 != null) {
      return cached_DuplicateComputeFields_2;
    }
    cached_DuplicateComputeFields_2 = generateTestOutput(false, "DuplicateComputeFields_2", "./test_code/Document_DuplicateComputeFields_failure.a");
    return cached_DuplicateComputeFields_2;
  }

  @Test
  public void testDuplicateComputeFieldsFailure() {
    assertLiveFail(get_DuplicateComputeFields_2());
  }

  @Test
  public void testDuplicateComputeFieldsNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DuplicateComputeFields_2());
  }

  @Test
  public void testDuplicateComputeFieldsExceptionFree() {
    assertExceptionFree(get_DuplicateComputeFields_2());
  }

  @Test
  public void testDuplicateComputeFieldsTODOFree() {
    assertTODOFree(get_DuplicateComputeFields_2());
  }

  @Test
  public void stable_DuplicateComputeFields_2() {
    String live = get_DuplicateComputeFields_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Document_DuplicateComputeFields_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":0,\"byte\":14},\"end\":{\"line\":1,\"character\":13,\"byte\":27}},\"severity\":1,\"source\":\"error\",\"message\":\"Global field 'x' was already defined\",\"file\":\"./test_code/Document_DuplicateComputeFields_failure.a\"},{\"range\":{\"start\":{\"line\":3,\"character\":0,\"byte\":42},\"end\":{\"line\":3,\"character\":13,\"byte\":55}},\"severity\":1,\"source\":\"error\",\"message\":\"Global field 'y' was already defined\",\"file\":\"./test_code/Document_DuplicateComputeFields_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DuplicateEnums_3 = null;
  private String get_DuplicateEnums_3() {
    if (cached_DuplicateEnums_3 != null) {
      return cached_DuplicateEnums_3;
    }
    cached_DuplicateEnums_3 = generateTestOutput(false, "DuplicateEnums_3", "./test_code/Document_DuplicateEnums_failure.a");
    return cached_DuplicateEnums_3;
  }

  @Test
  public void testDuplicateEnumsFailure() {
    assertLiveFail(get_DuplicateEnums_3());
  }

  @Test
  public void testDuplicateEnumsNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DuplicateEnums_3());
  }

  @Test
  public void testDuplicateEnumsExceptionFree() {
    assertExceptionFree(get_DuplicateEnums_3());
  }

  @Test
  public void testDuplicateEnumsTODOFree() {
    assertTODOFree(get_DuplicateEnums_3());
  }

  @Test
  public void stable_DuplicateEnums_3() {
    String live = get_DuplicateEnums_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Document_DuplicateEnums_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":0,\"byte\":13},\"end\":{\"line\":1,\"character\":12,\"byte\":25}},\"severity\":1,\"source\":\"error\",\"message\":\"The enumeration 'X' was already defined.\",\"file\":\"./test_code/Document_DuplicateEnums_failure.a\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":12,\"byte\":12}},\"severity\":1,\"source\":\"error\",\"message\":\"The enumeration 'X' was defined here.\",\"file\":\"./test_code/Document_DuplicateEnums_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DuplicateFields_4 = null;
  private String get_DuplicateFields_4() {
    if (cached_DuplicateFields_4 != null) {
      return cached_DuplicateFields_4;
    }
    cached_DuplicateFields_4 = generateTestOutput(false, "DuplicateFields_4", "./test_code/Document_DuplicateFields_failure.a");
    return cached_DuplicateFields_4;
  }

  @Test
  public void testDuplicateFieldsFailure() {
    assertLiveFail(get_DuplicateFields_4());
  }

  @Test
  public void testDuplicateFieldsNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DuplicateFields_4());
  }

  @Test
  public void testDuplicateFieldsExceptionFree() {
    assertExceptionFree(get_DuplicateFields_4());
  }

  @Test
  public void testDuplicateFieldsTODOFree() {
    assertTODOFree(get_DuplicateFields_4());
  }

  @Test
  public void stable_DuplicateFields_4() {
    String live = get_DuplicateFields_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Document_DuplicateFields_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":0,\"byte\":14},\"end\":{\"line\":1,\"character\":13,\"byte\":27}},\"severity\":1,\"source\":\"error\",\"message\":\"Global field 'x' was already defined\",\"file\":\"./test_code/Document_DuplicateFields_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DuplicateMessages_5 = null;
  private String get_DuplicateMessages_5() {
    if (cached_DuplicateMessages_5 != null) {
      return cached_DuplicateMessages_5;
    }
    cached_DuplicateMessages_5 = generateTestOutput(false, "DuplicateMessages_5", "./test_code/Document_DuplicateMessages_failure.a");
    return cached_DuplicateMessages_5;
  }

  @Test
  public void testDuplicateMessagesFailure() {
    assertLiveFail(get_DuplicateMessages_5());
  }

  @Test
  public void testDuplicateMessagesNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DuplicateMessages_5());
  }

  @Test
  public void testDuplicateMessagesExceptionFree() {
    assertExceptionFree(get_DuplicateMessages_5());
  }

  @Test
  public void testDuplicateMessagesTODOFree() {
    assertTODOFree(get_DuplicateMessages_5());
  }

  @Test
  public void stable_DuplicateMessages_5() {
    String live = get_DuplicateMessages_5();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Document_DuplicateMessages_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":4,\"character\":0,\"byte\":24},\"end\":{\"line\":6,\"character\":1,\"byte\":46}},\"severity\":1,\"source\":\"error\",\"message\":\"The message 'M' was already defined.\",\"file\":\"./test_code/Document_DuplicateMessages_failure.a\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":2,\"character\":1,\"byte\":22}},\"severity\":1,\"source\":\"error\",\"message\":\"The message 'M' was defined here.\",\"file\":\"./test_code/Document_DuplicateMessages_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DuplicatePolicies_6 = null;
  private String get_DuplicatePolicies_6() {
    if (cached_DuplicatePolicies_6 != null) {
      return cached_DuplicatePolicies_6;
    }
    cached_DuplicatePolicies_6 = generateTestOutput(false, "DuplicatePolicies_6", "./test_code/Document_DuplicatePolicies_failure.a");
    return cached_DuplicatePolicies_6;
  }

  @Test
  public void testDuplicatePoliciesFailure() {
    assertLiveFail(get_DuplicatePolicies_6());
  }

  @Test
  public void testDuplicatePoliciesNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DuplicatePolicies_6());
  }

  @Test
  public void testDuplicatePoliciesExceptionFree() {
    assertExceptionFree(get_DuplicatePolicies_6());
  }

  @Test
  public void testDuplicatePoliciesTODOFree() {
    assertTODOFree(get_DuplicatePolicies_6());
  }

  @Test
  public void stable_DuplicatePolicies_6() {
    String live = get_DuplicatePolicies_6();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Document_DuplicatePolicies_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":4,\"character\":0,\"byte\":31},\"end\":{\"line\":6,\"character\":1,\"byte\":60}},\"severity\":1,\"source\":\"error\",\"message\":\"Global policy 'foo' was already defined\",\"file\":\"./test_code/Document_DuplicatePolicies_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DuplicateRecords_7 = null;
  private String get_DuplicateRecords_7() {
    if (cached_DuplicateRecords_7 != null) {
      return cached_DuplicateRecords_7;
    }
    cached_DuplicateRecords_7 = generateTestOutput(false, "DuplicateRecords_7", "./test_code/Document_DuplicateRecords_failure.a");
    return cached_DuplicateRecords_7;
  }

  @Test
  public void testDuplicateRecordsFailure() {
    assertLiveFail(get_DuplicateRecords_7());
  }

  @Test
  public void testDuplicateRecordsNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DuplicateRecords_7());
  }

  @Test
  public void testDuplicateRecordsExceptionFree() {
    assertExceptionFree(get_DuplicateRecords_7());
  }

  @Test
  public void testDuplicateRecordsTODOFree() {
    assertTODOFree(get_DuplicateRecords_7());
  }

  @Test
  public void stable_DuplicateRecords_7() {
    String live = get_DuplicateRecords_7();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Document_DuplicateRecords_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":4,\"character\":0,\"byte\":30},\"end\":{\"line\":6,\"character\":1,\"byte\":59}},\"severity\":1,\"source\":\"error\",\"message\":\"The record 'R' was already defined.\",\"file\":\"./test_code/Document_DuplicateRecords_failure.a\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":2,\"character\":1,\"byte\":28}},\"severity\":1,\"source\":\"error\",\"message\":\"The record 'R' was defined here.\",\"file\":\"./test_code/Document_DuplicateRecords_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_GlobalPolicy_8 = null;
  private String get_GlobalPolicy_8() {
    if (cached_GlobalPolicy_8 != null) {
      return cached_GlobalPolicy_8;
    }
    cached_GlobalPolicy_8 = generateTestOutput(true, "GlobalPolicy_8", "./test_code/Document_GlobalPolicy_success.a");
    return cached_GlobalPolicy_8;
  }

  @Test
  public void testGlobalPolicyEmission() {
    assertEmissionGood(get_GlobalPolicy_8());
  }

  @Test
  public void testGlobalPolicySuccess() {
    assertLivePass(get_GlobalPolicy_8());
  }

  @Test
  public void testGlobalPolicyGoodWillHappy() {
    assertGoodWillHappy(get_GlobalPolicy_8());
  }

  @Test
  public void testGlobalPolicyExceptionFree() {
    assertExceptionFree(get_GlobalPolicy_8());
  }

  @Test
  public void testGlobalPolicyTODOFree() {
    assertTODOFree(get_GlobalPolicy_8());
  }

  @Test
  public void stable_GlobalPolicy_8() {
    String live = get_GlobalPolicy_8();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Document_GlobalPolicy_success.a");
    gold.append("\n--EMISSION-----------------------------------------");
    gold.append("\nEmission Success, Yay");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[]\"--JAVA---------------------------------------------");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.delta.*;");
    gold.append("\nimport org.adamalang.runtime.delta.secure.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
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
    gold.append("\npublic class GlobalPolicy_8 extends LivingDocument {");
    gold.append("\n  private final RxTable<RTxR> r;");
    gold.append("\n  private final RxInt32 z2;");
    gold.append("\n  public boolean __POLICY_foo(NtPrincipal __who){");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return true;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public long __memory() {");
    gold.append("\n    long __sum = super.__memory();");
    gold.append("\n    __sum += r.__memory();");
    gold.append("\n    __sum += z2.__memory();");
    gold.append("\n    return __sum;");
    gold.append("\n  }");
    gold.append("\n  public GlobalPolicy_8(DocumentMonitor __monitor) {");
    gold.append("\n    super(__monitor);");
    gold.append("\n    r = new RxTable<>(__self, this, \"r\", (RxParent __parent) -> new RTxR(__parent).__link(), 0);");
    gold.append("\n    z2 = new RxInt32(this, 0);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __insert(JsonStreamReader __reader) {");
    gold.append("\n    if (__reader.startObject()) {");
    gold.append("\n      while(__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"r\":");
    gold.append("\n            r.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"z2\":");
    gold.append("\n            z2.__insert(__reader);");
    gold.append("\n            break;");
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
    gold.append("\n          case \"r\":");
    gold.append("\n            r.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"z2\":");
    gold.append("\n            z2.__patch(__reader);");
    gold.append("\n            break;");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"r\");");
    gold.append("\n    r.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"z2\");");
    gold.append("\n    z2.__dump(__writer);");
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
    gold.append("\n    r.__commit(\"r\", __forward, __reverse);");
    gold.append("\n    z2.__commit(\"z2\", __forward, __reverse);");
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
    gold.append("\n    r.__revert();");
    gold.append("\n    z2.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  public class DeltaPrivacyCache {");
    gold.append("\n    public final boolean foo;");
    gold.append("\n    public DeltaPrivacyCache(NtPrincipal __who) {");
    gold.append("\n      this.foo=__POLICY_foo(__who);");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private class DeltaGlobalPolicy_8 implements DeltaNode {");
    gold.append("\n    private DInt32 __dz2;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaGlobalPolicy_8() {");
    gold.append("\n      __dz2 = new DInt32();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      __sum += __dz2.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public boolean show(GlobalPolicy_8 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      DeltaPrivacyCache __policy_cache = new DeltaPrivacyCache(__writer.who);");
    gold.append("\n      __writer.setCacheObject(__policy_cache);");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __obj.manifest();");
    gold.append("\n      if (__policy_cache.foo) {");
    gold.append("\n        __dz2.show(__item.z2.get(), __obj.planField(\"z2\"));");
    gold.append("\n        /* privacy check close up */");
    gold.append("\n      } else {");
    gold.append("\n        __dz2.hide(__obj.planField(\"z2\"));");
    gold.append("\n      }");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n      return true;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __dz2.clear();");
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
    gold.append("\n  @Override");
    gold.append("\n  public Set<String> __get_intern_strings() {");
    gold.append("\n    HashSet<String> __interns = new HashSet<>();");
    gold.append("\n    __interns.add(\"\");");
    gold.append("\n    __interns.add(\"?\");");
    gold.append("\n    return __interns;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public PrivateView __createPrivateView(NtPrincipal __who, Perspective ___perspective, AssetIdEncoder __encoder) {");
    gold.append("\n    GlobalPolicy_8 __self = this;");
    gold.append("\n    DeltaGlobalPolicy_8 __state = new DeltaGlobalPolicy_8();");
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
    gold.append("\n  private static String[] __INDEX_COLUMNS_R = new String[] {};");
    gold.append("\n  private class RTxR extends RxRecordBase<RTxR> {");
    gold.append("\n    private final RTxR __this;");
    gold.append("\n    private final RxInt32 id;");
    gold.append("\n    private final RxInt32 x;");
    gold.append("\n    private RTxR(RxParent __owner) {");
    gold.append("\n      super(__owner);");
    gold.append("\n      this.__this = this;");
    gold.append("\n      id = new RxInt32(this, 0);");
    gold.append("\n      x = new RxInt32(this, 0);");
    gold.append("\n      if (__owner instanceof RxTable) {");
    gold.append("\n        /* ok */");
    gold.append("\n      } else {");
    gold.append("\n        /* ok */");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = super.__memory();");
    gold.append("\n      __sum += id.__memory();");
    gold.append("\n      __sum += x.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS_R;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int[] __getIndexValues() {");
    gold.append("\n      return new int[] {};");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public Object __fieldOf(String __name) {");
    gold.append("\n      switch (__name) {");
    gold.append("\n        case \"id\":");
    gold.append("\n          return id;");
    gold.append("\n        case \"x\":");
    gold.append("\n          return x;");
    gold.append("\n        default:");
    gold.append("\n          return null;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __insert(JsonStreamReader __reader) {");
    gold.append("\n      if (__reader.startObject()) {");
    gold.append("\n        while(__reader.notEndOfObject()) {");
    gold.append("\n          String __fieldName = __reader.fieldName();");
    gold.append("\n          switch (__fieldName) {");
    gold.append("\n            case \"id\":");
    gold.append("\n              id.__insert(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"x\":");
    gold.append("\n              x.__insert(__reader);");
    gold.append("\n              break;");
    gold.append("\n            default:");
    gold.append("\n              __reader.skipValue();");
    gold.append("\n          }");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __patch(JsonStreamReader __reader) {");
    gold.append("\n      if (__reader.startObject()) {");
    gold.append("\n        while(__reader.notEndOfObject()) {");
    gold.append("\n          String __fieldName = __reader.fieldName();");
    gold.append("\n          switch (__fieldName) {");
    gold.append("\n            case \"id\":");
    gold.append("\n              id.__patch(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"x\":");
    gold.append("\n              x.__patch(__reader);");
    gold.append("\n              break;");
    gold.append("\n            default:");
    gold.append("\n              __reader.skipValue();");
    gold.append("\n          }");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __dump(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.writeObjectFieldIntro(\"id\");");
    gold.append("\n      id.__dump(__writer);");
    gold.append("\n      __writer.writeObjectFieldIntro(\"x\");");
    gold.append("\n      x.__dump(__writer);");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __commit(String __name, JsonStreamWriter __forward, JsonStreamWriter __reverse) {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        __forward.writeObjectFieldIntro(__name);");
    gold.append("\n        __forward.beginObject();");
    gold.append("\n        __reverse.writeObjectFieldIntro(__name);");
    gold.append("\n        __reverse.beginObject();");
    gold.append("\n        id.__commit(\"id\", __forward, __reverse);");
    gold.append("\n        x.__commit(\"x\", __forward, __reverse);");
    gold.append("\n        __forward.endObject();");
    gold.append("\n        __reverse.endObject();");
    gold.append("\n        __lowerDirtyCommit();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __revert() {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        __isDying = false;");
    gold.append("\n        id.__revert();");
    gold.append("\n        x.__revert();");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __killFields() {}");
    gold.append("\n    @Override");
    gold.append("\n    public RTxR __link() {");
    gold.append("\n      return this;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __pumpIndexEvents(TablePubSub __pubsub) {}");
    gold.append("\n    @Override");
    gold.append("\n    public String __name() {");
    gold.append("\n      return \"R\";");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __deindex() {");
    gold.append("\n      /* ok */");
    gold.append("\n    }");
    gold.append("\n    public void __reindex() {");
    gold.append("\n      /* ok */");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int __id() {");
    gold.append("\n      return id.get();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __setId(int __id, boolean __force) {");
    gold.append("\n      if (__force) {");
    gold.append("\n        id.forceSet(__id);");
    gold.append("\n      } else {");
    gold.append("\n        id.set(__id);");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTxR implements DeltaNode {");
    gold.append("\n    private DInt32 __dx;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTxR() {");
    gold.append("\n      __dx = new DInt32();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      __sum += __dx.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public boolean show(RTxR __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      DeltaPrivacyCache __policy_cache = (DeltaPrivacyCache) __writer.getCacheObject();");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      if (__policy_cache.foo) {");
    gold.append("\n        __dx.show(__item.x.get(), __obj.planField(\"x\"));");
    gold.append("\n        /* privacy check close up */");
    gold.append("\n      } else {");
    gold.append("\n        __dx.hide(__obj.planField(\"x\"));");
    gold.append("\n      }");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n      return true;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __dx.clear();");
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
    gold.append("\n  class DynCmp_RTxR implements Comparator<RTxR> {");
    gold.append("\n    private final CompareField[] parsed;");
    gold.append("\n    DynCmp_RTxR(String instructions) {");
    gold.append("\n      this.parsed = DynCompareParser.parse(instructions);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int compare(RTxR __a, RTxR __b) {");
    gold.append("\n      for (CompareField field : parsed) {");
    gold.append("\n        int delta = 0;");
    gold.append("\n        switch (field.name) {");
    gold.append("\n          case \"id\":");
    gold.append("\n            delta = __a.id.compareTo(__b.id);");
    gold.append("\n            break;");
    gold.append("\n          case \"x\":");
    gold.append("\n            delta = __a.x.compareTo(__b.x);");
    gold.append("\n            break;");
    gold.append("\n        }");
    gold.append("\n        if (delta != 0) {");
    gold.append("\n          return field.desc ? -delta : delta;");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n      return 0;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static class RTx_AnonObjConvert_0 extends NtMessageBase {");
    gold.append("\n    private final RTx_AnonObjConvert_0 __this;");
    gold.append("\n    private int x = 0;");
    gold.append("\n    public void __hash(HashBuilder __hash) {");
    gold.append("\n      __hash.hashString(\"x\");");
    gold.append("\n      __hash.hashInteger(this.x);");
    gold.append("\n      __hash.hashString(\"anonymous\");");
    gold.append("\n    }");
    gold.append("\n    private static String[] __INDEX_COLUMNS__AnonObjConvert_0 = new String[] {};");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS__AnonObjConvert_0;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int[] __getIndexValues() {");
    gold.append("\n      return new int[] {};");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_0(JsonStreamReader __reader) {");
    gold.append("\n      __this = this;");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      __reader.mustStartObject();");
    gold.append("\n      while (__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"x\":");
    gold.append("\n            this.x = __reader.readInteger();");
    gold.append("\n            break;");
    gold.append("\n          default:");
    gold.append("\n            __reader.skipValue();");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.writeObjectFieldIntro(\"x\");");
    gold.append("\n      __writer.writeInteger(x);");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_0() { __this = this; }");
    gold.append("\n    private RTx_AnonObjConvert_0(int x) {");
    gold.append("\n      this.__this = this;");
    gold.append("\n      this.x = x;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTx_AnonObjConvert_0 implements DeltaNode {");
    gold.append("\n    private DInt32 __dx;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTx_AnonObjConvert_0() {");
    gold.append("\n      __dx = new DInt32();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      __sum += __dx.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public void show(RTx_AnonObjConvert_0 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __dx.show(__item.x, __obj.planField(\"x\"));");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __dx.clear();");
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
    gold.append("\n  @Override");
    gold.append("\n  protected boolean __is_direct_channel(String channel) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __handle_direct(CoreRequestContext context, String channel, Object __message) throws AbortMessageException {");
    gold.append("\n    return;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected Object __parse_message(String channel, JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __bindReplication() {}");
    gold.append("\n  @Override");
    gold.append("\n  public String __metrics() { return \"{}\"; }");
    gold.append("\n  @Override");
    gold.append("\n  public void __debug(JsonStreamWriter __writer) {");
    gold.append("\n    __writer.writeObjectFieldIntro(\"tables\");");
    gold.append("\n    __writer.beginObject();");
    gold.append("\n    __writer.writeObjectFieldIntro(\"r\");");
    gold.append("\n    r.debug(__writer);");
    gold.append("\n    __writer.endObject();");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __open_channel(String name) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String __auth(CoreRequestContext __context, String username, String password) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __password(CoreRequestContext __context, String __pw) {}");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {}");
    gold.append("\n  public boolean __onConnected__0(CoreRequestContext __context, NtPrincipal __who) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(1);");
    gold.append("\n    return true;");
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
    gold.append("\n    boolean __result = false;");
    gold.append("\n    if (__onConnected__0(__cvalue, __cvalue.who)) __result = true;");
    gold.append("\n    return __result;");
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
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(2);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxR> _AutoRef1 = r;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr3 = new RTx_AnonObjConvert_0(1);");
    gold.append("\n      RTxR _CreateRef2 = _AutoRef1.make();");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      _CreateRef2.x.set(_AutoExpr3.x);");
    gold.append("\n    }");
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
    gold.append("\n{\"types\":{\"__Root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{\"r\":{\"type\":{\"nature\":\"reactive_table\",\"record_name\":\"R\"},\"privacy\":\"private\"},\"z2\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"policy\"}}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}},\"R\":{\"nature\":\"reactive_record\",\"name\":\"R\",\"fields\":{\"id\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"private\"},\"x\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"policy\"}}},\"_AnonObjConvert_0\":{\"nature\":\"native_message\",\"name\":\"_AnonObjConvert_0\",\"anonymous\":true,\"fields\":{\"x\":{\"type\":{\"nature\":\"native_value\",\"type\":\"int\"},\"privacy\":\"public\"}}}},\"channels\":{},\"channels-privacy\":{},\"constructors\":[],\"labels\":[]}");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\",\"key\":\"0\",\"origin\":\"origin\",\"ip\":\"ip\"}-->{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__auto_table_row_id\":1,\"r\":{\"1\":{\"id\":1,\"x\":1}},\"__messages\":null,\"__seq\":1} need:false in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\nCPU:3");
    gold.append("\nMEMORY:696");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seq\":3,\"__entropy\":\"-1034601897293430941\",\"__time\":\"50\"} need:false in:-50");
    gold.append("\nNO_ONE: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{\"z2\":0},\"seq\":3}");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"75\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"key\":\"key\",\"origin\":\"phase\",\"ip\":\"ip\"}-->{\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__connection_id\":1,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__messages\":null} need:false in:-75");
    gold.append("\nNO_ONE|SUCCESS:4");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":5}");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seq\":6,\"__entropy\":\"488730542833106255\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\nRANDO: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":6}");
    gold.append("\n+ RANDO DELTA:{\"data\":{\"z2\":0},\"seq\":6}");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"125\",\"who\":{\"agent\":\"rando\",\"authority\":\"random-place\"},\"key\":\"key\",\"origin\":\"phase\",\"ip\":\"ip\"}-->{\"__seq\":8,\"__entropy\":\"-4023469995120514721\",\"__connection_id\":2,\"__time\":\"125\",\"__clients\":{\"1\":{\"agent\":\"rando\",\"authority\":\"random-place\"}},\"__messages\":null} need:false in:-125");
    gold.append("\nRANDO|SUCCESS:7");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":8}");
    gold.append("\n+ RANDO DELTA:{\"seq\":8}");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"150\"}-->{\"__messages\":null,\"__seq\":9,\"__entropy\":\"-652523483647487500\",\"__time\":\"150\"} need:false in:-150");
    gold.append("\nRANDO|SUCCESS:9");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":9}");
    gold.append("\n+ RANDO DELTA:{\"seq\":9}");
    gold.append("\nMEMORY:894");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"r\":{\"1\":{\"id\":1,\"x\":1}},\"z2\":0,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":9,\"__entropy\":\"-652523483647487500\",\"__auto_future_id\":0,\"__connection_id\":2,\"__message_id\":0,\"__time\":\"150\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":1,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"},\"1\":{\"agent\":\"rando\",\"authority\":\"random-place\"}},\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{}");
    gold.append("\n--METRIC RESULTS-----------------------------------");
    gold.append("\n{\"r\":{\"1\":{\"id\":1,\"x\":1}},\"z2\":0,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":9,\"__entropy\":\"-652523483647487500\",\"__auto_future_id\":0,\"__connection_id\":2,\"__message_id\":0,\"__time\":\"150\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":1,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"},\"1\":{\"agent\":\"rando\",\"authority\":\"random-place\"}},\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n{\"r\":{\"1\":{\"id\":1,\"x\":1}},\"z2\":0,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":9,\"__entropy\":\"-652523483647487500\",\"__auto_future_id\":0,\"__connection_id\":2,\"__message_id\":0,\"__time\":\"150\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":1,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"},\"1\":{\"agent\":\"rando\",\"authority\":\"random-place\"}},\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_UnknownType_9 = null;
  private String get_UnknownType_9() {
    if (cached_UnknownType_9 != null) {
      return cached_UnknownType_9;
    }
    cached_UnknownType_9 = generateTestOutput(false, "UnknownType_9", "./test_code/Document_UnknownType_failure.a");
    return cached_UnknownType_9;
  }

  @Test
  public void testUnknownTypeFailure() {
    assertLiveFail(get_UnknownType_9());
  }

  @Test
  public void testUnknownTypeNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_UnknownType_9());
  }

  @Test
  public void testUnknownTypeExceptionFree() {
    assertExceptionFree(get_UnknownType_9());
  }

  @Test
  public void testUnknownTypeTODOFree() {
    assertTODOFree(get_UnknownType_9());
  }

  @Test
  public void stable_UnknownType_9() {
    String live = get_UnknownType_9();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Document_UnknownType_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":1,\"byte\":1}},\"severity\":1,\"source\":\"error\",\"message\":\"Type not found: the type 'X' was not found.\",\"file\":\"./test_code/Document_UnknownType_failure.a\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":4,\"byte\":4}},\"severity\":1,\"source\":\"error\",\"message\":\"The field 'x' has no type\",\"file\":\"./test_code/Document_UnknownType_failure.a\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":4,\"byte\":4}},\"severity\":1,\"source\":\"error\",\"message\":\"Variable 'x' has no backing type\",\"file\":\"./test_code/Document_UnknownType_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

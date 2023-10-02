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

public class GeneratedCOETests extends GeneratedBase {
  private String cached_InfiniteCrossFormulaRecursion_1 = null;
  private String get_InfiniteCrossFormulaRecursion_1() {
    if (cached_InfiniteCrossFormulaRecursion_1 != null) {
      return cached_InfiniteCrossFormulaRecursion_1;
    }
    cached_InfiniteCrossFormulaRecursion_1 = generateTestOutput(false, "InfiniteCrossFormulaRecursion_1", "./test_code/COE_InfiniteCrossFormulaRecursion_failure.a");
    return cached_InfiniteCrossFormulaRecursion_1;
  }

  @Test
  public void testInfiniteCrossFormulaRecursionFailure() {
    assertLiveFail(get_InfiniteCrossFormulaRecursion_1());
  }

  @Test
  public void testInfiniteCrossFormulaRecursionNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_InfiniteCrossFormulaRecursion_1());
  }

  @Test
  public void testInfiniteCrossFormulaRecursionExceptionFree() {
    assertExceptionFree(get_InfiniteCrossFormulaRecursion_1());
  }

  @Test
  public void testInfiniteCrossFormulaRecursionTODOFree() {
    assertTODOFree(get_InfiniteCrossFormulaRecursion_1());
  }

  @Test
  public void stable_InfiniteCrossFormulaRecursion_1() {
    String live = get_InfiniteCrossFormulaRecursion_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:COE_InfiniteCrossFormulaRecursion_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":0,\"byte\":0}},\"severity\":1,\"source\":\"error\",\"message\":\"A cycle was detected within records: A, B, C, A\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":0,\"byte\":0}},\"severity\":1,\"source\":\"error\",\"message\":\"The record/message 'A' has the potential to create an infinite serialization\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_InfiniteCrossFormula_2 = null;
  private String get_InfiniteCrossFormula_2() {
    if (cached_InfiniteCrossFormula_2 != null) {
      return cached_InfiniteCrossFormula_2;
    }
    cached_InfiniteCrossFormula_2 = generateTestOutput(false, "InfiniteCrossFormula_2", "./test_code/COE_InfiniteCrossFormula_failure.a");
    return cached_InfiniteCrossFormula_2;
  }

  @Test
  public void testInfiniteCrossFormulaFailure() {
    assertLiveFail(get_InfiniteCrossFormula_2());
  }

  @Test
  public void testInfiniteCrossFormulaNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_InfiniteCrossFormula_2());
  }

  @Test
  public void testInfiniteCrossFormulaExceptionFree() {
    assertExceptionFree(get_InfiniteCrossFormula_2());
  }

  @Test
  public void testInfiniteCrossFormulaTODOFree() {
    assertTODOFree(get_InfiniteCrossFormula_2());
  }

  @Test
  public void stable_InfiniteCrossFormula_2() {
    String live = get_InfiniteCrossFormula_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:COE_InfiniteCrossFormula_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":0,\"byte\":0}},\"severity\":1,\"source\":\"error\",\"message\":\"A cycle was detected within records: A, B, A\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":0,\"byte\":0}},\"severity\":1,\"source\":\"error\",\"message\":\"The record/message 'A' has the potential to create an infinite serialization\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_InfiniteCycle_3 = null;
  private String get_InfiniteCycle_3() {
    if (cached_InfiniteCycle_3 != null) {
      return cached_InfiniteCycle_3;
    }
    cached_InfiniteCycle_3 = generateTestOutput(false, "InfiniteCycle_3", "./test_code/COE_InfiniteCycle_failure.a");
    return cached_InfiniteCycle_3;
  }

  @Test
  public void testInfiniteCycleFailure() {
    assertLiveFail(get_InfiniteCycle_3());
  }

  @Test
  public void testInfiniteCycleNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_InfiniteCycle_3());
  }

  @Test
  public void testInfiniteCycleExceptionFree() {
    assertExceptionFree(get_InfiniteCycle_3());
  }

  @Test
  public void testInfiniteCycleTODOFree() {
    assertTODOFree(get_InfiniteCycle_3());
  }

  @Test
  public void stable_InfiniteCycle_3() {
    String live = get_InfiniteCycle_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:COE_InfiniteCycle_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":2,\"character\":19,\"byte\":63},\"end\":{\"line\":2,\"character\":20,\"byte\":64}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'x' was not defined\",\"file\":\"./test_code/COE_InfiniteCycle_failure.a\"},{\"range\":{\"start\":{\"line\":2,\"character\":0,\"byte\":44},\"end\":{\"line\":2,\"character\":21,\"byte\":65}},\"severity\":1,\"source\":\"error\",\"message\":\"The field 'z' has no type\",\"file\":\"./test_code/COE_InfiniteCycle_failure.a\"},{\"range\":{\"start\":{\"line\":2,\"character\":0,\"byte\":44},\"end\":{\"line\":2,\"character\":21,\"byte\":65}},\"severity\":1,\"source\":\"error\",\"message\":\"Variable 'z' has no backing type\",\"file\":\"./test_code/COE_InfiniteCycle_failure.a\"},{\"range\":{\"start\":{\"line\":1,\"character\":19,\"byte\":41},\"end\":{\"line\":1,\"character\":20,\"byte\":42}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'z' was not defined\",\"file\":\"./test_code/COE_InfiniteCycle_failure.a\"},{\"range\":{\"start\":{\"line\":1,\"character\":0,\"byte\":22},\"end\":{\"line\":1,\"character\":21,\"byte\":43}},\"severity\":1,\"source\":\"error\",\"message\":\"The field 'y' has no type\",\"file\":\"./test_code/COE_InfiniteCycle_failure.a\"},{\"range\":{\"start\":{\"line\":1,\"character\":0,\"byte\":22},\"end\":{\"line\":1,\"character\":21,\"byte\":43}},\"severity\":1,\"source\":\"error\",\"message\":\"Variable 'y' has no backing type\",\"file\":\"./test_code/COE_InfiniteCycle_failure.a\"},{\"range\":{\"start\":{\"line\":0,\"character\":19,\"byte\":19},\"end\":{\"line\":0,\"character\":20,\"byte\":20}},\"severity\":1,\"source\":\"error\",\"message\":\"The variable 'y' was not defined\",\"file\":\"./test_code/COE_InfiniteCycle_failure.a\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":21,\"byte\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"The field 'x' has no type\",\"file\":\"./test_code/COE_InfiniteCycle_failure.a\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":21,\"byte\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"Variable 'x' has no backing type\",\"file\":\"./test_code/COE_InfiniteCycle_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_InfiniteInvalidate_4 = null;
  private String get_InfiniteInvalidate_4() {
    if (cached_InfiniteInvalidate_4 != null) {
      return cached_InfiniteInvalidate_4;
    }
    cached_InfiniteInvalidate_4 = generateTestOutput(false, "InfiniteInvalidate_4", "./test_code/COE_InfiniteInvalidate_failure.a");
    return cached_InfiniteInvalidate_4;
  }

  @Test
  public void testInfiniteInvalidateFailure() {
    assertLiveFail(get_InfiniteInvalidate_4());
  }

  @Test
  public void testInfiniteInvalidateNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_InfiniteInvalidate_4());
  }

  @Test
  public void testInfiniteInvalidateExceptionFree() {
    assertExceptionFree(get_InfiniteInvalidate_4());
  }

  @Test
  public void testInfiniteInvalidateTODOFree() {
    assertTODOFree(get_InfiniteInvalidate_4());
  }

  @Test
  public void stable_InfiniteInvalidate_4() {
    String live = get_InfiniteInvalidate_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:COE_InfiniteInvalidate_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":0,\"byte\":0}},\"severity\":1,\"source\":\"error\",\"message\":\"A cycle was detected within records: X, Y, X\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":0,\"byte\":0}},\"severity\":1,\"source\":\"error\",\"message\":\"The record/message 'X' has the potential to create an infinite serialization\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_InfiniteRecord_5 = null;
  private String get_InfiniteRecord_5() {
    if (cached_InfiniteRecord_5 != null) {
      return cached_InfiniteRecord_5;
    }
    cached_InfiniteRecord_5 = generateTestOutput(false, "InfiniteRecord_5", "./test_code/COE_InfiniteRecord_failure.a");
    return cached_InfiniteRecord_5;
  }

  @Test
  public void testInfiniteRecordFailure() {
    assertLiveFail(get_InfiniteRecord_5());
  }

  @Test
  public void testInfiniteRecordNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_InfiniteRecord_5());
  }

  @Test
  public void testInfiniteRecordExceptionFree() {
    assertExceptionFree(get_InfiniteRecord_5());
  }

  @Test
  public void testInfiniteRecordTODOFree() {
    assertTODOFree(get_InfiniteRecord_5());
  }

  @Test
  public void stable_InfiniteRecord_5() {
    String live = get_InfiniteRecord_5();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:COE_InfiniteRecord_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":0,\"byte\":0}},\"severity\":1,\"source\":\"error\",\"message\":\"A cycle was detected within records: R, R\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":0,\"byte\":0}},\"severity\":1,\"source\":\"error\",\"message\":\"The record/message 'R' has the potential to create an infinite serialization\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_Issue20230513_6 = null;
  private String get_Issue20230513_6() {
    if (cached_Issue20230513_6 != null) {
      return cached_Issue20230513_6;
    }
    cached_Issue20230513_6 = generateTestOutput(true, "Issue20230513_6", "./test_code/COE_Issue20230513_success.a");
    return cached_Issue20230513_6;
  }

  @Test
  public void testIssue20230513Emission() {
    assertEmissionGood(get_Issue20230513_6());
  }

  @Test
  public void testIssue20230513Success() {
    assertLivePass(get_Issue20230513_6());
  }

  @Test
  public void testIssue20230513GoodWillHappy() {
    assertGoodWillHappy(get_Issue20230513_6());
  }

  @Test
  public void testIssue20230513ExceptionFree() {
    assertExceptionFree(get_Issue20230513_6());
  }

  @Test
  public void testIssue20230513TODOFree() {
    assertTODOFree(get_Issue20230513_6());
  }

  @Test
  public void stable_Issue20230513_6() {
    String live = get_Issue20230513_6();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:COE_Issue20230513_success.a");
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
    gold.append("\npublic class Issue20230513_6 extends LivingDocument {");
    gold.append("\n  private final RxTable<RTxFamily> _families;");
    gold.append("\n  private final RxTable<RTxPerson> _people;");
    gold.append("\n  private final RxGuard ___your_family_1;");
    gold.append("\n  private final RxGuard ___your_family_2;");
    gold.append("\n  public NtMaybe<RTxFamily> __COMPUTE_your_family_1(NtPrincipal __who, RTx__ViewerType __viewer) {");
    gold.append("\n    return (_families.iterate(false).where(true, new __CLOSURE_WhereClause1(__who, _people))).lookup(0);");
    gold.append("\n  }");
    gold.append("\n  public NtMaybe<RTxFamily> __COMPUTE_your_family_2(NtPrincipal __who, RTx__ViewerType __viewer) {");
    gold.append("\n    return (_families.iterate(false).where(true, new __CLOSURE_WhereClause2(__who, _people))).lookup(0);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public long __memory() {");
    gold.append("\n    long __sum = super.__memory();");
    gold.append("\n    __sum += _families.__memory();");
    gold.append("\n    __sum += _people.__memory();");
    gold.append("\n    return __sum;");
    gold.append("\n  }");
    gold.append("\n  public Issue20230513_6(DocumentMonitor __monitor) {");
    gold.append("\n    super(__monitor);");
    gold.append("\n    _families = new RxTable<>(__self, this, \"_families\", (RxParent __parent) -> new RTxFamily(__parent).__link(), 0);");
    gold.append("\n    _people = new RxTable<>(__self, this, \"_people\", (RxParent __parent) -> new RTxPerson(__parent).__link(), 0);");
    gold.append("\n    ___your_family_1 =  new RxGuard(this);");
    gold.append("\n    _families.__subscribe(___your_family_1);");
    gold.append("\n    _people.__subscribe(___your_family_1);");
    gold.append("\n    ___your_family_2 =  new RxGuard(this);");
    gold.append("\n    _families.__subscribe(___your_family_2);");
    gold.append("\n    _people.__subscribe(___your_family_2);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __insert(JsonStreamReader __reader) {");
    gold.append("\n    if (__reader.startObject()) {");
    gold.append("\n      while(__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"_families\":");
    gold.append("\n            _families.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"_people\":");
    gold.append("\n            _people.__insert(__reader);");
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
    gold.append("\n          case \"_families\":");
    gold.append("\n            _families.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"_people\":");
    gold.append("\n            _people.__patch(__reader);");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"_families\");");
    gold.append("\n    _families.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"_people\");");
    gold.append("\n    _people.__dump(__writer);");
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
    gold.append("\n    _families.__commit(\"_families\", __forward, __reverse);");
    gold.append("\n    _people.__commit(\"_people\", __forward, __reverse);");
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
    gold.append("\n    _families.__revert();");
    gold.append("\n    _people.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  public class DeltaPrivacyCache {");
    gold.append("\n    public DeltaPrivacyCache(NtPrincipal __who) {}");
    gold.append("\n  }");
    gold.append("\n  private class DeltaIssue20230513_6 implements DeltaNode {");
    gold.append("\n    private long __gyour_family_1;");
    gold.append("\n    private DMaybe<DeltaRTxFamily> __dyour_family_1;");
    gold.append("\n    private long __gyour_family_2;");
    gold.append("\n    private DMaybe<DeltaRTxFamily> __dyour_family_2;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaIssue20230513_6() {");
    gold.append("\n      __gyour_family_1 = -1;");
    gold.append("\n      __dyour_family_1 = new DMaybe<DeltaRTxFamily>();");
    gold.append("\n      __gyour_family_2 = -1;");
    gold.append("\n      __dyour_family_2 = new DMaybe<DeltaRTxFamily>();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      __sum += __dyour_family_1.__memory();");
    gold.append("\n      __sum += __dyour_family_2.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public boolean show(Issue20230513_6 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      DeltaPrivacyCache __policy_cache = new DeltaPrivacyCache(__writer.who);");
    gold.append("\n      __writer.setCacheObject(__policy_cache);");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __obj.manifest();");
    gold.append("\n      RTx__ViewerType __VIEWER = (RTx__ViewerType) __writer.viewerState;");
    gold.append("\n      long __CHECK = 0;");
    gold.append("\n      __CHECK = __item.___your_family_1.getGeneration();");
    gold.append("\n      if (__gyour_family_1 != __CHECK)  {");
    gold.append("\n        NtMaybe<RTxFamily> __local_your_family_1 = __item.__COMPUTE_your_family_1(__writer.who, __VIEWER);");
    gold.append("\n        if (__local_your_family_1.has()) {");
    gold.append("\n          RTxFamily __maybeElement1 = (RTxFamily)(__local_your_family_1.get());");
    gold.append("\n          DeltaRTxFamily __maybeDeltaElement2 = __dyour_family_1.get(() -> new DeltaRTxFamily());");
    gold.append("\n          __maybeDeltaElement2.show(__maybeElement1, __obj.planField(\"your_family_1\"));");
    gold.append("\n        } else {");
    gold.append("\n          __dyour_family_1.hide(__obj.planField(\"your_family_1\"));");
    gold.append("\n        }");
    gold.append("\n        __gyour_family_1 = __CHECK;");
    gold.append("\n      }");
    gold.append("\n      __CHECK = __item.___your_family_2.getGeneration();");
    gold.append("\n      if (__gyour_family_2 != __CHECK)  {");
    gold.append("\n        NtMaybe<RTxFamily> __local_your_family_2 = __item.__COMPUTE_your_family_2(__writer.who, __VIEWER);");
    gold.append("\n        if (__local_your_family_2.has()) {");
    gold.append("\n          RTxFamily __maybeElement3 = (RTxFamily)(__local_your_family_2.get());");
    gold.append("\n          DeltaRTxFamily __maybeDeltaElement4 = __dyour_family_2.get(() -> new DeltaRTxFamily());");
    gold.append("\n          __maybeDeltaElement4.show(__maybeElement3, __obj.planField(\"your_family_2\"));");
    gold.append("\n        } else {");
    gold.append("\n          __dyour_family_2.hide(__obj.planField(\"your_family_2\"));");
    gold.append("\n        }");
    gold.append("\n        __gyour_family_2 = __CHECK;");
    gold.append("\n      }");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n      return true;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __dyour_family_1.clear();");
    gold.append("\n      __dyour_family_2.clear();");
    gold.append("\n      __code_cost += 2;");
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
    gold.append("\n    Issue20230513_6 __self = this;");
    gold.append("\n    DeltaIssue20230513_6 __state = new DeltaIssue20230513_6();");
    gold.append("\n    RTx__ViewerType __viewerState = new RTx__ViewerType();");
    gold.append("\n    return new PrivateView(__genViewId(), __who, ___perspective, __encoder) {");
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
    gold.append("\n        __state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer, __viewerState, __encoder));");
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
    gold.append("\n  private static String[] __INDEX_COLUMNS_Family = new String[] {};");
    gold.append("\n  private class RTxFamily extends RxRecordBase<RTxFamily> {");
    gold.append("\n    private final RTxFamily __this;");
    gold.append("\n    private final RxInt32 id;");
    gold.append("\n    private final RxString name;");
    gold.append("\n    private RTxFamily(RxParent __owner) {");
    gold.append("\n      super(__owner);");
    gold.append("\n      this.__this = this;");
    gold.append("\n      id = new RxInt32(this, 0);");
    gold.append("\n      name = new RxString(this, \"\");");
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
    gold.append("\n      __sum += name.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS_Family;");
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
    gold.append("\n        case \"name\":");
    gold.append("\n          return name;");
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
    gold.append("\n            case \"name\":");
    gold.append("\n              name.__insert(__reader);");
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
    gold.append("\n            case \"name\":");
    gold.append("\n              name.__patch(__reader);");
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
    gold.append("\n      __writer.writeObjectFieldIntro(\"name\");");
    gold.append("\n      name.__dump(__writer);");
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
    gold.append("\n        name.__commit(\"name\", __forward, __reverse);");
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
    gold.append("\n        name.__revert();");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __killFields() {}");
    gold.append("\n    @Override");
    gold.append("\n    public RTxFamily __link() {");
    gold.append("\n      return this;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __pumpIndexEvents(TablePubSub __pubsub) {}");
    gold.append("\n    @Override");
    gold.append("\n    public String __name() {");
    gold.append("\n      return \"Family\";");
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
    gold.append("\n  private class DeltaRTxFamily implements DeltaNode {");
    gold.append("\n    private DInt32 __did;");
    gold.append("\n    private DString __dname;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTxFamily() {");
    gold.append("\n      __did = new DInt32();");
    gold.append("\n      __dname = new DString();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      __sum += __did.__memory();");
    gold.append("\n      __sum += __dname.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public boolean show(RTxFamily __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      DeltaPrivacyCache __policy_cache = (DeltaPrivacyCache) __writer.getCacheObject();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __did.show(__item.id.get(), __obj.planField(\"id\"));");
    gold.append("\n      __dname.show(__item.name.get(), __obj.planField(\"name\"));");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n      return true;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __did.clear();");
    gold.append("\n      __dname.clear();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n    }");
    gold.append("\n    public void hide(PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      if (__emitted) {");
    gold.append("\n        clear();");
    gold.append("\n        __emitted = false;");
    gold.append("\n        __writer.writeNull();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  class DynCmp_RTxFamily implements Comparator<RTxFamily> {");
    gold.append("\n    private final CompareField[] parsed;");
    gold.append("\n    DynCmp_RTxFamily(String instructions) {");
    gold.append("\n      this.parsed = DynCompareParser.parse(instructions);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int compare(RTxFamily __a, RTxFamily __b) {");
    gold.append("\n      for (CompareField field : parsed) {");
    gold.append("\n        int delta = 0;");
    gold.append("\n        switch (field.name) {");
    gold.append("\n          case \"id\":");
    gold.append("\n            delta = __a.id.compareTo(__b.id);");
    gold.append("\n            break;");
    gold.append("\n          case \"name\":");
    gold.append("\n            delta = __a.name.compareTo(__b.name);");
    gold.append("\n            break;");
    gold.append("\n        }");
    gold.append("\n        if (delta != 0) {");
    gold.append("\n          return field.desc ? -delta : delta;");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n      return 0;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static String[] __INDEX_COLUMNS_Person = new String[] {};");
    gold.append("\n  private class RTxPerson extends RxRecordBase<RTxPerson> {");
    gold.append("\n    private final RTxPerson __this;");
    gold.append("\n    private final RxInt32 id;");
    gold.append("\n    private final RxPrincipal account;");
    gold.append("\n    private final RxInt32 family_id;");
    gold.append("\n    private RTxPerson(RxParent __owner) {");
    gold.append("\n      super(__owner);");
    gold.append("\n      this.__this = this;");
    gold.append("\n      id = new RxInt32(this, 0);");
    gold.append("\n      account = new RxPrincipal(this, NtPrincipal.NO_ONE);");
    gold.append("\n      family_id = new RxInt32(this, 0);");
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
    gold.append("\n      __sum += account.__memory();");
    gold.append("\n      __sum += family_id.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS_Person;");
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
    gold.append("\n        case \"account\":");
    gold.append("\n          return account;");
    gold.append("\n        case \"family_id\":");
    gold.append("\n          return family_id;");
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
    gold.append("\n            case \"account\":");
    gold.append("\n              account.__insert(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"family_id\":");
    gold.append("\n              family_id.__insert(__reader);");
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
    gold.append("\n            case \"account\":");
    gold.append("\n              account.__patch(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"family_id\":");
    gold.append("\n              family_id.__patch(__reader);");
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
    gold.append("\n      __writer.writeObjectFieldIntro(\"account\");");
    gold.append("\n      account.__dump(__writer);");
    gold.append("\n      __writer.writeObjectFieldIntro(\"family_id\");");
    gold.append("\n      family_id.__dump(__writer);");
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
    gold.append("\n        account.__commit(\"account\", __forward, __reverse);");
    gold.append("\n        family_id.__commit(\"family_id\", __forward, __reverse);");
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
    gold.append("\n        account.__revert();");
    gold.append("\n        family_id.__revert();");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __killFields() {}");
    gold.append("\n    @Override");
    gold.append("\n    public RTxPerson __link() {");
    gold.append("\n      return this;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __pumpIndexEvents(TablePubSub __pubsub) {}");
    gold.append("\n    @Override");
    gold.append("\n    public String __name() {");
    gold.append("\n      return \"Person\";");
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
    gold.append("\n  private class DeltaRTxPerson implements DeltaNode {");
    gold.append("\n    private DInt32 __did;");
    gold.append("\n    private DPrincipal __daccount;");
    gold.append("\n    private DInt32 __dfamily_id;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTxPerson() {");
    gold.append("\n      __did = new DInt32();");
    gold.append("\n      __daccount = new DPrincipal();");
    gold.append("\n      __dfamily_id = new DInt32();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      __sum += __did.__memory();");
    gold.append("\n      __sum += __daccount.__memory();");
    gold.append("\n      __sum += __dfamily_id.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public boolean show(RTxPerson __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      DeltaPrivacyCache __policy_cache = (DeltaPrivacyCache) __writer.getCacheObject();");
    gold.append("\n      __code_cost += 3;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __did.show(__item.id.get(), __obj.planField(\"id\"));");
    gold.append("\n      __daccount.show(__item.account.get(), __obj.planField(\"account\"));");
    gold.append("\n      __dfamily_id.show(__item.family_id.get(), __obj.planField(\"family_id\"));");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n      return true;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __did.clear();");
    gold.append("\n      __daccount.clear();");
    gold.append("\n      __dfamily_id.clear();");
    gold.append("\n      __code_cost += 3;");
    gold.append("\n    }");
    gold.append("\n    public void hide(PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      if (__emitted) {");
    gold.append("\n        clear();");
    gold.append("\n        __emitted = false;");
    gold.append("\n        __writer.writeNull();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  class DynCmp_RTxPerson implements Comparator<RTxPerson> {");
    gold.append("\n    private final CompareField[] parsed;");
    gold.append("\n    DynCmp_RTxPerson(String instructions) {");
    gold.append("\n      this.parsed = DynCompareParser.parse(instructions);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int compare(RTxPerson __a, RTxPerson __b) {");
    gold.append("\n      for (CompareField field : parsed) {");
    gold.append("\n        int delta = 0;");
    gold.append("\n        switch (field.name) {");
    gold.append("\n          case \"id\":");
    gold.append("\n            delta = __a.id.compareTo(__b.id);");
    gold.append("\n            break;");
    gold.append("\n          case \"account\":");
    gold.append("\n            delta = __a.account.compareTo(__b.account);");
    gold.append("\n            break;");
    gold.append("\n          case \"family_id\":");
    gold.append("\n            delta = __a.family_id.compareTo(__b.family_id);");
    gold.append("\n            break;");
    gold.append("\n        }");
    gold.append("\n        if (delta != 0) {");
    gold.append("\n          return field.desc ? -delta : delta;");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n      return 0;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private int __FUNC_0_your_family_id_or_zero(NtPrincipal w) {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(0);");
    gold.append("\n    NtMaybe<RTxPerson> _AutoConditionperson_5;");
    gold.append("\n    if ((_AutoConditionperson_5 = (_people.iterate(false).where(true, new __CLOSURE_WhereClause0(w))).lookup(0)).has()) {");
    gold.append("\n      RTxPerson person = _AutoConditionperson_5.get();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(1);");
    gold.append("\n      return person.family_id.get();");
    gold.append("\n    }");
    gold.append("\n    __track(2);");
    gold.append("\n    return 0;");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"_families\");");
    gold.append("\n    _families.debug(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"_people\");");
    gold.append("\n    _people.debug(__writer);");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(CoreRequestContext _c, NtMessageBase _m) {}");
    gold.append("\n  @Override");
    gold.append("\n  protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  private class __CLOSURE_WhereClause0 implements WhereClause<RTxPerson> {");
    gold.append("\n    private NtPrincipal w;");
    gold.append("\n    @Override");
    gold.append("\n    public int[] getIndices() {");
    gold.append("\n      return new int[] {};");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void scopeByIndicies(IndexQuerySet __set) {}");
    gold.append("\n    @Override");
    gold.append("\n    public Integer getPrimaryKey() {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n    private __CLOSURE_WhereClause0(NtPrincipal w) {");
    gold.append("\n      this.w = w;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public boolean test(RTxPerson __obj) {");
    gold.append("\n      NtPrincipal account = __obj.account.get();");
    gold.append("\n      __code_cost ++;");
    gold.append("\n      return (account).equals(w);");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private class __CLOSURE_WhereClause1 implements WhereClause<RTxFamily> {");
    gold.append("\n    private NtPrincipal __who;");
    gold.append("\n    private RxTable<RTxPerson> _people;");
    gold.append("\n    @Override");
    gold.append("\n    public int[] getIndices() {");
    gold.append("\n      return new int[] {};");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void scopeByIndicies(IndexQuerySet __set) {}");
    gold.append("\n    @Override");
    gold.append("\n    public Integer getPrimaryKey() {");
    gold.append("\n      return __FUNC_0_your_family_id_or_zero(__who);");
    gold.append("\n    }");
    gold.append("\n    private __CLOSURE_WhereClause1(NtPrincipal __who, RxTable<RTxPerson> _people) {");
    gold.append("\n      this.__who = __who;");
    gold.append("\n      this._people = _people;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public boolean test(RTxFamily __obj) {");
    gold.append("\n      int id = __obj.id.get();");
    gold.append("\n      __code_cost ++;");
    gold.append("\n      return ((int) id) == ((int) __FUNC_0_your_family_id_or_zero(__who));");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private class __CLOSURE_WhereClause2 implements WhereClause<RTxFamily> {");
    gold.append("\n    private NtPrincipal __who;");
    gold.append("\n    private RxTable<RTxPerson> _people;");
    gold.append("\n    @Override");
    gold.append("\n    public int[] getIndices() {");
    gold.append("\n      return new int[] {};");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void scopeByIndicies(IndexQuerySet __set) {}");
    gold.append("\n    @Override");
    gold.append("\n    public Integer getPrimaryKey() {");
    gold.append("\n      return LibMath.forceId((_people.iterate(false).where(true, new __CLOSURE_WhereClause3(__who))).lookup(0).unpack((item) -> ((RTxPerson) item).family_id.get()));");
    gold.append("\n    }");
    gold.append("\n    private __CLOSURE_WhereClause2(NtPrincipal __who, RxTable<RTxPerson> _people) {");
    gold.append("\n      this.__who = __who;");
    gold.append("\n      this._people = _people;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public boolean test(RTxFamily __obj) {");
    gold.append("\n      int id = __obj.id.get();");
    gold.append("\n      __code_cost ++;");
    gold.append("\n      return LibMath.equality((_people.iterate(false).where(true, new __CLOSURE_WhereClause3(__who))).lookup(0).unpack((item) -> ((RTxPerson) item).family_id.get()), id, (__x, __y) -> __x.intValue() == (int) __y);");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private class __CLOSURE_WhereClause3 implements WhereClause<RTxPerson> {");
    gold.append("\n    private NtPrincipal __who;");
    gold.append("\n    @Override");
    gold.append("\n    public int[] getIndices() {");
    gold.append("\n      return new int[] {};");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void scopeByIndicies(IndexQuerySet __set) {}");
    gold.append("\n    @Override");
    gold.append("\n    public Integer getPrimaryKey() {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n    private __CLOSURE_WhereClause3(NtPrincipal __who) {");
    gold.append("\n      this.__who = __who;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public boolean test(RTxPerson __obj) {");
    gold.append("\n      NtPrincipal account = __obj.account.get();");
    gold.append("\n      __code_cost ++;");
    gold.append("\n      return (account).equals(__who);");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--=[LivingDocumentFactory COMPILING]=---");
    gold.append("\n--=[LivingDocumentFactory MADE]=---");
    gold.append("\n--REFLECTION RESULTS-------------------------------------");
    gold.append("\n{\"types\":{\"__Root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{\"_families\":{\"type\":{\"nature\":\"reactive_table\",\"record_name\":\"Family\"},\"privacy\":\"private\"},\"_people\":{\"type\":{\"nature\":\"reactive_table\",\"record_name\":\"Person\"},\"privacy\":\"private\"},\"your_family_1\":{\"type\":{\"nature\":\"native_maybe\",\"type\":{\"nature\":\"reactive_ref\",\"ref\":\"Family\"}},\"privacy\":\"bubble\"},\"your_family_2\":{\"type\":{\"nature\":\"native_maybe\",\"type\":{\"nature\":\"reactive_ref\",\"ref\":\"Family\"}},\"privacy\":\"bubble\"}}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}},\"Family\":{\"nature\":\"reactive_record\",\"name\":\"Family\",\"fields\":{\"id\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"public\"},\"name\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"string\"},\"privacy\":\"public\"}}},\"Person\":{\"nature\":\"reactive_record\",\"name\":\"Person\",\"fields\":{\"id\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"public\"},\"account\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"principal\"},\"privacy\":\"public\"},\"family_id\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"public\"}}}},\"channels\":{},\"channels-privacy\":{},\"constructors\":[],\"labels\":[]}");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\",\"key\":\"0\",\"origin\":\"origin\",\"ip\":\"ip\"}-->{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__messages\":null,\"__seq\":1} need:false in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\nCPU:0");
    gold.append("\nMEMORY:624");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seq\":3,\"__entropy\":\"-1034601897293430941\",\"__time\":\"50\"} need:false in:-50");
    gold.append("\nNO_ONE: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":3}");
    gold.append("\nNO_ONE|FAILURE:184333");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"75\"}-->{\"__messages\":null,\"__seq\":4,\"__entropy\":\"7848011421992302230\",\"__time\":\"75\"} need:false in:-75");
    gold.append("\nRANDO: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":4}");
    gold.append("\n+ RANDO DELTA:{\"seq\":4}");
    gold.append("\nRANDO|FAILURE:184333");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\nRANDO|SUCCESS:5");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":5}");
    gold.append("\n+ RANDO DELTA:{\"seq\":5}");
    gold.append("\nMEMORY:902");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"_families\":{},\"_people\":{},\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{}");
    gold.append("\n--METRIC RESULTS-----------------------------------");
    gold.append("\n{\"_families\":{},\"_people\":{},\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n{\"_families\":{},\"_people\":{},\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
}

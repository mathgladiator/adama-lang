/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedEnumsTests extends GeneratedBase {
  private String cached_CantCrossTypes_1 = null;
  private String get_CantCrossTypes_1() {
    if (cached_CantCrossTypes_1 != null) {
      return cached_CantCrossTypes_1;
    }
    cached_CantCrossTypes_1 = generateTestOutput(false, "CantCrossTypes_1", "./test_code/Enums_CantCrossTypes_failure.a");
    return cached_CantCrossTypes_1;
  }

  @Test
  public void testCantCrossTypesFailure() {
    assertLiveFail(get_CantCrossTypes_1());
  }

  @Test
  public void testCantCrossTypesNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_CantCrossTypes_1());
  }

  @Test
  public void testCantCrossTypesExceptionFree() {
    assertExceptionFree(get_CantCrossTypes_1());
  }

  @Test
  public void testCantCrossTypesTODOFree() {
    assertTODOFree(get_CantCrossTypes_1());
  }

  @Test
  public void stable_CantCrossTypes_1() {
    String live = get_CantCrossTypes_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_CantCrossTypes_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":12,\"byte\":12}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: enum types are incompatible 'X' vs 'T'. (Assignment)\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":12,\"byte\":12}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the type 'X' is unable to store type 'T'. (TypeCheckReferences)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_CantDefineDuplicates_2 = null;
  private String get_CantDefineDuplicates_2() {
    if (cached_CantDefineDuplicates_2 != null) {
      return cached_CantDefineDuplicates_2;
    }
    cached_CantDefineDuplicates_2 = generateTestOutput(false, "CantDefineDuplicates_2", "./test_code/Enums_CantDefineDuplicates_failure.a");
    return cached_CantDefineDuplicates_2;
  }

  @Test
  public void testCantDefineDuplicatesFailure() {
    assertLiveFail(get_CantDefineDuplicates_2());
  }

  @Test
  public void testCantDefineDuplicatesNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_CantDefineDuplicates_2());
  }

  @Test
  public void testCantDefineDuplicatesExceptionFree() {
    assertExceptionFree(get_CantDefineDuplicates_2());
  }

  @Test
  public void testCantDefineDuplicatesTODOFree() {
    assertTODOFree(get_CantDefineDuplicates_2());
  }

  @Test
  public void stable_CantDefineDuplicates_2() {
    String live = get_CantDefineDuplicates_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_CantDefineDuplicates_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":32,\"byte\":32}},\"severity\":1,\"source\":\"error\",\"message\":\"The enumeration 'E' has duplicates for 'X' defined. (DocumentDefine)\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":32,\"byte\":32}},\"severity\":1,\"source\":\"error\",\"message\":\"The enumeration 'E' has duplicates for 'Z' defined. (DocumentDefine)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_CantFindEnumName_3 = null;
  private String get_CantFindEnumName_3() {
    if (cached_CantFindEnumName_3 != null) {
      return cached_CantFindEnumName_3;
    }
    cached_CantFindEnumName_3 = generateTestOutput(false, "CantFindEnumName_3", "./test_code/Enums_CantFindEnumName_failure.a");
    return cached_CantFindEnumName_3;
  }

  @Test
  public void testCantFindEnumNameFailure() {
    assertLiveFail(get_CantFindEnumName_3());
  }

  @Test
  public void testCantFindEnumNameNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_CantFindEnumName_3());
  }

  @Test
  public void testCantFindEnumNameExceptionFree() {
    assertExceptionFree(get_CantFindEnumName_3());
  }

  @Test
  public void testCantFindEnumNameTODOFree() {
    assertTODOFree(get_CantFindEnumName_3());
  }

  @Test
  public void stable_CantFindEnumName_3() {
    String live = get_CantFindEnumName_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_CantFindEnumName_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":1,\"character\":1,\"byte\":11}},\"severity\":1,\"source\":\"error\",\"message\":\"enum 'X' has no values (EnumStorage)\"},{\"range\":{\"start\":{\"line\":4,\"character\":10,\"byte\":39},\"end\":{\"line\":4,\"character\":14,\"byte\":43}},\"severity\":1,\"source\":\"error\",\"message\":\"Type not found: an enumeration named 'Y' was not found. (TypeCheckReferences)\"},{\"range\":{\"start\":{\"line\":5,\"character\":10,\"byte\":56},\"end\":{\"line\":5,\"character\":14,\"byte\":60}},\"severity\":1,\"source\":\"error\",\"message\":\"Type not found: an enumeration named 'Y' was not found. (TypeCheckReferences)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_CantFindEnumValue_4 = null;
  private String get_CantFindEnumValue_4() {
    if (cached_CantFindEnumValue_4 != null) {
      return cached_CantFindEnumValue_4;
    }
    cached_CantFindEnumValue_4 = generateTestOutput(false, "CantFindEnumValue_4", "./test_code/Enums_CantFindEnumValue_failure.a");
    return cached_CantFindEnumValue_4;
  }

  @Test
  public void testCantFindEnumValueFailure() {
    assertLiveFail(get_CantFindEnumValue_4());
  }

  @Test
  public void testCantFindEnumValueNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_CantFindEnumValue_4());
  }

  @Test
  public void testCantFindEnumValueExceptionFree() {
    assertExceptionFree(get_CantFindEnumValue_4());
  }

  @Test
  public void testCantFindEnumValueTODOFree() {
    assertTODOFree(get_CantFindEnumValue_4());
  }

  @Test
  public void stable_CantFindEnumValue_4() {
    String live = get_CantFindEnumValue_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_CantFindEnumValue_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":5,\"character\":10,\"byte\":46},\"end\":{\"line\":5,\"character\":14,\"byte\":50}},\"severity\":1,\"source\":\"error\",\"message\":\"Type lookup failure: unable to find value 'x' within the enumeration 'X' (Enumerations)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_Cycle_5 = null;
  private String get_Cycle_5() {
    if (cached_Cycle_5 != null) {
      return cached_Cycle_5;
    }
    cached_Cycle_5 = generateTestOutput(true, "Cycle_5", "./test_code/Enums_Cycle_success.a");
    return cached_Cycle_5;
  }

  @Test
  public void testCycleEmission() {
    assertEmissionGood(get_Cycle_5());
  }

  @Test
  public void testCycleSuccess() {
    assertLivePass(get_Cycle_5());
  }

  @Test
  public void testCycleGoodWillHappy() {
    assertGoodWillHappy(get_Cycle_5());
  }

  @Test
  public void testCycleExceptionFree() {
    assertExceptionFree(get_Cycle_5());
  }

  @Test
  public void testCycleTODOFree() {
    assertTODOFree(get_Cycle_5());
  }

  @Test
  public void stable_Cycle_5() {
    String live = get_Cycle_5();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_Cycle_success.a");
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
    gold.append("\nimport org.adamalang.runtime.remote.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport org.adamalang.runtime.sys.*;");
    gold.append("\nimport org.adamalang.runtime.sys.web.*;");
    gold.append("\nimport org.adamalang.runtime.text.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.util.HashMap;");
    gold.append("\nimport java.util.HashSet;");
    gold.append("\nimport java.util.Map;");
    gold.append("\nimport java.util.Set;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class Cycle_5 extends LivingDocument {");
    gold.append("\n  private final RxInt32 head;");
    gold.append("\n  private final RxLazy<Integer> n1;");
    gold.append("\n  private final RxLazy<Integer> n2;");
    gold.append("\n  private final RxLazy<Integer> n3;");
    gold.append("\n  private final RxLazy<Integer> n4;");
    gold.append("\n  private final RxLazy<Integer> p3;");
    gold.append("\n  private final RxLazy<Integer> p2;");
    gold.append("\n  private final RxLazy<Integer> p1;");
    gold.append("\n  private final RxLazy<Integer> p0;");
    gold.append("\n  @Override");
    gold.append("\n  public long __memory() {");
    gold.append("\n    long __sum = super.__memory();");
    gold.append("\n    __sum += head.__memory();");
    gold.append("\n    __sum += n1.__memory();");
    gold.append("\n    __sum += n2.__memory();");
    gold.append("\n    __sum += n3.__memory();");
    gold.append("\n    __sum += n4.__memory();");
    gold.append("\n    __sum += p3.__memory();");
    gold.append("\n    __sum += p2.__memory();");
    gold.append("\n    __sum += p1.__memory();");
    gold.append("\n    __sum += p0.__memory();");
    gold.append("\n    return __sum;");
    gold.append("\n  }");
    gold.append("\n  public Cycle_5(DocumentMonitor __monitor) {");
    gold.append("\n    super(__monitor);");
    gold.append("\n    head = new RxInt32(this, 0);");
    gold.append("\n    n1 = new RxLazy<Integer>(this, () -> (__EnumCycleNext_E(head.get())));");
    gold.append("\n    head.__subscribe(n1);");
    gold.append("\n    n2 = new RxLazy<Integer>(this, () -> (__EnumCycleNext_E(n1.get())));");
    gold.append("\n    n1.__subscribe(n2);");
    gold.append("\n    n3 = new RxLazy<Integer>(this, () -> (__EnumCycleNext_E(n2.get())));");
    gold.append("\n    n2.__subscribe(n3);");
    gold.append("\n    n4 = new RxLazy<Integer>(this, () -> (__EnumCycleNext_E(n3.get())));");
    gold.append("\n    n3.__subscribe(n4);");
    gold.append("\n    p3 = new RxLazy<Integer>(this, () -> (__EnumCyclePrev_E(n4.get())));");
    gold.append("\n    n4.__subscribe(p3);");
    gold.append("\n    p2 = new RxLazy<Integer>(this, () -> (__EnumCyclePrev_E(p3.get())));");
    gold.append("\n    p3.__subscribe(p2);");
    gold.append("\n    p1 = new RxLazy<Integer>(this, () -> (__EnumCyclePrev_E(p2.get())));");
    gold.append("\n    p2.__subscribe(p1);");
    gold.append("\n    p0 = new RxLazy<Integer>(this, () -> (__EnumCyclePrev_E(p1.get())));");
    gold.append("\n    p1.__subscribe(p0);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __insert(JsonStreamReader __reader) {");
    gold.append("\n    if (__reader.startObject()) {");
    gold.append("\n      while(__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"head\":");
    gold.append("\n            head.__insert(__reader);");
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
    gold.append("\n          case \"__dedupe\":");
    gold.append("\n            __hydrateDeduper(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__clients\":");
    gold.append("\n            __hydrateClients(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__messages\":");
    gold.append("\n            __hydrateMessages(__reader);");
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
    gold.append("\n          case \"head\":");
    gold.append("\n            head.__patch(__reader);");
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
    gold.append("\n          case \"__dedupe\":");
    gold.append("\n            __hydrateDeduper(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__clients\":");
    gold.append("\n            __hydrateClients(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__messages\":");
    gold.append("\n            __hydrateMessages(__reader);");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"head\");");
    gold.append("\n    head.__dump(__writer);");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_table_row_id\");");
    gold.append("\n    __auto_table_row_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_gen\");");
    gold.append("\n    __auto_gen.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_cache_id\");");
    gold.append("\n    __auto_cache_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__cache\");");
    gold.append("\n    __cache.__dump(__writer);");
    gold.append("\n    __dumpDeduper(__writer);");
    gold.append("\n    __dumpClients(__writer);");
    gold.append("\n    __dumpMessages(__writer);");
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
    gold.append("\n    __auto_table_row_id.__commit(\"__auto_table_row_id\", __forward, __reverse);");
    gold.append("\n    __auto_gen.__commit(\"__auto_gen\", __forward, __reverse);");
    gold.append("\n    __auto_cache_id.__commit(\"__auto_cache_id\", __forward, __reverse);");
    gold.append("\n    __cache.__commit(\"__cache\", __forward, __reverse);");
    gold.append("\n    head.__commit(\"head\", __forward, __reverse);");
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
    gold.append("\n    __auto_table_row_id.__revert();");
    gold.append("\n    head.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  private class DeltaCycle_5 implements DeltaNode {");
    gold.append("\n    private DInt32 __dhead;");
    gold.append("\n    private int __gn1;");
    gold.append("\n    private DInt32 __dn1;");
    gold.append("\n    private int __gn2;");
    gold.append("\n    private DInt32 __dn2;");
    gold.append("\n    private int __gn3;");
    gold.append("\n    private DInt32 __dn3;");
    gold.append("\n    private int __gn4;");
    gold.append("\n    private DInt32 __dn4;");
    gold.append("\n    private int __gp3;");
    gold.append("\n    private DInt32 __dp3;");
    gold.append("\n    private int __gp2;");
    gold.append("\n    private DInt32 __dp2;");
    gold.append("\n    private int __gp1;");
    gold.append("\n    private DInt32 __dp1;");
    gold.append("\n    private int __gp0;");
    gold.append("\n    private DInt32 __dp0;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaCycle_5() {");
    gold.append("\n      __dhead = new DInt32();");
    gold.append("\n      __gn1 = -1;");
    gold.append("\n      __dn1 = new DInt32();");
    gold.append("\n      __gn2 = -1;");
    gold.append("\n      __dn2 = new DInt32();");
    gold.append("\n      __gn3 = -1;");
    gold.append("\n      __dn3 = new DInt32();");
    gold.append("\n      __gn4 = -1;");
    gold.append("\n      __dn4 = new DInt32();");
    gold.append("\n      __gp3 = -1;");
    gold.append("\n      __dp3 = new DInt32();");
    gold.append("\n      __gp2 = -1;");
    gold.append("\n      __dp2 = new DInt32();");
    gold.append("\n      __gp1 = -1;");
    gold.append("\n      __dp1 = new DInt32();");
    gold.append("\n      __gp0 = -1;");
    gold.append("\n      __dp0 = new DInt32();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      __sum += __dhead.__memory();");
    gold.append("\n      __sum += __dn1.__memory();");
    gold.append("\n      __sum += __dn2.__memory();");
    gold.append("\n      __sum += __dn3.__memory();");
    gold.append("\n      __sum += __dn4.__memory();");
    gold.append("\n      __sum += __dp3.__memory();");
    gold.append("\n      __sum += __dp2.__memory();");
    gold.append("\n      __sum += __dp1.__memory();");
    gold.append("\n      __sum += __dp0.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public void show(Cycle_5 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      __code_cost += 9;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __obj.manifest();");
    gold.append("\n      __dhead.show(__item.head.get(), __obj.planField(\"head\"));");
    gold.append("\n      if (__gn1 != __item.n1.getGeneration()) {");
    gold.append("\n        __dn1.show(__item.n1.get(), __obj.planField(\"n1\"));");
    gold.append("\n        __gn1 = __item.n1.getGeneration();");
    gold.append("\n      }");
    gold.append("\n      if (__gn2 != __item.n2.getGeneration()) {");
    gold.append("\n        __dn2.show(__item.n2.get(), __obj.planField(\"n2\"));");
    gold.append("\n        __gn2 = __item.n2.getGeneration();");
    gold.append("\n      }");
    gold.append("\n      if (__gn3 != __item.n3.getGeneration()) {");
    gold.append("\n        __dn3.show(__item.n3.get(), __obj.planField(\"n3\"));");
    gold.append("\n        __gn3 = __item.n3.getGeneration();");
    gold.append("\n      }");
    gold.append("\n      if (__gn4 != __item.n4.getGeneration()) {");
    gold.append("\n        __dn4.show(__item.n4.get(), __obj.planField(\"n4\"));");
    gold.append("\n        __gn4 = __item.n4.getGeneration();");
    gold.append("\n      }");
    gold.append("\n      if (__gp3 != __item.p3.getGeneration()) {");
    gold.append("\n        __dp3.show(__item.p3.get(), __obj.planField(\"p3\"));");
    gold.append("\n        __gp3 = __item.p3.getGeneration();");
    gold.append("\n      }");
    gold.append("\n      if (__gp2 != __item.p2.getGeneration()) {");
    gold.append("\n        __dp2.show(__item.p2.get(), __obj.planField(\"p2\"));");
    gold.append("\n        __gp2 = __item.p2.getGeneration();");
    gold.append("\n      }");
    gold.append("\n      if (__gp1 != __item.p1.getGeneration()) {");
    gold.append("\n        __dp1.show(__item.p1.get(), __obj.planField(\"p1\"));");
    gold.append("\n        __gp1 = __item.p1.getGeneration();");
    gold.append("\n      }");
    gold.append("\n      if (__gp0 != __item.p0.getGeneration()) {");
    gold.append("\n        __dp0.show(__item.p0.get(), __obj.planField(\"p0\"));");
    gold.append("\n        __gp0 = __item.p0.getGeneration();");
    gold.append("\n      }");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __dhead.clear();");
    gold.append("\n      __dn1.clear();");
    gold.append("\n      __dn2.clear();");
    gold.append("\n      __dn3.clear();");
    gold.append("\n      __dn4.clear();");
    gold.append("\n      __dp3.clear();");
    gold.append("\n      __dp2.clear();");
    gold.append("\n      __dp1.clear();");
    gold.append("\n      __dp0.clear();");
    gold.append("\n      __code_cost += 9;");
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
    gold.append("\n  public PrivateView __createPrivateView(NtClient __who, Perspective ___perspective, AssetIdEncoder __encoder) {");
    gold.append("\n    Cycle_5 __self = this;");
    gold.append("\n    DeltaCycle_5 __state = new DeltaCycle_5();");
    gold.append("\n    RTx__ViewerType __viewerState = new RTx__ViewerType();");
    gold.append("\n    return new PrivateView(__who, ___perspective, __encoder) {");
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
    gold.append("\n    public void __hash(HashBuilder __hash) {");
    gold.append("\n      __hash.hashString(\"anonymous\");");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType(JsonStreamReader __reader) {");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    public int __DATA_GENERATION = 1;");
    gold.append("\n    @Override");
    gold.append("\n    public void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      __reader.skipValue();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType() {}");
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
    gold.append("\n  private static final int [] __ALL_VALUES_E = new int[] {0, 1, 4};");
    gold.append("\n  private static final int __EnumCycleNext_E(int value) {");
    gold.append("\n    switch (value) {");
    gold.append("\n      case 1:");
    gold.append("\n        return 4;");
    gold.append("\n      case 4:");
    gold.append("\n        return 0;");
    gold.append("\n      default:");
    gold.append("\n        return value + 1;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final int __EnumCyclePrev_E(int value) {");
    gold.append("\n    switch (value) {");
    gold.append("\n      case 0:");
    gold.append("\n        return 4;");
    gold.append("\n      case 4:");
    gold.append("\n        return 1;");
    gold.append("\n      default:");
    gold.append("\n        return value - 1;");
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
    gold.append("\n  protected boolean __is_direct_channel(String channel) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __handle_direct(NtClient who, String channel, Object __message) throws AbortMessageException {");
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
    gold.append("\n  public WebResponse __get(WebGet __request) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected WebResponse __put_internal(WebPut __request) {");
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
    gold.append("\n  public boolean __onCanAssetAttached(NtClient __cvalue) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onAssetAttached(NtClient __cvalue, NtAsset __pvalue) {");
    gold.append("\n");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onConnected(NtClient __cvalue) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onDisconnected(NtClient __cvalue) {");
    gold.append("\n");
    gold.append("\n  }");
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
    gold.append("\n  protected void __construct_intern(NtClient __who, NtMessageBase message) {}");
    gold.append("\n  @Override");
    gold.append("\n  protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\nBegin");
    gold.append("\nEnd");
    gold.append("\n--REFLECTION RESULTS-------------------------------------");
    gold.append("\n{\"types\":{\"__Root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{\"head\":{\"type\":{\"nature\":\"reactive_enum\",\"type\":\"E\"},\"privacy\":\"public\"},\"n1\":{\"type\":{\"nature\":\"native_value\",\"enum\":\"E\",\"options\":{\"options\":{\"A\":0,\"B\":1,\"C\":4},\"default\":\"B\"}},\"privacy\":\"public\"},\"n2\":{\"type\":{\"nature\":\"native_value\",\"enum\":\"E\",\"options\":{\"options\":{\"A\":0,\"B\":1,\"C\":4},\"default\":\"B\"}},\"privacy\":\"public\"},\"n3\":{\"type\":{\"nature\":\"native_value\",\"enum\":\"E\",\"options\":{\"options\":{\"A\":0,\"B\":1,\"C\":4},\"default\":\"B\"}},\"privacy\":\"public\"},\"n4\":{\"type\":{\"nature\":\"native_value\",\"enum\":\"E\",\"options\":{\"options\":{\"A\":0,\"B\":1,\"C\":4},\"default\":\"B\"}},\"privacy\":\"public\"},\"p3\":{\"type\":{\"nature\":\"native_value\",\"enum\":\"E\",\"options\":{\"options\":{\"A\":0,\"B\":1,\"C\":4},\"default\":\"B\"}},\"privacy\":\"public\"},\"p2\":{\"type\":{\"nature\":\"native_value\",\"enum\":\"E\",\"options\":{\"options\":{\"A\":0,\"B\":1,\"C\":4},\"default\":\"B\"}},\"privacy\":\"public\"},\"p1\":{\"type\":{\"nature\":\"native_value\",\"enum\":\"E\",\"options\":{\"options\":{\"A\":0,\"B\":1,\"C\":4},\"default\":\"B\"}},\"privacy\":\"public\"},\"p0\":{\"type\":{\"nature\":\"native_value\",\"enum\":\"E\",\"options\":{\"options\":{\"A\":0,\"B\":1,\"C\":4},\"default\":\"B\"}},\"privacy\":\"public\"}}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}},\"E\":{\"nature\":\"native_value\",\"enum\":\"E\",\"options\":{\"options\":{\"A\":0,\"B\":1,\"C\":4},\"default\":\"B\"}}},\"channels\":{},\"constructors\":[],\"labels\":[]}");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__messages\":null,\"__seq\":1} need:false in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\nCPU:0");
    gold.append("\nMEMORY:752");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seq\":3,\"__entropy\":\"-1034601897293430941\",\"__time\":\"50\"} need:false in:-50");
    gold.append("\nNO_ONE: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{\"head\":0,\"n1\":1,\"n2\":4,\"n3\":0,\"n4\":1,\"p3\":0,\"p2\":4,\"p1\":1,\"p0\":0},\"seq\":3}");
    gold.append("\nNO_ONE|FAILURE:184333");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"75\"}-->{\"__messages\":null,\"__seq\":4,\"__entropy\":\"7848011421992302230\",\"__time\":\"75\"} need:false in:-75");
    gold.append("\nRANDO: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":4}");
    gold.append("\n+ RANDO DELTA:{\"data\":{\"head\":0,\"n1\":1,\"n2\":4,\"n3\":0,\"n4\":1,\"p3\":0,\"p2\":4,\"p1\":1,\"p0\":0},\"seq\":4}");
    gold.append("\nRANDO|FAILURE:184333");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\nRANDO|SUCCESS:5");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":5}");
    gold.append("\n+ RANDO DELTA:{\"seq\":5}");
    gold.append("\nMEMORY:1590");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"head\":0,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{}}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"head\":0,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{}}");
    gold.append("\n{\"head\":0,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{}}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_DispatchDisagreeReturnType1_6 = null;
  private String get_DispatchDisagreeReturnType1_6() {
    if (cached_DispatchDisagreeReturnType1_6 != null) {
      return cached_DispatchDisagreeReturnType1_6;
    }
    cached_DispatchDisagreeReturnType1_6 = generateTestOutput(false, "DispatchDisagreeReturnType1_6", "./test_code/Enums_DispatchDisagreeReturnType1_failure.a");
    return cached_DispatchDisagreeReturnType1_6;
  }

  @Test
  public void testDispatchDisagreeReturnType1Failure() {
    assertLiveFail(get_DispatchDisagreeReturnType1_6());
  }

  @Test
  public void testDispatchDisagreeReturnType1NotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatchDisagreeReturnType1_6());
  }

  @Test
  public void testDispatchDisagreeReturnType1ExceptionFree() {
    assertExceptionFree(get_DispatchDisagreeReturnType1_6());
  }

  @Test
  public void testDispatchDisagreeReturnType1TODOFree() {
    assertTODOFree(get_DispatchDisagreeReturnType1_6());
  }

  @Test
  public void stable_DispatchDisagreeReturnType1_6() {
    String live = get_DispatchDisagreeReturnType1_6();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatchDisagreeReturnType1_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":4,\"character\":0,\"byte\":23},\"end\":{\"line\":6,\"character\":1,\"byte\":65}},\"severity\":1,\"source\":\"error\",\"message\":\"Dispatcher 'x' do not agree on return type. (EnumStorage)\"},{\"range\":{\"start\":{\"line\":8,\"character\":0,\"byte\":69},\"end\":{\"line\":10,\"character\":1,\"byte\":116}},\"severity\":1,\"source\":\"error\",\"message\":\"Dispatcher 'x' do not agree on return type. (EnumStorage)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatchDisagreeReturnType2_7 = null;
  private String get_DispatchDisagreeReturnType2_7() {
    if (cached_DispatchDisagreeReturnType2_7 != null) {
      return cached_DispatchDisagreeReturnType2_7;
    }
    cached_DispatchDisagreeReturnType2_7 = generateTestOutput(false, "DispatchDisagreeReturnType2_7", "./test_code/Enums_DispatchDisagreeReturnType2_failure.a");
    return cached_DispatchDisagreeReturnType2_7;
  }

  @Test
  public void testDispatchDisagreeReturnType2Failure() {
    assertLiveFail(get_DispatchDisagreeReturnType2_7());
  }

  @Test
  public void testDispatchDisagreeReturnType2NotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatchDisagreeReturnType2_7());
  }

  @Test
  public void testDispatchDisagreeReturnType2ExceptionFree() {
    assertExceptionFree(get_DispatchDisagreeReturnType2_7());
  }

  @Test
  public void testDispatchDisagreeReturnType2TODOFree() {
    assertTODOFree(get_DispatchDisagreeReturnType2_7());
  }

  @Test
  public void stable_DispatchDisagreeReturnType2_7() {
    String live = get_DispatchDisagreeReturnType2_7();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatchDisagreeReturnType2_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":4,\"character\":0,\"byte\":23},\"end\":{\"line\":6,\"character\":1,\"byte\":65}},\"severity\":1,\"source\":\"error\",\"message\":\"Dispatcher 'x' do not agree on return type. (EnumStorage)\"},{\"range\":{\"start\":{\"line\":8,\"character\":0,\"byte\":69},\"end\":{\"line\":9,\"character\":1,\"byte\":91}},\"severity\":1,\"source\":\"error\",\"message\":\"Dispatcher 'x' do not agree on return type. (EnumStorage)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatchInvoke_8 = null;
  private String get_DispatchInvoke_8() {
    if (cached_DispatchInvoke_8 != null) {
      return cached_DispatchInvoke_8;
    }
    cached_DispatchInvoke_8 = generateTestOutput(true, "DispatchInvoke_8", "./test_code/Enums_DispatchInvoke_success.a");
    return cached_DispatchInvoke_8;
  }

  @Test
  public void testDispatchInvokeEmission() {
    assertEmissionGood(get_DispatchInvoke_8());
  }

  @Test
  public void testDispatchInvokeSuccess() {
    assertLivePass(get_DispatchInvoke_8());
  }

  @Test
  public void testDispatchInvokeGoodWillHappy() {
    assertGoodWillHappy(get_DispatchInvoke_8());
  }

  @Test
  public void testDispatchInvokeExceptionFree() {
    assertExceptionFree(get_DispatchInvoke_8());
  }

  @Test
  public void testDispatchInvokeTODOFree() {
    assertTODOFree(get_DispatchInvoke_8());
  }

  @Test
  public void stable_DispatchInvoke_8() {
    String live = get_DispatchInvoke_8();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatchInvoke_success.a");
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
    gold.append("\nimport org.adamalang.runtime.remote.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport org.adamalang.runtime.sys.*;");
    gold.append("\nimport org.adamalang.runtime.sys.web.*;");
    gold.append("\nimport org.adamalang.runtime.text.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.util.HashMap;");
    gold.append("\nimport java.util.HashSet;");
    gold.append("\nimport java.util.Map;");
    gold.append("\nimport java.util.Set;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatchInvoke_8 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxInt32 y;");
    gold.append("\n  @Override");
    gold.append("\n  public long __memory() {");
    gold.append("\n    long __sum = super.__memory();");
    gold.append("\n    __sum += x.__memory();");
    gold.append("\n    __sum += y.__memory();");
    gold.append("\n    return __sum;");
    gold.append("\n  }");
    gold.append("\n  public DispatchInvoke_8(DocumentMonitor __monitor) {");
    gold.append("\n    super(__monitor);");
    gold.append("\n    x = new RxInt32(this, 0);");
    gold.append("\n    y = new RxInt32(this, 0);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __insert(JsonStreamReader __reader) {");
    gold.append("\n    if (__reader.startObject()) {");
    gold.append("\n      while(__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"x\":");
    gold.append("\n            x.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"y\":");
    gold.append("\n            y.__insert(__reader);");
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
    gold.append("\n          case \"__dedupe\":");
    gold.append("\n            __hydrateDeduper(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__clients\":");
    gold.append("\n            __hydrateClients(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__messages\":");
    gold.append("\n            __hydrateMessages(__reader);");
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
    gold.append("\n          case \"x\":");
    gold.append("\n            x.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"y\":");
    gold.append("\n            y.__patch(__reader);");
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
    gold.append("\n          case \"__dedupe\":");
    gold.append("\n            __hydrateDeduper(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__clients\":");
    gold.append("\n            __hydrateClients(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__messages\":");
    gold.append("\n            __hydrateMessages(__reader);");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"x\");");
    gold.append("\n    x.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"y\");");
    gold.append("\n    y.__dump(__writer);");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_table_row_id\");");
    gold.append("\n    __auto_table_row_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_gen\");");
    gold.append("\n    __auto_gen.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_cache_id\");");
    gold.append("\n    __auto_cache_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__cache\");");
    gold.append("\n    __cache.__dump(__writer);");
    gold.append("\n    __dumpDeduper(__writer);");
    gold.append("\n    __dumpClients(__writer);");
    gold.append("\n    __dumpMessages(__writer);");
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
    gold.append("\n    __auto_table_row_id.__commit(\"__auto_table_row_id\", __forward, __reverse);");
    gold.append("\n    __auto_gen.__commit(\"__auto_gen\", __forward, __reverse);");
    gold.append("\n    __auto_cache_id.__commit(\"__auto_cache_id\", __forward, __reverse);");
    gold.append("\n    __cache.__commit(\"__cache\", __forward, __reverse);");
    gold.append("\n    x.__commit(\"x\", __forward, __reverse);");
    gold.append("\n    y.__commit(\"y\", __forward, __reverse);");
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
    gold.append("\n    __auto_table_row_id.__revert();");
    gold.append("\n    x.__revert();");
    gold.append("\n    y.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  private class DeltaDispatchInvoke_8 implements DeltaNode {");
    gold.append("\n    private DInt32 __dx;");
    gold.append("\n    private DInt32 __dy;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaDispatchInvoke_8() {");
    gold.append("\n      __dx = new DInt32();");
    gold.append("\n      __dy = new DInt32();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      __sum += __dx.__memory();");
    gold.append("\n      __sum += __dy.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public void show(DispatchInvoke_8 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __obj.manifest();");
    gold.append("\n      __dx.show(__item.x.get(), __obj.planField(\"x\"));");
    gold.append("\n      __dy.show(__item.y.get(), __obj.planField(\"y\"));");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __dx.clear();");
    gold.append("\n      __dy.clear();");
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
    gold.append("\n  public PrivateView __createPrivateView(NtClient __who, Perspective ___perspective, AssetIdEncoder __encoder) {");
    gold.append("\n    DispatchInvoke_8 __self = this;");
    gold.append("\n    DeltaDispatchInvoke_8 __state = new DeltaDispatchInvoke_8();");
    gold.append("\n    RTx__ViewerType __viewerState = new RTx__ViewerType();");
    gold.append("\n    return new PrivateView(__who, ___perspective, __encoder) {");
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
    gold.append("\n    public void __hash(HashBuilder __hash) {");
    gold.append("\n      __hash.hashString(\"anonymous\");");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType(JsonStreamReader __reader) {");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    public int __DATA_GENERATION = 1;");
    gold.append("\n    @Override");
    gold.append("\n    public void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      __reader.skipValue();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType() {}");
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
    gold.append("\n  private static final int [] __ALL_VALUES_E = new int[] {0, 1};");
    gold.append("\n  private static final int __EnumCycleNext_E(int value) {");
    gold.append("\n    switch (value) {");
    gold.append("\n      case 1:");
    gold.append("\n        return 0;");
    gold.append("\n      default:");
    gold.append("\n        return value + 1;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final int __EnumCyclePrev_E(int value) {");
    gold.append("\n    switch (value) {");
    gold.append("\n      case 0:");
    gold.append("\n        return 1;");
    gold.append("\n      default:");
    gold.append("\n        return value - 1;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private int __IND_DISPATCH_0_foo__0(int self, int z) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return 13;");
    gold.append("\n  }");
    gold.append("\n  private int __IND_DISPATCH_0_foo__1(int self, int z) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(1);");
    gold.append("\n    return 42;");
    gold.append("\n  }");
    gold.append("\n  private int __DISPATCH_0_foo(int __value, int z) {");
    gold.append("\n    if (__value == 0) {");
    gold.append("\n      return __IND_DISPATCH_0_foo__0(__value, z);");
    gold.append("\n    }");
    gold.append("\n    if (__value == 1) {");
    gold.append("\n      return __IND_DISPATCH_0_foo__1(__value, z);");
    gold.append("\n    }");
    gold.append("\n    return __IND_DISPATCH_0_foo__0(__value, z);");
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
    gold.append("\n  protected boolean __is_direct_channel(String channel) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __handle_direct(NtClient who, String channel, Object __message) throws AbortMessageException {");
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
    gold.append("\n  public WebResponse __get(WebGet __request) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected WebResponse __put_internal(WebPut __request) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {}");
    gold.append("\n  public boolean __onConnected__0(NtClient __who) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(2);");
    gold.append("\n    return (__who).equals(NtClient.NO_ONE);");
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
    gold.append("\n  public boolean __onCanAssetAttached(NtClient __cvalue) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onAssetAttached(NtClient __cvalue, NtAsset __pvalue) {");
    gold.append("\n");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onConnected(NtClient __cvalue) {");
    gold.append("\n    boolean __result = false;");
    gold.append("\n    if (__onConnected__0(__cvalue)) __result = true;");
    gold.append("\n    return __result;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onDisconnected(NtClient __cvalue) {");
    gold.append("\n");
    gold.append("\n  }");
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
    gold.append("\n  private void __construct_0(NtClient __who, NtMessageBase __object) {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(3);");
    gold.append("\n    x.set( __DISPATCH_0_foo(0, 1));");
    gold.append("\n    __track(4);");
    gold.append("\n    y.set( __DISPATCH_0_foo(1, 1));");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient __who, NtMessageBase __object) {");
    gold.append("\n    __construct_0(__who, __object);");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\nBegin");
    gold.append("\nEnd");
    gold.append("\n--REFLECTION RESULTS-------------------------------------");
    gold.append("\n{\"types\":{\"__Root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{\"x\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"public\"},\"y\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"public\"}}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}},\"E\":{\"nature\":\"native_value\",\"enum\":\"E\",\"options\":{\"options\":{\"X\":0,\"Y\":1},\"default\":\"X\"}}},\"channels\":{},\"constructors\":[],\"labels\":[]}");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"x\":13,\"y\":42,\"__messages\":null,\"__seq\":1} need:false in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\nCPU:7");
    gold.append("\nMEMORY:480");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seq\":3,\"__entropy\":\"-1034601897293430941\",\"__time\":\"50\"} need:false in:-50");
    gold.append("\nNO_ONE: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{\"x\":13,\"y\":42},\"seq\":3}");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"75\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__connection_id\":1,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__messages\":null} need:false in:-75");
    gold.append("\nNO_ONE|SUCCESS:4");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":5}");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seq\":6,\"__entropy\":\"488730542833106255\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\nRANDO: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":6}");
    gold.append("\n+ RANDO DELTA:{\"data\":{\"x\":13,\"y\":42},\"seq\":6}");
    gold.append("\nRANDO|FAILURE:184333");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"125\"}-->{\"__messages\":null,\"__seq\":7,\"__entropy\":\"5082315122564986995\",\"__time\":\"125\"} need:false in:-125");
    gold.append("\nRANDO|SUCCESS:7");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":7}");
    gold.append("\n+ RANDO DELTA:{\"seq\":7}");
    gold.append("\nMEMORY:758");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"x\":13,\"y\":42,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":7,\"__entropy\":\"5082315122564986995\",\"__auto_future_id\":0,\"__connection_id\":1,\"__message_id\":0,\"__time\":\"125\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"x\":13,\"y\":42,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":7,\"__entropy\":\"5082315122564986995\",\"__auto_future_id\":0,\"__connection_id\":1,\"__message_id\":0,\"__time\":\"125\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}");
    gold.append("\n{\"x\":13,\"y\":42,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":7,\"__entropy\":\"5082315122564986995\",\"__auto_future_id\":0,\"__connection_id\":1,\"__message_id\":0,\"__time\":\"125\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_DispatchManyMissing_9 = null;
  private String get_DispatchManyMissing_9() {
    if (cached_DispatchManyMissing_9 != null) {
      return cached_DispatchManyMissing_9;
    }
    cached_DispatchManyMissing_9 = generateTestOutput(false, "DispatchManyMissing_9", "./test_code/Enums_DispatchManyMissing_failure.a");
    return cached_DispatchManyMissing_9;
  }

  @Test
  public void testDispatchManyMissingFailure() {
    assertLiveFail(get_DispatchManyMissing_9());
  }

  @Test
  public void testDispatchManyMissingNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatchManyMissing_9());
  }

  @Test
  public void testDispatchManyMissingExceptionFree() {
    assertExceptionFree(get_DispatchManyMissing_9());
  }

  @Test
  public void testDispatchManyMissingTODOFree() {
    assertTODOFree(get_DispatchManyMissing_9());
  }

  @Test
  public void stable_DispatchManyMissing_9() {
    String live = get_DispatchManyMissing_9();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatchManyMissing_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":2,\"character\":1,\"byte\":31}},\"severity\":1,\"source\":\"error\",\"message\":\"Enum 'E' has a dispatcher 'x' which is incomplete and lacks: X, Z. (EnumStorage)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatchOverloading_10 = null;
  private String get_DispatchOverloading_10() {
    if (cached_DispatchOverloading_10 != null) {
      return cached_DispatchOverloading_10;
    }
    cached_DispatchOverloading_10 = generateTestOutput(true, "DispatchOverloading_10", "./test_code/Enums_DispatchOverloading_success.a");
    return cached_DispatchOverloading_10;
  }

  @Test
  public void testDispatchOverloadingEmission() {
    assertEmissionGood(get_DispatchOverloading_10());
  }

  @Test
  public void testDispatchOverloadingSuccess() {
    assertLivePass(get_DispatchOverloading_10());
  }

  @Test
  public void testDispatchOverloadingGoodWillHappy() {
    assertGoodWillHappy(get_DispatchOverloading_10());
  }

  @Test
  public void testDispatchOverloadingExceptionFree() {
    assertExceptionFree(get_DispatchOverloading_10());
  }

  @Test
  public void testDispatchOverloadingTODOFree() {
    assertTODOFree(get_DispatchOverloading_10());
  }

  @Test
  public void stable_DispatchOverloading_10() {
    String live = get_DispatchOverloading_10();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatchOverloading_success.a");
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
    gold.append("\nimport org.adamalang.runtime.remote.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport org.adamalang.runtime.sys.*;");
    gold.append("\nimport org.adamalang.runtime.sys.web.*;");
    gold.append("\nimport org.adamalang.runtime.text.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.util.HashMap;");
    gold.append("\nimport java.util.HashSet;");
    gold.append("\nimport java.util.Map;");
    gold.append("\nimport java.util.Set;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatchOverloading_10 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxInt32 y;");
    gold.append("\n  private final RxDouble u;");
    gold.append("\n  private final RxDouble v;");
    gold.append("\n  @Override");
    gold.append("\n  public long __memory() {");
    gold.append("\n    long __sum = super.__memory();");
    gold.append("\n    __sum += x.__memory();");
    gold.append("\n    __sum += y.__memory();");
    gold.append("\n    __sum += u.__memory();");
    gold.append("\n    __sum += v.__memory();");
    gold.append("\n    return __sum;");
    gold.append("\n  }");
    gold.append("\n  public DispatchOverloading_10(DocumentMonitor __monitor) {");
    gold.append("\n    super(__monitor);");
    gold.append("\n    x = new RxInt32(this, 0);");
    gold.append("\n    y = new RxInt32(this, 0);");
    gold.append("\n    u = new RxDouble(this, 0.0);");
    gold.append("\n    v = new RxDouble(this, 0.0);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __insert(JsonStreamReader __reader) {");
    gold.append("\n    if (__reader.startObject()) {");
    gold.append("\n      while(__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"x\":");
    gold.append("\n            x.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"y\":");
    gold.append("\n            y.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"u\":");
    gold.append("\n            u.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"v\":");
    gold.append("\n            v.__insert(__reader);");
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
    gold.append("\n          case \"__dedupe\":");
    gold.append("\n            __hydrateDeduper(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__clients\":");
    gold.append("\n            __hydrateClients(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__messages\":");
    gold.append("\n            __hydrateMessages(__reader);");
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
    gold.append("\n          case \"x\":");
    gold.append("\n            x.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"y\":");
    gold.append("\n            y.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"u\":");
    gold.append("\n            u.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"v\":");
    gold.append("\n            v.__patch(__reader);");
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
    gold.append("\n          case \"__dedupe\":");
    gold.append("\n            __hydrateDeduper(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__clients\":");
    gold.append("\n            __hydrateClients(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__messages\":");
    gold.append("\n            __hydrateMessages(__reader);");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"x\");");
    gold.append("\n    x.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"y\");");
    gold.append("\n    y.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"u\");");
    gold.append("\n    u.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"v\");");
    gold.append("\n    v.__dump(__writer);");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_table_row_id\");");
    gold.append("\n    __auto_table_row_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_gen\");");
    gold.append("\n    __auto_gen.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_cache_id\");");
    gold.append("\n    __auto_cache_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__cache\");");
    gold.append("\n    __cache.__dump(__writer);");
    gold.append("\n    __dumpDeduper(__writer);");
    gold.append("\n    __dumpClients(__writer);");
    gold.append("\n    __dumpMessages(__writer);");
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
    gold.append("\n    __auto_table_row_id.__commit(\"__auto_table_row_id\", __forward, __reverse);");
    gold.append("\n    __auto_gen.__commit(\"__auto_gen\", __forward, __reverse);");
    gold.append("\n    __auto_cache_id.__commit(\"__auto_cache_id\", __forward, __reverse);");
    gold.append("\n    __cache.__commit(\"__cache\", __forward, __reverse);");
    gold.append("\n    x.__commit(\"x\", __forward, __reverse);");
    gold.append("\n    y.__commit(\"y\", __forward, __reverse);");
    gold.append("\n    u.__commit(\"u\", __forward, __reverse);");
    gold.append("\n    v.__commit(\"v\", __forward, __reverse);");
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
    gold.append("\n    __auto_table_row_id.__revert();");
    gold.append("\n    x.__revert();");
    gold.append("\n    y.__revert();");
    gold.append("\n    u.__revert();");
    gold.append("\n    v.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  private class DeltaDispatchOverloading_10 implements DeltaNode {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaDispatchOverloading_10() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public void show(DispatchOverloading_10 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __obj.manifest();");
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
    gold.append("\n  @Override");
    gold.append("\n  public Set<String> __get_intern_strings() {");
    gold.append("\n    HashSet<String> __interns = new HashSet<>();");
    gold.append("\n    __interns.add(\"\");");
    gold.append("\n    __interns.add(\"?\");");
    gold.append("\n    return __interns;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public PrivateView __createPrivateView(NtClient __who, Perspective ___perspective, AssetIdEncoder __encoder) {");
    gold.append("\n    DispatchOverloading_10 __self = this;");
    gold.append("\n    DeltaDispatchOverloading_10 __state = new DeltaDispatchOverloading_10();");
    gold.append("\n    RTx__ViewerType __viewerState = new RTx__ViewerType();");
    gold.append("\n    return new PrivateView(__who, ___perspective, __encoder) {");
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
    gold.append("\n    public void __hash(HashBuilder __hash) {");
    gold.append("\n      __hash.hashString(\"anonymous\");");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType(JsonStreamReader __reader) {");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    public int __DATA_GENERATION = 1;");
    gold.append("\n    @Override");
    gold.append("\n    public void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      __reader.skipValue();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType() {}");
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
    gold.append("\n  private static final int [] __ALL_VALUES_E = new int[] {0, 1};");
    gold.append("\n  private static final int __EnumCycleNext_E(int value) {");
    gold.append("\n    switch (value) {");
    gold.append("\n      case 1:");
    gold.append("\n        return 0;");
    gold.append("\n      default:");
    gold.append("\n        return value + 1;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final int __EnumCyclePrev_E(int value) {");
    gold.append("\n    switch (value) {");
    gold.append("\n      case 0:");
    gold.append("\n        return 1;");
    gold.append("\n      default:");
    gold.append("\n        return value - 1;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private int __IND_DISPATCH_0_foo__0(int self, int z) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return 13;");
    gold.append("\n  }");
    gold.append("\n  private int __IND_DISPATCH_0_foo__1(int self, int z) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(1);");
    gold.append("\n    return 42;");
    gold.append("\n  }");
    gold.append("\n  private int __DISPATCH_0_foo(int __value, int z) {");
    gold.append("\n    if (__value == 0) {");
    gold.append("\n      return __IND_DISPATCH_0_foo__0(__value, z);");
    gold.append("\n    }");
    gold.append("\n    if (__value == 1) {");
    gold.append("\n      return __IND_DISPATCH_0_foo__1(__value, z);");
    gold.append("\n    }");
    gold.append("\n    return __IND_DISPATCH_0_foo__0(__value, z);");
    gold.append("\n  }");
    gold.append("\n  private double __IND_DISPATCH_1_foo__0(int self, double z) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(2);");
    gold.append("\n    return 13.5 + z;");
    gold.append("\n  }");
    gold.append("\n  private double __IND_DISPATCH_1_foo__1(int self, double z) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(3);");
    gold.append("\n    return 42.5 + z;");
    gold.append("\n  }");
    gold.append("\n  private double __DISPATCH_1_foo(int __value, double z) {");
    gold.append("\n    if (__value == 0) {");
    gold.append("\n      return __IND_DISPATCH_1_foo__0(__value, z);");
    gold.append("\n    }");
    gold.append("\n    if (__value == 1) {");
    gold.append("\n      return __IND_DISPATCH_1_foo__1(__value, z);");
    gold.append("\n    }");
    gold.append("\n    return __IND_DISPATCH_1_foo__0(__value, z);");
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
    gold.append("\n  protected boolean __is_direct_channel(String channel) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __handle_direct(NtClient who, String channel, Object __message) throws AbortMessageException {");
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
    gold.append("\n  public WebResponse __get(WebGet __request) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected WebResponse __put_internal(WebPut __request) {");
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
    gold.append("\n  public boolean __onCanAssetAttached(NtClient __cvalue) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onAssetAttached(NtClient __cvalue, NtAsset __pvalue) {");
    gold.append("\n");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onConnected(NtClient __cvalue) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onDisconnected(NtClient __cvalue) {");
    gold.append("\n");
    gold.append("\n  }");
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
    gold.append("\n  private void __construct_0(NtClient __who, NtMessageBase __object) {");
    gold.append("\n    __code_cost += 5;");
    gold.append("\n    __track(4);");
    gold.append("\n    x.set( __DISPATCH_0_foo(0, 1));");
    gold.append("\n    __track(5);");
    gold.append("\n    y.set( __DISPATCH_0_foo(1, 1));");
    gold.append("\n    __track(6);");
    gold.append("\n    u.set( __DISPATCH_1_foo(0, 1.5));");
    gold.append("\n    __track(7);");
    gold.append("\n    v.set( __DISPATCH_1_foo(1, 1.5));");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient __who, NtMessageBase __object) {");
    gold.append("\n    __construct_0(__who, __object);");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\nBegin");
    gold.append("\nEnd");
    gold.append("\n--REFLECTION RESULTS-------------------------------------");
    gold.append("\n{\"types\":{\"__Root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}},\"E\":{\"nature\":\"native_value\",\"enum\":\"E\",\"options\":{\"options\":{\"X\":0,\"Y\":1},\"default\":\"X\"}}},\"channels\":{},\"constructors\":[],\"labels\":[]}");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"x\":13,\"y\":42,\"u\":15.0,\"v\":44.0,\"__messages\":null,\"__seq\":1} need:false in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\nCPU:13");
    gold.append("\nMEMORY:592");
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
    gold.append("\nMEMORY:710");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"x\":13,\"y\":42,\"u\":15.0,\"v\":44.0,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{}}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"x\":13,\"y\":42,\"u\":15.0,\"v\":44.0,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{}}");
    gold.append("\n{\"x\":13,\"y\":42,\"u\":15.0,\"v\":44.0,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{}}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_DispatcherForNoEnum_11 = null;
  private String get_DispatcherForNoEnum_11() {
    if (cached_DispatcherForNoEnum_11 != null) {
      return cached_DispatcherForNoEnum_11;
    }
    cached_DispatcherForNoEnum_11 = generateTestOutput(false, "DispatcherForNoEnum_11", "./test_code/Enums_DispatcherForNoEnum_failure.a");
    return cached_DispatcherForNoEnum_11;
  }

  @Test
  public void testDispatcherForNoEnumFailure() {
    assertLiveFail(get_DispatcherForNoEnum_11());
  }

  @Test
  public void testDispatcherForNoEnumNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatcherForNoEnum_11());
  }

  @Test
  public void testDispatcherForNoEnumExceptionFree() {
    assertExceptionFree(get_DispatcherForNoEnum_11());
  }

  @Test
  public void testDispatcherForNoEnumTODOFree() {
    assertTODOFree(get_DispatcherForNoEnum_11());
  }

  @Test
  public void stable_DispatcherForNoEnum_11() {
    String live = get_DispatcherForNoEnum_11();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatcherForNoEnum_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":2,\"character\":1,\"byte\":56}},\"severity\":1,\"source\":\"error\",\"message\":\"Dispatcher 'foo' was unable to find the given enumeration type of 'E' (DocumentDefine)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatcherForWrongType_12 = null;
  private String get_DispatcherForWrongType_12() {
    if (cached_DispatcherForWrongType_12 != null) {
      return cached_DispatcherForWrongType_12;
    }
    cached_DispatcherForWrongType_12 = generateTestOutput(false, "DispatcherForWrongType_12", "./test_code/Enums_DispatcherForWrongType_failure.a");
    return cached_DispatcherForWrongType_12;
  }

  @Test
  public void testDispatcherForWrongTypeFailure() {
    assertLiveFail(get_DispatcherForWrongType_12());
  }

  @Test
  public void testDispatcherForWrongTypeNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatcherForWrongType_12());
  }

  @Test
  public void testDispatcherForWrongTypeExceptionFree() {
    assertExceptionFree(get_DispatcherForWrongType_12());
  }

  @Test
  public void testDispatcherForWrongTypeTODOFree() {
    assertTODOFree(get_DispatcherForWrongType_12());
  }

  @Test
  public void stable_DispatcherForWrongType_12() {
    String live = get_DispatcherForWrongType_12();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatcherForWrongType_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":2,\"character\":0,\"byte\":15},\"end\":{\"line\":4,\"character\":1,\"byte\":71}},\"severity\":1,\"source\":\"error\",\"message\":\"Dispatcher 'foo' found 'E', but it was 'E' (DocumentDefine)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatcherFoundNoValues_13 = null;
  private String get_DispatcherFoundNoValues_13() {
    if (cached_DispatcherFoundNoValues_13 != null) {
      return cached_DispatcherFoundNoValues_13;
    }
    cached_DispatcherFoundNoValues_13 = generateTestOutput(false, "DispatcherFoundNoValues_13", "./test_code/Enums_DispatcherFoundNoValues_failure.a");
    return cached_DispatcherFoundNoValues_13;
  }

  @Test
  public void testDispatcherFoundNoValuesFailure() {
    assertLiveFail(get_DispatcherFoundNoValues_13());
  }

  @Test
  public void testDispatcherFoundNoValuesNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatcherFoundNoValues_13());
  }

  @Test
  public void testDispatcherFoundNoValuesExceptionFree() {
    assertExceptionFree(get_DispatcherFoundNoValues_13());
  }

  @Test
  public void testDispatcherFoundNoValuesTODOFree() {
    assertTODOFree(get_DispatcherFoundNoValues_13());
  }

  @Test
  public void stable_DispatcherFoundNoValues_13() {
    String live = get_DispatcherFoundNoValues_13();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatcherFoundNoValues_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":4,\"character\":0,\"byte\":23},\"end\":{\"line\":6,\"character\":1,\"byte\":60}},\"severity\":1,\"source\":\"error\",\"message\":\"Dispatcher 'foo' has a value prefix 'C' which does not relate to any value within enum 'X' (EnumStorage)\"},{\"range\":{\"start\":{\"line\":5,\"character\":2,\"byte\":48},\"end\":{\"line\":5,\"character\":11,\"byte\":57}},\"severity\":1,\"source\":\"error\",\"message\":\"The return statement expects no expression (ReturnFlow)\"},{\"range\":{\"start\":{\"line\":9,\"character\":2,\"byte\":89},\"end\":{\"line\":9,\"character\":11,\"byte\":98}},\"severity\":1,\"source\":\"error\",\"message\":\"The return statement expects no expression (ReturnFlow)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatcherLacksCoverage_14 = null;
  private String get_DispatcherLacksCoverage_14() {
    if (cached_DispatcherLacksCoverage_14 != null) {
      return cached_DispatcherLacksCoverage_14;
    }
    cached_DispatcherLacksCoverage_14 = generateTestOutput(false, "DispatcherLacksCoverage_14", "./test_code/Enums_DispatcherLacksCoverage_failure.a");
    return cached_DispatcherLacksCoverage_14;
  }

  @Test
  public void testDispatcherLacksCoverageFailure() {
    assertLiveFail(get_DispatcherLacksCoverage_14());
  }

  @Test
  public void testDispatcherLacksCoverageNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatcherLacksCoverage_14());
  }

  @Test
  public void testDispatcherLacksCoverageExceptionFree() {
    assertExceptionFree(get_DispatcherLacksCoverage_14());
  }

  @Test
  public void testDispatcherLacksCoverageTODOFree() {
    assertTODOFree(get_DispatcherLacksCoverage_14());
  }

  @Test
  public void stable_DispatcherLacksCoverage_14() {
    String live = get_DispatcherLacksCoverage_14();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatcherLacksCoverage_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":2,\"character\":1,\"byte\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"Enum 'X' has a dispatcher 'foo' which is incomplete and lacks: B. (EnumStorage)\"},{\"range\":{\"start\":{\"line\":5,\"character\":2,\"byte\":48},\"end\":{\"line\":5,\"character\":11,\"byte\":57}},\"severity\":1,\"source\":\"error\",\"message\":\"The return statement expects no expression (ReturnFlow)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatcherMustHaveNoOverlapWhenReturnValue_15 = null;
  private String get_DispatcherMustHaveNoOverlapWhenReturnValue_15() {
    if (cached_DispatcherMustHaveNoOverlapWhenReturnValue_15 != null) {
      return cached_DispatcherMustHaveNoOverlapWhenReturnValue_15;
    }
    cached_DispatcherMustHaveNoOverlapWhenReturnValue_15 = generateTestOutput(false, "DispatcherMustHaveNoOverlapWhenReturnValue_15", "./test_code/Enums_DispatcherMustHaveNoOverlapWhenReturnValue_failure.a");
    return cached_DispatcherMustHaveNoOverlapWhenReturnValue_15;
  }

  @Test
  public void testDispatcherMustHaveNoOverlapWhenReturnValueFailure() {
    assertLiveFail(get_DispatcherMustHaveNoOverlapWhenReturnValue_15());
  }

  @Test
  public void testDispatcherMustHaveNoOverlapWhenReturnValueNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatcherMustHaveNoOverlapWhenReturnValue_15());
  }

  @Test
  public void testDispatcherMustHaveNoOverlapWhenReturnValueExceptionFree() {
    assertExceptionFree(get_DispatcherMustHaveNoOverlapWhenReturnValue_15());
  }

  @Test
  public void testDispatcherMustHaveNoOverlapWhenReturnValueTODOFree() {
    assertTODOFree(get_DispatcherMustHaveNoOverlapWhenReturnValue_15());
  }

  @Test
  public void stable_DispatcherMustHaveNoOverlapWhenReturnValue_15() {
    String live = get_DispatcherMustHaveNoOverlapWhenReturnValue_15();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatcherMustHaveNoOverlapWhenReturnValue_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":4,\"character\":0,\"byte\":41},\"end\":{\"line\":6,\"character\":1,\"byte\":97}},\"severity\":1,\"source\":\"error\",\"message\":\"Dispatcher 'foo' returns and matches too many for 'X' (EnumStorage)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatcherWithShouldReturn_16 = null;
  private String get_DispatcherWithShouldReturn_16() {
    if (cached_DispatcherWithShouldReturn_16 != null) {
      return cached_DispatcherWithShouldReturn_16;
    }
    cached_DispatcherWithShouldReturn_16 = generateTestOutput(false, "DispatcherWithShouldReturn_16", "./test_code/Enums_DispatcherWithShouldReturn_failure.a");
    return cached_DispatcherWithShouldReturn_16;
  }

  @Test
  public void testDispatcherWithShouldReturnFailure() {
    assertLiveFail(get_DispatcherWithShouldReturn_16());
  }

  @Test
  public void testDispatcherWithShouldReturnNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatcherWithShouldReturn_16());
  }

  @Test
  public void testDispatcherWithShouldReturnExceptionFree() {
    assertExceptionFree(get_DispatcherWithShouldReturn_16());
  }

  @Test
  public void testDispatcherWithShouldReturnTODOFree() {
    assertTODOFree(get_DispatcherWithShouldReturn_16());
  }

  @Test
  public void stable_DispatcherWithShouldReturn_16() {
    String live = get_DispatcherWithShouldReturn_16();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatcherWithShouldReturn_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":3,\"character\":0,\"byte\":20},\"end\":{\"line\":4,\"character\":1,\"byte\":63}},\"severity\":1,\"source\":\"error\",\"message\":\"Dispatch 'foo' does not return in all cases (DefineDispatcher)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

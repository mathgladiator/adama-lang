/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
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
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 12");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type check failure: enum types are incompatible 'X' vs 'T'. (Assignment)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 12");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type check failure: the type 'X' is unable to store type 'T'. (TypeCheckReferences)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
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
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 32");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"The enumeration 'E' has duplicates for 'X' defined. (DocumentDefine)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 32");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"The enumeration 'E' has duplicates for 'Z' defined. (DocumentDefine)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
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
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 1");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"enum 'X' has no values (EnumStorage)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 4,");
    gold.append("\n      \"character\" : 10");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 4,");
    gold.append("\n      \"character\" : 14");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type not found: an enumeration named 'Y' was not found. (TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 5,");
    gold.append("\n      \"character\" : 10");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 5,");
    gold.append("\n      \"character\" : 14");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type not found: an enumeration named 'Y' was not found. (TypeCheckReferences)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
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
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 5,");
    gold.append("\n      \"character\" : 10");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 5,");
    gold.append("\n      \"character\" : 14");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type lookup failure: unable to find value 'x' within the enumeration 'X' (Enumerations)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatchDisagreeReturnType1_5 = null;
  private String get_DispatchDisagreeReturnType1_5() {
    if (cached_DispatchDisagreeReturnType1_5 != null) {
      return cached_DispatchDisagreeReturnType1_5;
    }
    cached_DispatchDisagreeReturnType1_5 = generateTestOutput(false, "DispatchDisagreeReturnType1_5", "./test_code/Enums_DispatchDisagreeReturnType1_failure.a");
    return cached_DispatchDisagreeReturnType1_5;
  }

  @Test
  public void testDispatchDisagreeReturnType1Failure() {
    assertLiveFail(get_DispatchDisagreeReturnType1_5());
  }

  @Test
  public void testDispatchDisagreeReturnType1NotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatchDisagreeReturnType1_5());
  }

  @Test
  public void testDispatchDisagreeReturnType1ExceptionFree() {
    assertExceptionFree(get_DispatchDisagreeReturnType1_5());
  }

  @Test
  public void testDispatchDisagreeReturnType1TODOFree() {
    assertTODOFree(get_DispatchDisagreeReturnType1_5());
  }

  @Test
  public void stable_DispatchDisagreeReturnType1_5() {
    String live = get_DispatchDisagreeReturnType1_5();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatchDisagreeReturnType1_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 4,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 6,");
    gold.append("\n      \"character\" : 1");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Dispatcher 'x' do not agree on return type. (EnumStorage)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 8,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 10,");
    gold.append("\n      \"character\" : 1");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Dispatcher 'x' do not agree on return type. (EnumStorage)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatchDisagreeReturnType2_6 = null;
  private String get_DispatchDisagreeReturnType2_6() {
    if (cached_DispatchDisagreeReturnType2_6 != null) {
      return cached_DispatchDisagreeReturnType2_6;
    }
    cached_DispatchDisagreeReturnType2_6 = generateTestOutput(false, "DispatchDisagreeReturnType2_6", "./test_code/Enums_DispatchDisagreeReturnType2_failure.a");
    return cached_DispatchDisagreeReturnType2_6;
  }

  @Test
  public void testDispatchDisagreeReturnType2Failure() {
    assertLiveFail(get_DispatchDisagreeReturnType2_6());
  }

  @Test
  public void testDispatchDisagreeReturnType2NotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatchDisagreeReturnType2_6());
  }

  @Test
  public void testDispatchDisagreeReturnType2ExceptionFree() {
    assertExceptionFree(get_DispatchDisagreeReturnType2_6());
  }

  @Test
  public void testDispatchDisagreeReturnType2TODOFree() {
    assertTODOFree(get_DispatchDisagreeReturnType2_6());
  }

  @Test
  public void stable_DispatchDisagreeReturnType2_6() {
    String live = get_DispatchDisagreeReturnType2_6();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatchDisagreeReturnType2_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 4,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 6,");
    gold.append("\n      \"character\" : 1");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Dispatcher 'x' do not agree on return type. (EnumStorage)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 8,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 9,");
    gold.append("\n      \"character\" : 1");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Dispatcher 'x' do not agree on return type. (EnumStorage)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatchInvoke_7 = null;
  private String get_DispatchInvoke_7() {
    if (cached_DispatchInvoke_7 != null) {
      return cached_DispatchInvoke_7;
    }
    cached_DispatchInvoke_7 = generateTestOutput(true, "DispatchInvoke_7", "./test_code/Enums_DispatchInvoke_success.a");
    return cached_DispatchInvoke_7;
  }

  @Test
  public void testDispatchInvokeEmission() {
    assertEmissionGood(get_DispatchInvoke_7());
  }

  @Test
  public void testDispatchInvokeSuccess() {
    assertLivePass(get_DispatchInvoke_7());
  }

  @Test
  public void testDispatchInvokeGoodWillHappy() {
    assertGoodWillHappy(get_DispatchInvoke_7());
  }

  @Test
  public void testDispatchInvokeExceptionFree() {
    assertExceptionFree(get_DispatchInvoke_7());
  }

  @Test
  public void testDispatchInvokeTODOFree() {
    assertTODOFree(get_DispatchInvoke_7());
  }

  @Test
  public void stable_DispatchInvoke_7() {
    String live = get_DispatchInvoke_7();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatchInvoke_success.a");
    gold.append("\n--EMISSION-----------------------------------------");
    gold.append("\nEmission Success, Yay");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.delta.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.json.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.util.Map;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatchInvoke_7 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxInt32 y;");
    gold.append("\n  public DispatchInvoke_7(DocumentMonitor __monitor) {");
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
    gold.append("\n    __dumpClients(__writer);");
    gold.append("\n    __dumpMessages(__writer);");
    gold.append("\n    __writer.endObject();");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, JsonStreamWriter __forward, JsonStreamWriter __reverse) {");
    gold.append("\n    __state.__commit(\"__state\", __forward, __reverse);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __forward, __reverse);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __forward, __reverse);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __forward, __reverse);");
    gold.append("\n    __seq.__commit(\"__seq\", __forward, __reverse);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __forward, __reverse);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __forward, __reverse);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __forward, __reverse);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __forward, __reverse);");
    gold.append("\n    __time.__commit(\"__time\", __forward, __reverse);");
    gold.append("\n    x.__commit(\"x\", __forward, __reverse);");
    gold.append("\n    y.__commit(\"y\", __forward, __reverse);");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __revert() {");
    gold.append("\n    __state.__revert();");
    gold.append("\n    __constructed.__revert();");
    gold.append("\n    __next_time.__revert();");
    gold.append("\n    __blocked.__revert();");
    gold.append("\n    __seq.__revert();");
    gold.append("\n    __entropy.__revert();");
    gold.append("\n    __auto_future_id.__revert();");
    gold.append("\n    __connection_id.__revert();");
    gold.append("\n    __message_id.__revert();");
    gold.append("\n    __time.__revert();");
    gold.append("\n    x.__revert();");
    gold.append("\n    y.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  private class DeltaDispatchInvoke_7 {");
    gold.append("\n    private DInt32 __dx;");
    gold.append("\n    private DInt32 __dy;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaDispatchInvoke_7() {");
    gold.append("\n      __dx = new DInt32();");
    gold.append("\n      __dy = new DInt32();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(DispatchInvoke_7 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __obj.manifest();");
    gold.append("\n      __dx.show(__item.x.get(), __obj.planField(\"x\"));");
    gold.append("\n      __dy.show(__item.y.get(), __obj.planField(\"y\"));");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    public void hide(PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      if (__emitted) {");
    gold.append("\n        __emitted = false;");
    gold.append("\n        __writer.writeNull();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public PrivateView __createPrivateView(NtClient __who, Consumer<String> __updates) {");
    gold.append("\n    DispatchInvoke_7 __self = this;");
    gold.append("\n    DeltaDispatchInvoke_7 __state = new DeltaDispatchInvoke_7();");
    gold.append("\n    return new PrivateView(__who, __updates) {");
    gold.append("\n      @Override");
    gold.append("\n      public void update(JsonStreamWriter __writer) {");
    gold.append("\n        __state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer));");
    gold.append("\n      }");
    gold.append("\n    };");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_E = new int[] {0, 1};");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected Object __parse_message2(String channel, JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {}");
    gold.append("\n  public boolean __onConnected__0(NtClient who){");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(2);");
    gold.append("\n    return (who.equals(NtClient.NO_ONE));");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onConnected(NtClient __cvalue) {");
    gold.append("\n    boolean __result = false;");
    gold.append("\n    if (__onConnected__0(__cvalue)) __result = true;");
    gold.append("\n    return __result;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onDisconnected(NtClient __cvalue) {}");
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
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__constructed\":true,\"__entropy\":\"0\",\"x\":13,\"y\":42} need:true in:0");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"25\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":1,\"__connection_id\":1,\"__time\":\"25\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}} need:true in:0");
    gold.append("\nRANDO was DENIED:5011");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{\"x\":13,\"y\":42},\"outstanding\":[],\"blockers\":[],\"seq\":2}");
    gold.append("\n+ RANDO DELTA:{\"data\":{\"x\":13,\"y\":42},\"outstanding\":[],\"blockers\":[],\"seq\":2}");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seq\":2,\"__entropy\":\"-4962768465676381896\",\"__time\":\"50\"} need:false in:-50");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"75\"}-->{\"__goodwill_used\":0,\"__cost\":15,\"__billing_seq\":2} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"x\":13,\"y\":42,\"__seq\":2,\"__connection_id\":1,\"__time\":\"50\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__goodwill_used\":0,\"__cost\":15,\"__billing_seq\":2}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"x\":13,\"y\":42,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__blocked\":false,\"__seq\":2,\"__entropy\":\"-4962768465676381896\",\"__auto_future_id\":0,\"__connection_id\":1,\"__message_id\":0,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}");
    gold.append("\n{\"x\":13,\"y\":42,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__blocked\":false,\"__seq\":2,\"__entropy\":\"-4962768465676381896\",\"__auto_future_id\":0,\"__connection_id\":1,\"__message_id\":0,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_DispatchManyMissing_8 = null;
  private String get_DispatchManyMissing_8() {
    if (cached_DispatchManyMissing_8 != null) {
      return cached_DispatchManyMissing_8;
    }
    cached_DispatchManyMissing_8 = generateTestOutput(false, "DispatchManyMissing_8", "./test_code/Enums_DispatchManyMissing_failure.a");
    return cached_DispatchManyMissing_8;
  }

  @Test
  public void testDispatchManyMissingFailure() {
    assertLiveFail(get_DispatchManyMissing_8());
  }

  @Test
  public void testDispatchManyMissingNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatchManyMissing_8());
  }

  @Test
  public void testDispatchManyMissingExceptionFree() {
    assertExceptionFree(get_DispatchManyMissing_8());
  }

  @Test
  public void testDispatchManyMissingTODOFree() {
    assertTODOFree(get_DispatchManyMissing_8());
  }

  @Test
  public void stable_DispatchManyMissing_8() {
    String live = get_DispatchManyMissing_8();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatchManyMissing_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 2,");
    gold.append("\n      \"character\" : 1");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Enum 'E' has a dispatcher 'x' which is incomplete and lacks: X, Z. (EnumStorage)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatchOverloading_9 = null;
  private String get_DispatchOverloading_9() {
    if (cached_DispatchOverloading_9 != null) {
      return cached_DispatchOverloading_9;
    }
    cached_DispatchOverloading_9 = generateTestOutput(true, "DispatchOverloading_9", "./test_code/Enums_DispatchOverloading_success.a");
    return cached_DispatchOverloading_9;
  }

  @Test
  public void testDispatchOverloadingEmission() {
    assertEmissionGood(get_DispatchOverloading_9());
  }

  @Test
  public void testDispatchOverloadingSuccess() {
    assertLivePass(get_DispatchOverloading_9());
  }

  @Test
  public void testDispatchOverloadingGoodWillHappy() {
    assertGoodWillHappy(get_DispatchOverloading_9());
  }

  @Test
  public void testDispatchOverloadingExceptionFree() {
    assertExceptionFree(get_DispatchOverloading_9());
  }

  @Test
  public void testDispatchOverloadingTODOFree() {
    assertTODOFree(get_DispatchOverloading_9());
  }

  @Test
  public void stable_DispatchOverloading_9() {
    String live = get_DispatchOverloading_9();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatchOverloading_success.a");
    gold.append("\n--EMISSION-----------------------------------------");
    gold.append("\nEmission Success, Yay");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.delta.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.json.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.util.Map;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatchOverloading_9 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxInt32 y;");
    gold.append("\n  private final RxDouble u;");
    gold.append("\n  private final RxDouble v;");
    gold.append("\n  public DispatchOverloading_9(DocumentMonitor __monitor) {");
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
    gold.append("\n    __dumpClients(__writer);");
    gold.append("\n    __dumpMessages(__writer);");
    gold.append("\n    __writer.endObject();");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, JsonStreamWriter __forward, JsonStreamWriter __reverse) {");
    gold.append("\n    __state.__commit(\"__state\", __forward, __reverse);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __forward, __reverse);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __forward, __reverse);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __forward, __reverse);");
    gold.append("\n    __seq.__commit(\"__seq\", __forward, __reverse);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __forward, __reverse);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __forward, __reverse);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __forward, __reverse);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __forward, __reverse);");
    gold.append("\n    __time.__commit(\"__time\", __forward, __reverse);");
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
    gold.append("\n    __blocked.__revert();");
    gold.append("\n    __seq.__revert();");
    gold.append("\n    __entropy.__revert();");
    gold.append("\n    __auto_future_id.__revert();");
    gold.append("\n    __connection_id.__revert();");
    gold.append("\n    __message_id.__revert();");
    gold.append("\n    __time.__revert();");
    gold.append("\n    x.__revert();");
    gold.append("\n    y.__revert();");
    gold.append("\n    u.__revert();");
    gold.append("\n    v.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  private class DeltaDispatchOverloading_9 {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaDispatchOverloading_9() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(DispatchOverloading_9 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __obj.manifest();");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    public void hide(PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      if (__emitted) {");
    gold.append("\n        __emitted = false;");
    gold.append("\n        __writer.writeNull();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public PrivateView __createPrivateView(NtClient __who, Consumer<String> __updates) {");
    gold.append("\n    DispatchOverloading_9 __self = this;");
    gold.append("\n    DeltaDispatchOverloading_9 __state = new DeltaDispatchOverloading_9();");
    gold.append("\n    return new PrivateView(__who, __updates) {");
    gold.append("\n      @Override");
    gold.append("\n      public void update(JsonStreamWriter __writer) {");
    gold.append("\n        __state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer));");
    gold.append("\n      }");
    gold.append("\n    };");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_E = new int[] {0, 1};");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected Object __parse_message2(String channel, JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {}");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onConnected(NtClient __cvalue) {");
    gold.append("\n    boolean __result = false;");
    gold.append("\n    return __result;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onDisconnected(NtClient __cvalue) {}");
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
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__constructed\":true,\"__entropy\":\"0\",\"x\":13,\"y\":42,\"u\":15.0,\"v\":44.0} need:true in:0");
    gold.append("\nNO_ONE was DENIED:5011");
    gold.append("\nRANDO was DENIED:5011");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":1}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":1}");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":13,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"x\":13,\"y\":42,\"u\":15.0,\"v\":44.0,\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":13,\"__billing_seq\":1}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"x\":13,\"y\":42,\"u\":15.0,\"v\":44.0,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__blocked\":false,\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"50\"}");
    gold.append("\n{\"x\":13,\"y\":42,\"u\":15.0,\"v\":44.0,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__blocked\":false,\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"50\"}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_DispatcherForNoEnum_10 = null;
  private String get_DispatcherForNoEnum_10() {
    if (cached_DispatcherForNoEnum_10 != null) {
      return cached_DispatcherForNoEnum_10;
    }
    cached_DispatcherForNoEnum_10 = generateTestOutput(false, "DispatcherForNoEnum_10", "./test_code/Enums_DispatcherForNoEnum_failure.a");
    return cached_DispatcherForNoEnum_10;
  }

  @Test
  public void testDispatcherForNoEnumFailure() {
    assertLiveFail(get_DispatcherForNoEnum_10());
  }

  @Test
  public void testDispatcherForNoEnumNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatcherForNoEnum_10());
  }

  @Test
  public void testDispatcherForNoEnumExceptionFree() {
    assertExceptionFree(get_DispatcherForNoEnum_10());
  }

  @Test
  public void testDispatcherForNoEnumTODOFree() {
    assertTODOFree(get_DispatcherForNoEnum_10());
  }

  @Test
  public void stable_DispatcherForNoEnum_10() {
    String live = get_DispatcherForNoEnum_10();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatcherForNoEnum_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 2,");
    gold.append("\n      \"character\" : 1");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Dispatcher 'foo' was unable to find the given enumeration type of 'E' (DocumentDefine)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatcherForWrongType_11 = null;
  private String get_DispatcherForWrongType_11() {
    if (cached_DispatcherForWrongType_11 != null) {
      return cached_DispatcherForWrongType_11;
    }
    cached_DispatcherForWrongType_11 = generateTestOutput(false, "DispatcherForWrongType_11", "./test_code/Enums_DispatcherForWrongType_failure.a");
    return cached_DispatcherForWrongType_11;
  }

  @Test
  public void testDispatcherForWrongTypeFailure() {
    assertLiveFail(get_DispatcherForWrongType_11());
  }

  @Test
  public void testDispatcherForWrongTypeNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatcherForWrongType_11());
  }

  @Test
  public void testDispatcherForWrongTypeExceptionFree() {
    assertExceptionFree(get_DispatcherForWrongType_11());
  }

  @Test
  public void testDispatcherForWrongTypeTODOFree() {
    assertTODOFree(get_DispatcherForWrongType_11());
  }

  @Test
  public void stable_DispatcherForWrongType_11() {
    String live = get_DispatcherForWrongType_11();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatcherForWrongType_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 2,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 4,");
    gold.append("\n      \"character\" : 1");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Dispatcher 'foo' found 'E', but it was 'E' (DocumentDefine)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatcherFoundNoValues_12 = null;
  private String get_DispatcherFoundNoValues_12() {
    if (cached_DispatcherFoundNoValues_12 != null) {
      return cached_DispatcherFoundNoValues_12;
    }
    cached_DispatcherFoundNoValues_12 = generateTestOutput(false, "DispatcherFoundNoValues_12", "./test_code/Enums_DispatcherFoundNoValues_failure.a");
    return cached_DispatcherFoundNoValues_12;
  }

  @Test
  public void testDispatcherFoundNoValuesFailure() {
    assertLiveFail(get_DispatcherFoundNoValues_12());
  }

  @Test
  public void testDispatcherFoundNoValuesNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatcherFoundNoValues_12());
  }

  @Test
  public void testDispatcherFoundNoValuesExceptionFree() {
    assertExceptionFree(get_DispatcherFoundNoValues_12());
  }

  @Test
  public void testDispatcherFoundNoValuesTODOFree() {
    assertTODOFree(get_DispatcherFoundNoValues_12());
  }

  @Test
  public void stable_DispatcherFoundNoValues_12() {
    String live = get_DispatcherFoundNoValues_12();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatcherFoundNoValues_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 4,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 6,");
    gold.append("\n      \"character\" : 1");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Dispatcher 'foo' has a value prefix 'C' which does not relate to any value within enum 'X' (EnumStorage)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 5,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 5,");
    gold.append("\n      \"character\" : 11");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"The return statement expects no expression (ReturnFlow)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 9,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 9,");
    gold.append("\n      \"character\" : 11");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"The return statement expects no expression (ReturnFlow)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatcherLacksCoverage_13 = null;
  private String get_DispatcherLacksCoverage_13() {
    if (cached_DispatcherLacksCoverage_13 != null) {
      return cached_DispatcherLacksCoverage_13;
    }
    cached_DispatcherLacksCoverage_13 = generateTestOutput(false, "DispatcherLacksCoverage_13", "./test_code/Enums_DispatcherLacksCoverage_failure.a");
    return cached_DispatcherLacksCoverage_13;
  }

  @Test
  public void testDispatcherLacksCoverageFailure() {
    assertLiveFail(get_DispatcherLacksCoverage_13());
  }

  @Test
  public void testDispatcherLacksCoverageNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatcherLacksCoverage_13());
  }

  @Test
  public void testDispatcherLacksCoverageExceptionFree() {
    assertExceptionFree(get_DispatcherLacksCoverage_13());
  }

  @Test
  public void testDispatcherLacksCoverageTODOFree() {
    assertTODOFree(get_DispatcherLacksCoverage_13());
  }

  @Test
  public void stable_DispatcherLacksCoverage_13() {
    String live = get_DispatcherLacksCoverage_13();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatcherLacksCoverage_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 2,");
    gold.append("\n      \"character\" : 1");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Enum 'X' has a dispatcher 'foo' which is incomplete and lacks: B. (EnumStorage)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 5,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 5,");
    gold.append("\n      \"character\" : 11");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"The return statement expects no expression (ReturnFlow)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatcherMustHaveNoOverlapWhenReturnValue_14 = null;
  private String get_DispatcherMustHaveNoOverlapWhenReturnValue_14() {
    if (cached_DispatcherMustHaveNoOverlapWhenReturnValue_14 != null) {
      return cached_DispatcherMustHaveNoOverlapWhenReturnValue_14;
    }
    cached_DispatcherMustHaveNoOverlapWhenReturnValue_14 = generateTestOutput(false, "DispatcherMustHaveNoOverlapWhenReturnValue_14", "./test_code/Enums_DispatcherMustHaveNoOverlapWhenReturnValue_failure.a");
    return cached_DispatcherMustHaveNoOverlapWhenReturnValue_14;
  }

  @Test
  public void testDispatcherMustHaveNoOverlapWhenReturnValueFailure() {
    assertLiveFail(get_DispatcherMustHaveNoOverlapWhenReturnValue_14());
  }

  @Test
  public void testDispatcherMustHaveNoOverlapWhenReturnValueNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatcherMustHaveNoOverlapWhenReturnValue_14());
  }

  @Test
  public void testDispatcherMustHaveNoOverlapWhenReturnValueExceptionFree() {
    assertExceptionFree(get_DispatcherMustHaveNoOverlapWhenReturnValue_14());
  }

  @Test
  public void testDispatcherMustHaveNoOverlapWhenReturnValueTODOFree() {
    assertTODOFree(get_DispatcherMustHaveNoOverlapWhenReturnValue_14());
  }

  @Test
  public void stable_DispatcherMustHaveNoOverlapWhenReturnValue_14() {
    String live = get_DispatcherMustHaveNoOverlapWhenReturnValue_14();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatcherMustHaveNoOverlapWhenReturnValue_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 4,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 6,");
    gold.append("\n      \"character\" : 1");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Dispatcher 'foo' returns and matches too many for 'X' (EnumStorage)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DispatcherWithShouldReturn_15 = null;
  private String get_DispatcherWithShouldReturn_15() {
    if (cached_DispatcherWithShouldReturn_15 != null) {
      return cached_DispatcherWithShouldReturn_15;
    }
    cached_DispatcherWithShouldReturn_15 = generateTestOutput(false, "DispatcherWithShouldReturn_15", "./test_code/Enums_DispatcherWithShouldReturn_failure.a");
    return cached_DispatcherWithShouldReturn_15;
  }

  @Test
  public void testDispatcherWithShouldReturnFailure() {
    assertLiveFail(get_DispatcherWithShouldReturn_15());
  }

  @Test
  public void testDispatcherWithShouldReturnNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DispatcherWithShouldReturn_15());
  }

  @Test
  public void testDispatcherWithShouldReturnExceptionFree() {
    assertExceptionFree(get_DispatcherWithShouldReturn_15());
  }

  @Test
  public void testDispatcherWithShouldReturnTODOFree() {
    assertTODOFree(get_DispatcherWithShouldReturn_15());
  }

  @Test
  public void stable_DispatcherWithShouldReturn_15() {
    String live = get_DispatcherWithShouldReturn_15();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Enums_DispatcherWithShouldReturn_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 3,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 4,");
    gold.append("\n      \"character\" : 1");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Dispatch 'foo' does not return in all cases (DefineDispatcher)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

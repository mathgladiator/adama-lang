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
    gold.append("Path:.\\test_code\\Enums_CantCrossTypes_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 2147483647,");
    gold.append("\n      \"character\" : 2147483647");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type check failure: enum types are incompatible 'X' vs 'T'.(Assignment)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 2147483647,");
    gold.append("\n      \"character\" : 2147483647");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type check failure: the type 'X' is unable to store type 'T'.(TypeCheckReferences)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class CantCrossTypes_1 extends LivingDocument {");
    gold.append("\n  public CantCrossTypes_1(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
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
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_X = new int[] {0};");
    gold.append("\n  private static final int [] __ALL_VALUES_T = new int[] {0};");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
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
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {");
    gold.append("\n    __construct_0(who, message);");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
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
    gold.append("Path:.\\test_code\\Enums_CantDefineDuplicates_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 2147483647,");
    gold.append("\n      \"character\" : 2147483647");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Enum 'E' was has duplicates for X defined(DocumentDefine)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 2147483647,");
    gold.append("\n      \"character\" : 2147483647");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Enum 'E' was has duplicates for Z defined(DocumentDefine)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class CantDefineDuplicates_2 extends LivingDocument {");
    gold.append("\n  public CantDefineDuplicates_2(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
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
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_E = new int[] {2, 1, 2, 3};");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
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
    gold.append("Path:.\\test_code\\Enums_CantFindEnumName_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 2147483647,");
    gold.append("\n      \"character\" : 2147483647");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"enum 'X' has no values(EnumStorage)\"");
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
    gold.append("\n  \"message\" : \"Type not found: an enumeration named 'Y' was not found.(TypeCheckReferences)\"");
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
    gold.append("\n  \"message\" : \"Type not found: an enumeration named 'Y' was not found.(TypeCheckReferences)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class CantFindEnumName_3 extends LivingDocument {");
    gold.append("\n  public CantFindEnumName_3(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
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
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_X = new int[] {};");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
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
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(0);");
    gold.append("\n");
    gold.append("\n    __track(1);");
    gold.append("\n");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {");
    gold.append("\n    __construct_0(who, message);");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
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
    gold.append("Path:.\\test_code\\Enums_CantFindEnumValue_failure.a");
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
    gold.append("\n  \"message\" : \"Type lookup failure: unable to find value 'x' within the enumeration 'X'(Enumerations)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class CantFindEnumValue_4 extends LivingDocument {");
    gold.append("\n  public CantFindEnumValue_4(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
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
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_X = new int[] {1};");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
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
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    int x = 0;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {");
    gold.append("\n    __construct_0(who, message);");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
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
    gold.append("Path:.\\test_code\\Enums_DispatchDisagreeReturnType1_failure.a");
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
    gold.append("\n  \"message\" : \"Dispatcher 'x' do not agree on return type.(EnumStorage)\"");
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
    gold.append("\n  \"message\" : \"Dispatcher 'x' do not agree on return type.(EnumStorage)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatchDisagreeReturnType1_5 extends LivingDocument {");
    gold.append("\n  public DispatchDisagreeReturnType1_5(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
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
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_E = new int[] {0, 1};");
    gold.append("\n  private int __IND_DISPATCH_0_x__0(int self) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return 1;");
    gold.append("\n  }");
    gold.append("\n  private String __IND_DISPATCH_0_x__1(int self) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(1);");
    gold.append("\n    return \"1\";");
    gold.append("\n  }");
    gold.append("\n  private int __DISPATCH_0_x(int __value) {");
    gold.append("\n    if (__value == 0) {");
    gold.append("\n      return __IND_DISPATCH_0_x__0(__value);");
    gold.append("\n    }");
    gold.append("\n    if (__value == 1) {");
    gold.append("\n      return __IND_DISPATCH_0_x__1(__value);");
    gold.append("\n    }");
    gold.append("\n    return __IND_DISPATCH_0_x__0(__value);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
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
    gold.append("Path:.\\test_code\\Enums_DispatchDisagreeReturnType2_failure.a");
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
    gold.append("\n  \"message\" : \"Dispatcher 'x' do not agree on return type.(EnumStorage)\"");
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
    gold.append("\n  \"message\" : \"Dispatcher 'x' do not agree on return type.(EnumStorage)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatchDisagreeReturnType2_6 extends LivingDocument {");
    gold.append("\n  public DispatchDisagreeReturnType2_6(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
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
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_E = new int[] {0, 1};");
    gold.append("\n  private int __IND_DISPATCH_0_x__0(int self) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return 1;");
    gold.append("\n  }");
    gold.append("\n  private void __IND_DISPATCH_0_x__1(int self) {}");
    gold.append("\n  private int __DISPATCH_0_x(int __value) {");
    gold.append("\n    if (__value == 0) {");
    gold.append("\n      return __IND_DISPATCH_0_x__0(__value);");
    gold.append("\n    }");
    gold.append("\n    if (__value == 1) {");
    gold.append("\n      __IND_DISPATCH_0_x__1(__value);");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    return __IND_DISPATCH_0_x__0(__value);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
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
    gold.append("Path:.\\test_code\\Enums_DispatchInvoke_success.a");
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
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatchInvoke_7 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxInt32 y;");
    gold.append("\n  public DispatchInvoke_7(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxInt32(this, __root, \"x\", 0);");
    gold.append("\n    y = RxFactory.makeRxInt32(this, __root, \"y\", 0);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
    gold.append("\n    x.__commit(\"x\", __child);");
    gold.append("\n    y.__commit(\"y\", __child);");
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
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    if (!(true)) {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n    ObjectNode __view = Utility.createObjectNode();");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"x\", NativeBridge.INTEGER_NATIVE_SUPPORT.toPrivateJsonNode(__who, x.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"y\", NativeBridge.INTEGER_NATIVE_SUPPORT.toPrivateJsonNode(__who, y.get()));");
    gold.append("\n    return __view;");
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
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(3);");
    gold.append("\n    x.set( __DISPATCH_0_foo(0, 1));");
    gold.append("\n    __track(4);");
    gold.append("\n    y.set( __DISPATCH_0_foo(1, 1));");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {");
    gold.append("\n    __construct_0(who, message);");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"x\":13,\"y\":42,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":7,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"x\":13,\"y\":42,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":7,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"75\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":2,\"__connection_id\":1,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__seq\":3,\"__entropy\":\"4804307197456638271\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\nAS NO_ONE:{\"data\":{\"x\":13,\"y\":42},\"outstanding\":[],\"blockers\":[]}");
    gold.append("\nRANDO was DENIED");
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
    gold.append("Path:.\\test_code\\Enums_DispatchManyMissing_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 2147483647,");
    gold.append("\n      \"character\" : 2147483647");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Enum 'E' has a dispatcher 'x' which is incomplete and lacks: X, Z.(EnumStorage)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatchManyMissing_8 extends LivingDocument {");
    gold.append("\n  public DispatchManyMissing_8(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
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
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_E = new int[] {0, 1, 2};");
    gold.append("\n  private int __IND_DISPATCH_0_x__0(int self) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return 1;");
    gold.append("\n  }");
    gold.append("\n  private int __DISPATCH_0_x(int __value) {");
    gold.append("\n    if (__value == 0) {");
    gold.append("\n      /* no value defined for this, :( */");
    gold.append("\n    }");
    gold.append("\n    if (__value == 1) {");
    gold.append("\n      return __IND_DISPATCH_0_x__0(__value);");
    gold.append("\n    }");
    gold.append("\n    if (__value == 2) {");
    gold.append("\n      /* no value defined for this, :( */");
    gold.append("\n    }");
    gold.append("\n    /* no catch all */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
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
    gold.append("Path:.\\test_code\\Enums_DispatchOverloading_success.a");
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
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatchOverloading_9 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxInt32 y;");
    gold.append("\n  private final RxDouble u;");
    gold.append("\n  private final RxDouble v;");
    gold.append("\n  public DispatchOverloading_9(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxInt32(this, __root, \"x\", 0);");
    gold.append("\n    y = RxFactory.makeRxInt32(this, __root, \"y\", 0);");
    gold.append("\n    u = RxFactory.makeRxDouble(this, __root, \"u\", 0.0);");
    gold.append("\n    v = RxFactory.makeRxDouble(this, __root, \"v\", 0.0);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
    gold.append("\n    x.__commit(\"x\", __child);");
    gold.append("\n    y.__commit(\"y\", __child);");
    gold.append("\n    u.__commit(\"u\", __child);");
    gold.append("\n    v.__commit(\"v\", __child);");
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
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    if (!(true)) {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n    ObjectNode __view = Utility.createObjectNode();");
    gold.append("\n    return __view;");
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
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
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
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {");
    gold.append("\n    __construct_0(who, message);");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"x\":13,\"y\":42,\"u\":15.0,\"v\":44.0,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":13,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"x\":13,\"y\":42,\"u\":15.0,\"v\":44.0,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":13,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
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
    gold.append("Path:.\\test_code\\Enums_DispatcherForNoEnum_failure.a");
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
    gold.append("\n  \"message\" : \"Dispatcher 'foo' was unable to find the given enumeration type of 'E'(DocumentDefine)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatcherForNoEnum_10 extends LivingDocument {");
    gold.append("\n  public DispatcherForNoEnum_10(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
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
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
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
    gold.append("Path:.\\test_code\\Enums_DispatcherForWrongType_failure.a");
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
    gold.append("\n  \"message\" : \"Dispatcher 'foo' found 'E', but it was 'E'(DocumentDefine)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatcherForWrongType_11 extends LivingDocument {");
    gold.append("\n  public DispatcherForWrongType_11(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
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
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  private class RTxE extends RxRecordBase<RTxE> {");
    gold.append("\n    private final RxInt32 id;");
    gold.append("\n    private RTxE(ObjectNode __node, RxParent __owner) {");
    gold.append("\n      super(__owner);");
    gold.append("\n      id = RxFactory.makeRxInt32(this, __node, \"id\", 0);");
    gold.append("\n      if (__owner instanceof RxTable) {");
    gold.append("\n        /* ok */");
    gold.append("\n      } else {");
    gold.append("\n        /* ok */");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n      if (!(true)) {");
    gold.append("\n        return null;");
    gold.append("\n      }");
    gold.append("\n      ObjectNode __view = Utility.createObjectNode();");
    gold.append("\n      return __view;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public boolean __privacyPolicyAllowsCache() {");
    gold.append("\n      return true;");
    gold.append("\n    }");
    gold.append("\n    String[] __INDEX_COLUMNS = new String[] {};");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int[] __getIndexValues() {");
    gold.append("\n      return new int[] {};");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        ObjectNode __child = __delta.putObject(__name);");
    gold.append("\n        id.__commit(\"id\", __child);");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __revert() {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        __isDying = false;");
    gold.append("\n        id.__revert();");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public String __name() {");
    gold.append("\n      return \"E\";");
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
    gold.append("\n  }");
    gold.append("\n  private final RecordBridge<RTxE> __BRIDGE_E = new RecordBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public int  getNumberColumns() {");
    gold.append("\n      return 0;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxE construct(ObjectNode __item, RxParent __parent) {");
    gold.append("\n      return new RTxE(__item, __parent);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxE[] makeArray(int __n) {");
    gold.append("\n      return new RTxE[__n];");
    gold.append("\n    }");
    gold.append("\n  };");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
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
    gold.append("Path:.\\test_code\\Enums_DispatcherFoundNoValues_failure.a");
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
    gold.append("\n  \"message\" : \"Dispatcher 'foo' has a value prefix 'C' which does not relate to any value within enum 'X'(EnumStorage)\"");
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
    gold.append("\n  \"message\" : \"The return statement expects no expression(ReturnFlow)\"");
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
    gold.append("\n  \"message\" : \"The return statement expects no expression(ReturnFlow)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatcherFoundNoValues_12 extends LivingDocument {");
    gold.append("\n  public DispatcherFoundNoValues_12(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
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
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_X = new int[] {0, 1};");
    gold.append("\n  private void __IND_DISPATCH_0_foo__0(int self) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return 0;");
    gold.append("\n  }");
    gold.append("\n  private void __IND_DISPATCH_0_foo__1(int self) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(1);");
    gold.append("\n    return 0;");
    gold.append("\n  }");
    gold.append("\n  private void __DISPATCH_0_foo(int __value) {");
    gold.append("\n    if (__value == 0) {");
    gold.append("\n      __IND_DISPATCH_0_foo__1(__value);");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    if (__value == 1) {");
    gold.append("\n      __IND_DISPATCH_0_foo__1(__value);");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    __IND_DISPATCH_0_foo__1(__value);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
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
    gold.append("Path:.\\test_code\\Enums_DispatcherLacksCoverage_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 2147483647,");
    gold.append("\n      \"character\" : 2147483647");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Enum 'X' has a dispatcher 'foo' which is incomplete and lacks: B.(EnumStorage)\"");
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
    gold.append("\n  \"message\" : \"The return statement expects no expression(ReturnFlow)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatcherLacksCoverage_13 extends LivingDocument {");
    gold.append("\n  public DispatcherLacksCoverage_13(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
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
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_X = new int[] {0, 1};");
    gold.append("\n  private void __IND_DISPATCH_0_foo__0(int self) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return 0;");
    gold.append("\n  }");
    gold.append("\n  private void __DISPATCH_0_foo(int __value) {");
    gold.append("\n    if (__value == 0) {");
    gold.append("\n      __IND_DISPATCH_0_foo__0(__value);");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    if (__value == 1) {");
    gold.append("\n      /* no value defined for this, :( */");
    gold.append("\n    }");
    gold.append("\n    __IND_DISPATCH_0_foo__0(__value);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
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
    gold.append("Path:.\\test_code\\Enums_DispatcherMustHaveNoOverlapWhenReturnValue_failure.a");
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
    gold.append("\n  \"message\" : \"Dispatcher 'foo' returns and matches too many for 'X'(EnumStorage)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatcherMustHaveNoOverlapWhenReturnValue_14 extends LivingDocument {");
    gold.append("\n  public DispatcherMustHaveNoOverlapWhenReturnValue_14(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
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
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_E = new int[] {1};");
    gold.append("\n  private int __IND_DISPATCH_0_foo__0(int self, int x, int y) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return 0;");
    gold.append("\n  }");
    gold.append("\n  private int __IND_DISPATCH_0_foo__1(int self, int x, int y) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(1);");
    gold.append("\n    return 0;");
    gold.append("\n  }");
    gold.append("\n  private int __DISPATCH_0_foo(int __value, int x, int y) {");
    gold.append("\n    if (__value == 1) {");
    gold.append("\n      return __IND_DISPATCH_0_foo__0(__value, x, y);");
    gold.append("\n      return __IND_DISPATCH_0_foo__1(__value, x, y);");
    gold.append("\n    }");
    gold.append("\n    return __IND_DISPATCH_0_foo__1(__value, x, y);");
    gold.append("\n  }");
    gold.append("\n  private void __IND_DISPATCH_1_zoo__0(int self, int x, int y) {}");
    gold.append("\n  private void __IND_DISPATCH_1_zoo__1(int self, int x, int y) {}");
    gold.append("\n  private void __DISPATCH_1_zoo(int __value, int x, int y) {");
    gold.append("\n    if (__value == 1) {");
    gold.append("\n      __IND_DISPATCH_1_zoo__0(__value, x, y);");
    gold.append("\n      __IND_DISPATCH_1_zoo__1(__value, x, y);");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    __IND_DISPATCH_1_zoo__1(__value, x, y);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
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
    gold.append("Path:.\\test_code\\Enums_DispatcherWithShouldReturn_failure.a");
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
    gold.append("\n  \"message\" : \"Dispatch 'foo' does not return in all cases(DefineDispatcher)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\nimport com.fasterxml.jackson.databind.JsonNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ArrayNode;");
    gold.append("\nimport com.fasterxml.jackson.databind.node.ObjectNode;");
    gold.append("\nimport org.adamalang.runtime.*;");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.bridges.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class DispatcherWithShouldReturn_15 extends LivingDocument {");
    gold.append("\n  public DispatcherWithShouldReturn_15(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n    ObjectNode __child = __delta;");
    gold.append("\n    __state.__commit(\"__state\", __child);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __child);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __child);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __child);");
    gold.append("\n    __seq.__commit(\"__seq\", __child);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __child);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __child);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __child);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __child);");
    gold.append("\n    __time.__commit(\"__time\", __child);");
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
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_E = new int[] {1};");
    gold.append("\n  private int __IND_DISPATCH_0_foo__0(int self, int x, int y) {}");
    gold.append("\n  private int __DISPATCH_0_foo(int __value, int x, int y) {");
    gold.append("\n    if (__value == 1) {");
    gold.append("\n      return __IND_DISPATCH_0_foo__0(__value, x, y);");
    gold.append("\n    }");
    gold.append("\n    return __IND_DISPATCH_0_foo__0(__value, x, y);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

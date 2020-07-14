/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedGlobalsTests extends GeneratedBase {
  private String cached_FunctionOverloadingIncorrect_1 = null;
  private String get_FunctionOverloadingIncorrect_1() {
    if (cached_FunctionOverloadingIncorrect_1 != null) {
      return cached_FunctionOverloadingIncorrect_1;
    }
    cached_FunctionOverloadingIncorrect_1 = generateTestOutput(false, "FunctionOverloadingIncorrect_1", "./test_code/Globals_FunctionOverloadingIncorrect_failure.a");
    return cached_FunctionOverloadingIncorrect_1;
  }

  @Test
  public void testFunctionOverloadingIncorrectFailure() {
    assertLiveFail(get_FunctionOverloadingIncorrect_1());
  }

  @Test
  public void testFunctionOverloadingIncorrectExceptionFree() {
    assertExceptionFree(get_FunctionOverloadingIncorrect_1());
  }

  @Test
  public void testFunctionOverloadingIncorrectTODOFree() {
    assertTODOFree(get_FunctionOverloadingIncorrect_1());
  }

  @Test
  public void stable_FunctionOverloadingIncorrect_1() {
    String live = get_FunctionOverloadingIncorrect_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Globals_FunctionOverloadingIncorrect_failure.a");
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
    gold.append("\n  \"message\" : \"Overloaded Functioned has many identical calls(FunctionOverlap)\"");
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
    gold.append("\npublic class FunctionOverloadingIncorrect_1 extends LivingDocument {");
    gold.append("\n  public FunctionOverloadingIncorrect_1(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n  private int __FUNC_0_poo(int x) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return x + 1;");
    gold.append("\n  }");
    gold.append("\n  private int __FUNC_1_poo(int x) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return x + 2;");
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
  private String cached_FunctionOverloading_2 = null;
  private String get_FunctionOverloading_2() {
    if (cached_FunctionOverloading_2 != null) {
      return cached_FunctionOverloading_2;
    }
    cached_FunctionOverloading_2 = generateTestOutput(true, "FunctionOverloading_2", "./test_code/Globals_FunctionOverloading_success.a");
    return cached_FunctionOverloading_2;
  }

  @Test
  public void testFunctionOverloadingEmission() {
    assertEmissionGood(get_FunctionOverloading_2());
  }

  @Test
  public void testFunctionOverloadingSuccess() {
    assertLivePass(get_FunctionOverloading_2());
  }

  @Test
  public void testFunctionOverloadingGoodWillHappy() {
    assertGoodWillHappy(get_FunctionOverloading_2());
  }

  @Test
  public void testFunctionOverloadingExceptionFree() {
    assertExceptionFree(get_FunctionOverloading_2());
  }

  @Test
  public void testFunctionOverloadingTODOFree() {
    assertTODOFree(get_FunctionOverloading_2());
  }

  @Test
  public void stable_FunctionOverloading_2() {
    String live = get_FunctionOverloading_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Globals_FunctionOverloading_success.a");
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
    gold.append("\npublic class FunctionOverloading_2 extends LivingDocument {");
    gold.append("\n  private final RxInt32 z1;");
    gold.append("\n  private final RxInt32 z2;");
    gold.append("\n  public FunctionOverloading_2(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    z1 = RxFactory.makeRxInt32(this, __root, \"z1\", 0);");
    gold.append("\n    z2 = RxFactory.makeRxInt32(this, __root, \"z2\", 0);");
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
    gold.append("\n    z1.__commit(\"z1\", __child);");
    gold.append("\n    z2.__commit(\"z2\", __child);");
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
    gold.append("\n    z1.__revert();");
    gold.append("\n    z2.__revert();");
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
    gold.append("\n  private int __FUNC_0_poo() {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return 1;");
    gold.append("\n  }");
    gold.append("\n  private int __FUNC_1_poo(int x) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return x + 1;");
    gold.append("\n  }");
    gold.append("\n  private void __FUNC_2_dope() {}");
    gold.append("\n  private void __FUNC_3_dope(int z) {}");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  private void __step_z() {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(0);");
    gold.append("\n    __FUNC_2_dope();");
    gold.append("\n    __track(1);");
    gold.append("\n    __FUNC_3_dope(42);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"z\":");
    gold.append("\n        __step_z();");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
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
    gold.append("\n    __track(2);");
    gold.append("\n    z1.set(__FUNC_0_poo());");
    gold.append("\n    __track(3);");
    gold.append("\n    z2.set(__FUNC_1_poo(122));");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"z1\":1,\"z2\":123,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":7,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"z1\":1,\"z2\":123,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":7,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_MathLib_3 = null;
  private String get_MathLib_3() {
    if (cached_MathLib_3 != null) {
      return cached_MathLib_3;
    }
    cached_MathLib_3 = generateTestOutput(true, "MathLib_3", "./test_code/Globals_MathLib_success.a");
    return cached_MathLib_3;
  }

  @Test
  public void testMathLibEmission() {
    assertEmissionGood(get_MathLib_3());
  }

  @Test
  public void testMathLibSuccess() {
    assertLivePass(get_MathLib_3());
  }

  @Test
  public void testMathLibGoodWillHappy() {
    assertGoodWillHappy(get_MathLib_3());
  }

  @Test
  public void testMathLibExceptionFree() {
    assertExceptionFree(get_MathLib_3());
  }

  @Test
  public void testMathLibTODOFree() {
    assertTODOFree(get_MathLib_3());
  }

  @Test
  public void stable_MathLib_3() {
    String live = get_MathLib_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Globals_MathLib_success.a");
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
    gold.append("\npublic class MathLib_3 extends LivingDocument {");
    gold.append("\n  private final RxDouble x;");
    gold.append("\n  private final RxInt32 y;");
    gold.append("\n  private final RxDouble zzz;");
    gold.append("\n  public MathLib_3(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxDouble(this, __root, \"x\", 0.0);");
    gold.append("\n    y = RxFactory.makeRxInt32(this, __root, \"y\", 0);");
    gold.append("\n    zzz = RxFactory.makeRxDouble(this, __root, \"zzz\", 0.0);");
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
    gold.append("\n    zzz.__commit(\"zzz\", __child);");
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
    gold.append("\n    zzz.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    if (!(true)) {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n    ObjectNode __view = Utility.createObjectNode();");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"x\", NativeBridge.DOUBLE_NATIVE_SUPPORT.toPrivateJsonNode(__who, x.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"y\", NativeBridge.INTEGER_NATIVE_SUPPORT.toPrivateJsonNode(__who, y.get()));");
    gold.append("\n    return __view;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  private void __step_next() {");
    gold.append("\n    __code_cost += 6;");
    gold.append("\n    __track(0);");
    gold.append("\n    y.set(3);");
    gold.append("\n    __track(1);");
    gold.append("\n    int i1 = Math.abs(y.get());");
    gold.append("\n    __track(2);");
    gold.append("\n    double i2 = Math.floor(x.get());");
    gold.append("\n    __track(3);");
    gold.append("\n    double i3 = Math.ceil(x.get());");
    gold.append("\n    __track(4);");
    gold.append("\n    long i4 = Math.round(x.get());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"next\":");
    gold.append("\n        __step_next();");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
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
    gold.append("\n    __code_cost += 8;");
    gold.append("\n    __track(5);");
    gold.append("\n    x.set(12.7);");
    gold.append("\n    __track(6);");
    gold.append("\n    double d1 = Math.abs(x.get());");
    gold.append("\n    __track(7);");
    gold.append("\n    double d2 = Math.sin(x.get());");
    gold.append("\n    __track(8);");
    gold.append("\n    double d3 = Math.cos(x.get());");
    gold.append("\n    __track(9);");
    gold.append("\n    double d4 = Math.tan(x.get());");
    gold.append("\n    __track(10);");
    gold.append("\n    double d5 = (d1 + (d2 + d3) + d4);");
    gold.append("\n    __track(11);");
    gold.append("\n    zzz.set(Math.PI);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"x\":12.7,\"zzz\":3.141592653589793,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":8,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"x\":12.7,\"zzz\":3.141592653589793,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":8,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_NotFound_4 = null;
  private String get_NotFound_4() {
    if (cached_NotFound_4 != null) {
      return cached_NotFound_4;
    }
    cached_NotFound_4 = generateTestOutput(false, "NotFound_4", "./test_code/Globals_NotFound_failure.a");
    return cached_NotFound_4;
  }

  @Test
  public void testNotFoundFailure() {
    assertLiveFail(get_NotFound_4());
  }

  @Test
  public void testNotFoundExceptionFree() {
    assertExceptionFree(get_NotFound_4());
  }

  @Test
  public void testNotFoundTODOFree() {
    assertTODOFree(get_NotFound_4());
  }

  @Test
  public void stable_NotFound_4() {
    String live = get_NotFound_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Globals_NotFound_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 10");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 20");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Global 'String' lacks 'foo'(GlobaLookup)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 10");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 20");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Expression is not a function(FunctionInvoke)\"");
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
    gold.append("\npublic class NotFound_4 extends LivingDocument {");
    gold.append("\n  public NotFound_4(ObjectNode __root, DocumentMonitor __monitor) {");
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
  private String cached_Random_5 = null;
  private String get_Random_5() {
    if (cached_Random_5 != null) {
      return cached_Random_5;
    }
    cached_Random_5 = generateTestOutput(true, "Random_5", "./test_code/Globals_Random_success.a");
    return cached_Random_5;
  }

  @Test
  public void testRandomEmission() {
    assertEmissionGood(get_Random_5());
  }

  @Test
  public void testRandomSuccess() {
    assertLivePass(get_Random_5());
  }

  @Test
  public void testRandomGoodWillHappy() {
    assertGoodWillHappy(get_Random_5());
  }

  @Test
  public void testRandomExceptionFree() {
    assertExceptionFree(get_Random_5());
  }

  @Test
  public void testRandomTODOFree() {
    assertTODOFree(get_Random_5());
  }

  @Test
  public void stable_Random_5() {
    String live = get_Random_5();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Globals_Random_success.a");
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
    gold.append("\npublic class Random_5 extends LivingDocument {");
    gold.append("\n  private final RxInt32 v0;");
    gold.append("\n  private final RxInt32 v1;");
    gold.append("\n  private final RxInt32 v2;");
    gold.append("\n  private final RxDouble d0;");
    gold.append("\n  private final RxDouble d1;");
    gold.append("\n  private final RxInt64 l;");
    gold.append("\n  public Random_5(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    v0 = RxFactory.makeRxInt32(this, __root, \"v0\", 0);");
    gold.append("\n    v1 = RxFactory.makeRxInt32(this, __root, \"v1\", 0);");
    gold.append("\n    v2 = RxFactory.makeRxInt32(this, __root, \"v2\", 0);");
    gold.append("\n    d0 = RxFactory.makeRxDouble(this, __root, \"d0\", 0.0);");
    gold.append("\n    d1 = RxFactory.makeRxDouble(this, __root, \"d1\", 0.0);");
    gold.append("\n    l = RxFactory.makeRxInt64(this, __root, \"l\", 0L);");
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
    gold.append("\n    v0.__commit(\"v0\", __child);");
    gold.append("\n    v1.__commit(\"v1\", __child);");
    gold.append("\n    v2.__commit(\"v2\", __child);");
    gold.append("\n    d0.__commit(\"d0\", __child);");
    gold.append("\n    d1.__commit(\"d1\", __child);");
    gold.append("\n    l.__commit(\"l\", __child);");
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
    gold.append("\n    v0.__revert();");
    gold.append("\n    v1.__revert();");
    gold.append("\n    v2.__revert();");
    gold.append("\n    d0.__revert();");
    gold.append("\n    d1.__revert();");
    gold.append("\n    l.__revert();");
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
    gold.append("\n    __code_cost += 7;");
    gold.append("\n    __track(0);");
    gold.append("\n    v0.set(__randomInt());");
    gold.append("\n    __track(1);");
    gold.append("\n    v1.set(__randomBoundInt(100));");
    gold.append("\n    __track(2);");
    gold.append("\n    v2.set(__randomBoundInt(-1));");
    gold.append("\n    __track(3);");
    gold.append("\n    d0.set(__randomDouble());");
    gold.append("\n    __track(4);");
    gold.append("\n    d1.set(__randomGaussian());");
    gold.append("\n    __track(5);");
    gold.append("\n    l.set(__randomLong());");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"v0\":-1155484576,\"v1\":48,\"d0\":0.24053641567148587,\"d1\":2.080920790428163,\"l\":\"-7423979211207825555\",\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":7,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"v0\":-1155484576,\"v1\":48,\"d0\":0.24053641567148587,\"d1\":2.080920790428163,\"l\":\"-7423979211207825555\",\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":7,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
}

/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedBubbleTests extends GeneratedBase {
  private String cached_CanOnlyCallPure_1 = null;
  private String get_CanOnlyCallPure_1() {
    if (cached_CanOnlyCallPure_1 != null) {
      return cached_CanOnlyCallPure_1;
    }
    cached_CanOnlyCallPure_1 = generateTestOutput(true, "CanOnlyCallPure_1", "./test_code/Bubble_CanOnlyCallPure_success.a");
    return cached_CanOnlyCallPure_1;
  }

  @Test
  public void testCanOnlyCallPureEmission() {
    assertEmissionGood(get_CanOnlyCallPure_1());
  }

  @Test
  public void testCanOnlyCallPureSuccess() {
    assertLivePass(get_CanOnlyCallPure_1());
  }

  @Test
  public void testCanOnlyCallPureGoodWillHappy() {
    assertGoodWillHappy(get_CanOnlyCallPure_1());
  }

  @Test
  public void testCanOnlyCallPureExceptionFree() {
    assertExceptionFree(get_CanOnlyCallPure_1());
  }

  @Test
  public void testCanOnlyCallPureTODOFree() {
    assertTODOFree(get_CanOnlyCallPure_1());
  }

  @Test
  public void stable_CanOnlyCallPure_1() {
    String live = get_CanOnlyCallPure_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Bubble_CanOnlyCallPure_success.a");
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
    gold.append("\npublic class CanOnlyCallPure_1 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxGuard ___k;");
    gold.append("\n  public int __COMPUTE_k(NtClient c) {");
    gold.append("\n    return __FUNC_0_square((x.get() + 1) * (x.get() + 1));");
    gold.append("\n  }");
    gold.append("\n  public CanOnlyCallPure_1(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxInt32(this, __root, \"x\", 7);");
    gold.append("\n    ___k =  new RxGuard();");
    gold.append("\n    x.__subscribe(___k);");
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
    gold.append("\n    Integer __local_k = __COMPUTE_k(__who);");
    gold.append("\n    if (__local_k != null) {");
    gold.append("\n      __view.set(\"k\", NativeBridge.INTEGER_NATIVE_SUPPORT.toPrivateJsonNode(__who, __local_k));");
    gold.append("\n    }");
    gold.append("\n    return __view;");
    gold.append("\n  }");
    gold.append("\n  private int __FUNC_0_square(int x) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return x * x;");
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
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":0,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":0,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_Define_2 = null;
  private String get_Define_2() {
    if (cached_Define_2 != null) {
      return cached_Define_2;
    }
    cached_Define_2 = generateTestOutput(true, "Define_2", "./test_code/Bubble_Define_success.a");
    return cached_Define_2;
  }

  @Test
  public void testDefineEmission() {
    assertEmissionGood(get_Define_2());
  }

  @Test
  public void testDefineSuccess() {
    assertLivePass(get_Define_2());
  }

  @Test
  public void testDefineGoodWillHappy() {
    assertGoodWillHappy(get_Define_2());
  }

  @Test
  public void testDefineExceptionFree() {
    assertExceptionFree(get_Define_2());
  }

  @Test
  public void testDefineTODOFree() {
    assertTODOFree(get_Define_2());
  }

  @Test
  public void stable_Define_2() {
    String live = get_Define_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Bubble_Define_success.a");
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
    gold.append("\npublic class Define_2 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxGuard ___k;");
    gold.append("\n  public int __COMPUTE_k(NtClient c) {");
    gold.append("\n    return (x.get() + 1) * (x.get() + 1);");
    gold.append("\n  }");
    gold.append("\n  public Define_2(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxInt32(this, __root, \"x\", 7);");
    gold.append("\n    ___k =  new RxGuard();");
    gold.append("\n    x.__subscribe(___k);");
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
    gold.append("\n    Integer __local_k = __COMPUTE_k(__who);");
    gold.append("\n    if (__local_k != null) {");
    gold.append("\n      __view.set(\"k\", NativeBridge.INTEGER_NATIVE_SUPPORT.toPrivateJsonNode(__who, __local_k));");
    gold.append("\n    }");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":0,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":0,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
}

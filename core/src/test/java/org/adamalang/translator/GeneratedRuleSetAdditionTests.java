/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedRuleSetAdditionTests extends GeneratedBase {
  private String cached_HappyCases_1 = null;
  private String get_HappyCases_1() {
    if (cached_HappyCases_1 != null) {
      return cached_HappyCases_1;
    }
    cached_HappyCases_1 = generateTestOutput(true, "HappyCases_1", "./test_code/RuleSetAddition_HappyCases_success.a");
    return cached_HappyCases_1;
  }

  @Test
  public void testHappyCasesEmission() {
    assertEmissionGood(get_HappyCases_1());
  }

  @Test
  public void testHappyCasesSuccess() {
    assertLivePass(get_HappyCases_1());
  }

  @Test
  public void testHappyCasesGoodWillHappy() {
    assertGoodWillHappy(get_HappyCases_1());
  }

  @Test
  public void testHappyCasesExceptionFree() {
    assertExceptionFree(get_HappyCases_1());
  }

  @Test
  public void testHappyCasesTODOFree() {
    assertTODOFree(get_HappyCases_1());
  }

  @Test
  public void stable_HappyCases_1() {
    String live = get_HappyCases_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\RuleSetAddition_HappyCases_success.a");
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
    gold.append("\npublic class HappyCases_1 extends LivingDocument {");
    gold.append("\n  private final RxInt32 i;");
    gold.append("\n  private final RxDouble d;");
    gold.append("\n  private final RxString s;");
    gold.append("\n  public HappyCases_1(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    i = RxFactory.makeRxInt32(this, __root, \"i\", 1);");
    gold.append("\n    d = RxFactory.makeRxDouble(this, __root, \"d\", 3.14);");
    gold.append("\n    s = RxFactory.makeRxString(this, __root, \"s\", \"x\");");
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
    gold.append("\n    i.__commit(\"i\", __child);");
    gold.append("\n    d.__commit(\"d\", __child);");
    gold.append("\n    s.__commit(\"s\", __child);");
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
    gold.append("\n    i.__revert();");
    gold.append("\n    d.__revert();");
    gold.append("\n    s.__revert();");
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
    gold.append("\n  public void __test_PrimaryTest(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"PrimaryTest\");");
    gold.append("\n    {}");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {\"PrimaryTest\"};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {");
    gold.append("\n    switch(testName) {");
    gold.append("\n      case \"PrimaryTest\":");
    gold.append("\n          __test_PrimaryTest(report);");
    gold.append("\n          return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 12;");
    gold.append("\n    __track(0);");
    gold.append("\n    int r0 = i.get() + i.get();");
    gold.append("\n    __track(1);");
    gold.append("\n    int r1 = r0 + i.get();");
    gold.append("\n    __track(2);");
    gold.append("\n    int r2 = 1 + r0;");
    gold.append("\n    __track(3);");
    gold.append("\n    int r3 = 4 + 4 + r0 + r1 + r2;");
    gold.append("\n    __track(4);");
    gold.append("\n    double r4 = i.get() + d.get();");
    gold.append("\n    __track(5);");
    gold.append("\n    double r5 = 2.71 + d.get();");
    gold.append("\n    __track(6);");
    gold.append("\n    double r6 = d.get() + r4 + r5;");
    gold.append("\n    __track(7);");
    gold.append("\n    String r7 = s.get() + \"yz\";");
    gold.append("\n    __track(8);");
    gold.append("\n    String r8 = s.get() + true;");
    gold.append("\n    __track(9);");
    gold.append("\n    String r9 = s.get() + 123;");
    gold.append("\n    __track(10);");
    gold.append("\n    String r10 = r7 + r8 + r9;");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":12,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":12,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[PrimaryTest] HAS NO ASSERTS");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
}

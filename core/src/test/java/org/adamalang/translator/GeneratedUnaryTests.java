/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedUnaryTests extends GeneratedBase {
  private String cached_AggregateOps_1 = null;
  private String get_AggregateOps_1() {
    if (cached_AggregateOps_1 != null) {
      return cached_AggregateOps_1;
    }
    cached_AggregateOps_1 = generateTestOutput(true, "AggregateOps_1", "./test_code/Unary_AggregateOps_success.a");
    return cached_AggregateOps_1;
  }

  @Test
  public void testAggregateOpsEmission() {
    assertEmissionGood(get_AggregateOps_1());
  }

  @Test
  public void testAggregateOpsSuccess() {
    assertLivePass(get_AggregateOps_1());
  }

  @Test
  public void testAggregateOpsGoodWillHappy() {
    assertGoodWillHappy(get_AggregateOps_1());
  }

  @Test
  public void testAggregateOpsExceptionFree() {
    assertExceptionFree(get_AggregateOps_1());
  }

  @Test
  public void testAggregateOpsTODOFree() {
    assertTODOFree(get_AggregateOps_1());
  }

  @Test
  public void stable_AggregateOps_1() {
    String live = get_AggregateOps_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Unary_AggregateOps_success.a");
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
    gold.append("\npublic class AggregateOps_1 extends LivingDocument {");
    gold.append("\n  private final RxTable<RTxR> t;");
    gold.append("\n  public AggregateOps_1(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    t = RxFactory.makeRxTable(__self, this, __root, \"t\", __BRIDGE_R);");
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
    gold.append("\n    t.__commit(\"t\", __child);");
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
    gold.append("\n    t.__revert();");
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
    gold.append("\n  private class RTxR extends RxRecordBase<RTxR> {");
    gold.append("\n    private final RxInt32 x;");
    gold.append("\n    private final RxInt32 id;");
    gold.append("\n    private RTxR(ObjectNode __node, RxParent __owner) {");
    gold.append("\n      super(__owner);");
    gold.append("\n      x = RxFactory.makeRxInt32(this, __node, \"x\", 0);");
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
    gold.append("\n    String[] __INDEX_COLUMNS = new String[] {\"x\"};");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int[] __getIndexValues() {");
    gold.append("\n      return new int[] {x.getIndexValue()};");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        ObjectNode __child = __delta.putObject(__name);");
    gold.append("\n        x.__commit(\"x\", __child);");
    gold.append("\n        id.__commit(\"id\", __child);");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __revert() {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        __isDying = false;");
    gold.append("\n        x.__revert();");
    gold.append("\n        id.__revert();");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
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
    gold.append("\n  }");
    gold.append("\n  private final RecordBridge<RTxR> __BRIDGE_R = new RecordBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public int  getNumberColumns() {");
    gold.append("\n      return 0;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxR construct(ObjectNode __item, RxParent __parent) {");
    gold.append("\n      return new RTxR(__item, __parent);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxR[] makeArray(int __n) {");
    gold.append("\n      return new RTxR[__n];");
    gold.append("\n    }");
    gold.append("\n  };");
    gold.append("\n  private static class RTx_AnonObjConvert_0 implements NtMessageBase {");
    gold.append("\n    private int x;");
    gold.append("\n    private RTx_AnonObjConvert_0(ObjectNode payload) {");
    gold.append("\n      this.x = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"x\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"x\", x, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_0(int x) {");
    gold.append("\n      this.x = x;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final MessageBridge<RTx_AnonObjConvert_0> __BRIDGE__AnonObjConvert_0 = new MessageBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public RTx_AnonObjConvert_0 convert(ObjectNode __node) {");
    gold.append("\n      return new RTx_AnonObjConvert_0(__node);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTx_AnonObjConvert_0[] makeArray(int __n) {");
    gold.append("\n      return new RTx_AnonObjConvert_0[__n];");
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
    gold.append("\n  public boolean __onConnected__0(NtClient who){");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
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
    gold.append("\n    __code_cost += 4;");
    gold.append("\n    __track(1);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxR> _AutoRef1 = t;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr3 = new RTx_AnonObjConvert_0(4);");
    gold.append("\n      RTxR _CreateRef2 = _AutoRef1.make();");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      _CreateRef2.x.set(_AutoExpr3.x);");
    gold.append("\n    }");
    gold.append("\n    __track(2);");
    gold.append("\n    (t.iterate(true)).transform((item) -> item.x, null /* no bridge needed */).transform((item) -> item.bumpUpPost(), null /** no bridge needed */);");
    gold.append("\n    __track(3);");
    gold.append("\n    ((t.iterate(true)).transform((item) -> item.x, null /* no bridge needed */)).transform((item) -> item.bumpUpPre(), null /** no bridge needed */);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"t\":{\"auto_key\":1,\"rows\":{\"0\":{\"x\":6}}},\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":5,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"t\":{\"auto_key\":1,\"rows\":{\"0\":{\"x\":6}}},\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":5,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"75\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":2,\"__connection_id\":1,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__seq\":3,\"__entropy\":\"4804307197456638271\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\nAS NO_ONE:{\"data\":{},\"outstanding\":[],\"blockers\":[]}");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_Happy_2 = null;
  private String get_Happy_2() {
    if (cached_Happy_2 != null) {
      return cached_Happy_2;
    }
    cached_Happy_2 = generateTestOutput(true, "Happy_2", "./test_code/Unary_Happy_success.a");
    return cached_Happy_2;
  }

  @Test
  public void testHappyEmission() {
    assertEmissionGood(get_Happy_2());
  }

  @Test
  public void testHappySuccess() {
    assertLivePass(get_Happy_2());
  }

  @Test
  public void testHappyGoodWillHappy() {
    assertGoodWillHappy(get_Happy_2());
  }

  @Test
  public void testHappyExceptionFree() {
    assertExceptionFree(get_Happy_2());
  }

  @Test
  public void testHappyTODOFree() {
    assertTODOFree(get_Happy_2());
  }

  @Test
  public void stable_Happy_2() {
    String live = get_Happy_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Unary_Happy_success.a");
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
    gold.append("\npublic class Happy_2 extends LivingDocument {");
    gold.append("\n  private final RxInt32 rx;");
    gold.append("\n  private final RxDouble ry;");
    gold.append("\n  private final RxDouble z;");
    gold.append("\n  public Happy_2(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    rx = RxFactory.makeRxInt32(this, __root, \"rx\", 0);");
    gold.append("\n    ry = RxFactory.makeRxDouble(this, __root, \"ry\", 0.0);");
    gold.append("\n    z = RxFactory.makeRxDouble(this, __root, \"z\", 0.0);");
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
    gold.append("\n    rx.__commit(\"rx\", __child);");
    gold.append("\n    ry.__commit(\"ry\", __child);");
    gold.append("\n    z.__commit(\"z\", __child);");
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
    gold.append("\n    rx.__revert();");
    gold.append("\n    ry.__revert();");
    gold.append("\n    z.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    if (!(true)) {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n    ObjectNode __view = Utility.createObjectNode();");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"rx\", NativeBridge.INTEGER_NATIVE_SUPPORT.toPrivateJsonNode(__who, rx.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"ry\", NativeBridge.DOUBLE_NATIVE_SUPPORT.toPrivateJsonNode(__who, ry.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"z\", NativeBridge.DOUBLE_NATIVE_SUPPORT.toPrivateJsonNode(__who, z.get()));");
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
    gold.append("\n  public boolean __onConnected__0(NtClient who){");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
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
    gold.append("\n  public void __test_Negate(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"Negate\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 4;");
    gold.append("\n      __track(1);");
    gold.append("\n      __assert_truth(!false, 21, 2, 21, 16);");
    gold.append("\n      __track(2);");
    gold.append("\n      __assert_truth(!(!true), 22, 2, 22, 18);");
    gold.append("\n      __track(3);");
    gold.append("\n      __assert_truth(-(1 + 4) == -5, 23, 2, 23, 24);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {\"Negate\"};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {");
    gold.append("\n    switch(testName) {");
    gold.append("\n      case \"Negate\":");
    gold.append("\n          __test_Negate(report);");
    gold.append("\n          return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 14;");
    gold.append("\n    __track(4);");
    gold.append("\n    int x = 4;");
    gold.append("\n    __track(5);");
    gold.append("\n    rx.set(x++);");
    gold.append("\n    __track(6);");
    gold.append("\n    x++;");
    gold.append("\n    __track(7);");
    gold.append("\n    ++x;");
    gold.append("\n    __track(8);");
    gold.append("\n    double y = 3.14;");
    gold.append("\n    __track(9);");
    gold.append("\n    ry.set(++y);");
    gold.append("\n    __track(10);");
    gold.append("\n    ++y;");
    gold.append("\n    __track(11);");
    gold.append("\n    y++;");
    gold.append("\n    __track(12);");
    gold.append("\n    int x2 = rx.bumpUpPre();");
    gold.append("\n    __track(13);");
    gold.append("\n    int x3 = rx.bumpUpPost();");
    gold.append("\n    __track(14);");
    gold.append("\n    double y2 = ry.bumpUpPre();");
    gold.append("\n    __track(15);");
    gold.append("\n    double y3 = ry.bumpUpPost();");
    gold.append("\n    __track(16);");
    gold.append("\n    z.set(x2 + x3 + y2 + y3);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"rx\":6,\"ry\":6.140000000000001,\"z\":20.28,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":14,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"rx\":6,\"ry\":6.140000000000001,\"z\":20.28,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":14,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"75\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":2,\"__connection_id\":1,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__seq\":3,\"__entropy\":\"4804307197456638271\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\nAS NO_ONE:{\"data\":{\"rx\":6,\"ry\":6.140000000000001,\"z\":20.28},\"outstanding\":[],\"blockers\":[]}");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[Negate] = 100.0%");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
}

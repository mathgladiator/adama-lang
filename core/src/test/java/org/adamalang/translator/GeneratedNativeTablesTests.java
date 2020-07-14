/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedNativeTablesTests extends GeneratedBase {
  private String cached_BadMethod_1 = null;
  private String get_BadMethod_1() {
    if (cached_BadMethod_1 != null) {
      return cached_BadMethod_1;
    }
    cached_BadMethod_1 = generateTestOutput(false, "BadMethod_1", "./test_code/NativeTables_BadMethod_failure.a");
    return cached_BadMethod_1;
  }

  @Test
  public void testBadMethodFailure() {
    assertLiveFail(get_BadMethod_1());
  }

  @Test
  public void testBadMethodExceptionFree() {
    assertExceptionFree(get_BadMethod_1());
  }

  @Test
  public void testBadMethodTODOFree() {
    assertTODOFree(get_BadMethod_1());
  }

  @Test
  public void stable_BadMethod_1() {
    String live = get_BadMethod_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\NativeTables_BadMethod_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 6,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 6,");
    gold.append("\n      \"character\" : 8");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Record 'table<M>' lacks field 'nope'(FieldLookup)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 6,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 6,");
    gold.append("\n      \"character\" : 8");
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
    gold.append("\npublic class BadMethod_1 extends LivingDocument {");
    gold.append("\n  public BadMethod_1(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n  private static class RTxM implements NtMessageBase {");
    gold.append("\n    private int x;");
    gold.append("\n    private RTxM(ObjectNode payload) {");
    gold.append("\n      this.x = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"x\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"x\", x, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTxM(int x) {");
    gold.append("\n      this.x = x;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final MessageBridge<RTxM> __BRIDGE_M = new MessageBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public RTxM convert(ObjectNode __node) {");
    gold.append("\n      return new RTxM(__node);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxM[] makeArray(int __n) {");
    gold.append("\n      return new RTxM[__n];");
    gold.append("\n    }");
    gold.append("\n  };");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  private void __step_bad() {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(0);");
    gold.append("\n    NtTable<RTxM> t = new NtTable<RTxM>(__BRIDGE_M);");
    gold.append("\n    __track(1);");
    gold.append("\n    ;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"bad\":");
    gold.append("\n        __step_bad();");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_FunctionPassing_2 = null;
  private String get_FunctionPassing_2() {
    if (cached_FunctionPassing_2 != null) {
      return cached_FunctionPassing_2;
    }
    cached_FunctionPassing_2 = generateTestOutput(true, "FunctionPassing_2", "./test_code/NativeTables_FunctionPassing_success.a");
    return cached_FunctionPassing_2;
  }

  @Test
  public void testFunctionPassingEmission() {
    assertEmissionGood(get_FunctionPassing_2());
  }

  @Test
  public void testFunctionPassingSuccess() {
    assertLivePass(get_FunctionPassing_2());
  }

  @Test
  public void testFunctionPassingGoodWillHappy() {
    assertGoodWillHappy(get_FunctionPassing_2());
  }

  @Test
  public void testFunctionPassingExceptionFree() {
    assertExceptionFree(get_FunctionPassing_2());
  }

  @Test
  public void testFunctionPassingTODOFree() {
    assertTODOFree(get_FunctionPassing_2());
  }

  @Test
  public void stable_FunctionPassing_2() {
    String live = get_FunctionPassing_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\NativeTables_FunctionPassing_success.a");
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
    gold.append("\npublic class FunctionPassing_2 extends LivingDocument {");
    gold.append("\n  private final RxInt32 sz;");
    gold.append("\n  private final RxInt32 wz;");
    gold.append("\n  public FunctionPassing_2(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    sz = RxFactory.makeRxInt32(this, __root, \"sz\", 0);");
    gold.append("\n    wz = RxFactory.makeRxInt32(this, __root, \"wz\", 0);");
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
    gold.append("\n    sz.__commit(\"sz\", __child);");
    gold.append("\n    wz.__commit(\"wz\", __child);");
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
    gold.append("\n    sz.__revert();");
    gold.append("\n    wz.__revert();");
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
    gold.append("\n  private static class RTxM implements NtMessageBase {");
    gold.append("\n    private int x;");
    gold.append("\n    private int y;");
    gold.append("\n    private RTxM(ObjectNode payload) {");
    gold.append("\n      this.x = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"x\");");
    gold.append("\n      this.y = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"y\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"x\", x, __node);");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"y\", y, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTxM(int x, int y) {");
    gold.append("\n      this.x = x;");
    gold.append("\n      this.y = y;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final MessageBridge<RTxM> __BRIDGE_M = new MessageBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public RTxM convert(ObjectNode __node) {");
    gold.append("\n      return new RTxM(__node);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxM[] makeArray(int __n) {");
    gold.append("\n      return new RTxM[__n];");
    gold.append("\n    }");
    gold.append("\n  };");
    gold.append("\n  private static class RTx_AnonObjConvert_1 implements NtMessageBase {");
    gold.append("\n    private int x;");
    gold.append("\n    private RTx_AnonObjConvert_1(ObjectNode payload) {");
    gold.append("\n      this.x = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"x\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"x\", x, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_1(int x) {");
    gold.append("\n      this.x = x;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final MessageBridge<RTx_AnonObjConvert_1> __BRIDGE__AnonObjConvert_1 = new MessageBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public RTx_AnonObjConvert_1 convert(ObjectNode __node) {");
    gold.append("\n      return new RTx_AnonObjConvert_1(__node);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTx_AnonObjConvert_1[] makeArray(int __n) {");
    gold.append("\n      return new RTx_AnonObjConvert_1[__n];");
    gold.append("\n    }");
    gold.append("\n  };");
    gold.append("\n  private static class RTx_AnonObjConvert_2 implements NtMessageBase {");
    gold.append("\n    private int y;");
    gold.append("\n    private RTx_AnonObjConvert_2(ObjectNode payload) {");
    gold.append("\n      this.y = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"y\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"y\", y, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_2(int y) {");
    gold.append("\n      this.y = y;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final MessageBridge<RTx_AnonObjConvert_2> __BRIDGE__AnonObjConvert_2 = new MessageBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public RTx_AnonObjConvert_2 convert(ObjectNode __node) {");
    gold.append("\n      return new RTx_AnonObjConvert_2(__node);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTx_AnonObjConvert_2[] makeArray(int __n) {");
    gold.append("\n      return new RTx_AnonObjConvert_2[__n];");
    gold.append("\n    }");
    gold.append("\n  };");
    gold.append("\n  private void __FUNC_0_fill(NtTable<RTxM> ttt) {");
    gold.append("\n    __code_cost += 5;");
    gold.append("\n    __track(0);");
    gold.append("\n    {");
    gold.append("\n      NtTable<RTxM> _AutoRef3 = ttt;");
    gold.append("\n      RTx_AnonObjConvert_1 _AutoExpr5 = new RTx_AnonObjConvert_1(1);");
    gold.append("\n      RTxM _CreateRef4 = _AutoRef3.make();");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      _CreateRef4.x = _AutoExpr5.x;");
    gold.append("\n    }");
    gold.append("\n    __track(1);");
    gold.append("\n    {");
    gold.append("\n      NtTable<RTxM> _AutoRef6 = ttt;");
    gold.append("\n      RTx_AnonObjConvert_2 _AutoExpr8 = new RTx_AnonObjConvert_2(1);");
    gold.append("\n      RTxM _CreateRef7 = _AutoRef6.make();");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      _CreateRef7.y = _AutoExpr8.y;");
    gold.append("\n    }");
    gold.append("\n    __track(2);");
    gold.append("\n    {");
    gold.append("\n      NtTable<RTxM> _AutoRef9 = ttt;");
    gold.append("\n      RTxM _AutoExpr11 = new RTxM(1, 1);");
    gold.append("\n      RTxM _CreateRef10 = _AutoRef9.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef10.x = _AutoExpr11.x;");
    gold.append("\n      _CreateRef10.y = _AutoExpr11.y;");
    gold.append("\n    }");
    gold.append("\n    __track(3);");
    gold.append("\n    {");
    gold.append("\n      NtTable<RTxM> _AutoRef12 = ttt;");
    gold.append("\n      for (RTxM _AutoElement13 : new RTxM[] {new RTxM(4, 3), new RTxM(2, 2)}) {");
    gold.append("\n        RTxM _CreateRef13 = _AutoRef12.make();");
    gold.append("\n        __code_cost += 2;");
    gold.append("\n        _CreateRef13.x = _AutoElement13.x;");
    gold.append("\n        _CreateRef13.y = _AutoElement13.y;");
    gold.append("\n      }");
    gold.append("\n    }");
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
    gold.append("\n    __code_cost += 6;");
    gold.append("\n    __track(4);");
    gold.append("\n    NtTable<RTxM> t = new NtTable<RTxM>(__BRIDGE_M);");
    gold.append("\n    __track(5);");
    gold.append("\n    __FUNC_0_fill(t);");
    gold.append("\n    __track(6);");
    gold.append("\n    __FUNC_0_fill(t);");
    gold.append("\n    __track(7);");
    gold.append("\n    sz.set(t.size());");
    gold.append("\n    __track(8);");
    gold.append("\n    wz.set((t.iterate(false).where(true, new __CLOSURE_WhereClause0())).size());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {");
    gold.append("\n    __construct_0(who, message);");
    gold.append("\n  }");
    gold.append("\n  private class __CLOSURE_WhereClause0 implements WhereClause<RTxM> {");
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
    gold.append("\n    @Override");
    gold.append("\n    public boolean test(RTxM __obj) {");
    gold.append("\n      int y = __obj.y;");
    gold.append("\n      __code_cost ++;");
    gold.append("\n      return y == 2;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"sz\":10,\"wz\":2,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":42,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"sz\":10,\"wz\":2,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":42,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_Happy_3 = null;
  private String get_Happy_3() {
    if (cached_Happy_3 != null) {
      return cached_Happy_3;
    }
    cached_Happy_3 = generateTestOutput(true, "Happy_3", "./test_code/NativeTables_Happy_success.a");
    return cached_Happy_3;
  }

  @Test
  public void testHappyEmission() {
    assertEmissionGood(get_Happy_3());
  }

  @Test
  public void testHappySuccess() {
    assertLivePass(get_Happy_3());
  }

  @Test
  public void testHappyGoodWillHappy() {
    assertGoodWillHappy(get_Happy_3());
  }

  @Test
  public void testHappyExceptionFree() {
    assertExceptionFree(get_Happy_3());
  }

  @Test
  public void testHappyTODOFree() {
    assertTODOFree(get_Happy_3());
  }

  @Test
  public void stable_Happy_3() {
    String live = get_Happy_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\NativeTables_Happy_success.a");
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
    gold.append("\npublic class Happy_3 extends LivingDocument {");
    gold.append("\n  private final RxInt32 sz1;");
    gold.append("\n  private final RxInt32 sz2;");
    gold.append("\n  private final RxInt32 sz3;");
    gold.append("\n  public Happy_3(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    sz1 = RxFactory.makeRxInt32(this, __root, \"sz1\", 0);");
    gold.append("\n    sz2 = RxFactory.makeRxInt32(this, __root, \"sz2\", 0);");
    gold.append("\n    sz3 = RxFactory.makeRxInt32(this, __root, \"sz3\", 0);");
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
    gold.append("\n    sz1.__commit(\"sz1\", __child);");
    gold.append("\n    sz2.__commit(\"sz2\", __child);");
    gold.append("\n    sz3.__commit(\"sz3\", __child);");
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
    gold.append("\n    sz1.__revert();");
    gold.append("\n    sz2.__revert();");
    gold.append("\n    sz3.__revert();");
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
    gold.append("\n  private static class RTxM implements NtMessageBase {");
    gold.append("\n    private int x;");
    gold.append("\n    private int y;");
    gold.append("\n    private RTxM(ObjectNode payload) {");
    gold.append("\n      this.x = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"x\");");
    gold.append("\n      this.y = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"y\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"x\", x, __node);");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"y\", y, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTxM(int x, int y) {");
    gold.append("\n      this.x = x;");
    gold.append("\n      this.y = y;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final MessageBridge<RTxM> __BRIDGE_M = new MessageBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public RTxM convert(ObjectNode __node) {");
    gold.append("\n      return new RTxM(__node);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxM[] makeArray(int __n) {");
    gold.append("\n      return new RTxM[__n];");
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
    gold.append("\n    NtTable<RTxM> t = new NtTable<RTxM>(__BRIDGE_M);");
    gold.append("\n    __track(1);");
    gold.append("\n    {");
    gold.append("\n      NtTable<RTxM> _AutoRef1 = t;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr3 = new RTx_AnonObjConvert_0(1);");
    gold.append("\n      RTxM _CreateRef2 = _AutoRef1.make();");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      _CreateRef2.x = _AutoExpr3.x;");
    gold.append("\n    }");
    gold.append("\n    __track(2);");
    gold.append("\n    sz1.set(t.size());");
    gold.append("\n    __track(3);");
    gold.append("\n    sz2.set((t.iterate(true)).size());");
    gold.append("\n    __track(4);");
    gold.append("\n    t.delete();");
    gold.append("\n    __track(5);");
    gold.append("\n    sz3.set(t.size() + 1000);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"sz1\":1,\"sz2\":1,\"sz3\":1000,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":8,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"sz1\":1,\"sz2\":1,\"sz3\":1000,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":8,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_NoMessage_4 = null;
  private String get_NoMessage_4() {
    if (cached_NoMessage_4 != null) {
      return cached_NoMessage_4;
    }
    cached_NoMessage_4 = generateTestOutput(false, "NoMessage_4", "./test_code/NativeTables_NoMessage_failure.a");
    return cached_NoMessage_4;
  }

  @Test
  public void testNoMessageFailure() {
    assertLiveFail(get_NoMessage_4());
  }

  @Test
  public void testNoMessageExceptionFree() {
    assertExceptionFree(get_NoMessage_4());
  }

  @Test
  public void testNoMessageTODOFree() {
    assertTODOFree(get_NoMessage_4());
  }

  @Test
  public void stable_NoMessage_4() {
    String live = get_NoMessage_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\NativeTables_NoMessage_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 8");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 9");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type not found: the type 'M' was not found.(TypeCheckReferences)\"");
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
    gold.append("\npublic class NoMessage_4 extends LivingDocument {");
    gold.append("\n  public NoMessage_4(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n  private void __step_bad() {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    NtTable<RTxM> t = new NtTable<RTxM>();");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"bad\":");
    gold.append("\n        __step_bad();");
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
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_TableCopy_5 = null;
  private String get_TableCopy_5() {
    if (cached_TableCopy_5 != null) {
      return cached_TableCopy_5;
    }
    cached_TableCopy_5 = generateTestOutput(true, "TableCopy_5", "./test_code/NativeTables_TableCopy_success.a");
    return cached_TableCopy_5;
  }

  @Test
  public void testTableCopyEmission() {
    assertEmissionGood(get_TableCopy_5());
  }

  @Test
  public void testTableCopySuccess() {
    assertLivePass(get_TableCopy_5());
  }

  @Test
  public void testTableCopyGoodWillHappy() {
    assertGoodWillHappy(get_TableCopy_5());
  }

  @Test
  public void testTableCopyExceptionFree() {
    assertExceptionFree(get_TableCopy_5());
  }

  @Test
  public void testTableCopyTODOFree() {
    assertTODOFree(get_TableCopy_5());
  }

  @Test
  public void stable_TableCopy_5() {
    String live = get_TableCopy_5();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\NativeTables_TableCopy_success.a");
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
    gold.append("\npublic class TableCopy_5 extends LivingDocument {");
    gold.append("\n  private final RxInt32 sz;");
    gold.append("\n  public TableCopy_5(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    sz = RxFactory.makeRxInt32(this, __root, \"sz\", 0);");
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
    gold.append("\n    sz.__commit(\"sz\", __child);");
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
    gold.append("\n    sz.__revert();");
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
    gold.append("\n  private static class RTxM implements NtMessageBase {");
    gold.append("\n    private int x;");
    gold.append("\n    private int y;");
    gold.append("\n    private RTxM(ObjectNode payload) {");
    gold.append("\n      this.x = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"x\");");
    gold.append("\n      this.y = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"y\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"x\", x, __node);");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"y\", y, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTxM(int x, int y) {");
    gold.append("\n      this.x = x;");
    gold.append("\n      this.y = y;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final MessageBridge<RTxM> __BRIDGE_M = new MessageBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public RTxM convert(ObjectNode __node) {");
    gold.append("\n      return new RTxM(__node);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxM[] makeArray(int __n) {");
    gold.append("\n      return new RTxM[__n];");
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
    gold.append("\n    __track(0);");
    gold.append("\n    NtTable<RTxM> t = new NtTable<RTxM>(__BRIDGE_M);");
    gold.append("\n    __track(1);");
    gold.append("\n    {");
    gold.append("\n      NtTable<RTxM> _AutoRef1 = t;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr3 = new RTx_AnonObjConvert_0(1);");
    gold.append("\n      RTxM _CreateRef2 = _AutoRef1.make();");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      _CreateRef2.x = _AutoExpr3.x;");
    gold.append("\n    }");
    gold.append("\n    __track(2);");
    gold.append("\n    NtTable<RTxM> t2 = new NtTable<RTxM>(t);");
    gold.append("\n    __track(3);");
    gold.append("\n    sz.set(t2.size());");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"sz\":1,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":6,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"sz\":1,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":6,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
}

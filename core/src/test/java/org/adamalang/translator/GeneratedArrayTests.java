/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedArrayTests extends GeneratedBase {
  private String cached_Empty_1 = null;
  private String get_Empty_1() {
    if (cached_Empty_1 != null) {
      return cached_Empty_1;
    }
    cached_Empty_1 = generateTestOutput(true, "Empty_1", "./test_code/Array_Empty_success.a");
    return cached_Empty_1;
  }

  @Test
  public void testEmptyEmission() {
    assertEmissionGood(get_Empty_1());
  }

  @Test
  public void testEmptySuccess() {
    assertLivePass(get_Empty_1());
  }

  @Test
  public void testEmptyGoodWillHappy() {
    assertGoodWillHappy(get_Empty_1());
  }

  @Test
  public void testEmptyExceptionFree() {
    assertExceptionFree(get_Empty_1());
  }

  @Test
  public void testEmptyTODOFree() {
    assertTODOFree(get_Empty_1());
  }

  @Test
  public void stable_Empty_1() {
    String live = get_Empty_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Array_Empty_success.a");
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
    gold.append("\npublic class Empty_1 extends LivingDocument {");
    gold.append("\n  private final RxInt32 count;");
    gold.append("\n  public Empty_1(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    count = RxFactory.makeRxInt32(this, __root, \"count\", 0);");
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
    gold.append("\n    count.__commit(\"count\", __child);");
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
    gold.append("\n    count.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    if (!(true)) {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n    ObjectNode __view = Utility.createObjectNode();");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"count\", NativeBridge.INTEGER_NATIVE_SUPPORT.toPrivateJsonNode(__who, count.get()));");
    gold.append("\n    return __view;");
    gold.append("\n  }");
    gold.append("\n  private static class RTx__EmptyMessageNoArgs_ implements NtMessageBase {");
    gold.append("\n    private RTx__EmptyMessageNoArgs_(ObjectNode payload) {}");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTx__EmptyMessageNoArgs_() {}");
    gold.append("\n  }");
    gold.append("\n  private static final MessageBridge<RTx__EmptyMessageNoArgs_> __BRIDGE___EmptyMessageNoArgs_ = new MessageBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public RTx__EmptyMessageNoArgs_ convert(ObjectNode __node) {");
    gold.append("\n      return new RTx__EmptyMessageNoArgs_(__node);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTx__EmptyMessageNoArgs_[] makeArray(int __n) {");
    gold.append("\n      return new RTx__EmptyMessageNoArgs_[__n];");
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
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    count.set(new RTx__EmptyMessageNoArgs_[] {}.length);");
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
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":2,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":2,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_IndexLookupLegacy_2 = null;
  private String get_IndexLookupLegacy_2() {
    if (cached_IndexLookupLegacy_2 != null) {
      return cached_IndexLookupLegacy_2;
    }
    cached_IndexLookupLegacy_2 = generateTestOutput(true, "IndexLookupLegacy_2", "./test_code/Array_IndexLookupLegacy_success.a");
    return cached_IndexLookupLegacy_2;
  }

  @Test
  public void testIndexLookupLegacyEmission() {
    assertEmissionGood(get_IndexLookupLegacy_2());
  }

  @Test
  public void testIndexLookupLegacySuccess() {
    assertLivePass(get_IndexLookupLegacy_2());
  }

  @Test
  public void testIndexLookupLegacyGoodWillHappy() {
    assertGoodWillHappy(get_IndexLookupLegacy_2());
  }

  @Test
  public void testIndexLookupLegacyExceptionFree() {
    assertExceptionFree(get_IndexLookupLegacy_2());
  }

  @Test
  public void testIndexLookupLegacyTODOFree() {
    assertTODOFree(get_IndexLookupLegacy_2());
  }

  @Test
  public void stable_IndexLookupLegacy_2() {
    String live = get_IndexLookupLegacy_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Array_IndexLookupLegacy_success.a");
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
    gold.append("\npublic class IndexLookupLegacy_2 extends LivingDocument {");
    gold.append("\n  private final RxTable<RTxX> tbl;");
    gold.append("\n  private final RxInt32 sum;");
    gold.append("\n  private final RxBoolean found_impossible_thing;");
    gold.append("\n  public IndexLookupLegacy_2(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    tbl = RxFactory.makeRxTable(__self, this, __root, \"tbl\", __BRIDGE_X);");
    gold.append("\n    sum = RxFactory.makeRxInt32(this, __root, \"sum\", 0);");
    gold.append("\n    found_impossible_thing = RxFactory.makeRxBoolean(this, __root, \"found_impossible_thing\", false);");
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
    gold.append("\n    tbl.__commit(\"tbl\", __child);");
    gold.append("\n    sum.__commit(\"sum\", __child);");
    gold.append("\n    found_impossible_thing.__commit(\"found_impossible_thing\", __child);");
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
    gold.append("\n    tbl.__revert();");
    gold.append("\n    sum.__revert();");
    gold.append("\n    found_impossible_thing.__revert();");
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
    gold.append("\n  private class RTxX extends RxRecordBase<RTxX> {");
    gold.append("\n    private final RxInt32 x;");
    gold.append("\n    private final RxInt32 y;");
    gold.append("\n    private final RxInt32 id;");
    gold.append("\n    private RTxX(ObjectNode __node, RxParent __owner) {");
    gold.append("\n      super(__owner);");
    gold.append("\n      x = RxFactory.makeRxInt32(this, __node, \"x\", 0);");
    gold.append("\n      y = RxFactory.makeRxInt32(this, __node, \"y\", 0);");
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
    gold.append("\n    String[] __INDEX_COLUMNS = new String[] {\"x\", \"y\"};");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int[] __getIndexValues() {");
    gold.append("\n      return new int[] {x.getIndexValue(), y.getIndexValue()};");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        ObjectNode __child = __delta.putObject(__name);");
    gold.append("\n        x.__commit(\"x\", __child);");
    gold.append("\n        y.__commit(\"y\", __child);");
    gold.append("\n        id.__commit(\"id\", __child);");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __revert() {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        __isDying = false;");
    gold.append("\n        x.__revert();");
    gold.append("\n        y.__revert();");
    gold.append("\n        id.__revert();");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public String __name() {");
    gold.append("\n      return \"X\";");
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
    gold.append("\n  private final RecordBridge<RTxX> __BRIDGE_X = new RecordBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public int  getNumberColumns() {");
    gold.append("\n      return 0;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxX construct(ObjectNode __item, RxParent __parent) {");
    gold.append("\n      return new RTxX(__item, __parent);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxX[] makeArray(int __n) {");
    gold.append("\n      return new RTxX[__n];");
    gold.append("\n    }");
    gold.append("\n  };");
    gold.append("\n  private static class RTx_AnonObjConvert_0 implements NtMessageBase {");
    gold.append("\n    private int x;");
    gold.append("\n    private int y;");
    gold.append("\n    private RTx_AnonObjConvert_0(ObjectNode payload) {");
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
    gold.append("\n    private RTx_AnonObjConvert_0(int x, int y) {");
    gold.append("\n      this.x = x;");
    gold.append("\n      this.y = y;");
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
    gold.append("\n    __code_cost += 11;");
    gold.append("\n    __track(0);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef1 = tbl;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr3 = new RTx_AnonObjConvert_0(100, 3);");
    gold.append("\n      RTxX _CreateRef2 = _AutoRef1.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef2.x.set(_AutoExpr3.x);");
    gold.append("\n      _CreateRef2.y.set(_AutoExpr3.y);");
    gold.append("\n    }");
    gold.append("\n    __track(1);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef4 = tbl;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr6 = new RTx_AnonObjConvert_0(2, 2);");
    gold.append("\n      RTxX _CreateRef5 = _AutoRef4.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef5.x.set(_AutoExpr6.x);");
    gold.append("\n      _CreateRef5.y.set(_AutoExpr6.y);");
    gold.append("\n    }");
    gold.append("\n    __track(2);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef7 = tbl;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr9 = new RTx_AnonObjConvert_0(100, 1);");
    gold.append("\n      RTxX _CreateRef8 = _AutoRef7.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef8.x.set(_AutoExpr9.x);");
    gold.append("\n      _CreateRef8.y.set(_AutoExpr9.y);");
    gold.append("\n    }");
    gold.append("\n    __track(3);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef10 = tbl;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr12 = new RTx_AnonObjConvert_0(4, 2);");
    gold.append("\n      RTxX _CreateRef11 = _AutoRef10.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef11.x.set(_AutoExpr12.x);");
    gold.append("\n      _CreateRef11.y.set(_AutoExpr12.y);");
    gold.append("\n    }");
    gold.append("\n    __track(4);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef13 = tbl;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr15 = new RTx_AnonObjConvert_0(5, 2);");
    gold.append("\n      RTxX _CreateRef14 = _AutoRef13.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef14.x.set(_AutoExpr15.x);");
    gold.append("\n      _CreateRef14.y.set(_AutoExpr15.y);");
    gold.append("\n    }");
    gold.append("\n    __track(5);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef16 = tbl;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr18 = new RTx_AnonObjConvert_0(1, 2);");
    gold.append("\n      RTxX _CreateRef17 = _AutoRef16.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef17.x.set(_AutoExpr18.x);");
    gold.append("\n      _CreateRef17.y.set(_AutoExpr18.y);");
    gold.append("\n    }");
    gold.append("\n    __track(6);");
    gold.append("\n    NtMaybe<RTxX> _AutoConditionthing_19;");
    gold.append("\n    if ((_AutoConditionthing_19 = (tbl.iterate(false).orderBy(true, __ORDER_X_x_d_y_d)).lookup(0)).has()) {");
    gold.append("\n      RTxX thing = _AutoConditionthing_19.get();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(7);");
    gold.append("\n      sum.set(thing.x.get() + thing.y.get());");
    gold.append("\n    }");
    gold.append("\n    __track(8);");
    gold.append("\n    found_impossible_thing.set(false);");
    gold.append("\n    __track(9);");
    gold.append("\n    NtMaybe<RTxX> _AutoConditionthing_20;");
    gold.append("\n    if ((_AutoConditionthing_20 = (tbl.iterate(false).orderBy(true, __ORDER_X_x_d_y_d)).lookup(100)).has()) {");
    gold.append("\n      RTxX thing = _AutoConditionthing_20.get();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(10);");
    gold.append("\n      found_impossible_thing.set(true);");
    gold.append("\n    }");
    gold.append("\n    __track(11);");
    gold.append("\n    NtMaybe<RTxX> _AutoConditionthing_21;");
    gold.append("\n    if ((_AutoConditionthing_21 = (tbl.iterate(false).orderBy(true, __ORDER_X_x_d_y_d)).lookup(-100)).has()) {");
    gold.append("\n      RTxX thing = _AutoConditionthing_21.get();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(12);");
    gold.append("\n      found_impossible_thing.set(true);");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {");
    gold.append("\n    __construct_0(who, message);");
    gold.append("\n  }");
    gold.append("\n  private final static Comparator<RTxX> __ORDER_X_x_d_y_d = new Comparator<RTxX>() {");
    gold.append("\n    @Override");
    gold.append("\n    public int compare(RTxX __a, RTxX __b) {");
    gold.append("\n      int result = -__a.x.compareTo(__b.x);");
    gold.append("\n      if (result != 0) return result;");
    gold.append("\n      return -__a.y.compareTo(__b.y);");
    gold.append("\n    }");
    gold.append("\n  };");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"tbl\":{\"auto_key\":6,\"rows\":{\"0\":{\"x\":100,\"y\":3},\"1\":{\"x\":2,\"y\":2},\"2\":{\"x\":100,\"y\":1},\"3\":{\"x\":4,\"y\":2},\"4\":{\"x\":5,\"y\":2},\"5\":{\"x\":1,\"y\":2}}},\"sum\":103,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":25,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"tbl\":{\"auto_key\":6,\"rows\":{\"0\":{\"x\":100,\"y\":3},\"1\":{\"x\":2,\"y\":2},\"2\":{\"x\":100,\"y\":1},\"3\":{\"x\":4,\"y\":2},\"4\":{\"x\":5,\"y\":2},\"5\":{\"x\":1,\"y\":2}}},\"sum\":103,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":25,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_OfAllTypes_3 = null;
  private String get_OfAllTypes_3() {
    if (cached_OfAllTypes_3 != null) {
      return cached_OfAllTypes_3;
    }
    cached_OfAllTypes_3 = generateTestOutput(true, "OfAllTypes_3", "./test_code/Array_OfAllTypes_success.a");
    return cached_OfAllTypes_3;
  }

  @Test
  public void testOfAllTypesEmission() {
    assertEmissionGood(get_OfAllTypes_3());
  }

  @Test
  public void testOfAllTypesSuccess() {
    assertLivePass(get_OfAllTypes_3());
  }

  @Test
  public void testOfAllTypesGoodWillHappy() {
    assertGoodWillHappy(get_OfAllTypes_3());
  }

  @Test
  public void testOfAllTypesExceptionFree() {
    assertExceptionFree(get_OfAllTypes_3());
  }

  @Test
  public void testOfAllTypesTODOFree() {
    assertTODOFree(get_OfAllTypes_3());
  }

  @Test
  public void stable_OfAllTypes_3() {
    String live = get_OfAllTypes_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Array_OfAllTypes_success.a");
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
    gold.append("\npublic class OfAllTypes_3 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  public OfAllTypes_3(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxInt32(this, __root, \"x\", 0);");
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
    gold.append("\n    return __view;");
    gold.append("\n  }");
    gold.append("\n  private static final int [] __ALL_VALUES_E = new int[] {0, 1};");
    gold.append("\n  private int __FUNC_0_mesum(int[] s) {");
    gold.append("\n    __code_cost += 4;");
    gold.append("\n    __track(0);");
    gold.append("\n    int r = 0;");
    gold.append("\n    __track(1);");
    gold.append("\n    for(Integer v : s) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(2);");
    gold.append("\n      r += v;");
    gold.append("\n    }");
    gold.append("\n    __track(3);");
    gold.append("\n    return r;");
    gold.append("\n  }");
    gold.append("\n  private void __FUNC_1_foo1(boolean[] b) {}");
    gold.append("\n  private void __FUNC_2_foo2(int[] b) {}");
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
    gold.append("\n    x.set(__FUNC_0_mesum(new int[] {1, 2, 3}));");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"x\":6,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":12,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"x\":6,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":12,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4 = null;
  private String get_ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4() {
    if (cached_ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4 != null) {
      return cached_ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4;
    }
    cached_ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4 = generateTestOutput(false, "ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4", "./test_code/Array_ParenthesisInvalidTypeDuringTwoPassTypeConstruction_failure.a");
    return cached_ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4;
  }

  @Test
  public void testParenthesisInvalidTypeDuringTwoPassTypeConstructionFailure() {
    assertLiveFail(get_ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4());
  }

  @Test
  public void testParenthesisInvalidTypeDuringTwoPassTypeConstructionExceptionFree() {
    assertExceptionFree(get_ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4());
  }

  @Test
  public void testParenthesisInvalidTypeDuringTwoPassTypeConstructionTODOFree() {
    assertTODOFree(get_ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4());
  }

  @Test
  public void stable_ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4() {
    String live = get_ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Array_ParenthesisInvalidTypeDuringTwoPassTypeConstruction_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 19");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 23");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type check failure: The types '_AnonObjConvert_0' and 'bool' are not compatible for type unification(TypeCompatabilities)\"");
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
    gold.append("\npublic class ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4 extends LivingDocument {");
    gold.append("\n  public ParenthesisInvalidTypeDuringTwoPassTypeConstruction_4(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(0);");
    gold.append("\n");
    gold.append("\n    __track(1);");
    gold.append("\n    int[] y = new int[] {1, 2};");
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
}

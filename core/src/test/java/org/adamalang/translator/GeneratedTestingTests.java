/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedTestingTests extends GeneratedBase {
  private String cached_CantForceOutsideOfTest_1 = null;
  private String get_CantForceOutsideOfTest_1() {
    if (cached_CantForceOutsideOfTest_1 != null) {
      return cached_CantForceOutsideOfTest_1;
    }
    cached_CantForceOutsideOfTest_1 = generateTestOutput(false, "CantForceOutsideOfTest_1", "./test_code/Testing_CantForceOutsideOfTest_failure.a");
    return cached_CantForceOutsideOfTest_1;
  }

  @Test
  public void testCantForceOutsideOfTestFailure() {
    assertLiveFail(get_CantForceOutsideOfTest_1());
  }

  @Test
  public void testCantForceOutsideOfTestExceptionFree() {
    assertExceptionFree(get_CantForceOutsideOfTest_1());
  }

  @Test
  public void testCantForceOutsideOfTestTODOFree() {
    assertTODOFree(get_CantForceOutsideOfTest_1());
  }

  @Test
  public void stable_CantForceOutsideOfTest_1() {
    String live = get_CantForceOutsideOfTest_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Testing_CantForceOutsideOfTest_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 2,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 2,");
    gold.append("\n      \"character\" : 8");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Forcing a step designed exclusively for testing(Testing)\"");
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
    gold.append("\npublic class CantForceOutsideOfTest_1 extends LivingDocument {");
    gold.append("\n  public CantForceOutsideOfTest_1(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n    __test_progress();");
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
  private String cached_CantPumpOutsideOfTest_2 = null;
  private String get_CantPumpOutsideOfTest_2() {
    if (cached_CantPumpOutsideOfTest_2 != null) {
      return cached_CantPumpOutsideOfTest_2;
    }
    cached_CantPumpOutsideOfTest_2 = generateTestOutput(false, "CantPumpOutsideOfTest_2", "./test_code/Testing_CantPumpOutsideOfTest_failure.a");
    return cached_CantPumpOutsideOfTest_2;
  }

  @Test
  public void testCantPumpOutsideOfTestFailure() {
    assertLiveFail(get_CantPumpOutsideOfTest_2());
  }

  @Test
  public void testCantPumpOutsideOfTestExceptionFree() {
    assertExceptionFree(get_CantPumpOutsideOfTest_2());
  }

  @Test
  public void testCantPumpOutsideOfTestTODOFree() {
    assertTODOFree(get_CantPumpOutsideOfTest_2());
  }

  @Test
  public void stable_CantPumpOutsideOfTest_2() {
    String live = get_CantPumpOutsideOfTest_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Testing_CantPumpOutsideOfTest_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 8,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 8,");
    gold.append("\n      \"character\" : 29");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Pumping a message is designed exclusively for testing(Testing)\"");
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
    gold.append("\npublic class CantPumpOutsideOfTest_2 extends LivingDocument {");
    gold.append("\n  public CantPumpOutsideOfTest_2(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n  private static class RTxX implements NtMessageBase {");
    gold.append("\n    private int x;");
    gold.append("\n    private int y;");
    gold.append("\n    private RTxX(ObjectNode payload) {");
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
    gold.append("\n    private RTxX(int x, int y) {");
    gold.append("\n      this.x = x;");
    gold.append("\n      this.y = y;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final MessageBridge<RTxX> __BRIDGE_X = new MessageBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public RTxX convert(ObjectNode __node) {");
    gold.append("\n      return new RTxX(__node);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxX[] makeArray(int __n) {");
    gold.append("\n      return new RTxX[__n];");
    gold.append("\n    }");
    gold.append("\n  };");
    gold.append("\n  private final Sink<RTxX> __queue_chan = new Sink<>(\"chan\");");
    gold.append("\n  private final NtChannel<RTxX> chan = new NtChannel<>(__futures, __queue_chan);");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    switch (task.channel) {");
    gold.append("\n      case \"chan\":");
    gold.append("\n        __queue_chan.enqueue(task, new RTxX(task.message));");
    gold.append("\n        return;");
    gold.append("\n      default:");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n    __queue_chan.clear();");
    gold.append("\n  }");
    gold.append("\n  private void __step_huh() {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    __queue.add(new AsyncTask(0, NtClient.NO_ONE, \"chan\", new RTxX(4, 8).convertToObjectNode()));");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"huh\":");
    gold.append("\n        __step_huh();");
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
  private String cached_Legacy_3 = null;
  private String get_Legacy_3() {
    if (cached_Legacy_3 != null) {
      return cached_Legacy_3;
    }
    cached_Legacy_3 = generateTestOutput(false, "Legacy_3", "./test_code/Testing_Legacy_failure.a");
    return cached_Legacy_3;
  }

  @Test
  public void testLegacyFailure() {
    assertLiveFail(get_Legacy_3());
  }

  @Test
  public void testLegacyExceptionFree() {
    assertExceptionFree(get_Legacy_3());
  }

  @Test
  public void testLegacyTODOFree() {
    assertTODOFree(get_Legacy_3());
  }

  @Test
  public void stable_Legacy_3() {
    String live = get_Legacy_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Testing_Legacy_failure.a");
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
    gold.append("\npublic class Legacy_3 extends LivingDocument {");
    gold.append("\n  private final RxString x;");
    gold.append("\n  private final RxTable<RTxX> zzz;");
    gold.append("\n  public Legacy_3(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxString(this, __root, \"x\", \"\");");
    gold.append("\n    zzz = RxFactory.makeRxTable(__self, this, __root, \"zzz\", __BRIDGE_X);");
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
    gold.append("\n    zzz.__revert();");
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
    gold.append("\n    private final RxInt32 z;");
    gold.append("\n    private final RxBoolean b;");
    gold.append("\n    private final RxInt32 id;");
    gold.append("\n    private RTxX(ObjectNode __node, RxParent __owner) {");
    gold.append("\n      super(__owner);");
    gold.append("\n      z = RxFactory.makeRxInt32(this, __node, \"z\", 0);");
    gold.append("\n      b = RxFactory.makeRxBoolean(this, __node, \"b\", false);");
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
    gold.append("\n    String[] __INDEX_COLUMNS = new String[] {\"z\"};");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int[] __getIndexValues() {");
    gold.append("\n      return new int[] {z.getIndexValue()};");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __commit(String __name, ObjectNode __delta) {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        ObjectNode __child = __delta.putObject(__name);");
    gold.append("\n        z.__commit(\"z\", __child);");
    gold.append("\n        b.__commit(\"b\", __child);");
    gold.append("\n        id.__commit(\"id\", __child);");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __revert() {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        __isDying = false;");
    gold.append("\n        z.__revert();");
    gold.append("\n        b.__revert();");
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
    gold.append("\n    private int z;");
    gold.append("\n    private RTx_AnonObjConvert_0(ObjectNode payload) {");
    gold.append("\n      this.z = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"z\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"z\", z, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_0(int z) {");
    gold.append("\n      this.z = z;");
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
    gold.append("\n  private void __step_next() {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    x.set(\"Bye\");");
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
    gold.append("\n  public void __test_Test1(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"Test1\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 6;");
    gold.append("\n      __track(1);");
    gold.append("\n      __assert_truth(LibString.equality(x.get(), \"\"), 23, 2, 23, 17);");
    gold.append("\n      __track(2);");
    gold.append("\n      __test_progress();");
    gold.append("\n      __track(3);");
    gold.append("\n      __assert_truth(LibString.equality(x.get(), \"Hello World\"), 25, 2, 25, 28);");
    gold.append("\n      __track(4);");
    gold.append("\n      __test_progress();");
    gold.append("\n      __track(5);");
    gold.append("\n      __assert_truth(LibString.equality(x.get(), \"Bye\"), 27, 2, 27, 20);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  public void __test_Test2(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"Test2\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(6);");
    gold.append("\n      __assert_truth(false, 31, 2, 31, 15);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  public void __test_Test3(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"Test3\");");
    gold.append("\n    {}");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {\"Test1\", \"Test2\", \"Test3\"};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {");
    gold.append("\n    switch(testName) {");
    gold.append("\n      case \"Test1\":");
    gold.append("\n          __test_Test1(report);");
    gold.append("\n          return;");
    gold.append("\n      case \"Test2\":");
    gold.append("\n          __test_Test2(report);");
    gold.append("\n          return;");
    gold.append("\n      case \"Test3\":");
    gold.append("\n          __test_Test3(report);");
    gold.append("\n          return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 7;");
    gold.append("\n    __track(7);");
    gold.append("\n    x.set(\"Hello World\");");
    gold.append("\n    __track(8);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef1 = zzz;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr3 = new RTx_AnonObjConvert_0(2);");
    gold.append("\n      RTxX _CreateRef2 = _AutoRef1.make();");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      _CreateRef2.z.set(_AutoExpr3.z);");
    gold.append("\n    }");
    gold.append("\n    __track(9);");
    gold.append("\n    (zzz.iterate(true)).transform((item) -> item.z, null /* no bridge needed */).transform((item) -> item.bumpUpPost(), null /** no bridge needed */);");
    gold.append("\n    __track(10);");
    gold.append("\n    NtList<RxInt32> _auto_4 = (zzz.iterate(true)).transform((item) -> item.z, null /* no bridge needed */);");
    gold.append("\n    for (RxInt32 _auto_5 : _auto_4) {");
    gold.append("\n      _auto_5.opAddTo(100);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    __track(11);");
    gold.append("\n    NtList<RxInt32> _auto_6 = (zzz.iterate(true)).transform((item) -> item.z, null /* no bridge needed */);");
    gold.append("\n    for (RxInt32 _auto_7 : _auto_6) {");
    gold.append("\n      _auto_7.opMultBy(3);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    __track(12);");
    gold.append("\n    __transitionStateMachine(\"next\", 0);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__state\":\"next\",\"__constructed\":true,\"x\":\"Hello World\",\"zzz\":{\"auto_key\":1,\"rows\":{\"0\":{\"z\":309}}},\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__state\":\"\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\",\"x\":\"Bye\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":10,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"x\":\"Bye\",\"zzz\":{\"auto_key\":1,\"rows\":{\"0\":{\"z\":309}}},\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":10,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nASSERT FAILURE:23,2 --> 23,17 (1/1)");
    gold.append("\nASSERT FAILURE:25,2 --> 25,28 (2/2)");
    gold.append("\nASSERT FAILURE:31,2 --> 31,15 (1/1)");
    gold.append("\nTEST[Test1] = 33.3% (HAS FAILURES)");
    gold.append("\nTEST[Test2] = 0.0% (HAS FAILURES)");
    gold.append("\nTEST[Test3] HAS NO ASSERTS");
    gold.append("\n");
    gold.append("\nAlmostTestsNotPassing");
    assertStable(live, gold);
  }
  private String cached_MessagingLegacy_4 = null;
  private String get_MessagingLegacy_4() {
    if (cached_MessagingLegacy_4 != null) {
      return cached_MessagingLegacy_4;
    }
    cached_MessagingLegacy_4 = generateTestOutput(true, "MessagingLegacy_4", "./test_code/Testing_MessagingLegacy_success.a");
    return cached_MessagingLegacy_4;
  }

  @Test
  public void testMessagingLegacyEmission() {
    assertEmissionGood(get_MessagingLegacy_4());
  }

  @Test
  public void testMessagingLegacySuccess() {
    assertLivePass(get_MessagingLegacy_4());
  }

  @Test
  public void testMessagingLegacyGoodWillHappy() {
    assertGoodWillHappy(get_MessagingLegacy_4());
  }

  @Test
  public void testMessagingLegacyExceptionFree() {
    assertExceptionFree(get_MessagingLegacy_4());
  }

  @Test
  public void testMessagingLegacyTODOFree() {
    assertTODOFree(get_MessagingLegacy_4());
  }

  @Test
  public void stable_MessagingLegacy_4() {
    String live = get_MessagingLegacy_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Testing_MessagingLegacy_success.a");
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
    gold.append("\npublic class MessagingLegacy_4 extends LivingDocument {");
    gold.append("\n  private final RxInt32 out;");
    gold.append("\n  public MessagingLegacy_4(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    out = RxFactory.makeRxInt32(this, __root, \"out\", 0);");
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
    gold.append("\n    out.__commit(\"out\", __child);");
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
    gold.append("\n    out.__revert();");
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
    gold.append("\n  private static class RTxX implements NtMessageBase {");
    gold.append("\n    private int x;");
    gold.append("\n    private int y;");
    gold.append("\n    private RTxX(ObjectNode payload) {");
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
    gold.append("\n    private RTxX(int x, int y) {");
    gold.append("\n      this.x = x;");
    gold.append("\n      this.y = y;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final MessageBridge<RTxX> __BRIDGE_X = new MessageBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public RTxX convert(ObjectNode __node) {");
    gold.append("\n      return new RTxX(__node);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxX[] makeArray(int __n) {");
    gold.append("\n      return new RTxX[__n];");
    gold.append("\n    }");
    gold.append("\n  };");
    gold.append("\n  private void handleChannelMessage_chan(NtClient client, RTxX payload) throws AbortMessageException {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    out.set(payload.x + payload.y);");
    gold.append("\n");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    switch (task.channel) {");
    gold.append("\n      case \"chan\":");
    gold.append("\n        task.setAction(() -> handleChannelMessage_chan(task.who, new RTxX(task.message)));");
    gold.append("\n        return;");
    gold.append("\n      default:");
    gold.append("\n        return;");
    gold.append("\n    }");
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
    gold.append("\n  public void __test_pumping(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"pumping\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 5;");
    gold.append("\n      __track(1);");
    gold.append("\n      __assert_truth(true, 12, 2, 12, 14);");
    gold.append("\n      __track(2);");
    gold.append("\n      __queue.add(new AsyncTask(0, NtClient.NO_ONE, \"chan\", new RTxX(4, 8).convertToObjectNode()));");
    gold.append("\n      __track(3);");
    gold.append("\n      __test_progress();");
    gold.append("\n      __track(4);");
    gold.append("\n      __assert_truth(out.get() == 12, 15, 2, 15, 19);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {\"pumping\"};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {");
    gold.append("\n    switch(testName) {");
    gold.append("\n      case \"pumping\":");
    gold.append("\n          __test_pumping(report);");
    gold.append("\n          return;");
    gold.append("\n    }");
    gold.append("\n  }");
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
    gold.append("\nTEST[pumping] = 100.0%");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_PumpMessages_5 = null;
  private String get_PumpMessages_5() {
    if (cached_PumpMessages_5 != null) {
      return cached_PumpMessages_5;
    }
    cached_PumpMessages_5 = generateTestOutput(true, "PumpMessages_5", "./test_code/Testing_PumpMessages_success.a");
    return cached_PumpMessages_5;
  }

  @Test
  public void testPumpMessagesEmission() {
    assertEmissionGood(get_PumpMessages_5());
  }

  @Test
  public void testPumpMessagesSuccess() {
    assertLivePass(get_PumpMessages_5());
  }

  @Test
  public void testPumpMessagesGoodWillHappy() {
    assertGoodWillHappy(get_PumpMessages_5());
  }

  @Test
  public void testPumpMessagesExceptionFree() {
    assertExceptionFree(get_PumpMessages_5());
  }

  @Test
  public void testPumpMessagesTODOFree() {
    assertTODOFree(get_PumpMessages_5());
  }

  @Test
  public void stable_PumpMessages_5() {
    String live = get_PumpMessages_5();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Testing_PumpMessages_success.a");
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
    gold.append("\npublic class PumpMessages_5 extends LivingDocument {");
    gold.append("\n  private final RxString status;");
    gold.append("\n  public PumpMessages_5(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    status = RxFactory.makeRxString(this, __root, \"status\", \"\");");
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
    gold.append("\n    status.__commit(\"status\", __child);");
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
    gold.append("\n    status.__revert();");
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
    gold.append("\n  private static class RTxX implements NtMessageBase {");
    gold.append("\n    private int x;");
    gold.append("\n    private int y;");
    gold.append("\n    private RTxX(ObjectNode payload) {");
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
    gold.append("\n    private RTxX(int x, int y) {");
    gold.append("\n      this.x = x;");
    gold.append("\n      this.y = y;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final MessageBridge<RTxX> __BRIDGE_X = new MessageBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public RTxX convert(ObjectNode __node) {");
    gold.append("\n      return new RTxX(__node);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxX[] makeArray(int __n) {");
    gold.append("\n      return new RTxX[__n];");
    gold.append("\n    }");
    gold.append("\n  };");
    gold.append("\n  private final Sink<RTxX> __queue_chan = new Sink<>(\"chan\");");
    gold.append("\n  private final NtChannel<RTxX> chan = new NtChannel<>(__futures, __queue_chan);");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    switch (task.channel) {");
    gold.append("\n      case \"chan\":");
    gold.append("\n        __queue_chan.enqueue(task, new RTxX(task.message));");
    gold.append("\n        return;");
    gold.append("\n      default:");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n    __queue_chan.clear();");
    gold.append("\n  }");
    gold.append("\n  private void __step_setup() {");
    gold.append("\n    __code_cost += 5;");
    gold.append("\n    __track(0);");
    gold.append("\n    status.set(\"Blocked\");");
    gold.append("\n    __track(1);");
    gold.append("\n    SimpleFuture<RTxX> fut = chan.fetch(NtClient.NO_ONE);");
    gold.append("\n    __track(2);");
    gold.append("\n    RTxX val = fut.await();");
    gold.append("\n    __track(3);");
    gold.append("\n    status.set(\"Value:\" + val.x + \"/\" + val.y);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"setup\":");
    gold.append("\n        __step_setup();");
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
    gold.append("\n  public void __test_drive_it(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"drive_it\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 12;");
    gold.append("\n      __track(4);");
    gold.append("\n      __assert_truth(!(__blocked.get()), 21, 2, 21, 21);");
    gold.append("\n      __track(5);");
    gold.append("\n      __assert_truth(LibString.equality(status.get(), \"\"), 22, 2, 22, 22);");
    gold.append("\n      __track(6);");
    gold.append("\n      __test_progress();");
    gold.append("\n      __track(7);");
    gold.append("\n      __assert_truth(__blocked.get(), 24, 2, 24, 18);");
    gold.append("\n      __track(8);");
    gold.append("\n      __assert_truth(LibString.equality(status.get(), \"\"), 25, 2, 25, 22);");
    gold.append("\n      __track(9);");
    gold.append("\n      __queue.add(new AsyncTask(0, NtClient.NO_ONE, \"chan\", new RTxX(4, 8).convertToObjectNode()));");
    gold.append("\n      __track(10);");
    gold.append("\n      __assert_truth(__blocked.get(), 27, 2, 27, 18);");
    gold.append("\n      __track(11);");
    gold.append("\n      __assert_truth(LibString.equality(status.get(), \"\"), 28, 2, 28, 22);");
    gold.append("\n      __track(12);");
    gold.append("\n      __test_progress();");
    gold.append("\n      __track(13);");
    gold.append("\n      __assert_truth(!(__blocked.get()), 30, 2, 30, 21);");
    gold.append("\n      __track(14);");
    gold.append("\n      __assert_truth(LibString.equality(status.get(), \"Value:4/8\"), 31, 2, 31, 31);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {\"drive_it\"};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {");
    gold.append("\n    switch(testName) {");
    gold.append("\n      case \"drive_it\":");
    gold.append("\n          __test_drive_it(report);");
    gold.append("\n          return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(15);");
    gold.append("\n    __transitionStateMachine(\"setup\", 0);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__state\":\"setup\",\"__constructed\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__seedUsed\":\"0\",\"__blocked_on\":\"chan\",\"__blocked\":true,\"__seq\":1} need:false in:0");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":7,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"setup\",\"__constructed\":true,\"__entropy\":\"0\",\"__seedUsed\":\"0\",\"__blocked_on\":\"chan\",\"__blocked\":true,\"__seq\":1,\"__goodwill_used\":0,\"__cost\":7,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[drive_it] = 100.0%");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_PumpNoChannel_6 = null;
  private String get_PumpNoChannel_6() {
    if (cached_PumpNoChannel_6 != null) {
      return cached_PumpNoChannel_6;
    }
    cached_PumpNoChannel_6 = generateTestOutput(false, "PumpNoChannel_6", "./test_code/Testing_PumpNoChannel_failure.a");
    return cached_PumpNoChannel_6;
  }

  @Test
  public void testPumpNoChannelFailure() {
    assertLiveFail(get_PumpNoChannel_6());
  }

  @Test
  public void testPumpNoChannelExceptionFree() {
    assertExceptionFree(get_PumpNoChannel_6());
  }

  @Test
  public void testPumpNoChannelTODOFree() {
    assertTODOFree(get_PumpNoChannel_6());
  }

  @Test
  public void stable_PumpNoChannel_6() {
    String live = get_PumpNoChannel_6();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Testing_PumpNoChannel_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 6,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 6,");
    gold.append("\n      \"character\" : 30");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Channel 'chanx' does not exist(Testing)\"");
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
    gold.append("\npublic class PumpNoChannel_6 extends LivingDocument {");
    gold.append("\n  public PumpNoChannel_6(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n  private static class RTxX implements NtMessageBase {");
    gold.append("\n    private int x;");
    gold.append("\n    private int y;");
    gold.append("\n    private RTxX(ObjectNode payload) {");
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
    gold.append("\n    private RTxX(int x, int y) {");
    gold.append("\n      this.x = x;");
    gold.append("\n      this.y = y;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static final MessageBridge<RTxX> __BRIDGE_X = new MessageBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public RTxX convert(ObjectNode __node) {");
    gold.append("\n      return new RTxX(__node);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxX[] makeArray(int __n) {");
    gold.append("\n      return new RTxX[__n];");
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
    gold.append("\n  public void __test_drive_it(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"drive_it\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(0);");
    gold.append("\n      __queue.add(new AsyncTask(0, NtClient.NO_ONE, \"chanx\", new RTxX(4, 8).convertToObjectNode()));");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {\"drive_it\"};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {");
    gold.append("\n    switch(testName) {");
    gold.append("\n      case \"drive_it\":");
    gold.append("\n          __test_drive_it(report);");
    gold.append("\n          return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_PumpNoMessage_7 = null;
  private String get_PumpNoMessage_7() {
    if (cached_PumpNoMessage_7 != null) {
      return cached_PumpNoMessage_7;
    }
    cached_PumpNoMessage_7 = generateTestOutput(false, "PumpNoMessage_7", "./test_code/Testing_PumpNoMessage_failure.a");
    return cached_PumpNoMessage_7;
  }

  @Test
  public void testPumpNoMessageFailure() {
    assertLiveFail(get_PumpNoMessage_7());
  }

  @Test
  public void testPumpNoMessageExceptionFree() {
    assertExceptionFree(get_PumpNoMessage_7());
  }

  @Test
  public void testPumpNoMessageTODOFree() {
    assertTODOFree(get_PumpNoMessage_7());
  }

  @Test
  public void stable_PumpNoMessage_7() {
    String live = get_PumpNoMessage_7();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Testing_PumpNoMessage_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 16");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type not found: a message named 'X' was not found.(TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 3,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 3,");
    gold.append("\n      \"character\" : 29");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type not found: a message named 'X' was not found.(TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 4,");
    gold.append("\n      \"character\" : 8");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 4,");
    gold.append("\n      \"character\" : 11");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type check failure: must have a type of 'message', but got a type of 'int'.(TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 4,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 4,");
    gold.append("\n      \"character\" : 19");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Channel 'x' does not exist(Testing)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 16");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type not found: a message named 'X' was not found.(TypeCheckReferences)\"");
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
    gold.append("\npublic class PumpNoMessage_7 extends LivingDocument {");
    gold.append("\n  public PumpNoMessage_7(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n    switch (task.channel) {");
    gold.append("\n      default:");
    gold.append("\n        return;");
    gold.append("\n    }");
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
    gold.append("\n  public void __test_drive_it(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"drive_it\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 3;");
    gold.append("\n      __track(0);");
    gold.append("\n      __queue.add(new AsyncTask(0, NtClient.NO_ONE, \"chan\", new RTx_AnonObjConvert_0(4, 8).convertToObjectNode()));");
    gold.append("\n      __track(1);");
    gold.append("\n      __queue.add(new AsyncTask(0, NtClient.NO_ONE, \"x\", 123.convertToObjectNode()));");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {\"drive_it\"};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {");
    gold.append("\n    switch(testName) {");
    gold.append("\n      case \"drive_it\":");
    gold.append("\n          __test_drive_it(report);");
    gold.append("\n          return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, ObjectNode message) {}");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

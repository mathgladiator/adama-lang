/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedControlTests extends GeneratedBase {
  private String cached_CantAbortOutsideOfMessageHandler_1 = null;
  private String get_CantAbortOutsideOfMessageHandler_1() {
    if (cached_CantAbortOutsideOfMessageHandler_1 != null) {
      return cached_CantAbortOutsideOfMessageHandler_1;
    }
    cached_CantAbortOutsideOfMessageHandler_1 = generateTestOutput(false, "CantAbortOutsideOfMessageHandler_1", "./test_code/Control_CantAbortOutsideOfMessageHandler_failure.a");
    return cached_CantAbortOutsideOfMessageHandler_1;
  }

  @Test
  public void testCantAbortOutsideOfMessageHandlerFailure() {
    assertLiveFail(get_CantAbortOutsideOfMessageHandler_1());
  }

  @Test
  public void testCantAbortOutsideOfMessageHandlerExceptionFree() {
    assertExceptionFree(get_CantAbortOutsideOfMessageHandler_1());
  }

  @Test
  public void testCantAbortOutsideOfMessageHandlerTODOFree() {
    assertTODOFree(get_CantAbortOutsideOfMessageHandler_1());
  }

  @Test
  public void stable_CantAbortOutsideOfMessageHandler_1() {
    String live = get_CantAbortOutsideOfMessageHandler_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Control_CantAbortOutsideOfMessageHandler_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 8,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 8,");
    gold.append("\n      \"character\" : 8");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Can only 'abort' from a message handler(EvaluationContext)\"");
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
    gold.append("\npublic class CantAbortOutsideOfMessageHandler_1 extends LivingDocument {");
    gold.append("\n  public CantAbortOutsideOfMessageHandler_1(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n    private RTxX(ObjectNode payload) {");
    gold.append("\n      this.x = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"x\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"x\", x, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTxX(int x) {");
    gold.append("\n      this.x = x;");
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
    gold.append("\n  private void handleChannelMessage_foo(NtClient client, RTxX x) throws AbortMessageException {");
    gold.append("\n    __code_cost += 1;");
    gold.append("\n");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    switch (task.channel) {");
    gold.append("\n      case \"foo\":");
    gold.append("\n        task.setAction(() -> handleChannelMessage_foo(task.who, new RTxX(task.message)));");
    gold.append("\n        return;");
    gold.append("\n      default:");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  private void __step_foo() {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    throw new AbortMessageException();");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"foo\":");
    gold.append("\n        __step_foo();");
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
  private String cached_CantBlockOutsideOfStateTransition_2 = null;
  private String get_CantBlockOutsideOfStateTransition_2() {
    if (cached_CantBlockOutsideOfStateTransition_2 != null) {
      return cached_CantBlockOutsideOfStateTransition_2;
    }
    cached_CantBlockOutsideOfStateTransition_2 = generateTestOutput(false, "CantBlockOutsideOfStateTransition_2", "./test_code/Control_CantBlockOutsideOfStateTransition_failure.a");
    return cached_CantBlockOutsideOfStateTransition_2;
  }

  @Test
  public void testCantBlockOutsideOfStateTransitionFailure() {
    assertLiveFail(get_CantBlockOutsideOfStateTransition_2());
  }

  @Test
  public void testCantBlockOutsideOfStateTransitionExceptionFree() {
    assertExceptionFree(get_CantBlockOutsideOfStateTransition_2());
  }

  @Test
  public void testCantBlockOutsideOfStateTransitionTODOFree() {
    assertTODOFree(get_CantBlockOutsideOfStateTransition_2());
  }

  @Test
  public void stable_CantBlockOutsideOfStateTransition_2() {
    String live = get_CantBlockOutsideOfStateTransition_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Control_CantBlockOutsideOfStateTransition_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 5,");
    gold.append("\n      \"character\" : 2");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 5,");
    gold.append("\n      \"character\" : 8");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Can only 'block' from a state machine transition(EvaluationContext)\"");
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
    gold.append("\npublic class CantBlockOutsideOfStateTransition_2 extends LivingDocument {");
    gold.append("\n  public CantBlockOutsideOfStateTransition_2(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n    private RTxX(ObjectNode payload) {");
    gold.append("\n      this.x = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"x\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"x\", x, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTxX(int x) {");
    gold.append("\n      this.x = x;");
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
    gold.append("\n  private void handleChannelMessage_foo(NtClient client, RTxX x) throws AbortMessageException {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    throw new ComputeBlockedException(null, null);");
    gold.append("\n");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    switch (task.channel) {");
    gold.append("\n      case \"foo\":");
    gold.append("\n        task.setAction(() -> handleChannelMessage_foo(task.who, new RTxX(task.message)));");
    gold.append("\n        return;");
    gold.append("\n      default:");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  private void __step_foo() {}");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"foo\":");
    gold.append("\n        __step_foo();");
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
  private String cached_LegacyBlock_3 = null;
  private String get_LegacyBlock_3() {
    if (cached_LegacyBlock_3 != null) {
      return cached_LegacyBlock_3;
    }
    cached_LegacyBlock_3 = generateTestOutput(true, "LegacyBlock_3", "./test_code/Control_LegacyBlock_success.a");
    return cached_LegacyBlock_3;
  }

  @Test
  public void testLegacyBlockEmission() {
    assertEmissionGood(get_LegacyBlock_3());
  }

  @Test
  public void testLegacyBlockSuccess() {
    assertLivePass(get_LegacyBlock_3());
  }

  @Test
  public void testLegacyBlockGoodWillHappy() {
    assertGoodWillHappy(get_LegacyBlock_3());
  }

  @Test
  public void testLegacyBlockExceptionFree() {
    assertExceptionFree(get_LegacyBlock_3());
  }

  @Test
  public void testLegacyBlockTODOFree() {
    assertTODOFree(get_LegacyBlock_3());
  }

  @Test
  public void stable_LegacyBlock_3() {
    String live = get_LegacyBlock_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Control_LegacyBlock_success.a");
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
    gold.append("\npublic class LegacyBlock_3 extends LivingDocument {");
    gold.append("\n  private final RxBoolean skip;");
    gold.append("\n  private final RxBoolean done;");
    gold.append("\n  private final RxInt32 lastValue;");
    gold.append("\n  public LegacyBlock_3(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    skip = RxFactory.makeRxBoolean(this, __root, \"skip\", false);");
    gold.append("\n    done = RxFactory.makeRxBoolean(this, __root, \"done\", false);");
    gold.append("\n    lastValue = RxFactory.makeRxInt32(this, __root, \"lastValue\", 0);");
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
    gold.append("\n    skip.__commit(\"skip\", __child);");
    gold.append("\n    done.__commit(\"done\", __child);");
    gold.append("\n    lastValue.__commit(\"lastValue\", __child);");
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
    gold.append("\n    skip.__revert();");
    gold.append("\n    done.__revert();");
    gold.append("\n    lastValue.__revert();");
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
    gold.append("\n    private int v;");
    gold.append("\n    private RTxX(ObjectNode payload) {");
    gold.append("\n      this.v = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"v\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"v\", v, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTxX(int v) {");
    gold.append("\n      this.v = v;");
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
    gold.append("\n  private void handleChannelMessage_x(NtClient client, RTxX msg) throws AbortMessageException {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(0);");
    gold.append("\n    lastValue.set(msg.v);");
    gold.append("\n    __track(1);");
    gold.append("\n    if (msg.v == 50) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(2);");
    gold.append("\n      skip.set(true);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    switch (task.channel) {");
    gold.append("\n      case \"x\":");
    gold.append("\n        task.setAction(() -> handleChannelMessage_x(task.who, new RTxX(task.message)));");
    gold.append("\n        return;");
    gold.append("\n      default:");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  private void __step_setup() {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(3);");
    gold.append("\n    if (!skip.get()) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(4);");
    gold.append("\n      throw new ComputeBlockedException(null, null);");
    gold.append("\n    }");
    gold.append("\n    __track(5);");
    gold.append("\n    done.set(true);");
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
    gold.append("\n  public void __test_foo(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"foo\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 9;");
    gold.append("\n      __track(6);");
    gold.append("\n      __queue.add(new AsyncTask(0, NtClient.NO_ONE, \"x\", new RTxX(42).convertToObjectNode()));");
    gold.append("\n      __track(7);");
    gold.append("\n      __test_progress();");
    gold.append("\n      __track(8);");
    gold.append("\n      __assert_truth(!done.get(), 29, 2, 29, 15);");
    gold.append("\n      __track(9);");
    gold.append("\n      __assert_truth(!skip.get(), 30, 2, 30, 15);");
    gold.append("\n      __track(10);");
    gold.append("\n      __queue.add(new AsyncTask(0, NtClient.NO_ONE, \"x\", new RTxX(50).convertToObjectNode()));");
    gold.append("\n      __track(11);");
    gold.append("\n      __test_progress();");
    gold.append("\n      __track(12);");
    gold.append("\n      __assert_truth(done.get(), 33, 2, 33, 14);");
    gold.append("\n      __track(13);");
    gold.append("\n      __assert_truth(skip.get(), 34, 2, 34, 14);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {\"foo\"};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {");
    gold.append("\n    switch(testName) {");
    gold.append("\n      case \"foo\":");
    gold.append("\n          __test_foo(report);");
    gold.append("\n          return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(14);");
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
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__seedUsed\":\"0\",\"__blocked_on\":null,\"__blocked\":true,\"__seq\":1} need:false in:0");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":7,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"setup\",\"__constructed\":true,\"__entropy\":\"0\",\"__seedUsed\":\"0\",\"__blocked\":true,\"__seq\":1,\"__goodwill_used\":0,\"__cost\":7,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[foo] = 100.0%");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_LegacyForEach_4 = null;
  private String get_LegacyForEach_4() {
    if (cached_LegacyForEach_4 != null) {
      return cached_LegacyForEach_4;
    }
    cached_LegacyForEach_4 = generateTestOutput(true, "LegacyForEach_4", "./test_code/Control_LegacyForEach_success.a");
    return cached_LegacyForEach_4;
  }

  @Test
  public void testLegacyForEachEmission() {
    assertEmissionGood(get_LegacyForEach_4());
  }

  @Test
  public void testLegacyForEachSuccess() {
    assertLivePass(get_LegacyForEach_4());
  }

  @Test
  public void testLegacyForEachGoodWillHappy() {
    assertGoodWillHappy(get_LegacyForEach_4());
  }

  @Test
  public void testLegacyForEachExceptionFree() {
    assertExceptionFree(get_LegacyForEach_4());
  }

  @Test
  public void testLegacyForEachTODOFree() {
    assertTODOFree(get_LegacyForEach_4());
  }

  @Test
  public void stable_LegacyForEach_4() {
    String live = get_LegacyForEach_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Control_LegacyForEach_success.a");
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
    gold.append("\npublic class LegacyForEach_4 extends LivingDocument {");
    gold.append("\n  private final RxTable<RTxX> zzz;");
    gold.append("\n  private final RxInt32 out;");
    gold.append("\n  public LegacyForEach_4(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    zzz = RxFactory.makeRxTable(__self, this, __root, \"zzz\", __BRIDGE_X);");
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
    gold.append("\n    zzz.__commit(\"zzz\", __child);");
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
    gold.append("\n    zzz.__revert();");
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
    gold.append("\n  private class RTxX extends RxRecordBase<RTxX> {");
    gold.append("\n    private final RxInt32 z;");
    gold.append("\n    private final RxInt32 id;");
    gold.append("\n    private RTxX(ObjectNode __node, RxParent __owner) {");
    gold.append("\n      super(__owner);");
    gold.append("\n      z = RxFactory.makeRxInt32(this, __node, \"z\", 0);");
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
    gold.append("\n        id.__commit(\"id\", __child);");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __revert() {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        __isDying = false;");
    gold.append("\n        z.__revert();");
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
    gold.append("\n  private void __step_setup() {");
    gold.append("\n    __code_cost += 4;");
    gold.append("\n    __track(0);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef1 = zzz;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr3 = new RTx_AnonObjConvert_0(2);");
    gold.append("\n      RTxX _CreateRef2 = _AutoRef1.make();");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      _CreateRef2.z.set(_AutoExpr3.z);");
    gold.append("\n    }");
    gold.append("\n    __track(1);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef4 = zzz;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr6 = new RTx_AnonObjConvert_0(3);");
    gold.append("\n      RTxX _CreateRef5 = _AutoRef4.make();");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      _CreateRef5.z.set(_AutoExpr6.z);");
    gold.append("\n    }");
    gold.append("\n    __track(2);");
    gold.append("\n    for(RTxX chk : (zzz.iterate(true))) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(3);");
    gold.append("\n      out.opAddTo(chk.z.get());");
    gold.append("\n    }");
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
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {}");
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(4);");
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
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__state\":\"\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\",\"zzz\":{\"auto_key\":2,\"rows\":{\"0\":{\"z\":2},\"1\":{\"z\":3}}},\"out\":5} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":12,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"zzz\":{\"auto_key\":2,\"rows\":{\"0\":{\"z\":2},\"1\":{\"z\":3}}},\"out\":5,\"__goodwill_used\":0,\"__cost\":12,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_LegacyIf_5 = null;
  private String get_LegacyIf_5() {
    if (cached_LegacyIf_5 != null) {
      return cached_LegacyIf_5;
    }
    cached_LegacyIf_5 = generateTestOutput(true, "LegacyIf_5", "./test_code/Control_LegacyIf_success.a");
    return cached_LegacyIf_5;
  }

  @Test
  public void testLegacyIfEmission() {
    assertEmissionGood(get_LegacyIf_5());
  }

  @Test
  public void testLegacyIfSuccess() {
    assertLivePass(get_LegacyIf_5());
  }

  @Test
  public void testLegacyIfGoodWillHappy() {
    assertGoodWillHappy(get_LegacyIf_5());
  }

  @Test
  public void testLegacyIfExceptionFree() {
    assertExceptionFree(get_LegacyIf_5());
  }

  @Test
  public void testLegacyIfTODOFree() {
    assertTODOFree(get_LegacyIf_5());
  }

  @Test
  public void stable_LegacyIf_5() {
    String live = get_LegacyIf_5();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Control_LegacyIf_success.a");
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
    gold.append("\npublic class LegacyIf_5 extends LivingDocument {");
    gold.append("\n  private final RxMaybe<RxInt32> x;");
    gold.append("\n  private final RxInt32 y;");
    gold.append("\n  private final RxMaybe<RxBoolean> b;");
    gold.append("\n  private final RxMaybe<RxString> s;");
    gold.append("\n  private final RxMaybe<RxDouble> d;");
    gold.append("\n  private final RxMaybe<RxString> s2;");
    gold.append("\n  private final RxInt32 testDel;");
    gold.append("\n  private final RxBoolean bbbb;");
    gold.append("\n  public LegacyIf_5(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxMaybe(this, __root, \"x\", (RxParent __parent) -> RxFactory.makeRxInt32(__parent, __root, \"x\", 0));");
    gold.append("\n    y = RxFactory.makeRxInt32(this, __root, \"y\", 0);");
    gold.append("\n    b = RxFactory.makeRxMaybe(this, __root, \"b\", (RxParent __parent) -> RxFactory.makeRxBoolean(__parent, __root, \"b\", false));");
    gold.append("\n    s = RxFactory.makeRxMaybe(this, __root, \"s\", (RxParent __parent) -> RxFactory.makeRxString(__parent, __root, \"s\", \"\"));");
    gold.append("\n    d = RxFactory.makeRxMaybe(this, __root, \"d\", (RxParent __parent) -> RxFactory.makeRxDouble(__parent, __root, \"d\", 0.0));");
    gold.append("\n    s2 = RxFactory.makeRxMaybe(this, __root, \"s2\", (RxParent __parent) -> RxFactory.makeRxString(__parent, __root, \"s2\", \"\"));");
    gold.append("\n    testDel = RxFactory.makeRxInt32(this, __root, \"testDel\", 0);");
    gold.append("\n    bbbb = RxFactory.makeRxBoolean(this, __root, \"bbbb\", false);");
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
    gold.append("\n    b.__commit(\"b\", __child);");
    gold.append("\n    s.__commit(\"s\", __child);");
    gold.append("\n    d.__commit(\"d\", __child);");
    gold.append("\n    s2.__commit(\"s2\", __child);");
    gold.append("\n    testDel.__commit(\"testDel\", __child);");
    gold.append("\n    bbbb.__commit(\"bbbb\", __child);");
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
    gold.append("\n    b.__revert();");
    gold.append("\n    s.__revert();");
    gold.append("\n    d.__revert();");
    gold.append("\n    s2.__revert();");
    gold.append("\n    testDel.__revert();");
    gold.append("\n    bbbb.__revert();");
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
    gold.append("\n  private void __step_setup() {");
    gold.append("\n    __code_cost += 8;");
    gold.append("\n    __track(0);");
    gold.append("\n    NtMaybe<Integer> _AutoConditionxx_0;");
    gold.append("\n    if ((_AutoConditionxx_0 = x.get()).has()) {");
    gold.append("\n      int xx = _AutoConditionxx_0.get();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(1);");
    gold.append("\n      y.set(xx * 2);");
    gold.append("\n    }");
    gold.append("\n    __track(2);");
    gold.append("\n    x.make().set(42);");
    gold.append("\n    __track(3);");
    gold.append("\n    NtMaybe<Integer> _AutoConditionxx_1;");
    gold.append("\n    if ((_AutoConditionxx_1 = x.get()).has()) {");
    gold.append("\n      int xx = _AutoConditionxx_1.get();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(4);");
    gold.append("\n      y.set(xx * 3);");
    gold.append("\n    } else {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(5);");
    gold.append("\n      y.set(42);");
    gold.append("\n    }");
    gold.append("\n    __track(6);");
    gold.append("\n    b.make().set(true);");
    gold.append("\n    __track(7);");
    gold.append("\n    s.make().set(\"Hi There\");");
    gold.append("\n    __track(8);");
    gold.append("\n    d.make().set(3.14);");
    gold.append("\n    __track(9);");
    gold.append("\n    __transitionStateMachine(\"next\", 0);");
    gold.append("\n  }");
    gold.append("\n  private void __step_next() {");
    gold.append("\n    __code_cost += 6;");
    gold.append("\n    __track(10);");
    gold.append("\n    x.make().set(40);");
    gold.append("\n    __track(11);");
    gold.append("\n    b.make().set(false);");
    gold.append("\n    __track(12);");
    gold.append("\n    s.make().set(\"Hello There\");");
    gold.append("\n    __track(13);");
    gold.append("\n    d.make().set(2.71);");
    gold.append("\n    __track(14);");
    gold.append("\n    __transitionStateMachine(\"end\", 0);");
    gold.append("\n  }");
    gold.append("\n  private void __step_end() {");
    gold.append("\n    __code_cost += 6;");
    gold.append("\n    __track(15);");
    gold.append("\n    x.get().delete();");
    gold.append("\n    __track(16);");
    gold.append("\n    b.get().delete();");
    gold.append("\n    __track(17);");
    gold.append("\n    s.get().delete();");
    gold.append("\n    __track(18);");
    gold.append("\n    d.get().delete();");
    gold.append("\n    __track(19);");
    gold.append("\n    __transitionStateMachine(\"done\", 0);");
    gold.append("\n  }");
    gold.append("\n  private void __step_done() {");
    gold.append("\n    __code_cost += 10;");
    gold.append("\n    __track(20);");
    gold.append("\n    NtMaybe<Boolean> _AutoConditionbbb_2;");
    gold.append("\n    if ((_AutoConditionbbb_2 = b.get()).has()) {");
    gold.append("\n      boolean bbb = _AutoConditionbbb_2.get();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(21);");
    gold.append("\n      testDel.set(1);");
    gold.append("\n    } else {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(22);");
    gold.append("\n      testDel.set(0);");
    gold.append("\n    }");
    gold.append("\n    __track(23);");
    gold.append("\n    NtMaybe<String> _AutoConditionsss_3;");
    gold.append("\n    if ((_AutoConditionsss_3 = s.get()).has()) {");
    gold.append("\n      String sss = _AutoConditionsss_3.get();");
    gold.append("\n      __code_cost += 3;");
    gold.append("\n      __track(24);");
    gold.append("\n      testDel.opAddTo(4);");
    gold.append("\n      __track(25);");
    gold.append("\n      s2.make().set(sss);");
    gold.append("\n    } else {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(26);");
    gold.append("\n      testDel.opAddTo(2);");
    gold.append("\n    }");
    gold.append("\n    __track(27);");
    gold.append("\n    NtMaybe<Double> _AutoConditionddd_4;");
    gold.append("\n    if ((_AutoConditionddd_4 = d.get()).has()) {");
    gold.append("\n      double ddd = _AutoConditionddd_4.get();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(28);");
    gold.append("\n      testDel.opAddTo(16);");
    gold.append("\n    } else {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(29);");
    gold.append("\n      testDel.opAddTo(8);");
    gold.append("\n    }");
    gold.append("\n    __track(30);");
    gold.append("\n    NtMaybe<Boolean> mmbb = new NtMaybe<Boolean>();");
    gold.append("\n    __track(31);");
    gold.append("\n    NtMaybe<Boolean> mmbb2 = new NtMaybe<Boolean>(true);");
    gold.append("\n    __track(32);");
    gold.append("\n    mmbb.set(true);");
    gold.append("\n    __track(33);");
    gold.append("\n    NtMaybe<Boolean> _AutoConditionmbv_5;");
    gold.append("\n    if ((_AutoConditionmbv_5 = mmbb).has()) {");
    gold.append("\n      boolean mbv = _AutoConditionmbv_5.get();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(34);");
    gold.append("\n      __assert_truth(mbv, 70, 4, 70, 15);");
    gold.append("\n    }");
    gold.append("\n    __track(35);");
    gold.append("\n    mmbb.delete();");
    gold.append("\n    __track(36);");
    gold.append("\n    NtMaybe<Boolean> _AutoConditionmbv_6;");
    gold.append("\n    if ((_AutoConditionmbv_6 = mmbb).has()) {");
    gold.append("\n      boolean mbv = _AutoConditionmbv_6.get();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(37);");
    gold.append("\n      bbbb.set(mbv);");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"setup\":");
    gold.append("\n        __step_setup();");
    gold.append("\n        return;");
    gold.append("\n      case \"next\":");
    gold.append("\n        __step_next();");
    gold.append("\n        return;");
    gold.append("\n      case \"end\":");
    gold.append("\n        __step_end();");
    gold.append("\n        return;");
    gold.append("\n      case \"done\":");
    gold.append("\n        __step_done();");
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
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(38);");
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
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__state\":\"next\",!TimeHiddenForStability!\"25\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\",\"x\":42,\"y\":126,\"b\":true,\"s\":\"Hi There\",\"d\":3.14} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__state\":\"end\",!TimeHiddenForStability!\"50\",\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"50\",\"x\":40,\"b\":false,\"s\":\"Hello There\",\"d\":2.71} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"75\"}-->{\"__messages\":null,\"__seedUsed\":\"4804307197456638271\",\"__state\":\"done\",!TimeHiddenForStability!\"75\",\"__seq\":3,\"__entropy\":\"-1034601897293430941\",\"__time\":\"75\",\"x\":null,\"b\":null,\"s\":null,\"d\":null} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seedUsed\":\"-1034601897293430941\",\"__state\":\"\",\"__seq\":4,\"__entropy\":\"7848011421992302230\",\"__time\":\"100\",\"testDel\":10} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"125\"}-->{\"__goodwill_used\":0,\"__cost\":42,\"__billing_seq\":4} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"7848011421992302230\",\"__seedUsed\":\"-1034601897293430941\",!TimeHiddenForStability!\"75\",\"__seq\":4,\"__time\":\"100\",\"y\":126,\"testDel\":10,\"__goodwill_used\":0,\"__cost\":42,\"__billing_seq\":4}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_LegacyLoops_6 = null;
  private String get_LegacyLoops_6() {
    if (cached_LegacyLoops_6 != null) {
      return cached_LegacyLoops_6;
    }
    cached_LegacyLoops_6 = generateTestOutput(true, "LegacyLoops_6", "./test_code/Control_LegacyLoops_success.a");
    return cached_LegacyLoops_6;
  }

  @Test
  public void testLegacyLoopsEmission() {
    assertEmissionGood(get_LegacyLoops_6());
  }

  @Test
  public void testLegacyLoopsSuccess() {
    assertLivePass(get_LegacyLoops_6());
  }

  @Test
  public void testLegacyLoopsGoodWillHappy() {
    assertGoodWillHappy(get_LegacyLoops_6());
  }

  @Test
  public void testLegacyLoopsExceptionFree() {
    assertExceptionFree(get_LegacyLoops_6());
  }

  @Test
  public void testLegacyLoopsTODOFree() {
    assertTODOFree(get_LegacyLoops_6());
  }

  @Test
  public void stable_LegacyLoops_6() {
    String live = get_LegacyLoops_6();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Control_LegacyLoops_success.a");
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
    gold.append("\npublic class LegacyLoops_6 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxInt32 y;");
    gold.append("\n  private final RxInt32 out;");
    gold.append("\n  private final RxTable<RTxX> tbl2;");
    gold.append("\n  public LegacyLoops_6(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxInt32(this, __root, \"x\", 0);");
    gold.append("\n    y = RxFactory.makeRxInt32(this, __root, \"y\", 0);");
    gold.append("\n    out = RxFactory.makeRxInt32(this, __root, \"out\", 0);");
    gold.append("\n    tbl2 = RxFactory.makeRxTable(__self, this, __root, \"tbl2\", __BRIDGE_X);");
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
    gold.append("\n    out.__commit(\"out\", __child);");
    gold.append("\n    tbl2.__commit(\"tbl2\", __child);");
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
    gold.append("\n    out.__revert();");
    gold.append("\n    tbl2.__revert();");
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
    gold.append("\n    private int x;");
    gold.append("\n    private int y;");
    gold.append("\n    private RTx_AnonObjConvert_2(ObjectNode payload) {");
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
    gold.append("\n    private RTx_AnonObjConvert_2(int x, int y) {");
    gold.append("\n      this.x = x;");
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
    gold.append("\n  private int __FUNC_0_foo() {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(0);");
    gold.append("\n    {");
    gold.append("\n      for (;__goodwill(5, 2, 7, 3);) {");
    gold.append("\n        __code_cost += 2;");
    gold.append("\n        __track(1);");
    gold.append("\n        return 1;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    __track(2);");
    gold.append("\n    return 0;");
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
    gold.append("\n    __code_cost += 20;");
    gold.append("\n    __track(0);");
    gold.append("\n    x.set(0);");
    gold.append("\n    __track(1);");
    gold.append("\n    while (__goodwill(20, 9, 20, 14) && (x.get() < 5)) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(2);");
    gold.append("\n      x.bumpUpPost();");
    gold.append("\n    }");
    gold.append("\n    __track(3);");
    gold.append("\n    do {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(4);");
    gold.append("\n      x.bumpDownPost();");
    gold.append("\n    } while (__goodwill(26, 11, 26, 16) && (x.get() > 0));");
    gold.append("\n    __track(5);");
    gold.append("\n    y.set(1);");
    gold.append("\n    __track(6);");
    gold.append("\n    {");
    gold.append("\n      int z = 2;");
    gold.append("\n      for (;__goodwill(29, 17, 29, 23) && (z < 10);z++) {");
    gold.append("\n        __code_cost += 2;");
    gold.append("\n        __track(7);");
    gold.append("\n        y.set(z * y.get());");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    __track(8);");
    gold.append("\n    int z = 2;");
    gold.append("\n    __track(9);");
    gold.append("\n    {");
    gold.append("\n      for (;__goodwill(34, 8, 34, 14) && (z < 10);z++) {");
    gold.append("\n        __code_cost += 2;");
    gold.append("\n        __track(10);");
    gold.append("\n        y.set(z * y.get());");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    __track(11);");
    gold.append("\n    z = 2;");
    gold.append("\n    __track(12);");
    gold.append("\n    {");
    gold.append("\n      for (;__goodwill(39, 8, 39, 14) && (z < 10);) {");
    gold.append("\n        __code_cost += 3;");
    gold.append("\n        __track(13);");
    gold.append("\n        y.set(z * y.get());");
    gold.append("\n        __track(14);");
    gold.append("\n        z++;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    __track(15);");
    gold.append("\n    y.set(0);");
    gold.append("\n    __track(16);");
    gold.append("\n    for(RTx_AnonObjConvert_1 k : new RTx_AnonObjConvert_1[] {new RTx_AnonObjConvert_1(1), new RTx_AnonObjConvert_1(2)}) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(17);");
    gold.append("\n      y.opAddTo(k.x);");
    gold.append("\n    }");
    gold.append("\n    __track(18);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef3 = tbl2;");
    gold.append("\n      RTx_AnonObjConvert_2 _AutoExpr5 = new RTx_AnonObjConvert_2(100, 3);");
    gold.append("\n      RTxX _CreateRef4 = _AutoRef3.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef4.x.set(_AutoExpr5.x);");
    gold.append("\n      _CreateRef4.y.set(_AutoExpr5.y);");
    gold.append("\n    }");
    gold.append("\n    __track(19);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef6 = tbl2;");
    gold.append("\n      RTx_AnonObjConvert_2 _AutoExpr8 = new RTx_AnonObjConvert_2(2, 2);");
    gold.append("\n      RTxX _CreateRef7 = _AutoRef6.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef7.x.set(_AutoExpr8.x);");
    gold.append("\n      _CreateRef7.y.set(_AutoExpr8.y);");
    gold.append("\n    }");
    gold.append("\n    __track(20);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef9 = tbl2;");
    gold.append("\n      RTx_AnonObjConvert_2 _AutoExpr11 = new RTx_AnonObjConvert_2(100, 1);");
    gold.append("\n      RTxX _CreateRef10 = _AutoRef9.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef10.x.set(_AutoExpr11.x);");
    gold.append("\n      _CreateRef10.y.set(_AutoExpr11.y);");
    gold.append("\n    }");
    gold.append("\n    __track(21);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef12 = tbl2;");
    gold.append("\n      RTx_AnonObjConvert_2 _AutoExpr14 = new RTx_AnonObjConvert_2(4, 2);");
    gold.append("\n      RTxX _CreateRef13 = _AutoRef12.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef13.x.set(_AutoExpr14.x);");
    gold.append("\n      _CreateRef13.y.set(_AutoExpr14.y);");
    gold.append("\n    }");
    gold.append("\n    __track(22);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef15 = tbl2;");
    gold.append("\n      RTx_AnonObjConvert_2 _AutoExpr17 = new RTx_AnonObjConvert_2(5, 2);");
    gold.append("\n      RTxX _CreateRef16 = _AutoRef15.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef16.x.set(_AutoExpr17.x);");
    gold.append("\n      _CreateRef16.y.set(_AutoExpr17.y);");
    gold.append("\n    }");
    gold.append("\n    __track(23);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef18 = tbl2;");
    gold.append("\n      RTx_AnonObjConvert_2 _AutoExpr20 = new RTx_AnonObjConvert_2(1, 2);");
    gold.append("\n      RTxX _CreateRef19 = _AutoRef18.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef19.x.set(_AutoExpr20.x);");
    gold.append("\n      _CreateRef19.y.set(_AutoExpr20.y);");
    gold.append("\n    }");
    gold.append("\n    __track(24);");
    gold.append("\n    out.set(0);");
    gold.append("\n    __track(25);");
    gold.append("\n    for(RTxX vv : tbl2.iterate(true)) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(26);");
    gold.append("\n      out.opAddTo(vv.x.get() + vv.y.get());");
    gold.append("\n    }");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"x\":0,\"y\":3,\"out\":224,\"tbl2\":{\"auto_key\":6,\"rows\":{\"0\":{\"x\":100,\"y\":3},\"1\":{\"x\":2,\"y\":2},\"2\":{\"x\":100,\"y\":1},\"3\":{\"x\":4,\"y\":2},\"4\":{\"x\":5,\"y\":2},\"5\":{\"x\":1,\"y\":2}}},\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":38,\"__cost\":124,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"x\":0,\"y\":3,\"out\":224,\"tbl2\":{\"auto_key\":6,\"rows\":{\"0\":{\"x\":100,\"y\":3},\"1\":{\"x\":2,\"y\":2},\"2\":{\"x\":100,\"y\":1},\"3\":{\"x\":4,\"y\":2},\"4\":{\"x\":5,\"y\":2},\"5\":{\"x\":1,\"y\":2}}},\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":38,\"__cost\":124,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_Legacy_7 = null;
  private String get_Legacy_7() {
    if (cached_Legacy_7 != null) {
      return cached_Legacy_7;
    }
    cached_Legacy_7 = generateTestOutput(true, "Legacy_7", "./test_code/Control_Legacy_success.a");
    return cached_Legacy_7;
  }

  @Test
  public void testLegacyEmission() {
    assertEmissionGood(get_Legacy_7());
  }

  @Test
  public void testLegacySuccess() {
    assertLivePass(get_Legacy_7());
  }

  @Test
  public void testLegacyGoodWillHappy() {
    assertGoodWillHappy(get_Legacy_7());
  }

  @Test
  public void testLegacyExceptionFree() {
    assertExceptionFree(get_Legacy_7());
  }

  @Test
  public void testLegacyTODOFree() {
    assertTODOFree(get_Legacy_7());
  }

  @Test
  public void stable_Legacy_7() {
    String live = get_Legacy_7();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Control_Legacy_success.a");
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
    gold.append("\npublic class Legacy_7 extends LivingDocument {");
    gold.append("\n  private final RxInt32 sum;");
    gold.append("\n  private final RxInt32 lastV;");
    gold.append("\n  public Legacy_7(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    sum = RxFactory.makeRxInt32(this, __root, \"sum\", 0);");
    gold.append("\n    lastV = RxFactory.makeRxInt32(this, __root, \"lastV\", 0);");
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
    gold.append("\n    sum.__commit(\"sum\", __child);");
    gold.append("\n    lastV.__commit(\"lastV\", __child);");
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
    gold.append("\n    sum.__revert();");
    gold.append("\n    lastV.__revert();");
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
    gold.append("\n    private int v;");
    gold.append("\n    private RTxX(ObjectNode payload) {");
    gold.append("\n      this.v = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"v\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"v\", v, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTxX(int v) {");
    gold.append("\n      this.v = v;");
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
    gold.append("\n  private void handleChannelMessage_x(NtClient client, RTxX msg) throws AbortMessageException {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(0);");
    gold.append("\n    lastV.set(msg.v);");
    gold.append("\n    __track(1);");
    gold.append("\n    if (msg.v == 32) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(2);");
    gold.append("\n      throw new AbortMessageException();");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    switch (task.channel) {");
    gold.append("\n      case \"x\":");
    gold.append("\n        task.setAction(() -> handleChannelMessage_x(task.who, new RTxX(task.message)));");
    gold.append("\n        return;");
    gold.append("\n      default:");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  private void __step_setup() {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(3);");
    gold.append("\n    {");
    gold.append("\n      int k = 0;");
    gold.append("\n      for (;__goodwill(21, 18, 21, 25) && (k < 100);k++) {");
    gold.append("\n        __code_cost += 4;");
    gold.append("\n        __track(4);");
    gold.append("\n        if (k == 5) {");
    gold.append("\n          __code_cost += 2;");
    gold.append("\n          __track(5);");
    gold.append("\n          continue;");
    gold.append("\n        }");
    gold.append("\n        __track(6);");
    gold.append("\n        if (k == 50) {");
    gold.append("\n          __code_cost += 2;");
    gold.append("\n          __track(7);");
    gold.append("\n          break;");
    gold.append("\n        }");
    gold.append("\n        __track(8);");
    gold.append("\n        sum.opAddTo(k);");
    gold.append("\n      }");
    gold.append("\n    }");
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
    gold.append("\n  public void __test_foo(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"foo\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 10;");
    gold.append("\n      __track(9);");
    gold.append("\n      __queue.add(new AsyncTask(0, NtClient.NO_ONE, \"x\", new RTxX(42).convertToObjectNode()));");
    gold.append("\n      __track(10);");
    gold.append("\n      __test_progress();");
    gold.append("\n      __track(11);");
    gold.append("\n      __assert_truth(lastV.get() == 42, 35, 2, 35, 21);");
    gold.append("\n      __track(12);");
    gold.append("\n      __queue.add(new AsyncTask(0, NtClient.NO_ONE, \"x\", new RTxX(32).convertToObjectNode()));");
    gold.append("\n      __track(13);");
    gold.append("\n      __test_progress();");
    gold.append("\n      __track(14);");
    gold.append("\n      __assert_truth(lastV.get() == 42, 38, 2, 38, 21);");
    gold.append("\n      __track(15);");
    gold.append("\n      __queue.add(new AsyncTask(0, NtClient.NO_ONE, \"x\", new RTxX(50).convertToObjectNode()));");
    gold.append("\n      __track(16);");
    gold.append("\n      __test_progress();");
    gold.append("\n      __track(17);");
    gold.append("\n      __assert_truth(lastV.get() == 50, 41, 2, 41, 21);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {\"foo\"};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {");
    gold.append("\n    switch(testName) {");
    gold.append("\n      case \"foo\":");
    gold.append("\n          __test_foo(report);");
    gold.append("\n          return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(18);");
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
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__state\":\"\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\",\"sum\":1220} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":51,\"__cost\":212,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"sum\":1220,\"__goodwill_used\":51,\"__cost\":212,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[foo] = 100.0%");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_MegaIfLegacy_8 = null;
  private String get_MegaIfLegacy_8() {
    if (cached_MegaIfLegacy_8 != null) {
      return cached_MegaIfLegacy_8;
    }
    cached_MegaIfLegacy_8 = generateTestOutput(true, "MegaIfLegacy_8", "./test_code/Control_MegaIfLegacy_success.a");
    return cached_MegaIfLegacy_8;
  }

  @Test
  public void testMegaIfLegacyEmission() {
    assertEmissionGood(get_MegaIfLegacy_8());
  }

  @Test
  public void testMegaIfLegacySuccess() {
    assertLivePass(get_MegaIfLegacy_8());
  }

  @Test
  public void testMegaIfLegacyGoodWillHappy() {
    assertGoodWillHappy(get_MegaIfLegacy_8());
  }

  @Test
  public void testMegaIfLegacyExceptionFree() {
    assertExceptionFree(get_MegaIfLegacy_8());
  }

  @Test
  public void testMegaIfLegacyTODOFree() {
    assertTODOFree(get_MegaIfLegacy_8());
  }

  @Test
  public void stable_MegaIfLegacy_8() {
    String live = get_MegaIfLegacy_8();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Control_MegaIfLegacy_success.a");
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
    gold.append("\npublic class MegaIfLegacy_8 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxInt32 y;");
    gold.append("\n  public MegaIfLegacy_8(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n    __code_cost += 4;");
    gold.append("\n    __track(0);");
    gold.append("\n    if (true) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(1);");
    gold.append("\n      x.set(1);");
    gold.append("\n    }");
    gold.append("\n    __track(2);");
    gold.append("\n    if (x.get() > 0) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(3);");
    gold.append("\n      x.set(0);");
    gold.append("\n    } else {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(4);");
    gold.append("\n      x.bumpUpPost();");
    gold.append("\n    }");
    gold.append("\n    __track(5);");
    gold.append("\n    if (x.get() == 0) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(6);");
    gold.append("\n      y.set(1);");
    gold.append("\n    } else if (x.get() == 1) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(7);");
    gold.append("\n      y.set(2);");
    gold.append("\n    } else if (x.get() == 2) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(8);");
    gold.append("\n      y.set(3);");
    gold.append("\n    } else {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(9);");
    gold.append("\n      y.set(4);");
    gold.append("\n    }");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"x\":0,\"y\":1,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":10,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"x\":0,\"y\":1,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":10,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_NotMaybeInIf_9 = null;
  private String get_NotMaybeInIf_9() {
    if (cached_NotMaybeInIf_9 != null) {
      return cached_NotMaybeInIf_9;
    }
    cached_NotMaybeInIf_9 = generateTestOutput(false, "NotMaybeInIf_9", "./test_code/Control_NotMaybeInIf_failure.a");
    return cached_NotMaybeInIf_9;
  }

  @Test
  public void testNotMaybeInIfFailure() {
    assertLiveFail(get_NotMaybeInIf_9());
  }

  @Test
  public void testNotMaybeInIfExceptionFree() {
    assertExceptionFree(get_NotMaybeInIf_9());
  }

  @Test
  public void testNotMaybeInIfTODOFree() {
    assertTODOFree(get_NotMaybeInIf_9());
  }

  @Test
  public void stable_NotMaybeInIf_9() {
    String live = get_NotMaybeInIf_9();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Control_NotMaybeInIf_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 6");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 7");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type check failure: the type 'int' was expected to be a maybe<?>(RuleSetMaybe)\"");
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
    gold.append("\npublic class NotMaybeInIf_9 extends LivingDocument {");
    gold.append("\n  public NotMaybeInIf_9(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n    if (1) {}");
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
  private String cached_VariousFor_10 = null;
  private String get_VariousFor_10() {
    if (cached_VariousFor_10 != null) {
      return cached_VariousFor_10;
    }
    cached_VariousFor_10 = generateTestOutput(true, "VariousFor_10", "./test_code/Control_VariousFor_success.a");
    return cached_VariousFor_10;
  }

  @Test
  public void testVariousForEmission() {
    assertEmissionGood(get_VariousFor_10());
  }

  @Test
  public void testVariousForSuccess() {
    assertLivePass(get_VariousFor_10());
  }

  @Test
  public void testVariousForGoodWillHappy() {
    assertGoodWillHappy(get_VariousFor_10());
  }

  @Test
  public void testVariousForExceptionFree() {
    assertExceptionFree(get_VariousFor_10());
  }

  @Test
  public void testVariousForTODOFree() {
    assertTODOFree(get_VariousFor_10());
  }

  @Test
  public void stable_VariousFor_10() {
    String live = get_VariousFor_10();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Control_VariousFor_success.a");
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
    gold.append("\npublic class VariousFor_10 extends LivingDocument {");
    gold.append("\n  private final RxInt32 sum;");
    gold.append("\n  public VariousFor_10(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    sum = RxFactory.makeRxInt32(this, __root, \"sum\", 0);");
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
    gold.append("\n    sum.__commit(\"sum\", __child);");
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
    gold.append("\n    sum.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    if (!(true)) {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n    ObjectNode __view = Utility.createObjectNode();");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"sum\", NativeBridge.INTEGER_NATIVE_SUPPORT.toPrivateJsonNode(__who, sum.get()));");
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
    gold.append("\n    __code_cost += 6;");
    gold.append("\n    __track(0);");
    gold.append("\n    {");
    gold.append("\n      int x = 0;");
    gold.append("\n      for (;__goodwill(3, 18, 3, 24) && (x < 10);x++) {");
    gold.append("\n        __code_cost += 2;");
    gold.append("\n        __track(1);");
    gold.append("\n        sum.opAddTo(x);");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    __track(2);");
    gold.append("\n    int k = sum.get() % 7;");
    gold.append("\n    __track(3);");
    gold.append("\n    {");
    gold.append("\n      for (;__goodwill(7, 8, 7, 15) && (k < 100);k++) {");
    gold.append("\n        __code_cost += 2;");
    gold.append("\n        __track(4);");
    gold.append("\n        sum.bumpUpPost();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    __track(5);");
    gold.append("\n    {");
    gold.append("\n      for (;__goodwill(11, 2, 13, 3);) {");
    gold.append("\n        __code_cost += 2;");
    gold.append("\n        __track(6);");
    gold.append("\n        break;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    __track(7);");
    gold.append("\n    {");
    gold.append("\n      for (;__goodwill(15, 7, 15, 11) && (true);k++) {");
    gold.append("\n        __code_cost += 2;");
    gold.append("\n        __track(8);");
    gold.append("\n        break;");
    gold.append("\n      }");
    gold.append("\n    }");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"sum\":142,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":111,\"__cost\":224,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"sum\":142,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":111,\"__cost\":224,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
}

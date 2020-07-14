/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedTypesTests extends GeneratedBase {
  private String cached_BadEnumConstants_1 = null;
  private String get_BadEnumConstants_1() {
    if (cached_BadEnumConstants_1 != null) {
      return cached_BadEnumConstants_1;
    }
    cached_BadEnumConstants_1 = generateTestOutput(false, "BadEnumConstants_1", "./test_code/Types_BadEnumConstants_failure.a");
    return cached_BadEnumConstants_1;
  }

  @Test
  public void testBadEnumConstantsFailure() {
    assertLiveFail(get_BadEnumConstants_1());
  }

  @Test
  public void testBadEnumConstantsExceptionFree() {
    assertExceptionFree(get_BadEnumConstants_1());
  }

  @Test
  public void testBadEnumConstantsTODOFree() {
    assertTODOFree(get_BadEnumConstants_1());
  }

  @Test
  public void stable_BadEnumConstants_1() {
    String live = get_BadEnumConstants_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_BadEnumConstants_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 7,");
    gold.append("\n      \"character\" : 11");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 7,");
    gold.append("\n      \"character\" : 15");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type incorrect: expecting 'M' to be an enumeration; instead, found a type of 'M'.(TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 8,");
    gold.append("\n      \"character\" : 11");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 8,");
    gold.append("\n      \"character\" : 15");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type incorrect: expecting 'R' to be an enumeration; instead, found a type of 'R'.(TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 9,");
    gold.append("\n      \"character\" : 11");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 9,");
    gold.append("\n      \"character\" : 18");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type not found: an enumeration named 'Nope' was not found.(TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 10,");
    gold.append("\n      \"character\" : 11");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 10,");
    gold.append("\n      \"character\" : 15");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type incorrect: expecting 'M' to be an enumeration; instead, found a type of 'M'.(TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 11,");
    gold.append("\n      \"character\" : 11");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 11,");
    gold.append("\n      \"character\" : 15");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type incorrect: expecting 'R' to be an enumeration; instead, found a type of 'R'.(TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 12,");
    gold.append("\n      \"character\" : 11");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 12,");
    gold.append("\n      \"character\" : 18");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type not found: an enumeration named 'Nope' was not found.(TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 13,");
    gold.append("\n      \"character\" : 11");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 13,");
    gold.append("\n      \"character\" : 21");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type incorrect: expecting 'M' to be an enumeration; instead, found a type of 'M'.(TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 14,");
    gold.append("\n      \"character\" : 11");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 14,");
    gold.append("\n      \"character\" : 21");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type incorrect: expecting 'R' to be an enumeration; instead, found a type of 'R'.(TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 15,");
    gold.append("\n      \"character\" : 11");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 15,");
    gold.append("\n      \"character\" : 24");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type not found: an enumeration named 'Nope' was not found.(TypeCheckReferences)\"");
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
    gold.append("\npublic class BadEnumConstants_1 extends LivingDocument {");
    gold.append("\n  public BadEnumConstants_1(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n    private RTxM(ObjectNode payload) {}");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTxM() {}");
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
    gold.append("\n  private class RTxR extends RxRecordBase<RTxR> {");
    gold.append("\n    private final RxInt32 id;");
    gold.append("\n    private RTxR(ObjectNode __node, RxParent __owner) {");
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
    gold.append("\n    __code_cost += 10;");
    gold.append("\n    __track(0);");
    gold.append("\n");
    gold.append("\n    __track(1);");
    gold.append("\n");
    gold.append("\n    __track(2);");
    gold.append("\n");
    gold.append("\n    __track(3);");
    gold.append("\n");
    gold.append("\n    __track(4);");
    gold.append("\n");
    gold.append("\n    __track(5);");
    gold.append("\n");
    gold.append("\n    __track(6);");
    gold.append("\n");
    gold.append("\n    __track(7);");
    gold.append("\n");
    gold.append("\n    __track(8);");
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
  private String cached_BooleanBulk_2 = null;
  private String get_BooleanBulk_2() {
    if (cached_BooleanBulk_2 != null) {
      return cached_BooleanBulk_2;
    }
    cached_BooleanBulk_2 = generateTestOutput(true, "BooleanBulk_2", "./test_code/Types_BooleanBulk_success.a");
    return cached_BooleanBulk_2;
  }

  @Test
  public void testBooleanBulkEmission() {
    assertEmissionGood(get_BooleanBulk_2());
  }

  @Test
  public void testBooleanBulkSuccess() {
    assertLivePass(get_BooleanBulk_2());
  }

  @Test
  public void testBooleanBulkGoodWillHappy() {
    assertGoodWillHappy(get_BooleanBulk_2());
  }

  @Test
  public void testBooleanBulkExceptionFree() {
    assertExceptionFree(get_BooleanBulk_2());
  }

  @Test
  public void testBooleanBulkTODOFree() {
    assertTODOFree(get_BooleanBulk_2());
  }

  @Test
  public void stable_BooleanBulk_2() {
    String live = get_BooleanBulk_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_BooleanBulk_success.a");
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
    gold.append("\npublic class BooleanBulk_2 extends LivingDocument {");
    gold.append("\n  private final RxBoolean b;");
    gold.append("\n  private final RxLazy<Boolean> ib;");
    gold.append("\n  public BooleanBulk_2(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    b = RxFactory.makeRxBoolean(this, __root, \"b\", true);");
    gold.append("\n    ib = new RxLazy<>(this, NativeBridge.BOOLEAN_NATIVE_SUPPORT, () -> (!b.get()));");
    gold.append("\n    b.__subscribe(ib);");
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
    gold.append("\n    b.__commit(\"b\", __child);");
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
    gold.append("\n    b.__revert();");
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
    gold.append("\n    {");
    gold.append("\n      __code_cost += 3;");
    gold.append("\n      __track(0);");
    gold.append("\n      __assert_truth(!b.get(), 8, 2, 8, 12);");
    gold.append("\n      __track(1);");
    gold.append("\n      __assert_truth(ib.get(), 9, 2, 9, 12);");
    gold.append("\n    }");
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
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(2);");
    gold.append("\n    b.set(false);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"b\":false,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":2,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"b\":false,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":2,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[PrimaryTest] = 100.0%");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_BooleanLegacy_3 = null;
  private String get_BooleanLegacy_3() {
    if (cached_BooleanLegacy_3 != null) {
      return cached_BooleanLegacy_3;
    }
    cached_BooleanLegacy_3 = generateTestOutput(true, "BooleanLegacy_3", "./test_code/Types_BooleanLegacy_success.a");
    return cached_BooleanLegacy_3;
  }

  @Test
  public void testBooleanLegacyEmission() {
    assertEmissionGood(get_BooleanLegacy_3());
  }

  @Test
  public void testBooleanLegacySuccess() {
    assertLivePass(get_BooleanLegacy_3());
  }

  @Test
  public void testBooleanLegacyGoodWillHappy() {
    assertGoodWillHappy(get_BooleanLegacy_3());
  }

  @Test
  public void testBooleanLegacyExceptionFree() {
    assertExceptionFree(get_BooleanLegacy_3());
  }

  @Test
  public void testBooleanLegacyTODOFree() {
    assertTODOFree(get_BooleanLegacy_3());
  }

  @Test
  public void stable_BooleanLegacy_3() {
    String live = get_BooleanLegacy_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_BooleanLegacy_success.a");
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
    gold.append("\npublic class BooleanLegacy_3 extends LivingDocument {");
    gold.append("\n  private final RxBoolean b1;");
    gold.append("\n  private final RxBoolean b2;");
    gold.append("\n  private final RxBoolean bAnd1;");
    gold.append("\n  private final RxBoolean bOr1;");
    gold.append("\n  private final RxBoolean bAnd2;");
    gold.append("\n  private final RxBoolean bOr2;");
    gold.append("\n  private final RxBoolean bAnd3;");
    gold.append("\n  private final RxBoolean bOr3;");
    gold.append("\n  private final RxBoolean bAnd4;");
    gold.append("\n  private final RxBoolean bOr4;");
    gold.append("\n  public BooleanLegacy_3(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    b1 = RxFactory.makeRxBoolean(this, __root, \"b1\", false);");
    gold.append("\n    b2 = RxFactory.makeRxBoolean(this, __root, \"b2\", false);");
    gold.append("\n    bAnd1 = RxFactory.makeRxBoolean(this, __root, \"bAnd1\", false);");
    gold.append("\n    bOr1 = RxFactory.makeRxBoolean(this, __root, \"bOr1\", false);");
    gold.append("\n    bAnd2 = RxFactory.makeRxBoolean(this, __root, \"bAnd2\", false);");
    gold.append("\n    bOr2 = RxFactory.makeRxBoolean(this, __root, \"bOr2\", false);");
    gold.append("\n    bAnd3 = RxFactory.makeRxBoolean(this, __root, \"bAnd3\", false);");
    gold.append("\n    bOr3 = RxFactory.makeRxBoolean(this, __root, \"bOr3\", false);");
    gold.append("\n    bAnd4 = RxFactory.makeRxBoolean(this, __root, \"bAnd4\", false);");
    gold.append("\n    bOr4 = RxFactory.makeRxBoolean(this, __root, \"bOr4\", false);");
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
    gold.append("\n    b1.__commit(\"b1\", __child);");
    gold.append("\n    b2.__commit(\"b2\", __child);");
    gold.append("\n    bAnd1.__commit(\"bAnd1\", __child);");
    gold.append("\n    bOr1.__commit(\"bOr1\", __child);");
    gold.append("\n    bAnd2.__commit(\"bAnd2\", __child);");
    gold.append("\n    bOr2.__commit(\"bOr2\", __child);");
    gold.append("\n    bAnd3.__commit(\"bAnd3\", __child);");
    gold.append("\n    bOr3.__commit(\"bOr3\", __child);");
    gold.append("\n    bAnd4.__commit(\"bAnd4\", __child);");
    gold.append("\n    bOr4.__commit(\"bOr4\", __child);");
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
    gold.append("\n    b1.__revert();");
    gold.append("\n    b2.__revert();");
    gold.append("\n    bAnd1.__revert();");
    gold.append("\n    bOr1.__revert();");
    gold.append("\n    bAnd2.__revert();");
    gold.append("\n    bOr2.__revert();");
    gold.append("\n    bAnd3.__revert();");
    gold.append("\n    bOr3.__revert();");
    gold.append("\n    bAnd4.__revert();");
    gold.append("\n    bOr4.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    if (!(true)) {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n    ObjectNode __view = Utility.createObjectNode();");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"b1\", NativeBridge.BOOLEAN_NATIVE_SUPPORT.toPrivateJsonNode(__who, b1.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"b2\", NativeBridge.BOOLEAN_NATIVE_SUPPORT.toPrivateJsonNode(__who, b2.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"bAnd1\", NativeBridge.BOOLEAN_NATIVE_SUPPORT.toPrivateJsonNode(__who, bAnd1.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"bAnd2\", NativeBridge.BOOLEAN_NATIVE_SUPPORT.toPrivateJsonNode(__who, bAnd2.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"bAnd3\", NativeBridge.BOOLEAN_NATIVE_SUPPORT.toPrivateJsonNode(__who, bAnd3.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"bAnd4\", NativeBridge.BOOLEAN_NATIVE_SUPPORT.toPrivateJsonNode(__who, bAnd4.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"bOr1\", NativeBridge.BOOLEAN_NATIVE_SUPPORT.toPrivateJsonNode(__who, bOr1.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"bOr2\", NativeBridge.BOOLEAN_NATIVE_SUPPORT.toPrivateJsonNode(__who, bOr2.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"bOr3\", NativeBridge.BOOLEAN_NATIVE_SUPPORT.toPrivateJsonNode(__who, bOr3.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"bOr4\", NativeBridge.BOOLEAN_NATIVE_SUPPORT.toPrivateJsonNode(__who, bOr4.get()));");
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
    gold.append("\n    __code_cost += 18;");
    gold.append("\n    __track(0);");
    gold.append("\n    b1.set(false);");
    gold.append("\n    __track(1);");
    gold.append("\n    b2.set(false);");
    gold.append("\n    __track(2);");
    gold.append("\n    bAnd1.set(b1.get() && b2.get());");
    gold.append("\n    __track(3);");
    gold.append("\n    bOr1.set(b1.get() || b2.get());");
    gold.append("\n    __track(4);");
    gold.append("\n    b1.set(false);");
    gold.append("\n    __track(5);");
    gold.append("\n    b2.set(true);");
    gold.append("\n    __track(6);");
    gold.append("\n    bAnd2.set(b1.get() && b2.get());");
    gold.append("\n    __track(7);");
    gold.append("\n    bOr2.set(b1.get() || b2.get());");
    gold.append("\n    __track(8);");
    gold.append("\n    b1.set(true);");
    gold.append("\n    __track(9);");
    gold.append("\n    b2.set(false);");
    gold.append("\n    __track(10);");
    gold.append("\n    bAnd3.set(b1.get() && b2.get());");
    gold.append("\n    __track(11);");
    gold.append("\n    bOr3.set(b1.get() || b2.get());");
    gold.append("\n    __track(12);");
    gold.append("\n    b1.set(true);");
    gold.append("\n    __track(13);");
    gold.append("\n    b2.set(true);");
    gold.append("\n    __track(14);");
    gold.append("\n    bAnd4.set(b1.get() && b2.get());");
    gold.append("\n    __track(15);");
    gold.append("\n    bOr4.set(b1.get() || b2.get());");
    gold.append("\n    __track(16);");
    gold.append("\n    __transitionStateMachine(\"next\", 0);");
    gold.append("\n  }");
    gold.append("\n  private void __step_next() {");
    gold.append("\n    __code_cost += 37;");
    gold.append("\n    __track(17);");
    gold.append("\n    boolean b1 = false;");
    gold.append("\n    __track(18);");
    gold.append("\n    boolean b2 = false;");
    gold.append("\n    __track(19);");
    gold.append("\n    boolean bAnd1 = false;");
    gold.append("\n    __track(20);");
    gold.append("\n    boolean bOr1 = false;");
    gold.append("\n    __track(21);");
    gold.append("\n    boolean bAnd2 = false;");
    gold.append("\n    __track(22);");
    gold.append("\n    boolean bOr2 = false;");
    gold.append("\n    __track(23);");
    gold.append("\n    boolean bAnd3 = false;");
    gold.append("\n    __track(24);");
    gold.append("\n    boolean bOr3 = false;");
    gold.append("\n    __track(25);");
    gold.append("\n    boolean bAnd4 = false;");
    gold.append("\n    __track(26);");
    gold.append("\n    boolean bOr4 = false;");
    gold.append("\n    __track(27);");
    gold.append("\n    boolean checkEq = false;");
    gold.append("\n    __track(28);");
    gold.append("\n    boolean checkNotEq = false;");
    gold.append("\n    __track(29);");
    gold.append("\n    b1 = false;");
    gold.append("\n    __track(30);");
    gold.append("\n    b2 = false;");
    gold.append("\n    __track(31);");
    gold.append("\n    bAnd1 = b1 && b2;");
    gold.append("\n    __track(32);");
    gold.append("\n    bOr1 = b1 || b2;");
    gold.append("\n    __track(33);");
    gold.append("\n    checkEq = b1 == b2;");
    gold.append("\n    __track(34);");
    gold.append("\n    checkNotEq = b1 != b2;");
    gold.append("\n    __track(35);");
    gold.append("\n    b1 = false;");
    gold.append("\n    __track(36);");
    gold.append("\n    b2 = true;");
    gold.append("\n    __track(37);");
    gold.append("\n    bAnd2 = b1 && b2;");
    gold.append("\n    __track(38);");
    gold.append("\n    bOr2 = b1 || b2;");
    gold.append("\n    __track(39);");
    gold.append("\n    checkEq = b1 == b2;");
    gold.append("\n    __track(40);");
    gold.append("\n    checkNotEq = b1 != b2;");
    gold.append("\n    __track(41);");
    gold.append("\n    b1 = true;");
    gold.append("\n    __track(42);");
    gold.append("\n    b2 = false;");
    gold.append("\n    __track(43);");
    gold.append("\n    bAnd3 = b1 && b2;");
    gold.append("\n    __track(44);");
    gold.append("\n    bOr3 = b1 || b2;");
    gold.append("\n    __track(45);");
    gold.append("\n    checkEq = b1 == b2;");
    gold.append("\n    __track(46);");
    gold.append("\n    checkNotEq = b1 != b2;");
    gold.append("\n    __track(47);");
    gold.append("\n    b1 = true;");
    gold.append("\n    __track(48);");
    gold.append("\n    b2 = true;");
    gold.append("\n    __track(49);");
    gold.append("\n    bAnd4 = b1 && b2;");
    gold.append("\n    __track(50);");
    gold.append("\n    bOr4 = b1 || b2;");
    gold.append("\n    __track(51);");
    gold.append("\n    checkEq = b1 == b2;");
    gold.append("\n    __track(52);");
    gold.append("\n    checkNotEq = b1 != b2;");
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
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  public boolean __onConnected__0(NtClient who){");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(53);");
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
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(54);");
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
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__state\":\"next\",!TimeHiddenForStability!\"25\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\",\"b1\":true,\"b2\":true,\"bOr2\":true,\"bOr3\":true,\"bAnd4\":true,\"bOr4\":true} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__state\":\"\",\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"50\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"75\"}-->{\"__goodwill_used\":0,\"__cost\":57,\"__billing_seq\":2} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"4804307197456638271\",\"__seedUsed\":\"-4962768465676381896\",!TimeHiddenForStability!\"25\",\"__seq\":2,\"__time\":\"50\",\"b1\":true,\"b2\":true,\"bOr2\":true,\"bOr3\":true,\"bAnd4\":true,\"bOr4\":true,\"__goodwill_used\":0,\"__cost\":57,\"__billing_seq\":2}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"100\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":3,\"__connection_id\":1,\"__time\":\"100\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"125\"}-->{\"__messages\":null,\"__seedUsed\":\"4804307197456638271\",\"__seq\":4,\"__entropy\":\"-1034601897293430941\",\"__time\":\"125\"} need:false in:-100");
    gold.append("\nAS NO_ONE:{\"data\":{\"b1\":true,\"b2\":true,\"bAnd1\":false,\"bAnd2\":false,\"bAnd3\":false,\"bAnd4\":true,\"bOr1\":false,\"bOr2\":true,\"bOr3\":true,\"bOr4\":true},\"outstanding\":[],\"blockers\":[]}");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_ChannelFunky_4 = null;
  private String get_ChannelFunky_4() {
    if (cached_ChannelFunky_4 != null) {
      return cached_ChannelFunky_4;
    }
    cached_ChannelFunky_4 = generateTestOutput(true, "ChannelFunky_4", "./test_code/Types_ChannelFunky_success.a");
    return cached_ChannelFunky_4;
  }

  @Test
  public void testChannelFunkyEmission() {
    assertEmissionGood(get_ChannelFunky_4());
  }

  @Test
  public void testChannelFunkySuccess() {
    assertLivePass(get_ChannelFunky_4());
  }

  @Test
  public void testChannelFunkyGoodWillHappy() {
    assertGoodWillHappy(get_ChannelFunky_4());
  }

  @Test
  public void testChannelFunkyExceptionFree() {
    assertExceptionFree(get_ChannelFunky_4());
  }

  @Test
  public void testChannelFunkyTODOFree() {
    assertTODOFree(get_ChannelFunky_4());
  }

  @Test
  public void stable_ChannelFunky_4() {
    String live = get_ChannelFunky_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_ChannelFunky_success.a");
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
    gold.append("\npublic class ChannelFunky_4 extends LivingDocument {");
    gold.append("\n  public ChannelFunky_4(ObjectNode __root, DocumentMonitor __monitor) {");
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
    gold.append("\n  private void __FUNC_0_wacky(NtChannel<RTxX> ch) {");
    gold.append("\n    __code_cost += 4;");
    gold.append("\n    __track(0);");
    gold.append("\n    ch.fetch(NtClient.NO_ONE);");
    gold.append("\n    __track(1);");
    gold.append("\n    ch.decide(NtClient.NO_ONE, new RTxX[] {new RTxX(12), new RTxX(152)});");
    gold.append("\n    __track(2);");
    gold.append("\n    duo.choose(NtClient.NO_ONE, new RTxX[] {new RTxX(12), new RTxX(152)}, 2);");
    gold.append("\n  }");
    gold.append("\n  private void __FUNC_1_w00t(NtChannel<RTxX[]> carr) {}");
    gold.append("\n  private final Sink<RTxX> __queue_foo = new Sink<>(\"foo\");");
    gold.append("\n  private final NtChannel<RTxX> foo = new NtChannel<>(__futures, __queue_foo);");
    gold.append("\n  private final Sink<RTxX> __queue_goo = new Sink<>(\"goo\");");
    gold.append("\n  private final NtChannel<RTxX> goo = new NtChannel<>(__futures, __queue_goo);");
    gold.append("\n  private final Sink<RTxX[]> __queue_duo = new Sink<>(\"duo\");");
    gold.append("\n  private final NtChannel<RTxX[]> duo = new NtChannel<>(__futures, __queue_duo);");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    switch (task.channel) {");
    gold.append("\n      case \"foo\":");
    gold.append("\n        __queue_foo.enqueue(task, new RTxX(task.message));");
    gold.append("\n        return;");
    gold.append("\n      case \"goo\":");
    gold.append("\n        __queue_goo.enqueue(task, new RTxX(task.message));");
    gold.append("\n        return;");
    gold.append("\n      case \"duo\":");
    gold.append("\n        __queue_duo.enqueue(task, __BRIDGE_X.convertArrayMessage(task.message));");
    gold.append("\n        return;");
    gold.append("\n      default:");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n    __queue_foo.clear();");
    gold.append("\n    __queue_goo.clear();");
    gold.append("\n    __queue_duo.clear();");
    gold.append("\n  }");
    gold.append("\n  private void __step_neat() {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(3);");
    gold.append("\n    __FUNC_0_wacky((true ? foo : goo));");
    gold.append("\n    __track(4);");
    gold.append("\n    __FUNC_1_w00t((false ? duo : duo));");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"neat\":");
    gold.append("\n        __step_neat();");
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
  private String cached_ConstantsLegacy_5 = null;
  private String get_ConstantsLegacy_5() {
    if (cached_ConstantsLegacy_5 != null) {
      return cached_ConstantsLegacy_5;
    }
    cached_ConstantsLegacy_5 = generateTestOutput(true, "ConstantsLegacy_5", "./test_code/Types_ConstantsLegacy_success.a");
    return cached_ConstantsLegacy_5;
  }

  @Test
  public void testConstantsLegacyEmission() {
    assertEmissionGood(get_ConstantsLegacy_5());
  }

  @Test
  public void testConstantsLegacySuccess() {
    assertLivePass(get_ConstantsLegacy_5());
  }

  @Test
  public void testConstantsLegacyGoodWillHappy() {
    assertGoodWillHappy(get_ConstantsLegacy_5());
  }

  @Test
  public void testConstantsLegacyExceptionFree() {
    assertExceptionFree(get_ConstantsLegacy_5());
  }

  @Test
  public void testConstantsLegacyTODOFree() {
    assertTODOFree(get_ConstantsLegacy_5());
  }

  @Test
  public void stable_ConstantsLegacy_5() {
    String live = get_ConstantsLegacy_5();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_ConstantsLegacy_success.a");
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
    gold.append("\npublic class ConstantsLegacy_5 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxDouble y;");
    gold.append("\n  private final RxString z;");
    gold.append("\n  private final RxBoolean u;");
    gold.append("\n  public ConstantsLegacy_5(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxInt32(this, __root, \"x\", 0);");
    gold.append("\n    y = RxFactory.makeRxDouble(this, __root, \"y\", 0.0);");
    gold.append("\n    z = RxFactory.makeRxString(this, __root, \"z\", \"\");");
    gold.append("\n    u = RxFactory.makeRxBoolean(this, __root, \"u\", false);");
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
    gold.append("\n    z.__commit(\"z\", __child);");
    gold.append("\n    u.__commit(\"u\", __child);");
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
    gold.append("\n    z.__revert();");
    gold.append("\n    u.__revert();");
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
  private String cached_DoubleBulk_6 = null;
  private String get_DoubleBulk_6() {
    if (cached_DoubleBulk_6 != null) {
      return cached_DoubleBulk_6;
    }
    cached_DoubleBulk_6 = generateTestOutput(true, "DoubleBulk_6", "./test_code/Types_DoubleBulk_success.a");
    return cached_DoubleBulk_6;
  }

  @Test
  public void testDoubleBulkEmission() {
    assertEmissionGood(get_DoubleBulk_6());
  }

  @Test
  public void testDoubleBulkSuccess() {
    assertLivePass(get_DoubleBulk_6());
  }

  @Test
  public void testDoubleBulkGoodWillHappy() {
    assertGoodWillHappy(get_DoubleBulk_6());
  }

  @Test
  public void testDoubleBulkExceptionFree() {
    assertExceptionFree(get_DoubleBulk_6());
  }

  @Test
  public void testDoubleBulkTODOFree() {
    assertTODOFree(get_DoubleBulk_6());
  }

  @Test
  public void stable_DoubleBulk_6() {
    String live = get_DoubleBulk_6();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_DoubleBulk_success.a");
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
    gold.append("\npublic class DoubleBulk_6 extends LivingDocument {");
    gold.append("\n  private final RxDouble x;");
    gold.append("\n  private final RxDouble y;");
    gold.append("\n  private final RxDouble z;");
    gold.append("\n  private final RxLazy<Double> sum;");
    gold.append("\n  public DoubleBulk_6(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxDouble(this, __root, \"x\", 0.0);");
    gold.append("\n    y = RxFactory.makeRxDouble(this, __root, \"y\", 3.14);");
    gold.append("\n    z = RxFactory.makeRxDouble(this, __root, \"z\", 2000.0);");
    gold.append("\n    sum = new RxLazy<>(this, NativeBridge.DOUBLE_NATIVE_SUPPORT, () -> (x.get() + y.get() + z.get()));");
    gold.append("\n    x.__subscribe(sum);");
    gold.append("\n    y.__subscribe(sum);");
    gold.append("\n    z.__subscribe(sum);");
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
    gold.append("\n    x.__revert();");
    gold.append("\n    y.__revert();");
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
    gold.append("\n    __view.set(\"sum\", NativeBridge.DOUBLE_NATIVE_SUPPORT.toPrivateJsonNode(__who, sum.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"x\", NativeBridge.DOUBLE_NATIVE_SUPPORT.toPrivateJsonNode(__who, x.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"y\", NativeBridge.DOUBLE_NATIVE_SUPPORT.toPrivateJsonNode(__who, y.get()));");
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
    gold.append("\n  public void __test_PrimaryTest(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"PrimaryTest\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(1);");
    gold.append("\n      __assert_truth(LibMath.near(sum.get(), 2005.85), 10, 2, 10, 24);");
    gold.append("\n    }");
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
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(2);");
    gold.append("\n    x.set(2.71);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"x\":2.71,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":2,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"x\":2.71,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":2,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"75\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":2,\"__connection_id\":1,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__seq\":3,\"__entropy\":\"4804307197456638271\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\nAS NO_ONE:{\"data\":{\"sum\":2005.85,\"x\":2.71,\"y\":3.14,\"z\":2000.0},\"outstanding\":[],\"blockers\":[]}");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[PrimaryTest] = 100.0%");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_IntegerBulk_7 = null;
  private String get_IntegerBulk_7() {
    if (cached_IntegerBulk_7 != null) {
      return cached_IntegerBulk_7;
    }
    cached_IntegerBulk_7 = generateTestOutput(true, "IntegerBulk_7", "./test_code/Types_IntegerBulk_success.a");
    return cached_IntegerBulk_7;
  }

  @Test
  public void testIntegerBulkEmission() {
    assertEmissionGood(get_IntegerBulk_7());
  }

  @Test
  public void testIntegerBulkSuccess() {
    assertLivePass(get_IntegerBulk_7());
  }

  @Test
  public void testIntegerBulkGoodWillHappy() {
    assertGoodWillHappy(get_IntegerBulk_7());
  }

  @Test
  public void testIntegerBulkExceptionFree() {
    assertExceptionFree(get_IntegerBulk_7());
  }

  @Test
  public void testIntegerBulkTODOFree() {
    assertTODOFree(get_IntegerBulk_7());
  }

  @Test
  public void stable_IntegerBulk_7() {
    String live = get_IntegerBulk_7();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_IntegerBulk_success.a");
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
    gold.append("\npublic class IntegerBulk_7 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxInt32 y;");
    gold.append("\n  private final RxInt32 z;");
    gold.append("\n  private final RxLazy<Integer> sum;");
    gold.append("\n  public IntegerBulk_7(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxInt32(this, __root, \"x\", 1);");
    gold.append("\n    y = RxFactory.makeRxInt32(this, __root, \"y\", 2);");
    gold.append("\n    z = RxFactory.makeRxInt32(this, __root, \"z\", 0);");
    gold.append("\n    sum = new RxLazy<>(this, NativeBridge.INTEGER_NATIVE_SUPPORT, () -> (x.get() + y.get() + z.get()));");
    gold.append("\n    x.__subscribe(sum);");
    gold.append("\n    y.__subscribe(sum);");
    gold.append("\n    z.__subscribe(sum);");
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
    gold.append("\n    x.__revert();");
    gold.append("\n    y.__revert();");
    gold.append("\n    z.__revert();");
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
    gold.append("\n    {");
    gold.append("\n      __code_cost += 4;");
    gold.append("\n      __track(0);");
    gold.append("\n      __assert_truth(sum.get() == 6, 9, 2, 9, 18);");
    gold.append("\n      __track(1);");
    gold.append("\n      z.set(10);");
    gold.append("\n      __track(2);");
    gold.append("\n      __assert_truth(sum.get() == 13, 11, 2, 11, 19);");
    gold.append("\n    }");
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
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(3);");
    gold.append("\n    z.set(3);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"z\":3,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":2,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"z\":3,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":2,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[PrimaryTest] = 100.0%");
    gold.append("\n...DUMP:{\"z\":10}");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_LabelLegacy_8 = null;
  private String get_LabelLegacy_8() {
    if (cached_LabelLegacy_8 != null) {
      return cached_LabelLegacy_8;
    }
    cached_LabelLegacy_8 = generateTestOutput(true, "LabelLegacy_8", "./test_code/Types_LabelLegacy_success.a");
    return cached_LabelLegacy_8;
  }

  @Test
  public void testLabelLegacyEmission() {
    assertEmissionGood(get_LabelLegacy_8());
  }

  @Test
  public void testLabelLegacySuccess() {
    assertLivePass(get_LabelLegacy_8());
  }

  @Test
  public void testLabelLegacyGoodWillHappy() {
    assertGoodWillHappy(get_LabelLegacy_8());
  }

  @Test
  public void testLabelLegacyExceptionFree() {
    assertExceptionFree(get_LabelLegacy_8());
  }

  @Test
  public void testLabelLegacyTODOFree() {
    assertTODOFree(get_LabelLegacy_8());
  }

  @Test
  public void stable_LabelLegacy_8() {
    String live = get_LabelLegacy_8();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_LabelLegacy_success.a");
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
    gold.append("\npublic class LabelLegacy_8 extends LivingDocument {");
    gold.append("\n  private final RxString ptr;");
    gold.append("\n  private final RxString output;");
    gold.append("\n  private final RxString output2;");
    gold.append("\n  public LabelLegacy_8(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    ptr = RxFactory.makeRxString(this, __root, \"ptr\", \"\");");
    gold.append("\n    output = RxFactory.makeRxString(this, __root, \"output\", \"\");");
    gold.append("\n    output2 = RxFactory.makeRxString(this, __root, \"output2\", \"\");");
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
    gold.append("\n    ptr.__commit(\"ptr\", __child);");
    gold.append("\n    output.__commit(\"output\", __child);");
    gold.append("\n    output2.__commit(\"output2\", __child);");
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
    gold.append("\n    ptr.__revert();");
    gold.append("\n    output.__revert();");
    gold.append("\n    output2.__revert();");
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
    gold.append("\n  private void __step_begin() {");
    gold.append("\n    __code_cost += 4;");
    gold.append("\n    __track(0);");
    gold.append("\n    ptr.set(\"begin\");");
    gold.append("\n    __track(1);");
    gold.append("\n    if (LibString.equality(ptr.get(), \"begin\")) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(2);");
    gold.append("\n      output.set(\"YES\");");
    gold.append("\n    }");
    gold.append("\n    __track(3);");
    gold.append("\n    if (!LibString.equality(ptr.get(), \"end\")) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(4);");
    gold.append("\n      output2.set(\"WHOOP\");");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private void __step_end() {}");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"begin\":");
    gold.append("\n        __step_begin();");
    gold.append("\n        return;");
    gold.append("\n      case \"end\":");
    gold.append("\n        __step_end();");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  public boolean __onConnected__0(NtClient who){");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(5);");
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
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(6);");
    gold.append("\n    __transitionStateMachine(\"begin\", 0);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__state\":\"begin\",\"__constructed\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__state\":\"\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\",\"ptr\":\"begin\",\"output\":\"YES\",\"output2\":\"WHOOP\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":10,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"ptr\":\"begin\",\"output\":\"YES\",\"output2\":\"WHOOP\",\"__goodwill_used\":0,\"__cost\":10,\"__billing_seq\":1}");
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
  private String cached_LabelNotFound_9 = null;
  private String get_LabelNotFound_9() {
    if (cached_LabelNotFound_9 != null) {
      return cached_LabelNotFound_9;
    }
    cached_LabelNotFound_9 = generateTestOutput(false, "LabelNotFound_9", "./test_code/Types_LabelNotFound_failure.a");
    return cached_LabelNotFound_9;
  }

  @Test
  public void testLabelNotFoundFailure() {
    assertLiveFail(get_LabelNotFound_9());
  }

  @Test
  public void testLabelNotFoundExceptionFree() {
    assertExceptionFree(get_LabelNotFound_9());
  }

  @Test
  public void testLabelNotFoundTODOFree() {
    assertTODOFree(get_LabelNotFound_9());
  }

  @Test
  public void stable_LabelNotFound_9() {
    String live = get_LabelNotFound_9();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_LabelNotFound_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 2,");
    gold.append("\n      \"character\" : 10");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 2,");
    gold.append("\n      \"character\" : 15");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"State machine transition not found: a state machine label 'nope' was not found.(StateMachineLabels)\"");
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
    gold.append("\npublic class LabelNotFound_9 extends LivingDocument {");
    gold.append("\n  public LabelNotFound_9(ObjectNode __root, DocumentMonitor __monitor) {");
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
  private String cached_LegacyClient_10 = null;
  private String get_LegacyClient_10() {
    if (cached_LegacyClient_10 != null) {
      return cached_LegacyClient_10;
    }
    cached_LegacyClient_10 = generateTestOutput(true, "LegacyClient_10", "./test_code/Types_LegacyClient_success.a");
    return cached_LegacyClient_10;
  }

  @Test
  public void testLegacyClientEmission() {
    assertEmissionGood(get_LegacyClient_10());
  }

  @Test
  public void testLegacyClientSuccess() {
    assertLivePass(get_LegacyClient_10());
  }

  @Test
  public void testLegacyClientGoodWillHappy() {
    assertGoodWillHappy(get_LegacyClient_10());
  }

  @Test
  public void testLegacyClientExceptionFree() {
    assertExceptionFree(get_LegacyClient_10());
  }

  @Test
  public void testLegacyClientTODOFree() {
    assertTODOFree(get_LegacyClient_10());
  }

  @Test
  public void stable_LegacyClient_10() {
    String live = get_LegacyClient_10();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_LegacyClient_success.a");
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
    gold.append("\npublic class LegacyClient_10 extends LivingDocument {");
    gold.append("\n  private final RxClient x;");
    gold.append("\n  private final RxClient y;");
    gold.append("\n  public LegacyClient_10(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxClient(this, __root, \"x\", NtClient.NO_ONE);");
    gold.append("\n    y = RxFactory.makeRxClient(this, __root, \"y\", NtClient.NO_ONE);");
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
    gold.append("\n  private void __step_setup() {");
    gold.append("\n    __code_cost += 6;");
    gold.append("\n    __track(0);");
    gold.append("\n    x.set(y.get());");
    gold.append("\n    __track(1);");
    gold.append("\n    NtClient z = x.get();");
    gold.append("\n    __track(2);");
    gold.append("\n    NtClient cake = NtClient.NO_ONE;");
    gold.append("\n    __track(3);");
    gold.append("\n    cake = z;");
    gold.append("\n    __track(4);");
    gold.append("\n    boolean eq = (NtClient.NO_ONE.equals(NtClient.NO_ONE));");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"setup\":");
    gold.append("\n        __step_setup();");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  public boolean __onConnected__0(NtClient who){");
    gold.append("\n    __code_cost += 4;");
    gold.append("\n    __track(5);");
    gold.append("\n    boolean eq = (NtClient.NO_ONE.equals(who));");
    gold.append("\n    __track(6);");
    gold.append("\n    boolean neq = !(NtClient.NO_ONE.equals(who));");
    gold.append("\n    __track(7);");
    gold.append("\n    return true;");
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
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(8);");
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
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__state\":\"\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":8,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":8,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"75\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":2,\"__connection_id\":1,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__seq\":3,\"__entropy\":\"4804307197456638271\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\nAS NO_ONE:{\"data\":{},\"outstanding\":[],\"blockers\":[]}");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"125\",\"who\":{\"agent\":\"rando\",\"authority\":\"random-place\"}}-->{\"__seq\":4,\"__connection_id\":2,\"__time\":\"125\",\"__clients\":{\"1\":{\"agent\":\"rando\",\"authority\":\"random-place\"}}} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"150\"}-->{\"__messages\":null,\"__seedUsed\":\"4804307197456638271\",\"__seq\":5,\"__entropy\":\"-1034601897293430941\",\"__time\":\"150\"} need:false in:-150");
    gold.append("\nAS RANDO:{\"data\":{},\"outstanding\":[],\"blockers\":[]}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_LegacyDouble_11 = null;
  private String get_LegacyDouble_11() {
    if (cached_LegacyDouble_11 != null) {
      return cached_LegacyDouble_11;
    }
    cached_LegacyDouble_11 = generateTestOutput(true, "LegacyDouble_11", "./test_code/Types_LegacyDouble_success.a");
    return cached_LegacyDouble_11;
  }

  @Test
  public void testLegacyDoubleEmission() {
    assertEmissionGood(get_LegacyDouble_11());
  }

  @Test
  public void testLegacyDoubleSuccess() {
    assertLivePass(get_LegacyDouble_11());
  }

  @Test
  public void testLegacyDoubleGoodWillHappy() {
    assertGoodWillHappy(get_LegacyDouble_11());
  }

  @Test
  public void testLegacyDoubleExceptionFree() {
    assertExceptionFree(get_LegacyDouble_11());
  }

  @Test
  public void testLegacyDoubleTODOFree() {
    assertTODOFree(get_LegacyDouble_11());
  }

  @Test
  public void stable_LegacyDouble_11() {
    String live = get_LegacyDouble_11();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_LegacyDouble_success.a");
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
    gold.append("\npublic class LegacyDouble_11 extends LivingDocument {");
    gold.append("\n  private final RxDouble d1;");
    gold.append("\n  private final RxDouble d3;");
    gold.append("\n  private final RxBoolean is_near;");
    gold.append("\n  private final RxDouble dX;");
    gold.append("\n  private final RxDouble dY;");
    gold.append("\n  private final RxDouble assign;");
    gold.append("\n  public LegacyDouble_11(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    d1 = RxFactory.makeRxDouble(this, __root, \"d1\", 0.0);");
    gold.append("\n    d3 = RxFactory.makeRxDouble(this, __root, \"d3\", 0.0);");
    gold.append("\n    is_near = RxFactory.makeRxBoolean(this, __root, \"is_near\", false);");
    gold.append("\n    dX = RxFactory.makeRxDouble(this, __root, \"dX\", 0.0);");
    gold.append("\n    dY = RxFactory.makeRxDouble(this, __root, \"dY\", 0.0);");
    gold.append("\n    assign = RxFactory.makeRxDouble(this, __root, \"assign\", 0.0);");
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
    gold.append("\n    d1.__commit(\"d1\", __child);");
    gold.append("\n    d3.__commit(\"d3\", __child);");
    gold.append("\n    is_near.__commit(\"is_near\", __child);");
    gold.append("\n    dX.__commit(\"dX\", __child);");
    gold.append("\n    dY.__commit(\"dY\", __child);");
    gold.append("\n    assign.__commit(\"assign\", __child);");
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
    gold.append("\n    d1.__revert();");
    gold.append("\n    d3.__revert();");
    gold.append("\n    is_near.__revert();");
    gold.append("\n    dX.__revert();");
    gold.append("\n    dY.__revert();");
    gold.append("\n    assign.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    if (!(true)) {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n    ObjectNode __view = Utility.createObjectNode();");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"d1\", NativeBridge.DOUBLE_NATIVE_SUPPORT.toPrivateJsonNode(__who, d1.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"d3\", NativeBridge.DOUBLE_NATIVE_SUPPORT.toPrivateJsonNode(__who, d3.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"dX\", NativeBridge.DOUBLE_NATIVE_SUPPORT.toPrivateJsonNode(__who, dX.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"dY\", NativeBridge.DOUBLE_NATIVE_SUPPORT.toPrivateJsonNode(__who, dY.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"is_near\", NativeBridge.BOOLEAN_NATIVE_SUPPORT.toPrivateJsonNode(__who, is_near.get()));");
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
    gold.append("\n    __code_cost += 17;");
    gold.append("\n    __track(0);");
    gold.append("\n    d1.set(3.14);");
    gold.append("\n    __track(1);");
    gold.append("\n    double d2 = 2.71;");
    gold.append("\n    __track(2);");
    gold.append("\n    d3.set(d1.get() + d2);");
    gold.append("\n    __track(3);");
    gold.append("\n    double d4 = 1000000.0;");
    gold.append("\n    __track(4);");
    gold.append("\n    double d5 = d4 * d3.get();");
    gold.append("\n    __track(5);");
    gold.append("\n    double x = 0.1;");
    gold.append("\n    __track(6);");
    gold.append("\n    x += 0.2;");
    gold.append("\n    __track(7);");
    gold.append("\n    is_near.set(LibMath.near(x, 0.3));");
    gold.append("\n    __track(8);");
    gold.append("\n    dX.set(x);");
    gold.append("\n    __track(9);");
    gold.append("\n    dY.set(0.3);");
    gold.append("\n    __track(10);");
    gold.append("\n    String foo = 0.1 + \"x\" + 0.2;");
    gold.append("\n    __track(11);");
    gold.append("\n    assign.set(0);");
    gold.append("\n    __track(12);");
    gold.append("\n    assign.opAddTo(100);");
    gold.append("\n    __track(13);");
    gold.append("\n    assign.opDivBy(2);");
    gold.append("\n    __track(14);");
    gold.append("\n    assign.opMultBy(3);");
    gold.append("\n    __track(15);");
    gold.append("\n    assign.opSubFrom(17);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"setup\":");
    gold.append("\n        __step_setup();");
    gold.append("\n        return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  public boolean __onConnected__0(NtClient who){");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(16);");
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
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(17);");
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
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__state\":\"\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\",\"d1\":3.14,\"d3\":5.85,\"is_near\":true,\"dX\":0.30000000000000004,\"dY\":0.3,\"assign\":133.0} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":19,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"d1\":3.14,\"d3\":5.85,\"is_near\":true,\"dX\":0.30000000000000004,\"dY\":0.3,\"assign\":133.0,\"__goodwill_used\":0,\"__cost\":19,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"75\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":2,\"__connection_id\":1,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__seq\":3,\"__entropy\":\"4804307197456638271\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\nAS NO_ONE:{\"data\":{\"d1\":3.14,\"d3\":5.85,\"dX\":0.30000000000000004,\"dY\":0.3,\"is_near\":true},\"outstanding\":[],\"blockers\":[]}");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_LegacyEnum_12 = null;
  private String get_LegacyEnum_12() {
    if (cached_LegacyEnum_12 != null) {
      return cached_LegacyEnum_12;
    }
    cached_LegacyEnum_12 = generateTestOutput(true, "LegacyEnum_12", "./test_code/Types_LegacyEnum_success.a");
    return cached_LegacyEnum_12;
  }

  @Test
  public void testLegacyEnumEmission() {
    assertEmissionGood(get_LegacyEnum_12());
  }

  @Test
  public void testLegacyEnumSuccess() {
    assertLivePass(get_LegacyEnum_12());
  }

  @Test
  public void testLegacyEnumGoodWillHappy() {
    assertGoodWillHappy(get_LegacyEnum_12());
  }

  @Test
  public void testLegacyEnumExceptionFree() {
    assertExceptionFree(get_LegacyEnum_12());
  }

  @Test
  public void testLegacyEnumTODOFree() {
    assertTODOFree(get_LegacyEnum_12());
  }

  @Test
  public void stable_LegacyEnum_12() {
    String live = get_LegacyEnum_12();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_LegacyEnum_success.a");
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
    gold.append("\npublic class LegacyEnum_12 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxBoolean check1;");
    gold.append("\n  private final RxBoolean check2;");
    gold.append("\n  private final RxInt32 d;");
    gold.append("\n  private final RxInt32 ee;");
    gold.append("\n  public LegacyEnum_12(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxInt32(this, __root, \"x\", 1);");
    gold.append("\n    check1 = RxFactory.makeRxBoolean(this, __root, \"check1\", false);");
    gold.append("\n    check2 = RxFactory.makeRxBoolean(this, __root, \"check2\", false);");
    gold.append("\n    d = RxFactory.makeRxInt32(this, __root, \"d\", 2);");
    gold.append("\n    ee = RxFactory.makeRxInt32(this, __root, \"ee\", 1);");
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
    gold.append("\n    check1.__commit(\"check1\", __child);");
    gold.append("\n    check2.__commit(\"check2\", __child);");
    gold.append("\n    d.__commit(\"d\", __child);");
    gold.append("\n    ee.__commit(\"ee\", __child);");
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
    gold.append("\n    check1.__revert();");
    gold.append("\n    check2.__revert();");
    gold.append("\n    d.__revert();");
    gold.append("\n    ee.__revert();");
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
    gold.append("\n  private static final int [] __ALL_VALUES_E = new int[] {1, 2};");
    gold.append("\n  private static final int [] __ALL_VALUES_D = new int[] {1, 2, 3};");
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
    gold.append("\n  public void __test_hasDefaultAtStart(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"hasDefaultAtStart\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(0);");
    gold.append("\n      __assert_truth(d.get() == 2, 23, 2, 23, 21);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {\"hasDefaultAtStart\"};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {");
    gold.append("\n    switch(testName) {");
    gold.append("\n      case \"hasDefaultAtStart\":");
    gold.append("\n          __test_hasDefaultAtStart(report);");
    gold.append("\n          return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 6;");
    gold.append("\n    __track(1);");
    gold.append("\n    x.set(1);");
    gold.append("\n    __track(2);");
    gold.append("\n    check1.set(x.get() == 1);");
    gold.append("\n    __track(3);");
    gold.append("\n    check2.set(x.get() == 1);");
    gold.append("\n    __track(4);");
    gold.append("\n    __assert_truth(Utility.identity(1) == 1, 18, 2, 18, 28);");
    gold.append("\n    __track(5);");
    gold.append("\n    __assert_truth(Utility.identity(ee.get()) == 1, 19, 2, 19, 26);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"check1\":true,\"check2\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":6,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"check1\":true,\"check2\":true,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":6,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[hasDefaultAtStart] = 100.0%");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_LegacyEnvironment_13 = null;
  private String get_LegacyEnvironment_13() {
    if (cached_LegacyEnvironment_13 != null) {
      return cached_LegacyEnvironment_13;
    }
    cached_LegacyEnvironment_13 = generateTestOutput(true, "LegacyEnvironment_13", "./test_code/Types_LegacyEnvironment_success.a");
    return cached_LegacyEnvironment_13;
  }

  @Test
  public void testLegacyEnvironmentEmission() {
    assertEmissionGood(get_LegacyEnvironment_13());
  }

  @Test
  public void testLegacyEnvironmentSuccess() {
    assertLivePass(get_LegacyEnvironment_13());
  }

  @Test
  public void testLegacyEnvironmentGoodWillHappy() {
    assertGoodWillHappy(get_LegacyEnvironment_13());
  }

  @Test
  public void testLegacyEnvironmentExceptionFree() {
    assertExceptionFree(get_LegacyEnvironment_13());
  }

  @Test
  public void testLegacyEnvironmentTODOFree() {
    assertTODOFree(get_LegacyEnvironment_13());
  }

  @Test
  public void stable_LegacyEnvironment_13() {
    String live = get_LegacyEnvironment_13();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_LegacyEnvironment_success.a");
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
    gold.append("\npublic class LegacyEnvironment_13 extends LivingDocument {");
    gold.append("\n  private final RxBoolean a;");
    gold.append("\n  private final RxBoolean b;");
    gold.append("\n  public LegacyEnvironment_13(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    a = RxFactory.makeRxBoolean(this, __root, \"a\", false);");
    gold.append("\n    b = RxFactory.makeRxBoolean(this, __root, \"b\", false);");
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
    gold.append("\n    a.__commit(\"a\", __child);");
    gold.append("\n    b.__commit(\"b\", __child);");
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
    gold.append("\n    a.__revert();");
    gold.append("\n    b.__revert();");
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
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(0);");
    gold.append("\n    a.set(__blocked.get());");
    gold.append("\n    __track(1);");
    gold.append("\n    b.set((!__state.has()));");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"b\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":3,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"b\":true,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":3,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_LegacyInt_14 = null;
  private String get_LegacyInt_14() {
    if (cached_LegacyInt_14 != null) {
      return cached_LegacyInt_14;
    }
    cached_LegacyInt_14 = generateTestOutput(true, "LegacyInt_14", "./test_code/Types_LegacyInt_success.a");
    return cached_LegacyInt_14;
  }

  @Test
  public void testLegacyIntEmission() {
    assertEmissionGood(get_LegacyInt_14());
  }

  @Test
  public void testLegacyIntSuccess() {
    assertLivePass(get_LegacyInt_14());
  }

  @Test
  public void testLegacyIntGoodWillHappy() {
    assertGoodWillHappy(get_LegacyInt_14());
  }

  @Test
  public void testLegacyIntExceptionFree() {
    assertExceptionFree(get_LegacyInt_14());
  }

  @Test
  public void testLegacyIntTODOFree() {
    assertTODOFree(get_LegacyInt_14());
  }

  @Test
  public void stable_LegacyInt_14() {
    String live = get_LegacyInt_14();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_LegacyInt_success.a");
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
    gold.append("\npublic class LegacyInt_14 extends LivingDocument {");
    gold.append("\n  private final RxInt32 assign;");
    gold.append("\n  private final RxInt32 hex;");
    gold.append("\n  public LegacyInt_14(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    assign = RxFactory.makeRxInt32(this, __root, \"assign\", 0);");
    gold.append("\n    hex = RxFactory.makeRxInt32(this, __root, \"hex\", 0);");
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
    gold.append("\n    assign.__commit(\"assign\", __child);");
    gold.append("\n    hex.__commit(\"hex\", __child);");
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
    gold.append("\n    assign.__revert();");
    gold.append("\n    hex.__revert();");
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
    gold.append("\n    __code_cost += 29;");
    gold.append("\n    __track(0);");
    gold.append("\n    __assert_truth(1 < 2, 4, 2, 4, 15);");
    gold.append("\n    __track(1);");
    gold.append("\n    __assert_truth(1 <= 3, 5, 2, 5, 16);");
    gold.append("\n    __track(2);");
    gold.append("\n    __assert_truth(1 < 2.0, 6, 2, 6, 17);");
    gold.append("\n    __track(3);");
    gold.append("\n    __assert_truth(1 <= 3.0, 7, 2, 7, 18);");
    gold.append("\n    __track(4);");
    gold.append("\n    __assert_truth(2 > 1, 8, 2, 8, 15);");
    gold.append("\n    __track(5);");
    gold.append("\n    __assert_truth(3 >= 1, 9, 2, 9, 16);");
    gold.append("\n    __track(6);");
    gold.append("\n    __assert_truth(2.0 > 1, 10, 2, 10, 17);");
    gold.append("\n    __track(7);");
    gold.append("\n    __assert_truth(2.0 >= 1, 11, 2, 11, 18);");
    gold.append("\n    __track(8);");
    gold.append("\n    __assert_truth(2 == 2, 12, 2, 12, 16);");
    gold.append("\n    __track(9);");
    gold.append("\n    __assert_truth(2 != 3, 13, 2, 13, 16);");
    gold.append("\n    __track(10);");
    gold.append("\n    __assert_truth(1 + 1 == 2, 14, 2, 14, 20);");
    gold.append("\n    __track(11);");
    gold.append("\n    __assert_truth(LibMath.near(1 + 2.0, 3.0), 15, 2, 15, 24);");
    gold.append("\n    __track(12);");
    gold.append("\n    __assert_truth(LibString.equality(1 + \" donkey\", \"1 donkey\"), 16, 2, 16, 37);");
    gold.append("\n    __track(13);");
    gold.append("\n    __assert_truth(1 + 1 == 2, 17, 2, 17, 20);");
    gold.append("\n    __track(14);");
    gold.append("\n    __assert_truth(LibMath.near(2.0 + 1, 3.0), 18, 2, 18, 24);");
    gold.append("\n    __track(15);");
    gold.append("\n    __assert_truth(LibString.equality(\"#\" + 1, \"#1\"), 19, 2, 19, 25);");
    gold.append("\n    __track(16);");
    gold.append("\n    __assert_truth(4 / 2 > 1, 21, 2, 21, 19);");
    gold.append("\n    __track(17);");
    gold.append("\n    __assert_truth(5.0 / 2 > 1.9, 22, 2, 22, 23);");
    gold.append("\n    __track(18);");
    gold.append("\n    __assert_truth(10 % 3 > 0, 23, 2, 23, 20);");
    gold.append("\n    __track(19);");
    gold.append("\n    __assert_truth(2 * 3 > 5, 24, 2, 24, 19);");
    gold.append("\n    __track(20);");
    gold.append("\n    __assert_truth(2 * 3.0 > 5, 25, 2, 25, 21);");
    gold.append("\n    __track(21);");
    gold.append("\n    __assert_truth(2.0 * 3 > 5.9, 26, 2, 26, 23);");
    gold.append("\n    __track(22);");
    gold.append("\n    assign.set(0);");
    gold.append("\n    __track(23);");
    gold.append("\n    assign.opAddTo(100);");
    gold.append("\n    __track(24);");
    gold.append("\n    assign.opMultBy(3);");
    gold.append("\n    __track(25);");
    gold.append("\n    assign.opSubFrom(17);");
    gold.append("\n    __track(26);");
    gold.append("\n    assign.opModBy(17);");
    gold.append("\n    __track(27);");
    gold.append("\n    hex.set(74747);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"assign\":11,\"hex\":74747,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":29,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"assign\":11,\"hex\":74747,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":29,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_LegacyMaybe_15 = null;
  private String get_LegacyMaybe_15() {
    if (cached_LegacyMaybe_15 != null) {
      return cached_LegacyMaybe_15;
    }
    cached_LegacyMaybe_15 = generateTestOutput(true, "LegacyMaybe_15", "./test_code/Types_LegacyMaybe_success.a");
    return cached_LegacyMaybe_15;
  }

  @Test
  public void testLegacyMaybeEmission() {
    assertEmissionGood(get_LegacyMaybe_15());
  }

  @Test
  public void testLegacyMaybeSuccess() {
    assertLivePass(get_LegacyMaybe_15());
  }

  @Test
  public void testLegacyMaybeGoodWillHappy() {
    assertGoodWillHappy(get_LegacyMaybe_15());
  }

  @Test
  public void testLegacyMaybeExceptionFree() {
    assertExceptionFree(get_LegacyMaybe_15());
  }

  @Test
  public void testLegacyMaybeTODOFree() {
    assertTODOFree(get_LegacyMaybe_15());
  }

  @Test
  public void stable_LegacyMaybe_15() {
    String live = get_LegacyMaybe_15();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_LegacyMaybe_success.a");
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
    gold.append("\npublic class LegacyMaybe_15 extends LivingDocument {");
    gold.append("\n  private final RxMaybe<RxDouble> md;");
    gold.append("\n  private final RxMaybe<RxInt32> mi;");
    gold.append("\n  private final RxMaybe<RxString> ms;");
    gold.append("\n  public LegacyMaybe_15(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    md = RxFactory.makeRxMaybe(this, __root, \"md\", (RxParent __parent) -> RxFactory.makeRxDouble(__parent, __root, \"md\", 0.0));");
    gold.append("\n    mi = RxFactory.makeRxMaybe(this, __root, \"mi\", (RxParent __parent) -> RxFactory.makeRxInt32(__parent, __root, \"mi\", 0));");
    gold.append("\n    ms = RxFactory.makeRxMaybe(this, __root, \"ms\", (RxParent __parent) -> RxFactory.makeRxString(__parent, __root, \"ms\", \"\"));");
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
    gold.append("\n    md.__commit(\"md\", __child);");
    gold.append("\n    mi.__commit(\"mi\", __child);");
    gold.append("\n    ms.__commit(\"ms\", __child);");
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
    gold.append("\n    md.__revert();");
    gold.append("\n    mi.__revert();");
    gold.append("\n    ms.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    if (!(true)) {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n    ObjectNode __view = Utility.createObjectNode();");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n");
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
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {}");
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 4;");
    gold.append("\n    __track(1);");
    gold.append("\n    md.make().set(3.14);");
    gold.append("\n    __track(2);");
    gold.append("\n    mi.make().set(2);");
    gold.append("\n    __track(3);");
    gold.append("\n    ms.make().set(\"hi\");");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"md\":3.14,\"mi\":2,\"ms\":\"hi\",\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":4,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"md\":3.14,\"mi\":2,\"ms\":\"hi\",\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":4,\"__billing_seq\":1}");
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
  private String cached_LegacyStrings_16 = null;
  private String get_LegacyStrings_16() {
    if (cached_LegacyStrings_16 != null) {
      return cached_LegacyStrings_16;
    }
    cached_LegacyStrings_16 = generateTestOutput(true, "LegacyStrings_16", "./test_code/Types_LegacyStrings_success.a");
    return cached_LegacyStrings_16;
  }

  @Test
  public void testLegacyStringsEmission() {
    assertEmissionGood(get_LegacyStrings_16());
  }

  @Test
  public void testLegacyStringsSuccess() {
    assertLivePass(get_LegacyStrings_16());
  }

  @Test
  public void testLegacyStringsGoodWillHappy() {
    assertGoodWillHappy(get_LegacyStrings_16());
  }

  @Test
  public void testLegacyStringsExceptionFree() {
    assertExceptionFree(get_LegacyStrings_16());
  }

  @Test
  public void testLegacyStringsTODOFree() {
    assertTODOFree(get_LegacyStrings_16());
  }

  @Test
  public void stable_LegacyStrings_16() {
    String live = get_LegacyStrings_16();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_LegacyStrings_success.a");
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
    gold.append("\npublic class LegacyStrings_16 extends LivingDocument {");
    gold.append("\n  private final RxString s1;");
    gold.append("\n  private final RxString s2;");
    gold.append("\n  private final RxString s5;");
    gold.append("\n  private final RxString s6;");
    gold.append("\n  private final RxInt32 len;");
    gold.append("\n  private final RxString s7;");
    gold.append("\n  private final RxString re1;");
    gold.append("\n  private final RxString re2;");
    gold.append("\n  private final RxString re3;");
    gold.append("\n  private final RxString big_finish;");
    gold.append("\n  private final RxString mult1;");
    gold.append("\n  public LegacyStrings_16(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    s1 = RxFactory.makeRxString(this, __root, \"s1\", \"\");");
    gold.append("\n    s2 = RxFactory.makeRxString(this, __root, \"s2\", \"\");");
    gold.append("\n    s5 = RxFactory.makeRxString(this, __root, \"s5\", \"\");");
    gold.append("\n    s6 = RxFactory.makeRxString(this, __root, \"s6\", \"\");");
    gold.append("\n    len = RxFactory.makeRxInt32(this, __root, \"len\", 0);");
    gold.append("\n    s7 = RxFactory.makeRxString(this, __root, \"s7\", \"\");");
    gold.append("\n    re1 = RxFactory.makeRxString(this, __root, \"re1\", \"\");");
    gold.append("\n    re2 = RxFactory.makeRxString(this, __root, \"re2\", \"\");");
    gold.append("\n    re3 = RxFactory.makeRxString(this, __root, \"re3\", \"\");");
    gold.append("\n    big_finish = RxFactory.makeRxString(this, __root, \"big_finish\", \"\");");
    gold.append("\n    mult1 = RxFactory.makeRxString(this, __root, \"mult1\", \"\");");
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
    gold.append("\n    s1.__commit(\"s1\", __child);");
    gold.append("\n    s2.__commit(\"s2\", __child);");
    gold.append("\n    s5.__commit(\"s5\", __child);");
    gold.append("\n    s6.__commit(\"s6\", __child);");
    gold.append("\n    len.__commit(\"len\", __child);");
    gold.append("\n    s7.__commit(\"s7\", __child);");
    gold.append("\n    re1.__commit(\"re1\", __child);");
    gold.append("\n    re2.__commit(\"re2\", __child);");
    gold.append("\n    re3.__commit(\"re3\", __child);");
    gold.append("\n    big_finish.__commit(\"big_finish\", __child);");
    gold.append("\n    mult1.__commit(\"mult1\", __child);");
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
    gold.append("\n    s1.__revert();");
    gold.append("\n    s2.__revert();");
    gold.append("\n    s5.__revert();");
    gold.append("\n    s6.__revert();");
    gold.append("\n    len.__revert();");
    gold.append("\n    s7.__revert();");
    gold.append("\n    re1.__revert();");
    gold.append("\n    re2.__revert();");
    gold.append("\n    re3.__revert();");
    gold.append("\n    big_finish.__revert();");
    gold.append("\n    mult1.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    if (!(true)) {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n    ObjectNode __view = Utility.createObjectNode();");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"big_finish\", NativeBridge.STRING_NATIVE_SUPPORT.toPrivateJsonNode(__who, big_finish.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"len\", NativeBridge.INTEGER_NATIVE_SUPPORT.toPrivateJsonNode(__who, len.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"mult1\", NativeBridge.STRING_NATIVE_SUPPORT.toPrivateJsonNode(__who, mult1.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"re1\", NativeBridge.STRING_NATIVE_SUPPORT.toPrivateJsonNode(__who, re1.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"re2\", NativeBridge.STRING_NATIVE_SUPPORT.toPrivateJsonNode(__who, re2.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"re3\", NativeBridge.STRING_NATIVE_SUPPORT.toPrivateJsonNode(__who, re3.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"s1\", NativeBridge.STRING_NATIVE_SUPPORT.toPrivateJsonNode(__who, s1.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"s2\", NativeBridge.STRING_NATIVE_SUPPORT.toPrivateJsonNode(__who, s2.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"s5\", NativeBridge.STRING_NATIVE_SUPPORT.toPrivateJsonNode(__who, s5.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"s6\", NativeBridge.STRING_NATIVE_SUPPORT.toPrivateJsonNode(__who, s6.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"s7\", NativeBridge.STRING_NATIVE_SUPPORT.toPrivateJsonNode(__who, s7.get()));");
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
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {}");
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 21;");
    gold.append("\n    __track(1);");
    gold.append("\n    s1.set(\"Hello \");");
    gold.append("\n    __track(2);");
    gold.append("\n    s2.set(\"World\");");
    gold.append("\n    __track(3);");
    gold.append("\n    String s3 = s1.get() + s2.get();");
    gold.append("\n    __track(4);");
    gold.append("\n    String s4 = \"\";");
    gold.append("\n    __track(5);");
    gold.append("\n    s5.set(s3 + \"/\" + s4 + 3.14);");
    gold.append("\n    __track(6);");
    gold.append("\n    s6.set(\"cake\" + \" ninja\" + \"\\\"\");");
    gold.append("\n    __track(7);");
    gold.append("\n    len.set(s6.get().length() + 1);");
    gold.append("\n    __track(8);");
    gold.append("\n    s7.set(LibString.reverse(s6.get()));");
    gold.append("\n    __track(9);");
    gold.append("\n    re1.set((LibString.compare(\"x\", \"y\") < 0) + \";\" + (LibString.compare(\"x\", \"y\") <= 0) + \";\" + (LibString.compare(\"x\", \"y\") > 0) + \";\" + (LibString.compare(\"x\", \"y\") >= 0) + \";\" + (LibString.equality(\"x\", \"y\")) + \";\" + (!LibString.equality(\"x\", \"y\")) + \";\");");
    gold.append("\n    __track(10);");
    gold.append("\n    re2.set((LibString.compare(\"y\", \"x\") < 0) + \";\" + (LibString.compare(\"y\", \"x\") <= 0) + \";\" + (LibString.compare(\"y\", \"x\") > 0) + \";\" + (LibString.compare(\"y\", \"x\") >= 0) + \";\" + (LibString.equality(\"y\", \"x\")) + \";\" + (!LibString.equality(\"y\", \"x\")) + \";\");");
    gold.append("\n    __track(11);");
    gold.append("\n    re3.set((LibString.compare(\"x\", \"x\") < 0) + \";\" + (LibString.compare(\"x\", \"x\") <= 0) + \";\" + (LibString.compare(\"x\", \"x\") > 0) + \";\" + (LibString.compare(\"x\", \"x\") >= 0) + \";\" + (LibString.equality(\"x\", \"x\")) + \";\" + (!LibString.equality(\"x\", \"x\")) + \";\");");
    gold.append("\n    __track(12);");
    gold.append("\n    big_finish.set(\"X:\");");
    gold.append("\n    __track(13);");
    gold.append("\n    big_finish.opAddTo(1);");
    gold.append("\n    __track(14);");
    gold.append("\n    big_finish.opAddTo(\"/\");");
    gold.append("\n    __track(15);");
    gold.append("\n    big_finish.opAddTo(true);");
    gold.append("\n    __track(16);");
    gold.append("\n    big_finish.opAddTo(\" := \");");
    gold.append("\n    __track(17);");
    gold.append("\n    big_finish.opAddTo(3.14);");
    gold.append("\n    __track(18);");
    gold.append("\n    big_finish.opAddTo(\"\\n\\t\\b\\f\\r\\\\\\\"' cake:\" + 74747);");
    gold.append("\n    __track(19);");
    gold.append("\n    big_finish.opAddTo(\"\\n\\t\\b\\f\\r ninja:\" + 16702650);");
    gold.append("\n    __track(20);");
    gold.append("\n    mult1.set(LibString.multiply(\"Red\", 2));");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"s1\":\"Hello \",\"s2\":\"World\",\"s5\":\"Hello World/3.14\",\"s6\":\"cake ninja\\\"\",\"len\":12,\"s7\":\"\\\"ajnin ekac\",\"re1\":\"true;true;false;false;false;true;\",\"re2\":\"false;false;true;true;false;true;\",\"re3\":\"false;true;false;true;true;false;\",\"big_finish\":\"X:1/true := 3.14\\n\\t\\b\\f\\r\\\\\\\"' cake:74747\\n\\t\\b\\f\\r ninja:16702650\",\"mult1\":\"RedRed\",\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":21,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"s1\":\"Hello \",\"s2\":\"World\",\"s5\":\"Hello World/3.14\",\"s6\":\"cake ninja\\\"\",\"len\":12,\"s7\":\"\\\"ajnin ekac\",\"re1\":\"true;true;false;false;false;true;\",\"re2\":\"false;false;true;true;false;true;\",\"re3\":\"false;true;false;true;true;false;\",\"big_finish\":\"X:1/true := 3.14\\n\\t\\b\\f\\r\\\\\\\"' cake:74747\\n\\t\\b\\f\\r ninja:16702650\",\"mult1\":\"RedRed\",\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":21,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"75\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":2,\"__connection_id\":1,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__seq\":3,\"__entropy\":\"4804307197456638271\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\nAS NO_ONE:{\"data\":{\"big_finish\":\"X:1/true := 3.14\\n\\t\\b\\f\\r\\\\\\\"' cake:74747\\n\\t\\b\\f\\r ninja:16702650\",\"len\":12,\"mult1\":\"RedRed\",\"re1\":\"true;true;false;false;false;true;\",\"re2\":\"false;false;true;true;false;true;\",\"re3\":\"false;true;false;true;true;false;\",\"s1\":\"Hello \",\"s2\":\"World\",\"s5\":\"Hello World/3.14\",\"s6\":\"cake ninja\\\"\",\"s7\":\"\\\"ajnin ekac\"},\"outstanding\":[],\"blockers\":[]}");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_Long64Bits_17 = null;
  private String get_Long64Bits_17() {
    if (cached_Long64Bits_17 != null) {
      return cached_Long64Bits_17;
    }
    cached_Long64Bits_17 = generateTestOutput(true, "Long64Bits_17", "./test_code/Types_Long64Bits_success.a");
    return cached_Long64Bits_17;
  }

  @Test
  public void testLong64BitsEmission() {
    assertEmissionGood(get_Long64Bits_17());
  }

  @Test
  public void testLong64BitsSuccess() {
    assertLivePass(get_Long64Bits_17());
  }

  @Test
  public void testLong64BitsGoodWillHappy() {
    assertGoodWillHappy(get_Long64Bits_17());
  }

  @Test
  public void testLong64BitsExceptionFree() {
    assertExceptionFree(get_Long64Bits_17());
  }

  @Test
  public void testLong64BitsTODOFree() {
    assertTODOFree(get_Long64Bits_17());
  }

  @Test
  public void stable_Long64Bits_17() {
    String live = get_Long64Bits_17();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_Long64Bits_success.a");
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
    gold.append("\npublic class Long64Bits_17 extends LivingDocument {");
    gold.append("\n  private final RxInt64 x;");
    gold.append("\n  private final RxInt64 big;");
    gold.append("\n  public Long64Bits_17(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxInt64(this, __root, \"x\", 0L);");
    gold.append("\n    big = RxFactory.makeRxInt64(this, __root, \"big\", 0L);");
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
    gold.append("\n    big.__commit(\"big\", __child);");
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
    gold.append("\n    big.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public JsonNode getPrivateViewFor(NtClient __who) {");
    gold.append("\n    if (!(true)) {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n    ObjectNode __view = Utility.createObjectNode();");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"big\", NativeBridge.LONG_NATIVE_SUPPORT.toPrivateJsonNode(__who, big.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"x\", NativeBridge.LONG_NATIVE_SUPPORT.toPrivateJsonNode(__who, x.get()));");
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
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {}");
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 6;");
    gold.append("\n    __track(1);");
    gold.append("\n    x.set(123);");
    gold.append("\n    __track(2);");
    gold.append("\n    long y = 42;");
    gold.append("\n    __track(3);");
    gold.append("\n    long z = x.get() + y;");
    gold.append("\n    __track(4);");
    gold.append("\n    long u = 2423421234124213412L;");
    gold.append("\n    __track(5);");
    gold.append("\n    big.set((z + u));");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"x\":\"123\",\"big\":\"2423421234124213577\",\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":6,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"x\":\"123\",\"big\":\"2423421234124213577\",\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":6,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"75\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":2,\"__connection_id\":1,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__seq\":3,\"__entropy\":\"4804307197456638271\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\nAS NO_ONE:{\"data\":{\"big\":\"2423421234124213577\",\"x\":\"123\"},\"outstanding\":[],\"blockers\":[]}");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_LongFun_18 = null;
  private String get_LongFun_18() {
    if (cached_LongFun_18 != null) {
      return cached_LongFun_18;
    }
    cached_LongFun_18 = generateTestOutput(true, "LongFun_18", "./test_code/Types_LongFun_success.a");
    return cached_LongFun_18;
  }

  @Test
  public void testLongFunEmission() {
    assertEmissionGood(get_LongFun_18());
  }

  @Test
  public void testLongFunSuccess() {
    assertLivePass(get_LongFun_18());
  }

  @Test
  public void testLongFunGoodWillHappy() {
    assertGoodWillHappy(get_LongFun_18());
  }

  @Test
  public void testLongFunExceptionFree() {
    assertExceptionFree(get_LongFun_18());
  }

  @Test
  public void testLongFunTODOFree() {
    assertTODOFree(get_LongFun_18());
  }

  @Test
  public void stable_LongFun_18() {
    String live = get_LongFun_18();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_LongFun_success.a");
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
    gold.append("\npublic class LongFun_18 extends LivingDocument {");
    gold.append("\n  private final RxInt64 x;");
    gold.append("\n  private final RxTable<RTxR> t;");
    gold.append("\n  public LongFun_18(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxInt64(this, __root, \"x\", 0L);");
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
    gold.append("\n    x.__commit(\"x\", __child);");
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
    gold.append("\n    x.__revert();");
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
    gold.append("\n    private final RxInt64 y;");
    gold.append("\n    private final RxInt32 id;");
    gold.append("\n    private RTxR(ObjectNode __node, RxParent __owner) {");
    gold.append("\n      super(__owner);");
    gold.append("\n      y = RxFactory.makeRxInt64(this, __node, \"y\", 0L);");
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
    gold.append("\n        y.__commit(\"y\", __child);");
    gold.append("\n        id.__commit(\"id\", __child);");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __revert() {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        __isDying = false;");
    gold.append("\n        y.__revert();");
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
    gold.append("\n    x.set(123L);");
    gold.append("\n    __track(1);");
    gold.append("\n    NtList<RxInt64> _auto_0 = (t.iterate(true)).transform((item) -> item.y, null /* no bridge needed */);");
    gold.append("\n    for (RxInt64 _auto_1 : _auto_0) {");
    gold.append("\n      _auto_1.set(42);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    __track(2);");
    gold.append("\n    (t.iterate(true)).transform((item) -> item.y, null /* no bridge needed */).transform((item) -> item.bumpUpPost(), null /** no bridge needed */);");
    gold.append("\n    __track(3);");
    gold.append("\n    NtList<RxInt64> _auto_2 = (t.iterate(true)).transform((item) -> item.y, null /* no bridge needed */);");
    gold.append("\n    for (RxInt64 _auto_3 : _auto_2) {");
    gold.append("\n      _auto_3.opAddTo(90L);");
    gold.append("\n    }");
    gold.append("\n");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"x\":\"123\",\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":5,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"x\":\"123\",\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":5,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_MaybeDeleteFlow_19 = null;
  private String get_MaybeDeleteFlow_19() {
    if (cached_MaybeDeleteFlow_19 != null) {
      return cached_MaybeDeleteFlow_19;
    }
    cached_MaybeDeleteFlow_19 = generateTestOutput(true, "MaybeDeleteFlow_19", "./test_code/Types_MaybeDeleteFlow_success.a");
    return cached_MaybeDeleteFlow_19;
  }

  @Test
  public void testMaybeDeleteFlowEmission() {
    assertEmissionGood(get_MaybeDeleteFlow_19());
  }

  @Test
  public void testMaybeDeleteFlowSuccess() {
    assertLivePass(get_MaybeDeleteFlow_19());
  }

  @Test
  public void testMaybeDeleteFlowGoodWillHappy() {
    assertGoodWillHappy(get_MaybeDeleteFlow_19());
  }

  @Test
  public void testMaybeDeleteFlowExceptionFree() {
    assertExceptionFree(get_MaybeDeleteFlow_19());
  }

  @Test
  public void testMaybeDeleteFlowTODOFree() {
    assertTODOFree(get_MaybeDeleteFlow_19());
  }

  @Test
  public void stable_MaybeDeleteFlow_19() {
    String live = get_MaybeDeleteFlow_19();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_MaybeDeleteFlow_success.a");
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
    gold.append("\npublic class MaybeDeleteFlow_19 extends LivingDocument {");
    gold.append("\n  private final RxMaybe<RxInt32> x;");
    gold.append("\n  public MaybeDeleteFlow_19(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxMaybe(this, __root, \"x\", (RxParent __parent) -> RxFactory.makeRxInt32(__parent, __root, \"x\", 0));");
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
    gold.append("\n    x.make().set(123);");
    gold.append("\n    __track(1);");
    gold.append("\n    x.get().delete();");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"x\":null,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":3,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":3,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_MaybeDelete_20 = null;
  private String get_MaybeDelete_20() {
    if (cached_MaybeDelete_20 != null) {
      return cached_MaybeDelete_20;
    }
    cached_MaybeDelete_20 = generateTestOutput(true, "MaybeDelete_20", "./test_code/Types_MaybeDelete_success.a");
    return cached_MaybeDelete_20;
  }

  @Test
  public void testMaybeDeleteEmission() {
    assertEmissionGood(get_MaybeDelete_20());
  }

  @Test
  public void testMaybeDeleteSuccess() {
    assertLivePass(get_MaybeDelete_20());
  }

  @Test
  public void testMaybeDeleteGoodWillHappy() {
    assertGoodWillHappy(get_MaybeDelete_20());
  }

  @Test
  public void testMaybeDeleteExceptionFree() {
    assertExceptionFree(get_MaybeDelete_20());
  }

  @Test
  public void testMaybeDeleteTODOFree() {
    assertTODOFree(get_MaybeDelete_20());
  }

  @Test
  public void stable_MaybeDelete_20() {
    String live = get_MaybeDelete_20();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_MaybeDelete_success.a");
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
    gold.append("\npublic class MaybeDelete_20 extends LivingDocument {");
    gold.append("\n  private final RxMaybe<RxString> s;");
    gold.append("\n  private final RxMaybe<RxString> w00t;");
    gold.append("\n  public MaybeDelete_20(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    s = RxFactory.makeRxMaybe(this, __root, \"s\", (RxParent __parent) -> RxFactory.makeRxString(__parent, __root, \"s\", \"\"));");
    gold.append("\n    w00t = RxFactory.makeRxMaybe(this, __root, \"w00t\", (RxParent __parent) -> RxFactory.makeRxString(__parent, __root, \"w00t\", \"\"));");
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
    gold.append("\n    s.__commit(\"s\", __child);");
    gold.append("\n    w00t.__commit(\"w00t\", __child);");
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
    gold.append("\n    s.__revert();");
    gold.append("\n    w00t.__revert();");
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
    gold.append("\n  private void __step_start() {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(0);");
    gold.append("\n    s.make().set(\"xyz\");");
    gold.append("\n    __track(1);");
    gold.append("\n    __transitionStateMachine(\"next\", 0);");
    gold.append("\n  }");
    gold.append("\n  private void __step_next() {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(2);");
    gold.append("\n    s.make().set(\"abc\");");
    gold.append("\n    __track(3);");
    gold.append("\n    __transitionStateMachine(\"almost\", 0);");
    gold.append("\n  }");
    gold.append("\n  private void __step_almost() {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(4);");
    gold.append("\n    s.get().delete();");
    gold.append("\n    __track(5);");
    gold.append("\n    __transitionStateMachine(\"end\", 0);");
    gold.append("\n  }");
    gold.append("\n  private void __step_end() {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(6);");
    gold.append("\n    s.make().set(\"42\");");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"start\":");
    gold.append("\n        __step_start();");
    gold.append("\n        return;");
    gold.append("\n      case \"next\":");
    gold.append("\n        __step_next();");
    gold.append("\n        return;");
    gold.append("\n      case \"almost\":");
    gold.append("\n        __step_almost();");
    gold.append("\n        return;");
    gold.append("\n      case \"end\":");
    gold.append("\n        __step_end();");
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
    gold.append("\n  public void __test_flow(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"flow\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 8;");
    gold.append("\n      __track(7);");
    gold.append("\n      int haves = 0;");
    gold.append("\n      __track(8);");
    gold.append("\n      NtMaybe<String> _AutoConditionw_0;");
    gold.append("\n      if ((_AutoConditionw_0 = w00t.get()).has()) {");
    gold.append("\n        String w = _AutoConditionw_0.get();");
    gold.append("\n        __code_cost += 2;");
    gold.append("\n        __track(9);");
    gold.append("\n        haves++;");
    gold.append("\n      }");
    gold.append("\n      __track(10);");
    gold.append("\n      w00t.make().set(\"noice\");");
    gold.append("\n      __track(11);");
    gold.append("\n      NtMaybe<String> _AutoConditionw_1;");
    gold.append("\n      if ((_AutoConditionw_1 = w00t.get()).has()) {");
    gold.append("\n        String w = _AutoConditionw_1.get();");
    gold.append("\n        __code_cost += 2;");
    gold.append("\n        __track(12);");
    gold.append("\n        haves++;");
    gold.append("\n      }");
    gold.append("\n      __track(13);");
    gold.append("\n      w00t.get().delete();");
    gold.append("\n      __track(14);");
    gold.append("\n      NtMaybe<String> _AutoConditionw_2;");
    gold.append("\n      if ((_AutoConditionw_2 = w00t.get()).has()) {");
    gold.append("\n        String w = _AutoConditionw_2.get();");
    gold.append("\n        __code_cost += 2;");
    gold.append("\n        __track(15);");
    gold.append("\n        haves++;");
    gold.append("\n      }");
    gold.append("\n      __track(16);");
    gold.append("\n      __assert_truth(haves == 1, 41, 2, 41, 20);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {\"flow\"};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {");
    gold.append("\n    switch(testName) {");
    gold.append("\n      case \"flow\":");
    gold.append("\n          __test_flow(report);");
    gold.append("\n          return;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private void __construct_0(NtClient __who, ObjectNode __message) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(17);");
    gold.append("\n    __transitionStateMachine(\"start\", 0);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__state\":\"start\",\"__constructed\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__state\":\"next\",!TimeHiddenForStability!\"25\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\",\"s\":\"xyz\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__state\":\"almost\",!TimeHiddenForStability!\"50\",\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"50\",\"s\":\"abc\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"75\"}-->{\"__messages\":null,\"__seedUsed\":\"4804307197456638271\",\"__state\":\"end\",!TimeHiddenForStability!\"75\",\"__seq\":3,\"__entropy\":\"-1034601897293430941\",\"__time\":\"75\",\"s\":null} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seedUsed\":\"-1034601897293430941\",\"__state\":\"\",\"__seq\":4,\"__entropy\":\"7848011421992302230\",\"__time\":\"100\",\"s\":\"42\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"125\"}-->{\"__goodwill_used\":0,\"__cost\":13,\"__billing_seq\":4} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"7848011421992302230\",\"__seedUsed\":\"-1034601897293430941\",!TimeHiddenForStability!\"75\",\"__seq\":4,\"__time\":\"100\",\"s\":\"42\",\"__goodwill_used\":0,\"__cost\":13,\"__billing_seq\":4}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[flow] = 100.0%");
    gold.append("\n...DUMP:{\"w00t\":null}");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_NumberBulk_21 = null;
  private String get_NumberBulk_21() {
    if (cached_NumberBulk_21 != null) {
      return cached_NumberBulk_21;
    }
    cached_NumberBulk_21 = generateTestOutput(true, "NumberBulk_21", "./test_code/Types_NumberBulk_success.a");
    return cached_NumberBulk_21;
  }

  @Test
  public void testNumberBulkEmission() {
    assertEmissionGood(get_NumberBulk_21());
  }

  @Test
  public void testNumberBulkSuccess() {
    assertLivePass(get_NumberBulk_21());
  }

  @Test
  public void testNumberBulkGoodWillHappy() {
    assertGoodWillHappy(get_NumberBulk_21());
  }

  @Test
  public void testNumberBulkExceptionFree() {
    assertExceptionFree(get_NumberBulk_21());
  }

  @Test
  public void testNumberBulkTODOFree() {
    assertTODOFree(get_NumberBulk_21());
  }

  @Test
  public void stable_NumberBulk_21() {
    String live = get_NumberBulk_21();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_NumberBulk_success.a");
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
    gold.append("\npublic class NumberBulk_21 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxInt32 y;");
    gold.append("\n  private final RxDouble z;");
    gold.append("\n  private final RxLazy<Double> sum;");
    gold.append("\n  public NumberBulk_21(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxInt32(this, __root, \"x\", 1);");
    gold.append("\n    y = RxFactory.makeRxInt32(this, __root, \"y\", 2);");
    gold.append("\n    z = RxFactory.makeRxDouble(this, __root, \"z\", 0.0);");
    gold.append("\n    sum = new RxLazy<>(this, NativeBridge.DOUBLE_NATIVE_SUPPORT, () -> (x.get() + y.get() + z.get()));");
    gold.append("\n    x.__subscribe(sum);");
    gold.append("\n    y.__subscribe(sum);");
    gold.append("\n    z.__subscribe(sum);");
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
    gold.append("\n    x.__revert();");
    gold.append("\n    y.__revert();");
    gold.append("\n    z.__revert();");
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
    gold.append("\n    {");
    gold.append("\n      __code_cost += 4;");
    gold.append("\n      __track(0);");
    gold.append("\n      __assert_truth(LibMath.near((1 + 2 + 3.14), 6.14), 10, 2, 10, 35);");
    gold.append("\n      __track(1);");
    gold.append("\n      z.set(10);");
    gold.append("\n      __track(2);");
    gold.append("\n      __assert_truth(LibMath.near(sum.get(), 13), 12, 2, 12, 19);");
    gold.append("\n    }");
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
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(3);");
    gold.append("\n    z.set(3.14);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"z\":3.14,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":2,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"z\":3.14,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":2,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[PrimaryTest] = 100.0%");
    gold.append("\n...DUMP:{\"z\":10.0}");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_ReactiveAssignmentMismatches_22 = null;
  private String get_ReactiveAssignmentMismatches_22() {
    if (cached_ReactiveAssignmentMismatches_22 != null) {
      return cached_ReactiveAssignmentMismatches_22;
    }
    cached_ReactiveAssignmentMismatches_22 = generateTestOutput(false, "ReactiveAssignmentMismatches_22", "./test_code/Types_ReactiveAssignmentMismatches_failure.a");
    return cached_ReactiveAssignmentMismatches_22;
  }

  @Test
  public void testReactiveAssignmentMismatchesFailure() {
    assertLiveFail(get_ReactiveAssignmentMismatches_22());
  }

  @Test
  public void testReactiveAssignmentMismatchesExceptionFree() {
    assertExceptionFree(get_ReactiveAssignmentMismatches_22());
  }

  @Test
  public void testReactiveAssignmentMismatchesTODOFree() {
    assertTODOFree(get_ReactiveAssignmentMismatches_22());
  }

  @Test
  public void stable_ReactiveAssignmentMismatches_22() {
    String live = get_ReactiveAssignmentMismatches_22();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_ReactiveAssignmentMismatches_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 14,");
    gold.append("\n      \"character\" : 12");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 14,");
    gold.append("\n      \"character\" : 13");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type check failure: the type 'int' is unable to store type 'maybe<int>'.(TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 10,");
    gold.append("\n      \"character\" : 0");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 10,");
    gold.append("\n      \"character\" : 5");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type check failure: the type 'label' is unable to store type 'int'.(TypeCheckReferences)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 16,");
    gold.append("\n      \"character\" : 10");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 16,");
    gold.append("\n      \"character\" : 21");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Variable 'z' was already defined(EnvironmentDefine)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 16,");
    gold.append("\n      \"character\" : 10");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 16,");
    gold.append("\n      \"character\" : 21");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Variable 'z' was already defined(EnvironmentDefine)\"");
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
    gold.append("\npublic class ReactiveAssignmentMismatches_22 extends LivingDocument {");
    gold.append("\n  private final RxMaybe<RxInt32> x;");
    gold.append("\n  private final RxMaybe<RTxR> r;");
    gold.append("\n  private final RxString garrrr;");
    gold.append("\n  public ReactiveAssignmentMismatches_22(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxMaybe(this, __root, \"x\", (RxParent __parent) -> RxFactory.makeRxInt32(__parent, __root, \"x\", 0));");
    gold.append("\n    r = RxFactory.makeRxMaybe(this, __root, \"r\", (RxParent __parent) -> new RTxR(RxFactory.ensureChildNodeExists(__root, \"r\"), __parent));");
    gold.append("\n    garrrr = RxFactory.makeRxString(this, __root, \"garrrr\", \"\");");
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
    gold.append("\n    r.__commit(\"r\", __child);");
    gold.append("\n    garrrr.__commit(\"garrrr\", __child);");
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
    gold.append("\n    r.__revert();");
    gold.append("\n    garrrr.__revert();");
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
    gold.append("\n    private final RxString lbl;");
    gold.append("\n    private final RxInt32 id;");
    gold.append("\n    private RTxR(ObjectNode __node, RxParent __owner) {");
    gold.append("\n      super(__owner);");
    gold.append("\n      x = RxFactory.makeRxInt32(this, __node, \"x\", 0);");
    gold.append("\n      lbl = RxFactory.makeRxString(this, __node, \"lbl\", \"\");");
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
    gold.append("\n        lbl.__commit(\"lbl\", __child);");
    gold.append("\n        id.__commit(\"id\", __child);");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __revert() {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        __isDying = false;");
    gold.append("\n        x.__revert();");
    gold.append("\n        lbl.__revert();");
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
    gold.append("\n  private static class RTxM implements NtMessageBase {");
    gold.append("\n    private NtMaybe<Integer> mi;");
    gold.append("\n    private RTxM(ObjectNode payload) {");
    gold.append("\n      this.mi = NativeBridge.WRAP_MAYBE(NativeBridge.INTEGER_NATIVE_SUPPORT).readFromMessageObject(payload, \"mi\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.WRAP_MAYBE(NativeBridge.INTEGER_NATIVE_SUPPORT).writeTo(\"mi\", mi, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTxM(NtMaybe<Integer> mi) {");
    gold.append("\n      this.mi = mi;");
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
    gold.append("\n    private String lbl;");
    gold.append("\n    private int x;");
    gold.append("\n    private RTx_AnonObjConvert_0(ObjectNode payload) {");
    gold.append("\n      this.lbl = NativeBridge.STRING_NATIVE_SUPPORT.readFromMessageObject(payload, \"lbl\");");
    gold.append("\n      this.x = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"x\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.STRING_NATIVE_SUPPORT.writeTo(\"lbl\", lbl, __node);");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"x\", x, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_0(String lbl, int x) {");
    gold.append("\n      this.lbl = lbl;");
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
    gold.append("\n  private static class RTx_AnonObjConvert_1 implements NtMessageBase {");
    gold.append("\n    private int mi;");
    gold.append("\n    private RTx_AnonObjConvert_1(ObjectNode payload) {");
    gold.append("\n      this.mi = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(payload, \"mi\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.INTEGER_NATIVE_SUPPORT.writeTo(\"mi\", mi, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_1(int mi) {");
    gold.append("\n      this.mi = mi;");
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
    gold.append("\n    private String g;");
    gold.append("\n    private RTx_AnonObjConvert_2(ObjectNode payload) {");
    gold.append("\n      this.g = NativeBridge.STRING_NATIVE_SUPPORT.readFromMessageObject(payload, \"g\");");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      NativeBridge.STRING_NATIVE_SUPPORT.writeTo(\"g\", g, __node);");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_2(String g) {");
    gold.append("\n      this.g = g;");
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
    gold.append("\n  private static class RTx_AnonObjConvert_3 implements NtMessageBase {");
    gold.append("\n    private RTx_AnonObjConvert_3(ObjectNode payload) {}");
    gold.append("\n    @Override");
    gold.append("\n    public ObjectNode convertToObjectNode() {");
    gold.append("\n      ObjectNode __node = Utility.createObjectNode();");
    gold.append("\n      return __node;");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_3() {}");
    gold.append("\n  }");
    gold.append("\n  private static final MessageBridge<RTx_AnonObjConvert_3> __BRIDGE__AnonObjConvert_3 = new MessageBridge<>() {");
    gold.append("\n    @Override");
    gold.append("\n    public RTx_AnonObjConvert_3 convert(ObjectNode __node) {");
    gold.append("\n      return new RTx_AnonObjConvert_3(__node);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public RTx_AnonObjConvert_3[] makeArray(int __n) {");
    gold.append("\n      return new RTx_AnonObjConvert_3[__n];");
    gold.append("\n    }");
    gold.append("\n  };");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  private void __step_g() {}");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"g\":");
    gold.append("\n        __step_g();");
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
    gold.append("\n    __code_cost += 5;");
    gold.append("\n    __track(0);");
    gold.append("\n    {");
    gold.append("\n      RxMaybe<RTxR> _AutoRef5 = r;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr7 = new RTx_AnonObjConvert_0(\"g\", 1);");
    gold.append("\n      RTxR _CreateRef6 = _AutoRef5.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef6.lbl.set(_AutoExpr7.lbl);");
    gold.append("\n      _CreateRef6.x.set(_AutoExpr7.x);");
    gold.append("\n    }");
    gold.append("\n    __track(1);");
    gold.append("\n    RTxM z = new RTxM(1);");
    gold.append("\n    __track(2);");
    gold.append("\n    garrrr.set(123);");
    gold.append("\n    __track(3);");
    gold.append("\n    RTx_AnonObjConvert_2[] z = new RTx_AnonObjConvert_2[] {new RTx_AnonObjConvert_2(\"g\"), new RTx_AnonObjConvert_2(\"\")};");
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
  private String cached_SpecialConstants_23 = null;
  private String get_SpecialConstants_23() {
    if (cached_SpecialConstants_23 != null) {
      return cached_SpecialConstants_23;
    }
    cached_SpecialConstants_23 = generateTestOutput(true, "SpecialConstants_23", "./test_code/Types_SpecialConstants_success.a");
    return cached_SpecialConstants_23;
  }

  @Test
  public void testSpecialConstantsEmission() {
    assertEmissionGood(get_SpecialConstants_23());
  }

  @Test
  public void testSpecialConstantsSuccess() {
    assertLivePass(get_SpecialConstants_23());
  }

  @Test
  public void testSpecialConstantsGoodWillHappy() {
    assertGoodWillHappy(get_SpecialConstants_23());
  }

  @Test
  public void testSpecialConstantsExceptionFree() {
    assertExceptionFree(get_SpecialConstants_23());
  }

  @Test
  public void testSpecialConstantsTODOFree() {
    assertTODOFree(get_SpecialConstants_23());
  }

  @Test
  public void stable_SpecialConstants_23() {
    String live = get_SpecialConstants_23();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_SpecialConstants_success.a");
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
    gold.append("\npublic class SpecialConstants_23 extends LivingDocument {");
    gold.append("\n  public SpecialConstants_23(ObjectNode __root, DocumentMonitor __monitor) {");
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
  private String cached_StringBulk_24 = null;
  private String get_StringBulk_24() {
    if (cached_StringBulk_24 != null) {
      return cached_StringBulk_24;
    }
    cached_StringBulk_24 = generateTestOutput(true, "StringBulk_24", "./test_code/Types_StringBulk_success.a");
    return cached_StringBulk_24;
  }

  @Test
  public void testStringBulkEmission() {
    assertEmissionGood(get_StringBulk_24());
  }

  @Test
  public void testStringBulkSuccess() {
    assertLivePass(get_StringBulk_24());
  }

  @Test
  public void testStringBulkGoodWillHappy() {
    assertGoodWillHappy(get_StringBulk_24());
  }

  @Test
  public void testStringBulkExceptionFree() {
    assertExceptionFree(get_StringBulk_24());
  }

  @Test
  public void testStringBulkTODOFree() {
    assertTODOFree(get_StringBulk_24());
  }

  @Test
  public void stable_StringBulk_24() {
    String live = get_StringBulk_24();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:.\\test_code\\Types_StringBulk_success.a");
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
    gold.append("\npublic class StringBulk_24 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  private final RxString y;");
    gold.append("\n  private final RxDouble z;");
    gold.append("\n  private final RxLazy<String> sum;");
    gold.append("\n  public StringBulk_24(ObjectNode __root, DocumentMonitor __monitor) {");
    gold.append("\n    super(__root, __monitor);");
    gold.append("\n    x = RxFactory.makeRxInt32(this, __root, \"x\", 1);");
    gold.append("\n    y = RxFactory.makeRxString(this, __root, \"y\", \"2\");");
    gold.append("\n    z = RxFactory.makeRxDouble(this, __root, \"z\", 0.0);");
    gold.append("\n    sum = new RxLazy<>(this, NativeBridge.STRING_NATIVE_SUPPORT, () -> (x.get() + y.get() + z.get()));");
    gold.append("\n    x.__subscribe(sum);");
    gold.append("\n    y.__subscribe(sum);");
    gold.append("\n    z.__subscribe(sum);");
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
    gold.append("\n    x.__revert();");
    gold.append("\n    y.__revert();");
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
    gold.append("\n    __view.set(\"sum\", NativeBridge.STRING_NATIVE_SUPPORT.toPrivateJsonNode(__who, sum.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"x\", NativeBridge.INTEGER_NATIVE_SUPPORT.toPrivateJsonNode(__who, x.get()));");
    gold.append("\n    __code_cost++;");
    gold.append("\n    __view.set(\"y\", NativeBridge.STRING_NATIVE_SUPPORT.toPrivateJsonNode(__who, y.get()));");
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
    gold.append("\n  public void __test_PrimaryTest(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"PrimaryTest\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 4;");
    gold.append("\n      __track(1);");
    gold.append("\n      __assert_truth(LibString.equality(sum.get(), \"123.14\"), 10, 2, 10, 25);");
    gold.append("\n      __track(2);");
    gold.append("\n      z.set(10);");
    gold.append("\n      __track(3);");
    gold.append("\n      __assert_truth(LibString.equality(sum.get(), \"1210.0\"), 12, 2, 12, 25);");
    gold.append("\n    }");
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
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(4);");
    gold.append("\n    z.set(3.14);");
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
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{}}-->{\"__constructed\":true,\"z\":3.14,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":2,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"z\":3.14,\"__entropy\":\"-4962768465676381896\",\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":2,\"__billing_seq\":1}");
    gold.append("\n--PRIVACY QUERIES----------------------------------");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"75\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":2,\"__connection_id\":1,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__seq\":3,\"__entropy\":\"4804307197456638271\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\nAS NO_ONE:{\"data\":{\"sum\":\"123.14\",\"x\":1,\"y\":\"2\",\"z\":3.14},\"outstanding\":[],\"blockers\":[]}");
    gold.append("\nRANDO was DENIED");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[PrimaryTest] = 100.0%");
    gold.append("\n...DUMP:{\"z\":10.0}");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
}

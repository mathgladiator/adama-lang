/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedConnectionEventsTests extends GeneratedBase {
  private String cached_ConnectMustReturn_1 = null;
  private String get_ConnectMustReturn_1() {
    if (cached_ConnectMustReturn_1 != null) {
      return cached_ConnectMustReturn_1;
    }
    cached_ConnectMustReturn_1 = generateTestOutput(false, "ConnectMustReturn_1", "./test_code/ConnectionEvents_ConnectMustReturn_failure.a");
    return cached_ConnectMustReturn_1;
  }

  @Test
  public void testConnectMustReturnFailure() {
    assertLiveFail(get_ConnectMustReturn_1());
  }

  @Test
  public void testConnectMustReturnNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_ConnectMustReturn_1());
  }

  @Test
  public void testConnectMustReturnExceptionFree() {
    assertExceptionFree(get_ConnectMustReturn_1());
  }

  @Test
  public void testConnectMustReturnTODOFree() {
    assertTODOFree(get_ConnectMustReturn_1());
  }

  @Test
  public void stable_ConnectMustReturn_1() {
    String live = get_ConnectMustReturn_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:./test_code/ConnectionEvents_ConnectMustReturn_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 0,");
    gold.append("\n      \"character\" : 15");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 1,");
    gold.append("\n      \"character\" : 1");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"The @connected handler must return a boolean (ConnectionEvents)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_Happy_2 = null;
  private String get_Happy_2() {
    if (cached_Happy_2 != null) {
      return cached_Happy_2;
    }
    cached_Happy_2 = generateTestOutput(true, "Happy_2", "./test_code/ConnectionEvents_Happy_success.a");
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
    gold.append("Path:./test_code/ConnectionEvents_Happy_success.a");
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
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.delta.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.json.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.util.Map;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class Happy_2 extends LivingDocument {");
    gold.append("\n  public Happy_2(DocumentMonitor __monitor) {");
    gold.append("\n    super(__monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __insert(JsonStreamReader __reader) {");
    gold.append("\n    if (__reader.startObject()) {");
    gold.append("\n      while(__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"__state\":");
    gold.append("\n            __state.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__constructed\":");
    gold.append("\n            __constructed.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__next_time\":");
    gold.append("\n            __next_time.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__blocked\":");
    gold.append("\n            __blocked.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__seq\":");
    gold.append("\n            __seq.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__entropy\":");
    gold.append("\n            __entropy.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_future_id\":");
    gold.append("\n            __auto_future_id.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__connection_id\":");
    gold.append("\n            __connection_id.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__message_id\":");
    gold.append("\n            __message_id.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__time\":");
    gold.append("\n            __time.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__clients\":");
    gold.append("\n            __hydrateClients(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__messages\":");
    gold.append("\n            __hydrateMessages(__reader);");
    gold.append("\n            break;");
    gold.append("\n          default:");
    gold.append("\n            __reader.skipValue();");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __dump(JsonStreamWriter __writer) {");
    gold.append("\n    __writer.beginObject();");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__state\");");
    gold.append("\n    __state.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__constructed\");");
    gold.append("\n    __constructed.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__next_time\");");
    gold.append("\n    __next_time.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__blocked\");");
    gold.append("\n    __blocked.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__seq\");");
    gold.append("\n    __seq.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__entropy\");");
    gold.append("\n    __entropy.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_future_id\");");
    gold.append("\n    __auto_future_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__connection_id\");");
    gold.append("\n    __connection_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__message_id\");");
    gold.append("\n    __message_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__time\");");
    gold.append("\n    __time.__dump(__writer);");
    gold.append("\n    __dumpClients(__writer);");
    gold.append("\n    __dumpMessages(__writer);");
    gold.append("\n    __writer.endObject();");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, JsonStreamWriter __writer) {");
    gold.append("\n    __state.__commit(\"__state\", __writer);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __writer);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __writer);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __writer);");
    gold.append("\n    __seq.__commit(\"__seq\", __writer);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __writer);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __writer);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __writer);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __writer);");
    gold.append("\n    __time.__commit(\"__time\", __writer);");
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
    gold.append("\n  private class DeltaHappy_2 {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaHappy_2() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(Happy_2 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __obj.manifest();");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    public void hide(PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      if (__emitted) {");
    gold.append("\n        __emitted = false;");
    gold.append("\n        __writer.writeNull();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public PrivateView __createPrivateView(NtClient __who, Consumer<String> __updates) {");
    gold.append("\n    Happy_2 __self = this;");
    gold.append("\n    DeltaHappy_2 __state = new DeltaHappy_2();");
    gold.append("\n    return new PrivateView(__who, __updates) {");
    gold.append("\n      @Override");
    gold.append("\n      public void update(JsonStreamWriter __writer) {");
    gold.append("\n        __state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer));");
    gold.append("\n      }");
    gold.append("\n    };");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected Object __parse_message2(String channel, JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {}");
    gold.append("\n  public boolean __onConnected__0(NtClient cv){");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  public boolean __onConnected__1(NtClient cv){");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(1);");
    gold.append("\n    return true;");
    gold.append("\n  }");
    gold.append("\n  public void __onDisconnected__0(NtClient cv) {}");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onConnected(NtClient __cvalue) {");
    gold.append("\n    boolean __result = false;");
    gold.append("\n    if (__onConnected__0(__cvalue)) __result = true;");
    gold.append("\n    if (__onConnected__1(__cvalue)) __result = true;");
    gold.append("\n    return __result;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onDisconnected(NtClient __cvalue) {");
    gold.append("\n    __onDisconnected__0(__cvalue);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {}");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, NtMessageBase message) {}");
    gold.append("\n  @Override");
    gold.append("\n  protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__constructed\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"25\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":1,\"__connection_id\":1,\"__time\":\"25\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}} need:true in:0");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"50\",\"who\":{\"agent\":\"rando\",\"authority\":\"random-place\"}}-->{\"__seq\":2,\"__connection_id\":2,\"__time\":\"50\",\"__clients\":{\"1\":{\"agent\":\"rando\",\"authority\":\"random-place\"}}} need:true in:0");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":3}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":3}");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"75\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":3,\"__entropy\":\"-4962768465676381896\",\"__time\":\"75\"} need:false in:-75");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"100\"}-->{\"__goodwill_used\":0,\"__cost\":8,\"__billing_seq\":3} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__seq\":3,\"__connection_id\":2,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"},\"1\":{\"agent\":\"rando\",\"authority\":\"random-place\"}},\"__seedUsed\":\"0\",\"__goodwill_used\":0,\"__cost\":8,\"__billing_seq\":3}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__blocked\":false,\"__seq\":3,\"__entropy\":\"-4962768465676381896\",\"__auto_future_id\":0,\"__connection_id\":2,\"__message_id\":0,\"__time\":\"100\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"},\"1\":{\"agent\":\"rando\",\"authority\":\"random-place\"}}}");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__blocked\":false,\"__seq\":3,\"__entropy\":\"-4962768465676381896\",\"__auto_future_id\":0,\"__connection_id\":2,\"__message_id\":0,\"__time\":\"100\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"},\"1\":{\"agent\":\"rando\",\"authority\":\"random-place\"}}}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_Legacy_3 = null;
  private String get_Legacy_3() {
    if (cached_Legacy_3 != null) {
      return cached_Legacy_3;
    }
    cached_Legacy_3 = generateTestOutput(true, "Legacy_3", "./test_code/ConnectionEvents_Legacy_success.a");
    return cached_Legacy_3;
  }

  @Test
  public void testLegacyEmission() {
    assertEmissionGood(get_Legacy_3());
  }

  @Test
  public void testLegacySuccess() {
    assertLivePass(get_Legacy_3());
  }

  @Test
  public void testLegacyGoodWillHappy() {
    assertGoodWillHappy(get_Legacy_3());
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
    gold.append("Path:./test_code/ConnectionEvents_Legacy_success.a");
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
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.delta.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.json.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.util.Map;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class Legacy_3 extends LivingDocument {");
    gold.append("\n  public Legacy_3(DocumentMonitor __monitor) {");
    gold.append("\n    super(__monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __insert(JsonStreamReader __reader) {");
    gold.append("\n    if (__reader.startObject()) {");
    gold.append("\n      while(__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"__state\":");
    gold.append("\n            __state.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__constructed\":");
    gold.append("\n            __constructed.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__next_time\":");
    gold.append("\n            __next_time.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__blocked\":");
    gold.append("\n            __blocked.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__seq\":");
    gold.append("\n            __seq.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__entropy\":");
    gold.append("\n            __entropy.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_future_id\":");
    gold.append("\n            __auto_future_id.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__connection_id\":");
    gold.append("\n            __connection_id.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__message_id\":");
    gold.append("\n            __message_id.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__time\":");
    gold.append("\n            __time.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__clients\":");
    gold.append("\n            __hydrateClients(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__messages\":");
    gold.append("\n            __hydrateMessages(__reader);");
    gold.append("\n            break;");
    gold.append("\n          default:");
    gold.append("\n            __reader.skipValue();");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __dump(JsonStreamWriter __writer) {");
    gold.append("\n    __writer.beginObject();");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__state\");");
    gold.append("\n    __state.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__constructed\");");
    gold.append("\n    __constructed.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__next_time\");");
    gold.append("\n    __next_time.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__blocked\");");
    gold.append("\n    __blocked.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__seq\");");
    gold.append("\n    __seq.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__entropy\");");
    gold.append("\n    __entropy.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_future_id\");");
    gold.append("\n    __auto_future_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__connection_id\");");
    gold.append("\n    __connection_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__message_id\");");
    gold.append("\n    __message_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__time\");");
    gold.append("\n    __time.__dump(__writer);");
    gold.append("\n    __dumpClients(__writer);");
    gold.append("\n    __dumpMessages(__writer);");
    gold.append("\n    __writer.endObject();");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, JsonStreamWriter __writer) {");
    gold.append("\n    __state.__commit(\"__state\", __writer);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __writer);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __writer);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __writer);");
    gold.append("\n    __seq.__commit(\"__seq\", __writer);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __writer);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __writer);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __writer);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __writer);");
    gold.append("\n    __time.__commit(\"__time\", __writer);");
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
    gold.append("\n  private class DeltaLegacy_3 {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaLegacy_3() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(Legacy_3 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __obj.manifest();");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    public void hide(PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      if (__emitted) {");
    gold.append("\n        __emitted = false;");
    gold.append("\n        __writer.writeNull();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public PrivateView __createPrivateView(NtClient __who, Consumer<String> __updates) {");
    gold.append("\n    Legacy_3 __self = this;");
    gold.append("\n    DeltaLegacy_3 __state = new DeltaLegacy_3();");
    gold.append("\n    return new PrivateView(__who, __updates) {");
    gold.append("\n      @Override");
    gold.append("\n      public void update(JsonStreamWriter __writer) {");
    gold.append("\n        __state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer));");
    gold.append("\n      }");
    gold.append("\n    };");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected Object __parse_message2(String channel, JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {}");
    gold.append("\n  public boolean __onConnected__0(NtClient cv){");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  public boolean __onConnected__1(NtClient cv){");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(1);");
    gold.append("\n    return true;");
    gold.append("\n  }");
    gold.append("\n  public void __onDisconnected__0(NtClient cv) {}");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onConnected(NtClient __cvalue) {");
    gold.append("\n    boolean __result = false;");
    gold.append("\n    if (__onConnected__0(__cvalue)) __result = true;");
    gold.append("\n    if (__onConnected__1(__cvalue)) __result = true;");
    gold.append("\n    return __result;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onDisconnected(NtClient __cvalue) {");
    gold.append("\n    __onDisconnected__0(__cvalue);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {}");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient who, NtMessageBase message) {}");
    gold.append("\n  @Override");
    gold.append("\n  protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__constructed\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"25\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"}}-->{\"__seq\":1,\"__connection_id\":1,\"__time\":\"25\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}} need:true in:0");
    gold.append("\n{\"command\":\"connect\",\"timestamp\":\"50\",\"who\":{\"agent\":\"rando\",\"authority\":\"random-place\"}}-->{\"__seq\":2,\"__connection_id\":2,\"__time\":\"50\",\"__clients\":{\"1\":{\"agent\":\"rando\",\"authority\":\"random-place\"}}} need:true in:0");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":3}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":3}");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"75\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__seq\":3,\"__entropy\":\"-4962768465676381896\",\"__time\":\"75\"} need:false in:-75");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"100\"}-->{\"__goodwill_used\":0,\"__cost\":8,\"__billing_seq\":3} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__seq\":3,\"__connection_id\":2,\"__time\":\"75\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"},\"1\":{\"agent\":\"rando\",\"authority\":\"random-place\"}},\"__seedUsed\":\"0\",\"__goodwill_used\":0,\"__cost\":8,\"__billing_seq\":3}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__blocked\":false,\"__seq\":3,\"__entropy\":\"-4962768465676381896\",\"__auto_future_id\":0,\"__connection_id\":2,\"__message_id\":0,\"__time\":\"100\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"},\"1\":{\"agent\":\"rando\",\"authority\":\"random-place\"}}}");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__blocked\":false,\"__seq\":3,\"__entropy\":\"-4962768465676381896\",\"__auto_future_id\":0,\"__connection_id\":2,\"__message_id\":0,\"__time\":\"100\",\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"},\"1\":{\"agent\":\"rando\",\"authority\":\"random-place\"}}}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
}

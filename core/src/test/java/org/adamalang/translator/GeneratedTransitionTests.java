/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedTransitionTests extends GeneratedBase {
  private String cached_HappyInstant_1 = null;
  private String get_HappyInstant_1() {
    if (cached_HappyInstant_1 != null) {
      return cached_HappyInstant_1;
    }
    cached_HappyInstant_1 = generateTestOutput(true, "HappyInstant_1", "./test_code/Transition_HappyInstant_success.a");
    return cached_HappyInstant_1;
  }

  @Test
  public void testHappyInstantEmission() {
    assertEmissionGood(get_HappyInstant_1());
  }

  @Test
  public void testHappyInstantSuccess() {
    assertLivePass(get_HappyInstant_1());
  }

  @Test
  public void testHappyInstantGoodWillHappy() {
    assertGoodWillHappy(get_HappyInstant_1());
  }

  @Test
  public void testHappyInstantExceptionFree() {
    assertExceptionFree(get_HappyInstant_1());
  }

  @Test
  public void testHappyInstantTODOFree() {
    assertTODOFree(get_HappyInstant_1());
  }

  @Test
  public void stable_HappyInstant_1() {
    String live = get_HappyInstant_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:./test_code/Transition_HappyInstant_success.a");
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
    gold.append("\npublic class HappyInstant_1 extends LivingDocument {");
    gold.append("\n  public HappyInstant_1(DocumentMonitor __monitor) {");
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
    gold.append("\n  private class DeltaHappyInstant_1 {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaHappyInstant_1() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(HappyInstant_1 __item, PrivateLazyDeltaWriter __writer) {");
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
    gold.append("\n    HappyInstant_1 __self = this;");
    gold.append("\n    DeltaHappyInstant_1 __state = new DeltaHappyInstant_1();");
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
    gold.append("\n  private void __step_start() {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    __transitionStateMachine(\"next\", 0);");
    gold.append("\n  }");
    gold.append("\n  private void __step_next() {}");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"start\":");
    gold.append("\n        __step_start();");
    gold.append("\n        return;");
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
    gold.append("\n  private void __construct_0(NtClient __who, NtMessageBase __object) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(1);");
    gold.append("\n    __transitionStateMachine(\"start\", 0);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient __who, NtMessageBase __object) {");
    gold.append("\n    __construct_0(__who, __object);");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__state\":\"start\",\"__constructed\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\nNO_ONE was DENIED:5011");
    gold.append("\nRANDO was DENIED:5011");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":1}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":1}");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__state\":\"next\",\"__next_time\":\"25\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:true in:0");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":2}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":2}");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__state\":\"\",\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"50\"} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"75\"}-->{\"__goodwill_used\":0,\"__cost\":4,\"__billing_seq\":2} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"4804307197456638271\",\"__seedUsed\":\"-4962768465676381896\",\"__next_time\":\"25\",\"__seq\":2,\"__time\":\"50\",\"__goodwill_used\":0,\"__cost\":4,\"__billing_seq\":2}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"25\",\"__blocked\":false,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"75\"}");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"25\",\"__blocked\":false,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"75\"}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_HappyTimeTravel_2 = null;
  private String get_HappyTimeTravel_2() {
    if (cached_HappyTimeTravel_2 != null) {
      return cached_HappyTimeTravel_2;
    }
    cached_HappyTimeTravel_2 = generateTestOutput(true, "HappyTimeTravel_2", "./test_code/Transition_HappyTimeTravel_success.a");
    return cached_HappyTimeTravel_2;
  }

  @Test
  public void testHappyTimeTravelEmission() {
    assertEmissionGood(get_HappyTimeTravel_2());
  }

  @Test
  public void testHappyTimeTravelSuccess() {
    assertLivePass(get_HappyTimeTravel_2());
  }

  @Test
  public void testHappyTimeTravelGoodWillHappy() {
    assertGoodWillHappy(get_HappyTimeTravel_2());
  }

  @Test
  public void testHappyTimeTravelExceptionFree() {
    assertExceptionFree(get_HappyTimeTravel_2());
  }

  @Test
  public void testHappyTimeTravelTODOFree() {
    assertTODOFree(get_HappyTimeTravel_2());
  }

  @Test
  public void stable_HappyTimeTravel_2() {
    String live = get_HappyTimeTravel_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:./test_code/Transition_HappyTimeTravel_success.a");
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
    gold.append("\npublic class HappyTimeTravel_2 extends LivingDocument {");
    gold.append("\n  public HappyTimeTravel_2(DocumentMonitor __monitor) {");
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
    gold.append("\n  private class DeltaHappyTimeTravel_2 {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaHappyTimeTravel_2() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(HappyTimeTravel_2 __item, PrivateLazyDeltaWriter __writer) {");
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
    gold.append("\n    HappyTimeTravel_2 __self = this;");
    gold.append("\n    DeltaHappyTimeTravel_2 __state = new DeltaHappyTimeTravel_2();");
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
    gold.append("\n  private void __step_start() {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(0);");
    gold.append("\n    __transitionStateMachine(\"next\", 0.01);");
    gold.append("\n  }");
    gold.append("\n  private void __step_next() {}");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"start\":");
    gold.append("\n        __step_start();");
    gold.append("\n        return;");
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
    gold.append("\n  private void __construct_0(NtClient __who, NtMessageBase __object) {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(1);");
    gold.append("\n    __transitionStateMachine(\"start\", 0.01);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient __who, NtMessageBase __object) {");
    gold.append("\n    __construct_0(__who, __object);");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__state\":\"start\",\"__constructed\":true,\"__next_time\":\"10\",\"__entropy\":\"0\"} need:true in:0");
    gold.append("\nNO_ONE was DENIED:5011");
    gold.append("\nRANDO was DENIED:5011");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":1}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":1}");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__state\":\"next\",\"__next_time\":\"35\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:true in:10");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":2}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":2}");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seedUsed\":\"-4962768465676381896\",\"__state\":\"\",\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"50\"} need:false in:-15");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"75\"}-->{\"__goodwill_used\":0,\"__cost\":4,\"__billing_seq\":2} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"35\",\"__entropy\":\"4804307197456638271\",\"__seedUsed\":\"-4962768465676381896\",\"__seq\":2,\"__time\":\"50\",\"__goodwill_used\":0,\"__cost\":4,\"__billing_seq\":2}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"35\",\"__blocked\":false,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"75\"}");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"35\",\"__blocked\":false,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"75\"}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_InvokeLegacy_3 = null;
  private String get_InvokeLegacy_3() {
    if (cached_InvokeLegacy_3 != null) {
      return cached_InvokeLegacy_3;
    }
    cached_InvokeLegacy_3 = generateTestOutput(true, "InvokeLegacy_3", "./test_code/Transition_InvokeLegacy_success.a");
    return cached_InvokeLegacy_3;
  }

  @Test
  public void testInvokeLegacyEmission() {
    assertEmissionGood(get_InvokeLegacy_3());
  }

  @Test
  public void testInvokeLegacySuccess() {
    assertLivePass(get_InvokeLegacy_3());
  }

  @Test
  public void testInvokeLegacyGoodWillHappy() {
    assertGoodWillHappy(get_InvokeLegacy_3());
  }

  @Test
  public void testInvokeLegacyExceptionFree() {
    assertExceptionFree(get_InvokeLegacy_3());
  }

  @Test
  public void testInvokeLegacyTODOFree() {
    assertTODOFree(get_InvokeLegacy_3());
  }

  @Test
  public void stable_InvokeLegacy_3() {
    String live = get_InvokeLegacy_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:./test_code/Transition_InvokeLegacy_success.a");
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
    gold.append("\npublic class InvokeLegacy_3 extends LivingDocument {");
    gold.append("\n  private final RxInt32 x;");
    gold.append("\n  public InvokeLegacy_3(DocumentMonitor __monitor) {");
    gold.append("\n    super(__monitor);");
    gold.append("\n    x = new RxInt32(this, 0);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __insert(JsonStreamReader __reader) {");
    gold.append("\n    if (__reader.startObject()) {");
    gold.append("\n      while(__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"x\":");
    gold.append("\n            x.__insert(__reader);");
    gold.append("\n            break;");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"x\");");
    gold.append("\n    x.__dump(__writer);");
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
    gold.append("\n    x.__commit(\"x\", __writer);");
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
    gold.append("\n  private class DeltaInvokeLegacy_3 {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaInvokeLegacy_3() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(InvokeLegacy_3 __item, PrivateLazyDeltaWriter __writer) {");
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
    gold.append("\n    InvokeLegacy_3 __self = this;");
    gold.append("\n    DeltaInvokeLegacy_3 __state = new DeltaInvokeLegacy_3();");
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
    gold.append("\n  private void __step_init() {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(0);");
    gold.append("\n    __invoke(\"change_x\");");
    gold.append("\n    __track(1);");
    gold.append("\n    __assert_truth(x.get() == 42, 5, 2, 5, 15);");
    gold.append("\n  }");
    gold.append("\n  private void __step_change_x() {");
    gold.append("\n    __code_cost += 2;");
    gold.append("\n    __track(2);");
    gold.append("\n    x.set(42);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {");
    gold.append("\n    switch(__new_state) {");
    gold.append("\n      case \"init\":");
    gold.append("\n        __step_init();");
    gold.append("\n        return;");
    gold.append("\n      case \"change_x\":");
    gold.append("\n        __step_change_x();");
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
    gold.append("\n  private void __construct_0(NtClient __who, NtMessageBase __object) {");
    gold.append("\n    __code_cost += 3;");
    gold.append("\n    __track(3);");
    gold.append("\n    x.set(10);");
    gold.append("\n    __track(4);");
    gold.append("\n    __transitionStateMachine(\"init\", 0);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(NtClient __who, NtMessageBase __object) {");
    gold.append("\n    __construct_0(__who, __object);");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--JAVA COMPILE RESULTS-----------------------------");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__state\":\"init\",\"__constructed\":true,\"__entropy\":\"0\",\"x\":10} need:true in:0");
    gold.append("\nNO_ONE was DENIED:5011");
    gold.append("\nRANDO was DENIED:5011");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":1}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":1}");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seedUsed\":\"0\",\"__state\":\"\",\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\",\"x\":42} need:false in:-25");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"50\"}-->{\"__goodwill_used\":0,\"__cost\":8,\"__billing_seq\":1} need:true in:0");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"x\":42,\"__seedUsed\":\"0\",\"__seq\":1,\"__time\":\"25\",\"__goodwill_used\":0,\"__cost\":8,\"__billing_seq\":1}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"x\":42,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__blocked\":false,\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"50\"}");
    gold.append("\n{\"x\":42,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__blocked\":false,\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"50\"}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_RequiresNumber_4 = null;
  private String get_RequiresNumber_4() {
    if (cached_RequiresNumber_4 != null) {
      return cached_RequiresNumber_4;
    }
    cached_RequiresNumber_4 = generateTestOutput(false, "RequiresNumber_4", "./test_code/Transition_RequiresNumber_failure.a");
    return cached_RequiresNumber_4;
  }

  @Test
  public void testRequiresNumberFailure() {
    assertLiveFail(get_RequiresNumber_4());
  }

  @Test
  public void testRequiresNumberNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_RequiresNumber_4());
  }

  @Test
  public void testRequiresNumberExceptionFree() {
    assertExceptionFree(get_RequiresNumber_4());
  }

  @Test
  public void testRequiresNumberTODOFree() {
    assertTODOFree(get_RequiresNumber_4());
  }

  @Test
  public void stable_RequiresNumber_4() {
    String live = get_RequiresNumber_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:./test_code/Transition_RequiresNumber_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[ {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 6,");
    gold.append("\n      \"character\" : 22");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 6,");
    gold.append("\n      \"character\" : 26");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type check failure: Must have a type of 'int' or 'double', but the type is actually 'bool' (TypeCheckFailures)\"");
    gold.append("\n}, {");
    gold.append("\n  \"range\" : {");
    gold.append("\n    \"start\" : {");
    gold.append("\n      \"line\" : 2,");
    gold.append("\n      \"character\" : 23");
    gold.append("\n    },");
    gold.append("\n    \"end\" : {");
    gold.append("\n      \"line\" : 2,");
    gold.append("\n      \"character\" : 26");
    gold.append("\n    }");
    gold.append("\n  },");
    gold.append("\n  \"severity\" : 1,");
    gold.append("\n  \"source\" : \"error\",");
    gold.append("\n  \"message\" : \"Type check failure: Must have a type of 'int' or 'double', but the type is actually 'string' (TypeCheckFailures)\"");
    gold.append("\n} ]");
    gold.append("\n--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

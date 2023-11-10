/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedAuthPipeTests extends GeneratedBase {
  private String cached_BadReturn_1 = null;
  private String get_BadReturn_1() {
    if (cached_BadReturn_1 != null) {
      return cached_BadReturn_1;
    }
    cached_BadReturn_1 = generateTestOutput(false, "BadReturn_1", "./test_code/AuthPipe_BadReturn_failure.a");
    return cached_BadReturn_1;
  }

  @Test
  public void testBadReturnFailure() {
    assertLiveFail(get_BadReturn_1());
  }

  @Test
  public void testBadReturnNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_BadReturn_1());
  }

  @Test
  public void testBadReturnExceptionFree() {
    assertExceptionFree(get_BadReturn_1());
  }

  @Test
  public void testBadReturnTODOFree() {
    assertTODOFree(get_BadReturn_1());
  }

  @Test
  public void stable_BadReturn_1() {
    String live = get_BadReturn_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:AuthPipe_BadReturn_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":4,\"character\":24,\"byte\":62},\"end\":{\"line\":4,\"character\":28,\"byte\":66}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'string', but the type is actually 'bool'\",\"file\":\"./test_code/AuthPipe_BadReturn_failure.a\"},{\"range\":{\"start\":{\"line\":4,\"character\":16,\"byte\":54},\"end\":{\"line\":4,\"character\":17,\"byte\":55}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'string', but the type is actually 'int'\",\"file\":\"./test_code/AuthPipe_BadReturn_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_Happy_2 = null;
  private String get_Happy_2() {
    if (cached_Happy_2 != null) {
      return cached_Happy_2;
    }
    cached_Happy_2 = generateTestOutput(true, "Happy_2", "./test_code/AuthPipe_Happy_success.a");
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
  public void testHappyNoFormatException() {
    assertNoFormatException(get_Happy_2());
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
    gold.append("Path:AuthPipe_Happy_success.a");
    gold.append("\n--EMISSION-----------------------------------------");
    gold.append("\nEmission Success, Yay");
    gold.append("\n=FORMAT===================================================");
    gold.append("\nmessage M {");
    gold.append("\n}");
    gold.append("\n@authorization (M m) {");
    gold.append("\n  return {");
    gold.append("\n      agent:\"1\",");
    gold.append("\n      hash:\"hash\"");
    gold.append("\n    };");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n==========================================================");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[]\"--JAVA---------------------------------------------");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.delta.*;");
    gold.append("\nimport org.adamalang.runtime.delta.secure.*;");
    gold.append("\nimport org.adamalang.runtime.exceptions.*;");
    gold.append("\nimport org.adamalang.runtime.graph.*;");
    gold.append("\nimport org.adamalang.runtime.index.*;");
    gold.append("\nimport org.adamalang.runtime.json.*;");
    gold.append("\nimport org.adamalang.runtime.natives.*;");
    gold.append("\nimport org.adamalang.runtime.natives.algo.*;");
    gold.append("\nimport org.adamalang.runtime.natives.lists.*;");
    gold.append("\nimport org.adamalang.runtime.ops.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.*;");
    gold.append("\nimport org.adamalang.runtime.reactives.tables.*;");
    gold.append("\nimport org.adamalang.runtime.remote.*;");
    gold.append("\nimport org.adamalang.runtime.stdlib.*;");
    gold.append("\nimport org.adamalang.runtime.sys.*;");
    gold.append("\nimport org.adamalang.runtime.sys.web.*;");
    gold.append("\nimport org.adamalang.runtime.text.*;");
    gold.append("\nimport java.time.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.util.HashMap;");
    gold.append("\nimport java.util.HashSet;");
    gold.append("\nimport java.util.Map;");
    gold.append("\nimport java.util.Set;");
    gold.append("\npublic class Happy_2 extends LivingDocument {");
    gold.append("\n  @Override");
    gold.append("\n  public long __memory() {");
    gold.append("\n    long __sum = super.__memory();");
    gold.append("\n    return __sum;");
    gold.append("\n  }");
    gold.append("\n  public Happy_2(DocumentMonitor __monitor) {");
    gold.append("\n    super(__monitor);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __settle(Set<Integer> __viewers) {");
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
    gold.append("\n          case \"__last_expire_time\":");
    gold.append("\n            __last_expire_time.__insert(__reader);");
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
    gold.append("\n          case \"__timezone\":");
    gold.append("\n            __timezone.__insert(__reader);");
    gold.append("\n            __timezoneCachedZoneId = ZoneId.of(__timezone.get());");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_table_row_id\":");
    gold.append("\n            __auto_table_row_id.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_gen\":");
    gold.append("\n            __auto_gen.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_cache_id\":");
    gold.append("\n            __auto_cache_id.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__cache\":");
    gold.append("\n            __cache.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__webTaskId\":");
    gold.append("\n            __webTaskId.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__dedupe\":");
    gold.append("\n            __hydrateDeduper(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__clients\":");
    gold.append("\n            __hydrateClients(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__messages\":");
    gold.append("\n            __hydrateMessages(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__webqueue\":");
    gold.append("\n            __hydrateWebQueue(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__timeouts\":");
    gold.append("\n            __hydrateTimeouts(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__replication\":");
    gold.append("\n            __hydrateReplicationEngine(__reader);");
    gold.append("\n            break;");
    gold.append("\n          default:");
    gold.append("\n            __reader.skipValue();");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __patch(JsonStreamReader __reader) {");
    gold.append("\n    if (__reader.startObject()) {");
    gold.append("\n      while(__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"__state\":");
    gold.append("\n            __state.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__constructed\":");
    gold.append("\n            __constructed.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__next_time\":");
    gold.append("\n            __next_time.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__last_expire_time\":");
    gold.append("\n            __last_expire_time.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__blocked\":");
    gold.append("\n            __blocked.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__seq\":");
    gold.append("\n            __seq.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__entropy\":");
    gold.append("\n            __entropy.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_future_id\":");
    gold.append("\n            __auto_future_id.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__connection_id\":");
    gold.append("\n            __connection_id.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__message_id\":");
    gold.append("\n            __message_id.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__time\":");
    gold.append("\n            __time.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__timezone\":");
    gold.append("\n            __timezone.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_table_row_id\":");
    gold.append("\n            __auto_table_row_id.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_gen\":");
    gold.append("\n            __auto_gen.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__auto_cache_id\":");
    gold.append("\n            __auto_cache_id.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__cache\":");
    gold.append("\n            __cache.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__webTaskId\":");
    gold.append("\n            __webTaskId.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__dedupe\":");
    gold.append("\n            __hydrateDeduper(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__clients\":");
    gold.append("\n            __hydrateClients(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__messages\":");
    gold.append("\n            __hydrateMessages(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__webqueue\":");
    gold.append("\n            __hydrateWebQueue(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__timeouts\":");
    gold.append("\n            __hydrateTimeouts(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"__replication\":");
    gold.append("\n            __hydrateReplicationEngine(__reader);");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"__last_expire_time\");");
    gold.append("\n    __last_expire_time.__dump(__writer);");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"__timezone\");");
    gold.append("\n    __timezone.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_table_row_id\");");
    gold.append("\n    __auto_table_row_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_gen\");");
    gold.append("\n    __auto_gen.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_cache_id\");");
    gold.append("\n    __auto_cache_id.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__cache\");");
    gold.append("\n    __cache.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"__webTaskId\");");
    gold.append("\n    __webTaskId.__dump(__writer);");
    gold.append("\n    __dumpDeduper(__writer);");
    gold.append("\n    __dumpClients(__writer);");
    gold.append("\n    __dumpMessages(__writer);");
    gold.append("\n    __dumpTimeouts(__writer);");
    gold.append("\n    __dumpWebQueue(__writer);");
    gold.append("\n    __dumpReplicationEngine(__writer);");
    gold.append("\n    __writer.endObject();");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __commit(String __name, JsonStreamWriter __forward, JsonStreamWriter __reverse) {");
    gold.append("\n    __state.__commit(\"__state\", __forward, __reverse);");
    gold.append("\n    __constructed.__commit(\"__constructed\", __forward, __reverse);");
    gold.append("\n    __next_time.__commit(\"__next_time\", __forward, __reverse);");
    gold.append("\n    __last_expire_time.__commit(\"__last_expire_time\", __forward, __reverse);");
    gold.append("\n    __blocked.__commit(\"__blocked\", __forward, __reverse);");
    gold.append("\n    __seq.__commit(\"__seq\", __forward, __reverse);");
    gold.append("\n    __entropy.__commit(\"__entropy\", __forward, __reverse);");
    gold.append("\n    __auto_future_id.__commit(\"__auto_future_id\", __forward, __reverse);");
    gold.append("\n    __connection_id.__commit(\"__connection_id\", __forward, __reverse);");
    gold.append("\n    __message_id.__commit(\"__message_id\", __forward, __reverse);");
    gold.append("\n    __time.__commit(\"__time\", __forward, __reverse);");
    gold.append("\n    __timezone.__commit(\"__timezone\", __forward, __reverse);");
    gold.append("\n    __auto_table_row_id.__commit(\"__auto_table_row_id\", __forward, __reverse);");
    gold.append("\n    __auto_gen.__commit(\"__auto_gen\", __forward, __reverse);");
    gold.append("\n    __auto_cache_id.__commit(\"__auto_cache_id\", __forward, __reverse);");
    gold.append("\n    __cache.__commit(\"__cache\", __forward, __reverse);");
    gold.append("\n    __webTaskId.__commit(\"__webTaskId\", __forward, __reverse);");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __revert() {");
    gold.append("\n    __state.__revert();");
    gold.append("\n    __constructed.__revert();");
    gold.append("\n    __next_time.__revert();");
    gold.append("\n    __last_expire_time.__revert();");
    gold.append("\n    __blocked.__revert();");
    gold.append("\n    __seq.__revert();");
    gold.append("\n    __entropy.__revert();");
    gold.append("\n    __auto_future_id.__revert();");
    gold.append("\n    __connection_id.__revert();");
    gold.append("\n    __message_id.__revert();");
    gold.append("\n    __time.__revert();");
    gold.append("\n    __timezone.__revert();");
    gold.append("\n    __auto_table_row_id.__revert();");
    gold.append("\n    __webTaskId.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  public class DeltaPrivacyCache {");
    gold.append("\n    public DeltaPrivacyCache(NtPrincipal __who) {}");
    gold.append("\n  }");
    gold.append("\n  private class DeltaHappy_2 implements DeltaNode {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaHappy_2() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public boolean show(Happy_2 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      DeltaPrivacyCache __policy_cache = new DeltaPrivacyCache(__writer.who);");
    gold.append("\n      __writer.setCacheObject(__policy_cache);");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __obj.manifest();");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n      return true;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __code_cost += 0;");
    gold.append("\n    }");
    gold.append("\n    public void hide(PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      if (__emitted) {");
    gold.append("\n        clear();");
    gold.append("\n        __emitted = false;");
    gold.append("\n        __writer.writeNull();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public Set<String> __get_intern_strings() {");
    gold.append("\n    HashSet<String> __interns = new HashSet<>();");
    gold.append("\n    __interns.add(\"\");");
    gold.append("\n    __interns.add(\"?\");");
    gold.append("\n    return __interns;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public PrivateView __createPrivateView(NtPrincipal __who, Perspective ___perspective, AssetIdEncoder __encoder) {");
    gold.append("\n    Happy_2 __self = this;");
    gold.append("\n    DeltaHappy_2 __state = new DeltaHappy_2();");
    gold.append("\n    RTx__ViewerType __viewerState = new RTx__ViewerType();");
    gold.append("\n    int __viewId = __genViewId();");
    gold.append("\n    return new PrivateView(__viewId, __who, ___perspective, __encoder) {");
    gold.append("\n      @Override");
    gold.append("\n      public long memory() {");
    gold.append("\n        return __state.__memory();");
    gold.append("\n      }");
    gold.append("\n      @Override");
    gold.append("\n      public void dumpViewer(JsonStreamWriter __writer) {");
    gold.append("\n        __viewerState.__writeOut(__writer);");
    gold.append("\n      }");
    gold.append("\n      @Override");
    gold.append("\n      public void ingest(JsonStreamReader __reader) {");
    gold.append("\n        __viewerState.__ingest(__reader);");
    gold.append("\n      }");
    gold.append("\n      @Override");
    gold.append("\n      public void update(JsonStreamWriter __writer) {");
    gold.append("\n        __state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer, __viewerState, __encoder, __viewId));");
    gold.append("\n      }");
    gold.append("\n    };");
    gold.append("\n  }");
    gold.append("\n  private static class RTx__ViewerType extends NtMessageBase {");
    gold.append("\n    private final RTx__ViewerType __this;");
    gold.append("\n    public void __hash(HashBuilder __hash) {");
    gold.append("\n      __hash.hashString(\"anonymous\");");
    gold.append("\n    }");
    gold.append("\n    private static String[] __INDEX_COLUMNS___ViewerType = new String[] {};");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS___ViewerType;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int[] __getIndexValues() {");
    gold.append("\n      return new int[] {};");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType(JsonStreamReader __reader) {");
    gold.append("\n      __this = this;");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      __reader.mustSkipObject();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType() { __this = this; }");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTx__ViewerType implements DeltaNode {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTx__ViewerType() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public void show(RTx__ViewerType __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __code_cost += 0;");
    gold.append("\n    }");
    gold.append("\n    public void hide(PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      if (__emitted) {");
    gold.append("\n        clear();");
    gold.append("\n        __emitted = false;");
    gold.append("\n        __writer.writeNull();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static class RTxM extends NtMessageBase {");
    gold.append("\n    private final RTxM __this;");
    gold.append("\n    public void __hash(HashBuilder __hash) {");
    gold.append("\n      __hash.hashString(\"M\");");
    gold.append("\n    }");
    gold.append("\n    private static String[] __INDEX_COLUMNS_M = new String[] {};");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS_M;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int[] __getIndexValues() {");
    gold.append("\n      return new int[] {};");
    gold.append("\n    }");
    gold.append("\n    private RTxM(JsonStreamReader __reader) {");
    gold.append("\n      __this = this;");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      __reader.mustSkipObject();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTxM() { __this = this; }");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTxM implements DeltaNode {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTxM() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public void show(RTxM __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __code_cost += 0;");
    gold.append("\n    }");
    gold.append("\n    public void hide(PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      if (__emitted) {");
    gold.append("\n        clear();");
    gold.append("\n        __emitted = false;");
    gold.append("\n        __writer.writeNull();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static class RTx_AnonObjConvert_0 extends NtMessageBase {");
    gold.append("\n    private final RTx_AnonObjConvert_0 __this;");
    gold.append("\n    private String agent = \"\";");
    gold.append("\n    private String hash = \"\";");
    gold.append("\n    public void __hash(HashBuilder __hash) {");
    gold.append("\n      __hash.hashString(\"agent\");");
    gold.append("\n      __hash.hashString(this.agent);");
    gold.append("\n      __hash.hashString(\"hash\");");
    gold.append("\n      __hash.hashString(this.hash);");
    gold.append("\n      __hash.hashString(\"anonymous\");");
    gold.append("\n    }");
    gold.append("\n    private static String[] __INDEX_COLUMNS__AnonObjConvert_0 = new String[] {};");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS__AnonObjConvert_0;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int[] __getIndexValues() {");
    gold.append("\n      return new int[] {};");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_0(JsonStreamReader __reader) {");
    gold.append("\n      __this = this;");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      __reader.mustStartObject();");
    gold.append("\n      while (__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"agent\":");
    gold.append("\n            this.agent = __reader.readString();");
    gold.append("\n            break;");
    gold.append("\n          case \"hash\":");
    gold.append("\n            this.hash = __reader.readString();");
    gold.append("\n            break;");
    gold.append("\n          default:");
    gold.append("\n            __reader.skipValue();");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.writeObjectFieldIntro(\"agent\");");
    gold.append("\n      __writer.writeString(agent);");
    gold.append("\n      __writer.writeObjectFieldIntro(\"hash\");");
    gold.append("\n      __writer.writeString(hash);");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_0() { __this = this; }");
    gold.append("\n    private RTx_AnonObjConvert_0(String agent, String hash) {");
    gold.append("\n      this.__this = this;");
    gold.append("\n      this.agent = agent;");
    gold.append("\n      this.hash = hash;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTx_AnonObjConvert_0 implements DeltaNode {");
    gold.append("\n    private DString __dagent;");
    gold.append("\n    private DString __dhash;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTx_AnonObjConvert_0() {");
    gold.append("\n      __dagent = new DString();");
    gold.append("\n      __dhash = new DString();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      __sum += __dagent.__memory();");
    gold.append("\n      __sum += __dhash.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public void show(RTx_AnonObjConvert_0 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __dagent.show(__item.agent, __obj.planField(\"agent\"));");
    gold.append("\n      __dhash.show(__item.hash, __obj.planField(\"hash\"));");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __dagent.clear();");
    gold.append("\n      __dhash.clear();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n    }");
    gold.append("\n    public void hide(PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      if (__emitted) {");
    gold.append("\n        clear();");
    gold.append("\n        __emitted = false;");
    gold.append("\n        __writer.writeNull();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  public static HashMap<String, HashMap<String, Object>> __services() {");
    gold.append("\n    HashMap<String, HashMap<String, Object>> __map = new HashMap<>();");
    gold.append("\n    return __map;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __link(ServiceRegistry __registry) {}");
    gold.append("\n  @Override");
    gold.append("\n  public void __executeServiceCalls(boolean cancel) {}");
    gold.append("\n  @Override");
    gold.append("\n  public String __getViewStateFilter() {");
    gold.append("\n    return \"[]\";");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected boolean __is_direct_channel(String channel) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __handle_direct(CoreRequestContext context, String channel, Object __message) throws AbortMessageException {");
    gold.append("\n    return;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __route(AsyncTask task) {");
    gold.append("\n    return;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected Object __parse_message(String channel, JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_future_queues() {");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __bindReplication() {}");
    gold.append("\n  @Override");
    gold.append("\n  public String __metrics() { return \"{}\"; }");
    gold.append("\n  @Override");
    gold.append("\n  public void __debug(JsonStreamWriter __writer) {}");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __open_channel(String name) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String __auth(CoreRequestContext __context, String username, String password) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  public AuthResponse __authpipe(CoreRequestContext __context, String __message) {");
    gold.append("\n    try {");
    gold.append("\n      if (__message == null) throw new AbortMessageException();");
    gold.append("\n      RTxM m = new RTxM(new JsonStreamReader(__message));");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(0);");
    gold.append("\n      {");
    gold.append("\n        RTx_AnonObjConvert_0 __capture1 = new RTx_AnonObjConvert_0(\"1\", \"hash\");");
    gold.append("\n        return new AuthResponse().agent(__capture1.agent).hash(__capture1.hash);");
    gold.append("\n      }");
    gold.append("\n    } catch (AbortMessageException ame) {");
    gold.append("\n      return null;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __password(CoreRequestContext __context, String __pw) {}");
    gold.append("\n  @Override");
    gold.append("\n  protected WebResponse __get_internal(CoreRequestContext __context, WebGet __request) throws AbortMessageException {");
    gold.append("\n    WebPath __path = new WebPath(__request.uri);");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected WebResponse __put_internal(CoreRequestContext __context, WebPut __request) throws AbortMessageException {");
    gold.append("\n    WebPath __path = new WebPath(__request.uri);");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected WebResponse __delete_internal(CoreRequestContext __context, WebDelete __request) throws AbortMessageException {");
    gold.append("\n    WebPath __path = new WebPath(__request.uri);");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public WebResponse __options(CoreRequestContext __context, WebGet __request) {");
    gold.append("\n    WebPath __path = new WebPath(__request.uri);");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __invoke_label(String __new_state) {}");
    gold.append("\n  public static boolean __onCanCreate(CoreRequestContext __context) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  public static boolean __onCanInvent(CoreRequestContext __context) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  public static boolean __onCanSendWhileDisconnected(CoreRequestContext __context) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onLoad() {}");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onCanAssetAttached(CoreRequestContext __cvalue) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onAssetAttached(CoreRequestContext __cvalue, NtAsset __pvalue) {}");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __delete(CoreRequestContext __cvalue) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onConnected(CoreRequestContext __cvalue) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onDisconnected(CoreRequestContext __cvalue) {}");
    gold.append("\n  public static HashMap<String, Object> __config() {");
    gold.append("\n    HashMap<String, Object> __map = new HashMap<>();");
    gold.append("\n    return __map;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {}");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(CoreRequestContext _c, NtMessageBase _m) {}");
    gold.append("\n  @Override");
    gold.append("\n  protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--=[LivingDocumentFactory COMPILING]=---");
    gold.append("\n--=[LivingDocumentFactory MADE]=---");
    gold.append("\n--REFLECTION RESULTS-------------------------------------");
    gold.append("\n{\"types\":{\"__Root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}},\"M\":{\"nature\":\"native_message\",\"name\":\"M\",\"anonymous\":false,\"fields\":{}},\"_AnonObjConvert_0\":{\"nature\":\"native_message\",\"name\":\"_AnonObjConvert_0\",\"anonymous\":true,\"fields\":{\"agent\":{\"type\":{\"nature\":\"native_value\",\"type\":\"string\"},\"privacy\":\"public\"},\"hash\":{\"type\":{\"nature\":\"native_value\",\"type\":\"string\"},\"privacy\":\"public\"}}}},\"channels\":{},\"channels-privacy\":{},\"constructors\":[],\"labels\":[]}");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\",\"key\":\"0\",\"origin\":\"origin\",\"ip\":\"ip\"}-->{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__messages\":null,\"__seq\":1} need:false in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"25\"} need:false in:0");
    gold.append("\nCPU:0");
    gold.append("\nMEMORY:384");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seq\":3,\"__entropy\":\"-1034601897293430941\",\"__time\":\"50\"} need:false in:0");
    gold.append("\nNO_ONE: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":3}");
    gold.append("\nNO_ONE|FAILURE:184333");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"75\"}-->{\"__messages\":null,\"__seq\":4,\"__entropy\":\"7848011421992302230\",\"__time\":\"75\"} need:false in:0");
    gold.append("\nRANDO: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":4}");
    gold.append("\n+ RANDO DELTA:{\"seq\":4}");
    gold.append("\nRANDO|FAILURE:184333");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__time\":\"100\"} need:false in:0");
    gold.append("\nRANDO|SUCCESS:5");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":5}");
    gold.append("\n+ RANDO DELTA:{\"seq\":5}");
    gold.append("\nMEMORY:502");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{}");
    gold.append("\n--METRIC RESULTS-----------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_MissingReturn_3 = null;
  private String get_MissingReturn_3() {
    if (cached_MissingReturn_3 != null) {
      return cached_MissingReturn_3;
    }
    cached_MissingReturn_3 = generateTestOutput(false, "MissingReturn_3", "./test_code/AuthPipe_MissingReturn_failure.a");
    return cached_MissingReturn_3;
  }

  @Test
  public void testMissingReturnFailure() {
    assertLiveFail(get_MissingReturn_3());
  }

  @Test
  public void testMissingReturnNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_MissingReturn_3());
  }

  @Test
  public void testMissingReturnExceptionFree() {
    assertExceptionFree(get_MissingReturn_3());
  }

  @Test
  public void testMissingReturnTODOFree() {
    assertTODOFree(get_MissingReturn_3());
  }

  @Test
  public void stable_MissingReturn_3() {
    String live = get_MissingReturn_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:AuthPipe_MissingReturn_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":4,\"character\":2,\"byte\":40},\"end\":{\"line\":4,\"character\":12,\"byte\":50}},\"severity\":1,\"source\":\"error\",\"message\":\"The return statement within a @authorization expects a hash\",\"file\":\"./test_code/AuthPipe_MissingReturn_failure.a\"},{\"range\":{\"start\":{\"line\":4,\"character\":2,\"byte\":40},\"end\":{\"line\":4,\"character\":12,\"byte\":50}},\"severity\":1,\"source\":\"error\",\"message\":\"The return statement within a @authorization expects an agent\",\"file\":\"./test_code/AuthPipe_MissingReturn_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_NoReturn_4 = null;
  private String get_NoReturn_4() {
    if (cached_NoReturn_4 != null) {
      return cached_NoReturn_4;
    }
    cached_NoReturn_4 = generateTestOutput(false, "NoReturn_4", "./test_code/AuthPipe_NoReturn_failure.a");
    return cached_NoReturn_4;
  }

  @Test
  public void testNoReturnFailure() {
    assertLiveFail(get_NoReturn_4());
  }

  @Test
  public void testNoReturnNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_NoReturn_4());
  }

  @Test
  public void testNoReturnExceptionFree() {
    assertExceptionFree(get_NoReturn_4());
  }

  @Test
  public void testNoReturnTODOFree() {
    assertTODOFree(get_NoReturn_4());
  }

  @Test
  public void stable_NoReturn_4() {
    String live = get_NoReturn_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:AuthPipe_NoReturn_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":3,\"character\":0,\"byte\":15},\"end\":{\"line\":4,\"character\":1,\"byte\":39}},\"severity\":1,\"source\":\"error\",\"message\":\"@authorization must either return a special message\",\"file\":\"./test_code/AuthPipe_NoReturn_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_TooMany_5 = null;
  private String get_TooMany_5() {
    if (cached_TooMany_5 != null) {
      return cached_TooMany_5;
    }
    cached_TooMany_5 = generateTestOutput(false, "TooMany_5", "./test_code/AuthPipe_TooMany_failure.a");
    return cached_TooMany_5;
  }

  @Test
  public void testTooManyFailure() {
    assertLiveFail(get_TooMany_5());
  }

  @Test
  public void testTooManyNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_TooMany_5());
  }

  @Test
  public void testTooManyExceptionFree() {
    assertExceptionFree(get_TooMany_5());
  }

  @Test
  public void testTooManyTODOFree() {
    assertTODOFree(get_TooMany_5());
  }

  @Test
  public void stable_TooMany_5() {
    String live = get_TooMany_5();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:AuthPipe_TooMany_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":6,\"character\":0,\"byte\":41},\"end\":{\"line\":7,\"character\":1,\"byte\":65}},\"severity\":1,\"source\":\"error\",\"message\":\"Only one @authorization action allowed\",\"file\":\"./test_code/AuthPipe_TooMany_failure.a\"},{\"range\":{\"start\":{\"line\":3,\"character\":0,\"byte\":15},\"end\":{\"line\":4,\"character\":1,\"byte\":39}},\"severity\":1,\"source\":\"error\",\"message\":\"@authorization must either return a special message\",\"file\":\"./test_code/AuthPipe_TooMany_failure.a\"},{\"range\":{\"start\":{\"line\":6,\"character\":0,\"byte\":41},\"end\":{\"line\":7,\"character\":1,\"byte\":65}},\"severity\":1,\"source\":\"error\",\"message\":\"@authorization must either return a special message\",\"file\":\"./test_code/AuthPipe_TooMany_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

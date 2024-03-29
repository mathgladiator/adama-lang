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

public class GeneratedLinqUniqueTests extends GeneratedBase {
  private String cached_BadElementType_1 = null;
  private String get_BadElementType_1() {
    if (cached_BadElementType_1 != null) {
      return cached_BadElementType_1;
    }
    cached_BadElementType_1 = generateTestOutput(false, "BadElementType_1", "./test_code/LinqUnique_BadElementType_failure.a");
    return cached_BadElementType_1;
  }

  @Test
  public void testBadElementTypeFailure() {
    assertLiveFail(get_BadElementType_1());
  }

  @Test
  public void testBadElementTypeNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_BadElementType_1());
  }

  @Test
  public void testBadElementTypeExceptionFree() {
    assertExceptionFree(get_BadElementType_1());
  }

  @Test
  public void testBadElementTypeTODOFree() {
    assertTODOFree(get_BadElementType_1());
  }

  @Test
  public void stable_BadElementType_1() {
    String live = get_BadElementType_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:LinqUnique_BadElementType_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":17,\"character\":21,\"byte\":186},\"end\":{\"line\":17,\"character\":41,\"byte\":206}},\"severity\":1,\"source\":\"error\",\"message\":\"the element has a type of'r<map<int,r<int>>>' which is not capable of being compared, hashed, and equality tested for uniqueness\",\"file\":\"./test_code/LinqUnique_BadElementType_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_BadFieldType_2 = null;
  private String get_BadFieldType_2() {
    if (cached_BadFieldType_2 != null) {
      return cached_BadFieldType_2;
    }
    cached_BadFieldType_2 = generateTestOutput(false, "BadFieldType_2", "./test_code/LinqUnique_BadFieldType_failure.a");
    return cached_BadFieldType_2;
  }

  @Test
  public void testBadFieldTypeFailure() {
    assertLiveFail(get_BadFieldType_2());
  }

  @Test
  public void testBadFieldTypeNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_BadFieldType_2());
  }

  @Test
  public void testBadFieldTypeExceptionFree() {
    assertExceptionFree(get_BadFieldType_2());
  }

  @Test
  public void testBadFieldTypeTODOFree() {
    assertTODOFree(get_BadFieldType_2());
  }

  @Test
  public void stable_BadFieldType_2() {
    String live = get_BadFieldType_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:LinqUnique_BadFieldType_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":17,\"character\":21,\"byte\":186},\"end\":{\"line\":17,\"character\":45,\"byte\":210}},\"severity\":1,\"source\":\"error\",\"message\":\"the key 'x' must be capable of being compared, hashed, and equality tested for uniqueness\",\"file\":\"./test_code/LinqUnique_BadFieldType_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_BadKey_3 = null;
  private String get_BadKey_3() {
    if (cached_BadKey_3 != null) {
      return cached_BadKey_3;
    }
    cached_BadKey_3 = generateTestOutput(false, "BadKey_3", "./test_code/LinqUnique_BadKey_failure.a");
    return cached_BadKey_3;
  }

  @Test
  public void testBadKeyFailure() {
    assertLiveFail(get_BadKey_3());
  }

  @Test
  public void testBadKeyNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_BadKey_3());
  }

  @Test
  public void testBadKeyExceptionFree() {
    assertExceptionFree(get_BadKey_3());
  }

  @Test
  public void testBadKeyTODOFree() {
    assertTODOFree(get_BadKey_3());
  }

  @Test
  public void stable_BadKey_3() {
    String live = get_BadKey_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:LinqUnique_BadKey_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":16,\"character\":21,\"byte\":167},\"end\":{\"line\":16,\"character\":45,\"byte\":191}},\"severity\":1,\"source\":\"error\",\"message\":\"the key 'x' is not a field of 'X'\",\"file\":\"./test_code/LinqUnique_BadKey_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_BadThing_4 = null;
  private String get_BadThing_4() {
    if (cached_BadThing_4 != null) {
      return cached_BadThing_4;
    }
    cached_BadThing_4 = generateTestOutput(false, "BadThing_4", "./test_code/LinqUnique_BadThing_failure.a");
    return cached_BadThing_4;
  }

  @Test
  public void testBadThingFailure() {
    assertLiveFail(get_BadThing_4());
  }

  @Test
  public void testBadThingNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_BadThing_4());
  }

  @Test
  public void testBadThingExceptionFree() {
    assertExceptionFree(get_BadThing_4());
  }

  @Test
  public void testBadThingTODOFree() {
    assertTODOFree(get_BadThing_4());
  }

  @Test
  public void stable_BadThing_4() {
    String live = get_BadThing_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:LinqUnique_BadThing_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":3,\"character\":1,\"byte\":31}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: expected an list, but was actually type 'X'.\",\"file\":\"./test_code/LinqUnique_BadThing_failure.a\"},{\"range\":{\"start\":{\"line\":7,\"character\":22,\"byte\":61},\"end\":{\"line\":7,\"character\":30,\"byte\":69}},\"severity\":1,\"source\":\"error\",\"message\":\"unique requires a list\",\"file\":\"./test_code/LinqUnique_BadThing_failure.a\"},{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":3,\"character\":1,\"byte\":31}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: expected an list, but was actually type 'X'.\",\"file\":\"./test_code/LinqUnique_BadThing_failure.a\"},{\"range\":{\"start\":{\"line\":8,\"character\":22,\"byte\":93},\"end\":{\"line\":8,\"character\":38,\"byte\":109}},\"severity\":1,\"source\":\"error\",\"message\":\"unique with a key requires the list to contain records or messages\",\"file\":\"./test_code/LinqUnique_BadThing_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_Happy_5 = null;
  private String get_Happy_5() {
    if (cached_Happy_5 != null) {
      return cached_Happy_5;
    }
    cached_Happy_5 = generateTestOutput(true, "Happy_5", "./test_code/LinqUnique_Happy_success.a");
    return cached_Happy_5;
  }

  @Test
  public void testHappyEmission() {
    assertEmissionGood(get_Happy_5());
  }

  @Test
  public void testHappySuccess() {
    assertLivePass(get_Happy_5());
  }

  @Test
  public void testHappyNoFormatException() {
    assertNoFormatException(get_Happy_5());
  }

  @Test
  public void testHappyGoodWillHappy() {
    assertGoodWillHappy(get_Happy_5());
  }

  @Test
  public void testHappyExceptionFree() {
    assertExceptionFree(get_Happy_5());
  }

  @Test
  public void testHappyTODOFree() {
    assertTODOFree(get_Happy_5());
  }

  @Test
  public void stable_Happy_5() {
    String live = get_Happy_5();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:LinqUnique_Happy_success.a");
    gold.append("\n--EMISSION-----------------------------------------");
    gold.append("\nEmission Success, Yay");
    gold.append("\n=FORMAT===================================================");
    gold.append("\nrecord X {");
    gold.append("\n  int id;");
    gold.append("\n  int z;");
    gold.append("\n  int x;");
    gold.append("\n}");
    gold.append("\ntable<X> t;");
    gold.append("\n@construct {");
    gold.append("\n  t <- {z:0};");
    gold.append("\n  t <- {z:1};");
    gold.append("\n  t <- {z:2};");
    gold.append("\n  t <- {z:5};");
    gold.append("\n  t <- {z:0};");
    gold.append("\n  t <- {z:0};");
    gold.append("\n  t <- {z:5};");
    gold.append("\n}");
    gold.append("\npublic formula z_first = iterate t unique first z;");
    gold.append("\npublic formula z_last = iterate t unique last z;");
    gold.append("\n");
    gold.append("\n==========================================================");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[]\"--JAVA---------------------------------------------");
    gold.append("\nimport org.adamalang.runtime.async.*;");
    gold.append("\nimport org.adamalang.runtime.contracts.*;");
    gold.append("\nimport org.adamalang.runtime.delta.*;");
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
    gold.append("\nimport org.adamalang.runtime.sys.cron.*;");
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
    gold.append("\npublic class Happy_5 extends LivingDocument {");
    gold.append("\n  private final RxTable<RTxX> t;");
    gold.append("\n  private final RxLazy<NtList<RTxX>> z_first;");
    gold.append("\n  private final RxTableGuard __z_first_t;");
    gold.append("\n  private final RxLazy<NtList<RTxX>> z_last;");
    gold.append("\n  private final RxTableGuard __z_last_t;");
    gold.append("\n  @Override");
    gold.append("\n  public long __memory() {");
    gold.append("\n    long __sum = super.__memory();");
    gold.append("\n    __sum += t.__memory();");
    gold.append("\n    __sum += z_first.__memory();");
    gold.append("\n    __sum += z_last.__memory();");
    gold.append("\n    return __sum;");
    gold.append("\n  }");
    gold.append("\n  public Happy_5(DocumentMonitor __monitor) {");
    gold.append("\n    super(__monitor);");
    gold.append("\n    t = new RxTable<>(__self, this, \"t\", (RxParent __parent) -> new RTxX(__parent).__link(), 0);");
    gold.append("\n    z_first = new RxLazy<NtList<RTxX>>(this, () -> (NtList<RTxX>)(t.iterate(false).unique(ListUniqueMode.First, (__x) -> __x.z.get())), null);");
    gold.append("\n    __z_first_t = new RxTableGuard(z_first);");
    gold.append("\n    z_last = new RxLazy<NtList<RTxX>>(this, () -> (NtList<RTxX>)(t.iterate(false).unique(ListUniqueMode.Last, (__x) -> __x.z.get())), null);");
    gold.append("\n    __z_last_t = new RxTableGuard(z_last);");
    gold.append("\n    t.__subscribe(__z_first_t);");
    gold.append("\n    z_first.__guard(t,__z_first_t);");
    gold.append("\n    t.__subscribe(__z_last_t);");
    gold.append("\n    z_last.__guard(t,__z_last_t);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __settle(Set<Integer> __viewers) {");
    gold.append("\n    t.__settle(__viewers);");
    gold.append("\n    z_first.__settle(__viewers);");
    gold.append("\n    __z_first_t.__settle(__viewers);");
    gold.append("\n    z_last.__settle(__viewers);");
    gold.append("\n    __z_last_t.__settle(__viewers);");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __insert(JsonStreamReader __reader) {");
    gold.append("\n    if (__reader.startObject()) {");
    gold.append("\n      while(__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"t\":");
    gold.append("\n            t.__insert(__reader);");
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
    gold.append("\n          case \"__enqueued\":");
    gold.append("\n            __hydrateEnqueuedTaskManager(__reader);");
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
    gold.append("\n          case \"t\":");
    gold.append("\n            t.__patch(__reader);");
    gold.append("\n            break;");
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
    gold.append("\n          case \"__enqueued\":");
    gold.append("\n            __hydrateEnqueuedTaskManager(__reader);");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"__snapshot\");");
    gold.append("\n    __writer.writeString(__space + \"/\" + __key);__writer.writeObjectFieldIntro(\"t\");");
    gold.append("\n    t.__dump(__writer);");
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
    gold.append("\n    __dumpEnqueuedTaskManager(__writer);");
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
    gold.append("\n    t.__commit(\"t\", __forward, __reverse);");
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
    gold.append("\n    t.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __writeRxReport(JsonStreamWriter __writer) { }");
    gold.append("\n  public class DeltaPrivacyCache {");
    gold.append("\n    public DeltaPrivacyCache(NtPrincipal __who) {}");
    gold.append("\n  }");
    gold.append("\n  private class DeltaHappy_5 implements DeltaNode {");
    gold.append("\n    private int __gz_first;");
    gold.append("\n    private DRecordList<DeltaRTxX> __dz_first;");
    gold.append("\n    private int __gz_last;");
    gold.append("\n    private DRecordList<DeltaRTxX> __dz_last;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaHappy_5() {");
    gold.append("\n      __gz_first = -1;");
    gold.append("\n      __dz_first = new DRecordList<DeltaRTxX>();");
    gold.append("\n      __gz_last = -1;");
    gold.append("\n      __dz_last = new DRecordList<DeltaRTxX>();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      __sum += __dz_first.__memory();");
    gold.append("\n      __sum += __dz_last.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public boolean show(Happy_5 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      DeltaPrivacyCache __policy_cache = new DeltaPrivacyCache(__writer.who);");
    gold.append("\n      __writer.setCacheObject(__policy_cache);");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __obj.manifest();");
    gold.append("\n      if (__gz_first != __item.z_first.getGeneration()) {");
    gold.append("\n        {");
    gold.append("\n          PrivateLazyDeltaWriter __list7 = __obj.planField(\"z_first\").planObject();");
    gold.append("\n          DRecordList<DeltaRTxX> __deltaList8 = __dz_first;");
    gold.append("\n          DRecordList<DeltaRTxX>.Walk __deltaListWalker9 = __deltaList8.begin();");
    gold.append("\n          for (RTxX __listElement10 : __item.z_first.get()) {");
    gold.append("\n            DeltaRTxX __deltaElement11 = __deltaList8.getPrior(__listElement10.__id(), () -> new DeltaRTxX());");
    gold.append("\n            boolean __gate12 = __deltaElement11.show(__listElement10, __list7.planField(__listElement10.__id()));");
    gold.append("\n            if (__gate12) {");
    gold.append("\n              __deltaListWalker9.next(__listElement10.__id());");
    gold.append("\n            }");
    gold.append("\n          }");
    gold.append("\n          __deltaListWalker9.end(__list7);");
    gold.append("\n          __list7.end();");
    gold.append("\n        }");
    gold.append("\n        __gz_first = __item.z_first.getGeneration();");
    gold.append("\n      }");
    gold.append("\n      if (__gz_last != __item.z_last.getGeneration()) {");
    gold.append("\n        {");
    gold.append("\n          PrivateLazyDeltaWriter __list13 = __obj.planField(\"z_last\").planObject();");
    gold.append("\n          DRecordList<DeltaRTxX> __deltaList14 = __dz_last;");
    gold.append("\n          DRecordList<DeltaRTxX>.Walk __deltaListWalker15 = __deltaList14.begin();");
    gold.append("\n          for (RTxX __listElement16 : __item.z_last.get()) {");
    gold.append("\n            DeltaRTxX __deltaElement17 = __deltaList14.getPrior(__listElement16.__id(), () -> new DeltaRTxX());");
    gold.append("\n            boolean __gate18 = __deltaElement17.show(__listElement16, __list13.planField(__listElement16.__id()));");
    gold.append("\n            if (__gate18) {");
    gold.append("\n              __deltaListWalker15.next(__listElement16.__id());");
    gold.append("\n            }");
    gold.append("\n          }");
    gold.append("\n          __deltaListWalker15.end(__list13);");
    gold.append("\n          __list13.end();");
    gold.append("\n        }");
    gold.append("\n        __gz_last = __item.z_last.getGeneration();");
    gold.append("\n      }");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n      return true;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __dz_first.clear();");
    gold.append("\n      __dz_last.clear();");
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
    gold.append("\n  @Override");
    gold.append("\n  public Set<String> __get_intern_strings() {");
    gold.append("\n    HashSet<String> __interns = new HashSet<>();");
    gold.append("\n    __interns.add(\"\");");
    gold.append("\n    __interns.add(\"?\");");
    gold.append("\n    return __interns;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public PrivateView __createPrivateView(NtPrincipal __who, Perspective ___perspective) {");
    gold.append("\n    Happy_5 __self = this;");
    gold.append("\n    DeltaHappy_5 __state = new DeltaHappy_5();");
    gold.append("\n    RTx__ViewerType __viewerState = new RTx__ViewerType();");
    gold.append("\n    int __viewId = __genViewId();");
    gold.append("\n    return new PrivateView(__viewId, __who, ___perspective) {");
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
    gold.append("\n        __state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer, __viewerState, __viewId));");
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
    gold.append("\n  private static String[] __INDEX_COLUMNS_X = new String[] {};");
    gold.append("\n  private class RTxX extends RxRecordBase<RTxX> {");
    gold.append("\n    private final RTxX __this;");
    gold.append("\n    private final RxInt32 id;");
    gold.append("\n    private final RxInt32 z;");
    gold.append("\n    private final RxInt32 x;");
    gold.append("\n    private RTxX(RxParent __owner) {");
    gold.append("\n      super(__owner);");
    gold.append("\n      this.__this = this;");
    gold.append("\n      id = new RxInt32(this, 0);");
    gold.append("\n      z = new RxInt32(this, 0);");
    gold.append("\n      x = new RxInt32(this, 0);");
    gold.append("\n      if (__owner instanceof RxTable) {");
    gold.append("\n        /* ok */");
    gold.append("\n      } else {");
    gold.append("\n        /* ok */");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = super.__memory();");
    gold.append("\n      __sum += id.__memory();");
    gold.append("\n      __sum += z.__memory();");
    gold.append("\n      __sum += x.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS_X;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int[] __getIndexValues() {");
    gold.append("\n      return new int[] {};");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public Object __fieldOf(String __name) {");
    gold.append("\n      switch (__name) {");
    gold.append("\n        case \"id\":");
    gold.append("\n          return id;");
    gold.append("\n        case \"z\":");
    gold.append("\n          return z;");
    gold.append("\n        case \"x\":");
    gold.append("\n          return x;");
    gold.append("\n        default:");
    gold.append("\n          return null;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __settle(Set<Integer> __viewers) {");
    gold.append("\n      __lowerInvalid();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __insert(JsonStreamReader __reader) {");
    gold.append("\n      if (__reader.startObject()) {");
    gold.append("\n        while(__reader.notEndOfObject()) {");
    gold.append("\n          String __fieldName = __reader.fieldName();");
    gold.append("\n          switch (__fieldName) {");
    gold.append("\n            case \"id\":");
    gold.append("\n              id.__insert(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"z\":");
    gold.append("\n              z.__insert(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"x\":");
    gold.append("\n              x.__insert(__reader);");
    gold.append("\n              break;");
    gold.append("\n            default:");
    gold.append("\n              __reader.skipValue();");
    gold.append("\n          }");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __patch(JsonStreamReader __reader) {");
    gold.append("\n      if (__reader.startObject()) {");
    gold.append("\n        while(__reader.notEndOfObject()) {");
    gold.append("\n          String __fieldName = __reader.fieldName();");
    gold.append("\n          switch (__fieldName) {");
    gold.append("\n            case \"id\":");
    gold.append("\n              id.__patch(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"z\":");
    gold.append("\n              z.__patch(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"x\":");
    gold.append("\n              x.__patch(__reader);");
    gold.append("\n              break;");
    gold.append("\n            default:");
    gold.append("\n              __reader.skipValue();");
    gold.append("\n          }");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __dump(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.writeObjectFieldIntro(\"id\");");
    gold.append("\n      id.__dump(__writer);");
    gold.append("\n      __writer.writeObjectFieldIntro(\"z\");");
    gold.append("\n      z.__dump(__writer);");
    gold.append("\n      __writer.writeObjectFieldIntro(\"x\");");
    gold.append("\n      x.__dump(__writer);");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __commit(String __name, JsonStreamWriter __forward, JsonStreamWriter __reverse) {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        __forward.writeObjectFieldIntro(__name);");
    gold.append("\n        __forward.beginObject();");
    gold.append("\n        __reverse.writeObjectFieldIntro(__name);");
    gold.append("\n        __reverse.beginObject();");
    gold.append("\n        id.__commit(\"id\", __forward, __reverse);");
    gold.append("\n        z.__commit(\"z\", __forward, __reverse);");
    gold.append("\n        x.__commit(\"x\", __forward, __reverse);");
    gold.append("\n        __forward.endObject();");
    gold.append("\n        __reverse.endObject();");
    gold.append("\n        __lowerDirtyCommit();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __revert() {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        __isDying = false;");
    gold.append("\n        id.__revert();");
    gold.append("\n        z.__revert();");
    gold.append("\n        x.__revert();");
    gold.append("\n        __lowerDirtyRevert();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __killFields() {}");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeRxReport(JsonStreamWriter __writer) { }");
    gold.append("\n    @Override");
    gold.append("\n    public RTxX __link() {");
    gold.append("\n      return this;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __invalidateIndex(TablePubSub __pubsub) {}");
    gold.append("\n    @Override");
    gold.append("\n    public void __pumpIndexEvents(TablePubSub __pubsub) {}");
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
    gold.append("\n    @Override");
    gold.append("\n    public void __setId(int __id, boolean __force) {");
    gold.append("\n      if (__force) {");
    gold.append("\n        id.forceSet(__id);");
    gold.append("\n      } else {");
    gold.append("\n        id.set(__id);");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTxX implements DeltaNode {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTxX() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public boolean show(RTxX __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      DeltaPrivacyCache __policy_cache = (DeltaPrivacyCache) __writer.getCacheObject();");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
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
    gold.append("\n  class DynCmp_RTxX implements Comparator<RTxX> {");
    gold.append("\n    private final CompareField[] parsed;");
    gold.append("\n    DynCmp_RTxX(String instructions) {");
    gold.append("\n      this.parsed = DynCompareParser.parse(instructions);");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int compare(RTxX __a, RTxX __b) {");
    gold.append("\n      for (CompareField field : parsed) {");
    gold.append("\n        int delta = 0;");
    gold.append("\n        switch (field.name) {");
    gold.append("\n          case \"id\":");
    gold.append("\n            delta = __a.id.compareTo(__b.id);");
    gold.append("\n            break;");
    gold.append("\n          case \"z\":");
    gold.append("\n            delta = __a.z.compareTo(__b.z);");
    gold.append("\n            break;");
    gold.append("\n          case \"x\":");
    gold.append("\n            delta = __a.x.compareTo(__b.x);");
    gold.append("\n            break;");
    gold.append("\n        }");
    gold.append("\n        if (delta != 0) {");
    gold.append("\n          return field.desc ? -delta : delta;");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n      return 0;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private static class RTx_AnonObjConvert_0 extends NtMessageBase {");
    gold.append("\n    private final RTx_AnonObjConvert_0 __this;");
    gold.append("\n    private int z = 0;");
    gold.append("\n    public void __hash(HashBuilder __hash) {");
    gold.append("\n      __hash.hashString(\"z\");");
    gold.append("\n      __hash.hashInteger(this.z);");
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
    gold.append("\n          case \"z\":");
    gold.append("\n            this.z = __reader.readInteger();");
    gold.append("\n            break;");
    gold.append("\n          default:");
    gold.append("\n            __reader.skipValue();");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.writeObjectFieldIntro(\"z\");");
    gold.append("\n      __writer.writeInteger(z);");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_0() { __this = this; }");
    gold.append("\n    private RTx_AnonObjConvert_0(int z) {");
    gold.append("\n      this.__this = this;");
    gold.append("\n      this.z = z;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTx_AnonObjConvert_0 implements DeltaNode {");
    gold.append("\n    private DInt32 __dz;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTx_AnonObjConvert_0() {");
    gold.append("\n      __dz = new DInt32();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public long __memory() {");
    gold.append("\n      long __sum = 40;");
    gold.append("\n      __sum += __dz.__memory();");
    gold.append("\n      return __sum;");
    gold.append("\n    }");
    gold.append("\n    public void show(RTx_AnonObjConvert_0 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      __code_cost += 1;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __dz.show(__item.z, __obj.planField(\"z\"));");
    gold.append("\n      if (__obj.end()) {");
    gold.append("\n        __emitted = true;");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void clear() {");
    gold.append("\n      __dz.clear();");
    gold.append("\n      __code_cost += 1;");
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
    gold.append("\n  public String __traffic(CoreRequestContext __context) { return \"main\"; }");
    gold.append("\n  @Override");
    gold.append("\n  public void __debug(JsonStreamWriter __writer) {");
    gold.append("\n    __writer.writeObjectFieldIntro(\"tables\");");
    gold.append("\n    __writer.beginObject();");
    gold.append("\n    __writer.writeObjectFieldIntro(\"t\");");
    gold.append("\n    t.debug(__writer);");
    gold.append("\n    __writer.endObject();");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __open_channel(String name) {");
    gold.append("\n    return false;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String __auth(CoreRequestContext __context, String username, String password) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  public AuthResponse __authpipe(CoreRequestContext __context, String __message) {");
    gold.append("\n    return null;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __password(CoreRequestContext __context, String __pw) {}");
    gold.append("\n  @Override");
    gold.append("\n  public void __make_cron_progress() {}");
    gold.append("\n  @Override");
    gold.append("\n  protected void __reset_cron() {}");
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
    gold.append("\n  public void __test(TestReportBuilder report, String testName) throws AbortMessageException {}");
    gold.append("\n  private void __construct_0(CoreRequestContext __context, NtPrincipal __who, NtMessageBase __object) {");
    gold.append("\n    __code_cost += 8;");
    gold.append("\n    __track(0);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef19 = t;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr21 = new RTx_AnonObjConvert_0(0);");
    gold.append("\n      RTxX _CreateRef20 = _AutoRef19.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef20.z.set(_AutoExpr21.z);");
    gold.append("\n    }");
    gold.append("\n    __track(1);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef22 = t;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr24 = new RTx_AnonObjConvert_0(1);");
    gold.append("\n      RTxX _CreateRef23 = _AutoRef22.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef23.z.set(_AutoExpr24.z);");
    gold.append("\n    }");
    gold.append("\n    __track(2);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef25 = t;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr27 = new RTx_AnonObjConvert_0(2);");
    gold.append("\n      RTxX _CreateRef26 = _AutoRef25.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef26.z.set(_AutoExpr27.z);");
    gold.append("\n    }");
    gold.append("\n    __track(3);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef28 = t;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr30 = new RTx_AnonObjConvert_0(5);");
    gold.append("\n      RTxX _CreateRef29 = _AutoRef28.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef29.z.set(_AutoExpr30.z);");
    gold.append("\n    }");
    gold.append("\n    __track(4);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef31 = t;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr33 = new RTx_AnonObjConvert_0(0);");
    gold.append("\n      RTxX _CreateRef32 = _AutoRef31.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef32.z.set(_AutoExpr33.z);");
    gold.append("\n    }");
    gold.append("\n    __track(5);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef34 = t;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr36 = new RTx_AnonObjConvert_0(0);");
    gold.append("\n      RTxX _CreateRef35 = _AutoRef34.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef35.z.set(_AutoExpr36.z);");
    gold.append("\n    }");
    gold.append("\n    __track(6);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef37 = t;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr39 = new RTx_AnonObjConvert_0(5);");
    gold.append("\n      RTxX _CreateRef38 = _AutoRef37.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef38.z.set(_AutoExpr39.z);");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected NtMessageBase __parse_construct_arg(JsonStreamReader __reader) {");
    gold.append("\n    __reader.skipValue();");
    gold.append("\n    return NtMessageBase.NULL;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  protected void __construct_intern(CoreRequestContext __context, NtMessageBase __object) {");
    gold.append("\n    __construct_0(__context, __context.who, __object);");
    gold.append("\n  }");
    gold.append("\n  /* end of file */");
    gold.append("\n}");
    gold.append("\n");
    gold.append("\n--=[LivingDocumentFactory COMPILING]=---");
    gold.append("\n--=[LivingDocumentFactory MADE]=---");
    gold.append("\n--REFLECTION RESULTS-------------------------------------");
    gold.append("\n{\"types\":{\"__Root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{\"t\":{\"type\":{\"nature\":\"reactive_table\",\"record_name\":\"X\"},\"privacy\":\"private\"},\"z_first\":{\"type\":{\"nature\":\"native_list\",\"type\":{\"nature\":\"reactive_ref\",\"ref\":\"X\"}},\"privacy\":\"public\"},\"z_last\":{\"type\":{\"nature\":\"native_list\",\"type\":{\"nature\":\"reactive_ref\",\"ref\":\"X\"}},\"privacy\":\"public\"}}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}},\"X\":{\"nature\":\"reactive_record\",\"name\":\"X\",\"fields\":{\"id\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"private\"},\"z\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"private\"},\"x\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"private\"}}},\"_AnonObjConvert_0\":{\"nature\":\"native_message\",\"name\":\"_AnonObjConvert_0\",\"anonymous\":true,\"fields\":{\"z\":{\"type\":{\"nature\":\"native_value\",\"type\":\"int\"},\"privacy\":\"public\"}}}},\"channels\":{},\"channels-privacy\":{},\"constructors\":[],\"labels\":[]}");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\",\"key\":\"0\",\"origin\":\"origin\",\"ip\":\"ip\"}-->{\"__constructed\":true,\"__entropy\":\"-4962768465676381896\",\"__auto_table_row_id\":7,\"t\":{\"1\":{\"id\":1,\"z\":0,\"x\":0},\"2\":{\"id\":2,\"z\":1,\"x\":0},\"3\":{\"id\":3,\"z\":2,\"x\":0},\"4\":{\"id\":4,\"z\":5,\"x\":0},\"5\":{\"id\":5,\"z\":0,\"x\":0},\"6\":{\"id\":6,\"z\":0,\"x\":0},\"7\":{\"id\":7,\"z\":5,\"x\":0}},\"__messages\":null,\"__seq\":1} need:false in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"25\"} need:false in:0");
    gold.append("\nCPU:22");
    gold.append("\nMEMORY:4316");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seq\":3,\"__entropy\":\"-1034601897293430941\",\"__time\":\"50\"} need:false in:0");
    gold.append("\nNO_ONE: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{\"z_first\":{\"@o\":[1,2,3,4]},\"z_last\":{\"@o\":[6,2,3,7]}},\"seq\":3}");
    gold.append("\nNO_ONE|FAILURE:184333");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"75\"}-->{\"__messages\":null,\"__seq\":4,\"__entropy\":\"7848011421992302230\",\"__time\":\"75\"} need:false in:0");
    gold.append("\nRANDO: CREATED PRIVATE VIEW");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":4}");
    gold.append("\n+ RANDO DELTA:{\"data\":{\"z_first\":{\"@o\":[1,2,3,4]},\"z_last\":{\"@o\":[6,2,3,7]}},\"seq\":4}");
    gold.append("\nRANDO|FAILURE:184333");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__time\":\"100\"} need:false in:0");
    gold.append("\nRANDO|SUCCESS:5");
    gold.append("\n+ NO_ONE DELTA:{\"seq\":5}");
    gold.append("\n+ RANDO DELTA:{\"seq\":5}");
    gold.append("\nMEMORY:6226");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__snapshot\":\"0/0\",\"t\":{\"1\":{\"id\":1,\"z\":0,\"x\":0},\"2\":{\"id\":2,\"z\":1,\"x\":0},\"3\":{\"id\":3,\"z\":2,\"x\":0},\"4\":{\"id\":4,\"z\":5,\"x\":0},\"5\":{\"id\":5,\"z\":0,\"x\":0},\"6\":{\"id\":6,\"z\":0,\"x\":0},\"7\":{\"id\":7,\"z\":5,\"x\":0}},\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":7,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{}");
    gold.append("\n--METRIC RESULTS-----------------------------------");
    gold.append("\n{\"__snapshot\":\"0/0\",\"t\":{\"1\":{\"id\":1,\"z\":0,\"x\":0},\"2\":{\"id\":2,\"z\":1,\"x\":0},\"3\":{\"id\":3,\"z\":2,\"x\":0},\"4\":{\"id\":4,\"z\":5,\"x\":0},\"5\":{\"id\":5,\"z\":0,\"x\":0},\"6\":{\"id\":6,\"z\":0,\"x\":0},\"7\":{\"id\":7,\"z\":5,\"x\":0}},\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":7,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n{\"__snapshot\":\"0/0\",\"t\":{\"1\":{\"id\":1,\"z\":0,\"x\":0},\"2\":{\"id\":2,\"z\":1,\"x\":0},\"3\":{\"id\":3,\"z\":2,\"x\":0},\"4\":{\"id\":4,\"z\":5,\"x\":0},\"5\":{\"id\":5,\"z\":0,\"x\":0},\"6\":{\"id\":6,\"z\":0,\"x\":0},\"7\":{\"id\":7,\"z\":5,\"x\":0}},\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"100\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":7,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__webqueue\":{},\"__replication\":{}}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_MissingKey_6 = null;
  private String get_MissingKey_6() {
    if (cached_MissingKey_6 != null) {
      return cached_MissingKey_6;
    }
    cached_MissingKey_6 = generateTestOutput(false, "MissingKey_6", "./test_code/LinqUnique_MissingKey_failure.a");
    return cached_MissingKey_6;
  }

  @Test
  public void testMissingKeyFailure() {
    assertLiveFail(get_MissingKey_6());
  }

  @Test
  public void testMissingKeyNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_MissingKey_6());
  }

  @Test
  public void testMissingKeyExceptionFree() {
    assertExceptionFree(get_MissingKey_6());
  }

  @Test
  public void testMissingKeyTODOFree() {
    assertTODOFree(get_MissingKey_6());
  }

  @Test
  public void stable_MissingKey_6() {
    String live = get_MissingKey_6();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:LinqUnique_MissingKey_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":18,\"character\":21,\"byte\":190},\"end\":{\"line\":18,\"character\":37,\"byte\":206}},\"severity\":1,\"source\":\"error\",\"message\":\"the element has a type of'X' which is not capable of being compared, hashed, and equality tested for uniqueness\",\"file\":\"./test_code/LinqUnique_MissingKey_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

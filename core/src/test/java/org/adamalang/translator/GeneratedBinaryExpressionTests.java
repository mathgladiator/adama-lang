/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedBinaryExpressionTests extends GeneratedBase {
  private String cached_AggregateMath_1 = null;
  private String get_AggregateMath_1() {
    if (cached_AggregateMath_1 != null) {
      return cached_AggregateMath_1;
    }
    cached_AggregateMath_1 = generateTestOutput(true, "AggregateMath_1", "./test_code/BinaryExpression_AggregateMath_success.a");
    return cached_AggregateMath_1;
  }

  @Test
  public void testAggregateMathEmission() {
    assertEmissionGood(get_AggregateMath_1());
  }

  @Test
  public void testAggregateMathSuccess() {
    assertLivePass(get_AggregateMath_1());
  }

  @Test
  public void testAggregateMathGoodWillHappy() {
    assertGoodWillHappy(get_AggregateMath_1());
  }

  @Test
  public void testAggregateMathExceptionFree() {
    assertExceptionFree(get_AggregateMath_1());
  }

  @Test
  public void testAggregateMathTODOFree() {
    assertTODOFree(get_AggregateMath_1());
  }

  @Test
  public void stable_AggregateMath_1() {
    String live = get_AggregateMath_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BinaryExpression_AggregateMath_success.a");
    gold.append("\n--EMISSION-----------------------------------------");
    gold.append("\nEmission Success, Yay");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[]\"--JAVA---------------------------------------------");
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
    gold.append("\nimport org.adamalang.runtime.sys.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.util.HashSet;");
    gold.append("\nimport java.util.Map;");
    gold.append("\nimport java.util.Set;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class AggregateMath_1 extends LivingDocument {");
    gold.append("\n  private final RxTable<RTxX> t;");
    gold.append("\n  public AggregateMath_1(DocumentMonitor __monitor) {");
    gold.append("\n    super(__monitor);");
    gold.append("\n    t = new RxTable<>(__self, this, \"t\", (RxParent __parent) -> new RTxX(__parent), 0);");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
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
    gold.append("\n          case \"__auto_table_row_id\":");
    gold.append("\n            __auto_table_row_id.__insert(__reader);");
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
    gold.append("\n          case \"__auto_table_row_id\":");
    gold.append("\n            __auto_table_row_id.__patch(__reader);");
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
    gold.append("\n          default:");
    gold.append("\n            __reader.skipValue();");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __dump(JsonStreamWriter __writer) {");
    gold.append("\n    __writer.beginObject();");
    gold.append("\n    __writer.writeObjectFieldIntro(\"t\");");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_table_row_id\");");
    gold.append("\n    __auto_table_row_id.__dump(__writer);");
    gold.append("\n    __dumpDeduper(__writer);");
    gold.append("\n    __dumpClients(__writer);");
    gold.append("\n    __dumpMessages(__writer);");
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
    gold.append("\n    __auto_table_row_id.__commit(\"__auto_table_row_id\", __forward, __reverse);");
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
    gold.append("\n    __auto_table_row_id.__revert();");
    gold.append("\n    t.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  private class DeltaAggregateMath_1 {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaAggregateMath_1() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(AggregateMath_1 __item, PrivateLazyDeltaWriter __writer) {");
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
    gold.append("\n  public Set<String> __get_intern_strings() {");
    gold.append("\n    HashSet<String> __interns = new HashSet<>();");
    gold.append("\n    __interns.add(\"\");");
    gold.append("\n    __interns.add(\"?\");");
    gold.append("\n    return __interns;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public PrivateView __createPrivateView(NtClient __who, Perspective ___perspective) {");
    gold.append("\n    AggregateMath_1 __self = this;");
    gold.append("\n    DeltaAggregateMath_1 __state = new DeltaAggregateMath_1();");
    gold.append("\n    RTx__ViewerType __viewerState = new RTx__ViewerType();");
    gold.append("\n    return new PrivateView(__who, ___perspective) {");
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
    gold.append("\n        __state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer, __viewerState));");
    gold.append("\n      }");
    gold.append("\n    };");
    gold.append("\n  }");
    gold.append("\n  private static class RTx__ViewerType implements NtMessageBase {");
    gold.append("\n    private RTx__ViewerType(JsonStreamReader __reader) {");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    public int __DATA_GENERATION = 1;");
    gold.append("\n    private void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      __reader.skipValue();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType() {}");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTx__ViewerType {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTx__ViewerType() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(RTx__ViewerType __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
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
    gold.append("\n  private static String[] __INDEX_COLUMNS_X = new String[] {\"i\"};");
    gold.append("\n  private class RTxX extends RxRecordBase<RTxX> {");
    gold.append("\n    private final RxInt32 i;");
    gold.append("\n    private final RxDouble d;");
    gold.append("\n    private final RxString s;");
    gold.append("\n    private final RxInt32 id;");
    gold.append("\n    private RTxX(RxParent __owner) {");
    gold.append("\n      super(__owner);");
    gold.append("\n      i = new RxInt32(this, 1);");
    gold.append("\n      d = new RxDouble(this, 1);");
    gold.append("\n      s = new RxString(this, \"x\");");
    gold.append("\n      id = new RxInt32(this, 0);");
    gold.append("\n      if (__owner instanceof RxTable) {");
    gold.append("\n        /* ok */");
    gold.append("\n      } else {");
    gold.append("\n        /* ok */");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public String[] __getIndexColumns() {");
    gold.append("\n      return __INDEX_COLUMNS_X;");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public int[] __getIndexValues() {");
    gold.append("\n      return new int[] {i.getIndexValue()};");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __insert(JsonStreamReader __reader) {");
    gold.append("\n      if (__reader.startObject()) {");
    gold.append("\n        while(__reader.notEndOfObject()) {");
    gold.append("\n          String __fieldName = __reader.fieldName();");
    gold.append("\n          switch (__fieldName) {");
    gold.append("\n            case \"i\":");
    gold.append("\n              i.__insert(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"d\":");
    gold.append("\n              d.__insert(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"s\":");
    gold.append("\n              s.__insert(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"id\":");
    gold.append("\n              id.__insert(__reader);");
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
    gold.append("\n            case \"i\":");
    gold.append("\n              i.__patch(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"d\":");
    gold.append("\n              d.__patch(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"s\":");
    gold.append("\n              s.__patch(__reader);");
    gold.append("\n              break;");
    gold.append("\n            case \"id\":");
    gold.append("\n              id.__patch(__reader);");
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
    gold.append("\n      __writer.writeObjectFieldIntro(\"i\");");
    gold.append("\n      i.__dump(__writer);");
    gold.append("\n      __writer.writeObjectFieldIntro(\"d\");");
    gold.append("\n      d.__dump(__writer);");
    gold.append("\n      __writer.writeObjectFieldIntro(\"s\");");
    gold.append("\n      s.__dump(__writer);");
    gold.append("\n      __writer.writeObjectFieldIntro(\"id\");");
    gold.append("\n      id.__dump(__writer);");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __commit(String __name, JsonStreamWriter __forward, JsonStreamWriter __reverse) {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        __forward.writeObjectFieldIntro(__name);");
    gold.append("\n        __forward.beginObject();");
    gold.append("\n        __reverse.writeObjectFieldIntro(__name);");
    gold.append("\n        __reverse.beginObject();");
    gold.append("\n        i.__commit(\"i\", __forward, __reverse);");
    gold.append("\n        d.__commit(\"d\", __forward, __reverse);");
    gold.append("\n        s.__commit(\"s\", __forward, __reverse);");
    gold.append("\n        id.__commit(\"id\", __forward, __reverse);");
    gold.append("\n        __forward.endObject();");
    gold.append("\n        __reverse.endObject();");
    gold.append("\n        __lowerDirtyCommit();");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __revert() {");
    gold.append("\n      if (__isDirty()) {");
    gold.append("\n        __isDying = false;");
    gold.append("\n        i.__revert();");
    gold.append("\n        d.__revert();");
    gold.append("\n        s.__revert();");
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
    gold.append("\n    @Override");
    gold.append("\n    public void __setId(int __id, boolean __force) {");
    gold.append("\n      if (__force) {");
    gold.append("\n        id.forceSet(__id);");
    gold.append("\n      } else {");
    gold.append("\n        id.set(__id);");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTxX {");
    gold.append("\n    private DInt32 __di;");
    gold.append("\n    private DDouble __dd;");
    gold.append("\n    private DString __ds;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTxX() {");
    gold.append("\n      __di = new DInt32();");
    gold.append("\n      __dd = new DDouble();");
    gold.append("\n      __ds = new DString();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(RTxX __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      __code_cost += 3;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __di.show(__item.i.get(), __obj.planField(\"i\"));");
    gold.append("\n      __dd.show(__item.d.get(), __obj.planField(\"d\"));");
    gold.append("\n      __ds.show(__item.s.get(), __obj.planField(\"s\"));");
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
    gold.append("\n  private static class RTx_AnonObjConvert_0 implements NtMessageBase {");
    gold.append("\n    private int d = 0;");
    gold.append("\n    private int i = 0;");
    gold.append("\n    private RTx_AnonObjConvert_0(JsonStreamReader __reader) {");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    private void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      if (__reader.startObject()) {");
    gold.append("\n        while (__reader.notEndOfObject()) {");
    gold.append("\n          String __fieldName = __reader.fieldName();");
    gold.append("\n          switch (__fieldName) {");
    gold.append("\n            case \"d\":");
    gold.append("\n              this.d = __reader.readInteger();");
    gold.append("\n              break;");
    gold.append("\n            case \"i\":");
    gold.append("\n              this.i = __reader.readInteger();");
    gold.append("\n              break;");
    gold.append("\n            default:");
    gold.append("\n              __reader.skipValue();");
    gold.append("\n          }");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.writeObjectFieldIntro(\"d\");");
    gold.append("\n      __writer.writeInteger(d);");
    gold.append("\n      __writer.writeObjectFieldIntro(\"i\");");
    gold.append("\n      __writer.writeInteger(i);");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTx_AnonObjConvert_0() {}");
    gold.append("\n    private RTx_AnonObjConvert_0(int d, int i) {");
    gold.append("\n      this.d = d;");
    gold.append("\n      this.i = i;");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTx_AnonObjConvert_0 {");
    gold.append("\n    private DInt32 __dd;");
    gold.append("\n    private DInt32 __di;");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTx_AnonObjConvert_0() {");
    gold.append("\n      __dd = new DInt32();");
    gold.append("\n      __di = new DInt32();");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(RTx_AnonObjConvert_0 __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
    gold.append("\n      __dd.show(__item.d, __obj.planField(\"d\"));");
    gold.append("\n      __di.show(__item.i, __obj.planField(\"i\"));");
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
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onConnected(NtClient __cvalue) {");
    gold.append("\n    boolean __result = false;");
    gold.append("\n    return __result;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onDisconnected(NtClient __cvalue) {}");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onCanAssetAttached(NtClient __cvalue) {");
    gold.append("\n    boolean __result = false;");
    gold.append("\n    return __result;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onAssetAttached(NtClient __cvalue, NtAsset __asset) {}");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {}");
    gold.append("\n  private void __construct_0(NtClient __who, NtMessageBase __object) {");
    gold.append("\n    __code_cost += 11;");
    gold.append("\n    __track(0);");
    gold.append("\n    {");
    gold.append("\n      RxTable<RTxX> _AutoRef1 = t;");
    gold.append("\n      RTx_AnonObjConvert_0 _AutoExpr3 = new RTx_AnonObjConvert_0(3, 1);");
    gold.append("\n      RTxX _CreateRef2 = _AutoRef1.make();");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      _CreateRef2.d.set(_AutoExpr3.d);");
    gold.append("\n      _CreateRef2.i.set(_AutoExpr3.i);");
    gold.append("\n    }");
    gold.append("\n    __track(1);");
    gold.append("\n    NtList<RxInt32> _auto_4 = (t.iterate(true)).transform((item) -> item.i);");
    gold.append("\n    for (RxInt32 _auto_5 : _auto_4) {");
    gold.append("\n      _auto_5.opAddTo(2);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    __track(2);");
    gold.append("\n    NtList<RxInt32> _auto_6 = (t.iterate(true)).transform((item) -> item.i);");
    gold.append("\n    for (RxInt32 _auto_7 : _auto_6) {");
    gold.append("\n      _auto_7.opMultBy(5);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    __track(3);");
    gold.append("\n    NtList<RxInt32> _auto_8 = (t.iterate(true)).transform((item) -> item.i);");
    gold.append("\n    for (RxInt32 _auto_9 : _auto_8) {");
    gold.append("\n      _auto_9.opSubFrom(3);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    __track(4);");
    gold.append("\n    NtList<RxInt32> _auto_10 = (t.iterate(true)).transform((item) -> item.i);");
    gold.append("\n    for (RxInt32 _auto_11 : _auto_10) {");
    gold.append("\n      _auto_11.opModBy(5);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    __track(5);");
    gold.append("\n    NtList<RxDouble> _auto_12 = (t.iterate(true)).transform((item) -> item.d);");
    gold.append("\n    for (RxDouble _auto_13 : _auto_12) {");
    gold.append("\n      _auto_13.opAddTo(2);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    __track(6);");
    gold.append("\n    NtList<RxDouble> _auto_14 = (t.iterate(true)).transform((item) -> item.d);");
    gold.append("\n    for (RxDouble _auto_15 : _auto_14) {");
    gold.append("\n      _auto_15.opMultBy(5);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    __track(7);");
    gold.append("\n    NtList<RxDouble> _auto_16 = (t.iterate(true)).transform((item) -> item.d);");
    gold.append("\n    for (RxDouble _auto_17 : _auto_16) {");
    gold.append("\n      _auto_17.opSubFrom(3);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    __track(8);");
    gold.append("\n    NtList<RxDouble> _auto_18 = (t.iterate(true)).transform((item) -> item.d);");
    gold.append("\n    for (RxDouble _auto_19 : _auto_18) {");
    gold.append("\n      _auto_19.opDivBy(1.5);");
    gold.append("\n    }");
    gold.append("\n");
    gold.append("\n    __track(9);");
    gold.append("\n    NtList<RxString> _auto_20 = (t.iterate(true)).transform((item) -> item.s);");
    gold.append("\n    for (RxString _auto_21 : _auto_20) {");
    gold.append("\n      _auto_21.opAddTo(\"yz\");");
    gold.append("\n    }");
    gold.append("\n");
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
    gold.append("\nBegin");
    gold.append("\nEnd");
    gold.append("\n--REFLECTION RESULTS-------------------------------------");
    gold.append("\n{\"types\":{\"#root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}},\"X\":{\"nature\":\"reactive_record\",\"name\":\"X\",\"fields\":{\"i\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"public\"},\"d\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"double\"},\"privacy\":\"public\"},\"s\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"string\"},\"privacy\":\"public\"}}},\"_AnonObjConvert_0\":{\"nature\":\"native_message\",\"name\":\"_AnonObjConvert_0\",\"anonymous\":true,\"fields\":{\"d\":{\"type\":{\"nature\":\"native_value\",\"type\":\"int\"}},\"i\":{\"type\":{\"nature\":\"native_value\",\"type\":\"int\"}}}}},\"channels\":{},\"constructors\":[],\"labels\":[]}");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__constructed\":true,\"__entropy\":\"0\",\"__auto_table_row_id\":1,\"t\":{\"1\":{\"i\":2,\"d\":14.666666666666666,\"s\":\"xyz\",\"id\":1}}} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"50\"} need:false in:-50");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":2}");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"75\"}-->{\"__messages\":null,\"__seq\":3,\"__entropy\":\"-1034601897293430941\",\"__time\":\"75\"} need:false in:-75");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":3}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":3}");
    gold.append("\nRANDO was DENIED:");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seq\":4,\"__entropy\":\"7848011421992302230\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":4}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":4}");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"125\"}-->{\"__goodwill_used\":0,\"__cost\":13,\"__billing_seq\":4,\"__seq\":5,\"__time\":\"125\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"150\"}-->{\"__messages\":null,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__time\":\"150\"} need:false in:-150");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":6}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":6}");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"t\":{\"1\":{\"i\":2,\"d\":14.666666666666666,\"s\":\"xyz\",\"id\":1}},\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"150\",\"__auto_table_row_id\":1}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"t\":{\"1\":{\"i\":2,\"d\":14.666666666666666,\"s\":\"xyz\",\"id\":1}},\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"150\",\"__auto_table_row_id\":1}");
    gold.append("\n{\"t\":{\"1\":{\"i\":2,\"d\":14.666666666666666,\"s\":\"xyz\",\"id\":1}},\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"150\",\"__auto_table_row_id\":1}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_NumberCompare_2 = null;
  private String get_NumberCompare_2() {
    if (cached_NumberCompare_2 != null) {
      return cached_NumberCompare_2;
    }
    cached_NumberCompare_2 = generateTestOutput(true, "NumberCompare_2", "./test_code/BinaryExpression_NumberCompare_success.a");
    return cached_NumberCompare_2;
  }

  @Test
  public void testNumberCompareEmission() {
    assertEmissionGood(get_NumberCompare_2());
  }

  @Test
  public void testNumberCompareSuccess() {
    assertLivePass(get_NumberCompare_2());
  }

  @Test
  public void testNumberCompareGoodWillHappy() {
    assertGoodWillHappy(get_NumberCompare_2());
  }

  @Test
  public void testNumberCompareExceptionFree() {
    assertExceptionFree(get_NumberCompare_2());
  }

  @Test
  public void testNumberCompareTODOFree() {
    assertTODOFree(get_NumberCompare_2());
  }

  @Test
  public void stable_NumberCompare_2() {
    String live = get_NumberCompare_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BinaryExpression_NumberCompare_success.a");
    gold.append("\n--EMISSION-----------------------------------------");
    gold.append("\nEmission Success, Yay");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[]\"--JAVA---------------------------------------------");
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
    gold.append("\nimport org.adamalang.runtime.sys.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.util.HashSet;");
    gold.append("\nimport java.util.Map;");
    gold.append("\nimport java.util.Set;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class NumberCompare_2 extends LivingDocument {");
    gold.append("\n  public NumberCompare_2(DocumentMonitor __monitor) {");
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
    gold.append("\n          case \"__auto_table_row_id\":");
    gold.append("\n            __auto_table_row_id.__insert(__reader);");
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
    gold.append("\n          case \"__auto_table_row_id\":");
    gold.append("\n            __auto_table_row_id.__patch(__reader);");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_table_row_id\");");
    gold.append("\n    __auto_table_row_id.__dump(__writer);");
    gold.append("\n    __dumpDeduper(__writer);");
    gold.append("\n    __dumpClients(__writer);");
    gold.append("\n    __dumpMessages(__writer);");
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
    gold.append("\n    __auto_table_row_id.__commit(\"__auto_table_row_id\", __forward, __reverse);");
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
    gold.append("\n    __auto_table_row_id.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  private class DeltaNumberCompare_2 {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaNumberCompare_2() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(NumberCompare_2 __item, PrivateLazyDeltaWriter __writer) {");
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
    gold.append("\n  public Set<String> __get_intern_strings() {");
    gold.append("\n    HashSet<String> __interns = new HashSet<>();");
    gold.append("\n    __interns.add(\"\");");
    gold.append("\n    __interns.add(\"?\");");
    gold.append("\n    return __interns;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public PrivateView __createPrivateView(NtClient __who, Perspective ___perspective) {");
    gold.append("\n    NumberCompare_2 __self = this;");
    gold.append("\n    DeltaNumberCompare_2 __state = new DeltaNumberCompare_2();");
    gold.append("\n    RTx__ViewerType __viewerState = new RTx__ViewerType();");
    gold.append("\n    return new PrivateView(__who, ___perspective) {");
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
    gold.append("\n        __state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer, __viewerState));");
    gold.append("\n      }");
    gold.append("\n    };");
    gold.append("\n  }");
    gold.append("\n  private static class RTx__ViewerType implements NtMessageBase {");
    gold.append("\n    private RTx__ViewerType(JsonStreamReader __reader) {");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    public int __DATA_GENERATION = 1;");
    gold.append("\n    private void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      __reader.skipValue();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType() {}");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTx__ViewerType {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTx__ViewerType() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(RTx__ViewerType __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
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
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onConnected(NtClient __cvalue) {");
    gold.append("\n    boolean __result = false;");
    gold.append("\n    return __result;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onDisconnected(NtClient __cvalue) {}");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onCanAssetAttached(NtClient __cvalue) {");
    gold.append("\n    boolean __result = false;");
    gold.append("\n    return __result;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onAssetAttached(NtClient __cvalue, NtAsset __asset) {}");
    gold.append("\n  public void __test_PrimaryTest(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"PrimaryTest\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 28;");
    gold.append("\n      __track(0);");
    gold.append("\n      __assert_truth(1 == 1, 1, 2, 1, 16);");
    gold.append("\n      __track(1);");
    gold.append("\n      __assert_truth((1 != 2), 2, 2, 2, 18);");
    gold.append("\n      __track(2);");
    gold.append("\n      __assert_truth(!(1 == 2), 3, 2, 3, 19);");
    gold.append("\n      __track(3);");
    gold.append("\n      __assert_truth(1 < 2, 4, 2, 4, 15);");
    gold.append("\n      __track(4);");
    gold.append("\n      __assert_truth(1 <= 2, 5, 2, 5, 16);");
    gold.append("\n      __track(5);");
    gold.append("\n      __assert_truth(!(1 > 2), 6, 2, 6, 18);");
    gold.append("\n      __track(6);");
    gold.append("\n      __assert_truth(!(1 >= 2), 7, 2, 7, 19);");
    gold.append("\n      __track(7);");
    gold.append("\n      __assert_truth(2 > 1, 8, 2, 8, 15);");
    gold.append("\n      __track(8);");
    gold.append("\n      __assert_truth(2 >= 1, 9, 2, 9, 16);");
    gold.append("\n      __track(9);");
    gold.append("\n      __assert_truth(!(2 < 1), 10, 2, 10, 18);");
    gold.append("\n      __track(10);");
    gold.append("\n      __assert_truth(!(2 <= 1), 11, 2, 11, 19);");
    gold.append("\n      __track(11);");
    gold.append("\n      __assert_truth(1 < 3.14, 12, 2, 12, 18);");
    gold.append("\n      __track(12);");
    gold.append("\n      __assert_truth(1 <= 3.14, 13, 2, 13, 19);");
    gold.append("\n      __track(13);");
    gold.append("\n      __assert_truth(!(1 > 3.14), 14, 2, 14, 21);");
    gold.append("\n      __track(14);");
    gold.append("\n      __assert_truth(!(1 >= 3.14), 15, 2, 15, 22);");
    gold.append("\n      __track(15);");
    gold.append("\n      __assert_truth(3.14 > 1, 16, 2, 16, 18);");
    gold.append("\n      __track(16);");
    gold.append("\n      __assert_truth(3.14 >= 1, 17, 2, 17, 19);");
    gold.append("\n      __track(17);");
    gold.append("\n      __assert_truth(!(3.14 < 1), 18, 2, 18, 21);");
    gold.append("\n      __track(18);");
    gold.append("\n      __assert_truth(!(3.14 <= 1), 19, 2, 19, 22);");
    gold.append("\n      __track(19);");
    gold.append("\n      __assert_truth(2.71 < 3.14, 20, 2, 20, 21);");
    gold.append("\n      __track(20);");
    gold.append("\n      __assert_truth(2.71 <= 3.14, 21, 2, 21, 22);");
    gold.append("\n      __track(21);");
    gold.append("\n      __assert_truth(!(2.71 > 3.14), 22, 2, 22, 24);");
    gold.append("\n      __track(22);");
    gold.append("\n      __assert_truth(!(2.71 >= 3.14), 23, 2, 23, 25);");
    gold.append("\n      __track(23);");
    gold.append("\n      __assert_truth(3.14 > 2.71, 24, 2, 24, 21);");
    gold.append("\n      __track(24);");
    gold.append("\n      __assert_truth(3.14 >= 2.71, 25, 2, 25, 22);");
    gold.append("\n      __track(25);");
    gold.append("\n      __assert_truth(!(3.14 < 2.71), 26, 2, 26, 24);");
    gold.append("\n      __track(26);");
    gold.append("\n      __assert_truth(!(3.14 <= 2.71), 27, 2, 27, 25);");
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
    gold.append("\nBegin");
    gold.append("\nEnd");
    gold.append("\n--REFLECTION RESULTS-------------------------------------");
    gold.append("\n{\"types\":{\"#root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}}},\"channels\":{},\"constructors\":[],\"labels\":[]}");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__constructed\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"50\"} need:false in:-50");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":2}");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"75\"}-->{\"__messages\":null,\"__seq\":3,\"__entropy\":\"-1034601897293430941\",\"__time\":\"75\"} need:false in:-75");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":3}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":3}");
    gold.append("\nRANDO was DENIED:");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seq\":4,\"__entropy\":\"7848011421992302230\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":4}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":4}");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"125\"}-->{\"__goodwill_used\":0,\"__cost\":0,\"__billing_seq\":4,\"__seq\":5,\"__time\":\"125\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"150\"}-->{\"__messages\":null,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__time\":\"150\"} need:false in:-150");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":6}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":6}");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"150\",\"__auto_table_row_id\":0}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"150\",\"__auto_table_row_id\":0}");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"150\",\"__auto_table_row_id\":0}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[PrimaryTest] = 100.0%");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_OperationsHappy_3 = null;
  private String get_OperationsHappy_3() {
    if (cached_OperationsHappy_3 != null) {
      return cached_OperationsHappy_3;
    }
    cached_OperationsHappy_3 = generateTestOutput(true, "OperationsHappy_3", "./test_code/BinaryExpression_OperationsHappy_success.a");
    return cached_OperationsHappy_3;
  }

  @Test
  public void testOperationsHappyEmission() {
    assertEmissionGood(get_OperationsHappy_3());
  }

  @Test
  public void testOperationsHappySuccess() {
    assertLivePass(get_OperationsHappy_3());
  }

  @Test
  public void testOperationsHappyGoodWillHappy() {
    assertGoodWillHappy(get_OperationsHappy_3());
  }

  @Test
  public void testOperationsHappyExceptionFree() {
    assertExceptionFree(get_OperationsHappy_3());
  }

  @Test
  public void testOperationsHappyTODOFree() {
    assertTODOFree(get_OperationsHappy_3());
  }

  @Test
  public void stable_OperationsHappy_3() {
    String live = get_OperationsHappy_3();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BinaryExpression_OperationsHappy_success.a");
    gold.append("\n--EMISSION-----------------------------------------");
    gold.append("\nEmission Success, Yay");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[]\"--JAVA---------------------------------------------");
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
    gold.append("\nimport org.adamalang.runtime.sys.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.util.HashSet;");
    gold.append("\nimport java.util.Map;");
    gold.append("\nimport java.util.Set;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class OperationsHappy_3 extends LivingDocument {");
    gold.append("\n  private final RxClient other;");
    gold.append("\n  private final RxString s;");
    gold.append("\n  public OperationsHappy_3(DocumentMonitor __monitor) {");
    gold.append("\n    super(__monitor);");
    gold.append("\n    other = new RxClient(this, NtClient.NO_ONE);");
    gold.append("\n    s = new RxString(this, \"\");");
    gold.append("\n    __goodwillBudget = 100000;");
    gold.append("\n    __goodwillLimitOfBudget = 100000;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __insert(JsonStreamReader __reader) {");
    gold.append("\n    if (__reader.startObject()) {");
    gold.append("\n      while(__reader.notEndOfObject()) {");
    gold.append("\n        String __fieldName = __reader.fieldName();");
    gold.append("\n        switch (__fieldName) {");
    gold.append("\n          case \"other\":");
    gold.append("\n            other.__insert(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"s\":");
    gold.append("\n            s.__insert(__reader);");
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
    gold.append("\n          case \"__auto_table_row_id\":");
    gold.append("\n            __auto_table_row_id.__insert(__reader);");
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
    gold.append("\n          case \"other\":");
    gold.append("\n            other.__patch(__reader);");
    gold.append("\n            break;");
    gold.append("\n          case \"s\":");
    gold.append("\n            s.__patch(__reader);");
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
    gold.append("\n          case \"__auto_table_row_id\":");
    gold.append("\n            __auto_table_row_id.__patch(__reader);");
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
    gold.append("\n          default:");
    gold.append("\n            __reader.skipValue();");
    gold.append("\n        }");
    gold.append("\n      }");
    gold.append("\n    }");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __dump(JsonStreamWriter __writer) {");
    gold.append("\n    __writer.beginObject();");
    gold.append("\n    __writer.writeObjectFieldIntro(\"other\");");
    gold.append("\n    other.__dump(__writer);");
    gold.append("\n    __writer.writeObjectFieldIntro(\"s\");");
    gold.append("\n    s.__dump(__writer);");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_table_row_id\");");
    gold.append("\n    __auto_table_row_id.__dump(__writer);");
    gold.append("\n    __dumpDeduper(__writer);");
    gold.append("\n    __dumpClients(__writer);");
    gold.append("\n    __dumpMessages(__writer);");
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
    gold.append("\n    __auto_table_row_id.__commit(\"__auto_table_row_id\", __forward, __reverse);");
    gold.append("\n    other.__commit(\"other\", __forward, __reverse);");
    gold.append("\n    s.__commit(\"s\", __forward, __reverse);");
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
    gold.append("\n    __auto_table_row_id.__revert();");
    gold.append("\n    other.__revert();");
    gold.append("\n    s.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  private class DeltaOperationsHappy_3 {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaOperationsHappy_3() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(OperationsHappy_3 __item, PrivateLazyDeltaWriter __writer) {");
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
    gold.append("\n  public Set<String> __get_intern_strings() {");
    gold.append("\n    HashSet<String> __interns = new HashSet<>();");
    gold.append("\n    __interns.add(\"\");");
    gold.append("\n    __interns.add(\"?\");");
    gold.append("\n    return __interns;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public PrivateView __createPrivateView(NtClient __who, Perspective ___perspective) {");
    gold.append("\n    OperationsHappy_3 __self = this;");
    gold.append("\n    DeltaOperationsHappy_3 __state = new DeltaOperationsHappy_3();");
    gold.append("\n    RTx__ViewerType __viewerState = new RTx__ViewerType();");
    gold.append("\n    return new PrivateView(__who, ___perspective) {");
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
    gold.append("\n        __state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer, __viewerState));");
    gold.append("\n      }");
    gold.append("\n    };");
    gold.append("\n  }");
    gold.append("\n  private static class RTx__ViewerType implements NtMessageBase {");
    gold.append("\n    private RTx__ViewerType(JsonStreamReader __reader) {");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    public int __DATA_GENERATION = 1;");
    gold.append("\n    private void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      __reader.skipValue();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType() {}");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTx__ViewerType {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTx__ViewerType() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(RTx__ViewerType __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
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
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onConnected(NtClient __cvalue) {");
    gold.append("\n    boolean __result = false;");
    gold.append("\n    return __result;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onDisconnected(NtClient __cvalue) {}");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onCanAssetAttached(NtClient __cvalue) {");
    gold.append("\n    boolean __result = false;");
    gold.append("\n    return __result;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onAssetAttached(NtClient __cvalue, NtAsset __asset) {}");
    gold.append("\n  public void __test_Addition(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"Addition\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 12;");
    gold.append("\n      __track(0);");
    gold.append("\n      __assert_truth(LibString.equality(\"xy\", \"x\" + \"y\"), 1, 2, 1, 27);");
    gold.append("\n      __track(1);");
    gold.append("\n      __assert_truth(LibString.equality(\"xtrue\", \"x\" + true), 2, 2, 2, 31);");
    gold.append("\n      __track(2);");
    gold.append("\n      __assert_truth(LibString.equality(\"falsex\", false + \"x\"), 3, 2, 3, 33);");
    gold.append("\n      __track(3);");
    gold.append("\n      __assert_truth(LibString.equality(\"x1\", \"x\" + 1), 4, 2, 4, 25);");
    gold.append("\n      __track(4);");
    gold.append("\n      __assert_truth(LibString.equality(\"1x\", 1 + \"x\"), 5, 2, 5, 25);");
    gold.append("\n      __track(5);");
    gold.append("\n      __assert_truth(LibString.equality(\"x3.14\", \"x\" + 3.14), 6, 2, 6, 31);");
    gold.append("\n      __track(6);");
    gold.append("\n      __assert_truth(LibString.equality(\"3.14x\", 3.14 + \"x\"), 7, 2, 7, 31);");
    gold.append("\n      __track(7);");
    gold.append("\n      __assert_truth(3 == 1 + 2, 8, 2, 8, 20);");
    gold.append("\n      __track(8);");
    gold.append("\n      __assert_truth(3 == 2.0 + 1, 9, 2, 9, 22);");
    gold.append("\n      __track(9);");
    gold.append("\n      __assert_truth(3 == 1 + 2.0, 10, 2, 10, 22);");
    gold.append("\n      __track(10);");
    gold.append("\n      __assert_truth(LibMath.near(3.0, 2.0 + 1.0), 11, 2, 11, 26);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  public void __test_Subtraction(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"Subtraction\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 5;");
    gold.append("\n      __track(11);");
    gold.append("\n      __assert_truth(5 == 10 - 5, 15, 2, 15, 21);");
    gold.append("\n      __track(12);");
    gold.append("\n      __assert_truth(LibMath.near(3.0, 6 - 3), 16, 2, 16, 22);");
    gold.append("\n      __track(13);");
    gold.append("\n      __assert_truth(LibMath.near(3.0, 6.0 - 3.0), 17, 2, 17, 26);");
    gold.append("\n      __track(14);");
    gold.append("\n      __assert_truth(LibMath.near(3.0, 6.0 - 3), 18, 2, 18, 24);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  public void __test_Multiply(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"Multiply\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 6;");
    gold.append("\n      __track(15);");
    gold.append("\n      __assert_truth(LibString.equality(\"xxx\", LibString.multiply(\"x\", 3)), 22, 2, 22, 26);");
    gold.append("\n      __track(16);");
    gold.append("\n      __assert_truth(6 == 2 * 3, 23, 2, 23, 20);");
    gold.append("\n      __track(17);");
    gold.append("\n      __assert_truth(10 == 2.0 * 5.0, 24, 2, 24, 25);");
    gold.append("\n      __track(18);");
    gold.append("\n      __assert_truth(10 == 2.0 * 5, 25, 2, 25, 23);");
    gold.append("\n      __track(19);");
    gold.append("\n      __assert_truth(10 == 2 * 5.0, 26, 2, 26, 23);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  public void __test_Divide(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"Divide\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 5;");
    gold.append("\n      __track(20);");
    gold.append("\n      __assert_truth(2 == 4 / 2, 30, 2, 30, 20);");
    gold.append("\n      __track(21);");
    gold.append("\n      __assert_truth(LibMath.near(2.0, 4 / 2.0), 31, 2, 31, 24);");
    gold.append("\n      __track(22);");
    gold.append("\n      __assert_truth(LibMath.near(2.0, 4.0 / 2.0), 32, 2, 32, 26);");
    gold.append("\n      __track(23);");
    gold.append("\n      __assert_truth(LibMath.near(1.5, 3.0 / 2), 33, 2, 33, 24);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  public void __test_Mod(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"Mod\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 2;");
    gold.append("\n      __track(24);");
    gold.append("\n      __assert_truth(2 == 7 % 5, 37, 2, 37, 20);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  public void __test_Relate(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"Relate\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 3;");
    gold.append("\n      __track(25);");
    gold.append("\n      __assert_truth(1 < 2, 41, 2, 41, 15);");
    gold.append("\n      __track(26);");
    gold.append("\n      __assert_truth(1.5 < 4.3, 42, 2, 42, 19);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  public void __test_Logic(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"Logic\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 11;");
    gold.append("\n      __track(27);");
    gold.append("\n      __assert_truth(true == true, 46, 2, 46, 22);");
    gold.append("\n      __track(28);");
    gold.append("\n      __assert_truth(false != true, 47, 2, 47, 23);");
    gold.append("\n      __track(29);");
    gold.append("\n      __assert_truth(true && true, 48, 2, 48, 22);");
    gold.append("\n      __track(30);");
    gold.append("\n      __assert_truth(!(true && false), 49, 2, 49, 26);");
    gold.append("\n      __track(31);");
    gold.append("\n      __assert_truth(!(false && true), 50, 2, 50, 26);");
    gold.append("\n      __track(32);");
    gold.append("\n      __assert_truth(!(false && false), 51, 2, 51, 27);");
    gold.append("\n      __track(33);");
    gold.append("\n      __assert_truth(true || true, 52, 2, 52, 22);");
    gold.append("\n      __track(34);");
    gold.append("\n      __assert_truth(true || false, 53, 2, 53, 23);");
    gold.append("\n      __track(35);");
    gold.append("\n      __assert_truth(false || true, 54, 2, 54, 23);");
    gold.append("\n      __track(36);");
    gold.append("\n      __assert_truth(!(false || false), 55, 2, 55, 27);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  public void __test_Equals(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"Equals\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 8;");
    gold.append("\n      __track(37);");
    gold.append("\n      __assert_truth((NtClient.NO_ONE.equals(NtClient.NO_ONE)), 62, 2, 62, 28);");
    gold.append("\n      __track(38);");
    gold.append("\n      __assert_truth((NtClient.NO_ONE.equals(other.get())), 63, 2, 63, 26);");
    gold.append("\n      __track(39);");
    gold.append("\n      __assert_truth((other.get().equals(NtClient.NO_ONE)), 64, 2, 64, 26);");
    gold.append("\n      __track(40);");
    gold.append("\n      __assert_truth(!(!(NtClient.NO_ONE.equals(NtClient.NO_ONE))), 65, 2, 65, 31);");
    gold.append("\n      __track(41);");
    gold.append("\n      __assert_truth(LibString.equality(s.get(), \"\"), 66, 2, 66, 17);");
    gold.append("\n      __track(42);");
    gold.append("\n      __assert_truth(LibString.equality(\"\", s.get()), 67, 2, 67, 17);");
    gold.append("\n      __track(43);");
    gold.append("\n      __assert_truth(LibString.equality(s.get(), s.get()), 68, 2, 68, 16);");
    gold.append("\n    }");
    gold.append("\n    report.end(getAndResetAssertions());");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public String[] __getTests() {");
    gold.append("\n    return new String[] {\"Addition\", \"Subtraction\", \"Multiply\", \"Divide\", \"Mod\", \"Relate\", \"Logic\", \"Equals\"};");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __test(TestReportBuilder report, String testName) {");
    gold.append("\n    switch(testName) {");
    gold.append("\n      case \"Addition\":");
    gold.append("\n          __test_Addition(report);");
    gold.append("\n          return;");
    gold.append("\n      case \"Subtraction\":");
    gold.append("\n          __test_Subtraction(report);");
    gold.append("\n          return;");
    gold.append("\n      case \"Multiply\":");
    gold.append("\n          __test_Multiply(report);");
    gold.append("\n          return;");
    gold.append("\n      case \"Divide\":");
    gold.append("\n          __test_Divide(report);");
    gold.append("\n          return;");
    gold.append("\n      case \"Mod\":");
    gold.append("\n          __test_Mod(report);");
    gold.append("\n          return;");
    gold.append("\n      case \"Relate\":");
    gold.append("\n          __test_Relate(report);");
    gold.append("\n          return;");
    gold.append("\n      case \"Logic\":");
    gold.append("\n          __test_Logic(report);");
    gold.append("\n          return;");
    gold.append("\n      case \"Equals\":");
    gold.append("\n          __test_Equals(report);");
    gold.append("\n          return;");
    gold.append("\n    }");
    gold.append("\n  }");
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
    gold.append("\nBegin");
    gold.append("\nEnd");
    gold.append("\n--REFLECTION RESULTS-------------------------------------");
    gold.append("\n{\"types\":{\"#root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}}},\"channels\":{},\"constructors\":[],\"labels\":[]}");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__constructed\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"50\"} need:false in:-50");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":2}");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"75\"}-->{\"__messages\":null,\"__seq\":3,\"__entropy\":\"-1034601897293430941\",\"__time\":\"75\"} need:false in:-75");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":3}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":3}");
    gold.append("\nRANDO was DENIED:");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seq\":4,\"__entropy\":\"7848011421992302230\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":4}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":4}");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"125\"}-->{\"__goodwill_used\":0,\"__cost\":0,\"__billing_seq\":4,\"__seq\":5,\"__time\":\"125\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"150\"}-->{\"__messages\":null,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__time\":\"150\"} need:false in:-150");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":6}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":6}");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"other\":{\"agent\":\"?\",\"authority\":\"?\"},\"s\":\"\",\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"150\",\"__auto_table_row_id\":0}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"other\":{\"agent\":\"?\",\"authority\":\"?\"},\"s\":\"\",\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"150\",\"__auto_table_row_id\":0}");
    gold.append("\n{\"other\":{\"agent\":\"?\",\"authority\":\"?\"},\"s\":\"\",\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"150\",\"__auto_table_row_id\":0}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[Addition] = 100.0%");
    gold.append("\nTEST[Subtraction] = 100.0%");
    gold.append("\nTEST[Multiply] = 100.0%");
    gold.append("\nTEST[Divide] = 100.0%");
    gold.append("\nTEST[Mod] = 100.0%");
    gold.append("\nTEST[Relate] = 100.0%");
    gold.append("\nTEST[Logic] = 100.0%");
    gold.append("\nTEST[Equals] = 100.0%");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_StringCompare_4 = null;
  private String get_StringCompare_4() {
    if (cached_StringCompare_4 != null) {
      return cached_StringCompare_4;
    }
    cached_StringCompare_4 = generateTestOutput(true, "StringCompare_4", "./test_code/BinaryExpression_StringCompare_success.a");
    return cached_StringCompare_4;
  }

  @Test
  public void testStringCompareEmission() {
    assertEmissionGood(get_StringCompare_4());
  }

  @Test
  public void testStringCompareSuccess() {
    assertLivePass(get_StringCompare_4());
  }

  @Test
  public void testStringCompareGoodWillHappy() {
    assertGoodWillHappy(get_StringCompare_4());
  }

  @Test
  public void testStringCompareExceptionFree() {
    assertExceptionFree(get_StringCompare_4());
  }

  @Test
  public void testStringCompareTODOFree() {
    assertTODOFree(get_StringCompare_4());
  }

  @Test
  public void stable_StringCompare_4() {
    String live = get_StringCompare_4();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BinaryExpression_StringCompare_success.a");
    gold.append("\n--EMISSION-----------------------------------------");
    gold.append("\nEmission Success, Yay");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[]\"--JAVA---------------------------------------------");
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
    gold.append("\nimport org.adamalang.runtime.sys.*;");
    gold.append("\nimport java.util.function.Consumer;");
    gold.append("\nimport java.util.function.Function;");
    gold.append("\nimport java.util.ArrayList;");
    gold.append("\nimport java.util.Comparator;");
    gold.append("\nimport java.util.HashSet;");
    gold.append("\nimport java.util.Map;");
    gold.append("\nimport java.util.Set;");
    gold.append("\nimport java.lang.Math;");
    gold.append("\npublic class StringCompare_4 extends LivingDocument {");
    gold.append("\n  public StringCompare_4(DocumentMonitor __monitor) {");
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
    gold.append("\n          case \"__auto_table_row_id\":");
    gold.append("\n            __auto_table_row_id.__insert(__reader);");
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
    gold.append("\n          case \"__auto_table_row_id\":");
    gold.append("\n            __auto_table_row_id.__patch(__reader);");
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
    gold.append("\n    __writer.writeObjectFieldIntro(\"__auto_table_row_id\");");
    gold.append("\n    __auto_table_row_id.__dump(__writer);");
    gold.append("\n    __dumpDeduper(__writer);");
    gold.append("\n    __dumpClients(__writer);");
    gold.append("\n    __dumpMessages(__writer);");
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
    gold.append("\n    __auto_table_row_id.__commit(\"__auto_table_row_id\", __forward, __reverse);");
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
    gold.append("\n    __auto_table_row_id.__revert();");
    gold.append("\n    /* root */");
    gold.append("\n  }");
    gold.append("\n  private class DeltaStringCompare_4 {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaStringCompare_4() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(StringCompare_4 __item, PrivateLazyDeltaWriter __writer) {");
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
    gold.append("\n  public Set<String> __get_intern_strings() {");
    gold.append("\n    HashSet<String> __interns = new HashSet<>();");
    gold.append("\n    __interns.add(\"\");");
    gold.append("\n    __interns.add(\"?\");");
    gold.append("\n    return __interns;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public PrivateView __createPrivateView(NtClient __who, Perspective ___perspective) {");
    gold.append("\n    StringCompare_4 __self = this;");
    gold.append("\n    DeltaStringCompare_4 __state = new DeltaStringCompare_4();");
    gold.append("\n    RTx__ViewerType __viewerState = new RTx__ViewerType();");
    gold.append("\n    return new PrivateView(__who, ___perspective) {");
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
    gold.append("\n        __state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer, __viewerState));");
    gold.append("\n      }");
    gold.append("\n    };");
    gold.append("\n  }");
    gold.append("\n  private static class RTx__ViewerType implements NtMessageBase {");
    gold.append("\n    private RTx__ViewerType(JsonStreamReader __reader) {");
    gold.append("\n      __ingest(__reader);");
    gold.append("\n    }");
    gold.append("\n    public int __DATA_GENERATION = 1;");
    gold.append("\n    private void __ingest(JsonStreamReader __reader) {");
    gold.append("\n      __reader.skipValue();");
    gold.append("\n    }");
    gold.append("\n    @Override");
    gold.append("\n    public void __writeOut(JsonStreamWriter __writer) {");
    gold.append("\n      __writer.beginObject();");
    gold.append("\n      __writer.endObject();");
    gold.append("\n    }");
    gold.append("\n    private RTx__ViewerType() {}");
    gold.append("\n  }");
    gold.append("\n  private class DeltaRTx__ViewerType {");
    gold.append("\n    private boolean __emitted;");
    gold.append("\n    private DeltaRTx__ViewerType() {");
    gold.append("\n      __emitted = false;");
    gold.append("\n    }");
    gold.append("\n    public void show(RTx__ViewerType __item, PrivateLazyDeltaWriter __writer) {");
    gold.append("\n      PrivateLazyDeltaWriter __obj = __writer.planObject();");
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
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onConnected(NtClient __cvalue) {");
    gold.append("\n    boolean __result = false;");
    gold.append("\n    return __result;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onDisconnected(NtClient __cvalue) {}");
    gold.append("\n  @Override");
    gold.append("\n  public boolean __onCanAssetAttached(NtClient __cvalue) {");
    gold.append("\n    boolean __result = false;");
    gold.append("\n    return __result;");
    gold.append("\n  }");
    gold.append("\n  @Override");
    gold.append("\n  public void __onAssetAttached(NtClient __cvalue, NtAsset __asset) {}");
    gold.append("\n  public void __test_PrimaryTest(TestReportBuilder report) {");
    gold.append("\n    report.begin(\"PrimaryTest\");");
    gold.append("\n    {");
    gold.append("\n      __code_cost += 12;");
    gold.append("\n      __track(0);");
    gold.append("\n      __assert_truth(LibString.equality(\"x\", \"x\"), 1, 2, 1, 20);");
    gold.append("\n      __track(1);");
    gold.append("\n      __assert_truth((!LibString.equality(\"x\", \"y\")), 2, 2, 2, 22);");
    gold.append("\n      __track(2);");
    gold.append("\n      __assert_truth(!(LibString.equality(\"x\", \"y\")), 3, 2, 3, 23);");
    gold.append("\n      __track(3);");
    gold.append("\n      __assert_truth(LibString.compare(\"x\", \"y\") < 0, 4, 2, 4, 19);");
    gold.append("\n      __track(4);");
    gold.append("\n      __assert_truth(LibString.compare(\"x\", \"y\") <= 0, 5, 2, 5, 20);");
    gold.append("\n      __track(5);");
    gold.append("\n      __assert_truth(!(LibString.compare(\"x\", \"y\") > 0), 6, 2, 6, 22);");
    gold.append("\n      __track(6);");
    gold.append("\n      __assert_truth(!(LibString.compare(\"x\", \"y\") >= 0), 7, 2, 7, 23);");
    gold.append("\n      __track(7);");
    gold.append("\n      __assert_truth(LibString.compare(\"y\", \"x\") > 0, 8, 2, 8, 19);");
    gold.append("\n      __track(8);");
    gold.append("\n      __assert_truth(LibString.compare(\"y\", \"x\") >= 0, 9, 2, 9, 20);");
    gold.append("\n      __track(9);");
    gold.append("\n      __assert_truth(!(LibString.compare(\"y\", \"x\") < 0), 10, 2, 10, 22);");
    gold.append("\n      __track(10);");
    gold.append("\n      __assert_truth(!(LibString.compare(\"y\", \"x\") <= 0), 11, 2, 11, 23);");
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
    gold.append("\nBegin");
    gold.append("\nEnd");
    gold.append("\n--REFLECTION RESULTS-------------------------------------");
    gold.append("\n{\"types\":{\"#root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}}},\"channels\":{},\"constructors\":[],\"labels\":[]}");
    gold.append("\n--JAVA RUNNING-------------------------------------");
    gold.append("\n{\"command\":\"construct\",\"timestamp\":\"0\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"arg\":{},\"entropy\":\"0\"}-->{\"__constructed\":true,\"__entropy\":\"0\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"25\"}-->{\"__messages\":null,\"__seq\":1,\"__entropy\":\"-4962768465676381896\",\"__time\":\"25\"} need:false in:-25");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"50\"}-->{\"__messages\":null,\"__seq\":2,\"__entropy\":\"4804307197456638271\",\"__time\":\"50\"} need:false in:-50");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":2}");
    gold.append("\nNO_ONE was DENIED");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"75\"}-->{\"__messages\":null,\"__seq\":3,\"__entropy\":\"-1034601897293430941\",\"__time\":\"75\"} need:false in:-75");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":3}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":3}");
    gold.append("\nRANDO was DENIED:");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"100\"}-->{\"__messages\":null,\"__seq\":4,\"__entropy\":\"7848011421992302230\",\"__time\":\"100\"} need:false in:-100");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":4}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":4}");
    gold.append("\n{\"command\":\"bill\",\"timestamp\":\"125\"}-->{\"__goodwill_used\":0,\"__cost\":0,\"__billing_seq\":4,\"__seq\":5,\"__time\":\"125\"} need:true in:0");
    gold.append("\n{\"command\":\"invalidate\",\"timestamp\":\"150\"}-->{\"__messages\":null,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__time\":\"150\"} need:false in:-150");
    gold.append("\n+ NO_ONE DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":6}");
    gold.append("\n+ RANDO DELTA:{\"data\":{},\"outstanding\":[],\"blockers\":[],\"seq\":6}");
    gold.append("\n--JAVA RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"150\",\"__auto_table_row_id\":0}");
    gold.append("\n--DUMP RESULTS-------------------------------------");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"150\",\"__auto_table_row_id\":0}");
    gold.append("\n{\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":6,\"__entropy\":\"-8929183248358367000\",\"__auto_future_id\":0,\"__connection_id\":0,\"__message_id\":0,\"__time\":\"150\",\"__auto_table_row_id\":0}");
    gold.append("\n--JAVA TEST RESULTS--------------------------------");
    gold.append("\nTEST[PrimaryTest] = 100.0%");
    gold.append("\n");
    gold.append("\nSuccess");
    assertStable(live, gold);
  }
  private String cached_WhenCantAdd_5 = null;
  private String get_WhenCantAdd_5() {
    if (cached_WhenCantAdd_5 != null) {
      return cached_WhenCantAdd_5;
    }
    cached_WhenCantAdd_5 = generateTestOutput(false, "WhenCantAdd_5", "./test_code/BinaryExpression_WhenCantAdd_failure.a");
    return cached_WhenCantAdd_5;
  }

  @Test
  public void testWhenCantAddFailure() {
    assertLiveFail(get_WhenCantAdd_5());
  }

  @Test
  public void testWhenCantAddNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_WhenCantAdd_5());
  }

  @Test
  public void testWhenCantAddExceptionFree() {
    assertExceptionFree(get_WhenCantAdd_5());
  }

  @Test
  public void testWhenCantAddTODOFree() {
    assertTODOFree(get_WhenCantAdd_5());
  }

  @Test
  public void stable_WhenCantAdd_5() {
    String live = get_WhenCantAdd_5();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BinaryExpression_WhenCantAdd_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":17,\"character\":12},\"end\":{\"line\":17,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'long' and 'bool' are unable to be added with the + operator.\\n\\tThe left hand side has a numeric type of 'long' which can be added with types: 'int', 'long', or 'string'. (ADD01)\"},{\"range\":{\"start\":{\"line\":18,\"character\":12},\"end\":{\"line\":18,\"character\":34}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'list<int>' and 'bool' are unable to be added with the + operator.\\n\\tThe left hand side has a type that is unable to the added. (ADD01)\"},{\"range\":{\"start\":{\"line\":1,\"character\":11},\"end\":{\"line\":1,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'bool' are unable to be added with the + operator.\\n\\tThe left hand side has a type of 'bool' which may only be added with a right hand type of 'string'. (ADD01)\"},{\"range\":{\"start\":{\"line\":2,\"character\":11},\"end\":{\"line\":2,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'int' are unable to be added with the + operator.\\n\\tThe left hand side has a type of 'bool' which may only be added with a right hand type of 'string'. (ADD01)\"},{\"range\":{\"start\":{\"line\":3,\"character\":11},\"end\":{\"line\":3,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'int' and 'bool' are unable to be added with the + operator.\\n\\tThe left hand side has a numeric type of 'int' which can be added with types: 'int', 'double', or 'string'. (ADD01)\"},{\"range\":{\"start\":{\"line\":4,\"character\":11},\"end\":{\"line\":4,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'double' are unable to be added with the + operator.\\n\\tThe left hand side has a type of 'bool' which may only be added with a right hand type of 'string'. (ADD01)\"},{\"range\":{\"start\":{\"line\":5,\"character\":11},\"end\":{\"line\":5,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'double' and 'bool' are unable to be added with the + operator.\\n\\tThe left hand side has a numeric type of 'double' which can be added with types: 'int, 'double', or 'string'. (ADD01)\"},{\"range\":{\"start\":{\"line\":6,\"character\":11},\"end\":{\"line\":6,\"character\":25}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'client' are unable to be added with the + operator.\\n\\tThe left hand side has a type of 'bool' which may only be added with a right hand type of 'string'. (ADD01)\"},{\"range\":{\"start\":{\"line\":7,\"character\":11},\"end\":{\"line\":7,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'client' and '__ViewerType' are unable to be added with the + operator.\\n\\tThe left hand side has a type that is unable to the added. (ADD01)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_WhenCantCompare_6 = null;
  private String get_WhenCantCompare_6() {
    if (cached_WhenCantCompare_6 != null) {
      return cached_WhenCantCompare_6;
    }
    cached_WhenCantCompare_6 = generateTestOutput(false, "WhenCantCompare_6", "./test_code/BinaryExpression_WhenCantCompare_failure.a");
    return cached_WhenCantCompare_6;
  }

  @Test
  public void testWhenCantCompareFailure() {
    assertLiveFail(get_WhenCantCompare_6());
  }

  @Test
  public void testWhenCantCompareNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_WhenCantCompare_6());
  }

  @Test
  public void testWhenCantCompareExceptionFree() {
    assertExceptionFree(get_WhenCantCompare_6());
  }

  @Test
  public void testWhenCantCompareTODOFree() {
    assertTODOFree(get_WhenCantCompare_6());
  }

  @Test
  public void stable_WhenCantCompare_6() {
    String live = get_WhenCantCompare_6();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BinaryExpression_WhenCantCompare_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":4,\"character\":11},\"end\":{\"line\":4,\"character\":20}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: unable to compare types 'bool' and 'int' for equality. (RuleSetEquality)\"},{\"range\":{\"start\":{\"line\":5,\"character\":11},\"end\":{\"line\":5,\"character\":20}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: unable to compare types 'int' and 'bool' for equality. (RuleSetEquality)\"},{\"range\":{\"start\":{\"line\":6,\"character\":11},\"end\":{\"line\":6,\"character\":22}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: unable to compare types 'bool' and 'double' for equality. (RuleSetEquality)\"},{\"range\":{\"start\":{\"line\":7,\"character\":11},\"end\":{\"line\":7,\"character\":22}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: unable to compare types 'double' and 'bool' for equality. (RuleSetEquality)\"},{\"range\":{\"start\":{\"line\":8,\"character\":11},\"end\":{\"line\":8,\"character\":26}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: unable to compare types 'bool' and 'client' for equality. (RuleSetEquality)\"},{\"range\":{\"start\":{\"line\":9,\"character\":11},\"end\":{\"line\":9,\"character\":24}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: unable to compare types 'client' and '__ViewerType' for equality. (RuleSetEquality)\"},{\"range\":{\"start\":{\"line\":10,\"character\":10},\"end\":{\"line\":10,\"character\":22}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: enum types are incompatible 'X' vs 'T'. (RuleSetEquality)\"},{\"range\":{\"start\":{\"line\":15,\"character\":11},\"end\":{\"line\":15,\"character\":20}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: unable to compare types 'bool' and 'int' for equality. (RuleSetEquality)\"},{\"range\":{\"start\":{\"line\":16,\"character\":11},\"end\":{\"line\":16,\"character\":20}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: unable to compare types 'int' and 'bool' for equality. (RuleSetEquality)\"},{\"range\":{\"start\":{\"line\":17,\"character\":11},\"end\":{\"line\":17,\"character\":22}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: unable to compare types 'bool' and 'double' for equality. (RuleSetEquality)\"},{\"range\":{\"start\":{\"line\":18,\"character\":11},\"end\":{\"line\":18,\"character\":22}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: unable to compare types 'double' and 'bool' for equality. (RuleSetEquality)\"},{\"range\":{\"start\":{\"line\":19,\"character\":11},\"end\":{\"line\":19,\"character\":26}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: unable to compare types 'bool' and 'client' for equality. (RuleSetEquality)\"},{\"range\":{\"start\":{\"line\":20,\"character\":11},\"end\":{\"line\":20,\"character\":24}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: unable to compare types 'client' and '__ViewerType' for equality. (RuleSetEquality)\"},{\"range\":{\"start\":{\"line\":21,\"character\":10},\"end\":{\"line\":21,\"character\":22}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: enum types are incompatible 'X' vs 'T'. (RuleSetEquality)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_WhenCantDivide_7 = null;
  private String get_WhenCantDivide_7() {
    if (cached_WhenCantDivide_7 != null) {
      return cached_WhenCantDivide_7;
    }
    cached_WhenCantDivide_7 = generateTestOutput(false, "WhenCantDivide_7", "./test_code/BinaryExpression_WhenCantDivide_failure.a");
    return cached_WhenCantDivide_7;
  }

  @Test
  public void testWhenCantDivideFailure() {
    assertLiveFail(get_WhenCantDivide_7());
  }

  @Test
  public void testWhenCantDivideNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_WhenCantDivide_7());
  }

  @Test
  public void testWhenCantDivideExceptionFree() {
    assertExceptionFree(get_WhenCantDivide_7());
  }

  @Test
  public void testWhenCantDivideTODOFree() {
    assertTODOFree(get_WhenCantDivide_7());
  }

  @Test
  public void stable_WhenCantDivide_7() {
    String live = get_WhenCantDivide_7();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BinaryExpression_WhenCantDivide_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":11},\"end\":{\"line\":1,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'bool' and 'bool' are unable to be divided with the / operator. (Divide)\"},{\"range\":{\"start\":{\"line\":2,\"character\":11},\"end\":{\"line\":2,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'bool' and 'int' are unable to be divided with the / operator. (Divide)\"},{\"range\":{\"start\":{\"line\":3,\"character\":11},\"end\":{\"line\":3,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'int' and 'bool' are unable to be divided with the / operator. (Divide)\"},{\"range\":{\"start\":{\"line\":4,\"character\":11},\"end\":{\"line\":4,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'bool' and 'double' are unable to be divided with the / operator. (Divide)\"},{\"range\":{\"start\":{\"line\":5,\"character\":11},\"end\":{\"line\":5,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'double' and 'bool' are unable to be divided with the / operator. (Divide)\"},{\"range\":{\"start\":{\"line\":6,\"character\":11},\"end\":{\"line\":6,\"character\":25}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'bool' and 'client' are unable to be divided with the / operator. (Divide)\"},{\"range\":{\"start\":{\"line\":7,\"character\":11},\"end\":{\"line\":7,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'client' and '__ViewerType' are unable to be divided with the / operator. (Divide)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_WhenCantLogic_8 = null;
  private String get_WhenCantLogic_8() {
    if (cached_WhenCantLogic_8 != null) {
      return cached_WhenCantLogic_8;
    }
    cached_WhenCantLogic_8 = generateTestOutput(false, "WhenCantLogic_8", "./test_code/BinaryExpression_WhenCantLogic_failure.a");
    return cached_WhenCantLogic_8;
  }

  @Test
  public void testWhenCantLogicFailure() {
    assertLiveFail(get_WhenCantLogic_8());
  }

  @Test
  public void testWhenCantLogicNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_WhenCantLogic_8());
  }

  @Test
  public void testWhenCantLogicExceptionFree() {
    assertExceptionFree(get_WhenCantLogic_8());
  }

  @Test
  public void testWhenCantLogicTODOFree() {
    assertTODOFree(get_WhenCantLogic_8());
  }

  @Test
  public void stable_WhenCantLogic_8() {
    String live = get_WhenCantLogic_8();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BinaryExpression_WhenCantLogic_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":2,\"character\":19},\"end\":{\"line\":2,\"character\":20}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually 'int' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":2,\"character\":11},\"end\":{\"line\":2,\"character\":20}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'int' are unable to be joined with logical operators (&&, ||).\\n\\tBoth left and right hand side of the operator must be of type 'bool'. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":3,\"character\":11},\"end\":{\"line\":3,\"character\":12}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually 'int' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":3,\"character\":11},\"end\":{\"line\":3,\"character\":20}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'int' and 'bool' are unable to be joined with logical operators (&&, ||).\\n\\tBoth left and right hand side of the operator must be of type 'bool'. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":4,\"character\":19},\"end\":{\"line\":4,\"character\":22}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually 'double' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":4,\"character\":11},\"end\":{\"line\":4,\"character\":22}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'double' are unable to be joined with logical operators (&&, ||).\\n\\tBoth left and right hand side of the operator must be of type 'bool'. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":5,\"character\":11},\"end\":{\"line\":5,\"character\":14}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually 'double' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":5,\"character\":11},\"end\":{\"line\":5,\"character\":22}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'double' and 'bool' are unable to be joined with logical operators (&&, ||).\\n\\tBoth left and right hand side of the operator must be of type 'bool'. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":6,\"character\":19},\"end\":{\"line\":6,\"character\":26}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually 'client' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":6,\"character\":11},\"end\":{\"line\":6,\"character\":26}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'client' are unable to be joined with logical operators (&&, ||).\\n\\tBoth left and right hand side of the operator must be of type 'bool'. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":7,\"character\":11},\"end\":{\"line\":7,\"character\":18}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually 'client' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":17,\"character\":22},\"end\":{\"line\":17,\"character\":24}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually '__ViewerType' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":7,\"character\":11},\"end\":{\"line\":7,\"character\":24}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'client' and '__ViewerType' are unable to be joined with logical operators (&&, ||).\\n\\tBoth left and right hand side of the operator must be of type 'bool'. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":12,\"character\":19},\"end\":{\"line\":12,\"character\":20}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually 'int' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":12,\"character\":11},\"end\":{\"line\":12,\"character\":20}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'int' are unable to be joined with logical operators (&&, ||).\\n\\tBoth left and right hand side of the operator must be of type 'bool'. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":13,\"character\":11},\"end\":{\"line\":13,\"character\":12}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually 'int' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":13,\"character\":11},\"end\":{\"line\":13,\"character\":20}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'int' and 'bool' are unable to be joined with logical operators (&&, ||).\\n\\tBoth left and right hand side of the operator must be of type 'bool'. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":14,\"character\":19},\"end\":{\"line\":14,\"character\":22}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually 'double' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":14,\"character\":11},\"end\":{\"line\":14,\"character\":22}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'double' are unable to be joined with logical operators (&&, ||).\\n\\tBoth left and right hand side of the operator must be of type 'bool'. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":15,\"character\":11},\"end\":{\"line\":15,\"character\":14}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually 'double' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":15,\"character\":11},\"end\":{\"line\":15,\"character\":22}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'double' and 'bool' are unable to be joined with logical operators (&&, ||).\\n\\tBoth left and right hand side of the operator must be of type 'bool'. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":16,\"character\":19},\"end\":{\"line\":16,\"character\":26}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually 'client' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":16,\"character\":11},\"end\":{\"line\":16,\"character\":26}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'client' are unable to be joined with logical operators (&&, ||).\\n\\tBoth left and right hand side of the operator must be of type 'bool'. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":17,\"character\":11},\"end\":{\"line\":17,\"character\":18}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually 'client' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":17,\"character\":22},\"end\":{\"line\":17,\"character\":24}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: must have a type of 'bool', but the type is actually '__ViewerType' (TypeCheckFailures)\"},{\"range\":{\"start\":{\"line\":17,\"character\":11},\"end\":{\"line\":17,\"character\":24}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'client' and '__ViewerType' are unable to be joined with logical operators (&&, ||).\\n\\tBoth left and right hand side of the operator must be of type 'bool'. (RuleSetLogic)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_WhenCantMod_9 = null;
  private String get_WhenCantMod_9() {
    if (cached_WhenCantMod_9 != null) {
      return cached_WhenCantMod_9;
    }
    cached_WhenCantMod_9 = generateTestOutput(false, "WhenCantMod_9", "./test_code/BinaryExpression_WhenCantMod_failure.a");
    return cached_WhenCantMod_9;
  }

  @Test
  public void testWhenCantModFailure() {
    assertLiveFail(get_WhenCantMod_9());
  }

  @Test
  public void testWhenCantModNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_WhenCantMod_9());
  }

  @Test
  public void testWhenCantModExceptionFree() {
    assertExceptionFree(get_WhenCantMod_9());
  }

  @Test
  public void testWhenCantModTODOFree() {
    assertTODOFree(get_WhenCantMod_9());
  }

  @Test
  public void stable_WhenCantMod_9() {
    String live = get_WhenCantMod_9();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BinaryExpression_WhenCantMod_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":11},\"end\":{\"line\":1,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'bool' and 'bool' are unable to be used with the mod (%) operator/. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":2,\"character\":11},\"end\":{\"line\":2,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'bool' and 'int' are unable to be used with the mod (%) operator/. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":3,\"character\":11},\"end\":{\"line\":3,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'int' and 'bool' are unable to be used with the mod (%) operator/. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":4,\"character\":11},\"end\":{\"line\":4,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'bool' and 'double' are unable to be used with the mod (%) operator/. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":5,\"character\":11},\"end\":{\"line\":5,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'double' and 'bool' are unable to be used with the mod (%) operator/. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":6,\"character\":11},\"end\":{\"line\":6,\"character\":25}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'bool' and 'client' are unable to be used with the mod (%) operator/. (RuleSetLogic)\"},{\"range\":{\"start\":{\"line\":7,\"character\":11},\"end\":{\"line\":7,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'client' and '__ViewerType' are unable to be used with the mod (%) operator/. (RuleSetLogic)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_WhenCantMultiply_10 = null;
  private String get_WhenCantMultiply_10() {
    if (cached_WhenCantMultiply_10 != null) {
      return cached_WhenCantMultiply_10;
    }
    cached_WhenCantMultiply_10 = generateTestOutput(false, "WhenCantMultiply_10", "./test_code/BinaryExpression_WhenCantMultiply_failure.a");
    return cached_WhenCantMultiply_10;
  }

  @Test
  public void testWhenCantMultiplyFailure() {
    assertLiveFail(get_WhenCantMultiply_10());
  }

  @Test
  public void testWhenCantMultiplyNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_WhenCantMultiply_10());
  }

  @Test
  public void testWhenCantMultiplyExceptionFree() {
    assertExceptionFree(get_WhenCantMultiply_10());
  }

  @Test
  public void testWhenCantMultiplyTODOFree() {
    assertTODOFree(get_WhenCantMultiply_10());
  }

  @Test
  public void stable_WhenCantMultiply_10() {
    String live = get_WhenCantMultiply_10();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BinaryExpression_WhenCantMultiply_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":11},\"end\":{\"line\":1,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'bool' and 'bool' are unable to be multiplied with the * operator. (Multiply)\"},{\"range\":{\"start\":{\"line\":2,\"character\":11},\"end\":{\"line\":2,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'bool' and 'int' are unable to be multiplied with the * operator. (Multiply)\"},{\"range\":{\"start\":{\"line\":3,\"character\":11},\"end\":{\"line\":3,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'int' and 'bool' are unable to be multiplied with the * operator. (Multiply)\"},{\"range\":{\"start\":{\"line\":4,\"character\":11},\"end\":{\"line\":4,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'bool' and 'double' are unable to be multiplied with the * operator. (Multiply)\"},{\"range\":{\"start\":{\"line\":5,\"character\":11},\"end\":{\"line\":5,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'double' and 'bool' are unable to be multiplied with the * operator. (Multiply)\"},{\"range\":{\"start\":{\"line\":6,\"character\":11},\"end\":{\"line\":6,\"character\":25}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'bool' and 'client' are unable to be multiplied with the * operator. (Multiply)\"},{\"range\":{\"start\":{\"line\":7,\"character\":11},\"end\":{\"line\":7,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the types 'client' and '__ViewerType' are unable to be multiplied with the * operator. (Multiply)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_WhenCantRelate_11 = null;
  private String get_WhenCantRelate_11() {
    if (cached_WhenCantRelate_11 != null) {
      return cached_WhenCantRelate_11;
    }
    cached_WhenCantRelate_11 = generateTestOutput(false, "WhenCantRelate_11", "./test_code/BinaryExpression_WhenCantRelate_failure.a");
    return cached_WhenCantRelate_11;
  }

  @Test
  public void testWhenCantRelateFailure() {
    assertLiveFail(get_WhenCantRelate_11());
  }

  @Test
  public void testWhenCantRelateNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_WhenCantRelate_11());
  }

  @Test
  public void testWhenCantRelateExceptionFree() {
    assertExceptionFree(get_WhenCantRelate_11());
  }

  @Test
  public void testWhenCantRelateTODOFree() {
    assertTODOFree(get_WhenCantRelate_11());
  }

  @Test
  public void stable_WhenCantRelate_11() {
    String live = get_WhenCantRelate_11();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BinaryExpression_WhenCantRelate_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":11},\"end\":{\"line\":1,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'bool' is unable to be compared with type 'bool'. (Compare)\"},{\"range\":{\"start\":{\"line\":2,\"character\":11},\"end\":{\"line\":2,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'bool' is unable to be compared with type 'int'. (Compare)\"},{\"range\":{\"start\":{\"line\":3,\"character\":11},\"end\":{\"line\":3,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'int' is unable to be compared with type 'bool'. (Compare)\"},{\"range\":{\"start\":{\"line\":4,\"character\":11},\"end\":{\"line\":4,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'bool' is unable to be compared with type 'double'. (Compare)\"},{\"range\":{\"start\":{\"line\":5,\"character\":11},\"end\":{\"line\":5,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'double' is unable to be compared with type 'bool'. (Compare)\"},{\"range\":{\"start\":{\"line\":6,\"character\":11},\"end\":{\"line\":6,\"character\":25}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'bool' is unable to be compared with type 'client'. (Compare)\"},{\"range\":{\"start\":{\"line\":7,\"character\":11},\"end\":{\"line\":7,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'client' is unable to be compared with type '__ViewerType'. (Compare)\"},{\"range\":{\"start\":{\"line\":11,\"character\":11},\"end\":{\"line\":11,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'bool' is unable to be compared with type 'bool'. (Compare)\"},{\"range\":{\"start\":{\"line\":12,\"character\":11},\"end\":{\"line\":12,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'bool' is unable to be compared with type 'int'. (Compare)\"},{\"range\":{\"start\":{\"line\":13,\"character\":11},\"end\":{\"line\":13,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'int' is unable to be compared with type 'bool'. (Compare)\"},{\"range\":{\"start\":{\"line\":14,\"character\":11},\"end\":{\"line\":14,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'bool' is unable to be compared with type 'double'. (Compare)\"},{\"range\":{\"start\":{\"line\":15,\"character\":11},\"end\":{\"line\":15,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'double' is unable to be compared with type 'bool'. (Compare)\"},{\"range\":{\"start\":{\"line\":16,\"character\":11},\"end\":{\"line\":16,\"character\":25}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'bool' is unable to be compared with type 'client'. (Compare)\"},{\"range\":{\"start\":{\"line\":17,\"character\":11},\"end\":{\"line\":17,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"The type 'client' is unable to be compared with type '__ViewerType'. (Compare)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_WhenCantSubtract_12 = null;
  private String get_WhenCantSubtract_12() {
    if (cached_WhenCantSubtract_12 != null) {
      return cached_WhenCantSubtract_12;
    }
    cached_WhenCantSubtract_12 = generateTestOutput(false, "WhenCantSubtract_12", "./test_code/BinaryExpression_WhenCantSubtract_failure.a");
    return cached_WhenCantSubtract_12;
  }

  @Test
  public void testWhenCantSubtractFailure() {
    assertLiveFail(get_WhenCantSubtract_12());
  }

  @Test
  public void testWhenCantSubtractNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_WhenCantSubtract_12());
  }

  @Test
  public void testWhenCantSubtractExceptionFree() {
    assertExceptionFree(get_WhenCantSubtract_12());
  }

  @Test
  public void testWhenCantSubtractTODOFree() {
    assertTODOFree(get_WhenCantSubtract_12());
  }

  @Test
  public void stable_WhenCantSubtract_12() {
    String live = get_WhenCantSubtract_12();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:BinaryExpression_WhenCantSubtract_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":1,\"character\":11},\"end\":{\"line\":1,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'bool' are unable to be subtracted with the - operator. (Subtracted)\"},{\"range\":{\"start\":{\"line\":2,\"character\":11},\"end\":{\"line\":2,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'int' are unable to be subtracted with the - operator. (Subtracted)\"},{\"range\":{\"start\":{\"line\":3,\"character\":11},\"end\":{\"line\":3,\"character\":19}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'int' and 'bool' are unable to be subtracted with the - operator. (Subtracted)\"},{\"range\":{\"start\":{\"line\":4,\"character\":11},\"end\":{\"line\":4,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'double' are unable to be subtracted with the - operator. (Subtracted)\"},{\"range\":{\"start\":{\"line\":5,\"character\":11},\"end\":{\"line\":5,\"character\":21}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'double' and 'bool' are unable to be subtracted with the - operator. (Subtracted)\"},{\"range\":{\"start\":{\"line\":6,\"character\":11},\"end\":{\"line\":6,\"character\":25}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'bool' and 'client' are unable to be subtracted with the - operator. (Subtracted)\"},{\"range\":{\"start\":{\"line\":7,\"character\":11},\"end\":{\"line\":7,\"character\":23}},\"severity\":1,\"source\":\"error\",\"message\":\"The types 'client' and '__ViewerType' are unable to be subtracted with the - operator. (Subtracted)\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}

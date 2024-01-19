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
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.privacy.DefineCustomPolicy;
import org.adamalang.translator.tree.types.TySimpleReactive;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.reactive.*;
import org.adamalang.translator.tree.types.structures.BubbleDefinition;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.JoinAssoc;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.CanBeMapDomain;
import org.adamalang.translator.tree.types.traits.DetailNeedsSettle;
import org.adamalang.translator.tree.types.traits.IsKillable;
import org.adamalang.translator.tree.types.traits.IsReactiveValue;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailInventDefaultValueExpression;

import java.util.ArrayList;
import java.util.Map;

/** responsible for writing the code for records */
public class CodeGenRecords {
  private static boolean isCommitRevertable(final TyType fieldType) {
    return fieldType instanceof TySimpleReactive || fieldType instanceof TyReactiveMaybe || fieldType instanceof TyReactiveTable || fieldType instanceof TyReactiveRef || fieldType instanceof TyReactiveRecord || fieldType instanceof TyReactiveMap || fieldType instanceof TyReactiveText;
  }

  private static boolean isCommitCache(final FieldDefinition fd, TyType fieldType) {
    if (fieldType instanceof TyReactiveLazy) {
      return fd.servicesToWatch.size() > 0;
    }
    return false;
  }

  private static boolean canRevertOther(String other) {
    if ("__auto_gen".equals(other)) {
      return false;
    }
    if ("__auto_cache_id".equals(other)) {
      return false;
    }
    return !"__cache".equals(other);
  }

  public static void writeCommitAndRevert(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment, final boolean isRoot, final String... others) {
    if (!isRoot) {
      writeFieldOf(storage, sb);
    }
    writeSettles(storage, sb, environment, isRoot);
    writeInsert(storage, sb, environment, isRoot, others);
    writerDump(storage, sb, environment, isRoot, others);
    sb.append("@Override").writeNewline();
    sb.append("public void __commit(String __name, JsonStreamWriter __forward, JsonStreamWriter __reverse) {").tabUp().writeNewline();
    if (!isRoot) {
      sb.append("if (__isDirty()) {").tabUp().writeNewline();
      sb.append("__forward.writeObjectFieldIntro(__name);").writeNewline();
      sb.append("__forward.beginObject();").writeNewline();
      sb.append("__reverse.writeObjectFieldIntro(__name);").writeNewline();
      sb.append("__reverse.beginObject();").writeNewline();
    }
    for (final String other : others) {
      sb.append(other).append(".__commit(\"").append(other).append("\", __forward, __reverse);").writeNewline();
    }
    ArrayList<String> fieldsToKill = new ArrayList<>();
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      final var fieldType = environment.rules.Resolve(fdInOrder.type, false);
      if (isCommitRevertable(fieldType)) {
        sb.append(fieldName).append(".__commit(\"").append(fieldName).append("\", __forward, __reverse);").writeNewline();
      }
      if (isCommitCache(fdInOrder, fieldType)) {
        sb.append(fieldName).append(".get();").writeNewline();
        fieldsToKill.add("__c" + fieldName);
        sb.append("__c").append(fieldName).append(".__commit(\"__c").append(fieldName).append("\", __forward, __reverse);").writeNewline();
      }
      if (fieldType instanceof IsKillable) {
        fieldsToKill.add(fieldName);
      }
    }
    if (!isRoot) {
      sb.append("__forward.endObject();").writeNewline();
      sb.append("__reverse.endObject();").writeNewline();
      sb.append("__lowerDirtyCommit();").tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
    } else {
      sb.append("/* root */").tabDown().writeNewline();
    }
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public void __revert() {").tabUp().writeNewline();
    if (!isRoot) {
      sb.append("if (__isDirty()) {").tabUp().writeNewline();
      sb.append("__isDying = false;").writeNewline();
    }
    for (final String other : others) {
      if (canRevertOther(other)) {
        sb.append(other).append(".__revert();").writeNewline();
      }
    }
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      final var fieldType = environment.rules.Resolve(fdInOrder.type, false);
      if (isCommitRevertable(fieldType)) {
        sb.append(fieldName).append(".__revert();").writeNewline();
      }
      if (isCommitCache(fdInOrder, fieldType)) {
        sb.append(fieldName).append(".get();").writeNewline();
        sb.append("__c").append(fieldName).append(".__revert();").writeNewline();
      }
    }
    if (!isRoot) {
      sb.append("__lowerDirtyRevert();").tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
    } else {
      sb.append("/* root */").tabDown().writeNewline();
    }
    sb.append("}").writeNewline();
    if (!isRoot) {
      sb.append("@Override").writeNewline();
      int n = fieldsToKill.size();
      if (n > 0) {
        sb.append("public void __killFields() {").tabUp().writeNewline();
        for (String fieldToKill : fieldsToKill) {
          sb.append(fieldToKill).append(".__kill();");
          n--;
          if (n == 0) {
            sb.tabDown();
          }
          sb.writeNewline();
        }
        sb.append("}").writeNewline();
      } else {
        sb.append("public void __killFields() {}").writeNewline();
      }
    }
  }

  public static String make(String parent, String className, TyType fieldType, Expression valueOverride, Environment environment, boolean link) {
    StringBuilder result = new StringBuilder();
    if (fieldType instanceof TyReactiveTable) {
      final var numberIndicies = ((TyReactiveRecord) ((TyReactiveTable) fieldType).getEmbeddedType(environment)).storage.indices.size();
      result.append("new RxTable<>(__self, ").append(parent).append(", \"").append(className);
      result.append("\", (RxParent __parent) -> new RTx").append(((TyReactiveTable) fieldType).recordName);
      result.append("(__parent).__link(), ").append(numberIndicies).append(")");
    } else if (fieldType instanceof TyReactiveText) {
      result.append("new RxText(").append(parent).append(",__auto_gen)");
    } else if (fieldType instanceof TyReactiveRecord) {
      result.append("new ").append(fieldType.getJavaConcreteType(environment)).append("(").append(parent).append(")");
      if (link) {
        result.append(".__link()");
      }
    } else if (fieldType instanceof TyReactiveMap) {
      String codec = ((CanBeMapDomain) ((TyReactiveMap) fieldType).domainType).getRxStringCodexName();
      var rangeType = environment.rules.Resolve(((TyReactiveMap) fieldType).getRangeType(environment), true);
      String range = rangeType.getJavaBoxType(environment);
      result.append("new ").append(fieldType.getJavaConcreteType(environment)).append("(").append(parent).append(", new ").append(codec).append("<").append(range).append(">() { @Override public ");
      result.append(range).append(" make(RxParent __parent) { return ");
      result.append(make("__parent", range, rangeType, null, environment, true));
      result.append(";}").append(" })");
    } else if (fieldType instanceof IsReactiveValue) {
      final var javaConcreteType = fieldType.getJavaConcreteType(environment);
      if (fieldType instanceof DetailInventDefaultValueExpression) {
        var defaultValue = ((DetailInventDefaultValueExpression) fieldType).inventDefaultValueExpression(fieldType);
        if (valueOverride != null) {
          defaultValue = valueOverride;
        }
        if (defaultValue != null) {
          defaultValue.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
        }
        result.append("new ").append(javaConcreteType).append("(").append(parent).append(", ");
        defaultValue.writeJava(result, environment.scopeWithComputeContext(ComputeContext.Computation));
        if (fieldType instanceof TyReactiveEnum) {
          String name = ((TyReactiveEnum) fieldType).name;
          result.append(", (__v) -> __EnumFix_").append(name).append("(__v)");
        }
        result.append(")");
      }
    } else if (fieldType instanceof TyReactiveMaybe) {
      // OK, what do I do here... is this bad?
    }
    return result.toString();
  }

  public static void writeCommonBetweenRecordAndRoot(final StructureStorage storage, final StringBuilderWithTabs classConstructorX, final StringBuilderWithTabs classLinker, final StringBuilderWithTabs classFields, final Environment environment, final boolean injectRootObject) {
    if (injectRootObject) {
      classConstructorX.append("super(__owner);").writeNewline();
      classConstructorX.append("this.__this = this;").writeNewline();
    }
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      final var fieldType = environment.rules.Resolve(fdInOrder.type, false);
      if (fieldType instanceof TyReactiveLazy && fdInOrder.computeExpression != null) {
        final var lazyType = ((TyReactiveLazy) fieldType).getEmbeddedType(environment);
        String boxType = lazyType.getJavaBoxType(environment);
        classFields.append("private final RxLazy<" + boxType + "> " + fieldName + ";").writeNewline();

        boolean hasCache = !fdInOrder.servicesToWatch.isEmpty();
        if (hasCache) {
          classFields.append("private final RxCache __c").append(fieldName).append(";").writeNewline();
          classConstructorX.append("__c").append(fieldName).append(" = new RxCache(__self, this);").writeNewline();
        }
        classConstructorX.append(fieldName).append(" = new RxLazy<").append(lazyType.getJavaBoxType(environment));
        if (hasCache) {
          classConstructorX.append(">(this,__c").append(fieldName).append(".wrap(() -> (").append(boxType).append(")(");
          fdInOrder.computeExpression.writeJava(classConstructorX, environment.scopeWithCache("__c" + fieldName).scopeWithComputeContext(ComputeContext.Computation));
          classConstructorX.append("))");
        } else {
          classConstructorX.append(">(this, () -> (").append(boxType).append(")(");
          fdInOrder.computeExpression.writeJava(classConstructorX, environment.scopeWithCache("__c" + fieldName).scopeWithComputeContext(ComputeContext.Computation));
          classConstructorX.append(")");
        }
        if (environment.state.options.instrumentPerf) {
          classConstructorX.append(", () -> __perf.measure(\"").append("f_" + storage.name.text + "_" + fdInOrder.name).append("\")");
        } else {
          classConstructorX.append(", null");
        }
        classConstructorX.append(");").writeNewline();
        for (final String tableToWatch : fdInOrder.tablesToInject) {
          classFields.append("private final RxTableGuard __").append(fieldName).append("_").append(tableToWatch).append(";").writeNewline();
          classConstructorX.append("__").append(fieldName).append("_").append(tableToWatch).append(" = new RxTableGuard(").append(fieldName).append(");").writeNewline();
        }
        environment.define(fieldName, new TyReactiveLazy(lazyType), false, fdInOrder);
        for (final String watched : fdInOrder.variablesToWatch) {
          classLinker.append(watched).append(".__subscribe(").append(fieldName).append(");").writeNewline();
        }
        for (final String watched : fdInOrder.tablesToInject) {
          // TODO: REMOVE THIS LINE ONCE TABLE EVENTS ARE BEING PRODUCED! SUPER AWESOME MODE!
          // classLinker.append(watched).append(".__subscribe(").append(fieldName).append(");").writeNewline();
          classLinker.append(watched).append(".__subscribe(__").append(fieldName).append("_").append(watched).append(");").writeNewline();
          classLinker.append(fieldName).append(".__guard(").append(watched).append(",__").append(fieldName).append("_").append(watched).append(");").writeNewline();
        }
        if (hasCache) {
          classConstructorX.append("__c").append(fieldName).append(".__subscribe(").append(fieldName).append(");").writeNewline();
        }
        if (injectRootObject) {
          classConstructorX.append(fieldName).append(".__subscribe(this);").writeNewline();
        }
        continue;
      }
      if (fieldType instanceof TyReactiveLazy && fdInOrder.computeExpression != null) {
        classConstructorX.append(fieldName).append(".__link();").writeNewline();
      }
      final var javaConcreteType = fieldType.getJavaConcreteType(environment);
      classFields.append("private final ").append(javaConcreteType).append(" ").append(fieldName).append(";").writeNewline();
      if (fieldType instanceof TyReactiveTable || fieldType instanceof TyReactiveText || fieldType instanceof TyReactiveMap || fieldType instanceof TyReactiveRecord) {
        classConstructorX.append(fieldName).append(" = ").append(make("this", fieldName, fieldType, null, environment, false)).append(";").writeNewline();
        if (fieldType instanceof TyReactiveRecord) {
          classLinker.append(fieldName + ".__link();").writeNewline();
        }
      } else if (fieldType instanceof IsReactiveValue) {
        classConstructorX.append(fieldName).append(" = ").append(make("this", fieldName, fieldType, fdInOrder.defaultValueOverride, environment, true)).append(";").writeNewline();
      } else if (fieldType instanceof TyReactiveMaybe) {
        var doImmediateGet = false;
        boolean doSetDefaultValueOverride = false;
        final var elementType = ((DetailContainsAnEmbeddedType) fieldType).getEmbeddedType(environment);
        String primary = elementType.getJavaBoxType(environment);
        final String secondary;
        if (elementType instanceof DetailComputeRequiresGet) {
          secondary = environment.rules.Resolve(((DetailComputeRequiresGet) elementType).typeAfterGet(environment), false).getJavaBoxType(environment);
        } else {
          secondary = primary;
        }
        classConstructorX.append(fieldName).append(" = new RxMaybe<").append(primary).append(",").append(secondary).append(">(this, (RxParent __parent) -> ");
        if (elementType instanceof TyReactiveRecord) {
          classConstructorX.append("new ").append(elementType.getJavaConcreteType(environment)).append("(__parent).__link()");
        } else if (elementType instanceof DetailInventDefaultValueExpression && elementType instanceof IsReactiveValue) {
          var defaultValue = ((DetailInventDefaultValueExpression) elementType).inventDefaultValueExpression(fieldType);
          if (defaultValue != null) {
            defaultValue.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
          }
          if (fdInOrder.defaultValueOverride != null) {
            TyType valueOverrideType = fdInOrder.defaultValueOverride.typing(environment.scopeWithComputeContext(ComputeContext.Computation), null);
            if (valueOverrideType != null && environment.rules.IsMaybe(valueOverrideType, true)) {
              doSetDefaultValueOverride = true;
            } else {
              doImmediateGet = true;
              defaultValue = fdInOrder.defaultValueOverride;
            }
          }
          classConstructorX.append("new " + elementType.getJavaConcreteType(environment) + "(__parent, ");
          defaultValue.writeJava(classConstructorX, environment.scopeWithComputeContext(ComputeContext.Computation));
          if (elementType instanceof TyReactiveEnum) {
            String name = ((TyReactiveEnum) elementType).name;
            classConstructorX.append(", (__v) -> __EnumFix_").append(name).append("(__v)");
          }
          classConstructorX.append(")");
          if (elementType instanceof TyReactiveRecord) {
            classLinker.append(fieldName + ".__link()");
          }
        }
        classConstructorX.append(");").writeNewline();
        if (doSetDefaultValueOverride) {
          classConstructorX.append(fieldName).append(".set(");
          fdInOrder.defaultValueOverride.writeJava(classConstructorX, environment.scopeWithComputeContext(ComputeContext.Computation));
          classConstructorX.append(");").writeNewline();
        }
        if (doImmediateGet) {
          classConstructorX.append(fieldName).append(".make();").writeNewline();
        }
      }
      environment.define(fieldName, fieldType, false, fieldType);
    }

    for (final BubbleDefinition bubble : storage.bubbles.values()) {
      classFields.append("private final RxGuard ___" + bubble.nameToken.text + ";").writeNewline();
      classConstructorX.append("___").append(bubble.nameToken.text).append(" =  new RxGuard(this);").writeNewline();
      for (final String tableToWatch : bubble.tablesToWatch) {
        classFields.append("private final RxTableGuard __").append(bubble.nameToken.text).append("_").append(tableToWatch).append(";").writeNewline();
        classConstructorX.append("__").append(bubble.nameToken.text).append("_").append(tableToWatch).append(" = new RxTableGuard(___").append(bubble.nameToken.text).append(");").writeNewline();
      }
      for (final String watched : bubble.variablesToWatch) {
        classLinker.append(watched).append(".__subscribe(").append("___").append(bubble.nameToken.text).append(");").writeNewline();
      }
      for (final String watched : bubble.tablesToWatch) {
        // TODO: REMOVE THIS LINE ONCE TABLE EVENTS ARE BEING PRODUCED! SUPER AWESOME MODE!
        // classLinker.append(watched).append(".__subscribe(").append("___").append(bubble.nameToken.text).append(");").writeNewline();
        classLinker.append(watched).append(".__subscribe(__").append(bubble.nameToken.text).append("_").append(watched).append(");").writeNewline();
        classLinker.append("___").append(bubble.nameToken.text).append(".__guard(").append(watched).append(",__").append(bubble.nameToken.text).append("_").append(watched).append(");").writeNewline();
      }
    }

    CodeGenJoins.writeJoins(storage, classLinker, environment);
  }

  public static void writeFieldOf(final StructureStorage storage, final StringBuilderWithTabs sb) {
    sb.append("@Override").writeNewline();
    sb.append("public Object __fieldOf(String __name) {").tabUp().writeNewline();
    sb.append("switch (__name) {").tabUp().writeNewline();
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      sb.append("case \"").append(fieldName).append("\":").tabUp().writeNewline();
      sb.append("return ").append(fieldName).append(";").tabDown().writeNewline();
    }
    sb.append("default:").tabUp().writeNewline();
    sb.append("return null;").tabDown().tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }

  public static void writeSettles(final StructureStorage storage, final StringBuilderWithTabs sb, Environment environment, boolean isRoot) {
    ArrayList<String> thingsToSettle = new ArrayList<>();
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      final var fieldType = environment.rules.Resolve(fdInOrder.type, false);
      if (fieldType instanceof TyReactiveLazy && fdInOrder.computeExpression != null) {
        thingsToSettle.add(fieldName);
        for (final String watched : fdInOrder.tablesToInject) {
          thingsToSettle.add("__" + fieldName + "_" + watched);
        }
      } else if (fieldType instanceof DetailNeedsSettle) {
        thingsToSettle.add(fieldName);
      }
    }
    for (final BubbleDefinition bubble : storage.bubbles.values()) {
      thingsToSettle.add("___" + bubble.nameToken.text);
      for (final String watched : bubble.tablesToWatch) {
        thingsToSettle.add("__" + bubble.nameToken.text + "_" + watched);
      }
    }
    int n = thingsToSettle.size();
    if (isRoot) {
      if (n == 0) {
        sb.append("@Override").writeNewline();
        sb.append("public void __settle(Set<Integer> __viewers) {").writeNewline();
        sb.append("}").writeNewline();
      } else {
        sb.append("@Override").writeNewline();
        sb.append("public void __settle(Set<Integer> __viewers) {").tabUp().writeNewline();
        for (int k = 0; k < n; k++) {
          sb.append(thingsToSettle.get(k)).append(".__settle(__viewers);");
          if (k == n - 1) {
            sb.tabDown();
          }
          sb.writeNewline();
        }
        sb.append("}").writeNewline();
      }
    } else {
      sb.append("@Override").writeNewline();
      sb.append("public void __settle(Set<Integer> __viewers) {").tabUp().writeNewline();
      for (int k = 0; k < n; k++) {
        sb.append(thingsToSettle.get(k)).append(".__settle(__viewers);").writeNewline();
      }
      sb.append("__lowerInvalid();").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
  }

  public static void writeInsert(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment, final boolean isRoot, final String... others) {
    sb.append("@Override").writeNewline();
    sb.append("public void __insert(JsonStreamReader __reader) {").tabUp().writeNewline();
    sb.append("if (__reader.startObject()) {").tabUp().writeNewline();
    sb.append("while(__reader.notEndOfObject()) {").tabUp().writeNewline();
    sb.append("String __fieldName = __reader.fieldName();").writeNewline();
    sb.append("switch (__fieldName) {").tabUp().writeNewline();
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      final var fieldType = environment.rules.Resolve(fdInOrder.type, false);
      if (isCommitRevertable(fieldType)) {
        sb.append("case \"").append(fieldName).append("\":").tabUp().writeNewline();
        sb.append(fieldName).append(".__insert(__reader);").writeNewline();
        sb.append("break;").tabDown().writeNewline();
      }
      if (isCommitCache(fdInOrder, fieldType)) {
        sb.append("case \"__c").append(fieldName).append("\":").tabUp().writeNewline();
        sb.append("__c").append(fieldName).append(".__insert(__reader);").writeNewline();
        sb.append("break;").tabDown().writeNewline();
      }
    }
    for (final String other : others) {
      sb.append("case \"").append(other).append("\":").tabUp().writeNewline();
      sb.append(other).append(".__insert(__reader);").writeNewline();
      if (other.equals("__timezone")) {
        sb.append("__timezoneCachedZoneId = ZoneId.of(__timezone.get());").writeNewline();
      }
      sb.append("break;").tabDown().writeNewline();
    }
    if (isRoot) {
      sb.append("case \"__dedupe\":").tabUp().writeNewline();
      sb.append("__hydrateDeduper(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
      sb.append("case \"__clients\":").tabUp().writeNewline();
      sb.append("__hydrateClients(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
      sb.append("case \"__messages\":").tabUp().writeNewline();
      sb.append("__hydrateMessages(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
      sb.append("case \"__enqueued\":").tabUp().writeNewline();
      sb.append("__hydrateEnqueuedTaskManager(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
      sb.append("case \"__webqueue\":").tabUp().writeNewline();
      sb.append("__hydrateWebQueue(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
      sb.append("case \"__timeouts\":").tabUp().writeNewline();
      sb.append("__hydrateTimeouts(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
      sb.append("case \"__replication\":").tabUp().writeNewline();
      sb.append("__hydrateReplicationEngine(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
    }
    sb.append("default:").tabUp().writeNewline();
    sb.append("__reader.skipValue();").tabDown().tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
    // patch
    sb.append("@Override").writeNewline();
    sb.append("public void __patch(JsonStreamReader __reader) {").tabUp().writeNewline();
    sb.append("if (__reader.startObject()) {").tabUp().writeNewline();
    sb.append("while(__reader.notEndOfObject()) {").tabUp().writeNewline();
    sb.append("String __fieldName = __reader.fieldName();").writeNewline();
    sb.append("switch (__fieldName) {").tabUp().writeNewline();
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      final var fieldType = environment.rules.Resolve(fdInOrder.type, false);
      if (isCommitRevertable(fieldType)) {
        sb.append("case \"").append(fieldName).append("\":").tabUp().writeNewline();
        sb.append(fieldName).append(".__patch(__reader);").writeNewline();
        sb.append("break;").tabDown().writeNewline();
      }
      if (isCommitCache(fdInOrder, fieldType)) {
        sb.append("case \"__c").append(fieldName).append("\":").tabUp().writeNewline();
        sb.append("__c").append(fieldName).append(".__patch(__reader);").writeNewline();
        sb.append("break;").tabDown().writeNewline();
      }
    }
    for (final String other : others) {
      sb.append("case \"").append(other).append("\":").tabUp().writeNewline();
      sb.append(other).append(".__patch(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
    }
    if (isRoot) {
      sb.append("case \"__dedupe\":").tabUp().writeNewline();
      sb.append("__hydrateDeduper(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
      sb.append("case \"__clients\":").tabUp().writeNewline();
      sb.append("__hydrateClients(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
      sb.append("case \"__messages\":").tabUp().writeNewline();
      sb.append("__hydrateMessages(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
      sb.append("case \"__enqueued\":").tabUp().writeNewline();
      sb.append("__hydrateEnqueuedTaskManager(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
      sb.append("case \"__webqueue\":").tabUp().writeNewline();
      sb.append("__hydrateWebQueue(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
      sb.append("case \"__timeouts\":").tabUp().writeNewline();
      sb.append("__hydrateTimeouts(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
      sb.append("case \"__replication\":").tabUp().writeNewline();
      sb.append("__hydrateReplicationEngine(__reader);").writeNewline();
      sb.append("break;").tabDown().writeNewline();
    }
    sb.append("default:").tabUp().writeNewline();
    sb.append("__reader.skipValue();").tabDown().tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }

  public static void writePrivacyCommonBetweenRecordAndRoot(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    final var policyRoot = environment.scopeAsReadOnlyBoundary();
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      final var fieldType = environment.rules.Resolve(fdInOrder.type, false);
      policyRoot.define(fieldName, fieldType, true, fieldType);
    }
    for (final Map.Entry<String, DefineCustomPolicy> customPolicyEntry : storage.policies.entrySet()) {
      final var policyExec = customPolicyEntry.getValue().scope(policyRoot, customPolicyEntry.getValue());
      sb.append("public boolean __POLICY_").append(customPolicyEntry.getKey()).append("(NtPrincipal __who)");
      customPolicyEntry.getValue().code.typing(policyExec);
      customPolicyEntry.getValue().code.writeJava(sb, policyExec);
      sb.writeNewline();
    }
    for (final Map.Entry<String, BubbleDefinition> bubbleDefinitionEntry : storage.bubbles.entrySet()) {
      bubbleDefinitionEntry.getValue().writeSetup(sb, environment);
    }
    sb.append("@Override").writeNewline();
    sb.append("public long __memory() {").tabUp().writeNewline();
    sb.append("long __sum = super.__memory();").writeNewline();
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      sb.append("__sum += ").append(fdInOrder.name).append(".__memory();").writeNewline();
    }
    sb.append("return __sum;").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }

  public static void writerDump(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment, final boolean isRoot, final String... others) {
    sb.append("@Override").writeNewline();
    sb.append("public void __dump(JsonStreamWriter __writer) {").tabUp().writeNewline();
    sb.append("__writer.beginObject();").writeNewline();
    if (isRoot) {
      sb.append("__writer.writeObjectFieldIntro(\"__snapshot\");").writeNewline();
      sb.append("__writer.writeString(__space + \"/\" + __key);");
    }
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      final var fieldType = environment.rules.Resolve(fdInOrder.type, false);
      if (isCommitRevertable(fieldType)) {
        sb.append("__writer.writeObjectFieldIntro(\"").append(fieldName).append("\");").writeNewline();
        sb.append(fieldName).append(".__dump(__writer);").writeNewline();
      }
      if (isCommitCache(fdInOrder, fieldType)) {
        sb.append("__writer.writeObjectFieldIntro(\"__c").append(fieldName).append("\");").writeNewline();
        sb.append("__c").append(fieldName).append(".__dump(__writer);").writeNewline();
      }
    }
    for (final String otherField : others) {
      sb.append("__writer.writeObjectFieldIntro(\"").append(otherField).append("\");").writeNewline();
      sb.append(otherField).append(".__dump(__writer);").writeNewline();
    }
    if (isRoot) {
      sb.append("__dumpDeduper(__writer);").writeNewline();
      sb.append("__dumpClients(__writer);").writeNewline();
      sb.append("__dumpMessages(__writer);").writeNewline();
      sb.append("__dumpEnqueuedTaskManager(__writer);").writeNewline();
      sb.append("__dumpTimeouts(__writer);").writeNewline();
      sb.append("__dumpWebQueue(__writer);").writeNewline();
      sb.append("__dumpReplicationEngine(__writer);").writeNewline();
    }
    sb.append("__writer.endObject();").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }

  public static void writeRootDocument(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    final var classConstructor = new StringBuilderWithTabs().tabUp().tabUp();
    final var classFields = new StringBuilderWithTabs().tabUp();
    final var classLinker = new StringBuilderWithTabs().tabUp().tabUp();
    writeCommonBetweenRecordAndRoot(storage, classConstructor, classLinker, classFields, environment, false);
    final var trimedClassFields = classFields.toString().stripTrailing();
    if (trimedClassFields.length() > 0) {
      sb.append(trimedClassFields).writeNewline();
    }
    writePrivacyCommonBetweenRecordAndRoot(storage, sb, environment);
    if (classConstructor.toString().length() > 0) {
      sb.append("public " + environment.document.getClassName() + "(DocumentMonitor __monitor) {").tabUp().writeNewline();
      sb.append("super(__monitor);").writeNewline();
      sb.append(classConstructor.toString().stripTrailing()).writeNewline();
      String linkerCompact = classLinker.toString().trim();
      if (linkerCompact.length() > 0) {
        sb.append(linkerCompact).writeNewline();
      }
    } else {
      sb.append("public " + environment.document.getClassName() + "(DocumentMonitor __monitor) {").tabUp().writeNewline();
      sb.append("super(__monitor);").writeNewline();
    }
    sb.append("__goodwillBudget = ").append(environment.state.options.goodwillBudget + ";").writeNewline();
    sb.append("__goodwillLimitOfBudget = ").append(environment.state.options.goodwillBudget + ";").tabDown().writeNewline();
    sb.append("}").writeNewline();
    writeCommitAndRevert(storage, sb, environment, true, "__state", "__constructed", "__next_time", "__last_expire_time", "__blocked", "__seq", "__entropy", "__auto_future_id", "__connection_id", "__message_id", "__time", "__timezone", "__auto_table_row_id", "__auto_gen", "__auto_cache_id", "__cache", "__webTaskId");
    CodeGenDocumentPolicyCache.writeRecordDeltaClass(storage, sb);
    CodeGenDeltaClass.writeRecordDeltaClass(storage, sb, environment, environment.document.getClassName(), true);
    sb.append("@Override").writeNewline();
    sb.append("public Set<String> __get_intern_strings() {").tabUp().writeNewline();
    sb.append("HashSet<String> __interns = new HashSet<>();").writeNewline();
    for (String intern : environment.interns) {
      sb.append("__interns.add(").append(intern).append(");").writeNewline();
    }
    sb.append("return __interns;").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public PrivateView __createPrivateView(NtPrincipal __who, Perspective ___perspective) {").tabUp().writeNewline();
    sb.append(environment.document.getClassName()).append(" __self = this;").writeNewline();
    sb.append("Delta").append(environment.document.getClassName()).append(" __state = new Delta").append(environment.document.getClassName()).append("();").writeNewline();
    sb.append("RTx__ViewerType __viewerState = new RTx__ViewerType();").writeNewline();
    sb.append("int __viewId = __genViewId();").writeNewline();
    sb.append("return new PrivateView(__viewId, __who, ___perspective) {").tabUp().writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public long memory() {").tabUp().writeNewline();
    sb.append("return __state.__memory();").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public void dumpViewer(JsonStreamWriter __writer) {").tabUp().writeNewline();
    sb.append("__viewerState.__writeOut(__writer);").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public void ingest(JsonStreamReader __reader) {").tabUp().writeNewline();
    sb.append("__viewerState.__ingest(__reader);").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public void update(JsonStreamWriter __writer) {").tabUp().writeNewline();
    sb.append("__state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer, __viewerState, __viewId));").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("};").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }
}

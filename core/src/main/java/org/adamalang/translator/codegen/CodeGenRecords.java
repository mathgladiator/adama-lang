/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.CanBeMapDomain;
import org.adamalang.translator.tree.types.traits.IsReactiveValue;
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
    if ("__cache".equals(other)) {
      return false;
    }
    return true;
  }

  public static void writeCommitAndRevert(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment, final boolean isRoot, final String... others) {
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

  public static String make(String parent, String className, TyType fieldType, Expression valueOverride, Environment environment) {
    StringBuilder result = new StringBuilder();
    if (fieldType instanceof TyReactiveTable) {
      final var numberIndicies = ((TyReactiveRecord) ((TyReactiveTable) fieldType).getEmbeddedType(environment)).storage.indices.size();
      result.append("new RxTable<>(__self, ").append(parent).append(", \"").append(className);
      result.append("\", (RxParent __parent) -> new RTx").append(((TyReactiveTable) fieldType).recordName);
      result.append("(__parent), ").append(numberIndicies).append(")");
    } else if (fieldType instanceof TyReactiveText) {
      result.append("new RxText(").append(parent).append(",__auto_gen)");
    } else if (fieldType instanceof TyReactiveRecord) {
      result.append("new ").append(fieldType.getJavaConcreteType(environment)).append("(").append(parent).append(")");
    } else if (fieldType instanceof TyReactiveMap) {
      String codec = ((CanBeMapDomain) ((TyReactiveMap) fieldType).domainType).getRxStringCodexName();
      var rangeType = ((TyReactiveMap) fieldType).getRangeType(environment);
      String range = rangeType.getJavaBoxType(environment);
      result.append("new ").append(fieldType.getJavaConcreteType(environment)).append("(").append(parent).append(", new ").append(codec).append("<").append(range).append(">() { @Override public ");
      result.append(range).append(" make(RxParent __parent) { return ");
      result.append(make("__parent", range, rangeType, null, environment));
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
        result.append(")");
      }
    } else if (fieldType instanceof TyReactiveMaybe) {

    }
    return result.toString();
  }

  public static void writeCommonBetweenRecordAndRoot(final StructureStorage storage, final StringBuilderWithTabs classConstructorX, final StringBuilderWithTabs classFields, final Environment environment, final boolean injectRootObject) {
    if (injectRootObject) {
      classConstructorX.append("super(__owner);").writeNewline();
    }
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      final var fieldType = environment.rules.Resolve(fdInOrder.type, false);
      if (fieldType instanceof TyReactiveLazy && fdInOrder.computeExpression != null) {
        final var lazyType = ((TyReactiveLazy) fieldType).getEmbeddedType(environment);
        classFields.append("private final RxLazy<" + lazyType.getJavaBoxType(environment) + "> " + fieldName + ";").writeNewline();
        boolean hasCache = !fdInOrder.servicesToWatch.isEmpty();
        if (hasCache) {
          classFields.append("private final RxCache __c").append(fieldName).append(";");
          classConstructorX.append("__c").append(fieldName).append(" = new RxCache(__self, this);").writeNewline();
        }
        classConstructorX.append(fieldName).append(" = new RxLazy<").append(lazyType.getJavaBoxType(environment));
        if (hasCache) {
          classConstructorX.append(">(this,__c").append(fieldName).append(".wrap(() -> (");
          fdInOrder.computeExpression.writeJava(classConstructorX, environment.scopeWithCache("__c" + fieldName).scopeWithComputeContext(ComputeContext.Computation));
          classConstructorX.append(")));").writeNewline();
        } else {
          classConstructorX.append(">(this, () -> (");
          fdInOrder.computeExpression.writeJava(classConstructorX, environment.scopeWithCache("__c" + fieldName).scopeWithComputeContext(ComputeContext.Computation));
          classConstructorX.append("));").writeNewline();
        }
        environment.define(fieldName, new TyReactiveLazy(lazyType), false, fdInOrder);
        for (final String watched : fdInOrder.variablesToWatch) {
          classConstructorX.append(watched).append(".__subscribe(").append(fieldName).append(");").writeNewline();
        }
        if (hasCache) {
          classConstructorX.append("__c").append(fieldName).append(".__subscribe(").append(fieldName).append(");").writeNewline();
        }
        if (injectRootObject) {
          classConstructorX.append(fieldName).append(".__subscribe(this);").writeNewline();
        }
        continue;
      }
      final var javaConcreteType = fieldType.getJavaConcreteType(environment);
      classFields.append("private final ").append(javaConcreteType).append(" ").append(fieldName).append(";").writeNewline();
      if (fieldType instanceof TyReactiveTable || fieldType instanceof TyReactiveText || fieldType instanceof TyReactiveMap || fieldType instanceof TyReactiveRecord) {
        classConstructorX.append(fieldName).append(" = ").append(make("this", fieldName, fieldType, null, environment)).append(";").writeNewline();
      } else if (fieldType instanceof IsReactiveValue) {
        classConstructorX.append(fieldName).append(" = ").append(make("this", fieldName, fieldType, fdInOrder.defaultValueOverride, environment)).append(";").writeNewline();
      } else if (fieldType instanceof TyReactiveMaybe) {
        var doImmediateGet = false;
        boolean doSetDefaultValueOverride = false;
        final var elementType = ((DetailContainsAnEmbeddedType) fieldType).getEmbeddedType(environment);
        classConstructorX.append(fieldName).append(" = new RxMaybe<>(this, (RxParent __parent) -> ");
        if (elementType instanceof TyReactiveRecord) {
          classConstructorX.append("new ").append(elementType.getJavaConcreteType(environment)).append("(__parent)");
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
          classConstructorX.append(")");
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
      classConstructorX.append("___").append(bubble.nameToken.text).append(" =  new RxGuard();").writeNewline();
      for (final String watched : bubble.variablesToWatch) {
        classConstructorX.append(watched).append(".__subscribe(").append("___").append(bubble.nameToken.text).append(");").writeNewline();
      }
    }
  }

  public static void writeIndexConstant(final String name, final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("private static String[] __INDEX_COLUMNS_").append(name).append(" = new String[] {");
    boolean first = true;
    for (final Map.Entry<String, FieldDefinition> entry : storage.fields.entrySet()) {
      if ("id".equals(entry.getKey())) {
        continue;
      }
      final var fieldType = environment.rules.Resolve(entry.getValue().type, false);
      if (fieldType instanceof TyReactiveInteger || fieldType instanceof TyReactiveEnum || fieldType instanceof TyReactivePrincipal) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append("\"").append(entry.getKey()).append("\"");
      }
    }
    sb.append("};").writeNewline();
  }

  public static void writeIndices(final String name, final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    boolean first;
    sb.append("@Override").writeNewline();
    sb.append("public String[] __getIndexColumns() {").tabUp().writeNewline();
    sb.append("return __INDEX_COLUMNS_").append(name).append(";").tabDown().writeNewline();
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public int[] __getIndexValues() {").tabUp().writeNewline();
    sb.append("return new int[] {");
    first = true;
    for (final Map.Entry<String, FieldDefinition> entry : storage.fields.entrySet()) {
      if ("id".equals(entry.getKey())) {
        continue;
      }
      final var fieldType = environment.rules.Resolve(entry.getValue().type, false);
      if (fieldType instanceof TyReactiveInteger || fieldType instanceof TyReactiveEnum || fieldType instanceof TyReactivePrincipal) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append("").append(entry.getKey()).append(".getIndexValue()");
      }
    }
    sb.append("};").tabDown().writeNewline();
    sb.append("}").writeNewline();
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
    }
    sb.append("__writer.endObject();").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }

  public static void writeRootDocument(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    final var classConstructor = new StringBuilderWithTabs().tabUp().tabUp();
    final var classFields = new StringBuilderWithTabs().tabUp();
    writeCommonBetweenRecordAndRoot(storage, classConstructor, classFields, environment, false);
    final var trimedClassFields = classFields.toString().stripTrailing();
    if (trimedClassFields.length() > 0) {
      sb.append(trimedClassFields).writeNewline();
    }
    writePrivacyCommonBetweenRecordAndRoot(storage, sb, environment);
    if (classConstructor.toString().length() > 0) {
      sb.append("public " + environment.document.getClassName() + "(DocumentMonitor __monitor) {").tabUp().writeNewline();
      sb.append("super(__monitor);").writeNewline();
      sb.append(classConstructor.toString().stripTrailing()).writeNewline();
    } else {
      sb.append("public " + environment.document.getClassName() + "(DocumentMonitor __monitor) {").tabUp().writeNewline();
      sb.append("super(__monitor);").writeNewline();
    }
    sb.append("__goodwillBudget = ").append(environment.state.options.goodwillBudget + ";").writeNewline();
    sb.append("__goodwillLimitOfBudget = ").append(environment.state.options.goodwillBudget + ";").tabDown().writeNewline();
    sb.append("}").writeNewline();
    writeCommitAndRevert(storage, sb, environment, true, "__state", "__constructed", "__next_time", "__last_expire_time", "__blocked", "__seq", "__entropy", "__auto_future_id", "__connection_id", "__message_id", "__time", "__auto_table_row_id", "__auto_gen", "__auto_cache_id", "__cache");
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
    sb.append("public PrivateView __createPrivateView(NtPrincipal __who, Perspective ___perspective, AssetIdEncoder __encoder) {").tabUp().writeNewline();
    sb.append(environment.document.getClassName()).append(" __self = this;").writeNewline();
    sb.append("Delta").append(environment.document.getClassName()).append(" __state = new Delta").append(environment.document.getClassName()).append("();").writeNewline();
    sb.append("RTx__ViewerType __viewerState = new RTx__ViewerType();").writeNewline();
    sb.append("return new PrivateView(__who, ___perspective, __encoder) {").tabUp().writeNewline();
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
    sb.append("__state.show(__self, PrivateLazyDeltaWriter.bind(__who, __writer, __viewerState, __encoder));").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("};").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }
}

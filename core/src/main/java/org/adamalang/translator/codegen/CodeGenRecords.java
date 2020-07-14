/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.codegen;

import java.util.Map;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.privacy.DefineCustomPolicy;
import org.adamalang.translator.tree.types.TySimpleReactive;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;
import org.adamalang.translator.tree.types.reactive.TyReactiveClient;
import org.adamalang.translator.tree.types.reactive.TyReactiveEnum;
import org.adamalang.translator.tree.types.reactive.TyReactiveInteger;
import org.adamalang.translator.tree.types.reactive.TyReactiveLazy;
import org.adamalang.translator.tree.types.reactive.TyReactiveMaybe;
import org.adamalang.translator.tree.types.reactive.TyReactiveRecord;
import org.adamalang.translator.tree.types.reactive.TyReactiveRef;
import org.adamalang.translator.tree.types.reactive.TyReactiveTable;
import org.adamalang.translator.tree.types.structures.BubbleDefinition;
import org.adamalang.translator.tree.types.structures.DefineMethod;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.IsReactiveValue;
import org.adamalang.translator.tree.types.traits.details.DetailContainsAnEmbeddedType;
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;
import org.adamalang.translator.tree.types.traits.details.DetailInventDefaultValueExpression;

/** responsible for writing the code for records */
public class CodeGenRecords {
  private static boolean isCommitRevertable(final TyType fieldType) {
    return fieldType instanceof TySimpleReactive || fieldType instanceof TyReactiveMaybe || fieldType instanceof TyReactiveTable || fieldType instanceof TyReactiveRef || fieldType instanceof TyReactiveRecord;
  }

  public static void writeCommitAndRevert(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment, final boolean isRoot, final String... others) {
    sb.append("@Override").writeNewline();
    sb.append("public void __commit(String __name, ObjectNode __delta) {").tabUp().writeNewline();
    if (!isRoot) {
      sb.append("if (__isDirty()) {").tabUp().writeNewline();
      sb.append("ObjectNode __child = __delta.putObject(__name);").writeNewline();
    } else {
      sb.append("ObjectNode __child = __delta;").writeNewline();
    }
    for (final String other : others) {
      sb.append(other).append(".__commit(\"").append(other).append("\", __child);").writeNewline();
    }
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      final var fieldType = environment.rules.Resolve(fdInOrder.type, false);
      if (fieldType == null) {
        continue;
      }
      if (isCommitRevertable(fieldType)) {
        sb.append(fieldName).append(".__commit(\"").append(fieldName).append("\", __child);").writeNewline();
      }
    }
    if (!isRoot) {
      sb.append("__lowerDirtyRevert();").tabDown().writeNewline();
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
      sb.append(other).append(".__revert();").writeNewline();
    }
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      final var fieldType = environment.rules.Resolve(fdInOrder.type, false);
      if (fieldType == null) {
        continue;
      }
      if (isCommitRevertable(fieldType)) {
        sb.append(fieldName).append(".__revert();").writeNewline();
      }
    }
    if (!isRoot) {
      sb.append("__lowerDirtyRevert();").tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
    } else {
      sb.append("/* root */").tabDown().writeNewline();
    }
    sb.append("}").writeNewline();
  }

  public static void writeCommonBetweenRecordAndRoot(final StructureStorage storage, final StringBuilderWithTabs classConstructorX, final StringBuilderWithTabs classFields, final Environment environment, final String rootObject,
      final boolean injectRootObject) {
    if (injectRootObject) {
      classConstructorX.append("super(__owner);").writeNewline();
    }
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      final var fieldType = environment.rules.Resolve(fdInOrder.type, false);
      if (fieldType == null) {
        continue;
      }
      if (fieldType instanceof TyReactiveLazy && fdInOrder.computeExpression != null) {
        final var lazyType = ((TyReactiveLazy) fieldType).getEmbeddedType(environment);
        if (lazyType != null) {
          classFields.append("private final RxLazy<" + lazyType.getJavaBoxType(environment) + "> " + fieldName + ";").writeNewline();
        }
        classConstructorX.append(fieldName).append(" = new RxLazy<>(this, ");
        classConstructorX.append(((DetailHasBridge) lazyType).getBridge(environment));
        classConstructorX.append(", () -> (");
        fdInOrder.computeExpression.writeJava(classConstructorX, environment.scopeWithComputeContext(ComputeContext.Computation));
        classConstructorX.append("));").writeNewline();
        environment.define(fieldName, new TyReactiveLazy(lazyType), false, fdInOrder);
        for (final String watched : fdInOrder.variablesToWatch) {
          classConstructorX.append(watched).append(".__subscribe(").append(fieldName).append(");").writeNewline();
        }
        if (injectRootObject) {
          classConstructorX.append(fieldName).append(".__subscribe(this);").writeNewline();
        }
        continue;
      }
      final var javaConcreteType = fieldType.getJavaConcreteType(environment);
      classFields.append("private final " + javaConcreteType + " " + fieldName + ";").writeNewline();
      if (fieldType instanceof TyReactiveTable) {
        classConstructorX.append(fieldName).append(" = RxFactory.makeRxTable(__self, this, ").append(rootObject).append(", \"").append(fieldName).append("\", __BRIDGE_").append(((TyReactiveTable) fieldType).recordName).append(");")
            .writeNewline();
      } else if (fieldType instanceof TyReactiveRecord) {
        classConstructorX.append(fieldName).append(" = new ").append(fieldType.getJavaConcreteType(environment)).append("(RxFactory.ensureChildNodeExists(").append(rootObject).append(", \"").append(fieldName).append("\"), this);")
            .writeNewline();
      } else if (fieldType instanceof IsReactiveValue) {
        if (fieldType instanceof DetailInventDefaultValueExpression) {
          var defaultValue = ((DetailInventDefaultValueExpression) fieldType).inventDefaultValueExpression(fieldType);
          if (fdInOrder.defaultValueOverride != null) {
            defaultValue = fdInOrder.defaultValueOverride;
          }
          if (defaultValue != null) {
            defaultValue.typing(environment, null);
          }
          classConstructorX.append(fieldName).append(" = RxFactory.make").append(javaConcreteType).append("(this, ").append(rootObject).append(", \"").append(fieldName).append("\", ");
          defaultValue.writeJava(classConstructorX, environment.scopeWithComputeContext(ComputeContext.Computation));
          classConstructorX.append(");").writeNewline();
        }
      } else if (fieldType instanceof TyReactiveMaybe) {
        var doImmediateGet = false;
        final var elementType = ((DetailContainsAnEmbeddedType) fieldType).getEmbeddedType(environment);
        classConstructorX.append(fieldName + " = RxFactory.makeRxMaybe(this, " + rootObject + ", \"" + fieldName + "\", (RxParent __parent) -> ");
        if (elementType instanceof TyReactiveRecord) {
          classConstructorX.append("new ").append(elementType.getJavaConcreteType(environment)).append("(RxFactory.ensureChildNodeExists(").append(rootObject).append(", \"").append(fieldName).append("\"), __parent)");
        } else if (elementType instanceof DetailInventDefaultValueExpression && elementType instanceof IsReactiveValue) {
          var defaultValue = ((DetailInventDefaultValueExpression) elementType).inventDefaultValueExpression(fieldType);
          if (fdInOrder.defaultValueOverride != null) {
            doImmediateGet = true;
            defaultValue = fdInOrder.defaultValueOverride;
          }
          if (defaultValue != null) {
            defaultValue.typing(environment, null);
          }
          classConstructorX.append("RxFactory.make" + elementType.getJavaConcreteType(environment) + "(__parent, " + rootObject + ", \"" + fieldName + "\", ");
          defaultValue.writeJava(classConstructorX, environment.scopeWithComputeContext(ComputeContext.Computation));
          classConstructorX.append(")");
        }
        classConstructorX.append(");").writeNewline();
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

  public static void writeIndices(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    boolean first;
    sb.append("String[] __INDEX_COLUMNS = new String[] {");
    first = true;
    for (final Map.Entry<String, FieldDefinition> entry : storage.fields.entrySet()) {
      if ("id".equals(entry.getKey())) {
        continue;
      }
      final var fieldType = environment.rules.Resolve(entry.getValue().type, false);
      if (fieldType instanceof TyReactiveInteger || fieldType instanceof TyReactiveEnum || fieldType instanceof TyReactiveClient) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append("\"").append(entry.getKey()).append("\"");
      }
    }
    sb.append("};").writeNewline();
    sb.append("@Override").writeNewline();
    sb.append("public String[] __getIndexColumns() {").tabUp().writeNewline();
    sb.append("return __INDEX_COLUMNS;").tabDown().writeNewline();
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
      if (fieldType instanceof TyReactiveInteger || fieldType instanceof TyReactiveEnum || fieldType instanceof TyReactiveClient) {
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

  public static void writeMethods(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    for (final DefineMethod dm : storage.methods) {
      dm.writeFunctionJava(sb, environment);
    }
  }

  public static void writePrivacyCommonBetweenRecordAndRoot(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    final var policyRoot = environment.scopeAsReadOnlyBoundary();
    for (final FieldDefinition fdInOrder : storage.fieldsByOrder) {
      final var fieldName = fdInOrder.name;
      final var fieldType = environment.rules.Resolve(fdInOrder.type, false);
      if (fieldType != null) {
        policyRoot.define(fieldName, fieldType, true, fieldType);
      }
    }
    for (final Map.Entry<String, DefineCustomPolicy> customPolicyEntry : storage.policies.entrySet()) {
      final var policyExec = policyRoot.scopeAsNoCost().scopeWithComputeContext(ComputeContext.Computation).setReturnType(new TyNativeBoolean(customPolicyEntry.getValue().name).withPosition(customPolicyEntry.getValue()));
      policyExec.define(customPolicyEntry.getValue().clientVar.text, customPolicyEntry.getValue().clientType, true, customPolicyEntry.getValue().clientType);
      sb.append("public boolean __POLICY_").append(customPolicyEntry.getKey()).append("(NtClient ").append(customPolicyEntry.getValue().clientVar.text).append(")");
      customPolicyEntry.getValue().code.typing(policyExec);
      customPolicyEntry.getValue().code.writeJava(sb, policyExec);
      sb.writeNewline();
    }
    for (final Map.Entry<String, BubbleDefinition> bubbleDefinitionEntry : storage.bubbles.entrySet()) {
      bubbleDefinitionEntry.getValue().writeSetup(sb, environment);
    }
  }

  public static void writePrivacyExtractor(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment, final boolean isRoot) {
    sb.append("@Override").writeNewline();
    sb.append("public JsonNode getPrivateViewFor(NtClient __who) {").tabUp().writeNewline();
    if (storage.policiesForVisibility.size() == 0 && storage.fields.size() == 0 && storage.bubbles.size() == 0) {
      sb.append("return null;").tabDown().writeNewline();
      sb.append("}").writeNewline();
    } else {
      sb.append("if (!(true");
      for (final String policyToCheck : storage.policiesForVisibility) {
        sb.append(" && ").append("__POLICY_").append(policyToCheck).append("(__who)");
      }
      sb.append(")) {").tabUp().writeNewline();
      sb.append("return null;").tabDown().writeNewline();
      sb.append("}").writeNewline();
      sb.append("ObjectNode __view = Utility.createObjectNode();").writeNewline();
      for (final Map.Entry<String, FieldDefinition> fieldDefinitionEntry : storage.fields.entrySet()) {
        fieldDefinitionEntry.getValue().writePolicy(sb, environment);
      }
      for (final Map.Entry<String, BubbleDefinition> bubbleDefinitionEntry : storage.bubbles.entrySet()) {
        bubbleDefinitionEntry.getValue().writePolicy(sb, environment);
      }
      sb.append("return __view;").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
    if (!isRoot) {
      sb.append("@Override").writeNewline();
      sb.append("public boolean __privacyPolicyAllowsCache() {").tabUp().writeNewline();
      sb.append("return ").append(storage.hasViewerIndependentPrivacyPolicy(environment, 4) ? "true" : "false").append(";").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
  }

  public static void writeRootDocument(final StructureStorage storage, final StringBuilderWithTabs sb, final Environment environment) {
    final var classConstructor = new StringBuilderWithTabs().tabUp().tabUp();
    final var classFields = new StringBuilderWithTabs().tabUp();
    writeCommonBetweenRecordAndRoot(storage, classConstructor, classFields, environment, "__root", false);
    final var trimedClassFields = classFields.toString().stripTrailing();
    if (trimedClassFields.length() > 0) {
      sb.append(trimedClassFields).writeNewline();
    }
    writePrivacyCommonBetweenRecordAndRoot(storage, sb, environment);
    if (classConstructor.toString().length() > 0) {
      sb.append("public " + environment.document.getClassName() + "(ObjectNode __root, DocumentMonitor __monitor) {").tabUp().writeNewline();
      sb.append("super(__root, __monitor);").writeNewline();
      sb.append(classConstructor.toString().stripTrailing()).writeNewline();
    } else {
      sb.append("public " + environment.document.getClassName() + "(ObjectNode __root, DocumentMonitor __monitor) {").tabUp().writeNewline();
      sb.append("super(__root, __monitor);").writeNewline();
    }
    sb.append("__goodwillBudget = ").append(environment.state.options.goodwillBudget + ";").writeNewline();
    sb.append("__goodwillLimitOfBudget = ").append(environment.state.options.goodwillBudget + ";").tabDown().writeNewline();
    sb.append("}").writeNewline();
    writeCommitAndRevert(storage, sb, environment, true, "__state", "__constructed", "__next_time", "__blocked", "__seq", "__entropy", "__auto_future_id", "__connection_id", "__message_id", "__time");
    writePrivacyExtractor(storage, sb, environment, true);
  }
}

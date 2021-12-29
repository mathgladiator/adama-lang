/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.codegen;

import java.util.Map;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.FieldLookup;
import org.adamalang.translator.tree.expressions.Lookup;
import org.adamalang.translator.tree.statements.Assignment;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeArray;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.traits.IsMap;
import org.adamalang.translator.tree.types.traits.IsStructure;

/** responsible for the recursive "ingestion operator" (<-) which is how data
 * gets into the tables or objects */
public class CodeGenIngestion {
  private static void finish(final Environment environment, final StringBuilderWithTabs sb, final TyType assignTypeRaw, final String assignVar, final TyType elementTypeRaw, final String elementVar) {
    if (assignVar != null && elementTypeRaw != null) {
      TyType assignType = environment.rules.ResolvePtr(assignTypeRaw, false);
      TyType elementType = environment.rules.ResolvePtr(elementTypeRaw, false);
      if (environment.rules.IsStructure(assignType, true) && environment.rules.IsStructure(elementType, true)) {
        var countDownUntilTab = ((IsStructure) elementType).storage().fields.size();
        environment.define(assignVar, assignType, false, DocumentPosition.ZERO);
        environment.define(elementVar, elementType, false, DocumentPosition.ZERO);
        if (!environment.state.hasNoCost()) {
          sb.append("__code_cost += ").append(Integer.toString(((IsStructure) elementType).storage().fields.size())).append(";").writeNewline();
        }
        for (final Map.Entry<String, FieldDefinition> entryType : ((IsStructure) elementType).storage().fields.entrySet()) {
          final var fd = ((IsStructure) assignType).storage().fields.get(entryType.getKey());
          if (fd != null) {
            final var leftAssignType = ((IsStructure) assignType).storage().fields.get(entryType.getKey()).type;
            final var op = environment.rules.IngestionLeftElementRequiresRecursion(leftAssignType) ? "<-" : "=";
            final var ass = new Assignment( //
                new FieldLookup(new Lookup(Token.WRAP(assignVar)), null, entryType.getValue().nameToken), Token.WRAP(op), //
                new FieldLookup(new Lookup(Token.WRAP(elementVar)), null, entryType.getValue().nameToken), null, null, null, false);
            ass.typing(environment);
            ass.writeJava(sb, environment);
            if (--countDownUntilTab == 0) {
              sb.tabDown();
            }
            sb.writeNewline();
          }
        }
      } else {
        final var ass = new Assignment( //
          new Lookup(Token.WRAP(assignVar)), Token.WRAP("="), //
          new Lookup(Token.WRAP(elementVar)), null, null, null, false);

        environment.define(assignVar, assignType, false, DocumentPosition.ZERO);
        environment.define(elementVar, elementType, false, DocumentPosition.ZERO);
        ass.typing(environment);
        ass.writeJava(sb, environment);
        sb.tabDown().writeNewline();
      }
    }
  }

  public static void writeJava(final StringBuilderWithTabs sb, final Environment original, final Assignment assignment, Token exportIdsAs) {
    final var refType = assignment.ref.getCachedType();
    final var exprType = assignment.expression.getCachedType();
    boolean isArray = original.rules.IngestionRightSideRequiresIteration(exprType);
    boolean isMap = original.rules.IsMap(exprType);
    boolean hasId = original.rules.IngestionLeftSideRequiresBridgeCreate(refType);

    if (exportIdsAs != null) {
      if (isArray) {
        sb.append("int[] ").append(exportIdsAs.text).append(";").writeNewline();
        original.define(exportIdsAs.text, new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, exportIdsAs, exportIdsAs), exportIdsAs), true, assignment);
      } else {
        sb.append("int ").append(exportIdsAs.text).append(";").writeNewline();
        sb.append("// EXPORT:" + exportIdsAs.text).writeNewline();
        original.define(exportIdsAs.text, new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, exportIdsAs, exportIdsAs), true, assignment);
      }
    }

    final var environment = original.scope();
    sb.append("{").tabUp().writeNewline();

    String generatedIntArrayVar = null;
    if (exportIdsAs != null && isArray) {
      generatedIntArrayVar = "_AutoVarArrId" + environment.autoVariable();
      sb.append("ArrayList<Integer> ").append(generatedIntArrayVar).append(" = new ArrayList<Integer>();").writeNewline();
    }

    final var generatedRefVariable = "_AutoRef" + environment.autoVariable();
    sb.append(refType.getJavaConcreteType(environment)).append(" ").append(generatedRefVariable).append(" = ");
    assignment.ref.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Assignment));
    sb.append(";").writeNewline();
    final var autoVar = environment.autoVariable();
    var generateAssignVar = generatedRefVariable;
    var generateAssignType = refType;
    if (isMap) {
      TyType inputDomainType = environment.rules.Resolve(((IsMap) exprType).getDomainType(environment), true);
      TyType inputRangeType = environment.rules.Resolve(((IsMap) exprType).getRangeType(environment), true);
      TyType outputRangeType = environment.rules.Resolve(((IsMap) refType).getRangeType(environment), true);
      final var generatedExprVariableEntry = "_AutoEntry" + autoVar;
      sb.append("for (Map.Entry<").append(inputDomainType.getJavaBoxType(environment)).append(",").append(inputRangeType.getJavaBoxType(environment)).append("> ").append(generatedExprVariableEntry);
      sb.append(" : (");
      assignment.expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
      sb.append(").entries()) {").tabUp().writeNewline();
      generateAssignType = outputRangeType.makeCopyWithNewPosition(refType, TypeBehavior.ReadWriteNative);
      final var generatedExprVariable = "_AutoExpr" + autoVar;
      generateAssignVar = "_AutoRefElement" + autoVar;
      sb.append(inputRangeType.getJavaConcreteType(environment)).append(" ").append(generatedExprVariable).append(" = ").append(generatedExprVariableEntry).append(".getValue();").writeNewline();
      sb.append(generateAssignType.getJavaConcreteType(environment)).append(" ").append(generateAssignVar).append(" = ");
      sb.append(generatedRefVariable).append(".getOrCreate(").append(generatedExprVariableEntry).append(".getKey());").writeNewline();
      finish(environment, sb, generateAssignType, generateAssignVar, inputRangeType, generatedExprVariable);
      sb.append("}").tabDown().writeNewline();
    } else if (isArray) {
      final var iterateElementType = environment.rules.ExtractEmbeddedType(exprType, true);
      final var generatedExprVariable = "_AutoElement" + autoVar;
      sb.append("for (");
      sb.append(iterateElementType.getJavaConcreteType(environment)).append(" ").append(generatedExprVariable).append(" : ");
      assignment.expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
      sb.append(") {").tabUp().writeNewline();
      if (hasId) {
        generateAssignType = environment.rules.Resolve(environment.rules.ExtractEmbeddedType(refType, true), true).makeCopyWithNewPosition(refType, TypeBehavior.ReadWriteNative);
        generateAssignVar = "_CreateRef" + autoVar;
        sb.append(generateAssignType.getJavaConcreteType(environment)).append(" ").append(generateAssignVar).append(" = ").append(generatedRefVariable).append(".make();").writeNewline();
        if (exportIdsAs != null && isArray) {
          sb.append(generatedIntArrayVar).append(".add(").append(generateAssignVar).append(".id.get());").writeNewline();
        }
      }
      finish(environment, sb, generateAssignType, generateAssignVar, iterateElementType, generatedExprVariable);
      sb.append("}");
      if (exportIdsAs != null) {
        sb.writeNewline();
        sb.append(exportIdsAs.text).append(" = Utility.convertIntegerArrayList(").append(generatedIntArrayVar).append(");");
      }
      sb.tabDown().writeNewline();
    } else {
      final var generatedExprVariable = "_AutoExpr" + environment.autoVariable();
      sb.append(exprType.getJavaConcreteType(environment)).append(" ").append(generatedExprVariable).append(" = ");
      assignment.expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
      sb.append(";").writeNewline();
      if (hasId) {
        generateAssignType = environment.rules.Resolve(environment.rules.ExtractEmbeddedType(refType, true), true).makeCopyWithNewPosition(refType, TypeBehavior.ReadWriteNative);
        generateAssignVar = "_CreateRef" + autoVar;
        sb.append(generateAssignType.getJavaConcreteType(environment)).append(" ").append(generateAssignVar).append(" = ").append(generatedRefVariable).append(".make();").writeNewline();
        if (exportIdsAs != null) {
          sb.append(exportIdsAs.text).append(" = ").append(generateAssignVar).append(".id.get();").writeNewline();
        }
      }
      finish(environment, sb, generateAssignType, generateAssignVar, exprType, generatedExprVariable);
    }
    sb.append("}");
  }
}

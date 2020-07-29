/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
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
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.traits.IsStructure;

/** responsible for the recursive "ingestion operator" (<-) which is how data
 * gets into the tables or objects */
public class CodeGenIngestion {
  private static void finish(final Environment environment, final StringBuilderWithTabs sb, final TyType assignType, final String assignVar, final TyType elementType, final String elementVar) {
    if (assignType != null && elementType != null && assignType instanceof IsStructure && elementType instanceof IsStructure) {
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
              new FieldLookup(new Lookup(Token.WRAP(elementVar)), null, entryType.getValue().nameToken), null, false);
          ass.typing(environment);
          ass.writeJava(sb, environment);
          if (--countDownUntilTab == 0) {
            sb.tabDown();
          }
          sb.writeNewline();
        }
      }
    }
  }

  public static void writeJava(final StringBuilderWithTabs sb, final Environment original, final Assignment assignment) {
    final var environment = original.scope();
    final var refType = assignment.ref.getCachedType();
    final var exprType = assignment.expression.getCachedType();
    sb.append("{").tabUp().writeNewline();
    final var generatedRefVariable = "_AutoRef" + environment.autoVariable();
    sb.append(refType.getJavaConcreteType(environment)).append(" ").append(generatedRefVariable).append(" = ");
    assignment.ref.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Assignment));
    sb.append(";").writeNewline();
    final var autoVar = environment.autoVariable();
    var generateAssignVar = generatedRefVariable;
    var generateAssignType = refType;
    if (environment.rules.IngestionRightSideRequiresIteration(exprType)) {
      final var iterateElementType = environment.rules.ExtractEmbeddedType(exprType, true);
      final var generatedExprVariable = "_AutoElement" + autoVar;
      sb.append("for (");
      sb.append(iterateElementType.getJavaConcreteType(environment)).append(" ").append(generatedExprVariable).append(" : ");
      assignment.expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
      sb.append(") {").tabUp().writeNewline();
      if (environment.rules.IngestionLeftSideRequiresBridgeCreate(refType)) {
        generateAssignType = environment.rules.Resolve(environment.rules.ExtractEmbeddedType(refType, true), true).makeCopyWithNewPosition(refType, TypeBehavior.ReadWriteNative);
        generateAssignVar = "_CreateRef" + autoVar;
        sb.append(generateAssignType.getJavaConcreteType(environment)).append(" ").append(generateAssignVar).append(" = ").append(generatedRefVariable).append(".make();").writeNewline();
      }
      finish(environment, sb, generateAssignType, generateAssignVar, iterateElementType, generatedExprVariable);
      sb.append("}").tabDown().writeNewline();
    } else {
      final var generatedExprVariable = "_AutoExpr" + environment.autoVariable();
      sb.append(exprType.getJavaConcreteType(environment)).append(" ").append(generatedExprVariable).append(" = ");
      assignment.expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
      sb.append(";").writeNewline();
      if (environment.rules.IngestionLeftSideRequiresBridgeCreate(refType)) {
        generateAssignType = environment.rules.Resolve(environment.rules.ExtractEmbeddedType(refType, true), true).makeCopyWithNewPosition(refType, TypeBehavior.ReadWriteNative);
        generateAssignVar = "_CreateRef" + autoVar;
        sb.append(generateAssignType.getJavaConcreteType(environment)).append(" ").append(generateAssignVar).append(" = ").append(generatedRefVariable).append(".make();").writeNewline();
      }
      finish(environment, sb, generateAssignType, generateAssignVar, exprType, generatedExprVariable);
    }
    sb.append("}");
  }
}

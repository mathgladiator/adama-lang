/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.statements;

import java.util.function.Consumer;
import org.adamalang.translator.codegen.CodeGenIngestion;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.operands.AssignmentOp;
import org.adamalang.translator.tree.types.checking.LocalTypeAssignmentResult;
import org.adamalang.translator.tree.types.checking.properties.CanAssignResult;
import org.adamalang.translator.tree.types.natives.TyNativeList;

public class Assignment extends Statement {
  public Expression altExpression;
  public final Expression expression;
  public final boolean inForLoop;
  public final AssignmentOp op;
  public final Token opToken;
  public final Expression ref;
  private LocalTypeAssignmentResult result;
  public final Token trailingToken;

  public Assignment(final Expression ref, final Token opToken, final Expression expression, final Token trailingToken, final boolean inForLoop) {
    this.ref = ref;
    this.expression = expression;
    this.opToken = opToken;
    op = AssignmentOp.fromText(opToken.text);
    this.inForLoop = inForLoop;
    this.trailingToken = trailingToken;
    ingest(ref);
    ingest(expression);
    if (trailingToken != null) {
      ingest(trailingToken);
    }
    altExpression = null;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    ref.emit(yielder);
    yielder.accept(opToken);
    expression.emit(yielder);
    if (trailingToken != null) {
      yielder.accept(trailingToken);
    }
  }

  @Override
  public ControlFlow typing(final Environment environment) {
    result = new LocalTypeAssignmentResult(environment, ref, expression);
    switch (op) {
      case IngestFrom:
        result.ingest();
        break;
      case Set:
        result.set();
        break;
      case AddTo:
        result.add();
        break;
      case SubtractFrom:
        result.subtract();
        break;
      case MultiplyBy:
        result.multiply();
        break;
      case DivideBy:
        result.divide();
        break;
      case ModBy:
        result.mod();
        break;
    }
    return ControlFlow.Open;
  }

  @Override
  public void writeJava(final StringBuilderWithTabs sb, final Environment environment) {
    if (result == null || result.bad()) { return; }
    if (result.assignResult == CanAssignResult.YesWithNativeOp) {
      ref.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Assignment));
      sb.append(" ").append(op.js).append(" ");
      expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
      if (!inForLoop) {
        sb.append(";");
      }
    } else if (result.assignResult == CanAssignResult.YesWithSetter) {
      ref.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Assignment));
      sb.append(op.notNative).append("(");
      expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
      sb.append(");");
    } else if (result.assignResult == CanAssignResult.YesWithMakeThenSetter) {
      ref.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Assignment));
      sb.append(".make()");
      sb.append(op.notNative).append("(");
      expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
      sb.append(");");
    } else if ((result.assignResult == CanAssignResult.YesWithTransformSetter || result.assignResult == CanAssignResult.YesWithTransformThenMakeSetter) && result.ltype != null) {
      final var varToCache = "_auto_" + environment.autoVariable();
      final var varToIterate = "_auto_" + environment.autoVariable();
      final var embeddedType = ((TyNativeList) result.ltype).getEmbeddedType(environment);
      if (embeddedType != null) {
        sb.append(result.ltype.getJavaConcreteType(environment)).append(" ").append(varToCache).append(" = ");
        ref.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Assignment));
        sb.append(";").writeNewline();
        sb.append("for (").append(embeddedType.getJavaConcreteType(environment)).append(" ").append(varToIterate).append(" : ").append(varToCache).append(") {").tabUp().writeNewline();
        sb.append(varToIterate);
        if (result.assignResult == CanAssignResult.YesWithTransformThenMakeSetter) {
          sb.append(".make()");
        }
        sb.append(op.notNative).append("(");
        expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation));
        sb.append(");").tabDown().writeNewline();
        sb.append("}").writeNewline();
      }
    } else if (result.assignResult == CanAssignResult.YesWithIngestionCodeGen) {
      CodeGenIngestion.writeJava(sb, environment, this);
    }
  }
}

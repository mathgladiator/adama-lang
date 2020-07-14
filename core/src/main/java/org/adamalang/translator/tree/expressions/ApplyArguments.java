/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.expressions;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.LatentCodeSnippet;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeList;
import org.adamalang.translator.tree.types.natives.TyNativeVoid;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.natives.functions.TyNativeAggregateFunctional;
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;

/** functional application. This applies arguments to the given expression, and
 * the expectation is that the expression is a function of some sorts */
public class ApplyArguments extends Expression implements LatentCodeSnippet {
  private TyType aggregateInputType;
  private TyType aggregateOutputType;
  private final ArrayList<TokenizedItem<Expression>> args;
  private final Token closeParenToken;
  private TreeMap<String, TyType> closureTyTypes;
  private final Expression expression;
  private FunctionOverloadInstance functionInstance;
  private FunctionStyleJava functionStyle;
  private boolean isAggregate;
  private ArrayList<String> latentLines;
  private final Token openParenToken;

  /** @param expression the expression that must be something that is or contains
   *                   a function */
  public ApplyArguments(final Expression expression, final Token openParenToken, final ArrayList<TokenizedItem<Expression>> args, final Token closeParenToken) {
    this.expression = expression;
    this.openParenToken = openParenToken;
    this.args = args;
    this.closeParenToken = closeParenToken;
    this.ingest(expression);
    this.ingest(openParenToken);
    this.ingest(closeParenToken);
    functionInstance = null;
    closureTyTypes = null;
    latentLines = null;
  }

  private int buildLatentApplyClassInstance(final Environment environment) {
    final var id = environment.autoVariable();
    final var sb = new StringBuilderWithTabs();
    if (aggregateOutputType != null) {
      sb.append("private class __CLOSURE_Apply").append("_" + id).append(" implements Function<").append(aggregateInputType.getJavaBoxType(environment)).append(",").append(aggregateOutputType.getJavaBoxType(environment)).append("> {")
          .tabUp().writeNewline();
    } else {
      sb.append("private class __CLOSURE_Apply").append("_" + id).append(" implements Consumer<").append(aggregateInputType.getJavaBoxType(environment)).append("> {").tabUp().writeNewline();
    }
    for (final Map.Entry<String, TyType> entry : closureTyTypes.entrySet()) {
      sb.append("private ").append(entry.getValue().getJavaConcreteType(environment)).append(" ").append(entry.getKey()).append(";").writeNewline();
    }
    sb.append("private __CLOSURE_Apply").append("_" + id + "(");
    var first = true;
    for (final Map.Entry<String, TyType> entry : closureTyTypes.entrySet()) {
      if (first) {
        first = false;
      } else {
        sb.append(", ");
      }
      sb.append(entry.getValue().getJavaConcreteType(environment)).append(" ").append(entry.getKey());
    }
    sb.append(") {").writeNewline();
    for (final Map.Entry<String, TyType> entry : closureTyTypes.entrySet()) {
      sb.append("this.").append(entry.getKey()).append(" = ").append(entry.getKey()).append(";").writeNewline();
    }
    sb.append("}").writeNewline();
    sb.append("@Override").writeNewline();
    if (aggregateOutputType != null) {
      sb.append("public ").append(aggregateOutputType.getJavaBoxType(environment)).append(" apply(").append(aggregateInputType.getJavaBoxType(environment)).append(" __item) {").tabUp().writeNewline();
      sb.append("return __item.").append(functionInstance.javaFunction);
      sb.append("(");
      final var temp = new StringBuilder();
      writeArgsJava(temp, environment, true);
      sb.append(temp.toString());
      sb.append(");").tabDown().writeNewline();
    } else {
      sb.append("public void accept(").append(aggregateInputType.getJavaBoxType(environment)).append(" __item) {").tabUp().writeNewline();
      sb.append("__item.").append(functionInstance.javaFunction);
      sb.append("(");
      final var temp = new StringBuilder();
      writeArgsJava(temp, environment, true);
      sb.append(temp.toString());
      sb.append(");").tabDown().writeNewline();
    }
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
    latentLines = sb.toLines();
    return id;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    expression.emit(yielder);
    yielder.accept(openParenToken);
    for (final TokenizedItem<Expression> element : args) {
      element.emitBefore(yielder);
      element.item.emit(yielder);
      element.emitAfter(yielder);
    }
    yielder.accept(closeParenToken);
  }

  @Override
  protected TyType typingInternal(final Environment environmentX, final TyType suggestion) {
    final var exprType = expression.typing(environmentX, null);
    environmentX.rules.IsFunction(exprType, false);
    if (exprType != null && exprType instanceof TyNativeFunctional) {
      isAggregate = exprType instanceof TyNativeAggregateFunctional;
      var environmentToUse = environmentX;
      if (isAggregate) {
        closureTyTypes = new TreeMap<>();
        environmentToUse = environmentX.watch(name -> {
          if (!closureTyTypes.containsKey(name)) {
            final var ty = environmentX.lookup(name, true, this, false);
            if (ty != null) {
              closureTyTypes.put(name, ty);
            }
          }
        });
        environmentToUse.document.add(this);
      }
      final var argTypes = new ArrayList<TyType>();
      for (final TokenizedItem<Expression> arg : args) {
        argTypes.add(arg.item.typing(environmentToUse, null));
      }
      exprType.typing(environmentToUse);
      functionStyle = ((TyNativeFunctional) exprType).style;
      functionInstance = ((TyNativeFunctional) exprType).find(expression, argTypes, environmentToUse);
      if (environmentToUse.state.isPure() && !functionInstance.pure) {
        environmentToUse.document.createError(expression, String.format("Pure functions can only call other pure functions"), "FunctionInvoke");
      }
      if (environmentToUse.state.isReactiveExpression() && !functionInstance.pure) {
        environmentToUse.document.createError(expression, String.format("Reactive expressions can only invoke pure functions"), "FunctionInvoke");
      }
      var returnType = functionInstance.returnType;
      if (isAggregate) {
        aggregateInputType = ((TyNativeAggregateFunctional) exprType).typeBase;
        aggregateOutputType = returnType;
      }
      if (returnType == null) {
        returnType = new TyNativeVoid();
      }
      if (isAggregate) {
        return new TyNativeList(null, new TokenizedItem<>(returnType)).withPosition(this);
      } else {
        return returnType.makeCopyWithNewPosition(this);
      }
    }
    environmentX.document.createError(expression, String.format("Expression is not a function"), "FunctionInvoke");
    return null;
  }

  /** write the arguments */
  private void writeArgsJava(final StringBuilder sb, final Environment environment, final boolean firstSeed) {
    var first = firstSeed;
    for (final TokenizedItem<Expression> arg : args) {
      if (!first) {
        sb.append(", ");
      } else {
        first = false;
      }
      arg.item.writeJava(sb, environment);
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    if (functionInstance != null) {
      final var childEnv = environment.scopeWithComputeContext(ComputeContext.Computation);
      switch (functionStyle) {
        case ExpressionThenNameWithArgs:
        case ExpressionThenArgs:
          expression.writeJava(sb, environment);
          if (isAggregate) {
            final var method = functionInstance.returnType != null ? ".transform" : ".map";
            if (closureTyTypes.size() > 0) {
              final var id = buildLatentApplyClassInstance(childEnv);
              sb.append(method).append("(new __CLOSURE_Apply").append("_" + id).append("(");
              var first = true;
              for (final Map.Entry<String, TyType> entry : closureTyTypes.entrySet()) {
                if (first) {
                  first = false;
                } else {
                  sb.append(", ");
                }
                sb.append(entry.getKey());
              }
              sb.append(")");
            } else if (functionInstance.returnType != null) {
              sb.append(method).append("((__item) -> __item.").append(functionInstance.javaFunction);
              sb.append("(");
              final var temp = new StringBuilder();
              writeArgsJava(temp, environment, true);
              sb.append(temp.toString());
              sb.append(")");
            } else {
              sb.append(method).append("((__item) -> { __item.").append(functionInstance.javaFunction);
              sb.append("(");
              final var temp = new StringBuilder();
              writeArgsJava(temp, environment, true);
              sb.append(temp.toString());
              sb.append("); }");
            }
            if (functionInstance.returnType == null) {
              sb.append(")");
            } else {
              sb.append(", ").append(((DetailHasBridge) functionInstance.returnType).getBridge(environment)).append(")");
            }
          } else {
            if (functionStyle == FunctionStyleJava.ExpressionThenNameWithArgs) {
              sb.append(".").append(functionInstance.javaFunction);
            }
            sb.append("(");
            writeArgsJava(sb, childEnv, true);
            sb.append(")");
          }
          break;
        case InjectName:
          sb.append(functionInstance.javaFunction);
          break;
        case InjectNameThenArgs:
          sb.append(functionInstance.javaFunction);
          sb.append("(");
          writeArgsJava(sb, childEnv, true);
          sb.append(")");
          break;
        case InjectNameThenExpressionAndArgs:
          sb.append(functionInstance.javaFunction);
          sb.append("(");
          expression.writeJava(sb, childEnv);
          writeArgsJava(sb, childEnv, false);
          sb.append(")");
          break;
        case None:
        default:
          expression.writeJava(sb, childEnv);
          break;
      }
    }
  }

  @Override
  public void writeLatentJava(final StringBuilderWithTabs sb) {
    if (latentLines != null) {
      for (final String line : latentLines) {
        sb.append(line).writeNewline();
      }
    }
  }
}

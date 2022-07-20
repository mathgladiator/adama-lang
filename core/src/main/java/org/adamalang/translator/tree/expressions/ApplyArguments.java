/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.expressions;

import org.adamalang.translator.codegen.CodeGenFunctions;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.LatentCodeSnippet;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;
import org.adamalang.translator.tree.types.natives.functions.TyNativeAggregateFunctional;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * functional application. This applies arguments to the given expression, and the expectation is
 * that the expression is a function of some sorts
 */
public class ApplyArguments extends Expression implements LatentCodeSnippet {
  private final ArrayList<TokenizedItem<Expression>> args;
  private final Token closeParenToken;
  private final Expression expression;
  private final Token openParenToken;
  private TyType aggregateInputType;
  private TyType aggregateOutputType;
  private TreeMap<String, TyType> closureTyTypes;
  private FunctionOverloadInstance functionInstance;
  private FunctionStyleJava functionStyle;
  private boolean isAggregate;
  private ArrayList<String> latentLines;

  /** @param expression the expression that must be something that is or contains a function */
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
        environmentToUse = environmentX.watch((String name, TyType tyUn) -> {
          TyType ty = environmentX.rules.Resolve(tyUn, false);
          if (ty instanceof TyNativeGlobalObject) {
            return;
          }
          if (!closureTyTypes.containsKey(name) && ty != null) {
            closureTyTypes.put(name, ty);
          }
        });
        environmentToUse.document.add(this);
      }
      final var argTypes = new ArrayList<TyType>();
      for (final TokenizedItem<Expression> arg : args) {
        argTypes.add(arg.item.typing(environmentToUse.scopeWithComputeContext(ComputeContext.Computation), null));
      }
      exprType.typing(environmentToUse);
      functionStyle = ((TyNativeFunctional) exprType).style;
      if (functionStyle == FunctionStyleJava.RemoteCall) {
        if (environmentToUse.state.isReadonlyEnvironment() || environmentToUse.state.isPure()) {
          environmentToUse.document.createError(expression, String.format("Services can not be invoked in read-only or pure functional context"), "FunctionInvoke");
        } else if (environmentToUse.state.getCacheObject() == null) {
          environmentToUse.document.createError(expression, String.format("Remote invocation not available in this scope"), "FunctionInvoke");
        }
      }
      functionInstance = ((TyNativeFunctional) exprType).find(expression, argTypes, environmentToUse);
      if (environmentToUse.state.isPure() && !functionInstance.pure) {
        environmentToUse.document.createError(expression, String.format("Pure functions can only call other pure functions"), "FunctionInvoke");
      }
      if (environmentToUse.state.isReadonlyEnvironment() && !functionInstance.pure) {
        environmentToUse.document.createError(expression, String.format("Read only methods can only call other read-only methods or pure functions"), "FunctionInvoke");
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
        return new TyNativeList(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(returnType)).withPosition(this);
      } else {
        return returnType.makeCopyWithNewPosition(this, returnType.behavior);
      }
    }
    environmentX.document.createError(expression, String.format("Expression is not a function"), "FunctionInvoke");
    return null;
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    if (functionInstance != null) {
      final var childEnv = environment.scopeWithComputeContext(ComputeContext.Computation);
      switch (functionStyle) {
        case RemoteCall: {
          expression.writeJava(sb, environment);
          sb.append(".invoke(__self, \"");
          sb.append(functionInstance.javaFunction).append("\", ").append(environment.state.getCacheObject());
          final var temp = new StringBuilder();
          CodeGenFunctions.writeArgsJava(temp, childEnv, false, args, functionInstance);
          sb.append(temp);
          TyNativeResult resultType = (TyNativeResult) (functionInstance.returnType);
          if (resultType.tokenElementType.item instanceof TyNativeArray) {
            TyNativeMessage msgType = (TyNativeMessage) (((TyNativeArray) resultType.tokenElementType.item).elementType);
            sb.append(", (__json) -> Utility.readArray(new JsonStreamReader(__json), (__reader) -> new RTx")
                .append(msgType.name).append("(__reader), (__n) -> new RTx")
                .append(msgType.name).append("[__n]))");
          } else {
            TyNativeMessage msgType = (TyNativeMessage) (resultType.tokenElementType.item);
            sb.append(", (__json) -> new RTx").append(msgType.name).append("(new JsonStreamReader(__json)))");
          }
        } break;
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
              CodeGenFunctions.writeArgsJava(temp, childEnv, true, args, functionInstance);
              sb.append(temp);
              sb.append(")");
            } else {
              sb.append(method).append("((__item) -> { __item.").append(functionInstance.javaFunction);
              sb.append("(");
              final var temp = new StringBuilder();
              CodeGenFunctions.writeArgsJava(temp, childEnv, true, args, functionInstance);
              sb.append(temp);
              sb.append("); }");
            }
            sb.append(")");
          } else {
            if (functionStyle == FunctionStyleJava.ExpressionThenNameWithArgs) {
              sb.append(".").append(functionInstance.javaFunction);
            }
            sb.append("(");
            CodeGenFunctions.writeArgsJava(sb, childEnv, true, args, functionInstance);
            sb.append(")");
          }
          break;
        case InjectName:
          sb.append(functionInstance.javaFunction);
          break;
        case InjectNameThenArgs:
          sb.append(functionInstance.javaFunction);
          sb.append("(");
          CodeGenFunctions.writeArgsJava(sb, childEnv, true, args, functionInstance);
          sb.append(")");
          break;
        case InjectNameThenExpressionAndArgs:
          sb.append(functionInstance.javaFunction);
          sb.append("(");
          expression.writeJava(sb, childEnv);
          CodeGenFunctions.writeArgsJava(sb, childEnv, false, args, functionInstance);
          sb.append(")");
          break;
        case None:
        default:
          expression.writeJava(sb, childEnv);
          break;
      }
    }
  }

  private int buildLatentApplyClassInstance(final Environment environment) {
    final var id = environment.autoVariable();
    final var sb = new StringBuilderWithTabs();
    if (aggregateOutputType != null) {
      sb.append("private class __CLOSURE_Apply").append("_" + id).append(" implements Function<").append(aggregateInputType.getJavaBoxType(environment)).append(",").append(aggregateOutputType.getJavaBoxType(environment)).append("> {").tabUp().writeNewline();
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
      CodeGenFunctions.writeArgsJava(temp, environment, true, args, functionInstance);
      sb.append(temp.toString());
      sb.append(");").tabDown().writeNewline();
    } else {
      sb.append("public void accept(").append(aggregateInputType.getJavaBoxType(environment)).append(" __item) {").tabUp().writeNewline();
      sb.append("__item.").append(functionInstance.javaFunction);
      sb.append("(");
      final var temp = new StringBuilder();
      CodeGenFunctions.writeArgsJava(temp, environment, true, args, functionInstance);
      sb.append(temp.toString());
      sb.append(");").tabDown().writeNewline();
    }
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
    latentLines = sb.toLines();
    return id;
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

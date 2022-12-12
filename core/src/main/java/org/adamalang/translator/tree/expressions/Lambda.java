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

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.LatentCodeSnippet;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeGlobalObject;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/** a very simple single variable lambda expression */
public class Lambda extends Expression implements LatentCodeSnippet {

  private final Token lambdaToken;
  private final Token variable;
  private final Token colon;
  private final Expression expr;
  private TyType variableType;
  private TyType exprType;
  private int generatedClassId;
  private final StringBuilder exprCode;
  private String evalReturnType;
  private String evalVariableType;

  private final ArrayList<String> trapBuilder;
  private final TreeMap<String, String> closureTypes;
  private final TreeMap<String, TyType> closureTyTypes;

  // @lambda x -> x + 1;
  public Lambda(final Token lambdaToken, final Token variable, final Token colon, final Expression expr) {
    this.lambdaToken = lambdaToken;
    this.variable = variable;
    this.colon = colon;
    this.expr = expr;
    ingest(lambdaToken);
    ingest(variable);
    ingest(colon);
    ingest(expr);
    variableType = null;
    exprType = null;
    generatedClassId = -1;
    exprCode = new StringBuilder();
    evalReturnType = null;
    evalVariableType = null;
    trapBuilder = new ArrayList<>();
    closureTypes = new TreeMap<>();
    closureTyTypes = new TreeMap<>();
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(lambdaToken);
    yielder.accept(variable);
    yielder.accept(colon);
    expr.emit(yielder);
  }

  @Override
  protected TyType typingInternal(Environment environment, TyType suggestion) {
    // how can we infer the type of variable
    if (suggestion != null && suggestion instanceof TyNativeFunctional) {
      ArrayList<FunctionOverloadInstance> instances = ((TyNativeFunctional) suggestion).overloads;
      if (instances.size() != 1) {
        environment.document.createError(this, "Not enough functional overload instances available", "Lambda");
        return null;
      }
      FunctionOverloadInstance instance = instances.get(0);
      if (instance.types.size() != 1) {
        environment.document.createError(this, "Lambda requires exactly one parameter on type inference", "Lambda");
        return null;
      }

      final var watch = environment.watch((name, tyUn) -> {
        TyType ty = environment.rules.Resolve(tyUn, false);
        if (ty instanceof TyNativeGlobalObject) {
          return;
        }
        if (!closureTypes.containsKey(name) && ty != null) {
          closureTyTypes.put(name, ty);
          closureTypes.put(name, ty.getJavaConcreteType(environment));
        }
      }).captureSpecials();
      HashMap<String, TyType> specialsUsed = watch.specials();
      if (specialsUsed != null) {
        for (Map.Entry<String, TyType> entry : specialsUsed.entrySet()) {
          closureTyTypes.put(entry.getKey(), entry.getValue());
          closureTypes.put(entry.getKey(), entry.getValue().getJavaConcreteType(environment));
        }
      }
      Environment next = watch.scopeAsReadOnlyBoundary();
      variableType = environment.rules.Resolve(instance.types.get(0), false);
      next.define(variable.text, variableType, true, this);
      if (variableType == null) {
        environment.document.createError(this, "Failed to infer the variable type of the lambda", "Lambda");
      }

      exprType = expr.typing(next, null);
      if (exprType != null && variableType != null) {
        FunctionOverloadInstance created = new FunctionOverloadInstance("apply", exprType, instance.types, true, false);
        return new TyNativeFunctional("apply", FunctionOverloadInstance.WRAP(created), FunctionStyleJava.ExpressionThenArgs);
      }
    }
    environment.document.createError(this, "Failed to infer the type arguments for the @lambda", "Lambda");
    return null;
  }

  @Override
  public void writeJava(StringBuilder sb, Environment environment) {
    environment.document.add(this);
    if (exprType != null && variableType != null) {
      evalReturnType = exprType.getJavaBoxType(environment);
      evalVariableType = variableType.getJavaBoxType(environment);
      generatedClassId = environment.document.inventClassId();
      expr.writeJava(exprCode, environment.scopeWithComputeContext(ComputeContext.Computation));
      sb.append("new __CLOSURE_Lambda" + generatedClassId + "(");
      var notfirst = false;
      for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
        if (notfirst) {
          sb.append(", ");
        }
        notfirst = true;
        sb.append(entry.getKey());
      }
      // list the variables
      sb.append(")");
    }
  }

  @Override
  public void writeLatentJava(final StringBuilderWithTabs sb) {
    sb.append("private class __CLOSURE_Lambda").append("" + generatedClassId).append(" implements Function<").append(evalVariableType).append(", ").append(evalReturnType).append(">").append(" {").tabUp().writeNewline();
    for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
      sb.append("private ").append(entry.getValue()).append(" ").append(entry.getKey()).append(";").writeNewline();
    }
    if (closureTypes.size() > 0) {
      sb.append("private __CLOSURE_Lambda" + generatedClassId + "(");
      var notfirst = false;
      for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
        if (notfirst) {
          sb.append(", ");
        }
        notfirst = true;
        sb.append(entry.getValue()).append(" ").append(entry.getKey());
      }
      sb.append(") {").tabUp().writeNewline();
      var untilTabDown = closureTypes.size();
      for (final Map.Entry<String, String> entry : closureTypes.entrySet()) {
        sb.append("this." + entry.getKey() + " = " + entry.getKey() + ";");
        if (--untilTabDown <= 0) {
          sb.tabDown();
        }
        sb.writeNewline();
      }
      sb.append("}").writeNewline();
    }
    sb.append("@Override").writeNewline();
    sb.append("public ").append(evalReturnType).append(" apply(").append(evalVariableType).append(" ").append(variable.text).append(") {").tabUp().writeNewline();
    for (final String trapToWrite : trapBuilder) {
      sb.append(trapToWrite).writeNewline();
    }
    sb.append(String.format("__code_cost ++;")).writeNewline();
    sb.append("return " + exprCode.toString() + ";").tabDown().writeNewline();
    sb.append("}").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }
}

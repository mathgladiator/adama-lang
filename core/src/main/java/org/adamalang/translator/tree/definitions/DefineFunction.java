/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;

import java.util.ArrayList;
import java.util.function.Consumer;

/** defines a function */
public class DefineFunction extends Definition {
  /** write the set of all functions in the environment */
  public final ArrayList<FunctionArg> args;

  public final Token closeParen;
  public final Token functionTypeToken;
  public final Token introReturnType;
  public final String name;
  public final Token nameToken;
  public final Token openParen;
  public final Token readOnlyToken;
  public final FunctionSpecialization specialization;
  public Block code;
  public TyType returnType;
  private boolean beenGivenId;
  private int uniqueFunctionId;

  public DefineFunction(
      final Token functionTypeToken,
      final FunctionSpecialization specialization,
      final Token nameToken,
      final Token openParen,
      final ArrayList<FunctionArg> args,
      final Token closeParen,
      final Token introReturnType,
      final TyType returnType,
      final Token readOnlyToken,
      final Block code) {
    this.functionTypeToken = functionTypeToken;
    this.specialization = specialization;
    this.nameToken = nameToken;
    name = nameToken.text;
    this.openParen = openParen;
    this.args = args;
    this.closeParen = closeParen;
    this.introReturnType = introReturnType;
    this.returnType = returnType;
    this.readOnlyToken = readOnlyToken;
    this.code = code;
    uniqueFunctionId = 0;
    beenGivenId = false;
    ingest(functionTypeToken);
    ingest(nameToken);
    ingest(openParen);
    ingest(closeParen);
    ingest(code);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(functionTypeToken);
    yielder.accept(nameToken);
    yielder.accept(openParen);
    for (final FunctionArg arg : args) {
      if (arg.commaToken != null) {
        yielder.accept(arg.commaToken);
      }
      arg.type.emit(yielder);
      yielder.accept(arg.argNameToken);
    }
    yielder.accept(closeParen);
    if (introReturnType != null) {
      yielder.accept(introReturnType);
      returnType.emit(yielder);
    }
    if (readOnlyToken != null) {
      yielder.accept(readOnlyToken);
    }
    code.emit(yielder);
  }

  @Override
  public void typing(final Environment environment) {
    getFuncId(environment);
    returnType = environment.rules.Resolve(returnType, false);
    for (final FunctionArg arg : args) {
      arg.typing(environment);
    }
    final var flow = code.typing(prepareEnvironment(environment));
    if (returnType != null && flow == ControlFlow.Open) {
      environment.document.createError(
          this,
          String.format(
              "The %s '%s' does not return in all cases",
              specialization == FunctionSpecialization.Pure ? "function" : "procedure",
              nameToken.text),
          "FunctionDefine");
    }
  }

  public int getFuncId(final Environment environment) {
    if (!beenGivenId) {
      uniqueFunctionId = environment.autoVariable();
      beenGivenId = true;
    }
    return uniqueFunctionId;
  }

  /** prepare the environment for execution */
  public Environment prepareEnvironment(final Environment environment) {
    Environment toUse;
    final var pure = specialization == FunctionSpecialization.Pure;
    if (pure) {
      toUse = environment.scopeAsPureFunction(); // what makes at pure function pure
    } else {
      if (readOnlyToken != null) {
        toUse = environment.scopeAsReadOnlyBoundary(); // what makes procedure so dirty
      } else {
        toUse = environment.scope();
      }
    }
    for (final FunctionArg arg : args) {
      toUse.define(
          arg.argName,
          arg.type,
          pure || readOnlyToken != null || arg.type instanceof TyNativeMessage,
          arg.type);
    }
    toUse.setReturnType(returnType);
    return toUse;
  }

  public FunctionOverloadInstance toFunctionOverloadInstance() {
    final var argTypes = new ArrayList<TyType>();
    for (final FunctionArg arg : args) {
      argTypes.add(arg.type);
    }
    FunctionOverloadInstance foi =
        new FunctionOverloadInstance(
            "__FUNC_" + uniqueFunctionId + "_" + name,
            returnType,
            argTypes,
            specialization == FunctionSpecialization.Pure);
    foi.ingest(this);
    return foi;
  }

  /** write the java for the function/procedure */
  public void writeFunctionJava(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("private ");
    if (returnType == null) {
      sb.append("void");
    } else {
      sb.append(returnType.getJavaConcreteType(environment));
    }
    sb.append(" __FUNC_").append("" + getFuncId(environment) + "_").append(name).append("(");
    var first = true;
    for (final FunctionArg arg : args) {
      if (first) {
        first = false;
      } else {
        sb.append(", ");
      }
      sb.append(arg.type.getJavaConcreteType(environment)).append(" ").append(arg.argName);
    }
    sb.append(") ");
    code.writeJava(sb, environment);
    sb.writeNewline();
  }
}

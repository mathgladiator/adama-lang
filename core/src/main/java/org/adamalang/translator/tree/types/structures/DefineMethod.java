/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.FunctionArg;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DefineMethod extends StructureComponent {
  public final ArrayList<FunctionArg> args;
  public final Token closeParen;
  /** code that defines the function */
  public final Block code;
  /** return type of the function */
  public final Token introduceReturnToken;
  /** the name of the function */
  public final Token methodToken;

  public final String name;
  public final Token nameToken;
  /** arguments of the function */
  public final Token openParen;

  public final Token tokenReadonly;
  public final Token abortsToken;
  public TyType returnType;
  private FunctionOverloadInstance cachedInstance;
  private int functionId;

  /** construct the function of a type with a name */
  public DefineMethod(final Token methodToken, final Token nameToken, final Token openParen, final ArrayList<FunctionArg> args, final Token closeParen, final Token introduceReturnToken, final TyType returnType, final Token tokenReadonly, final Token abortsToken, final Block code) {
    this.methodToken = methodToken;
    this.nameToken = nameToken;
    name = nameToken.text;
    this.openParen = openParen;
    this.args = args;
    this.closeParen = closeParen;
    this.introduceReturnToken = introduceReturnToken;
    this.returnType = returnType;
    this.tokenReadonly = tokenReadonly;
    this.abortsToken = abortsToken;
    this.code = code;
    cachedInstance = null;
    ingest(methodToken);
    ingest(nameToken);
    ingest(openParen);
    ingest(closeParen);
    ingest(code);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(methodToken);
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
    if (introduceReturnToken != null) {
      yielder.accept(introduceReturnToken);
      returnType.emit(yielder);
    }
    if (tokenReadonly != null) {
      yielder.accept(tokenReadonly);
    }
    if (abortsToken != null) {
      yielder.accept(abortsToken);
    }
    code.emit(yielder);
  }

  public FunctionOverloadInstance typing(final Environment environment) {
    if (cachedInstance == null) {
      functionId = environment.autoVariable();
      returnType = environment.rules.Resolve(returnType, false);
      final var argTypes = new ArrayList<TyType>();
      for (final FunctionArg arg : args) {
        arg.typing(environment);
        argTypes.add(arg.type);
      }
      final var flow = code.typing(prepareEnvironment(environment));
      if (returnType != null && flow == ControlFlow.Open) {
        environment.document.createError(this, String.format("Function '%s' does not return in all cases", nameToken.text), "MethodDefine");
      }
      cachedInstance = new FunctionOverloadInstance("__METH_" + functionId + "_" + name, returnType, argTypes, tokenReadonly != null, false, abortsToken != null);
      cachedInstance.ingest(this);
    }
    return cachedInstance;
  }

  /** prepare the environment for execution */
  private Environment prepareEnvironment(final Environment environment) {
    var toUse = tokenReadonly != null ? environment.scopeAsReadOnlyBoundary() : environment.scopeWithCache("__cache");
    if (abortsToken != null) {
      toUse = toUse.scopeAsAbortable();
    }
    for (final FunctionArg arg : args) {
      toUse.define(arg.argName, arg.type, true, arg.type);
    }
    toUse.setReturnType(returnType);
    return toUse;
  }

  /** write the java for the function/procedure */
  public void writeFunctionJava(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("private ");
    if (returnType == null) {
      sb.append("void");
    } else {
      sb.append(returnType.getJavaConcreteType(environment));
    }
    sb.append(" ").append("__METH_").append(functionId + "_").append(name).append("(");
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
    if (abortsToken != null) {
      sb.append("throws AbortMessageException ");
    }
    code.writeJava(sb, prepareEnvironment(environment));
    sb.writeNewline();
  }
}

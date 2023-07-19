/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.FunctionArg;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.Watcher;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.function.Consumer;

/** defines a method within a structure */
public class DefineMethod extends StructureComponent {
  public final ArrayList<FunctionArg> args;
  public final Token closeParen;
  public final Block code;
  public final Token introduceReturnToken;
  public final Token methodToken;
  public final String name;
  public final Token nameToken;
  public final Token openParen;
  public TyType returnType;
  private FunctionOverloadInstance cachedInstance;
  private int functionId;
  private LinkedHashSet<String> depends;
  private LinkedHashSet<String> services;
  private final FunctionPaint paint;

  /** construct the function of a type with a name */
  public DefineMethod(final Token methodToken, final Token nameToken, final Token openParen, final ArrayList<FunctionArg> args, final Token closeParen, final Token introduceReturnToken, final TyType returnType, final FunctionPaint paint, final Block code) {
    this.methodToken = methodToken;
    this.nameToken = nameToken;
    name = nameToken.text;
    this.openParen = openParen;
    this.args = args;
    this.closeParen = closeParen;
    this.introduceReturnToken = introduceReturnToken;
    this.returnType = returnType;
    this.paint = paint;
    this.code = code;
    cachedInstance = null;
    ingest(methodToken);
    ingest(nameToken);
    ingest(openParen);
    ingest(closeParen);
    ingest(code);
    this.depends = new LinkedHashSet<>();
    this.services = new LinkedHashSet<>();
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
    paint.emit(yielder);
    code.emit(yielder);
  }

  public FunctionOverloadInstance typing(final Environment environment, HashSet<String> local) {
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
        environment.document.createError(this, String.format("Function '%s' does not return in all cases", nameToken.text));
      }
      cachedInstance = new FunctionOverloadInstance("__METH_" + functionId + "_" + name, returnType, argTypes, paint);
      for (String depend : depends) {
        if (!local.contains(depend)) {
          cachedInstance.dependencies.add(depend);
        }
      }
      cachedInstance.ingest(this);
    }
    return cachedInstance;
  }

  /** prepare the environment for execution */
  private Environment prepareEnvironment(final Environment environment) {
    var toUse = paint.pure ? environment.scopeAsReadOnlyBoundary() : environment.scopeWithCache("__cache");
    toUse = toUse.watch(Watcher.make(toUse, depends, services)).scopeDefine();
    if (paint.aborts) {
      toUse = toUse.scopeAsAbortable();
    }
    if (paint.viewer) {
      toUse = toUse.scopeWithViewer();
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
    if (paint.viewer) {
      if (!first) {
        sb.append(", ");
      }
      sb.append("RTx__ViewerType __viewer");
    }
    sb.append(") ");
    if (paint.aborts) {
      sb.append("throws AbortMessageException ");
    }
    code.writeJava(sb, prepareEnvironment(environment));
    sb.writeNewline();
  }
}

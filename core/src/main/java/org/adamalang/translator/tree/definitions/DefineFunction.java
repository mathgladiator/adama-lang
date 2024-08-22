/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.watcher.FunctionalWatcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.function.Consumer;

/** defines a function */
public class DefineFunction extends Definition {
  /** write the set of all functions in the environment */
  public final ArrayList<FunctionArg> args;
  public final HashSet<String> depends;
  public final HashSet<String> assocs;
  public final TreeSet<String> viewerFields;

  public final Token closeParen;
  public final Token functionTypeToken;
  public final Token introReturnType;
  public final String name;
  public final Token nameToken;
  public final Token openParen;
  public final FunctionPaint paint;
  public final FunctionSpecialization specialization;
  public Block code;
  public TyType returnType;
  private boolean beenGivenId;
  private int uniqueFunctionId;
  private FunctionOverloadInstance producedInstance;

  public DefineFunction(final Token functionTypeToken, final FunctionSpecialization specialization, final Token nameToken, final Token openParen, final ArrayList<FunctionArg> args, final Token closeParen, final Token introReturnType, final TyType returnType, final FunctionPaint paint, final Block code) {
    this.depends = new HashSet<>();
    this.assocs = new HashSet<>();
    this.functionTypeToken = functionTypeToken;
    this.specialization = specialization;
    this.nameToken = nameToken;
    name = nameToken.text;
    this.openParen = openParen;
    this.args = args;
    this.closeParen = closeParen;
    this.introReturnType = introReturnType;
    this.returnType = returnType;
    this.paint = paint;
    this.code = code;
    uniqueFunctionId = 0;
    beenGivenId = false;
    producedInstance = null;
    ingest(functionTypeToken);
    ingest(nameToken);
    ingest(openParen);
    ingest(closeParen);
    ingest(code);
    this.viewerFields = new TreeSet<>();
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(functionTypeToken);
    yielder.accept(nameToken);
    yielder.accept(openParen);
    for (final FunctionArg arg : args) {
      arg.emit(yielder);
    }
    yielder.accept(closeParen);
    if (introReturnType != null) {
      yielder.accept(introReturnType);
      returnType.emit(yielder);
    }
    paint.emit(yielder);
    code.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    formatter.startLine(functionTypeToken);
    code.format(formatter);
  }

  public void typing(TypeCheckerRoot checker) {
    FreeEnvironment fe = FreeEnvironment.root();
    for(FunctionArg arg : args) {
      fe.define(arg.argName);
    }
    code.free(fe);
    checker.define(nameToken, fe.free, (environment) -> {
      getFuncId(environment);
      returnType = environment.rules.Resolve(returnType, false);
      for (final FunctionArg arg : args) {
        arg.typing(environment);
      }
      final var flow = code.typing(prepareEnvironment(environment));
      if (producedInstance != null) {
        producedInstance.dependencies.addAll(depends);
        producedInstance.viewerFields.addAll(viewerFields);
        producedInstance.assocs.addAll(assocs);
      }
      if (returnType != null && flow == ControlFlow.Open) {
        environment.document.createError(this, String.format("The %s '%s' does not return in all cases", specialization == FunctionSpecialization.Pure ? "function" : "procedure", nameToken.text));
      }
    });
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
      if (paint.pure) {
        toUse = environment.scopeAsReadOnlyBoundary(); // what makes procedure so dirty
      } else {
        toUse = environment.scopeWithCache("__cache");
      }
    }
    if (paint.aborts) {
      toUse = toUse.scopeAsAbortable();
    }
    if (paint.viewer) {
      toUse = toUse.scopeWithViewer(viewerFields);
    }
    toUse = toUse.watch(new FunctionalWatcher(environment, depends, assocs)).scopeDefine();
    for (final FunctionArg arg : args) {
      if (arg.type != null) {
        boolean readonly = arg.evalReadonly(pure || paint.pure || arg.type instanceof TyNativeMessage, this, environment);
        toUse.define(arg.argName, arg.type, readonly, arg.type);
      }
    }
    toUse.setReturnType(returnType);
    return toUse;
  }

  public FunctionOverloadInstance toFunctionOverloadInstance() {
    if (producedInstance != null) {
      return producedInstance;
    }
    final var argTypes = new ArrayList<TyType>();
    for (final FunctionArg arg : args) {
      argTypes.add(arg.type);
    }
    FunctionOverloadInstance foi = new FunctionOverloadInstance("__FUNC_" + uniqueFunctionId + "_" + name, returnType, argTypes, paint);
    foi.viewerFields.addAll(viewerFields);
    foi.dependencies.addAll(depends);
    foi.assocs.addAll(assocs);
    foi.ingest(this);
    producedInstance = foi;
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
    if (environment.state.options.instrumentPerf) {
      String measure = "__measure_" + environment.autoVariable();
      sb.append("{").tabUp().writeNewline();
      sb.append("Runnable ").append(measure).append(" = __perf.measure(\"").append("fn_").append(name).append("\");").writeNewline();
      sb.append("try {").tabUp().writeNewline();
      code.specialWriteJava(sb, environment, false, true);
      sb.append("} finally {").tabUp().writeNewline();
      sb.append(measure).append(".run();").tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
      sb.append("}");
    } else {
      code.writeJava(sb, environment);
    }
    sb.writeNewline();
  }
}

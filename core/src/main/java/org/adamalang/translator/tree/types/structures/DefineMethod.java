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
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.FunctionArg;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.watcher.SimpleWatcherForVariablesAndServices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;
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
  public final TreeSet<String> viewerFields;
  private final StructureStorage storage;

  /** construct the function of a type with a name */
  public DefineMethod(final Token methodToken, final Token nameToken, final Token openParen, final ArrayList<FunctionArg> args, final Token closeParen, final Token introduceReturnToken, final TyType returnType, final FunctionPaint paint, final Block code, StructureStorage storage) {
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
    this.viewerFields = new TreeSet<>();
    this.storage = storage;
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(methodToken);
    yielder.accept(nameToken);
    yielder.accept(openParen);
    for (final FunctionArg arg : args) {
      arg.emit(yielder);
    }
    yielder.accept(closeParen);
    if (introduceReturnToken != null) {
      yielder.accept(introduceReturnToken);
      returnType.emit(yielder);
    }
    paint.emit(yielder);
    code.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    formatter.startLine(methodToken);
    for (final FunctionArg arg : args) {
      arg.type.format(formatter);
    }
    if (introduceReturnToken != null) {
      returnType.format(formatter);
    }
    paint.format(formatter);
    code.format(formatter);
  }

  public FunctionOverloadInstance typing(StructureStorage storage, final Environment environment, HashSet<String> local) {
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
        } else {
          cachedInstance.recordDependencies.add(depend);
        }
      }
      cachedInstance.viewerFields.addAll(viewerFields);
      cachedInstance.withinRecord.set(storage.name.text);
      cachedInstance.ingest(this);
    }
    return cachedInstance;
  }

  /** prepare the environment for execution */
  private Environment prepareEnvironment(final Environment environment) {
    var toUse = paint.pure ? environment.scopeAsReadOnlyBoundary() : environment.scopeWithCache("__cache");
    toUse = toUse.watch(new SimpleWatcherForVariablesAndServices(environment, depends, services)).scopeDefine();
    if (paint.aborts) {
      toUse = toUse.scopeAsAbortable();
    }
    if (paint.viewer) {
      toUse = toUse.scopeWithViewer(viewerFields);
    }
    for (final FunctionArg arg : args) {
      boolean readonly = arg.evalReadonly(true, this, environment);
      toUse.define(arg.argName, arg.type, readonly, arg.type);
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

    if (environment.state.options.instrumentPerf && storage.specialization == StorageSpecialization.Record) {
      String measure = "__measure_" + environment.autoVariable();
      sb.append("{").tabUp().writeNewline();
      sb.append("Runnable ").append(measure).append(" = __perf.measure(\"").append("mt_").append(name).append("\");").writeNewline();
      sb.append("try {").tabUp().writeNewline();
      code.specialWriteJava(sb, prepareEnvironment(environment), false, true);
      sb.append("} finally {").tabUp().writeNewline();
      sb.append(measure).append(".run();").tabDown().writeNewline();
      sb.append("}").tabDown().writeNewline();
      sb.append("}");
    } else {
      code.writeJava(sb, prepareEnvironment(environment));
    }
    sb.writeNewline();
  }
}

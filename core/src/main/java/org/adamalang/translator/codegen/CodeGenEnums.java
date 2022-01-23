/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineDispatcher;
import org.adamalang.translator.tree.definitions.FunctionArg;
import org.adamalang.translator.tree.types.shared.EnumStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** generates the code to support enumerations */
public class CodeGenEnums {
  public static void writeDispatchers(final StringBuilderWithTabs sb, final EnumStorage storage, final HashMap<String, ArrayList<DefineDispatcher>> multi, final String name, final Environment environment) {
    for (final Map.Entry<String, ArrayList<DefineDispatcher>> entry : multi.entrySet()) {
      writeDispatcher(sb, storage, entry.getValue(), storage.getId(name, entry.getKey()), environment);
    }
  }

  public static void writeDispatcher(final StringBuilderWithTabs sb, final EnumStorage storage, final ArrayList<DefineDispatcher> dispatchers, final int dispatcherIndex, final Environment environment) {
    // write the individual functions
    final var firstDispatcher = dispatchers.get(0);
    DefineDispatcher catchAll = null;
    for (final DefineDispatcher potential : storage.findFindingDispatchers(dispatchers, storage.getDefaultLabel(), true).values()) {
      final var takeIt = catchAll == null || potential.valueToken != null && storage.getDefaultLabel().equals(potential.valueToken.text) && potential.starToken == null;
      if (takeIt) {
        catchAll = potential;
      }
    }
    for (final DefineDispatcher dispatcher : dispatchers) {
      sb.append("private ");
      if (dispatcher.returnType == null) {
        sb.append("void");
      } else {
        sb.append(dispatcher.returnType.getJavaConcreteType(environment));
      }
      if (dispatcher.starToken != null && dispatcher.valueToken == null) {
        catchAll = dispatcher;
      }
      sb.append(" __IND_DISPATCH_").append(dispatcherIndex + "_").append(dispatcher.functionName.text).append("__" + dispatcher.positionIndex).append("(");
      sb.append("int self");
      for (final FunctionArg arg : dispatcher.args) {
        sb.append(", ");
        sb.append(arg.type.getJavaConcreteType(environment)).append(" ").append(arg.argName);
      }
      sb.append(") ");
      dispatcher.code.writeJava(sb, dispatcher.prepareEnvironment(environment));
      sb.writeNewline();
    }
    sb.append("private ");
    if (firstDispatcher.returnType == null) {
      sb.append("void");
    } else {
      sb.append(firstDispatcher.returnType.getJavaConcreteType(environment));
    }
    sb.append(" __DISPATCH_").append(dispatcherIndex + "_").append(firstDispatcher.functionName.text).append("(int __value");
    for (final FunctionArg arg : firstDispatcher.args) {
      sb.append(", ");
      sb.append(arg.type.getJavaConcreteType(environment)).append(" ").append(arg.argName);
    }
    sb.append(") {").tabUp().writeNewline();
    for (final Map.Entry<String, Integer> option : storage.options.entrySet()) {
      final var matches = storage.findFindingDispatchers(dispatchers, option.getKey(), firstDispatcher.returnType == null);
      sb.append("if (__value == ").append(option.getValue() + "").append(") {").tabUp().writeNewline();
      var atSpecifc = 0;
      for (final Map.Entry<String, DefineDispatcher> associatedDispatcher : matches.entrySet()) {
        atSpecifc++;
        if (associatedDispatcher.getValue().returnType != null) {
          sb.append("return ");
        }
        sb.append("__IND_DISPATCH_").append(dispatcherIndex + "_").append(firstDispatcher.functionName.text).append("__" + associatedDispatcher.getValue().positionIndex).append("(__value");
        for (final FunctionArg arg : associatedDispatcher.getValue().args) {
          sb.append(", ");
          sb.append(arg.argName);
        }
        sb.append(");");
        if (atSpecifc == matches.size()) {
          if (associatedDispatcher.getValue().returnType == null) {
            sb.writeNewline().append("return;");
          }
          sb.tabDown();
        }
        sb.writeNewline();
      }
      sb.append("}");
      sb.writeNewline();
    }
    if (catchAll.returnType != null) {
      sb.append("return ");
    }
    sb.append("__IND_DISPATCH_").append(dispatcherIndex + "_").append(firstDispatcher.functionName.text).append("__" + catchAll.positionIndex).append("(__value");
    for (final FunctionArg arg : catchAll.args) {
      sb.append(", ");
      sb.append(arg.argName);
    }
    sb.append(");").tabDown().writeNewline();
    sb.append("}").writeNewline();
  }

  /** write an enum array out, maybe it filtered by prefix */
  public static void writeEnumArray(final StringBuilderWithTabs sb, final String name, final String id, final String prefix, final EnumStorage storage) {
    sb.append("private static final int [] __").append(id).append("_").append(name).append(" = new int[] {");
    final var filtered = new ArrayList<Map.Entry<String, Integer>>();
    for (final Map.Entry<String, Integer> option : storage.options.entrySet()) {
      if (option.getKey().startsWith(prefix)) {
        filtered.add(option);
      }
    }
    var first = true;
    for (final Map.Entry<String, Integer> option : filtered) {
      if (first) {
        first = false;
      } else {
        sb.append(", ");
      }
      sb.append(Integer.toString(option.getValue())); // value
    }
    sb.append("};").writeNewline();
  }
}

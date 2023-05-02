/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.definitions.DefineFunction;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;

import java.util.ArrayList;
import java.util.Iterator;

/** responsible for writing all document level functions */
public class CodeGenFunctions {
  public static void writeArgsJava(final StringBuilder sb, final Environment environment, final boolean firstSeed, final ArrayList<TokenizedItem<Expression>> args, final FunctionOverloadInstance functionInstance) {
    var first = firstSeed;
    Iterator<TyType> castIt = functionInstance.types.iterator();
    for (final TokenizedItem<Expression> arg : args) {
      if (!first) {
        sb.append(", ");
      } else {
        first = false;
      }
      boolean cast = functionInstance.castArgs && castIt.hasNext() && arg.item.passedTypeChecking();
      if (cast) {
        String concreteType = castIt.next().getJavaConcreteType(environment);
        String givenType = arg.item.getCachedType().getJavaConcreteType(environment);
        cast = !concreteType.equals(givenType);
        if (cast) {
          sb.append("(");
          sb.append(concreteType);
          sb.append(")");
          sb.append("(");
        }
      }
      arg.item.writeJava(sb, environment);
      if (cast) {
        sb.append(")");
      }
    }
    for (final String hiddenSuffix : functionInstance.hiddenSuffixArgs) {
      if (!first) {
        sb.append(", ");
      } else {
        first = false;
      }
      sb.append(hiddenSuffix);
    }
  }

  public static void writeFunctionsJava(final StringBuilderWithTabs sb, final Environment environment) {
    for (final DefineFunction df : environment.document.functionDefinitions) {
      df.writeFunctionJava(sb, df.prepareEnvironment(environment));
    }
  }
}

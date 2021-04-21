/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.codegen;

import java.util.ArrayList;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.definitions.DefineFunction;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;

/** responsible for writing all document level functions */
public class CodeGenFunctions {
  public static void writeArgsJava(final StringBuilder sb, final Environment environment, final boolean firstSeed, final ArrayList<TokenizedItem<Expression>> args, final FunctionOverloadInstance functionInstance) {
    var first = firstSeed;
    for (final TokenizedItem<Expression> arg : args) {
      if (!first) {
        sb.append(", ");
      } else {
        first = false;
      }
      arg.item.writeJava(sb, environment);
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

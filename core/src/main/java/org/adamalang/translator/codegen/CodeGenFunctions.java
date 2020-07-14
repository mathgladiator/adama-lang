/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.codegen;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.definitions.DefineFunction;

/** responsible for writing all document level functions */
public class CodeGenFunctions {
  public static void writeFunctionsJava(final StringBuilderWithTabs sb, final Environment environment) {
    for (final DefineFunction df : environment.document.functionDefinitions) {
      df.writeFunctionJava(sb, df.prepareEnvironment(environment));
    }
  }
}

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

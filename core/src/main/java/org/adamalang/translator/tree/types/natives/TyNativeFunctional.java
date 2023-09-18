/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class TyNativeFunctional extends TyType {
  public final String name;
  public final ArrayList<FunctionOverloadInstance> overloads;
  public final FunctionStyleJava style;

  public TyNativeFunctional(final String name, final ArrayList<FunctionOverloadInstance> overloads, final FunctionStyleJava style) {
    super(TypeBehavior.ReadOnlyNativeValue);
    this.name = name;
    this.overloads = overloads;
    this.style = style;
  }

  @Override
  public void emitInternal(final Consumer<Token> yielder) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getAdamaType() {
    return "$<" + name + ">";
  }

  @Override
  public String getJavaBoxType(final Environment environment) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getJavaConcreteType(final Environment environment) {
    throw new UnsupportedOperationException();
  }

  @Override
  public TyType makeCopyWithNewPositionInternal(final DocumentPosition position, final TypeBehavior newBehavior) {
    return new TyNativeFunctional(name, overloads, style).withPosition(position);
  }

  @Override
  public void typing(final Environment environment) {
    for (final FunctionOverloadInstance fo : overloads) {
      fo.typing(environment);
    }
    for (var k = 0; k < overloads.size(); k++) {
      for (var j = k + 1; j < overloads.size(); j++) {
        overloads.get(k).testOverlap(overloads.get(j), environment);
      }
    }
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer, ReflectionSource source) {
    writer.beginObject();
    writer.writeObjectFieldIntro("nature");
    writer.writeString("native_functional");
    writeAnnotations(writer);
    writer.endObject();
  }

  /** to help typing anonymous messages to existing messages */
  public FunctionOverloadInstance guess(int count) {
    if (overloads.size() == 1) {
      if (overloads.get(0).types.size() == count) {
        return overloads.get(0);
      }
    }
    return null;
  }


  /** find the right instance basedd on the given types */
  public FunctionOverloadInstance find(final DocumentPosition position, final ArrayList<TyType> argTypes, final Environment environment) {
    var result = overloads.get(0);
    var score = (argTypes.size() + 1) * (argTypes.size() + 1);
    for (final FunctionOverloadInstance candidate : overloads) {
      final var testScore = candidate.score(environment, argTypes);
      if (testScore < score) {
        score = testScore;
        result = candidate;
      }
    }
    result.test(position, environment, argTypes);
    return result;
  }

  public Set<String> gatherDependencies() {
    HashSet<String> set = new HashSet<>();
    for (FunctionOverloadInstance foi : overloads) {
      set.addAll(foi.dependencies);
    }
    return set;
  }
}

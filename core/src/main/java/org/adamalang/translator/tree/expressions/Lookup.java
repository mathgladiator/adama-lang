/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.expressions;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.traits.details.DetailComputeRequiresGet;

/** look up a variable in the current environment and then extract its value */
public class Lookup extends Expression {
  private boolean addGet;
  private boolean hide;
  public final Token variableToken;

  /** the variable to look up */
  public Lookup(final Token variableToken) {
    this.variableToken = variableToken;
    hide = false;
    addGet = false;
    ingest(variableToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(variableToken);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    var type = environment.lookup(variableToken.text, environment.state.isContextComputation(), this, false);
    if (type == null) {
      final var globalObject = environment.state.globals.get(variableToken.text);
      if (globalObject != null) {
        hide = true;
        return globalObject;
      }
      environment.document.createError(this, String.format("The variable '%s' was not defined", variableToken.text), "VariableLookup");
    }
    if (type != null && environment.state.isContextComputation() && type instanceof DetailComputeRequiresGet) {
      addGet = true;
      type = ((DetailComputeRequiresGet) type).typeAfterGet(environment);
      if (type != null) {
        type = type.makeCopyWithNewPosition(this, type.behavior);
      }
    }
    return type;
  }

  /** note: the context matters here. If we are assigning, then we must return a
   * relevant mode of assignment to the underlying variable. */
  @Override
  // move the context into the environment
  public void writeJava(final StringBuilder sb, final Environment environment) {
    environment.lookup(variableToken.text, environment.state.isContextComputation(), this, true);
    if (!hide) {
      sb.append(variableToken.text);
      if (addGet) {
        sb.append(".get()");
      }
    }
  }
}

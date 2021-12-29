/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;

/** argument pair for the tuple (type, name) */
public class FunctionArg {
  public String argName;
  public final Token argNameToken;
  public final Token commaToken;
  public TyType type;

  public FunctionArg(final Token commaToken, final TyType type, final Token argNameToken) {
    this.commaToken = commaToken;
    this.type = type;
    this.argNameToken = argNameToken;
    argName = argNameToken.text;
  }

  public void typing(final Environment environment) {
    type = environment.rules.Resolve(type, false);
    if (type != null) {
      type.typing(environment);
    }
  }
}

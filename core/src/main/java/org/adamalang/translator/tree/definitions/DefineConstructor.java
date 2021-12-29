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
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeClient;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;

import java.util.function.Consumer;

/** defines a constructor which runs when the document is created */
public class DefineConstructor extends Definition {
  public TyNativeClient clientType;
  public final Token clientTypeToken;
  public final Token clientVarToken;
  public final Block code;
  public final Token commaToken;
  public final Token constructToken;
  public final Token endParenToken;
  public final Token messageNameToken;
  public final Token messageTypeToken;
  public final Token openParenToken;
  public TyType unifiedMessageType;
  public String unifiedMessageTypeNameToUse;

  public DefineConstructor(final Token constructToken, final Token openParenToken, final Token clientTypeToken, final Token clientVarToken, final Token commaToken, final Token messageTypeToken, final Token messageNameToken,
      final Token endParenToken, final Block code) {
    this.constructToken = constructToken;
    this.openParenToken = openParenToken;
    this.clientTypeToken = clientTypeToken;
    this.clientVarToken = clientVarToken;
    if (clientTypeToken != null) {
      clientType = new TyNativeClient(TypeBehavior.ReadOnlyNativeValue, null, clientTypeToken);
      clientType.ingest(clientTypeToken);
      clientType.ingest(clientVarToken);
    }
    this.commaToken = commaToken;
    this.messageTypeToken = messageTypeToken;
    this.messageNameToken = messageNameToken;
    this.endParenToken = endParenToken;
    this.code = code;
    ingest(constructToken);
    ingest(code);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(constructToken);
    if (commaToken != null || clientTypeToken != null) {
      yielder.accept(openParenToken);
      if (clientTypeToken != null) {
        yielder.accept(clientTypeToken);
        yielder.accept(clientVarToken);
      }
      if (commaToken != null) {
        yielder.accept(commaToken);
        yielder.accept(messageTypeToken);
        yielder.accept(messageNameToken);
      }
      yielder.accept(endParenToken);
    } else if (messageNameToken != null) {
      yielder.accept(openParenToken);
      yielder.accept(messageTypeToken);
      yielder.accept(messageNameToken);
      yielder.accept(endParenToken);
    }
    code.emit(yielder);
  }

  @Override
  public void typing(final Environment environment) {
    final var next = environment.scope();
    if (clientType != null) {
      next.define(clientVarToken.text, clientType, true, clientType);
    }
    if (messageNameToken != null && messageTypeToken != null && unifiedMessageType != null) {
      next.define(messageNameToken.text, unifiedMessageType, false, unifiedMessageType);
      unifiedMessageTypeNameToUse = ((TyNativeMessage) unifiedMessageType).name;
    }
    next.setReturnType(null);
    if (code != null) {
      code.typing(next);
    }
  }
}

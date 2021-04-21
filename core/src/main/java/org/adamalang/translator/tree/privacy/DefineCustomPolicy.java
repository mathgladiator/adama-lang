/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree.privacy;

import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;
import org.adamalang.translator.tree.types.natives.TyNativeClient;

/** used within a record to define a custom policy */
public class DefineCustomPolicy extends DocumentPosition {
  public final TyNativeClient clientType;
  public final Token clientVar;
  public final Block code;
  public final Token definePolicy;
  public final Token endParen;
  public final Token name;
  public final Token openParen;
  public final TyNativeBoolean policyType;

  public DefineCustomPolicy(final Token definePolicy, final Token name, final Token openParen, final Token clientVar, final Token endParen, final Block code) {
    this.definePolicy = definePolicy;
    this.name = name;
    this.openParen = openParen;
    this.clientVar = clientVar;
    this.endParen = endParen;
    this.code = code;
    policyType = new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, name);
    clientType = new TyNativeClient(TypeBehavior.ReadOnlyNativeValue, null, clientVar);
    ingest(name);
    ingest(code);
    clientType.ingest(clientVar);
    policyType.ingest(name);
  }

  public void emit(final Consumer<Token> yielder) {
    yielder.accept(definePolicy);
    yielder.accept(name);
    yielder.accept(openParen);
    yielder.accept(clientVar);
    yielder.accept(endParen);
    code.emit(yielder);
  }

  public void typeCheck(final Environment environment) {
    final var flow = code.typing(environment.scope().define(clientVar.text, clientType, true, clientType).setReturnType(policyType));
    if (flow == ControlFlow.Open) {
      environment.document.createError(this, String.format("Policy '%s' does not return in all cases", name.text), "PolicyDefine");
    }
  }
}

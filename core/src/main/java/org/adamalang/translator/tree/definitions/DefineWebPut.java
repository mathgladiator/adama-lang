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
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.tree.definitions.web.Uri;
import org.adamalang.translator.tree.definitions.web.UriAction;
import org.adamalang.translator.tree.definitions.web.WebGuard;
import org.adamalang.translator.tree.statements.Block;
import org.adamalang.translator.tree.statements.ControlFlow;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.topo.TypeCheckerRoot;
import org.adamalang.translator.tree.types.natives.TyNativeRef;

import java.util.TreeMap;
import java.util.function.Consumer;

public class DefineWebPut extends Definition implements UriAction {
  public final Token webToken;
  public final Token postToken;
  public final Uri uri;
  public final Token openParen;
  public final Token messageType;
  public final Token messageVariable;
  public final Token closeParen;
  public final WebGuard guard;
  public final Block code;
  private TyType messageTypeFound;

  public DefineWebPut(Token webToken, Token postToken, Uri uri, Token openParen, Token messageType, Token messageVariable, Token closeParen, WebGuard guard, Block code) {
    this.webToken = webToken;
    this.postToken = postToken;
    this.uri = uri;
    this.openParen = openParen;
    this.messageType = messageType;
    this.messageVariable = messageVariable;
    this.closeParen = closeParen;
    this.guard = guard;
    this.code = code;
    this.messageTypeFound = null;
    ingest(webToken);
    ingest(code);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(webToken);
    yielder.accept(postToken);
    uri.emit(yielder);
    yielder.accept(openParen);
    yielder.accept(messageType);
    yielder.accept(messageVariable);
    yielder.accept(closeParen);
    if (guard != null) {
      guard.emit(yielder);
    }
    code.emit(yielder);
  }

  @Override
  public void format(Formatter formatter) {
    formatter.startLine(webToken);
    uri.format(formatter);
    if (guard != null) {
      guard.format(formatter);
    }
    code.format(formatter);
  }

  @Override
  public TreeMap<String, TyType> parameters() {
    return uri.variables;
  }

  public Environment next(Environment environment) {
    Environment env = environment.scopeAsAbortable().scopeAsWeb("put");
    uri.extendInto(env);
    env.define(messageVariable.text, messageTypeFound, false, this);
    uri.typing(env);
    return env.scopeWithCache("__currentWebCache");
  }

  public void typing(TypeCheckerRoot checker) {
    if (guard != null) {
      guard.typing(checker);
    }
    FreeEnvironment fe = FreeEnvironment.root();
    code.free(fe);
    checker.register(fe.free, (environment) -> {
      messageTypeFound = environment.rules.Resolve(new TyNativeRef(TypeBehavior.ReadWriteNative, null, messageType), false);
      if (messageTypeFound != null) {
        if (environment.rules.IsNativeMessage(messageTypeFound, false)) {
          Environment env = next(environment);
          if (code.typing(env) == ControlFlow.Open) {
            environment.document.createError(this, String.format("The @web handlers must return a message"));
          }
        }
      }
    });
  }
}

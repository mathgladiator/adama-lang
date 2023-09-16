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
package org.adamalang.translator.tree.expressions.constants;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeSecurePrincipal;

import java.util.function.Consumer;

public class WhoClientConstant extends Expression {
  public final Token token;

  public WhoClientConstant(final Token token) {
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    if (environment.state.isStatic() || environment.state.isMessageHandler() || environment.state.isPolicy() || environment.state.isBubble() || environment.state.isWeb()) {
      environment.mustBeComputeContext(this);
      TyType type = new TyNativeSecurePrincipal(TypeBehavior.ReadOnlyNativeValue, null, token, token, token, token).withPosition(this);
      environment.useSpecial(type, "__who");
      return type;
    } else {
      environment.document.createError(this, "@who is only available from static policies, document policies, privacy policies, bubbles, web paths, and message handlers");
      return null;
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append("__who");
  }

  @Override
  public void free(FreeEnvironment environment) {
  }
}

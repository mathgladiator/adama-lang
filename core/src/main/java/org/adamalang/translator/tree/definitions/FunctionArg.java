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
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;
import org.adamalang.translator.tree.types.natives.TyNativeTable;

import java.util.function.Consumer;

/** argument pair for the tuple (type, name) */
public class FunctionArg {
  public final Token commaToken;
  public final Token modifierToken;
  public TyType type;
  public final Token argNameToken;
  public String argName;

  public FunctionArg(final Token commaToken, final Token modifierToken, final TyType type, final Token argNameToken) {
    this.modifierToken = modifierToken;
    this.commaToken = commaToken;
    this.type = type;
    this.argNameToken = argNameToken;
    argName = argNameToken.text;
  }

  public void emit(final Consumer<Token> yielder) {
    if (commaToken != null) {
      yielder.accept(commaToken);
    }
    if (modifierToken != null) {
      yielder.accept(modifierToken);
    }
    type.emit(yielder);
    yielder.accept(argNameToken);
  }

  public void typing(final Environment environment) {
    type = environment.rules.Resolve(type, false);
    if (type != null) {
      type.typing(environment);
    }
  }

  public boolean evalReadonly(boolean previous, DocumentPosition pos, Environment environment) {
    if (modifierToken != null) {
      if (modifierToken.text.equals("readonly")) {
        return true;
      } else if (modifierToken.text.equals("mutable")) {
        validateMutableType(pos, environment);
        return false;
      }
    }
    if (type.behavior == TypeBehavior.ReadOnlyWithGet) {
      return true;
    }
    return previous;
  }

  public void validateMutableType(DocumentPosition pos, Environment env) {
    if (type != null) {
      TyType resolved = env.rules.Resolve(type, false);
      if (resolved instanceof TyNativeTable) {
        // tis-valid
        return;
      }
      if (resolved instanceof TyNativeMessage) {
        return;
      }
      env.document.createError(pos, "Type " + type.getAdamaType() + " is not a mutable type.");
    }
  }
}

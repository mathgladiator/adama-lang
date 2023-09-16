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
package org.adamalang.translator.tree.privacy;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.checking.ruleset.RuleSetAsync;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;

import java.util.function.Consumer;

/** a policy that enables the field to be visible if another field is the viewer */
public class ViewerIsPolicy extends Policy {
  public final Token closeToken;
  public final Token fieldToken;
  public final Token openToken;
  public final Token viewerIsToken;

  public ViewerIsPolicy(final Token viewerIsToken, final Token openToken, final Token fieldToken, final Token closeToken) {
    this.viewerIsToken = viewerIsToken;
    this.openToken = openToken;
    this.fieldToken = fieldToken;
    this.closeToken = closeToken;
    ingest(viewerIsToken);
    ingest(closeToken);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(viewerIsToken);
    yielder.accept(openToken);
    yielder.accept(fieldToken);
    yielder.accept(closeToken);
  }

  @Override
  public void typing(final Environment environment, final StructureStorage owningStructureStorage) {
    final var fd = owningStructureStorage.fields.get(fieldToken.text);
    if (fd == null) {
      environment.document.createError(this, String.format("Field '%s' was not defined within the record", fieldToken.text));
      return;
    }
    RuleSetAsync.IsPrincipal(environment, fd.type, false);
  }

  @Override
  public boolean writePrivacyCheckGuard(final StringBuilderWithTabs sb, final FieldDefinition field, final Environment environment) {
    sb.append("if (__writer.who.equals(__item.").append(fieldToken.text).append(".get())) {").tabUp().writeNewline();
    return true;
  }

  @Override
  public void writeTypeReflectionJson(JsonStreamWriter writer) {
    writer.writeString("viewer_is");
  }

  @Override
  public void free(FreeEnvironment environment) {
    environment.require(fieldToken.text);
  }
}

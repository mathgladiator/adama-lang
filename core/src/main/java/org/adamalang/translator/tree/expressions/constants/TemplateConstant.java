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

import org.adamalang.common.Escaping;
import org.adamalang.common.template.Parser;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.env.FreeEnvironment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.Formatter;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeTemplate;

import java.util.function.Consumer;

/** constant templates */
public class TemplateConstant extends Expression {
  private final Token token;

  public TemplateConstant(Token token) {
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  public void format(Formatter formatter) {
  }

  @Override
  protected TyType typingInternal(Environment environment, TyType suggestion) {
    try {
      Parser.parse(raw());
    } catch (Exception ex) {
      environment.document.createError(this, "the template doesn't parse");
    }
    return new TyNativeTemplate(token);
  }

  @Override
  public void free(FreeEnvironment environment) {
  }

  private String raw() {
    String text = token.text;
    int kSecond = text.indexOf('`', 1);
    return text.substring(kSecond + 1, text.length() - kSecond * 2 + 1);
  }

  @Override
  public void writeJava(StringBuilder sb, Environment environment) {
    sb.append("new NtTemplate(\"").append(new Escaping(raw()).go()).append("\")");
  }
}

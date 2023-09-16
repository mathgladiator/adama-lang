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
package org.adamalang.rxhtml;

import org.adamalang.common.Escaping;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.exceptions.ParseException;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.definitions.web.Uri;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeDouble;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.natives.TyNativeString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

/** Tools to convert RxHTML scheme into Adama */
public class RxHtmlToAdama {

  public static String codegen(String rxhtml) throws AdamaLangException {
    RxHtmlResult result = RxHtmlTool.convertStringToTemplateForest(rxhtml, ShellConfig.start().end());
    StringBuilder adama = new StringBuilder();
    for (Uri uri : assembleUrisFrom(rxhtml)) {
      String shell = result.shell.makeShell(result);
      adama.append("@web get ").append(uri.rxhtmlPath()).append(" {\n");
      adama.append("  return {\n");
      adama.append("    html: \"").append(new Escaping(shell).go()).append("\"\n");
      adama.append("  };\n");
      adama.append("}\n");
    }
    return adama.toString();
  }

  /** parse and assemble the uris */
  public static Uri[] assembleUrisFrom(String rxhtml) throws AdamaLangException {
    ArrayList<Uri> uris = new ArrayList<>();
    Document document = Jsoup.parse(rxhtml);
    for (Element element : document.getElementsByTag("page")) {
      uris.add(uriOf(element.attr("uri")));
    }
    return uris.toArray(new Uri[uris.size()]);
  }

  /** convert the raw string uri into a parsed Uri structure used by Adama */
  public static Uri uriOf(String uriRaw) throws AdamaLangException {
    String[] uriRawParts = uriRaw.split(Pattern.quote("/"), -1);
    for (int k = 0; k < uriRawParts.length; k++) {
      if (!(uriRawParts[k].startsWith("$") || uriRawParts[k].equals(""))) {
        uriRawParts[k] = "\"" + uriRawParts[k] + "\"";
      }
    }
    Uri uri = new Uri();
    TokenEngine tokens = new TokenEngine("uri", String.join("/", uriRawParts).codePoints().iterator());
    Token hasMore;
    while ((hasMore = tokens.popIf((t) -> t.isSymbolWithTextEq("/"))) != null) {
      Token isParameter = tokens.popIf((t) -> t.isSymbolWithTextEq("$"));
      if (isParameter != null) {
        Token parameter = id(tokens);
        var colon = tokens.popIf((t) -> t.isSymbolWithTextEq(":"));
        final TyType type;
        if (colon == null) {
          colon = Token.WRAP(":");
          type = new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, parameter);
        } else {
          Token typeRaw = id(tokens);
          String typeNameFixed = typeRaw.text.trim().toLowerCase(Locale.ROOT);
          if ("number".equals(typeNameFixed) || "double".equals(typeNameFixed)) {
            type = new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, typeRaw);
          } else if ("int".equals(typeNameFixed)) {
            type = new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, typeRaw);
          } else {
            type = new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, typeRaw);
          }
        }
        uri.push(hasMore, isParameter, parameter, null, colon, type);
      } else {
        Token uriMore = tokens.popIf((t) -> t.isIdentifier() || t.isStringLiteral());
        uri.push(hasMore, null, uriMore, null, null, null);
      }
    }
    return uri;
  }

  /** read an identifier from the tokenengine */
  private static Token id(TokenEngine tokens) throws AdamaLangException {
    Token id = tokens.pop();
    if (id == null) {
      throw new ParseException("Parser was expecting an identifier, but got end of stream instead.", tokens.getLastTokenIfAvailable());
    } else {
      if (id.isIdentifier()) {
        return id;
      } else {
        throw new ParseException("Parser was expecting an identifier, but got a " + id.majorType + ":" + id.text + " instead.", id);
      }
    }
  }
}

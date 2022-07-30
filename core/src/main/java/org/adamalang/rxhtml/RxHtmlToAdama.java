/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

import org.adamalang.rxhtml.template.Escapes;
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
import java.util.Random;

/** Tools to convert RxHTML scheme into Adama */
public class RxHtmlToAdama {

  public static String codegen(String rxhtml) throws AdamaLangException {
    String template = RxHtmlTool.convertStringToTemplateForest(rxhtml, Feedback.NoOp);
    String escapedTemplate = Escapes.escape34(template);
    // TODO escape
    String path = "t_" + Long.toString(System.currentTimeMillis(), 16) + ".js";

    StringBuilder adama = new StringBuilder();
    adama.append("@web get /\"").append(path).append("\" {\n");
    adama.append("  return {\n");
    adama.append("    js: \"" + escapedTemplate + "\"\n");
    adama.append("  };\n");
    adama.append("}\n");

    // TODO: make a pure shell function; Or a add CONST to adama?
    StringBuilder shell = new StringBuilder();
    shell.append("link to:" + path);

    for (Uri uri : assembleUrisFrom(rxhtml)) {
      adama.append("@web get ").append(uri.rxhtmlPath()).append(" {\n");
      adama.append("  return {\n");
      adama.append("    html: \"TODO\"\n");
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
    Uri uri = new Uri();
    TokenEngine tokens = new TokenEngine("uri", uriRaw.codePoints().iterator());
    Token hasMore;
    while ((hasMore = tokens.popIf((t) -> t.isSymbolWithTextEq("/"))) != null) {
      Token isParameter = tokens.popIf((t) -> t.isSymbolWithTextEq("$"));
      if (isParameter != null) {
        Token parameter = id(tokens);

        var colon = tokens.popIf((t) -> t.isSymbolWithTextEq(":"));
        TyType type = null;
        if (colon == null) {
          colon = Token.WRAP(":");
          type = new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, parameter);
        } else {
          Token typeRaw = id(tokens);
          if ("text".equals(typeRaw.text) || "string".equals(typeRaw.text)) {
            type = new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, typeRaw);
          } else if ("number".equals(typeRaw.text) || "double".equals(typeRaw.text)) {
            type = new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, typeRaw);
          } else if ("int".equals(typeRaw.text)) {
            type = new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, typeRaw);
          }
        }
        if (type == null) {
          throw new ParseException("Parser was expected a type of text, string, number, double, or int.", parameter);
        }
        uri.push(hasMore, isParameter, parameter, null, colon, type);
      } else {
        Token uriMore = tokens.popIf((t) -> t.isIdentifier());
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

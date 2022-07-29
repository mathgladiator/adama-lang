package org.adamalang.rxhtml;

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

/** Tools to convert RxHTML scheme into Adama */
public class RxHtmlToAdama {

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

        final var colon = tokens.pop();
        if (colon == null) {
          throw new ParseException("Parser was expecting a colon", tokens.getLastTokenIfAvailable());
        }
        if (!colon.isSymbolWithTextEq(":")) {
          throw new ParseException("Parser was expecting a colon", tokens.getLastTokenIfAvailable());
        }
        Token typeRaw = id(tokens);
        TyType type = null;
        if ("text".equals(typeRaw.text) || "string".equals(typeRaw.text)) {
          type = new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, typeRaw);
        } else if ("number".equals(typeRaw.text) || "double".equals(typeRaw.text)) {
          type = new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, typeRaw);
        } else if ("int".equals(typeRaw.text)) {
          type = new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, typeRaw);
        }
        if (type == null) {
          throw new ParseException("Parser was expected a type of text, string, number, double, or int.", typeRaw);
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

package org.adamalang.rxhtml;

import org.adamalang.rxhtml.tree.Attribute;
import org.adamalang.rxhtml.tree.Item;
import org.adamalang.rxhtml.tree.Node;
import org.adamalang.rxhtml.tree.Text;
import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.exceptions.ParseException;
import org.adamalang.translator.parser.exceptions.ScanException;
import org.adamalang.translator.parser.token.MajorTokenType;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.common.TokenizedItem;

import java.util.ArrayList;

public class Parser {
  public TokenEngine tokens;

  public Parser(final TokenEngine tokens) {
    this.tokens = tokens;
  }

  private Token testId(final Token id) throws AdamaLangException {
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
  public Token id() throws AdamaLangException {
    return testId(tokens.pop());
  }

  private Token notNullNext(String message) throws AdamaLangException {
    Token next = tokens.pop();
    if (next == null) {
      throw new ScanException("Parser was expecting " + message + ", but got end of stream instead", tokens.position());
    }
    return next;
  }

  public Node root() throws AdamaLangException {
    Token openSym = notNullNext("<");
    Token tag = id();
    TokenizedItem<Token> open = new TokenizedItem<>(tag);
    open.before(openSym);
    return node(open);
  }

  private Node node(TokenizedItem<Token> open) throws AdamaLangException {
    // Already have scanned '<' 'tag'
    ArrayList<Attribute> attributes = new ArrayList<>();
    ArrayList<Item> children = new ArrayList<>();
    Token next = notNullNext("'>' or an identifier");
    while (!next.isSymbolWithTextEq(">")) {
      if (next.majorType == MajorTokenType.Identifier) {
        Token name = next;
        next = notNullNext("'=' or an identifier");
        if (next.isSymbolWithTextEq("=")) {
          Token equals = next;
          attributes.add(new Attribute(name, equals, notNullNext("anything")));
          next = tokens.pop();
        } else {
          attributes.add(new Attribute(name, null, null));
        }
      } else {
        throw new ScanException("Was expecting a '>' or an identifier, but got '" + next.text + "' instead", tokens.position());
      }
    }
    open.after(next);
    while (true) {
      next = notNullNext("anything");
      if (next.isSymbolWithTextEq("<")) {
        Token testOpen = next;
        next = notNullNext("anything");
        if (next.isSymbolWithTextEq("/")) {
          Token tag = id();
          TokenizedItem<Token> close = new TokenizedItem<>(tag);
          close.before(testOpen);
          close.before(next);
          next = notNullNext(">");
          close.after(next);
          if (!next.isSymbolWithTextEq(">")) {
            throw new ScanException("Was expecting a '>' or an identifier, but got '" + next.text + "' instead", tokens.position());
          }
          return new Node(open, attributes.toArray(new Attribute[attributes.size()]), children.toArray(new Item[children.size()]), close);
        } else {
          TokenizedItem<Token> newOpen = new TokenizedItem<>(next);
          newOpen.before(testOpen);
          children.add(node(newOpen));
        }
      } else {
        children.add(new Text(next));
      }
    }
  }
}

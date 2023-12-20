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
package org.adamalang.translator.parser;

import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.exceptions.ParseException;
import org.adamalang.translator.parser.token.MajorTokenType;
import org.adamalang.translator.parser.token.MinorTokenType;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.definitions.*;
import org.adamalang.translator.tree.definitions.config.DefineDocumentEvent;
import org.adamalang.translator.tree.definitions.config.DocumentConfig;
import org.adamalang.translator.tree.definitions.config.StaticPiece;
import org.adamalang.translator.tree.definitions.web.Uri;
import org.adamalang.translator.tree.expressions.*;
import org.adamalang.translator.tree.expressions.constants.*;
import org.adamalang.translator.tree.expressions.linq.*;
import org.adamalang.translator.tree.expressions.operators.*;
import org.adamalang.translator.tree.expressions.testing.EnvLookupName;
import org.adamalang.translator.tree.expressions.testing.EnvStatus;
import org.adamalang.translator.tree.privacy.*;
import org.adamalang.translator.tree.statements.*;
import org.adamalang.translator.tree.statements.control.*;
import org.adamalang.translator.tree.statements.loops.DoWhile;
import org.adamalang.translator.tree.statements.loops.For;
import org.adamalang.translator.tree.statements.loops.ForEach;
import org.adamalang.translator.tree.statements.loops.While;
import org.adamalang.translator.tree.statements.testing.AssertTruth;
import org.adamalang.translator.tree.statements.testing.Force;
import org.adamalang.translator.tree.statements.testing.Forward;
import org.adamalang.translator.tree.statements.testing.PumpMessage;
import org.adamalang.translator.tree.types.TyTablePtr;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeAnnotation;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
import org.adamalang.translator.tree.types.reactive.*;
import org.adamalang.translator.tree.types.shared.EnumStorage;
import org.adamalang.translator.tree.types.structures.*;
import org.adamalang.translator.tree.types.traits.CanBeNativeArray;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * recursive descent parser for the Adama language; this contains all the rules which tear down a
 * token stream (via TokenEngine) against a TopLevelDocumentHandler
 */
public class Parser {
  private final TokenEngine tokens;
  private final Scope rootScope;

  public Parser(final TokenEngine tokens, Scope rootScope) {
    this.tokens = tokens;
    this.rootScope = rootScope;
  }

  public Expression additive(Scope scope) throws AdamaLangException {
    var result = multiplicative(scope);
    var op = forwardScanMathOp("+", "-");
    while (op != null) {
      result = new BinaryExpression(result, op, multiplicative(scope));
      op = forwardScanMathOp("+", "-");
    }
    return result;
  }

  public ArrayList<FunctionArg> arg_list() throws AdamaLangException {
    final var list = new ArrayList<FunctionArg>();
    final var peekCheck = tokens.peek();
    if (peekCheck == null) {
      return list;
    }
    if (peekCheck.isSymbolWithTextEq(")")) {
      return list;
    }
    var argType = native_type(true);
    var name = id();
    list.add(new FunctionArg(null, argType, name));
    while (true) {
      final var next = tokens.popIf(t -> t.isSymbolWithTextEq(","));
      if (next == null) {
        return list;
      }
      argType = native_type(true);
      name = id();
      list.add(new FunctionArg(next, argType, name));
    }
  }

  public Expression atomic(Scope scope) throws AdamaLangException {
    var token = tokens.pop();
    if (token == null) {
      throw new ParseException("Parser was expecting an atomic expression, but got end of stream instead.", tokens.getLastTokenIfAvailable());
    }
    if (token.isSymbolWithTextEq(".")) {
      final var newTail = tokens.pop();
      if (newTail != null) {
        if (newTail.minorType != MinorTokenType.NumberIsInteger) {
          throw new ParseException("Parser was expecting an atomic expression, but got end of stream instead.", tokens.getLastTokenIfAvailable());
        }
        token = Token.mergeAdjacentTokens(token, newTail, newTail.majorType, MinorTokenType.NumberIsDouble);
      }
    }
    if (token.isLabel()) {
      return new StateMachineConstant(token);
    }
    if (token.isIdentifier() || token.isKeyword()) {
      switch (token.text) {
        case "true":
          return new BooleanConstant(token, true);
        case "false":
          return new BooleanConstant(token, false);
        case "@no_one":
          return new NoOneClientConstant(token);
        case "@who":
          return new WhoClientConstant(token);
        case "@self":
          return new SelfConstant(token);
        case "@context":
          return new ContextConstant(token);
        case "@headers":
          return new HeadersConstant(token);
        case "@parameters":
          return new ParametersConstant(token);
        case "@viewer":
          return new ViewerConstant(token);
        case "@i":
          return new ComplexConstant(0.0, 1.0, token);
        case "@datetime":
          Token literal = tokens.pop();
          try {
            return new DateTimeConstant(ZonedDateTime.parse(literal.text.substring(1, literal.text.length() - 1)), token, literal);
          } catch (Exception ex) {
            throw new ParseException("Failed to parse datetime: " + literal.text, literal);
          }
        case "@date":
        {
          Token year = consumeInteger();
          Token slash1 = consumeExpectedSymbol("/");
          Token month = consumeInteger();
          Token slash2 = consumeExpectedSymbol("/");
          Token day = consumeInteger();
          return new DateConstant(intval(year), intval(month), intval(day), token, year, slash1, month, slash2, day);
        }
        case "@time":
        {
          Token hour = consumeInteger();
          Token colon = consumeExpectedSymbol(":");
          Token minute = consumeInteger();
          return new TimeConstant(intval(hour), intval(minute), token, hour, colon, minute);
        }
        case "@timespan":
          Token quantity = tokens.pop();
          double quantityVal = Double.parseDouble(quantity.text);
          Token unit = tokens.pop();
          switch (unit.text) {
            case "sec":
            case "s":
              return new TimeSpanConstant(quantityVal, token, quantity, unit);
            case "min":
            case "m":
              return new TimeSpanConstant(quantityVal * 60.0, token, quantity, unit);
            case "hr":
            case "h":
              return new TimeSpanConstant(quantityVal * 60.0 * 60.0, token, quantity, unit);
            case "day":
            case "d":
              return new TimeSpanConstant(quantityVal * 60.0 * 60.0 * 24.0, token, quantity, unit);
            case "week":
            case "w":
              return new TimeSpanConstant(quantityVal * 60.0 * 60.0 * 24.0 * 7, token, quantity, unit);
            default:
              throw new ParseException("unknown unit:" + unit.text, unit);
          }
        case "@nothing":
          return new NothingAssetConstant(token);
        case "@null":
          return new DynamicNullConstant(token);
        case "@stable":
          return new EnvStatus(token, EnvLookupName.Stable);
        case "@pair": {
          final var keyExpr = expression(scope);
          final var arrow = consumeArrow("@pair was expected an arrow to bond a key to a value");
          final var valExpr = expression(scope);
          return new PairCons(token, keyExpr, arrow, valExpr);
        }
        case "@lambda": {
          final var varName = id();
          final var colon = consumeExpectedSymbol(":");
          final var resultExpr = expression(scope.makeLambdaScope());
          return new Lambda(token, varName, colon, resultExpr);
        }
        case "@maybe":
          var openExpr = tokens.peek();
          if (openExpr == null) {
            throw new ParseException("Parser was expected either a '<' or '(' after @maybe, but got end of stream instead.", token);
          }
          if (openExpr.isSymbolWithTextEq("(")) {
            openExpr = tokens.pop();
            final var expr = expression(scope);
            final var closeExpr = consumeExpectedSymbol(")");
            return new MaybeLift(token, null, openExpr, expr, closeExpr);
          } else if (openExpr.isSymbolWithTextEq("<")) {
            return new MaybeLift(token, native_parameter_type(), null, null, null);
          } else {
            throw new ParseException(String.format("Parser was expected either a '<' or '(' after @maybe, but got `%s` instead.", openExpr), openExpr);
          }
        case "@blocked":
          return new EnvStatus(token, EnvLookupName.Blocked);
        case "@convert": {
          final var openType = consumeExpectedSymbol("<");
          final var toType = tokens.pop();
          final var closeType = consumeExpectedSymbol(">");
          final var openParen = consumeExpectedSymbol("(");
          final var toConvert = expression(scope);
          final var closeParen = consumeExpectedSymbol(")");
          return new ConvertMessage(token, openType, toType, closeType, openParen, toConvert, closeParen);
        }
      }
      // it may be a variable, cool
      final var doubleColon = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("::"));
      if (doubleColon != null) {
        final var value = tokens.pop();
        if (value == null) {
          throw new ParseException("Parser was expecting either an identifier or an `*` after `::`, but got end of stream instead.", doubleColon);
        } else if (value.isSymbolWithTextEq("*")) {
          return new EnumValuesArray(token, doubleColon, null, value);
        } else if (value.isIdentifier()) {
          final var valueMaybeTisPrefix = tokens.peek();
          if (valueMaybeTisPrefix.isSymbolWithTextEq("*")) {
            return new EnumValuesArray(token, doubleColon, value, tokens.pop());
          } else {
            return new EnumConstant(token, doubleColon, value);
          }
        } else {
          throw new ParseException(String.format("Parser was expecting either an identifier or an `*` after `::`, but got `%s` instead.", value.text), doubleColon);
        }
      } else {
        return new Lookup(token);
      }
    } else if (token.isNumberLiteralDouble()) {
      try {
        return new DoubleConstant(token, Double.parseDouble(token.text));
      } catch (final NumberFormatException nfe) {
        throw new ParseException("Parser was unable to parse `" + token.text + "` as a double.", token);
      }
    } else if (token.isNumberLiteralInteger()) {
      if (token.text.endsWith("L")) {
        try {
          return new LongConstant(token, Long.parseLong(token.text.substring(0, token.text.length() - 1)));
        } catch (final NumberFormatException nfe) {
          throw new ParseException("Parser was expecting a valid integral sequence, but got a '" + token.text + "' instead.", token);
        }
      } else {
        return new IntegerConstant(token, intval(token));
      }
    } else if (token.isStringLiteral()) {
      return new StringConstant(token);
    } else if (token.isSymbolWithTextEq("{")) {
      final var ao = new AnonymousObject(token);
      var current = tokens.pop();
      Token commaToken = null;
      while (current != null && !current.isSymbolWithTextEq("}")) {
        if (current.isIdentifier()) {
          var colon = tokens.peek();
          if (colon.isSymbolWithTextEq(":")) {
            colon = tokens.pop();
            ao.add(commaToken, current, colon, expression(scope));
          } else {
            ao.add(commaToken, current, null, new Lookup(current));
          }
        }
        current = tokens.pop();
        if (current == null) {
          throw new ParseException("Parser expected a `}` or `,`, but instead got end of stream.", tokens.getLastTokenIfAvailable());
        } else if (current.isSymbolWithTextEq(",")) {
          commaToken = current;
          current = tokens.pop();
        } else if (!current.isSymbolWithTextEq("}")) {
          throw new ParseException("Parser expected a `}` or `,`, but instead got `" + current.text + "`", current);
        }
      }
      if (current == null) {
        throw new ParseException("Parser expected a `}`, but instead got end of stream.", tokens.getLastTokenIfAvailable());
      } else {
        ao.end(current);
      }
      return ao;
    } else if (token.isSymbolWithTextEq("[")) {
      final var aa = new AnonymousArray(token);
      var current = tokens.peek();
      if (current != null && current.isSymbolWithTextEq("]")) {
        aa.end(tokens.pop());
        return aa;
      }
      TokenizedItem<Expression> prior = null;
      while (current != null && !current.isSymbolWithTextEq("]")) {
        if (current.isSymbolWithTextEq(",") && prior != null) {
          prior.after(current);
        }
        final var item = new TokenizedItem<>(expression(scope));
        aa.add(item);
        prior = item;
        current = tokens.popIf(t -> t.isSymbolWithTextEq(",", "]"));
      }
      if (current == null) {
        throw new ParseException("Parser expected a `]`, but instead got end of stream.", token);
      } else {
        aa.end(current);
      }
      return aa;
    } else if (token.isSymbolWithTextEq("(")) {
      final var expr = expression(scope);
      final var endParenMaybe = tokens.pop();
      if (endParenMaybe == null) {
        throw new ParseException("Parser expected a ), but instead got end of stream.", token);
      }
      if (endParenMaybe.isSymbolWithTextEq(",")) {
        Token lastNotNullToken = endParenMaybe;
        Token tokenToCheck = endParenMaybe;
        AnonymousTuple anonymousTuple = new AnonymousTuple();
        anonymousTuple.add(token, expr);
        while (tokenToCheck != null && tokenToCheck.isSymbolWithTextEq(",")) {
          lastNotNullToken = tokenToCheck;
          anonymousTuple.add(tokenToCheck, expression(scope));
          tokenToCheck = tokens.pop();
        }
        if (tokenToCheck == null) {
          throw new ParseException("Parser expected a ',' or ')', but instead got end of stream.", lastNotNullToken);
        } else if (tokenToCheck.isSymbolWithTextEq(")")) {
          anonymousTuple.finish(tokenToCheck);
        } else {
          throw new ParseException("Parser expected a ), but instead got an `" + tokenToCheck + "`", tokenToCheck);
        }
        return anonymousTuple;
      } else if (endParenMaybe.isSymbolWithTextEq(")")) {
        return new Parentheses(token, expr, endParenMaybe);
      } else {
        throw new ParseException("Parser expected a ), but instead got an `" + endParenMaybe + "`", endParenMaybe);
      }
    }
    throw new ParseException(String.format("Parser expected an atomic, but instead got `%s`", token.text), token);
  }

  public void blackhole_commas(final Token toAffix) throws AdamaLangException {
    var removeComma = tokens.popIf(t -> t.isSymbolWithTextEq(","));
    while (removeComma != null) {
      if (toAffix.nonSemanticTokensAfter == null) {
        toAffix.nonSemanticTokensAfter = new ArrayList<>();
      }
      toAffix.nonSemanticTokensAfter.add(removeComma);
      removeComma = tokens.popIf(t -> t.isSymbolWithTextEq(","));
    }
  }

  public Block block(Scope scope) throws AdamaLangException {
    final var openBrace = tokens.popIf(t -> t.isSymbolWithTextEq("{"));
    final var b = new Block(openBrace);
    if (openBrace != null) {
      var next = tokens.popIf(t -> t.isSymbolWithTextEq("}"));
      while (next == null) {
        b.add(statement(scope));
        next = tokens.popIf(t -> t.isSymbolWithTextEq("}"));
      }
      b.end(next);
    } else {
      b.add(statement(scope));
    }
    return b;
  }

  private Token consumeExpectedIdentifier(final String ident) throws AdamaLangException {
    final var token = tokens.pop();
    if (token != null && token.isIdentifier(ident)) {
      return token;
    }
    if (token == null) {
      throw new ParseException(String.format("Parser was expecting `%s`, but got an end of the stream instead.", ident), tokens.getLastTokenIfAvailable());
    } else {
      throw new ParseException(String.format("Parser was expecting `%s`, but got `%s` instead.", ident, token.text), token);
    }
  }

  private Token consumeExpectedKeyword(final String keyword) throws AdamaLangException {
    final var token = tokens.pop();
    if (token != null && token.isKeyword(keyword)) {
      return token;
    }
    if (token == null) {
      throw new ParseException(String.format("Parser was expecting `%s`, but got an end of the stream instead.", keyword), tokens.getLastTokenIfAvailable());
    } else {
      throw new ParseException(String.format("Parser was expecting keyword:`%s`, but got `%s` instead.", keyword, token.text), token);
    }
  }

  private Token consumeArrow(String errorIfNotPresent) throws AdamaLangException {
    Token arrow = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("->"));
    if (arrow == null && errorIfNotPresent != null) {
      throw new ParseException("Parser was an expecting an '->'; " + errorIfNotPresent, tokens.getLastTokenIfAvailable());
    }
    return arrow;
  }

  private Token consumeInteger() throws AdamaLangException {
    final var token = tokens.pop();
    if (token == null) {
      throw new ParseException("Parser was expecting an integer; got end of stream", tokens.getLastTokenIfAvailable());
    }
    try {
      Integer.parseInt(token.text);
      return token;
    } catch (NumberFormatException nfe) {
      throw new ParseException("Parser was expecting an integer; got '" + token.text + "'instead", token);
    }
  }

  private Token consumeExpectedSymbol(final String... symbols) throws AdamaLangException {
    final var token = tokens.pop();
    if (token != null && token.isSymbolWithTextEq(symbols)) {
      return token;
    }
    final var sb = new StringBuilder();
    if (symbols.length == 1) {
      sb.append("Parser was expecting a Symbol=`").append(symbols[0]).append("`");
    } else {
      sb.append("Parser was expecting one of the following symbols:");
      sb.append(String.join(", ", symbols));
    }
    if (token == null) {
      sb.append("; instead, the parser got an end of stream.");
      throw new ParseException(sb.toString(), tokens.getLastTokenIfAvailable());
    } else {
      sb.append("; instead, the parse got ");
      sb.append(token.majorType);
      sb.append(":").append(token.text);
      sb.append(".");
      throw new ParseException(sb.toString(), token);
    }
  }

  public Statement declare_native_or_assign_or_eval(Scope scope, final boolean secondPartOfForLoop) throws AdamaLangException {
    if (!secondPartOfForLoop) {
      if (test_native_declare()) {
        final var type = native_type(false);
        final var varName = id();
        final var equalToken = tokens.popIf(t -> t.isSymbolWithTextEq("="));
        Expression valueExpr = null;
        if (equalToken != null) {
          valueExpr = expression(scope);
        }
        final var endToken = consumeExpectedSymbol(";");
        final var defineVariable = new DefineVariable(null, varName, type, equalToken, valueExpr, endToken);
        return defineVariable;
      }
    }
    final var leftSide = expression(scope);
    var hasAssignment = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("<-"));
    if (hasAssignment == null) {
      hasAssignment = tokens.popIf(t -> t.isSymbolWithTextEq("="));
    }
    if (hasAssignment != null) {
      final var value = expression(scope);
      Token asToken = null;
      Token ingestionToken = null;
      if (hasAssignment.isSymbolWithTextEq("<-")) {
        asToken = tokens.popIf((t) -> t.isIdentifier("as"));
        if (asToken != null) {
          ingestionToken = tokens.pop();
          if (ingestionToken == null) {
            throw new ParseException("Parser tried to read a identifier, but got end of stream", tokens.getLastTokenIfAvailable());
          }
        }
      }
      final var trailingToken = !secondPartOfForLoop ? consumeExpectedSymbol(";") : null;
      return new Assignment(leftSide, hasAssignment, value, asToken, ingestionToken, trailingToken, secondPartOfForLoop);
    }
    return new Evaluate(leftSide, secondPartOfForLoop, !secondPartOfForLoop ? consumeExpectedSymbol(";") : null);
  }

  public DocumentConfig define_config(Scope scope, final Token nameToken) throws AdamaLangException {
    final var equals = consumeExpectedSymbol("=");
    final var expr = expression(scope);
    final var semicolon = consumeExpectedSymbol(";");
    return new DocumentConfig(nameToken, equals, expr, semicolon);
  }

  public Consumer<TopLevelDocumentHandler> define_static(Token staticToken) throws AdamaLangException {
    Scope staticScope = rootScope.makeStaticScope();
    Token openContext = tokens.popIf((t) -> t.isSymbolWithTextEq("("));
    Token contextName = null;
    Token closeContext = null;
    if (openContext != null) {
      contextName = tokens.pop();
      closeContext = consumeExpectedSymbol(")");
    }
    var open = consumeExpectedSymbol("{");
    blackhole_commas(open);
    ArrayList<StaticPiece> definitions = new ArrayList<>();

    var nextOrClose = tokens.pop();
    while (!nextOrClose.isSymbolWithTextEq("}")) {
      if (!nextOrClose.isIdentifier("create", "invent", "send", "maximum_history", "delete_on_close", "frequency")) {
        throw new ParseException("Parser was expecting a static definition. Candidates are create, invent, send, maximum_history, delete_on_close, frequency", tokens.getLastTokenIfAvailable());
      }
      switch (nextOrClose.text) {
        case "create":
          definitions.add(define_document_event_raw(staticScope, nextOrClose, DocumentEvent.AskCreation));
          break;
        case "invent":
          definitions.add(define_document_event_raw(staticScope, nextOrClose, DocumentEvent.AskInvention));
          break;
        case "send":
          definitions.add(define_document_event_raw(staticScope, nextOrClose, DocumentEvent.AskSendWhileDisconnected));
          break;
        case "maximum_history":
        case "delete_on_close":
        case "frequency":
          definitions.add(define_config(staticScope, nextOrClose));
          break;
      }
      nextOrClose = tokens.pop();
      if (nextOrClose == null) {
        throw new ParseException("Parser was expecting either a Symbol=} or an Identifier to define a new enum label, but got end of stream instead.", tokens.getLastTokenIfAvailable());
      }
    }
    DefineStatic staticDefn = new DefineStatic(staticToken, openContext, contextName, closeContext, open, definitions, nextOrClose);
    return (handler) -> handler.add(staticDefn);
  }

  public Uri uri() throws AdamaLangException {
    Uri uri = new Uri();
    Token hasMore;
    while ((hasMore = tokens.popIf((t) -> t.isSymbolWithTextEq("/"))) != null) {
      Token isParameter = tokens.popIf((t) -> t.isSymbolWithTextEq("$"));
      if (isParameter != null) {
        Token parameter = typesafe_id();
        Token starToken = tokens.popIf((t) -> t.isSymbolWithTextEq("*"));
        if (starToken != null) {
          uri.push(hasMore, isParameter, parameter, starToken, null, null);
          return uri;
        } else {
          Token colon = consumeExpectedSymbol(":");
          TyType type = native_type_base(false);
          uri.push(hasMore, isParameter, parameter, null, colon, type);
        }
      } else {
        Token uriMore = tokens.popIf((t) -> t.isIdentifier() || t.isStringLiteral());
        uri.push(hasMore, null, uriMore, null, null, null);
      }
    }
    return uri;
  }

  public Consumer<TopLevelDocumentHandler> define_web(Token webToken) throws AdamaLangException {
    Token methodToken = tokens.popIf((t) -> t.isIdentifier("get", "put", "options", "delete"));
    if (methodToken == null) {
      throw new ParseException("Parser was get or put after @web to indicate a read (i.e. get), write (i.e. put) request, or the prelight options for CORS.", tokens.getLastTokenIfAvailable());
    }
    Uri uri = uri();
    if ("put".equals(methodToken.text)) {
      final var open = consumeExpectedSymbol("(");
      final var messageTypeName = id();
      final var messageVariableName = id();
      final var close = consumeExpectedSymbol(")");
      final var body = block(rootScope.makeWebHandler("put"));
      // @web post URI / childPath / $var (messageType messageVar) {
      // }
      DefineWebPut dwp = new DefineWebPut(webToken, methodToken, uri, open, messageTypeName, messageVariableName, close, body);
      return (doc) -> doc.add(dwp);
    } else if ("options".equals(methodToken.text)) {
      final var body = block(rootScope.makeWebHandler("options"));
      // @web options URI / childPath / $var {
      // }
      DefineWebOptions dwo = new DefineWebOptions(webToken, methodToken, uri, body);
      return (doc) -> doc.add(dwo);
    } else if ("delete".equals(methodToken.text)) {
      final var body = block(rootScope.makeWebHandler("delete"));
      // @web delete URI / childPath / $var {
      // }
      DefineWebDelete dwd = new DefineWebDelete(webToken, methodToken, uri, body);
      return (doc) -> doc.add(dwd);
    } else { // GET
      // @web get URI / childPath / $var {
      // }
      final var body = block(rootScope.makeWebHandler("get"));
      DefineWebGet dwg = new DefineWebGet(webToken, methodToken, uri, body);
      return (doc) -> doc.add(dwg);
    }
  }

  public Token[] cron_schedule() throws AdamaLangException {
    ArrayList<Token> schedule = new ArrayList<>();
    Token head = id();
    schedule.add(head);
    switch (head.text) {
      case "daily":
        schedule.add(consumeInteger());
        schedule.add(consumeExpectedSymbol(":"));
        schedule.add(consumeInteger());
        break;
    }
    return schedule.toArray(new Token[schedule.size()]);
  }

  public Consumer<TopLevelDocumentHandler> define_cron(Token cronToken) throws AdamaLangException {
    Token name = id();
    Token[] schedule = cron_schedule();
    DefineCronTask dct = new DefineCronTask(cronToken, name, schedule, block(rootScope.makeCronTask()));
    return (doc) -> doc.add(dct);
  }

  public Consumer<TopLevelDocumentHandler> execute_import(Token includeToken) throws AdamaLangException {
    ArrayList<Token> resource = new ArrayList<>();
    resource.add(typesafe_id());
    Token path = tokens.popIf((t) -> t.isSymbolWithTextEq("/"));
    while (path != null) {
      resource.add(path);
      resource.add(typesafe_id());
      path = tokens.popIf((t) -> t.isSymbolWithTextEq("/"));
    }

    Token semicolon = consumeExpectedSymbol(";");
    Include include = new Include(includeToken, resource.toArray(new Token[resource.size()]), semicolon);
    return (doc) -> doc.add(include, rootScope);
  }

  public Consumer<TopLevelDocumentHandler> execute_link(Token linkToken) throws AdamaLangException {
    Token what = typesafe_id();
    Token open = consumeExpectedSymbol("{");
    ArrayList<Consumer<Consumer<Token>>> emissions = new ArrayList<>();
    ArrayList<DefineService.ServiceAspect> aspects = new ArrayList<>();
    var nextOrClose = tokens.pop();
    while (!nextOrClose.isSymbolWithTextEq("}")) {
      Token equals = consumeExpectedSymbol("=");
      Expression val = expression(rootScope.makeLinkScope());
      Token semicolon = consumeExpectedSymbol(";");
      DefineService.ServiceAspect aspect = new DefineService.ServiceAspect(nextOrClose, equals, val, semicolon);
      emissions.add((y) -> aspect.emit(y));
      aspects.add(aspect);
      nextOrClose = tokens.pop();
    }
    LinkService link = new LinkService(linkToken, what, open, emissions, aspects, nextOrClose);
    return (doc) -> doc.add(link, rootScope);
  }

  public Consumer<TopLevelDocumentHandler> define_service(Token serviceToken) throws AdamaLangException {
    Token name = id();
    Token open = consumeExpectedSymbol("{");
    ArrayList<DefineService.ServiceAspect> aspects = new ArrayList<>();
    ArrayList<DefineService.ServiceMethod> methods = new ArrayList<>();
    ArrayList<DefineService.ServiceReplication> replications = new ArrayList<>();
    ArrayList<Consumer<Consumer<Token>>> emissions = new ArrayList<>();
    ArrayList<Consumer<Formatter>> formatting = new ArrayList<>();
    var nextOrClose = tokens.pop();
    while (!nextOrClose.isSymbolWithTextEq("}")) {
      if (!nextOrClose.isIdentifier()) {
        throw new ParseException("Service was expecting an identifier", tokens.getLastTokenIfAvailable());
      }

      if (nextOrClose.isIdentifier("replication")) {
        Token pairOpen = consumeExpectedSymbol("<");
        Token inputTypeName = typesafe_id();
        Token pairClose = consumeExpectedSymbol(">");
        Token methodName = id();
        Token semicolon = consumeExpectedSymbol(";");
        DefineService.ServiceReplication replication = new DefineService.ServiceReplication(nextOrClose, pairOpen, inputTypeName, pairClose, methodName, semicolon);
        replications.add(replication);
        emissions.add((y) -> replication.emit(y));
        formatting.add((y) -> replication.format(y));

      } else if (nextOrClose.isIdentifier("method")) {
        Token secured = tokens.popIf((t) -> t.isIdentifier("secured"));
        Token pairOpen = consumeExpectedSymbol("<");
        Token inputTypeName = typesafe_id();
        Token comma = consumeExpectedSymbol(",");
        Token outputTypeName = typesafe_id();
        Token outputArrayExt = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("[]"));
        Token pairClose = consumeExpectedSymbol(">");
        Token methodName = id();
        Token semicolon = consumeExpectedSymbol(";");
        DefineService.ServiceMethod method = new DefineService.ServiceMethod(nextOrClose, secured, pairOpen, inputTypeName, comma, outputTypeName, outputArrayExt, pairClose, methodName, semicolon);
        methods.add(method);
        emissions.add((y) -> method.emit(y));
        formatting.add((y) -> method.format(y));
      } else {
        Token equals = consumeExpectedSymbol("=");
        Expression val = expression(rootScope.makeServiceScope());
        Token semicolon = consumeExpectedSymbol(";");
        DefineService.ServiceAspect aspect = new DefineService.ServiceAspect(nextOrClose, equals, val, semicolon);
        emissions.add((y) -> aspect.emit(y));
        formatting.add((y) -> aspect.format(y));
        aspects.add(aspect);
      }
      nextOrClose = tokens.pop();
    }
    DefineService ds = new DefineService(serviceToken, name, open, aspects, methods, replications, nextOrClose, emissions, formatting);
    return (doc) -> doc.add(ds);
  }

  public Consumer<TopLevelDocumentHandler> define() throws AdamaLangException {
    // define a state machine transition
    var op = tokens.popIf(Token::isLabel);
    if (op != null) {
      final var dst = new DefineStateTransition(op, block(rootScope.makeStateMachineTransition()));
      return doc -> doc.add(dst);
    }
    op = tokens.popIf(t -> t.isKeyword("enum", "@construct", "@connected", "@authorization", "@authorize", "@password", "@disconnected", "@delete", "@attached", "@static", "@can_attach", "@web", "@include", "@import", "@link", "@load", "@cron"));
    if (op == null) {
      op = tokens.popIf(t -> t.isIdentifier("record", "message", "channel", "rpc", "function", "procedure", "test", "import", "view", "policy", "bubble", "dispatch", "service", "replication", "metric", "assoc", "join", "template"));
    }
    if (op != null) {
      switch (op.text) {
        case "@import":
        case "@include":
          return execute_import(op);
        case "@link":
          return execute_link(op);
        case "@authorize":
          return define_authorize(op);
        case "@authorization":
          return define_authorization(op);
        case "@password":
          return define_password(op);
        case "enum":
          return define_enum_trailer(op);
        case "dispatch":
          return define_dispatch(op);
        case "record":
          return define_record_trailer(op);
        case "message":
          return define_message_trailer(op);
        case "channel":
          return define_handler_trailer(op);
        case "rpc":
          return define_rpc(op);
        case "function":
          return define_function_trailer(op);
        case "procedure":
          return define_procedure_trailer(op);
        case "test":
          return define_test_trailer(op);
        case "@construct":
          return define_constructor_trailer(op);
        case "@load":
          return define_document_event(op, DocumentEvent.Load);
        case "@connected":
          return define_document_event(op, DocumentEvent.ClientConnected);
        case "@delete":
          return define_document_event(op, DocumentEvent.Delete);
        case "@disconnected":
          return define_document_event(op, DocumentEvent.ClientDisconnected);
        case "@attached":
          return define_document_event(op, DocumentEvent.AssetAttachment);
        case "@can_attach":
          return define_document_event(op, DocumentEvent.AskAssetAttachment);
        case "@static":
          return define_static(op);
        case "@web":
          return define_web(op);
        case "@cron":
          return define_cron(op);
        case "service":
          return define_service(op);
        case "view": {
          final var ntype = native_type(false);
          final var name = id();
          final var semicolon = consumeExpectedSymbol(";");
          AugmentViewerState avs = new AugmentViewerState(op, ntype, name, semicolon);
          return doc -> doc.add(avs);
        }
        case "bubble":
          final var bubble = define_bubble(rootScope.makeBubble(), op);
          return doc -> doc.add(bubble);
        case "policy":
          final var policy = define_policy_trailer(rootScope.makePolicy(), op);
          return doc -> doc.add(policy);
        case "replication":
          final var replicate = define_replication(rootScope.makeReplication(), op);
          return doc -> doc.add(replicate);
        case "assoc":
          final var assoc = define_assoc(op);
          return doc -> doc.add(assoc);
        case "join":
          final var join = join_assoc(rootScope, op);
          return doc -> doc.add(join);
        case "metric":
          final var metric = define_metric(rootScope, op);
          return doc -> doc.add(metric);
        case "template":
          final var template = define_template(op);
          return doc -> doc.add(template);
      }
    }
    final var newField = define_field_record(rootScope);
    return doc -> doc.add(newField);
  }

  public JoinAssoc join_assoc(Scope scope, Token join) throws AdamaLangException {
    Token assoc = id();
    Token via = consumeExpectedIdentifier("via");
    Token tableName = id();
    Token brackOpen = consumeExpectedSymbol("[");
    Token itemVar = id();
    Token brackClose = consumeExpectedSymbol("]");
    Token fromLabel  = consumeExpectedIdentifier("from");

    /*
    Token brackOpenF = consumeExpectedSymbol("[");
    Token fromTable = id();
    Token brackCloseF = consumeExpectedSymbol("]");
    */

    Expression fromExpr = expression(scope);
    Token toLabel  = consumeExpectedIdentifier("to");

    /*
    Token brackOpenT = consumeExpectedSymbol("[");
    Token toTable = id();
    Token brackCloseT = consumeExpectedSymbol("]");
    */

    Expression toExpr = expression(scope);
    Token semicolon = consumeExpectedSymbol(";");
    return new JoinAssoc(join, assoc, via, tableName, brackOpen, itemVar, brackClose, fromLabel, fromExpr, toLabel, toExpr, semicolon);
  }

  public DefineTemplate define_template(Token templateToken) throws AdamaLangException {
    Token name = id();
    Token colon = consumeExpectedSymbol(":");
    Token template = tokens.pop();
    if (template == null) {
      throw new ParseException(String.format("Expected a template, but got end of stream instead."), colon);
    } else if (!template.isTemplate()) {
      throw new ParseException(String.format("Expected a template, but got %s instead", template.majorType.toString()), colon);
    }
    return new DefineTemplate(templateToken, name, colon, new TemplateConstant(template));
  }

  public DefineAssoc define_assoc(Token op) throws AdamaLangException {
    Token open = consumeExpectedSymbol("<");
    Token fromType = id();
    Token comma = consumeExpectedSymbol(",");
    Token toType = id();
    Token secondComma = tokens.popIf((t) -> t.isSymbolWithTextEq(","));
    Token edgeType = null;
    if (secondComma != null) {
      edgeType = id();
    }
    Token close = consumeExpectedSymbol(">");
    Token name = id();
    Token semicolon = consumeExpectedSymbol(";");
    return new DefineAssoc(op, open, fromType, comma, toType, secondComma, edgeType, close, name, semicolon);
  }


  public DefineMetric define_metric(Scope scope, Token op) throws AdamaLangException {
    Token name = id();
    Token equals = consumeExpectedSymbol("=");
    Expression value = expression(scope);
    Token semicolon = consumeExpectedSymbol(";");
    return new DefineMetric(op, name, equals, value, semicolon);
  }

  public ReplicationDefinition define_replication(Scope scope, Token op) throws AdamaLangException {
    Token open = consumeExpectedSymbol("<");
    Token service = id();
    Token split = consumeExpectedSymbol(":");
    Token method = id();
    Token close = consumeExpectedSymbol(">");
    Token name = id();
    Token equals = consumeExpectedSymbol("=");
    Expression expression = expression(scope);
    Token end = consumeExpectedSymbol(";");
    return new ReplicationDefinition(op, open, service, split, method, close, name, equals, expression, end);
  }

  public Guard define_guard(Token open) throws AdamaLangException {
    ArrayList<TokenizedItem<String>> policies = new ArrayList<>();
    boolean hasOne = true;
    Token last = null;
    while (hasOne) {
      Token policy = id();
      TokenizedItem<String> item = new TokenizedItem<>(policy.text);
      item.before(policy);
      last = consumeExpectedSymbol(",", ">");
      hasOne = last.text.equals(",");
      if (hasOne) {
        item.after(last);
      }
      policies.add(item);
    }
    return new Guard(open, policies, last);
  }

  public BubbleDefinition define_bubble(Scope scope, final Token bubbleToken) throws AdamaLangException {
    Token guardOpen = tokens.popIf((t) -> t.isSymbolWithTextEq("<"));
    Guard guard = null;
    if (guardOpen != null) {
      guard = define_guard(guardOpen);
    }
    final var nameToken = id();
    final var equalsToken = consumeExpectedSymbol("=");
    final var expression = expression(scope);
    final var semicolonToken = consumeExpectedSymbol(";");
    return new BubbleDefinition(bubbleToken, guard, nameToken, equalsToken, expression, semicolonToken);
  }

  public DefineDocumentEvent define_document_event_raw(final Scope scope, final Token eventToken, final DocumentEvent which) throws AdamaLangException {
    if (which.hasParameter) {
      final var openParen = consumeExpectedSymbol("(");
      final var parameterNameToken = id();
      final var closeParen = consumeExpectedSymbol(")");
      return new DefineDocumentEvent(eventToken, which, openParen, parameterNameToken, closeParen, block(scope.makeDocumentEvent(which)));
    } else {
      return new DefineDocumentEvent(eventToken, which,  null, null, null, block(scope.makeDocumentEvent(which)));
    }
  }

  @Deprecated
  public Consumer<TopLevelDocumentHandler> define_authorize(final Token authorizeToken) throws AdamaLangException {
    Token openParen = consumeExpectedSymbol("(");
    Token username = id();
    Token comma = consumeExpectedSymbol(",");
    Token password = id();
    Token closeParen = consumeExpectedSymbol(")");
    Block code = block(rootScope.makeAuthorize());
    return (doc) -> doc.add(new DefineAuthorization(authorizeToken, openParen, username, comma, password, closeParen, code));
  }

  public Consumer<TopLevelDocumentHandler> define_authorization(final Token authorizeToken) throws AdamaLangException {
    Token openParen = consumeExpectedSymbol("(");
    Token messageType = id();
    Token messageValue = id();
    Token closeParen = consumeExpectedSymbol(")");
    Block code = block(rootScope.makeAuthorize());
    return (doc) -> doc.add(new DefineAuthorizationPipe(authorizeToken, openParen, messageType, messageValue, closeParen, code));
  }

  public Consumer<TopLevelDocumentHandler> define_password(final Token passwordToken) throws AdamaLangException {
    Token openParen = consumeExpectedSymbol("(");
    Token password = id();
    Token closeParen = consumeExpectedSymbol(")");
    Block code = block(rootScope.makePassword());
    return (doc) -> doc.add(new DefinePassword(passwordToken, openParen, password, closeParen, code));
  }

  public Consumer<TopLevelDocumentHandler> define_document_event(final Token eventToken, final DocumentEvent which) throws AdamaLangException {
    final var dce = define_document_event_raw(rootScope.makeDocumentEvent(which), eventToken, which);
    return doc -> doc.add(dce);
  }

  public Consumer<TopLevelDocumentHandler> define_constructor_trailer(final Token constructorToken) throws AdamaLangException {
    final var openParenToken = tokens.popIf(t -> t.isSymbolWithTextEq("("));
    if (openParenToken != null) {
      final var identA = id();
      final var identB = id();
      final var endParenToken = consumeExpectedSymbol(")");
      final var dc = new DefineConstructor(constructorToken, openParenToken, identA, identB, endParenToken, block(rootScope.makeConstructor()));
      return doc -> doc.add(dc);
    } else {
      final var dc = new DefineConstructor(constructorToken,  null, null, null, null, block(rootScope.makeConstructor()));
      return doc -> doc.add(dc);
    }
  }

  public Consumer<TopLevelDocumentHandler> define_dispatch(final Token dispatchToken) throws AdamaLangException {
    final var enumNameToken = id();
    final var doubleColonToken = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("::"));
    if (doubleColonToken == null) {
      final var next = tokens.pop();
      throw new ParseException(String.format("Expected `::`, but got %s instead.", next == null ? "end of stream" : next.text), enumNameToken);
    }
    var valueToken = tokens.peek();
    Token starToken;
    if (valueToken == null) {
      throw new ParseException(String.format("Expected an id or `*`, but got end of stream instead"), doubleColonToken);
    }
    if (valueToken.isSymbolWithTextEq("*")) {
      valueToken = null;
      starToken = tokens.pop();
    } else {
      valueToken = id();
      starToken = tokens.popIf(t -> t.isSymbolWithTextEq("*"));
    }
    final var functionName = id();
    final var openParen = consumeExpectedSymbol("(");
    final var args = arg_list();
    final var closeParen = consumeExpectedSymbol(")");
    final var introReturnType = consumeArrow(null);
    TyType returnType = null;
    if (introReturnType != null) {
      returnType = native_type(true);
    }
    final var code = block(rootScope.makeDispatch());
    final var dd = new DefineDispatcher(dispatchToken, enumNameToken, doubleColonToken, valueToken, starToken, functionName, openParen, args, closeParen, introReturnType, returnType, code);
    return doc -> doc.add(dd);
  }

  public Consumer<TopLevelDocumentHandler> define_enum_trailer(final Token enumToken) throws AdamaLangException {
    var autoId = 0;
    final var enumName = id();
    final var es = new EnumStorage(enumName.text);
    final var openBrace = consumeExpectedSymbol("{");
    blackhole_commas(openBrace);
    var next = tokens.pop();
    while (!next.isSymbolWithTextEq("}")) {
      Token isDefault = null;
      if (next.isKeyword("@default")) {
        isDefault = next;
        next = tokens.pop();
      }
      testId(next);
      var toAffixCommas = next;
      final var name = next;
      final var colon = tokens.popIf(t -> t.isSymbolWithTextEq(":"));
      Token intValue = null;
      if (colon != null) {
        intValue = tokens.pop();
        if (intValue == null) {
          throw new ParseException("Parser was expecting an integer after ':', but got end of stream instead.", colon);
        }
        toAffixCommas = intValue;
        autoId = intval(intValue);
      }
      blackhole_commas(toAffixCommas);
      es.add(isDefault, name, colon, intValue, autoId);
      autoId++;
      next = tokens.pop();
      if (next == null) {
        throw new ParseException("Parser was expecting either a Symbol=} or an Identifier to define a new enum label, but got end of stream instead.", tokens.getLastTokenIfAvailable());
      }
    }
    final var endBrace = next;
    return doc -> doc.add(new TyNativeEnum(TypeBehavior.ReadWriteNative, enumToken, enumName, openBrace, es, endBrace));
  }

  public FieldDefinition define_field_record(Scope scope) throws AdamaLangException {
    final var policy = field_privacy();
    final var isAuto = tokens.popIf(t -> t.isIdentifier("auto", "formula"));
    if (isAuto != null) {
      final var id = id();
      final var equalsToken = consumeExpectedSymbol("=");
      final var compute = expression(scope);
      return new FieldDefinition(policy, isAuto, null, id, equalsToken, compute, null, null, null, consumeExpectedSymbol(";"));
    } else {
      // we allow a superfluous privacy policy as that is the same as absent
      final var readonly = tokens.popIf((t) -> t.isIdentifier("readonly"));
      final var type = reactive_type(readonly != null, policy instanceof PrivatePolicy ? false : (policy != null));
      final var id = id();
      final var equalsToken = tokens.popIf(t -> t.isSymbolWithTextEq("="));
      Expression defaultValue = null;
      if (equalsToken != null) {
        defaultValue = expression(scope.makeConstant());
      }
      Token required = tokens.popIf(t -> t.isIdentifier("required"));
      return new FieldDefinition(policy, readonly, type, id, equalsToken, null, defaultValue, required, null, consumeExpectedSymbol(";"));
    }
  }

  public Consumer<TopLevelDocumentHandler> define_function_trailer(final Token functionToken) throws AdamaLangException {
    final var name = id();
    final var openParen = consumeExpectedSymbol("(");
    final var args = arg_list();
    final var closeParen = consumeExpectedSymbol(")");
    final var introReturn = consumeArrow("pure functions must have return types.");
    final var returnType = native_type(true);
    FunctionPaint paint = new FunctionPaint(true, false, false, false);
    final var code = block(rootScope.makeFunction(paint));
    final var df = new DefineFunction(functionToken, FunctionSpecialization.Pure, name, openParen, args, closeParen, introReturn, returnType, paint, code);
    return doc -> doc.add(df);
  }

  public Consumer<TopLevelDocumentHandler> define_handler_trailer(final Token channelToken) throws AdamaLangException {
    final var maybeFutureStyle = tokens.peek();
    if (maybeFutureStyle != null && maybeFutureStyle.isSymbolWithTextEq("<")) {
      final var openType = consumeExpectedSymbol("<");
      final var typeName = id();
      final var arrayExt = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("[]"));
      final var endType = consumeExpectedSymbol(">");
      final var name = id();
      final var semicolon = consumeExpectedSymbol(";");
      final var handler = new DefineHandler(channelToken, name);
      handler.setFuture(openType, typeName, arrayExt, endType, semicolon);
      return doc -> doc.add(handler);
    }
    final var name = id();
    final var handler = new DefineHandler(channelToken, name);
    final var openParen = consumeExpectedSymbol("(");
    final var nextType = tokens.pop();
    if (nextType == null) {
      throw new ParseException("Parser expected a type, but instead got end of stream", openParen);
    }
    final var messageType = nextType;
    testId(messageType);
    final var arrayToken = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("[]"));
    final var messageVarToken = id();
    final var endParen = consumeExpectedSymbol(")");
    final var isOpen = tokens.popIf((t) -> t.isIdentifier("open"));
    final var requires = tokens.popIf((t) -> t.isIdentifier("requires"));
    if (requires != null) {
      Token openGuard = consumeExpectedSymbol("<");
      handler.setGuard(requires, define_guard(openGuard));
    }
    handler.setMessageOnlyHandler(openParen, messageType, arrayToken, messageVarToken, endParen, isOpen, block(rootScope.makeMessageHandler()));
    return doc -> doc.add(handler);
  }

  public Consumer<TopLevelDocumentHandler> define_rpc(final Token rpcToken) throws AdamaLangException {
    final var name = id();
    final var openParen = consumeExpectedSymbol("(");
    final var clientVar = id();
    Token comma;
    ArrayList<FunctionArg> args = new ArrayList<>();
    while ((comma = tokens.popIf((t) -> t.isSymbolWithTextEq(","))) != null) {
      final var paramTyType = native_type(false);
      args.add(new FunctionArg(comma, paramTyType, id()));
    }
    final var closeParen = consumeExpectedSymbol(")");
    final var code = block(rootScope.makeMessageHandler());
    DefineRPC rpc = new DefineRPC(rpcToken, name, openParen, clientVar, args, closeParen, code);
    return (doc) -> {
      doc.add(rpc);
    };
  }

  public IndexDefinition define_indexing(final Token indexToken) throws AdamaLangException {
    final var name = id();
    final var semicolon = consumeExpectedSymbol(";");
    return new IndexDefinition(indexToken, name, semicolon);
  }

  public Consumer<TopLevelDocumentHandler> define_message_trailer(final Token messageToken) throws AdamaLangException {
    Scope scope = rootScope.makeMessageType();
    PublicPolicy policy = new PublicPolicy(null);
    policy.ingest(messageToken);
    final var name = id();
    final var storage = new StructureStorage(name, StorageSpecialization.Message, false, false, consumeExpectedSymbol("{"));
    storage.setSelf(new TyNativeRef(TypeBehavior.ReadOnlyNativeValue, null, name));
    var endBrace = tokens.popIf(t -> t.isSymbolWithTextEq("}"));
    while (endBrace == null) {
      Token indexToken = tokens.popIf((t) -> t.isIdentifier("index"));
      if (indexToken != null) {
        storage.add(define_indexing(indexToken));
      } else {
        Token methodToken = tokens.popIf((t) -> t.isIdentifier("method"));
        if (methodToken != null) {
          storage.add(define_method_trailer(scope, methodToken, storage));
        } else {
          final var type = native_type(false);
          final var field = id();
          final var equalsToken = tokens.popIf(t -> t.isSymbolWithTextEq("="));
          Expression defaultValueOverride = null;
          if (equalsToken != null) {
            defaultValueOverride = expression(rootScope.makeConstant());
          }
          Token lossy = tokens.popIf((t) -> t.isIdentifier("lossy"));
          Token unique = null;
          if ("id".equals(field.text)) {
            unique = tokens.popIf((t) -> t.isIdentifier("unique"));
          }
          Token end = consumeExpectedSymbol(";");
          FieldDefinition fd = new FieldDefinition(policy, null, type, field, equalsToken, null, defaultValueOverride, lossy, unique, end);
          storage.add(fd);
        }
      }
      endBrace = tokens.popIf(t -> t.isSymbolWithTextEq("}"));
    }
    storage.end(endBrace);
    TyNativeMessage tyMsg = enrich(new TyNativeMessage(TypeBehavior.ReadOnlyNativeValue, messageToken, name, storage));

    return doc -> doc.add(tyMsg);
  }

  public FunctionPaint painting() throws AdamaLangException {
    ArrayList<Token> arr = new ArrayList<>();
    boolean again = true;
    while (again) {
      again = false;
      final var readonlyToken = tokens.popIf(t -> t.isIdentifier("readonly"));
      if (readonlyToken != null) {
        arr.add(readonlyToken);
        again = true;
      }
      final var abortsToken = tokens.popIf(t -> t.isIdentifier("aborts"));
      if (abortsToken != null) {
        arr.add(abortsToken);
        again = true;
      }
      final var viewerToken = tokens.popIf(t -> t.isIdentifier("viewer"));
      if (viewerToken != null) {
        arr.add(viewerToken);
        again = true;
      }
    }
    return new FunctionPaint(arr.toArray(new Token[arr.size()]));
  }

  public DefineMethod define_method_trailer(Scope scope, final Token methodToken, StructureStorage storage) throws AdamaLangException {
    final var name = id();
    final var openParen = consumeExpectedSymbol("(");
    final var args = arg_list();
    final var closeParen = consumeExpectedSymbol(")");
    final var hasReturnType = consumeArrow(null);
    TyType returnType = null;
    if (hasReturnType != null) {
      returnType = native_type(true);
    }
    FunctionPaint fp = painting();
    Scope mscope = scope.makeMethod(fp);
    final var code = block(mscope);
    return new DefineMethod(methodToken, name, openParen, args, closeParen, hasReturnType, returnType, fp, code, storage);
  }

  public DefineCustomPolicy define_policy_trailer(Scope scope, final Token definePolicy) throws AdamaLangException {
    final var id = id();
    return new DefineCustomPolicy(definePolicy, id, block(scope));
  }

  public Consumer<TopLevelDocumentHandler> define_procedure_trailer(final Token procedureToken) throws AdamaLangException {
    final var name = id();
    final var openParen = consumeExpectedSymbol("(");
    final var args = arg_list();
    final var closeParen = consumeExpectedSymbol(")");
    final var introReturn = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("->"));
    TyType returnType = null;
    if (introReturn != null) {
      returnType = native_type(true);
    }
    FunctionPaint paint = painting();
    final var code = block(rootScope.makeProcedure(paint));
    final var df = new DefineFunction(procedureToken, FunctionSpecialization.Impure, name, openParen, args, closeParen, introReturn, returnType, paint, code);
    return doc -> doc.add(df);
  }

  public Consumer<TopLevelDocumentHandler> define_record_trailer(final Token recordToken) throws AdamaLangException {
    Scope scope = rootScope.makeRecordType();
    final var name = id();
    final var storage = new StructureStorage(name, StorageSpecialization.Record, false, false, consumeExpectedSymbol("{"));
    storage.setSelf(new TyReactiveRef(false, name));
    while (true) {
      var op = tokens.popIf(t -> t.isIdentifier("require", "policy", "method", "bubble", "index", "join"));
      while (op != null) {
        switch (op.text) {
          case "require": {
            final var policyRequire = id();
            storage.markPolicyForVisibility(op, policyRequire, consumeExpectedSymbol(";"));
            break;
          }
          case "bubble":
            storage.add(define_bubble(scope, op));
            break;
          case "index":
            storage.add(define_indexing(op));
            break;
          case "policy":
            storage.addPolicy(define_policy_trailer(scope, op));
            break;
          case "method":
            storage.add(define_method_trailer(scope, op, storage));
            break;
          case "join":
            storage.add(join_assoc(scope, op));
            break;
        }
        op = tokens.popIf(t -> t.isIdentifier("require", "policy", "method", "bubble", "index", "join"));
      }
      op = tokens.popIf(t -> t.isSymbolWithTextEq("}"));
      if (op != null) {
        storage.finalizeRecord();
        storage.end(op);
        TyReactiveRecord tyRec = enrich(new TyReactiveRecord(recordToken, name, storage));
        return doc -> doc.add(tyRec);
      }
      storage.add(define_field_record(scope));
    }
  }

  public Consumer<TopLevelDocumentHandler> define_test_trailer(final Token testToken) throws AdamaLangException {
    final var name = id();
    final var dt = new DefineTest(testToken, name, block(rootScope.makeTest()));
    return doc -> doc.add(dt);
  }

  public DoWhile do_statement_trailer(Scope scope, final Token doToken) throws AdamaLangException {
    final var code = block(scope);
    final var whileToken = consumeExpectedKeyword("while");
    final var openParen = consumeExpectedSymbol("(");
    final var condition = expression(scope);
    final var endParen = consumeExpectedSymbol(")");
    final var semicolon = consumeExpectedSymbol(";");
    return new DoWhile(doToken, code, whileToken, openParen, condition, endParen, semicolon);
  }

  public Consumer<TopLevelDocumentHandler> document() throws AdamaLangException {
    final var steps = new ArrayList<Consumer<TopLevelDocumentHandler>>();
    pumpsemicolons(steps);
    while (tokens.peek() != null) {
      steps.add(define());
      pumpsemicolons(steps);
    }
    if (tokens.getNonsemanticForwardingTokens() != null) {
      for (final Token token : tokens.getNonsemanticForwardingTokens()) {
        steps.add(doc -> doc.add(token));
      }
    }
    return doc -> {
      for (final Consumer<TopLevelDocumentHandler> step : steps) {
        step.accept(doc);
      }
    };
  }

  public Expression equality(Scope scope) throws AdamaLangException {
    final var left = relate(scope);
    final var op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("==", "!=", "=?"));
    if (op != null) {
      return new BinaryExpression(left, op, relate(scope));
    }
    return left;
  }

  public Expression expression(Scope scope) throws AdamaLangException {
    Expression base;
    final var iterateToken = tokens.popIf(t -> t.isIdentifier("iterate"));
    if (iterateToken != null) {
      base = new Iterate(iterateToken, assignment(scope));
    } else {
      base = assignment(scope);
    }
    Token op;
    while ((op = tokens.popIf(t -> t.isIdentifier("materialize", "where", "where_as", "order", "order_dyn", "shuffle", "map", "reduce", "limit", "offset", "unique"))) != null) {
      base = wrap_linq(scope, base, op);
    }
    return base;
  }

  public Policy field_privacy() throws AdamaLangException {
    final var policy = tokens.popIf(t -> t.isIdentifier("public", "private", "viewer_is", "use_policy"));
    if (policy != null) {
      switch (policy.text) {
        case "public":
          return new PublicPolicy(policy);
        case "private":
          return new PrivatePolicy(policy);
        case "viewer_is": {
          final var open = consumeExpectedSymbol("<");
          final var token = id();
          final var close = consumeExpectedSymbol(">");
          return new ViewerIsPolicy(policy, open, token, close);
        }
        case "use_policy":
          return new UseCustomPolicy(policy, policy_list());
      }
    }
    return null;
  }

  public For for_statement_trailer(Scope scope, final Token forToken) throws AdamaLangException {
    final var openParen = consumeExpectedSymbol("(");
    final var noInitialSemicolon = tokens.popIf(t -> t.isSymbolWithTextEq(";"));
    Statement initial = null;
    if (noInitialSemicolon == null) {
      initial = declare_native_or_assign_or_eval(scope, false);
    }
    Expression condition = null;
    var endConditionSemicolon = tokens.popIf(t -> t.isSymbolWithTextEq(";"));
    if (endConditionSemicolon == null) {
      condition = expression(scope);
      endConditionSemicolon = consumeExpectedSymbol(";");
    }
    final var noAdvance = tokens.peek();
    Statement advance = null;
    if (noAdvance != null && !noAdvance.isSymbolWithTextEq(")")) {
      advance = declare_native_or_assign_or_eval(scope, true);
    }
    final var endParen = consumeExpectedSymbol(")");
    final var code = block(scope);
    return new For(forToken, openParen, initial, noInitialSemicolon, condition, endConditionSemicolon, advance, endParen, code);
  }

  public ForEach foreach_statement_trailer(Scope scope, final Token foreachToken) throws AdamaLangException {
    final var openToken = consumeExpectedSymbol("(");
    final var varName = id();
    final var inToken = consumeExpectedIdentifier("in");
    final var list = expression(scope);
    final var endToken = consumeExpectedSymbol(")");
    final var code = block(scope);
    return new ForEach(foreachToken, openToken, varName, inToken, list, endToken, code);
  }

  private Token forwardScanMathOp(final String... args) throws AdamaLangException {
    final var scan = tokens.peek();
    if (scan == null) {
      return null;
    }
    if (scan.isSymbolWithTextEq(args)) {
      final var afterScan = tokens.peek(1);
      if (afterScan != null && (afterScan.isSymbolWithTextEq("="))) {
        return null;
      }
      if (afterScan != null && (scan.isSymbolWithTextEq("-") && afterScan.isSymbolWithTextEq(">"))) {
        return null;
      }
    }
    return tokens.popIf(t -> t.isSymbolWithTextEq(args));
  }

  private Token forwardScaRelate() throws AdamaLangException {
    final var scan = tokens.peek();
    if (scan == null) {
      return null;
    }
    if (scan.isSymbolWithTextEq("<")) {
      final var afterScan = tokens.peek(1);
      if (afterScan != null && afterScan.isSymbolWithTextEq("-")) {
        return null;
      }
    }
    return tokens.popIf(t -> t.isSymbolWithTextEq("<", ">"));
  }

  public Token id() throws AdamaLangException {
    Token token = testId(tokens.pop());
    if (is_token_native_declare(token)) {
      throw new ParseException("Identifier '" + token.text + "' is reserved", token);
    }
    switch (token.text) {
      case "abstract":
      case "assert":
      case "boolean":
      case "break":
      case "byte":
      case "case":
      case "catch":
      case "char":
      case "class":
      case "const":
      case "continue":
      case "default":
      case "do":
      case "double":
      case "else":
      case "enum":
      case "extends":
      case "final":
      case "finally":
      case "float":
      case "for":
      case "goto":
      case "if":
      case "implements":
      case "import":
      case "instanceof":
      case "int":
      case "interface":
      case "long":
      case "native":
      case "new":
      case "package":
      case "private":
      case "protected":
      case "public":
      case "return":
      case "short":
      case "static":
      case "strictfp":
      case "super":
      case "switch":
      case "synchronized":
      case "this":
      case "throw":
      case "throws":
      case "transient":
      case "try":
      case "void":
      case "volatile":
      case "while":
        throw new ParseException("Identifier '" + token.text + "' is reserved", token);
    }
    return token;
  }

  public Token typesafe_id() throws AdamaLangException {
    return testId(tokens.pop());
  }

  public MegaIf.Condition if_condition(Scope scope) throws AdamaLangException {
    final var openParen = consumeExpectedSymbol("(");
    final var guard = expression(scope);
    final var asToken = tokens.popIf(t -> t.isIdentifier("as"));
    Token nameToken = null;
    if (asToken != null) {
      nameToken = id();
    }
    final var endParen = consumeExpectedSymbol(")");
    return new MegaIf.Condition(openParen, guard, asToken, nameToken, endParen);
  }

  public MegaIf if_statement_trailer(Scope scope, final Token ifToken) throws AdamaLangException {
    final var primary = if_condition(scope);
    final var result = new MegaIf(ifToken, primary, block(scope.makeBranchScope()));
    var elseToken = tokens.popIf(t -> t.isKeyword("else"));
    while (elseToken != null) {
      final var ifToken2 = tokens.popIf(t -> t.isKeyword("if"));
      if (ifToken2 != null) {
        final var branchCond = if_condition(scope);
        result.add(elseToken, ifToken2, branchCond, block(scope.makeBranchScope()));
      } else {
        result.setElse(elseToken, block(scope.makeBranchScope()));
        return result;
      }
      elseToken = tokens.popIf(t -> t.isKeyword("else"));
    }
    return result;
  }

  public int intval(final Token token) throws AdamaLangException {
    if (token.majorType == MajorTokenType.NumberLiteral) {
      try {
        if (token.text.startsWith("0x")) {
          return Integer.parseInt(token.text.substring(2), 16);
        } else {
          return Integer.parseInt(token.text);
        }
      } catch (final NumberFormatException nfe) {
        throw new ParseException("Parser was expecting a valid numeric sequence, but got a '" + token.text + "' instead.", token);
      }
    } else {
      throw new ParseException("Parser was expecting a valid numeric sequence, but got a " + token.majorType + " instead.", token);
    }
  }

  public Expression logic_and(Scope scope) throws AdamaLangException {
    var result = equality(scope);
    var op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("&&"));
    while (op != null) {
      result = new BinaryExpression(result, op, equality(scope));
      op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("&&"));
    }
    return result;
  }

  public Expression logic_or(Scope scope) throws AdamaLangException {
    var result = logic_and(scope);
    var op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("||"));
    while (op != null) {
      result = new BinaryExpression(result, op, logic_and(scope));
      op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("||"));
    }
    return result;
  }

  public Expression logic_xor(Scope scope) throws AdamaLangException {
    var result = logic_or(scope);
    var op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("^^"));
    while (op != null) {
      result = new BinaryExpression(result, op, logic_or(scope));
      op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("^^"));
    }
    return result;
  }

  public Expression multiplicative(Scope scope) throws AdamaLangException {
    var result = prefix(scope);
    var op = forwardScanMathOp("*", "/", "%");
    while (op != null) {
      result = new BinaryExpression(result, op, prefix(scope));
      op = forwardScanMathOp("*", "/", "%");
    }
    return result;
  }

  public TyNativeMap native_map(final TypeBehavior behavior, final Token readonlyToken, final Token mapToken) throws AdamaLangException {
    final var openThing = consumeExpectedSymbol("<");
    final var domainType = native_type(false);
    final var commaToken = consumeExpectedSymbol(",");
    final var rangeType = native_type(false);
    final var closeThing = consumeExpectedSymbol(">");
    return new TyNativeMap(behavior, readonlyToken, mapToken, openThing, domainType, commaToken, rangeType, closeThing);
  }

  public TyNativePair native_pair(final TypeBehavior behavior, final Token readonlyToken, final Token pairToken) throws AdamaLangException {
    final var openThing = consumeExpectedSymbol("<");
    final var domainType = native_type(false);
    final var commaToken = consumeExpectedSymbol(",");
    final var rangeType = native_type(false);
    final var closeThing = consumeExpectedSymbol(">");
    return new TyNativePair(behavior, readonlyToken, pairToken, openThing, domainType, commaToken, rangeType, closeThing);
  }

  public TokenizedItem<TyType> native_parameter_type() throws AdamaLangException {
    final var before = consumeExpectedSymbol("<");
    final var subtype = new TokenizedItem<>(native_type(false));
    final var after = consumeExpectedSymbol(">");
    subtype.before(before);
    subtype.after(after);
    return subtype;
  }

  private <T extends TyType> T enrich(T tyType) throws AdamaLangException {
    Token openAnnotation = tokens.popIf((t) -> t.isSymbolWithTextEq("("));
    if (openAnnotation != null) {
      ArrayList<TokenizedItem<TypeAnnotation.Annotation>> annotations = new ArrayList<>();
      Token commaOrEnd = tokens.popIf((t) -> t.isSymbolWithTextEq(",", ")"));
      while (commaOrEnd == null || commaOrEnd.isSymbolWithTextEq(",")) {
        Token name = typesafe_id();
        Token comma = tokens.popIf((t) -> t.isSymbolWithTextEq("="));
        TokenizedItem<TypeAnnotation.Annotation> annotation;
        if (comma != null) {
          Token value = tokens.pop();
          annotation = new TokenizedItem<>(new TypeAnnotation.Annotation(name, comma, value));
        } else {
          annotation = new TokenizedItem<>(new TypeAnnotation.Annotation(name, null, null));
        }
        if (commaOrEnd != null) {
          annotation.before.add(commaOrEnd);
        }
        annotations.add(annotation);
        commaOrEnd = tokens.popIf((t) -> t.isSymbolWithTextEq(",", ")"));
      }
      tyType.annotate(new TypeAnnotation(openAnnotation, annotations, commaOrEnd));
    }
    return tyType;
  }

  public TyType native_type(boolean args) throws AdamaLangException {
    final var baseType = native_type_base(args);
    if (baseType instanceof CanBeNativeArray) {
      final var arrayToken = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("[]"));
      if (arrayToken != null) {
        return enrich(new TyNativeArray(baseType.behavior, baseType, arrayToken));
      }
    }
    return enrich(baseType);
  }

  public TyType native_type_base(boolean args) throws AdamaLangException {
    var readonlyToken = tokens.popIf((t) -> t.isIdentifier("readonly"));
    return native_type_base_with_behavior(readonlyToken != null, readonlyToken, args);
  }

  public TyNativeTuple native_tuple(final TypeBehavior behavior, final Token readonlyToken, final Token tupleToken) throws AdamaLangException {
    TyNativeTuple tuple = new TyNativeTuple(behavior, readonlyToken, tupleToken);
    final var intro = consumeExpectedSymbol("<");
    tuple.add(intro, native_type(false));

    final var comma = consumeExpectedSymbol(",");;
    tuple.add(comma, native_type(false));

    while (true) {
      final var next = consumeExpectedSymbol(",", ">");
      if (next.isSymbolWithTextEq(">")) {
        tuple.finish(next);
        return tuple;
      } else {
        tuple.add(next, native_type(false));
      }
    }
  }

  public TyType native_type_base_with_behavior(final boolean readonly, final Token readonlyToken, boolean args) throws AdamaLangException {
    final var behavior = readonly ? TypeBehavior.ReadOnlyNativeValue : TypeBehavior.ReadWriteNative;
    final var token = typesafe_id();
    switch (token.text) {
      case "bool":
        return new TyNativeBoolean(behavior, readonlyToken, token);
      case "client":
      case "principal":
        return new TyNativePrincipal(behavior, readonlyToken, token);
      case "secure": {
        Token open = consumeExpectedSymbol("<");
        Token principal = consumeExpectedIdentifier("principal");
        Token close = consumeExpectedSymbol(">");
        return new TyNativeSecurePrincipal(behavior, readonlyToken, token, open, principal, close);
      }
      case "asset":
        return new TyNativeAsset(behavior, readonlyToken, token);
      case "dynamic":
        return new TyNativeDynamic(behavior, readonlyToken, token);
      case "double":
        return new TyNativeDouble(behavior, readonlyToken, token);
      case "complex":
        return new TyNativeComplex(behavior, readonlyToken, token);
      case "date":
        return new TyNativeDate(behavior, readonlyToken, token);
      case "datetime":
        return new TyNativeDateTime(behavior, readonlyToken, token);
      case "time":
        return new TyNativeTime(behavior, readonlyToken, token);
      case "timespan":
        return new TyNativeTimeSpan(behavior, readonlyToken, token);
      case "int":
        return new TyNativeInteger(behavior, readonlyToken, token);
      case "long":
        return new TyNativeLong(behavior, readonlyToken, token);
      case "string":
        return new TyNativeString(behavior, readonlyToken, token);
      case "label":
        return new TyNativeStateMachineRef(behavior, readonlyToken, token);
      case "pair":
        return native_pair(behavior, readonlyToken, token);
      case "map":
        return native_map(behavior, readonlyToken, token);
      case "table":
        if (args) {
          final var typeParameter = type_parameter();
          return new TyTablePtr(behavior, readonlyToken, token, typeParameter);
        } else {
          final var typeParameter = type_parameter();
          return new TyNativeTable(behavior, readonlyToken, token, typeParameter);
        }
      case "channel":
        return new TyNativeChannel(behavior, readonlyToken, token, native_parameter_type());
      case "list":
        return new TyNativeList(behavior, readonlyToken, token, native_parameter_type());
      case "maybe":
        return new TyNativeMaybe(behavior, readonlyToken, token, native_parameter_type());
      case "future":
        return new TyNativeFuture(behavior, readonlyToken, token, native_parameter_type());
      case "tuple":
        return native_tuple(behavior, readonlyToken, token);
      default:
        return new TyNativeRef(behavior, readonlyToken, token);
    }
  }

  public OrderPair order_pair(final Token commaToken) throws AdamaLangException {
    final var id = tokens.pop();
    final var followup = tokens.popIf(t -> t.isIdentifier("asc", "desc"));
    return new OrderPair(commaToken, id, followup);
  }

  @SuppressWarnings("unchecked")
  public TokenizedItem<Token>[] policy_list() throws AdamaLangException {
    final var open = consumeExpectedSymbol("<");
    final var list = new ArrayList<TokenizedItem<Token>>();
    var thing = new TokenizedItem<>(id());
    thing.before(open);
    list.add(thing);
    while (true) {
      final var comma = consumeExpectedSymbol(",", ">");
      if (comma.isSymbolWithTextEq(",")) {
        thing = new TokenizedItem<>(id());
        thing.before(comma);
        list.add(thing);
      } else {
        thing.after(comma);
        return (TokenizedItem<Token>[]) list.toArray(new TokenizedItem[list.size()]);
      }
    }
  }

  public Expression postfix(Scope scope) throws AdamaLangException {
    var result = atomic(scope);
    while (true) {
      var op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("++", "--"));
      if (op == null) {
        op = tokens.popIf(t -> t.isSymbolWithTextEq("[", "(", "."));
      }
      if (op != null) {
        if (op.isSymbolWithTextEq("++") || op.isSymbolWithTextEq("--")) {
          return new PostfixMutate(result, op);
        } else if (op.isSymbolWithTextEq(".")) {
          final var field = tokens.pop();
          if (field == null) {
            throw new ParseException("Parser was expecting an identifier, but instead got end of stream.", op);
          }
          result = new FieldLookup(result, op, field);
        } else if (op.isSymbolWithTextEq("[")) {
          final var arg = expression(scope);
          final var closeBracket = consumeExpectedSymbol("]");
          result = new IndexLookup(result, op, arg, closeBracket);
        } else if (op.isSymbolWithTextEq("(")) {
          final var args = new ArrayList<TokenizedItem<Expression>>();
          var next = tokens.popIf(t -> t.isSymbolWithTextEq(")"));
          Token comma = null;
          while (next == null) {
            final var arg = new TokenizedItem<>(expression(scope));
            if (comma != null) {
              arg.before(comma);
            }
            args.add(arg);
            next = tokens.popIf(t -> t.isSymbolWithTextEq(")", ","));
            if (next != null) {
              if (next.isSymbolWithTextEq(",")) {
                comma = next;
                next = null;
              }
            }
          }
          result = new ApplyArguments(result, op, args, next);
        }
      } else {
        return result;
      }
    }
  }

  public Expression prefix(Scope scope) throws AdamaLangException {
    var op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("++", "--"));
    if (op == null) {
      op = tokens.popIf(t -> t.isSymbolWithTextEq("!", "-"));
    }
    if (op != null) {
      return new PrefixMutate(postfix(scope), op);
    }
    return postfix(scope);
  }

  private void pumpsemicolons(final ArrayList<Consumer<TopLevelDocumentHandler>> target) throws AdamaLangException {
    var semicolon = tokens.popIf(t -> t.isSymbolWithTextEq(";"));
    while (semicolon != null) {
      final var scoped = semicolon;
      target.add(doc -> doc.add(scoped));
      semicolon = tokens.popIf(t -> t.isSymbolWithTextEq(";"));
    }
  }

  public TokenizedItem<TyType> reactive_parameter_type(boolean readonly) throws AdamaLangException {
    final var before = consumeExpectedSymbol("<");
    final var token = new TokenizedItem<>(reactive_type(readonly, false));
    token.before(before);
    token.after(consumeExpectedSymbol(">"));
    return token;
  }

  public TyReactiveMap reactive_map(boolean readonly, final Token mapToken) throws AdamaLangException {
    final var openThing = consumeExpectedSymbol("<");
    final var domainType = native_type(false);
    final var commaToken = consumeExpectedSymbol(",");
    final var rangeType = reactive_type(readonly, false);
    final var closeThing = consumeExpectedSymbol(">");
    return new TyReactiveMap(readonly, mapToken, openThing, domainType, commaToken, rangeType, closeThing);
  }

  private TyType reactive_type(boolean readonly, boolean has_policy) throws AdamaLangException {
    return enrich(reactive_type_intern(readonly, has_policy));
  }

  private TyType reactive_type_intern(boolean readonly, boolean has_policy) throws AdamaLangException {
    final var token = tokens.pop();
    if (token == null) {
      throw new ParseException("Parser was expecting a reactive type, but got an end of stream instead.", tokens.getLastTokenIfAvailable());
    }
    switch (token.text) {
      case "bool":
        return new TyReactiveBoolean(readonly, token);
      case "client":
      case "principal":
        return new TyReactivePrincipal(readonly, token);
      case "asset":
        return new TyReactiveAsset(readonly, token);
      case "dynamic":
        return new TyReactiveDynamic(readonly, token);
      case "double":
        return new TyReactiveDouble(readonly, token);
      case "complex":
        return new TyReactiveComplex(readonly, token);
      case "date":
        return new TyReactiveDate(readonly, token);
      case "datetime":
        return new TyReactiveDateTime(readonly, token);
      case "time":
        return new TyReactiveTime(readonly, token);
      case "timespan":
        return new TyReactiveTimeSpan(readonly, token);
      case "int":
        return new TyReactiveInteger(readonly, token);
      case "long":
        return new TyReactiveLong(readonly, token);
      case "string":
        return new TyReactiveString(readonly, token);
      case "label":
        return new TyReactiveStateMachineRef(readonly, token);
      case "text":
        return new TyReactiveText(readonly, token);
      case "table": {
        final var typeParameter = type_parameter();
        TyReactiveTable table =  new TyReactiveTable(readonly, token, typeParameter);
        if (has_policy) {
          // the reason we do it this way is because want to let typing throw the error such that multiple errors can happen
          table.raiseHasPolicy();
        }
        return table;
      }
      case "map":
        return reactive_map(readonly, token);
      case "maybe":
        return new TyReactiveMaybe(readonly, token, reactive_parameter_type(readonly));
      default:
        testId(token);
        return new TyReactiveRef(readonly, token);
    }
  }

  public Expression relate(Scope scope) throws AdamaLangException {
    final var left = additive(scope);
    var op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("<=", ">="));
    if (op != null) {
      return new BinaryExpression(left, op, additive(scope));
    }
    op = forwardScaRelate();
    if (op != null) {
      return new BinaryExpression(left, op, additive(scope));
    }
    return left;
  }

  public Statement statement(Scope scope) throws AdamaLangException {
    var op = tokens.popIf(t -> t.isSymbolWithTextEq(";"));
    if (op != null) {
      return new EmptyStatement(op);
    }
    op = tokens.peek();
    if (op != null && op.isSymbolWithTextEq("{")) {
      return new ScopeWrap(block(scope));
    }
    op = tokens.popIf(t -> t.isKeyword("if", "auto", "let", "var", "do", "while", "switch", "case", "default", "for", "foreach", "return", "continue", "abort", "block", "break", "@step", "@pump", "@forward"));
    if (op == null) {
      op = tokens.popIf(t -> t.isIdentifier("auto", "let", "var", "transition", "invoke", "assert", "preempt"));
    }
    if (op != null) {
      switch (op.text) {
        case "if":
          return if_statement_trailer(scope, op);
        case "let":
        case "var":
        case "auto": {
          final var varName = id();
          final var eqToken = consumeExpectedSymbol("=");
          final var value = expression(scope);
          final var endToken = consumeExpectedSymbol(";");
          return new DefineVariable(op, varName, null, eqToken, value, endToken);
        }
        case "do":
          return do_statement_trailer(scope, op);
        case "while":
          return while_statement_trailer(scope, op);
        case "for":
          return for_statement_trailer(scope, op);
        case "foreach":
          return foreach_statement_trailer(scope, op);
        case "switch": {
          Token openParen = consumeExpectedSymbol("(");
          Expression val = expression(scope);
          Token closeParen = consumeExpectedSymbol(")");
          Block code = block(scope);
          return new Switch(op, openParen, val, closeParen, code);
        }
        case "case": {
          Expression val = expression(scope);
          Token colon = consumeExpectedSymbol(":");
          return new Case(op, val, colon);
        }
        case "default": {
          Token colon = consumeExpectedSymbol(":");
          return new DefaultCase(op, colon);
        }
        case "transition": {
          final var toTransition = expression(scope);
          final var testIn = tokens.popIf(t -> t.isIdentifier("in"));
          final var evalIn = testIn != null ? expression(scope) : null;
          return new TransitionStateMachine(op, toTransition, testIn, evalIn, consumeExpectedSymbol(";"));
        }
        case "preempt": {
          final var toTransition = expression(scope);
          return new PreemptStateMachine(op, toTransition, consumeExpectedSymbol(";"));
        }
        case "invoke": {
          final var toInvoke = expression(scope);
          return new InvokeStateMachine(op, toInvoke, consumeExpectedSymbol(";"));
        }
        case "assert": {
          final var toAssert = expression(scope);
          return new AssertTruth(op, toAssert, consumeExpectedSymbol(";"));
        }
        case "@step": {
          return new Force(op, Force.Action.Step, consumeExpectedSymbol(";"));
        }
        case "@forward": {
          final var delta = expression(scope);
          return new Forward(op, delta, consumeExpectedSymbol(";"));
        }
        case "@pump": {
          final var toAssert = expression(scope);
          final var intoToken = consumeExpectedIdentifier("into");
          final var channelName = id();
          final var semiColon = consumeExpectedSymbol(";");
          return new PumpMessage(op, toAssert, intoToken, channelName, semiColon);
        }
        case "break":
          return new AlterControlFlow(op, AlterControlFlowMode.Break, consumeExpectedSymbol(";"));
        case "continue":
          return new AlterControlFlow(op, AlterControlFlowMode.Continue, consumeExpectedSymbol(";"));
        case "block":
          return new AlterControlFlow(op, AlterControlFlowMode.Block, consumeExpectedSymbol(";"));
        case "abort":
          return new AlterControlFlow(op, AlterControlFlowMode.Abort, consumeExpectedSymbol(";"));
        case "return": {
          final var hasSemicolon = tokens.popIf(t -> t.isSymbolWithTextEq(";"));
          if (hasSemicolon != null) {
            return new Return(op, null, hasSemicolon);
          } else {
            final var returnStatement = expression(scope);
            return new Return(op, returnStatement, consumeExpectedSymbol(";"));
          }
        }
      }
    }
    return declare_native_or_assign_or_eval(scope, false);
  }

  public Expression ternary(Scope scope) throws AdamaLangException {
    final var condition = logic_xor(scope);
    final var questionToken = tokens.popIf(t -> t.isSymbolWithTextEq("?"));
    if (questionToken != null) {
      final var trueValue = logic_xor(scope);
      final var colonToken = consumeExpectedSymbol(":");
      return new InlineConditional(condition, questionToken, trueValue, colonToken, logic_xor(scope));
    }
    return condition;
  }

  public Expression containment(Scope scope) throws AdamaLangException {
    final var key = ternary(scope);
    final var containToken = tokens.popIf(t -> t.isIdentifier("inside", "outside"));
    if (containToken != null) {
      final var container = ternary(scope);
      return new BinaryExpression(key, containToken, container);
    }
    return key;
  }

  public Expression assignment(Scope scope) throws AdamaLangException {
    final var left = containment(scope);
    var hasAssignment = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("+=", "-=", "*="));
    if (hasAssignment != null) {
      final var right = assignment(scope);
      return new BinaryExpression(left, hasAssignment, right);
    }
    return left;
  }

  public boolean is_token_native_declare(Token token) {
    switch (token.text) {
      case "bool":
      case "client":
      case "secure":
      case "principal":
      case "dynamic":
      case "double":
      case "complex":
      case "date":
      case "datetime":
      case "time":
      case "timespan":
      case "int":
      case "string":
      case "label":
      case "list":
      case "maybe":
      case "future":
      case "map":
      case "pair":
      case "tuple":
      case "table":
      case "readonly":
      case "long":
        return true;
    }
    return false;
  }

  /** predict if we are declaring a variable natively */
  public boolean test_native_declare() throws AdamaLangException {
    final var token = tokens.peek();
    if (token == null) {
      return false;
    }

    if (is_token_native_declare(token)) {
      return true;
    }

    final var futureToken = tokens.peek(1);
    if (token.isIdentifier() && futureToken != null) {
      switch (token.text) {
        case "iterate":
          return false;
      }
      if (futureToken.isIdentifier()) {
        return true;
      }
      if (futureToken.isSymbolWithTextEq("[")) {
        final var futureFutureToken = tokens.peek(2);
        return futureFutureToken != null && futureFutureToken.isSymbolWithTextEq("]");
      }
    }

    return false;
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

  public TokenizedItem<Token> type_parameter() throws AdamaLangException {
    final var before = consumeExpectedSymbol("<");
    final var token = new TokenizedItem<>(typesafe_id());
    token.before(before);
    token.after(consumeExpectedSymbol(">"));
    return token;
  }

  public While while_statement_trailer(Scope scope, final Token whileToken) throws AdamaLangException {
    final var openParen = consumeExpectedSymbol("(");
    final var condition = expression(scope);
    final var endParen = consumeExpectedSymbol(")");
    final var code = block(scope);
    return new While(whileToken, openParen, condition, endParen, code);
  }

  Expression wrap_linq(Scope scope, final Expression base, final Token op) throws AdamaLangException {
    switch (op.text) {
      case "materialize":
        return new Materialize(base, op);
      case "unique": {
        Token mode = tokens.popIf((t) -> t.isIdentifier("first", "last"));
        Token key = null;
        if (mode != null) {
          key = id();
        }
        return new Unique(base, op, mode, key);
      }
      case "where":
        return new Where(base, op, null, null, assignment(scope));
      case "where_as": {
        final var id = tokens.pop();
        final var colonAlias = consumeExpectedSymbol(":");
        return new Where(base, op, id, colonAlias, assignment(scope));
      }
      case "order": {
        final var byToken = tokens.popIf(t -> t.isIdentifier("by"));
        final var keysToOrderBy = new ArrayList<OrderPair>();
        keysToOrderBy.add(order_pair(null));
        var commaToken = tokens.popIf(t -> t.isSymbolWithTextEq(","));
        while (commaToken != null) {
          keysToOrderBy.add(order_pair(commaToken));
          commaToken = tokens.popIf(t -> t.isSymbolWithTextEq(","));
        }
        return new OrderBy(base, op, byToken, keysToOrderBy);
      }
      case "order_dyn":
        Expression str = expression(scope);
        return new OrderDyn(base, op, str);
      case "map": {
        final var fun = expression(scope);
        return new Map(base, op, fun);
      }
      case "shuffle":
        return new Shuffle(op, base);
      case "reduce": {
        final var onToken = tokens.popIf(t -> t.isIdentifier("on"));
        final var fieldToken = id();
        final var viaToken = tokens.popIf(t -> t.isIdentifier("via"));
        if (viaToken != null) {
          final var function = expression(scope);
          return new Reduce(base, op, onToken, fieldToken, viaToken, function);
        } else {
          return new Reduce(base, op, onToken, fieldToken, null, null);
        }
      }
      default: // this is a code coverage hack
      case "limit": {
        final var eLim = assignment(scope);
        return new Limit(base, op, eLim);
      }
      case "offset": {
        final var offsetExpr = assignment(scope);
        return new Offset(base, op, offsetExpr);
      }
    }
  }
}

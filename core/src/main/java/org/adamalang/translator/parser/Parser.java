/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.parser;

import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.exceptions.ParseException;
import org.adamalang.translator.parser.token.MajorTokenType;
import org.adamalang.translator.parser.token.MinorTokenType;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.definitions.*;
import org.adamalang.translator.tree.expressions.*;
import org.adamalang.translator.tree.expressions.constants.*;
import org.adamalang.translator.tree.expressions.linq.*;
import org.adamalang.translator.tree.expressions.operators.*;
import org.adamalang.translator.tree.privacy.*;
import org.adamalang.translator.tree.statements.*;
import org.adamalang.translator.tree.statements.control.*;
import org.adamalang.translator.tree.statements.loops.DoWhile;
import org.adamalang.translator.tree.statements.loops.For;
import org.adamalang.translator.tree.statements.loops.ForEach;
import org.adamalang.translator.tree.statements.loops.While;
import org.adamalang.translator.tree.statements.testing.AssertTruth;
import org.adamalang.translator.tree.statements.testing.Force;
import org.adamalang.translator.tree.statements.testing.PumpMessage;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.reactive.*;
import org.adamalang.translator.tree.types.shared.EnumStorage;
import org.adamalang.translator.tree.types.structures.*;
import org.adamalang.translator.tree.types.traits.CanBeNativeArray;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * recursive descent parser for the Adama language; this contains all the rules which tear down a
 * token stream (via TokenEngine) against a TopLevelDocumentHandler
 */
public class Parser {
  public TokenEngine tokens;

  public Parser(final TokenEngine tokens) {
    this.tokens = tokens;
  }

  public Expression additive() throws AdamaLangException {
    var result = multiplicative();
    var op = forwardScanMathOp("+", "-");
    while (op != null) {
      result = new BinaryExpression(result, op, multiplicative());
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
    var argType = native_type();
    var name = id();
    list.add(new FunctionArg(null, argType, name));
    while (true) {
      final var next = tokens.popIf(t -> t.isSymbolWithTextEq(","));
      if (next == null) {
        return list;
      }
      argType = native_type();
      name = id();
      list.add(new FunctionArg(next, argType, name));
    }
  }

  public Expression atomic() throws AdamaLangException {
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
        case "@i":
          return new ComplexConstant(0.0, 1.0, token);
        case "@nothing":
          return new NothingAssetConstant(token);
        case "@null":
          return new DynamicNullConstant(token);
        case "@stable":
          return new EnvStatus(token, EnvLookupName.Stable);
        case "@maybe":
          var openExpr = tokens.peek();
          if (openExpr == null) {
            throw new ParseException("Parser was expected either a '<' or '(' after @maybe, but got end of stream instead.", token);
          }
          if (openExpr.isSymbolWithTextEq("(")) {
            openExpr = tokens.pop();
            final var expr = expression();
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
          final var toConvert = expression();
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
            ao.add(commaToken, current, colon, expression());
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
      while (current != null && !current.isSymbolWithTextEq("]")) {
        final var item = new TokenizedItem<>(expression());
        if (current.isSymbolWithTextEq(",")) {
          item.before(current);
        }
        aa.add(item);
        current = tokens.popIf(t -> t.isSymbolWithTextEq(",", "]"));
      }
      if (current == null) {
        throw new ParseException("Parser expected a `]`, but instead got end of stream.", token);
      } else {
        aa.end(current);
      }
      return aa;
    } else if (token.isSymbolWithTextEq("(")) {
      final var expr = expression();
      final var endParen = tokens.pop();
      if (endParen == null) {
        throw new ParseException("Parser expected a ), but instead got end of stream.", token);
      }
      if (endParen.isSymbolWithTextEq(")")) {
        return new Parentheses(token, expr, endParen);
      } else {
        throw new ParseException("Parser expected a ), but instead got an `" + endParen + "`", endParen);
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

  public Block block() throws AdamaLangException {
    final var openBrace = tokens.popIf(t -> t.isSymbolWithTextEq("{"));
    final var b = new Block(openBrace);
    if (openBrace != null) {
      var next = tokens.popIf(t -> t.isSymbolWithTextEq("}"));
      while (next == null) {
        b.add(statement());
        next = tokens.popIf(t -> t.isSymbolWithTextEq("}"));
      }
      b.end(next);
    } else {
      b.add(statement());
    }
    return b;
  }

  private Token consumeExpectedIdentifer(final String ident) throws AdamaLangException {
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

  public Statement declare_native_or_assign_or_eval(final boolean secondPartOfForLoop) throws AdamaLangException {
    if (!secondPartOfForLoop) {
      if (test_native_declare()) {
        final var type = native_type();
        final var varName = id();
        final var equalToken = tokens.popIf(t -> t.isSymbolWithTextEq("="));
        Expression valueExpr = null;
        if (equalToken != null) {
          valueExpr = expression();
        }
        final var endToken = consumeExpectedSymbol(";");
        final var defineVariable = new DefineVariable(null, varName, type, equalToken, valueExpr, endToken);
        return defineVariable;
      }
    }
    final var leftSide = expression();
    var hasAssignment = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("<-"));
    if (hasAssignment == null) {
      hasAssignment = tokens.popIf(t -> t.isSymbolWithTextEq("="));
    }
    if (hasAssignment != null) {
      final var value = expression();
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

  public DocumentConfig define_config(final Token nameToken) throws AdamaLangException {
    final var equals = consumeExpectedSymbol("=");
    final var expr = expression();
    final var semicolon = consumeExpectedSymbol(";");
    return new DocumentConfig(nameToken, equals, expr, semicolon);
  }

  public Consumer<TopLevelDocumentHandler> define_static(Token staticToken) throws AdamaLangException {
    var open = consumeExpectedSymbol("{");
    blackhole_commas(open);
    ArrayList<Definition> definitions = new ArrayList<>();

    var nextOrClose = tokens.pop();
    while (!nextOrClose.isSymbolWithTextEq("}")) {
      if (!nextOrClose.isIdentifier("create", "invent", "send", "maximum_history")) {
        throw new ParseException("Parser was expecting a static definition. Candidates are create, invent, send", tokens.getLastTokenIfAvailable());
      }
      switch (nextOrClose.text) {
        case "create":
          definitions.add(define_document_event_raw(nextOrClose, DocumentEvent.AskCreation));
          break;
        case "invent":
          definitions.add(define_document_event_raw(nextOrClose, DocumentEvent.AskInvention));
          break;
        case "send":
          definitions.add(define_document_event_raw(nextOrClose, DocumentEvent.AskSendWhileDisconnected));
          break;
        case "maximum_history":
          definitions.add(define_config(nextOrClose));
          break;
      }
      nextOrClose = tokens.pop();
      if (nextOrClose == null) {
        throw new ParseException("Parser was expecting either a Symbol=} or an Identifer to define a new enum label, but got end of stream instead.", tokens.getLastTokenIfAvailable());
      }
    }
    DefineStatic staticDefn = new DefineStatic(staticToken, open, definitions, nextOrClose);
    return (handler) -> handler.add(staticDefn);
  }

  public Consumer<TopLevelDocumentHandler> define() throws AdamaLangException {
    // define a state machine transition
    var op = tokens.popIf(Token::isLabel);
    if (op != null) {
      final var dst = new DefineStateTransition(op, block());
      return doc -> doc.add(dst);
    }
    op = tokens.popIf(t -> t.isKeyword("enum", "@construct", "@connected", "@disconnected", "@attached", "@static", "@can_attach"));
    if (op == null) {
      op = tokens.popIf(t -> t.isIdentifier("record", "message", "channel", "rpc", "function", "procedure", "test", "import", "view", "policy", "bubble", "dispatch"));
    }
    if (op != null) {
      switch (op.text) {
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
        case "@connected":
          return define_document_event(op, DocumentEvent.ClientConnected);
        case "@disconnected":
          return define_document_event(op, DocumentEvent.ClientDisconnected);
        case "@attached":
          return define_document_event(op, DocumentEvent.AssetAttachment);
        case "@can_attach":
          return define_document_event(op, DocumentEvent.AskAssetAttachment);
        case "@static":
          return define_static(op);
        case "view": {
          final var ntype = native_type();
          final var name = id();
          final var semicolon = consumeExpectedSymbol(";");
          AugmentViewerState avs = new AugmentViewerState(op, ntype, name, semicolon);
          return doc -> doc.add(avs);
        }
        case "bubble":
          final var bubble = define_bubble(op);
          return doc -> doc.add(bubble);
        case "policy":
          final var policy = define_policy_trailer(op);
          return doc -> doc.add(policy);
      }
    }
    final var newField = define_field_record();
    return doc -> doc.add(newField);
  }

  public BubbleDefinition define_bubble(final Token bubbleToken) throws AdamaLangException {
    final var openClient = consumeExpectedSymbol("<");
    final var clientVar = id();
    final var comma = tokens.popIf((t) -> t.isSymbolWithTextEq(","));
    final var viewerStateName = comma != null ? id() : null;
    final var closeClient = consumeExpectedSymbol(">");
    final var nameToken = id();
    final var equalsToken = consumeExpectedSymbol("=");
    final var expression = expression();
    final var semicolonToken = consumeExpectedSymbol(";");
    return new BubbleDefinition(bubbleToken, openClient, clientVar, comma, viewerStateName, closeClient, nameToken, equalsToken, expression, semicolonToken);
  }

  public DefineDocumentEvent define_document_event_raw(final Token eventToken, final DocumentEvent which) throws AdamaLangException {
    final var openParen = consumeExpectedSymbol("(");
    final var name = id();
    final var commaToken = tokens.popIf((t) -> t.isSymbolWithTextEq(","));
    final var parameterNameToken = commaToken != null ? id() : null;
    final var closeParen = consumeExpectedSymbol(")");
    return new DefineDocumentEvent(eventToken, which, openParen, name, commaToken, parameterNameToken, closeParen, block());
  }

  public Consumer<TopLevelDocumentHandler> define_document_event(final Token eventToken, final DocumentEvent which) throws AdamaLangException {
    final var dce = define_document_event_raw(eventToken, which);
    return doc -> doc.add(dce);
  }

  public Consumer<TopLevelDocumentHandler> define_constructor_trailer(final Token constructorToken) throws AdamaLangException {
    final var openParenToken = tokens.popIf(t -> t.isSymbolWithTextEq("("));
    if (openParenToken != null) {
      final var identA = id();
      final var identB = id();
      if (identA.text.equals("client")) {
        final var clientTypeToken = identA;
        final var clientVarToken = identB;
        final var commaToken = tokens.popIf(t -> t.isSymbolWithTextEq(","));
        if (commaToken != null) {
          final var messageTypeToken = id();
          final var messageNameToken = id();
          final var endParenToken = consumeExpectedSymbol(")");
          final var dc = new DefineConstructor(constructorToken, openParenToken, clientTypeToken, clientVarToken, commaToken, messageTypeToken, messageNameToken, endParenToken, block());
          return doc -> doc.add(dc);
        } else {
          final var endParenToken = consumeExpectedSymbol(")");
          final var dc = new DefineConstructor(constructorToken, openParenToken, clientTypeToken, clientVarToken, commaToken, null, null, endParenToken, block());
          return doc -> doc.add(dc);
        }
      } else {
        final var endParenToken = consumeExpectedSymbol(")");
        final var dc = new DefineConstructor(constructorToken, openParenToken, null, null, null, identA, identB, endParenToken, block());
        return doc -> doc.add(dc);
      }
    } else {
      final var dc = new DefineConstructor(constructorToken, null, null, null, null, null, null, null, block());
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
    final var introReturnType = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("->"));
    TyType returnType = null;
    if (introReturnType != null) {
      returnType = native_type();
    }
    final var code = block();
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
        throw new ParseException("Parser was expecting either a Symbol=} or an Identifer to define a new enum label, but got end of stream instead.", tokens.getLastTokenIfAvailable());
      }
    }
    final var endBrace = next;
    return doc -> doc.add(new TyNativeEnum(TypeBehavior.ReadWriteNative, enumToken, enumName, openBrace, es, endBrace));
  }

  public FieldDefinition define_field_record() throws AdamaLangException {
    final var policy = field_privacy();
    final var isAuto = tokens.popIf(t -> t.isIdentifier("auto", "formula"));
    if (isAuto != null) {
      final var id = id();
      final var equalsToken = consumeExpectedSymbol("=");
      final var compute = expression();
      return new FieldDefinition(policy, isAuto, null, id, equalsToken, compute, null, consumeExpectedSymbol(";"));
    } else {
      final var type = reactive_type();
      final var id = id();
      final var equalsToken = tokens.popIf(t -> t.isSymbolWithTextEq("="));
      Expression defaultValue = null;
      if (equalsToken != null) {
        defaultValue = expression();
      }
      return new FieldDefinition(policy, null, type, id, equalsToken, null, defaultValue, consumeExpectedSymbol(";"));
    }
  }

  public Consumer<TopLevelDocumentHandler> define_function_trailer(final Token functionToken) throws AdamaLangException {
    final var name = id();
    final var openParen = consumeExpectedSymbol("(");
    final var args = arg_list();
    final var closeParen = consumeExpectedSymbol(")");
    final var introReturn = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("->"));
    if (introReturn == null) {
      throw new ParseException("Parser was expecting -> for the pure function, and pure functions must have return types.", closeParen);
    }
    final var returnType = native_type();
    final var code = block();
    final var df = new DefineFunction(functionToken, FunctionSpecialization.Pure, name, openParen, args, closeParen, introReturn, returnType, null, code);
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
    if ("client".equals(nextType.text)) {
      final var clientType = nextType;
      final var clientVar = id();
      final var comma = consumeExpectedSymbol(",");
      final var messageType = id();
      final var arrayToken = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("[]"));
      final var messageVar = id();
      final var endParen = consumeExpectedSymbol(")");
      handler.setFullHandler(openParen, clientType, clientVar, comma, messageType, arrayToken, messageVar, endParen, block());
    } else {
      final var messageType = nextType;
      testId(messageType);
      final var arrayToken = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("[]"));
      final var messageVarToken = id();
      final var endParen = consumeExpectedSymbol(")");
      handler.setMessageOnlyHandler(openParen, messageType, arrayToken, messageVarToken, endParen, block());
    }
    return doc -> doc.add(handler);
  }

  public Consumer<TopLevelDocumentHandler> define_rpc(final Token rpcToken) throws AdamaLangException {
    final var name = id();
    final var openParen = consumeExpectedSymbol("(");
    final var clientVar = id();
    Token comma;
    ArrayList<FunctionArg> args = new ArrayList<>();
    while ((comma = tokens.popIf((t) -> t.isSymbolWithTextEq(","))) != null) {
      final var paramTyType = native_type();
      args.add(new FunctionArg(comma, paramTyType, id()));
    }
    final var closeParen = consumeExpectedSymbol(")");
    final var code = block();
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
    PublicPolicy policy = new PublicPolicy(null);
    policy.ingest(messageToken);
    final var name = id();
    final var storage = new StructureStorage(StorageSpecialization.Message, false, consumeExpectedSymbol("{"));
    var endBrace = tokens.popIf(t -> t.isSymbolWithTextEq("}"));
    while (endBrace == null) {
      final var type = native_type();
      final var field = id();
      final var equalsToken = tokens.popIf(t -> t.isSymbolWithTextEq("="));
      Expression defaultValueOverride = null;
      if (equalsToken != null) {
        defaultValueOverride = expression();
      }
      storage.add(new FieldDefinition(policy, null, type, field, equalsToken, null, defaultValueOverride, consumeExpectedSymbol(";")));
      endBrace = tokens.popIf(t -> t.isSymbolWithTextEq("}"));
    }
    storage.end(endBrace);
    return doc -> doc.add(new TyNativeMessage(TypeBehavior.ReadOnlyNativeValue, messageToken, name, storage));
  }

  public DefineMethod define_method_trailer(final Token methodToken) throws AdamaLangException {
    final var name = id();
    final var openParen = consumeExpectedSymbol("(");
    final var args = arg_list();
    final var closeParen = consumeExpectedSymbol(")");
    final var hasReturnType = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("->"));
    TyType returnType = null;
    if (hasReturnType != null) {
      returnType = native_type();
    }
    final var readonlyToken = tokens.popIf(t -> t.isIdentifier("readonly"));
    final var code = block();
    return new DefineMethod(methodToken, name, openParen, args, closeParen, hasReturnType, returnType, readonlyToken, code);
  }

  public DefineCustomPolicy define_policy_trailer(final Token definePolicy) throws AdamaLangException {
    final var id = id();
    final var openParen = consumeExpectedSymbol("(");
    final var clientVar = id();
    final var endParen = consumeExpectedSymbol(")");
    final var code = block();
    return new DefineCustomPolicy(definePolicy, id, openParen, clientVar, endParen, code);
  }

  public Consumer<TopLevelDocumentHandler> define_procedure_trailer(final Token procedureToken) throws AdamaLangException {
    final var name = id();
    final var openParen = consumeExpectedSymbol("(");
    final var args = arg_list();
    final var closeParen = consumeExpectedSymbol(")");
    final var introReturn = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("->"));
    TyType returnType = null;
    if (introReturn != null) {
      returnType = native_type();
    }
    final var readonlyToken = tokens.popIf(t -> t.isIdentifier("readonly"));
    final var code = block();
    final var df = new DefineFunction(procedureToken, FunctionSpecialization.Impure, name, openParen, args, closeParen, introReturn, returnType, readonlyToken, code);
    return doc -> doc.add(df);
  }

  public Consumer<TopLevelDocumentHandler> define_record_trailer(final Token recordToken) throws AdamaLangException {
    final var name = id();
    final var storage = new StructureStorage(StorageSpecialization.Record, false, consumeExpectedSymbol("{"));
    while (true) {
      var op = tokens.popIf(t -> t.isIdentifier("require", "policy", "method", "bubble", "index"));
      while (op != null) {
        switch (op.text) {
          case "require": {
            final var policyRequire = id();
            storage.markPolicyForVisibility(op, policyRequire, consumeExpectedSymbol(";"));
            break;
          }
          case "bubble":
            storage.add(define_bubble(op));
            break;
          case "index":
            storage.add(define_indexing(op));
            break;
          case "policy":
            storage.addPolicy(define_policy_trailer(op));
            break;
          case "method":
            storage.add(define_method_trailer(op));
            break;
        }
        op = tokens.popIf(t -> t.isIdentifier("require", "policy", "method", "bubble", "index"));
      }
      op = tokens.popIf(t -> t.isSymbolWithTextEq("}"));
      if (op != null) {
        storage.finalizeRecord();
        storage.end(op);
        return doc -> doc.add(new TyReactiveRecord(recordToken, name, storage));
      }
      storage.add(define_field_record());
    }
  }

  public Consumer<TopLevelDocumentHandler> define_test_trailer(final Token testToken) throws AdamaLangException {
    final var name = id();
    final var dt = new DefineTest(testToken, name, block());
    return doc -> doc.add(dt);
  }

  public DoWhile do_statement_trailer(final Token doToken) throws AdamaLangException {
    final var code = block();
    final var whileToken = consumeExpectedKeyword("while");
    final var openParen = consumeExpectedSymbol("(");
    final var condition = expression();
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

  public Expression equality() throws AdamaLangException {
    final var left = relate();
    final var op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("==", "!="));
    if (op != null) {
      return new BinaryExpression(left, op, relate());
    }
    return left;
  }

  public Expression expression() throws AdamaLangException {
    Expression base;
    final var iterateToken = tokens.popIf(t -> t.isIdentifier("iterate"));
    if (iterateToken != null) {
      base = new Iterate(iterateToken, assignment());
    } else {
      base = assignment();
    }
    Token op;
    while ((op = tokens.popIf(t -> t.isIdentifier("where", "where_as", "order", "shuffle", "reduce", "limit", "offset"))) != null) {
      base = wrap_linq(base, op);
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

  public For for_statement_trailer(final Token forToken) throws AdamaLangException {
    final var openParen = consumeExpectedSymbol("(");
    final var noInitialSemicolon = tokens.popIf(t -> t.isSymbolWithTextEq(";"));
    Statement initial = null;
    if (noInitialSemicolon == null) {
      initial = declare_native_or_assign_or_eval(false);
    }
    Expression condition = null;
    var endConditionSemicolon = tokens.popIf(t -> t.isSymbolWithTextEq(";"));
    if (endConditionSemicolon == null) {
      condition = expression();
      endConditionSemicolon = consumeExpectedSymbol(";");
    }
    final var noAdvance = tokens.peek();
    Statement advance = null;
    if (noAdvance != null && !noAdvance.isSymbolWithTextEq(")")) {
      advance = declare_native_or_assign_or_eval(true);
    }
    final var endParen = consumeExpectedSymbol(")");
    final var code = block();
    return new For(forToken, openParen, initial, noInitialSemicolon, condition, endConditionSemicolon, advance, endParen, code);
  }

  public ForEach foreach_statement_trailer(final Token foreachToken) throws AdamaLangException {
    final var openToken = consumeExpectedSymbol("(");
    final var varName = id();
    final var inToken = consumeExpectedIdentifer("in");
    final var list = expression();
    final var endToken = consumeExpectedSymbol(")");
    final var code = block();
    return new ForEach(foreachToken, openToken, varName, inToken, list, endToken, code);
  }

  private Token forwardScanMathOp(final String... args) throws AdamaLangException {
    final var scan = tokens.peek();
    if (scan == null) {
      return null;
    }
    if (scan.isSymbolWithTextEq(args)) {
      final var afterScan = tokens.peek(1);
      if (afterScan != null && afterScan.isSymbolWithTextEq("=")) {
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
    return testId(tokens.pop());
  }

  public MegaIf.Condition if_condition() throws AdamaLangException {
    final var openParen = consumeExpectedSymbol("(");
    final var guard = expression();
    final var asToken = tokens.popIf(t -> t.isIdentifier("as"));
    Token nameToken = null;
    if (asToken != null) {
      nameToken = id();
    }
    final var endParen = consumeExpectedSymbol(")");
    return new MegaIf.Condition(openParen, guard, asToken, nameToken, endParen);
  }

  public MegaIf if_statement_trailer(final Token ifToken) throws AdamaLangException {
    final var primary = if_condition();
    final var result = new MegaIf(ifToken, primary, block());
    var elseToken = tokens.popIf(t -> t.isKeyword("else"));
    while (elseToken != null) {
      final var ifToken2 = tokens.popIf(t -> t.isKeyword("if"));
      if (ifToken2 != null) {
        final var branchCond = if_condition();
        result.add(elseToken, ifToken2, branchCond, block());
      } else {
        result.setElse(elseToken, block());
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

  public Expression logic_and() throws AdamaLangException {
    var result = equality();
    var op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("&&"));
    while (op != null) {
      result = new BinaryExpression(result, op, equality());
      op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("&&"));
    }
    return result;
  }

  public Expression logic_or() throws AdamaLangException {
    var result = logic_and();
    var op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("||"));
    while (op != null) {
      result = new BinaryExpression(result, op, logic_and());
      op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("||"));
    }
    return result;
  }

  public Expression logic_xor() throws AdamaLangException {
    var result = logic_or();
    var op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("^^"));
    while (op != null) {
      result = new BinaryExpression(result, op, logic_or());
      op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("^^"));
    }
    return result;
  }

  public Expression multiplicative() throws AdamaLangException {
    var result = prefix();
    var op = forwardScanMathOp("*", "/", "%");
    while (op != null) {
      result = new BinaryExpression(result, op, prefix());
      op = forwardScanMathOp("*", "/", "%");
    }
    return result;
  }

  public TyNativeMap native_map(final TypeBehavior behavior, final Token mapToken) throws AdamaLangException {
    final var openThing = consumeExpectedSymbol("<");
    final var domainType = native_type();
    final var commaToken = consumeExpectedSymbol(",");
    final var rangeType = native_type();
    final var closeThing = consumeExpectedSymbol(">");
    return new TyNativeMap(behavior, mapToken, openThing, domainType, commaToken, rangeType, closeThing);
  }

  public TokenizedItem<TyType> native_parameter_type() throws AdamaLangException {
    final var before = consumeExpectedSymbol("<");
    final var subtype = new TokenizedItem<>(native_type());
    final var after = consumeExpectedSymbol(">");
    subtype.before(before);
    subtype.after(after);
    return subtype;
  }

  public TyType native_type() throws AdamaLangException {
    final var baseType = native_type_base();
    if (baseType instanceof CanBeNativeArray) {
      final var arrayToken = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("[]"));
      if (arrayToken != null) {
        return new TyNativeArray(baseType.behavior, baseType, arrayToken);
      }
    }
    return baseType;
  }

  public TyType native_type_base() throws AdamaLangException {
    var readonlyToken = tokens.peek();
    final var readonly = readonlyToken != null && readonlyToken.isIdentifier("readonly");
    if (readonly) {
      readonlyToken = tokens.pop();
    } else {
      readonlyToken = null;
    }
    return native_type_base_with_behavior(readonly, readonlyToken);
  }

  public TyType native_type_base_with_behavior(final boolean readonly, final Token readonlyToken) throws AdamaLangException {
    final var behavior = readonly ? TypeBehavior.ReadOnlyNativeValue : TypeBehavior.ReadWriteNative;
    final var token = tokens.pop();
    switch (token.text) {
      case "bool":
        return new TyNativeBoolean(behavior, readonlyToken, token);
      case "client":
        return new TyNativeClient(behavior, readonlyToken, token);
      case "asset":
        return new TyNativeAsset(behavior, readonlyToken, token);
      case "dynamic":
        return new TyNativeDynamic(behavior, readonlyToken, token);
      case "double":
        return new TyNativeDouble(behavior, readonlyToken, token);
      case "complex":
        return new TyNativeComplex(behavior, readonlyToken, token);
      case "int":
        return new TyNativeInteger(behavior, readonlyToken, token);
      case "long":
        return new TyNativeLong(behavior, readonlyToken, token);
      case "string":
        return new TyNativeString(behavior, readonlyToken, token);
      case "label":
        return new TyNativeStateMachineRef(behavior, readonlyToken, token);
      case "map":
        return native_map(behavior, token);
      case "table":
        final var typeParameter = type_parameter();
        return new TyNativeTable(behavior, readonlyToken, token, typeParameter);
      case "channel":
        return new TyNativeChannel(behavior, readonlyToken, token, native_parameter_type());
      case "list":
        return new TyNativeList(behavior, readonlyToken, token, native_parameter_type());
      case "maybe":
        return new TyNativeMaybe(behavior, readonlyToken, token, native_parameter_type());
      case "future":
        return new TyNativeFuture(behavior, readonlyToken, token, native_parameter_type());
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

  public Expression postfix() throws AdamaLangException {
    var result = atomic();
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
          final var arg = expression();
          final var closeBracket = consumeExpectedSymbol("]");
          result = new IndexLookup(result, op, arg, closeBracket);
        } else if (op.isSymbolWithTextEq("(")) {
          final var args = new ArrayList<TokenizedItem<Expression>>();
          var next = tokens.popIf(t -> t.isSymbolWithTextEq(")"));
          Token comma = null;
          while (next == null) {
            final var arg = new TokenizedItem<>(expression());
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

  public Expression prefix() throws AdamaLangException {
    var op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("++", "--"));
    if (op == null) {
      op = tokens.popIf(t -> t.isSymbolWithTextEq("!", "-"));
    }
    if (op != null) {
      return new PrefixMutate(postfix(), op);
    }
    return postfix();
  }

  private void pumpsemicolons(final ArrayList<Consumer<TopLevelDocumentHandler>> target) throws AdamaLangException {
    var semicolon = tokens.popIf(t -> t.isSymbolWithTextEq(";"));
    while (semicolon != null) {
      final var scoped = semicolon;
      target.add(doc -> doc.add(scoped));
      semicolon = tokens.popIf(t -> t.isSymbolWithTextEq(";"));
    }
  }

  public TokenizedItem<TyType> reactive_parameter_type() throws AdamaLangException {
    final var before = consumeExpectedSymbol("<");
    final var token = new TokenizedItem<>(reactive_type());
    token.before(before);
    token.after(consumeExpectedSymbol(">"));
    return token;
  }

  public TyReactiveMap reactive_map(final Token mapToken) throws AdamaLangException {
    final var openThing = consumeExpectedSymbol("<");
    final var domainType = native_type();
    final var commaToken = consumeExpectedSymbol(",");
    final var rangeType = reactive_type();
    final var closeThing = consumeExpectedSymbol(">");
    return new TyReactiveMap(mapToken, openThing, domainType, commaToken, rangeType, closeThing);
  }

  public TyType reactive_type() throws AdamaLangException {
    final var token = tokens.pop();
    if (token == null) {
      throw new ParseException("Parser was expecting a reactive type, but got an end of stream instead.", tokens.getLastTokenIfAvailable());
    }
    switch (token.text) {
      case "bool":
        return new TyReactiveBoolean(token);
      case "client":
        return new TyReactiveClient(token);
      case "asset":
        return new TyReactiveAsset(token);
      case "dynamic":
        return new TyReactiveDynamic(token);
      case "double":
        return new TyReactiveDouble(token);
      case "complex":
        return new TyReactiveComplex(token);
      case "int":
        return new TyReactiveInteger(token);
      case "long":
        return new TyReactiveLong(token);
      case "string":
        return new TyReactiveString(token);
      case "label":
        return new TyReactiveStateMachineRef(token);
      case "table": {
        final var typeParameter = type_parameter();
        return new TyReactiveTable(token, typeParameter);
      }
      case "map":
        return reactive_map(token);
      case "maybe":
        return new TyReactiveMaybe(token, reactive_parameter_type());
      default:
        testId(token);
        return new TyReactiveRef(token);
    }
  }

  public Expression relate() throws AdamaLangException {
    final var left = additive();
    var op = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("<=", ">="));
    if (op != null) {
      return new BinaryExpression(left, op, additive());
    }
    op = forwardScaRelate();
    if (op != null) {
      return new BinaryExpression(left, op, additive());
    }
    return left;
  }

  public Statement statement() throws AdamaLangException {
    var op = tokens.popIf(t -> t.isSymbolWithTextEq(";"));
    if (op != null) {
      return new EmptyStatement(op);
    }
    op = tokens.popIf(t -> t.isKeyword("if", "auto", "let", "var", "do", "while", "for", "foreach", "return", "continue", "abort", "block", "break", "@step", "@pump"));
    if (op == null) {
      op = tokens.popIf(t -> t.isIdentifier("auto", "let", "var", "transition", "invoke", "assert", "preempt"));
    }
    if (op != null) {
      switch (op.text) {
        case "if":
          return if_statement_trailer(op);
        case "let":
        case "var":
        case "auto": {
          final var varName = id();
          final var eqToken = consumeExpectedSymbol("=");
          final var value = expression();
          final var endToken = consumeExpectedSymbol(";");
          return new DefineVariable(op, varName, null, eqToken, value, endToken);
        }
        case "do":
          return do_statement_trailer(op);
        case "while":
          return while_statement_trailer(op);
        case "for":
          return for_statement_trailer(op);
        case "foreach":
          return foreach_statement_trailer(op);
        case "transition": {
          final var toTransition = expression();
          final var testIn = tokens.popIf(t -> t.isIdentifier("in"));
          final var evalIn = testIn != null ? expression() : null;
          return new TransitionStateMachine(op, toTransition, testIn, evalIn, consumeExpectedSymbol(";"));
        }
        case "preempt": {
          final var toTransition = expression();
          return new PreemptStateMachine(op, toTransition, consumeExpectedSymbol(";"));
        }
        case "invoke": {
          final var toInvoke = expression();
          return new InvokeStateMachine(op, toInvoke, consumeExpectedSymbol(";"));
        }
        case "assert": {
          final var toAssert = expression();
          return new AssertTruth(op, toAssert, consumeExpectedSymbol(";"));
        }
        case "@step": {
          return new Force(op, Force.Action.Step, consumeExpectedSymbol(";"));
        }
        case "@pump": {
          final var toAssert = expression();
          final var intoToken = consumeExpectedIdentifer("into");
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
            final var returnStatement = expression();
            return new Return(op, returnStatement, consumeExpectedSymbol(";"));
          }
        }
      }
    }
    return declare_native_or_assign_or_eval(false);
  }

  public Expression ternary() throws AdamaLangException {
    final var condition = logic_xor();
    final var questionToken = tokens.popIf(t -> t.isSymbolWithTextEq("?"));
    if (questionToken != null) {
      final var trueValue = logic_xor();
      final var colonToken = consumeExpectedSymbol(":");
      return new InlineConditional(condition, questionToken, trueValue, colonToken, logic_xor());
    }
    return condition;
  }

  public Expression assignment() throws AdamaLangException {
    final var left = ternary();
    var hasAssignment = tokens.popNextAdjSymbolPairIf(t -> t.isSymbolWithTextEq("+=", "-=", "*="));
    if (hasAssignment != null) {
      final var right = assignment();
      return new BinaryExpression(left, hasAssignment, right);
    }
    return left;
  }

  public boolean test_native_declare() throws AdamaLangException {
    final var token = tokens.peek();
    if (token == null) {
      return false;
    }
    switch (token.text) {
      case "bool":
      case "client":
      case "dynamic":
      case "double":
      case "complex":
      case "int":
      case "string":
      case "label":
      case "list":
      case "maybe":
      case "future":
      case "map":
      case "table":
      case "readonly":
        return true;
      default:
        final var futureToken = tokens.peek(1);
        if (token.isIdentifier() && futureToken != null) {
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
    final var token = new TokenizedItem<>(id());
    token.before(before);
    token.after(consumeExpectedSymbol(">"));
    return token;
  }

  public While while_statement_trailer(final Token whileToken) throws AdamaLangException {
    final var openParen = consumeExpectedSymbol("(");
    final var condition = expression();
    final var endParen = consumeExpectedSymbol(")");
    final var code = block();
    return new While(whileToken, openParen, condition, endParen, code);
  }

  Expression wrap_linq(final Expression base, final Token op) throws AdamaLangException {
    switch (op.text) {
      case "where":
        return new Where(base, op, null, null, assignment());
      case "where_as": {
        final var id = tokens.pop();
        final var colonAlias = consumeExpectedSymbol(":");
        return new Where(base, op, id, colonAlias, assignment());
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
      case "shuffle":
        return new Shuffle(op, base);
      case "reduce": {
        final var onToken = tokens.popIf(t -> t.isIdentifier("on"));
        final var fieldToken = id();
        final var viaToken = consumeExpectedIdentifer("via");
        final var function = expression();
        return new Reduce(base, op, onToken, fieldToken, viaToken, function);
      }
      default: // this is a code coverage hack
      case "limit": {
        final var eLim = assignment();
        return new Limit(base, op, eLim);
      }
      case "offset": {
        final var offsetExpr = assignment();
        return new Offset(base, op, offsetExpr);
      }
    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.expressions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;
import org.adamalang.translator.tree.types.natives.TyNativeArray;
import org.adamalang.translator.tree.types.natives.TyNativeMessage;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;
import org.adamalang.translator.tree.types.structures.StructureStorage;
import org.adamalang.translator.tree.types.traits.SupportsTwoPhaseTyping;

import java.util.ArrayList;
import java.util.function.Consumer;

/** an anonymous array of items [item1, item2, ..., itemN] */
public class AnonymousArray extends Expression implements SupportsTwoPhaseTyping {
  private static final TyNativeMessage EMPTY_MESSAGE = new TyNativeMessage(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("__EmptyMessageNoArgs_"), new StructureStorage(StorageSpecialization.Message, true, null));
  public final ArrayList<TokenizedItem<Expression>> elements;
  public Token closeBracketToken;
  public Token openBracketToken;

  public AnonymousArray(final Token openBracketToken) {
    elements = new ArrayList<>();
    this.openBracketToken = openBracketToken;
    ingest(openBracketToken);
  }

  /** add an anonymous object to the array */
  public void add(final TokenizedItem<Expression> aobject) {
    elements.add(aobject);
    ingest(aobject.item);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(openBracketToken);
    for (final TokenizedItem<Expression> element : elements) {
      element.emitBefore(yielder);
      element.item.emit(yielder);
      element.emitAfter(yielder);
    }
    yielder.accept(closeBracketToken);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    environment.mustBeComputeContext(this);
    if (suggestion != null) {
      if (environment.rules.IsNativeArray(suggestion, false)) {
        final var elementType = environment.rules.ExtractEmbeddedType(suggestion, false);
        for (final TokenizedItem<Expression> elementExpr : elements) {
          final var computedType = elementExpr.item.typing(environment, elementType);
          environment.rules.CanTypeAStoreTypeB(elementType, computedType, StorageTweak.None, false);
        }
        return suggestion;
      }
      return null;
    } else {
      var proposal = estimateType(environment, suggestion);
      if (proposal != null) {
        proposal = environment.rules.EnsureRegisteredAndDedupe(proposal, false);
        upgradeType(environment, proposal);
      }
      return proposal;
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    final var me = (TyNativeArray) cachedType;
    if (me != null) {
      sb.append("new ").append(me.getJavaConcreteType(environment)).append(" {");
      var first = true;
      for (final TokenizedItem<Expression> element : elements) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        element.item.writeJava(sb, environment);
      }
      sb.append("}");
    }
  }

  public TyType estimateType(final Environment environment, final TyType suggestion) {
    TyType proposal = null;
    if (elements.size() > 0) {
      final var firstExpr = elements.get(0).item;
      if (firstExpr instanceof SupportsTwoPhaseTyping) {
        proposal = ((SupportsTwoPhaseTyping) firstExpr).estimateType(environment);
      } else {
        proposal = firstExpr.typing(environment, suggestion instanceof TyNativeArray ? environment.rules.ExtractEmbeddedType(suggestion, false) : null);
      }
    }
    if (proposal == null) {
      proposal = EMPTY_MESSAGE;
    }
    for (final TokenizedItem<Expression> elementExpr : elements) {
      TyType candidate = null;
      if (elementExpr.item instanceof SupportsTwoPhaseTyping) {
        candidate = ((SupportsTwoPhaseTyping) elementExpr.item).estimateType(environment);
      } else {
        candidate = elementExpr.item.typing(environment, null);
      }
      proposal = environment.rules.GetMaxType(proposal, candidate, false);
    }
    if (proposal != null) {
      return new TyNativeArray(TypeBehavior.ReadOnlyNativeValue, proposal.withPosition(this), null).withPosition(this);
    } else {
      return null;
    }
  }

  public void end(final Token closeBracketToken) {
    this.closeBracketToken = closeBracketToken;
    ingest(closeBracketToken);
  }

  @Override
  public TyType estimateType(final Environment environment) {
    return estimateType(environment, null);
  }

  @Override
  public void upgradeType(final Environment environment, final TyType proposalArray) {
    cachedType = proposalArray.withPosition(this);
    if (proposalArray != null && proposalArray instanceof TyNativeArray) {
      final var proposalElement = ((TyNativeArray) proposalArray).getEmbeddedType(environment);
      for (final TokenizedItem<Expression> elementExpr : elements) {
        if (elementExpr.item instanceof SupportsTwoPhaseTyping) {
          ((SupportsTwoPhaseTyping) elementExpr.item).upgradeType(environment, proposalElement);
        }
      }
    }
  }
}

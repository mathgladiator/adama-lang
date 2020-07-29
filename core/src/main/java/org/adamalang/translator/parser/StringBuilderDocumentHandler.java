/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.parser;

import java.util.function.Consumer;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.definitions.DefineConstructor;
import org.adamalang.translator.tree.definitions.DefineDispatcher;
import org.adamalang.translator.tree.definitions.DefineDocumentEvent;
import org.adamalang.translator.tree.definitions.DefineFunction;
import org.adamalang.translator.tree.definitions.DefineHandler;
import org.adamalang.translator.tree.definitions.DefineStateTransition;
import org.adamalang.translator.tree.definitions.DefineTest;
import org.adamalang.translator.tree.definitions.ImportDocument;
import org.adamalang.translator.tree.privacy.DefineCustomPolicy;
import org.adamalang.translator.tree.types.natives.TyNativeEnum;
import org.adamalang.translator.tree.types.structures.BubbleDefinition;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.traits.IsEnum;
import org.adamalang.translator.tree.types.traits.IsStructure;

public class StringBuilderDocumentHandler implements Consumer<Token>, TopLevelDocumentHandler {
  public final StringBuilder builder = new StringBuilder();

  @Override
  public void accept(final Token token) {
    if (token.nonSemanticTokensPrior != null) {
      for (final Token b : token.nonSemanticTokensPrior) {
        accept(b);
      }
    }
    builder.append(token.text);
    if (token.nonSemanticTokensAfter != null) {
      for (final Token a : token.nonSemanticTokensAfter) {
        accept(a);
      }
    }
  }

  @Override
  public void add(final BubbleDefinition bd) {
    bd.emit(this);
  }

  @Override
  public void add(final DefineConstructor dc) {
    dc.emit(this);
  }

  @Override
  public void add(final DefineCustomPolicy customPolicy) {
    customPolicy.emit(this);
  }

  @Override
  public void add(final DefineDispatcher dd) {
    dd.emit(this);
  }

  @Override
  public void add(final DefineDocumentEvent dce) {
    dce.emit(this);
  }

  @Override
  public void add(final DefineFunction func) {
    func.emit(this);
  }

  @Override
  public void add(final DefineHandler handler) {
    handler.emit(this);
  }

  @Override
  public void add(final DefineStateTransition transition) {
    transition.emit(this);
  }

  @Override
  public void add(final DefineTest test) {
    test.emit(this);
  }

  @Override
  public void add(final FieldDefinition fd) {
    fd.emit(this);
  }

  @Override
  public void add(final ImportDocument importDocument) {
    importDocument.emit(this);
  }

  @Override
  public void add(final IsEnum storage) {
    if (storage instanceof TyNativeEnum) {
      ((TyNativeEnum) storage).emit(this);
    }
  }

  @Override
  public void add(final IsStructure storage) {
    storage.emit(this);
  }

  @Override
  public void add(final Token token) {
    accept(token);
  }
}

/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.parser;

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
import org.adamalang.translator.tree.types.structures.BubbleDefinition;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.traits.IsEnum;
import org.adamalang.translator.tree.types.traits.IsStructure;

/** the Parser will pump these messages */
public interface TopLevelDocumentHandler {
  public void add(BubbleDefinition bd);
  public void add(DefineConstructor dc);
  public void add(DefineCustomPolicy customPolicy);
  public void add(DefineDispatcher dd);
  public void add(DefineDocumentEvent dce);
  public void add(DefineFunction func);
  public void add(DefineHandler handler);
  public void add(DefineStateTransition transition);
  public void add(DefineTest test);
  public void add(FieldDefinition fd);
  public void add(ImportDocument importDocument);
  public void add(IsEnum storage);
  public void add(IsStructure storage);
  public void add(Token token);
}

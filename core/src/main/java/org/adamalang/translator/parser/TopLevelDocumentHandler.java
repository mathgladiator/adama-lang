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

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.definitions.*;
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
  public void add(AugmentViewerState avs);
  public void add(DefineRPC rpc);
}

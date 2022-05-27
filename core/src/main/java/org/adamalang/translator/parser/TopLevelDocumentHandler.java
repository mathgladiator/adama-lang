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
  void add(BubbleDefinition bd);

  void add(DefineConstructor dc);

  void add(DefineCustomPolicy customPolicy);

  void add(DefineDispatcher dd);

  void add(DefineDocumentEvent dce);

  void add(DefineFunction func);

  void add(DefineHandler handler);

  void add(DefineStateTransition transition);

  void add(DefineTest test);

  void add(FieldDefinition fd);

  void add(IsEnum storage);

  void add(IsStructure storage);

  void add(Token token);

  void add(AugmentViewerState avs);

  void add(DefineRPC rpc);

  void add(DefineStatic ds);

  void add(DefineWebGet dwg);
}

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
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.definitions.*;
import org.adamalang.translator.tree.definitions.config.DefineDocumentEvent;
import org.adamalang.translator.tree.privacy.DefineCustomPolicy;
import org.adamalang.translator.tree.types.structures.BubbleDefinition;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.ReplicationDefinition;
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

  void add(DefineWebPut dwp);

  void add(DefineWebOptions dwo);

  void add(DefineWebDelete dwd);

  void add(Include in, Scope rootScope);

  void add(LinkService link, Scope rootScope);

  void add(DefineService ds);

  void add(DefineAuthorization da);

  void add(DefinePassword dp);

  void add(ReplicationDefinition rd);

  void add(DefineMetric dm);
}

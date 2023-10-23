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
import org.adamalang.translator.tree.types.natives.TyNativeEnum;
import org.adamalang.translator.tree.types.structures.BubbleDefinition;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.ReplicationDefinition;
import org.adamalang.translator.tree.types.traits.IsEnum;
import org.adamalang.translator.tree.types.traits.IsStructure;

import java.util.function.Consumer;

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

  @Override
  public void add(AugmentViewerState avs) {
    avs.emit(this);
  }

  @Override
  public void add(DefineRPC rpc) {
    rpc.emit(this);
  }

  @Override
  public void add(DefineStatic ds) {
    ds.emit(this);
  }

  @Override
  public void add(DefineWebGet dwg) {
    dwg.emit(this);
  }

  @Override
  public void add(DefineWebPut dwp) {
    dwp.emit(this);
  }

  @Override
  public void add(DefineWebOptions dwo) {
    dwo.emit(this);
  }

  @Override
  public void add(DefineWebDelete dwd) {
    dwd.emit(this);
  }

  @Override
  public void add(Include in, Scope rootScope) {
    in.emit(this);
  }

  @Override
  public void add(LinkService link, Scope rootScope) { link.emit(this); }

  @Override
  public void add(DefineService ds) {
    ds.emit(this);
  }

  @Override
  public void add(DefineAuthorization da) {
    da.emit(this);
  }

  @Override
  public void add(DefinePassword dp) { dp.emit(this); }

  @Override
  public void add(ReplicationDefinition rd) {
    rd.emit(this);
  }

  @Override
  public void add(DefineMetric dm) {
    dm.emit(this);
  }

  @Override
  public void add(DefineAssoc da) {
    da.emit(this);
  }

  @Override
  public void add(DefineGraph dg) {
    dg.emit(this);
  }
}

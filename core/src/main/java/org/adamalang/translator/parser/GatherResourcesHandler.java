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
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.definitions.*;
import org.adamalang.translator.tree.definitions.config.DefineDocumentEvent;
import org.adamalang.translator.tree.privacy.DefineCustomPolicy;
import org.adamalang.translator.tree.types.structures.BubbleDefinition;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.JoinAssoc;
import org.adamalang.translator.tree.types.structures.ReplicationDefinition;
import org.adamalang.translator.tree.types.traits.IsEnum;
import org.adamalang.translator.tree.types.traits.IsStructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Function;

/** helper class to gather the includes and resolve them recursively */
public class GatherResourcesHandler implements TopLevelDocumentHandler {
  public final Function<String, String> resolver;
  public final HashSet<String> includes;
  public final ArrayList<String> errors;

  public GatherResourcesHandler(Function<String, String> resolver) {
    this.resolver = resolver;
    this.includes = new HashSet<>();
    this.errors = new ArrayList<>();
  }

  @Override
  public void add(BubbleDefinition bd) {

  }

  @Override
  public void add(DefineConstructor dc) {

  }

  @Override
  public void add(DefineCustomPolicy customPolicy) {

  }

  @Override
  public void add(DefineDispatcher dd) {

  }

  @Override
  public void add(DefineDocumentEvent dce) {

  }

  @Override
  public void add(DefineFunction func) {

  }

  @Override
  public void add(DefineHandler handler) {

  }

  @Override
  public void add(DefineStateTransition transition) {

  }

  @Override
  public void add(DefineTest test) {

  }

  @Override
  public void add(FieldDefinition fd) {

  }

  @Override
  public void add(IsEnum storage) {

  }

  @Override
  public void add(IsStructure storage) {

  }

  @Override
  public void add(Token token) {

  }

  @Override
  public void add(AugmentViewerState avs) {

  }

  @Override
  public void add(DefineRPC rpc) {

  }

  @Override
  public void add(DefineStatic ds) {

  }

  @Override
  public void add(DefineWebGet dwg) {

  }

  @Override
  public void add(DefineWebPut dwp) {

  }

  @Override
  public void add(DefineWebOptions dwo) {

  }

  @Override
  public void add(DefineWebDelete dwd) {

  }

  @Override
  public void add(Include in, Scope rootScope) {
    includes.add(in.import_name);
    String code = resolver.apply(in.import_name);
    if (code != null) {
      final var tokenEngine = new TokenEngine(in.import_name, code.codePoints().iterator());
      final var parser = new Parser(tokenEngine, rootScope);
      try {
        parser.document().accept(this);
      } catch (AdamaLangException ale) {
        errors.add(in.import_name + ":" + ale.getMessage());
      }
    } else {
      errors.add("could not resolve: " + in.import_name);
    }
  }

  @Override
  public void add(LinkService link, Scope rootScope) {
  }

  @Override
  public void add(DefineService ds) {
  }

  @Override
  public void add(DefineAuthorization da) {}

  @Override
  public void add(DefinePassword dp) {}

  @Override
  public void add(ReplicationDefinition rd) {}

  @Override
  public void add(DefineMetric dm) {}

  @Override
  public void add(DefineAssoc da) {
  }

  @Override
  public void add(JoinAssoc ja) {
  }

  @Override
  public void add(DefineTemplate dt) {
  }
}

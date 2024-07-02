/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
import org.adamalang.translator.tree.types.traits.IsEnum;
import org.adamalang.translator.tree.types.traits.IsStructure;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class GatherResourcesHandlerTests {

  @Test
  public void flow() {
    HashMap<String, String> includes = new HashMap<>();
    GatherResourcesHandler gi = new GatherResourcesHandler((in) -> {
      return includes.get(in);
    });
    gi.add((BubbleDefinition) null);
    gi.add((DefineConstructor) null);
    gi.add((DefineCustomPolicy) null);
    gi.add((DefineDispatcher) null);
    gi.add((DefineDocumentEvent) null);
    gi.add((DefineFunction) null);
    gi.add((DefineHandler) null);
    gi.add((DefineStateTransition) null);
    gi.add((DefineTest) null);
    gi.add((FieldDefinition) null);
    gi.add((IsEnum) null);
    gi.add((IsStructure) null);
    gi.add((Token) null);
    gi.add((AugmentViewerState) null);
    gi.add((DefineRPC) null);
    gi.add((DefineStatic) null);
    gi.add((DefineWebGet) null);
    gi.add((DefineWebPut) null);

    includes.put("bad", "public int ");
    includes.put("good", "public int x = 123;");
    includes.put("recurse", "@include good;");
    gi.add(new Include(null, new Token[]{Token.WRAP("recurse")}, null), Scope.makeRootDocument());
    Assert.assertEquals(0, gi.errors.size());
    Assert.assertTrue(gi.includes.contains("good"));
    Assert.assertTrue(gi.includes.contains("recurse"));

    gi.add(new Include(null, new Token[]{Token.WRAP("nope")}, null), Scope.makeRootDocument());
    Assert.assertEquals(1, gi.errors.size());
    gi.add(new Include(null, new Token[]{Token.WRAP("bad")}, null), Scope.makeRootDocument());
    Assert.assertEquals(2, gi.errors.size());
    gi.add((DefineService) null);
  }
}

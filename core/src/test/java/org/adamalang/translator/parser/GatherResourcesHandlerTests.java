/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.parser;

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
    gi.add(new Include(null, new Token[]{Token.WRAP("recurse")}, null));
    Assert.assertEquals(0, gi.errors.size());
    Assert.assertTrue(gi.includes.contains("good"));
    Assert.assertTrue(gi.includes.contains("recurse"));

    gi.add(new Include(null, new Token[]{Token.WRAP("nope")}, null));
    Assert.assertEquals(1, gi.errors.size());
    gi.add(new Include(null, new Token[]{Token.WRAP("bad")}, null));
    Assert.assertEquals(2, gi.errors.size());
    gi.add((DefineService) null);
  }
}

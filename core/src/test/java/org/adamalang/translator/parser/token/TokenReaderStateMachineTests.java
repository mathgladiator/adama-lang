/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.parser.token;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TokenReaderStateMachineTests {
  @Test
  public void symbol_cluster() throws Exception {
    final var list = new ArrayList<Token>();
    final var trsm = new TokenReaderStateMachine("Source", list::add);
    trsm.consume('+');
    trsm.consume('+');
    trsm.consume('+');
    trsm.consume('+');
    trsm.consume('1');
    Assert.assertEquals(4, list.size());
  }
}

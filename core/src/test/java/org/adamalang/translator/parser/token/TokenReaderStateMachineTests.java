/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.parser.token;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

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

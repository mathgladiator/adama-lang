/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.parser.token;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TokenReaderStateMachineTests {
    @Test
    public void symbol_cluster() throws Exception {
        ArrayList<Token> list = new ArrayList<>();
        TokenReaderStateMachine trsm = new TokenReaderStateMachine("Source", list::add);
        trsm.consume((int) '+');
        trsm.consume((int) '+');
        trsm.consume((int) '+');
        trsm.consume((int) '+');
        trsm.consume((int) '1');
        Assert.assertEquals(4, list.size());
    }
}

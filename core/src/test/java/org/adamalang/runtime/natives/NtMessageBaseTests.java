/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.junit.Test;

public class NtMessageBaseTests {
    @Test
    public void coverage() {
        NtMessageBase.NULL.__writeOut(new JsonStreamWriter());
    }
}

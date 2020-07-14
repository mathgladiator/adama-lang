/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.env.Environment;
import org.junit.Test;

import java.util.function.Consumer;

public class DefinitionTests {
    @Test
    public void coverage() {
        Definition df = new Definition() {
            @Override
            public void emit(Consumer<Token> yielder) {
            }

            @Override
            public void typing(Environment environment) {
            }
        };
    }
}

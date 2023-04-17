/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.parser;

import org.adamalang.translator.parser.token.TokenEngine;
import org.junit.Test;

public class ParserSanityTests {
  @Test
  public void testGithub138_a() throws Exception {
    Parser p = new Parser(new TokenEngine("source", "procedure foo(complex x) -> double { return x.re();//\n}".codePoints().iterator()));
    p.document();
  }

  @Test
  public void testGithub138_b() throws Exception {
    Parser p = new Parser(new TokenEngine("source", "procedure foo(complex x) -> double { return x.re();/* yay */\n}".codePoints().iterator()));
    p.document();
  }
}

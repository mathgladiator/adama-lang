/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class EndToEnd_SpaceTests {
  @Test
  public void flow() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      String alice = fe.generateIdentity("alice@x.com");
      String bob = fe.generateIdentity("bob@x.com");

      Iterator<String> c1 =
          fe.execute(
              "{\"id\":1,\"identity\":\""
                  + alice
                  + "\",\"method\":\"space/create\",\"space\":\"spacename\"}");
      Assert.assertEquals("FINISH:{}", c1.next());

      Iterator<String> c2 =
          fe.execute("{\"id\":2,\"identity\":\"" + alice + "\",\"method\":\"space/list\"}");
      Assert.assertEquals(
          "STREAM:{\"space\":\"spacename\",\"role\":\"owner\",\"billing\":\"free\",\"created\":",
          c2.next().substring(0, 70));
      Assert.assertEquals("FINISH:{}", c2.next());

      Iterator<String> c3 =
          fe.execute("{\"id\":3,\"identity\":\"" + bob + "\",\"method\":\"space/list\"}");
      Assert.assertEquals("FINISH:{}", c3.next());

      Iterator<String> c4 =
          fe.execute(
              "{\"id\":4,\"identity\":\""
                  + alice
                  + "\",\"method\":\"space/set-role\",\"space\":\"spacename\",\"email\":\"bob@x.com\",\"role\":\"developer\"}");
      Assert.assertEquals("FINISH:{}", c4.next());

      Iterator<String> c5 =
          fe.execute("{\"id\":5,\"identity\":\"" + bob + "\",\"method\":\"space/list\"}");
      Assert.assertEquals(
          "STREAM:{\"space\":\"spacename\",\"role\":\"developer\",\"billing\":\"free\",\"created\":",
          c5.next().substring(0, 74));
      Assert.assertEquals("FINISH:{}", c5.next());

      Iterator<String> c6 =
          fe.execute(
              "{\"id\":6,\"identity\":\""
                  + alice
                  + "\",\"method\":\"space/set-role\",\"space\":\"spacename\",\"email\":\"bob@x.com\",\"role\":\"none\"}");
      Assert.assertEquals("FINISH:{}", c6.next());

      Iterator<String> c7 =
          fe.execute("{\"id\":7,\"identity\":\"" + bob + "\",\"method\":\"space/list\"}");
      Assert.assertEquals("FINISH:{}", c7.next());
    }
  }
}

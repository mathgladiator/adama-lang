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

import org.adamalang.transforms.UserIdResolver;
import org.adamalang.transforms.results.Keystore;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class EndToEndDB_FailureTests {
  @Test
  public void flow() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      String identity = fe.setupDevIdentity();
      fe.kill("authorities");
      fe.kill("spaces");
      fe.kill("initiations");
      Iterator<String> c3 = fe.execute("{\"id\":3,\"method\":\"authority/create\",\"identity\":\"" + identity + "\"}");
      Assert.assertEquals("ERROR:982016", c3.next());
      Keystore ks = Keystore.parse("{}");
      String privateKeyFile = ks.generate("xyz");
      Iterator<String> c4 = fe.execute("{\"id\":6,\"method\":\"authority/set\",\"identity\":\"" + identity + "\",\"authority\":\"nope\",\"key-store\":"+ks.persist()+"}");
      Assert.assertEquals("ERROR:900098", c4.next());
      Iterator<String> c5 = fe.execute("{\"id\":6,\"method\":\"authority/get\",\"identity\":\"" + identity + "\",\"authority\":\"x\"}");
      Assert.assertEquals("ERROR:928819", c5.next());
      Iterator<String> c6 = fe.execute("{\"id\":4,\"method\":\"authority/list\",\"identity\":\"" + identity + "\"}");
      Assert.assertEquals("ERROR:998430", c6.next());
      Iterator<String> c7 = fe.execute("{\"id\":1,\"identity\":\""+ identity + "\",\"method\":\"space/create\",\"space\":\"spacename\"}");
      Assert.assertEquals("ERROR:900104", c7.next());
      Iterator<String> c8 = fe.execute("{\"id\":6,\"method\":\"authority/destroy\",\"identity\":\"" + identity + "\",\"authority\":\"x\"}");
      Assert.assertEquals("ERROR:913436", c8.next());
      Iterator<String> c9 = fe.execute("{\"id\":7,\"method\":\"nooop\",\"identity\":\"" + identity + "\",\"authority\":\"x\"}");
      Assert.assertEquals("ERROR:945213", c9.next());
      Iterator<String> c10 = fe.execute("{\"id\":7,\"method\":\"init/setup-account\",\"identity\":\"" + identity + "\",\"email\":\"x@x.com\"}");
      Assert.assertEquals("ERROR:965636", c10.next());
      Iterator<String> c11 = fe.execute("{\"id\":7,\"method\":\"init/complete-account\",\"identity\":\"" + identity + "\",\"email\":\"x@x.com\",\"code\":\"42\"}");
      Assert.assertEquals("ERROR:946179", c11.next());
      fe.kill("emails");
      Iterator<String> c12 = fe.execute("{\"id\":7,\"method\":\"init/setup-account\",\"identity\":\"" + identity + "\",\"email\":\"x@x.com\"}");
      Assert.assertEquals("ERROR:979980", c12.next());
    }
  }
}

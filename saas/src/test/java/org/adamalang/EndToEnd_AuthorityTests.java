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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.adamalang.common.Json;
import org.adamalang.transforms.results.Keystore;
import org.junit.Assert;
import org.junit.Test;

import java.security.PrivateKey;
import java.util.Iterator;
import java.util.regex.Pattern;

public class EndToEnd_AuthorityTests {
  @Test
  public void flow() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      Iterator<String> c0 = fe.execute("{}");
      Assert.assertEquals("ERROR:233120", c0.next());
      Runnable latch1 = fe.latchOnEmail("x@x.com");
      Iterator<String> c1 =
          fe.execute("{\"id\":1,\"method\":\"init/start\",\"email\":\"x@x.com\"}");
      latch1.run();
      Iterator<String> c2 =
          fe.execute(
              "{\"id\":2,\"connection\":1,\"method\":\"init/generate-identity\",\"code\":\""
                  + fe.codesSentToEmail.get("x@x.com")
                  + "\"}");
      String result1 = c2.next();
      Assert.assertTrue(result1.length() > 0);
      Assert.assertEquals("FINISH:{\"identity\":", result1.substring(0, 19));
      String identity1 = Json.parseJsonObject(result1.substring(7)).get("identity").textValue();
      Assert.assertEquals("FINISH:{}", c1.next());
      Iterator<String> c3 =
          fe.execute("{\"id\":3,\"method\":\"authority/create\",\"identity\":\"" + identity1 + "\"}");
      String authorityCreatedLog = c3.next();
      Assert.assertTrue(authorityCreatedLog.startsWith("FINISH:{\"authority\":\""));
      String authority = authorityCreatedLog.split(Pattern.quote("\""))[3];
      Iterator<String> c4 =
          fe.execute("{\"id\":4,\"method\":\"authority/list\",\"identity\":\"" + identity1 + "\"}");
      Assert.assertEquals("STREAM:{\"authority\":\""+authority+"\"}", c4.next());
      Iterator<String> c5 =
          fe.execute("{\"id\":5,\"method\":\"authority/destroy\",\"authority\":\""+authority+"\",\"identity\":\"" + identity1 + "\"}");
      Assert.assertEquals("FINISH:{}", c5.next());
      Iterator<String> c6 =
          fe.execute("{\"id\":6,\"method\":\"authority/create\",\"identity\":\"" + identity1 + "\"}");
      authorityCreatedLog = c6.next();
      authority = authorityCreatedLog.split(Pattern.quote("\""))[3];
      Assert.assertTrue(authorityCreatedLog.startsWith("FINISH:{\"authority\":\""));
      Iterator<String> c7 = fe.execute("{\"id\":6,\"method\":\"authority/set\",\"identity\":\"" + identity1 + "\",\"authority\":\"nope\",\"keystore\":\"\"}");
      Assert.assertEquals("ERROR:457743", c7.next());
      Iterator<String> c8 = fe.execute("{\"id\":6,\"method\":\"authority/set\",\"identity\":\"" + identity1 + "\",\"authority\":\""+authority+"\",\"keystore\":\"{}\"}");
      Assert.assertEquals("ERROR:457743", c8.next());
      Iterator<String> c9 = fe.execute("{\"id\":6,\"method\":\"authority/get\",\"identity\":\"" + identity1 + "\",\"authority\":\""+authority+"\"}");
      Assert.assertEquals("FINISH:{\"keystore\":{}}", c9.next());
      Keystore ks = Keystore.parse("{}");
      String privateKeyFile = ks.generate(authority);
      Iterator<String> c10 = fe.execute("{\"id\":6,\"method\":\"authority/set\",\"identity\":\"" + identity1 + "\",\"authority\":\""+authority+"\",\"key-store\":"+ks.persist()+"}");
      Assert.assertEquals("FINISH:{}", c10.next());
      PrivateKey key = Keystore.parsePrivateKey(Json.parseJsonObject(privateKeyFile));
      String newIdentity = Jwts.builder().setSubject("me").setIssuer(authority).signWith(key).compact();
      Iterator<String> c11 = fe.execute("{\"id\":3,\"method\":\"authority/create\",\"identity\":\"" + newIdentity + "\"}");
      Assert.assertEquals("ERROR:990208", c11.next());
      Iterator<String> c12 = fe.execute("{\"id\":3,\"method\":\"authority/list\",\"identity\":\"" + newIdentity + "\"}");
      Assert.assertEquals("ERROR:904223", c12.next());
      Iterator<String> c13 = fe.execute("{\"id\":6,\"method\":\"authority/set\",\"identity\":\"" + newIdentity + "\",\"authority\":\""+authority+"\",\"key-store\":"+ks.persist()+"}");
      Assert.assertEquals("ERROR:970780", c13.next());
      Iterator<String> c14 = fe.execute("{\"id\":6,\"method\":\"authority/get\",\"identity\":\"" + newIdentity + "\",\"authority\":\""+authority+"\"}");
      Assert.assertEquals("ERROR:978992", c14.next());
      Iterator<String> c15 = fe.execute("{\"id\":6,\"method\":\"authority/destroy\",\"identity\":\"" + newIdentity + "\",\"authority\":\""+authority+"\"}");
      Assert.assertEquals("ERROR:901144", c15.next());
    }
  }
}

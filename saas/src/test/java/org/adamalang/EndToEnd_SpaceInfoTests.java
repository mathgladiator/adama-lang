/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang;

import io.jsonwebtoken.Jwts;
import org.adamalang.common.Json;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.security.Keystore;
import org.junit.Assert;
import org.junit.Test;

import java.security.PrivateKey;
import java.util.Iterator;
import java.util.regex.Pattern;

public class EndToEnd_SpaceInfoTests {
  public static String planFor(String code) {
    JsonStreamWriter planWriter = new JsonStreamWriter();
    planWriter.beginObject();
    planWriter.writeObjectFieldIntro("versions");
    planWriter.beginObject();
    planWriter.writeObjectFieldIntro("x");
    planWriter.writeString(code);
    planWriter.endObject();
    planWriter.writeObjectFieldIntro("default");
    planWriter.writeString("x");
    planWriter.endObject();
    return planWriter.toString();
  }

  @Test
  public void flow() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      String alice = fe.generateIdentity("alice@x.com", false);
      String bob = fe.generateIdentity("bob@x.com", true);
      Iterator<String> c1 =
          fe.execute(
              "{\"id\":1,\"identity\":\""
                  + alice
                  + "\",\"method\":\"space/create\",\"space\":\"spacename\"}");
      Assert.assertEquals("FINISH:{}", c1.next());
      Iterator<String> c2 =
          fe.execute("{\"id\":2,\"identity\":\"" + alice + "\",\"method\":\"space/list\"}");
      Assert.assertEquals(
          "STREAM:{\"space\":\"spacename\",\"role\":\"owner\",\"created\":\"",
          c2.next().substring(0, 54));
      Assert.assertEquals("FINISH:null", c2.next());
      Iterator<String> c3 =
          fe.execute("{\"id\":3,\"identity\":\"" + bob + "\",\"method\":\"space/list\"}");
      Assert.assertEquals("FINISH:null", c3.next());
      Iterator<String> c4 =
          fe.execute(
              "{\"id\":4,\"identity\":\""
                  + alice
                  + "\",\"method\":\"space/set-role\",\"space\":\"spacename\",\"email\":\"bob@x.com\",\"role\":\"developer\"}");
      Assert.assertEquals("FINISH:{}", c4.next());
      Iterator<String> c5 =
          fe.execute("{\"id\":5,\"identity\":\"" + bob + "\",\"method\":\"space/list\"}");
      Assert.assertEquals(
          "STREAM:{\"space\":\"spacename\",\"role\":\"developer\",\"created\":\"",
          c5.next().substring(0, 58));
      Assert.assertEquals("FINISH:null", c5.next());
      Iterator<String> c6 =
          fe.execute(
              "{\"id\":6,\"identity\":\""
                  + alice
                  + "\",\"method\":\"space/set-role\",\"space\":\"spacename\",\"email\":\"bob@x.com\",\"role\":\"none\"}");
      Assert.assertEquals("FINISH:{}", c6.next());
      Iterator<String> c7 =
          fe.execute("{\"id\":7,\"identity\":\"" + bob + "\",\"method\":\"space/list\"}");
      Assert.assertEquals("FINISH:null", c7.next());
      Iterator<String> c8 =
          fe.execute("{\"id\":6,\"method\":\"authority/create\",\"identity\":\"" + bob + "\"}");
      String authorityCreatedLog = c8.next();
      String authority = authorityCreatedLog.split(Pattern.quote("\""))[3];
      Keystore ks = Keystore.parse("{}");
      String privateKeyFile = ks.generate(authority);
      Iterator<String> c9 = fe.execute("{\"id\":6,\"method\":\"authority/set\",\"identity\":\"" + bob + "\",\"authority\":\""+authority+"\",\"key-store\":"+ks.persist()+"}");
      Assert.assertEquals("FINISH:{}", c9.next());
      PrivateKey key = Keystore.parsePrivateKey(Json.parseJsonObject(privateKeyFile));
      String userIdentity = Jwts.builder().subject("me").issuer(authority).signWith(key).compact();
      Iterator<String> c10 =
          fe.execute("{\"id\":7,\"identity\":\"" + userIdentity + "\",\"method\":\"space/list\"}");
      Assert.assertEquals("ERROR:920576", c10.next());
      Iterator<String> c11  =
          fe.execute("{\"id\":7,\"identity\":\"" + userIdentity + "\",\"method\":\"space/create\",\"space\":\"newspace\"}");
      Assert.assertEquals("ERROR:998384", c11.next());
      Iterator<String> c12  =
          fe.execute("{\"id\":7,\"identity\":\"" + alice + "\",\"method\":\"space/create\",\"space\":\"myspace\"}");
      Assert.assertEquals("FINISH:{}", c12.next());
      Iterator<String> c13  =
          fe.execute("{\"id\":7,\"identity\":\"" + userIdentity + "\",\"method\":\"space/get\",\"space\":\"myspace\"}");
      Assert.assertEquals("ERROR:965635", c13.next());
      Iterator<String> c14  =
          fe.execute("{\"id\":7,\"identity\":\"" + alice + "\",\"method\":\"space/get\",\"space\":\"myspace\"}");
      Assert.assertTrue(c14.next().startsWith("FINISH:{\"plan\":{"));
      Iterator<String> c15  =
          fe.execute("{\"id\":7,\"identity\":\"" + alice + "\",\"method\":\"space/set\",\"space\":\"myspace\",\"plan\":"+planFor("@static { create { return true; } } ")+ "}");
      Assert.assertEquals("FINISH:{}", c15.next());
      Iterator<String> c16  =
          fe.execute("{\"id\":7,\"identity\":\"" + userIdentity + "\",\"method\":\"space/set\",\"space\":\"myspace\",\"plan\":"+planFor("@static { create { return true; } } ")+ "}");
      Assert.assertEquals("ERROR:901127", c16.next());
      Iterator<String> c17  =
          fe.execute("{\"id\":7,\"identity\":\"" + alice + "\",\"method\":\"space/get\",\"space\":\"myspace\"}");
      Assert.assertEquals("FINISH:{\"plan\":{\"versions\":{\"x\":\"@static { create { return true; } } \"},\"default\":\"x\"}}", c17.next());
      Iterator<String> c18  =
          fe.execute("{\"id\":7,\"identity\":\"" + alice + "\",\"method\":\"space/reflect\",\"space\":\"myspace\",\"key\":\"k\"}");
      Assert.assertEquals("FINISH:{\"reflection\":{\"types\":{\"__Root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}}},\"channels\":{},\"channels-privacy\":{},\"constructors\":[],\"labels\":[]}}", c18.next());
      Iterator<String> c19  =
          fe.execute("{\"id\":7,\"identity\":\"" + alice + "\",\"method\":\"space/reflect\",\"space\":\"nope\",\"key\":\"k\"}");
      Assert.assertEquals("ERROR:625678", c19.next());
      Iterator<String> c21  =
          fe.execute("{\"id\":7,\"identity\":\"" + alice + "\",\"method\":\"space/delete\",\"space\":\"myspace\"}");
      Assert.assertEquals("FINISH:{}", c21.next());
      Iterator<String> c22 = fe.execute("{\"id\":1,\"identity\":\"" + alice+ "\",\"method\":\"space/create\",\"space\":\"ide\"}");
      Assert.assertEquals("ERROR:947404", c22.next());
      Iterator<String> c23 = fe.execute("{\"id\":1,\"identity\":\"" + alice+ "\",\"method\":\"space/create\",\"space\":\"wildcard\"}");
      Assert.assertEquals("ERROR:947404", c23.next());
      Iterator<String> c24 = fe.execute("{\"id\":1,\"identity\":\"" + alice+ "\",\"method\":\"deinit\"}");
      Assert.assertEquals("ERROR:999665", c24.next());
      Iterator<String> c25  =
          fe.execute("{\"id\":7,\"identity\":\"" + alice + "\",\"method\":\"space/delete\",\"space\":\"spacename\"}");
      Assert.assertEquals("FINISH:{}", c25.next());
      Iterator<String> c26 =
          fe.execute("{\"id\":2,\"identity\":\"" + alice + "\",\"method\":\"space/list\"}");
      Assert.assertEquals("FINISH:null", c26.next());
      Iterator<String> c27 = fe.execute("{\"id\":1,\"identity\":\"" + alice+ "\",\"method\":\"deinit\"}");
      Assert.assertEquals("FINISH:{}", c27.next());
    }
  }
}

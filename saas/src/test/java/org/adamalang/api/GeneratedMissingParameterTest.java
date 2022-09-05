/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.TestFrontEnd;
import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class GeneratedMissingParameterTest {
  @Test
  public void missing() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      ObjectNode node;
      String _identity = fe.setupDevIdentity();
      //InitSetupAccount
      node = Json.newJsonObject();
      node.put("id", 1);
      node.put("method", "init/setup-account");
      Iterator<String> c2 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:473103", c2.next());
      node.put("email", "x@x.com");
      //InitConvertGoogleUser
      node = Json.newJsonObject();
      node.put("id", 2);
      node.put("method", "init/convert-google-user");
      Iterator<String> c3 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:407544", c3.next());
      node.put("access-token", "xzya");
      //InitCompleteAccount
      node = Json.newJsonObject();
      node.put("id", 3);
      node.put("method", "init/complete-account");
      Iterator<String> c4 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:473103", c4.next());
      node.put("email", "x@x.com");
      Iterator<String> c5 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:455681", c5.next());
      node.put("code", "xzya");
      //AccountSetPassword
      node = Json.newJsonObject();
      node.put("id", 5);
      node.put("method", "account/set-password");
      Iterator<String> c6 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c6.next());
      node.put("identity", _identity);
      Iterator<String> c7 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c7.next());
      node.put("password", "xzya");
      //AccountLogin
      node = Json.newJsonObject();
      node.put("id", 7);
      node.put("method", "account/login");
      Iterator<String> c8 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:473103", c8.next());
      node.put("email", "x@x.com");
      Iterator<String> c9 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c9.next());
      node.put("password", "xzya");
      //Probe
      node = Json.newJsonObject();
      node.put("id", 9);
      node.put("method", "probe");
      Iterator<String> c10 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c10.next());
      node.put("identity", _identity);
      //AuthorityCreate
      node = Json.newJsonObject();
      node.put("id", 10);
      node.put("method", "authority/create");
      Iterator<String> c11 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c11.next());
      node.put("identity", _identity);
      //AuthoritySet
      node = Json.newJsonObject();
      node.put("id", 11);
      node.put("method", "authority/set");
      Iterator<String> c12 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c12.next());
      node.put("identity", _identity);
      Iterator<String> c13 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:430095", c13.next());
      node.put("authority", "xzya");
      Iterator<String> c14 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:457743", c14.next());
      node.put("key-store", Json.newJsonObject());
      //AuthorityGet
      node = Json.newJsonObject();
      node.put("id", 14);
      node.put("method", "authority/get");
      Iterator<String> c15 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c15.next());
      node.put("identity", _identity);
      Iterator<String> c16 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:430095", c16.next());
      node.put("authority", "xzya");
      //AuthorityList
      node = Json.newJsonObject();
      node.put("id", 16);
      node.put("method", "authority/list");
      Iterator<String> c17 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c17.next());
      node.put("identity", _identity);
      //AuthorityDestroy
      node = Json.newJsonObject();
      node.put("id", 17);
      node.put("method", "authority/destroy");
      Iterator<String> c18 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c18.next());
      node.put("identity", _identity);
      Iterator<String> c19 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:430095", c19.next());
      node.put("authority", "xzya");
      //SpaceCreate
      node = Json.newJsonObject();
      node.put("id", 19);
      node.put("method", "space/create");
      Iterator<String> c20 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c20.next());
      node.put("identity", _identity);
      Iterator<String> c21 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c21.next());
      node.put("space", "xzya");
      //SpaceGenerateKey
      node = Json.newJsonObject();
      node.put("id", 21);
      node.put("method", "space/generate-key");
      Iterator<String> c22 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c22.next());
      node.put("identity", _identity);
      Iterator<String> c23 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c23.next());
      node.put("space", "xzya");
      //SpaceUsage
      node = Json.newJsonObject();
      node.put("id", 23);
      node.put("method", "space/usage");
      Iterator<String> c24 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c24.next());
      node.put("identity", _identity);
      Iterator<String> c25 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c25.next());
      node.put("space", "xzya");
      //SpaceGet
      node = Json.newJsonObject();
      node.put("id", 25);
      node.put("method", "space/get");
      Iterator<String> c26 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c26.next());
      node.put("identity", _identity);
      Iterator<String> c27 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c27.next());
      node.put("space", "xzya");
      //SpaceSet
      node = Json.newJsonObject();
      node.put("id", 27);
      node.put("method", "space/set");
      Iterator<String> c28 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c28.next());
      node.put("identity", _identity);
      Iterator<String> c29 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c29.next());
      node.put("space", "xzya");
      Iterator<String> c30 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425999", c30.next());
      node.put("plan", Json.newJsonObject());
      //SpaceSetRxhtml
      node = Json.newJsonObject();
      node.put("id", 30);
      node.put("method", "space/set-rxhtml");
      Iterator<String> c31 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c31.next());
      node.put("identity", _identity);
      Iterator<String> c32 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c32.next());
      node.put("space", "xzya");
      Iterator<String> c33 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:402428", c33.next());
      node.put("rxhtml", "xzya");
      //SpaceGetRxhtml
      node = Json.newJsonObject();
      node.put("id", 33);
      node.put("method", "space/get-rxhtml");
      Iterator<String> c34 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c34.next());
      node.put("identity", _identity);
      Iterator<String> c35 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c35.next());
      node.put("space", "xzya");
      //SpaceDelete
      node = Json.newJsonObject();
      node.put("id", 35);
      node.put("method", "space/delete");
      Iterator<String> c36 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c36.next());
      node.put("identity", _identity);
      Iterator<String> c37 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c37.next());
      node.put("space", "xzya");
      //SpaceSetRole
      node = Json.newJsonObject();
      node.put("id", 37);
      node.put("method", "space/set-role");
      Iterator<String> c38 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c38.next());
      node.put("identity", _identity);
      Iterator<String> c39 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c39.next());
      node.put("space", "xzya");
      Iterator<String> c40 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:473103", c40.next());
      node.put("email", "x@x.com");
      Iterator<String> c41 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:456716", c41.next());
      node.put("role", "xzya");
      //SpaceReflect
      node = Json.newJsonObject();
      node.put("id", 41);
      node.put("method", "space/reflect");
      Iterator<String> c42 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c42.next());
      node.put("identity", _identity);
      Iterator<String> c43 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c43.next());
      node.put("space", "xzya");
      Iterator<String> c44 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c44.next());
      node.put("key", "xzya");
      //SpaceList
      node = Json.newJsonObject();
      node.put("id", 44);
      node.put("method", "space/list");
      Iterator<String> c45 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c45.next());
      node.put("identity", _identity);
      //DomainMap
      node = Json.newJsonObject();
      node.put("id", 45);
      node.put("method", "domain/map");
      Iterator<String> c46 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c46.next());
      node.put("identity", _identity);
      Iterator<String> c47 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c47.next());
      node.put("domain", "xzya");
      Iterator<String> c48 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c48.next());
      node.put("space", "xzya");
      Iterator<String> c49 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:428028", c49.next());
      node.put("certificate", "xzya");
      //DomainUnmap
      node = Json.newJsonObject();
      node.put("id", 49);
      node.put("method", "domain/unmap");
      Iterator<String> c50 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c50.next());
      node.put("identity", _identity);
      Iterator<String> c51 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c51.next());
      node.put("domain", "xzya");
      //DomainGet
      node = Json.newJsonObject();
      node.put("id", 51);
      node.put("method", "domain/get");
      Iterator<String> c52 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c52.next());
      node.put("identity", _identity);
      Iterator<String> c53 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c53.next());
      node.put("domain", "xzya");
      //DocumentCreate
      node = Json.newJsonObject();
      node.put("id", 53);
      node.put("method", "document/create");
      Iterator<String> c54 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c54.next());
      node.put("identity", _identity);
      Iterator<String> c55 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c55.next());
      node.put("space", "xzya");
      Iterator<String> c56 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c56.next());
      node.put("key", "xzya");
      Iterator<String> c57 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461826", c57.next());
      node.put("arg", Json.newJsonObject());
      //DocumentList
      node = Json.newJsonObject();
      node.put("id", 57);
      node.put("method", "document/list");
      Iterator<String> c58 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c58.next());
      node.put("identity", _identity);
      Iterator<String> c59 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c59.next());
      node.put("space", "xzya");
      //ConnectionCreate
      node = Json.newJsonObject();
      node.put("id", 59);
      node.put("method", "connection/create");
      Iterator<String> c60 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c60.next());
      node.put("identity", _identity);
      Iterator<String> c61 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c61.next());
      node.put("space", "xzya");
      Iterator<String> c62 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c62.next());
      node.put("key", "xzya");
      //ConnectionSend
      node = Json.newJsonObject();
      node.put("id", 62);
      node.put("method", "connection/send");
      Iterator<String> c63 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c63.next());
      node.put("connection", 100L);
      Iterator<String> c64 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:454659", c64.next());
      node.put("channel", "xzya");
      Iterator<String> c65 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425987", c65.next());
      node.put("message", Json.newJsonObject());
      //ConnectionSendOnce
      node = Json.newJsonObject();
      node.put("id", 65);
      node.put("method", "connection/send-once");
      Iterator<String> c66 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c66.next());
      node.put("connection", 100L);
      Iterator<String> c67 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:454659", c67.next());
      node.put("channel", "xzya");
      Iterator<String> c68 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425987", c68.next());
      node.put("message", Json.newJsonObject());
      //ConnectionCanAttach
      node = Json.newJsonObject();
      node.put("id", 68);
      node.put("method", "connection/can-attach");
      Iterator<String> c69 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c69.next());
      node.put("connection", 100L);
      //ConnectionAttach
      node = Json.newJsonObject();
      node.put("id", 69);
      node.put("method", "connection/attach");
      Iterator<String> c70 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c70.next());
      node.put("connection", 100L);
      Iterator<String> c71 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:476156", c71.next());
      node.put("asset-id", "xzya");
      Iterator<String> c72 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:470028", c72.next());
      node.put("filename", "xzya");
      Iterator<String> c73 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:455691", c73.next());
      node.put("content-type", "xzya");
      Iterator<String> c74 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:477179", c74.next());
      node.put("size", 100L);
      Iterator<String> c75 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:445437", c75.next());
      node.put("digest-md5", "xzya");
      Iterator<String> c76 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:406525", c76.next());
      node.put("digest-sha384", "xzya");
      //ConnectionUpdate
      node = Json.newJsonObject();
      node.put("id", 76);
      node.put("method", "connection/update");
      Iterator<String> c77 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c77.next());
      node.put("connection", 100L);
      //ConnectionEnd
      node = Json.newJsonObject();
      node.put("id", 77);
      node.put("method", "connection/end");
      Iterator<String> c78 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c78.next());
      node.put("connection", 100L);
      //ConfigureMakeOrGetAssetKey
      node = Json.newJsonObject();
      node.put("id", 78);
      node.put("method", "configure/make-or-get-asset-key");
      //AttachmentStart
      node = Json.newJsonObject();
      node.put("id", 78);
      node.put("method", "attachment/start");
      Iterator<String> c79 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c79.next());
      node.put("identity", _identity);
      Iterator<String> c80 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c80.next());
      node.put("space", "xzya");
      Iterator<String> c81 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c81.next());
      node.put("key", "xzya");
      Iterator<String> c82 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:470028", c82.next());
      node.put("filename", "xzya");
      Iterator<String> c83 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:455691", c83.next());
      node.put("content-type", "xzya");
      //AttachmentAppend
      node = Json.newJsonObject();
      node.put("id", 83);
      node.put("method", "attachment/append");
      Iterator<String> c84 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:409609", c84.next());
      node.put("upload", 100L);
      Iterator<String> c85 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:462859", c85.next());
      node.put("chunk-md5", "xzya");
      Iterator<String> c86 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:409608", c86.next());
      node.put("base64-bytes", "xzya");
      //AttachmentFinish
      node = Json.newJsonObject();
      node.put("id", 86);
      node.put("method", "attachment/finish");
      Iterator<String> c87 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:409609", c87.next());
      node.put("upload", 100L);
    }
  }
}

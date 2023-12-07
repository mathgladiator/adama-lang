/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
      //Deinit
      node = Json.newJsonObject();
      node.put("id", 5);
      node.put("method", "deinit");
      Iterator<String> c6 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c6.next());
      node.put("identity", _identity);
      //AccountSetPassword
      node = Json.newJsonObject();
      node.put("id", 6);
      node.put("method", "account/set-password");
      Iterator<String> c7 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c7.next());
      node.put("identity", _identity);
      Iterator<String> c8 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c8.next());
      node.put("password", "xzya");
      //AccountGetPaymentPlan
      node = Json.newJsonObject();
      node.put("id", 8);
      node.put("method", "account/get-payment-plan");
      Iterator<String> c9 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c9.next());
      node.put("identity", _identity);
      //AccountLogin
      node = Json.newJsonObject();
      node.put("id", 9);
      node.put("method", "account/login");
      Iterator<String> c10 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:473103", c10.next());
      node.put("email", "x@x.com");
      Iterator<String> c11 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c11.next());
      node.put("password", "xzya");
      //Probe
      node = Json.newJsonObject();
      node.put("id", 11);
      node.put("method", "probe");
      Iterator<String> c12 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c12.next());
      node.put("identity", _identity);
      //Stats
      node = Json.newJsonObject();
      node.put("id", 12);
      node.put("method", "stats");
      //IdentityHash
      node = Json.newJsonObject();
      node.put("id", 12);
      node.put("method", "identity/hash");
      Iterator<String> c13 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c13.next());
      node.put("identity", _identity);
      //IdentityStash
      node = Json.newJsonObject();
      node.put("id", 13);
      node.put("method", "identity/stash");
      Iterator<String> c14 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c14.next());
      node.put("identity", _identity);
      Iterator<String> c15 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:453647", c15.next());
      node.put("name", "xzya");
      //AuthorityCreate
      node = Json.newJsonObject();
      node.put("id", 15);
      node.put("method", "authority/create");
      Iterator<String> c16 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c16.next());
      node.put("identity", _identity);
      //AuthoritySet
      node = Json.newJsonObject();
      node.put("id", 16);
      node.put("method", "authority/set");
      Iterator<String> c17 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c17.next());
      node.put("identity", _identity);
      Iterator<String> c18 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:430095", c18.next());
      node.put("authority", "xzya");
      Iterator<String> c19 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:457743", c19.next());
      node.put("key-store", Json.newJsonObject());
      //AuthorityGet
      node = Json.newJsonObject();
      node.put("id", 19);
      node.put("method", "authority/get");
      Iterator<String> c20 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c20.next());
      node.put("identity", _identity);
      Iterator<String> c21 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:430095", c21.next());
      node.put("authority", "xzya");
      //AuthorityList
      node = Json.newJsonObject();
      node.put("id", 21);
      node.put("method", "authority/list");
      Iterator<String> c22 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c22.next());
      node.put("identity", _identity);
      //AuthorityDestroy
      node = Json.newJsonObject();
      node.put("id", 22);
      node.put("method", "authority/destroy");
      Iterator<String> c23 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c23.next());
      node.put("identity", _identity);
      Iterator<String> c24 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:430095", c24.next());
      node.put("authority", "xzya");
      //SpaceCreate
      node = Json.newJsonObject();
      node.put("id", 24);
      node.put("method", "space/create");
      Iterator<String> c25 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c25.next());
      node.put("identity", _identity);
      Iterator<String> c26 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c26.next());
      node.put("space", "xzya");
      //SpaceGenerateKey
      node = Json.newJsonObject();
      node.put("id", 26);
      node.put("method", "space/generate-key");
      Iterator<String> c27 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c27.next());
      node.put("identity", _identity);
      Iterator<String> c28 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c28.next());
      node.put("space", "xzya");
      //SpaceGet
      node = Json.newJsonObject();
      node.put("id", 28);
      node.put("method", "space/get");
      Iterator<String> c29 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c29.next());
      node.put("identity", _identity);
      Iterator<String> c30 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c30.next());
      node.put("space", "xzya");
      //SpaceSet
      node = Json.newJsonObject();
      node.put("id", 30);
      node.put("method", "space/set");
      Iterator<String> c31 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c31.next());
      node.put("identity", _identity);
      Iterator<String> c32 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c32.next());
      node.put("space", "xzya");
      Iterator<String> c33 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425999", c33.next());
      node.put("plan", Json.newJsonObject());
      //SpaceRedeployKick
      node = Json.newJsonObject();
      node.put("id", 33);
      node.put("method", "space/redeploy-kick");
      Iterator<String> c34 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c34.next());
      node.put("identity", _identity);
      Iterator<String> c35 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c35.next());
      node.put("space", "xzya");
      //SpaceSetRxhtml
      node = Json.newJsonObject();
      node.put("id", 35);
      node.put("method", "space/set-rxhtml");
      Iterator<String> c36 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c36.next());
      node.put("identity", _identity);
      Iterator<String> c37 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c37.next());
      node.put("space", "xzya");
      Iterator<String> c38 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:402428", c38.next());
      node.put("rxhtml", "xzya");
      //SpaceGetRxhtml
      node = Json.newJsonObject();
      node.put("id", 38);
      node.put("method", "space/get-rxhtml");
      Iterator<String> c39 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c39.next());
      node.put("identity", _identity);
      Iterator<String> c40 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c40.next());
      node.put("space", "xzya");
      //SpaceSetPolicy
      node = Json.newJsonObject();
      node.put("id", 40);
      node.put("method", "space/set-policy");
      Iterator<String> c41 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c41.next());
      node.put("identity", _identity);
      Iterator<String> c42 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c42.next());
      node.put("space", "xzya");
      Iterator<String> c43 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:402255", c43.next());
      node.put("access-policy", Json.newJsonObject());
      //SpaceGetPolicy
      node = Json.newJsonObject();
      node.put("id", 43);
      node.put("method", "space/get-policy");
      Iterator<String> c44 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c44.next());
      node.put("identity", _identity);
      Iterator<String> c45 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c45.next());
      node.put("space", "xzya");
      //SpaceMetrics
      node = Json.newJsonObject();
      node.put("id", 45);
      node.put("method", "space/metrics");
      Iterator<String> c46 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c46.next());
      node.put("identity", _identity);
      Iterator<String> c47 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c47.next());
      node.put("space", "xzya");
      //SpaceDelete
      node = Json.newJsonObject();
      node.put("id", 47);
      node.put("method", "space/delete");
      Iterator<String> c48 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c48.next());
      node.put("identity", _identity);
      Iterator<String> c49 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c49.next());
      node.put("space", "xzya");
      //SpaceSetRole
      node = Json.newJsonObject();
      node.put("id", 49);
      node.put("method", "space/set-role");
      Iterator<String> c50 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c50.next());
      node.put("identity", _identity);
      Iterator<String> c51 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c51.next());
      node.put("space", "xzya");
      Iterator<String> c52 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:473103", c52.next());
      node.put("email", "x@x.com");
      Iterator<String> c53 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:456716", c53.next());
      node.put("role", "xzya");
      //SpaceListDevelopers
      node = Json.newJsonObject();
      node.put("id", 53);
      node.put("method", "space/list-developers");
      Iterator<String> c54 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c54.next());
      node.put("identity", _identity);
      Iterator<String> c55 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c55.next());
      node.put("space", "xzya");
      //SpaceReflect
      node = Json.newJsonObject();
      node.put("id", 55);
      node.put("method", "space/reflect");
      Iterator<String> c56 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c56.next());
      node.put("identity", _identity);
      Iterator<String> c57 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c57.next());
      node.put("space", "xzya");
      Iterator<String> c58 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c58.next());
      node.put("key", "xzya");
      //SpaceList
      node = Json.newJsonObject();
      node.put("id", 58);
      node.put("method", "space/list");
      Iterator<String> c59 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c59.next());
      node.put("identity", _identity);
      //PushRegister
      node = Json.newJsonObject();
      node.put("id", 59);
      node.put("method", "push/register");
      Iterator<String> c60 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c60.next());
      node.put("identity", _identity);
      Iterator<String> c61 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c61.next());
      node.put("domain", "xzya");
      Iterator<String> c62 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:407308", c62.next());
      node.put("subscription", Json.newJsonObject());
      Iterator<String> c63 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:446218", c63.next());
      node.put("device-info", Json.newJsonObject());
      //DomainMap
      node = Json.newJsonObject();
      node.put("id", 63);
      node.put("method", "domain/map");
      Iterator<String> c64 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c64.next());
      node.put("identity", _identity);
      Iterator<String> c65 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c65.next());
      node.put("domain", "xzya");
      Iterator<String> c66 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c66.next());
      node.put("space", "xzya");
      //DomainConfigure
      node = Json.newJsonObject();
      node.put("id", 66);
      node.put("method", "domain/configure");
      Iterator<String> c67 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c67.next());
      node.put("identity", _identity);
      Iterator<String> c68 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c68.next());
      node.put("domain", "xzya");
      Iterator<String> c69 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:453621", c69.next());
      node.put("product-config", Json.newJsonObject());
      //DomainReflect
      node = Json.newJsonObject();
      node.put("id", 69);
      node.put("method", "domain/reflect");
      Iterator<String> c70 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c70.next());
      node.put("identity", _identity);
      Iterator<String> c71 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c71.next());
      node.put("domain", "xzya");
      //DomainMapDocument
      node = Json.newJsonObject();
      node.put("id", 71);
      node.put("method", "domain/map-document");
      Iterator<String> c72 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c72.next());
      node.put("identity", _identity);
      Iterator<String> c73 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c73.next());
      node.put("domain", "xzya");
      Iterator<String> c74 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c74.next());
      node.put("space", "xzya");
      Iterator<String> c75 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c75.next());
      node.put("key", "xzya");
      //DomainList
      node = Json.newJsonObject();
      node.put("id", 75);
      node.put("method", "domain/list");
      Iterator<String> c76 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c76.next());
      node.put("identity", _identity);
      //DomainListBySpace
      node = Json.newJsonObject();
      node.put("id", 76);
      node.put("method", "domain/list-by-space");
      Iterator<String> c77 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c77.next());
      node.put("identity", _identity);
      Iterator<String> c78 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c78.next());
      node.put("space", "xzya");
      //DomainGetVapidPublicKey
      node = Json.newJsonObject();
      node.put("id", 78);
      node.put("method", "domain/get-vapid-public-key");
      Iterator<String> c79 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c79.next());
      node.put("identity", _identity);
      Iterator<String> c80 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c80.next());
      node.put("domain", "xzya");
      //DomainUnmap
      node = Json.newJsonObject();
      node.put("id", 80);
      node.put("method", "domain/unmap");
      Iterator<String> c81 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c81.next());
      node.put("identity", _identity);
      Iterator<String> c82 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c82.next());
      node.put("domain", "xzya");
      //DomainGet
      node = Json.newJsonObject();
      node.put("id", 82);
      node.put("method", "domain/get");
      Iterator<String> c83 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c83.next());
      node.put("identity", _identity);
      Iterator<String> c84 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c84.next());
      node.put("domain", "xzya");
      //DocumentDownloadArchive
      node = Json.newJsonObject();
      node.put("id", 84);
      node.put("method", "document/download-archive");
      Iterator<String> c85 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c85.next());
      node.put("space", "xzya");
      Iterator<String> c86 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c86.next());
      node.put("key", "xzya");
      //DocumentAuthorization
      node = Json.newJsonObject();
      node.put("id", 86);
      node.put("method", "document/authorization");
      Iterator<String> c87 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c87.next());
      node.put("space", "xzya");
      Iterator<String> c88 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c88.next());
      node.put("key", "xzya");
      Iterator<String> c89 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425987", c89.next());
      node.put("message", Json.newJsonObject());
      //DocumentAuthorizationDomain
      node = Json.newJsonObject();
      node.put("id", 89);
      node.put("method", "document/authorization-domain");
      Iterator<String> c90 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c90.next());
      node.put("domain", "xzya");
      Iterator<String> c91 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425987", c91.next());
      node.put("message", Json.newJsonObject());
      //DocumentAuthorize
      node = Json.newJsonObject();
      node.put("id", 91);
      node.put("method", "document/authorize");
      Iterator<String> c92 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c92.next());
      node.put("space", "xzya");
      Iterator<String> c93 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c93.next());
      node.put("key", "xzya");
      Iterator<String> c94 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458737", c94.next());
      node.put("username", "xzya");
      Iterator<String> c95 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c95.next());
      node.put("password", "xzya");
      //DocumentAuthorizeDomain
      node = Json.newJsonObject();
      node.put("id", 95);
      node.put("method", "document/authorize-domain");
      Iterator<String> c96 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c96.next());
      node.put("domain", "xzya");
      Iterator<String> c97 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458737", c97.next());
      node.put("username", "xzya");
      Iterator<String> c98 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c98.next());
      node.put("password", "xzya");
      //DocumentAuthorizeWithReset
      node = Json.newJsonObject();
      node.put("id", 98);
      node.put("method", "document/authorize-with-reset");
      Iterator<String> c99 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c99.next());
      node.put("space", "xzya");
      Iterator<String> c100 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c100.next());
      node.put("key", "xzya");
      Iterator<String> c101 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458737", c101.next());
      node.put("username", "xzya");
      Iterator<String> c102 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c102.next());
      node.put("password", "xzya");
      Iterator<String> c103 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466931", c103.next());
      node.put("new_password", "xzya");
      //DocumentAuthorizeDomainWithReset
      node = Json.newJsonObject();
      node.put("id", 103);
      node.put("method", "document/authorize-domain-with-reset");
      Iterator<String> c104 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c104.next());
      node.put("domain", "xzya");
      Iterator<String> c105 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458737", c105.next());
      node.put("username", "xzya");
      Iterator<String> c106 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c106.next());
      node.put("password", "xzya");
      Iterator<String> c107 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466931", c107.next());
      node.put("new_password", "xzya");
      //DocumentCreate
      node = Json.newJsonObject();
      node.put("id", 107);
      node.put("method", "document/create");
      Iterator<String> c108 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c108.next());
      node.put("identity", _identity);
      Iterator<String> c109 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c109.next());
      node.put("space", "xzya");
      Iterator<String> c110 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c110.next());
      node.put("key", "xzya");
      Iterator<String> c111 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461826", c111.next());
      node.put("arg", Json.newJsonObject());
      //DocumentDelete
      node = Json.newJsonObject();
      node.put("id", 111);
      node.put("method", "document/delete");
      Iterator<String> c112 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c112.next());
      node.put("identity", _identity);
      Iterator<String> c113 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c113.next());
      node.put("space", "xzya");
      Iterator<String> c114 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c114.next());
      node.put("key", "xzya");
      //DocumentList
      node = Json.newJsonObject();
      node.put("id", 114);
      node.put("method", "document/list");
      Iterator<String> c115 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c115.next());
      node.put("identity", _identity);
      Iterator<String> c116 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c116.next());
      node.put("space", "xzya");
      //MessageDirectSend
      node = Json.newJsonObject();
      node.put("id", 116);
      node.put("method", "message/direct-send");
      Iterator<String> c117 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c117.next());
      node.put("identity", _identity);
      Iterator<String> c118 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c118.next());
      node.put("space", "xzya");
      Iterator<String> c119 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c119.next());
      node.put("key", "xzya");
      Iterator<String> c120 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:454659", c120.next());
      node.put("channel", "xzya");
      Iterator<String> c121 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425987", c121.next());
      node.put("message", Json.newJsonObject());
      //MessageDirectSendOnce
      node = Json.newJsonObject();
      node.put("id", 121);
      node.put("method", "message/direct-send-once");
      Iterator<String> c122 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c122.next());
      node.put("identity", _identity);
      Iterator<String> c123 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c123.next());
      node.put("space", "xzya");
      Iterator<String> c124 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c124.next());
      node.put("key", "xzya");
      Iterator<String> c125 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:454659", c125.next());
      node.put("channel", "xzya");
      Iterator<String> c126 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425987", c126.next());
      node.put("message", Json.newJsonObject());
      //ConnectionCreate
      node = Json.newJsonObject();
      node.put("id", 126);
      node.put("method", "connection/create");
      Iterator<String> c127 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c127.next());
      node.put("identity", _identity);
      Iterator<String> c128 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c128.next());
      node.put("space", "xzya");
      Iterator<String> c129 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c129.next());
      node.put("key", "xzya");
      //ConnectionCreateViaDomain
      node = Json.newJsonObject();
      node.put("id", 129);
      node.put("method", "connection/create-via-domain");
      Iterator<String> c130 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c130.next());
      node.put("identity", _identity);
      Iterator<String> c131 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c131.next());
      node.put("domain", "xzya");
      //ConnectionSend
      node = Json.newJsonObject();
      node.put("id", 131);
      node.put("method", "connection/send");
      Iterator<String> c132 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c132.next());
      node.put("connection", 100L);
      Iterator<String> c133 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:454659", c133.next());
      node.put("channel", "xzya");
      Iterator<String> c134 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425987", c134.next());
      node.put("message", Json.newJsonObject());
      //ConnectionPassword
      node = Json.newJsonObject();
      node.put("id", 134);
      node.put("method", "connection/password");
      Iterator<String> c135 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c135.next());
      node.put("connection", 100L);
      Iterator<String> c136 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458737", c136.next());
      node.put("username", "xzya");
      Iterator<String> c137 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c137.next());
      node.put("password", "xzya");
      Iterator<String> c138 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466931", c138.next());
      node.put("new_password", "xzya");
      //ConnectionSendOnce
      node = Json.newJsonObject();
      node.put("id", 138);
      node.put("method", "connection/send-once");
      Iterator<String> c139 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c139.next());
      node.put("connection", 100L);
      Iterator<String> c140 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:454659", c140.next());
      node.put("channel", "xzya");
      Iterator<String> c141 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425987", c141.next());
      node.put("message", Json.newJsonObject());
      //ConnectionCanAttach
      node = Json.newJsonObject();
      node.put("id", 141);
      node.put("method", "connection/can-attach");
      Iterator<String> c142 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c142.next());
      node.put("connection", 100L);
      //ConnectionAttach
      node = Json.newJsonObject();
      node.put("id", 142);
      node.put("method", "connection/attach");
      Iterator<String> c143 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c143.next());
      node.put("connection", 100L);
      Iterator<String> c144 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:476156", c144.next());
      node.put("asset-id", "xzya");
      Iterator<String> c145 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:470028", c145.next());
      node.put("filename", "xzya");
      Iterator<String> c146 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:455691", c146.next());
      node.put("content-type", "xzya");
      Iterator<String> c147 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:477179", c147.next());
      node.put("size", 100L);
      Iterator<String> c148 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:445437", c148.next());
      node.put("digest-md5", "xzya");
      Iterator<String> c149 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:406525", c149.next());
      node.put("digest-sha384", "xzya");
      //ConnectionUpdate
      node = Json.newJsonObject();
      node.put("id", 149);
      node.put("method", "connection/update");
      Iterator<String> c150 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c150.next());
      node.put("connection", 100L);
      //ConnectionEnd
      node = Json.newJsonObject();
      node.put("id", 150);
      node.put("method", "connection/end");
      Iterator<String> c151 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:405505", c151.next());
      node.put("connection", 100L);
      //DocumentsHashPassword
      node = Json.newJsonObject();
      node.put("id", 151);
      node.put("method", "documents/hash-password");
      Iterator<String> c152 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:465917", c152.next());
      node.put("password", "xzya");
      //BillingConnectionCreate
      node = Json.newJsonObject();
      node.put("id", 152);
      node.put("method", "billing-connection/create");
      Iterator<String> c153 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c153.next());
      node.put("identity", _identity);
      //ConfigureMakeOrGetAssetKey
      node = Json.newJsonObject();
      node.put("id", 153);
      node.put("method", "configure/make-or-get-asset-key");
      //AttachmentStart
      node = Json.newJsonObject();
      node.put("id", 153);
      node.put("method", "attachment/start");
      Iterator<String> c154 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c154.next());
      node.put("identity", _identity);
      Iterator<String> c155 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c155.next());
      node.put("space", "xzya");
      Iterator<String> c156 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c156.next());
      node.put("key", "xzya");
      Iterator<String> c157 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:470028", c157.next());
      node.put("filename", "xzya");
      Iterator<String> c158 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:455691", c158.next());
      node.put("content-type", "xzya");
      //AttachmentStartByDomain
      node = Json.newJsonObject();
      node.put("id", 158);
      node.put("method", "attachment/start-by-domain");
      Iterator<String> c159 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c159.next());
      node.put("identity", _identity);
      Iterator<String> c160 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c160.next());
      node.put("domain", "xzya");
      Iterator<String> c161 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:470028", c161.next());
      node.put("filename", "xzya");
      Iterator<String> c162 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:455691", c162.next());
      node.put("content-type", "xzya");
      //AttachmentAppend
      node = Json.newJsonObject();
      node.put("id", 162);
      node.put("method", "attachment/append");
      Iterator<String> c163 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:409609", c163.next());
      node.put("upload", 100L);
      Iterator<String> c164 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:462859", c164.next());
      node.put("chunk-md5", "xzya");
      Iterator<String> c165 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:409608", c165.next());
      node.put("base64-bytes", "xzya");
      //AttachmentFinish
      node = Json.newJsonObject();
      node.put("id", 165);
      node.put("method", "attachment/finish");
      Iterator<String> c166 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:409609", c166.next());
      node.put("upload", 100L);
      //SuperCheckIn
      node = Json.newJsonObject();
      node.put("id", 166);
      node.put("method", "super/check-in");
      Iterator<String> c167 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c167.next());
      node.put("identity", _identity);
      //SuperListAutomaticDomains
      node = Json.newJsonObject();
      node.put("id", 167);
      node.put("method", "super/list-automatic-domains");
      Iterator<String> c168 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c168.next());
      node.put("identity", _identity);
      Iterator<String> c169 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:439292", c169.next());
      node.put("timestamp", 100L);
      //SuperSetDomainCertificate
      node = Json.newJsonObject();
      node.put("id", 169);
      node.put("method", "super/set-domain-certificate");
      Iterator<String> c170 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c170.next());
      node.put("identity", _identity);
      Iterator<String> c171 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c171.next());
      node.put("domain", "xzya");
      Iterator<String> c172 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:439292", c172.next());
      node.put("timestamp", 100L);
      //RegionalDomainLookup
      node = Json.newJsonObject();
      node.put("id", 172);
      node.put("method", "regional/domain-lookup");
      Iterator<String> c173 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c173.next());
      node.put("identity", _identity);
      Iterator<String> c174 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:488444", c174.next());
      node.put("domain", "xzya");
      //RegionalEmitMetrics
      node = Json.newJsonObject();
      node.put("id", 174);
      node.put("method", "regional/emit-metrics");
      Iterator<String> c175 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c175.next());
      node.put("identity", _identity);
      Iterator<String> c176 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c176.next());
      node.put("space", "xzya");
      Iterator<String> c177 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c177.next());
      node.put("key", "xzya");
      Iterator<String> c178 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:425213", c178.next());
      node.put("metrics", Json.newJsonObject());
      //RegionalInitHost
      node = Json.newJsonObject();
      node.put("id", 178);
      node.put("method", "regional/init-host");
      Iterator<String> c179 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c179.next());
      node.put("identity", _identity);
      Iterator<String> c180 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c180.next());
      node.put("region", "xzya");
      Iterator<String> c181 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9005", c181.next());
      node.put("machine", "xzya");
      Iterator<String> c182 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:456716", c182.next());
      node.put("role", "xzya");
      Iterator<String> c183 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:90000", c183.next());
      node.put("public-key", "xzya");
      //RegionalFinderFind
      node = Json.newJsonObject();
      node.put("id", 183);
      node.put("method", "regional/finder/find");
      Iterator<String> c184 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c184.next());
      node.put("identity", _identity);
      Iterator<String> c185 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c185.next());
      node.put("space", "xzya");
      Iterator<String> c186 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c186.next());
      node.put("key", "xzya");
      //RegionalFinderFree
      node = Json.newJsonObject();
      node.put("id", 186);
      node.put("method", "regional/finder/free");
      Iterator<String> c187 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c187.next());
      node.put("identity", _identity);
      Iterator<String> c188 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c188.next());
      node.put("space", "xzya");
      Iterator<String> c189 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c189.next());
      node.put("key", "xzya");
      Iterator<String> c190 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c190.next());
      node.put("region", "xzya");
      Iterator<String> c191 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9005", c191.next());
      node.put("machine", "xzya");
      //RegionalFinderBind
      node = Json.newJsonObject();
      node.put("id", 191);
      node.put("method", "regional/finder/bind");
      Iterator<String> c192 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c192.next());
      node.put("identity", _identity);
      Iterator<String> c193 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c193.next());
      node.put("space", "xzya");
      Iterator<String> c194 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c194.next());
      node.put("key", "xzya");
      Iterator<String> c195 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c195.next());
      node.put("region", "xzya");
      Iterator<String> c196 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9005", c196.next());
      node.put("machine", "xzya");
      //RegionalFinderDeleteMark
      node = Json.newJsonObject();
      node.put("id", 196);
      node.put("method", "regional/finder/delete/mark");
      Iterator<String> c197 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c197.next());
      node.put("identity", _identity);
      Iterator<String> c198 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c198.next());
      node.put("space", "xzya");
      Iterator<String> c199 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c199.next());
      node.put("key", "xzya");
      Iterator<String> c200 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c200.next());
      node.put("region", "xzya");
      Iterator<String> c201 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9005", c201.next());
      node.put("machine", "xzya");
      //RegionalFinderDeleteCommit
      node = Json.newJsonObject();
      node.put("id", 201);
      node.put("method", "regional/finder/delete/commit");
      Iterator<String> c202 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c202.next());
      node.put("identity", _identity);
      Iterator<String> c203 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c203.next());
      node.put("space", "xzya");
      Iterator<String> c204 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c204.next());
      node.put("key", "xzya");
      Iterator<String> c205 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c205.next());
      node.put("region", "xzya");
      Iterator<String> c206 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9005", c206.next());
      node.put("machine", "xzya");
      //RegionalFinderBackUp
      node = Json.newJsonObject();
      node.put("id", 206);
      node.put("method", "regional/finder/back-up");
      Iterator<String> c207 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c207.next());
      node.put("identity", _identity);
      Iterator<String> c208 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c208.next());
      node.put("space", "xzya");
      Iterator<String> c209 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:466947", c209.next());
      node.put("key", "xzya");
      Iterator<String> c210 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c210.next());
      node.put("region", "xzya");
      Iterator<String> c211 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9005", c211.next());
      node.put("machine", "xzya");
      Iterator<String> c212 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9007", c212.next());
      node.put("archive", "xzya");
      Iterator<String> c213 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461836", c213.next());
      node.put("seq", 42);
      Iterator<String> c214 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:492531", c214.next());
      node.put("delta-bytes", 100L);
      Iterator<String> c215 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:460787", c215.next());
      node.put("asset-bytes", 100L);
      //RegionalFinderList
      node = Json.newJsonObject();
      node.put("id", 215);
      node.put("method", "regional/finder/list");
      Iterator<String> c216 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c216.next());
      node.put("identity", _identity);
      Iterator<String> c217 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c217.next());
      node.put("region", "xzya");
      Iterator<String> c218 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9005", c218.next());
      node.put("machine", "xzya");
      //RegionalFinderDeletionList
      node = Json.newJsonObject();
      node.put("id", 218);
      node.put("method", "regional/finder/deletion-list");
      Iterator<String> c219 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c219.next());
      node.put("identity", _identity);
      Iterator<String> c220 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c220.next());
      node.put("region", "xzya");
      Iterator<String> c221 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9005", c221.next());
      node.put("machine", "xzya");
      //RegionalAuth
      node = Json.newJsonObject();
      node.put("id", 221);
      node.put("method", "regional/auth");
      Iterator<String> c222 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c222.next());
      node.put("identity", _identity);
      //RegionalGetPlan
      node = Json.newJsonObject();
      node.put("id", 222);
      node.put("method", "regional/get-plan");
      Iterator<String> c223 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c223.next());
      node.put("identity", _identity);
      Iterator<String> c224 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c224.next());
      node.put("space", "xzya");
      //RegionalCapacityAdd
      node = Json.newJsonObject();
      node.put("id", 224);
      node.put("method", "regional/capacity/add");
      Iterator<String> c225 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c225.next());
      node.put("identity", _identity);
      Iterator<String> c226 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c226.next());
      node.put("space", "xzya");
      Iterator<String> c227 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c227.next());
      node.put("region", "xzya");
      Iterator<String> c228 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9005", c228.next());
      node.put("machine", "xzya");
      //RegionalCapacityRemove
      node = Json.newJsonObject();
      node.put("id", 228);
      node.put("method", "regional/capacity/remove");
      Iterator<String> c229 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c229.next());
      node.put("identity", _identity);
      Iterator<String> c230 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c230.next());
      node.put("space", "xzya");
      Iterator<String> c231 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c231.next());
      node.put("region", "xzya");
      Iterator<String> c232 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9005", c232.next());
      node.put("machine", "xzya");
      //RegionalCapacityNuke
      node = Json.newJsonObject();
      node.put("id", 232);
      node.put("method", "regional/capacity/nuke");
      Iterator<String> c233 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c233.next());
      node.put("identity", _identity);
      Iterator<String> c234 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c234.next());
      node.put("space", "xzya");
      //RegionalCapacityListSpace
      node = Json.newJsonObject();
      node.put("id", 234);
      node.put("method", "regional/capacity/list-space");
      Iterator<String> c235 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c235.next());
      node.put("identity", _identity);
      Iterator<String> c236 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c236.next());
      node.put("space", "xzya");
      //RegionalCapacityListMachine
      node = Json.newJsonObject();
      node.put("id", 236);
      node.put("method", "regional/capacity/list-machine");
      Iterator<String> c237 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c237.next());
      node.put("identity", _identity);
      Iterator<String> c238 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c238.next());
      node.put("region", "xzya");
      Iterator<String> c239 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9005", c239.next());
      node.put("machine", "xzya");
      //RegionalCapacityListRegion
      node = Json.newJsonObject();
      node.put("id", 239);
      node.put("method", "regional/capacity/list-region");
      Iterator<String> c240 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c240.next());
      node.put("identity", _identity);
      Iterator<String> c241 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c241.next());
      node.put("space", "xzya");
      Iterator<String> c242 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c242.next());
      node.put("region", "xzya");
      //RegionalCapacityPickSpaceHost
      node = Json.newJsonObject();
      node.put("id", 242);
      node.put("method", "regional/capacity/pick-space-host");
      Iterator<String> c243 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c243.next());
      node.put("identity", _identity);
      Iterator<String> c244 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c244.next());
      node.put("space", "xzya");
      Iterator<String> c245 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c245.next());
      node.put("region", "xzya");
      //RegionalCapacityPickSpaceHostNew
      node = Json.newJsonObject();
      node.put("id", 245);
      node.put("method", "regional/capacity/pick-space-host-new");
      Iterator<String> c246 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:458759", c246.next());
      node.put("identity", _identity);
      Iterator<String> c247 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:461828", c247.next());
      node.put("space", "xzya");
      Iterator<String> c248 = fe.execute(node.toString());
      Assert.assertEquals("ERROR:9006", c248.next());
      node.put("region", "xzya");
    }
  }
}

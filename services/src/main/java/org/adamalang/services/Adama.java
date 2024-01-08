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
package org.adamalang.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.api.*;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.internal.InternalSigner;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.ServiceConfig;
import org.adamalang.runtime.remote.SimpleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashSet;
import java.util.function.Consumer;

public class Adama extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(Adama.class);
  private final FirstPartyMetrics metrics;
  private final SelfClient client;
  private final InternalSigner signer;

  public Adama(FirstPartyMetrics metrics, SelfClient client, InternalSigner signer, ServiceConfig config) throws ErrorCodeException {
    super("adama", new NtPrincipal("adama", "service"), true);
    this.client = client;
    this.metrics = metrics;
    this.signer = signer;
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message _AdamaSpaceReflectReq { string space; string key; }\n");
    sb.append("message _AdamaReflectionRes { dynamic reflection }\n");
    sb.append("message _AdamaDomainMapReq { string domain; string space; maybe<string> certificate; }\n");
    sb.append("message _AdamaSimpleRes {  }\n");
    sb.append("message _AdamaDomainClaimApexReq { string domain; }\n");
    sb.append("message _AdamaDomainVerifyRes { bool claimed string txtToken }\n");
    sb.append("message _AdamaDomainRedirectReq { string domain; string destinationDomain; }\n");
    sb.append("message _AdamaDomainConfigureReq { string domain; dynamic productConfig; }\n");
    sb.append("message _AdamaDomainMapDocumentReq { string domain; string space; string key; maybe<bool> route; maybe<string> certificate; }\n");
    sb.append("message _AdamaDocumentCreateReq { string space; string key; maybe<string> entropy; dynamic arg; }\n");
    sb.append("message _AdamaDocumentDeleteReq { string space; string key; }\n");
    sb.append("message _AdamaMessageDirectSendReq { string space; string key; string channel; dynamic message; }\n");
    sb.append("message _AdamaSeqRes { int seq }\n");
    sb.append("message _AdamaMessageDirectSendOnceReq { string space; string key; maybe<string> dedupe; string channel; dynamic message; }\n");
    sb.append("service adama {\n");
    sb.append("  class=\"adama\";\n");
    sb.append("  method secured<_AdamaSpaceReflectReq, _AdamaReflectionRes)> spaceReflect;\n");
    sb.append("  method secured<_AdamaDomainMapReq, _AdamaSimpleRes)> domainMap;\n");
    sb.append("  method secured<_AdamaDomainClaimApexReq, _AdamaDomainVerifyRes)> domainClaimApex;\n");
    sb.append("  method secured<_AdamaDomainRedirectReq, _AdamaSimpleRes)> domainRedirect;\n");
    sb.append("  method secured<_AdamaDomainConfigureReq, _AdamaSimpleRes)> domainConfigure;\n");
    sb.append("  method secured<_AdamaDomainMapDocumentReq, _AdamaSimpleRes)> domainMapDocument;\n");
    sb.append("  method secured<_AdamaDocumentCreateReq, _AdamaSimpleRes)> documentCreate;\n");
    sb.append("  method secured<_AdamaDocumentDeleteReq, _AdamaSimpleRes)> documentDelete;\n");
    sb.append("  method secured<_AdamaMessageDirectSendReq, _AdamaSeqRes)> messageDirectSend;\n");
    sb.append("  method secured<_AdamaMessageDirectSendOnceReq, _AdamaSeqRes)> messageDirectSendOnce;\n");
    sb.append("}\n");
    return sb.toString();
  }
  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    String identity = signer.toIdentity(who);
    ObjectNode requestNode = Json.parseJsonObject(request);
    switch (method) {
      case "spaceReflect": {
        ClientSpaceReflectRequest req = new ClientSpaceReflectRequest();
        req.identity = identity;
        req.space = Json.readString(requestNode, "space");
        req.key = Json.readString(requestNode, "key");
        client.spaceReflect(req, new Callback<>() {
          @Override
          public void success(ClientReflectionResponse response) {
            callback.success(response.toInternalJson());
          }
          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
        return;
      } 
      case "domainMap": {
        ClientDomainMapRequest req = new ClientDomainMapRequest();
        req.identity = identity;
        req.domain = Json.readString(requestNode, "domain");
        req.space = Json.readString(requestNode, "space");
        req.certificate = Json.readString(requestNode, "certificate");
        client.domainMap(req, new Callback<>() {
          @Override
          public void success(ClientSimpleResponse response) {
            callback.success(response.toInternalJson());
          }
          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
        return;
      } 
      case "domainClaimApex": {
        ClientDomainClaimApexRequest req = new ClientDomainClaimApexRequest();
        req.identity = identity;
        req.domain = Json.readString(requestNode, "domain");
        client.domainClaimApex(req, new Callback<>() {
          @Override
          public void success(ClientDomainVerifyResponse response) {
            callback.success(response.toInternalJson());
          }
          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
        return;
      } 
      case "domainRedirect": {
        ClientDomainRedirectRequest req = new ClientDomainRedirectRequest();
        req.identity = identity;
        req.domain = Json.readString(requestNode, "domain");
        req.destinationDomain = Json.readString(requestNode, "destinationDomain");
        client.domainRedirect(req, new Callback<>() {
          @Override
          public void success(ClientSimpleResponse response) {
            callback.success(response.toInternalJson());
          }
          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
        return;
      } 
      case "domainConfigure": {
        ClientDomainConfigureRequest req = new ClientDomainConfigureRequest();
        req.identity = identity;
        req.domain = Json.readString(requestNode, "domain");
        req.productConfig = Json.readObject(requestNode, "productConfig");
        client.domainConfigure(req, new Callback<>() {
          @Override
          public void success(ClientSimpleResponse response) {
            callback.success(response.toInternalJson());
          }
          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
        return;
      } 
      case "domainMapDocument": {
        ClientDomainMapDocumentRequest req = new ClientDomainMapDocumentRequest();
        req.identity = identity;
        req.domain = Json.readString(requestNode, "domain");
        req.space = Json.readString(requestNode, "space");
        req.key = Json.readString(requestNode, "key");
        req.route = Json.readBool(requestNode, "route");
        req.certificate = Json.readString(requestNode, "certificate");
        client.domainMapDocument(req, new Callback<>() {
          @Override
          public void success(ClientSimpleResponse response) {
            callback.success(response.toInternalJson());
          }
          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
        return;
      } 
      case "documentCreate": {
        ClientDocumentCreateRequest req = new ClientDocumentCreateRequest();
        req.identity = identity;
        req.space = Json.readString(requestNode, "space");
        req.key = Json.readString(requestNode, "key");
        req.entropy = Json.readString(requestNode, "entropy");
        req.arg = Json.readObject(requestNode, "arg");
        client.documentCreate(req, new Callback<>() {
          @Override
          public void success(ClientSimpleResponse response) {
            callback.success(response.toInternalJson());
          }
          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
        return;
      } 
      case "documentDelete": {
        ClientDocumentDeleteRequest req = new ClientDocumentDeleteRequest();
        req.identity = identity;
        req.space = Json.readString(requestNode, "space");
        req.key = Json.readString(requestNode, "key");
        client.documentDelete(req, new Callback<>() {
          @Override
          public void success(ClientSimpleResponse response) {
            callback.success(response.toInternalJson());
          }
          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
        return;
      } 
      case "messageDirectSend": {
        ClientMessageDirectSendRequest req = new ClientMessageDirectSendRequest();
        req.identity = identity;
        req.space = Json.readString(requestNode, "space");
        req.key = Json.readString(requestNode, "key");
        req.channel = Json.readString(requestNode, "channel");
        req.message = Json.readJsonNode(requestNode, "message");
        client.messageDirectSend(req, new Callback<>() {
          @Override
          public void success(ClientSeqResponse response) {
            callback.success(response.toInternalJson());
          }
          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
        return;
      } 
      case "messageDirectSendOnce": {
        ClientMessageDirectSendOnceRequest req = new ClientMessageDirectSendOnceRequest();
        req.identity = identity;
        req.space = Json.readString(requestNode, "space");
        req.key = Json.readString(requestNode, "key");
        req.dedupe = Json.readString(requestNode, "dedupe");
        req.channel = Json.readString(requestNode, "channel");
        req.message = Json.readJsonNode(requestNode, "message");
        client.messageDirectSendOnce(req, new Callback<>() {
          @Override
          public void success(ClientSeqResponse response) {
            callback.success(response.toInternalJson());
          }
          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
        return;
      } 
      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }
}

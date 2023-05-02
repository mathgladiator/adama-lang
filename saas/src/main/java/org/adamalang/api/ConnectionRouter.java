/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.*;
import org.adamalang.common.metrics.*;
import org.adamalang.connection.*;
import org.adamalang.web.io.*;
import org.adamalang.ErrorCodes;

import java.util.HashMap;
import java.util.Map;

public class ConnectionRouter {
  public final Session session;
  public final ConnectionNexus nexus;
  public final RootHandler handler;
  public final HashMap<Long, AttachmentUploadHandler> inflightAttachmentUpload;
  public final HashMap<Long, DocumentStreamHandler> inflightDocumentStream;

  public ConnectionRouter(Session session, ConnectionNexus nexus, RootHandler handler) {
    this.session = session;
    this.nexus = nexus;
    this.handler = handler;
    this.inflightAttachmentUpload = new HashMap<>();
    this.inflightDocumentStream = new HashMap<>();
  }

  public void disconnect() {
    nexus.executor.execute(new NamedRunnable("disconnect") {
      @Override
      public void execute() throws Exception {
        for (Map.Entry<Long, AttachmentUploadHandler> entry : inflightAttachmentUpload.entrySet()) {
          entry.getValue().disconnect(entry.getKey());
        }
        inflightAttachmentUpload.clear();
        for (Map.Entry<Long, DocumentStreamHandler> entry : inflightDocumentStream.entrySet()) {
          entry.getValue().disconnect(entry.getKey());
        }
        inflightDocumentStream.clear();
        handler.disconnect();
      }
    });
  }

  public void route(JsonRequest request, JsonResponder responder) {
    try {
      ObjectNode _accessLogItem = Json.newJsonObject();
      long requestId = request.id();
      String method = request.method();
      _accessLogItem.put("method", method);
      request.dumpIntoLog(_accessLogItem);
      nexus.executor.execute(new NamedRunnable("handle", method) {
        @Override
        public void execute() throws Exception {
          session.activity();
          switch (method) {
            case "init/setup-account": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_InitSetupAccount.start();
              InitSetupAccountRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(InitSetupAccountRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "init/convert-google-user": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_InitConvertGoogleUser.start();
              InitConvertGoogleUserRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(InitConvertGoogleUserRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new InitiationResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "init/complete-account": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_InitCompleteAccount.start();
              InitCompleteAccountRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(InitCompleteAccountRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new InitiationResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "account/set-password": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AccountSetPassword.start();
              AccountSetPasswordRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(AccountSetPasswordRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "account/get-payment-plan": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AccountGetPaymentPlan.start();
              AccountGetPaymentPlanRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(AccountGetPaymentPlanRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new PaymentResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "account/login": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AccountLogin.start();
              AccountLoginRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(AccountLoginRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new InitiationResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "probe": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_Probe.start();
              ProbeRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(ProbeRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "authority/create": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AuthorityCreate.start();
              AuthorityCreateRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(AuthorityCreateRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new ClaimResultResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "authority/set": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AuthoritySet.start();
              AuthoritySetRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(AuthoritySetRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "authority/get": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AuthorityGet.start();
              AuthorityGetRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(AuthorityGetRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new KeystoreResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "authority/list": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AuthorityList.start();
              AuthorityListRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(AuthorityListRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new AuthorityListingResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "authority/destroy": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AuthorityDestroy.start();
              AuthorityDestroyRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(AuthorityDestroyRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/create": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceCreate.start();
              SpaceCreateRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceCreateRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/generate-key": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceGenerateKey.start();
              SpaceGenerateKeyRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceGenerateKeyRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new KeyPairResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/usage": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceUsage.start();
              SpaceUsageRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceUsageRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new BillingUsageResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/get": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceGet.start();
              SpaceGetRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceGetRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new PlanResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/set": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceSet.start();
              SpaceSetRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceSetRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/redeploy-kick": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceRedeployKick.start();
              SpaceRedeployKickRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceRedeployKickRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/set-rxhtml": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceSetRxhtml.start();
              SpaceSetRxhtmlRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceSetRxhtmlRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/get-rxhtml": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceGetRxhtml.start();
              SpaceGetRxhtmlRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceGetRxhtmlRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new RxhtmlResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/delete": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceDelete.start();
              SpaceDeleteRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceDeleteRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/set-role": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceSetRole.start();
              SpaceSetRoleRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceSetRoleRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/reflect": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceReflect.start();
              SpaceReflectRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceReflectRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new ReflectionResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/list": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceList.start();
              SpaceListRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceListRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SpaceListingResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "domain/map": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DomainMap.start();
              DomainMapRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DomainMapRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "domain/list": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DomainList.start();
              DomainListRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DomainListRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new DomainListingResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "domain/unmap": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DomainUnmap.start();
              DomainUnmapRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DomainUnmapRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "domain/get": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DomainGet.start();
              DomainGetRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DomainGetRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new DomainPolicyResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "document/create": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DocumentCreate.start();
              DocumentCreateRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DocumentCreateRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "document/delete": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DocumentDelete.start();
              DocumentDeleteRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DocumentDeleteRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "document/list": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DocumentList.start();
              DocumentListRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DocumentListRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new KeyListingResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "message/direct-send": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_MessageDirectSend.start();
              MessageDirectSendRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(MessageDirectSendRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SeqResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "message/direct-send-once": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_MessageDirectSendOnce.start();
              MessageDirectSendOnceRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(MessageDirectSendOnceRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SeqResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "connection/create": {
              StreamMonitor.StreamMonitorInstance mInstance = nexus.metrics.monitor_ConnectionCreate.start();
              ConnectionCreateRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(ConnectionCreateRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  DocumentStreamHandler handlerMade = handler.handle(session, resolved, new DataResponder(new JsonResponderHashMapCleanupProxy<>(mInstance, nexus.executor, inflightDocumentStream, requestId, responder, _accessLogItem, nexus.logger)));
                  inflightDocumentStream.put(requestId, handlerMade);
                  handlerMade.bind();
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "connection/send": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_ConnectionSend.start();
              ConnectionSendRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(ConnectionSendRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  DocumentStreamHandler handlerToUse = inflightDocumentStream.get(resolved.connection);
                  if (handlerToUse != null) {
                    handlerToUse.logInto(_accessLogItem);
                    handlerToUse.handle(resolved, new SeqResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                  } else {
                    _accessLogItem.put("success", false);
                    _accessLogItem.put("failure-code", 457745);
                    nexus.logger.log(_accessLogItem);
                    mInstance.failure(457745);
                    responder.error(new ErrorCodeException(457745));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "connection/send-once": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_ConnectionSendOnce.start();
              ConnectionSendOnceRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(ConnectionSendOnceRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  DocumentStreamHandler handlerToUse = inflightDocumentStream.get(resolved.connection);
                  if (handlerToUse != null) {
                    handlerToUse.logInto(_accessLogItem);
                    handlerToUse.handle(resolved, new SeqResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                  } else {
                    _accessLogItem.put("success", false);
                    _accessLogItem.put("failure-code", 410619);
                    nexus.logger.log(_accessLogItem);
                    mInstance.failure(410619);
                    responder.error(new ErrorCodeException(410619));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "connection/can-attach": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_ConnectionCanAttach.start();
              ConnectionCanAttachRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(ConnectionCanAttachRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  DocumentStreamHandler handlerToUse = inflightDocumentStream.get(resolved.connection);
                  if (handlerToUse != null) {
                    handlerToUse.logInto(_accessLogItem);
                    handlerToUse.handle(resolved, new YesResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                  } else {
                    _accessLogItem.put("success", false);
                    _accessLogItem.put("failure-code", 494559);
                    nexus.logger.log(_accessLogItem);
                    mInstance.failure(494559);
                    responder.error(new ErrorCodeException(494559));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "connection/attach": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_ConnectionAttach.start();
              ConnectionAttachRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(ConnectionAttachRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  DocumentStreamHandler handlerToUse = inflightDocumentStream.get(resolved.connection);
                  if (handlerToUse != null) {
                    handlerToUse.logInto(_accessLogItem);
                    handlerToUse.handle(resolved, new SeqResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                  } else {
                    _accessLogItem.put("success", false);
                    _accessLogItem.put("failure-code", 442363);
                    nexus.logger.log(_accessLogItem);
                    mInstance.failure(442363);
                    responder.error(new ErrorCodeException(442363));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "connection/update": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_ConnectionUpdate.start();
              ConnectionUpdateRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(ConnectionUpdateRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  DocumentStreamHandler handlerToUse = inflightDocumentStream.get(resolved.connection);
                  if (handlerToUse != null) {
                    handlerToUse.logInto(_accessLogItem);
                    handlerToUse.handle(resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                  } else {
                    _accessLogItem.put("success", false);
                    _accessLogItem.put("failure-code", 438302);
                    nexus.logger.log(_accessLogItem);
                    mInstance.failure(438302);
                    responder.error(new ErrorCodeException(438302));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "connection/end": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_ConnectionEnd.start();
              ConnectionEndRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(ConnectionEndRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  DocumentStreamHandler handlerToUse = inflightDocumentStream.remove(resolved.connection);
                  if (handlerToUse != null) {
                    handlerToUse.logInto(_accessLogItem);
                    handlerToUse.handle(resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                  } else {
                    _accessLogItem.put("success", false);
                    _accessLogItem.put("failure-code", 474128);
                    nexus.logger.log(_accessLogItem);
                    mInstance.failure(474128);
                    responder.error(new ErrorCodeException(474128));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "configure/make-or-get-asset-key": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_ConfigureMakeOrGetAssetKey.start();
              ConfigureMakeOrGetAssetKeyRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(ConfigureMakeOrGetAssetKeyRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new AssetKeyResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "attachment/start": {
              StreamMonitor.StreamMonitorInstance mInstance = nexus.metrics.monitor_AttachmentStart.start();
              AttachmentStartRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(AttachmentStartRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  AttachmentUploadHandler handlerMade = handler.handle(session, resolved, new ProgressResponder(new JsonResponderHashMapCleanupProxy<>(mInstance, nexus.executor, inflightAttachmentUpload, requestId, responder, _accessLogItem, nexus.logger)));
                  inflightAttachmentUpload.put(requestId, handlerMade);
                  handlerMade.bind();
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "attachment/append": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AttachmentAppend.start();
              AttachmentAppendRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(AttachmentAppendRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  AttachmentUploadHandler handlerToUse = inflightAttachmentUpload.get(resolved.upload);
                  if (handlerToUse != null) {
                    handlerToUse.logInto(_accessLogItem);
                    handlerToUse.handle(resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                  } else {
                    _accessLogItem.put("success", false);
                    _accessLogItem.put("failure-code", 477201);
                    nexus.logger.log(_accessLogItem);
                    mInstance.failure(477201);
                    responder.error(new ErrorCodeException(477201));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "attachment/finish": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_AttachmentFinish.start();
              AttachmentFinishRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(AttachmentFinishRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  AttachmentUploadHandler handlerToUse = inflightAttachmentUpload.remove(resolved.upload);
                  if (handlerToUse != null) {
                    handlerToUse.logInto(_accessLogItem);
                    handlerToUse.handle(resolved, new AssetIdResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                  } else {
                    _accessLogItem.put("success", false);
                    _accessLogItem.put("failure-code", 478227);
                    nexus.logger.log(_accessLogItem);
                    mInstance.failure(478227);
                    responder.error(new ErrorCodeException(478227));
                  }
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "super/check-in": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SuperCheckIn.start();
              SuperCheckInRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SuperCheckInRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "super/list-automatic-domains": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SuperListAutomaticDomains.start();
              SuperListAutomaticDomainsRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SuperListAutomaticDomainsRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new AutomaticDomainListingResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "super/set-domain-certificate": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SuperSetDomainCertificate.start();
              SuperSetDomainCertificateRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SuperSetDomainCertificateRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
          }
          responder.error(new ErrorCodeException(ErrorCodes.API_METHOD_NOT_FOUND));
        }
      });
    } catch (ErrorCodeException ex) {
      responder.error(ex);
    }
  }
}

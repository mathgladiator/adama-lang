/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.*;
import org.adamalang.common.metrics.*;
import org.adamalang.frontend.Session;
import org.adamalang.web.io.*;
import org.adamalang.ErrorCodes;

import java.util.HashMap;
import java.util.Map;

public class GlobalConnectionRouter {
  public final Session session;
  public final GlobalConnectionNexus nexus;
  public final RootGlobalHandler handler;

  public GlobalConnectionRouter(Session session, GlobalConnectionNexus nexus, RootGlobalHandler handler) {
    this.session = session;
    this.nexus = nexus;
    this.handler = handler;
  }

  public void disconnect() {
    nexus.executor.execute(new NamedRunnable("disconnect") {
      @Override
      public void execute() throws Exception {
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
            case "space/list-developers": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceListDevelopers.start();
              SpaceListDevelopersRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceListDevelopersRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new DeveloperResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "domain/reflect": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DomainReflect.start();
              DomainReflectRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DomainReflectRequest resolved) {
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
            case "domain/map-document": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DomainMapDocument.start();
              DomainMapDocumentRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DomainMapDocumentRequest resolved) {
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
            case "regional/domain-lookup": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalDomainLookup.start();
              RegionalDomainLookupRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalDomainLookupRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new DomainRawResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "regional/finder/find": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalFinderFind.start();
              RegionalFinderFindRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalFinderFindRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new FinderResultResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "regional/finder/free": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalFinderFree.start();
              RegionalFinderFreeRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalFinderFreeRequest resolved) {
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
            case "regional/finder/findbind": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalFinderFindbind.start();
              RegionalFinderFindbindRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalFinderFindbindRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new FinderResultResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "regional/finder/delete/mark": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalFinderDeleteMark.start();
              RegionalFinderDeleteMarkRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalFinderDeleteMarkRequest resolved) {
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
            case "regional/finder/delete/commit": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalFinderDeleteCommit.start();
              RegionalFinderDeleteCommitRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalFinderDeleteCommitRequest resolved) {
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
            case "regional/finder/back-up": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalFinderBackUp.start();
              RegionalFinderBackUpRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalFinderBackUpRequest resolved) {
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
            case "regional/finder/list": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalFinderList.start();
              RegionalFinderListRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalFinderListRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new KeysResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "regional/finder/deletion-list": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalFinderDeletionList.start();
              RegionalFinderDeletionListRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalFinderDeletionListRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new KeysResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "regional/auth": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalAuth.start();
              RegionalAuthRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalAuthRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new AuthResultResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "regional/get-plan": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalGetPlan.start();
              RegionalGetPlanRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalGetPlanRequest resolved) {
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
            case "regional/capacity/add": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalCapacityAdd.start();
              RegionalCapacityAddRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalCapacityAddRequest resolved) {
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
            case "regional/capacity/remove": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalCapacityRemove.start();
              RegionalCapacityRemoveRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalCapacityRemoveRequest resolved) {
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
            case "regional/capacity/nuke": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalCapacityNuke.start();
              RegionalCapacityNukeRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalCapacityNukeRequest resolved) {
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
            case "regional/capacity/list-space": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalCapacityListSpace.start();
              RegionalCapacityListSpaceRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalCapacityListSpaceRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new CapacityListResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "regional/capacity/list-machine": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalCapacityListMachine.start();
              RegionalCapacityListMachineRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalCapacityListMachineRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new CapacityListResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "regional/capacity/list-region": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalCapacityListRegion.start();
              RegionalCapacityListRegionRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalCapacityListRegionRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new CapacityListResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "regional/capacity/pick-space-host": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalCapacityPickSpaceHost.start();
              RegionalCapacityPickSpaceHostRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalCapacityPickSpaceHostRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new CapacityHostResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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

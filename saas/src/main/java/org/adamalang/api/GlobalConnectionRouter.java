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
import org.adamalang.common.*;
import org.adamalang.common.metrics.*;
import org.adamalang.contracts.data.DefaultPolicyBehavior;
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
      final long started = System.currentTimeMillis();
      _accessLogItem.put("handler", "websocket");
      _accessLogItem.put("method", method);
      _accessLogItem.put("region", nexus.region);
      _accessLogItem.put("machine", nexus.machine);
      _accessLogItem.put("@timestamp", LogTimestamp.now());
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new InitiationResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new InitiationResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "deinit": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_Deinit.start();
              DeinitRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DeinitRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new PaymentResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new InitiationResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new ClaimResultResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new KeystoreResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new AuthorityListingResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  if (!resolved.policy.checkPolicy("space/generate-key", DefaultPolicyBehavior.Owner, resolved.who)) {
                    responder.error(new ErrorCodeException(908435));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new KeyPairResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  if (!resolved.policy.checkPolicy("space/get", DefaultPolicyBehavior.OwnerAndDevelopers, resolved.who)) {
                    responder.error(new ErrorCodeException(965635));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new PlanResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  if (!resolved.policy.checkPolicy("space/set", DefaultPolicyBehavior.OwnerAndDevelopers, resolved.who)) {
                    responder.error(new ErrorCodeException(901127));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  if (!resolved.policy.checkPolicy("space/redeploy-kick", DefaultPolicyBehavior.OwnerAndDevelopers, resolved.who)) {
                    responder.error(new ErrorCodeException(962752));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  if (!resolved.policy.checkPolicy("space/set-rxhtml", DefaultPolicyBehavior.OwnerAndDevelopers, resolved.who)) {
                    responder.error(new ErrorCodeException(966835));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  if (!resolved.policy.checkPolicy("space/get-rxhtml", DefaultPolicyBehavior.OwnerAndDevelopers, resolved.who)) {
                    responder.error(new ErrorCodeException(928959));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new RxhtmlResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/set-policy": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceSetPolicy.start();
              SpaceSetPolicyRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceSetPolicyRequest resolved) {
                  if (!resolved.policy.checkPolicy("space/set-policy", DefaultPolicyBehavior.Owner, resolved.who)) {
                    responder.error(new ErrorCodeException(904392));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/get-policy": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceGetPolicy.start();
              SpaceGetPolicyRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceGetPolicyRequest resolved) {
                  if (!resolved.policy.checkPolicy("space/get-policy", DefaultPolicyBehavior.OwnerAndDevelopers, resolved.who)) {
                    responder.error(new ErrorCodeException(991435));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new AccessPolicyResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "space/metrics": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_SpaceMetrics.start();
              SpaceMetricsRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(SpaceMetricsRequest resolved) {
                  if (!resolved.policy.checkPolicy("space/metrics", DefaultPolicyBehavior.OwnerAndDevelopers, resolved.who)) {
                    responder.error(new ErrorCodeException(996596));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new MetricsAggregateResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  if (!resolved.policy.checkPolicy("space/delete", DefaultPolicyBehavior.Owner, resolved.who)) {
                    responder.error(new ErrorCodeException(904285));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  if (!resolved.policy.checkPolicy("space/set-role", DefaultPolicyBehavior.Owner, resolved.who)) {
                    responder.error(new ErrorCodeException(921607));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  if (!resolved.policy.checkPolicy("space/list-developers", DefaultPolicyBehavior.OwnerAndDevelopers, resolved.who)) {
                    responder.error(new ErrorCodeException(966875));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new DeveloperResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  if (!resolved.policy.checkPolicy("space/reflect", DefaultPolicyBehavior.OwnerAndDevelopers, resolved.who)) {
                    responder.error(new ErrorCodeException(907343));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new ReflectionResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SpaceListingResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "push/register": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_PushRegister.start();
              PushRegisterRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(PushRegisterRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  if (!resolved.policy.checkPolicy("domain/map", DefaultPolicyBehavior.Owner, resolved.who)) {
                    responder.error(new ErrorCodeException(998539));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "domain/claim-apex": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DomainClaimApex.start();
              DomainClaimApexRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DomainClaimApexRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new DomainVerifyResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "domain/redirect": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DomainRedirect.start();
              DomainRedirectRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DomainRedirectRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "domain/configure": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DomainConfigure.start();
              DomainConfigureRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DomainConfigureRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new ReflectionResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  if (!resolved.policy.checkPolicy("domain/map-document", DefaultPolicyBehavior.Owner, resolved.who)) {
                    responder.error(new ErrorCodeException(924877));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new DomainListingResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "domain/list-by-space": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DomainListBySpace.start();
              DomainListBySpaceRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DomainListBySpaceRequest resolved) {
                  if (!resolved.policy.checkPolicy("domain/list-by-space", DefaultPolicyBehavior.Owner, resolved.who)) {
                    responder.error(new ErrorCodeException(913655));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new DomainListingResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "domain/get-vapid-public-key": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DomainGetVapidPublicKey.start();
              DomainGetVapidPublicKeyRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DomainGetVapidPublicKeyRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new DomainVapidResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new DomainPolicyResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "document/download-archive": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DocumentDownloadArchive.start();
              DocumentDownloadArchiveRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DocumentDownloadArchiveRequest resolved) {
                  if (!resolved.policy.checkPolicy("document/download-archive", DefaultPolicyBehavior.Owner, resolved.who)) {
                    responder.error(new ErrorCodeException(913913));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new BackupStreamResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "document/list-push-tokens": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DocumentListPushTokens.start();
              DocumentListPushTokensRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DocumentListPushTokensRequest resolved) {
                  if (!resolved.policy.checkPolicy("document/list-push-tokens", DefaultPolicyBehavior.Owner, resolved.who)) {
                    responder.error(new ErrorCodeException(913242));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new TokenStreamResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  if (!resolved.policy.checkPolicy("document/list", DefaultPolicyBehavior.OwnerAndDevelopers, resolved.who)) {
                    responder.error(new ErrorCodeException(900160));
                    return;
                  }
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new KeyListingResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new AutomaticDomainListingResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new DomainRawResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "regional/emit-metrics": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalEmitMetrics.start();
              RegionalEmitMetricsRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalEmitMetricsRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "regional/init-host": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalInitHost.start();
              RegionalInitHostRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalInitHostRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new HostInitResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new FinderResultResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "regional/finder/bind": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalFinderBind.start();
              RegionalFinderBindRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalFinderBindRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new KeysResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new KeysResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new AuthResultResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new PlanWithKeysResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new CapacityListResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new CapacityListResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new CapacityListResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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
                  handler.handle(session, resolved, new CapacityHostResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
                  _accessLogItem.put("failure-code", ex.code);
                  nexus.logger.log(_accessLogItem);
                  responder.error(ex);
                }
              });
            } return;
            case "regional/capacity/pick-space-host-new": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_RegionalCapacityPickSpaceHostNew.start();
              RegionalCapacityPickSpaceHostNewRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(RegionalCapacityPickSpaceHostNewRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new CapacityHostResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger, started)));
                }
                @Override
                public void failure(ErrorCodeException ex) {
                  mInstance.failure(ex.code);
                  _accessLogItem.put("success", false);
                  _accessLogItem.put("latency", System.currentTimeMillis() - started);
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

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.control;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.*;
import org.adamalang.common.metrics.*;
import org.adamalang.Session;
import org.adamalang.web.io.*;
import org.adamalang.ErrorCodes;

import java.util.HashMap;
import java.util.Map;

public class ConnectionRouter {
  public final Session session;
  public final ConnectionNexus nexus;
  public final RootHandler handler;

  public ConnectionRouter(Session session, ConnectionNexus nexus, RootHandler handler) {
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
            case "global/machine/start": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalMachineStart.start();
              GlobalMachineStartRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalMachineStartRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new MachineStartResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "global/finder/find": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalFinderFind.start();
              GlobalFinderFindRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalFinderFindRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new FoundResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "global/finder/findbind": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalFinderFindbind.start();
              GlobalFinderFindbindRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalFinderFindbindRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new FoundResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "global/finder/free": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalFinderFree.start();
              GlobalFinderFreeRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalFinderFreeRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new VoidResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "global/finder/delete/mark": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalFinderDeleteMark.start();
              GlobalFinderDeleteMarkRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalFinderDeleteMarkRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new VoidResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "global/finder/delete/commit": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalFinderDeleteCommit.start();
              GlobalFinderDeleteCommitRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalFinderDeleteCommitRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new VoidResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "global/finder/back-up": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalFinderBackUp.start();
              GlobalFinderBackUpRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalFinderBackUpRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new VoidResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "global/finder/list": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalFinderList.start();
              GlobalFinderListRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalFinderListRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new KeyidResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "global/authorities/create": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalAuthoritiesCreate.start();
              GlobalAuthoritiesCreateRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalAuthoritiesCreateRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new AuthorityResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "global/authorities/set": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalAuthoritiesSet.start();
              GlobalAuthoritiesSetRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalAuthoritiesSetRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new VoidResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "global/authorities/get/public": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalAuthoritiesGetPublic.start();
              GlobalAuthoritiesGetPublicRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalAuthoritiesGetPublicRequest resolved) {
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
            case "global/authorities/get/protected": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalAuthoritiesGetProtected.start();
              GlobalAuthoritiesGetProtectedRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalAuthoritiesGetProtectedRequest resolved) {
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
            case "global/authorities/list": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalAuthoritiesList.start();
              GlobalAuthoritiesListRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalAuthoritiesListRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new AuthorityListResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "global/authorities/delete": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_GlobalAuthoritiesDelete.start();
              GlobalAuthoritiesDeleteRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(GlobalAuthoritiesDeleteRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new VoidResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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

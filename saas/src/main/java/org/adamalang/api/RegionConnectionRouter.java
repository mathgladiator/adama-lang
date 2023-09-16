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

public class RegionConnectionRouter {
  public final Session session;
  public final RegionConnectionNexus nexus;
  public final RootRegionHandler handler;
  public final HashMap<Long, AttachmentUploadHandler> inflightAttachmentUpload;
  public final HashMap<Long, DocumentStreamHandler> inflightDocumentStream;

  public RegionConnectionRouter(Session session, RegionConnectionNexus nexus, RootRegionHandler handler) {
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
            case "document/authorize": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DocumentAuthorize.start();
              DocumentAuthorizeRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DocumentAuthorizeRequest resolved) {
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
            case "document/authorize-domain": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DocumentAuthorizeDomain.start();
              DocumentAuthorizeDomainRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DocumentAuthorizeDomainRequest resolved) {
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
            case "document/authorize-with-reset": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DocumentAuthorizeWithReset.start();
              DocumentAuthorizeWithResetRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DocumentAuthorizeWithResetRequest resolved) {
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
            case "document/authorize-domain-with-reset": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DocumentAuthorizeDomainWithReset.start();
              DocumentAuthorizeDomainWithResetRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DocumentAuthorizeDomainWithResetRequest resolved) {
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
                  if (handlerMade != null) {
                    inflightDocumentStream.put(requestId, handlerMade);
                    handlerMade.bind();
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
            case "connection/create-via-domain": {
              StreamMonitor.StreamMonitorInstance mInstance = nexus.metrics.monitor_ConnectionCreateViaDomain.start();
              ConnectionCreateViaDomainRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(ConnectionCreateViaDomainRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  DocumentStreamHandler handlerMade = handler.handle(session, resolved, new DataResponder(new JsonResponderHashMapCleanupProxy<>(mInstance, nexus.executor, inflightDocumentStream, requestId, responder, _accessLogItem, nexus.logger)));
                  if (handlerMade != null) {
                    inflightDocumentStream.put(requestId, handlerMade);
                    handlerMade.bind();
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
            case "connection/password": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_ConnectionPassword.start();
              ConnectionPasswordRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(ConnectionPasswordRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  DocumentStreamHandler handlerToUse = inflightDocumentStream.get(resolved.connection);
                  if (handlerToUse != null) {
                    handlerToUse.logInto(_accessLogItem);
                    handlerToUse.handle(resolved, new SimpleResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
                  } else {
                    _accessLogItem.put("success", false);
                    _accessLogItem.put("failure-code", 462832);
                    nexus.logger.log(_accessLogItem);
                    mInstance.failure(462832);
                    responder.error(new ErrorCodeException(462832));
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
            case "documents/hash-password": {
              RequestResponseMonitor.RequestResponseMonitorInstance mInstance = nexus.metrics.monitor_DocumentsHashPassword.start();
              DocumentsHashPasswordRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(DocumentsHashPasswordRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  handler.handle(session, resolved, new HashedPasswordResponder(new SimpleMetricsProxyResponder(mInstance, responder, _accessLogItem, nexus.logger)));
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
            case "billing-connection/create": {
              StreamMonitor.StreamMonitorInstance mInstance = nexus.metrics.monitor_BillingConnectionCreate.start();
              BillingConnectionCreateRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(BillingConnectionCreateRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  DocumentStreamHandler handlerMade = handler.handle(session, resolved, new DataResponder(new JsonResponderHashMapCleanupProxy<>(mInstance, nexus.executor, inflightDocumentStream, requestId, responder, _accessLogItem, nexus.logger)));
                  if (handlerMade != null) {
                    inflightDocumentStream.put(requestId, handlerMade);
                    handlerMade.bind();
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
            case "attachment/start": {
              StreamMonitor.StreamMonitorInstance mInstance = nexus.metrics.monitor_AttachmentStart.start();
              AttachmentStartRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(AttachmentStartRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  AttachmentUploadHandler handlerMade = handler.handle(session, resolved, new ProgressResponder(new JsonResponderHashMapCleanupProxy<>(mInstance, nexus.executor, inflightAttachmentUpload, requestId, responder, _accessLogItem, nexus.logger)));
                  if (handlerMade != null) {
                    inflightAttachmentUpload.put(requestId, handlerMade);
                    handlerMade.bind();
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
            case "attachment/start-by-domain": {
              StreamMonitor.StreamMonitorInstance mInstance = nexus.metrics.monitor_AttachmentStartByDomain.start();
              AttachmentStartByDomainRequest.resolve(session, nexus, request, new Callback<>() {
                @Override
                public void success(AttachmentStartByDomainRequest resolved) {
                  resolved.logInto(_accessLogItem);
                  AttachmentUploadHandler handlerMade = handler.handle(session, resolved, new ProgressResponder(new JsonResponderHashMapCleanupProxy<>(mInstance, nexus.executor, inflightAttachmentUpload, requestId, responder, _accessLogItem, nexus.logger)));
                  if (handlerMade != null) {
                    inflightAttachmentUpload.put(requestId, handlerMade);
                    handlerMade.bind();
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
          }
          responder.error(new ErrorCodeException(ErrorCodes.API_METHOD_NOT_FOUND));
        }
      });
    } catch (ErrorCodeException ex) {
      responder.error(ex);
    }
  }
}

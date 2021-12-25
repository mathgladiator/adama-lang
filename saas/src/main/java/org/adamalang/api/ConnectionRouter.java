package org.adamalang.api;


import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.io.*;

import java.util.HashMap;
import java.util.Map;

public class ConnectionRouter {
  public final ConnectionNexus nexus;
  public final RootHandler handler;
  public final HashMap<Long, AttachmentUploadHandler> inflightAttachmentUpload;
  public final HashMap<Long, WaitingForEmailHandler> inflightWaitingForEmail;
  public final HashMap<Long, DocumentStreamHandler> inflightDocumentStream;

  public ConnectionRouter(ConnectionNexus nexus, RootHandler handler) {
    this.nexus = nexus;
    this.handler = handler;
    this.inflightAttachmentUpload = new HashMap<>();
    this.inflightWaitingForEmail = new HashMap<>();
    this.inflightDocumentStream = new HashMap<>();
  }

  public void disconnect() {
    for (Map.Entry<Long, AttachmentUploadHandler> entry : inflightAttachmentUpload.entrySet()) {
      entry.getValue().disconnect(entry.getKey());
    }
    inflightAttachmentUpload.clear();
    for (Map.Entry<Long, WaitingForEmailHandler> entry : inflightWaitingForEmail.entrySet()) {
      entry.getValue().disconnect(entry.getKey());
    }
    inflightWaitingForEmail.clear();
    for (Map.Entry<Long, DocumentStreamHandler> entry : inflightDocumentStream.entrySet()) {
      entry.getValue().disconnect(entry.getKey());
    }
    inflightDocumentStream.clear();
  }

  public void route(JsonRequest request, JsonResponder responder) {
    try {
      long requestId = request.id();
      String method = request.method();
      nexus.executor.execute(() -> {
        switch (method) {
          case "init/start": {
            InitStartRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(InitStartRequest resolved) {
                WaitingForEmailHandler handlerMade = handler.handle(resolved, new SimpleResponder(new JsonResponderHashMapCleanupProxy<>(nexus.executor, inflightWaitingForEmail, requestId, responder)));
                inflightWaitingForEmail.put(requestId, handlerMade);
                handlerMade.bind();
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "init/revoke-all": {
            InitRevokeAllRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(InitRevokeAllRequest resolved) {
                WaitingForEmailHandler handlerToUse = inflightWaitingForEmail.get(resolved.connection);
                if (handlerToUse != null) {
                  handlerToUse.handle(resolved, new SimpleResponder(responder));
                } else {
                  responder.error(new ErrorCodeException(441361));
                }
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "init/generate-identity": {
            InitGenerateIdentityRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(InitGenerateIdentityRequest resolved) {
                WaitingForEmailHandler handlerToUse = inflightWaitingForEmail.remove(resolved.connection);
                if (handlerToUse != null) {
                  handlerToUse.handle(resolved, new InitiationResponder(responder));
                } else {
                  responder.error(new ErrorCodeException(454673));
                }
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "probe": {
            ProbeRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(ProbeRequest resolved) {
                handler.handle(resolved, new SimpleResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "authority/create": {
            AuthorityCreateRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(AuthorityCreateRequest resolved) {
                handler.handle(resolved, new ClaimResultResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "authority/set": {
            AuthoritySetRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(AuthoritySetRequest resolved) {
                handler.handle(resolved, new SimpleResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "authority/transfer": {
            AuthorityTransferRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(AuthorityTransferRequest resolved) {
                handler.handle(resolved, new SimpleResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "authority/list": {
            AuthorityListRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(AuthorityListRequest resolved) {
                handler.handle(resolved, new AuthorityListingResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "authority/destroy": {
            AuthorityDestroyRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(AuthorityDestroyRequest resolved) {
                handler.handle(resolved, new SimpleResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "space/create": {
            SpaceCreateRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(SpaceCreateRequest resolved) {
                handler.handle(resolved, new SimpleResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "space/get": {
            SpaceGetRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(SpaceGetRequest resolved) {
                handler.handle(resolved, new PlanResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "space/set": {
            SpaceSetRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(SpaceSetRequest resolved) {
                handler.handle(resolved, new SimpleResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "space/delete": {
            SpaceDeleteRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(SpaceDeleteRequest resolved) {
                handler.handle(resolved, new SimpleResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "space/set-role": {
            SpaceSetRoleRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(SpaceSetRoleRequest resolved) {
                handler.handle(resolved, new SimpleResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "space/owner/set": {
            SpaceOwnerSetRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(SpaceOwnerSetRequest resolved) {
                handler.handle(resolved, new SimpleResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "space/reflect": {
            SpaceReflectRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(SpaceReflectRequest resolved) {
                handler.handle(resolved, new ReflectionResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "space/list": {
            SpaceListRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(SpaceListRequest resolved) {
                handler.handle(resolved, new SpaceListingResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "document/create": {
            DocumentCreateRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(DocumentCreateRequest resolved) {
                handler.handle(resolved, new SimpleResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "document/list": {
            DocumentListRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(DocumentListRequest resolved) {
                handler.handle(resolved, new SimpleResponder(responder));
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "connection/create": {
            ConnectionCreateRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(ConnectionCreateRequest resolved) {
                DocumentStreamHandler handlerMade = handler.handle(resolved, new DataResponder(new JsonResponderHashMapCleanupProxy<>(nexus.executor, inflightDocumentStream, requestId, responder)));
                inflightDocumentStream.put(requestId, handlerMade);
                handlerMade.bind();
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "connection/send": {
            ConnectionSendRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(ConnectionSendRequest resolved) {
                DocumentStreamHandler handlerToUse = inflightDocumentStream.get(resolved.connection);
                if (handlerToUse != null) {
                  handlerToUse.handle(resolved, new SimpleResponder(responder));
                } else {
                  responder.error(new ErrorCodeException(457745));
                }
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "connection/end": {
            ConnectionEndRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(ConnectionEndRequest resolved) {
                DocumentStreamHandler handlerToUse = inflightDocumentStream.remove(resolved.connection);
                if (handlerToUse != null) {
                  handlerToUse.handle(resolved, new SimpleResponder(responder));
                } else {
                  responder.error(new ErrorCodeException(474128));
                }
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "attachment/start": {
            AttachmentStartRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(AttachmentStartRequest resolved) {
                AttachmentUploadHandler handlerMade = handler.handle(resolved, new SimpleResponder(new JsonResponderHashMapCleanupProxy<>(nexus.executor, inflightAttachmentUpload, requestId, responder)));
                inflightAttachmentUpload.put(requestId, handlerMade);
                handlerMade.bind();
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "attachment/append": {
            AttachmentAppendRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(AttachmentAppendRequest resolved) {
                AttachmentUploadHandler handlerToUse = inflightAttachmentUpload.get(resolved.upload);
                if (handlerToUse != null) {
                  handlerToUse.handle(resolved, new SimpleResponder(responder));
                } else {
                  responder.error(new ErrorCodeException(477201));
                }
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
          case "attachment/finish": {
            AttachmentFinishRequest.resolve(nexus, request, new Callback<>() {
              @Override
              public void success(AttachmentFinishRequest resolved) {
                AttachmentUploadHandler handlerToUse = inflightAttachmentUpload.get(resolved.upload);
                if (handlerToUse != null) {
                  handlerToUse.handle(resolved, new SimpleResponder(responder));
                } else {
                  responder.error(new ErrorCodeException(478227));
                }
              }
              @Override
              public void failure(ErrorCodeException ex) {
                responder.error(ex);
              }
            });
          } return;
        }
        responder.error(new ErrorCodeException(42));
      });
    } catch (ErrorCodeException ex) {
      responder.error(ex);
    }
  }
}

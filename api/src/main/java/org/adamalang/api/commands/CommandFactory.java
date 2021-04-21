package org.adamalang.api.commands;

import org.adamalang.api.commands.admin.Reflect;
import org.adamalang.api.commands.contracts.Backbone;
import org.adamalang.api.commands.contracts.Command;
import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.api.commands.document.*;
import org.adamalang.api.operations.CounterFactory;
import org.adamalang.api.operations.RequestMetrics;
import org.adamalang.api.session.Session;
import org.adamalang.runtime.exceptions.ErrorCodeException;

/** find a command from a request and instrument */
public class CommandFactory {
  private final Backbone backbone;

  private final RequestMetrics admin_reflect;

  private final RequestMetrics document_attach;
  private final RequestMetrics document_connect;
  private final RequestMetrics document_create;
  private final RequestMetrics document_delete;
  private final RequestMetrics document_disconnect;
  private final RequestMetrics document_reserve;
  private final RequestMetrics document_rewind;
  private final RequestMetrics document_send;
  private final RequestMetrics document_unsend;

  public CommandFactory(Backbone backbone, CounterFactory factory) {
    this.backbone = backbone;
    this.admin_reflect = new RequestMetrics(factory, "admin_reflect");

    this.document_attach = new RequestMetrics(factory, "doc_attach");
    this.document_connect = new RequestMetrics(factory, "doc_connect");
    this.document_create = new RequestMetrics(factory, "doc_create");
    this.document_delete = new RequestMetrics(factory, "doc_delete");
    this.document_disconnect = new RequestMetrics(factory, "doc_disconnect");
    this.document_reserve = new RequestMetrics(factory, "doc_reserve");
    this.document_rewind = new RequestMetrics(factory, "doc_rewind");
    this.document_send = new RequestMetrics(factory, "doc_send");
    this.document_unsend = new RequestMetrics(factory, "doc_unsend");
  }

  private RequestContext context(Session session, RequestMetrics metrics, CommandResponder responder) {
    return new RequestContext(backbone, session, RequestMetrics.wrap(responder, metrics));
  }

  public Command findAndInstrument(Request request, Session session, CommandResponder responder) throws ErrorCodeException {
    switch (request.method()) {
      // administrative commands
      case "reflect":
        return Reflect.validateAndParse(context(session, admin_reflect, responder), request);

      // document commands
      case "attach":
        return Attach.validateAndParse(context(session, document_attach, responder), request);
      case "connect":
        return Connect.validateAndParse(context(session, document_connect, responder), request);
      case "create":
        return Create.validateAndParse(context(session, document_create, responder), request);
      case "delete":
        return Delete.validateAndParse(context(session, document_delete, responder), request);
      case "disconnect":
        return Disconnect.validateAndParse(context(session, document_disconnect, responder), request);
      case "reserve":
        return Reserve.validateAndParse(context(session, document_reserve, responder), request);
      case "rewind":
        return Rewind.validateAndParse(context(session, document_rewind, responder), request);
      case "send":
        return Send.validateAndParse(context(session, document_send, responder), request);
      case "unsend":
        return Unsend.validateAndParse(context(session, document_unsend, responder), request);
      default:
        throw new ErrorCodeException(ErrorCodes.USERLAND_REQUEST_INVALID_METHOD_PROPERTY);
    }
  }
}

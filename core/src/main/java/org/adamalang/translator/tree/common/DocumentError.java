/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.common;

import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** Defines an error within the document that can also be tied to a specific
 * position within the document */
public class DocumentError {
  public final String message;
  public final DocumentPosition position;
  public final String tutorial;

  /** construct the error
   *
   * @param position where within the file the error happened
   * @param message  what is the message for the error */
  public DocumentError(final DocumentPosition position, final String message, final String tutorial) {
    if (position == null || message == null) { throw new NullPointerException(); }
    this.message = message;
    this.position = position;
    this.tutorial = tutorial;
  }

  /** create a json notification for this error (in LSP) */
  public ObjectNode toPublishableDiagnostic() {
    final var response = Utility.createObjectNode();
    response.put("jsonrpc", "2.0");
    response.put("method", "textDocument/publishDiagnostics");
    final var responseParams = response.putObject("params");
    final var diagnostics = responseParams.putArray("diagnostics");
    writeAsLanguageServerDiagnostic(diagnostics.addObject());
    return response;
  }

  /** write the error out into the given ObjectNode using the LSP format */
  public void writeAsLanguageServerDiagnostic(final ObjectNode diagnostic) {
    final var range = diagnostic.putObject("range");
    position.writeAsLanguageServerDiagnostic(range);
    diagnostic.put("severity", 1);
    diagnostic.put("source", "error");
    diagnostic.put("message", tutorial == null ? message : message + "(" + tutorial + ")");
  }
}

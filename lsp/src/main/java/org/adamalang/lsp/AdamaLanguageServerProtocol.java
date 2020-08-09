/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.lsp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.exceptions.ParseException;
import org.adamalang.translator.parser.exceptions.ScanException;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.common.DocumentError;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AdamaLanguageServerProtocol {
  private final AtomicInteger nameId;

  public AdamaLanguageServerProtocol(AtomicInteger nameId) {
    this.nameId = nameId;
  }

  private ObjectNode craftResponse(ObjectNode request, boolean copyId) {
    ObjectNode response = Utility.createObjectNode();
    response.put("jsonrpc", "2.0");
    if (copyId && request.has("id")) {
      response.set("id", request.get("id"));
    }
    return response;
  }

  public ObjectNode handle(ObjectNode request) throws Exception {
    if (!request.has("method")) {
      throw new Exception("request has no method");
    }

    // extract the parameters
    ObjectNode params = null;
    if (request.has("params")) {
      JsonNode paramsNode = request.get("params");
      if (paramsNode != null && paramsNode.isObject()) {
        params = (ObjectNode) paramsNode;
      }
    }

    String method = request.get("method").textValue();
    switch (method) {
      case "initialize": {
        ObjectNode response = craftResponse(request, true);
        ObjectNode sync = response.putObject("result").putObject("capabilities").putObject("textDocumentSync");
        sync.put("openClose", true);
        sync.put("change", 1);
        return response;
      }
      case "initialized": {
        return null;
      }
      case "textDocument/didOpen":
      case "textDocument/didChange":
        ObjectNode response = craftResponse(request, true);
        response.put("method", "textDocument/publishDiagnostics");
        ObjectNode responseParams = response.putObject("params");
        if (params == null) {
          throw new Exception("request has no params");
        }
        JsonNode textDocumentNode = params.get("textDocument");
        if (textDocumentNode == null || !textDocumentNode.isObject()) {
          throw new Exception("params has no textDocument");
        }
        ObjectNode textDocumentObjectNode = (ObjectNode) textDocumentNode;
        if (!textDocumentNode.has("uri")) {
          throw new Exception("params has no uri");
        }
        JsonNode uriNode = textDocumentObjectNode.get("uri");
        if (uriNode == null || !uriNode.isTextual()) {
          throw new Exception("uri node is not valid");
        }
        String uri = uriNode.textValue();
        responseParams.put("uri", uri);
        String text = null;
        if (textDocumentObjectNode.has("text")) {
          JsonNode textNode = textDocumentObjectNode.get("text");
          if (textNode != null && textNode.isTextual()) {
            text = textNode.textValue();
          } else {
            throw new Exception("has no text field available");
          }
        } else if (params.has("contentChanges")) {
          JsonNode contentChangesNode = params.get("contentChanges");
          if (contentChangesNode != null && contentChangesNode.isArray()) {
            ArrayNode contentChangesArrayNode = (ArrayNode) contentChangesNode;
            if (contentChangesArrayNode.size() == 1) {
              JsonNode firstChild = contentChangesArrayNode.get(0);
              if (firstChild != null && firstChild.has("text")) {
                JsonNode firstChildText = firstChild.get("text");
                if (firstChildText != null && firstChildText.isTextual()) {
                  text = firstChildText.textValue();
                } else {
                  throw new Exception("handler text change no present 1");
                }
              } else {
                throw new Exception("handler text change no present 2");
              }
            } else {
              throw new Exception("handler text change no present 3");
            }
          } else {
            throw new Exception("handler text change no present 4");
          }
        } else {
          throw new Exception("no text field and no content changes");
        }
        ArrayNode diagnostics = responseParams.putArray("diagnostics");
        code(text, diagnostics);
        return response;
    }
    return null;
  }

  private void code(String code, ArrayNode diagnostics) throws Exception {
    GlobalObjectPool globals = GlobalObjectPool.createPoolWithStdLib();
    CompilerOptions options = CompilerOptions.start().enableCodeCoverage().make();
    EnvironmentState state = new EnvironmentState(globals, options);
    String className = "XGen" + nameId.incrementAndGet();
    boolean success = false;
    try {
      // create a lexer
      TokenEngine tokenEngine = new TokenEngine(code, code.codePoints().iterator());
      Parser parser = new Parser(tokenEngine);
      Document document = new Document();
      parser.document().accept(document);

      // typecheck and compile the document
      document.setClassName(className);
      if (document.check(state)) {
        // TODO: run the tests, and emit issues!
        return;
      }
      // forward the errors to the input array
      document.writeErrorsAsLanguageServerDiagnosticArray(diagnostics);
    } catch (ScanException se) {
      DocumentError error = new DocumentError(se.position, se.getMessage(), "Scanner");
      error.writeAsLanguageServerDiagnostic(diagnostics.addObject());
    } catch (ParseException pe) {
      DocumentError error = new DocumentError(pe.toDocumentPosition(), pe.rawMessage, "Parser");
      error.writeAsLanguageServerDiagnostic(diagnostics.addObject());
    }
  }

  public static byte[] encode(ObjectNode json) throws Exception {
    byte[] body = json.toString().getBytes();
    byte[] header = ("Content-Length: " + body.length + "\r\n\r\n").getBytes();
    ByteArrayOutputStream memory = new ByteArrayOutputStream();
    memory.write(header);
    memory.write(body);
    return memory.toByteArray();
  }

  public void drive(InputStream input, OutputStream output) throws Exception {
    BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(input)));
    HashMap<String, String> headers = new HashMap<>();
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.equals("")) {
        String lengthString = headers.get("content-length");
        if (lengthString == null) {
          System.err.println("Failed to Extract Content Length");
          throw new IOException("failed to find a content-length");
        }
        int length = Integer.parseInt(lengthString);
        char[] buffer = new char[length];
        int readIn = 0;
        while (readIn < buffer.length) {
          readIn += reader.read(buffer, readIn, buffer.length - readIn);
        }
        ObjectNode response = handle(Utility.parseJsonObject(new String(buffer)));
        if (response != null) {
          output.write(encode(response));
          output.flush();
        }
      } else {
        int kColon = line.indexOf(":");
        if (kColon > 0) {
          headers.put(line.substring(0, kColon).trim().toLowerCase(), line.substring(kColon + 1).trim());
        } else {
          throw new IOException("failed to read a colon in header");
        }
      }
    }
  }

}

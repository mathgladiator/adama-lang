/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.language;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Pathing;
import org.adamalang.devbox.DiagnosticsSubscriber;
import org.adamalang.devbox.TerminalIO;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.SymbolIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/** an implementation of the language protocl built for establishing a connection between the devbox and an editor */
public class LanguageProtocol implements DiagnosticsSubscriber {
  private static final Logger LOGGER = LoggerFactory.getLogger(LanguageProtocol.class);
  private final InputStream input;
  private final OutputStream output;
  private final HashMap<String, Binding> bindings;
  private final TerminalIO io;
  private ArrayNode lastDiagnostics;
  private SymbolIndex lastIndex;

  public LanguageProtocol(InputStream input, OutputStream output, TerminalIO io) {
    this.input = input;
    this.output = output;
    this.io = io;
    this.bindings = new HashMap<>();
    this.lastDiagnostics = null;
    this.lastIndex = null;
  }

  public void drive() throws Exception {
    BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(input)));
    HashMap<String, String> headers = new HashMap<>();
    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.equals("")) {
        String lengthString = headers.get("content-length");
        if (lengthString == null) {
          io.error("lsp|protocol-failure due to no content-length");
          throw new IOException("failed to find a content-length");
        }
        int length = Integer.parseInt(lengthString);
        char[] buffer = new char[length];
        int readIn = 0;
        while (readIn < buffer.length) {
          readIn += reader.read(buffer, readIn, buffer.length - readIn);
        }
        try {
          ObjectNode request = (ObjectNode) new JsonMapper().readTree(new String(buffer));
          ObjectNode response = handle(request);
          if (response != null) {
            output.write(encode(response));
            output.flush();
          }
        } catch (Exception ex) {
          io.error("lsp|core-failure! " + ex.getMessage());
          LOGGER.error("lsp-failed", ex);
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

  public ObjectNode handle(ObjectNode request) throws Exception {
    if (!request.has("method")) {
      throw new Exception("request has no method");
    }
    String method = request.get("method").textValue();
    switch (method) {
      case "initialize": {
        ObjectNode response = craftResponse(request, true);
        ObjectNode caps = response.putObject("result").putObject("capabilities");
        ObjectNode textDocumentSync = caps.putObject("textDocumentSync");
        textDocumentSync.put("openClose", true);
        textDocumentSync.put("change", 0);
        // caps.put("hoverProvider", true);
        caps.put("definitionProvider", true);
        caps.put("referencesProvider", true);
        // caps.put("documentFormattingProvider", true);
        return response;
      }
      case "initialized": {
        return null;
      }
      case "textDocument/didOpen": {
        String uri = request.get("params").get("textDocument").get("uri").textValue();
        io.witness("ide|open=" + uri);
        Binding binding = new Binding(uri, craftResponse(request, true));
        synchronized (bindings) {
          bindings.put(uri, binding);
        }
        ArrayNode fireOnThis = null;
        synchronized (this) {
          fireOnThis = lastDiagnostics;
        }
        if (fireOnThis != null) {
          fire(fireOnThis, uri);
        }
        return null;
      }
      case "textDocument/didClose": {
        String uri = request.get("params").get("textDocument").get("uri").textValue();
        io.witness("ide|close=" + uri);
        synchronized (bindings) {
          bindings.remove(uri);
        }
        return null;
      }
      case "textDocument/hover": {
        ObjectNode textDocument = (ObjectNode) request.get("params").get("textDocument");
        String uri = textDocument.get("uri").textValue();
        int line = textDocument.get("position").get("line").intValue();
        int ch = textDocument.get("position").get("character").intValue();
        ObjectNode response = craftResponse(request, true);
        // TODO: query the built index of interesting things
        return null;
      }
      case "textDocument/references": {
        ObjectNode textDocument = (ObjectNode) request.get("params").get("textDocument");
        String uri = textDocument.get("uri").textValue();
        ObjectNode position = (ObjectNode) request.get("params").get("position");
        int ln = position.get("line").intValue();
        int ch = position.get("character").intValue();
        ObjectNode response = craftResponse(request, true);
        ArrayNode result = response.putArray("result");
        SymbolIndex indexCopy = null;
        synchronized (this) {
          indexCopy = lastIndex;
        }
        boolean foundAny = false;
        if (indexCopy != null) {
          BuiltSymbolsIndex index = new BuiltSymbolsIndex(indexCopy);
          String foundSource = index.findBestMatch(uri);
          if (foundSource != null) {
            Token token = index.tokenAt(foundSource, ln, ch);
            if (token != null) {
              String commonSuffix = Pathing.maxSharedSuffix(foundSource, uri);
              ArrayList<Token> usages = index.findUsages(token);
              if (usages != null) {
                foundAny = true;
                for (Token usage : usages) {
                  String whatIsShared = Pathing.maxSharedPrefix(foundSource, usage.sourceName);
                  String newPrefix = uri.substring(0, uri.length() - commonSuffix.length());
                  int trim = foundSource.substring(0, foundSource.length() - commonSuffix.length()).length();
                  String finalFile = newPrefix + whatIsShared.substring(trim) + usage.sourceName.substring(whatIsShared.length());
                  ObjectNode location = result.addObject();
                  location.put("uri", finalFile);
                  ObjectNode range = location.putObject("range");
                  ObjectNode start = range.putObject("start");
                  start.put("line", usage.lineStart);
                  start.put("character", usage.charStart);
                  ObjectNode end = range.putObject("end");
                  end.put("line", usage.lineEnd);
                  end.put("character", usage.charEnd);
                }
              }
            }
          }
        }
        if (!foundAny) {
          response.putNull("result");
        }
        return response;
      }
      case "textDocument/definition": {
        ObjectNode textDocument = (ObjectNode) request.get("params").get("textDocument");
        String uri = textDocument.get("uri").textValue();
        ObjectNode position = (ObjectNode) request.get("params").get("position");
        int ln = position.get("line").intValue();
        int ch = position.get("character").intValue();
        ObjectNode response = craftResponse(request, true);
        ObjectNode result = response.putObject("result");
        SymbolIndex indexCopy = null;
        synchronized (this) {
          indexCopy = lastIndex;
        }
        boolean found = false;
        if (indexCopy != null) {
          BuiltSymbolsIndex index = new BuiltSymbolsIndex(indexCopy);
          String foundSource = index.findBestMatch(uri);
          if (foundSource != null) {
            Token token = index.tokenAt(foundSource, ln, ch);
            if (token != null) {
              String commonSuffix = Pathing.maxSharedSuffix(foundSource, uri);
              Token defn = index.findDefinition(token);
              if (defn != null) {
                String whatIsShared = Pathing.maxSharedPrefix(foundSource, defn.sourceName);
                String newPrefix = uri.substring(0, uri.length() - commonSuffix.length());;
                int trim = foundSource.substring(0, foundSource.length() - commonSuffix.length()).length();
                String finalFile = newPrefix + whatIsShared.substring(trim) + defn.sourceName.substring(whatIsShared.length());
                result.put("uri", finalFile);
                ObjectNode range = result.putObject("range");
                ObjectNode start = range.putObject("start");
                start.put("line", defn.lineStart);
                start.put("character", defn.charStart);
                ObjectNode end = range.putObject("end");
                end.put("line", defn.lineEnd);
                end.put("character", defn.charEnd);
                found = true;
              }
            }
          }
        }
        if (!found) {
          response.putNull("result");
        }
        return response;
      }
      case "textDocument/formatting": {
        ObjectNode textDocument = (ObjectNode) request.get("params").get("textDocument");
        String uri = textDocument.get("uri").textValue();
        ObjectNode response = craftResponse(request, true);
        // TODO: find the file, format it,
        return null;
      }
      case "$/setTrace": {
        return null;
      }
      default:
    }
    return null;
  }

  public static byte[] encode(ObjectNode json) throws Exception {
    byte[] body = json.toString().getBytes();
    byte[] header = ("Content-Length: " + body.length + "\r\n\r\n").getBytes();
    ByteArrayOutputStream memory = new ByteArrayOutputStream();
    memory.write(header);
    memory.write(body);
    return memory.toByteArray();
  }

  private ObjectNode craftResponse(ObjectNode request, boolean copyId) {
    ObjectNode response = new JsonMapper().createObjectNode();
    response.put("jsonrpc", "2.0");
    if (copyId && request.has("id")) {
      response.set("id", request.get("id"));
    }
    return response;
  }

  private void fire(ArrayNode last, String focusedOn) {
    HashMap<String, String> normalized = new HashMap<>();
    HashMap<String, ObjectNode> requests = new HashMap<>();

    synchronized (bindings) {
      for (Binding binding : bindings.values()) {
        ObjectNode reset = binding.cloneForReset();
        if (reset != null) {
          requests.put(binding.uri, reset);
        }
      }
    }

    for (int k = 0; k < last.size(); k++) {
      ObjectNode errorItem = last.get(k).deepCopy();
      String file = errorItem.has("file") ? errorItem.get("file").textValue() : "unknown-file (bug)";
      String nfile = normalized.get(file);
      if (nfile == null) {
        Binding binding = findBinding(file);
        if (binding != null) {
          nfile = binding.uri;
          normalized.put(file, nfile);
          ObjectNode cloned = binding.cloneForDirty();
          requests.put(nfile, cloned);
        }
      }
      ObjectNode request = null;
      if (nfile != null) {
        request = requests.get(nfile);
        if (request != null) {
          errorItem.remove("file");
          ((ArrayNode) request.get("params").get("diagnostics")).add(errorItem);
        }
      }
    }

    try {
      if (focusedOn == null) {
        for (ObjectNode request : requests.values()) {
          publish(request);
        }
      } else {
        publish(requests.get(focusedOn));
      }
    } catch (Exception ex) {
      io.error("lsp|failed publishing '" + ex.getMessage() + "'");
      LOGGER.error("lsp-failed-publish", ex);
    }
  }

  private Binding findBinding(String uri) {
    int winnerLen = 0;
    Binding winner = null;
    synchronized (bindings) {
      for (String file : new ArrayList<>(bindings.keySet())) {
        String candidate = Pathing.maxSharedSuffix(uri, file);
        int candidateLen = candidate.length();
        if (candidateLen > winnerLen) {
          winnerLen = candidateLen;
          winner = bindings.get(file);
        }
      }
    }
    return winner;
  }

  private void publish(ObjectNode node) throws Exception {
    if (node != null) {
      output.write(encode(node));
      output.flush();
    }
  }

  @Override
  public void updated(ArrayNode diagnostics) {
    synchronized (this) {
      lastDiagnostics = diagnostics;
    }
    fire(diagnostics, null);
  }

  @Override
  public void indexed(SymbolIndex index) {
    synchronized (this) {
      lastIndex = index;
    }
  }

  class Binding {
    private final String uri;
    private final ObjectNode basis;
    private boolean dirty;

    public Binding(String uri, ObjectNode basis) {
      this.uri = uri;
      this.basis = basis;
      basis.put("method", "textDocument/publishDiagnostics");
      ObjectNode params = basis.putObject("params");
      params.put("uri", uri);
      params.putArray("diagnostics");
      this.dirty = false;
    }

    public ObjectNode cloneForDirty() {
      dirty = true;
      return basis.deepCopy();
    }

    public ObjectNode cloneForReset() {
      if (dirty) {
        dirty = false;
        return basis.deepCopy();
      }
      return null;
    }
  }
}

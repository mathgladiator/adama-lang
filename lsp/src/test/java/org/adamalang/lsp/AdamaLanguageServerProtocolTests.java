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
package org.adamalang.lsp;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class AdamaLanguageServerProtocolTests {
  private static AtomicInteger id = new AtomicInteger();

  @Test
  public void testNoMethod() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(JsonHelp.parseJsonObject("{}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("request has no method", e.getMessage());
    }
  }

  @Test
  public void testInitialize() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    ObjectNode response = protocol.handle(JsonHelp.parseJsonObject("{\"method\":\"initialize\"}"));
    Assert.assertEquals(
        "{\"jsonrpc\":\"2.0\",\"result\":{\"capabilities\":{\"textDocumentSync\":{\"openClose\":true,\"change\":1}}}}",
        response.toString());
  }

  @Test
  public void testInitialized() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    ObjectNode response = protocol.handle(JsonHelp.parseJsonObject("{\"method\":\"initialized\"}"));
    Assert.assertEquals(null, response);
  }

  @Test
  public void testInvalidMethod() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    ObjectNode response = protocol.handle(JsonHelp.parseJsonObject("{\"method\":\"dropme\"}"));
    Assert.assertEquals(null, response);
  }

  @Test
  public void incompleteDidChangeNoParams() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(JsonHelp.parseJsonObject("{\"method\":\"textDocument/didOpen\"}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("request has no params", e.getMessage());
    }
  }

  @Test
  public void incompleteDidChangeNoTextDocumentNotPresent() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(
          JsonHelp.parseJsonObject("{\"method\":\"textDocument/didChange\",\"params\":{}}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("params has no textDocument", e.getMessage());
    }
  }

  @Test
  public void incompleteDidChangeNoTextDocumentNull() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(
          JsonHelp.parseJsonObject(
              "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":null}}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("params has no textDocument", e.getMessage());
    }
  }

  @Test
  public void incompleteDidChangeNoTextDocumentNotObject() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(
          JsonHelp.parseJsonObject(
              "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":true}}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("params has no textDocument", e.getMessage());
    }
  }

  @Test
  public void incompleteDidChangeNoTextDocumentEmptyObject() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(
          JsonHelp.parseJsonObject(
              "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{}}}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("params has no uri", e.getMessage());
    }
  }

  @Test
  public void incompleteDidChangeUriIsNull() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(
          JsonHelp.parseJsonObject(
              "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":null}}}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("uri node is not valid", e.getMessage());
    }
  }

  @Test
  public void incompleteDidChangeUriIsInt() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(
          JsonHelp.parseJsonObject(
              "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":1232}}}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("uri node is not valid", e.getMessage());
    }
  }

  @Test
  public void incompleteDidChangeNoContent() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(
          JsonHelp.parseJsonObject(
              "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"URI\"}}}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("no text field and no content changes", e.getMessage());
    }
  }

  @Test
  public void incompleteDidChangeInvalidText() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(
          JsonHelp.parseJsonObject(
              "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"URI\",\"text\":null}}}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("has no text field available", e.getMessage());
    }
  }

  @Test
  public void incompleteDidChangeInvalidChange1() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(
          JsonHelp.parseJsonObject(
              "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"URI\"},\"contentChanges\":null}}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("handler text change no present 4", e.getMessage());
    }
  }

  @Test
  public void incompleteDidChangeInvalidChange2() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(
          JsonHelp.parseJsonObject(
              "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"URI\"},\"contentChanges\":1}}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("handler text change no present 4", e.getMessage());
    }
  }

  @Test
  public void incompleteDidChangeInvalidChange3() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(
          JsonHelp.parseJsonObject(
              "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"URI\"},\"contentChanges\":[]}}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("handler text change no present 3", e.getMessage());
    }
  }

  @Test
  public void incompleteDidChangeInvalidChange4() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(
          JsonHelp.parseJsonObject(
              "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"URI\"},\"contentChanges\":[{}]}}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("handler text change no present 2", e.getMessage());
    }
  }

  @Test
  public void incompleteDidChangeInvalidChange5() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    try {
      protocol.handle(
          JsonHelp.parseJsonObject(
              "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"URI\"},\"contentChanges\":[{\"text\":null}]}}"));
      Assert.fail();
    } catch (Exception e) {
      Assert.assertEquals("handler text change no present 1", e.getMessage());
    }
  }

  @Test
  public void validViaChange() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    ObjectNode response =
        protocol.handle(
            JsonHelp.parseJsonObject(
                "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"URI\",\"text\":\"#sm{}\"}}}"));
    Assert.assertEquals(
        "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/publishDiagnostics\",\"params\":{\"uri\":\"URI\",\"diagnostics\":[]}}",
        response.toString());
  }

  @Test
  public void validViaContentChange() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    ObjectNode response =
        protocol.handle(
            JsonHelp.parseJsonObject(
                "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"URI\"},\"contentChanges\":[{\"text\":\"#sm{}\"}]}}"));
    Assert.assertEquals(
        "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/publishDiagnostics\",\"params\":{\"uri\":\"URI\",\"diagnostics\":[]}}",
        response.toString());
  }

  @Test
  public void parseIssue() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    ObjectNode response =
        protocol.handle(
            JsonHelp.parseJsonObject(
                "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"URI\",\"text\":\"#sm\"}}}"));
    Assert.assertEquals(
        "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/publishDiagnostics\",\"params\":{\"uri\":\"URI\",\"diagnostics\":[{\"range\":{\"start\":{\"line\":0,\"character\":0,\"byte\":0},\"end\":{\"line\":0,\"character\":3,\"byte\":3}},\"severity\":1,\"source\":\"error\",\"message\":\"Parser was expecting an atomic expression, but got end of stream instead.\",\"file\":\"#sm\"}]}}",
        response.toString());
  }

  @Test
  public void typeIssue() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    ObjectNode response =
        protocol.handle(
            JsonHelp.parseJsonObject(
                "{\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"URI\",\"text\":\"#sm { int x = true; }\"}}}"));
    Assert.assertEquals(
        "{\"jsonrpc\":\"2.0\",\"method\":\"textDocument/publishDiagnostics\",\"params\":{\"uri\":\"URI\",\"diagnostics\":[{\"range\":{\"start\":{\"line\":0,\"character\":6,\"byte\":6},\"end\":{\"line\":0,\"character\":9,\"byte\":9}},\"severity\":1,\"source\":\"error\",\"message\":\"Type check failure: the type 'int' is unable to store type 'bool'.\",\"file\":\"#sm { int x = true; }\"}]}}",
        response.toString());
  }

  @Test
  public void driveNoContentLength() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    ByteArrayOutputStream memory = new ByteArrayOutputStream();
    try {
      protocol.drive(new ByteArrayInputStream("\r\n\r\n".getBytes()), memory);
    } catch (Exception err) {
      Assert.assertEquals("failed to find a content-length", err.getMessage());
    }
  }

  @Test
  public void driveNoColon() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    ByteArrayOutputStream memory = new ByteArrayOutputStream();
    try {
      protocol.drive(new ByteArrayInputStream("x\r\n\r\n".getBytes()), memory);
    } catch (Exception err) {
      Assert.assertEquals("failed to read a colon in header", err.getMessage());
    }
  }

  @Test
  public void driveError() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    ByteArrayOutputStream memory = new ByteArrayOutputStream();
    try {
      protocol.drive(sum(JsonHelp.parseJsonObject("{\"id\":42}")), memory);
    } catch (Exception err) {
      Assert.assertEquals("request has no method", err.getMessage());
    }
  }

  public static InputStream sum(ObjectNode... nodes) throws Exception {
    ByteArrayOutputStream memory = new ByteArrayOutputStream();
    for (ObjectNode node : nodes) {
      memory.write(AdamaLanguageServerProtocol.encode(node));
    }
    memory.flush();
    return new ByteArrayInputStream(memory.toByteArray());
  }

  @Test
  public void driveSuccess() throws Exception {
    AdamaLanguageServerProtocol protocol = new AdamaLanguageServerProtocol(id);
    ByteArrayOutputStream memory = new ByteArrayOutputStream();
    protocol.drive(
        sum(
            JsonHelp.parseJsonObject(
                "{\"id\":42,\"method\":\"textDocument/didChange\",\"params\":{\"textDocument\":{\"uri\":\"URI\",\"text\":\"#sm { int x = true; }\"}}}")),
        memory);
    String result = new String(memory.toByteArray());
    Assert.assertEquals(364, result.length());
  }
}

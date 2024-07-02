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

import org.adamalang.devbox.TerminalIO;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/** the actual JSONRPC server thread */
public class LanguageServer {
  public final DiagnosticsPubSub pubsub;
  private final int port;
  private final TerminalIO io;
  private final AtomicBoolean alive;

  public LanguageServer(int port, TerminalIO io, AtomicBoolean alive) {
    this.pubsub = new DiagnosticsPubSub();
    this.port = port;
    this.io = io;
    this.alive = alive;
  }

  public void spinup() {
    Thread serverThread = new Thread(() -> {
      try {
        ServerSocket server = new ServerSocket(port);
        while (alive.get()) {
          Socket client = server.accept();
          io.info("lsp|client connected");
          Thread clientThread = new Thread(() -> {
            try {
              LanguageProtocol protocol = new LanguageProtocol(client.getInputStream(), client.getOutputStream(), io);
              Runnable kill = pubsub.create(protocol);
              try {
                protocol.drive();
              } finally {
                kill.run();
              }
            } catch (Exception ex) {
              io.error("lsp|client-failed:" + ex.getMessage());
            }
            try {
              client.close();
            } catch (Exception ex) {
              io.error("lsp|client-closure-exception:" + ex.getMessage());
            }
          });
          clientThread.setName("lsp-client-thread-" + client.getLocalPort());
          clientThread.start();
        }
      } catch (Exception ex) {
        io.error("lsp|server-exception:" + ex.getMessage());
      }
    });
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        serverThread.interrupt();
      }
    }));
    serverThread.start();
  }
}

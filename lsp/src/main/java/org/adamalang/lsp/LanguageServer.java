/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.lsp;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class LanguageServer {
  public static void singleThread(int port) throws Exception {
    AtomicInteger classNameId = new AtomicInteger();
    ServerSocket server = new ServerSocket(port);
    while (true) {
      Socket client = server.accept();
      System.err.println("connected");
      Thread clientThread = new Thread(() -> {
        try {
          new AdamaLanguageServerProtocol(classNameId).drive(client.getInputStream(), client.getOutputStream());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        forceClose(client);
      });
      clientThread.setName("lsp-client-thread-" + client.getLocalPort());
      clientThread.start();
    }
  }

  public static void forceClose(Socket socket) {
    try {
      socket.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}

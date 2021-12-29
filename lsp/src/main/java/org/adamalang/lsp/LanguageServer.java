/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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

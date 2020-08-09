/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.lsp;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class LanguageServer {
    public static void main(String[] args) throws Exception {
        AtomicInteger classNameId = new AtomicInteger();
        int port = 2423;
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

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.canary;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.*;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientConnection;
import org.adamalang.web.contracts.WebJsonStream;
import org.adamalang.web.contracts.WebLifecycle;
import org.adamalang.web.service.WebConfig;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CanaryLoadGenTool {
  public static void execute() {
    String canaryURL = ""; // TODO: stand up a ELB instance backed by some capacity
    String canarySpace = ""; // TODO: create and define a space, maybe have a tool for it
    String canaryIdentity = ""; // TODO: maybe automate this, maybe not... This requires an init flow OR an authorities setup for the space. Either way
    // TODO: create a scalable WebSocket client and use it here to spin up; this will need to be a MUCH better version than what the CLI uses as we intend to create SERIOUS load from a dev machine instance
    int distinctSockets = 100; // TODO: define as a parameter
    int connectionsPerSocket = 20; // TODO: define as a parameter
    int sendRatePerConnectionPerSecond = 5; // TODO: define as a parameter
  }

  public static void main(String[] args) {
    // TODO: move this to config
    String url = "http://adama-lb-us-east-2-2073537616.us-east-2.elb.amazonaws.com/s";
    WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(Json.newJsonObject())));
    SimpleExecutor other = SimpleExecutor.create("other");
    base.open(url, new WebLifecycle() {
      @Override
      public void connected(WebClientConnection connection) {
        System.err.println("connected");
        ObjectNode request = Json.newJsonObject();
        request.put("method", "connection/create");
        // TODO: revoke this, and move to config
        request.put("space", "demo1");
        request.put("key", "key3");
        // TODO: revoke this, and move to config
        request.put("identity", "eyJhbGciOiJFUzI1NiJ9.eyJzdWIiOiIxIiwiaXNzIjoiYWRhbWEifQ.eqo02oPRxALrmHUKRaUNHZWyr2cPLkP470gzuE1EjYEn1-VZDlYlh5cz-osZbdBSxuwC2nBKA7-_399kfCO-2A");
        CountDownLatch latchGotData = new CountDownLatch(1);
        long started = System.currentTimeMillis();
        int connectionId = connection.execute(request, new WebJsonStream() {
          @Override
          public void data(ObjectNode node) {
            System.err.println("DATA:" + node.toString());
            System.err.println("LATENCY:" + (System.currentTimeMillis() - started));
            latchGotData.countDown();
          }

          @Override
          public void complete() {
            System.err.println("FINISHED");
          }

          @Override
          public void failure(int code) {
            System.err.println("FAILURE:" + code);
          }
        });
        try {
          other.execute(new NamedRunnable("wait-to-send") {
            @Override
            public void execute() throws Exception {
              if (latchGotData.await(2000, TimeUnit.MILLISECONDS)) {
                ObjectNode send = Json.newJsonObject();
                send.put("method", "connection/send");
                send.put("connection", connectionId);
                send.put("channel", "foo");
                send.putObject("message");
                long sendStart = System.currentTimeMillis();
                connection.execute(send, new WebJsonStream() {
                  @Override
                  public void data(ObjectNode node) {
                    System.err.println("DATA:" + node.toString());
                    System.err.println("SEND LATENCY (Data):" + (System.currentTimeMillis() - sendStart));
                  }

                  @Override
                  public void complete() {
                    System.err.println("SEND LATENCY (Complete):" + (System.currentTimeMillis() - sendStart));
                  }

                  @Override
                  public void failure(int code) {
                    System.err.println("SEND FAILURE:" + code);
                  }
                });
            }
            }
          });
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }

      @Override
      public void ping(int latency) {
        System.err.println("Ping:" + latency);
      }

      @Override
      public void failure(Throwable t) {
        System.err.println("error");
      }

      @Override
      public void disconnected() {
        System.err.println("disconnected");
        // TODO: schedule retry
        // base.open(url, this);
      }
    });
  }
}

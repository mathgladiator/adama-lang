package org.adamalang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.api.util.Json;
import org.adamalang.data.disk.FileSystemLivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.DocumentThreadBase;
import org.adamalang.runtime.sys.DurableLivingDocument;
import org.adamalang.runtime.contracts.*;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.SimpleExecutor;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoPlay {
  public static void main(String[] args) {
    FileSystemLivingDocumentFactoryFactory factoryFactory = new FileSystemLivingDocumentFactoryFactory(new File("../adama-binary/schema"), CompilerOptions.start().make());
    factoryFactory.fetch(new Key("bsg", null), new Callback<LivingDocumentFactory>() {
      @Override
      public void success(LivingDocumentFactory value) {
        gotLivingDocumentFactory(value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
      }
    });
  }

  public static void gotLivingDocumentFactory(LivingDocumentFactory factory) {
    AtomicInteger writes = new AtomicInteger(0);
    DataService noop = new DataService() {
      @Override
      public void scan(ActiveKeyStream stream) {
        stream.finish();
      }

      @Override
      public void get(Key key, Callback<LocalDocumentChange> callback) {
      }

      @Override
      public void initialize(Key keyId, RemoteDocumentUpdate patch, Callback<Void> callback) {
        callback.success(null);
        writes.incrementAndGet();
      }

      @Override
      public void patch(Key key, RemoteDocumentUpdate patch, Callback<Void> callback) {
        callback.success(null);
        writes.incrementAndGet();
      }

      @Override
      public void compute(Key key, ComputeMethod method, int seq, Callback<LocalDocumentChange> callback) {
      }

      @Override
      public void delete(Key key, Callback<Void> callback) {
      }
    };
    StringBuilder arg = new StringBuilder();
    arg.append("{\"players\":[");
    arg.append("{\"player\":{\"agent\":\"alice\",\"authority\":\"benchmark\"}}");
    arg.append(",{\"player\":{\"agent\":\"bob\",\"authority\":\"benchmark\"}}");
    arg.append(",{\"player\":{\"agent\":\"carol\",\"authority\":\"benchmark\"}}");
    arg.append(",{\"player\":{\"agent\":\"dan\",\"authority\":\"benchmark\"}}");
    arg.append("]}");
    TimeSource TS = new TimeSource() {
      @Override
      public long nowMilliseconds() {
        return 1000;
      }
    };
    DocumentThreadBase base = new DocumentThreadBase(noop, SimpleExecutor.NOW, TS);
    for (int k = 0; k < 100; k++) {
      DurableLivingDocument.fresh(new Key("?", "" + k), factory, NtClient.NO_ONE, arg.toString(), "1", null, base, new Callback<>() {
        @Override
        public void success(DurableLivingDocument value) {
          try {
            gotDurableLivingDocument(value, writes);
          } catch (Exception ex) {
            throw new RuntimeException(ex);
          }
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
        }
      });
    }
  }

  public static class RandomRobot implements Perspective {
    private final CountDownLatch done;
    private final DurableLivingDocument document;
    private final NtClient who;
    private final Random rng;
    private final HashSet<Integer> respondedTo;
    private int billing;
    private final AtomicInteger writes;
    private boolean countedDown;

    public RandomRobot(CountDownLatch done, DurableLivingDocument document, NtClient who, AtomicInteger writes, int seed) {
      this.done = done;
      this.document = document;
      this.who = who;
      this.rng = new Random(seed);
      this.respondedTo = new HashSet<>();
      billing = 100;
      this.writes = writes;
      countedDown = false;
    }

    @Override
    public void data(String json) {
      ObjectNode data = Json.parseJsonObject(json);
      try {
        boolean finished = data.get("data").get("finished").asBoolean();
        if (finished) {
          if (!countedDown) {
            countedDown = true;
            done.countDown();
          }
          return;
        }
      } catch (NullPointerException npe) {
        // ignore
      }
      JsonNode outstandingNode = data.get("outstanding");
      if (outstandingNode != null && outstandingNode.isArray() && outstandingNode.size() > 0) {
        // report NOTDONE
        ArrayNode outstandingArray = (ArrayNode) outstandingNode;
        for (int k = 0; k < outstandingArray.size(); k++) {
          JsonNode decisionNode = outstandingArray.get(k);
          JsonNode decisionIdNode = decisionNode.get("id");
          if (decisionIdNode != null && decisionIdNode.isInt()) {
            int id = decisionIdNode.asInt();
            if (!respondedTo.contains(id)) {
              respondedTo.add(id);
              int count = decisionNode.get("options").size();
              String channel = decisionNode.get("channel").asText();
              String msg = decisionNode.get("options").get(rng.nextInt(count)).toString();
              document.send(who, "x", channel, msg, Callback.DONT_CARE_INTEGER);
            }
          }
        }
      }
    }

    @Override
    public void disconnect() {

    }
  }

  public static void gotDurableLivingDocument(DurableLivingDocument document, AtomicInteger writes) throws Exception {

    long started = System.currentTimeMillis();
    NtClient[] clients = new NtClient[] {
            new NtClient("alice", "benchmark"),
            new NtClient("bob", "benchmark"),
            new NtClient("carol", "benchmark"),
            new NtClient("dan", "benchmark"),
        };

    CountDownLatch done = new CountDownLatch(4);
    for(int k = 0; k < clients.length; k++) {
      document.connect(clients[k], Callback.DONT_CARE_INTEGER);
    }

    final ScheduledExecutorService bounce = Executors.newSingleThreadScheduledExecutor();
    for(int k = 0; k < clients.length; k++) {
      RandomRobot robot = new RandomRobot(done, document, clients[k], writes, k);
      final var kToUse = k;
      bounce.execute(() -> {
        document.createPrivateView(clients[kToUse], new Perspective() {
          @Override
          public void data(String data) {
            synchronized (bounce) {
              bounce.execute(() -> robot.data(data));
            }
          }

          @Override
          public void disconnect() {
          }
        }, new Callback<>() {
          @Override
          public void success(PrivateView value) {

          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
      });
    }
    done.await(2000, TimeUnit.MILLISECONDS);
    long delta = System.currentTimeMillis() - started;
    System.err.println("Delta:" + delta + "/" + writes.get());
    bounce.shutdown();
    writes.set(0);
  }

}


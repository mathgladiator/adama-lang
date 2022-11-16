/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.events.EventCodec;
import org.adamalang.caravan.events.Events;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.*;
import org.adamalang.common.jvm.MachineHeat;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.extern.aws.*;
import org.adamalang.runtime.data.Key;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.service.WebConfig;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AWS {
  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      awsHelp();
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "setup":
        awsSetup(config);
        return;
      case "test-email":
        awsTestEmail(config);
        return;
      case "test-asset-listing":
        awsTestAssetListing(next, config);
        return;
      case "test-enqueue":
        awsEnqueue(next, config);
        return;
      case "download-archive":
        awsDownloadArchive(next, config);
        return;
      case "memory-test":
        awsMemoryTest();
        return;
      case "help":
        awsHelp();
        return;
    }
  }

  public static void awsMemoryTest() throws Exception {
    MachineHeat.install();
    ArrayList<byte[]> chunks = new ArrayList<>();
    int MB = 0;
    System.out.println("memory MB, ms, %");
    while (true) {
      long started = System.nanoTime();
      chunks.add(new byte[1024*1024]);
      double taken = (System.nanoTime() - started) / 1000000.0;
      MB ++;
      System.out.println(MB + ", " + taken + ", " + MachineHeat.memory());
    }
  }

  public static void awsHelp() {
    System.out.println(Util.prefix("Production AWS Support.", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama aws", Util.ANSI.Green) + " " + Util.prefix("[AWSSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("AWSSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("setup", Util.ANSI.Green) + "             Interactive setup for the config");
    System.out.println("    " + Util.prefix("test-email", Util.ANSI.Green) + "        Test Email via AWS");
    System.out.println("    " + Util.prefix("download-archive", Util.ANSI.Green) + "  Download (and validate) an archive");
    System.out.println("    " + Util.prefix("memory-test", Util.ANSI.Green) + "       Crash by allocating memory");
    System.out.println("    " + Util.prefix("release", Util.ANSI.Green) + "           Release the binary to the world");
  }

  public static void awsDownloadArchive(String[] args, Config config) throws Exception {
    AWSConfig awsConfig = new AWSConfig(new ConfigObject(config.get_or_create_child("aws")));
    String archiveKey = Util.extractOrCrash("--archive", "-a", args);
    String space = Util.extractOrCrash("--space", "-s", args);
    String key = Util.extractOrCrash("--key", "-k", args);
    WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(config.get_or_create_child("web"))));
    try {
      CountDownLatch latch = new CountDownLatch(1);
      S3 s3 = new S3(base, awsConfig, new AWSMetrics(new NoOpMetricsFactory()));
      s3.restore(new Key(space, key), archiveKey, new Callback<File>() {
        @Override
        public void success(File archiveFile) {
          try {
            System.err.println("LOADED");
            int good = 0;
            try (DataInputStream input = new DataInputStream(new FileInputStream(archiveFile))) {
              while (input.readBoolean()) {
                byte[] bytes = new byte[input.readInt()];
                input.readFully(bytes);
                try {
                  EventCodec.route(Unpooled.wrappedBuffer(bytes), new EventCodec.HandlerEvent() {
                    @Override
                    public void handle(Events.Snapshot payload) {

                    }

                    @Override
                    public void handle(Events.Batch payload) {

                    }

                    @Override
                    public void handle(Events.Change payload) {

                    }
                  });
                  good++;
                } catch (Exception failedRoute) {
                  System.err.println("BAD! (prior good:" + good + "): " + failedRoute.getMessage());
                  failedRoute.printStackTrace();
                  good = 0;
                }
              }
            }
            System.err.println("Finished:" + good);
          } catch (Exception failedToScan) {
            failedToScan.printStackTrace();
          }
          archiveFile.delete();
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          System.err.println("Error:" + ex);
          latch.countDown();
        }
      });
      latch.await(60000, TimeUnit.MILLISECONDS);
    } finally {
      base.shutdown();
    }
  }

  public static void awsEnqueue(String[] args, Config config) throws Exception {
    AWSConfig awsConfig = new AWSConfig(new ConfigObject(config.get_or_create_child("aws")));
    WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(config.get_or_create_child("web"))));
    try {
      SQS sqs = new SQS(base, awsConfig, new AWSMetrics(new NoOpMetricsFactory()));
      CountDownLatch latch = new CountDownLatch(1);
      sqs.queue("{\"message\":\"Hello World\"}", new Callback<Void>() {
        @Override
        public void success(Void value) {
          System.err.println("Queue success!");
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          System.err.println("Queue failure:" + ex.code);
          latch.countDown();
        }
      });
      latch.await(5000, TimeUnit.MILLISECONDS);
    } finally {
      base.shutdown();
    }
  }

  public static void awsTestAssetListing(String[] args, Config config) throws Exception {
    AWSConfig awsConfig = new AWSConfig(new ConfigObject(config.get_or_create_child("aws")));
    String space = Util.extractOrCrash("--space", "-s", args);
    String key = Util.extractOrCrash("--key", "-k", args);
    WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(config.get_or_create_child("web"))));
    try {
      S3 s3 = new S3(base, awsConfig, new AWSMetrics(new NoOpMetricsFactory()));
      CountDownLatch latch = new CountDownLatch(1);
      s3.list(new Key(space, key), new Callback<List<String>>() {
        @Override
        public void success(List<String> value) {
          for (String id : value) {
            System.err.println(id);
          }
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
          latch.countDown();
        }
      });
      latch.await(2500, TimeUnit.MILLISECONDS);
    } finally {
      base.shutdown();
    }
  }

  public static void awsTestEmail(Config config) throws Exception {
    AWSConfig awsConfig = new AWSConfig(new ConfigObject(config.get_or_create_child("aws")));

    System.out.println();
    System.out.print(Util.prefix("To:", Util.ANSI.Yellow));
    String to = System.console().readLine();
    WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(config.get_or_create_child("web"))));
    try {
      SES ses = new SES(base, awsConfig, new AWSMetrics(new NoOpMetricsFactory()));
      ses.sendCode(to, "TESTCODE");
    } finally {
      base.shutdown();
    }
  }

  public static void awsSetup(Config config) throws Exception {
    System.out.println();
    System.out.print(Util.prefix("AccessKey:", Util.ANSI.Yellow));
    String accessKey = System.console().readLine();

    System.out.println();
    System.out.print(Util.prefix("SecretKey:", Util.ANSI.Yellow));
    String secretKey = System.console().readLine();

    System.out.println();
    System.out.print(Util.prefix("Region:", Util.ANSI.Yellow));
    String region = System.console().readLine();

    System.out.println();
    System.out.print(Util.prefix("Init-From-Email:", Util.ANSI.Yellow));
    String fromEmailAddressForInit = System.console().readLine();

    System.out.println();
    System.out.print(Util.prefix("Init-ReplyTo-Email:", Util.ANSI.Yellow));
    String replyToEmailAddressForInit = System.console().readLine();

    System.out.println();
    System.out.print(Util.prefix("Bucket:", Util.ANSI.Yellow));
    String bucket = System.console().readLine();

    config.manipulate((node) -> {
      ObjectNode roleNode = node.putObject("aws");
      roleNode.put("access_key", accessKey);
      roleNode.put("secret_key", secretKey);
      roleNode.put("region", region);
      roleNode.put("init_from_email", fromEmailAddressForInit);
      roleNode.put("init_reply_email", replyToEmailAddressForInit);
      roleNode.put("bucket", bucket);
    });
  }
}

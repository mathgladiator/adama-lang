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
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.*;
import org.adamalang.common.jvm.MachineHeat;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.extern.aws.*;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.service.WebConfig;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
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
      case "test-new-clients":
        awsTestNewStuff(config);
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
    System.out.println("    " + Util.prefix("memory-test", Util.ANSI.Green) + "       Crash by allocating memory");
    System.out.println("    " + Util.prefix("release", Util.ANSI.Green) + "           Release the binary to the world");
  }

  public static void awsTestEmail(Config config) throws Exception {
    AWSConfig awsConfig = new AWSConfig(new ConfigObject(config.get_or_create_child("aws")));

    System.out.println();
    System.out.print(Util.prefix("To:", Util.ANSI.Yellow));
    String to = System.console().readLine();

    SES ses = new SES(awsConfig, new AWSMetrics(new NoOpMetricsFactory()));
    ses.sendCode(to, "TESTCODE");
  }

  public static void awsTestNewStuff(Config config) throws Exception{
    AWSConfig awsConfig = new AWSConfig(new ConfigObject(config.get_or_create_child("aws")));
    WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(config.get_or_create_child("web"))));
    try {

      CountDownLatch latch = new CountDownLatch(2);
      {
        String url = "https://s3.us-east-2.amazonaws.com/us-east-2-adama-assets/demo.txt";
        HashMap<String, String> headers = new HashMap<>();

        new SignatureV4(awsConfig, "s3", "GET", "s3.us-east-2.amazonaws.com", "/us-east-2-adama-assets/demo.txt") //
            .withEmptyBody() //
            .signInto(headers);

        base.executeGet(url, headers, new Callback<String>() {
          @Override
          public void success(String value) {
            System.err.println("Success:" + value);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("Failure:" + ex.code + "/" + ex.getMessage());
            latch.countDown();
          }
        });
      }
      {
        String url = "https://email.us-east-2.amazonaws.com/v2/email/outbound-emails";
        HashMap<String, String> headers = new HashMap<>();

        final byte[] postBody;
        {
          ObjectNode request = Json.newJsonObject();
          request.put("FromEmailAddress", awsConfig.fromEmailAddressForInit);
          request.putArray("ReplyToAddresses").add(awsConfig.fromEmailAddressForInit);
          request.putObject("Destination").putArray("ToAddresses").add("test@mathgladiator.com");
          ObjectNode content = request.putObject("Content").putObject("Simple");

          ObjectNode subject = content.putObject("Subject");
          subject.put("Data", "Test Subject");
          subject.put("Charset", "UTF-8");

          ObjectNode body = content.putObject("Body").putObject("Text");
          body.put("Data", "Test Body");
          body.put("Charset", "UTF-8");

          postBody = request.toString().getBytes(StandardCharsets.UTF_8);
        }
        String sha256 = Hex.of(Hashing.sha256().digest(postBody));

        new SignatureV4(awsConfig, "ses", "POST", "email.us-east-2.amazonaws.com", "/v2/email/outbound-emails") //
            .withHeader("Content-Type", "application/json") //
            .withHeader("Content-Length", postBody.length + "") //
            .withContentHashSha256(sha256) //
            .signInto(headers);

        base.executePost(url, headers, postBody, new Callback<>() {
          @Override
          public void success(String value) {
            System.err.println("Success:" + value);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            System.err.println("OK Failure:" + ex.code + "/" + ex.getMessage());
            latch.countDown();
          }
        });
      }
      latch.await(5000, TimeUnit.MILLISECONDS);
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

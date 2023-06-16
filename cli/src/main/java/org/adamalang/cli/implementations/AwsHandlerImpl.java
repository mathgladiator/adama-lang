/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.events.EventCodec;
import org.adamalang.caravan.events.Events;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.AwsHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Callback;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.ErrorCodeException;
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

public class AwsHandlerImpl implements AwsHandler {
    @Override
    public void setup(Arguments.AwsSetupArgs args, Output.YesOrError output) throws Exception {
        Config config = args.config;
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

    @Override
    public void testEmail(Arguments.AwsTestEmailArgs args, Output.YesOrError output) throws Exception {
        AWSConfig awsConfig = new AWSConfig(new ConfigObject(args.config.get_or_create_child("aws")));

        System.out.println();
        System.out.print(Util.prefix("To:", Util.ANSI.Yellow));
        String to = System.console().readLine();
        WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(args.config.get_or_create_child("web"))));
        try {
            SES ses = new SES(base, awsConfig, new AWSMetrics(new NoOpMetricsFactory()));
            ses.sendCode(to, "TESTCODE");
        } finally {
            base.shutdown();
        }
    }

    @Override
    public void testAssetListing(Arguments.AwsTestAssetListingArgs args, Output.YesOrError output) throws Exception {
        AWSConfig awsConfig = new AWSConfig(new ConfigObject(args.config.get_or_create_child("aws")));
        WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(args.config.get_or_create_child("web"))));
        try {
            S3 s3 = new S3(base, awsConfig, new AWSMetrics(new NoOpMetricsFactory()));
            CountDownLatch latch = new CountDownLatch(1);
            s3.listAssetsOf(new Key(args.space, args.key), new Callback<List<String>>() {
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

    @Override
    public void testEnqueue(Arguments.AwsTestEnqueueArgs args, Output.YesOrError output) throws Exception {
        AWSConfig awsConfig = new AWSConfig(new ConfigObject(args.config.get_or_create_child("aws")));
        WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(args.config.get_or_create_child("web"))));
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

    @Override
    public void downloadArchive(Arguments.AwsDownloadArchiveArgs args, Output.YesOrError output) throws Exception {
        AWSConfig awsConfig = new AWSConfig(new ConfigObject(args.config.get_or_create_child("aws")));
        WebClientBase base = new WebClientBase(new WebConfig(new ConfigObject(args.config.get_or_create_child("web"))));
        try {
            CountDownLatch latch = new CountDownLatch(1);
            S3 s3 = new S3(base, awsConfig, new AWSMetrics(new NoOpMetricsFactory()));
            s3.restore(new Key(args.space, args.key), args.archive, new Callback<File>() {
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

    @Override
    public void memoryTest(Arguments.AwsMemoryTestArgs args, Output.YesOrError output) throws Exception {
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
}

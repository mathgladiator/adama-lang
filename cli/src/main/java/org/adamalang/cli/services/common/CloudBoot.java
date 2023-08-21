/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.services.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.ExceptionRunnable;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.extern.Email;
import org.adamalang.extern.aws.*;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CloudBoot {
  private static final Logger LOGGER = LoggerFactory.getLogger(CloudBoot.class);
  public final S3 s3;
  public final AWSConfig awsConfig;
  public final AWSMetrics awsMetrics;
  public final SQS sqs;
  public final SES ses;

  public CloudBoot(AtomicBoolean alive, MetricsFactory metricsFactory, WebClientBase webBase, ObjectNode config, String logsPrefix, SimpleExecutor system) throws Exception {
    this.awsConfig = new AWSConfig(new ConfigObject(config));
    this.awsMetrics = new AWSMetrics(metricsFactory);
    this.s3 = new S3(webBase, awsConfig, awsMetrics);
    this.sqs = new SQS(webBase, awsConfig, awsMetrics);
    AtomicReference<Runnable> cancel = new AtomicReference<>();
    this.ses = new SES(webBase, awsConfig, awsMetrics);
    cancel.set(system.schedule(new NamedRunnable("archive-s3") {
      @Override
      public void execute() throws Exception {
        System.out.println("[CloudBoot-Shutdown]");
        try {
          s3.uploadLogs(new File("logs"), logsPrefix);
        } catch (Exception ex) {
          LOGGER.error("error-uploading-logs", ex);
        } finally {
          if (alive.get()) {
            cancel.set(system.schedule(this, 60000));
          }
        }
      }
    }, 5000));
    Runtime.getRuntime().addShutdownHook(new Thread(ExceptionRunnable.TO_RUNTIME(() -> {
      alive.set(false);
      cancel.get().run();
    })));
  }
}

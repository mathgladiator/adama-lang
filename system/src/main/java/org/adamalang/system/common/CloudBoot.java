/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.system.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.ExceptionRunnable;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.MetricsFactory;
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
    File logs = new File("logs");
    logs.mkdirs();
    cancel.set(system.schedule(new NamedRunnable("archive-s3") {
      @Override
      public void execute() throws Exception {
        try {
          s3.uploadLogs(logs, logsPrefix);
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
      System.out.println("[CloudBoot-Shutdown]");
      alive.set(false);
      cancel.get().run();
    })));
  }
}

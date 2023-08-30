/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.region;

import org.adamalang.api.ClientMessageDirectSendOnceRequest;
import org.adamalang.api.ClientSeqResponse;
import org.adamalang.api.SelfClient;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.metering.BillingDocumentFinder;
import org.adamalang.runtime.sys.metering.DiskMeteringBatchMaker;
import org.adamalang.runtime.sys.metering.MeterReducerReader;
import org.adamalang.runtime.sys.metering.MeteringBatchReady;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/** convert batches from disk to messages and send them to the billing document */
public class MeteringBatchSubmit implements MeteringBatchReady {
  private static final Logger LOGGER = LoggerFactory.getLogger(MeteringBatchSubmit.class);
  private final MeteringBatchSubmitMetrics metrics;
  private final String identity;
  private final String region;
  private final String machine;
  private final BillingDocumentFinder billingDocumentFinder;
  private final SelfClient adama;
  private DiskMeteringBatchMaker maker;

  public MeteringBatchSubmit(MeteringBatchSubmitMetrics metrics, String identity, String region, String machine, BillingDocumentFinder billingDocumentFinder, SelfClient adama) {
    this.metrics = metrics;
    this.identity = identity;
    this.region = region;
    this.machine = machine;
    this.billingDocumentFinder = billingDocumentFinder;
    this.adama = adama;
  }

  @Override
  public void init(DiskMeteringBatchMaker me) {
    this.maker = me;
  }

  @Override
  public void ready(String batchId) {
    try {
      String batch = maker.getBatch(batchId);
      maker.deleteBatch(batchId);
      Map<String, String> messages = MeterReducerReader.convertMapToBillingMessages(batch, region, machine);
      for(Map.Entry<String, String> message : messages.entrySet()) {
        billingDocumentFinder.find(message.getKey(), metrics.metering_batch_submit_find.wrap(new Callback<Key>() {
          final String messageToSend = message.getValue();
          String space = message.getKey();
          @Override
          public void success(Key billingDocument) {
            ClientMessageDirectSendOnceRequest request = new ClientMessageDirectSendOnceRequest();
            request.dedupe = "billing-document-" + region + "-" + machine + "-" + batchId + "-" + space;
            request.identity = identity;
            request.space = billingDocument.space;
            request.key = billingDocument.key;
            request.channel = "ingest_new_usage_record";
            request.message = Json.parseJsonObject(messageToSend);
            adama.messageDirectSendOnce(request, metrics.metering_batch_submit_send.wrap(new Callback<>() {
              @Override
              public void success(ClientSeqResponse value) {
                metrics.metering_batch_happy.run();
              }

              @Override
              public void failure(ErrorCodeException ex) {
                LOGGER.error("lost-metering-batch-send:" + ex.code + "/" + space);
                metrics.metering_batch_lost.run();
              }
            }));
          }

          @Override
          public void failure(ErrorCodeException ex) {
            LOGGER.error("lost-metering-batch-find:" + ex.code + "/" + space);
            metrics.metering_batch_lost.run();
          }
        }));
      }
    } catch (Exception failedToDealWithBatch) {
      metrics.metering_batch_exception.run();
    }
  }
}

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

import java.util.Map;

public class MeteringBatchSubmit implements MeteringBatchReady {
  private final String identity;
  private final String region;
  private final String machine;
  private final BillingDocumentFinder billingDocumentFinder;
  private final SelfClient adama;
  private DiskMeteringBatchMaker maker;

  public MeteringBatchSubmit(String identity, String region, String machine, BillingDocumentFinder billingDocumentFinder, SelfClient adama) {
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
      //public void directSend(String ip, String origin, String agent, String authority, String space, String key, String marker, String channel, String message, Callback<Integer> callback) {
      for(Map.Entry<String, String> message : messages.entrySet()) {
        billingDocumentFinder.find(message.getKey(), new Callback<Key>() {
          final String messageToSend = message.getValue();
          @Override
          public void success(Key billingDocument) {
            ClientMessageDirectSendOnceRequest request = new ClientMessageDirectSendOnceRequest();
            request.dedupe = "billing-document-" + region + "-" + machine + "-" + batchId;
            request.identity = identity;
            request.space = billingDocument.space;
            request.key = billingDocument.key;
            request.channel = "ingest_new_usage_record";
            request.message = Json.parseJsonObject(messageToSend);
            adama.messageDirectSendOnce(request, new Callback<ClientSeqResponse>() {
              @Override
              public void success(ClientSeqResponse value) {

              }

              @Override
              public void failure(ErrorCodeException ex) {

              }
            });
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
      }
    } catch (Exception failedToDealWithBatch) {

    }
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.services.billing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.FirstPartyMetrics;
import org.adamalang.services.ServiceConfig;
import org.adamalang.services.sms.Twilio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/** https://stripe.com/docs/api */
public class Stripe extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(Twilio.class);
  private final FirstPartyMetrics metrics;
  private final ExecutorService executor;

  public Stripe(FirstPartyMetrics metrics, ServiceConfig config, ExecutorService executor) throws ErrorCodeException {
    super("stripe", new NtPrincipal("stripe", "service"), true);
    this.metrics = metrics;
    this.executor = executor;
    String apikey = config.getDecryptedSecret("apikey");
  }

  @Override
  public void request(String method, String request, Callback<String> callback) {
    ObjectNode node = Json.parseJsonObject(request);
    switch (method) {
      case "create_customer":

        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_IMPLEMENTED));
        return;

      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }

  private void createCustomer(ObjectNode node, Callback<String> callback) {
    String email = Json.readString(node, "email");
    String name = Json.readString(node, "name");

    callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_IMPLEMENTED));
  }
}

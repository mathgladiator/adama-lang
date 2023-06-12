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

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

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

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message Stripe_SimpleCreateCustomer").append(uniqueId).append(" { string email; string name; }\n");
    sb.append("message Stripe_SimpleUpdateCustomer").append(uniqueId).append(" { string id; string email; string name; }\n");
    sb.append("message Stripe_SimpleCreatePaymentIntent").append(uniqueId).append(" {  }\n");

    sb.append("message Stripe_Empty_").append(uniqueId).append(" { }\n");
    sb.append("message Stripe_JustId_").append(uniqueId).append(" { string id; }\n");

    sb.append("service amazonses {\n");
    sb.append("  class=\"amazonses\";\n");
    sb.append("  ").append(params).append("\n");
    if (!names.contains("apikey")) {
      error.accept("Stripe requires an 'apikey' (and it should be encrypted)");
    }
    sb.append("  method<Stripe_SimpleCreateCustomer").append(uniqueId).append(", Stripe_JustId_").append(uniqueId).append("> simple_create_customer;\n");
    sb.append("  method<Stripe_SimpleUpdateCustomer").append(uniqueId).append(", Stripe_Empty_").append(uniqueId).append("> simple_update_customer;\n");
    sb.append("  method<Stripe_SimpleCreatePaymentIntent").append(uniqueId).append(", Stripe_JustId_").append(uniqueId).append("> simple_create_payment_intent;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(String method, String request, Callback<String> callback) {
    ObjectNode node = Json.parseJsonObject(request);
    switch (method) {
      case "simple_create_customer":
        simpleCreateOrUpdateCustomer(node, callback);
        return;
      case "simple_update_customer":
        simpleCreateOrUpdateCustomer(node, callback);
        return;
      case "simple_create_payment_intent":
        simpleCreatePaymentIntent(node, callback);
        return;
      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }

  private void simpleCreateOrUpdateCustomer(ObjectNode node, Callback<String> callback) {
    String id = Json.readString(node, "id");
    String email = Json.readString(node, "email");
    String name = Json.readString(node, "name");

    callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_IMPLEMENTED));
  }

  private void simpleCreatePaymentIntent(ObjectNode node, Callback<String> callback) {
    callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_IMPLEMENTED));
  }
}

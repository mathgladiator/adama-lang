/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;

/** generated class for the responder: payment */
public class ClientPaymentResponse {
  public final ObjectNode _original;
  public final String paymentPlan;
  public final String publishableKey;

  public ClientPaymentResponse(ObjectNode response) {
    this._original = response;
    this.paymentPlan = Json.readString(response, "payment-plan");
    this.publishableKey = Json.readString(response, "publishable-key");
  }
}

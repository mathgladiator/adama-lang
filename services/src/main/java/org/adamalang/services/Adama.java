/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class Adama extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(Adama.class);
  private final ExecutorService executor;

  public Adama(ServiceConfig config, ExecutorService executor) throws ErrorCodeException {
    super("adama", new NtPrincipal("adama", "service"), true);
    this.executor = executor;
  }

  @Override
  public void request(String method, String request, Callback<String> callback) {
    ObjectNode node = Json.parseJsonObject(request);
    switch (method) {
      case "create":
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_IMPLEMENTED));
        return;
      case "sendDirect":
        return;
      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }
}

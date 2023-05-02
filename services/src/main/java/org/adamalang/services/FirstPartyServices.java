/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.services;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedThreadFactory;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.remote.Service;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.services.sms.Twilio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirstPartyServices {
  public static void install(DataBase dataBase) {
    ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory("first-party"));
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        executor.shutdown();
      }
    }));
    /*
    ServiceRegistry.REGISTRY.put("twilio", (space, configRaw) -> {
      ServiceConfig config = new ServiceConfig(dataBase, space, configRaw);
      try {
        return new Twilio(config, executor);
      } catch (ErrorCodeException ex) {
        return Service.FAILURE;
      }
    });
    */
  }
}

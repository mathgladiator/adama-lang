/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.frontend.global;

import org.adamalang.api.*;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.SimpleExecutorFactory;
import org.adamalang.frontend.Session;
import org.adamalang.transforms.DomainResolver;
import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.transforms.SpacePolicyLocator;
import org.adamalang.transforms.UserIdResolver;
import org.adamalang.transforms.global.GlobalDomainResolver;
import org.adamalang.transforms.global.GlobalPerSessionAuthenticator;
import org.adamalang.web.assets.AssetSystem;
import org.adamalang.web.contracts.*;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;

import java.util.Random;

public class BootstrapGlobalServiceBase {
  public static ServiceBase make(GlobalExternNexus extern, HttpHandler httpHandler) throws Exception {
    SimpleExecutor[] executors = SimpleExecutorFactory.DEFAULT.makeMany("saas", extern.config.threads);
    SpacePolicyLocator spacePolicyLocator = new SpacePolicyLocator(SimpleExecutor.create("space-policy-locator"), extern);
    UserIdResolver userIdResolver = new UserIdResolver(SimpleExecutor.create("user-id-resolver"), extern);
    GlobalControlHandler globalControlHandler = new GlobalControlHandler(extern, spacePolicyLocator);
    GlobalDataHandler globalDataHandler = new GlobalDataHandler(extern);
    DomainResolver domainResolver = new GlobalDomainResolver(SimpleExecutor.create("domain-resolver"), spacePolicyLocator, extern);

    Random randomExecutorIndex = new Random();
    return new ServiceBase() {
      @Override
      public ServiceConnection establish(ConnectionContext context) {
        return new ServiceConnection() {
          final Session session = new Session(new GlobalPerSessionAuthenticator(extern.database, extern.masterKey, context, extern.superPublicKeys));
          final GlobalConnectionNexus globalNexus =
              new GlobalConnectionNexus(extern.accessLogger, //
                  extern.globalApiMetrics, //
                  executors[randomExecutorIndex.nextInt(executors.length)], //
                  domainResolver, //
                  userIdResolver, //
                  session.authenticator, //
                  spacePolicyLocator); //

          final RegionConnectionNexus regionNexus =
              new RegionConnectionNexus(extern.accessLogger, //
                  extern.regionApiMetrics, //
                  executors[randomExecutorIndex.nextInt(executors.length)], //
                  domainResolver, //
                  session.authenticator); //
          final GlobalConnectionRouter globalRouter = new GlobalConnectionRouter(session, globalNexus, globalControlHandler);
          final RegionConnectionRouter regionRouter = new RegionConnectionRouter(session, regionNexus, globalDataHandler);

          @Override
          public void execute(JsonRequest request, JsonResponder responder) {
            try {
              if (RootGlobalHandler.test(request.method())) {
                globalRouter.route(request, responder);
              } else {
                regionRouter.route(request, responder);
              }
            } catch (ErrorCodeException ex) {
              responder.error(ex);
            }
          }

          @Override
          public boolean keepalive() {
            return session.keepalive();
          }

          @Override
          public void kill() {
            globalRouter.disconnect();
            regionRouter.disconnect();
          }
        };
      }

      @Override
      public HttpHandler http() {
        return httpHandler;
      }

      @Override
      public AssetSystem assets() {
        return extern.assets;
      }
    };
  }
}

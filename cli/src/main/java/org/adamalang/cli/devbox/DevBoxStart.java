/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.devbox;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.api.SelfClient;
import org.adamalang.cli.router.Arguments;
import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.region.DeploymentSync;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.socket.ConnectionReady;
import org.adamalang.web.client.socket.MultiWebClientRetryPool;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolConfig;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolMetrics;
import org.adamalang.web.service.WebConfig;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DevBoxStart {

  public static void start(Arguments.FrontendDevServerArgs args) throws Exception {
    TerminalIO terminal = new TerminalIO();
    String developerIdentity = args.config.get_string("identity", null);
    SimpleExecutor offload = SimpleExecutor.create("executor");
    WebClientBase webClientBase = new WebClientBase(new WebConfig(new ConfigObject(Json.newJsonObject())));
    MultiWebClientRetryPoolConfig config = new MultiWebClientRetryPoolConfig(new ConfigObject(Json.parseJsonObject("{\"multi-connection-count\":1}")));
    MultiWebClientRetryPool productionPool = new MultiWebClientRetryPool(offload, webClientBase, new MultiWebClientRetryPoolMetrics(new NoOpMetricsFactory()), config, ConnectionReady.TRIVIAL, "wss://aws-us-east-2.adama-platform.com/~s");
    SelfClient production = new SelfClient(productionPool);
    DeploymentSync sync = new DeploymentSync(production, offload, developerIdentity, (initial, space) -> {
      if (!initial) {
        terminal.important("hivemind|" + space + " was deployed!");
      }
    });
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        sync.shutdown();
      }
    }));

    DynamicControl control = new DynamicControl();
    AtomicBoolean alive = new AtomicBoolean(true);
    String localLibAdamaJSPath = "".equals(args.localLibadamaPath) ? null : args.localLibadamaPath;
    File localLibAdamaJSFile = null;
    if (localLibAdamaJSPath == null) {
      localLibAdamaJSPath = args.config.get_nullable_string("local-libadama-path-default");
      terminal.info("js|using 'local-libadama-path-default' from config to pull Adama javascript from " + localLibAdamaJSPath);
    }
    if (localLibAdamaJSPath != null) {
      localLibAdamaJSFile = new File(localLibAdamaJSPath);
      if (!(localLibAdamaJSFile.exists() && localLibAdamaJSFile.isDirectory())) {
        terminal.error("js|--local-libadama-path was provided but the directory doesn't exist (or is a file)");
        localLibAdamaJSFile = null;
      }
    }
    DevBoxServices.install(offload, (line) -> terminal.info(line));
    DevBoxAdamaMicroVerse verse = null;
    if (args.microverse != null) {
      File microverseDef = new File(args.microverse);
      if (microverseDef.exists() && microverseDef.isFile()) {
        ObjectNode defn = Json.parseJsonObject(Files.readString(microverseDef.toPath()));
        verse = DevBoxAdamaMicroVerse.load(alive, terminal, defn);
        if (verse == null) {
          terminal.error("verse|microverse: '" + args.microverse + "' failed, using production");
        } else {
          for (DevBoxAdamaMicroVerse.LocalSpaceDefn space : verse.spaces) {
            terminal.notice("devbox|connecting to hivemind for " + space.spaceName);
            sync.watch(space.spaceName);
          }
        }
      } else {
        terminal.error("verse|microverse: '" + args.microverse + "' is not present, using production");
      }
    }
    if (localLibAdamaJSPath == null && verse != null) {
      terminal.error("js|currently, --local-libadama-path is required (or configured) when using a verse");
      return;
    }


    terminal.info("devbox|starting up");
    AtomicReference<RxHTMLScanner.RxHTMLBundle> bundle = new AtomicReference<>();
    try (RxHTMLScanner scanner = new RxHTMLScanner(alive, terminal, new File(args.rxhtmlPath), localLibAdamaJSPath != null, (b) -> bundle.set(b))) {
      WebConfig webConfig = new WebConfig(new ConfigObject(args.config.get_or_create_child("web")));
      terminal.notice("devbox|starting webserver");
      DevBoxServiceBase base = new DevBoxServiceBase(control, terminal, webConfig, bundle, new File(args.assetPath), localLibAdamaJSFile, verse);
      Thread webServerThread = base.start();
      while (alive.get()) {
        Command command = Command.parse(terminal.readline().trim());
        if (command.is("kill", "exit", "quit", "q", "exut")) {
          terminal.notice("devbox|killing");
          alive.set(false);
          webServerThread.interrupt();
          if (verse != null) {
            verse.shutdown();
          }
          base.shutdown();
        }
        if (command.is("help", "h", "?", "")) {
          terminal.info("Wouldn't it be great if there was some like... help here?");
        }
        if (command.is("viewer-updates")) {
          if (command.argIs(0, "slow")) {
            terminal.notice("devbox|slowing down view updates by 5 seconds");
            control.slowViewerStateUpdates.set(true);
          }
          if (command.argIs(0, "fast")) {
            terminal.notice("devbox|normalizing view update speed");
            control.slowViewerStateUpdates.set(false);
          }
        }
        if (command.is("delete")) {
          if (command.requireArg(1)) {
            String space = command.argAt(0);
            String key = command.argAt(1);
            verse.service.delete(new CoreRequestContext(new NtPrincipal("terminal", "overlord"), "cli", "127.0.0.1", key), new Key(space, key), new Callback<Void>() {
              @Override
              public void success(Void value) {
                terminal.info(space + "/" + key + " deleted");
              }

              @Override
              public void failure(ErrorCodeException ex) {
                terminal.error("failed delete:" + ex.code);
              }
            });
          } else {
            terminal.notice("delete $space $key");
          }
        }
        if (command.is("init")) {
          if (command.requireArg(2)) {
            String space = command.argAt(0);
            String key = command.argAt(1);
            String file = command.argAt(2);
            try {
              ObjectNode parsed = Json.parseJsonObject(Files.readString(new File(file).toPath()));
              parsed.put("__seq", 1);
              RemoteDocumentUpdate update = new RemoteDocumentUpdate(0, 1, NtPrincipal.NO_ONE, "{}", parsed.toString(), "{}", true, 0, 0, UpdateType.Internal);
              verse.dataService.initialize(new Key(space, key), update, new Callback<Void>() {
                @Override
                public void success(Void value) {
                  terminal.info("init:loaded");
                }

                @Override
                public void failure(ErrorCodeException ex) {
                  terminal.error("failed restoring:" + ex.code);
                }
              });
            } catch (Exception ex) {
              terminal.error("failed loading: " + ex.getMessage());
            }
          } else {
            terminal.notice("load $space $key $file");
          }
        }
        if (command.is("save")) {
          if (command.requireArg(2)) {
            String space = command.argAt(0);
            String key = command.argAt(1);
            String file = command.argAt(2);
            verse.service.saveCustomerBackup(new Key(space, key), new Callback<String>() {
              @Override
              public void success(String value) {
                try {
                  Files.writeString(new File(file).toPath(), value);
                } catch (Exception ex) {
                  terminal.error("failed save: " + ex.getMessage());
                }
              }

              @Override
              public void failure(ErrorCodeException ex) {
                terminal.error("failed save from service:" + ex.code);
              }
            });
          } else {
            terminal.notice("save $space $key $file");
          }
        }
        if (command.is("deltas")) {
          if (command.requireArg(2)) {
            String space = command.argAt(0);
            String key = command.argAt(1);
            String count = command.argAt(2);
            terminal.error("TODO");
          } else {
            terminal.notice("deltas $space $key count");
          }
        }
        if (command.is("dump-log")) {
          if (command.requireArg(2)) {
            String space = command.argAt(0);
            String key = command.argAt(1);
            String output = command.argAt(2);
            long started = System.currentTimeMillis();
            FileWriter fw = new FileWriter(output, StandardCharsets.UTF_8);
            try {
              CountDownLatch readEverything = new CountDownLatch(1);
              verse.dataService.dumpLog(new Key(space, key), new Stream<String>() {
                @Override
                public void next(String value) {
                  try {
                    fw.write(value);
                    fw.write("\n");
                    fw.flush();
                  } catch (Exception ex) {
                    terminal.error("write exception:" + ex.getMessage());
                  }
                }

                @Override
                public void complete() {
                  readEverything.countDown();
                }

                @Override
                public void failure(ErrorCodeException ex) {

                }
              });
              readEverything.await(10000, TimeUnit.MILLISECONDS);
              terminal.notice("dump-log finished in " + (System.currentTimeMillis() - started) + "ms");
            } finally {
              fw.flush();
              fw.close();
            }
          }
        }
        if (command.is("diagnostics")) {
          verse.dataService.diagnostics(new Callback<String>() {
            @Override
            public void success(String value) {
              terminal.info("diagnostics|" + value);
            }

            @Override
            public void failure(ErrorCodeException ex) {
              terminal.error("failed to get diagnostics:" + ex.code);
            }
          });
        }
        if (command.is("query")) {
          if (command.requireArg(1)) {
            TreeMap<String, String> query = new TreeMap<>();
            query.put("space", command.argAt(0));
            query.put("key", command.argAt(1));
            verse.service.query(query, new Callback<>() {
              @Override
              public void success(String value) {
                terminal.notice("query-result|" + value);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                terminal.error("query|failed to query:" + ex.code);
              }
            });
          } else {
            terminal.notice("load $space $key $file");
          }
        }
      }
    }
  }
}

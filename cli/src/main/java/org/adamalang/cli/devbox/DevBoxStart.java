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
package org.adamalang.cli.devbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.api.SelfClient;
import org.adamalang.cli.Config;
import org.adamalang.cli.router.Arguments;
import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.devbox.*;
import org.adamalang.region.AdamaDeploymentSync;
import org.adamalang.region.AdamaDeploymentSyncMetrics;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.deploy.Deploy;
import org.adamalang.runtime.deploy.Undeploy;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientBaseMetrics;
import org.adamalang.web.client.socket.ConnectionReady;
import org.adamalang.web.client.socket.MultiWebClientRetryPool;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolConfig;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolMetrics;
import org.adamalang.web.service.WebConfig;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DevBoxStart {

  public static class DevBoxInputs {
    public final Config config;
    public final String rxhtmlPath;
    public final String assetPath;
    public final String microverse;
    public final String debugger;
    public final String localLibadamaPath;
    public final String environment;
    public final String preserveView;
    public final String types;

    public DevBoxInputs(Arguments.FrontendDevServerArgs args) {
      this.config = args.config;
      this.rxhtmlPath = args.rxhtmlPath;
      this.assetPath = args.assetPath;
      this.microverse = args.microverse;
      this.debugger = args.debugger;
      this.localLibadamaPath = args.localLibadamaPath;
      this.environment = args.environment;
      this.preserveView = args.preserveView;
      this.types = args.types;
    }

    public DevBoxInputs(Arguments.DevboxArgs args) {
      this.config = args.config;
      this.rxhtmlPath = args.rxhtmlPath;
      this.assetPath = args.assetPath;
      this.microverse = args.microverse;
      this.debugger = args.debugger;
      this.localLibadamaPath = args.localLibadamaPath;
      this.environment = args.environment;
      this.preserveView = args.preserveView;
      this.types = args.types;
    }
  }

  public static void start(DevBoxInputs args) throws Exception {
    TerminalIO terminal = new TerminalIO();
    String developerIdentity = args.config.get_string("identity", null);
    SimpleExecutor offload = SimpleExecutor.create("executor");
    WebClientBase webClientBase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), new WebConfig(new ConfigObject(Json.newJsonObject())));
    MultiWebClientRetryPoolConfig config = new MultiWebClientRetryPoolConfig(new ConfigObject(Json.parseJsonObject("{\"multi-connection-count\":1}")));
    MultiWebClientRetryPool productionPool = new MultiWebClientRetryPool(offload, webClientBase, new MultiWebClientRetryPoolMetrics(new NoOpMetricsFactory()), config, ConnectionReady.TRIVIAL, "wss://aws-us-east-2.adama-platform.com/~s");
    SelfClient production = new SelfClient(productionPool);
    AdamaDeploymentSync sync = new AdamaDeploymentSync(new AdamaDeploymentSyncMetrics(new NoOpMetricsFactory()), production, offload, developerIdentity, new Deploy() {
      private HashSet<String> ignoredFirst = new HashSet<>();

      @Override
      public void deploy(String space, Callback<Void> callback) {
        if (ignoredFirst.contains(space)) {
          terminal.important("hivemind|" + space + " was deployed!");
        } else {
          ignoredFirst.add(space);
        }
      }
    }, new Undeploy() {
      @Override
      public void undeploy(String space) {

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
      if (localLibAdamaJSPath == null) {
        terminal.info("js|using built-in libadama");
      } else {
        terminal.info("js|using 'local-libadama-path-default' from config: '" + localLibAdamaJSPath + "'");
      }
    } else {
      terminal.info("js|using libadama from args: '" + localLibAdamaJSPath + "'");
    }
    if (localLibAdamaJSPath != null) {
      localLibAdamaJSFile = new File(localLibAdamaJSPath);
      if (!(localLibAdamaJSFile.exists() && localLibAdamaJSFile.isDirectory())) {
        terminal.error("js|--local-libadama-path was provided but the directory doesn't exist (or is a file)");
        localLibAdamaJSFile = null;
      }
    }
    DevBoxAdamaMicroVerse verse = null;
    if (args.microverse != null) {
      File microverseDef = new File(args.microverse);
      if (microverseDef.exists() && microverseDef.isFile()) {
        ObjectNode defn = Json.parseJsonObject(Files.readString(microverseDef.toPath()));
        File privateVerse = new File("private.verse.json");
        if (privateVerse.exists()) {
          ObjectNode merge = Json.parseJsonObject(Files.readString(privateVerse.toPath()));
          Iterator<Map.Entry<String, JsonNode>> it = merge.fields();
          while (it.hasNext()) {
            Map.Entry<String, JsonNode> v = it.next();
            terminal.info("verse|merging in '" + v.getKey() + "' from private verse");
            defn.set(v.getKey(), v.getValue());
          }
        }
        DevBoxServices.install(defn, webClientBase, offload, (line) -> terminal.info(line));
        verse = DevBoxAdamaMicroVerse.load(alive, terminal, defn, webClientBase, new File(args.types));
        if (verse == null) {
          terminal.error("verse|microverse: '" + args.microverse + "' failed, using production");
        } else {
          terminal.info("verse|installing push notifications");
          verse.devPush.install();
          for (DevBoxAdamaMicroVerse.LocalSpaceDefn space : verse.spaces) {
            terminal.notice("devbox|connecting to hivemind for " + space.spaceName);
            sync.watch(space.spaceName);
          }
        }
      } else {
        terminal.error("verse|microverse: '" + args.microverse + "' is not present, using production");
      }
    }
    terminal.info("devbox|starting up");
    boolean debuggerAvailable = "true".equals(args.debugger);
    if (debuggerAvailable) {
      terminal.info("devbox|debugger available");
    } else {
      terminal.info("devbox|debugger disabled");
    }
    String env = args.environment;

    boolean preserveView = "true".equals(args.preserveView);
    if (preserveView) {
      terminal.info("devbox|preserving viewstate on auto-reload");
    } else {
      terminal.info("devbox|not preserving viewstate on auto-reload");
    }

    AtomicReference<RxHTMLScanner.RxHTMLBundle> bundle = new AtomicReference<>();
    RxPubSub pubSub = new RxPubSub(preserveView);
    try (RxHTMLScanner scanner = new RxHTMLScanner(alive, terminal, new File(args.rxhtmlPath), verse != null || localLibAdamaJSFile != null, env, (b) -> bundle.set(b), pubSub, new File(args.types))) {
      WebConfig webConfig = new WebConfig(new ConfigObject(args.config.get_or_create_child("web")));
      terminal.notice("devbox|starting webserver on port " + webConfig.port);
      File attachmentsPath = new File("attachments");
      attachmentsPath.mkdirs();
      DevBoxServiceBase base = new DevBoxServiceBase(control, terminal, webConfig, bundle, new File(args.assetPath), localLibAdamaJSFile, attachmentsPath, verse, debuggerAvailable, pubSub);
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
          terminal.info("Help for the Adama DevBox!!");
          terminal.info("");
          terminal.info("Commands:");
          terminal.info("  `exit` - turn off the devbox");
          terminal.info("  `viewer-updates slow` - slow down viewer updates by 5 seconds");
          terminal.info("  `viewer-updates fast` - disable the 5 second viewer update penalty");
          terminal.info("  `delete $space $key` - delete a local document by force");
          terminal.info("  `init $space $key $file.json` - initialize a document from the file system");
          terminal.info("  `save $space $key $file.json` - snapshot a document to the file system");
          terminal.info("  `dump-log $space $key $file.json` - dump a log of all deltas within caravan");
          terminal.info("  `diagnostics` - get some useful diagnostics");
          terminal.info("  `flush` - force flush caravan");
          terminal.info("  `query $space $key` - execute an op query against a document");
          terminal.info("  `timeslip $delta $unit [$timeframe-seconds]`");
          terminal.info("                   $unit \\in {ms, sec, min, hr, day, week}");
          terminal.info("      - change by $delta $unit over $timeframe-seconds (default is 5 seconds)");
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
                  terminal.info("saved " + file);
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
              terminal.info("caravan-diagnostics|" + value);
            }

            @Override
            public void failure(ErrorCodeException ex) {
              terminal.error("failed to get diagnostics:" + ex.code);
            }
          });
          terminal.info("base-diagnostics|" + base.diagnostics());
        }
        if (command.is("time-slip", "timeslip")) {
          if (verse == null) {
            terminal.error("time-slip|must have local verse");
          } else if (command.requireArg(1)) {
            Integer delta = command.argAtIsInt(0);
            if (delta != null) {
              Long deltaMs = null;
              switch (command.argAt(1)) {
                case "ms":
                  deltaMs = (long) delta;
                  break;
                case "s":
                case "sec":
                case "second":
                case "seconds":
                  deltaMs = delta * 1000L;
                  break;
                case "m":
                case "min":
                case "mins":
                case "minute":
                case "minutes":
                  deltaMs = delta * 1000L * 60L;
                  break;
                case "h":
                case "hr":
                case "hour":
                case "hours":
                  deltaMs = delta * 1000L * 60L * 60L;
                  break;
                case "d":
                case "day":
                case "days":
                  deltaMs = delta * 1000L * 60L * 60L * 24L;
                  break;
                case "w":
                case "wk":
                case "week":
                case "weeks":
                  deltaMs = delta * 1000L * 60L * 60L * 24L * 7L;
                  break;
              }
              if (deltaMs != null) {
                Integer timeframeSeconds = command.argAtIsInt(2);
                if (timeframeSeconds == null) {
                  timeframeSeconds = 5;
                }
                verse.timeMachine.add(deltaMs, timeframeSeconds);
              } else {
                terminal.notice("time-slip delta unit [timeframe-sec]; unit must ms, sec, min, hr, day, week");
              }
            } else {
              terminal.notice("time-slip delta unit [timeframe-sec]; delta must be an integer");
            }
          } else {
            terminal.notice("time-slip delta unit [timeframe-sec]");
          }
        }
        if (command.is("flush")) {
          verse.dataService.flush(true).await(1000, TimeUnit.MILLISECONDS);
          terminal.info("caravan|flushed");
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
            terminal.notice("query $space $key");
          }
        }
      }
    }
  }
}

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
package org.adamalang.devbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.api.SelfClient;
import org.adamalang.common.*;
import org.adamalang.language.LanguageServer;
import org.adamalang.region.AdamaDeploymentSync;
import org.adamalang.region.AdamaDeploymentSyncMetrics;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.deploy.Deploy;
import org.adamalang.runtime.deploy.Undeploy;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientBaseMetrics;
import org.adamalang.web.client.socket.ConnectionReady;
import org.adamalang.web.client.socket.MultiWebClientRetryPool;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolConfig;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolMetrics;
import org.adamalang.web.service.WebConfig;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Start {
  public static void start(Inputs args) throws Exception {
    TerminalIO terminal = new TerminalIO();
    DevBoxMetricsFactory metricsFactory = new DevBoxMetricsFactory((ln) -> terminal.notice("metrics|" + ln));
    String developerIdentity = args.developerIdentity;
    SimpleExecutor offload = SimpleExecutor.create("executor");
    WebClientBase webClientBase = new WebClientBase(new WebClientBaseMetrics(metricsFactory), new WebConfig(new ConfigObject(Json.newJsonObject())));
    MultiWebClientRetryPoolConfig config = new MultiWebClientRetryPoolConfig(new ConfigObject(Json.parseJsonObject("{\"multi-connection-count\":1}")));
    MultiWebClientRetryPool productionPool = new MultiWebClientRetryPool(offload, webClientBase, new MultiWebClientRetryPoolMetrics(metricsFactory), config, ConnectionReady.TRIVIAL, "wss://aws-us-east-2.adama-platform.com/~s");
    SelfClient production = new SelfClient(productionPool);
    DevBoxStats stats = new DevBoxStats();
    AdamaDeploymentSync sync = new AdamaDeploymentSync(new AdamaDeploymentSyncMetrics(metricsFactory), production, offload, developerIdentity, new Deploy() {
      private final HashSet<String> ignoredFirst = new HashSet<>();

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
      localLibAdamaJSPath = args.localPathForLibAdamaOverride;
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
    AdamaMicroVerse verse = null;
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
        LanguageServer server = new LanguageServer(args.lspPort, terminal, alive);
        Services.install(defn, webClientBase, offload, (line) -> terminal.info(line), metricsFactory);
        verse = AdamaMicroVerse.load(alive, stats, terminal, defn, webClientBase, new File(args.types), server.pubsub, metricsFactory);
        if (verse == null) {
          terminal.error("verse|microverse: '" + args.microverse + "' failed, using production");
        } else {
          terminal.info("verse|installing push notifications");
          verse.devPush.install(metricsFactory);
          for (AdamaMicroVerse.LocalSpaceDefn space : verse.spaces) {
            terminal.notice("devbox|connecting to hivemind for " + space.spaceName);
            sync.watch(space.spaceName);
          }
          terminal.info("lsp|starting language server on port " + args.lspPort);
          server.spinup();
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
    try (RxHTMLScanner scanner = new RxHTMLScanner(alive, stats, terminal, new File(args.rxhtmlPath), verse != null || localLibAdamaJSFile != null, env, (b) -> bundle.set(b), pubSub, new File(args.types))) {
      WebConfig webConfig = new WebConfig(new ConfigObject(args.webConfig));
      webConfig.validateForServerUse();
      terminal.notice("devbox|starting webserver on port " + webConfig.port);
      File attachmentsPath = new File("attachments");
      attachmentsPath.mkdirs();
      LocalServiceBase base = new LocalServiceBase(stats, control, terminal, webConfig, bundle, new File(args.assetPath), localLibAdamaJSFile, attachmentsPath, verse, debuggerAvailable, pubSub, metricsFactory);
      Thread webServerThread = base.start();
      terminal.important("people|Enjoy your developer experience... we hope it is pleasurable!");
      Key focusedKey = verse.domainKeyToUse;
      CommandProcessor processor = new CommandProcessor(terminal, verse);
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
          if (focusedKey != null) {
            terminal.info("[current key=" + focusedKey.space + "/" + focusedKey.key + "]");
          } else {
            terminal.info("[no focused key, use `use $space $key`]");
          }
          terminal.info("DevBox Commands:");
          terminal.info("  `exit` - turn off the devbox");
          terminal.info("  `flush` - force flush caravan");
          terminal.info("  `timeslip $delta $unit [$timeframe-seconds]`");
          terminal.info("                   $unit \\in {ms, sec, min, hr, day, week}");
          terminal.info("      - change time by $delta $unit over $timeframe-seconds (default is 5 seconds)");
          terminal.info("  `diagnostics` - get some useful diagnostics");

          terminal.info("");
          terminal.info("Behavior:");
          terminal.info("  `viewer-updates slow` - slow down viewer updates by 5 seconds");
          terminal.info("  `viewer-updates fast` - disable the 5 second viewer update penalty");
          terminal.info("  `autotest` - toggle tests to run after a deployment");
          terminal.info("");

          terminal.info("Old State Commands:");
          terminal.info("  `delete $space $key` - delete a local document by force");
          terminal.info("  `init $space $key $file.json` - initialize a document from the file system");
          terminal.info("  `save $space $key $file.json` - snapshot a document to the file system");
          terminal.info("  `restore $space $key $file.json` - restore a snapshot of the given file to the given space");
          terminal.info("  `dump-log $space $key $file.log` - dump a log of all deltas within caravan");
          terminal.info("  `query $space $key` - execute an op query against a document");
          terminal.info("  `test $space $key` - run tests for the given key");
          terminal.info("");

          terminal.info("New State Commands:");
          terminal.info("  `use $space $key` - focus commands on a specific document");
          terminal.info("  `delete` - delete the focused document");
          terminal.info("  `init $file.json` - initialize the focused document with the given file");
          terminal.info("  `save $file.json` - save the focused document to the given file");
          terminal.info("  `restore $file.json` - restore the focused document to the given file");
          terminal.info("  `dump-log $file.log` - dump a log of all deltas within caravan to the given file");
          terminal.info("  `query` - execute an op query against a document");
          terminal.info("  `test` - run tests for the focused key key");
          terminal.info("");

        }
        // Behavior and DevBox commands
        {
          if (command.is("reset")) {
            stats.reset();
            terminal.notice("devbox|reset stats");
          } else if (command.is("viewer-updates")) {
            if (command.argIs(0, "slow")) {
              terminal.notice("devbox|slowing down view updates by 5 seconds");
              control.slowViewerStateUpdates.set(true);
            }
            if (command.argIs(0, "fast")) {
              terminal.notice("devbox|normalizing view update speed");
              control.slowViewerStateUpdates.set(false);
            }
          } else if (command.is("autotest")) {
            if (verse != null && verse.domainKeyToUse != null) {
              terminal.info("verse|auto testing " + (verse.autoTest.get() ? "disabled" : "enabled"));
              verse.autoTest.set(!verse.autoTest.get());
            } else {
              terminal.error("verse|no verse nor domain key to use for autotest");
            }
          } else if (command.is("diagnostics")) {
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
          } else if (command.is("time-slip", "timeslip")) {
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
          } else if (command.is("flush")) {
            verse.dataService.flush(true).await(1000, TimeUnit.MILLISECONDS);
            terminal.info("caravan|flushed");
          }
        }

        // DOCUMENT COMMANDS
        {
          if (command.is("use")) {
            focusedKey = command.extractKeyAsFirstTwoArgs(focusedKey);
            if (focusedKey != null) {
              terminal.notice("using " + focusedKey.space + "/" + focusedKey.key);
            } else {
              terminal.notice("required key; `focus $space $key");
            }
          } else if (command.is("delete")) {
            Key keyToDelete = command.extractKeyAsFirstTwoArgs(focusedKey);
            if (keyToDelete != null) {
              processor.delete(keyToDelete);
            } else {
              terminal.notice("required key for deletion; either `delete $space $key` or `use $space $key` -> `delete`");
            }
          } else if (command.is("init")) {
            Key keyToInit = command.extractKeyAsFirstTwoArgs(focusedKey);
            File file = command.lastArgAsFileThatMustExist(terminal);
            if (keyToInit != null && file != null) {
              processor.init(keyToInit, file);
            } else {
              terminal.notice("required key and file for init; either `init $space $key $file` or `use $space $key` -> `init $file`");
            }
          } else if (command.is("restore")) {
            Key keyToRestore = command.extractKeyAsFirstTwoArgs(focusedKey);
            File file = command.lastArgAsFileThatMustExist(terminal);
            if (keyToRestore != null && file != null) {
              processor.restore(keyToRestore, file);
            } else {
              terminal.notice("required key and file for restore; either `restore $space $key $file` or `use $space $key` -> `init $file`");
            }
          } else if (command.is("save")) {
            Key keyToSave = command.extractKeyAsFirstTwoArgs(focusedKey);
            File file = command.lastArgAsFileThatMustNotExist(terminal);
            if (keyToSave != null && file != null) {
              processor.save(keyToSave, file);
            } else {
              terminal.notice("required key and file for save; either `save $space $key $file` or `use $space $key` -> `save $file`");
            }
          } else if (command.is("dump-log")) {
            Key keyToLog = command.extractKeyAsFirstTwoArgs(focusedKey);
            File file = command.lastArgAsFileThatMustNotExist(terminal);
            if (keyToLog != null && file != null) {
              processor.log(keyToLog, file);
            } else {
              terminal.notice("required key and file for init; either `dump-log $space $key $file` or `use $space $key` -> `dump-log $file`");
            }
          } else if (command.is("test")) {
            Key keyToTest = command.extractKeyAsFirstTwoArgs(focusedKey);
            if (verse != null && keyToTest != null) {
              verse.runTests(keyToTest);
            } else {
              terminal.notice("required key; either `test $space $key` or `use $space $key` -> `test`");
            }
          } else if (command.is("query")) {
            Key keyToQuery = command.extractKeyAsFirstTwoArgs(focusedKey);
            if (verse != null && keyToQuery != null) {
              processor.query(keyToQuery);
            } else {
              terminal.notice("required key; either `query $space $key` or `use $space $key` -> `query`");
            }
          }
        }

      }
    }
  }
}

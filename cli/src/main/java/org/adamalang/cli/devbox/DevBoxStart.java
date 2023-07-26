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
import org.adamalang.cli.router.Arguments;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.web.service.WebConfig;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DevBoxStart {
  public static void start(Arguments.FrontendDevServerArgs args) throws Exception {
    SimpleExecutor offload = SimpleExecutor.create("executor");
    DynamicControl control = new DynamicControl();
    AtomicBoolean alive = new AtomicBoolean(true);
    String localLibAdamaJSPath = "".equals(args.localLibadamaPath) ? null : args.localLibadamaPath;
    File localLibAdamaJSFile = null;
    TerminalIO terminal = new TerminalIO();
    if (localLibAdamaJSPath == null) {
      localLibAdamaJSPath = args.config.get_nullable_string("local-libadama-path-default");
      terminal.info("using 'local-libadama-path-default' from config to pull Adama javascript from");
    }
    if (localLibAdamaJSPath != null) {
      localLibAdamaJSFile = new File(localLibAdamaJSPath);
      if (!(localLibAdamaJSFile.exists() && localLibAdamaJSFile.isDirectory())) {
        terminal.error("--local-libadama-path was provided but the directory doesn't exist (or is a file)");
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
          terminal.error("microverse: '" + args.microverse + "' failed, using production");
        }
      } else {
        terminal.error("microverse: '" + args.microverse + "' is not present, using production");
      }
    }
    terminal.info("starting up");
    AtomicReference<RxHTMLScanner.RxHTMLBundle> bundle = new AtomicReference<>();
    try (RxHTMLScanner scanner = new RxHTMLScanner(alive, terminal, new File(args.rxhtmlPath), localLibAdamaJSPath != null, (b) -> bundle.set(b))) {
      WebConfig webConfig = new WebConfig(new ConfigObject(args.config.get_or_create_child("web")));
      terminal.notice("starting webserver");
      DevBoxServiceBase base = new DevBoxServiceBase(control, terminal, webConfig, bundle, new File(args.assetPath), localLibAdamaJSFile, verse);
      Thread webServerThread = base.start();
      while (alive.get()) {
        Command command = Command.parse(terminal.readline().trim());
        if (command.is("kill", "exit", "quit", "q", "exut")) {
          terminal.notice("lowering alive");
          alive.set(false);
          webServerThread.interrupt();
          if (verse != null) {
            verse.shutdown();
          }
          base.shutdown();
        }
        if (command.is("help", "h", "?")) {
          terminal.info("Wouldn't it be great if there was some like... help here?");
        }
        if (command.is("viewer-updates")) {
          if (command.argIs(0, "slow")) {
            terminal.notice("slowing down view updates by 5 seconds");
            control.slowViewerStateUpdates.set(true);
          }
          if (command.argIs(0, "fast")) {
            terminal.notice("normalizing view update speed");
            control.slowViewerStateUpdates.set(false);
          }
        }
        if (command.is("load")) {
          if (command.requireArg(2)) {
            String space = command.argAt(0);
            String key = command.argAt(1);
            String file = command.argAt(2);
            try {
              String json = Files.readString(new File(file).toPath());
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
          } else {
            terminal.notice("load $space $key $file");
          }
        }
      }
    }
  }
}

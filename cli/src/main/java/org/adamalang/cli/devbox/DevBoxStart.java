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
import org.adamalang.web.service.WebConfig;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DevBoxStart {
  public static void start(Arguments.FrontendDevServerArgs args) throws Exception {
    DynamicControl control = new DynamicControl();
    AtomicBoolean alive = new AtomicBoolean(true);
    String localLibAdamaJSPath = "".equals(args.localLibadamaPath) ? null : args.localLibadamaPath;
    File localLibAdamaJSFile = null;
    if (localLibAdamaJSPath != null) {
      localLibAdamaJSFile = new File(localLibAdamaJSPath);
      if (!(localLibAdamaJSFile.exists() && localLibAdamaJSFile.isDirectory())) {
        throw new Exception("--local-libadama-path was provided but the directory doesn't exist (or is a file)");
      }
    }
    TerminalIO terminal = new TerminalIO();
    DevBoxServices.install((line) -> terminal.info(line));
    DevBoxAdamaMicroVerse verse = null;
    if (args.microverse != null) {
      File microverseDef = new File(args.microverse);
      if (microverseDef.exists() && microverseDef.isFile()) {
        ObjectNode defn = Json.parseJsonObject(Files.readString(microverseDef.toPath()));
        verse = DevBoxAdamaMicroVerse.load(alive, terminal, defn);
        if (verse == null) {
          terminal.notice("microverse: '" + args.microverse + "' failed, using production");
        }
      } else {
        terminal.notice("microverse: '" + args.microverse + "' is not present, using production");
      }
    }
    AtomicReference<RxHTMLScanner.RxHTMLBundle> bundle = new AtomicReference<>();
    try (RxHTMLScanner scanner = new RxHTMLScanner(alive, terminal, new File(args.rxhtmlPath), localLibAdamaJSPath != null, (b) -> bundle.set(b))) {
      WebConfig webConfig = new WebConfig(new ConfigObject(args.config.get_or_create_child("web")));
      terminal.notice("Starting Webserver");
      DevBoxServiceBase base = new DevBoxServiceBase(control, terminal, webConfig, bundle, new File(args.assetPath), localLibAdamaJSFile, verse);
      Thread webServerThread = base.start();
      while (alive.get()) {
        String ln = terminal.readline().trim();
        if ("kill".equalsIgnoreCase(ln) || "exit".equalsIgnoreCase(ln) || "quit".equalsIgnoreCase(ln) || "q".equalsIgnoreCase(ln)) {
          terminal.notice("Lowering alive");
          alive.set(false);
          webServerThread.interrupt();
          if (verse != null) {
            verse.shutdown();
          }
          base.shutdown();
        }
        if ("slow-updates".equalsIgnoreCase(ln)) {
          terminal.notice("slowing down view updates by 5 seconds");
          control.slowViewerStateUpdates.set(true);
        }
      }
    }
  }
}

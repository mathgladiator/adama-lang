/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands;

import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.Callback;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.rxhtml.Feedback;
import org.adamalang.rxhtml.RxHtmlTool;
import org.adamalang.web.contracts.AssetDownloader;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.contracts.ServiceBase;
import org.adamalang.web.contracts.ServiceConnection;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.service.ContentType;
import org.adamalang.web.service.ServiceRunnable;
import org.adamalang.web.service.WebConfig;
import org.adamalang.web.service.WebMetrics;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.TreeMap;

public class Frontend {

  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      frontendHelp();
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "rxhtml":
        makeRxHTMLTemplate(next);
        return;
      case "dev":
      case "dev-server":
        spinUpDevServer(config, next);
        return;
      case "help":
      default:
        frontendHelp();
    }
  }

  public static void spinUpDevServer(Config config, String[] args) {
    WebConfig webConfig = new WebConfig(new ConfigObject(config.get_or_create_child("web")));
    // TODO: Scan for .rx.html files to register, then watch them for changes
    ServiceBase base = new ServiceBase() {
      @Override
      public ServiceConnection establish(ConnectionContext context) {
        // TODO: decide if we support a proxy mode for Adama?
        return null;
      }

      @Override
      public HttpHandler http() {
        return new HttpHandler() {
          @Override
          public void handleOptions(String uri, Callback<Boolean> callback) {
            callback.failure(new ErrorCodeException(0));
          }

          @Override
          public void handleGet(String uriRaw, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
            String uri = uriRaw + (uriRaw.endsWith("/") ? "index.html" : "");
            // TODO: check if part of RxHTML, then send to shell
            File file = new File(uri.substring(1));
            try {
              if (file.exists()) {
                byte[] bytes = Files.readAllBytes(file.toPath());
                callback.success(new HttpResult(ContentType.of(uri), bytes, true));
              } else {
                callback.failure(new ErrorCodeException(404));
              }
            } catch (Exception ex) {
              callback.failure(new ErrorCodeException(500));
            }
          }

          @Override
          public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
            callback.failure(new ErrorCodeException(0));
          }
        };
      }

      @Override
      public AssetDownloader downloader() {
        // TODO: decide if we support this here or not
        return null;
      }
    };
    ServiceRunnable webServer = new ServiceRunnable(webConfig, new WebMetrics(new NoOpMetricsFactory()), base, () -> {});
    webServer.run();
  }

  public static void frontendHelp() {
    System.out.println(Util.prefix("Tools to help with frontend.", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama frontend", Util.ANSI.Green) + " " + Util.prefix("[FRONTENDSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FRONTENDSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("rxhtml", Util.ANSI.Green) + "            Compile an rxhtml template set");
    System.out.println("    " + Util.prefix("dev-server", Util.ANSI.Green) + "        Host the working directory as a webserver");
  }

  private static void aggregateFiles(File file, ArrayList<File> files) {
    for (File child : file.listFiles()) {
      if (child.isDirectory()) {
        aggregateFiles(child, files);
      } else if (child.getName().endsWith(".rx.html")) {
        files.add(child);
      }
    }
  }

  private static ArrayList<File> convertArgsToFileList(String[] args) {
    ArrayList<File> files = new ArrayList<>();
    for (String arg : args) {
      if (!arg.startsWith("-")) {
        File file = new File(arg);
        if (!file.exists()) {
          continue;
        }
        if (file.isDirectory()) {
          aggregateFiles(file, files);
        } else if (file.getName().endsWith(".rx.html")) {
          files.add(file);
        }
      }
    }
    return files;
  }

  private static void makeRxHTMLTemplate(String[] args) throws Exception {
    ArrayList<File> files = convertArgsToFileList(args);
    String output = Util.extractOrCrash("--output", "-o", args);

    Files.writeString(new File(output).toPath(), RxHtmlTool.convertFilesToTemplateForest(files, new Feedback() {
      @Override
      public void warn(Element element, String warning) {
        System.err.println(warning);
      }
    }));
  }
}

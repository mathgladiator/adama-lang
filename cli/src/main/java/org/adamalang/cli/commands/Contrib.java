/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.commands;

import org.adamalang.GenerateTables;
import org.adamalang.apikit.Tool;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.DefaultCopyright;
import org.adamalang.net.codec.Generate;
import org.adamalang.support.GenerateLanguageTests;
import org.adamalang.support.GenerateTemplateTests;
import org.adamalang.web.service.BundleJavaScript;

import java.io.File;
import java.nio.file.Files;

public class Contrib {
  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      contribHelp();
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "tests-adama":
        GenerateLanguageTests.generate(0, next);
        return;
      case "tests-rxhtml":
        GenerateTemplateTests.generate(0, next);
        return;
      case "make-codec":
        Generate.main(next);
        return;
      case "make-api":
        Tool.build("saas/api.xml", new File("."));
        return;
      case "bundle-js":
        BundleJavaScript.main(next);
        return;
      case "make-et":
        GenerateTables.main(next);
        return;
      case "copyright":
        copyright();
        return;
      case "help":
        contribHelp();
        return;
    }
  }

  public static void contribHelp() {
    System.out.println(Util.prefix("Adama development tools for contributors (i.e. people that contribute to the Adama Platform).", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama contrib", Util.ANSI.Green) + " " + Util.prefix("[CONTRIBSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("CONTRIBSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("tests-adama", Util.ANSI.Green) + "       Generate tests for Adama Language.");
    System.out.println("    " + Util.prefix("tests-rxhtml", Util.ANSI.Green) + "      Generate tests for RxHTML.");
    System.out.println("    " + Util.prefix("make-api", Util.ANSI.Green) + "          Produces api files for SaaS and documentation for the WebSocket low level API.");
    System.out.println("    " + Util.prefix("make-et", Util.ANSI.Green) + "           Generates the error table which provides useful insight to issues");
    System.out.println("    " + Util.prefix("make-codec", Util.ANSI.Green) + "        Generates the networking codec");
    System.out.println("    " + Util.prefix("bundle-js", Util.ANSI.Green) + "         Bundles the libadama.js into the webserver");
    System.out.println("    " + Util.prefix("copyright", Util.ANSI.Green) + "         Sprinkle Jeff's name everywhere.");
  }

  private static void scan(File root) throws Exception {
    for (File f : root.listFiles()) {
      if (f.isDirectory()) {
        scan(f);
      } else {
        if (f.getName().endsWith(".java")) {
          String code = Files.readString(f.toPath());
          int start = code.indexOf("/*");
          int end = code.indexOf("*/");
          String newCode = null;
          if (start >= 0 && start <= 5 && end > start) {
            newCode = DefaultCopyright.COPYRIGHT_FILE_PREFIX + code.substring(end + 2).trim() + "\n";
          } else {
            newCode = DefaultCopyright.COPYRIGHT_FILE_PREFIX + code.trim() + "\n";
          }
          if (!code.equals(newCode)) {
            Files.writeString(f.toPath(), newCode);
          }
        }
      }
    }
  }

  public static void copyright() throws Exception {
    scan(new File("."));
  }
}

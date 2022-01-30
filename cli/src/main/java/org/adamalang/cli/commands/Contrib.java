/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.cli.commands;

import org.adamalang.apikit.Tool;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.DefaultCopyright;
import org.adamalang.support.GenerateLanguageTests;

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
      case "generate":
        GenerateLanguageTests.generate(0, next);
        return;
      case "make-api":
        Tool.build("saas/api.xml", new File("."));
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
    System.out.println("    " + Util.prefix("generate", Util.ANSI.Green) + "          Generates the core test files from scripts.");
    System.out.println("    " + Util.prefix("make-api", Util.ANSI.Green) + "          Produces api files for SaaS and documentation for the WebSocket low level API.");
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

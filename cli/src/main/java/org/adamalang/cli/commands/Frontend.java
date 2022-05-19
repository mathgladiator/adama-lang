package org.adamalang.cli.commands;

import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.rxhtml.RxHtmlTool;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;

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
      case "help":
        frontendHelp();
        return;
    }
  }

  public static void frontendHelp() {
    System.out.println(Util.prefix("Tools to help with frontend.", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama frontend", Util.ANSI.Green) + " " + Util.prefix("[FRONTENDSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FRONTENDSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("rxhtml", Util.ANSI.Green) + "            Compile an rxhtml template set");
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
    Files.writeString(new File(output).toPath(), RxHtmlTool.convertFilesToTemplateForest(files));
  }
}

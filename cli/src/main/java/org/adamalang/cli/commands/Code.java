/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.lsp.LanguageServer;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.services.FirstPartyServices;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;

import java.io.File;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Map;

public class Code {
  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      codeHelp();
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "lsp":
        lsp(next);
        return;
      case "validate-plan":
        validatePlan(config, next);
        return;
      case "compile-file":
        compileFile(config, next);
        return;
      case "reflect-dump":
        reflectDump(config, next);
        return;
      case "help":
        codeHelp();
        return;
    }
  }

  public static boolean validatePlan(Config config, String[] args) throws Exception {
    String planFilename = Util.extractOrCrash("--plan", "-p", args);
    File planFile = new File(planFilename);
    if (!planFile.exists()) {
      System.err.println(Util.prefix("Plan file does not exist; stopping", Util.ANSI.Red));
      return false;
    }
    if (planFile.isDirectory()) {
      System.err.println(Util.prefix("Plan file is a directory; stopping", Util.ANSI.Red));
      return false;
    }
    if (sharedValidatePlan(planFile)) {
      System.out.println(Util.prefix("Validated!", Util.ANSI.Green));
      return true;
    } else {
      System.err.println(Util.prefix("Plan file failed to validate", Util.ANSI.Red));
      return false;
    }
  }

  public static boolean compileFile(Config config, String[] args) throws Exception {
    String codeFilename = Util.extractOrCrash("--file", "-f", args);
    String dumpTo = Util.extractWithDefault("--dump-to", "-d", null, args);
    File codeFile = new File(codeFilename);
    if (!codeFile.exists()) {
      System.err.println(Util.prefix("File does not exist; stopping", Util.ANSI.Red));
      return false;
    }
    if (codeFile.isDirectory()) {
      System.err.println(Util.prefix("File is a directory; stopping", Util.ANSI.Red));
      return false;
    }
    CompileResult result = sharedCompileCode(codeFilename, Files.readString(codeFile.toPath()));
    if (result != null) {
      System.out.println(Util.prefix("Compiled!", Util.ANSI.Green));
      if (dumpTo != null) {
        System.out.println("Dumped: " + dumpTo);
        Files.writeString(new File(dumpTo).toPath(), result.code);
      }
      return true;
    } else {
      System.err.println(Util.prefix("File failed to compile", Util.ANSI.Red));
      return false;
    }
  }

  public static void reflectDump(Config config, String[] args) throws Exception {
    String codeFilename = Util.extractOrCrash("--file", "-f", args);
    String dumpTo = Util.extractWithDefault("--dump-to", "-d", null, args);
    File codeFile = new File(codeFilename);
    if (!codeFile.exists()) {
      System.err.println(Util.prefix("File does not exist; stopping", Util.ANSI.Red));
      return;
    }
    if (codeFile.isDirectory()) {
      System.err.println(Util.prefix("File is a directory; stopping", Util.ANSI.Red));
      return;
    }
    CompileResult result = sharedCompileCode(codeFilename, Files.readString(codeFile.toPath()));
    if (result != null) {
      System.out.println(Util.prefix("Compiled!", Util.ANSI.Green));
      if (dumpTo != null) {
        System.out.println("Dumped: " + dumpTo);
        Files.writeString(new File(dumpTo).toPath(), result.reflection);
      }
    } else {
      System.err.println(Util.prefix("File failed to compile", Util.ANSI.Red));
    }
  }

  public static boolean sharedValidatePlan(File file) {
    ObjectNode node;
    try {
      node = Json.parseJsonObject(Files.readString(file.toPath()));
    } catch (Exception ex) {
      System.err.println("Plan is not json; stopping");
      return false;
    }
    JsonNode versionsNode = node.get("versions");
    JsonNode defaultNode = node.get("default");
    JsonNode planNode = node.get("plan");

    boolean success = true;
    if (versionsNode == null || !versionsNode.isObject()) {
      System.err.println("'versions' node in root plan json is not an object or doesn't exist");
      success = false;
    } else {
      Iterator<Map.Entry<String, JsonNode>> it = ((ObjectNode) versionsNode).fields();
      while (it.hasNext()) {
        Map.Entry<String, JsonNode> entry = it.next();
        if (entry.getValue() == null || !entry.getValue().isTextual()) {
          System.err.println("version '"+entry.getKey()+"' didn't exist or wasn't text");
          success = false;
        } else {
          if (sharedCompileCode(entry.getKey(), entry.getValue().textValue()) == null) {
            System.err.println("version '"+entry.getValue()+"' failed to validate");
            success = false;
          }
        }
      }
    }

    if (defaultNode == null || !defaultNode.isTextual()) {
      System.err.println("'default' node in root plan json is not text or doesn't exist");
      success = false;
    } else {
      JsonNode planVersion = versionsNode.get(defaultNode.textValue());
      if (planVersion == null || planVersion.isNull()) {
        System.err.println("default version of '" + defaultNode.textValue() + "' pointed to a version which was not found");
        success = false;
      }
    }

    if (planNode == null || !planNode.isArray()) {
      System.err.println("'plan' node in root plan json is not an array (or doesn't exist)");
      success = false;
    } else {
      for (int k = 0; k < planNode.size(); k++) {
        if (!planNode.get(k).isTextual()) {
          System.err.println("'plan' element " + k + " is not text");
          success = false;
        } else {
          JsonNode planVersion = versionsNode.get(planNode.get(k).textValue());
          if (planVersion == null || planVersion.isNull()) {
            System.err.println("'plan' element " + k + " pointed to a version which was not found");
            success = false;
          }
        }
      }
    }
    return success;
  }

  public static class CompileResult {
    public final String code;
    public final String reflection;

    public CompileResult(String code, String reflection) {
      this.code = code;
      this.reflection = reflection;
    }
  }

  public static CompileResult sharedCompileCode(String filename, String code) {
    try {
      final var options = CompilerOptions.start().make();
      final var globals = GlobalObjectPool.createPoolWithStdLib();
      final var state = new EnvironmentState(globals, options);
      final var document = new Document();
      FirstPartyServices.install(new NoOpMetricsFactory(), null, null, null);
      document.setClassName("TempClass");
      final var tokenEngine = new TokenEngine(filename, code.codePoints().iterator());
      final var parser = new Parser(tokenEngine);
      parser.document().accept(document);
      boolean result = document.check(state);
      if (result) {
        String javaCode = document.compileJava(state);
        JsonStreamWriter reflect = new JsonStreamWriter();
        document.writeTypeReflectionJson(reflect);
        return new CompileResult(javaCode, reflect.toString());
      }
      ArrayNode parsed = (ArrayNode) (new JsonMapper().readTree(document.errorsJson()));
      for (int k = 0; k < parsed.size(); k++) {
        ObjectNode errorItem = (ObjectNode) parsed.get(k);
        int startLine = errorItem.get("range").get("start").get("line").intValue();
        int endLine = errorItem.get("range").get("end").get("line").intValue();
        int startCharacter = errorItem.get("range").get("start").get("character").intValue();
        int endCharacter = errorItem.get("range").get("end").get("character").intValue();
        System.err.println(Util.prefix("[start=" + startLine + ":" + startCharacter + ", end=" + endLine + ":" + endCharacter + "]", Util.ANSI.Red) + " :" + errorItem.get("message").textValue());
      }
      return null;
    } catch (Exception ex) {
      System.err.println(Util.prefix("Parsing exception:" + ex.getMessage(), Util.ANSI.Red));
      return null;
    }
  }

  public static void codeHelp() {
    System.out.println(Util.prefix("Local development tools.", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama code", Util.ANSI.Green) + " " + Util.prefix("[CODESUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("CODESUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("validate-plan", Util.ANSI.Green) + "     Validates a deployment plan (locally) for speed");
    System.out.println("    " + Util.prefix("compile-file", Util.ANSI.Green) + "      Compiles the adama file and shows any problems");
    System.out.println("    " + Util.prefix("reflect-dump", Util.ANSI.Green) + "      Compiles the adama file and dumps the reflection json");
    System.out.println("    " + Util.prefix("lsp", Util.ANSI.Green) + "               Spin up a single threaded language service protocol server");
  }

  public static void lsp(String[] args) throws Exception {
    int port = 2423;
    for (int k = 0; k + 1 < args.length; k++) {
      if ("--port".equals(args[k])) {
        port = Integer.parseInt(args[k + 1]);
      }
    }
    LanguageServer.singleThread(port);
  }
}

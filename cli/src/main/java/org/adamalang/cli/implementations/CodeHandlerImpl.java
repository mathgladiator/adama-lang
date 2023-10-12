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
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Util;
import org.adamalang.cli.implementations.code.Diagram;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.CodeHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.lsp.LanguageServer;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.services.FirstPartyServices;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class CodeHandlerImpl implements CodeHandler {
  @Override
  public void bundlePlan(Arguments.CodeBundlePlanArgs args, Output.YesOrError output) throws Exception {
    ObjectNode plan = Json.newJsonObject();
    if ("true".equalsIgnoreCase(args.instrument) || "yes".equalsIgnoreCase(args.instrument)) {
      plan.put("instrument", true);
    }
    ObjectNode version = plan.putObject("versions").putObject("file");
    version.put("main", Files.readString(new File(args.main).toPath()));
    ObjectNode includes = version.putObject("includes");
    for (Map.Entry<String, String> entry : getImports(args.imports).entrySet()) {
      includes.put(entry.getKey(), entry.getValue());
    }
    plan.put("default", "file");
    plan.putArray("plan");
    Files.writeString(new File(args.output).toPath(), plan.toPrettyString());
    output.out();
  }

  public void fillImports(File imports, String prefix, HashMap<String, String> map) throws Exception {
    if (imports.exists() && imports.isDirectory()) {
      for (File f : imports.listFiles()) {
        if (f.getName().endsWith(".adama")) {
          String name = prefix + f.getName().substring(0, f.getName().length() - 6);
          map.put(name, Files.readString(f.toPath()));
        } else if (f.isDirectory()) {
          fillImports(f, prefix + f.getName() + "/", map);
        }
      }
    }
  }

  public HashMap<String, String> getImports(String imports) throws Exception {
    HashMap<String, String> map = new HashMap<>();
    File fileImports = new File(imports);
    fillImports(fileImports, "", map);
    return map;
  }

  @Override
  public void compileFile(Arguments.CodeCompileFileArgs args, Output.YesOrError output) throws Exception {
    FirstPartyServices.install(null, new NoOpMetricsFactory(), null, null, null);
    CompileResult result = sharedCompileCode(args.file, Files.readString(new File(args.file).toPath()), getImports(args.imports));
    if (args.dumpTo != null) {
      Files.writeString(new File(args.dumpTo).toPath(), result.code);
    }
    output.out();
  }

  @Override
  public void diagram(Arguments.CodeDiagramArgs args, Output.YesOrError output) throws Exception {
    ObjectNode reflection = Json.parseJsonObject(Files.readString(new File(args.input).toPath()));
    Diagram diagram = new Diagram("Diagram");
    diagram.process(reflection);
    Files.writeString(new File(args.output).toPath(), diagram.finish());
    output.out();
  }

  @Override
  public void lsp(Arguments.CodeLspArgs args, Output.YesOrError output) throws Exception {
    int port = Integer.parseInt(args.port);
    LanguageServer.singleThread(port);
  }

  @Override
  public void reflectDump(Arguments.CodeReflectDumpArgs args, Output.YesOrError output) throws Exception {
    FirstPartyServices.install(null, new NoOpMetricsFactory(), null, null, null);
    CompileResult result = sharedCompileCode(args.file, Files.readString(new File(args.file).toPath()), getImports(args.imports));
    if (args.dumpTo != null) {
      Files.writeString(new File(args.dumpTo).toPath(), result.reflection);
    }
    output.out();
  }

  @Override
  public void validatePlan(Arguments.CodeValidatePlanArgs args, Output.YesOrError output) throws Exception {
    File planFile = new File(args.plan);
    if (!planFile.exists()) {
      throw new Exception("Plan file does not exist; stopping");
    }
    if (planFile.isDirectory()) {
      throw new Exception("Plan file is a directory; stopping");
    }
    if (!sharedValidatePlan(planFile)) {
      throw new Exception("Plan file failed to validate");
    }
    output.out();
  }

  public static boolean sharedValidatePlan(File file) {
    ObjectNode node;
    try {
      return sharedValidatePlan(Files.readString(file.toPath()));
    } catch (Exception ex) {
      System.err.println("Plan is not json; stopping");
      return false;
    }
  }

  public static boolean sharedValidatePlan(String plan) {
    return sharedValidatePlanGetLastReflection(plan, (ln) -> System.err.println(ln)) != null;
  }

  private static void reportVersionFailure(String version, Exception e, Consumer<String> println) {
    if (!(e instanceof KnownException)) {
      println.accept("version '" + version + "' failed to validate: " + e.getMessage());
      e.printStackTrace();
    } else {
      println.accept("version '" + version + "' failed to validate");
    }
  }

  public static String sharedValidatePlanGetLastReflection(String plan, Consumer<String> println) {
    ObjectNode node = Json.parseJsonObject(plan);
    JsonNode versionsNode = node.get("versions");
    JsonNode defaultNode = node.get("default");
    JsonNode planNode = node.get("plan");

    String lastReflection = null;
    boolean success = true;
    if (versionsNode == null || !versionsNode.isObject()) {
      println.accept("'versions' node in root plan json is not an object or doesn't exist");
      success = false;
    } else {
      Iterator<Map.Entry<String, JsonNode>> it = versionsNode.fields();
      while (it.hasNext()) {
        Map.Entry<String, JsonNode> entry = it.next();
        if (entry.getValue() == null) {
          println.accept("version '" + entry.getKey() + "' didn't exist or wasn't text");
          success = false;
        } else if (entry.getValue().isTextual()) {
          try {
            sharedCompileCode(entry.getKey(), entry.getValue().textValue(), new HashMap<>(), println);
          } catch (Exception e) {
            reportVersionFailure(entry.getKey(), e, println);
            success = false;
          }
        } else if (entry.getValue().isObject()) {
          ObjectNode bundle = (ObjectNode) entry.getValue();
          JsonNode main = bundle.get("main");
          if (main == null || !main.isTextual()) {
            println.accept("bundle '" + bundle.toPrettyString() + "' doesn't have a main or if it wasn't text");
            success = false;
          } else {
            JsonNode includesNode = bundle.get("includes");
            HashMap<String, String> includes = new HashMap<>();
            if (includesNode == null || includesNode.isNull()) {
              // this is fine, nothing to include
            } else if (!includesNode.isObject()) {
              println.accept("bundle '" + bundle.toPrettyString() + "' doesn't have an includes that is an object");
              success = false;
            } else {
              Iterator<Map.Entry<String, JsonNode>> inf = includesNode.fields();
              while (inf.hasNext()) {
                Map.Entry<String, JsonNode> infe = inf.next();
                if (infe.getValue() == null | !infe.getValue().isTextual()) {
                  println.accept("bundle '" + infe.getKey() + "' was either null or not text when it should be text");
                  success = false;
                } else {
                  includes.put(infe.getKey(), infe.getValue().textValue());
                }
              }
            }
            try {
              lastReflection = sharedCompileCode(entry.getKey(), main.textValue(), includes, println).reflection;
            } catch (Exception e) {
              reportVersionFailure(entry.getKey(), e, println);
              success = false;
            }
          }
        }
      }
    }

    if (defaultNode == null || !defaultNode.isTextual()) {
      println.accept("'default' node in root plan json is not text or doesn't exist");
      success = false;
    } else {
      JsonNode planVersion = versionsNode.get(defaultNode.textValue());
      if (planVersion == null || planVersion.isNull()) {
        println.accept("default version of '" + defaultNode.textValue() + "' pointed to a version which was not found");
        success = false;
      }
    }

    if (planNode == null || !planNode.isArray()) {
      println.accept("'plan' node in root plan json is not an array (or doesn't exist)");
      success = false;
    } else {
      for (int k = 0; k < planNode.size(); k++) {
        if (!planNode.get(k).isTextual()) {
          println.accept("'plan' element " + k + " is not text");
          success = false;
        } else {
          JsonNode planVersion = versionsNode.get(planNode.get(k).textValue());
          if (planVersion == null || planVersion.isNull()) {
            println.accept("'plan' element " + k + " pointed to a version which was not found");
            success = false;
          }
        }
      }
    }
    if (success) {
      return lastReflection;
    }
    return null;
  }

  public static CompileResult sharedCompileCode(String filename, String code, HashMap<String, String> includes) throws Exception {
    return sharedCompileCode(filename, code, includes, (String ln) -> System.err.println(Util.prefix(ln, Util.ANSI.Red)));
  }

  public static CompileResult sharedCompileCode(String filename, String code, HashMap<String, String> includes, Consumer<String> println) throws Exception {
    final var options = CompilerOptions.start().make();
    final var globals = GlobalObjectPool.createPoolWithStdLib();
    final var state = new EnvironmentState(globals, options);
    final var document = new Document();
    document.setClassName("TempClass");
    final var tokenEngine = new TokenEngine(filename, code.codePoints().iterator());
    final var parser = new Parser(tokenEngine, Scope.makeRootDocument());
    document.setIncludes(includes);
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
      String file = errorItem.has("file") ? errorItem.get("file").textValue() : "unknown-file (bug)";
      println.accept("[" + file + ";start=" + startLine + ":" + startCharacter + ", end=" + endLine + ":" + endCharacter + "] :" + errorItem.get("message").textValue());
    }
    throw new KnownException();
  }

  private static class KnownException extends Exception {

  }

  public static class CompileResult {
    public final String code;
    public final String reflection;

    public CompileResult(String code, String reflection) {
      this.code = code;
      this.reflection = reflection;
      try {
        new LivingDocumentFactory("space", "TempClass", code, reflection, Deliverer.FAILURE, new TreeMap<>());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}

/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.validators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.runtime.deploy.DeployedVersion;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.deploy.SyncCompiler;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.translator.env.CompilerOptions;
import org.adamalang.translator.env.EnvironmentState;
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.env.RuntimeEnvironment;
import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.Parser;
import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.Document;
import org.adamalang.translator.tree.SymbolIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatePlan {
  private static final Logger LOG = LoggerFactory.getLogger(ValidatePlan.class);
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(LOG);

  /** mainline path */
  public static void validate(String space, ObjectNode node) throws ErrorCodeException {
    DeploymentPlan plan = new DeploymentPlan(node.toString(), LOGGER);
    for (Map.Entry<String, DeployedVersion> entry : plan.versions.entrySet()) {
      CompilerOptions.Builder builder = CompilerOptions.start();
      if (plan.instrument) {
        builder = builder.instrument();
      }
      final var options = builder.make();
      final var globals = GlobalObjectPool.createPoolWithStdLib(RuntimeEnvironment.Tooling);
      final var state = new EnvironmentState(globals, options);
      final var document = new Document();

      MessageDigest digest = Hashing.sha384();
      digest.update( entry.getValue().main.getBytes(StandardCharsets.UTF_8));
      for (Map.Entry<String, String> includeEntry : entry.getValue().includes.entrySet()) {
        digest.update(includeEntry.getKey().getBytes(StandardCharsets.UTF_8));
        digest.update(includeEntry.getValue().getBytes(StandardCharsets.UTF_8));
      }
      String className = "Test" + space + "_" + Hashing.finishAndEncodeHex(digest);
      document.setClassName(className);
      document.setIncludes(entry.getValue().includes);
      final var tokenEngine = new TokenEngine("main", entry.getValue().main.codePoints().iterator());
      final var parser = new Parser(tokenEngine, document.getSymbolIndex(), Scope.makeRootDocument());
      try {
        parser.document().accept(document);
      } catch (AdamaLangException ex) {
        throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_CANT_PARSE_LANGUAGE, ex);
      }
      if (!document.check(state.scope())) {
        throw new ErrorCodeException(ErrorCodes.DEPLOYMENT_CANT_TYPE_LANGUAGE, document.errorsJson());
      }
      final var java = document.compileJava(state);
      SyncCompiler.compile(space, className, java, "");
    }
  }

  public static String sharedValidatePlanGetLastReflection(String plan, String mainName, File includePath, Consumer<String> log, Consumer<ArrayNode> diagnostics, Consumer<SymbolIndex> index) {
    ObjectNode node = Json.parseJsonObject(plan);
    JsonNode versionsNode = node.get("versions");
    JsonNode defaultNode = node.get("default");
    JsonNode planNode = node.get("plan");

    String lastReflection = null;
    boolean success = true;
    if (versionsNode == null || !versionsNode.isObject()) {
      log.accept("'versions' node in root plan json is not an object or doesn't exist");
      success = false;
    } else {
      Iterator<Map.Entry<String, JsonNode>> it = versionsNode.fields();
      while (it.hasNext()) {
        Map.Entry<String, JsonNode> entry = it.next();
        if (entry.getValue() == null) {
          log.accept("version '" + entry.getKey() + "' didn't exist or wasn't text");
          success = false;
        } else if (entry.getValue().isTextual()) {
          try {
            sharedCompileCode(mainName, includePath, entry.getValue().textValue(), new HashMap<>(), log, diagnostics, index);
          } catch (Exception e) {
            LOG.error("failed-compile", e);
            reportVersionFailure(entry.getKey(), e, log);
            success = false;
          }
        } else if (entry.getValue().isObject()) {
          ObjectNode bundle = (ObjectNode) entry.getValue();
          JsonNode main = bundle.get("main");
          if (main == null || !main.isTextual()) {
            log.accept("bundle '" + bundle.toPrettyString() + "' doesn't have a main or if it wasn't text");
            success = false;
          } else {
            JsonNode includesNode = bundle.get("includes");
            HashMap<String, String> includes = new HashMap<>();
            if (includesNode == null || includesNode.isNull()) {
              // this is fine, nothing to include
            } else if (!includesNode.isObject()) {
              log.accept("bundle '" + bundle.toPrettyString() + "' doesn't have an includes that is an object");
              success = false;
            } else {
              Iterator<Map.Entry<String, JsonNode>> inf = includesNode.fields();
              while (inf.hasNext()) {
                Map.Entry<String, JsonNode> infe = inf.next();
                if (infe.getValue() == null | !infe.getValue().isTextual()) {
                  log.accept("bundle '" + infe.getKey() + "' was either null or not text when it should be text");
                  success = false;
                } else {
                  includes.put(infe.getKey(), infe.getValue().textValue());
                }
              }
            }
            try {
              lastReflection = sharedCompileCode(mainName, includePath, main.textValue(), includes, log, diagnostics, index).reflection;
            } catch (Exception e) {
              reportVersionFailure(entry.getKey(), e, log);
              success = false;
            }
          }
        }
      }
    }

    if (defaultNode == null || !defaultNode.isTextual()) {
      log.accept("'default' node in root plan json is not text or doesn't exist");
      success = false;
    } else {
      JsonNode planVersion = versionsNode.get(defaultNode.textValue());
      if (planVersion == null || planVersion.isNull()) {
        log.accept("default version of '" + defaultNode.textValue() + "' pointed to a version which was not found");
        success = false;
      }
    }

    if (planNode == null || !planNode.isArray()) {
      log.accept("'plan' node in root plan json is not an array (or doesn't exist)");
      success = false;
    } else {
      for (int k = 0; k < planNode.size(); k++) {
        if (!planNode.get(k).isTextual()) {
          log.accept("'plan' element " + k + " is not text");
          success = false;
        } else {
          JsonNode planVersion = versionsNode.get(planNode.get(k).textValue());
          if (planVersion == null || planVersion.isNull()) {
            log.accept("'plan' element " + k + " pointed to a version which was not found");
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

  public static class CompileResult {
    public final String code;
    public final String reflection;

    public CompileResult(String code, String reflection) {
      this.code = code;
      this.reflection = reflection;
      try {
        new LivingDocumentFactory(SyncCompiler.compile("space", "TempClass", code, reflection), Deliverer.FAILURE, new TreeMap<>());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  private static class KnownException extends Exception {
  }

  public static CompileResult sharedCompileCode(String filename, File includePath, String code, HashMap<String, String> includes, Consumer<String> log, Consumer<ArrayNode> diagnostics, Consumer<SymbolIndex> index) throws Exception {
    final var options = CompilerOptions.start().make();
    final var globals = GlobalObjectPool.createPoolWithStdLib(RuntimeEnvironment.Tooling);
    final var state = new EnvironmentState(globals, options);
    final var document = new Document();
    document.setIncludeRoot(includePath);
    document.setClassName("TempClass");
    final var tokenEngine = new TokenEngine(filename, code.codePoints().iterator());
    final var parser = new Parser(tokenEngine, document.getSymbolIndex(), Scope.makeRootDocument());
    document.setIncludes(includes);
    parser.document().accept(document);
    boolean result = document.check(state);
    if (result) {
      diagnostics.accept(Json.newJsonArray());
      index.accept(document.getSymbolIndex());
      String javaCode = document.compileJava(state);
      JsonStreamWriter reflect = new JsonStreamWriter();
      document.writeTypeReflectionJson(reflect);
      return new CompileResult(javaCode, reflect.toString());
    }
    String potentialRoot = Pathing.removeLast(filename);
    ArrayNode parsed = (ArrayNode) (new JsonMapper().readTree(document.errorsJson()));
    diagnostics.accept(parsed);
    HashMap<String, String> normalized = new HashMap<>();
    for (int k = 0; k < parsed.size(); k++) {
      ObjectNode errorItem = (ObjectNode) parsed.get(k);
      int startLine = errorItem.get("range").get("start").get("line").intValue();
      int endLine = errorItem.get("range").get("end").get("line").intValue();
      int startCharacter = errorItem.get("range").get("start").get("character").intValue();
      int endCharacter = errorItem.get("range").get("end").get("character").intValue();
      String file = errorItem.has("file") ? errorItem.get("file").textValue() : "unknown-file (bug)";
      String nfile = normalized.get(file);
      if (nfile == null) {
        nfile = Pathing.removeCommonRootFromB(potentialRoot, file);
        normalized.put(file, nfile);
      }
      log.accept("[" + nfile + ";start=" + startLine + ":" + startCharacter + ", end=" + endLine + ":" + endCharacter + "] :" + errorItem.get("message").textValue());
    }
    throw new KnownException();
  }

  private static void reportVersionFailure(String version, Exception e, Consumer<String> log) {
    String prefix = "";
    if (!"file".equals(version)) {
      prefix = "version '" + version + "' ";
    }
    if (!(e instanceof KnownException)) {
      log.accept( prefix + "failed to validate: " + e.getMessage());
    } else {
      log.accept(prefix + "failed to validate");
    }
  }
}

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
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.CoreServicesNexus;
import org.adamalang.cli.implementations.code.BenchmarkArchiveReplay;
import org.adamalang.cli.implementations.code.BenchmarkSingleMessage;
import org.adamalang.cli.implementations.code.Diagram;
import org.adamalang.cli.implementations.code.Imports;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.CodeHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.*;
import org.adamalang.lsp.LanguageServer;
import org.adamalang.CoreServices;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.deploy.AsyncByteCodeCache;
import org.adamalang.runtime.deploy.AsyncCompiler;
import org.adamalang.runtime.deploy.DeploymentFactory;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.reactives.RxLazy;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.LivingDocument;
import org.adamalang.runtime.sys.LivingDocumentChange;
import org.adamalang.runtime.sys.PerfTracker;
import org.adamalang.translator.env.RuntimeEnvironment;
import org.adamalang.translator.env2.Scope;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.adamalang.translator.parser.*;
import org.adamalang.translator.parser.Formatter;
import org.adamalang.translator.parser.token.TokenEngine;
import org.adamalang.translator.tree.SymbolIndex;
import org.adamalang.validators.ValidatePlan;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
    for (Map.Entry<String, String> entry : Imports.get(args.imports).entrySet()) {
      includes.put(entry.getKey(), entry.getValue());
    }
    plan.put("default", "file");
    plan.putArray("plan");
    Files.writeString(new File(args.output).toPath(), plan.toPrettyString());
    output.out();
  }

  @Override
  public void benchmarkArchiveReplay(Arguments.CodeBenchmarkArchiveReplayArgs args, Output.YesOrError output) throws Exception {
    BenchmarkArchiveReplay.go(args, output);
  }

  @Override
  public void benchmarkMessage(Arguments.CodeBenchmarkMessageArgs args, Output.YesOrError output) throws Exception {
    BenchmarkSingleMessage.go(args, output);
  }

  public void formatSingleFile(File file) throws Exception {
    if (!file.getName().endsWith(".adama")) {
      return;
    }
    String input = Files.readString(file.toPath());
    final var tokenEngine = new TokenEngine(file.getName(), input.codePoints().iterator());
    final var parser = new Parser(tokenEngine, new SymbolIndex(), Scope.makeRootDocument());
    Consumer<TopLevelDocumentHandler> play = parser.document();
    Formatter formatter = new Formatter();
    play.accept(new WhiteSpaceNormalizeTokenDocumentHandler());
    play.accept(new FormatDocumentHandler(formatter));
    final var esb = new StringBuilderDocumentHandler();
    play.accept(esb);
    String output = esb.builder.toString();
    Files.writeString(file.toPath(), output);
  }

  private void scanFormat(File file) throws Exception {
    if (file.isDirectory()) {
      for (File ch : file.listFiles()) {
        scanFormat(ch);
      }
    } else {
      formatSingleFile(file);
    }
  }

  @Override
  public void format(Arguments.CodeFormatArgs args, Output.YesOrError output) throws Exception {
    scanFormat(new File(args.file));
  }

  @Override
  public void compileFile(Arguments.CodeCompileFileArgs args, Output.YesOrError output) throws Exception {
    CoreServices.install(CoreServicesNexus.NOOP());
    ValidatePlan.CompileResult result = sharedCompileCode(args.file, Files.readString(new File(args.file).toPath()), Imports.get(args.imports));
    if (args.dumpTo != null) {
      Files.writeString(new File(args.dumpTo).toPath(), result.code);
    }
    output.out();
  }

  @Override
  public void diagram(Arguments.CodeDiagramArgs args, Output.YesOrError output) throws Exception {
    ObjectNode reflection = Json.parseJsonObject(Files.readString(new File(args.input).toPath()));
    Diagram diagram = new Diagram("Diagram");
    diagram.process(reflection, "true".equalsIgnoreCase(args.includeRoot));
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
    CoreServices.install(CoreServicesNexus.NOOP());
    ValidatePlan.CompileResult result = CodeHandlerImpl.sharedCompileCode(args.file, Files.readString(new File(args.file).toPath()), Imports.get(args.imports));
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
    {
      try {
        if (!sharedValidatePlan(Files.readString(planFile.toPath()), planFile.toString())) {
          throw new Exception("Plan file failed to validate");
        }
      } catch (Exception ex) {
        throw new Exception("Plan is not json; stopping");
      }
    }
    output.out();
  }

  public static boolean sharedValidatePlan(String plan, String mainName) {
    return ValidatePlan.sharedValidatePlanGetLastReflection(plan, mainName, null, (ln) -> System.err.println(ln), (diag) -> {}, (index) -> {}) != null;
  }

  public static ValidatePlan.CompileResult sharedCompileCode(String filename, String code, HashMap<String, String> includes) throws Exception {
    return ValidatePlan.sharedCompileCode(filename, null, code, includes, (String ln) -> System.err.println(ColorUtilTools.prefix(ln, ANSI.Red)), (diag) -> {}, (index) -> {});
  }
}

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

import org.adamalang.CoreServices;
import org.adamalang.CoreServicesNexus;
import org.adamalang.GenerateTables;
import org.adamalang.caravan.events.Events;
import org.adamalang.common.*;
import org.adamalang.devbox.BundleRawJavaScriptForDevBox;
import org.adamalang.cli.implementations.docgen.BookGenerator;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.ContribHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.codec.CodecCodeGen;
import org.adamalang.common.gossip.codec.GossipProtocol;
import org.adamalang.frontend.EmbedTemplates;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerMessage;
import org.adamalang.support.GenerateLanguageTests;
import org.adamalang.support.GenerateTemplateTests;
import org.adamalang.train.message.Messages;
import org.adamalang.validators.ValidatePlan;
import org.adamalang.web.service.BundleJavaScript;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContribHandlerImpl implements ContribHandler {
  private static void copyrightScan(File root) throws Exception {
    for (File f : root.listFiles()) {
      if (f.isDirectory()) {
        copyrightScan(f);
      } else {
        if (f.getName().endsWith(".java")) {
          String code = Files.readString(f.toPath());
          int start = code.indexOf("/*");
          int end = code.indexOf("*/");
          String newCode = null;
          if (start >= 0 && start <= 5 && end > start) {
            newCode = DefaultCopyright.COPYRIGHT_FILE_PREFIX + code.substring(end + 2).trim().replaceAll(Pattern.quote("\r"), Matcher.quoteReplacement("")) + "\n";
          } else {
            newCode = DefaultCopyright.COPYRIGHT_FILE_PREFIX + code.trim().replaceAll(Pattern.quote("\r"), Matcher.quoteReplacement("")) + "\n";
          }
          if (!code.equals(newCode)) {
            Files.writeString(f.toPath(), newCode);
          }
        }
      }
    }
  }

  public static void main(String[] x) throws Exception {
    Arguments.ContribMakeBookArgs args = new Arguments.ContribMakeBookArgs();
    Output.YesOrError out = new Output(x).makeYesOrError();
    args.input = "reference/src";
    args.output = "reference/output";
    args.bookTemplate = "reference/template.html";
    args.bookMerge = "reference/merge";
    new ContribHandlerImpl().makeBook(args, out);
  }

  @Override
  public void bundleJs(Arguments.ContribBundleJsArgs args, Output.YesOrError output) throws Exception {
    System.out.println(ColorUtilTools.prefix("Bundling JavaScript for Web", ANSI.Cyan));
    Files.writeString(new File("web/src/main/java/org/adamalang/web/service/JavaScriptClient.java").toPath(), BundleJavaScript.bundle("./release/libadama.js", "./release/libadama-worker.js"));
    System.out.println(ColorUtilTools.prefix("Bundling JavaScript for DevBox", ANSI.Cyan));
    Files.writeString(new File("devbox/src/main/java/org/adamalang/devbox/JavaScriptResourcesRaw.java").toPath(), BundleRawJavaScriptForDevBox.bundle(new File("./clientjs")));
    output.out();
  }

  @Override
  public void copyright(Arguments.ContribCopyrightArgs args, Output.YesOrError output) throws Exception {
    System.out.println(ColorUtilTools.prefix("Ensuring all files have copyright notice", ANSI.Cyan));
    copyrightScan(new File("."));
    output.out();
  }

  @Override
  public void makeApi(Arguments.ContribMakeApiArgs args, Output.YesOrError output) throws Exception {
    System.out.println(ColorUtilTools.prefix("Assembling Public API", ANSI.Cyan));
    org.adamalang.apikit.Tool.build("saas/api.xml", new File("."));
    output.out();
  }

  @Override
  public void makeBook(Arguments.ContribMakeBookArgs args, Output.YesOrError output) throws Exception {
    new BookGenerator().go(args, output);
  }

  @Override
  public void makeCli(Arguments.ContribMakeCliArgs args, Output.YesOrError output) throws Exception {
    System.out.println(ColorUtilTools.prefix("Building CLI Router", ANSI.Cyan));
    org.adamalang.clikit.Tool.buildFileSystem("./cli/commands.xml");
    output.out();
  }

  @Override
  public void makeEmbed(Arguments.ContribMakeEmbedArgs args, Output.YesOrError output) throws Exception {
    System.out.println(ColorUtilTools.prefix("Building CLI Router", ANSI.Cyan));
    EmbedTemplates.doit();
    output.out();
  }

  @Override
  public void makeCodec(Arguments.ContribMakeCodecArgs args, Output.YesOrError output) throws Exception {
    System.out.println(ColorUtilTools.prefix("Creating Network Codec between Web to Adama", ANSI.Cyan));
    String client = CodecCodeGen.assembleCodec("org.adamalang.net.codec", "ClientCodec", ClientMessage.class.getDeclaredClasses());
    String server = CodecCodeGen.assembleCodec("org.adamalang.net.codec", "ServerCodec", ServerMessage.class.getDeclaredClasses());
    Files.writeString(new File("./net/src/main/java/org/adamalang/net/codec/ClientCodec.java").toPath(), client);
    Files.writeString(new File("./net/src/main/java/org/adamalang/net/codec/ServerCodec.java").toPath(), server);

    System.out.println(ColorUtilTools.prefix("Creating Gossip Codec", ANSI.Cyan));
    String gossipCodec = CodecCodeGen.assembleCodec("org.adamalang.common.gossip.codec", "GossipProtocolCodec", GossipProtocol.class.getDeclaredClasses());
    Files.writeString(new File("./common/src/main/java/org/adamalang/common/gossip/codec/GossipProtocolCodec.java").toPath(), gossipCodec);

    System.out.println(ColorUtilTools.prefix("Creating Disk Codec", ANSI.Cyan));
    String diskCodec = CodecCodeGen.assembleCodec("org.adamalang.caravan.events", "EventCodec", Events.class.getDeclaredClasses());
    Files.writeString(new File("./data-caravan/src/main/java/org/adamalang/caravan/events/EventCodec.java").toPath(), diskCodec);

    System.out.println(ColorUtilTools.prefix("Creating RaFT Codec", ANSI.Cyan));
    String raftCodec = CodecCodeGen.assembleCodec("org.adamalang.train.message", "MessagesCodec", Messages.class.getDeclaredClasses());
    Files.writeString(new File("./data-train/src/main/java/org/adamalang/train/message/MessagesCodec.java").toPath(), raftCodec);
    output.out();
  }

  @Override
  public void makeEt(Arguments.ContribMakeEtArgs args, Output.YesOrError output) throws Exception {
    System.out.println(ColorUtilTools.prefix("Making Error Table", ANSI.Cyan));
    Files.writeString(new File("errors/src/main/java/org/adamalang/ErrorTable.java").toPath(), GenerateTables.generate());
    output.out();
  }

  @Override
  public void strTemp(Arguments.ContribStrTempArgs args, Output.YesOrError output) throws Exception {
    System.out.println(ColorUtilTools.prefix("Converting string templates to code!", ANSI.Cyan));
    for (File template : new File("core/string_templates").listFiles()) {
      String str = Files.readString(template.toPath());
      String[] parts = template.getName().replaceAll("[\\.-]", "_").split(Pattern.quote("_"));
      for (int k = 0; k < parts.length; k++) {
        parts[k] = parts[k].substring(0, 1).toUpperCase(Locale.ENGLISH) + parts[k].substring(1).toLowerCase(Locale.ENGLISH);
      }
      String name = String.join("", parts);
      String java = "package org.adamalang.runtime.stdlib.intern;\n" + "\n" + "import org.adamalang.common.template.Parser;\n" + "import org.adamalang.common.template.tree.T;\n" + "\n" + "public class Template" + name + " {\n" + "  public static final String VALUE = \"" + new Escaping(str).go() + "\";\n" + "  public static final T TEMPLATE = Parser.parse(VALUE);\n" + "}\n";
      Files.writeString(new File("core/src/main/java/org/adamalang/runtime/stdlib/intern/Template" + name + ".java").toPath(), DefaultCopyright.COPYRIGHT_FILE_PREFIX + java);
    }
    output.out();
  }

  @Override
  public void testsAdama(Arguments.ContribTestsAdamaArgs args, Output.YesOrError output) throws Exception {
    System.out.println(ColorUtilTools.prefix("Generate Adama Tests", ANSI.Cyan));
    if (GenerateLanguageTests.generate(args.input, args.output, args.errors)) {
      output.out();
    } else {
      System.out.println(ColorUtilTools.prefix("Unable to generate Adama tests", ANSI.Red));
    }
  }

  @Override
  public void testsRxhtml(Arguments.ContribTestsRxhtmlArgs args, Output.YesOrError output) throws Exception {
    List<GenerateTemplateTests.CompileStep> steps = GenerateTemplateTests.prepareScripts(args.input, args.output);
    if (steps != null) {
      System.out.println(ColorUtilTools.prefix("Compiling Adama Sample Scripts for RxHTML Testing", ANSI.Cyan));
      CoreServices.install(CoreServicesNexus.NOOP());
      for (GenerateTemplateTests.CompileStep step : steps) {
        String reflection = CodeHandlerImpl.sharedCompileCode(step.input.getName(), Files.readString(step.input.toPath()), new HashMap<>()).reflection;
        Files.writeString(step.output.toPath(), Json.parseJsonObject(reflection).toPrettyString());
      }
      System.out.println(ColorUtilTools.prefix("Generating RxHTML Tests", ANSI.Cyan));
      GenerateTemplateTests.generate(args.input, args.output);
      output.out();
    } else {
      System.out.println(ColorUtilTools.prefix("Unable to generate RxHTML s", ANSI.Red));
    }
  }

  @Override
  public void version(Arguments.ContribVersionArgs args, Output.YesOrError output) throws Exception {
    String versionCode = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    MessageDigest md5 = Hashing.md5();
    md5.update(Files.readAllBytes(new File("release/libadama.js").toPath()));
    md5.update(Files.readAllBytes(new File("release/libadama-worker.js").toPath()));
    String jsver = Hashing.finishAndEncodeHex(md5);
    System.out.println(ColorUtilTools.prefix("Generating a version number: " + versionCode, ANSI.Cyan));
    String versionFile = "package org.adamalang.common;\n" + //
        "\n" + "public class Platform {\n" + //
        "  public static final String VERSION = \"" + versionCode + "\";\n" + //
        "  public static final String JS_VERSION = \"" + jsver + "\";\n" + //
        "}\n";
    Files.writeString(new File("common/src/main/java/org/adamalang/common/Platform.java").toPath(), DefaultCopyright.COPYRIGHT_FILE_PREFIX + versionFile);
    output.out();
  }
}

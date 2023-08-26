/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.implementations;

import org.adamalang.GenerateTables;
import org.adamalang.caravan.events.Events;
import org.adamalang.cli.Util;
import org.adamalang.cli.devbox.BundleRawJavaScriptForDevBox;
import org.adamalang.cli.implementations.docgen.BookGenerator;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.ContribHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.DefaultCopyright;
import org.adamalang.common.Escaping;
import org.adamalang.common.codec.CodecCodeGen;
import org.adamalang.common.gossip.codec.GossipProtocol;
import org.adamalang.frontend.EmbedTemplates;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerMessage;
import org.adamalang.support.GenerateLanguageTests;
import org.adamalang.support.GenerateTemplateTests;
import org.adamalang.web.service.BundleJavaScript;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    System.out.println(Util.prefix("Bundling JavaScript for Web", Util.ANSI.Cyan));
    Files.writeString(new File("web/src/main/java/org/adamalang/web/service/JavaScriptClient.java").toPath(), BundleJavaScript.bundle("./release/libadama.js"));
    System.out.println(Util.prefix("Bundling JavaScript for DevBox", Util.ANSI.Cyan));
    Files.writeString(new File("cli/src/main/java/org/adamalang/cli/devbox/JavaScriptResourcesRaw.java").toPath(), BundleRawJavaScriptForDevBox.bundle(new File("./clientjs")));
    output.out();
  }

  @Override
  public void copyright(Arguments.ContribCopyrightArgs args, Output.YesOrError output) throws Exception {
    System.out.println(Util.prefix("Ensuring all files have copyright notice", Util.ANSI.Cyan));
    copyrightScan(new File("."));
    output.out();
  }

  @Override
  public void makeApi(Arguments.ContribMakeApiArgs args, Output.YesOrError output) throws Exception {
    System.out.println(Util.prefix("Assembling Public API", Util.ANSI.Cyan));
    org.adamalang.apikit.Tool.build("saas/api.xml", new File("."));
    output.out();
  }

  @Override
  public void makeBook(Arguments.ContribMakeBookArgs args, Output.YesOrError output) throws Exception {
    new BookGenerator().go(args, output);
  }

  @Override
  public void makeCli(Arguments.ContribMakeCliArgs args, Output.YesOrError output) throws Exception {
    System.out.println(Util.prefix("Building CLI Router", Util.ANSI.Cyan));
    org.adamalang.clikit.Tool.buildFileSystem("./cli/commands.xml");
    output.out();
  }

  @Override
  public void makeEmbed(Arguments.ContribMakeEmbedArgs args, Output.YesOrError output) throws Exception {
    System.out.println(Util.prefix("Building CLI Router", Util.ANSI.Cyan));
    EmbedTemplates.doit();
    output.out();
  }

  @Override
  public void makeCodec(Arguments.ContribMakeCodecArgs args, Output.YesOrError output) throws Exception {
    System.out.println(Util.prefix("Creating Network Codec between Web to Adama", Util.ANSI.Cyan));
    String client = CodecCodeGen.assembleCodec("org.adamalang.net.codec", "ClientCodec", ClientMessage.class.getDeclaredClasses());
    String server = CodecCodeGen.assembleCodec("org.adamalang.net.codec", "ServerCodec", ServerMessage.class.getDeclaredClasses());
    Files.writeString(new File("./net/src/main/java/org/adamalang/net/codec/ClientCodec.java").toPath(), client);
    Files.writeString(new File("./net/src/main/java/org/adamalang/net/codec/ServerCodec.java").toPath(), server);
    System.out.println(Util.prefix("Creating Gossip Codec", Util.ANSI.Cyan));
    String gossipCodec = CodecCodeGen.assembleCodec("org.adamalang.common.gossip.codec", "GossipProtocolCodec", GossipProtocol.class.getDeclaredClasses());
    Files.writeString(new File("./common/src/main/java/org/adamalang/common/gossip/codec/GossipProtocolCodec.java").toPath(), gossipCodec);
    System.out.println(Util.prefix("Creating Disk Codec", Util.ANSI.Cyan));
    String diskCodec = CodecCodeGen.assembleCodec("org.adamalang.caravan.events", "EventCodec", Events.class.getDeclaredClasses());
    Files.writeString(new File("./data-caravan/src/main/java/org/adamalang/caravan/events/EventCodec.java").toPath(), diskCodec);
    output.out();
  }

  @Override
  public void makeEt(Arguments.ContribMakeEtArgs args, Output.YesOrError output) throws Exception {
    System.out.println(Util.prefix("Making Error Table", Util.ANSI.Cyan));
    Files.writeString(new File("errors/src/main/java/org/adamalang/ErrorTable.java").toPath(), GenerateTables.generate());
    output.out();
  }

  @Override
  public void strTemp(Arguments.ContribStrTempArgs args, Output.YesOrError output) throws Exception {
    System.out.println(Util.prefix("Converting string templates to code!", Util.ANSI.Cyan));
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
    System.out.println(Util.prefix("Generate Adama Tests", Util.ANSI.Cyan));
    GenerateLanguageTests.generate(args.input, args.output, args.errors);
    output.out();
  }

  @Override
  public void testsRxhtml(Arguments.ContribTestsRxhtmlArgs args, Output.YesOrError output) throws Exception {
    System.out.println(Util.prefix("Generating RxHTML Tests", Util.ANSI.Cyan));
    GenerateTemplateTests.generate(args.input, args.output);
    output.out();
  }

  @Override
  public void version(Arguments.ContribVersionArgs args, Output.YesOrError output) throws Exception {
    String versionCode = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    System.out.println(Util.prefix("Generating a version number: " + versionCode, Util.ANSI.Cyan));
    String versionFile = "package org.adamalang.common;\n" + "\n" + "public class Platform {\n" + "  public static final String VERSION = \"" + versionCode + "\";\n" + "}\n";
    Files.writeString(new File("common/src/main/java/org/adamalang/common/Platform.java").toPath(), DefaultCopyright.COPYRIGHT_FILE_PREFIX + versionFile);
    output.out();
  }
}

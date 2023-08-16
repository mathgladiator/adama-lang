/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.GenerateTables;
import org.adamalang.cli.Util;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.ContribHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.DefaultCopyright;
import org.adamalang.common.Escaping;
import org.adamalang.common.Json;
import org.adamalang.common.codec.CodecCodeGen;
import org.adamalang.common.gossip.codec.GossipProtocol;
import org.adamalang.common.template.Settings;
import org.adamalang.common.template.tree.T;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerMessage;
import org.adamalang.support.GenerateLanguageTests;
import org.adamalang.support.GenerateTemplateTests;
import org.adamalang.web.service.BundleJavaScript;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
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

  @Override
  public void bundleJs(Arguments.ContribBundleJsArgs args, Output.YesOrError output) throws Exception {
    System.out.println(Util.prefix("Bundling JavaScript", Util.ANSI.Cyan));
    Files.writeString(new File("web/src/main/java/org/adamalang/web/service/JavaScriptClient.java").toPath(), BundleJavaScript.bundle("./release/libadama.js"));
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
    System.out.println(Util.prefix("Assembling Global Control Plane API", Util.ANSI.Cyan));
    output.out();
  }

  @Override
  public void makeCli(Arguments.ContribMakeCliArgs args, Output.YesOrError output) throws Exception {
    System.out.println(Util.prefix("Building CLI Router", Util.ANSI.Cyan));
    org.adamalang.clikit.Tool.buildFileSystem("./cli/commands.xml");
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
    String codec = CodecCodeGen.assembleCodec("org.adamalang.common.gossip.codec", "GossipProtocolCodec", GossipProtocol.class.getDeclaredClasses());
    Files.writeString(new File("./common/src/main/java/org/adamalang/common/gossip/codec/GossipProtocolCodec.java").toPath(), codec);
    output.out();
  }

  @Override
  public void makeEt(Arguments.ContribMakeEtArgs args, Output.YesOrError output) throws Exception {
    System.out.println(Util.prefix("Making Error Table", Util.ANSI.Cyan));
    Files.writeString(new File("errors/src/main/java/org/adamalang/ErrorTable.java").toPath(), GenerateTables.generate());
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
      StringBuilder java = new StringBuilder();
      java.append("package org.adamalang.runtime.stdlib.intern;\n");
      java.append("\n");
      java.append("import org.adamalang.common.template.Parser;\n");
      java.append("import org.adamalang.common.template.tree.T;\n");
      java.append("\n");
      java.append("public class Template").append(name).append(" {\n");
      java.append("  public static final String VALUE = \"").append(new Escaping(str).go()).append("\";\n");
      java.append("  public static final T TEMPLATE = Parser.parse(VALUE);\n");
      java.append("}\n");
      Files.writeString(new File("core/src/main/java/org/adamalang/runtime/stdlib/intern/Template" + name + ".java").toPath(), DefaultCopyright.COPYRIGHT_FILE_PREFIX + java.toString());
    }
    output.out();
  }

  public void assembleBook(File root, String prefix, TreeMap<String, String> files) throws Exception {
    for (File child : root.listFiles()) {
      if (child.isDirectory()) {
        assembleBook(child, prefix + child.getName() + "/", files);
      } else {
        if (child.getName().endsWith(".md")) {
          files.put(prefix + child.getName(), Files.readString(child.toPath()));
        }
      }
    }
  }

  private String md2html(String markdown) {
    HashSet<Extension> ext = new HashSet<>();
    ext.add(TablesExtension.create());
    Parser parser = Parser.builder().extensions(ext).build();
    Node document = parser.parse(markdown);
    HtmlRenderer renderer = HtmlRenderer.builder().extensions(ext).build();
    Document doc = Jsoup.parse(renderer.render(document));
    for (Element a : doc.getElementsByTag("a")) {
      if (a.hasAttr("href")) {
        String href = a.attr("href");
        if (href.endsWith(".md")) {
          a.attr("href", href.substring(0, href.length() - 3) + ".html");
        }
      }
    }
    return doc.body().html();
  }

  private String render(String markdown, String navHtml, T template) {
    ObjectNode input = Json.newJsonObject();
    input.put("body", md2html(markdown));
    input.put("nav", navHtml);
    StringBuilder output = new StringBuilder();
    template.render(new Settings(), input, output);
    return output.toString();
  }

  private String renderNav(String markdown) {
    Document doc = Jsoup.parse(md2html(markdown));
    Element body = doc.body();
    body.getElementsByTag("h1").remove();
    for (Element element : body.children()) {
      if (element.tagName().endsWith("ul")) {
        element.attr("class", "text-sm list-disc list-inside");
        for (Element firstLevelItem : element.children()) {
          if (firstLevelItem.tagName().equals("li")) {
            firstLevelItem.attr("class", "mb-2");
            for (Element secondLevel : firstLevelItem.children()) {
              if (secondLevel.tagName().endsWith("ul")) {
                secondLevel.attr("class", "mb-3 ml-4 pl-6 border-l border-slate-200 dark:border-slate-800 list-decimal");
                for (Element secondLevelItem : secondLevel.children()) {
                  if (secondLevelItem.tagName().equals("li")) {
                    secondLevelItem.attr("class", "mt-3");
                  }
                }
              }
            }
          }
        }
      }
    }
    for (Element a : body.getElementsByTag("a")) {
      if (a.hasAttr("href")) {
        String href = a.attr("href");
        if (href.startsWith(".")) {
          a.attr("href", href.substring(1));
        }
      }
    }

    return body.html();
  }

  @Override
  public void makeBook(Arguments.ContribMakeBookArgs args, Output.YesOrError output) throws Exception {
    File input = new File(args.input);
    TreeMap<String, String> book = new TreeMap<>();
    assembleBook(input, "", book);
    File out = new File(args.output);
    out.mkdirs();

    T template = org.adamalang.common.template.Parser.parse(Files.readString(new File(args.bookTemplate).toPath()));

    String navHtml = renderNav(book.remove("SUMMARY.md"));

    for (Map.Entry<String, String> entry : book.entrySet()) {
      String path = entry.getKey();
      int lastSlash = path.lastIndexOf('/');
      File toPut = out;
      String name = path;
      if (lastSlash > 0) {
        toPut = new File(toPut, path.substring(0, lastSlash));
        toPut.mkdirs();
        name = path.substring(lastSlash + 1);
      }
      name = name.substring(0, name.lastIndexOf('.')) + ".html";
      Files.writeString(new File(toPut, name).toPath(), render(entry.getValue(), navHtml, template));
    }

    for (File child : new File(args.bookMerge).listFiles()) {
      Files.copy(child.toPath(), new File(out, child.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
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
}

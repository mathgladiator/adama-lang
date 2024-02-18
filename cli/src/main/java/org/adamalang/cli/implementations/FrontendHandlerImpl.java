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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.devbox.DevBoxInputTranslator;
import org.adamalang.cli.implementations.mobile.MobileCapacitor;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.FrontendHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.ANSI;
import org.adamalang.common.ColorUtilTools;
import org.adamalang.common.Json;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.keys.VAPIDFactory;
import org.adamalang.common.keys.VAPIDPublicPrivateKeyPair;
import org.adamalang.devbox.Start;
import org.adamalang.runtime.sys.web.rxhtml.RxHtmlResult;
import org.adamalang.rxhtml.*;
import org.adamalang.rxhtml.preprocess.MeasureAttributeSameness;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FrontendHandlerImpl implements FrontendHandler {
  public static void aggregateFiles(File file, ArrayList<File> files) {
    for (File child : file.listFiles()) {
      if (child.isDirectory()) {
        aggregateFiles(child, files);
      } else if (child.getName().endsWith(".rx.html")) {
        files.add(child);
      }
    }
  }

  @Override
  public void pushGenerate(Arguments.FrontendPushGenerateArgs args, Output.YesOrError output) throws Exception {
    VAPIDFactory factory = new VAPIDFactory(new SecureRandom());
    VAPIDPublicPrivateKeyPair vapid = factory.generateKeyPair();
    System.out.println("public:" + vapid.publicKeyBase64);
    System.out.println("private:" + vapid.privateKeyBase64);
    output.out();
  }

  private static void createFileIfNotExists(String name, Supplier<String> gen) throws Exception {
    File f = new File( name);
    if (!f.exists()) {
      Files.writeString(f.toPath(), gen.get());
      System.out.println(ColorUtilTools.prefix("created '" + name + "'", ANSI.Cyan));
    } else {
      System.out.println(ColorUtilTools.prefix("'" + name + "' already exists", ANSI.Yellow));
    }
  }

  @Override
  public void tailwindKick(Arguments.FrontendTailwindKickArgs args, Output.YesOrError output) throws Exception {
    File feDir = new File("frontend");
    if (!feDir.exists() || !feDir.isDirectory()) {
      System.err.println("'frontend' directory doesn't exist");
    }

    createFileIfNotExists("package.json", () -> //
        "{\n" + //
        "  \"scripts\": {\n" + //
        "    \"dev\": \"java -jar ~/adama.jar devbox\",\n" + //
        "    \"tailwind\": \"tailwindcss -i frontend/input.css -o assets/style.css -c frontend/tailwind.config.js --watch\"\n" + //
        "  },\n" + //
        "  \"devDependencies\": {\n" + //
        "    \"@tailwindcss/forms\": \"^0.5.7\",\n" + //
        "    \"@tailwindcss/line-clamp\": \"^0.4.4\",\n" + //
        "    \"@tailwindcss/typography\": \"^0.5.10\",\n" + //
        "    \"tailwindcss\": \"^3.4.1\",\n" + //
        "    \"tailwindcss-safe-area\": \"^0.5.1\"\n" + //
        "  }\n" + //
        "}\n");

    createFileIfNotExists("frontend/tailwind.config.js", () -> //
        "module.exports = {\n" + //
        "  content: ['./frontend/*.rx.html', './frontend/**/*.{rx.html,html}', './**/*.rx.html'],\n" + //
        "  plugins: [require('@tailwindcss/forms'), require('@tailwindcss/line-clamp'), require('tailwindcss-safe-area') ],\n" + //
        "};\n");

    createFileIfNotExists("frontend/input.css", () -> //
        "@tailwind base;\n" + //
        "@tailwind components;\n" +//
        "@tailwind utilities;\n");
    output.out();
  }

  @Override
  public void mobileCapacitor(Arguments.FrontendMobileCapacitorArgs args, Output.YesOrError output) throws Exception {
    MobileCapacitor.run(args);
    output.out();
  }

  @Override
  public void enableEncryption(Arguments.FrontendEnableEncryptionArgs args, Output.YesOrError output) throws Exception {
    File masterKey = new File("master.key.json");
    if (!masterKey.exists()) {
      ObjectNode masterKeyJson = Json.newJsonObject();
      masterKeyJson.put("mk", MasterKey.generateMasterKey());
      Files.writeString(masterKey.toPath(), masterKeyJson.toPrettyString());
    } else {
      throw new Exception(masterKey.getName() + " already exists");
    }
  }

  @Override
  public void encryptProductConfig(Arguments.FrontendEncryptProductConfigArgs args, Output.YesOrError output) throws Exception {
    String masterKey = args.config.getMasterKey();
    Files.writeString(new File(args.output).toPath(), MasterKey.encrypt(masterKey, Files.readString(new File(args.input).toPath())));
  }

  @Override
  public void decryptProductConfig(Arguments.FrontendDecryptProductConfigArgs args, Output.YesOrError output) throws Exception {
    String masterKey = args.config.getMasterKey();
    Files.writeString(new File(args.output).toPath(), MasterKey.decrypt(masterKey, Files.readString(new File(args.input).toPath())));
  }

  @Override
  public void bundle(Arguments.FrontendBundleArgs args, Output.YesOrError output) throws Exception {
    ArrayList<File> files = new ArrayList<>();
    File rxhtmlPath = new File(args.rxhtmlPath);
    aggregateFiles(rxhtmlPath, files);
    String result = Bundler.bundle(rxhtmlPath, files, false);
    Files.writeString(new File(args.output).toPath(), result);
    output.out();
  }

  @Override
  public void measure(Arguments.FrontendMeasureArgs args, Output.YesOrError output) throws Exception {
    ArrayList<File> files = new ArrayList<>();
    File rxhtmlPath = new File(args.rxhtmlPath);
    aggregateFiles(rxhtmlPath, files);
    String result = Bundler.bundle(rxhtmlPath, files, false);
    Document document = Jsoup.parse(result);
    int sourceLevelCompact = 0;
    int attributesToCompact = 0;
    System.out.println("name,value,count");
    for (Map.Entry<String, HashMap<String, Integer>> entry : MeasureAttributeSameness.measure(document).entrySet()) {
      for (Map.Entry<String, Integer> counts : entry.getValue().entrySet()) {
        int c = counts.getValue();
        if (c > 1) {
          System.out.println(entry.getKey() + "," + counts.getKey() + "," + c);
          sourceLevelCompact += counts.getKey().length() * c;
          attributesToCompact += c;
        }
      }
    }
    double percent = Math.round((10000.0 * sourceLevelCompact) / result.length()) / 100.0;
    System.out.println("# source total size: " + result.length());
    System.out.println("# source level compact: " + sourceLevelCompact);
    System.out.println("# min savings: " + percent);
    System.out.println("# attributes to compact: " + attributesToCompact);
    output.out();
  }

  @Override
  public void validate(Arguments.FrontendValidateArgs args, Output.YesOrError output) throws Exception {
    ArrayList<File> files = new ArrayList<>();
    File rxhtmlPath = new File(args.rxhtmlPath);
    aggregateFiles(rxhtmlPath, files);
    String result = Bundler.bundle(rxhtmlPath, files, true);
    TypeChecker.typecheck(result,  new File(args.types), (el, w) -> {
      String location = el.attr("ln:ch");
      System.err.println("warning:" + w + " @ " + location);
    });
    output.out();
  }

  @Override
  public void devServer(Arguments.FrontendDevServerArgs args, Output.YesOrError output) throws Exception {
    Start.start(DevBoxInputTranslator.from(args));
  }

  @Override
  public void make200(Arguments.FrontendMake200Args args, Output.YesOrError output) throws Exception {
    ArrayList<File> files = new ArrayList<>();
    File rxhtmlPath = new File(args.rxhtmlPath);
    aggregateFiles(rxhtmlPath, files);
    RxHtmlBundle bundle = RxHtmlTool.convertStringToTemplateForest(Bundler.bundle(rxhtmlPath, files, false), new File(args.types), ShellConfig.start().withEnvironment(args.environment).withFeedback((element, warning) -> System.err.println(warning)).end());
    RxHtmlResult updated = new RxHtmlResult(bundle);
    Files.writeString(new File(args.output).toPath(), updated.shell.makeShell(bundle));
    output.out();
  }

  @Override
  public void rxhtml(Arguments.FrontendRxhtmlArgs args, Output.YesOrError output) throws Exception {
    ArrayList<File> files = new ArrayList<>();
    File rxhtmlPath = new File(args.input);
    aggregateFiles(rxhtmlPath, files);
    Files.writeString(new File(args.output).toPath(), RxHtmlTool.convertStringToTemplateForest(Bundler.bundle(rxhtmlPath, files, false), new File(args.types), ShellConfig.start().withEnvironment(args.environment).withFeedback((element, warning) -> System.err.println(warning)).end()).javascript);
    output.out();
  }

  @Override
  public void setLibadama(Arguments.FrontendSetLibadamaArgs args, Output.YesOrError output) throws Exception {
    args.config.manipulate((node) -> {
      if (args.localLibadamaPath == null || "".equals(args.localLibadamaPath)) {
        node.remove("local-libadama-path-default");
      } else {
        node.put("local-libadama-path-default", args.localLibadamaPath);
      }
    });
    output.out();
  }

  @Override
  public void wrapCss(Arguments.FrontendWrapCssArgs args, Output.YesOrError output) throws Exception {
    String css = Files.readString(new File(args.input).toPath());
    Files.writeString(new File(args.output).toPath(), "<forest><style>\n" + css + "\n</style></forest>");
    output.out();
  }
}

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
import com.helger.css.ECSSVersion;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.reader.CSSReader;
import org.adamalang.cli.css.StudyEngine;
import org.adamalang.cli.devbox.DevBoxStart;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.FrontendHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;
import org.adamalang.common.keys.VAPIDFactory;
import org.adamalang.common.keys.VAPIDPublicPrivateKeyPair;
import org.adamalang.rxhtml.*;
import org.adamalang.rxhtml.template.config.ShellConfig;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;

public class FrontendHandlerImpl implements FrontendHandler {
  private static void aggregateFiles(File file, ArrayList<File> files) {
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

  @Override
  public void mobileCapacitor(Arguments.FrontendMobileCapacitorArgs args, Output.YesOrError output) throws Exception {
    ArrayList<File> files = new ArrayList<>();
    aggregateFiles(new File(args.rxhtmlPath), files);
    String result = Bundler.bundle(files, false);
    String shell = CapacitorJSShell.makeMobileShell(result, args.domain, (el, w) -> {
      System.err.println("warning:" + w);
    });
    Files.writeString(new File(args.output).toPath(), shell);
  }

  @Override
  public void bundle(Arguments.FrontendBundleArgs args, Output.YesOrError output) throws Exception {
    ArrayList<File> files = new ArrayList<>();
    aggregateFiles(new File(args.rxhtmlPath), files);
    String result = Bundler.bundle(files, false);
    Files.writeString(new File(args.output).toPath(), result);
    output.out();
  }

  @Override
  public void validate(Arguments.FrontendValidateArgs args, Output.YesOrError output) throws Exception {
    ArrayList<File> files = new ArrayList<>();
    aggregateFiles(new File(args.rxhtmlPath), files);
    String result = Bundler.bundle(files, false);
    TypeChecker.typecheck(result, (el, w) -> {
      System.err.println("warning:" + w);
    });
    output.out();
  }

  @Override
  public void devServer(Arguments.FrontendDevServerArgs args, Output.YesOrError output) throws Exception {
    DevBoxStart.start(new DevBoxStart.DevBoxInputs(args));
  }

  @Override
  public void make200(Arguments.FrontendMake200Args args, Output.YesOrError output) throws Exception {
    ArrayList<File> files = new ArrayList<>();
    aggregateFiles(new File(args.rxhtmlPath), files);
    RxHtmlResult updated = RxHtmlTool.convertStringToTemplateForest(Bundler.bundle(files, false), ShellConfig.start().withEnvironment(args.environment).withFeedback((element, warning) -> System.err.println(warning)).end());
    Files.writeString(new File(args.output).toPath(), updated.shell.makeShell(updated));
    output.out();
  }

  @Override
  public void rxhtml(Arguments.FrontendRxhtmlArgs args, Output.YesOrError output) throws Exception {
    ArrayList<File> files = new ArrayList<>();
    aggregateFiles(new File(args.input), files);
    Files.writeString(new File(args.output).toPath(), RxHtmlTool.convertStringToTemplateForest(Bundler.bundle(files, false), ShellConfig.start().withEnvironment(args.environment).withFeedback((element, warning) -> System.err.println(warning)).end()).javascript);
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
  public void studyCss(Arguments.FrontendStudyCssArgs args, Output.YesOrError output) throws Exception {
    final CascadingStyleSheet css = CSSReader.readFromFile(new File(args.input), StandardCharsets.UTF_8, ECSSVersion.CSS30);
    StringBuilder constant = new StringBuilder();
    ObjectNode db = Json.newJsonObject();
    String study = StudyEngine.study(css, constant, db);
    Files.writeString(new File("css.style.txt").toPath(), study);
    Files.writeString(new File("css.style.constants.txt").toPath(), constant.toString());
    Files.writeString(new File("css.style.db.json").toPath(), db.toPrettyString());
    output.out();
  }

  @Override
  public void wrapCss(Arguments.FrontendWrapCssArgs args, Output.YesOrError output) throws Exception {
    String css = Files.readString(new File(args.input).toPath());
    Files.writeString(new File(args.output).toPath(), "<forest><style>\n" + css + "\n</style></forest>");
    output.out();
  }
}

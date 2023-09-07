/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.implementations.space;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.remote.Connection;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.validators.ValidateSpace;

import java.io.File;
import java.nio.file.Files;

public class Kickstarter {

  public final Config config;
  public final Connection connection;
  public final String identity;
  private String template;
  private String space;

  public Kickstarter(Config config, Connection connection) {
    this.config = config;
    this.connection = connection;
    this.identity = config.get_string("identity", null);
    this.space = null;
    this.template = null;
  }

  public void flow() throws Exception {
    intro();
    askTemplate();
    askSpaceName();
    if (space == null) {
      System.out.print(Util.prefix("Failed to get space name resolved", Util.ANSI.Red));
      return;
    }
    File spaceDir = new File(space);
    if (spaceDir.exists()) {
      System.err.println(Util.prefix("The directory '" + space + "' already exists", Util.ANSI.Yellow));
    }
    spaceDir.mkdirs();
    File backendDir = new File(spaceDir, "backend");
    File frontendDir = new File(spaceDir, "frontend");
    File assetDir = new File(spaceDir, "assets");
    backendDir.mkdirs();
    frontendDir.mkdirs();
    downloadAdama(new File(spaceDir, "backend.adama"));
    downloadRxHTML(new File(frontendDir, "initial.rx.html"));
    Files.writeString(new File(backendDir, ".gitkeep").toPath(), "");
    Files.writeString(new File(assetDir, ".gitkeep").toPath(), "");
    String gitignore = "logs\nnode_modules\nplan.json\nfrontend.rx.html\ncaravan\ncloud\ncss.freq.json\nsummary.html\n";
    Files.writeString(new File(spaceDir, ".gitignore").toPath(), gitignore);
    String verse = "{\n  \"documents\":[\n" //
       + "    {\"space\": \"" + space + "\", \"key\":\"demo\", \"domain\":true}\n" //
       + "  ],\n" //
       + "  \"spaces\": [{\"name\": \""+space+"\", \"main\": \"backend.adama\", \"import\": \"backend\" }]\n" //
       + "}"; //
    Files.writeString(new File(spaceDir, "local.verse.json").toPath(), Json.parseJsonObject(verse).toPrettyString());
    StringBuilder readme = new StringBuilder();
    readme.append("README\n");
    readme.append("--------\n");
    readme.append("# Local Development \n");
    readme.append("This command will (assuming you have adama.jar in your home directory). You can start the adama devbox where you just need to go to http://localhost:8080 to test the application. Running this in this path will start the devbox.\n\n");
    readme.append("```sh\n");
    readme.append("java -jar ~/adama.jar frontend dev-server\n");
    readme.append("```\n");
    readme.append("# Production Backend \n");
    readme.append("Now, Deploying backend changes requires first bundling up the backend.adama and backend/*.adama via\n");
    readme.append("```sh\n");
    readme.append("java -jar ~/adama.jar code bundle-plan --main backend.adama -o plan.json --imports backend\n");
    readme.append("```\n");
    readme.append("Now, deploy the plan\n");
    readme.append("```sh\n");
    readme.append("java -jar ~/adama.jar space deploy --space " + space + " --plan plan.json\n");
    readme.append("```\n");
    readme.append("You may keep or delete the created plan.json file.\n\n");
    readme.append("# Production Frontend \n");
    readme.append("Deploying frontend changes requires bundling all the *.rx.html files from the frontend directory via:\n");
    readme.append("```sh\n");
    readme.append("java -jar ~/adama.jar frontend bundle\n");
    readme.append("```\n");
    readme.append("Now, deploy the frontend via:\n");
    readme.append("```sh\n");
    readme.append("java -jar ~/adama.jar spaces set-rxhtml --space " + space + " --file frontend.rx.html\n");
    readme.append("```\n");
    readme.append("You may keep or delete the created frontend.rx.html file. ");
    readme.append("Changes are available via https://" + space + ".adama.games (usually after a minute for various caches to expire)\n\n");
    readme.append("\n");
    readme.append("If you have any static resources in assets, then you can upload them to the space via\n");
    readme.append("```sh\n");
    readme.append("java -jar ~/adama.jar space upload --space " + space + " --directory assets\n");
    readme.append("```\n");
    readme.append("\n");

    Files.writeString(new File(spaceDir, "README.md").toPath(), readme.toString());
    System.out.println("Directory '" + Util.prefix(space, Util.ANSI.Cyan) + "' was created which contains a useful README.md\n");
    System.out.println("Your app is available online at https://" + space + ".adama.games/ \n");
    System.out.println(Util.prefix("Thank you for using Adama", Util.ANSI.Green));
    System.out.println();
  }

  public void downloadAdama(File backendAdama) {
    try {
      ObjectNode request = Json.newJsonObject();
      request.put("method", "space/get");
      request.put("identity", identity);
      request.put("space", space);
      ObjectNode response = connection.execute(request);
      Files.writeString(backendAdama.toPath(), response.get("plan").get("versions").get(response.get("plan").get("default").textValue()).get("main").textValue());
    } catch (Exception ex) {
      if (ex instanceof ErrorCodeException) {
        System.err.println(Util.prefix("Failed downloading Adama Specification files (.adama):" + ((ErrorCodeException) ex).code, Util.ANSI.Red));
      } else {
        System.err.println("Exception: " + ex.getMessage());
      }
    }
  }

  public void downloadRxHTML(File rxhtmlAdama) {
    try {
      ObjectNode request = Json.newJsonObject();
      request.put("method", "space/get-rxhtml");
      request.put("identity", identity);
      request.put("space", space);
      ObjectNode response = connection.execute(request);
      Files.writeString(rxhtmlAdama.toPath(), response.get("rxhtml").textValue());
    } catch (Exception ex) {
      if (ex instanceof ErrorCodeException) {
        System.err.println(Util.prefix("Failed downloading RxHTML files (*.rx.html):" + ((ErrorCodeException) ex).code, Util.ANSI.Red));
      } else {
        System.err.println("Exception: " + ex.getMessage());
      }
    }
  }

  public void intro() {
    System.out.println("Greetings fellow human! Welcome to the " + Util.prefix("Adama Project Kickstart Tool", Util.ANSI.Green) + "!");
    System.out.println();
    System.out.println("This tool will help create a project for you to experience the wonder");
    System.out.println("of the Adama Platform... First, let's begin by picking a template to");
    System.out.println("seed the project");
    System.out.println();

  }

  public void askTemplate() {
    // TODO: list templates from production
    System.out.print(Util.prefix("Template: ", Util.ANSI.Cyan));
    this.template = System.console().readLine();
    // TODO: validate
  }


  public void askSpaceName() {
    System.out.println();
    System.out.println("Second, let's name your space (a valid space name is lowercase");
    System.out.println("alphanumeric between 3 and 127 characters with hyphens");
    System.out.println("but no double hyphens.");
    System.out.println();

    while (true) {
      System.out.print(Util.prefix("Space name: ", Util.ANSI.Cyan));
      this.space = System.console().readLine();
      try {
        ValidateSpace.validate(space);
      } catch (ErrorCodeException ex) {
        System.out.println(Util.prefix("Invalid space name (reason=" + ex.code + ")!", Util.ANSI.Red) + " Try again please.");
        System.out.println();
        continue;
      }
      System.out.println();
      System.out.println("OK, the space name looks good! Let's see if it is globally unique by talking to the platform...");
      ObjectNode request = Json.newJsonObject();
      request.put("method", "space/create");
      request.put("identity", identity);
      request.put("space", space);
      if (template == null || "".equals(template)) {
        template = "none";
      }
      request.put("template", template);
      try {
        connection.execute(request);
        System.out.println();
        System.out.println(Util.prefix("Space created!", Util.ANSI.Green));
        System.out.println();
        return;
      } catch (Exception ex) {
        if (ex instanceof ErrorCodeException) {
          if (((ErrorCodeException) ex).code == 130092 || ((ErrorCodeException) ex).code == 667658) {
            System.err.println(Util.prefix("This space already exists!", Util.ANSI.Red));
            System.err.println(Util.prefix("Do you want to use it anyway and setup your local project anyway? [yes/no]", Util.ANSI.Yellow));
            String yes = System.console().readLine().trim().toLowerCase();
            if (yes.length() > 0 && yes.charAt(0) == 'y') {
              return;
            }
          }
          // IF exists, then ask "Since this project already exists, do you want to just construct your environment"
          System.err.println(Util.prefix("Failed:" + ((ErrorCodeException) ex).code + "!!", Util.ANSI.Red));
        } else {
          System.err.println("Exception: " + ex.getMessage());
          return;
        }
      }
    }
  }
}

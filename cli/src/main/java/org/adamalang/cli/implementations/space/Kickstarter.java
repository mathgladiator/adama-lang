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
    backendDir.mkdirs();
    frontendDir.mkdirs();
    downloadAdama(new File(spaceDir, "backend.adama"));
    downloadRxHTML(new File(frontendDir, "initial.rx.html"));
    Files.writeString(new File(backendDir, ".gitkeep").toPath(), "");
    String gitignore = "logs\n" + "node_modules\n" + "plan.json\n" + "caravan\n" + "cloud\n" + "css.freq.json\n" + "summary.html";
    Files.writeString(new File(spaceDir, ".gitkeep").toPath(), gitignore);
    String verse = "{\n  \"documents\":[\n" //
       + "    {\"space\": \"" + space + "\", \"key\":\"demo\", \"domain\":true}\n" //
       + "  ],\n" //
       + "  \"spaces\": [{\"name\": \""+space+"\", \"main\": \"backend.adama\", \"import\": \"backend\" }]\n" //
       + "}"; //
    Files.writeString(new File(spaceDir, "local.verse.json").toPath(), verse);
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
      System.out.println("OK, the space name looks good! Let's see if it is globally unique by talking to the platform. Beep Blop...");
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
        System.out.println(Util.prefix("Space created!", Util.ANSI.Green));
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

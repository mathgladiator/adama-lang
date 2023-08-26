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

public class Kickstarter {

  public final Config config;
  public final Connection connection;
  public final String identity;
  private final String template;
  private String space;

  public Kickstarter(Config config, Connection connection) {
    this.config = config;
    this.connection = connection;
    this.identity = config.get_string("identity", null);
    this.space = null;
    this.template = null;
  }

  public void flow() {
    intro();
    Kickstarter.askTemplate();
    askSpaceName();
    if (space == null) {
      System.out.print(Util.prefix("Failed to get space name resolved", Util.ANSI.Red));
    }
    // TODO: download plan, shred to directory
    // TODO: download rxhtml, shred into pages?
    // TODO: setup the verse
    // TODO: setup the .gitignore
  }

  public void intro() {
    System.out.println("Greetings fellow human! Welcome to the " + Util.prefix("Adama Project Kickstart Tool", Util.ANSI.Green) + "!");
    System.out.println();
    System.out.println("This tool will help create a project for you to experience the wonder");
    System.out.println("of the Adama Platform... First, let's begin by picking a template to");
    System.out.println("seed the project");
    System.out.println();

  }

  public static void askTemplate() {
    // TODO: list templates from production
    System.out.print("Template: ");
    String template = System.console().readLine();
    // TODO: validate
  }

  public void askSpaceName() {
    System.out.println("Second, let's name your space (a valid space name is lowercase");
    System.out.println("alphanumeric between 3 and 127 characters with hyphens");
    System.out.println("but no double hyphens.");
    System.out.println();

    while (true) {
      System.out.print("Space name:");
      space = System.console().readLine();
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
      request.put("template", template);
      try {
        connection.execute(request);
        System.out.println(Util.prefix("Space created!", Util.ANSI.Green));
        return;
      } catch (Exception ex) {
        if (ex instanceof ErrorCodeException) {
          // IF exists, then ask "Since this project already exists, do you want to just construct your environment"
          System.out.print(Util.prefix("Failed:" + ((ErrorCodeException) ex).code, Util.ANSI.Red));
        } else {
          System.err.println("Exception: " + ex.getMessage());
          return;
        }
      }
    }
  }
}

package org.adamalang.cli.implementations.space;

import org.adamalang.cli.Util;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.validators.ValidateSpace;

public class Kickstarter {
  public static void intro() {
    System.out.println("Greetings fellow human! Welcome to the " + Util.prefix("Adama Project Kickstart Tool", Util.ANSI.Green) + "!");
    System.out.println();
    System.out.println("This tool will help create a project for you to experience the wonder");
    System.out.println("of the Adama Platform... Alright, so let's begin by naming your");
    System.out.println(Util.prefix("Adama Specification", Util.ANSI.Cyan) + " by naming your space (a valid space name is");
    System.out.println("lowercase alphanumeric between 3 and 127 characters with hyphens");
    System.out.println("but no double hyphens.");
    System.out.println();
  }

  public static String name() {
    boolean askSpace = true;
    String name = null;
    while (askSpace) {
      askSpace = false;
      System.out.println("Space name:");
      name = System.console().readLine();
      try {
        ValidateSpace.validate(name);
      } catch (ErrorCodeException ex) {
        System.out.println(Util.prefix("Invalid space name (reason=" + ex.code + ")!", Util.ANSI.Red) + " Try again please.");
        askSpace = true;
      }
    }
    System.out.println("OK, the space name looks good! Let's see if it is globally unique by talking to the platform. Beep Blop...");
    // TODO: create the space
    return name;
  }

  public static String template() {
    return null;
  }
}

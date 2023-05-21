package org.adamalang.cli.router;

public class ArgumentType {
  public static class CreateSpaceArgs {
    public String space;
    public CreateSpaceArgs(Argument arg) {
      this.space =  arg.arguments.get("--space").value;
    }
  }

}
package org.adamalang.cli.router;

public class ArgumentType {
  public static class CreateSpaceArgs {
    public String space;
    public CreateSpaceArgs(Argument arg) {
      this.space = arg.arguments.get("--space").value;
    }
  }

  public static class DeleteSpaceArgs {
    public String space;
    public DeleteSpaceArgs(Argument arg) {
      this.space = arg.arguments.get("--space").value;
    }
  }

  public static class DeploySpaceArgs {
    public String space;
    public String plan;
    public String file;
    public DeploySpaceArgs(Argument arg) {
      this.space = arg.arguments.get("--space").value;
      this.plan = arg.arguments.get("--plan").value;
      this.file = arg.arguments.get("--file").value;
    }
  }

  public static class SetRxhtmlSpaceArgs {
    public String space;
    public String file;
    public SetRxhtmlSpaceArgs(Argument arg) {
      this.space = arg.arguments.get("--space").value;
      this.file = arg.arguments.get("--file").value;
    }
  }

  public static class UploadSpaceArgs {
    public String space;
    public String gc;
    public UploadSpaceArgs(Argument arg) {
      this.space = arg.arguments.get("--space").value;
      this.gc = arg.arguments.get("--gc").value;
    }
  }

  public static class DownloadSpaceArgs {
    public String space;
    public DownloadSpaceArgs(Argument arg) {
      this.space = arg.arguments.get("--space").value;
    }
  }

  public static class ListSpaceArgs {
    public String marker;
    public String limit;
    public ListSpaceArgs(Argument arg) {
      this.marker = arg.arguments.get("--marker").value;
      this.limit = arg.arguments.get("--limit").value;
    }
  }

  public static class UsageSpaceArgs {
    public String space;
    public String limit;
    public UsageSpaceArgs(Argument arg) {
      this.space = arg.arguments.get("--space").value;
      this.limit = arg.arguments.get("--limit").value;
    }
  }

  public static class ReflectSpaceArgs {
    public String space;
    public String marker;
    public String output;
    public String limit;
    public ReflectSpaceArgs(Argument arg) {
      this.space = arg.arguments.get("--space").value;
      this.marker = arg.arguments.get("--marker").value;
      this.output = arg.arguments.get("--output").value;
      this.limit = arg.arguments.get("--limit").value;
    }
  }

  public static class SetRoleSpaceArgs {
    public String space;
    public String marker;
    public String email;
    public String role;
    public SetRoleSpaceArgs(Argument arg) {
      this.space = arg.arguments.get("--space").value;
      this.marker = arg.arguments.get("--marker").value;
      this.email = arg.arguments.get("--email").value;
      this.role = arg.arguments.get("--role").value;
    }
  }

  public static class GenerateKeySpaceArgs {
    public String space;
    public GenerateKeySpaceArgs(Argument arg) {
      this.space = arg.arguments.get("--space").value;
    }
  }

  public static class EncryptSecretSpaceArgs {
    public String space;
    public EncryptSecretSpaceArgs(Argument arg) {
      this.space = arg.arguments.get("--space").value;
    }
  }

  public static class CreateAuthorityArgs {
    public CreateAuthorityArgs(Argument arg) {
    }
  }

  public static class SetAuthorityArgs {
    public String authority;
    public String keystore;
    public SetAuthorityArgs(Argument arg) {
      this.authority = arg.arguments.get("--authority").value;
      this.keystore = arg.arguments.get("--keystore").value;
    }
  }

  public static class GetAuthorityArgs {
    public String authority;
    public String keystore;
    public GetAuthorityArgs(Argument arg) {
      this.authority = arg.arguments.get("--authority").value;
      this.keystore = arg.arguments.get("--keystore").value;
    }
  }

  public static class DestroyAuthorityArgs {
    public String authority;
    public DestroyAuthorityArgs(Argument arg) {
      this.authority = arg.arguments.get("--authority").value;
    }
  }

  public static class ListAuthorityArgs {
    public ListAuthorityArgs(Argument arg) {
    }
  }

  public static class CreateLocalAuthorityArgs {
    public String authority;
    public String keystore;
    public String priv;
    public CreateLocalAuthorityArgs(Argument arg) {
      this.authority = arg.arguments.get("--authority").value;
      this.keystore = arg.arguments.get("--keystore").value;
      this.priv = arg.arguments.get("--priv").value;
    }
  }

  public static class AppendLocalAuthorityArgs {
    public String authority;
    public String keystore;
    public String priv;
    public AppendLocalAuthorityArgs(Argument arg) {
      this.authority = arg.arguments.get("--authority").value;
      this.keystore = arg.arguments.get("--keystore").value;
      this.priv = arg.arguments.get("--priv").value;
    }
  }

  public static class SignAuthorityArgs {
    public String key;
    public String agent;
    public String validate;
    public SignAuthorityArgs(Argument arg) {
      this.key = arg.arguments.get("--key").value;
      this.agent = arg.arguments.get("--agent").value;
      this.validate = arg.arguments.get("--validate").value;
    }
  }

}
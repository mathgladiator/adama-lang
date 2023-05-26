package org.adamalang.cli.router;

import org.adamalang.cli.runtime.Argument;
import org.adamalang.cli.Config;

public class ArgumentType {
  public static class CreateSpaceArgs {
    public Config config;
    public String space;
    public CreateSpaceArgs(Argument arg) {
    config = arg.config;
      this.space = arg.arguments.get("--space").value;
    }
  }

  public static class DeleteSpaceArgs {
    public Config config;
    public String space;
    public DeleteSpaceArgs(Argument arg) {
    config = arg.config;
      this.space = arg.arguments.get("--space").value;
    }
  }

  public static class DeploySpaceArgs {
    public Config config;
    public String space;
    public String plan;
    public String file;
    public DeploySpaceArgs(Argument arg) {
    config = arg.config;
      this.space = arg.arguments.get("--space").value;
      this.plan = arg.arguments.get("--plan").value;
      this.file = arg.arguments.get("--file").value;
    }
  }

  public static class SetRxhtmlSpaceArgs {
    public Config config;
    public String space;
    public String file;
    public SetRxhtmlSpaceArgs(Argument arg) {
    config = arg.config;
      this.space = arg.arguments.get("--space").value;
      this.file = arg.arguments.get("--file").value;
    }
  }

  public static class GetRxhtmlSpaceArgs {
    public Config config;
    public String space;
    public GetRxhtmlSpaceArgs(Argument arg) {
    config = arg.config;
      this.space = arg.arguments.get("--space").value;
    }
  }

  public static class UploadSpaceArgs {
    public Config config;
    public String space;
    public String gc;
    public String root;
    public String file;
    public UploadSpaceArgs(Argument arg) {
    config = arg.config;
      this.space = arg.arguments.get("--space").value;
      this.gc = arg.arguments.get("--gc").value;
      this.root = arg.arguments.get("--root").value;
      this.file = arg.arguments.get("--file").value;
    }
  }

  public static class DownloadSpaceArgs {
    public Config config;
    public String space;
    public DownloadSpaceArgs(Argument arg) {
    config = arg.config;
      this.space = arg.arguments.get("--space").value;
    }
  }

  public static class ListSpaceArgs {
    public Config config;
    public String marker;
    public String limit;
    public ListSpaceArgs(Argument arg) {
    config = arg.config;
      this.marker = arg.arguments.get("--marker").value;
      this.limit = arg.arguments.get("--limit").value;
    }
  }

  public static class UsageSpaceArgs {
    public Config config;
    public String space;
    public String limit;
    public UsageSpaceArgs(Argument arg) {
    config = arg.config;
      this.space = arg.arguments.get("--space").value;
      this.limit = arg.arguments.get("--limit").value;
    }
  }

  public static class ReflectSpaceArgs {
    public Config config;
    public String space;
    public String marker;
    public String output;
    public String key;
    public String limit;
    public ReflectSpaceArgs(Argument arg) {
    config = arg.config;
      this.space = arg.arguments.get("--space").value;
      this.marker = arg.arguments.get("--marker").value;
      this.output = arg.arguments.get("--output").value;
      this.key = arg.arguments.get("--key").value;
      this.limit = arg.arguments.get("--limit").value;
    }
  }

  public static class SetRoleSpaceArgs {
    public Config config;
    public String space;
    public String marker;
    public String email;
    public String role;
    public SetRoleSpaceArgs(Argument arg) {
    config = arg.config;
      this.space = arg.arguments.get("--space").value;
      this.marker = arg.arguments.get("--marker").value;
      this.email = arg.arguments.get("--email").value;
      this.role = arg.arguments.get("--role").value;
    }
  }

  public static class GenerateKeySpaceArgs {
    public Config config;
    public String space;
    public GenerateKeySpaceArgs(Argument arg) {
    config = arg.config;
      this.space = arg.arguments.get("--space").value;
    }
  }

  public static class EncryptSecretSpaceArgs {
    public Config config;
    public String space;
    public EncryptSecretSpaceArgs(Argument arg) {
    config = arg.config;
      this.space = arg.arguments.get("--space").value;
    }
  }

  public static class CreateAuthorityArgs {
    public Config config;
    public CreateAuthorityArgs(Argument arg) {
    config = arg.config;
    }
  }

  public static class SetAuthorityArgs {
    public Config config;
    public String authority;
    public String keystore;
    public SetAuthorityArgs(Argument arg) {
    config = arg.config;
      this.authority = arg.arguments.get("--authority").value;
      this.keystore = arg.arguments.get("--keystore").value;
    }
  }

  public static class GetAuthorityArgs {
    public Config config;
    public String authority;
    public String keystore;
    public GetAuthorityArgs(Argument arg) {
    config = arg.config;
      this.authority = arg.arguments.get("--authority").value;
      this.keystore = arg.arguments.get("--keystore").value;
    }
  }

  public static class DestroyAuthorityArgs {
    public Config config;
    public String authority;
    public DestroyAuthorityArgs(Argument arg) {
    config = arg.config;
      this.authority = arg.arguments.get("--authority").value;
    }
  }

  public static class ListAuthorityArgs {
    public Config config;
    public ListAuthorityArgs(Argument arg) {
    config = arg.config;
    }
  }

  public static class CreateLocalAuthorityArgs {
    public Config config;
    public String authority;
    public String keystore;
    public String priv;
    public CreateLocalAuthorityArgs(Argument arg) {
    config = arg.config;
      this.authority = arg.arguments.get("--authority").value;
      this.keystore = arg.arguments.get("--keystore").value;
      this.priv = arg.arguments.get("--priv").value;
    }
  }

  public static class AppendLocalAuthorityArgs {
    public Config config;
    public String authority;
    public String keystore;
    public String priv;
    public AppendLocalAuthorityArgs(Argument arg) {
    config = arg.config;
      this.authority = arg.arguments.get("--authority").value;
      this.keystore = arg.arguments.get("--keystore").value;
      this.priv = arg.arguments.get("--priv").value;
    }
  }

  public static class SignAuthorityArgs {
    public Config config;
    public String key;
    public String agent;
    public String validate;
    public SignAuthorityArgs(Argument arg) {
    config = arg.config;
      this.key = arg.arguments.get("--key").value;
      this.agent = arg.arguments.get("--agent").value;
      this.validate = arg.arguments.get("--validate").value;
    }
  }

  public static class InitArgs {
    public Config config;
    public InitArgs(Argument arg) {
    config = arg.config;
    }
  }

}
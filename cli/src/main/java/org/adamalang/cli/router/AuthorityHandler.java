package org.adamalang.cli.router;

import org.adamalang.cli.router.ArgumentType.*;

public interface AuthorityHandler {
  default int route(Argument args) {
    if (args.command == null) {
      return Help.displayHelp("authority");
    }
    switch (args.command.name) {
      case "create":
        return createAuthority("Output");
      case "set":
        return setAuthority(new SetAuthorityArgs(args), "Output");
      case "get":
        return getAuthority(new GetAuthorityArgs(args), "Output");
      case "destroy":
        return destroyAuthority(new DestroyAuthorityArgs(args), "Output");
      case "list":
        return listAuthority("Output");
      case "create-local":
        return createLocalAuthority(new CreateLocalAuthorityArgs(args), "Output");
      case "append-local":
        return appendLocalAuthority(new AppendLocalAuthorityArgs(args), "Output");
      case "sign":
        return signAuthority(new SignAuthorityArgs(args), "Output");
      default:
        Help.displayHelp("authority");
        return 0;
    }
  }
  int createAuthority(String output);
  int setAuthority(SetAuthorityArgs args, String output);
  int getAuthority(GetAuthorityArgs args, String output);
  int destroyAuthority(DestroyAuthorityArgs args, String output);
  int listAuthority(String output);
  int createLocalAuthority(CreateLocalAuthorityArgs args, String output);
  int appendLocalAuthority(AppendLocalAuthorityArgs args, String output);
  int signAuthority(SignAuthorityArgs args, String output);
}
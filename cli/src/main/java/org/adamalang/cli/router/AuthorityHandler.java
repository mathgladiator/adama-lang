package org.adamalang.cli.router;

import org.adamalang.cli.router.Output.*;
import org.adamalang.cli.router.ArgumentType.*;

public interface AuthorityHandler {
  default int route(Argument args) {
    if (args.command == null) {
      return Help.displayHelp("authority");
    }
    switch (args.command.name) {
      case "create":
        return createAuthority(new AnsiOutput());
      case "set":
        return setAuthority(new SetAuthorityArgs(args), new AnsiOutput());
      case "get":
        return getAuthority(new GetAuthorityArgs(args), new AnsiOutput());
      case "destroy":
        return destroyAuthority(new DestroyAuthorityArgs(args), new AnsiOutput());
      case "list":
        return listAuthority(new AnsiOutput());
      case "create-local":
        return createLocalAuthority(new CreateLocalAuthorityArgs(args), new AnsiOutput());
      case "append-local":
        return appendLocalAuthority(new AppendLocalAuthorityArgs(args), new AnsiOutput());
      case "sign":
        return signAuthority(new SignAuthorityArgs(args), new AnsiOutput());
      default:
        Help.displayHelp("authority");
        return 0;
    }
  }
  int createAuthority(AnsiOutput output);
  int setAuthority(SetAuthorityArgs args, AnsiOutput output);
  int getAuthority(GetAuthorityArgs args, AnsiOutput output);
  int destroyAuthority(DestroyAuthorityArgs args, AnsiOutput output);
  int listAuthority(AnsiOutput output);
  int createLocalAuthority(CreateLocalAuthorityArgs args, AnsiOutput output);
  int appendLocalAuthority(AppendLocalAuthorityArgs args, AnsiOutput output);
  int signAuthority(SignAuthorityArgs args, AnsiOutput output);
}
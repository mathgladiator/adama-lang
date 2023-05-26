package org.adamalang.cli.router;

import org.adamalang.cli.runtime.Argument;
import org.adamalang.cli.runtime.Help;
import org.adamalang.cli.runtime.Output;
import org.adamalang.cli.router.ArgumentType.*;

public interface AuthorityHandler {
  default int route(Argument args) throws Exception {
    if (args.command == null) {
      return Help.displayHelp("authority");
    }
    switch (args.command.name) {
      case "create":
        return createAuthority(new CreateAuthorityArgs(args), new Output(args));
      case "set":
        return setAuthority(new SetAuthorityArgs(args), new Output(args));
      case "get":
        return getAuthority(new GetAuthorityArgs(args), new Output(args));
      case "destroy":
        return destroyAuthority(new DestroyAuthorityArgs(args), new Output(args));
      case "list":
        return listAuthority(new ListAuthorityArgs(args), new Output(args));
      case "create-local":
        return createLocalAuthority(new CreateLocalAuthorityArgs(args), new Output(args));
      case "append-local":
        return appendLocalAuthority(new AppendLocalAuthorityArgs(args), new Output(args));
      case "sign":
        return signAuthority(new SignAuthorityArgs(args), new Output(args));
      default:
        Help.displayHelp("authority");
        return 0;
    }
  }
  int createAuthority(CreateAuthorityArgs args, Output output) throws Exception;
  int setAuthority(SetAuthorityArgs args, Output output) throws Exception;
  int getAuthority(GetAuthorityArgs args, Output output) throws Exception;
  int destroyAuthority(DestroyAuthorityArgs args, Output output) throws Exception;
  int listAuthority(ListAuthorityArgs args, Output output) throws Exception;
  int createLocalAuthority(CreateLocalAuthorityArgs args, Output output) throws Exception;
  int appendLocalAuthority(AppendLocalAuthorityArgs args, Output output) throws Exception;
  int signAuthority(SignAuthorityArgs args, Output output) throws Exception;
}
package org.adamalang.cli.router;

import org.adamalang.cli.runtime.Argument;
import org.adamalang.cli.runtime.Help;
import org.adamalang.cli.runtime.Output;
import org.adamalang.cli.router.ArgumentType.*;

public interface SpaceHandler {
  default int route(Argument args) {
    if (args.command == null) {
      return Help.displayHelp("space");
    }
    switch (args.command.name) {
      case "create":
        return createSpace(new CreateSpaceArgs(args), new Output(args));
      case "delete":
        return deleteSpace(new DeleteSpaceArgs(args), new Output(args));
      case "deploy":
        return deploySpace(new DeploySpaceArgs(args), new Output(args));
      case "set-rxhtml":
        return setRxhtmlSpace(new SetRxhtmlSpaceArgs(args), new Output(args));
      case "upload":
        return uploadSpace(new UploadSpaceArgs(args), new Output(args));
      case "download":
        return downloadSpace(new DownloadSpaceArgs(args), new Output(args));
      case "list":
        return listSpace(new ListSpaceArgs(args), new Output(args));
      case "usage":
        return usageSpace(new UsageSpaceArgs(args), new Output(args));
      case "reflect":
        return reflectSpace(new ReflectSpaceArgs(args), new Output(args));
      case "set-role":
        return setRoleSpace(new SetRoleSpaceArgs(args), new Output(args));
      case "generate-key":
        return generateKeySpace(new GenerateKeySpaceArgs(args), new Output(args));
      case "encrypt-secret":
        return encryptSecretSpace(new EncryptSecretSpaceArgs(args), new Output(args));
      default:
        Help.displayHelp("space");
        return 0;
    }
  }
  int createSpace(CreateSpaceArgs args, Output output);
  int deleteSpace(DeleteSpaceArgs args, Output output);
  int deploySpace(DeploySpaceArgs args, Output output);
  int setRxhtmlSpace(SetRxhtmlSpaceArgs args, Output output);
  int uploadSpace(UploadSpaceArgs args, Output output);
  int downloadSpace(DownloadSpaceArgs args, Output output);
  int listSpace(ListSpaceArgs args, Output output);
  int usageSpace(UsageSpaceArgs args, Output output);
  int reflectSpace(ReflectSpaceArgs args, Output output);
  int setRoleSpace(SetRoleSpaceArgs args, Output output);
  int generateKeySpace(GenerateKeySpaceArgs args, Output output);
  int encryptSecretSpace(EncryptSecretSpaceArgs args, Output output);
}
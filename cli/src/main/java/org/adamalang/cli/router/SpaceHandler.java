package org.adamalang.cli.router;

import org.adamalang.cli.router.Output.*;
import org.adamalang.cli.router.ArgumentType.*;

public interface SpaceHandler {
  default int route(Argument args) {
    if (args.command == null) {
      return Help.displayHelp("space");
    }
    switch (args.command.name) {
      case "create":
        return createSpace(new CreateSpaceArgs(args), new AnsiOutput());
      case "delete":
        return deleteSpace(new DeleteSpaceArgs(args), new AnsiOutput());
      case "deploy":
        return deploySpace(new DeploySpaceArgs(args), new AnsiOutput());
      case "set-rxhtml":
        return setRxhtmlSpace(new SetRxhtmlSpaceArgs(args), new AnsiOutput());
      case "upload":
        return uploadSpace(new UploadSpaceArgs(args), new AnsiOutput());
      case "download":
        return downloadSpace(new DownloadSpaceArgs(args), new AnsiOutput());
      case "list":
        return listSpace(new ListSpaceArgs(args), new AnsiOutput());
      case "usage":
        return usageSpace(new UsageSpaceArgs(args), new AnsiOutput());
      case "reflect":
        return reflectSpace(new ReflectSpaceArgs(args), new AnsiOutput());
      case "set-role":
        return setRoleSpace(new SetRoleSpaceArgs(args), new AnsiOutput());
      case "generate-key":
        return generateKeySpace(new GenerateKeySpaceArgs(args), new AnsiOutput());
      case "encrypt-secret":
        return encryptSecretSpace(new EncryptSecretSpaceArgs(args), new AnsiOutput());
      default:
        Help.displayHelp("space");
        return 0;
    }
  }
  int createSpace(CreateSpaceArgs args, AnsiOutput output);
  int deleteSpace(DeleteSpaceArgs args, AnsiOutput output);
  int deploySpace(DeploySpaceArgs args, AnsiOutput output);
  int setRxhtmlSpace(SetRxhtmlSpaceArgs args, AnsiOutput output);
  int uploadSpace(UploadSpaceArgs args, AnsiOutput output);
  int downloadSpace(DownloadSpaceArgs args, AnsiOutput output);
  int listSpace(ListSpaceArgs args, AnsiOutput output);
  int usageSpace(UsageSpaceArgs args, AnsiOutput output);
  int reflectSpace(ReflectSpaceArgs args, AnsiOutput output);
  int setRoleSpace(SetRoleSpaceArgs args, AnsiOutput output);
  int generateKeySpace(GenerateKeySpaceArgs args, AnsiOutput output);
  int encryptSecretSpace(EncryptSecretSpaceArgs args, AnsiOutput output);
}
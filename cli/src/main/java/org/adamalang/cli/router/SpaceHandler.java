package org.adamalang.cli.router;

import org.adamalang.cli.router.ArgumentType.*;

public interface SpaceHandler {
  default int route(Argument args) {
    if (args.command == null) {
      return Help.displayHelp("space");
    }
    switch (args.command.name) {
      case "create":
        return createSpace(new CreateSpaceArgs(args), "Output");
      case "delete":
        return deleteSpace(new DeleteSpaceArgs(args), "Output");
      case "deploy":
        return deploySpace(new DeploySpaceArgs(args), "Output");
      case "set-rxhtml":
        return setRxhtmlSpace(new SetRxhtmlSpaceArgs(args), "Output");
      case "upload":
        return uploadSpace(new UploadSpaceArgs(args), "Output");
      case "download":
        return downloadSpace(new DownloadSpaceArgs(args), "Output");
      case "list":
        return listSpace(new ListSpaceArgs(args), "Output");
      case "usage":
        return usageSpace(new UsageSpaceArgs(args), "Output");
      case "reflect":
        return reflectSpace(new ReflectSpaceArgs(args), "Output");
      case "set-role":
        return setRoleSpace(new SetRoleSpaceArgs(args), "Output");
      case "generate-key":
        return generateKeySpace(new GenerateKeySpaceArgs(args), "Output");
      case "encrypt-secret":
        return encryptSecretSpace(new EncryptSecretSpaceArgs(args), "Output");
      default:
        Help.displayHelp("space");
        return 0;
    }
  }
  int createSpace(CreateSpaceArgs args, String output);
  int deleteSpace(DeleteSpaceArgs args, String output);
  int deploySpace(DeploySpaceArgs args, String output);
  int setRxhtmlSpace(SetRxhtmlSpaceArgs args, String output);
  int uploadSpace(UploadSpaceArgs args, String output);
  int downloadSpace(DownloadSpaceArgs args, String output);
  int listSpace(ListSpaceArgs args, String output);
  int usageSpace(UsageSpaceArgs args, String output);
  int reflectSpace(ReflectSpaceArgs args, String output);
  int setRoleSpace(SetRoleSpaceArgs args, String output);
  int generateKeySpace(GenerateKeySpaceArgs args, String output);
  int encryptSecretSpace(EncryptSecretSpaceArgs args, String output);
}
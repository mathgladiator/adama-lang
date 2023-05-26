package org.adamalang.cli.router;

import org.adamalang.cli.runtime.Argument;
import org.adamalang.cli.runtime.Help;
import org.adamalang.cli.runtime.Output;
import org.adamalang.cli.router.ArgumentType.*;

public interface SpaceHandler {
  default int route(Argument args) throws Exception {
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
      case "get-rxhtml":
        return getRxhtmlSpace(new GetRxhtmlSpaceArgs(args), new Output(args));
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
  int createSpace(CreateSpaceArgs args, Output output) throws Exception;
  int deleteSpace(DeleteSpaceArgs args, Output output) throws Exception;
  int deploySpace(DeploySpaceArgs args, Output output) throws Exception;
  int setRxhtmlSpace(SetRxhtmlSpaceArgs args, Output output) throws Exception;
  int getRxhtmlSpace(GetRxhtmlSpaceArgs args, Output output) throws Exception;
  int uploadSpace(UploadSpaceArgs args, Output output) throws Exception;
  int downloadSpace(DownloadSpaceArgs args, Output output) throws Exception;
  int listSpace(ListSpaceArgs args, Output output) throws Exception;
  int usageSpace(UsageSpaceArgs args, Output output) throws Exception;
  int reflectSpace(ReflectSpaceArgs args, Output output) throws Exception;
  int setRoleSpace(SetRoleSpaceArgs args, Output output) throws Exception;
  int generateKeySpace(GenerateKeySpaceArgs args, Output output) throws Exception;
  int encryptSecretSpace(EncryptSecretSpaceArgs args, Output output) throws Exception;
}
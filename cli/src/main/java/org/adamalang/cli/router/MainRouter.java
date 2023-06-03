package org.adamalang.cli.router;

import org.adamalang.ErrorTable;
import org.adamalang.cli.Util;
import org.adamalang.cli.runtime.Output;
import org.adamalang.cli.runtime.Output.*;
import org.adamalang.common.ErrorCodeException;
import static org.adamalang.cli.router.Help.*;
import static org.adamalang.cli.router.Arguments.*;

public class MainRouter {
  public static int route(String[] args, RootHandler handler, Output output) {
    try {
      if (args.length == 0) {
        displayRootHelp();
        return 1;
      }
      switch (args[0]) {
        case "space":
          SpaceHandler spaceHandler = handler.makeSpaceHandler();
          if (args.length == 1) {
            displaySpaceHelp();
            return 1;
          }
          switch (args[1]) {
            case "create": {
              SpaceCreateArgs spaceArgs = SpaceCreateArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceCreateArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.create(spaceArgs, out);
               return 0;
            }
            case "delete": {
              SpaceDeleteArgs spaceArgs = SpaceDeleteArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceDeleteArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.delete(spaceArgs, out);
               return 0;
            }
            case "deploy": {
              SpaceDeployArgs spaceArgs = SpaceDeployArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceDeployArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.deploy(spaceArgs, out);
               return 0;
            }
            case "set-rxhtml": {
              SpaceSetRxhtmlArgs spaceArgs = SpaceSetRxhtmlArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceSetRxhtmlArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.setRxhtml(spaceArgs, out);
               return 0;
            }
            case "get-rxhtml": {
              SpaceGetRxhtmlArgs spaceArgs = SpaceGetRxhtmlArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceGetRxhtmlArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.getRxhtml(spaceArgs, out);
               return 0;
            }
            case "upload": {
              SpaceUploadArgs spaceArgs = SpaceUploadArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceUploadArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.upload(spaceArgs, out);
               return 0;
            }
            case "download": {
              SpaceDownloadArgs spaceArgs = SpaceDownloadArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceDownloadArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.download(spaceArgs, out);
               return 0;
            }
            case "list": {
              SpaceListArgs spaceArgs = SpaceListArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceListArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.list(spaceArgs, out);
               return 0;
            }
            case "usage": {
              SpaceUsageArgs spaceArgs = SpaceUsageArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceUsageArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.usage(spaceArgs, out);
               return 0;
            }
            case "reflect": {
              SpaceReflectArgs spaceArgs = SpaceReflectArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceReflectArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.reflect(spaceArgs, out);
               return 0;
            }
            case "set-role": {
              SpaceSetRoleArgs spaceArgs = SpaceSetRoleArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceSetRoleArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.setRole(spaceArgs, out);
               return 0;
            }
            case "generate-key": {
              SpaceGenerateKeyArgs spaceArgs = SpaceGenerateKeyArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceGenerateKeyArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.generateKey(spaceArgs, out);
               return 0;
            }
            case "encrypt-secret": {
              SpaceEncryptSecretArgs spaceArgs = SpaceEncryptSecretArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceEncryptSecretArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.encryptSecret(spaceArgs, out);
               return 0;
            }
            default:
              System.err.println("Invalid subcommand '" + args[1] + "' of command 'space'");
              System.err.println("See 'adama space help' for a list of subcommands.");
              return 1;
          }
        case "authority":
          AuthorityHandler authorityHandler = handler.makeAuthorityHandler();
          if (args.length == 1) {
            displayAuthorityHelp();
            return 1;
          }
          switch (args[1]) {
            case "create": {
              AuthorityCreateArgs authorityArgs = AuthorityCreateArgs.from(args, 2);
              if (authorityArgs == null) {
                AuthorityCreateArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               authorityHandler.create(authorityArgs, out);
               return 0;
            }
            case "set": {
              AuthoritySetArgs authorityArgs = AuthoritySetArgs.from(args, 2);
              if (authorityArgs == null) {
                AuthoritySetArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               authorityHandler.set(authorityArgs, out);
               return 0;
            }
            case "get": {
              AuthorityGetArgs authorityArgs = AuthorityGetArgs.from(args, 2);
              if (authorityArgs == null) {
                AuthorityGetArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               authorityHandler.get(authorityArgs, out);
               return 0;
            }
            case "destroy": {
              AuthorityDestroyArgs authorityArgs = AuthorityDestroyArgs.from(args, 2);
              if (authorityArgs == null) {
                AuthorityDestroyArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               authorityHandler.destroy(authorityArgs, out);
               return 0;
            }
            case "list": {
              AuthorityListArgs authorityArgs = AuthorityListArgs.from(args, 2);
              if (authorityArgs == null) {
                AuthorityListArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               authorityHandler.list(authorityArgs, out);
               return 0;
            }
            case "create-local": {
              AuthorityCreateLocalArgs authorityArgs = AuthorityCreateLocalArgs.from(args, 2);
              if (authorityArgs == null) {
                AuthorityCreateLocalArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               authorityHandler.createLocal(authorityArgs, out);
               return 0;
            }
            case "append-local": {
              AuthorityAppendLocalArgs authorityArgs = AuthorityAppendLocalArgs.from(args, 2);
              if (authorityArgs == null) {
                AuthorityAppendLocalArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               authorityHandler.appendLocal(authorityArgs, out);
               return 0;
            }
            case "sign": {
              AuthoritySignArgs authorityArgs = AuthoritySignArgs.from(args, 2);
              if (authorityArgs == null) {
                AuthoritySignArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               authorityHandler.sign(authorityArgs, out);
               return 0;
            }
            default:
              System.err.println("Invalid subcommand '" + args[1] + "' of command 'authority'");
              System.err.println("See 'adama authority help' for a list of subcommands.");
              return 1;
          }
          case "init": {
            InitArgs mainArgs = InitArgs.from(args, 1);
            if (mainArgs == null) {
              InitArgs.help();
              return 1;
             }
             YesOrError out = output.makeYesOrError();
             handler.init(mainArgs , out);
             return 0;
          }
          default:
            System.err.println("Invalid command '" + args[0] + "'");
            System.err.println("See 'adama help' for a list of commands.");
            return 1;
      }
    } catch (Exception ex) {
      if (ex instanceof ErrorCodeException) {
        System.err.println(Util.prefix("[ERROR]", Util.ANSI.Red));
        System.err.println("#:" + ((ErrorCodeException) ex).code);
        System.err.println("Name:" + ErrorTable.INSTANCE.names.get(((ErrorCodeException) ex).code));
        System.err.println("Description:" + ErrorTable.INSTANCE.descriptions.get(((ErrorCodeException) ex).code));
      } else {
        ex.printStackTrace();
      }
      return 1;
    }
  }
}
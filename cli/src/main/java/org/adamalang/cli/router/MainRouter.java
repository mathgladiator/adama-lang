/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.router;

import org.adamalang.ErrorTable;
import org.adamalang.cli.Config;
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
        case "help":
          Help.displayRootHelp();
          return 1;
        case "spaces":
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
            case "developers": {
              SpaceDevelopersArgs spaceArgs = SpaceDevelopersArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceDevelopersArgs.help();
                return 1;
               }
               JsonOrError out = output.makeJsonOrError();
               spaceHandler.developers(spaceArgs, out);
               return 0;
            }
            case "encrypt-priv": {
              SpaceEncryptPrivArgs spaceArgs = SpaceEncryptPrivArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceEncryptPrivArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.encryptPriv(spaceArgs, out);
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
            case "get": {
              SpaceGetArgs spaceArgs = SpaceGetArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceGetArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               spaceHandler.get(spaceArgs, out);
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
            case "list": {
              SpaceListArgs spaceArgs = SpaceListArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceListArgs.help();
                return 1;
               }
               JsonOrError out = output.makeJsonOrError();
               spaceHandler.list(spaceArgs, out);
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
            case "upload": {
              SpaceUploadArgs spaceArgs = SpaceUploadArgs.from(args, 2);
              if (spaceArgs == null) {
                SpaceUploadArgs.help();
                return 1;
               }
               JsonOrError out = output.makeJsonOrError();
               spaceHandler.upload(spaceArgs, out);
               return 0;
            }
            case "--help":
            case "-h":
            case "help": {
              displaySpaceHelp();
              return 1;
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
            case "create": {
              AuthorityCreateArgs authorityArgs = AuthorityCreateArgs.from(args, 2);
              if (authorityArgs == null) {
                AuthorityCreateArgs.help();
                return 1;
               }
               JsonOrError out = output.makeJsonOrError();
               authorityHandler.create(authorityArgs, out);
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
            case "list": {
              AuthorityListArgs authorityArgs = AuthorityListArgs.from(args, 2);
              if (authorityArgs == null) {
                AuthorityListArgs.help();
                return 1;
               }
               JsonOrError out = output.makeJsonOrError();
               authorityHandler.list(authorityArgs, out);
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
            case "sign": {
              AuthoritySignArgs authorityArgs = AuthoritySignArgs.from(args, 2);
              if (authorityArgs == null) {
                AuthoritySignArgs.help();
                return 1;
               }
               JsonOrError out = output.makeJsonOrError();
               authorityHandler.sign(authorityArgs, out);
               return 0;
            }
            case "--help":
            case "-h":
            case "help": {
              displayAuthorityHelp();
              return 1;
            }
            default:
              System.err.println("Invalid subcommand '" + args[1] + "' of command 'authority'");
              System.err.println("See 'adama authority help' for a list of subcommands.");
              return 1;
          }
        case "account":
          AccountHandler accountHandler = handler.makeAccountHandler();
          if (args.length == 1) {
            displayAccountHelp();
            return 1;
          }
          switch (args[1]) {
            case "set-password": {
              AccountSetPasswordArgs accountArgs = AccountSetPasswordArgs.from(args, 2);
              if (accountArgs == null) {
                AccountSetPasswordArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               accountHandler.setPassword(accountArgs, out);
               return 0;
            }
            case "--help":
            case "-h":
            case "help": {
              displayAccountHelp();
              return 1;
            }
            default:
              System.err.println("Invalid subcommand '" + args[1] + "' of command 'account'");
              System.err.println("See 'adama account help' for a list of subcommands.");
              return 1;
          }
        case "code":
          CodeHandler codeHandler = handler.makeCodeHandler();
          if (args.length == 1) {
            displayCodeHelp();
            return 1;
          }
          switch (args[1]) {
            case "bundle-plan": {
              CodeBundlePlanArgs codeArgs = CodeBundlePlanArgs.from(args, 2);
              if (codeArgs == null) {
                CodeBundlePlanArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               codeHandler.bundlePlan(codeArgs, out);
               return 0;
            }
            case "compile-file": {
              CodeCompileFileArgs codeArgs = CodeCompileFileArgs.from(args, 2);
              if (codeArgs == null) {
                CodeCompileFileArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               codeHandler.compileFile(codeArgs, out);
               return 0;
            }
            case "diagram": {
              CodeDiagramArgs codeArgs = CodeDiagramArgs.from(args, 2);
              if (codeArgs == null) {
                CodeDiagramArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               codeHandler.diagram(codeArgs, out);
               return 0;
            }
            case "lsp": {
              CodeLspArgs codeArgs = CodeLspArgs.from(args, 2);
              if (codeArgs == null) {
                CodeLspArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               codeHandler.lsp(codeArgs, out);
               return 0;
            }
            case "reflect-dump": {
              CodeReflectDumpArgs codeArgs = CodeReflectDumpArgs.from(args, 2);
              if (codeArgs == null) {
                CodeReflectDumpArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               codeHandler.reflectDump(codeArgs, out);
               return 0;
            }
            case "validate-plan": {
              CodeValidatePlanArgs codeArgs = CodeValidatePlanArgs.from(args, 2);
              if (codeArgs == null) {
                CodeValidatePlanArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               codeHandler.validatePlan(codeArgs, out);
               return 0;
            }
            case "--help":
            case "-h":
            case "help": {
              displayCodeHelp();
              return 1;
            }
            default:
              System.err.println("Invalid subcommand '" + args[1] + "' of command 'code'");
              System.err.println("See 'adama code help' for a list of subcommands.");
              return 1;
          }
        case "contrib":
          ContribHandler contribHandler = handler.makeContribHandler();
          if (args.length == 1) {
            displayContribHelp();
            return 1;
          }
          switch (args[1]) {
            case "bundle-js": {
              ContribBundleJsArgs contribArgs = ContribBundleJsArgs.from(args, 2);
              if (contribArgs == null) {
                ContribBundleJsArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               contribHandler.bundleJs(contribArgs, out);
               return 0;
            }
            case "copyright": {
              ContribCopyrightArgs contribArgs = ContribCopyrightArgs.from(args, 2);
              if (contribArgs == null) {
                ContribCopyrightArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               contribHandler.copyright(contribArgs, out);
               return 0;
            }
            case "make-api": {
              ContribMakeApiArgs contribArgs = ContribMakeApiArgs.from(args, 2);
              if (contribArgs == null) {
                ContribMakeApiArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               contribHandler.makeApi(contribArgs, out);
               return 0;
            }
            case "make-book": {
              ContribMakeBookArgs contribArgs = ContribMakeBookArgs.from(args, 2);
              if (contribArgs == null) {
                ContribMakeBookArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               contribHandler.makeBook(contribArgs, out);
               return 0;
            }
            case "make-cli": {
              ContribMakeCliArgs contribArgs = ContribMakeCliArgs.from(args, 2);
              if (contribArgs == null) {
                ContribMakeCliArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               contribHandler.makeCli(contribArgs, out);
               return 0;
            }
            case "make-codec": {
              ContribMakeCodecArgs contribArgs = ContribMakeCodecArgs.from(args, 2);
              if (contribArgs == null) {
                ContribMakeCodecArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               contribHandler.makeCodec(contribArgs, out);
               return 0;
            }
            case "make-embed": {
              ContribMakeEmbedArgs contribArgs = ContribMakeEmbedArgs.from(args, 2);
              if (contribArgs == null) {
                ContribMakeEmbedArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               contribHandler.makeEmbed(contribArgs, out);
               return 0;
            }
            case "make-et": {
              ContribMakeEtArgs contribArgs = ContribMakeEtArgs.from(args, 2);
              if (contribArgs == null) {
                ContribMakeEtArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               contribHandler.makeEt(contribArgs, out);
               return 0;
            }
            case "str-temp": {
              ContribStrTempArgs contribArgs = ContribStrTempArgs.from(args, 2);
              if (contribArgs == null) {
                ContribStrTempArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               contribHandler.strTemp(contribArgs, out);
               return 0;
            }
            case "tests-adama": {
              ContribTestsAdamaArgs contribArgs = ContribTestsAdamaArgs.from(args, 2);
              if (contribArgs == null) {
                ContribTestsAdamaArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               contribHandler.testsAdama(contribArgs, out);
               return 0;
            }
            case "tests-rxhtml": {
              ContribTestsRxhtmlArgs contribArgs = ContribTestsRxhtmlArgs.from(args, 2);
              if (contribArgs == null) {
                ContribTestsRxhtmlArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               contribHandler.testsRxhtml(contribArgs, out);
               return 0;
            }
            case "version": {
              ContribVersionArgs contribArgs = ContribVersionArgs.from(args, 2);
              if (contribArgs == null) {
                ContribVersionArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               contribHandler.version(contribArgs, out);
               return 0;
            }
            case "--help":
            case "-h":
            case "help": {
              displayContribHelp();
              return 1;
            }
            default:
              System.err.println("Invalid subcommand '" + args[1] + "' of command 'contrib'");
              System.err.println("See 'adama contrib help' for a list of subcommands.");
              return 1;
          }
        case "database":
          DatabaseHandler databaseHandler = handler.makeDatabaseHandler();
          if (args.length == 1) {
            displayDatabaseHelp();
            return 1;
          }
          switch (args[1]) {
            case "configure": {
              DatabaseConfigureArgs databaseArgs = DatabaseConfigureArgs.from(args, 2);
              if (databaseArgs == null) {
                DatabaseConfigureArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               databaseHandler.configure(databaseArgs, out);
               return 0;
            }
            case "install": {
              DatabaseInstallArgs databaseArgs = DatabaseInstallArgs.from(args, 2);
              if (databaseArgs == null) {
                DatabaseInstallArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               databaseHandler.install(databaseArgs, out);
               return 0;
            }
            case "migrate": {
              DatabaseMigrateArgs databaseArgs = DatabaseMigrateArgs.from(args, 2);
              if (databaseArgs == null) {
                DatabaseMigrateArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               databaseHandler.migrate(databaseArgs, out);
               return 0;
            }
            case "--help":
            case "-h":
            case "help": {
              displayDatabaseHelp();
              return 1;
            }
            default:
              System.err.println("Invalid subcommand '" + args[1] + "' of command 'database'");
              System.err.println("See 'adama database help' for a list of subcommands.");
              return 1;
          }
        case "documents":
        case "document":
          DocumentHandler documentHandler = handler.makeDocumentHandler();
          if (args.length == 1) {
            displayDocumentHelp();
            return 1;
          }
          switch (args[1]) {
            case "attach": {
              DocumentAttachArgs documentArgs = DocumentAttachArgs.from(args, 2);
              if (documentArgs == null) {
                DocumentAttachArgs.help();
                return 1;
               }
               JsonOrError out = output.makeJsonOrError();
               documentHandler.attach(documentArgs, out);
               return 0;
            }
            case "connect": {
              DocumentConnectArgs documentArgs = DocumentConnectArgs.from(args, 2);
              if (documentArgs == null) {
                DocumentConnectArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               documentHandler.connect(documentArgs, out);
               return 0;
            }
            case "create": {
              DocumentCreateArgs documentArgs = DocumentCreateArgs.from(args, 2);
              if (documentArgs == null) {
                DocumentCreateArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               documentHandler.create(documentArgs, out);
               return 0;
            }
            case "delete": {
              DocumentDeleteArgs documentArgs = DocumentDeleteArgs.from(args, 2);
              if (documentArgs == null) {
                DocumentDeleteArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               documentHandler.delete(documentArgs, out);
               return 0;
            }
            case "list": {
              DocumentListArgs documentArgs = DocumentListArgs.from(args, 2);
              if (documentArgs == null) {
                DocumentListArgs.help();
                return 1;
               }
               JsonOrError out = output.makeJsonOrError();
               documentHandler.list(documentArgs, out);
               return 0;
            }
            case "--help":
            case "-h":
            case "help": {
              displayDocumentHelp();
              return 1;
            }
            default:
              System.err.println("Invalid subcommand '" + args[1] + "' of command 'document'");
              System.err.println("See 'adama document help' for a list of subcommands.");
              return 1;
          }
        case "domains":
        case "domain":
          DomainHandler domainHandler = handler.makeDomainHandler();
          if (args.length == 1) {
            displayDomainHelp();
            return 1;
          }
          switch (args[1]) {
            case "list": {
              DomainListArgs domainArgs = DomainListArgs.from(args, 2);
              if (domainArgs == null) {
                DomainListArgs.help();
                return 1;
               }
               JsonOrError out = output.makeJsonOrError();
               domainHandler.list(domainArgs, out);
               return 0;
            }
            case "map": {
              DomainMapArgs domainArgs = DomainMapArgs.from(args, 2);
              if (domainArgs == null) {
                DomainMapArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               domainHandler.map(domainArgs, out);
               return 0;
            }
            case "unmap": {
              DomainUnmapArgs domainArgs = DomainUnmapArgs.from(args, 2);
              if (domainArgs == null) {
                DomainUnmapArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               domainHandler.unmap(domainArgs, out);
               return 0;
            }
            case "--help":
            case "-h":
            case "help": {
              displayDomainHelp();
              return 1;
            }
            default:
              System.err.println("Invalid subcommand '" + args[1] + "' of command 'domain'");
              System.err.println("See 'adama domain help' for a list of subcommands.");
              return 1;
          }
        case "frontend":
          FrontendHandler frontendHandler = handler.makeFrontendHandler();
          if (args.length == 1) {
            displayFrontendHelp();
            return 1;
          }
          switch (args[1]) {
            case "bundle": {
              FrontendBundleArgs frontendArgs = FrontendBundleArgs.from(args, 2);
              if (frontendArgs == null) {
                FrontendBundleArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               frontendHandler.bundle(frontendArgs, out);
               return 0;
            }
            case "dev-server": {
              FrontendDevServerArgs frontendArgs = FrontendDevServerArgs.from(args, 2);
              if (frontendArgs == null) {
                FrontendDevServerArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               frontendHandler.devServer(frontendArgs, out);
               return 0;
            }
            case "make-200": {
              FrontendMake200Args frontendArgs = FrontendMake200Args.from(args, 2);
              if (frontendArgs == null) {
                FrontendMake200Args.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               frontendHandler.make200(frontendArgs, out);
               return 0;
            }
            case "rxhtml": {
              FrontendRxhtmlArgs frontendArgs = FrontendRxhtmlArgs.from(args, 2);
              if (frontendArgs == null) {
                FrontendRxhtmlArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               frontendHandler.rxhtml(frontendArgs, out);
               return 0;
            }
            case "set-libadama": {
              FrontendSetLibadamaArgs frontendArgs = FrontendSetLibadamaArgs.from(args, 2);
              if (frontendArgs == null) {
                FrontendSetLibadamaArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               frontendHandler.setLibadama(frontendArgs, out);
               return 0;
            }
            case "study-css": {
              FrontendStudyCssArgs frontendArgs = FrontendStudyCssArgs.from(args, 2);
              if (frontendArgs == null) {
                FrontendStudyCssArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               frontendHandler.studyCss(frontendArgs, out);
               return 0;
            }
            case "wrap-css": {
              FrontendWrapCssArgs frontendArgs = FrontendWrapCssArgs.from(args, 2);
              if (frontendArgs == null) {
                FrontendWrapCssArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               frontendHandler.wrapCss(frontendArgs, out);
               return 0;
            }
            case "--help":
            case "-h":
            case "help": {
              displayFrontendHelp();
              return 1;
            }
            default:
              System.err.println("Invalid subcommand '" + args[1] + "' of command 'frontend'");
              System.err.println("See 'adama frontend help' for a list of subcommands.");
              return 1;
          }
        case "service":
        case "services":
          ServicesHandler servicesHandler = handler.makeServicesHandler();
          if (args.length == 1) {
            displayServicesHelp();
            return 1;
          }
          switch (args[1]) {
            case "auto": {
              ServicesAutoArgs servicesArgs = ServicesAutoArgs.from(args, 2);
              if (servicesArgs == null) {
                ServicesAutoArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               servicesHandler.auto(servicesArgs, out);
               return 0;
            }
            case "backend": {
              ServicesBackendArgs servicesArgs = ServicesBackendArgs.from(args, 2);
              if (servicesArgs == null) {
                ServicesBackendArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               servicesHandler.backend(servicesArgs, out);
               return 0;
            }
            case "dashboards": {
              ServicesDashboardsArgs servicesArgs = ServicesDashboardsArgs.from(args, 2);
              if (servicesArgs == null) {
                ServicesDashboardsArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               servicesHandler.dashboards(servicesArgs, out);
               return 0;
            }
            case "frontend": {
              ServicesFrontendArgs servicesArgs = ServicesFrontendArgs.from(args, 2);
              if (servicesArgs == null) {
                ServicesFrontendArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               servicesHandler.frontend(servicesArgs, out);
               return 0;
            }
            case "overlord": {
              ServicesOverlordArgs servicesArgs = ServicesOverlordArgs.from(args, 2);
              if (servicesArgs == null) {
                ServicesOverlordArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               servicesHandler.overlord(servicesArgs, out);
               return 0;
            }
            case "probe": {
              ServicesProbeArgs servicesArgs = ServicesProbeArgs.from(args, 2);
              if (servicesArgs == null) {
                ServicesProbeArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               servicesHandler.probe(servicesArgs, out);
               return 0;
            }
            case "solo": {
              ServicesSoloArgs servicesArgs = ServicesSoloArgs.from(args, 2);
              if (servicesArgs == null) {
                ServicesSoloArgs.help();
                return 1;
               }
               YesOrError out = output.makeYesOrError();
               servicesHandler.solo(servicesArgs, out);
               return 0;
            }
            case "--help":
            case "-h":
            case "help": {
              displayServicesHelp();
              return 1;
            }
            default:
              System.err.println("Invalid subcommand '" + args[1] + "' of command 'services'");
              System.err.println("See 'adama services help' for a list of subcommands.");
              return 1;
          }
          case "deinit": {
            DeinitArgs mainArgs = DeinitArgs.from(args, 1);
            if (mainArgs == null) {
              DeinitArgs.help();
              return 1;
             }
             YesOrError out = output.makeYesOrError();
             handler.deinit(mainArgs , out);
             return 0;
          }
          case "dumpenv": {
            DumpenvArgs mainArgs = DumpenvArgs.from(args, 1);
            if (mainArgs == null) {
              DumpenvArgs.help();
              return 1;
             }
             YesOrError out = output.makeYesOrError();
             handler.dumpenv(mainArgs , out);
             return 0;
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
          case "kickstart": {
            KickstartArgs mainArgs = KickstartArgs.from(args, 1);
            if (mainArgs == null) {
              KickstartArgs.help();
              return 1;
             }
             YesOrError out = output.makeYesOrError();
             handler.kickstart(mainArgs , out);
             return 0;
          }
          case "version": {
            VersionArgs mainArgs = VersionArgs.from(args, 1);
            if (mainArgs == null) {
              VersionArgs.help();
              return 1;
             }
             YesOrError out = output.makeYesOrError();
             handler.version(mainArgs , out);
             return 0;
          }
          default:
            System.err.println("Invalid command '" + args[0] + "'");
            System.err.println("See 'adama help' for a list of commands.");
            return 1;
      }
    } catch (Exception ex) {
      if (ex instanceof Config.BadException) {
        System.err.println(Util.prefix("[CONFIG ERROR]", Util.ANSI.Red));
        System.err.println(ex.getMessage());
      } else if (ex instanceof ErrorCodeException) {
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

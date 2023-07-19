/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.router;
import org.adamalang.cli.Util;
public class Help {
  public static void displayRootHelp() {
    System.out.println(Util.prefix("Interacts with the Adama Platform", Util.ANSI.Green));
    System.out.println();
    System.out.println("    " + Util.prefix("adama", Util.ANSI.Green) + " " + Util.prefix("[SUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println("    " + Util.prefix(Util.justifyLeft("--config", 15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("SUBCOMMANDS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("space", 15), Util.ANSI.Cyan) + "Provides command related to working with space collections...");
    System.out.println("    " + Util.prefix(Util.justifyLeft("authority", 15), Util.ANSI.Cyan) + "Manage authorities");
    System.out.println("    " + Util.prefix(Util.justifyLeft("account", 15), Util.ANSI.Cyan) + "Manage your account");
    System.out.println("    " + Util.prefix(Util.justifyLeft("code", 15), Util.ANSI.Cyan) + "Local developer tools");
    System.out.println("    " + Util.prefix(Util.justifyLeft("contrib", 15), Util.ANSI.Cyan) + "Open source contributor tools");
    System.out.println("    " + Util.prefix(Util.justifyLeft("database", 15), Util.ANSI.Cyan) + "Prepare database for usage");
    System.out.println("    " + Util.prefix(Util.justifyLeft("document", 15), Util.ANSI.Cyan) + "Interact with documents");
    System.out.println("    " + Util.prefix(Util.justifyLeft("domain", 15), Util.ANSI.Cyan) + "Manage Domains");
    System.out.println("    " + Util.prefix(Util.justifyLeft("frontend", 15), Util.ANSI.Cyan) + "Frontend tools (rxhtml)");
    System.out.println("    " + Util.prefix(Util.justifyLeft("services", 15), Util.ANSI.Cyan) + "Launch a service");
    System.out.println("    " + Util.prefix(Util.justifyLeft("dumpenv", 15), Util.ANSI.Green) + "Dump your environment variables");
    System.out.println("    " + Util.prefix(Util.justifyLeft("init", 15), Util.ANSI.Green) + "Initializes the config with a valid token");
  }
  public static void displaySpaceHelp() {
    System.out.println(Util.prefix("Provides command related to working with space collections...", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama space", Util.ANSI.Green) + " " + Util.prefix("[SPACESUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("--config", 15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("SPACESUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("create", 15), Util.ANSI.Green) + "Creates a new space");
    System.out.println("    " + Util.prefix(Util.justifyLeft("delete", 15), Util.ANSI.Green) + "Deletes an empty space");
    System.out.println("    " + Util.prefix(Util.justifyLeft("deploy", 15), Util.ANSI.Green) + "Deploy a plan to a space");
    System.out.println("    " + Util.prefix(Util.justifyLeft("developers", 15), Util.ANSI.Green) + "List developers for the given space");
    System.out.println("    " + Util.prefix(Util.justifyLeft("encrypt-secret", 15), Util.ANSI.Green) + "Encrypt a secret to store within code");
    System.out.println("    " + Util.prefix(Util.justifyLeft("generate-key", 15), Util.ANSI.Green) + "Generate a server-side key to use for storing secrets");
    System.out.println("    " + Util.prefix(Util.justifyLeft("get", 15), Util.ANSI.Green) + "Get a space's plan");
    System.out.println("    " + Util.prefix(Util.justifyLeft("get-rxhtml", 15), Util.ANSI.Green) + "Get the frontend RxHTML forest");
    System.out.println("    " + Util.prefix(Util.justifyLeft("list", 15), Util.ANSI.Green) + "List spaces available to your account");
    System.out.println("    " + Util.prefix(Util.justifyLeft("reflect", 15), Util.ANSI.Green) + "Get a file of the reflection of a space");
    System.out.println("    " + Util.prefix(Util.justifyLeft("set-role", 15), Util.ANSI.Green) + "Set the role of another developer");
    System.out.println("    " + Util.prefix(Util.justifyLeft("set-rxhtml", 15), Util.ANSI.Green) + "Set the frontend RxHTML forest");
    System.out.println("    " + Util.prefix(Util.justifyLeft("upload", 15), Util.ANSI.Green) + "Placeholder");
    System.out.println("    " + Util.prefix(Util.justifyLeft("usage", 15), Util.ANSI.Green) + "Iterate the billed usage");
  }
  public static void displayAuthorityHelp() {
    System.out.println(Util.prefix("Manage authorities", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama authority", Util.ANSI.Green) + " " + Util.prefix("[AUTHORITYSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("--config", 15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("AUTHORITYSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("append-local", 15), Util.ANSI.Green) + "Append a new public key to the public key file");
    System.out.println("    " + Util.prefix(Util.justifyLeft("create", 15), Util.ANSI.Green) + "Creates a new authority");
    System.out.println("    " + Util.prefix(Util.justifyLeft("create-local", 15), Util.ANSI.Green) + "Make a new set of public keys");
    System.out.println("    " + Util.prefix(Util.justifyLeft("destroy", 15), Util.ANSI.Green) + "Destroy an authority");
    System.out.println("    " + Util.prefix(Util.justifyLeft("get", 15), Util.ANSI.Green) + "Get released public keys for an authority");
    System.out.println("    " + Util.prefix(Util.justifyLeft("list", 15), Util.ANSI.Green) + "List authorities this developer owns");
    System.out.println("    " + Util.prefix(Util.justifyLeft("set", 15), Util.ANSI.Green) + "Set public keys to an authority");
    System.out.println("    " + Util.prefix(Util.justifyLeft("sign", 15), Util.ANSI.Green) + "Sign an agent with a local private key");
  }
  public static void displayAccountHelp() {
    System.out.println(Util.prefix("Manage your account", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama account", Util.ANSI.Green) + " " + Util.prefix("[ACCOUNTSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("--config", 15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("ACCOUNTSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("set-password", 15), Util.ANSI.Green) + "Create a password to be used on web");
  }
  public static void displayCodeHelp() {
    System.out.println(Util.prefix("Local developer tools", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama code", Util.ANSI.Green) + " " + Util.prefix("[CODESUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("--config", 15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("CODESUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("bundle-plan", 15), Util.ANSI.Green) + "Placeholder");
    System.out.println("    " + Util.prefix(Util.justifyLeft("compile-file", 15), Util.ANSI.Green) + "Compiles the adama file and shows any problems");
    System.out.println("    " + Util.prefix(Util.justifyLeft("lsp", 15), Util.ANSI.Green) + "Spin up a single threaded language service protocol server");
    System.out.println("    " + Util.prefix(Util.justifyLeft("reflect-dump", 15), Util.ANSI.Green) + "Compiles the adama file and dumps the reflection json");
    System.out.println("    " + Util.prefix(Util.justifyLeft("validate-plan", 15), Util.ANSI.Green) + "Validates a deployment plan (locally) for speed");
  }
  public static void displayContribHelp() {
    System.out.println(Util.prefix("Open source contributor tools", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama contrib", Util.ANSI.Green) + " " + Util.prefix("[CONTRIBSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("--config", 15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("CONTRIBSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("bundle-js", 15), Util.ANSI.Green) + "Bundles the libadama.js into the webserver");
    System.out.println("    " + Util.prefix(Util.justifyLeft("copyright", 15), Util.ANSI.Green) + "Sprinkle Jeff's name everywhere.");
    System.out.println("    " + Util.prefix(Util.justifyLeft("make-api", 15), Util.ANSI.Green) + "Produces api files for SaaS and documentation for the WebSocket low level API.");
    System.out.println("    " + Util.prefix(Util.justifyLeft("make-cli", 15), Util.ANSI.Green) + "Generate the command line router");
    System.out.println("    " + Util.prefix(Util.justifyLeft("make-codec", 15), Util.ANSI.Green) + "Generates the networking codec");
    System.out.println("    " + Util.prefix(Util.justifyLeft("make-et", 15), Util.ANSI.Green) + "Generates the error table which provides useful insight to issues");
    System.out.println("    " + Util.prefix(Util.justifyLeft("tests-adama", 15), Util.ANSI.Green) + "Generate tests for Adama Language.");
    System.out.println("    " + Util.prefix(Util.justifyLeft("tests-rxhtml", 15), Util.ANSI.Green) + "Generate tests for RxHTML.");
  }
  public static void displayDatabaseHelp() {
    System.out.println(Util.prefix("Prepare database for usage", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama database", Util.ANSI.Green) + " " + Util.prefix("[DATABASESUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("--config", 15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("DATABASESUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("configure", 15), Util.ANSI.Green) + "Update the configuration");
    System.out.println("    " + Util.prefix(Util.justifyLeft("install", 15), Util.ANSI.Green) + "Install the tables on a monolithic database");
    System.out.println("    " + Util.prefix(Util.justifyLeft("migrate", 15), Util.ANSI.Green) + "Migrate data from 'db' to 'nextdb'");
  }
  public static void displayDocumentHelp() {
    System.out.println(Util.prefix("Interact with documents", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama document", Util.ANSI.Green) + " " + Util.prefix("[DOCUMENTSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("--config", 15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("DOCUMENTSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("attach", 15), Util.ANSI.Green) + "Attach an asset to a document");
    System.out.println("    " + Util.prefix(Util.justifyLeft("connect", 15), Util.ANSI.Green) + "Connect to a document");
    System.out.println("    " + Util.prefix(Util.justifyLeft("create", 15), Util.ANSI.Green) + "Create a document");
    System.out.println("    " + Util.prefix(Util.justifyLeft("delete", 15), Util.ANSI.Green) + "Delete a document");
    System.out.println("    " + Util.prefix(Util.justifyLeft("list", 15), Util.ANSI.Green) + "List documents");
  }
  public static void displayDomainHelp() {
    System.out.println(Util.prefix("Manage Domains", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama domain", Util.ANSI.Green) + " " + Util.prefix("[DOMAINSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("--config", 15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("DOMAINSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("list", 15), Util.ANSI.Green) + "List domains");
    System.out.println("    " + Util.prefix(Util.justifyLeft("map", 15), Util.ANSI.Green) + "Map a domain to a space");
    System.out.println("    " + Util.prefix(Util.justifyLeft("unmap", 15), Util.ANSI.Green) + "Unmap a domain from a space");
  }
  public static void displayFrontendHelp() {
    System.out.println(Util.prefix("Frontend tools (rxhtml)", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama frontend", Util.ANSI.Green) + " " + Util.prefix("[FRONTENDSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("--config", 15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("FRONTENDSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("dev-server", 15), Util.ANSI.Green) + "Host the working directory as a webserver");
    System.out.println("    " + Util.prefix(Util.justifyLeft("edhtml", 15), Util.ANSI.Green) + "Compile an edhtml build instruction file");
    System.out.println("    " + Util.prefix(Util.justifyLeft("rxhtml", 15), Util.ANSI.Green) + "Compile an rxhtml template set");
  }
  public static void displayServicesHelp() {
    System.out.println(Util.prefix("Launch a service", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama services", Util.ANSI.Green) + " " + Util.prefix("[SERVICESSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("--config", 15), Util.ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("SERVICESSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix(Util.justifyLeft("auto", 15), Util.ANSI.Green) + "The config will decide the role");
    System.out.println("    " + Util.prefix(Util.justifyLeft("backend", 15), Util.ANSI.Green) + "Spin up a gRPC back-end node");
    System.out.println("    " + Util.prefix(Util.justifyLeft("dashboards", 15), Util.ANSI.Green) + "Produce dashboards for prometheus.");
    System.out.println("    " + Util.prefix(Util.justifyLeft("frontend", 15), Util.ANSI.Green) + "Spin up a WebSocket front-end node");
    System.out.println("    " + Util.prefix(Util.justifyLeft("overlord", 15), Util.ANSI.Green) + "Spin up the cluster overlord");
    System.out.println("    " + Util.prefix(Util.justifyLeft("solo", 15), Util.ANSI.Green) + "Spin up a solo machine");
  }
}

/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.cli.router;
import org.adamalang.common.ANSI;
import org.adamalang.common.ColorUtilTools;
public class Help {
  public static void displayRootHelp() {
    System.out.println(ColorUtilTools.prefix("Interacts with the Adama Platform", ANSI.Green));
    System.out.println();
    System.out.println("    " + ColorUtilTools.prefix("adama", ANSI.Green) + " " + ColorUtilTools.prefix("[SUBCOMMAND]", ANSI.Magenta));
    System.out.println();
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("--config", 15), ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("SUBCOMMANDS:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("space", 15), ANSI.Cyan) + "Provides command related to working with space collections...");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("authority", 15), ANSI.Cyan) + "Manage authorities");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("account", 15), ANSI.Cyan) + "Manage your account");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("code", 15), ANSI.Cyan) + "Local developer tools");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("contrib", 15), ANSI.Cyan) + "Open source contributor tools");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("database", 15), ANSI.Cyan) + "Work with production databases for the global service.");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("document", 15), ANSI.Cyan) + "Interact with documents within a space.");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("ops", 15), ANSI.Cyan) + "Operational tasks");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("domain", 15), ANSI.Cyan) + "Manage custom domains");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("frontend", 15), ANSI.Cyan) + "Frontend tools (rxhtml)");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("services", 15), ANSI.Cyan) + "Launch a service");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("canary", 15), ANSI.Green) + "Run an E2E test suite against production");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("deinit", 15), ANSI.Green) + "Destroy your account. This requires you to delete all spaces, documents, authorities, and domains.");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("devbox", 15), ANSI.Green) + "Host the working directory as a personal localhost instance");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("dumpenv", 15), ANSI.Green) + "Dump your environment variables");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("init", 15), ANSI.Green) + "Initializes the config with a valid token");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("kickstart", 15), ANSI.Green) + "Kickstart a project via an interactive process!");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("version", 15), ANSI.Green) + "Dump the current Adama version");
  }
  public static void displaySpaceHelp() {
    System.out.println(ColorUtilTools.prefix("Provides command related to working with space collections...", ANSI.Green));
    System.out.println();
    System.out.println(ColorUtilTools.prefix("USAGE:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix("adama space", ANSI.Green) + " " + ColorUtilTools.prefix("[SPACESUBCOMMAND]", ANSI.Magenta));
    System.out.println(ColorUtilTools.prefix("FLAGS:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("--config", 15), ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("SPACESUBCOMMAND:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("create", 15), ANSI.Green) + "Creates a new space");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("delete", 15), ANSI.Green) + "Deletes an empty space");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("deploy", 15), ANSI.Green) + "Deploy a plan to a space");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("developers", 15), ANSI.Green) + "List developers for the given space");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("encrypt-priv", 15), ANSI.Green) + "Encrypt a private key to store within code");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("encrypt-secret", 15), ANSI.Green) + "Encrypt a secret to store within code");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("generate-key", 15), ANSI.Green) + "Generate a server-side key to use for storing secrets");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("get", 15), ANSI.Green) + "Get a space's plan");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("get-policy", 15), ANSI.Green) + "Get the access control policy for the space");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("get-rxhtml", 15), ANSI.Green) + "Get the frontend RxHTML forest");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("list", 15), ANSI.Green) + "List spaces available to your account");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("metrics", 15), ANSI.Green) + "Get a metric report for the space and the documents that share the prefix");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("reflect", 15), ANSI.Green) + "Get a file of the reflection of a space");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("set-policy", 15), ANSI.Green) + "Set the space's access control policy");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("set-role", 15), ANSI.Green) + "Set the role of another developer");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("set-rxhtml", 15), ANSI.Green) + "Set the frontend RxHTML forest");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("upload", 15), ANSI.Green) + "Placeholder");
  }
  public static void displayAuthorityHelp() {
    System.out.println(ColorUtilTools.prefix("Manage authorities", ANSI.Green));
    System.out.println();
    System.out.println(ColorUtilTools.prefix("USAGE:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix("adama authority", ANSI.Green) + " " + ColorUtilTools.prefix("[AUTHORITYSUBCOMMAND]", ANSI.Magenta));
    System.out.println(ColorUtilTools.prefix("FLAGS:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("--config", 15), ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("AUTHORITYSUBCOMMAND:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("append-local", 15), ANSI.Green) + "Append a new public key to the public key file");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("create", 15), ANSI.Green) + "Creates a new authority");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("create-local", 15), ANSI.Green) + "Make a new set of public keys");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("destroy", 15), ANSI.Green) + "Destroy an authority");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("get", 15), ANSI.Green) + "Get released public keys for an authority");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("list", 15), ANSI.Green) + "List authorities this developer owns");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("set", 15), ANSI.Green) + "Set public keys to an authority");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("sign", 15), ANSI.Green) + "Sign an agent with a local private key");
  }
  public static void displayAccountHelp() {
    System.out.println(ColorUtilTools.prefix("Manage your account", ANSI.Green));
    System.out.println();
    System.out.println(ColorUtilTools.prefix("USAGE:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix("adama account", ANSI.Green) + " " + ColorUtilTools.prefix("[ACCOUNTSUBCOMMAND]", ANSI.Magenta));
    System.out.println(ColorUtilTools.prefix("FLAGS:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("--config", 15), ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("ACCOUNTSUBCOMMAND:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("set-password", 15), ANSI.Green) + "Create a password to be used on web");
  }
  public static void displayCodeHelp() {
    System.out.println(ColorUtilTools.prefix("Local developer tools", ANSI.Green));
    System.out.println();
    System.out.println(ColorUtilTools.prefix("USAGE:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix("adama code", ANSI.Green) + " " + ColorUtilTools.prefix("[CODESUBCOMMAND]", ANSI.Magenta));
    System.out.println(ColorUtilTools.prefix("FLAGS:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("--config", 15), ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("CODESUBCOMMAND:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("bundle-plan", 15), ANSI.Green) + "Bundle the main and imports into a single deployment plan.");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("compile-file", 15), ANSI.Green) + "Compiles the adama file and shows any problems");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("diagram", 15), ANSI.Green) + "Convert a reflection JSON into a mermaid diagram source");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("format", 15), ANSI.Green) + "Format the file or directory recursively (and inline updates)");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("lsp", 15), ANSI.Green) + "Spin up a single threaded language service protocol server");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("reflect-dump", 15), ANSI.Green) + "Compiles the adama file and dumps the reflection json");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("validate-plan", 15), ANSI.Green) + "Validates a deployment plan (locally) for speed");
  }
  public static void displayContribHelp() {
    System.out.println(ColorUtilTools.prefix("Open source contributor tools", ANSI.Green));
    System.out.println();
    System.out.println(ColorUtilTools.prefix("USAGE:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix("adama contrib", ANSI.Green) + " " + ColorUtilTools.prefix("[CONTRIBSUBCOMMAND]", ANSI.Magenta));
    System.out.println(ColorUtilTools.prefix("FLAGS:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("--config", 15), ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("CONTRIBSUBCOMMAND:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("bundle-js", 15), ANSI.Green) + "Bundles the libadama.js into the webserver");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("copyright", 15), ANSI.Green) + "Sprinkle the copyright everywhere.");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("make-api", 15), ANSI.Green) + "Produces api files for SaaS and documentation for the WebSocket low level API.");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("make-book", 15), ANSI.Green) + "Compile Adama's Book");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("make-cli", 15), ANSI.Green) + "Generate the command line router");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("make-codec", 15), ANSI.Green) + "Generates the networking codec");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("make-embed", 15), ANSI.Green) + "Generates the embedded templates");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("make-et", 15), ANSI.Green) + "Generates the error table which provides useful insight to issues");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("str-temp", 15), ANSI.Green) + "Generate string templates");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("tests-adama", 15), ANSI.Green) + "Generate tests for Adama Language.");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("tests-rxhtml", 15), ANSI.Green) + "Generate tests for RxHTML.");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("version", 15), ANSI.Green) + "Create the version number for the platform");
  }
  public static void displayDatabaseHelp() {
    System.out.println(ColorUtilTools.prefix("Work with production databases for the global service.", ANSI.Green));
    System.out.println();
    System.out.println(ColorUtilTools.prefix("USAGE:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix("adama database", ANSI.Green) + " " + ColorUtilTools.prefix("[DATABASESUBCOMMAND]", ANSI.Magenta));
    System.out.println(ColorUtilTools.prefix("FLAGS:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("--config", 15), ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("DATABASESUBCOMMAND:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("configure", 15), ANSI.Green) + "Update the configuration");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("install", 15), ANSI.Green) + "Install the tables on a monolithic database");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("make-reserved", 15), ANSI.Green) + "Create reserved spaces");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("migrate", 15), ANSI.Green) + "Migrate data from 'db' to 'nextdb'");
  }
  public static void displayDocumentHelp() {
    System.out.println(ColorUtilTools.prefix("Interact with documents within a space.", ANSI.Green));
    System.out.println();
    System.out.println(ColorUtilTools.prefix("USAGE:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix("adama document", ANSI.Green) + " " + ColorUtilTools.prefix("[DOCUMENTSUBCOMMAND]", ANSI.Magenta));
    System.out.println(ColorUtilTools.prefix("FLAGS:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("--config", 15), ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("DOCUMENTSUBCOMMAND:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("attach", 15), ANSI.Green) + "Attach an asset to a document");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("connect", 15), ANSI.Green) + "Connect to a document");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("create", 15), ANSI.Green) + "Create a document");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("delete", 15), ANSI.Green) + "Delete a document");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("download-archive", 15), ANSI.Green) + "Download the latest archive backup");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("list", 15), ANSI.Green) + "List documents");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("list-push-tokens", 15), ANSI.Green) + "List push tokens for a specific agent within a document's authority");
  }
  public static void displayOpsHelp() {
    System.out.println(ColorUtilTools.prefix("Operational tasks", ANSI.Green));
    System.out.println();
    System.out.println(ColorUtilTools.prefix("USAGE:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix("adama ops", ANSI.Green) + " " + ColorUtilTools.prefix("[OPSSUBCOMMAND]", ANSI.Magenta));
    System.out.println(ColorUtilTools.prefix("FLAGS:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("--config", 15), ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("OPSSUBCOMMAND:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("compact", 15), ANSI.Green) + "Compact an archive to a single JSON file");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("explain", 15), ANSI.Green) + "Explain the history of a value at a path");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("forensics", 15), ANSI.Green) + "Dive into a data store and recover snapshots");
  }
  public static void displayDomainHelp() {
    System.out.println(ColorUtilTools.prefix("Manage custom domains", ANSI.Green));
    System.out.println();
    System.out.println(ColorUtilTools.prefix("USAGE:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix("adama domain", ANSI.Green) + " " + ColorUtilTools.prefix("[DOMAINSUBCOMMAND]", ANSI.Magenta));
    System.out.println(ColorUtilTools.prefix("FLAGS:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("--config", 15), ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("DOMAINSUBCOMMAND:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("configure", 15), ANSI.Green) + "Provide a product configuration to define various aspects of a product by domain");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("list", 15), ANSI.Green) + "List domains");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("map", 15), ANSI.Green) + "Map a domain to a space");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("unmap", 15), ANSI.Green) + "Unmap a domain from a space");
  }
  public static void displayFrontendHelp() {
    System.out.println(ColorUtilTools.prefix("Frontend tools (rxhtml)", ANSI.Green));
    System.out.println();
    System.out.println(ColorUtilTools.prefix("USAGE:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix("adama frontend", ANSI.Green) + " " + ColorUtilTools.prefix("[FRONTENDSUBCOMMAND]", ANSI.Magenta));
    System.out.println(ColorUtilTools.prefix("FLAGS:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("--config", 15), ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("FRONTENDSUBCOMMAND:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("bundle", 15), ANSI.Green) + "Bundle many *.rx.html into one big one.");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("decrypt-product-config", 15), ANSI.Green) + "Decrypt product config");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("dev-server", 15), ANSI.Green) + "Host the working directory as a webserver");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("enable-encryption", 15), ANSI.Green) + "Encrypted product config encryption by generating a master key which");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("encrypt-product-config", 15), ANSI.Green) + "Encrypt product config");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("make-200", 15), ANSI.Green) + "Create a 200.html");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("mobile-capacitor", 15), ANSI.Green) + "Create a shell for https://capacitorjs.com/");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("push-generate", 15), ANSI.Green) + "Generate VAPID tokens for a devbox.");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("rxhtml", 15), ANSI.Green) + "Compile an rxhtml template set");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("set-libadama", 15), ANSI.Green) + "Update your config to have a local-libadama-path-default which will be used in 'dev-server' when --local-libadama-path is not specified.");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("validate", 15), ANSI.Green) + "(Temporary) Runs a deeper check on an RxHTML forest");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("wrap-css", 15), ANSI.Green) + "Wrap a CSS file in a rx.html script to be picked up during build");
  }
  public static void displayServicesHelp() {
    System.out.println(ColorUtilTools.prefix("Launch a service", ANSI.Green));
    System.out.println();
    System.out.println(ColorUtilTools.prefix("USAGE:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix("adama services", ANSI.Green) + " " + ColorUtilTools.prefix("[SERVICESSUBCOMMAND]", ANSI.Magenta));
    System.out.println(ColorUtilTools.prefix("FLAGS:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("--config", 15), ANSI.Green) + "Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(ColorUtilTools.prefix("SERVICESSUBCOMMAND:", ANSI.Yellow));
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("auto", 15), ANSI.Green) + "The config will decide the role");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("backend", 15), ANSI.Green) + "Spin up a Adama back-end node");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("dashboards", 15), ANSI.Green) + "Produce dashboards for prometheus.");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("frontend", 15), ANSI.Green) + "Spin up a WebSocket front-end node");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("overlord", 15), ANSI.Green) + "Spin up the cluster overlord");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("prepare", 15), ANSI.Green) + "Run code that signals a deployment is coming");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("probe", 15), ANSI.Green) + "Connect to the local Adama instance");
    System.out.println("    " + ColorUtilTools.prefix(ColorUtilTools.justifyLeft("solo", 15), ANSI.Green) + "Spin up a solo machine");
  }
}

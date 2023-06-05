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
    System.out.println("    " + Util.prefix(Util.justifyLeft("space", 15), Util.ANSI.Green) + "Provides command related to working with space collections...");
    System.out.println("    " + Util.prefix(Util.justifyLeft("authority", 15), Util.ANSI.Green) + "Manage authorities");
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
    System.out.println("    " + Util.prefix(Util.justifyLeft("set-rxhtml", 15), Util.ANSI.Green) + "Set the frontend RxHTML forest");
    System.out.println("    " + Util.prefix(Util.justifyLeft("get-rxhtml", 15), Util.ANSI.Green) + "Get the frontend RxHTML forest");
    System.out.println("    " + Util.prefix(Util.justifyLeft("upload", 15), Util.ANSI.Green) + "Placeholder");
    System.out.println("    " + Util.prefix(Util.justifyLeft("download", 15), Util.ANSI.Green) + "Download a space's plan");
    System.out.println("    " + Util.prefix(Util.justifyLeft("list", 15), Util.ANSI.Green) + "List spaces available to your account");
    System.out.println("    " + Util.prefix(Util.justifyLeft("usage", 15), Util.ANSI.Green) + "Iterate the billed usage");
    System.out.println("    " + Util.prefix(Util.justifyLeft("reflect", 15), Util.ANSI.Green) + "Get a file of the reflection of a space");
    System.out.println("    " + Util.prefix(Util.justifyLeft("set-role", 15), Util.ANSI.Green) + "Get a file of the reflection of a space");
    System.out.println("    " + Util.prefix(Util.justifyLeft("generate-key", 15), Util.ANSI.Green) + "Generate a server-side key to use for storing secrets");
    System.out.println("    " + Util.prefix(Util.justifyLeft("encrypt-secret", 15), Util.ANSI.Green) + "Encrypt a secret to store within code");
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
    System.out.println("    " + Util.prefix(Util.justifyLeft("create", 15), Util.ANSI.Green) + "Creates a new authority");
    System.out.println("    " + Util.prefix(Util.justifyLeft("set", 15), Util.ANSI.Green) + "Set public keys to an authority");
    System.out.println("    " + Util.prefix(Util.justifyLeft("get", 15), Util.ANSI.Green) + "Get released public keys for an authority");
    System.out.println("    " + Util.prefix(Util.justifyLeft("destroy", 15), Util.ANSI.Green) + "Destroy an authority");
    System.out.println("    " + Util.prefix(Util.justifyLeft("list", 15), Util.ANSI.Green) + "List authorities this developer owns");
    System.out.println("    " + Util.prefix(Util.justifyLeft("create-local", 15), Util.ANSI.Green) + "Make a new set of public keys");
    System.out.println("    " + Util.prefix(Util.justifyLeft("append-local", 15), Util.ANSI.Green) + "Append a new public key to the public key file");
    System.out.println("    " + Util.prefix(Util.justifyLeft("sign", 15), Util.ANSI.Green) + "Sign an agent with a local private key");
  }
}
package org.adamalang.cli.commands;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.common.Hashing;
import org.adamalang.common.Json;
import java.io.File;
import java.nio.file.Files;

public class Space { 
  public static void execute(Config config, String[] args) throws Exception{
    if (args.length == 0) {
      spaceHelp();
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "usage":
        spaceUsage(config, next);
         return;
      case "create":
        spaceCreate(config, next);
         return;
      case "delete":
        spaceDelete(config, next);
         return;
      case "deploy":
        spaceDeploy(config, next);
         return;
      case "download":
        spaceDownload(config, next);
         return;
      case "list":
        spaceList(config, next);
         return;
      case "set-role":
        spaceSetRole(config, next);
         return;
      case "generate-key":
        spaceGenerateKey(config, next);
         return;
      case "encrypt-secret":
        spaceEncryptSecret(config, next);
         return;
      case "set-rxhtml":
        spaceSetRxhtml(config, next);
         return;
      case "get-rxhtml":
        spaceGetRxhtml(config, next);
         return;
      case "reflect":
        spaceReflect(config, next);
         return;
      case "help":
        spaceHelp();
        return;
     }
  }
  public static void spaceUsage(Config config, String[] args) throws Exception {
  }
  public static void spaceCreate(Config config, String[] args) throws Exception {
  }
  public static void spaceDelete(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/delete");
        request.put("identity", identity);
        request.put("space", space);
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }
  public static void spaceDeploy(Config config, String[] args) throws Exception {
  }
  public static void spaceDownload(Config config, String[] args) throws Exception {
  }
  public static void spaceList(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String marker = Util.extractWithDefault("--marker", "-m", null, args);
    int limit = Integer.parseInt(Util.extractWithDefault("--limit", "-l", "100", args));
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/list");
        request.put("identity", identity);
        if (marker != null) {
          request.put("marker", marker);
        }
        request.put("limit", limit);
        connection.stream(request, (cId, response) -> {
          System.err.println(response.toPrettyString());
        });
      }
    }
  }
  public static void spaceSetRole(Config config, String[] args) throws Exception {
  }
  public static void spaceGenerateKey(Config config, String[] args) throws Exception {
  }
  public static void spaceEncryptSecret(Config config, String[] args) throws Exception {
  }
  public static void spaceSetRxhtml(Config config, String[] args) throws Exception {
  }
  public static void spaceGetRxhtml(Config config, String[] args) throws Exception {
  }
  public static void spaceReflect(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    String key = Util.extractWithDefault("--key", "-k", "", args);
    File output = new File(Util.extractOrCrash("--output", "-o", args));
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/reflect");
        request.put("identity", identity);
        request.put("space", space);
        request.put("key", key);
        ObjectNode response = connection.execute(request);
        Files.writeString(output.toPath(), response.toPrettyString());
      }
    }
  }
  public static void spaceHelp() throws Exception {
    System.out.println(Util.prefix("Manage a space", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama space", Util.ANSI.Green) + " " + Util.prefix("[SUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("SUBCOMMANDS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("usage             ", Util.ANSI.Green) + "Iterates the billed usage");
    System.out.println("    " + Util.prefix("create            ", Util.ANSI.Green) + "Creates a new space");
    System.out.println("    " + Util.prefix("delete            ", Util.ANSI.Green) + "Deletes an empty space");
    System.out.println("    " + Util.prefix("deploy            ", Util.ANSI.Green) + "Deploy a plan to a space");
    System.out.println("    " + Util.prefix("download          ", Util.ANSI.Green) + "Download a space's plan");
    System.out.println("    " + Util.prefix("list              ", Util.ANSI.Green) + "List spaces available to your account");
    System.out.println("    " + Util.prefix("set-role          ", Util.ANSI.Green) + "Share/unshare a space with another developer");
    System.out.println("    " + Util.prefix("generate-key      ", Util.ANSI.Green) + "Generate a server-side key to use for storing secrets");
    System.out.println("    " + Util.prefix("encrypt-secret    ", Util.ANSI.Green) + "Encrypt a secret to store within code");
    System.out.println("    " + Util.prefix("set-rxhtml        ", Util.ANSI.Green) + "Set the frontend RxHTML forest");
    System.out.println("    " + Util.prefix("get-rxhtml        ", Util.ANSI.Green) + "Get the frontend RxHTML forest");
    System.out.println("    " + Util.prefix("reflect           ", Util.ANSI.Green) + "Reflect a space");
    System.out.println("    " + Util.prefix("help              ", Util.ANSI.Green) + "Displays this screen");
  }
}
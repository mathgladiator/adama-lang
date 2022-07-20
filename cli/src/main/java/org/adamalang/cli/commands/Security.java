/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands;

import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.MachineIdentity;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.keys.PublicPrivateKeyPartnership;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyPair;

public class Security {

  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      securityHelp();
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "generate-mk":
        generateMK(next);
        return;
      case "sanity-check":
        sanityCheck(next);
        return;
      case "generate-ca":
        generateCA(next);
        return;
      case "new-server":
        newServer(next);
        return;
      case "help":
        securityHelp();
        return;
    }
  }

  public static void securityHelp() {
    System.out.println(Util.prefix("Manage certificates and keys for production.", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama security", Util.ANSI.Green) + " " + Util.prefix("[SECURITYSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("SECURITYSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("generate-mk", Util.ANSI.Green) + "       Generates a master key");
    System.out.println("    " + Util.prefix("generate-ca", Util.ANSI.Green) + "       Generates a new CA");
    System.out.println("    " + Util.prefix("sanity-check", Util.ANSI.Green) + "      Sanity check that the JVM has the appropriate security support");
    System.out.println("    " + Util.prefix("new-server", Util.ANSI.Green) + "        Generates a new private key for a gRPC server");
  }

  private static void sanityCheck(String[] args) throws Exception {
    String master = MasterKey.generateMasterKey();
    if ("secret".equals(MasterKey.decrypt(master, MasterKey.encrypt(master, "secret")))) {
      System.out.println("+" + Util.prefix("master key support", Util.ANSI.Green));
    }
    KeyPair keyPair = PublicPrivateKeyPartnership.genKeyPair();
    KeyPair clone = PublicPrivateKeyPartnership.keyPairFrom(PublicPrivateKeyPartnership.publicKeyOf(keyPair), PublicPrivateKeyPartnership.privateKeyOf(keyPair));
    byte[] secret = PublicPrivateKeyPartnership.secretFrom(clone);
    if ("secret".equals(PublicPrivateKeyPartnership.decrypt(secret, PublicPrivateKeyPartnership.encrypt(secret, "secret")))) {
      System.out.println("+" + Util.prefix("public-private partnership intact", Util.ANSI.Green));
    }
  }

  private static void generateMK(String[] args) throws Exception {
    System.err.println("-------------------------------------------------------");
    System.err.println("Master Key:" + MasterKey.generateMasterKey());
    System.err.println("-------------------------------------------------------");
  }

  private static void generateCA(String[] args) throws Exception {
    String caPath = "./ca";
    for (int k = 0; k + 1 < args.length; k++) {
      if ("--ca".equals(args[k])) {
        caPath = args[k + 1];
      }
    }
    File p = new File(caPath);
    p.mkdirs();
    pipe("/usr/bin/openssl req -x509 -newkey rsa:4096 -days 365 -nodes -keyout " + caPath + "/ca-key.pem -out " + caPath + "/ca-cert.pem -subj  /C=US/ST=Kansas/L=KansasCity/O=Adama/OU=Adama/CN=adama.com/emailAddress=admin@adama.com");
  }

  public static void newServer(String[] args) throws Exception {
    String caPath = "./ca";
    String ip = null;
    String output = "./me.identity";
    for (int k = 0; k + 1 < args.length; k++) {
      if ("--ca".equals(args[k])) {
        caPath = args[k + 1];
      }
      if ("--ip".equals(args[k])) {
        ip = args[k + 1];
      }
      if ("--out".equals(args[k])) {
        output = args[k + 1];
      }
    }
    boolean error = false;
    if (caPath == null) {
      System.err.println("require --ca $path");
      error = true;
    } else if (!new File(caPath).exists() || !new File(caPath).isDirectory()) {
      System.err.println("the path within the '--ca $path' option doesn't exist and must be a directory");
      error = true;
    }
    if (ip == null) {
      System.err.println("require --ip $ip");
      error = true;
    }
    if (output == null) {
      System.err.println("require --out $file");
      error = true;
    }
    if (error) {
      return;
    }

    File caCert = new File(new File(caPath), "ca-cert.pem");
    File caKey = new File(new File(caPath), "ca-key.pem");

    Files.copy(caCert.toPath(), new File("/tmp/ca-cert.pem").toPath());
    Files.copy(caKey.toPath(), new File("/tmp/ca-key.pem").toPath());

    // TODO: sort out a more generic subject
    pipe("/usr/bin/openssl req -newkey rsa:4096 -nodes -keyout /tmp/machine-key.pem -out /tmp/machine-req.pem -subj /C=US/ST=Kansas/L=KansasCity/O=Adama/OU=Adama/CN=adama.com/emailAddress=admin@adama.com");
    Files.writeString(new File("/tmp/machine.cnf").toPath(), "subjectAltName=IP:" + ip);
    pipe("/usr/bin/openssl x509 -req -in /tmp/machine-req.pem -days 365 -CA /tmp/ca-cert.pem -CAkey /tmp/ca-key.pem -CAcreateserial -out /tmp/machine-cert.pem -extfile /tmp/machine.cnf");
    String json = MachineIdentity.convertToJson(ip, new File("/tmp/ca-cert.pem"), new File("/tmp/machine-cert.pem"), new File("/tmp/machine-key.pem"));

    new File("/tmp/ca-cert.pem").delete();
    new File("/tmp/ca-key.pem").delete();
    new File("/tmp/machine-req.pem").delete();
    new File("/tmp/machine-cert.pem").delete();
    new File("/tmp/machine-key.pem").delete();
    new File("/tmp/machine.cnf").delete();

    Files.writeString(new File(output).toPath(), json);
  }

  private static void pipe(String cmd) throws Exception {
    Process process = Runtime.getRuntime().exec(cmd);
    System.out.println(new String(process.getInputStream().readAllBytes()));
    System.err.println(new String(process.getErrorStream().readAllBytes()));
  }
}

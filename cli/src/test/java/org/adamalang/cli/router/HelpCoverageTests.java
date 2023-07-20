/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.router;

import org.adamalang.cli.Main;
import org.junit.Test;

public class HelpCoverageTests {
  @Test
  public void coverage() {
    Main.testMain(new String[]{"space", "--help"});
    Main.testMain(new String[]{"space", "create", "--help"});
    Main.testMain(new String[]{"space", "delete", "--help"});
    Main.testMain(new String[]{"space", "deploy", "--help"});
    Main.testMain(new String[]{"space", "developers", "--help"});
    Main.testMain(new String[]{"space", "encrypt-secret", "--help"});
    Main.testMain(new String[]{"space", "generate-key", "--help"});
    Main.testMain(new String[]{"space", "get", "--help"});
    Main.testMain(new String[]{"space", "get-rxhtml", "--help"});
    Main.testMain(new String[]{"space", "list", "--help"});
    Main.testMain(new String[]{"space", "reflect", "--help"});
    Main.testMain(new String[]{"space", "set-role", "--help"});
    Main.testMain(new String[]{"space", "set-rxhtml", "--help"});
    Main.testMain(new String[]{"space", "upload", "--help"});
    Main.testMain(new String[]{"space", "usage", "--help"});
    Main.testMain(new String[]{"authority", "--help"});
    Main.testMain(new String[]{"authority", "append-local", "--help"});
    Main.testMain(new String[]{"authority", "create", "--help"});
    Main.testMain(new String[]{"authority", "create-local", "--help"});
    Main.testMain(new String[]{"authority", "destroy", "--help"});
    Main.testMain(new String[]{"authority", "get", "--help"});
    Main.testMain(new String[]{"authority", "list", "--help"});
    Main.testMain(new String[]{"authority", "set", "--help"});
    Main.testMain(new String[]{"authority", "sign", "--help"});
    Main.testMain(new String[]{"account", "--help"});
    Main.testMain(new String[]{"account", "set-password", "--help"});
    Main.testMain(new String[]{"code", "--help"});
    Main.testMain(new String[]{"code", "bundle-plan", "--help"});
    Main.testMain(new String[]{"code", "compile-file", "--help"});
    Main.testMain(new String[]{"code", "lsp", "--help"});
    Main.testMain(new String[]{"code", "reflect-dump", "--help"});
    Main.testMain(new String[]{"code", "validate-plan", "--help"});
    Main.testMain(new String[]{"contrib", "--help"});
    Main.testMain(new String[]{"contrib", "bundle-js", "--help"});
    Main.testMain(new String[]{"contrib", "copyright", "--help"});
    Main.testMain(new String[]{"contrib", "make-api", "--help"});
    Main.testMain(new String[]{"contrib", "make-cli", "--help"});
    Main.testMain(new String[]{"contrib", "make-codec", "--help"});
    Main.testMain(new String[]{"contrib", "make-et", "--help"});
    Main.testMain(new String[]{"contrib", "tests-adama", "--help"});
    Main.testMain(new String[]{"contrib", "tests-rxhtml", "--help"});
    Main.testMain(new String[]{"database", "--help"});
    Main.testMain(new String[]{"database", "configure", "--help"});
    Main.testMain(new String[]{"database", "install", "--help"});
    Main.testMain(new String[]{"database", "migrate", "--help"});
    Main.testMain(new String[]{"document", "--help"});
    Main.testMain(new String[]{"document", "attach", "--help"});
    Main.testMain(new String[]{"document", "connect", "--help"});
    Main.testMain(new String[]{"document", "create", "--help"});
    Main.testMain(new String[]{"document", "delete", "--help"});
    Main.testMain(new String[]{"document", "list", "--help"});
    Main.testMain(new String[]{"domain", "--help"});
    Main.testMain(new String[]{"domain", "list", "--help"});
    Main.testMain(new String[]{"domain", "map", "--help"});
    Main.testMain(new String[]{"domain", "unmap", "--help"});
    Main.testMain(new String[]{"frontend", "--help"});
    Main.testMain(new String[]{"frontend", "dev-server", "--help"});
    Main.testMain(new String[]{"frontend", "edhtml", "--help"});
    Main.testMain(new String[]{"frontend", "rxhtml", "--help"});
    Main.testMain(new String[]{"services", "--help"});
    Main.testMain(new String[]{"services", "auto", "--help"});
    Main.testMain(new String[]{"services", "backend", "--help"});
    Main.testMain(new String[]{"services", "dashboards", "--help"});
    Main.testMain(new String[]{"services", "frontend", "--help"});
    Main.testMain(new String[]{"services", "overlord", "--help"});
    Main.testMain(new String[]{"services", "solo", "--help"});
    Main.testMain(new String[]{"dumpenv", "--help"});
    Main.testMain(new String[]{"init", "--help"});
    Main.testMain(new String[]{"kickstart", "--help"});
  }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.router;

import org.adamalang.cli.NewMain;
import org.junit.Test;

public class HelpCoverageTests {
  @Test
  public void coverage() {
    NewMain.testMain(new String[]{"space", "--help"});
    NewMain.testMain(new String[]{"space", "create", "--help"});
    NewMain.testMain(new String[]{"space", "delete", "--help"});
    NewMain.testMain(new String[]{"space", "deploy", "--help"});
    NewMain.testMain(new String[]{"space", "set-rxhtml", "--help"});
    NewMain.testMain(new String[]{"space", "get-rxhtml", "--help"});
    NewMain.testMain(new String[]{"space", "upload", "--help"});
    NewMain.testMain(new String[]{"space", "download", "--help"});
    NewMain.testMain(new String[]{"space", "list", "--help"});
    NewMain.testMain(new String[]{"space", "usage", "--help"});
    NewMain.testMain(new String[]{"space", "reflect", "--help"});
    NewMain.testMain(new String[]{"space", "set-role", "--help"});
    NewMain.testMain(new String[]{"space", "generate-key", "--help"});
    NewMain.testMain(new String[]{"space", "encrypt-secret", "--help"});
    NewMain.testMain(new String[]{"authority", "--help"});
    NewMain.testMain(new String[]{"authority", "create", "--help"});
    NewMain.testMain(new String[]{"authority", "set", "--help"});
    NewMain.testMain(new String[]{"authority", "get", "--help"});
    NewMain.testMain(new String[]{"authority", "destroy", "--help"});
    NewMain.testMain(new String[]{"authority", "list", "--help"});
    NewMain.testMain(new String[]{"authority", "create-local", "--help"});
    NewMain.testMain(new String[]{"authority", "append-local", "--help"});
    NewMain.testMain(new String[]{"authority", "sign", "--help"});
    NewMain.testMain(new String[]{"account", "--help"});
    NewMain.testMain(new String[]{"account", "set-password", "--help"});
    NewMain.testMain(new String[]{"account", "test-gtoken", "--help"});
    NewMain.testMain(new String[]{"aws", "--help"});
    NewMain.testMain(new String[]{"aws", "setup", "--help"});
    NewMain.testMain(new String[]{"aws", "test-email", "--help"});
    NewMain.testMain(new String[]{"aws", "test-asset-listing", "--help"});
    NewMain.testMain(new String[]{"aws", "test-enqueue", "--help"});
    NewMain.testMain(new String[]{"aws", "download-archive", "--help"});
    NewMain.testMain(new String[]{"aws", "memory-test", "--help"});
    NewMain.testMain(new String[]{"business", "--help"});
    NewMain.testMain(new String[]{"business", "add-balance", "--help"});
    NewMain.testMain(new String[]{"code", "--help"});
    NewMain.testMain(new String[]{"code", "lsp", "--help"});
    NewMain.testMain(new String[]{"code", "validate-plan", "--help"});
    NewMain.testMain(new String[]{"code", "bundle-plan", "--help"});
    NewMain.testMain(new String[]{"code", "compile-file", "--help"});
    NewMain.testMain(new String[]{"code", "reflect-dump", "--help"});
    NewMain.testMain(new String[]{"contrib", "--help"});
    NewMain.testMain(new String[]{"contrib", "tests-adama", "--help"});
    NewMain.testMain(new String[]{"contrib", "tests-rxhtml", "--help"});
    NewMain.testMain(new String[]{"contrib", "make-codec", "--help"});
    NewMain.testMain(new String[]{"contrib", "make-api", "--help"});
    NewMain.testMain(new String[]{"contrib", "bundle-js", "--help"});
    NewMain.testMain(new String[]{"contrib", "make-et", "--help"});
    NewMain.testMain(new String[]{"contrib", "copyright", "--help"});
    NewMain.testMain(new String[]{"database", "--help"});
    NewMain.testMain(new String[]{"database", "configure", "--help"});
    NewMain.testMain(new String[]{"database", "install", "--help"});
    NewMain.testMain(new String[]{"database", "migrate", "--help"});
    NewMain.testMain(new String[]{"debug", "--help"});
    NewMain.testMain(new String[]{"debug", "archive", "--help"});
    NewMain.testMain(new String[]{"document", "--help"});
    NewMain.testMain(new String[]{"document", "connect", "--help"});
    NewMain.testMain(new String[]{"document", "create", "--help"});
    NewMain.testMain(new String[]{"document", "delete", "--help"});
    NewMain.testMain(new String[]{"document", "list", "--help"});
    NewMain.testMain(new String[]{"document", "attach", "--help"});
    NewMain.testMain(new String[]{"domain", "--help"});
    NewMain.testMain(new String[]{"domain", "map", "--help"});
    NewMain.testMain(new String[]{"domain", "list", "--help"});
    NewMain.testMain(new String[]{"domain", "unmap", "--help"});
    NewMain.testMain(new String[]{"frontend", "--help"});
    NewMain.testMain(new String[]{"frontend", "rxhtml", "--help"});
    NewMain.testMain(new String[]{"frontend", "edhtml", "--help"});
    NewMain.testMain(new String[]{"frontend", "dev-server", "--help"});
    NewMain.testMain(new String[]{"services", "--help"});
    NewMain.testMain(new String[]{"services", "auto", "--help"});
    NewMain.testMain(new String[]{"services", "backend", "--help"});
    NewMain.testMain(new String[]{"services", "frontend", "--help"});
    NewMain.testMain(new String[]{"services", "overlord", "--help"});
    NewMain.testMain(new String[]{"services", "solo", "--help"});
    NewMain.testMain(new String[]{"services", "dashboards", "--help"});
    NewMain.testMain(new String[]{"init", "--help"});
    NewMain.testMain(new String[]{"stress", "--help"});
    NewMain.testMain(new String[]{"dumpenv", "--help"});
  }
}

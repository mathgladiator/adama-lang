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
    NewMain.testMain(new String[]{"init", "--help"});
  }
}
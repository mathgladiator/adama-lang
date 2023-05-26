package org.adamalang.cli.router;

import org.adamalang.cli.NewMain;
import org.junit.Test;

public class HelpCoverageTests {
  @Test
  public void coverage() {
    NewMain.main(new String[]{"space", "--help"});
    NewMain.main(new String[]{"space", "create", "--help"});
    NewMain.main(new String[]{"space", "delete", "--help"});
    NewMain.main(new String[]{"space", "deploy", "--help"});
    NewMain.main(new String[]{"space", "set-rxhtml", "--help"});
    NewMain.main(new String[]{"space", "upload", "--help"});
    NewMain.main(new String[]{"space", "download", "--help"});
    NewMain.main(new String[]{"space", "list", "--help"});
    NewMain.main(new String[]{"space", "usage", "--help"});
    NewMain.main(new String[]{"space", "reflect", "--help"});
    NewMain.main(new String[]{"space", "set-role", "--help"});
    NewMain.main(new String[]{"space", "generate-key", "--help"});
    NewMain.main(new String[]{"space", "encrypt-secret", "--help"});
    NewMain.main(new String[]{"authority", "--help"});
    NewMain.main(new String[]{"authority", "create", "--help"});
    NewMain.main(new String[]{"authority", "set", "--help"});
    NewMain.main(new String[]{"authority", "get", "--help"});
    NewMain.main(new String[]{"authority", "destroy", "--help"});
    NewMain.main(new String[]{"authority", "list", "--help"});
    NewMain.main(new String[]{"authority", "create-local", "--help"});
    NewMain.main(new String[]{"authority", "append-local", "--help"});
    NewMain.main(new String[]{"authority", "sign", "--help"});
    NewMain.main(new String[]{"init", "--help"});
  }
}
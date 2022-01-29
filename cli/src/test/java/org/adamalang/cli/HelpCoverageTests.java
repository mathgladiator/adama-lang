package org.adamalang.cli;

import org.junit.Test;

public class HelpCoverageTests {
  @Test
  public void coverage() throws Exception {
    Main.main(new String[] {});
    Main.main(new String[] {"authority"});
    Main.main(new String[] {"aws"});
    Main.main(new String[] {"code"});
    Main.main(new String[] {"contrib"});
    Main.main(new String[] {"database"});
    Main.main(new String[] {"documents"});
    Main.main(new String[] {"fleet"});
    Main.main(new String[] {"init"});
    Main.main(new String[] {"security"});
    Main.main(new String[] {"service"});
    Main.main(new String[] {"space"});
    Main.main(new String[] {"help"});

    Main.main(new String[] {"authority", "help"});
    Main.main(new String[] {"aws", "help"});
    Main.main(new String[] {"code", "help"});
    Main.main(new String[] {"contrib", "help"});
    Main.main(new String[] {"database", "help"});
    Main.main(new String[] {"documents", "help"});
    Main.main(new String[] {"fleet", "help"});
    Main.main(new String[] {"init", "help"});
    Main.main(new String[] {"security", "help"});
    Main.main(new String[] {"service", "help"});
    Main.main(new String[] {"space", "help"});
  }
}
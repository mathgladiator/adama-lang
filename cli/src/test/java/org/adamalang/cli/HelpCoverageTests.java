/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli;

import org.junit.Test;

public class HelpCoverageTests {
  @Test
  public void coverage() throws Exception {
    Main.main(new String[] {});
    Main.main(new String[] {"account"});
    Main.main(new String[] {"authority"});
    Main.main(new String[] {"aws"});
    Main.main(new String[] {"code"});
    Main.main(new String[] {"contrib"});
    Main.main(new String[] {"database"});
    Main.main(new String[] {"documents"});
    Main.main(new String[] {"fleet"});
    Main.main(new String[] {"security"});
    Main.main(new String[] {"service"});
    Main.main(new String[] {"space"});
    Main.main(new String[] {"help"});

    Main.main(new String[] {"account", "help"});
    Main.main(new String[] {"authority", "help"});
    Main.main(new String[] {"aws", "help"});
    Main.main(new String[] {"code", "help"});
    Main.main(new String[] {"contrib", "help"});
    Main.main(new String[] {"database", "help"});
    Main.main(new String[] {"documents", "help"});
    Main.main(new String[] {"fleet", "help"});
    Main.main(new String[] {"security", "help"});
    Main.main(new String[] {"service", "help"});
    Main.main(new String[] {"space", "help"});
  }
}

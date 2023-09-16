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
package org.adamalang.cli.devbox;

import org.junit.Assert;
import org.junit.Test;

public class CommandTests {
  @Test
  public void simple() {
    Command cmd = Command.parse("Help");
    Assert.assertEquals("help", cmd.command);
    Assert.assertEquals(0, cmd.args.length);
  }

  @Test
  public void singlearg() {
    Command cmd = Command.parse("Help me");
    Assert.assertEquals("help", cmd.command);
    Assert.assertEquals(1, cmd.args.length);
    Assert.assertEquals("me", cmd.args[0]);
  }

  @Test
  public void twoarg() {
    Command cmd = Command.parse("Help me now");
    Assert.assertEquals("help", cmd.command);
    Assert.assertEquals(2, cmd.args.length);
    Assert.assertEquals("me", cmd.args[0]);
    Assert.assertEquals("now", cmd.args[1]);
  }

  @Test
  public void strarg() {
    Command cmd = Command.parse("Help me \"ok please\" \"no really, I need help\"");
    Assert.assertEquals("help", cmd.command);
    Assert.assertEquals(3, cmd.args.length);
    Assert.assertEquals("me", cmd.args[0]);
    Assert.assertEquals("ok please", cmd.args[1]);
    Assert.assertEquals("no really, I need help", cmd.args[2]);
  }

  @Test
  public void escaping() {
    Command cmd = Command.parse("Help \"escape \\n \\r \\t \\\\ \\\"hi\\\"\"");
    Assert.assertEquals("help", cmd.command);
    Assert.assertEquals(1, cmd.args.length);
    Assert.assertEquals("escape \n \r \t \\ \"hi\"", cmd.args[0]);
  }
}

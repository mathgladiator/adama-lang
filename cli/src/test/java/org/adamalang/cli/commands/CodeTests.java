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

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

public class CodeTests {
  @Test
  public void help() throws Exception {
    Code.execute(null, new String[] { "help" });
    Code.execute(null, new String[] {  });
  }

  @Test
  public void validation_plan() throws Exception {
    File temp = File.createTempFile("ADAMATEST_", System.currentTimeMillis() + ".plan");
    Code.execute(null, new String[] { "validate-plan", "--plan", temp.getParentFile().getAbsolutePath()});
    Code.execute(null, new String[] { "validate-plan", "--plan", new File(temp.getParentFile(), "NOOOO.adama").getAbsolutePath()});

    Files.writeString(temp.toPath(), "");
    Code.execute(null, new String[] { "validate-plan", "--plan", temp.getAbsolutePath()});
    Files.writeString(temp.toPath(), "{}");
    Code.execute(null, new String[] { "validate-plan", "--plan", temp.getAbsolutePath()});
    Files.writeString(temp.toPath(), "{\"versions\":\"\",\"default\":{},\"plan\":{}}");
    Code.execute(null, new String[] { "validate-plan", "--plan", temp.getAbsolutePath()});
    Files.writeString(temp.toPath(), "{\"versions\":{\"x\":\"!\",\"k\":{}},\"default\":\"y\",\"plan\":[\"z\",{}]}");
    Code.execute(null, new String[] { "validate-plan", "--plan", temp.getAbsolutePath()});
    Files.writeString(temp.toPath(), "{\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\",\"plan\":[\"x\"]}");
    Code.execute(null, new String[] { "validate-plan", "--plan", temp.getAbsolutePath()});
  }

  @Test
  public void validation_file() throws Exception {
    File temp = File.createTempFile("ADAMATEST_", System.currentTimeMillis() + ".adama");
    Code.execute(null, new String[] { "compile-file", "--file", temp.getParentFile().getAbsolutePath()});
    Code.execute(null, new String[] { "compile-file", "--file", new File(temp.getParentFile(), "NOOOO.adama").getAbsolutePath()});

    Files.writeString(temp.toPath(), "");
    Code.execute(null, new String[] { "compile-file", "--file", temp.getAbsolutePath()});
    Files.writeString(temp.toPath(), "!");
    Code.execute(null, new String[] { "compile-file", "--file", temp.getAbsolutePath()});
    Files.writeString(temp.toPath(), "#check { bool x = 1; }");
    Code.execute(null, new String[] { "compile-file", "--file", temp.getAbsolutePath()});
  }
}

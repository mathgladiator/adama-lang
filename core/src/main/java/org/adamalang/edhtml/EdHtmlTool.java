/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.edhtml;

import org.adamalang.edhtml.phases.Generate;
import org.adamalang.edhtml.phases.Stamp;
import org.adamalang.edhtml.phases.Use;

import java.nio.file.Files;

public class EdHtmlTool {
  public static void main(String[] args) throws Exception {
    EdHtmlState state = new EdHtmlState(args);
    Files.writeString(state.output.toPath(), phases(state));
  }

  public static String phases(EdHtmlState state) throws Exception {
    Use.execute(state);
    Generate.execute(state);
    Stamp.execute(state);
    return state.finish();
  }
}

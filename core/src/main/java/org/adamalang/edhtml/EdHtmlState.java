/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.edhtml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;

/** This defines the core state machine for the EdHtml builder */
public class EdHtmlState {
  public final File base;
  public final File input;
  public final File output;
  public final File includes_path;
  public final File gen_path;
  public final Element document;
  public final StringBuilder output_rx;

  public EdHtmlState(String[] args) throws Exception {
    this.base = baseOf(args);
    if (!(this.base.exists() && this.base.isDirectory())) {
      throw new Exception("The --base path does not exist:" + base.toString());
    }
    this.input = childOf(this.base, "input", "build.ed.html", args);
    if (!this.input.exists() || this.input.isDirectory()) {
      throw new Exception("The --input path doesn't exist or is a directory than a file:" + input.toString());
    }
    this.output = childOf(this.base, "output", "output.rx.html", args);
    if (this.output.exists() && this.output.isDirectory()) {
      throw new Exception("The --output path is a directory rather than a file:" + output.toString());
    }
    this.includes_path = childOf(this.base, "includes", "includes", args);
    this.gen_path = childOf(this.base, "gen", "gen", args);
    if (!(this.includes_path.exists() && this.includes_path.isDirectory())) {
      throw new Exception("The --includes path does not exist:" + includes_path.toString());
    }
    if (!(this.gen_path.exists() && this.gen_path.isDirectory())) {
      throw new Exception("The --gen path does not exist:" + includes_path.toString());
    }
    this.output_rx = new StringBuilder();
    this.output_rx.append("<forest>\n");
    this.document = Jsoup.parse(input).getElementsByTag("build").first();
  }

  public String finish() {
    this.output_rx.append("</forest>\n");
    return this.output_rx.toString();
  }

  /** helper: find the base path */
  private static File baseOf(String[] args) {
    for (int k = 0; k + 1 < args.length; k++) {
      if ("--base".equals(args[k])) {
        return new File(args[k+1]);
      }
    }
    return new File(".");
  }

  /** helper: find a child of the base path and allow it to be overridden */
  private static File childOf(File base, String key, String defaultValue, String[] args) {
    for (int k = 0; k + 1 < args.length; k++) {
      if (("--" + key).equals(args[k])) {
        return new File(base, args[k+1]);
      }
    }
    return new File(base, defaultValue);
  }
}

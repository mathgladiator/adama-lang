/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

import org.adamalang.rxhtml.template.Shell;

/** result of executing RxHtml */
public class RxHtmlResult {
  public final String javascript;
  public final String style;
  public final Shell shell;

  public RxHtmlResult(String javascript, String style, Shell shell) {
    this.javascript = javascript;
    this.style = style;
    this.shell = shell;
  }

  @Override
  public String toString() {
    return "JavaScript:" + javascript.trim() + "\nStyle:" + style.trim() + "\nShell:" + shell.makeShell(this, false, 0);
  }
}

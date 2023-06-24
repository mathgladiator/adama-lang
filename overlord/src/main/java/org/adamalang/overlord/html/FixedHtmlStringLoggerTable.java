/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.overlord.html;

/** a fixed log that is shown via HTML */
public class FixedHtmlStringLoggerTable {

  private final String begin;
  private final String[] rows;
  private final String end;
  private int at;

  public FixedHtmlStringLoggerTable(int n, String... labels) {
    this.rows = new String[n];

    StringBuilder beginBuffer = new StringBuilder();
    beginBuffer.append("<table><tr>");
    for (String label : labels) {
      beginBuffer.append("<th>").append(label).append("</th>");
    }
    beginBuffer.append("</tr>\n");
    this.begin = beginBuffer.toString();
    for (int k = 0; k < n; k++) {
      rows[k] = null;
    }
    this.end = "</table>";
    at = 0;
  }

  public void row(String... vals) {
    StringBuilder rowBuffer = new StringBuilder();
    rowBuffer.append("<tr>");
    for (String val : vals) {
      rowBuffer.append("<td>").append(val).append("</td>");
    }
    rowBuffer.append("</tr>");
    rows[at] = rowBuffer.toString();
    at++;
    at %= rows.length;
  }

  public String toHtml(String title) {
    StringBuilder html = new StringBuilder();
    html.append("<html><head><title>").append(title).append("</title></head><body>\n");
    html.append("<h1>Activity</h1>");
    html.append(begin);
    for (int k = 0; k < rows.length; k++) {
      int j = (at + k) % rows.length;
      if (rows[j] != null) {
        html.append(rows[j]);
      }
    }
    html.append(end);
    html.append("</body></html>");
    return html.toString();
  }
}

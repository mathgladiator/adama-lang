/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.stdlib;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.common.template.Settings;
import org.adamalang.runtime.stdlib.intern.TemplateMultiLineEmailTemplateHtml;

public class LibTemplates {

  public static String multilineEmailWithButton(String title, String first_line, String second_line, String body, String button_url, String button, String final_line) {
    ObjectNode data = Json.newJsonObject();
    data.put("title", title);
    data.put("first_line", first_line);
    data.put("second_line", second_line);
    data.put("body", body);
    data.put("button_url", button_url);
    data.put("button", button);
    data.put("final_line", final_line);
    StringBuilder output = new StringBuilder();
    TemplateMultiLineEmailTemplateHtml.TEMPLATE.render(new Settings(), data, output);
    return output.toString();
  }
}

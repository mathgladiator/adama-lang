/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.template.tree;

import com.fasterxml.jackson.databind.JsonNode;
import org.adamalang.common.template.Settings;
import org.jsoup.nodes.Entities;

/** pull the variable into the document */
public class TVariable implements T {
  public final String variable;
  public final boolean unescape;

  public TVariable(String variable, boolean unescape) {
    this.variable = variable;
    this.unescape = unescape;
  }

  @Override
  public void render(Settings settings, JsonNode node, StringBuilder output) {
    JsonNode fetched = node.get(variable);
    if (fetched != null && fetched.isTextual()) {
      if (settings.html && ! unescape) {
        output.append(Entities.escape(fetched.textValue()));
      } else {
        output.append(fetched.textValue());
      }
    }
  }
}

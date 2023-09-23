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
package org.adamalang.rxhtml.atl.tree;

import org.adamalang.common.Escaping;
import org.adamalang.rxhtml.atl.Context;
import org.adamalang.rxhtml.typing.ViewScope;

import java.util.Collections;
import java.util.Map;

/** Raw Text */
public class Text implements Tree {
  public final String text;

  public Text(String text) {
    this.text = text;
  }

  @Override
  public Map<String, String> variables() {
    return Collections.emptyMap();
  }

  @Override
  public String debug() {
    return "TEXT(" + text + ")";
  }

  @Override
  public String js(Context context, String env) {
    String textToUse = text;
    if (context.is_class) {
      String trimmed = textToUse.trim();
      context.cssTrack(trimmed);
      // The reason we do this is to ensure developers don't try to make a new class via concatenation as that will break future optimizers.
      textToUse = " " + trimmed + " ";
    }
    return "\"" + new Escaping(textToUse).removeNewLines().keepSlashes().go() + "\"";
  }

  /** silly optimization for empty strings and classes */
  public boolean skip(Context context) {
    if (context.is_class) {
      return text.trim().length() == 0;
    }
    return false;
  }

  @Override
  public boolean hasAuto() {
    return false;
  }

  @Override
  public void writeTypes(ViewScope vs) {
  }
}

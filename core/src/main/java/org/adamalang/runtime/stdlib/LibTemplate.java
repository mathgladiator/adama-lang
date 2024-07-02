/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.stdlib;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.common.template.Settings;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.natives.NtTemplate;
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;

/** library for NtTemplate */
public class LibTemplate {

  private static final Settings HTML = new Settings(true);
  private static final Settings RAW = new Settings(false);

  @Extension
  public static @HiddenType(clazz = String.class) NtMaybe<String> html(NtTemplate template, NtDynamic inputs) {
    try {
      ObjectNode parsed = Json.parseJsonObject(inputs.json);
      StringBuilder sb = new StringBuilder();
      template.template.render(HTML, parsed, sb);
      return new NtMaybe<>(sb.toString());
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }

  @Extension
  public static @HiddenType(clazz = String.class) NtMaybe<String> raw(NtTemplate template, NtDynamic inputs) {
    try {
      ObjectNode parsed = Json.parseJsonObject(inputs.json);
      StringBuilder sb = new StringBuilder();
      template.template.render(RAW, parsed, sb);
      return new NtMaybe<>(sb.toString());
    } catch (Exception ex) {
      return new NtMaybe<>();
    }
  }
}

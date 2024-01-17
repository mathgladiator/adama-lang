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
package org.adamalang.rxhtml.typing;

import java.util.Objects;

/** a simple tuple to combine a template name that has been checked against a structure */
public class DedupeTemplateCheck implements Comparable<DedupeTemplateCheck> {
  public final String templateName;
  public final String structureName;
  public final String privacySet;

  public DedupeTemplateCheck(String templateName, String structureName, String privacySet) {
    this.templateName = templateName;
    this.structureName = structureName;
    this.privacySet = privacySet;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DedupeTemplateCheck that = (DedupeTemplateCheck) o;
    return Objects.equals(templateName, that.templateName) && Objects.equals(structureName, that.structureName) && Objects.equals(privacySet, that.privacySet);
  }

  @Override
  public int hashCode() {
    return Objects.hash(templateName, structureName, privacySet);
  }

  @Override
  public int compareTo(DedupeTemplateCheck o) {
    int delta = templateName.compareTo(o.templateName);
    if (delta == 0) {
      delta = structureName.compareTo(o.structureName);
    }
    if (delta == 0) {
      delta = privacySet.compareTo(o.privacySet);
    }
    return delta;
  }
}

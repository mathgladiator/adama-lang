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
package org.adamalang.translator.codegen;

import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.types.structures.StructureStorage;

public class CodeGenDocumentPolicyCache {
  public static void writeRecordDeltaClass(final StructureStorage storage, final StringBuilderWithTabs sb) {
    sb.append("public class DeltaPrivacyCache {").tabUp().writeNewline();
    for (String policy : storage.policies.keySet()) {
      sb.append("public final boolean ").append(policy).append(";").writeNewline();
    }
    int countdown = storage.policies.size();
    if (countdown == 0) {
      sb.append("public DeltaPrivacyCache(NtPrincipal __who) {}").tabDown().writeNewline();
      sb.append("}").writeNewline();
    } else {
      sb.append("public DeltaPrivacyCache(NtPrincipal __who) {").tabUp().writeNewline();
      for (String policy : storage.policies.keySet()) {
        sb.append("this.").append(policy).append("=__POLICY_").append(policy).append("(__who);");
        countdown--;
        if (countdown == 0) {
          sb.tabDown();
        }
        sb.writeNewline();
      }
      sb.append("}").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }

  }
}

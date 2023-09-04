/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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

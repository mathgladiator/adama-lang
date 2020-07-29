/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.privacy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.structures.FieldDefinition;
import org.adamalang.translator.tree.types.structures.StructureStorage;

/** a policy that refers to code within the record */
public class UseCustomPolicy extends Policy {
  public final Token customToken;
  private final HashSet<String> globals;
  public final ArrayList<String> policyToChecks;
  public final TokenizedItem<Token>[] policyToCheckTokens;

  public UseCustomPolicy(final Token customToken, final TokenizedItem<Token>[] policyToCheckTokens) {
    this.customToken = customToken;
    policyToChecks = new ArrayList();
    ingest(customToken);
    for (final TokenizedItem<Token> token : policyToCheckTokens) {
      policyToChecks.add(token.item.text);
      ingest(token.item);
      for (final Token afterToken : token.after) {
        ingest(afterToken);
      }
    }
    this.policyToCheckTokens = policyToCheckTokens;
    globals = new HashSet<>();
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(customToken);
    for (final TokenizedItem<Token> token : policyToCheckTokens) {
      token.emitBefore(yielder);
      yielder.accept(token.item);
      token.emitAfter(yielder);
    }
  }

  @Override
  public void typing(final Environment environment, final StructureStorage owningStructureStorage) {
    for (final String policyToCheck : policyToChecks) {
      var dcp = owningStructureStorage.policies.get(policyToCheck);
      if (dcp == null) {
        globals.add(policyToCheck);
        dcp = environment.document.root.storage.policies.get(policyToCheck);
        if (dcp == null) {
          environment.document.createError(this, String.format("Policy '%s' was not found", policyToCheck), "CustomPolicy");
        }
      }
    }
  }

  @Override
  public boolean writePrivacyCheckGuard(final StringBuilderWithTabs sb, final FieldDefinition field, final Environment environment) {
    sb.append("if (");
    var first = true;
    for (final String policyToCheck : policyToChecks) {
      if (first) {
        first = false;
      } else {
        sb.append(" && ");
      }
      if (!globals.contains(policyToCheck)) {
        sb.append("__item.");
      }
      sb.append("__POLICY_").append(policyToCheck).append("(__writer.who)");
    }
    sb.append(") {").tabUp().writeNewline();
    return true;
  }
}

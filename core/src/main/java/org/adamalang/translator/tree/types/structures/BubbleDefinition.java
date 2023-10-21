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
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.privacy.Guard;
import org.adamalang.translator.tree.types.TyType;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import java.util.function.Consumer;

public class BubbleDefinition extends StructureComponent {
  public final Token bubbleToken;
  public final Guard guard;
  public final Token equalsToken;
  public final Expression expression;
  public final Token nameToken;
  public final Token semicolonToken;
  public final LinkedHashSet<String> servicesToWatch;
  public final LinkedHashSet<String> variablesToWatch;
  public TyType expressionType;
  public final HashSet<String> globalPolicies;
  public TreeSet<String> viewerFields;

  public BubbleDefinition(final Token bubbleToken, Guard guard, final Token nameToken, final Token equalsToken, final Expression expression, final Token semicolonToken) {
    this.bubbleToken = bubbleToken;
    this.guard = guard;
    this.nameToken = nameToken;
    this.equalsToken = equalsToken;
    this.expression = expression;
    this.semicolonToken = semicolonToken;
    ingest(bubbleToken);
    ingest(semicolonToken);
    servicesToWatch = new LinkedHashSet<>();
    variablesToWatch = new LinkedHashSet<>();
    this.globalPolicies = new HashSet<>();
    this.viewerFields = new TreeSet<>();
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(bubbleToken);
    if (guard != null) {
      guard.emit(yielder);
    }
    yielder.accept(nameToken);
    yielder.accept(equalsToken);
    expression.emit(yielder);
    yielder.accept(semicolonToken);
  }

  public void typing(final Environment environment, StructureStorage owningStructureStorage) {
    Environment env = next(environment);
    if (!owningStructureStorage.root) {
      env = env.scopeRecord(owningStructureStorage.name.text);
    }
    expressionType = environment.rules.Resolve(expression.typing(env, null), false);
    if (guard != null) {
      for (TokenizedItem<String> policy : guard.policies) {
        var dcp = owningStructureStorage.policies.get(policy.item);
        if (dcp == null) {
          dcp = environment.document.root.storage.policies.get(policy.item);
          if (dcp == null) {
            environment.document.createError(this, String.format("Policy '%s' was not found for bubble guard", policy.item));
          } else {
            globalPolicies.add(policy.item);
          }
        } else {
          if (owningStructureStorage.root) {
            globalPolicies.add(policy.item);
          }
        }
      }
    }
  }

  private Environment next(Environment environment) {
    final var next = environment.scopeWithComputeContext(ComputeContext.Computation).scopeReactiveExpression().scopeAsBubble(viewerFields);
    return next;
  }

  public void writeSetup(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("public ").append(expressionType.getJavaConcreteType(environment)).append(" __COMPUTE_").append(nameToken.text).append("(NtPrincipal __who, RTx__ViewerType __viewer) {").tabUp().writeNewline();
    sb.append("return ");
    expression.writeJava(sb, next(environment));
    sb.append(";").tabDown().writeNewline().append("}").writeNewline();
  }

  public boolean writePrivacyCheckGuard(final StringBuilderWithTabs sb) {
    sb.append("if (");
    var first = true;
    for (final TokenizedItem<String> policyToCheck : guard.policies) {
      if (first) {
        first = false;
      } else {
        sb.append(" && ");
      }
      if (globalPolicies.contains(policyToCheck.item)) {
        sb.append("__policy_cache.").append(policyToCheck.item);
      } else {
        sb.append("__item.__POLICY_").append(policyToCheck.item).append("(__writer.who)");
      }
    }
    sb.append(") {").tabUp().writeNewline();
    return true;
  }
}

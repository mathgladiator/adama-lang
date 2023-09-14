/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.function.Consumer;

public class BubbleDefinition extends StructureComponent {
  public final Token bubbleToken;
  public final BubbleGuard guard;
  public final Token equalsToken;
  public final Expression expression;
  public final Token nameToken;
  public final Token semicolonToken;
  public final LinkedHashSet<String> servicesToWatch;
  public final LinkedHashSet<String> variablesToWatch;
  public TyType expressionType;
  public final HashSet<String> globalPolicies;

  public BubbleDefinition(final Token bubbleToken, BubbleGuard guard, final Token nameToken, final Token equalsToken, final Expression expression, final Token semicolonToken) {
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
    expressionType = environment.rules.Resolve(expression.typing(next(environment), null), false);
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
        }
      }
    }
  }

  private Environment next(Environment environment) {
    final var next = environment.scopeWithComputeContext(ComputeContext.Computation).scopeReactiveExpression().scopeAsBubble();
    return next;
  }

  public void writeSetup(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("public ").append(expressionType.getJavaConcreteType(environment)).append(" __COMPUTE_").append(nameToken.text).append("(NtPrincipal __who, RTx__ViewerType __viewer) {").tabUp().writeNewline();
    sb.append("return ");
    expression.writeJava(sb, next(environment));
    sb.append(";").tabDown().writeNewline().append("}").writeNewline();
  }
}

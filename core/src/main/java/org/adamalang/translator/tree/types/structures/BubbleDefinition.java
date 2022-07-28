/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.structures;

import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativePrincipal;

import java.util.LinkedHashSet;
import java.util.function.Consumer;

public class BubbleDefinition extends StructureComponent {
  public final Token bubbleToken;
  public final Token equalsToken;
  public final Expression expression;
  public final Token nameToken;
  public final Token semicolonToken;
  public final LinkedHashSet<String> servicesToWatch;
  public final LinkedHashSet<String> variablesToWatch;
  public TyType expressionType;

  public BubbleDefinition(final Token bubbleToken, final Token nameToken, final Token equalsToken, final Expression expression, final Token semicolonToken) {
    this.bubbleToken = bubbleToken;
    this.nameToken = nameToken;
    this.equalsToken = equalsToken;
    this.expression = expression;
    this.semicolonToken = semicolonToken;
    ingest(bubbleToken);
    ingest(semicolonToken);
    servicesToWatch = new LinkedHashSet<>();
    variablesToWatch = new LinkedHashSet<>();
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(bubbleToken);
    yielder.accept(nameToken);
    yielder.accept(equalsToken);
    expression.emit(yielder);
    yielder.accept(semicolonToken);
  }

  public void typing(final Environment environment) {
    expressionType = environment.rules.Resolve(expression.typing(next(environment), null), false);
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

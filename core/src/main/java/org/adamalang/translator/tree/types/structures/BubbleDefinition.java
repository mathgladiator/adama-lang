/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.types.structures;

import java.util.LinkedHashSet;
import java.util.function.Consumer;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeClient;

public class BubbleDefinition extends StructureComponent {
  public final Token bubbleToken;
  public final TyType clientType;
  public final Token clientVar;
  public final Token comma;
  public final Token viewerStateName;
  public final Token closeClient;
  public final Token equalsToken;
  public final Expression expression;
  public TyType expressionType;
  public final Token nameToken;
  public final Token openClient;
  public final Token semicolonToken;
  public final LinkedHashSet<String> variablesToWatch;

  public BubbleDefinition(final Token bubbleToken, final Token openClient, final Token clientVar, final Token comma, final Token viewerStateName, final Token closeClient, final Token nameToken, final Token equalsToken, final Expression expression, final Token semicolonToken) {
    this.bubbleToken = bubbleToken;
    this.openClient = openClient;
    this.clientVar = clientVar;
    this.comma = comma;
    this.viewerStateName = viewerStateName;
    this.closeClient = closeClient;
    this.nameToken = nameToken;
    this.equalsToken = equalsToken;
    this.expression = expression;
    this.semicolonToken = semicolonToken;
    clientType = new TyNativeClient(TypeBehavior.ReadOnlyNativeValue, null, clientVar);
    ingest(bubbleToken);
    ingest(semicolonToken);
    variablesToWatch = new LinkedHashSet<>();
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(bubbleToken);
    yielder.accept(openClient);
    yielder.accept(clientVar);
    if (comma != null) {
      yielder.accept(comma);
      yielder.accept(viewerStateName);
    }
    yielder.accept(closeClient);
    yielder.accept(nameToken);
    yielder.accept(equalsToken);
    expression.emit(yielder);
    yielder.accept(semicolonToken);
  }

  private Environment next(Environment environment) {
    final var next = environment.scopeWithComputeContext(ComputeContext.Computation).scopeReactiveExpression();
    next.define(clientVar.text, clientType, true, clientType);
    if (viewerStateName != null) {
      next.define(viewerStateName.text, environment.document.viewerType, true, this);
    }
    return next;
  }

  public void typing(final Environment environment) {
    expressionType = environment.rules.Resolve(expression.typing(next(environment), null), false);
  }

  public void writeSetup(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("public ").append(expressionType.getJavaConcreteType(environment)).append(" __COMPUTE_").append(nameToken.text).append("(NtClient ").append(clientVar.text).append(", RTx__ViewerType ").append(viewerStateName != null ? viewerStateName.text : "__viewerState").append(") {").tabUp().writeNewline();
    sb.append("return ");
    expression.writeJava(sb, next(environment));
    sb.append(";").tabDown().writeNewline().append("}").writeNewline();
  }
}

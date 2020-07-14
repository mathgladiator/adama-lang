/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.structures;

import java.util.LinkedHashSet;
import java.util.function.Consumer;
import org.adamalang.translator.env.ComputeContext;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.StringBuilderWithTabs;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeClient;
import org.adamalang.translator.tree.types.traits.details.DetailHasBridge;

public class BubbleDefinition extends StructureComponent {
  public final Token bubbleToken;
  public final TyType clientType;
  public final Token clientVar;
  public final Token closeClient;
  public final Token equalsToken;
  public final Expression expression;
  private TyType expressionType;
  public final Token nameToken;
  public final Token openClient;
  public final Token semicolonToken;
  public final LinkedHashSet<String> variablesToWatch;

  public BubbleDefinition(final Token bubbleToken, final Token openClient, final Token clientVar, final Token closeClient, final Token nameToken, final Token equalsToken, final Expression expression, final Token semicolonToken) {
    this.bubbleToken = bubbleToken;
    this.openClient = openClient;
    this.clientVar = clientVar;
    this.closeClient = closeClient;
    this.nameToken = nameToken;
    this.equalsToken = equalsToken;
    this.expression = expression;
    this.semicolonToken = semicolonToken;
    clientType = new TyNativeClient(clientVar);
    ingest(bubbleToken);
    ingest(semicolonToken);
    variablesToWatch = new LinkedHashSet<>();
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(bubbleToken);
    yielder.accept(openClient);
    yielder.accept(clientVar);
    yielder.accept(closeClient);
    yielder.accept(nameToken);
    yielder.accept(equalsToken);
    expression.emit(yielder);
    yielder.accept(semicolonToken);
  }

  public void typing(final Environment environment) {
    final var next = environment.scopeWithComputeContext(ComputeContext.Computation).scopeReactiveExpression();
    next.define(clientVar.text, clientType, true, clientType);
    expressionType = environment.rules.Resolve(expression.typing(next, null), false);
  }

  public void writePolicy(final StringBuilderWithTabs sb, final Environment environment) {
    if (expressionType instanceof DetailHasBridge) {
      final var bridge = ((DetailHasBridge) expressionType).getBridge(environment);
      sb.append(expressionType.getJavaBoxType(environment)).append(" __local_").append(nameToken.text).append(" = __COMPUTE_").append(nameToken.text).append("(__who);").writeNewline();
      sb.append("if (__local_").append(nameToken.text).append(" != null) {").tabUp().writeNewline();
      sb.append("__view.set(\"").append(nameToken.text).append("\", ").append(bridge).append(".toPrivateJsonNode(__who, __local_").append(nameToken.text).append("));").tabDown().writeNewline();
      sb.append("}").writeNewline();
    }
  }

  public void writeSetup(final StringBuilderWithTabs sb, final Environment environment) {
    sb.append("public ").append(expressionType.getJavaConcreteType(environment)).append(" __COMPUTE_").append(nameToken.text).append("(NtClient ").append(clientVar.text).append(") {").tabUp().writeNewline();
    sb.append("return ");
    expression.writeJava(sb, environment.scopeWithComputeContext(ComputeContext.Computation).define(clientVar.text, clientType, true, clientType));
    sb.append(";").tabDown().writeNewline().append("}").writeNewline();
  }
}

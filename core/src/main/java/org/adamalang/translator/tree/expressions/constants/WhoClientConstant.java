package org.adamalang.translator.tree.expressions.constants;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.expressions.Expression;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeClient;

import java.util.function.Consumer;

public class WhoClientConstant extends Expression {
  public final Token token;

  public WhoClientConstant(final Token token) {
    this.token = token;
    ingest(token);
  }

  @Override
  public void emit(final Consumer<Token> yielder) {
    yielder.accept(token);
  }

  @Override
  protected TyType typingInternal(final Environment environment, final TyType suggestion) {
    if (environment.state.isStatic() || environment.state.isMessageHandler() || environment.state.isPolicy()) {
      environment.mustBeComputeContext(this);
      return new TyNativeClient(TypeBehavior.ReadOnlyNativeValue, null, token).withPosition(this);
    } else {
      environment.document.createError(this, "@who is only available from static policies, document policies, privacy policies, and message handlers", "WHO");
      return null;
    }
  }

  @Override
  public void writeJava(final StringBuilder sb, final Environment environment) {
    sb.append("__who");
  }
}

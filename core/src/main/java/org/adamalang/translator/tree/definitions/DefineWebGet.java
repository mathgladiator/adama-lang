package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.statements.Block;

import java.util.function.Consumer;

/** defines a URI to get a web resource */
public class DefineWebGet extends Definition {

  public final Token webToken;
  public final Token getToken;
  public final WebUri uri;
  public final Block code;

  public DefineWebGet(Token webToken, Token getToken, WebUri uri, Block code) {
    this.webToken = webToken;
    this.getToken = getToken;
    this.uri = uri;
    this.code = code;
  }

  @Override
  public void emit(Consumer<Token> yielder) {
    yielder.accept(webToken);
    yielder.accept(getToken);
    uri.emit(yielder);
    code.emit(yielder);
  }

  @Override
  public void typing(Environment environment) {
    Environment env = environment.scopeAsReadOnlyBoundary();
    uri.extendInto(env);
    uri.typing(env);
    code.typing(env);
  }
}

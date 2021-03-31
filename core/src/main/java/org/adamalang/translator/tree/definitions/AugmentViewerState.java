package org.adamalang.translator.tree.definitions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;

import java.util.function.Consumer;

public class AugmentViewerState extends Definition {
    public final Token viewToken;
    public final TyType type;
    public final Token name;
    public final Token semicolon;

    public AugmentViewerState(Token viewToken, TyType type, Token name, Token semicolon) {
        this.viewToken = viewToken;
        this.type = type;
        this.name = name;
        this.semicolon = semicolon;
    }

    @Override
    public void emit(Consumer<Token> yielder) {
        yielder.accept(viewToken);
        type.emit(yielder);
        yielder.accept(name);
        yielder.accept(semicolon);
    }

    @Override
    public void typing(Environment environment) {
        type.typing(environment);
    }
}

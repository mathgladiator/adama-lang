package org.adamalang.cli.implementations;

import org.adamalang.cli.router.ArgumentType.*;
import org.adamalang.cli.router.AuthorityHandler;
import org.adamalang.cli.router.Output;
import org.adamalang.cli.router.RootHandler;
import org.adamalang.cli.router.SpaceHandler;

public class RootHandlerImpl implements RootHandler {

    @Override
    public SpaceHandler createSpaceHandler() {
        return new SpaceHandlerImpl();
    }

    @Override
    public AuthorityHandler createAuthorityHandler() {
        return new AuthorityHandlerImpl();
    }

    @Override
    public int init(Output.AnsiOutput output) {
        return 0;
    }
}

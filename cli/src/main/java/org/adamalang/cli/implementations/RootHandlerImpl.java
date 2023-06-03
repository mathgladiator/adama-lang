package org.adamalang.cli.implementations;

import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.AuthorityHandler;
import org.adamalang.cli.router.RootHandler;
import org.adamalang.cli.router.SpaceHandler;
import org.adamalang.cli.runtime.Output;

public class RootHandlerImpl implements RootHandler {
    @Override
    public SpaceHandler makeSpaceHandler() {
        return new SpaceHandlerImpl();
    }

    @Override
    public AuthorityHandler makeAuthorityHandler() {
        return new AuthorityHandlerImpl();
    }

    @Override
    public void init(Arguments.InitArgs args, Output.YesOrError output) throws Exception {

    }
}

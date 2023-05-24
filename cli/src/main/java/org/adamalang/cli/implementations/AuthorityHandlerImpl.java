package org.adamalang.cli.implementations;

import org.adamalang.cli.router.ArgumentType;
import org.adamalang.cli.router.AuthorityHandler;
import org.adamalang.cli.router.Output;

public class AuthorityHandlerImpl implements AuthorityHandler{

    @Override
    public int createAuthority(Output.AnsiOutput output) {
        return 0;
    }

    @Override
    public int setAuthority(ArgumentType.SetAuthorityArgs args, Output.AnsiOutput output) {
        return 0;
    }

    @Override
    public int getAuthority(ArgumentType.GetAuthorityArgs args, Output.AnsiOutput output) {
        return 0;
    }

    @Override
    public int destroyAuthority(ArgumentType.DestroyAuthorityArgs args, Output.AnsiOutput output) {
        return 0;
    }

    @Override
    public int listAuthority(Output.AnsiOutput output) {
        return 0;
    }

    @Override
    public int createLocalAuthority(ArgumentType.CreateLocalAuthorityArgs args, Output.AnsiOutput output) {
        return 0;
    }

    @Override
    public int appendLocalAuthority(ArgumentType.AppendLocalAuthorityArgs args, Output.AnsiOutput output) {
        return 0;
    }

    @Override
    public int signAuthority(ArgumentType.SignAuthorityArgs args, Output.AnsiOutput output) {
        return 0;
    }
}
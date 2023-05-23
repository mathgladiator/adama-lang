package org.adamalang.cli.implementations;

import org.adamalang.cli.router.ArgumentType;
import org.adamalang.cli.router.AuthorityHandler;

public class AuthorityHandlerImpl implements AuthorityHandler {
    @Override
    public int createAuthority(ArgumentType.CreateAuthorityArgs args, String output) {
        return 0;
    }

    @Override
    public int setAuthority(ArgumentType.SetAuthorityArgs args, String output) {
        return 0;
    }

    @Override
    public int getAuthority(ArgumentType.GetAuthorityArgs args, String output) {
        return 0;
    }

    @Override
    public int destroyAuthority(ArgumentType.DestroyAuthorityArgs args, String output) {
        return 0;
    }

    @Override
    public int listAuthority(ArgumentType.ListAuthorityArgs args, String output) {
        return 0;
    }

    @Override
    public int createLocalAuthority(ArgumentType.CreateLocalAuthorityArgs args, String output) {
        return 0;
    }

    @Override
    public int appendLocalAuthority(ArgumentType.AppendLocalAuthorityArgs args, String output) {
        return 0;
    }

    @Override
    public int signAuthority(ArgumentType.SignAuthorityArgs args, String output) {
        return 0;
    }
}

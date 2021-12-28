package org.adamalang.mysql.frontend;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;

import java.util.Locale;

public enum Role {
    None(0x00),
    Developer(0x01);

    public int role;

    private Role(int role) {
        this.role = role;
    }

    public static Role from(String x) throws ErrorCodeException {
        switch (x.toLowerCase(Locale.ROOT).trim()) {
            case "developer":
                return Developer;
            case "none":
                return None;
        }
        throw new ErrorCodeException(ErrorCodes.INVALID_ROLE);
    }
}

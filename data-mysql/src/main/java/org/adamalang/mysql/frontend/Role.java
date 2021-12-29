/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
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

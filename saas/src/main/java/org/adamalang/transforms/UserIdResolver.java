/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.transforms;

import org.adamalang.ErrorCodes;
import org.adamalang.extern.ExternNexus;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.frontend.Users;
import org.adamalang.common.Callback;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.AsyncTransform;

import java.util.concurrent.Executor;

public class UserIdResolver implements AsyncTransform<String, Integer> {
    private final Executor executor;
    private final DataBase dataBase;
    private final ExceptionLogger logger;

    public UserIdResolver(Executor executor, ExternNexus nexus) {
        this.executor = executor;
        this.dataBase = nexus.dataBase;
        this.logger = nexus.makeLogger(UserIdResolver.class);
    }
    @Override
    public void execute(String email, Callback<Integer> callback) {
        executor.execute(() -> {
            // TODO: validate the email (roughly, for quick rejects)
            // https://stackoverflow.com/questions/624581/what-is-the-best-java-email-address-validation-method
            try {
                callback.success(Users.getOrCreateUserId(dataBase, email));
            } catch (Exception ex) {
                callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.USERID_RESOLVE_UNKNOWN_EXCEPTION, ex, logger));
            }
        });
    }
}

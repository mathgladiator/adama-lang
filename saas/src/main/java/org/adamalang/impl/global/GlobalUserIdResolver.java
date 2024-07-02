/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.impl.global;

import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.frontend.Session;
import org.adamalang.frontend.global.GlobalExternNexus;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Users;
import org.adamalang.contracts.UserIdResolver;

public class GlobalUserIdResolver implements UserIdResolver {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(UserIdResolver.class);
  private final SimpleExecutor executor;
  private final DataBase dataBase;

  public GlobalUserIdResolver(SimpleExecutor executor, GlobalExternNexus nexus) {
    this.executor = executor;
    this.dataBase = nexus.database;
  }

  public void execute(Session session, String email, Callback<Integer> callback) {
    executor.execute(new NamedRunnable("resolving-user-id") {
      @Override
      public void execute() throws Exception {
        try {
          callback.success(Users.getUserId(dataBase, email));
        } catch (Exception ex) {
          callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.USERID_RESOLVE_UNKNOWN_EXCEPTION, ex, LOGGER));
        }
      }
    });
  }
}

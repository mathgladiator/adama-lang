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
package org.adamalang.mysql.impl;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.runtime.sys.domains.DomainFinder;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.sys.domains.Domain;
import org.adamalang.mysql.model.Domains;

/** find domains from the database */
public class GlobalDomainFinder implements DomainFinder {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(GlobalDomainFinder.class);
  private final DataBase dataBase;
  private final String masterKey;

  public GlobalDomainFinder(DataBase dataBase, String masterKey) {
    this.dataBase = dataBase;
    this.masterKey = masterKey;
  }

  @Override
  public void find(String domain, Callback<Domain> callback) {
    try {
      Domain result = Domains.get(dataBase, domain);
      if (result != null) {
        if (result.certificate != null) {
          String cert = MasterKey.decrypt(masterKey, result.certificate);
          result = new Domain(result.domain, result.owner, result.space, result.key, result.forwardTo, result.routeKey, cert, result.updated, result.timestamp, result.configured);
        }
      }
      callback.success(result);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.DOMAIN_LOOKUP_FAILURE, ex, EXLOGGER));
    }
  }
}

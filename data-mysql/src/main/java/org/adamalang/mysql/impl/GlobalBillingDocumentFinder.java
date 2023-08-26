/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.impl;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.SpaceInfo;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.metering.BillingDocumentFinder;

public class GlobalBillingDocumentFinder implements BillingDocumentFinder  {
  private final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(GlobalBillingDocumentFinder.class);
  private final DataBase database;

  public GlobalBillingDocumentFinder(DataBase database) {
    this.database = database;
  }

  @Override
  public void find(String space, Callback<Key> callback) {
    try {
      SpaceInfo spaceInfo = Spaces.getSpaceInfo(database, space);
      callback.success(new Key("billing", "" + spaceInfo.owner));
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(0, ex, EXLOGGER));
    }
  }
}

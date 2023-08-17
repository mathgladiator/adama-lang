/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.impl;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.SpaceInfo;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.runtime.sys.web.rxhtml.LiveSiteRxHtmlResult;
import org.adamalang.runtime.sys.web.rxhtml.RxHtmlFetcher;
import org.adamalang.rxhtml.RxHtmlResult;
import org.adamalang.rxhtml.RxHtmlTool;
import org.adamalang.rxhtml.template.config.ShellConfig;

/** fetch an RxHTML document from a space */
public class GlobalRxHtmlFetcher implements RxHtmlFetcher {
  private final static ExceptionLogger EXLOGGER = ExceptionLogger.FOR(GlobalRxHtmlFetcher.class);
  private final DataBase database;

  public GlobalRxHtmlFetcher(DataBase database) {
    this.database = database;
  }

  @Override
  public void fetch(String space, Callback<LiveSiteRxHtmlResult> callback) {
    try {
      SpaceInfo spaceInfo = Spaces.getSpaceInfo(database, space);
      String rxhtml = Spaces.getRxHtml(database, spaceInfo.id);
      if (rxhtml != null) {
        RxHtmlResult rxhtmlResult = RxHtmlTool.convertStringToTemplateForest(rxhtml, ShellConfig.start().end());
        String html = rxhtmlResult.shell.makeShell(rxhtmlResult);
        callback.success(new LiveSiteRxHtmlResult(html, rxhtmlResult.paths));
      } else {
        callback.success(null);
      }
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.FRONTEND_FAILED_RXHTML_LOOKUP, ex, EXLOGGER));
    }
  }
}

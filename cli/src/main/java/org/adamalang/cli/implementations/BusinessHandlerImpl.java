/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.implementations;

import org.adamalang.cli.Util;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.BusinessHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.model.Users;

public class BusinessHandlerImpl implements BusinessHandler {
    @Override
    public void addBalance(Arguments.BusinessAddBalanceArgs args, Output.YesOrError output) throws Exception {
        int change = Integer.parseInt(args.pennies);
        DataBase db = new DataBase(new DataBaseConfig(new ConfigObject(args.config.read())), new DataBaseMetrics(new NoOpMetricsFactory()));
        int userId = Users.getOrCreateUserId(db, args.email);
        System.out.println("Balance Before: " + Util.prefix("" + Users.getBalance(db, userId), Util.ANSI.Green));
        Users.addToBalance(db, userId, change);
        System.out.println("Balance After: " + Util.prefix("" + Users.getBalance(db, userId), Util.ANSI.Green));
    }
}

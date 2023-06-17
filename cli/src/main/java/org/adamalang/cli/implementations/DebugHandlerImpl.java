/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.implementations;

import org.adamalang.caravan.events.AssetWalker;
import org.adamalang.caravan.events.RestoreDebuggerStdErr;
import org.adamalang.caravan.events.RestoreLoader;
import org.adamalang.cli.Util;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.DebugHandler;
import org.adamalang.cli.runtime.Output;

import java.io.File;
import java.util.ArrayList;

public class DebugHandlerImpl implements DebugHandler {
    @Override
    public void archive(Arguments.DebugArchiveArgs args, Output.YesOrError output) throws Exception {
        ArrayList<byte[]> writes = RestoreLoader.load(new File("archive/" + args.space + "/" + args.archive));
        System.err.println("Restore Log");
        RestoreDebuggerStdErr.print(writes);
        System.err.println("Live Asset Ids:");
        for (String id : AssetWalker.idsOf(writes)) {
            System.err.println(id);
        }
    }
}

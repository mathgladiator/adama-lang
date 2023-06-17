/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.implementations;

import org.adamalang.apikit.Tool;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.ContribHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.DefaultCopyright;

import java.io.File;
import java.nio.file.Files;

public class ContribHandlerImpl implements ContribHandler {
    @Override
    public void testsAdama(Arguments.ContribTestsAdamaArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void testsRxhtml(Arguments.ContribTestsRxhtmlArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void makeCodec(Arguments.ContribMakeCodecArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void makeApi(Arguments.ContribMakeApiArgs args, Output.YesOrError output) throws Exception {
        Tool.build("saas/api.xml", new File("."));
    }

    @Override
    public void bundleJs(Arguments.ContribBundleJsArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void makeEt(Arguments.ContribMakeEtArgs args, Output.YesOrError output) throws Exception {

    }

    private static void scan(File root) throws Exception {
        for (File f : root.listFiles()) {
            if (f.isDirectory()) {
                scan(f);
            } else {
                if (f.getName().endsWith(".java")) {
                    String code = Files.readString(f.toPath());
                    int start = code.indexOf("/*");
                    int end = code.indexOf("*/");
                    String newCode = null;
                    if (start >= 0 && start <= 5 && end > start) {
                        newCode = DefaultCopyright.COPYRIGHT_FILE_PREFIX + code.substring(end + 2).trim() + "\n";
                    } else {
                        newCode = DefaultCopyright.COPYRIGHT_FILE_PREFIX + code.trim() + "\n";
                    }
                    if (!code.equals(newCode)) {
                        Files.writeString(f.toPath(), newCode);
                    }
                }
            }
        }
    }

    @Override
    public void copyright(Arguments.ContribCopyrightArgs args, Output.YesOrError output) throws Exception {
        scan(new File("."));
    }
}

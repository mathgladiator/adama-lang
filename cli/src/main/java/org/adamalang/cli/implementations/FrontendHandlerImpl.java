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
import org.adamalang.cli.commands.frontend.FrontendDeveloperServer;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.FrontendHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.rxhtml.RxHtmlTool;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class FrontendHandlerImpl implements FrontendHandler {

    private static void aggregateFiles(File file, ArrayList<File> files) {
        for (File child : file.listFiles()) {
            if (child.isDirectory()) {
                aggregateFiles(child, files);
            } else if (child.getName().endsWith(".rx.html")) {
                files.add(child);
            }
        }
    }

    private static ArrayList<File> convertArgsToFileList(String[] args) {
        ArrayList<File> files = new ArrayList<>();
        for (String arg : args) {
            if (!arg.startsWith("-")) {
                File file = new File(arg);
                if (!file.exists()) {
                    continue;
                }
                if (file.isDirectory()) {
                    aggregateFiles(file, files);
                } else if (file.getName().endsWith(".rx.html")) {
                    files.add(file);
                }
            }
        }
        return files;
    }
    @Override
    public void rxhtml(Arguments.FrontendRxhtmlArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void edhtml(Arguments.FrontendEdhtmlArgs args, Output.YesOrError output) throws Exception {

    }

    @Override
    public void devServer(Arguments.FrontendDevServerArgs args, Output.YesOrError output) throws Exception {
        FrontendDeveloperServer.go(args.config, new String[] {});
    }
}

/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.devbox.*;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.FrontendHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.adamalang.edhtml.EdHtmlState;
import org.adamalang.edhtml.phases.Generate;
import org.adamalang.edhtml.phases.Stamp;
import org.adamalang.edhtml.phases.Use;
import org.adamalang.rxhtml.RxHtmlResult;
import org.adamalang.rxhtml.RxHtmlTool;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.adamalang.web.service.WebConfig;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

    @Override
    public void rxhtml(Arguments.FrontendRxhtmlArgs args, Output.YesOrError output) throws Exception {
        ArrayList<File> files = new ArrayList<>();
        aggregateFiles(new File(args.input), files);
        Files.writeString(new File(args.output).toPath(), RxHtmlTool.convertFilesToTemplateForest(files, new ArrayList<>(), ShellConfig.start().withFeedback((element, warning) -> System.err.println(warning)).end()).javascript);
    }

    @Override
    public void make200(Arguments.FrontendMake200Args args, Output.YesOrError output) throws Exception {
        ArrayList<File> files = new ArrayList<>();
        aggregateFiles(new File(args.rxhtmlPath), files);
        RxHtmlResult updated = RxHtmlTool.convertFilesToTemplateForest(files, new ArrayList<>(), ShellConfig.start().withFeedback((element, warning) -> System.err.println(warning)).end());
        Files.writeString(new File(args.output).toPath(), updated.shell.makeShell(updated));
    }

    @Override
    public void edhtml(Arguments.FrontendEdhtmlArgs args, Output.YesOrError output) throws Exception {
        EdHtmlState state = new EdHtmlState(args.base, args.input, args.output, args.includes, args.gen, "true".equals(args.skipuse));
        if (!state.skip_use) {
            Use.execute(state);
        }
        Generate.execute(state);
        Stamp.execute(state);
        Files.writeString(state.output.toPath(), state.finish());
    }

    @Override
    public void devServer(Arguments.FrontendDevServerArgs args, Output.YesOrError output) throws Exception {
        DevBoxStart.start(args);
    }

    @Override
    public void setLocalLibadamaPath(Arguments.FrontendSetLocalLibadamaPathArgs args, Output.YesOrError output) throws Exception {
        args.config.manipulate((node) -> {
            node.put("local-libadama-path-default", args.localLibadamaPath);
        });
        output.out();
    }
}

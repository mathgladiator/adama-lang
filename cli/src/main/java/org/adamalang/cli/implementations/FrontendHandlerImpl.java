/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.implementations;

import org.adamalang.cli.devbox.DevBoxServiceBase;
import org.adamalang.cli.devbox.RxHTMLScanner;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.FrontendHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.ConfigObject;
import org.adamalang.edhtml.EdHtmlState;
import org.adamalang.edhtml.phases.Generate;
import org.adamalang.edhtml.phases.Stamp;
import org.adamalang.edhtml.phases.Use;
import org.adamalang.rxhtml.RxHtmlTool;
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
        Files.writeString(new File(args.output).toPath(), RxHtmlTool.convertFilesToTemplateForest(files, new ArrayList<>(), (element, warning) -> System.err.println(warning)).javascript);
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
        AtomicBoolean alive = new AtomicBoolean(true);
        AtomicReference<RxHTMLScanner.RxHTMLBundle> bundle = new AtomicReference<>();
        try (RxHTMLScanner scanner = new RxHTMLScanner(alive, new File(args.rxhtmlPath), (b) -> bundle.set(b))) {
            WebConfig webConfig = new WebConfig(new ConfigObject(args.config.get_or_create_child("web")));
            DevBoxServiceBase base = new DevBoxServiceBase(webConfig, bundle, new File(args.assetPath));
            base.start();
            // TODO: throw the above into a thread, and then work with console IO here (FUN PART)
        }
    }
}

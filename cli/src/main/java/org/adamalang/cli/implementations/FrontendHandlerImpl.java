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
import org.adamalang.cli.devbox.DevBoxAdamaMicroVerse;
import org.adamalang.cli.devbox.DevBoxServiceBase;
import org.adamalang.cli.devbox.RxHTMLScanner;
import org.adamalang.cli.interactive.TerminalIO;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.FrontendHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import org.adamalang.edhtml.EdHtmlState;
import org.adamalang.edhtml.phases.Generate;
import org.adamalang.edhtml.phases.Stamp;
import org.adamalang.edhtml.phases.Use;
import org.adamalang.rxhtml.RxHtmlTool;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.adamalang.web.service.WebConfig;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.WatchService;
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
        String localLibAdamaJSPath = "".equals(args.localLibadamaPath) ? null : args.localLibadamaPath;
        File localLibAdamaJSFile = null;
        if (localLibAdamaJSPath != null) {
            localLibAdamaJSFile = new File(localLibAdamaJSPath);
            if (!(localLibAdamaJSFile.exists() && localLibAdamaJSFile.isDirectory())) {
                throw new Exception("--local-libadama-path was provided but the directory doesn't exist (or is a file)");
            }
        }
        TerminalIO terminal = new TerminalIO();
        DevBoxAdamaMicroVerse verse = null;
        if (args.microverse != null) {
            File microverseDef = new File(args.microverse);
            if (microverseDef.exists() && microverseDef.isFile()) {
                ObjectNode defn = Json.parseJsonObject(Files.readString(microverseDef.toPath()));
                verse = DevBoxAdamaMicroVerse.load(alive, terminal, defn);
                if (verse == null) {
                    terminal.notice("microverse: '" + args.microverse + "' failed, using production");
                }
            } else {
                terminal.notice("microverse: '" + args.microverse + "' is not present, using production");
            }
        }
        AtomicReference<RxHTMLScanner.RxHTMLBundle> bundle = new AtomicReference<>();
        try (RxHTMLScanner scanner = new RxHTMLScanner(alive, terminal, new File(args.rxhtmlPath), localLibAdamaJSPath != null, (b) -> bundle.set(b))) {
            WebConfig webConfig = new WebConfig(new ConfigObject(args.config.get_or_create_child("web")));
            terminal.notice("Starting Webserver");
            DevBoxServiceBase base = new DevBoxServiceBase(terminal, webConfig, bundle, new File(args.assetPath), localLibAdamaJSFile, verse);
            Thread webServerThread = base.start();
            while (alive.get()) {
                String ln = terminal.readline().trim();
                if ("kill".equalsIgnoreCase(ln) || "exit".equalsIgnoreCase(ln) || "quit".equalsIgnoreCase(ln) || "q".equalsIgnoreCase(ln)) {
                    terminal.notice("Lowering alive");
                    alive.set(false);
                    webServerThread.interrupt();
                    if (verse != null) {
                        verse.shutdown();
                    }
                }
            }
        }
    }
}

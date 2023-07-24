/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.devbox;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.web.UriMatcher;
import org.adamalang.rxhtml.template.config.Feedback;
import org.adamalang.rxhtml.RxHtmlResult;
import org.adamalang.rxhtml.RxHtmlTool;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/** this class will scan a directory for changes to .rx.html sources */
public class RxHTMLScanner implements AutoCloseable {
  private final AtomicBoolean alive;
  private final TerminalIO io;
  private final File scanRoot;
  private final boolean useLocalAdamaJavascript;
  private final Consumer<RxHTMLBundle> onBuilt;
  private final WatchService service;
  private final HashMap<String, WatchKey> watchKeyCache;
  private final Thread scanner;
  private final SimpleExecutor executor;
  private final AtomicBoolean scheduled;
  private final AtomicBoolean again;

  public RxHTMLScanner(AtomicBoolean alive, TerminalIO io, File scanRoot, boolean useLocalAdamaJavascript, Consumer<RxHTMLBundle> onBuilt) throws Exception {
    this.alive = alive;
    this.io = io;
    this.scanRoot = scanRoot;
    this.useLocalAdamaJavascript = useLocalAdamaJavascript;
    this.onBuilt = onBuilt;
    this.service = FileSystems.getDefault().newWatchService();
    this.watchKeyCache = new HashMap<>();
    sync(scanRoot);
    this.executor = SimpleExecutor.create("build");
    this.scheduled = new AtomicBoolean(false);
    this.again = new AtomicBoolean(false);
    this.scanner = new Thread(() -> {
      try {
        rebuild();
        while (alive.get()) {
          poll();
        }
      } catch (Exception ex) {
        if (!(ex instanceof InterruptedException)) {
          ex.printStackTrace();
        }
        alive.set(false);
      }
    });
    this.scanner.start();
  }

  @Override
  public void close() throws Exception {
    alive.set(false);
    scanner.interrupt();
  }

  /** sync the watcher with directories as they are mutated; cache them */
  private void sync(File root) throws Exception {
    String path = root.getPath();
    if (!watchKeyCache.containsKey(path)) {
      WatchKey rootWK = root.toPath().register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
      watchKeyCache.put(path, rootWK);
    }
    for (File child : root.listFiles()) {
      if (child.isDirectory()) {
        sync(child);
      }
    }
  }

  private void poll() throws Exception {
    boolean rescanRx = false;
    boolean rescanDir = false;
    WatchKey wk = service.take();
    for (WatchEvent<?> event : wk.pollEvents()) {
      final Path changed = (Path) event.context();
      String filename = changed.toFile().getName();
      if (changed.toFile().isDirectory()) {
        rescanDir = true;
      }
      if (filename.contains(".rx.html")) {
        rescanRx = true;
      }
      if (rescanDir) {
        sync(scanRoot);
      }
      if (rescanRx) {
        rebuild();
      }
      wk.reset();
    }
  }

  public class RxHTMLBundle {
    public final ArrayList<UriMatcher> matchers;
    public final String shell;
    public final String forestJavaScript;
    public final String forestStyle;

    public RxHTMLBundle(ArrayList<UriMatcher> matchers, String shell, String forestJavaScript, String forestStyle) {
      this.matchers = matchers;
      this.shell = shell;
      this.forestJavaScript = forestJavaScript;
      this.forestStyle = forestStyle;
    }
  }

  private void rebuild() throws Exception {
    if (scheduled.compareAndSet(false, true)) {
      executor.schedule(new NamedRunnable("rebuild") {
        @Override
        public void execute() throws Exception {
          try {
            do {
              again.set(false);
              io.notice("RxHTML rebuilt");
              ArrayList<UriMatcher> matchers = new ArrayList<>();
              Feedback feedback = new Feedback() {
                @Override
                public void warn(Element element, String warning) {
                  io.notice("Element Warning:" + warning);
                }
              };
              RxHtmlResult updated = RxHtmlTool.convertFilesToTemplateForest(rxhtml(scanRoot), matchers, ShellConfig.start().withFeedback(feedback).withUseLocalAdamaJavascript(useLocalAdamaJavascript).end());
              onBuilt.accept(new RxHTMLBundle(matchers, updated.shell.makeShell(updated), updated.javascript, updated.style));
            } while (again.get());
          } finally {
            scheduled.set(false);
          }
        }
      }, 100);
    } else {
      again.set(true);
    }
  }


  /** rebuild the files that are .rx.html */
  private static void scanRxHtml(File root, ArrayList<File> files) {
    for (File file : root.listFiles()) {
      if (file.isDirectory()) {
        scanRxHtml(file, files);
      }
      if (file.getName().endsWith(".rx.html")) {
        files.add(file);
      }
    }
  }

  private static ArrayList<File> rxhtml(File root) {
    ArrayList<File> files = new ArrayList<>();
    scanRxHtml(root, files);
    return files;
  }
}

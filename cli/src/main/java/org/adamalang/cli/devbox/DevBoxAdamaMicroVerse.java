/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.devbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.caravan.CaravanDataService;
import org.adamalang.cli.implementations.CodeHandlerImpl;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.deploy.Linter;
import org.adamalang.runtime.sys.CoreService;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** a microverse is a local cosmos of an Adama machine that outlines everything needed to run Adama locally without a DB */
public class DevBoxAdamaMicroVerse {
  private final TerminalIO io;
  private final AtomicBoolean alive;
  public final DevCoreServiceFactory factory;
  public final CaravanDataService dataService;
  public final CoreService service;
  public final ArrayList<LocalSpaceDefn> spaces;
  private final WatchService watchService;
  private final Thread scanner;
  public final Key domainKeyToUse;

  public static class LocalSpaceDefn {
    private final WatchService watchService;
    public final String spaceName;
    public final String mainFile;
    public final String includePath;
    private HashMap<String, WatchKey> watchKeyCache;
    private final DeploymentFactoryBase base;
    public String lastDeployedPlan;
    public String lastReflection;

    public LocalSpaceDefn(WatchService watchService, String spaceName, String mainFile, String includePath, DeploymentFactoryBase base) {
      this.watchService = watchService;
      this.spaceName = spaceName;
      this.mainFile = mainFile;
      this.includePath = includePath;
      this.watchKeyCache = new HashMap<>();
      this.base = base;
      this.lastDeployedPlan = "";
    }

    private File scan(File f) throws Exception {
      if (f.isDirectory()) {
        if (!watchKeyCache.containsKey(f.getPath())) {
          WatchKey rootWK = f.toPath().register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
          watchKeyCache.put(f.getPath(), rootWK);
        }
      }
      return f;
    }

    public HashMap<String, String> getImports(String imports) throws Exception {
      HashMap<String, String> map = new HashMap<>();
      File fileImports = scan(new File(imports));
      if (fileImports.exists() && fileImports.isDirectory()) {
        for (File f : fileImports.listFiles((dir, name) -> name.endsWith(".adama"))) {
          String name = f.getName().substring(0, f.getName().length() - 6);
          map.put(name, Files.readString(f.toPath()));
        }
      }
      return map;
    }

    public String bundle() throws Exception {
      ObjectNode plan = Json.newJsonObject();
      ObjectNode version = plan.putObject("versions").putObject("file");
      version.put("main", Files.readString(new File(mainFile).toPath()));
      scan(new File("."));
      ObjectNode includes = version.putObject("includes");
      if (includePath != null) {
        for (Map.Entry<String, String> entry : getImports(includePath).entrySet()) {
          includes.put(entry.getKey(), entry.getValue());
        }
      }
      plan.put("default", "file");
      plan.putArray("plan");
      return plan.toString();
    }
  }

  private void rebuild() {
      for (LocalSpaceDefn defn : spaces) {
        try {
          String plan = defn.bundle();
          if (!defn.lastDeployedPlan.equals(plan)) {
            long start = System.currentTimeMillis();
            io.notice("adama|validating: " + defn.spaceName);
            String newReflection = CodeHandlerImpl.sharedValidatePlanGetLastReflection(plan, (ln) -> io.error(ln));
            if (newReflection != null) {
              if (defn.lastReflection != null) {
                List<String> issues = Linter.compare(defn.lastReflection, newReflection);
                if (issues.size() == 0) {
                  io.notice("adama|no lint issues: " + defn.spaceName);
                } else {
                  io.notice("adama|" + issues.size() + " lint issues: " + defn.spaceName);
                }
                for (String issue : issues) {
                  io.notice("adama|lint-issue[" + defn.spaceName +"] := " + issue);
                }
              }
              io.notice("adama|deploying: " + defn.spaceName);
              defn.base.deploy(defn.spaceName, new DeploymentPlan(plan, (t, ec) -> {
                io.error("adama|deployment-issue[Code-" + ec + "]: " + t.getMessage());
              }), new TreeMap<>());
              CountDownLatch awaitDeployment = new CountDownLatch(1);
              service.deploy(new DeploymentMonitor() {
                 int documentsChanged;
                 @Override
                 public void bumpDocument(boolean changed) {
                   if (changed) {
                     documentsChanged++;
                   }
                 }

                 @Override
                 public void witnessException(ErrorCodeException ex) {
                   io.error("adama|deploy-exception:" + ex.getMessage());
                 }

                 @Override
                 public void finished(int ms) {
                   io.notice("adama:deployment-finished: " + documentsChanged + " changed; time=" + ms + "ms");
                   awaitDeployment.countDown();
                 }
               });
              defn.lastReflection = newReflection;
              defn.lastDeployedPlan = plan;
              awaitDeployment.await(30000, TimeUnit.MILLISECONDS);
              io.notice("adama|deployed: " + defn.spaceName + "; took " + (System.currentTimeMillis() - start) + "ms");
            } else {
              io.error("adama|failure: " + defn.spaceName);
            }
          }
        } catch (Exception ex) {
          io.error("adama|failed-bundling: " + defn.spaceName + "; reason=" + ex.getMessage());
        }
      }
  }

  private DevBoxAdamaMicroVerse(WatchService watchService, TerminalIO io, AtomicBoolean alive, DevCoreServiceFactory factory, ArrayList<LocalSpaceDefn> spaces, Key domainKeyToUse) throws Exception {
    this.io = io;
    this.alive = alive;
    this.factory = factory;
    this.dataService = factory.dataService;
    this.service = factory.service;
    this.domainKeyToUse = domainKeyToUse;
    this.spaces = spaces;
    this.watchService = watchService;
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

  public void shutdown() throws Exception {
    alive.set(false);
    scanner.interrupt();
    factory.shutdown();
  }

  private void poll() throws Exception {
    WatchKey wk = watchService.take();
    boolean doRebuild = false;
    for (WatchEvent<?> event : wk.pollEvents()) {
      final Path changed = (Path) event.context();
      String filename = changed.toFile().getName();
      if (changed.toFile().isDirectory() || filename.contains(".adama")) {
        doRebuild = true;
      }
      wk.reset();
    }
    if (doRebuild) {
      rebuild();
    }
    Thread.sleep(1000);
  }

  public static DevBoxAdamaMicroVerse load(AtomicBoolean alive, TerminalIO io, ObjectNode defn) throws Exception {
    String caravanLocation = "caravan";
    String cloudLocation = "cloud";
    if (defn.has("caravan-path")) {
      caravanLocation = defn.get("caravan-path").asText();
    }
    if (defn.has("cloud-path")) {
      cloudLocation = defn.get("cloud-path").asText();
    }
    File caravanPath = new File(caravanLocation);
    if (!caravanPath.exists()) {
      caravanPath.mkdirs();
    }
    File cloudPath = new File(cloudLocation);
    if (!cloudPath.exists()) {
      cloudPath.mkdirs();
    }
    Key domainKeyToUse = null;

    HashMap<Key, Long> keys = new HashMap<>();
    JsonNode documentsNode = defn.get("documents");
    if (documentsNode != null && documentsNode.isArray()) {
      ArrayNode documents = (ArrayNode) documentsNode;
      for (int k = 0; k < documents.size(); k++) {
        if (documents.get(k) != null && documents.get(k).isObject()) {
          ObjectNode document = (ObjectNode) documents.get(k);
          Key key = new Key(document.get("space").textValue(), document.get("key").textValue());
          if (document.has("domain")) {
            domainKeyToUse = key;
          }
          keys.put(key, document.get("id").longValue());
        }
      }
    }
    DevCoreServiceFactory factory = new DevCoreServiceFactory(alive, caravanPath, cloudPath, new NoOpMetricsFactory());
    JsonNode spacesNode = defn.get("spaces");
    if (spacesNode == null || !spacesNode.isArray()) {
      io.notice("verse|lacked a spaces array in microverse config");
      return null;
    }
    WatchService watchService = FileSystems.getDefault().newWatchService();
    ArrayNode spaces = (ArrayNode) spacesNode;
    ArrayList<LocalSpaceDefn> localSpaces = new ArrayList<>();
    for (int k = 0; k < spaces.size(); k++) {
      ObjectNode space = (ObjectNode) spaces.get(k);
      String name = space.get("name").textValue();
      String mainFile = space.get("main").textValue();
      String importPath = null;
      JsonNode importNode = space.get("import");
      if (importNode != null) {
        importPath = importNode.textValue();
      }
      localSpaces.add(new LocalSpaceDefn(watchService, name, mainFile, importPath, factory.base));
    }

    if (domainKeyToUse != null) {
      io.notice("verse|mapping host to use:" + domainKeyToUse.space + "/" + domainKeyToUse.key);
    }
    return new DevBoxAdamaMicroVerse(watchService, io, alive, factory, localSpaces, domainKeyToUse);
  }
}

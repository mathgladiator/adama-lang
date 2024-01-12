/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.devbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.caravan.CaravanDataService;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.TimeMachine;
import org.adamalang.common.keys.VAPIDPublicPrivateKeyPair;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.deploy.Linter;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.validators.ValidatePlan;
import org.adamalang.web.client.WebClientBase;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** a microverse is a local cosmos of an Adama machine that outlines everything needed to run Adama locally without a DB */
public class AdamaMicroVerse {
  public final LocalServiceFactory factory;
  public final CaravanDataService dataService;
  public final CoreService service;
  public final ArrayList<LocalSpaceDefn> spaces;
  public final Key domainKeyToUse;
  public final String vapidPublicKey;
  public final String vapidPrivateKey;
  public final DevPush devPush;
  public final TimeMachine timeMachine;
  private final TerminalIO io;
  private final AtomicBoolean alive;
  private final WatchService watchService;
  private final Thread scanner;
  private final DiagnosticsSubscriber diagnostics;

  private AdamaMicroVerse(WatchService watchService, TerminalIO io, AtomicBoolean alive, LocalServiceFactory factory, ArrayList<LocalSpaceDefn> spaces, Key domainKeyToUse, String vapidPublicKey, String vapidPrivateKey, DevPush devPush, DiagnosticsSubscriber diagnostics) throws Exception {
    this.io = io;
    this.alive = alive;
    this.factory = factory;
    this.dataService = factory.dataService;
    this.service = factory.service;
    this.timeMachine = factory.timeMachine;
    this.domainKeyToUse = domainKeyToUse;
    this.spaces = spaces;
    this.watchService = watchService;
    this.diagnostics = diagnostics;
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
    this.vapidPublicKey = vapidPublicKey;
    this.vapidPrivateKey = vapidPrivateKey;
    this.devPush = devPush;
  }

  private void rebuild() {
    for (LocalSpaceDefn defn : spaces) {
      try {
        String plan = defn.bundle(io);
        if (plan == null) {
          continue;
        }
        if (!defn.lastDeployedPlan.equals(plan)) {
          long start = System.currentTimeMillis();
          io.notice("adama|validating: " + defn.spaceName);
          String newReflection = ValidatePlan.sharedValidatePlanGetLastReflection(plan, new File(defn.mainFile).getAbsolutePath(), defn.includePath != null ? new File(defn.includePath) : null, (ln) -> io.error(ln), (d) -> {
            diagnostics.updated(d);
          }, (in) -> {
            diagnostics.indexed(in);
          });
          if (newReflection != null) {
            if (defn.lastReflection != null) {
              List<String> issues = Linter.compare(defn.lastReflection, newReflection);
              if (issues.size() == 0) {
                io.notice("adama|no lint issues: " + defn.spaceName);
              } else {
                io.notice("adama|" + issues.size() + " lint issues: " + defn.spaceName);
              }
              for (String issue : issues) {
                io.notice("adama|lint-issue[" + defn.spaceName + "] := " + issue);
              }
            }
            io.notice("adama|deploying: " + defn.spaceName);
            CountDownLatch awaitDeployment = new CountDownLatch(2);
            defn.base.deploy(defn.spaceName, new DeploymentPlan(plan, (t, ec) -> {
              io.error("adama|deployment-issue[Code-" + ec + "]: " + t.getMessage());
            }), new TreeMap<>(), Callback.FINISHED_LATCH_DONT_CARE_VOID(awaitDeployment));
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
            Files.writeString(defn.reflectFile.toPath(), Json.parseJsonObject(newReflection).toPrettyString());
            awaitDeployment.await(1000, TimeUnit.MILLISECONDS);
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
      // there is a bug where files show as empty immediately after a save, so wait a bit to hope the file system is stable by now
      Thread.sleep(250);
      rebuild();
    }
    Thread.sleep(1000);
  }

  public static AdamaMicroVerse load(AtomicBoolean alive, TerminalIO io, ObjectNode defn, WebClientBase webClientBase, File types, DiagnosticsSubscriber diagnostics, MetricsFactory metricsFactory) throws Exception {
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
    if (!types.exists()) {
      types.mkdirs();
    }
    Key domainKeyToUse = null;

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
        }
      }
    }
    LocalServiceFactory factory = new LocalServiceFactory(io, alive, caravanPath, cloudPath, metricsFactory);
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
      String reflectFileName = name + ".json";
      JsonNode reflectNode = space.get("reflect");
      if (reflectNode != null) {
        reflectFileName = reflectNode.textValue();
      }
      localSpaces.add(new LocalSpaceDefn(watchService, name, mainFile, importPath, new File(types, reflectFileName), factory.base));
    }

    if (domainKeyToUse != null) {
      io.notice("verse|mapping host to use:" + domainKeyToUse.space + "/" + domainKeyToUse.key);
    }

    String vapidPublic = "";
    String vapidPrivate = "";
    JsonNode vapid = defn.get("vapid");
    if (vapid != null && vapid.isObject()) {
      vapidPublic = vapid.get("public").textValue();
      vapidPrivate = vapid.get("private").textValue();
      io.notice("verse|using public key for VAPID:" + vapidPublic);
    }
    String pushFile = "pusher.json";
    if (defn.has("push-file")) {
      pushFile = defn.get("push-file").textValue();
    }
    String pushEmail = "";
    if (defn.has("push-email")) {
      pushEmail = defn.get("push-email").textValue();
    }
    VAPIDPublicPrivateKeyPair keyPair = null;
    try {
      keyPair = new VAPIDPublicPrivateKeyPair(vapidPublic, vapidPrivate);
    } catch (Exception ex) {
      io.notice("verse|VAPID has no valid keypair, web push is disabled");
    }
    return new AdamaMicroVerse(watchService, io, alive, factory, localSpaces, domainKeyToUse, vapidPublic, vapidPrivate, new DevPush(io, new File(pushFile), pushEmail, keyPair, webClientBase, metricsFactory), diagnostics);
  }

  public void shutdown() throws Exception {
    alive.set(false);
    scanner.interrupt();
    factory.shutdown();
  }

  public static class LocalSpaceDefn {
    public final String spaceName;
    public final String mainFile;
    public final String includePath;
    private final WatchService watchService;
    private final DeploymentFactoryBase base;
    private final File reflectFile;
    public String lastDeployedPlan;
    public String lastReflection;
    private final HashMap<String, WatchKey> watchKeyCache;

    public LocalSpaceDefn(WatchService watchService, String spaceName, String mainFile, String includePath, File reflectFile, DeploymentFactoryBase base) {
      this.watchService = watchService;
      this.spaceName = spaceName;
      this.mainFile = mainFile;
      this.includePath = includePath;
      this.watchKeyCache = new HashMap<>();
      this.base = base;
      this.lastDeployedPlan = "";
      this.reflectFile = reflectFile;
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

    public void fillImports(File imports, String prefix, HashMap<String, String> map) throws Exception {
      if (imports.exists() && imports.isDirectory()) {
        for (File f : imports.listFiles()) {
          if (f.getName().endsWith(".adama")) {
            String name = prefix + f.getName().substring(0, f.getName().length() - 6);
            map.put(name, Files.readString(f.toPath()));
          } else if (f.isDirectory()) {
            fillImports(scan(f), prefix + f.getName() + "/", map);
          }
        }
      }
    }

    public HashMap<String, String> getImports(String imports) throws Exception {
      HashMap<String, String> map = new HashMap<>();
      File fileImports = scan(new File(imports));
      fillImports(fileImports, "", map);
      return map;
    }

    public String bundle(TerminalIO io) throws Exception {
      ObjectNode plan = Json.newJsonObject();
      plan.put("instrument", true);
      ObjectNode version = plan.putObject("versions").putObject("file");
      String main = Files.readString(new File(mainFile).toPath());
      if (main.trim().equals("")) {
        io.notice("adama|bundled failed due to empty main for '" + spaceName + "'");
        return null;
      }
      version.put("main", main);
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
}

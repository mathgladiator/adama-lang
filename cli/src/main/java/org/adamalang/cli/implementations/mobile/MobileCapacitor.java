/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.cli.implementations.mobile;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.implementations.FrontendHandlerImpl;
import org.adamalang.cli.router.Arguments;
import org.adamalang.common.Callback;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.rxhtml.Bundler;
import org.adamalang.rxhtml.CapacitorJSShell;
import org.adamalang.web.client.*;
import org.adamalang.web.service.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileCapacitor {
  private static final Logger LOG = LoggerFactory.getLogger(MobileCapacitor.class);

  public static void run(Arguments.FrontendMobileCapacitorArgs args) throws Exception {
    final String forest;
    { // aggregate the RxHTML files into a forest
      ArrayList<File> files = new ArrayList<>();
      File rxhtmlPath = new File(args.rxhtmlPath);
      FrontendHandlerImpl.aggregateFiles(rxhtmlPath, files);
      forest = Bundler.bundle(rxhtmlPath, files, false);
    }

    CapacitorJSShell shellBuilder = new CapacitorJSShell((el, w) -> {
      System.err.println("warning:" + w);
    });

    // parse the mobile config bundle
    ObjectNode mobileConfig = Json.parseJsonObject(Files.readString(new File(args.mobileConfig).toPath()));

    // parse the mobile config bundle
    boolean devmode = false;
    boolean beta = false;
    if (mobileConfig.has("devmode") && mobileConfig.get("devmode").booleanValue()) {
      shellBuilder.enableDevMode();
      devmode = true;
    }
    if (mobileConfig.has("beta")) {
      beta = mobileConfig.get("beta").booleanValue();
    }
    if (beta) {
      devmode = true;
    }
    if (mobileConfig.has("multi-domain") && mobileConfig.get("multi-domain").booleanValue()) {
      if (!mobileConfig.has("start")) {
        throw new Exception("multi-domain requires a 'start' field");
      }
      if (!mobileConfig.has("beta-suffix")) {
        throw new Exception("multi-domain requires a 'beta-suffix' field");
      }
      if (!mobileConfig.has("prod-suffix")) {
        throw new Exception("multi-domain requires a 'prod-suffix' field");
      }
      if (beta) {
        throw new Exception("mobile shell can't have both beta and multi-domain set to true");
      }
      String start = mobileConfig.get("start").textValue();
      String betaSuffix = mobileConfig.get("beta-suffix").textValue();
      String prodSuffix = mobileConfig.get("prod-suffix").textValue();
      shellBuilder.setMultiDomain(start, betaSuffix, prodSuffix);
    } else {
      if (!mobileConfig.has("domain")) {
        throw new Exception("requries 'domain' to be set (or multi-domain & start)");
      }
      shellBuilder.setDomain(mobileConfig.get("domain").textValue());
    }
    if (!mobileConfig.has("root")) {
      throw new Exception("mobile-config missing root");
    }

    // PUSH related configs
    if (!mobileConfig.has("google")) {
      throw new Exception("mobile-config missing google");
    }
    if (!mobileConfig.has("google-ios")) {
      //throw new Exception("mobile-config missing google-ios");
    }
    if (!mobileConfig.has("manifest")) {
      throw new Exception("mobile-config missing manifest");
    }
    if (!mobileConfig.has("appid")) {
      throw new Exception("mobile-config missing appid");
    }
    if (!mobileConfig.has("app-entitlements")) {
      throw new Exception("mobile-config missing app-entitlements");
    }
    if (!mobileConfig.has("android-manifest")) {
      throw new Exception("mobile-config missing android-manifest");
    }

    String rootPath = mobileConfig.get("root").textValue();

    String google = mobileConfig.get("google").textValue();
    String manifest = mobileConfig.get("manifest").textValue();
    String appid = mobileConfig.get("appid").textValue();
    String googleIOS = mobileConfig.get("google-ios").textValue();
    String appEntitlements = mobileConfig.get("app-entitlements").textValue();
    String androidManifest = mobileConfig.get("android-manifest").textValue();

    File root = new File(rootPath);
    if (!(root.exists() && root.isDirectory())) {
      throw new Exception("root '" + rootPath + "' must exist");
    }
    File iOScapacitorConfig = new File(root + "/ios/App/App/", "capacitor.config.json");
    if (!iOScapacitorConfig.exists()) {
      throw new Exception("root '" + rootPath + iOScapacitorConfig.getPath() + "' lacks capacitor.config.json which means it isn't a mobile-iOS template");
    }

    File androidCapacitorConfig = new File(root + "/android/app/src/main/assets/", "capacitor.config.json");
    if (!androidCapacitorConfig.exists()) {
      throw new Exception("root '" + rootPath + androidCapacitorConfig.getPath() + "' lacks capacitor.config.json which means it isn't a mobile-Android template");
    }

    File rootSrc = new File(root, "src");
    if (!(rootSrc.exists() && rootSrc.isDirectory())) {
      throw new Exception("path '" + rootPath + "/src' must exist");
    }
    File android = new File(root, "android");
    if (!(android.exists() && android.isDirectory())) {
      throw new Exception("path '" + rootPath + "/android' must exist");
    }
    File androidApp = new File(android, "app");
    if (!(androidApp.exists() && androidApp.isDirectory())) {
      throw new Exception("path '" + rootPath + "/android/app' must exist");
    }

    File androidAppProdPath = new File(androidApp, "src/prodRelease");
    if(!(androidAppProdPath.exists() && androidAppProdPath.isDirectory())){
      if(!androidAppProdPath.mkdir()){
        throw new Exception("path '" + rootPath + "/android/app/src/prodRelease' must exist");
      }
    }

    File ios = new File(root, "ios");
    if (!(ios.exists() && ios.isDirectory())) {
      throw new Exception("path '" + rootPath + "/ios' must exist");
    }
    File iosApp = new File(ios, "App/App");
    if (!(iosApp.exists() && iosApp.isDirectory())) {
      throw new Exception("path '" + rootPath + "/ios/App/App' must exist");
    }

    WebClientBase webBase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), new WebConfig(new ConfigObject(args.config.get_or_create_child("web"))));
    try {
      ObjectNode manifestJson = Json.parseJsonObject(new String(fetch(webBase, manifest), StandardCharsets.UTF_8));
      String shell = shellBuilder.make(forest);
      Files.writeString(new File(rootSrc, "index.html").toPath(), shell);
      File assetsPath = new File(args.assetPath);
      if (!(assetsPath.exists() && assetsPath.isDirectory())) {
        throw new Exception(args.assetPath + " must be a directory");
      }
      TreeSet<String> ignore = new TreeSet<>();
      ignore.add(".adama-ignore");
      File assetsToIgnore = new File(assetsPath, ".adama-ignore");
      if (assetsToIgnore.exists()) {
        for (String ln : Files.readAllLines(assetsToIgnore.toPath())) {
          ignore.add(ln);
        }
      }
      copyAssets(assetsPath, rootSrc, ignore, "");
      final String[] filesToMigrate;
      if (devmode) {
        filesToMigrate = new String[]{"connection.js", "tree.js", "rxhtml.js", "rxcapacitor.js"};
      } else {
        filesToMigrate = new String[]{"libadama.js", "rxcapacitor.js"};
      }
      for (String f : filesToMigrate) {
        File src = new File(args.localLibadamaPath, f);
        File dest = new File(rootSrc, f);
        if (beta) {
          String input = Files.readString(src.toPath());
          input = input.replaceAll(Pattern.quote("Adama.Production"), Matcher.quoteReplacement("Adama.Beta"));
          Files.writeString(dest.toPath(), input);
        } else {
          Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
      }
      String googleServices = Files.readString(new File(google).toPath());
      if (google.endsWith(".encrypted")) {
        googleServices = MasterKey.decrypt(args.config.getMasterKey(), googleServices);
      }
      Files.writeString(new File(androidApp, "google-services.json").toPath(), googleServices);

      // iOS GoogleService-Info.plist
      String googleiOSServices = Files.readString(new File(googleIOS).toPath());
      if (googleIOS.endsWith(".encrypted")) {
        googleiOSServices = MasterKey.decrypt(args.config.getMasterKey(), googleiOSServices);
      }
      Files.writeString(new File(iosApp, "GoogleService-Info.plist").toPath(), googleiOSServices);

      // iOS App.entitlements
      String iOSAppEntitlements = Files.readString(new File(appEntitlements).toPath());
      if (appEntitlements.endsWith(".encrypted")) {
        iOSAppEntitlements = MasterKey.decrypt(args.config.getMasterKey(), iOSAppEntitlements);
      }
      Files.writeString(new File(iosApp, "App.entitlements").toPath(), iOSAppEntitlements);

      String androidAppManifest = Files.readString(new File(androidManifest).toPath());
      if(androidAppManifest.endsWith(".encrypted")){
        androidAppManifest = MasterKey.decrypt(args.config.getMasterKey(), androidAppManifest);
      }
      Files.writeString(new File(androidAppProdPath, "AndroidManifest.xml").toPath(), androidAppManifest);


      // iOS with path : ios/App/App/capacitor.config.json
      // Writing iOS specific configuration
      ObjectNode iOSCapacitorConfigNode = Json.parseJsonObject(Files.readString(iOScapacitorConfig.toPath()));
      iOSCapacitorConfigNode.put("appId", appid);
      iOSCapacitorConfigNode.put("appName", manifestJson.get("name").textValue());
      Files.writeString(iOScapacitorConfig.toPath(), iOSCapacitorConfigNode.toPrettyString());

      // Android with path: /android/app/src/main/assets/capacitor.config.json
      // Writing Android specific configuration
      ObjectNode androidCapacitorConfigNode = Json.parseJsonObject(Files.readString(androidCapacitorConfig.toPath()));
      androidCapacitorConfigNode.put("appId", appid);
      androidCapacitorConfigNode.put("appName", manifestJson.get("name").textValue());
      Files.writeString(androidCapacitorConfig.toPath(), androidCapacitorConfigNode.toPrettyString());

    } finally {
      webBase.shutdown();
    }
  }

  private static void copyAssets(File source, File dest, TreeSet<String> ignore, String prefix) throws Exception {
    for (File child : source.listFiles()) {
      if (child.isDirectory()) {
        File newDest = new File(dest, child.getName());
        newDest.mkdirs();
        if (newDest.isDirectory()) {
          if (!ignore.contains(prefix + child.getName())) {
            copyAssets(child, newDest, ignore, prefix + "/" + child.getName());
          }
        }
      } else {
        if (!ignore.contains(prefix + child.getName())) {
          Files.copy(child.toPath(), new File(dest, child.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
      }
    }
  }

  public static byte[] fetch(WebClientBase base, String url) throws Exception {
    SimpleHttpRequest request = new SimpleHttpRequest("GET", url, new TreeMap<>(), SimpleHttpRequestBody.EMPTY);
    AtomicReference<Object> ref = new AtomicReference<>(null);
    CountDownLatch latch = new CountDownLatch(1);
    base.executeShared(request, new ByteArrayCallbackHttpResponder(LOG, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<byte[]>() {
      @Override
      public void success(byte[] value) {
        ref.set(value);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ref.set(ex);
        latch.countDown();
      }
    }));
    if (latch.await(60000, TimeUnit.MILLISECONDS)) {
      if (ref.get() instanceof byte[]) {
        return (byte[]) ref.get();
      } else {
        throw (ErrorCodeException) ref.get();
      }
    } else {
      throw new Exception("Timed out:" + url);
    }
  }

}

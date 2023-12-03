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
      FrontendHandlerImpl.aggregateFiles(new File(args.rxhtmlPath), files);
      forest = Bundler.bundle(files, false);
    }

    // parse the mobile config bundle
    ObjectNode mobileConfig = Json.parseJsonObject(Files.readString(new File(args.mobileConfig).toPath()));

    // parse the mobile config bundle
    boolean devmode = false;
    boolean beta = false;
    if (mobileConfig.has("devmode")) {
      devmode = mobileConfig.get("devmode").booleanValue();
    }
    if (mobileConfig.has("beta")) {
      beta = mobileConfig.get("beta").booleanValue();
    }
    if (!mobileConfig.has("root")) {
      throw new Exception("mobile-config missing root");
    }
    if (!mobileConfig.has("domain")) {
      throw new Exception("mobile-config missing domain");
    }
    if (!mobileConfig.has("google")) {
      throw new Exception("mobile-config missing google");
    }
    if (!mobileConfig.has("manifest")) {
      throw new Exception("mobile-config missing manifest");
    }
    if (!mobileConfig.has("appid")) {
      throw new Exception("mobile-config missing appid");
    }
    String rootPath = mobileConfig.get("root").textValue();
    String domain = mobileConfig.get("domain").textValue();
    String google = mobileConfig.get("google").textValue();
    String manifest = mobileConfig.get("manifest").textValue();
    String appid = mobileConfig.get("appid").textValue();

    File root = new File(rootPath);
    if (!(root.exists() && root.isDirectory())) {
      throw new Exception("root '" + rootPath + "' must exist");
    }
    File capacitorConfig = new File(root, "capacitor.config.json");
    if (!capacitorConfig.exists()) {
      throw new Exception("root '" + rootPath + "' lacks capacitor.config.json which means it isn't a mobile template");
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
    WebClientBase webBase = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), new WebConfig(new ConfigObject(args.config.get_or_create_child("web"))));
    try {
      ObjectNode manifestJson = Json.parseJsonObject(new String(fetch(webBase, manifest), StandardCharsets.UTF_8));
      String shell = CapacitorJSShell.makeMobileShell(forest, domain, devmode, (el, w) -> {
        System.err.println("warning:" + w);
      });
      Files.writeString(new File(rootSrc, "index.html").toPath(), shell);
      File assetsPath = new File(args.assetPath);
      if (!(assetsPath.exists() && assetsPath.isDirectory())) {
        throw new Exception(args.assetPath + " must be a directory");
      }
      copyAssets(assetsPath, rootSrc);
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
      ObjectNode capacitorConfigNode = Json.parseJsonObject(Files.readString(capacitorConfig.toPath()));
      capacitorConfigNode.put("appId", appid);
      capacitorConfigNode.put("appName", manifestJson.get("name").textValue());
      Files.writeString(capacitorConfig.toPath(), capacitorConfigNode.toPrettyString());
    } finally {
      webBase.shutdown();
    }
  }

  private static void copyAssets(File source, File dest) throws Exception {
    for (File child : source.listFiles()) {
      if (child.isDirectory()) {
        File newDest = new File(dest, child.getName());
        newDest.mkdirs();
        if (newDest.isDirectory()) {
          copyAssets(child, newDest);
        }
      } else {
        Files.copy(child.toPath(), new File(dest, child.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
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

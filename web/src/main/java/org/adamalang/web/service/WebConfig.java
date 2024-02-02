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
package org.adamalang.web.service;

import org.adamalang.common.ConfigObject;

import java.io.File;
import java.util.TreeSet;

public class WebConfig {
  public final String healthCheckPath;
  public final String deepHealthCheckPath;
  public final int maxContentLengthSize;
  public final int maxWebSocketFrameSize;
  public final int port;
  public final int redirectPort;
  public final int timeoutWebsocketHandshake;
  public final int heartbeatTimeMilliseconds;
  public final int idleReadSeconds;
  public final int idleWriteSeconds;
  public final int idleAllSeconds;
  public final int bossThreads;
  public final int workerThreads;
  public final TreeSet<String> specialDomains;
  public final String regionalDomain;
  public final String[] globalDomains;
  public final int sharedConnectionPoolMaxLifetimeMilliseconds;
  public final int sharedConnectionPoolMaxUsageCount;
  public final int sharedConnectionPoolMaxPoolSize;
  public final File cacheRoot;
  public final String adamaJarDomain;
  public final int minDomainsToHoldTo;
  public final int maxDomainsToHoldTo;
  public final int maxDomainAge;
  public final boolean beta;
  public final File transformRoot;

  public WebConfig(ConfigObject config) throws Exception {
    // HTTP properties
    this.port = config.intOf("http-port", 8080);
    this.redirectPort = config.intOf("http-redirect-port", 8085);
    this.maxContentLengthSize = config.intOf("http-max-content-length-size", 12582912);
    this.healthCheckPath = config.strOf("http-health-check-path", "/~health_check_lb");
    this.deepHealthCheckPath = config.strOf("http-deep-health-check-path", "/~deep_health_check_status_page");
    // WebSocket properties
    this.timeoutWebsocketHandshake = config.intOf("websocket-handshake-timeout-ms", 2500);
    this.idleReadSeconds = config.intOf("http-read-idle-sec", 60);
    this.idleWriteSeconds = config.intOf("http-write-idle-sec", 60);
    this.idleAllSeconds = config.intOf("websocket-all-idle-sec", 60);
    this.maxWebSocketFrameSize = config.intOf("websocket-max-frame-size", 4 * 1024 * 1024);
    this.heartbeatTimeMilliseconds = config.intOf("websocket-heart-beat-ms", 1000);
    this.bossThreads = config.intOf("http-boss-threads", 2);
    this.workerThreads = config.intOf("http-worker-threads", 16);
    this.regionalDomain = config.strOf("regional-domain", "adama-platform.com");
    this.adamaJarDomain = config.strOf("adama-jar-domain", ".adama-platform.com");
    this.globalDomains = config.stringsOf("global-domains", new String[] { "adama.games" });
    this.specialDomains = new TreeSet<>();
    this.beta = config.boolOf("beta", false);
    for (String sd : config.stringsOf("special-domains", new String[] { "www.adama-platform.com", "ide.adama-platform.com", "book.adama-platform.com" })) {
      specialDomains.add(sd);
    }
    this.sharedConnectionPoolMaxLifetimeMilliseconds = config.intOf("shared-connection-max-lifetime-ms", 10000);
    this.sharedConnectionPoolMaxUsageCount = config.intOf("shared-connection-max-usage-count", 50);
    this.sharedConnectionPoolMaxPoolSize = config.intOf("shared-connection-max-pool-size", 50);
    this.cacheRoot = new File(config.strOf("cache-root", "cache"));
    this.transformRoot = new File(config.strOf("transform-root", "transform-cache"));
    if (!cacheRoot.exists()) {
      cacheRoot.mkdir();
    }
    if (!transformRoot.exists()) {
      transformRoot.mkdir();
    }

    // Domain Cache
    this.minDomainsToHoldTo = config.intOf("cert-cache-min-domains", 64);
    this.maxDomainsToHoldTo = config.intOf("cert-cache-max-domains", 2048);
    this.maxDomainAge = config.intOf("cert-cache-max-age", 5 * 60 * 1000);

    if (cacheRoot.exists() && !cacheRoot.isDirectory()) {
      throw new Exception("Cache root is not a directory");
    }
    if (transformRoot.exists() && !transformRoot.isDirectory()) {
      throw new Exception("Transform root is not a directory");
    }
  }
}

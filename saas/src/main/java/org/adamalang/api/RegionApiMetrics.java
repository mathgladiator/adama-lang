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
package org.adamalang.api;


import org.adamalang.common.metrics.*;

public class RegionApiMetrics {
  public final RequestResponseMonitor monitor_Stats;
  public final RequestResponseMonitor monitor_IdentityHash;
  public final RequestResponseMonitor monitor_IdentityStash;
  public final RequestResponseMonitor monitor_DocumentAuthorization;
  public final RequestResponseMonitor monitor_DocumentAuthorizationDomain;
  public final RequestResponseMonitor monitor_DocumentAuthorize;
  public final RequestResponseMonitor monitor_DocumentAuthorizeDomain;
  public final RequestResponseMonitor monitor_DocumentAuthorizeWithReset;
  public final RequestResponseMonitor monitor_DocumentAuthorizeDomainWithReset;
  public final RequestResponseMonitor monitor_DocumentCreate;
  public final RequestResponseMonitor monitor_DocumentDelete;
  public final RequestResponseMonitor monitor_MessageDirectSend;
  public final RequestResponseMonitor monitor_MessageDirectSendOnce;
  public final StreamMonitor monitor_ConnectionCreate;
  public final StreamMonitor monitor_ConnectionCreateViaDomain;
  public final RequestResponseMonitor monitor_ConnectionSend;
  public final RequestResponseMonitor monitor_ConnectionPassword;
  public final RequestResponseMonitor monitor_ConnectionSendOnce;
  public final RequestResponseMonitor monitor_ConnectionCanAttach;
  public final RequestResponseMonitor monitor_ConnectionAttach;
  public final RequestResponseMonitor monitor_ConnectionUpdate;
  public final RequestResponseMonitor monitor_ConnectionEnd;
  public final RequestResponseMonitor monitor_DocumentsHashPassword;
  public final StreamMonitor monitor_BillingConnectionCreate;
  public final RequestResponseMonitor monitor_ConfigureMakeOrGetAssetKey;
  public final StreamMonitor monitor_AttachmentStart;
  public final StreamMonitor monitor_AttachmentStartByDomain;
  public final RequestResponseMonitor monitor_AttachmentAppend;
  public final RequestResponseMonitor monitor_AttachmentFinish;

  public RegionApiMetrics(MetricsFactory factory) {
    this.monitor_Stats = factory.makeRequestResponseMonitor("stats");
    this.monitor_IdentityHash = factory.makeRequestResponseMonitor("identity/hash");
    this.monitor_IdentityStash = factory.makeRequestResponseMonitor("identity/stash");
    this.monitor_DocumentAuthorization = factory.makeRequestResponseMonitor("document/authorization");
    this.monitor_DocumentAuthorizationDomain = factory.makeRequestResponseMonitor("document/authorization-domain");
    this.monitor_DocumentAuthorize = factory.makeRequestResponseMonitor("document/authorize");
    this.monitor_DocumentAuthorizeDomain = factory.makeRequestResponseMonitor("document/authorize-domain");
    this.monitor_DocumentAuthorizeWithReset = factory.makeRequestResponseMonitor("document/authorize-with-reset");
    this.monitor_DocumentAuthorizeDomainWithReset = factory.makeRequestResponseMonitor("document/authorize-domain-with-reset");
    this.monitor_DocumentCreate = factory.makeRequestResponseMonitor("document/create");
    this.monitor_DocumentDelete = factory.makeRequestResponseMonitor("document/delete");
    this.monitor_MessageDirectSend = factory.makeRequestResponseMonitor("message/direct-send");
    this.monitor_MessageDirectSendOnce = factory.makeRequestResponseMonitor("message/direct-send-once");
    this.monitor_ConnectionCreate = factory.makeStreamMonitor("connection/create");
    this.monitor_ConnectionCreateViaDomain = factory.makeStreamMonitor("connection/create-via-domain");
    this.monitor_ConnectionSend = factory.makeRequestResponseMonitor("connection/send");
    this.monitor_ConnectionPassword = factory.makeRequestResponseMonitor("connection/password");
    this.monitor_ConnectionSendOnce = factory.makeRequestResponseMonitor("connection/send-once");
    this.monitor_ConnectionCanAttach = factory.makeRequestResponseMonitor("connection/can-attach");
    this.monitor_ConnectionAttach = factory.makeRequestResponseMonitor("connection/attach");
    this.monitor_ConnectionUpdate = factory.makeRequestResponseMonitor("connection/update");
    this.monitor_ConnectionEnd = factory.makeRequestResponseMonitor("connection/end");
    this.monitor_DocumentsHashPassword = factory.makeRequestResponseMonitor("documents/hash-password");
    this.monitor_BillingConnectionCreate = factory.makeStreamMonitor("billing-connection/create");
    this.monitor_ConfigureMakeOrGetAssetKey = factory.makeRequestResponseMonitor("configure/make-or-get-asset-key");
    this.monitor_AttachmentStart = factory.makeStreamMonitor("attachment/start");
    this.monitor_AttachmentStartByDomain = factory.makeStreamMonitor("attachment/start-by-domain");
    this.monitor_AttachmentAppend = factory.makeRequestResponseMonitor("attachment/append");
    this.monitor_AttachmentFinish = factory.makeRequestResponseMonitor("attachment/finish");
  }
}

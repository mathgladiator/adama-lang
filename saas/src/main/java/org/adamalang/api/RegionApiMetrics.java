/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;


import org.adamalang.common.metrics.*;

public class RegionApiMetrics {
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
  public final StreamMonitor monitor_AttachmentStart;
  public final StreamMonitor monitor_AttachmentStartByDomain;
  public final RequestResponseMonitor monitor_AttachmentAppend;
  public final RequestResponseMonitor monitor_AttachmentFinish;

  public RegionApiMetrics(MetricsFactory factory) {
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
    this.monitor_AttachmentStart = factory.makeStreamMonitor("attachment/start");
    this.monitor_AttachmentStartByDomain = factory.makeStreamMonitor("attachment/start-by-domain");
    this.monitor_AttachmentAppend = factory.makeRequestResponseMonitor("attachment/append");
    this.monitor_AttachmentFinish = factory.makeRequestResponseMonitor("attachment/finish");
  }
}

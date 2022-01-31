/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.api;


import org.adamalang.common.metrics.*;

public class ApiMetrics {
  public final StreamMonitor monitor_InitStart;
  public final RequestResponseMonitor monitor_InitRevokeAll;
  public final RequestResponseMonitor monitor_InitGenerateIdentity;
  public final RequestResponseMonitor monitor_Probe;
  public final RequestResponseMonitor monitor_AuthorityCreate;
  public final RequestResponseMonitor monitor_AuthoritySet;
  public final RequestResponseMonitor monitor_AuthorityGet;
  public final RequestResponseMonitor monitor_AuthorityList;
  public final RequestResponseMonitor monitor_AuthorityDestroy;
  public final RequestResponseMonitor monitor_SpaceCreate;
  public final RequestResponseMonitor monitor_SpaceUsage;
  public final RequestResponseMonitor monitor_SpaceGet;
  public final RequestResponseMonitor monitor_SpaceSet;
  public final RequestResponseMonitor monitor_SpaceDelete;
  public final RequestResponseMonitor monitor_SpaceSetRole;
  public final RequestResponseMonitor monitor_SpaceReflect;
  public final RequestResponseMonitor monitor_SpaceList;
  public final RequestResponseMonitor monitor_DocumentCreate;
  public final RequestResponseMonitor monitor_DocumentList;
  public final StreamMonitor monitor_ConnectionCreate;
  public final RequestResponseMonitor monitor_ConnectionSend;
  public final RequestResponseMonitor monitor_ConnectionEnd;
  public final StreamMonitor monitor_AttachmentStart;
  public final RequestResponseMonitor monitor_AttachmentAppend;
  public final RequestResponseMonitor monitor_AttachmentFinish;

  public ApiMetrics(MetricsFactory factory) {
    this.monitor_InitStart = factory.makeStreamMonitor("init/start");
    this.monitor_InitRevokeAll = factory.makeRequestResponseMonitor("init/revoke-all");
    this.monitor_InitGenerateIdentity = factory.makeRequestResponseMonitor("init/generate-identity");
    this.monitor_Probe = factory.makeRequestResponseMonitor("probe");
    this.monitor_AuthorityCreate = factory.makeRequestResponseMonitor("authority/create");
    this.monitor_AuthoritySet = factory.makeRequestResponseMonitor("authority/set");
    this.monitor_AuthorityGet = factory.makeRequestResponseMonitor("authority/get");
    this.monitor_AuthorityList = factory.makeRequestResponseMonitor("authority/list");
    this.monitor_AuthorityDestroy = factory.makeRequestResponseMonitor("authority/destroy");
    this.monitor_SpaceCreate = factory.makeRequestResponseMonitor("space/create");
    this.monitor_SpaceUsage = factory.makeRequestResponseMonitor("space/usage");
    this.monitor_SpaceGet = factory.makeRequestResponseMonitor("space/get");
    this.monitor_SpaceSet = factory.makeRequestResponseMonitor("space/set");
    this.monitor_SpaceDelete = factory.makeRequestResponseMonitor("space/delete");
    this.monitor_SpaceSetRole = factory.makeRequestResponseMonitor("space/set-role");
    this.monitor_SpaceReflect = factory.makeRequestResponseMonitor("space/reflect");
    this.monitor_SpaceList = factory.makeRequestResponseMonitor("space/list");
    this.monitor_DocumentCreate = factory.makeRequestResponseMonitor("document/create");
    this.monitor_DocumentList = factory.makeRequestResponseMonitor("document/list");
    this.monitor_ConnectionCreate = factory.makeStreamMonitor("connection/create");
    this.monitor_ConnectionSend = factory.makeRequestResponseMonitor("connection/send");
    this.monitor_ConnectionEnd = factory.makeRequestResponseMonitor("connection/end");
    this.monitor_AttachmentStart = factory.makeStreamMonitor("attachment/start");
    this.monitor_AttachmentAppend = factory.makeRequestResponseMonitor("attachment/append");
    this.monitor_AttachmentFinish = factory.makeRequestResponseMonitor("attachment/finish");
  }
}

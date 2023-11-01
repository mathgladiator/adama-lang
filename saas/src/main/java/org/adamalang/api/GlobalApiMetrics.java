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

public class GlobalApiMetrics {
  public final RequestResponseMonitor monitor_InitSetupAccount;
  public final RequestResponseMonitor monitor_InitConvertGoogleUser;
  public final RequestResponseMonitor monitor_InitCompleteAccount;
  public final RequestResponseMonitor monitor_Deinit;
  public final RequestResponseMonitor monitor_AccountSetPassword;
  public final RequestResponseMonitor monitor_AccountGetPaymentPlan;
  public final RequestResponseMonitor monitor_AccountLogin;
  public final RequestResponseMonitor monitor_Probe;
  public final RequestResponseMonitor monitor_AuthorityCreate;
  public final RequestResponseMonitor monitor_AuthoritySet;
  public final RequestResponseMonitor monitor_AuthorityGet;
  public final RequestResponseMonitor monitor_AuthorityList;
  public final RequestResponseMonitor monitor_AuthorityDestroy;
  public final RequestResponseMonitor monitor_SpaceCreate;
  public final RequestResponseMonitor monitor_SpaceGenerateKey;
  public final RequestResponseMonitor monitor_SpaceGet;
  public final RequestResponseMonitor monitor_SpaceSet;
  public final RequestResponseMonitor monitor_SpaceRedeployKick;
  public final RequestResponseMonitor monitor_SpaceSetRxhtml;
  public final RequestResponseMonitor monitor_SpaceGetRxhtml;
  public final RequestResponseMonitor monitor_SpaceSetPolicy;
  public final RequestResponseMonitor monitor_SpaceGetPolicy;
  public final RequestResponseMonitor monitor_SpaceMetrics;
  public final RequestResponseMonitor monitor_SpaceDelete;
  public final RequestResponseMonitor monitor_SpaceSetRole;
  public final RequestResponseMonitor monitor_SpaceListDevelopers;
  public final RequestResponseMonitor monitor_SpaceReflect;
  public final RequestResponseMonitor monitor_SpaceList;
  public final RequestResponseMonitor monitor_PushRegister;
  public final RequestResponseMonitor monitor_DomainMap;
  public final RequestResponseMonitor monitor_DomainReflect;
  public final RequestResponseMonitor monitor_DomainMapDocument;
  public final RequestResponseMonitor monitor_DomainList;
  public final RequestResponseMonitor monitor_DomainListBySpace;
  public final RequestResponseMonitor monitor_DomainGetVapidPublicKey;
  public final RequestResponseMonitor monitor_DomainUnmap;
  public final RequestResponseMonitor monitor_DomainGet;
  public final RequestResponseMonitor monitor_DocumentList;
  public final RequestResponseMonitor monitor_SuperCheckIn;
  public final RequestResponseMonitor monitor_SuperListAutomaticDomains;
  public final RequestResponseMonitor monitor_SuperSetDomainCertificate;
  public final RequestResponseMonitor monitor_RegionalDomainLookup;
  public final RequestResponseMonitor monitor_RegionalEmitMetrics;
  public final RequestResponseMonitor monitor_RegionalInitHost;
  public final RequestResponseMonitor monitor_RegionalFinderFind;
  public final RequestResponseMonitor monitor_RegionalFinderFree;
  public final RequestResponseMonitor monitor_RegionalFinderBind;
  public final RequestResponseMonitor monitor_RegionalFinderDeleteMark;
  public final RequestResponseMonitor monitor_RegionalFinderDeleteCommit;
  public final RequestResponseMonitor monitor_RegionalFinderBackUp;
  public final RequestResponseMonitor monitor_RegionalFinderList;
  public final RequestResponseMonitor monitor_RegionalFinderDeletionList;
  public final RequestResponseMonitor monitor_RegionalAuth;
  public final RequestResponseMonitor monitor_RegionalGetPlan;
  public final RequestResponseMonitor monitor_RegionalCapacityAdd;
  public final RequestResponseMonitor monitor_RegionalCapacityRemove;
  public final RequestResponseMonitor monitor_RegionalCapacityNuke;
  public final RequestResponseMonitor monitor_RegionalCapacityListSpace;
  public final RequestResponseMonitor monitor_RegionalCapacityListMachine;
  public final RequestResponseMonitor monitor_RegionalCapacityListRegion;
  public final RequestResponseMonitor monitor_RegionalCapacityPickSpaceHost;
  public final RequestResponseMonitor monitor_RegionalCapacityPickSpaceHostNew;

  public GlobalApiMetrics(MetricsFactory factory) {
    this.monitor_InitSetupAccount = factory.makeRequestResponseMonitor("init/setup-account");
    this.monitor_InitConvertGoogleUser = factory.makeRequestResponseMonitor("init/convert-google-user");
    this.monitor_InitCompleteAccount = factory.makeRequestResponseMonitor("init/complete-account");
    this.monitor_Deinit = factory.makeRequestResponseMonitor("deinit");
    this.monitor_AccountSetPassword = factory.makeRequestResponseMonitor("account/set-password");
    this.monitor_AccountGetPaymentPlan = factory.makeRequestResponseMonitor("account/get-payment-plan");
    this.monitor_AccountLogin = factory.makeRequestResponseMonitor("account/login");
    this.monitor_Probe = factory.makeRequestResponseMonitor("probe");
    this.monitor_AuthorityCreate = factory.makeRequestResponseMonitor("authority/create");
    this.monitor_AuthoritySet = factory.makeRequestResponseMonitor("authority/set");
    this.monitor_AuthorityGet = factory.makeRequestResponseMonitor("authority/get");
    this.monitor_AuthorityList = factory.makeRequestResponseMonitor("authority/list");
    this.monitor_AuthorityDestroy = factory.makeRequestResponseMonitor("authority/destroy");
    this.monitor_SpaceCreate = factory.makeRequestResponseMonitor("space/create");
    this.monitor_SpaceGenerateKey = factory.makeRequestResponseMonitor("space/generate-key");
    this.monitor_SpaceGet = factory.makeRequestResponseMonitor("space/get");
    this.monitor_SpaceSet = factory.makeRequestResponseMonitor("space/set");
    this.monitor_SpaceRedeployKick = factory.makeRequestResponseMonitor("space/redeploy-kick");
    this.monitor_SpaceSetRxhtml = factory.makeRequestResponseMonitor("space/set-rxhtml");
    this.monitor_SpaceGetRxhtml = factory.makeRequestResponseMonitor("space/get-rxhtml");
    this.monitor_SpaceSetPolicy = factory.makeRequestResponseMonitor("space/set-policy");
    this.monitor_SpaceGetPolicy = factory.makeRequestResponseMonitor("space/get-policy");
    this.monitor_SpaceMetrics = factory.makeRequestResponseMonitor("space/metrics");
    this.monitor_SpaceDelete = factory.makeRequestResponseMonitor("space/delete");
    this.monitor_SpaceSetRole = factory.makeRequestResponseMonitor("space/set-role");
    this.monitor_SpaceListDevelopers = factory.makeRequestResponseMonitor("space/list-developers");
    this.monitor_SpaceReflect = factory.makeRequestResponseMonitor("space/reflect");
    this.monitor_SpaceList = factory.makeRequestResponseMonitor("space/list");
    this.monitor_PushRegister = factory.makeRequestResponseMonitor("push/register");
    this.monitor_DomainMap = factory.makeRequestResponseMonitor("domain/map");
    this.monitor_DomainReflect = factory.makeRequestResponseMonitor("domain/reflect");
    this.monitor_DomainMapDocument = factory.makeRequestResponseMonitor("domain/map-document");
    this.monitor_DomainList = factory.makeRequestResponseMonitor("domain/list");
    this.monitor_DomainListBySpace = factory.makeRequestResponseMonitor("domain/list-by-space");
    this.monitor_DomainGetVapidPublicKey = factory.makeRequestResponseMonitor("domain/get-vapid-public-key");
    this.monitor_DomainUnmap = factory.makeRequestResponseMonitor("domain/unmap");
    this.monitor_DomainGet = factory.makeRequestResponseMonitor("domain/get");
    this.monitor_DocumentList = factory.makeRequestResponseMonitor("document/list");
    this.monitor_SuperCheckIn = factory.makeRequestResponseMonitor("super/check-in");
    this.monitor_SuperListAutomaticDomains = factory.makeRequestResponseMonitor("super/list-automatic-domains");
    this.monitor_SuperSetDomainCertificate = factory.makeRequestResponseMonitor("super/set-domain-certificate");
    this.monitor_RegionalDomainLookup = factory.makeRequestResponseMonitor("regional/domain-lookup");
    this.monitor_RegionalEmitMetrics = factory.makeRequestResponseMonitor("regional/emit-metrics");
    this.monitor_RegionalInitHost = factory.makeRequestResponseMonitor("regional/init-host");
    this.monitor_RegionalFinderFind = factory.makeRequestResponseMonitor("regional/finder/find");
    this.monitor_RegionalFinderFree = factory.makeRequestResponseMonitor("regional/finder/free");
    this.monitor_RegionalFinderBind = factory.makeRequestResponseMonitor("regional/finder/bind");
    this.monitor_RegionalFinderDeleteMark = factory.makeRequestResponseMonitor("regional/finder/delete/mark");
    this.monitor_RegionalFinderDeleteCommit = factory.makeRequestResponseMonitor("regional/finder/delete/commit");
    this.monitor_RegionalFinderBackUp = factory.makeRequestResponseMonitor("regional/finder/back-up");
    this.monitor_RegionalFinderList = factory.makeRequestResponseMonitor("regional/finder/list");
    this.monitor_RegionalFinderDeletionList = factory.makeRequestResponseMonitor("regional/finder/deletion-list");
    this.monitor_RegionalAuth = factory.makeRequestResponseMonitor("regional/auth");
    this.monitor_RegionalGetPlan = factory.makeRequestResponseMonitor("regional/get-plan");
    this.monitor_RegionalCapacityAdd = factory.makeRequestResponseMonitor("regional/capacity/add");
    this.monitor_RegionalCapacityRemove = factory.makeRequestResponseMonitor("regional/capacity/remove");
    this.monitor_RegionalCapacityNuke = factory.makeRequestResponseMonitor("regional/capacity/nuke");
    this.monitor_RegionalCapacityListSpace = factory.makeRequestResponseMonitor("regional/capacity/list-space");
    this.monitor_RegionalCapacityListMachine = factory.makeRequestResponseMonitor("regional/capacity/list-machine");
    this.monitor_RegionalCapacityListRegion = factory.makeRequestResponseMonitor("regional/capacity/list-region");
    this.monitor_RegionalCapacityPickSpaceHost = factory.makeRequestResponseMonitor("regional/capacity/pick-space-host");
    this.monitor_RegionalCapacityPickSpaceHostNew = factory.makeRequestResponseMonitor("regional/capacity/pick-space-host-new");
  }
}

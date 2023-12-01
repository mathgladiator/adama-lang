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

import org.adamalang.frontend.Session;

public interface RootGlobalHandler {
  public void handle(Session session, InitSetupAccountRequest request, SimpleResponder responder);

  public void handle(Session session, InitConvertGoogleUserRequest request, InitiationResponder responder);

  public void handle(Session session, InitCompleteAccountRequest request, InitiationResponder responder);

  public void handle(Session session, DeinitRequest request, SimpleResponder responder);

  public void handle(Session session, AccountSetPasswordRequest request, SimpleResponder responder);

  public void handle(Session session, AccountGetPaymentPlanRequest request, PaymentResponder responder);

  public void handle(Session session, AccountLoginRequest request, InitiationResponder responder);

  public void handle(Session session, ProbeRequest request, SimpleResponder responder);

  public void handle(Session session, AuthorityCreateRequest request, ClaimResultResponder responder);

  public void handle(Session session, AuthoritySetRequest request, SimpleResponder responder);

  public void handle(Session session, AuthorityGetRequest request, KeystoreResponder responder);

  public void handle(Session session, AuthorityListRequest request, AuthorityListingResponder responder);

  public void handle(Session session, AuthorityDestroyRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceCreateRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceGenerateKeyRequest request, KeyPairResponder responder);

  public void handle(Session session, SpaceGetRequest request, PlanResponder responder);

  public void handle(Session session, SpaceSetRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceRedeployKickRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceSetRxhtmlRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceGetRxhtmlRequest request, RxhtmlResponder responder);

  public void handle(Session session, SpaceSetPolicyRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceGetPolicyRequest request, AccessPolicyResponder responder);

  public void handle(Session session, SpaceMetricsRequest request, MetricsAggregateResponder responder);

  public void handle(Session session, SpaceDeleteRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceSetRoleRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceListDevelopersRequest request, DeveloperResponder responder);

  public void handle(Session session, SpaceReflectRequest request, ReflectionResponder responder);

  public void handle(Session session, SpaceListRequest request, SpaceListingResponder responder);

  public void handle(Session session, PushRegisterRequest request, SimpleResponder responder);

  public void handle(Session session, DomainMapRequest request, SimpleResponder responder);

  public void handle(Session session, DomainConfigureRequest request, SimpleResponder responder);

  public void handle(Session session, DomainReflectRequest request, ReflectionResponder responder);

  public void handle(Session session, DomainMapDocumentRequest request, SimpleResponder responder);

  public void handle(Session session, DomainListRequest request, DomainListingResponder responder);

  public void handle(Session session, DomainListBySpaceRequest request, DomainListingResponder responder);

  public void handle(Session session, DomainGetVapidPublicKeyRequest request, DomainVapidResponder responder);

  public void handle(Session session, DomainUnmapRequest request, SimpleResponder responder);

  public void handle(Session session, DomainGetRequest request, DomainPolicyResponder responder);

  public void handle(Session session, DocumentListRequest request, KeyListingResponder responder);

  public void handle(Session session, SuperCheckInRequest request, SimpleResponder responder);

  public void handle(Session session, SuperListAutomaticDomainsRequest request, AutomaticDomainListingResponder responder);

  public void handle(Session session, SuperSetDomainCertificateRequest request, SimpleResponder responder);

  public void handle(Session session, RegionalDomainLookupRequest request, DomainRawResponder responder);

  public void handle(Session session, RegionalEmitMetricsRequest request, SimpleResponder responder);

  public void handle(Session session, RegionalInitHostRequest request, HostInitResponder responder);

  public void handle(Session session, RegionalFinderFindRequest request, FinderResultResponder responder);

  public void handle(Session session, RegionalFinderFreeRequest request, SimpleResponder responder);

  public void handle(Session session, RegionalFinderBindRequest request, SimpleResponder responder);

  public void handle(Session session, RegionalFinderDeleteMarkRequest request, SimpleResponder responder);

  public void handle(Session session, RegionalFinderDeleteCommitRequest request, SimpleResponder responder);

  public void handle(Session session, RegionalFinderBackUpRequest request, SimpleResponder responder);

  public void handle(Session session, RegionalFinderListRequest request, KeysResponder responder);

  public void handle(Session session, RegionalFinderDeletionListRequest request, KeysResponder responder);

  public void handle(Session session, RegionalAuthRequest request, AuthResultResponder responder);

  public void handle(Session session, RegionalGetPlanRequest request, PlanWithKeysResponder responder);

  public void handle(Session session, RegionalCapacityAddRequest request, SimpleResponder responder);

  public void handle(Session session, RegionalCapacityRemoveRequest request, SimpleResponder responder);

  public void handle(Session session, RegionalCapacityNukeRequest request, SimpleResponder responder);

  public void handle(Session session, RegionalCapacityListSpaceRequest request, CapacityListResponder responder);

  public void handle(Session session, RegionalCapacityListMachineRequest request, CapacityListResponder responder);

  public void handle(Session session, RegionalCapacityListRegionRequest request, CapacityListResponder responder);

  public void handle(Session session, RegionalCapacityPickSpaceHostRequest request, CapacityHostResponder responder);

  public void handle(Session session, RegionalCapacityPickSpaceHostNewRequest request, CapacityHostResponder responder);

  public void disconnect();

  public static boolean test(String method) {
    switch (method) {
      case "init/setup-account":
      case "init/convert-google-user":
      case "init/complete-account":
      case "deinit":
      case "account/set-password":
      case "account/get-payment-plan":
      case "account/login":
      case "probe":
      case "authority/create":
      case "authority/set":
      case "authority/get":
      case "authority/list":
      case "authority/destroy":
      case "space/create":
      case "space/generate-key":
      case "space/get":
      case "space/set":
      case "space/redeploy-kick":
      case "space/set-rxhtml":
      case "space/get-rxhtml":
      case "space/set-policy":
      case "space/get-policy":
      case "space/metrics":
      case "space/delete":
      case "space/set-role":
      case "space/list-developers":
      case "space/reflect":
      case "space/list":
      case "push/register":
      case "domain/map":
      case "domain/configure":
      case "domain/reflect":
      case "domain/map-document":
      case "domain/list":
      case "domain/list-by-space":
      case "domain/get-vapid-public-key":
      case "domain/unmap":
      case "domain/get":
      case "document/list":
      case "super/check-in":
      case "super/list-automatic-domains":
      case "super/set-domain-certificate":
      case "regional/domain-lookup":
      case "regional/emit-metrics":
      case "regional/init-host":
      case "regional/finder/find":
      case "regional/finder/free":
      case "regional/finder/bind":
      case "regional/finder/delete/mark":
      case "regional/finder/delete/commit":
      case "regional/finder/back-up":
      case "regional/finder/list":
      case "regional/finder/deletion-list":
      case "regional/auth":
      case "regional/get-plan":
      case "regional/capacity/add":
      case "regional/capacity/remove":
      case "regional/capacity/nuke":
      case "regional/capacity/list-space":
      case "regional/capacity/list-machine":
      case "regional/capacity/list-region":
      case "regional/capacity/pick-space-host":
      case "regional/capacity/pick-space-host-new":
        return true;
      default:
        return false;
    }
  }
}

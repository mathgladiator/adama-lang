/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import org.adamalang.frontend.Session;

public interface RootHandler {
  public void handle(Session session, InitSetupAccountRequest request, SimpleResponder responder);

  public void handle(Session session, InitConvertGoogleUserRequest request, InitiationResponder responder);

  public void handle(Session session, InitCompleteAccountRequest request, InitiationResponder responder);

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

  public void handle(Session session, SpaceUsageRequest request, BillingUsageResponder responder);

  public void handle(Session session, SpaceGetRequest request, PlanResponder responder);

  public void handle(Session session, SpaceSetRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceRedeployKickRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceSetRxhtmlRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceGetRxhtmlRequest request, RxhtmlResponder responder);

  public void handle(Session session, SpaceDeleteRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceSetRoleRequest request, SimpleResponder responder);

  public void handle(Session session, SpaceListDevelopersRequest request, DeveloperResponder responder);

  public void handle(Session session, SpaceReflectRequest request, ReflectionResponder responder);

  public void handle(Session session, SpaceListRequest request, SpaceListingResponder responder);

  public void handle(Session session, DomainMapRequest request, SimpleResponder responder);

  public void handle(Session session, DomainMapDocumentRequest request, SimpleResponder responder);

  public void handle(Session session, DomainListRequest request, DomainListingResponder responder);

  public void handle(Session session, DomainUnmapRequest request, SimpleResponder responder);

  public void handle(Session session, DomainGetRequest request, DomainPolicyResponder responder);

  public void handle(Session session, DocumentAuthorizeRequest request, InitiationResponder responder);

  public void handle(Session session, DocumentAuthorizeDomainRequest request, InitiationResponder responder);

  public void handle(Session session, DocumentCreateRequest request, SimpleResponder responder);

  public void handle(Session session, DocumentDeleteRequest request, SimpleResponder responder);

  public void handle(Session session, DocumentListRequest request, KeyListingResponder responder);

  public void handle(Session session, MessageDirectSendRequest request, SeqResponder responder);

  public void handle(Session session, MessageDirectSendOnceRequest request, SeqResponder responder);

  public DocumentStreamHandler handle(Session session, ConnectionCreateRequest request, DataResponder responder);

  public DocumentStreamHandler handle(Session session, ConnectionCreateViaDomainRequest request, DataResponder responder);

  public void handle(Session session, DocumentsHashPasswordRequest request, HashedPasswordResponder responder);

  public void handle(Session session, ConfigureMakeOrGetAssetKeyRequest request, AssetKeyResponder responder);

  public AttachmentUploadHandler handle(Session session, AttachmentStartRequest request, ProgressResponder responder);

  public void handle(Session session, SuperCheckInRequest request, SimpleResponder responder);

  public void handle(Session session, SuperListAutomaticDomainsRequest request, AutomaticDomainListingResponder responder);

  public void handle(Session session, SuperSetDomainCertificateRequest request, SimpleResponder responder);

  public void disconnect();

}

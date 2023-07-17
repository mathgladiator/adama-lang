/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.devbox;

import org.adamalang.api.*;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.connection.Session;

public class DevBoxSocketAPI implements RootHandler {
  @Override
  public void handle(Session session, InitSetupAccountRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, InitConvertGoogleUserRequest request, InitiationResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, InitCompleteAccountRequest request, InitiationResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, AccountSetPasswordRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, AccountGetPaymentPlanRequest request, PaymentResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, AccountLoginRequest request, InitiationResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, ProbeRequest request, SimpleResponder responder) {
    responder.complete();
  }

  @Override
  public void handle(Session session, AuthorityCreateRequest request, ClaimResultResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, AuthoritySetRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, AuthorityGetRequest request, KeystoreResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, AuthorityListRequest request, AuthorityListingResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, AuthorityDestroyRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, SpaceCreateRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, SpaceGenerateKeyRequest request, KeyPairResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, SpaceUsageRequest request, BillingUsageResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, SpaceGetRequest request, PlanResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, SpaceSetRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, SpaceRedeployKickRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, SpaceSetRxhtmlRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, SpaceGetRxhtmlRequest request, RxhtmlResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, SpaceDeleteRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, SpaceSetRoleRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, SpaceReflectRequest request, ReflectionResponder responder) {
    // TODO
  }

  @Override
  public void handle(Session session, SpaceListRequest request, SpaceListingResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, DomainMapRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, DomainMapDocumentRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, DomainListRequest request, DomainListingResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, DomainUnmapRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, DomainGetRequest request, DomainPolicyResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, DocumentAuthorizeRequest request, InitiationResponder responder) {
    // TODO
  }

  @Override
  public void handle(Session session, DocumentCreateRequest request, SimpleResponder responder) {
    // TODO
  }

  @Override
  public void handle(Session session, DocumentDeleteRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, DocumentListRequest request, KeyListingResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, MessageDirectSendRequest request, SeqResponder responder) {
    // TODO
  }

  @Override
  public void handle(Session session, MessageDirectSendOnceRequest request, SeqResponder responder) {
    // TODO
  }

  @Override
  public DocumentStreamHandler handle(Session session, ConnectionCreateRequest request, DataResponder responder) {
    // TODO
    return null;
  }

  @Override
  public DocumentStreamHandler handle(Session session, ConnectionCreateViaDomainRequest request, DataResponder responder) {
    // TODO: pull in settings
    responder.error(new ErrorCodeException(0));
    return null;
  }

  @Override
  public void handle(Session session, DocumentsHashPasswordRequest request, HashedPasswordResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, ConfigureMakeOrGetAssetKeyRequest request, AssetKeyResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public AttachmentUploadHandler handle(Session session, AttachmentStartRequest request, ProgressResponder responder) {
    responder.error(new ErrorCodeException(0));
    return null;
  }

  @Override
  public void handle(Session session, SuperCheckInRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, SuperListAutomaticDomainsRequest request, AutomaticDomainListingResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void handle(Session session, SuperSetDomainCertificateRequest request, SimpleResponder responder) {
    responder.error(new ErrorCodeException(0));
  }

  @Override
  public void disconnect() {

  }
}

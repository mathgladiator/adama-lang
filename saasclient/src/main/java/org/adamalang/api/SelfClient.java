/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.common.Callback;
import org.adamalang.common.Stream;
import org.adamalang.web.client.socket.MultiWebClientRetryPool;
import org.adamalang.web.client.socket.WebClientConnection;

public class SelfClient {
private final MultiWebClientRetryPool pool;
  
  public SelfClient(MultiWebClientRetryPool pool) {
    this.pool = pool;
  }

  /** init/setup-account */
  public void initSetupAccount(ClientInitSetupAccountRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "init/setup-account");
    node.put("email", request.email);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** init/convert-google-user */
  public void initConvertGoogleUser(ClientInitConvertGoogleUserRequest request, Callback<ClientInitiationResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "init/convert-google-user");
    node.put("access-token", request.accessToken);
    pool.requestResponse(node, (obj) -> new ClientInitiationResponse(obj), callback);
  }

  /** init/complete-account */
  public void initCompleteAccount(ClientInitCompleteAccountRequest request, Callback<ClientInitiationResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "init/complete-account");
    node.put("email", request.email);
    node.put("revoke", request.revoke);
    node.put("code", request.code);
    pool.requestResponse(node, (obj) -> new ClientInitiationResponse(obj), callback);
  }

  /** deinit */
  public void deinit(ClientDeinitRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "deinit");
    node.put("identity", request.identity);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** account/set-password */
  public void accountSetPassword(ClientAccountSetPasswordRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "account/set-password");
    node.put("identity", request.identity);
    node.put("password", request.password);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** account/get-payment-plan */
  public void accountGetPaymentPlan(ClientAccountGetPaymentPlanRequest request, Callback<ClientPaymentResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "account/get-payment-plan");
    node.put("identity", request.identity);
    pool.requestResponse(node, (obj) -> new ClientPaymentResponse(obj), callback);
  }

  /** account/login */
  public void accountLogin(ClientAccountLoginRequest request, Callback<ClientInitiationResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "account/login");
    node.put("email", request.email);
    node.put("password", request.password);
    pool.requestResponse(node, (obj) -> new ClientInitiationResponse(obj), callback);
  }

  /** probe */
  public void probe(ClientProbeRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "probe");
    node.put("identity", request.identity);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** authority/create */
  public void authorityCreate(ClientAuthorityCreateRequest request, Callback<ClientClaimResultResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "authority/create");
    node.put("identity", request.identity);
    pool.requestResponse(node, (obj) -> new ClientClaimResultResponse(obj), callback);
  }

  /** authority/set */
  public void authoritySet(ClientAuthoritySetRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "authority/set");
    node.put("identity", request.identity);
    node.put("authority", request.authority);
    node.set("key-store", request.keyStore);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** authority/get */
  public void authorityGet(ClientAuthorityGetRequest request, Callback<ClientKeystoreResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "authority/get");
    node.put("identity", request.identity);
    node.put("authority", request.authority);
    pool.requestResponse(node, (obj) -> new ClientKeystoreResponse(obj), callback);
  }

  /** authority/list */
  public void authorityList(ClientAuthorityListRequest request, Stream<ClientAuthorityListingResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "authority/list");
    node.put("identity", request.identity);
    pool.requestStream(node, (obj) -> new ClientAuthorityListingResponse(obj), streamback);
  }

  /** authority/destroy */
  public void authorityDestroy(ClientAuthorityDestroyRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "authority/destroy");
    node.put("identity", request.identity);
    node.put("authority", request.authority);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** space/create */
  public void spaceCreate(ClientSpaceCreateRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/create");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("template", request.template);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** space/generate-key */
  public void spaceGenerateKey(ClientSpaceGenerateKeyRequest request, Callback<ClientKeyPairResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/generate-key");
    node.put("identity", request.identity);
    node.put("space", request.space);
    pool.requestResponse(node, (obj) -> new ClientKeyPairResponse(obj), callback);
  }

  /** space/get */
  public void spaceGet(ClientSpaceGetRequest request, Callback<ClientPlanResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/get");
    node.put("identity", request.identity);
    node.put("space", request.space);
    pool.requestResponse(node, (obj) -> new ClientPlanResponse(obj), callback);
  }

  /** space/set */
  public void spaceSet(ClientSpaceSetRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/set");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.set("plan", request.plan);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** space/redeploy-kick */
  public void spaceRedeployKick(ClientSpaceRedeployKickRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/redeploy-kick");
    node.put("identity", request.identity);
    node.put("space", request.space);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** space/set-rxhtml */
  public void spaceSetRxhtml(ClientSpaceSetRxhtmlRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/set-rxhtml");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("rxhtml", request.rxhtml);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** space/get-rxhtml */
  public void spaceGetRxhtml(ClientSpaceGetRxhtmlRequest request, Callback<ClientRxhtmlResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/get-rxhtml");
    node.put("identity", request.identity);
    node.put("space", request.space);
    pool.requestResponse(node, (obj) -> new ClientRxhtmlResponse(obj), callback);
  }

  /** space/set-policy */
  public void spaceSetPolicy(ClientSpaceSetPolicyRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/set-policy");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.set("access-policy", request.accessPolicy);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** space/get-policy */
  public void spaceGetPolicy(ClientSpaceGetPolicyRequest request, Callback<ClientAccessPolicyResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/get-policy");
    node.put("identity", request.identity);
    node.put("space", request.space);
    pool.requestResponse(node, (obj) -> new ClientAccessPolicyResponse(obj), callback);
  }

  /** space/metrics */
  public void spaceMetrics(ClientSpaceMetricsRequest request, Callback<ClientMetricsAggregateResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/metrics");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("prefix", request.prefix);
    node.set("metric-query", request.metricQuery);
    pool.requestResponse(node, (obj) -> new ClientMetricsAggregateResponse(obj), callback);
  }

  /** space/delete */
  public void spaceDelete(ClientSpaceDeleteRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/delete");
    node.put("identity", request.identity);
    node.put("space", request.space);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** space/set-role */
  public void spaceSetRole(ClientSpaceSetRoleRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/set-role");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("email", request.email);
    node.put("role", request.role);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** space/list-developers */
  public void spaceListDevelopers(ClientSpaceListDevelopersRequest request, Stream<ClientDeveloperResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/list-developers");
    node.put("identity", request.identity);
    node.put("space", request.space);
    pool.requestStream(node, (obj) -> new ClientDeveloperResponse(obj), streamback);
  }

  /** space/reflect */
  public void spaceReflect(ClientSpaceReflectRequest request, Callback<ClientReflectionResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/reflect");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    pool.requestResponse(node, (obj) -> new ClientReflectionResponse(obj), callback);
  }

  /** space/list */
  public void spaceList(ClientSpaceListRequest request, Stream<ClientSpaceListingResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "space/list");
    node.put("identity", request.identity);
    node.put("marker", request.marker);
    node.put("limit", request.limit);
    pool.requestStream(node, (obj) -> new ClientSpaceListingResponse(obj), streamback);
  }

  /** domain/map */
  public void domainMap(ClientDomainMapRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "domain/map");
    node.put("identity", request.identity);
    node.put("domain", request.domain);
    node.put("space", request.space);
    node.put("certificate", request.certificate);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** domain/reflect */
  public void domainReflect(ClientDomainReflectRequest request, Callback<ClientReflectionResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "domain/reflect");
    node.put("identity", request.identity);
    node.put("domain", request.domain);
    pool.requestResponse(node, (obj) -> new ClientReflectionResponse(obj), callback);
  }

  /** domain/map-document */
  public void domainMapDocument(ClientDomainMapDocumentRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "domain/map-document");
    node.put("identity", request.identity);
    node.put("domain", request.domain);
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("route", request.route);
    node.put("certificate", request.certificate);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** domain/list */
  public void domainList(ClientDomainListRequest request, Stream<ClientDomainListingResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "domain/list");
    node.put("identity", request.identity);
    pool.requestStream(node, (obj) -> new ClientDomainListingResponse(obj), streamback);
  }

  /** domain/list-by-space */
  public void domainListBySpace(ClientDomainListBySpaceRequest request, Stream<ClientDomainListingResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "domain/list-by-space");
    node.put("identity", request.identity);
    node.put("space", request.space);
    pool.requestStream(node, (obj) -> new ClientDomainListingResponse(obj), streamback);
  }

  /** domain/unmap */
  public void domainUnmap(ClientDomainUnmapRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "domain/unmap");
    node.put("identity", request.identity);
    node.put("domain", request.domain);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** domain/get */
  public void domainGet(ClientDomainGetRequest request, Callback<ClientDomainPolicyResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "domain/get");
    node.put("identity", request.identity);
    node.put("domain", request.domain);
    pool.requestResponse(node, (obj) -> new ClientDomainPolicyResponse(obj), callback);
  }

  /** document/authorize */
  public void documentAuthorize(ClientDocumentAuthorizeRequest request, Callback<ClientInitiationResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "document/authorize");
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("username", request.username);
    node.put("password", request.password);
    pool.requestResponse(node, (obj) -> new ClientInitiationResponse(obj), callback);
  }

  /** document/authorize-domain */
  public void documentAuthorizeDomain(ClientDocumentAuthorizeDomainRequest request, Callback<ClientInitiationResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "document/authorize-domain");
    node.put("domain", request.domain);
    node.put("username", request.username);
    node.put("password", request.password);
    pool.requestResponse(node, (obj) -> new ClientInitiationResponse(obj), callback);
  }

  /** document/create */
  public void documentCreate(ClientDocumentCreateRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "document/create");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("entropy", request.entropy);
    node.set("arg", request.arg);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** document/delete */
  public void documentDelete(ClientDocumentDeleteRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "document/delete");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** document/list */
  public void documentList(ClientDocumentListRequest request, Stream<ClientKeyListingResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "document/list");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("marker", request.marker);
    node.put("limit", request.limit);
    pool.requestStream(node, (obj) -> new ClientKeyListingResponse(obj), streamback);
  }

  /** message/direct-send */
  public void messageDirectSend(ClientMessageDirectSendRequest request, Callback<ClientSeqResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "message/direct-send");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("channel", request.channel);
    node.set("message", request.message);
    pool.requestResponse(node, (obj) -> new ClientSeqResponse(obj), callback);
  }

  /** message/direct-send-once */
  public void messageDirectSendOnce(ClientMessageDirectSendOnceRequest request, Callback<ClientSeqResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "message/direct-send-once");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("dedupe", request.dedupe);
    node.put("channel", request.channel);
    node.set("message", request.message);
    pool.requestResponse(node, (obj) -> new ClientSeqResponse(obj), callback);
  }

  /** connection/create */
  public void connectionCreate(ClientConnectionCreateRequest request, Callback<DocumentStreamHandler> callback, Stream<ClientDataResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "connection/create");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    node.set("viewer-state", request.viewerState);
    pool.requestStream(node, (wcc, id) -> new DocumentStreamHandler(wcc, id), (obj) -> new ClientDataResponse(obj), callback, streamback);
  }

  /** connection/create-via-domain */
  public void connectionCreateViaDomain(ClientConnectionCreateViaDomainRequest request, Callback<DocumentStreamHandler> callback, Stream<ClientDataResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "connection/create-via-domain");
    node.put("identity", request.identity);
    node.put("domain", request.domain);
    node.set("viewer-state", request.viewerState);
    pool.requestStream(node, (wcc, id) -> new DocumentStreamHandler(wcc, id), (obj) -> new ClientDataResponse(obj), callback, streamback);
  }

  /** documents/hash-password */
  public void documentsHashPassword(ClientDocumentsHashPasswordRequest request, Callback<ClientHashedPasswordResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "documents/hash-password");
    node.put("password", request.password);
    pool.requestResponse(node, (obj) -> new ClientHashedPasswordResponse(obj), callback);
  }

  /** billing-connection/create */
  public void billingConnectionCreate(ClientBillingConnectionCreateRequest request, Callback<DocumentStreamHandler> callback, Stream<ClientDataResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "billing-connection/create");
    node.put("identity", request.identity);
    pool.requestStream(node, (wcc, id) -> new DocumentStreamHandler(wcc, id), (obj) -> new ClientDataResponse(obj), callback, streamback);
  }

  /** configure/make-or-get-asset-key */
  public void configureMakeOrGetAssetKey(ClientConfigureMakeOrGetAssetKeyRequest request, Callback<ClientAssetKeyResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "configure/make-or-get-asset-key");
    pool.requestResponse(node, (obj) -> new ClientAssetKeyResponse(obj), callback);
  }

  /** attachment/start */
  public void attachmentStart(ClientAttachmentStartRequest request, Callback<AttachmentUploadHandler> callback, Stream<ClientProgressResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "attachment/start");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("filename", request.filename);
    node.put("content-type", request.contentType);
    pool.requestStream(node, (wcc, id) -> new AttachmentUploadHandler(wcc, id), (obj) -> new ClientProgressResponse(obj), callback, streamback);
  }

  /** attachment/start-by-domain */
  public void attachmentStartByDomain(ClientAttachmentStartByDomainRequest request, Callback<AttachmentUploadHandler> callback, Stream<ClientProgressResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "attachment/start-by-domain");
    node.put("identity", request.identity);
    node.put("domain", request.domain);
    node.put("filename", request.filename);
    node.put("content-type", request.contentType);
    pool.requestStream(node, (wcc, id) -> new AttachmentUploadHandler(wcc, id), (obj) -> new ClientProgressResponse(obj), callback, streamback);
  }

  /** super/check-in */
  public void superCheckIn(ClientSuperCheckInRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "super/check-in");
    node.put("identity", request.identity);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** super/list-automatic-domains */
  public void superListAutomaticDomains(ClientSuperListAutomaticDomainsRequest request, Stream<ClientAutomaticDomainListingResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "super/list-automatic-domains");
    node.put("identity", request.identity);
    node.put("timestamp", request.timestamp);
    pool.requestStream(node, (obj) -> new ClientAutomaticDomainListingResponse(obj), streamback);
  }

  /** super/set-domain-certificate */
  public void superSetDomainCertificate(ClientSuperSetDomainCertificateRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "super/set-domain-certificate");
    node.put("identity", request.identity);
    node.put("domain", request.domain);
    node.put("certificate", request.certificate);
    node.put("timestamp", request.timestamp);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** regional/domain-lookup */
  public void regionalDomainLookup(ClientRegionalDomainLookupRequest request, Callback<ClientDomainRawResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/domain-lookup");
    node.put("identity", request.identity);
    node.put("domain", request.domain);
    pool.requestResponse(node, (obj) -> new ClientDomainRawResponse(obj), callback);
  }

  /** regional/emit-metrics */
  public void regionalEmitMetrics(ClientRegionalEmitMetricsRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/emit-metrics");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    node.set("metrics", request.metrics);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** regional/init-host */
  public void regionalInitHost(ClientRegionalInitHostRequest request, Callback<ClientHostInitResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/init-host");
    node.put("identity", request.identity);
    node.put("region", request.region);
    node.put("machine", request.machine);
    node.put("role", request.role);
    node.put("public-key", request.publicKey);
    pool.requestResponse(node, (obj) -> new ClientHostInitResponse(obj), callback);
  }

  /** regional/finder/find */
  public void regionalFinderFind(ClientRegionalFinderFindRequest request, Callback<ClientFinderResultResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/finder/find");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    pool.requestResponse(node, (obj) -> new ClientFinderResultResponse(obj), callback);
  }

  /** regional/finder/free */
  public void regionalFinderFree(ClientRegionalFinderFreeRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/finder/free");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** regional/finder/bind */
  public void regionalFinderBind(ClientRegionalFinderBindRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/finder/bind");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** regional/finder/delete/mark */
  public void regionalFinderDeleteMark(ClientRegionalFinderDeleteMarkRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/finder/delete/mark");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** regional/finder/delete/commit */
  public void regionalFinderDeleteCommit(ClientRegionalFinderDeleteCommitRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/finder/delete/commit");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** regional/finder/back-up */
  public void regionalFinderBackUp(ClientRegionalFinderBackUpRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/finder/back-up");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("region", request.region);
    node.put("machine", request.machine);
    node.put("archive", request.archive);
    node.put("seq", request.seq);
    node.put("delta-bytes", request.deltaBytes);
    node.put("asset-bytes", request.assetBytes);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** regional/finder/list */
  public void regionalFinderList(ClientRegionalFinderListRequest request, Stream<ClientKeysResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/finder/list");
    node.put("identity", request.identity);
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestStream(node, (obj) -> new ClientKeysResponse(obj), streamback);
  }

  /** regional/finder/deletion-list */
  public void regionalFinderDeletionList(ClientRegionalFinderDeletionListRequest request, Stream<ClientKeysResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/finder/deletion-list");
    node.put("identity", request.identity);
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestStream(node, (obj) -> new ClientKeysResponse(obj), streamback);
  }

  /** regional/auth */
  public void regionalAuth(ClientRegionalAuthRequest request, Callback<ClientAuthResultResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/auth");
    node.put("identity", request.identity);
    pool.requestResponse(node, (obj) -> new ClientAuthResultResponse(obj), callback);
  }

  /** regional/get-plan */
  public void regionalGetPlan(ClientRegionalGetPlanRequest request, Callback<ClientPlanWithKeysResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/get-plan");
    node.put("identity", request.identity);
    node.put("space", request.space);
    pool.requestResponse(node, (obj) -> new ClientPlanWithKeysResponse(obj), callback);
  }

  /** regional/capacity/add */
  public void regionalCapacityAdd(ClientRegionalCapacityAddRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/capacity/add");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** regional/capacity/remove */
  public void regionalCapacityRemove(ClientRegionalCapacityRemoveRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/capacity/remove");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** regional/capacity/nuke */
  public void regionalCapacityNuke(ClientRegionalCapacityNukeRequest request, Callback<ClientSimpleResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/capacity/nuke");
    node.put("identity", request.identity);
    node.put("space", request.space);
    pool.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
  }

  /** regional/capacity/list-space */
  public void regionalCapacityListSpace(ClientRegionalCapacityListSpaceRequest request, Stream<ClientCapacityListResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/capacity/list-space");
    node.put("identity", request.identity);
    node.put("space", request.space);
    pool.requestStream(node, (obj) -> new ClientCapacityListResponse(obj), streamback);
  }

  /** regional/capacity/list-machine */
  public void regionalCapacityListMachine(ClientRegionalCapacityListMachineRequest request, Stream<ClientCapacityListResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/capacity/list-machine");
    node.put("identity", request.identity);
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestStream(node, (obj) -> new ClientCapacityListResponse(obj), streamback);
  }

  /** regional/capacity/list-region */
  public void regionalCapacityListRegion(ClientRegionalCapacityListRegionRequest request, Stream<ClientCapacityListResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/capacity/list-region");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("region", request.region);
    pool.requestStream(node, (obj) -> new ClientCapacityListResponse(obj), streamback);
  }

  /** regional/capacity/pick-space-host */
  public void regionalCapacityPickSpaceHost(ClientRegionalCapacityPickSpaceHostRequest request, Callback<ClientCapacityHostResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/capacity/pick-space-host");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("region", request.region);
    pool.requestResponse(node, (obj) -> new ClientCapacityHostResponse(obj), callback);
  }

  /** regional/capacity/pick-space-host-new */
  public void regionalCapacityPickSpaceHostNew(ClientRegionalCapacityPickSpaceHostNewRequest request, Callback<ClientCapacityHostResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "regional/capacity/pick-space-host-new");
    node.put("identity", request.identity);
    node.put("space", request.space);
    node.put("region", request.region);
    pool.requestResponse(node, (obj) -> new ClientCapacityHostResponse(obj), callback);
  }

  public class AttachmentUploadHandler {
    public final WebClientConnection _direct;
    public final int _id;
    
    public AttachmentUploadHandler(WebClientConnection _direct, int _id) {
      this._direct = _direct;
      this._id = _id;
    }

    /** attachment/append */
    public void append(ClientAttachmentAppendRequest request, Callback<ClientSimpleResponse> callback) {
      ObjectNode node = Json.newJsonObject();
      node.put("method", "attachment/append");
      node.put("upload", _id);
      node.put("chunk-md5", request.chunkMd5);
      node.put("base64-bytes", request.base64Bytes);
      _direct.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
    }
  
    /** attachment/finish */
    public void finish(ClientAttachmentFinishRequest request, Callback<ClientAssetIdResponse> callback) {
      ObjectNode node = Json.newJsonObject();
      node.put("method", "attachment/finish");
      node.put("upload", _id);
      _direct.requestResponse(node, (obj) -> new ClientAssetIdResponse(obj), callback);
    }
  }

  public class DocumentStreamHandler {
    public final WebClientConnection _direct;
    public final int _id;
    
    public DocumentStreamHandler(WebClientConnection _direct, int _id) {
      this._direct = _direct;
      this._id = _id;
    }

    /** connection/send */
    public void send(ClientConnectionSendRequest request, Callback<ClientSeqResponse> callback) {
      ObjectNode node = Json.newJsonObject();
      node.put("method", "connection/send");
      node.put("connection", _id);
      node.put("channel", request.channel);
      node.set("message", request.message);
      _direct.requestResponse(node, (obj) -> new ClientSeqResponse(obj), callback);
    }
  
    /** connection/password */
    public void password(ClientConnectionPasswordRequest request, Callback<ClientSeqResponse> callback) {
      ObjectNode node = Json.newJsonObject();
      node.put("method", "connection/password");
      node.put("connection", _id);
      node.put("username", request.username);
      node.put("password", request.password);
      node.put("new_password", request.new_password);
      _direct.requestResponse(node, (obj) -> new ClientSeqResponse(obj), callback);
    }
  
    /** connection/send-once */
    public void sendOnce(ClientConnectionSendOnceRequest request, Callback<ClientSeqResponse> callback) {
      ObjectNode node = Json.newJsonObject();
      node.put("method", "connection/send-once");
      node.put("connection", _id);
      node.put("channel", request.channel);
      node.put("dedupe", request.dedupe);
      node.set("message", request.message);
      _direct.requestResponse(node, (obj) -> new ClientSeqResponse(obj), callback);
    }
  
    /** connection/can-attach */
    public void canAttach(ClientConnectionCanAttachRequest request, Callback<ClientYesResponse> callback) {
      ObjectNode node = Json.newJsonObject();
      node.put("method", "connection/can-attach");
      node.put("connection", _id);
      _direct.requestResponse(node, (obj) -> new ClientYesResponse(obj), callback);
    }
  
    /** connection/attach */
    public void attach(ClientConnectionAttachRequest request, Callback<ClientSeqResponse> callback) {
      ObjectNode node = Json.newJsonObject();
      node.put("method", "connection/attach");
      node.put("connection", _id);
      node.put("asset-id", request.assetId);
      node.put("filename", request.filename);
      node.put("content-type", request.contentType);
      node.put("size", request.size);
      node.put("digest-md5", request.digestMd5);
      node.put("digest-sha384", request.digestSha384);
      _direct.requestResponse(node, (obj) -> new ClientSeqResponse(obj), callback);
    }
  
    /** connection/update */
    public void update(ClientConnectionUpdateRequest request, Callback<ClientSimpleResponse> callback) {
      ObjectNode node = Json.newJsonObject();
      node.put("method", "connection/update");
      node.put("connection", _id);
      node.set("viewer-state", request.viewerState);
      _direct.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
    }
  
    /** connection/end */
    public void end(ClientConnectionEndRequest request, Callback<ClientSimpleResponse> callback) {
      ObjectNode node = Json.newJsonObject();
      node.put("method", "connection/end");
      node.put("connection", _id);
      _direct.requestResponse(node, (obj) -> new ClientSimpleResponse(obj), callback);
    }
  }
}

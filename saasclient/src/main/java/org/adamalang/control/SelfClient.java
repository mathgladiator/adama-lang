/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.control;

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

  /** global/machine/start */
  public void globalMachineStart(ClientGlobalMachineStartRequest request, Callback<ClientMachineStartResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/machine/start");
    node.put("machine-identity", request.machineIdentity);
    node.put("role", request.role);
    pool.requestResponse(node, (obj) -> new ClientMachineStartResponse(obj), callback);
  }

  /** global/finder/find */
  public void globalFinderFind(ClientGlobalFinderFindRequest request, Callback<ClientFoundResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/finder/find");
    node.put("space", request.space);
    node.put("key", request.key);
    pool.requestResponse(node, (obj) -> new ClientFoundResponse(obj), callback);
  }

  /** global/finder/findbind */
  public void globalFinderFindbind(ClientGlobalFinderFindbindRequest request, Callback<ClientFoundResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/finder/findbind");
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestResponse(node, (obj) -> new ClientFoundResponse(obj), callback);
  }

  /** global/finder/free */
  public void globalFinderFree(ClientGlobalFinderFreeRequest request, Callback<ClientVoidResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/finder/free");
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestResponse(node, (obj) -> new ClientVoidResponse(obj), callback);
  }

  /** global/finder/delete/mark */
  public void globalFinderDeleteMark(ClientGlobalFinderDeleteMarkRequest request, Callback<ClientVoidResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/finder/delete/mark");
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestResponse(node, (obj) -> new ClientVoidResponse(obj), callback);
  }

  /** global/finder/delete/commit */
  public void globalFinderDeleteCommit(ClientGlobalFinderDeleteCommitRequest request, Callback<ClientVoidResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/finder/delete/commit");
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestResponse(node, (obj) -> new ClientVoidResponse(obj), callback);
  }

  /** global/finder/back-up */
  public void globalFinderBackUp(ClientGlobalFinderBackUpRequest request, Callback<ClientVoidResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/finder/back-up");
    node.put("space", request.space);
    node.put("key", request.key);
    node.put("region", request.region);
    node.put("machine", request.machine);
    node.put("archive-key", request.archiveKey);
    pool.requestResponse(node, (obj) -> new ClientVoidResponse(obj), callback);
  }

  /** global/finder/list */
  public void globalFinderList(ClientGlobalFinderListRequest request, Stream<ClientKeyidResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/finder/list");
    node.put("region", request.region);
    node.put("machine", request.machine);
    pool.requestStream(node, (obj) -> new ClientKeyidResponse(obj), streamback);
  }

  /** global/authorities/create */
  public void globalAuthoritiesCreate(ClientGlobalAuthoritiesCreateRequest request, Callback<ClientAuthorityResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/authorities/create");
    node.put("owner", request.owner);
    pool.requestResponse(node, (obj) -> new ClientAuthorityResponse(obj), callback);
  }

  /** global/authorities/set */
  public void globalAuthoritiesSet(ClientGlobalAuthoritiesSetRequest request, Callback<ClientVoidResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/authorities/set");
    node.put("owner", request.owner);
    node.put("authority", request.authority);
    node.put("keystore", request.keystore);
    pool.requestResponse(node, (obj) -> new ClientVoidResponse(obj), callback);
  }

  /** global/authorities/get/public */
  public void globalAuthoritiesGetPublic(ClientGlobalAuthoritiesGetPublicRequest request, Callback<ClientKeystoreResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/authorities/get/public");
    node.put("authority", request.authority);
    pool.requestResponse(node, (obj) -> new ClientKeystoreResponse(obj), callback);
  }

  /** global/authorities/get/protected */
  public void globalAuthoritiesGetProtected(ClientGlobalAuthoritiesGetProtectedRequest request, Callback<ClientKeystoreResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/authorities/get/protected");
    node.put("owner", request.owner);
    node.put("authority", request.authority);
    pool.requestResponse(node, (obj) -> new ClientKeystoreResponse(obj), callback);
  }

  /** global/authorities/list */
  public void globalAuthoritiesList(ClientGlobalAuthoritiesListRequest request, Stream<ClientAuthorityListResponse> streamback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/authorities/list");
    node.put("owner", request.owner);
    pool.requestStream(node, (obj) -> new ClientAuthorityListResponse(obj), streamback);
  }

  /** global/authorities/delete */
  public void globalAuthoritiesDelete(ClientGlobalAuthoritiesDeleteRequest request, Callback<ClientVoidResponse> callback) {
    ObjectNode node = Json.newJsonObject();
    node.put("method", "global/authorities/delete");
    node.put("owner", request.owner);
    node.put("authority", request.authority);
    pool.requestResponse(node, (obj) -> new ClientVoidResponse(obj), callback);
  }
}

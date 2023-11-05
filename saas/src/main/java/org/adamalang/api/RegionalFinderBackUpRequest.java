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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.auth.AuthenticatedUser;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.contracts.data.SpacePolicy;
import org.adamalang.frontend.Session;
import org.adamalang.validators.ValidateKey;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/** Set the archive key */
public class RegionalFinderBackUpRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String space;
  public final SpacePolicy policy;
  public final String key;
  public final String region;
  public final String machine;
  public final String archive;
  public final Integer seq;
  public final Long deltaBytes;
  public final Long assetBytes;

  public RegionalFinderBackUpRequest(final String identity, final AuthenticatedUser who, final String space, final SpacePolicy policy, final String key, final String region, final String machine, final String archive, final Integer seq, final Long deltaBytes, final Long assetBytes) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.key = key;
    this.region = region;
    this.machine = machine;
    this.archive = archive;
    this.seq = seq;
    this.deltaBytes = deltaBytes;
    this.assetBytes = assetBytes;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<RegionalFinderBackUpRequest> callback) {
    try {
      final BulkLatch<RegionalFinderBackUpRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String space = request.getStringNormalize("space", true, 461828);
      ValidateSpace.validate(space);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String key = request.getString("key", true, 466947);
      ValidateKey.validate(key);
      final String region = request.getString("region", true, 9006);
      final String machine = request.getString("machine", true, 9005);
      final String archive = request.getString("archive", true, 9007);
      final Integer seq = request.getInteger("seq", true, 461836);
      final Long deltaBytes = request.getLong("delta-bytes", true, 492531);
      final Long assetBytes = request.getLong("asset-bytes", true, 460787);
      _latch.with(() -> new RegionalFinderBackUpRequest(identity, who.get(), space, policy.get(), key, region, machine, archive, seq, deltaBytes, assetBytes));
      nexus.identityService.execute(session, identity, who);
      nexus.spaceService.execute(session, space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("regionalfinderbackup-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
    _node.put("space", space);
    org.adamalang.contracts.SpacePolicyLocator.logInto(policy, _node);
    _node.put("key", key);
    _node.put("region", region);
    _node.put("machine", machine);
    _node.put("archive", archive);
    _node.put("seq", seq);
    _node.put("delta-bytes", deltaBytes);
    _node.put("asset-bytes", assetBytes);
  }
}

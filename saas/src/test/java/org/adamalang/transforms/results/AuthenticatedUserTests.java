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
package org.adamalang.transforms.results;

import org.adamalang.contracts.data.AuthenticatedUser;
import org.adamalang.impl.common.PublicKeyCodec;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.contracts.data.ParsedToken;
import org.adamalang.web.io.ConnectionContext;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;

public class AuthenticatedUserTests {

  @Test
  public void flow() throws Exception {
    AuthenticatedUser user = new AuthenticatedUser(123, new NtPrincipal("jeff", "adama"), new ConnectionContext("origin", "ip", "agent", "asset-key"));
    KeyPair pair = PublicKeyCodec.inventHostKey();
    String identity = user.asIdentity(42, pair.getPrivate());
    ParsedToken parsedToken = new ParsedToken(identity);
    Assert.assertEquals(42, parsedToken.key_id);
    Assert.assertEquals(123, parsedToken.proxy_user_id);
    Assert.assertEquals("jeff", parsedToken.sub);
    Assert.assertEquals("host", parsedToken.iss);
    Assert.assertEquals("adama", parsedToken.proxy_authority);
    Assert.assertEquals("origin", parsedToken.proxy_origin);
    Assert.assertEquals("ip", parsedToken.proxy_ip);
    Assert.assertEquals("agent", parsedToken.proxy_useragent);
    Assert.assertEquals("asset-key", parsedToken.proxy_asset_key);
  }
}

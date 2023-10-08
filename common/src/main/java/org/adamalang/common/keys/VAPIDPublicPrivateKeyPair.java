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
package org.adamalang.common.keys;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import java.util.Base64;

/** Vapid keys; https://datatracker.ietf.org/doc/html/draft-ietf-webpush-vapid-01 */
public class VAPIDPublicPrivateKeyPair {
  public final String publicKeyBase64;
  public final String privateKeyBase64;
  public final PrivateKey privateKey;

  public VAPIDPublicPrivateKeyPair(String publicKeyBase64, String privateKeyBase64) throws Exception {
    this.publicKeyBase64 = publicKeyBase64;
    this.privateKeyBase64 = privateKeyBase64;
    AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
    parameters.init(new ECGenParameterSpec("secp256r1"));
    ECParameterSpec ecParameters = parameters.getParameterSpec(ECParameterSpec.class);
    ECPrivateKeySpec privateSpec = new ECPrivateKeySpec(new BigInteger(Base64.getDecoder().decode(privateKeyBase64)), ecParameters);
    privateKey = KeyFactory.getInstance("EC").generatePrivate(privateSpec);
  }
}

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
package org.adamalang.services.push.webpush;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Base64;

/** a subscription for web push */
public class Subscription {
  public final String endpoint;
  public final byte[] user;
  public final ECPublicKey p256dh;

  public Subscription(ObjectNode node) throws Exception {
    this.endpoint = node.get("endpoint").textValue();
    JsonNode keys = node.get("keys");
    this.user = Base64.getUrlDecoder().decode(keys.get("auth").textValue());
    AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
    parameters.init(new ECGenParameterSpec("secp256r1"));
    ECParameterSpec ecParameters = parameters.getParameterSpec(ECParameterSpec.class);
    byte[] publicRaw = Base64.getUrlDecoder().decode(keys.get("p256dh").textValue());
    BigInteger x = new BigInteger(1, publicRaw, 1, 32);
    BigInteger y = new BigInteger(1, publicRaw, 33, 32);
    ECPoint w = new ECPoint(x, y);
    ECPublicKeySpec publicSpec = new ECPublicKeySpec(w, ecParameters);
    KeyFactory kf = KeyFactory.getInstance("EC");
    this.p256dh = (ECPublicKey) kf.generatePublic(publicSpec);
  }
}

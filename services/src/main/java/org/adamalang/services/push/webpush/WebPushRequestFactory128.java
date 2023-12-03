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

import at.favre.lib.hkdf.HKDF;
import io.jsonwebtoken.Jwts;
import org.adamalang.common.keys.ECPublicKeyCodec;
import org.adamalang.common.keys.VAPIDPublicPrivateKeyPair;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpRequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

/** produce WebPush requests */
public class WebPushRequestFactory128 {
  private static final byte[] WEBPUSHINFO = "WebPush: info\u0000".getBytes(StandardCharsets.UTF_8);
  private static final byte[] ENC_4096 = make4096();
  private static final byte[] PADDING = new byte[] { 0x02 };

  private static byte[] make4096() {
    ByteBuffer buffer = ByteBuffer.allocate(4);
    buffer.putInt(4096);
    return buffer.array();
  }

  private final String email;
  private final SecureRandom random;

  public WebPushRequestFactory128(String email, SecureRandom random) {
    this.email = email;
    this.random = random;
  }

  public SimpleHttpRequest make(VAPIDPublicPrivateKeyPair keyPair, Subscription subscription, int ttlDays, byte[] payload) throws Exception {
    TreeMap<String, String> headers = new TreeMap<>();
    byte[] body = encrypt(subscription, payload);
    headers.put("Content-Encoding", "aes128gcm");
    headers.put("Content-Type", "application/octet-stream");
    headers.put("TTL", "" + (ttlDays * 86400));
    Date expiry = Date.from(LocalDateTime.now().plus(4, ChronoUnit.HOURS).atZone(ZoneId.systemDefault()).toInstant());
    URL url = new URL(subscription.endpoint);
    String token = Jwts.builder() //
        .expiration(expiry) //
        .subject("mailto:" + email) //
        .header()  //
        .add("typ", "JWT") //
        .and() //
        .audience() //
        .single(url.getProtocol() + "://" + url.getHost()) // TODO: WebPush needs a single now
        .signWith(keyPair.privateKey).compact();
    headers.put("Authorization", "vapid t=" + token + ", k=" + Base64.getUrlEncoder().withoutPadding().encodeToString(ECPublicKeyCodec.encode(keyPair.publicKey)));
    return new SimpleHttpRequest("POST", subscription.endpoint, headers, SimpleHttpRequestBody.WRAP(body));
  }

  public byte[] encrypt(Subscription subscription, byte[] payload) throws Exception {
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
    ECGenParameterSpec spec = new ECGenParameterSpec("secp256r1");
    generator.initialize(spec, this.random);
    KeyPair sender = generator.generateKeyPair();
    byte[] keyInfo = "Content-Encoding: aes128gcm\u0000".getBytes();
    byte[] nonceInfo = "Content-Encoding: nonce\u0000".getBytes();
    KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
    keyAgreement.init(sender.getPrivate());
    keyAgreement.doPhase(subscription.p256dh, true);
    byte[] secretGen = keyAgreement.generateSecret();
    ByteArrayOutputStream infoCat = new ByteArrayOutputStream();
    {
      infoCat.write(WEBPUSHINFO);
      infoCat.write(ECPublicKeyCodec.encode(subscription.p256dh));
      infoCat.write(ECPublicKeyCodec.encode((ECPublicKey) sender.getPublic()));
    }
    byte[] info = infoCat.toByteArray();
    byte[] secret = HKDF.fromHmacSha256().extractAndExpand(subscription.user, secretGen, info, 32);
    byte[] key = HKDF.fromHmacSha256().extractAndExpand(salt, secret, keyInfo, 16);
    byte[] nonce = HKDF.fromHmacSha256().extractAndExpand(salt, secret, nonceInfo, 12);
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    GCMParameterSpec params = new GCMParameterSpec(128, nonce);
    cipher.init(1, new SecretKeySpec(key, "AES"), params);
    byte[] keyIdBytes = ECPublicKeyCodec.encode((ECPublicKey) sender.getPublic());
    byte[] idlen = new byte[]{(byte)keyIdBytes.length};
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    output.write(salt);
    output.write(ENC_4096);
    output.write(idlen);
    output.write(keyIdBytes);
    output.write(cipher.update(payload));
    output.write(cipher.update(PADDING));
    output.write(cipher.doFinal());
    return output.toByteArray();
  }
}

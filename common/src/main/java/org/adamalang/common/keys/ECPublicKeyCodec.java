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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;

/** codec to encode ECPublicKey and ECPoint */
public class ECPublicKeyCodec {

  private static void write(BigInteger i, OutputStream o) throws Exception {
    byte[] bytes = i.toByteArray();
    if (bytes.length == 31) {
      o.write((byte) 0x00);
      o.write(bytes);
    } else if (bytes.length == 32) {
      o.write(bytes);
    } else if (bytes.length == 33 && bytes[0] == 0) {
      o.write(bytes, 1, bytes.length - 1);
    } else {
      throw new Exception("failed to generate:" + bytes.length);
    }
  }

  public static byte[] encode(ECPoint ecp) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
    outputStream.write((byte) 0x04);
    write(ecp.getAffineX(), outputStream);
    write(ecp.getAffineY(), outputStream);
    return outputStream.toByteArray();
  }

  public static byte[] encode(ECPublicKey key) throws Exception {
    return encode(key.getW());
  }
}

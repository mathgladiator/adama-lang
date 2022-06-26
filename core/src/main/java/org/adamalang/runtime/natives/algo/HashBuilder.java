package org.adamalang.runtime.natives.algo;

import org.adamalang.common.Hashing;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtDynamic;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** a streaming hasher of rich types */
public class HashBuilder {
  private static final byte[] BYTES_TRUE = new byte[] { 'T' };
  private static final byte[] BYTES_FALSE = new byte[] { 'T' };
  public final MessageDigest digest;

  public HashBuilder() {
    this.digest = Hashing.sha384();
  }

  public void hashNtDynamic(NtDynamic value) {
    digest.update(value.json.getBytes(StandardCharsets.UTF_8));
  }

  public void hashBoolean(final boolean b) {
    if (b) {
      digest.update(BYTES_TRUE);
    } else {
      digest.update(BYTES_FALSE);
    }
  }
  public void hashNtComplex(final NtComplex c) {
    hashDouble(c.real);
    hashDouble(c.imaginary);
  }
  public void hashDouble(final double d) {
    digest.update(("" + d).getBytes(StandardCharsets.UTF_8));
  }
  public void hashInteger(final int i) {
    digest.update(("" + i).getBytes(StandardCharsets.UTF_8));
  }

  public void hashNtAsset(final NtAsset a) {
    digest.update(a.id.getBytes(StandardCharsets.UTF_8));
    digest.update(a.md5.getBytes(StandardCharsets.UTF_8));
    digest.update(a.sha384.getBytes(StandardCharsets.UTF_8));
  }

  public void hashString(final String s) {
    digest.update(s.getBytes(StandardCharsets.UTF_8));
  }

  public void hashLong(final long l) {
    digest.update(("" + l).getBytes(StandardCharsets.UTF_8));
  }

  public void hashNtClient(final NtClient c) {
    digest.update(c.agent.getBytes(StandardCharsets.UTF_8));
    digest.update(c.authority.getBytes(StandardCharsets.UTF_8));
  }

  public String finish() {
    return Hashing.finishAndEncode(digest);
  }
}

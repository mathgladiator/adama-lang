package org.adamalang.runtime.natives.algo;

import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtDynamic;
import org.junit.Assert;
import org.junit.Test;

public class HashBuilderTests {
  @Test
  public void coverage() {
    HashBuilder hash = new HashBuilder();
    hash.hashNtDynamic(new NtDynamic("{}"));
    hash.hashBoolean(true);
    hash.hashBoolean(false);
    hash.hashDouble(3.14);
    hash.hashNtComplex(new NtComplex(2.71, 3.14));
    hash.hashInteger(42);
    hash.hashNtAsset(NtAsset.NOTHING);
    hash.hashString("xyz");
    hash.hashLong(123451234342L);
    hash.hashNtClient(NtClient.NO_ONE);
    Assert.assertEquals("UYoxfE+b51ZODGPUCfJwIne7mNacxQ7YqHWDkO0SLbB3sUqLRzz3S5JNDtAggHf/", hash.finish());
  }

  @Test
  public void coverageReorder() {
    HashBuilder hash = new HashBuilder();
    hash.hashNtAsset(NtAsset.NOTHING);
    hash.hashLong(123451234342L);
    hash.hashNtClient(NtClient.NO_ONE);
    hash.hashDouble(3.14);
    hash.hashInteger(42);
    hash.hashString("xyz");
    hash.hashBoolean(false);
    hash.hashBoolean(true);
    hash.hashNtDynamic(new NtDynamic("{}"));
    hash.hashNtComplex(new NtComplex(2.71, 3.14));
    Assert.assertEquals("fwA2dTJTT6mFZKy7B9Nh2Ol1U6tbqQu0/5UCck6cHUp64vYyf6ZWNaT5LZlejRBg", hash.finish());
  }
}

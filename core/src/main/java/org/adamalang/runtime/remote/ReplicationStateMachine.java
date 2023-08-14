/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.remote;

import org.adamalang.common.Hashing;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtToDynamic;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.function.Supplier;

public class ReplicationStateMachine {
  private static final NtPrincipal WHO = new NtPrincipal("replication", "adama-host");
  public final RxInvalidate invalidated;
  public final Caller caller;
  public final String name;
  public final Service service;
  public final String method;
  public final Supplier<NtToDynamic> supplier;
  public final ReplicationStatus status;

  public ReplicationStateMachine(Caller caller, String name, Service service, String method, Supplier<NtToDynamic> supplier, ReplicationStatus status) {
    this.invalidated = new RxInvalidate();
    this.caller = caller;
    this.name = name;
    this.service = service;
    this.method = method;
    this.supplier = supplier;
    this.status = status;
  }

  public void drive() {
    if (invalidated.getAndClearInvalidated() || status.needSend()) {
      JsonStreamWriter message = new JsonStreamWriter();
      message.beginObject();
      message.writeObjectFieldIntro("space");
      message.writeString(caller.__getSpace());
      message.writeObjectFieldIntro("key");
      message.writeString(caller.__getKey());
      message.writeObjectFieldIntro("name");
      message.writeString(name);
      message.writeObjectFieldIntro("body");
      message.writeNtDynamic(supplier.get().to_dynamic());
      message.endObject();

      String json = message.toString();
      MessageDigest digest = Hashing.md5();
      digest.update(json.getBytes(StandardCharsets.UTF_8));
      if (status.desire(Hashing.finishAndEncode(digest))) {
        status.result = service.invoke(caller, method + "::put", status.cache, WHO, new NtDynamic(json), (t) -> t);
      }
    }
  }
}

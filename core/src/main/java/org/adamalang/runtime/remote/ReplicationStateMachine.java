/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.remote;

import org.adamalang.runtime.natives.NtToDynamic;

import java.util.function.Supplier;

public class ReplicationStateMachine {
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
}

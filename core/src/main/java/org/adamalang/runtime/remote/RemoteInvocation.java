/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.remote;

import org.adamalang.runtime.natives.NtClient;

import java.util.Objects;

/** the inputs into an invocation */
public class RemoteInvocation implements Comparable<RemoteInvocation> {
  public final String scope;
  public final String service;
  public final String method;
  public final NtClient who;
  public final String parameter;

  public RemoteInvocation(String scope, String service, String method, NtClient who, String parameter) {
    this.scope = scope;
    this.service = service;
    this.method = method;
    this.who = who;
    this.parameter = parameter;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RemoteInvocation that = (RemoteInvocation) o;
    return Objects.equals(scope, that.scope) && Objects.equals(service, that.service) && Objects.equals(method, that.method) && Objects.equals(who, that.who) && Objects.equals(parameter, that.parameter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scope, service, method, who, parameter);
  }

  @Override
  public int compareTo(RemoteInvocation o) {
    int delta = scope.compareTo(o.scope);
    if (delta != 0) {
      return delta;
    }
    delta = service.compareTo(o.service);
    if (delta != 0) {
      return delta;
    }
    delta = method.compareTo(o.method);
    if (delta != 0) {
      return delta;
    }
    delta = who.compareTo(o.who);
    if (delta != 0) {
      return delta;
    }
    return parameter.compareTo(o.parameter);
  }
}

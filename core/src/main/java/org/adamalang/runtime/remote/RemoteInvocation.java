/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.remote;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;

import java.util.Objects;

/** the inputs of a service call */
public class RemoteInvocation implements Comparable<RemoteInvocation> {
  public final String service;
  public final String method;
  public final NtPrincipal who;
  public final String parameter;

  public RemoteInvocation(String service, String method, NtPrincipal who, String parameter) {
    this.service = service;
    this.method = method;
    this.who = who;
    this.parameter = parameter;
  }

  public RemoteInvocation(JsonStreamReader reader) {
    String _service = null;
    String _method = null;
    NtPrincipal _who = null;
    String _parameter = null;
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "service":
            _service = reader.readString();
            break;
          case "method":
            _method = reader.readString();
            break;
          case "who":
            _who = reader.readNtPrincipal();
            break;
          case "parameter":
            _parameter = reader.skipValueIntoJson();
            break;
        }
      }
    }
    this.service = _service;
    this.method = _method;
    this.who = _who;
    this.parameter = _parameter;
  }

  @Override
  public int hashCode() {
    return Objects.hash(service, method, who, parameter);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RemoteInvocation that = (RemoteInvocation) o;
    return Objects.equals(service, that.service) && Objects.equals(method, that.method) && Objects.equals(who, that.who) && Objects.equals(parameter, that.parameter);
  }

  @Override
  public int compareTo(RemoteInvocation o) {
    int delta = service.compareTo(o.service);
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

  public void write(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("service");
    writer.writeString(service);
    writer.writeObjectFieldIntro("method");
    writer.writeString(method);
    writer.writeObjectFieldIntro("who");
    writer.writeNtPrincipal(who);
    writer.writeObjectFieldIntro("parameter");
    writer.injectJson(parameter);
    writer.endObject();
  }
}

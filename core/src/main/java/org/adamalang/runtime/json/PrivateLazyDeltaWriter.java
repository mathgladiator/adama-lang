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
package org.adamalang.runtime.json;

import org.adamalang.runtime.natives.*;

public class PrivateLazyDeltaWriter {
  public static final Runnable DO_NOTHING = () -> {
  };
  public final NtPrincipal who;
  public Object cacheObject;
  public final Object viewerState;
  private final Runnable onEndWithManifest;
  private final Runnable onManifest;
  private final PrivateLazyDeltaWriter parent;
  private final JsonStreamWriter writer;
  private boolean manifested;
  private final int viewId;

  private PrivateLazyDeltaWriter(final NtPrincipal who, Object cacheObject, final JsonStreamWriter writer, final PrivateLazyDeltaWriter parent, final Runnable onManifest, final Runnable onEndWithManifest, final Object viewerState, int viewId) {
    this.who = who;
    this.cacheObject = cacheObject;
    this.writer = writer;
    this.parent = parent;
    this.onManifest = onManifest;
    this.onEndWithManifest = onEndWithManifest;
    this.viewerState = viewerState;
    this.viewId = viewId;
    manifested = false;
  }

  public int getViewId() {
    return viewId;
  }

  public static PrivateLazyDeltaWriter bind(final NtPrincipal who, final JsonStreamWriter writer, Object viewerState, int viewId) {
    return new PrivateLazyDeltaWriter(who, null, writer, null, DO_NOTHING, DO_NOTHING, viewerState, viewId);
  }

  public boolean end() {
    if (manifested) {
      onEndWithManifest.run();
    }
    return manifested;
  }

  public void manifest() {
    if (parent != null) {
      parent.manifest();
    }
    if (!manifested) {
      manifested = true;
      onManifest.run();
    }
  }

  public void setCacheObject(Object cacheObject) {
    this.cacheObject = cacheObject;
  }

  public Object getCacheObject() {
    return cacheObject;
  }

  public PrivateLazyDeltaWriter planArray() {
    return new PrivateLazyDeltaWriter(who,  cacheObject, writer, this, () -> writer.beginArray(), () -> writer.endArray(), viewerState, viewId);
  }

  public PrivateLazyDeltaWriter planField(final int fieldId) {
    return new PrivateLazyDeltaWriter(who,  cacheObject, writer, this, () -> writer.writeObjectFieldIntro("" + fieldId), DO_NOTHING, viewerState, viewId);
  }

  public PrivateLazyDeltaWriter planField(final String fieldName) {
    return new PrivateLazyDeltaWriter(who,  cacheObject, writer, this, () -> writer.writeObjectFieldIntro(fieldName), DO_NOTHING, viewerState, viewId);
  }

  public PrivateLazyDeltaWriter planObject() {
    return new PrivateLazyDeltaWriter(who, cacheObject, writer,this, () -> writer.beginObject(), () -> writer.endObject(), viewerState, viewId);
  }

  public void writeBool(final boolean b) {
    manifest();
    writer.writeBoolean(b);
  }

  public void writeDouble(final double d) {
    manifest();
    writer.writeDouble(d);
  }

  public void writeNtComplex(final NtComplex c) {
    manifest();
    writer.writeNtComplex(c);
  }

  public void writeNtDate(final NtDate d) {
    manifest();
    writer.writeNtDate(d);
  }

  public void writeNtDateTime(final NtDateTime d) {
    manifest();
    writer.writeNtDateTime(d);
  }

  public void writeNtTime(final NtTime d) {
    manifest();
    writer.writeNtTime(d);
  }

  public void writeNtTimeSpan(final NtTimeSpan d) {
    manifest();
    writer.writeNtTimeSpan(d);
  }

  public void writeFastString(final String str) {
    manifest();
    writer.writeFastString(str);
  }

  public void writeInt(final int x) {
    manifest();
    writer.writeInteger(x);
  }

  public void writeNull() {
    manifest();
    writer.writeNull();
  }

  public void writeString(final String str) {
    manifest();
    writer.writeString(str);
  }

  public void injectJson(final String json) {
    manifest();
    writer.injectJson(json);
  }

  public JsonStreamWriter force() {
    manifest();
    return writer;
  }
}

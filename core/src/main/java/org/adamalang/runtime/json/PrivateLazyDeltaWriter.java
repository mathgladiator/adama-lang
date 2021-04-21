/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.json;

import org.adamalang.runtime.natives.NtClient;

public class PrivateLazyDeltaWriter {
  public static PrivateLazyDeltaWriter bind(final NtClient who, final JsonStreamWriter writer, Object viewerState) {
    return new PrivateLazyDeltaWriter(who, writer, null, () -> {}, () -> {}, viewerState);
  }

  private boolean manifested;
  private final Runnable onEndWithManifest;
  private final Runnable onManifest;
  private final PrivateLazyDeltaWriter parent;
  public final NtClient who;
  private final JsonStreamWriter writer;
  public final Object viewerState;

  private PrivateLazyDeltaWriter(final NtClient who, final JsonStreamWriter writer, final PrivateLazyDeltaWriter parent, final Runnable onManifest, final Runnable onEndWithManifest, final Object viewerState) {
    this.who = who;
    this.writer = writer;
    this.parent = parent;
    this.onManifest = onManifest;
    this.onEndWithManifest = onEndWithManifest;
    this.viewerState = viewerState;
    manifested = false;
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

  public PrivateLazyDeltaWriter planArray() {
    return new PrivateLazyDeltaWriter(who, writer, this, () -> writer.beginArray(), () -> writer.endArray(), viewerState);
  }

  public PrivateLazyDeltaWriter planField(final int fieldId) {
    return new PrivateLazyDeltaWriter(who, writer, this, () -> writer.writeObjectFieldIntro("" + fieldId), () -> {}, viewerState);
  }

  public PrivateLazyDeltaWriter planField(final String fieldName) {
    return new PrivateLazyDeltaWriter(who, writer, this, () -> writer.writeObjectFieldIntro(fieldName), () -> {}, viewerState);
  }

  public PrivateLazyDeltaWriter planObject() {
    return new PrivateLazyDeltaWriter(who, writer, this, () -> writer.beginObject(), () -> writer.endObject(), viewerState);
  }

  public void writeBool(final boolean b) {
    manifest();
    writer.writeBoolean(b);
  }

  public void writeDouble(final double d) {
    manifest();
    writer.writeDouble(d);
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
}

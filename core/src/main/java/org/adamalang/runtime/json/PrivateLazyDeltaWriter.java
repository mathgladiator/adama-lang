/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.json;

import org.adamalang.runtime.delta.secure.AssetIdEncoder;
import org.adamalang.runtime.natives.*;

public class PrivateLazyDeltaWriter {
  public static final Runnable DO_NOTHING = () -> {
  };
  public final NtPrincipal who;
  public final Object viewerState;
  public final AssetIdEncoder assetIdEncoder;
  private final Runnable onEndWithManifest;
  private final Runnable onManifest;
  private final PrivateLazyDeltaWriter parent;
  private final JsonStreamWriter writer;
  private boolean manifested;

  private PrivateLazyDeltaWriter(final NtPrincipal who, final JsonStreamWriter writer, final PrivateLazyDeltaWriter parent, final Runnable onManifest, final Runnable onEndWithManifest, final Object viewerState, final AssetIdEncoder assetIdEncoder) {
    this.who = who;
    this.writer = writer;
    this.parent = parent;
    this.onManifest = onManifest;
    this.onEndWithManifest = onEndWithManifest;
    this.viewerState = viewerState;
    this.assetIdEncoder = assetIdEncoder;
    manifested = false;
  }

  public static PrivateLazyDeltaWriter bind(final NtPrincipal who, final JsonStreamWriter writer, Object viewerState, AssetIdEncoder encoder) {
    return new PrivateLazyDeltaWriter(who, writer, null, DO_NOTHING, DO_NOTHING, viewerState, encoder);
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
    return new PrivateLazyDeltaWriter(who, writer, this, () -> writer.beginArray(), () -> writer.endArray(), viewerState, assetIdEncoder);
  }

  public PrivateLazyDeltaWriter planField(final int fieldId) {
    return new PrivateLazyDeltaWriter(who, writer, this, () -> writer.writeObjectFieldIntro("" + fieldId), DO_NOTHING, viewerState, assetIdEncoder);
  }

  public PrivateLazyDeltaWriter planField(final String fieldName) {
    return new PrivateLazyDeltaWriter(who, writer, this, () -> writer.writeObjectFieldIntro(fieldName), DO_NOTHING, viewerState, assetIdEncoder);
  }

  public PrivateLazyDeltaWriter planObject() {
    return new PrivateLazyDeltaWriter(who, writer, this, () -> writer.beginObject(), () -> writer.endObject(), viewerState, assetIdEncoder);
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

/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.json;

import org.adamalang.runtime.delta.secure.AssetIdEncoder;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtComplex;

public class PrivateLazyDeltaWriter {
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
    return new PrivateLazyDeltaWriter(who, writer, null, () -> {
    }, () -> {
    }, viewerState, encoder);
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
    return new PrivateLazyDeltaWriter(who, writer, this, () -> writer.writeObjectFieldIntro("" + fieldId), () -> {
    }, viewerState, assetIdEncoder);
  }

  public PrivateLazyDeltaWriter planField(final String fieldName) {
    return new PrivateLazyDeltaWriter(who, writer, this, () -> writer.writeObjectFieldIntro(fieldName), () -> {
    }, viewerState, assetIdEncoder);
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

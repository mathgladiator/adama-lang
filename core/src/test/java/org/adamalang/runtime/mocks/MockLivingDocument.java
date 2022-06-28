/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.async.AsyncTask;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.delta.secure.AssetIdEncoder;
import org.adamalang.runtime.exceptions.AbortMessageException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.sys.LivingDocument;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebPut;
import org.adamalang.runtime.sys.web.WebResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MockLivingDocument extends LivingDocument {
  public final ArrayList<NtClient> connects;
  public final ArrayList<NtClient> disconnects;

  public MockLivingDocument() {
    super(null);
    connects = new ArrayList<>();
    disconnects = new ArrayList<>();
  }

  public MockLivingDocument(final DocumentMonitor monitor) {
    super(monitor);
    connects = new ArrayList<>();
    disconnects = new ArrayList<>();
  }

  @Override
  protected boolean __is_direct_channel(String channel) {
    return "__direct".equals(channel);
  }

  @Override
  protected void __handle_direct(NtClient who, String channel, Object message) throws AbortMessageException {
  }

  @Override
  public Set<String> __get_intern_strings() {
    return new HashSet<>();
  }

  @Override
  protected void __construct_intern(final NtClient who, final NtMessageBase message) {}

  @Override
  public PrivateView __createPrivateView(final NtClient __who, final Perspective __perspective, AssetIdEncoder encoder) {
    return null;
  }

  @Override
  public WebResponse __get(WebGet __get) {
    return null;
  }

  @Override
  protected WebResponse __put_internal(WebPut __get) {
    return null;
  }

  @Override
  public void __dump(final JsonStreamWriter __writer) {}

  @Override
  public String[] __getTests() {
    return new String[0];
  }

  @Override
  public void __revert() {}

  @Override
  protected Object __parse_message(final String channel, final JsonStreamReader reader) {
    return null;
  }

  @Override
  public void __insert(final JsonStreamReader __reader) {}

  @Override
  public void __patch(JsonStreamReader __reader) {}

  @Override
  protected void __invoke_label(final String __new_state) {}

  @Override
  public boolean __onConnected(final NtClient clientValue) {
    connects.add(clientValue);
    return true;
  }

  @Override
  public void __onDisconnected(final NtClient clientValue) {
    disconnects.add(clientValue);
  }

  @Override
  public void __onAssetAttached(NtClient __cvalue, NtAsset __asset) {}

  @Override
  public boolean __onCanAssetAttached(NtClient __cvalue) {
    return false;
  }

  @Override
  protected NtMessageBase __parse_construct_arg(final JsonStreamReader message) {
    return null;
  }

  @Override
  protected void __reset_future_queues() {}

  @Override
  protected void __route(final AsyncTask task) {}

  @Override
  public void __test(final TestReportBuilder report, final String testName) {}

  @Override
  public void __commit(
      final String name, final JsonStreamWriter writer, final JsonStreamWriter reverse) {}
}

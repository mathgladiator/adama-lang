/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.mocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.adamalang.runtime.LivingDocument;
import org.adamalang.runtime.async.AsyncTask;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.ops.TestReportBuilder;

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
  public void __commit(final String name, final JsonStreamWriter writer, final JsonStreamWriter reverse) {
  }

  @Override
  protected void __construct_intern(final NtClient who, final NtMessageBase message) {
  }

  @Override
  public PrivateView __createPrivateView(final NtClient __who, final Perspective __perspective) {
    return null;
  }

  @Override
  public void __dump(final JsonStreamWriter __writer) {
  }

  @Override
  public String[] __getTests() {
    return new String[0];
  }

  @Override
  public void __insert(final JsonStreamReader __reader) {
  }

  @Override
  public void __patch(JsonStreamReader __reader) {
  }

  @Override
  protected void __invoke_label(final String __new_state) {
  }

  @Override
  public boolean __onConnected(final NtClient clientValue) {
    connects.add(clientValue);
    return true;
  }

  @Override
  public void __onAssetAttached(NtClient __cvalue, NtAsset __asset) {
  }

  @Override
  public boolean __onCanAssetAttached(NtClient __cvalue) {
    return false;
  }

  @Override
  public void __onDisconnected(final NtClient clientValue) {
    disconnects.add(clientValue);
  }

  @Override
  protected NtMessageBase __parse_construct_arg(final JsonStreamReader message) {
    return null;
  }

  @Override
  protected Object __parse_message2(final String channel, final JsonStreamReader reader) {
    return null;
  }

  @Override
  protected void __reset_future_queues() {
  }

  @Override
  public void __revert() {
  }

  @Override
  protected void __route(final AsyncTask task) {
  }

  @Override
  public void __test(final TestReportBuilder report, final String testName) {
  }

  @Override
  public Set<String> __get_intern_strings() {
    return new HashSet<>();
  }
}

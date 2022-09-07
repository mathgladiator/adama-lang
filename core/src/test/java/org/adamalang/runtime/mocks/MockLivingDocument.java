/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.runtime.sys.LivingDocument;
import org.adamalang.runtime.sys.web.WebDelete;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebPut;
import org.adamalang.runtime.sys.web.WebResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MockLivingDocument extends LivingDocument {
  public final ArrayList<NtPrincipal> connects;
  public final ArrayList<NtPrincipal> disconnects;

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
  protected void __handle_direct(CoreRequestContext who, String channel, Object message) throws AbortMessageException {
  }

  @Override
  protected void __link(ServiceRegistry registry) {
  }

  @Override
  public void __onLoad() {
  }

  @Override
  protected void __executeServiceCalls(boolean cancel) {
  }

  @Override
  public Set<String> __get_intern_strings() {
    return new HashSet<>();
  }

  @Override
  protected void __construct_intern(CoreRequestContext context, final NtMessageBase message) {}

  @Override
  public PrivateView __createPrivateView(final NtPrincipal __who, final Perspective __perspective, AssetIdEncoder encoder) {
    return null;
  }

  @Override
  public WebResponse __get(WebGet __get) {
    return null;
  }

  @Override
  public WebResponse __options(WebGet __get) {
    return null;
  }

  @Override
  protected WebResponse __put_internal(WebPut __get) {
    return null;
  }

  @Override
  protected WebResponse __delete_internal(WebDelete __delete) {
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
  public boolean __onConnected(final CoreRequestContext context) {
    connects.add(context.who);
    return true;
  }

  @Override
  public boolean __delete(CoreRequestContext context) {
    return context.who.authority.equals("overlord");
  }

  @Override
  public void __onDisconnected(final CoreRequestContext context) {
    disconnects.add(context.who);
  }

  @Override
  public void __onAssetAttached(CoreRequestContext __cvalue, NtAsset __asset) {}

  @Override
  public boolean __onCanAssetAttached(CoreRequestContext __cvalue) {
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

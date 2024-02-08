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
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.async.AsyncTask;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.exceptions.AbortMessageException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.runtime.sys.AuthResponse;
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
  public String __traffic(CoreRequestContext __context) {
    return "main";
  }

  @Override
  public String __metrics() {
    return "{}";
  }

  @Override
  public void __make_cron_progress() {
  }

  @Override
  protected boolean __is_direct_channel(String channel) {
    return "__direct".equals(channel);
  }

  @Override
  protected void __handle_direct(CoreRequestContext who, String channel, Object message) throws AbortMessageException {
  }

  @Override
  public void __writeRxReport(JsonStreamWriter __writer) {
  }

  @Override
  protected void __link(ServiceRegistry registry) {
  }

  @Override
  public void __onLoad() {
  }

  @Override
  protected void __debug(JsonStreamWriter __writer) {

  }

  @Override
  public AuthResponse __authpipe(CoreRequestContext __context, String __messsage) {
    return null;
  }

  @Override
  protected void __bindReplication() {
  }

  @Override
  public String __getViewStateFilter() {
    return "[]";
  }

  @Override
  public boolean __open_channel(String name) {
    return false;
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
  public PrivateView __createPrivateView(final NtPrincipal __who, final Perspective __perspective) {
    return null;
  }

  @Override
  public String __auth(CoreRequestContext context, String username, String password) {
    return null;
  }

  @Override
  public void __password(CoreRequestContext context, String password) { }

  @Override
  public WebResponse __get_internal(CoreRequestContext __context, WebGet __get) {
    return null;
  }

  @Override
  public WebResponse __options(CoreRequestContext __context, WebGet __get) {
    return null;
  }

  @Override
  protected WebResponse __put_internal(CoreRequestContext __context, WebPut __get) {
    return null;
  }

  @Override
  protected WebResponse __delete_internal(CoreRequestContext __context, WebDelete __delete) {
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

  @Override
  public void __settle(Set<Integer> viewers) {
  }
}

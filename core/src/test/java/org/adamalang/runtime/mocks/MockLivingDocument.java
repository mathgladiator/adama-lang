/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.mocks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.LivingDocument;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.async.AsyncTask;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.runtime.natives.NtClient;

import java.util.ArrayList;

public class MockLivingDocument extends LivingDocument {
  
  public final ArrayList<NtClient> connects;
  public final ArrayList<NtClient> disconnects;

  public MockLivingDocument() {
    super(Utility.createObjectNode(), null);
    this.connects = new ArrayList<>();
    this.disconnects = new ArrayList<>();
  }

  public MockLivingDocument(DocumentMonitor monitor) {
    super(Utility.createObjectNode(), monitor);
    this.connects = new ArrayList<>();
    this.disconnects = new ArrayList<>();
  }
  
  @Override
  protected void __construct_intern(NtClient who, ObjectNode message) {
  
  }

  @Override
  protected void __invoke_label(String __new_state) {
  }

  @Override
  public boolean __onConnected(NtClient clientValue) {
    this.connects.add(clientValue);
    return true;
  }
  
  @Override
  public void __onDisconnected(NtClient clientValue) {
    this.disconnects.add(clientValue);
  }
  
  @Override
  protected void __route(AsyncTask task) {
  }

  @Override
  protected void __reset_future_queues() {
  
  }

  @Override
  public void __commit(String name, ObjectNode delta) {

  }

  @Override
  public void __revert() {
  }

  @Override
  public String[] __getTests() {
    return new String[0];
  }
  
  @Override
  public void __test(TestReportBuilder report, String testName) {
  
  }

  @Override
  public JsonNode getPrivateViewFor(NtClient __who) {
    return null;
  }
}

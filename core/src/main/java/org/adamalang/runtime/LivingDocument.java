/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import org.adamalang.runtime.async.AsyncTask;
import org.adamalang.runtime.async.OutstandingFutureTracker;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.HasPrivacyCheckAndExtract;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.exceptions.ComputeBlockedException;
import org.adamalang.runtime.exceptions.DocumentRequestRejectedException;
import org.adamalang.runtime.exceptions.DocumentRequestRejectedReason;
import org.adamalang.runtime.exceptions.GoodwillExhaustedException;
import org.adamalang.runtime.exceptions.RetryProgressException;
import org.adamalang.runtime.logger.TransactionResult;
import org.adamalang.runtime.logger.Transaction;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.ops.AssertionStats;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.reactives.RxBoolean;
import org.adamalang.runtime.reactives.RxFactory;
import org.adamalang.runtime.reactives.RxInt32;
import org.adamalang.runtime.reactives.RxInt64;
import org.adamalang.runtime.reactives.RxString;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** The central class for a living document (i.e. a tiny VM) */
public abstract class LivingDocument implements HasPrivacyCheckAndExtract, RxParent {
  protected int __assertionFailures = 0;
  protected int __assertionTotal = 0;
  protected final RxInt32 __auto_future_id;
  protected final RxBoolean __blocked;
  private final TreeMap<NtClient, Integer> __clients;
  private HashMap<NtClient, ObjectNode> __clientViews;
  protected int __code_cost;
  protected final RxInt32 __connection_id;
  protected final RxBoolean __constructed;
  protected final RxString __entropy;
  protected final OutstandingFutureTracker __futures;
  protected int __goodwillBudget;
  protected int __goodwillLimitOfBudget;
  protected final RxInt32 __message_id;
  public final DocumentMonitor __monitor;
  protected final RxInt64 __next_time;
  protected final ArrayList<AsyncTask> __queue;
  protected Random __random;
  public final LivingDocument __self;
  protected final RxInt32 __seq;
  protected final RxString __state;
  public ObjectNode __STORE; // TO KILL
  protected final RxInt64 __time;
  protected ArrayList<Integer> __trace;

  public LivingDocument(final ObjectNode __root, final DocumentMonitor __monitor) {
    this.__monitor = __monitor;
    __random = new Random();
    __self = this;
    __state = RxFactory.makeRxString(this, __root, "__state", "");
    __constructed = RxFactory.makeRxBoolean(this, __root, "__constructed", false);
    __blocked = RxFactory.makeRxBoolean(this, __root, "__blocked", false);
    __seq = RxFactory.makeRxInt32(this, __root, "__seq", 0);
    __entropy = RxFactory.makeRxString(this, __root, "__entropy", Long.toString(__random.nextLong()));
    __auto_future_id = RxFactory.makeRxInt32(this, __root, "__auto_future_id", 0);
    __connection_id = RxFactory.makeRxInt32(this, __root, "__connection_id", 0);
    __message_id = RxFactory.makeRxInt32(this, __root, "__message_id", 0);
    __time = RxFactory.makeRxInt64(this, __root, "__time", 0L);
    __next_time = RxFactory.makeRxInt64(this, __root, "__next_time", 0L);
    __queue = new ArrayList<>();
    __futures = new OutstandingFutureTracker(__auto_future_id);
    __code_cost = 0;
    __trace = new ArrayList<>();
    __STORE = __root;
    __clients = new TreeMap<>();
    if (__root.has("__clients")) {
      final var it = __root.get("__clients").fields();
      while (it.hasNext()) {
        final var value = it.next();
        final var who = NtClient.from(value.getValue());
        __clients.put(who, Integer.parseInt(value.getKey()));
      }
    }
    if (__root.has("__messages")) {
      final var tasks = new TreeMap<Integer, AsyncTask>();
      final var it = __root.get("__messages").fields();
      while (it.hasNext()) {
        final var value = it.next();
        final var who = NtClient.from(value.getValue().get("who"));
        final var msgId = Integer.parseInt(value.getKey());
        final var channel = value.getValue().get("channel").textValue();
        final var message = (ObjectNode) value.getValue().get("message");
        final var task = new AsyncTask(msgId, who, channel, message);
        tasks.put(msgId, task);
      }
      __queue.addAll(tasks.values());
    }
    __goodwillBudget = 100000;
    __goodwillLimitOfBudget = 100000;
  }

  /** exposed: assert something as truth */
  protected void __assert_truth(final boolean value, final int startLine, final int startPosition, final int endLine, final int endLinePosition) {
    __assertionTotal++;
    if (!value) {
      __assertionFailures++;
      if (__monitor != null) {
        __monitor.assertFailureAt(startLine, startPosition, endLine, endLinePosition, __assertionTotal, __assertionFailures);
      }
    }
  }

  /** code generated: commit the tree, and push data into the given delta */
  public abstract void __commit(String name, ObjectNode delta);
  /** code generated: what happens when the document is constructed */
  protected abstract void __construct_intern(NtClient who, ObjectNode message);

  /** internal: we compute per client */
  private synchronized void __distributeClientViews() {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("DistributeClientViews");
    }
    try {
      final var newClientViews = new HashMap<NtClient, ObjectNode>();
      for (final NtClient client : __clients.keySet()) {
        final var view = Utility.createObjectNode();
        view.set("data", getPrivateViewFor(client)); // TODO: sort out a better model, maybe code generate it... yes... YES
        __futures.dumpIntoView(view, client);
        newClientViews.put(client, view);
      }
      __clientViews = newClientViews;
      exception = false;
    } finally {
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  /** code generated: get the tests for the document */
  public abstract String[] __getTests();

  public synchronized ObjectNode __getView(final NtClient who) {
    return __clientViews.get(who);
  }

  /** exposed: this allows the child object to see if it can still do stuff */
  protected boolean __goodwill(final int startLine, final int startPosition, final int endLine, final int endLinePosition) {
    if (__goodwillBudget > 0) {
      __goodwillBudget--;
      if (__goodwillBudget == 0) {
        if (__monitor != null) {
          __monitor.goodwillFailureAt(startLine, startPosition, endLine, endLinePosition);
        }
        throw new GoodwillExhaustedException(startLine, startPosition, endLine, endLinePosition);
      }
    }
    return true;
  }

  /** exposed: invoke the given state machine label */
  protected void __invoke(final String __new_state) {
    if (__monitor != null) {
      final var started = System.nanoTime();
      __monitor.push(__new_state);
      var exception = true;
      try {
        __invoke_label(__new_state);
        exception = false;
      } finally {
        __monitor.pop(System.nanoTime() - started, exception);
      }
    } else {
      __invoke_label(__new_state);
    }
  }

  /** code generated: state machine labelslabels can be dynamically invoked */
  protected abstract void __invoke_label(String __new_state);
  /** code generated: allow the document to accept/reject the client */
  public abstract boolean __onConnected(NtClient clientValue);
  /** code generated: let the document know of a disconnected client */
  public abstract void __onDisconnected(NtClient clientValue);

  /** artifact: from RxParent */
  @Override
  public void __raiseDirty() {
  }

  /** exposed: random number between 0 and n exclusive */
  protected int __randomBoundInt(final int n) {
    if (n < 0) { return 0; }
    return __random.nextInt(n);
  }

  /** exposed: random double */
  protected double __randomDouble() {
    return __random.nextDouble();
  }

  /** exposed: random double that fits a */
  protected double __randomGaussian() {
    return __random.nextGaussian();
  }

  /** exposed: random number */
  protected int __randomInt() {
    return __random.nextInt();
  }

  /** exposed: randon long */
  protected long __randomLong() {
    return __random.nextLong();
  }

  /** code generated: reset internal queues */
  protected abstract void __reset_future_queues();
  /** code generated: revert the tree, all changes revert back */
  public abstract void __revert();
  /** code generated: route the given message */
  protected abstract void __route(AsyncTask task);

  /** available to the test runner... me thinks this can be done... mucho better
   * (requires precommit) */
  public ObjectNode __run_test(final TestReportBuilder report, final String testName) {
    __test(report, testName);
    final var delta = Utility.createObjectNode();
    __commit(null, delta);
    return delta;
  }

  /** code generated: run the test for the given test name */
  public abstract void __test(TestReportBuilder report, String testName);

  /** exposed: @step; for testing */
  protected void __test_progress() {
    try {
      for (final AsyncTask task : __queue) {
        __route(task);
      }
      for (final AsyncTask task : __queue) {
        task.execute();
      }
      final var stateToExecute = __state.get();
      __state.set("");
      __invoke_label(stateToExecute);
      __blocked.set(false);
      __seq.bumpUpPre();
      __entropy.set(Long.toString(__random.nextLong()));
      __futures.commit();
      __queue.clear();
      __reset_future_queues();
      __commit(null, Utility.createObjectNode());
    } catch (final ComputeBlockedException cbe) {
      __revert();
      __futures.restore();
      __reset_future_queues();
      __blocked.set(true);
    } catch (final RetryProgressException cbe) {
      __revert();
      __futures.restore();
      __reset_future_queues();
      __blocked.set(true);
      __test_progress();
    }
  }

  /** exposed: for code coverage */
  protected void __track(final int idx) {
    __trace.add(idx);
  }

  /** transaction: core API */
  public Transaction __transact(final ObjectNode request) throws DocumentRequestRejectedException {
    final var commandNode = request.get("command");
    if (commandNode == null || commandNode.isNull() || !commandNode.isTextual()) { throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.NoRequestCommand); }
    final var timeStampNode = request.get("timestamp");
    if (timeStampNode == null || timeStampNode.isNull()) { throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.NoRequestTimestamp); }
    __time.set(Long.parseLong(timeStampNode.textValue()));
    final var command = commandNode.textValue();
    final var whoNode = request.get("who");
    if ("bill".equals(command)) { return __transaction_bill(request); }
    if ("invalidate".equals(command)) {
      if (__monitor != null) {
        return __transaction_invalidate_monitored(request);
      } else {
        return __transaction_invalidate_body(request);
      }
    }
    if (whoNode == null || !whoNode.isObject()) { throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.NoRequestWho); }
    final var who = NtClient.from(whoNode);
    if ("construct".equals(command)) { return __transaction_construct(request, who); }
    if ("connect".equals(command)) { return __transaction_connect(request, who); }
    if ("disconnect".equals(command)) { return __transaction_disconnect(request, who); }
    if ("send".equals(command)) { return __transaction_send(request, who); }
    throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.CommandNotRecognized);
  }

  /** transaction: bill */
  private Transaction __transaction_bill(final ObjectNode request) {
    final var delta = Utility.createObjectNode();
    final var ticks = __goodwillLimitOfBudget - __goodwillBudget;
    final var cost = __code_cost;
    __goodwillBudget = __goodwillLimitOfBudget;
    __code_cost = 0;
    delta.put("__goodwill_used", ticks);
    delta.put("__cost", cost);
    delta.put("__billing_seq", __seq.get());
    return new Transaction(__seq.get(), request, delta, new TransactionResult(true, 0, __seq.get()));
  }

  /** transaction: a person connects to document */
  private Transaction __transaction_connect(final ObjectNode request, final NtClient who) throws DocumentRequestRejectedException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionConnect");
    }
    try {
      __random = new Random(Long.parseLong(__entropy.get()));
      if (__clients.containsKey(who)) { throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.ClientAlreadyConnected); }
      if (__onConnected(who)) {
        // user was accepted, so let's commit it
        final var delta = Utility.createObjectNode();
        // generate a connection id for the user
        final var cId = __connection_id.bumpUpPost();
        // associate the client to the connection id
        __clients.put(who, cId);
        // commit the tree to the delta
        __seq.bumpUpPre();
        __commit(null, delta);
        // inject the client
        who.dump(delta.putObject("__clients").putObject("" + cId));
        final var result = new Transaction(__seq.get(), request, delta, new TransactionResult(true, 0, __seq.get()));
        exception = false;
        return result;
      } else {
        // clean up because it was rejected
        __revert();
        throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.ClientConnectRejected);
      }
    } finally {
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  /** transaction: construct the document */
  private Transaction __transaction_construct(final ObjectNode request, final NtClient who) throws DocumentRequestRejectedException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionConstruct");
    }
    try {
      __random = new Random(Long.parseLong(__entropy.get()));
      if (__constructed.get()) { throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.AlreadyConstructed); }
      final var argNode = request.get("arg");
      if (argNode == null || !argNode.isObject()) { throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.NoConstructorArg); }
      final var delta = Utility.createObjectNode();
      __construct_intern(who, (ObjectNode) argNode);
      __constructed.set(true);
      __commit(null, delta);
      final var result = new Transaction(__seq.get(), request, delta, new TransactionResult(true, 0, __seq.get()));
      exception = false;
      return result;
    } finally {
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  /** transaction: a person disconnects from the document */
  private Transaction __transaction_disconnect(final ObjectNode request, final NtClient who) throws DocumentRequestRejectedException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionDisconnect");
    }
    try {
      __random = new Random(Long.parseLong(__entropy.get()));
      if (!__clients.containsKey(who)) { throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.ClientNotAlreadyConnected); }
      // disconnect them
      __onDisconnected(who);
      // stop tracking them
      final int id = __clients.remove(who);
      final var delta = Utility.createObjectNode();
      __seq.bumpUpPre();
      __commit(null, delta);
      delta.putObject("__clients").putNull("" + id);
      final var result = new Transaction(__seq.get(), request, delta, new TransactionResult(true, 0, __seq.get()));
      exception = false;
      return result;
    } finally {
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  /** transaction: an invalidation is happening on the document (no monitor) */
  private Transaction __transaction_invalidate_body(final ObjectNode request) {
    final var seedUsed = Long.parseLong(__entropy.get());
    try {
      __random = new Random(seedUsed);
      for (final AsyncTask task : __queue) {
        __route(task);
      }
      for (final AsyncTask task : __queue) {
        task.execute();
      }
      if (__state.get().length() > 0) {}
      // execute the state
      if (__state.has() && __next_time.get() <= __time.get()) {
        final var stateToExecute = __state.get();
        __state.set("");
        __invoke_label(stateToExecute);
      }
      __blocked.set(false);
      __seq.bumpUpPre();
      __entropy.set(Long.toString(__random.nextLong()));
      __futures.commit();
      __queue.clear();
      __reset_future_queues();
      final var delta = Utility.createObjectNode();
      delta.putNull("__messages");
      delta.put("__seedUsed", Long.toString(seedUsed));
      __commit(null, delta);
      __distributeClientViews();
      return new Transaction(__seq.get(), request, delta, new TransactionResult(__state.has(), (int) (__next_time.get() - __time.get()), __seq.get()));
    } catch (final ComputeBlockedException cbe) {
      __distributeClientViews();
      __revert();
      __futures.restore();
      __reset_future_queues();
      __blocked.set(true);
      __seq.bumpUpPre();
      final var delta = Utility.createObjectNode();
      delta.put("__seedUsed", Long.toString(seedUsed));
      delta.put("__blocked_on", cbe.channel);
      __commit(null, delta);
      return new Transaction(__seq.get(), request, delta, new TransactionResult(false, 0, __seq.get()));
    } catch (final RetryProgressException rpe) {
      __futures.restore();
      __reset_future_queues();
      __revert();
      final var delta = Utility.createObjectNode();
      delta.putObject("__messages").putNull(rpe.messageIdToDelete + "");
      __seq.bumpUpPre();
      __commit(null, delta);
      return new Transaction(__seq.get(), request, delta, new TransactionResult(true, 0, __seq.get()));
    }
  }

  /** transaction: an invalidation is happening on the document (use monitor) */
  private Transaction __transaction_invalidate_monitored(final ObjectNode request) {
    var exception = true;
    final var startedTime = System.nanoTime();
    __monitor.push("TransactionInvalidate");
    try {
      final var result = __transaction_invalidate_body(request);
      exception = false; // this is basically useless, but nothing within this function should be a
                         // subscribe
      return result;
    } finally {
      __monitor.pop(System.nanoTime() - startedTime, exception);
    }
  }

  /** transaction: a person is sending the document a message */
  private Transaction __transaction_send(final ObjectNode request, final NtClient who) throws DocumentRequestRejectedException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionSend");
    }
    try {
      __random = new Random(Long.parseLong(__entropy.get()));
      // they must be connected
      if (!__clients.containsKey(who)) { throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.ClientNotConnectedForSend); }
      // create the delta
      final var delta = Utility.createObjectNode();
      // allocate a message id
      final var msgId = __message_id.bumpUpPost();
      // inject into the __messages object under the message id
      // annotate the message with WHO
      // extract the channel
      final var channelNode = request.get("channel");
      if (channelNode == null || !channelNode.isTextual()) { throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.SendHasNoChannel); }
      // extract the message object
      final var messagePayload = request.get("message");
      if (messagePayload == null || !messagePayload.isObject()) { throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.SendHasNoMessage); }
      final var messageDelta = delta.putObject("__messages").putObject("" + msgId);
      who.dump(messageDelta.putObject("who"));
      messageDelta.put("channel", channelNode.textValue());
      messageDelta.set("message", messagePayload);
      // associate internally
      final var task = new AsyncTask(msgId, who, channelNode.textValue(), (ObjectNode) messagePayload);
      __queue.add(task);
      // commit changes (i.e. the message id)
      __seq.bumpUpPre();
      __commit(null, delta);
      final var result = new Transaction(__seq.get(), request, delta, new TransactionResult(true, 0, __seq.get()));
      exception = false;
      return result;
    } finally {
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  /** exposed: transition the current state machine label */
  protected void __transitionStateMachine(final String next, final double timeToTransitionSeconds) {
    __state.set(next);
    __next_time.set((long) (__time.get() + timeToTransitionSeconds * 1000.0));
  }

  /** get the current number of assertion failures, then return the prior number
   * of assertion failures.
   *
   * @return the number of assertion failures */
  @Deprecated
  public AssertionStats getAndResetAssertions() {
    final var stats = new AssertionStats(__assertionTotal, __assertionFailures);
    __assertionFailures = 0;
    __assertionTotal = 0;
    return stats;
  }
}

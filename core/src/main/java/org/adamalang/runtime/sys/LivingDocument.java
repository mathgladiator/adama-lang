/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.async.AsyncTask;
import org.adamalang.runtime.async.OutstandingFutureTracker;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.delta.secure.AssetIdEncoder;
import org.adamalang.runtime.exceptions.*;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.ops.AssertionStats;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.reactives.*;
import org.adamalang.runtime.remote.*;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebPut;
import org.adamalang.runtime.sys.web.WebPutRaw;
import org.adamalang.runtime.sys.web.WebResponse;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.*;

/** The central class for a living document (i.e. a tiny VM) */
public abstract class LivingDocument implements RxParent, Caller {
  public final DocumentMonitor __monitor;
  public final LivingDocument __self;
  protected final RxInt32 __auto_future_id;
  protected final RxInt32 __auto_table_row_id;
  protected final RxInt32 __auto_gen;
  protected final RxInt32 __auto_cache_id;
  protected final RxBoolean __blocked;
  protected final RxInt32 __connection_id;
  protected final RxBoolean __constructed;
  protected final RxString __entropy;
  protected final OutstandingFutureTracker __futures;
  protected final RxInt32 __message_id;
  protected final RxInt64 __next_time;
  protected final RxInt64 __last_expire_time;
  protected final ArrayList<AsyncTask> __queue;
  protected final RxInt32 __seq;
  protected final RxString __state;
  protected final RxInt64 __time;
  protected final RxCache __cache;
  private final TreeMap<NtPrincipal, Integer> __clients;
  private final HashMap<NtPrincipal, ArrayList<PrivateView>> __trackedViews;
  private final HashMap<String, Long> __dedupe;
  private final TreeMap<Integer, RxCache> __routing;
  protected int __assertionFailures = 0;
  protected int __assertionTotal = 0;
  protected int __code_cost;
  protected int __goodwillBudget;
  protected int __goodwillLimitOfBudget;
  protected Random __random;
  protected ArrayList<Integer> __trace;
  private String __preemptedStateOnNextComputeBlocked = null;
  private String __space;
  private String __key;
  private Deliverer __deliverer;
  private boolean __raisedDirtyCalled;

  public LivingDocument(final DocumentMonitor __monitor) {
    this.__monitor = __monitor;
    __random = new Random();
    __self = this;
    __state = new RxFastString(this, "");
    __constructed = new RxBoolean(this, false);
    __blocked = new RxBoolean(this, false);
    __seq = new RxInt32(this, 0);
    __entropy = new RxString(this, Long.toString(__random.nextLong()));
    __auto_table_row_id = new RxInt32(this, 0);
    __auto_future_id = new RxInt32(this, 0);
    __connection_id = new RxInt32(this, 0);
    __message_id = new RxInt32(this, 0);
    __time = new RxInt64(this, 0L);
    __next_time = new RxInt64(this, 0L);
    __last_expire_time = new RxInt64(this, 0L);
    __auto_cache_id = new RxInt32(this, 0);
    __queue = new ArrayList<>();
    __futures = new OutstandingFutureTracker(__auto_future_id);
    __trackedViews = new HashMap<>();
    __cache = new RxCache(this, this);
    __code_cost = 0;
    __trace = new ArrayList<>();
    __clients = new TreeMap<>();
    __goodwillBudget = 100000;
    __goodwillLimitOfBudget = 100000;
    __dedupe = new HashMap<>();
    __auto_gen = new RxInt32(this, 0);
    __routing = new TreeMap<>();
    __deliverer = Deliverer.FAILURE;
  }

  /** bind a route to a cache */
  public int __bindRoute(int id, RxCache cache) {
    __routing.put(id, cache);
    return id;
  }

  /** create a route id */
  public int __createRouteId() {
    return __auto_cache_id.bumpUpPre();
  }

  /** exposed: get the current time */
  protected long __timeNow() {
    return __time.get().longValue();
  }

  /** is the given route id in-flight */
  public boolean __isRouteInflight(int routeId) {
    return __routing.containsKey(routeId);
  }

  /** remove a route id */
  public void __removeRoute(int routeId) {
    __routing.remove(routeId);
  }

  /** generate a new auto key for a table; all tables share the space id space */
  public int __genNextAutoKey() {
    return __auto_table_row_id.bumpUpPre();
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

  public void __lateBind(String space, String key, Deliverer deliverer, ServiceRegistry registry) {
    this.__space = space;
    this.__key = key;
    this.__deliverer = deliverer;
    __link(registry);
  }

  protected abstract void __link(ServiceRegistry registry);

  protected abstract void __executeServiceCalls(boolean cancel);

  /** for Caller */
  @Override
  public Deliverer __getDeliverer() {
    return __deliverer;
  }

  /** Document.key(); */
  @Override
  public String __getKey() {
    return __key;
  }

  /** Document.space(); */
  @Override
  public String __getSpace() {
    return __space;
  }

  /** Document.seq() */
  public int __getSeq() {
    return __seq.get();
  }

  /** code generate: get strings that are part of the document */
  public abstract Set<String> __get_intern_strings();

  /** commit the variables that were excluded in CodeGenRecords::canRevertOther */
  private void __internalCommit(JsonStreamWriter forward, JsonStreamWriter reverse) {
    __cache.__commit("__cache", forward, reverse);
    __auto_cache_id.__commit("__auto_cache_id", forward, reverse);
    __auto_gen.__commit("__auto_gen", forward, reverse);
  }

  private LivingDocumentChange __invalidate_trailer(NtPrincipal who, final String request) {
    final var forward = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    forward.beginObject();
    forward.writeObjectFieldIntro("__messages");
    forward.writeNull();
    reverse.beginObject();
    __dumpMessages(reverse);
    __blocked.set(false);
    __seq.bumpUpPre();
    __entropy.set(Long.toString(__random.nextLong()));
    __futures.commit();
    __queue.clear();
    __cache.clear();
    __reset_future_queues();
    __internalCommit(forward, reverse);
    __commit(null, forward, reverse);
    forward.endObject();
    reverse.endObject();
    List<LivingDocumentChange.Broadcast> broadcasts = __buildBroadcastList();
    RemoteDocumentUpdate update = new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), __state.has(), (int) (__next_time.get() - __time.get()), 0L, UpdateType.Invalidate);
    return new LivingDocumentChange(update, broadcasts, null);
  }

  private LivingDocumentChange __simple_commit(NtPrincipal who, final String request, Object response) {
    final var forward = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    forward.beginObject();
    reverse.beginObject();
    __commit(null, forward, reverse);
    __internalCommit(forward, reverse);
    forward.endObject();
    reverse.endObject();
    List<LivingDocumentChange.Broadcast> broadcasts = __buildBroadcastList();
    RemoteDocumentUpdate update = new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), __state.has(), (int) (__next_time.get() - __time.get()), 0L, UpdateType.AddUserData);
    return new LivingDocumentChange(update, broadcasts, response);
  }

  public Integer __computeRequiresInvalidateMilliseconds() {
    if (__state.has()) {
      return (int) (__next_time.get() - __time.get());
    } else {
      return null;
    }
  }

  /** code generated: what happens when the document is constructed */
  protected abstract void __construct_intern(NtPrincipal who, NtMessageBase message);

  public void __usurp(LivingDocument usurpingDocument) {
    for (Map.Entry<NtPrincipal, ArrayList<PrivateView>> existing : __trackedViews.entrySet()) {
      for (PrivateView pv : existing.getValue()) {
        // create a new view within the usurping document
        PrivateView usurper = usurpingDocument.__createView(existing.getKey(), pv.perspective, pv.assetIdEncoder);
        // the usuper takes over the current view
        pv.usurp(usurper);
      }
    }
    __code_cost = usurpingDocument.__code_cost;
  }

  public PrivateView __createView(final NtPrincipal __who, final Perspective perspective, AssetIdEncoder __encoder) {
    final var view = __createPrivateView(__who, perspective, __encoder);
    var viewsForWho = __trackedViews.get(__who);
    if (viewsForWho == null) {
      viewsForWho = new ArrayList<>();
      __trackedViews.put(__who, viewsForWho);
    }
    viewsForWho.add(view);
    return view;
  }

  /** code generated: create a private view for the given person */
  public abstract PrivateView __createPrivateView(NtPrincipal __who, Perspective __perspective, AssetIdEncoder __encoder);

  /** build broadcast for a viewer */
  public LivingDocumentChange.Broadcast __buildBroadcast(NtPrincipal who, PrivateView pv) {
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    boolean wroteData = false;
    { // write out the data if there are changes
      JsonStreamWriter data = new JsonStreamWriter();
      pv.update(data);
      String dataStr = data.toString();
      if (!"{}".equals(dataStr)) {
        writer.writeObjectFieldIntro("data");
        writer.inline(dataStr);
        writer.force_comma_introduction();
        wroteData = true;
      }
    }
    { // write out the outstanding and blockers arrays if they have changed for the view
      JsonStreamWriter futures = new JsonStreamWriter();
      __futures.dump(futures, who);
      String futuresStr = futures.toString();
      if (pv.futures(futuresStr)) {
        if (wroteData) {
          writer.force_comma();
        }
        writer.inline(futuresStr);
        writer.force_comma_introduction();
      }
    }
    writer.writeObjectFieldIntro("seq");
    writer.writeInteger(__seq.get());
    writer.endObject();
    return new LivingDocumentChange.Broadcast(pv, writer.toString());
  }

  /** internal: we compute per client */
  private ArrayList<LivingDocumentChange.Broadcast> __buildBroadcastList() {
    ArrayList<LivingDocumentChange.Broadcast> broadcasts = new ArrayList<>(__trackedViews.size());
    final var itTrackedViews = __trackedViews.entrySet().iterator();
    while (itTrackedViews.hasNext()) {
      final var entryTrackedView = itTrackedViews.next();
      final var itView = entryTrackedView.getValue().iterator();
      while (itView.hasNext()) {
        final var pv = itView.next();
        if (pv.isAlive()) {
          broadcasts.add(__buildBroadcast(entryTrackedView.getKey(), pv));
        }
      }
    }
    return broadcasts;
  }

  /** code generator: dump the entire thing */
  public abstract void __dump(JsonStreamWriter __writer);

  protected void __dumpDeduper(final JsonStreamWriter writer) {
    if (__dedupe.size() > 0) {
      writer.writeObjectFieldIntro("__dedupe");
      writer.beginObject();
      for (final Map.Entry<String, Long> entry : __dedupe.entrySet()) {
        writer.writeObjectFieldIntro(entry.getKey());
        writer.writeLong(entry.getValue());
      }
      writer.endObject();
    }
  }

  protected void __dumpClients(final JsonStreamWriter writer) {
    if (__clients.size() > 0) {
      writer.writeObjectFieldIntro("__clients");
      writer.beginObject();
      for (final Map.Entry<NtPrincipal, Integer> entry : __clients.entrySet()) {
        writer.writeObjectFieldIntro(entry.getValue());
        writer.writeNtPrincipal(entry.getKey());
      }
      writer.endObject();
    }
  }

  protected void __dumpMessages(final JsonStreamWriter writer) {
    if (__queue.size() > 0) {
      writer.writeObjectFieldIntro("__messages");
      writer.beginObject();
      for (final AsyncTask task : __queue) {
        writer.writeObjectFieldIntro(task.messageId);
        task.dump(writer);
      }
      writer.endObject();
    }
  }

  /** garbage collect the views for the given client; return the number of views for that user */
  public int __garbageCollectViews(final NtPrincipal __who) {
    final var views = __trackedViews.get(__who);
    var count = 0;
    if (views != null) {
      final var it = views.iterator();
      while (it.hasNext()) {
        if (it.next().isAlive()) {
          count++;
        } else {
          it.remove();
        }
      }
      if (count == 0) {
        __trackedViews.remove(__who);
      }
    }
    return count;
  }

  /** nuke the views and disconnect all of them */
  public void __nukeViews() {
    for (Map.Entry<NtPrincipal, ArrayList<PrivateView>> entry : __trackedViews.entrySet()) {
      for (PrivateView pv : entry.getValue()) {
        pv.kill();
        pv.perspective.disconnect();
      }
      entry.getValue().clear();
    }
    __trackedViews.clear();
  }

  /** can we remove this document from memory */
  public boolean __canRemoveFromMemory() {
    // we have some active connections
    if (__trackedViews.size() > 0) {
      return false;
    }

    // we have some persisted connections which require reconcile
    if (__clients.size() > 0) {
      return false;
    }

    // if we have a running state (i.e. asking for input or a temporal time transition)
    if (__state.has()) {
      // if blocked by user input, then let allow expire if blocked for too long
      return __blocked.get();
    }

    return true;
  }

  /** get a list of clients to disconnect due to not actually being connected */
  public List<NtPrincipal> __reconcileClientsToForceDisconnect() {
    ArrayList<NtPrincipal> clientsToDisconnect = new ArrayList<>();
    for (NtPrincipal connected : __clients.keySet()) {
      if (!__trackedViews.containsKey(connected)) {
        clientsToDisconnect.add(connected);
      }
    }
    return clientsToDisconnect;
  }

  /** get how much the current code "costs" */
  public int __getCodeCost() {
    return __code_cost;
  }

  /** reset the cost */
  public void __zeroOutCodeCost() {
    __code_cost = 0;
  }

  /** get the number of connected clients */
  public int __getConnectionsCount() {
    int sumClients = 0;
    for (Map.Entry<NtPrincipal, ArrayList<PrivateView>> perClient : __trackedViews.entrySet()) {
      sumClients += perClient.getValue().size();
    }
    return sumClients;
  }

  /** code generated: get the tests for the document */
  public abstract String[] __getTests();

  /** exposed: this allows the child object to see if it can still do stuff */
  protected boolean __goodwill(final int startLine, final int startPosition, final int endLine, final int endLinePosition) {
    if (__goodwillBudget > 0) {
      __goodwillBudget--;
    }
    if (__goodwillBudget == 0) {
      if (__monitor != null) {
        __monitor.goodwillFailureAt(startLine, startPosition, endLine, endLinePosition);
      }
      __revert();
      throw new GoodwillExhaustedException(startLine, startPosition, endLine, endLinePosition);
    }
    return true;
  }

  /** code generated: revert the tree, all changes revert back */
  public abstract void __revert();

  protected void __hydrateDeduper(final JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        final var key = reader.fieldName();
        if (reader.testLackOfNull()) {
          __dedupe.put(key, reader.readLong());
        } else {
          __dedupe.remove(key);
        }
      }
    }
  }

  protected void __hydrateClients(final JsonStreamReader reader) {
    final var killSet = new HashSet<Integer>();
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        final var key = Integer.parseInt(reader.fieldName());
        if (reader.testLackOfNull()) {
          __clients.put(reader.readNtPrincipal(), key);
        } else {
          killSet.add(key);
        }
      }
    }
    final var it = __clients.entrySet().iterator();
    while (it.hasNext()) {
      if (killSet.contains(it.next().getValue())) {
        it.remove();
      }
    }
  }

  protected void __hydrateMessages(final JsonStreamReader reader) {
    if (reader.testLackOfNull()) {
      if (reader.startObject()) {
        while (reader.notEndOfObject()) {
          final var msgId = Integer.parseInt(reader.fieldName());
          final var tasks = new TreeMap<Integer, AsyncTask>();
          for (final AsyncTask oldTask : __queue) {
            tasks.put(oldTask.messageId, oldTask);
          }
          __queue.clear();
          if (reader.testLackOfNull()) {
            if (reader.startObject()) {
              NtPrincipal who = null;
              String channel = null;
              Object message = null;
              var timestamp = 0L;
              while (reader.notEndOfObject()) {
                final var f = reader.fieldName();
                switch (f) {
                  case "who":
                    who = reader.readNtPrincipal();
                    break;
                  case "channel":
                    channel = reader.readString();
                    break;
                  case "timestamp":
                    timestamp = reader.readLong();
                    break;
                  case "message":
                    message = __parse_message(channel, reader);
                    break;
                  default:
                    reader.skipValue();
                }
              }
              final var task = new AsyncTask(msgId, who, channel, timestamp, message);
              tasks.put(msgId, task);
            }
          } else {
            tasks.remove(msgId);
          }
          __queue.addAll(tasks.values());
        }
      }
    } else {
      __queue.clear();
    }
  }

  /** parse the message for the channel, and cache the result */
  protected abstract Object __parse_message(String channel, JsonStreamReader reader);

  protected abstract boolean __is_direct_channel(String channel);

  protected abstract void __handle_direct(NtPrincipal who, String channel, Object message) throws AbortMessageException;

  /** code generated: insert data */
  public abstract void __insert(JsonStreamReader __reader);

  /** code generated: patch data */
  public abstract void __patch(JsonStreamReader __reader);

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

  /** code generated: respond to a get request */
  public abstract WebResponse __get(WebGet __get);

  /** code generated: respond to a put request */
  protected abstract WebResponse __put_internal(WebPut __put);

  public boolean __isConnected(final NtPrincipal __who) {
    return __clients.containsKey(__who);
  }

  /** code generated: allow the document to accept/reject the client */
  public abstract boolean __onConnected(NtPrincipal clientValue);

  /** ran with loaded */
  public abstract void __onLoad();

  /** code generated: let the document know of a disconnected client */
  public abstract void __onDisconnected(NtPrincipal clientValue);

  /** code generate: let the document know an asset was uploaded */
  public abstract void __onAssetAttached(NtPrincipal __cvalue, NtAsset __asset);

  /** code generate: can the client even attach any data */
  public abstract boolean __onCanAssetAttached(NtPrincipal __cvalue);

  /** code generated: convert the reader into a constructor arg */
  protected abstract NtMessageBase __parse_construct_arg(JsonStreamReader reader);

  /** exposed: preempty the state machine */
  protected void __preemptStateMachine(final String next) {
    __preemptedStateOnNextComputeBlocked = next;
  }

  /** artifact: from RxParent */
  @Override
  public void __raiseDirty() {
    __raisedDirtyCalled = true;
  }

  /** for the reactive children, the root is always alive */
  public boolean __isAlive() {
    return true;
  }

  /** the code will vomit up a signal to destroy itself. This must be caught at a higher level. */
  protected void __destroyDocument() {
    throw new PerformDocumentDeleteException();
  }

  /**
   * this code will vomit up a signal to rewind to a prior state. This must be caught at a higher
   * level
   */
  protected void __rewindDocument(int seq) {
    // restore the document state
    __revert();
    throw new PerformDocumentRewindException(seq);
  }

  /** exposed: random number between 0 and n exclusive */
  protected int __randomBoundInt(final int n) {
    if (n < 0) {
      return 0;
    }
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

  /** exposed: random long */
  protected long __randomLong() {
    return __random.nextLong();
  }

  /** code generated: reset internal queues */
  protected abstract void __reset_future_queues();

  /** code generated: route the given message */
  protected abstract void __route(AsyncTask task);

  /**
   * available to the test runner... me thinks this can be done... mucho better (requires precommit)
   */
  public String __run_test(final TestReportBuilder report, final String testName) {
    __test(report, testName);
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    __commit(null, writer, new JsonStreamWriter());
    writer.endObject();
    return writer.toString();
  }

  /** code generated: run the test for the given test name */
  public abstract void __test(TestReportBuilder report, String testName);

  /** code generated: commit the tree, and push data into the given delta */
  public abstract void __commit(String name, JsonStreamWriter forward, JsonStreamWriter reverse);

  /** estimate the memory of the document */
  public long __memory() {
    long memory = 384;
    for (String dedupeKey : __dedupe.keySet()) {
      memory += dedupeKey.length() * 2 + 16;
    }
    for (Map.Entry<NtPrincipal, ArrayList<PrivateView>> entry : __trackedViews.entrySet()) {
      memory += entry.getKey().memory();
      for (PrivateView view : entry.getValue()) {
        memory += view.memory();
      }
    }
    return memory;
  }

  /** exposed: @step; for testing */
  protected void __test_progress() {
    try {
      for (final AsyncTask task : __queue) {
        __route(task);
      }
      final long timeBackup = __time.get();
      for (final AsyncTask task : __queue) {
        __time.set(task.timestamp);
        task.execute();
      }
      __time.set(timeBackup);
      final var stateToExecute = __state.get();
      __state.set("");
      __invoke_label(stateToExecute);
      __blocked.set(false);
      __seq.bumpUpPre();
      __entropy.set(Long.toString(__random.nextLong()));
      __futures.commit();
      __queue.clear();
      __reset_future_queues();
      __commit(null, new JsonStreamWriter(), new JsonStreamWriter());
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

  /** transaction: core API (New Version in Draft) */
  public LivingDocumentChange __transact(final String requestJson, LivingDocumentFactory factory) throws ErrorCodeException {
    try {
      final var reader = new JsonStreamReader(requestJson);
      String command = null;
      Long timestamp = null;
      Long limit = null;
      NtPrincipal who = null;
      Object message = null;
      NtMessageBase arg = null;
      String channel = null;
      String patch = null;
      String entropy = null;
      String marker = null;
      Integer delivery_id = null;
      RemoteResult result = null;
      NtAsset asset = null;
      String origin = null;
      String ip = null;
      String key = null;
      WebPutRaw put = null;
      if (reader.startObject()) {
        while (reader.notEndOfObject()) {
          final var fieldName = reader.fieldName();
          switch (fieldName) {
            case "command":
              command = reader.readString();
              break;
            case "origin":
              origin = reader.readString();
              break;
            case "ip":
              ip = reader.readString();
              break;
            case "key":
              key = reader.readString();
              break;
            case "marker":
              marker = reader.readString();
              break;
            case "timestamp":
              timestamp = reader.readLong();
              break;
            case "limit":
              limit = reader.readLong();
              break;
            case "who":
              who = reader.readNtPrincipal();
              break;
            case "channel":
              channel = reader.readString();
              break;
            case "entropy":
              entropy = reader.readString();
              break;
            case "patch":
              patch = reader.skipValueIntoJson();
              break;
            case "put":
              put = WebPutRaw.read(reader);
              break;
            case "delivery_id":
              delivery_id = reader.readInteger();
              break;
            case "result":
              result = new RemoteResult(reader);
              break;
            case "message":
              message = __parse_message(channel, reader);
              break;
            case "asset":
              asset = reader.readNtAsset();
              break;
            case "arg": // for constructor
              arg = __parse_construct_arg(reader);
              break;
            default:
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_UNRECOGNIZED_FIELD_PRESENT);
          }
        }
      }
      if (command == null) {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_COMMAND_FOUND);
      }
      if (timestamp == null) {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_TIMESTAMP);
      }
      if ("load".equals(command)) {
        return __transaction_load(requestJson, timestamp);
      }
      __time.set(timestamp);
      switch (command) {
        case "invalidate":
          if (__monitor != null) {
            return __transaction_invalidate_monitored(who, requestJson);
          } else {
            return __transaction_invalidate_body(who, requestJson);
          }
        case "construct":
          if (who == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO);
          }
          if (__constructed.get()) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_ALREADY_CONSTRUCTED);
          }
          if (arg == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_CONSTRUCTOR_ARG);
          }
          return __transaction_construct(requestJson, who, arg, entropy);
        case "connect":
          if (who == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO);
          }
          return __transaction_connect(requestJson, who);
        case "disconnect":
          if (who == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO);
          }
          return __transaction_disconnect(requestJson, who);
        case "attach":
          if (who == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO);
          }
          if (asset == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_ASSET);
          }
          return __transaction_attach(requestJson, who, asset);
        case "send":
          if (who == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO);
          }
          if (channel == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CHANNEL);
          }
          if (message == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_MESSAGE);
          }
          if (key == null || origin == null || ip == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CONTEXT);
          }
          CoreRequestContext context = new CoreRequestContext(who, origin, ip, key);
          return __transaction_send(context, requestJson, who, marker, channel, timestamp, message, factory);
        case "deliver":
          if (who == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO);
          }
          if (delivery_id == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_DELIVERY_ID);
          }
          if (result == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_RESULT);
          }
          return __transaction_deliver(requestJson, who, delivery_id, result);
        case "expire":
          if (limit == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_LIMIT);
          }
          return __transaction_expire(requestJson, limit);
        case "web_put":
          if (who == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO);
          }
          if (put == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_PUT);
          }
          return __transaction_web_put(requestJson, new WebPut(who, put));
        case "apply":
          if (who == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO);
          }
          if (patch == null) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_PATCH);
          }
          return __transaction_apply_patch(requestJson, who, patch);
      }
      throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_VALID_COMMAND_FOUND);
    } catch (GoodwillExhaustedException gee) {
      throw new ErrorCodeException(ErrorCodes.API_GOODWILL_EXCEPTION, gee);
    }
  }

  private LivingDocumentChange __transaction_load(final String request, long timestamp) throws ErrorCodeException {
    __raisedDirtyCalled = false;
    __onLoad();
    if (!__raisedDirtyCalled) {
      return null;
    }
    __time.set(timestamp);
    __seq.bumpUpPre();
    final var forward = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    forward.beginObject();
    reverse.beginObject();
    __commit(null, forward, reverse);
    __internalCommit(forward, reverse);
    forward.endObject();
    reverse.endObject();
    RemoteDocumentUpdate update = new RemoteDocumentUpdate(__seq.get(), __seq.get(), NtPrincipal.NO_ONE, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.AddUserData);
    return new LivingDocumentChange(update, null, null);
  }

  private LivingDocumentChange __transaction_deliver(final String request, NtPrincipal who, int deliveryId, RemoteResult result) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    boolean exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionWebPut");
    }
    try {
      RxCache route = __routing.get(deliveryId);
      if (route == null) {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_ROUTE_DOCUMENT);
      }
      if (!route.deliver(deliveryId, result)) {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_ROUTE_CACHE);
      }
      __seq.bumpUpPre();
      final var forward = new JsonStreamWriter();
      final var reverse = new JsonStreamWriter();
      forward.beginObject();
      reverse.beginObject();
      __commit(null, forward, reverse);
      __internalCommit(forward, reverse);
      forward.endObject();
      reverse.endObject();
      RemoteDocumentUpdate update = new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.AddUserData);
      return new LivingDocumentChange(update, null, null);
    } finally {
      if (exception) {
        __revert();
      }
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  private LivingDocumentChange __transaction_web_put(final String request, final WebPut put) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    boolean exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionWebPut");
    }
    try {
      __seq.bumpUpPre();
      __randomizeOutOfBand();
      WebResponse response = __put_internal(put);
      exception = false;
      return __simple_commit(put.who, request, response);
    } finally {
      if (exception) {
        __revert();
      }
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  private void __randomizeOutOfBand() {
    long entropy = Long.parseLong(__entropy.get());
    if (!__state.has()) {
      __random = new Random(entropy);
      __entropy.set(Long.toString(__random.nextLong()));
    } else {
      __random = new Random(entropy + __time.get());
    }
  }

  /** transaction: a person connects to document */
  private LivingDocumentChange __transaction_attach(final String request, final NtPrincipal who, final NtAsset asset) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionAttach");
    }
    try {
      if (!__clients.containsKey(who)) {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_ATTACH_NOT_CONNECTED);
      }
      // execute the attachment
      __onAssetAttached(who, asset);
      __seq.bumpUpPre();

      exception = false;
      return __simple_commit(who, request, null);
    } finally {
      if (exception) {
        __revert();
      }
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  /** transaction: a person connects to document */
  private LivingDocumentChange __transaction_connect(final String request, final NtPrincipal who) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionConnect");
    }
    try {
      if (__clients.containsKey(who)) {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_ALREADY_CONNECTED);
      }
      __randomizeOutOfBand();
      if (__onConnected(who)) {
        // user was accepted, so let's commit it
        // generate a connection id for the user
        final var cId = __connection_id.bumpUpPost();
        // associate the client to the connection id
        __clients.put(who, cId);
        // commit the tree to the delta
        __seq.bumpUpPre();
        final var forward = new JsonStreamWriter();
        final var reverse = new JsonStreamWriter();
        forward.beginObject();
        reverse.beginObject();
        __commit(null, forward, reverse);
        forward.writeObjectFieldIntro("__clients");
        forward.beginObject();
        forward.writeObjectFieldIntro(cId);
        forward.writeNtPrincipal(who);
        forward.endObject();
        forward.endObject();

        reverse.writeObjectFieldIntro("__clients");
        reverse.beginObject();
        reverse.writeObjectFieldIntro(cId);
        reverse.writeNull();
        reverse.endObject();
        reverse.endObject();
        final var result = new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.AddUserData);
        exception = false;
        return new LivingDocumentChange(result, null, null);
      } else {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CLIENT_REJECTED);
      }
    } finally {
      if (exception) {
        __revert();
      }
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  /** transaction: construct the document */
  private LivingDocumentChange __transaction_construct(final String request, final NtPrincipal who, final NtMessageBase arg, final String entropy) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionConstruct");
    }
    try {
      if (entropy != null) {
        __entropy.set(entropy);
      }
      __random = new Random(Long.parseLong(__entropy.get()));
      __construct_intern(who, arg);
      __constructed.set(true);
      final var forward = new JsonStreamWriter();
      final var reverse = new JsonStreamWriter();
      forward.beginObject();
      reverse.beginObject();
      __commit(null, forward, reverse);
      forward.endObject();
      reverse.endObject();
      final var result = new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.AddUserData);
      exception = false;
      return new LivingDocumentChange(result, null, null);
    } finally {
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  /** transaction: a person disconnects from the document */
  private LivingDocumentChange __transaction_disconnect(final String request, final NtPrincipal who) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionDisconnect");
    }
    try {
      __randomizeOutOfBand();
      if (!__clients.containsKey(who)) {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_DISCONNECT_DUE_TO_NOT_CONNECTED);
      }
      // disconnect them
      __onDisconnected(who);
      // stop tracking them
      final int id = __clients.remove(who);
      __seq.bumpUpPre();
      final var forward = new JsonStreamWriter();
      final var reverse = new JsonStreamWriter();
      forward.beginObject();
      reverse.beginObject();
      __commit(null, forward, reverse);
      forward.writeObjectFieldIntro("__clients");
      forward.beginObject();
      forward.writeObjectFieldIntro(id);
      forward.writeNull();
      forward.endObject();
      forward.endObject();
      reverse.writeObjectFieldIntro("__clients");
      reverse.beginObject();
      reverse.writeObjectFieldIntro(id);
      reverse.writeNtPrincipal(who);
      reverse.endObject();
      reverse.endObject();
      final var result = new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.AddUserData);
      exception = false;
      return new LivingDocumentChange(result, null, null);
    } finally {
      if (exception) {
        __revert();
      }
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  /** transaction: apply a data patch to the document */
  private LivingDocumentChange __transaction_apply_patch(final String request, final NtPrincipal who, String patch) {
    __patch(new JsonStreamReader(patch));
    __seq.bumpUpPre();
    return __simple_commit(who, request, null);
  }

  /** transaction: an invalidation is happening on the document (no monitor) */
  private LivingDocumentChange __transaction_invalidate_body(NtPrincipal who, final String request) {
    __preemptedStateOnNextComputeBlocked = null;
    final var seedUsed = Long.parseLong(__entropy.get());
    try {
      __random = new Random(seedUsed);
      for (final AsyncTask task : __queue) {
        __route(task);
      }
      final long timeBackup = __time.get();
      for (final AsyncTask task : __queue) {
        __time.set(task.timestamp);
        task.execute();
      }
      __time.set(timeBackup);
      // execute the state
      if (__state.has() && __next_time.get() <= __time.get()) {
        final var stateToExecute = __state.get();
        __state.set("");
        __invoke_label(stateToExecute);
      }
      return __invalidate_trailer(who, request);
    } catch (final ComputeBlockedException blockedOn) {
      if (__preemptedStateOnNextComputeBlocked != null) {
        __state.set(__preemptedStateOnNextComputeBlocked);
        __next_time.set(__time.get());
        __preemptedStateOnNextComputeBlocked = null;
        return __invalidate_trailer(who, request);
      } else {
        List<LivingDocumentChange.Broadcast> broadcasts = __buildBroadcastList();
        __revert();
        __futures.restore();
        __reset_future_queues();
        __blocked.set(true);
        __seq.bumpUpPre();
        final var forward = new JsonStreamWriter();
        final var reverse = new JsonStreamWriter();
        forward.beginObject();
        reverse.beginObject();
        if (blockedOn.channel != null) {
          forward.writeObjectFieldIntro("__blocked_on");
          forward.writeFastString(blockedOn.channel);
        }
        __internalCommit(forward, reverse);
        __commit(null, forward, reverse);
        forward.endObject();
        reverse.endObject();
        return new LivingDocumentChange(new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), false, 0, 0L, UpdateType.Internal), broadcasts, null);
      }
    } catch (final RetryProgressException rpe) {
      __futures.restore();
      __reset_future_queues();
      __revert();
      final var forward = new JsonStreamWriter();
      final var reverse = new JsonStreamWriter();
      forward.beginObject();
      forward.writeObjectFieldIntro("__messages");
      forward.beginObject();
      forward.writeObjectFieldIntro(rpe.failedTask.messageId);
      forward.writeNull();
      forward.endObject();
      reverse.beginObject();
      reverse.writeObjectFieldIntro("__messages");
      reverse.beginObject();
      reverse.writeObjectFieldIntro(rpe.failedTask.messageId);
      rpe.failedTask.dump(reverse);
      reverse.endObject();
      __seq.bumpUpPre();
      __internalCommit(forward, reverse);
      __commit(null, forward, reverse);
      forward.endObject();
      reverse.endObject();
      return new LivingDocumentChange(new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.Internal), null, null);
    }
  }

  /** transaction: an invalidation is happening on the document (use monitor) */
  private LivingDocumentChange __transaction_invalidate_monitored(final NtPrincipal who, final String request) {
    var exception = true;
    final var startedTime = System.nanoTime();
    __monitor.push("TransactionInvalidate");
    try {
      final var result = __transaction_invalidate_body(who, request);
      exception = false; // this is basically useless, but nothing within this function should be a
      // subscribe
      return result;
    } finally {
      __monitor.pop(System.nanoTime() - startedTime, exception);
    }
  }

  private LivingDocumentChange __transaction_expire(final String request, final long limit) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionExpire");
    }
    try {
      if (limit < 0) {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_EXPIRE_LIMIT_MUST_BE_POSITIVE);
      }
      // create the delta
      final var forward = new JsonStreamWriter();
      forward.beginObject();
      forward.writeObjectFieldIntro("__dedupe");
      final var reverse = new JsonStreamWriter();
      reverse.beginObject();
      reverse.writeObjectFieldIntro("__dedupe");
      // compute how how young an item could be to keep
      long expireBefore = __time.get() - limit;
      forward.beginObject();
      reverse.beginObject();
      boolean didNothing = true;
      Iterator<Map.Entry<String, Long>> it = __dedupe.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry<String, Long> entry = it.next();
        if (entry.getValue() < expireBefore) {
          didNothing = false;
          it.remove();
          forward.writeObjectFieldIntro(entry.getKey());
          forward.writeNull();
          reverse.writeObjectFieldIntro(entry.getKey());
          reverse.writeLong(entry.getValue());
        }
      }
      forward.endObject();
      reverse.endObject();
      if (didNothing) {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_EXPIRE_DID_NOTHING);
      }
      __last_expire_time.set(expireBefore);
      __seq.bumpUpPre();
      __commit(null, forward, reverse);
      forward.endObject();
      reverse.endObject();

      final var result = new RemoteDocumentUpdate(__seq.get(), __seq.get(), NtPrincipal.NO_ONE, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.Internal);
      exception = false;
      return new LivingDocumentChange(result, null, null);
    } finally {
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  private LivingDocumentChange __transaction_send_commit(final String request, final String dedupeKey, final NtPrincipal who, final String marker, final String channel, final long timestamp, final Object message, final LivingDocumentFactory factory) throws ErrorCodeException {
    final var forward = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    forward.beginObject();
    reverse.beginObject();
    if (marker != null) {
      forward.writeObjectFieldIntro("__dedupe");
      forward.beginObject();
      forward.writeObjectFieldIntro(dedupeKey);
      forward.writeLong(__time.get());
      forward.endObject();
    }
    __randomizeOutOfBand();
    __seq.bumpUpPre();
    __commit(null, forward, reverse);
    forward.endObject();
    reverse.endObject();
    List<LivingDocumentChange.Broadcast> broadcasts = __buildBroadcastList();
    RemoteDocumentUpdate update = new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), __state.has(), (int) Math.max(0, __next_time.get() - __time.get()), 0L, UpdateType.DirectMessageExecute);
    return new LivingDocumentChange(update, broadcasts, null);
  }

  private LivingDocumentChange __transaction_send_enqueue(final String request, final String dedupeKey, final NtPrincipal who, final String marker, final String channel, final long timestamp, final Object message, final LivingDocumentFactory factory) throws ErrorCodeException {
    // create the delta
    final var forward = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    // allocate a message id
    final var msgId = __message_id.bumpUpPost();
    // inject into the __messages object under the message id
    // annotate the message with WHO
    forward.beginObject();
    if (marker != null) {
      forward.writeObjectFieldIntro("__dedupe");
      forward.beginObject();
      forward.writeObjectFieldIntro(dedupeKey);
      forward.writeLong(__time.get());
      forward.endObject();
    }
    forward.writeObjectFieldIntro("__messages");
    forward.beginObject();
    forward.writeObjectFieldIntro(msgId);
    final var task = new AsyncTask(msgId, who, channel, timestamp, message);
    task.dump(forward);
    forward.endObject();
    reverse.beginObject();
    if (marker != null) {
      reverse.writeObjectFieldIntro("__dedupe");
      reverse.beginObject();
      reverse.writeObjectFieldIntro(dedupeKey);
      reverse.writeNull();
      reverse.endObject();
    }
    reverse.writeObjectFieldIntro("__messages");
    reverse.beginObject();
    reverse.writeObjectFieldIntro(msgId);
    reverse.writeNull();
    reverse.endObject();
    __queue.add(task);
    // commit changes (i.e. the message id)
    __seq.bumpUpPre();
    __commit(null, forward, reverse);
    forward.endObject();
    reverse.endObject();
    final var result = new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.AddUserData);
    return new LivingDocumentChange(result, null, null);
  }

  /** transaction: a person is sending the document a message */
  private LivingDocumentChange __transaction_send(CoreRequestContext context, final String request, final NtPrincipal who, final String marker, final String channel, final long timestamp, final Object message, final LivingDocumentFactory factory) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionSend");
    }
    try {
      // they must be connected
      if (!__clients.containsKey(who) && !factory.canSendWhileDisconnected(context)) {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NOT_CONNECTED);
      }

      String dedupeKey = who.agent + "/" + who.authority + "/" + marker;
      if (marker != null) {
        if (__dedupe.containsKey(dedupeKey)) {
          throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_MESSAGE_ALREADY_SENT);
        }
        __dedupe.put(dedupeKey, __time.get());
      }
      LivingDocumentChange change;
      if (__is_direct_channel(channel)) {
        try {
          __random = new Random(Long.parseLong(__entropy.get()) + timestamp);
          __handle_direct(who, channel, message);
          change = __transaction_send_commit(request, dedupeKey, who, marker, channel, timestamp, message, factory);
        } catch (AbortMessageException ame) {
          throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_MESSAGE_DIRECT_ABORT);
        } catch (ComputeBlockedException cbe) {
          __revert();
          change = __transaction_send_enqueue(request, dedupeKey, who, marker, channel, timestamp, message, factory);
        }
      } else {
        change = __transaction_send_enqueue(request, dedupeKey, who, marker, channel, timestamp, message, factory);
      }
      exception = false;
      return change;
    } finally {
      if (exception) {
        __revert();
      }
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

  /**
   * get the current number of assertion failures, then return the prior number of assertion
   * failures.
   * @return the number of assertion failures
   */
  @Deprecated
  public AssertionStats getAndResetAssertions() {
    final var stats = new AssertionStats(__assertionTotal, __assertionFailures);
    __assertionFailures = 0;
    __assertionTotal = 0;
    return stats;
  }

  public static class StaticState {
    protected int __goodwillBudget;

    public StaticState() {
      this.__goodwillBudget = 25000;
    }

    public boolean __goodwill(final int startLine, final int startPosition, final int endLine, final int endLinePosition) {
      if (__goodwillBudget > 0) {
        __goodwillBudget--;
        if (__goodwillBudget == 0) {
          throw new GoodwillExhaustedException(startLine, startPosition, endLine, endLinePosition);
        }
      }
      return true;
    }
  }
}

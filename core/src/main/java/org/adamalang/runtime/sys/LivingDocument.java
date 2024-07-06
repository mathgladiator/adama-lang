/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.sys;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.runtime.async.*;
import org.adamalang.runtime.contracts.*;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.data.UpdateType;
import org.adamalang.runtime.exceptions.*;
import org.adamalang.runtime.graph.Graph;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.json.TrivialPrivateView;
import org.adamalang.runtime.natives.*;
import org.adamalang.runtime.ops.AssertionStats;
import org.adamalang.runtime.ops.TestMockUniverse;
import org.adamalang.runtime.ops.TestReportBuilder;
import org.adamalang.runtime.reactives.*;
import org.adamalang.runtime.remote.*;
import org.adamalang.runtime.remote.replication.ReplicationEngine;
import org.adamalang.runtime.sys.web.*;
import org.adamalang.runtime.sys.web.partial.WebDeletePartial;
import org.adamalang.runtime.sys.web.partial.WebPutPartial;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Supplier;

/** The central class for a living document (i.e. a tiny VM) */
public abstract class LivingDocument implements RxParent, Caller {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LivingDocument.class);
  public final DocumentMonitor __monitor;
  public final LivingDocument __self;
  public final ReplicationEngine __replication;
  protected final RxInt32 __auto_future_id;
  protected final RxInt32 __auto_table_row_id;
  protected final RxInt32 __auto_gen;
  protected final RxInt32 __auto_cache_id;
  protected final RxBoolean __blocked;
  protected final RxInt32 __connection_id;
  protected final RxBoolean __constructed;
  protected final RxString __entropy;
  protected final TimeoutTracker __timeouts;
  protected final OutstandingFutureTracker __futures;
  protected final RxInt32 __message_id;
  protected final RxInt64 __next_time;
  protected final RxInt64 __last_expire_time;
  protected final ArrayList<AsyncTask> __queue;
  protected final RxInt32 __seq;
  protected final RxString __state;
  protected final RxInt64 __time;
  protected final RxLazy<NtDate> __today;
  protected final RxLazy<Long> __timeDelay;
  protected final RxCache __cache;
  protected final RxString __timezone;
  protected final RxInt32 __webTaskId;
  protected final WebQueue __webQueue;
  protected final ArrayList<EphemeralWebGet> __gets;
  protected final Graph __graph;
  protected final TreeMap<NtPrincipal, Integer> __clients;
  protected final HashMap<NtPrincipal, ArrayList<PrivateView>> __trackedViews;
  protected final HashMap<Integer, PrivateView> __viewsById;
  private final HashMap<String, Long> __dedupe;
  private final TreeMap<Integer, RxCache> __routing;
  public PerfTracker __perf;
  protected int __assertionFailures = 0;
  protected int __assertionTotal = 0;
  protected int __code_cost;
  protected int __goodwillBudget;
  protected int __goodwillLimitOfBudget;
  protected Random __random;
  protected ArrayList<Integer> __trace;
  protected RxCache __currentWebCache;
  protected ZoneId __timezoneCachedZoneId;
  private int __currentViewId;
  private String __preemptedStateOnNextComputeBlocked = null;
  protected String __space;
  protected String __key;
  private Deliverer __deliverer;
  private boolean __raisedDirtyCalled;
  private int __nextViewId;
  protected  long __optimisticNextCronCheck;
  protected final EnqueuedTaskManager __enqueued;
  private long __cpu_ms;
  private TestMockUniverse __mock_universe;
  private Integer __seq_message;

  public LivingDocument(final DocumentMonitor __monitor) {
    this.__monitor = __monitor;
    __random = new Random();
    __self = this;
    __cpu_ms = 0L;
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
    __today = new RxLazy<>(this, () -> __dateOfToday(), null) {
      @Override
      public boolean __raiseInvalid() {
        if (!__dateOfToday().equals(this.cached)) {
          super.__raiseInvalid();
          __forceSettle();
        }
        return true;
      }
    };
    __timeDelay = new RxLazy<>(this, () -> __datetimeNow().dateTime.toInstant().toEpochMilli(), null) {
      @Override
      public boolean __raiseInvalid() {
        if (this.cached == null || Math.abs(this.cached - formula.get()) > 60000) {
          super.__raiseInvalid();
          __forceSettle();
        }
        return true;
      }
    };
    __time.__subscribe(__today);
    __time.__subscribe(__timeDelay);
    __next_time = new RxInt64(this, 0L);
    __last_expire_time = new RxInt64(this, 0L);
    __auto_cache_id = new RxInt32(this, 0);
    __queue = new ArrayList<>();
    __timeouts = new TimeoutTracker(__time);
    __futures = new OutstandingFutureTracker(__auto_future_id, __timeouts);
    __trackedViews = new HashMap<>();
    __viewsById = new HashMap<>();
    __cache = new RxCache(this, this);
    __code_cost = 0;
    __trace = new ArrayList<>();
    __clients = new TreeMap<>();
    __goodwillBudget = 10000000;
    __goodwillLimitOfBudget = 10000000;
    __dedupe = new HashMap<>();
    __auto_gen = new RxInt32(this, 0);
    __routing = new TreeMap<>();
    __deliverer = Deliverer.FAILURE;
    __timezone = new RxString(this, "UTC");
    __timezoneCachedZoneId = ZoneId.of(__timezone.get());
    __nextViewId = 0;
    __webTaskId = new RxInt32(this, 0);
    __webQueue = new WebQueue(__webTaskId);
    __currentWebCache = null;
    __gets = new ArrayList<>();
    __replication = new ReplicationEngine(this);
    __perf = new PerfTracker(this);
    __graph = new Graph();
    __optimisticNextCronCheck = 0L;
    __enqueued = new EnqueuedTaskManager();
    __mock_universe = null;
  }

  /** exposed: get the document's timestamp as a date */
  protected NtDate __dateOfToday() {
    ZonedDateTime dt = __datetimeNow().dateTime;
    return new NtDate(dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth());
  }

  /** exposed: get the document's timestamp as a datetime */
  private long lastCachedValue = 0;
  private NtDateTime lastCachedDateTimeNow = null;
  protected NtDateTime __datetimeNow() {
    long now = __time.get().longValue();
    if (lastCachedDateTimeNow == null || lastCachedValue != now) {
      // create a system instance
      lastCachedValue = now;
      Instant instant = Instant.ofEpochMilli(now);
      ZonedDateTime pdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
      // convert to the document
      lastCachedDateTimeNow = new NtDateTime(pdt.withZoneSameInstant(__zoneId()));
    }
    return lastCachedDateTimeNow;
  }

  protected ZoneId __zoneId() {
    if (__timezoneCachedZoneId == null) {
      __timezoneCachedZoneId = ZoneId.of(__timeZone());
    }
    return __timezoneCachedZoneId;
  }

  /** exposed: get the document's time zone */
  protected String __timeZone() {
    if ("".equals(__timezone.get())) {
      return "UTC";
    }
    return __timezone.get();
  }

  /** exposed: get the document's timestamp as a time */
  protected NtTime __timeOfToday() {
    ZonedDateTime dt = __datetimeNow().dateTime;
    return new NtTime(dt.getHour(), dt.getMinute());
  }

  public void __removed() {
    __webQueue.cancel();
  }

  /** generate a view id */
  public int __genViewId() {
    return __nextViewId++;
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

  /** exposed: is the principal from the document */
  protected boolean __isFromDocument(NtPrincipal p) {
    return p.authority.equals("doc/" + __space + "/" + __key);
  }

  /** exposed: is the principal from the space (i.e. a document within */
  protected boolean __isFromSpace(NtPrincipal p) {
    return p.authority.startsWith("doc/" + __space + "/");
  }

  /** exposed: get a principal for the document */
  protected NtPrincipal __principalOf(String agent) {
    return new NtPrincipal(agent, "doc/" + __space + "/" + __key);
  }

  /** exposed: get the current time */
  protected long __timeNow() {
    return __time.get().longValue();
  }

  protected void __enqueue(String channel, NtPrincipal who, NtMessageBase message) {
    final var msgId = __message_id.bumpUpPost();
    __enqueued.add(new EnqueuedTask(msgId, who, channel, __currentViewId, message.to_dynamic()));
  }

  /** exposed: set the document's time zone */
  protected boolean __setTimeZone(String timezone) {
    try {
      ZoneId zoneId = ZoneId.of(timezone);
      if (zoneId != null) {
        __timezone.set(timezone);
        __timezoneCachedZoneId = zoneId;
        return true;
      }
    } catch (Exception nope) {
    }
    return false;
  }

  protected void __disconnect(NtPrincipal who) {
    ArrayList<PrivateView> views = __trackedViews.get(who);
    if (views != null) {
      for (PrivateView pv : views) {
        pv.deliver("{\"force-disconnect\":true}");
      }
    }
  }

  protected abstract void __debug(JsonStreamWriter __writer);

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
    // TODO: see if a message has a history of id generation
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
    if (registry instanceof TestMockUniverse) {
      // this is related to testing such that blocks can play with services
      __mock_universe = (TestMockUniverse) registry;
    }
  }

  protected abstract void __link(ServiceRegistry registry);

  public abstract String __metrics();

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
    if (__seq_message != null) {
      return __seq_message;
    }
    return __seq.get();
  }

  /** code generate: get strings that are part of the document */
  public abstract Set<String> __get_intern_strings();

  /** commit the variables that were excluded in CodeGenRecords::canRevertOther */
  private void __internalCommit(JsonStreamWriter forward, JsonStreamWriter reverse) {
    __cache.__commit("__cache", forward, reverse);
    __auto_cache_id.__commit("__auto_cache_id", forward, reverse);
    __auto_gen.__commit("__auto_gen", forward, reverse);
    __timeouts.commit(forward, reverse);
    __webQueue.commit(forward, reverse);
    __replication.commit(forward, reverse);
    __enqueued.commit(forward, reverse);
    __graph.compute();
  }

  private boolean __again(boolean hasTimeouts) {
    return __state.has() || __enqueued.size() > 0 || hasTimeouts;
  }

  private int __deltaTime() {
    int t = Math.max(__state.has() ? 25 : 0, (int) Math.min((Integer.MAX_VALUE / 2), (long) __next_time.get() - __time.get()));
    if (__enqueued.size() > 0) {
      t = Math.min(25, t);
    }
    return t;
  }

  private LivingDocumentChange __invalidate_trailer(NtPrincipal who, final String request, boolean again, boolean againDueToPendingWork, Integer sleepTime) {
    final var forward = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    forward.beginObject();
    forward.writeObjectFieldIntro("__messages");
    forward.writeNull();
    reverse.beginObject();
    __timeouts.nuke(forward, reverse);
    __dumpMessages(reverse);
    __blocked.set(false);
    __seq.bumpUpPre();
    __entropy.set(Long.toString(__random.nextLong()));
    __futures.commit();
    __queue.clear();
    __cache.clear();
    __reset_future_queues();
    __internalCommit(forward, reverse);
    boolean hasTimeouts = __timeouts.needsInvalidationAndUpdateNext(__next_time);
    __commit(null, forward, reverse);
    forward.endObject();
    reverse.endObject();
    __graph.compute();
    List<LivingDocumentChange.Broadcast> broadcasts = __buildBroadcastListGameMode();
    int timeAgain = __deltaTime();
    if (againDueToPendingWork) {
      timeAgain = Math.max(timeAgain, 10); // 100 web requests/second
    }
    boolean goAgain = __again(hasTimeouts) || again;
    if (sleepTime != null) {
      if (goAgain) {
        timeAgain = Math.min(timeAgain, sleepTime);
      } else {
        goAgain = true;
        timeAgain = sleepTime;
      }
    }
    RemoteDocumentUpdate update = new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), goAgain, timeAgain, 0L, UpdateType.Invalidate);
    return new LivingDocumentChange(update, broadcasts, null, shouldSignalBroadcast(BroadcastPathway.Invalidate, who));
  }

  private LivingDocumentChange __invalidation_queue_transfer(NtPrincipal who, long timestamp, final String request) {
    EnqueuedTask enqueuedTask = __enqueued.transfer();
    AsyncTask asyncLiveTask = new AsyncTask(enqueuedTask.messageId, __seq.get(), enqueuedTask.who, enqueuedTask.viewId, enqueuedTask.channel, timestamp, "adama", "0.0.0.0", __parse_message(enqueuedTask.channel, new JsonStreamReader(enqueuedTask.message.json)));
    __queue.add(asyncLiveTask);

    final var forward = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    forward.beginObject();
    reverse.beginObject();

    {
      forward.writeObjectFieldIntro("__messages");
      forward.beginObject();
      forward.writeObjectFieldIntro(enqueuedTask.messageId);
      asyncLiveTask.dump(forward);
      forward.endObject();
    }

    forward.writeObjectFieldIntro("__enqueued");
    {
      forward.beginObject();
      forward.writeObjectFieldIntro(enqueuedTask.messageId);
      forward.writeNull();
      forward.endObject();
    }

    reverse.writeObjectFieldIntro("__messages");
    {
      reverse.beginObject();
      reverse.writeObjectFieldIntro(enqueuedTask.messageId);
      reverse.writeNull();
      reverse.endObject();
    }

    reverse.writeObjectFieldIntro("__enqueued");
    {
      reverse.beginObject();
      reverse.writeObjectFieldIntro(enqueuedTask.messageId);
      enqueuedTask.writeTo(reverse);
      reverse.endObject();
    }
    __seq.bumpUpPre();
    __commit(null, forward, reverse);
    forward.endObject();
    reverse.endObject();
    RemoteDocumentUpdate update = new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.Invalidate);
    return new LivingDocumentChange(update, null, null, false);
  }

  private LivingDocumentChange __simple_commit(NtPrincipal who, final String request, Object response, long assetBytes) {
    final var forward = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    forward.beginObject();
    reverse.beginObject();
    __commit(null, forward, reverse);
    __internalCommit(forward, reverse);
    boolean hasTimeouts = __timeouts.needsInvalidationAndUpdateNext(__next_time);
    forward.endObject();
    reverse.endObject();
    List<LivingDocumentChange.Broadcast> broadcasts = __buildBroadcastListGameMode();
    RemoteDocumentUpdate update = new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), __again(hasTimeouts), __deltaTime(), assetBytes, UpdateType.AddUserData);
    return new LivingDocumentChange(update, broadcasts, response, shouldSignalBroadcast(BroadcastPathway.Other, who));
  }

  public Integer __computeRequiresInvalidateMilliseconds() {
    if (__state.has()) {
      return (int) (__next_time.get() - __time.get());
    } else {
      return null;
    }
  }

  /** code generated: what happens when the document is constructed */
  protected abstract void __construct_intern(CoreRequestContext context, NtMessageBase message);

  public void __usurp(LivingDocument usurpingDocument) {
    for (Map.Entry<NtPrincipal, ArrayList<PrivateView>> existing : __trackedViews.entrySet()) {
      for (PrivateView pv : existing.getValue()) {
        // create a new view within the usurping document
        PrivateView usurper = usurpingDocument.__createView(existing.getKey(), pv.perspective);
        // the usuper takes over the current view
        JsonStreamWriter priorView = new JsonStreamWriter();
        pv.dumpViewer(priorView);
        usurper.ingestViewUpdate(new JsonStreamReader(priorView.toString()));
        pv.usurp(usurper);
      }
    }
    usurpingDocument.__gets.addAll(__gets);
    __gets.clear();
    usurpingDocument.__code_cost = __code_cost;
  }

  private void register(NtPrincipal __who, PrivateView view) {
    var viewsForWho = __trackedViews.get(__who);
    if (viewsForWho == null) {
      viewsForWho = new ArrayList<>();
      __trackedViews.put(__who, viewsForWho);
    }
    __viewsById.put(view.getViewId(), view);
    viewsForWho.add(view);
  }

  public PrivateView __createView(final NtPrincipal __who, final Perspective perspective) {
    final var view = __createPrivateView(__who, perspective);
    view.setRefresh(() -> {
      __resetGoodWill(true);
      String delta = __makeRefreshJustData(view);
      if (delta != null) {
        view.deliver(delta);
      }
    });
    register(__who, view);
    return view;
  }

  public TrivialPrivateView __createTrivialPrivateView(final NtPrincipal __who, final Perspective perspective) {
    TrivialPrivateView tpv = new TrivialPrivateView(__genViewId(), __who, perspective);
    register(__who, tpv);
    return tpv;
  }

  /** code generated: create a private view for the given person */
  public abstract PrivateView __createPrivateView(NtPrincipal __who, Perspective __perspective);

  private String __makeRefreshJustData(PrivateView pv) {
    JsonStreamWriter data = new JsonStreamWriter();
    pv.update(data);
    String dataStr = data.toString();
    if (!"{}".equals(dataStr)) {
      final var writer = new JsonStreamWriter();
      writer.beginObject();
      writer.writeObjectFieldIntro("data");
      writer.inline(dataStr);
      writer.force_comma_introduction();
      writer.endObject();
      return writer.toString();
    }
    return null;
  }

  public boolean __hasInflightAsyncWork() {
    return __queue.size() > 0 || __webQueue.size() > 0;
  }

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

  /** exposed: gossip view state to the client (if possible) */
  public boolean __mergeViewState(NtDynamic vs) {
    if (__currentViewId >= 0) {
      PrivateView pv = __viewsById.get(__currentViewId);
      if (pv != null) {
        final var writer = new JsonStreamWriter();
        writer.beginObject();
        writer.writeObjectFieldIntro("viewstate");
        writer.writeNtDynamic(vs);
        writer.endObject();
        pv.deliver(writer.toString());
        return true;
      }
    }
    return false;
  }

  public boolean __sendViewState(String viewport, NtDynamic msg) {
    if (__currentViewId >= 0) {
      PrivateView pv = __viewsById.get(__currentViewId);
      if (pv != null) {
        final var writer = new JsonStreamWriter();
        writer.beginObject();
        writer.writeObjectFieldIntro("viewport");
        writer.writeString(viewport);
        writer.writeObjectFieldIntro("message");
        writer.writeNtDynamic(msg);
        writer.endObject();
        pv.deliver(writer.toString());
        return true;
      }
    }
    return false;
  }

  public boolean __logViewState(String log) {
    if (__currentViewId >= 0) {
      PrivateView pv = __viewsById.get(__currentViewId);
      if (pv != null) {
        final var writer = new JsonStreamWriter();
        writer.beginObject();
        writer.writeObjectFieldIntro("log");
        writer.writeString(log);
        writer.endObject();
        pv.deliver(writer.toString());
        return true;
      }
    }
    return false;
  }

  public boolean __gotoViewState(String uri) {
    if (__currentViewId >= 0) {
      PrivateView pv = __viewsById.get(__currentViewId);
      if (pv != null) {
        final var writer = new JsonStreamWriter();
        writer.beginObject();
        writer.writeObjectFieldIntro("goto");
        writer.writeString(uri);
        writer.endObject();
        pv.deliver(writer.toString());
        return true;
      }
    }
    return false;
  }

  private ArrayList<LivingDocumentChange.Broadcast> __buildBroadcastListFor(NtPrincipal who) {
    Runnable perf = __perf.measure("ldf_build_broadcast_list");
    Runnable settle = __perf.measure("ldf_settle");
    try {
      __settle(__viewsById.keySet());
    } finally {
      settle.run();
    }
    // build a broadcast task list
    ArrayList<BroadcastTask> tasks = new ArrayList<>(__trackedViews.size());
    final var itView = __trackedViews.get(who).iterator();
    while (itView.hasNext()) {
      final var pv = itView.next();
      if (pv.isAlive() && pv.hasRead()) {
        tasks.add(new BroadcastTask(who, pv));
      }
    }
    // convert the tasks to broadcasts
    ArrayList<LivingDocumentChange.Broadcast> broadcasts = new ArrayList<>(tasks.size());
    for (BroadcastTask task : tasks) {
      Runnable viewPerf = __perf.measure("ld_broadcast_make");
      broadcasts.add(task.convert());
      viewPerf.run();
    }
    perf.run();
    return broadcasts;
  }

  private ArrayList<LivingDocumentChange.Broadcast> __buildBroadcastListGameMode() {
    Runnable perf = __perf.measure("ld_build_broadcast_list");
    Runnable settle = __perf.measure("ld_settle");
    try {
      __settle(__viewsById.keySet());
    } finally {
      settle.run();
    }
    // build a broadcast task list
    ArrayList<BroadcastTask> tasks = new ArrayList<>(__trackedViews.size());
    final var itTrackedViews = __trackedViews.entrySet().iterator();
    while (itTrackedViews.hasNext()) {
      final var entryTrackedView = itTrackedViews.next();
      final var itView = entryTrackedView.getValue().iterator();
      while (itView.hasNext()) {
        final var pv = itView.next();
        if (pv.isAlive() && pv.hasRead()) {
          tasks.add(new BroadcastTask(entryTrackedView.getKey(), pv));
        }
      }
    }
    // convert the tasks to broadcasts
    ArrayList<LivingDocumentChange.Broadcast> broadcasts = new ArrayList<>(tasks.size());
    for (BroadcastTask task : tasks) {
      Runnable viewPerf = __perf.measure("ld_broadcast_make");
      broadcasts.add(task.convert());
      viewPerf.run();
    }
    perf.run();
    return broadcasts;
  }

  /** internal: we compute per client */
  private ArrayList<LivingDocumentChange.Broadcast> __buildBroadcastListSend(NtPrincipal sender, final LivingDocumentFactory factory) {
    if (factory.appMode) {
      return __buildBroadcastListFor(sender);
    }
    return __buildBroadcastListGameMode();
  }

  private boolean shouldSignalBroadcast(BroadcastPathway pathway, NtPrincipal sender) {
    return false;
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

  protected void __dumpTimeouts(final JsonStreamWriter writer) {
    __timeouts.dump(writer);
  }

  protected void __dumpWebQueue(final JsonStreamWriter writer) {
    __webQueue.dump(writer);
  }

  /** garbage collect the views for the given client; return the number of views for that user */
  public int __garbageCollectViews(final NtPrincipal __who) {
    final var views = __trackedViews.get(__who);
    var count = 0;
    if (views != null) {
      final var it = views.iterator();
      while (it.hasNext()) {
        PrivateView pv = it.next();
        if (pv.isAlive()) {
          count++;
        } else {
          __viewsById.remove(pv.getViewId());
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
    __viewsById.clear();
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

    // don't close the document if the prediction is within five minutes
    Long predict = __predict_cron_wake_time();
    if (predict != null) {
      return predict > 300000;
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

  /** get how much time did the cost run */
  public long __getCpuMilliseconds() {
    return __cpu_ms;
  }

  /** reset the cost */
  public void __zeroOutCodeCost() {
    __code_cost = 0;
    __cpu_ms = 0;
    __resetGoodWill(false);
  }

  private void __resetGoodWill(boolean force) {
    if (!__state.has() || force) {
      __goodwillBudget = __goodwillLimitOfBudget;
    }
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

  protected void __dumpReplicationEngine(final JsonStreamWriter writer) {
    writer.writeObjectFieldIntro("__replication");
    __replication.dump(writer);
  }

  protected void __hydrateReplicationEngine(final JsonStreamReader reader) {
    __replication.load(reader);
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

  /** parse the message for the channel, and cache the result */
  protected abstract Object __parse_message(String channel, JsonStreamReader reader);

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
              String origin = null;
              String ip = null;
              var timestamp = 0L;
              int seq = 0;
              while (reader.notEndOfObject()) {
                final var f = reader.fieldName();
                switch (f) {
                  case "who":
                    who = reader.readNtPrincipal();
                    break;
                  case "seq":
                    seq = reader.readInteger();
                    break;
                  case "channel":
                    channel = reader.readString();
                    break;
                  case "timestamp":
                    timestamp = reader.readLong();
                    break;
                  case "origin":
                    origin = reader.readString();
                    break;
                  case "ip":
                    ip = reader.readString();
                    break;
                  case "message":
                    message = __parse_message(channel, reader);
                    break;
                  default:
                    reader.skipValue();
                }
              }
              final var task = new AsyncTask(msgId, seq, who, null, channel, timestamp, origin, ip, message);
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

  protected void __hydrateEnqueuedTaskManager(final JsonStreamReader reader) {
    __enqueued.hydrate(reader);
  }

  protected void __dumpEnqueuedTaskManager(JsonStreamWriter writer) {
    __enqueued.dump(writer);
  }

  public void __hydrateTimeouts(final JsonStreamReader reader) {
    __timeouts.hydrate(reader);
  }

  protected void __hydrateWebQueue(final JsonStreamReader reader) {
    __webQueue.hydrate(reader, this);
  }

  /** does the channel have code associated to it */
  protected abstract boolean __is_direct_channel(String channel);

  protected abstract void __handle_direct(CoreRequestContext context, String channel, Object message) throws AbortMessageException;

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

  /** code generated: state machine labels can be dynamically invoked */
  protected abstract void __invoke_label(String __new_state);

  /** is the channel open */
  public abstract boolean __open_channel(String name);

  @Deprecated
  public String __authorize(CoreRequestContext __context, String username, String password) {
    __time.set(System.currentTimeMillis());
    String result = __auth(__context, username, password);
    __revert();
    return result;
  }

  /** authenticate a user; return null to indicate forbidden, return an agent to sign for the document */
  @Deprecated
  public abstract String __auth(CoreRequestContext context, String username, String password);

  public AuthResponse __authorization(CoreRequestContext __context, String message) {
    __time.set(System.currentTimeMillis());
    try {
      return __authpipe(__context, message);
    } finally {
      __revert();
    }
  }

  public abstract AuthResponse __authpipe(CoreRequestContext __context, String __messsage);

  public abstract void __make_cron_progress();

  public void __execute_reset_cron() {
    __optimisticNextCronCheck = 0L;
    __reset_cron();
  }

  public abstract Long __predict_cron_wake_time();

  protected abstract void __reset_cron();

  public abstract String __traffic(CoreRequestContext __context);

  @Deprecated
  public abstract void __password(CoreRequestContext context, String password);

  private void __drive_webget_queue() {
    Iterator<EphemeralWebGet> it = __gets.iterator();
    while (it.hasNext()) {
      if (__execute_web_get(it.next())) {
        it.remove();
      }
    }
  }

  public void __nukeWebGetQueue() {
    Iterator<EphemeralWebGet> it = __gets.iterator();
    while (it.hasNext()) {
      it.next().callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_WEB_GET_CANCEL));
      it.remove();
    }
  }

  public void __web_get(WebGet get, Callback<WebResponse> callback) {
    DelayParent delay = new DelayParent();
    RxCache cache = new RxCache(this, delay);
    EphemeralWebGet eget = new EphemeralWebGet(cache, get, callback, delay);
    if (!__execute_web_get(eget)) {
      __gets.add(eget);
    }
  }

  private boolean __execute_web_get(EphemeralWebGet get) {
    try {
      __currentWebCache = get.cache;
      try {
        get.callback.success(__get_internal(get.get.context.toCoreRequestContext(new Key(__space, __key)), get.get));
      } catch (AbortMessageException ame) {
        get.callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_WEB_GET_ABORT));
      }
      return true;
    } catch (ComputeBlockedException cbe) {
      return false;
    } catch (Throwable error) {
      get.callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.DOCUMENT_WEB_GET_EXCEPTION, error, EXLOGGER));
      return true;
    }
  }

  /** code generated: respond to a get request */
  protected abstract WebResponse __get_internal(CoreRequestContext __context, WebGet __get) throws AbortMessageException;

  /** code generated: respond to a get request */
  public abstract WebResponse __options(CoreRequestContext __context, WebGet __get);

  /** code generated: respond to a put request */
  protected abstract WebResponse __put_internal(CoreRequestContext __context, WebPut __put) throws AbortMessageException;

  /** code generated: respond to a delete request */
  protected abstract WebResponse __delete_internal(CoreRequestContext __context, WebDelete __delete) throws AbortMessageException;

  public boolean __isConnected(final NtPrincipal __who) {
    return __clients.containsKey(__who);
  }

  /** code generated: allow the document to accept/reject the client */
  public abstract boolean __onConnected(CoreRequestContext clientValue);

  /** code generated: allow the document to be deleted via a standard message */
  public abstract boolean __delete(CoreRequestContext clientValue);

  /** ran with loaded */
  public abstract void __onLoad();

  /** code generated: let the document know of a disconnected client */
  public abstract void __onDisconnected(CoreRequestContext clientValue);

  /** code generate: let the document know an asset was uploaded */
  public abstract void __onAssetAttached(CoreRequestContext __cvalue, NtAsset __asset);

  /** code generate: can the client even attach any data */
  public abstract boolean __onCanAssetAttached(CoreRequestContext __cvalue);

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

  @Override
  public void __cost(int cost) {
    __code_cost += cost;
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

  protected void __applyPatch(NtDynamic patch) {
    __patch(new JsonStreamReader(patch.json));
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
    try {
      __test(report, testName);
    } catch (AbortMessageException aborted) {
      report.aborted();
    }
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    __commit(null, writer, new JsonStreamWriter());
    writer.endObject();
    return writer.toString();
  }

  /** code generated: run the test for the given test name */
  public abstract void __test(TestReportBuilder report, String testName) throws AbortMessageException;

  /** code generated: commit the tree, and push data into the given delta */
  public abstract void __commit(String name, JsonStreamWriter forward, JsonStreamWriter reverse);

  private void __proxy_commit(String name, JsonStreamWriter forward, JsonStreamWriter reverse) {
    __seq.bumpUpPre();
    __commit(name, forward, reverse);
    __timeouts.commit(forward, reverse);
    __replication.commit(forward, reverse);
    __enqueued.commit(forward, reverse);
    __graph.compute();
  }

  /** estimate the memory of the document */
  public long __memory() {
    long memory = 384;
    for (String dedupeKey : __dedupe.keySet()) {
      memory += dedupeKey.length() * 2L + 16;
    }
    for (Map.Entry<NtPrincipal, ArrayList<PrivateView>> entry : __trackedViews.entrySet()) {
      memory += entry.getKey().memory();
      for (PrivateView view : entry.getValue()) {
        memory += view.memory();
      }
    }
    memory += __graph.memory();
    return memory;
  }

  protected void __test_send(final String channel, NtPrincipal __who, final Object message) throws AbortMessageException {
    AsyncTask task = new AsyncTask(__message_id.bumpUpPre(), __seq.get(), __who, 0, channel, __time.get(), "origin", "127.0.0.1", message);
    __queue.add(task);
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
        if (task.viewId != null) {
          __currentViewId = task.viewId;
        } else {
          __currentViewId = -1;
        }
        task.execute();
        __currentViewId = -1;
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

  protected void __forward(double time) {
    __time.set(__time.get() + (long) (time * 1000));
    __commit(null, new JsonStreamWriter(), new JsonStreamWriter());
  }

  /** exposed: for code coverage */
  protected void __track(final int idx) {
    __trace.add(idx);
  }

  /** transaction: core API (New Version in Draft) */
  public LivingDocumentChange __transact(final String requestJson, LivingDocumentFactory factory) throws ErrorCodeException {
    Runnable perf = __perf.measure("tx");
    long started = System.currentTimeMillis();
    try {
      Runnable perfParse = __perf.measure("parse");
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
      int viewId = -1;
      String password = null;
      Integer delivery_id = null;
      RemoteResult result = null;
      NtAsset asset = null;
      String origin = null;
      String ip = null;
      String key = null;
      WebPut put = null;
      WebDelete delete = null;
      try {
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
              case "view-id":
                viewId = reader.readInteger();
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
              case "password":
                password = reader.readString();
                break;
              case "patch":
                patch = reader.skipValueIntoJson();
                break;
              case "put":
                put = (WebPut) WebPutPartial.read(reader).convert(new WebContext(who, origin, ip));
                break;
              case "delete":
                delete = (WebDelete) WebDeletePartial.read(reader).convert(new WebContext(who, origin, ip));
                break;
              case "delivery_id":
                delivery_id = reader.readInteger();
                break;
              case "result":
                result = new RemoteResult(reader);
                break;
              case "message":
                try {
                  message = __parse_message(channel, reader);
                } catch (Exception ex) {
                  throw ErrorCodeException.detectOrWrap(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_FAILED_PARSE_MESSAGE, ex, EXLOGGER);
                }
                break;
              case "asset":
                asset = reader.readNtAsset();
                break;
              case "arg": // for constructor
                arg = __parse_construct_arg(reader);
                break;
              case "reason":
                // don't use
                reader.readString();
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
      } finally {
        perfParse.run();
      }
      if ("load".equals(command)) {
        Runnable perfLoad = __perf.measure("load");
        try {
          return __transaction_load(requestJson, timestamp);
        } finally {
          perfLoad.run();
        }
      }
      CoreRequestContext context = null;
      Runnable perfSetTime = __perf.measure("st");
      try {
        if (who != null && key != null && origin != null && ip != null) {
          context = new CoreRequestContext(who, origin, ip, key);
        }
        if (Math.abs(timestamp - __time.get()) > factory.temporalResolutionMilliseconds) {
          __time.set(timestamp);
        }
      } finally {
        perfSetTime.run();
      }
      Runnable perfRun = __perf.measure("run");
      try {
        switch (command) {
          case "invalidate":
            if (__monitor != null) {
              return __transaction_invalidate_monitored(who, requestJson);
            } else {
              return __transaction_invalidate_body(who, requestJson);
            }
          case "construct":
            if (__constructed.get()) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_ALREADY_CONSTRUCTED);
            }
            if (arg == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_CONSTRUCTOR_ARG);
            }
            if (context == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CONTEXT);
            }
            return __transaction_construct(requestJson, context, arg, entropy);
          case "delete":
            if (context == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CONTEXT);
            }
            return __transaction_delete(requestJson, context);
          case "connect":
            if (context == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CONTEXT);
            }
            return __transaction_connect(requestJson, context);
          case "disconnect":
            if (context == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CONTEXT);
            }
            return __transaction_disconnect(requestJson, context);
          case "attach":
            if (context == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CONTEXT);
            }
            if (asset == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_ASSET);
            }
            return __transaction_attach(requestJson, context, asset);
          case "send":
            if (channel == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CHANNEL);
            }
            if (message == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_MESSAGE);
            }
            if (context == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CONTEXT);
            }
            return __transaction_send(context, requestJson, viewId, marker, channel, timestamp, message, factory);
          case "password":
            if (context == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SET_PASSWORD_NO_CONTEXT);
            }
            if (password == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SET_PASSWORD_NO_PASSWORD);
            }
            return _transact_password(context, password);
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
            return __transaction_web_put(requestJson, put);
          case "web_delete":
            if (who == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO);
            }
            if (delete == null) {
              throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_DELETE);
            }
            return __transaction_web_delete(requestJson, delete);
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
      } finally {
        perfRun.run();
      }
    } catch (GoodwillExhaustedException gee) {
      throw new ErrorCodeException(ErrorCodes.API_GOODWILL_EXCEPTION, gee);
    } finally {
      perf.run();
      __cpu_ms += (System.currentTimeMillis() - started);
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
    return new LivingDocumentChange(update, null, null, false);
  }

  public boolean __forceDeliverForTest(int deliveryId, RemoteResult result) {
    RxCache route = __routing.get(deliveryId);
    if (route == null) {
      return false;
    }
    return route.deliver(deliveryId, result);
  }

  private LivingDocumentChange __transaction_deliver(final String request, NtPrincipal who, int deliveryId, RemoteResult result) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    boolean exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionDeliver");
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
      exception = false;
      __commit(null, forward, reverse);
      __internalCommit(forward, reverse);
      forward.endObject();
      reverse.endObject();
      RemoteDocumentUpdate update = new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.AddUserData);
      return new LivingDocumentChange(update, null, null, false);
    } finally {
      if (exception) {
        __revert();
      }
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  private LivingDocumentChange _transact_password(CoreRequestContext context, String password) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    boolean exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionPassword");
    }
    try {
      __seq.bumpUpPre();
      __randomizeOutOfBand();
      __password(context, password);
      exception = false;
      return __simple_commit(context.who, "{\"password\":\"private\"}", null, 0L);
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
      __time.set(System.currentTimeMillis());
      __randomizeOutOfBand();
      DelayParent delay = new DelayParent();
      RxCache cache = new RxCache(this, delay);
      try {
        __currentWebCache = cache;
        WebResponse response = __put_internal(put.context.toCoreRequestContext(new Key(__space, __key)), put);
        exception = false;
        return __simple_commit(put.context.who, request, response, 0L);
      } catch (ComputeBlockedException cbe) {
        __revert();
        exception = false;
        EphemeralFuture<WebResponse> future = new EphemeralFuture<>();
        __seq.bumpUpPre();
        __webQueue.queue(put.context, put, future, cache, delay);
        return __simple_commit(put.context.who, request, future, 0L);
      } catch (AbortMessageException ame) {
        __revert();
        exception = false;
        throw new ErrorCodeException(ErrorCodes.DOCUMENT_WEB_PUT_ABORT);
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

  private LivingDocumentChange __transaction_web_delete(final String request, final WebDelete del) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    boolean exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionWebDelete");
    }
    try {
      __seq.bumpUpPre();
      __time.set(System.currentTimeMillis());
      __randomizeOutOfBand();
      DelayParent delay = new DelayParent();
      RxCache cache = new RxCache(this, delay);
      try {
        __currentWebCache = cache;
        WebResponse response = __delete_internal(del.context.toCoreRequestContext(new Key(__space, __key)), del);
        exception = false;
        return __simple_commit(del.context.who, request, response, 0L);
      } catch (ComputeBlockedException cbe) {
        __revert();
        exception = false;
        EphemeralFuture<WebResponse> future = new EphemeralFuture<>();
        __seq.bumpUpPre();
        __webQueue.queue(del.context, del, future, cache, delay);
        return __simple_commit(del.context.who, request, future, 0L);
      } catch (AbortMessageException ame) {
        __revert();
        exception = false;
        throw new ErrorCodeException(ErrorCodes.DOCUMENT_WEB_DELETE_ABORT);
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
  private LivingDocumentChange __transaction_attach(final String request, final CoreRequestContext context, final NtAsset asset) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionAttach");
    }
    try {
      if (!__clients.containsKey(context.who)) {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_ATTACH_NOT_CONNECTED);
      }
      // execute the attachment
      __onAssetAttached(context, asset);
      __seq.bumpUpPre();

      exception = false;
      return __simple_commit(context.who, request, null, asset.size);
    } finally {
      if (exception) {
        __revert();
      }
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  /** write an rx report */
  public abstract void __writeRxReport(JsonStreamWriter __writer);

  /** transaction: a person connects to document */
  private LivingDocumentChange __transaction_connect(final String request, final CoreRequestContext context) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionConnect");
    }
    try {
      if (__clients.containsKey(context.who)) {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_ALREADY_CONNECTED);
      }
      __randomizeOutOfBand();
      if (__onConnected(context)) {
        // user was accepted, so let's commit it
        // generate a connection id for the user
        final var cId = __connection_id.bumpUpPost();
        // associate the client to the connection id
        __clients.put(context.who, cId);
        // commit the tree to the delta
        final var forward = new JsonStreamWriter();
        final var reverse = new JsonStreamWriter();
        forward.beginObject();
        reverse.beginObject();
        __proxy_commit(null, forward, reverse);
        forward.writeObjectFieldIntro("__clients");
        forward.beginObject();
        forward.writeObjectFieldIntro(cId);
        forward.writeNtPrincipal(context.who);
        forward.endObject();
        forward.endObject();
        reverse.writeObjectFieldIntro("__clients");
        reverse.beginObject();
        reverse.writeObjectFieldIntro(cId);
        reverse.writeNull();
        reverse.endObject();
        reverse.endObject();
        final var result = new RemoteDocumentUpdate(__seq.get(), __seq.get(), context.who, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.AddUserData);
        exception = false;
        return new LivingDocumentChange(result, null, null, false);
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

  public abstract String __getViewStateFilter();

  /** transaction: a person connects to document */
  private LivingDocumentChange __transaction_delete(final String request, final CoreRequestContext context) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionDelete");
    }
    try {
      if (context.who.authority.equals("overlord") || __delete(context)) {
        throw new PerformDocumentDeleteException();
      } else {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_DELETE_REJECTED);
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
  private LivingDocumentChange __transaction_construct(final String request, final CoreRequestContext context, final NtMessageBase arg, final String entropy) throws ErrorCodeException {
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
      __construct_intern(context, arg);
      __constructed.set(true);
      final var forward = new JsonStreamWriter();
      final var reverse = new JsonStreamWriter();
      forward.beginObject();
      reverse.beginObject();
      __commit(null, forward, reverse);
      __replication.commit(forward, reverse);
      __graph.compute();
      forward.endObject();
      reverse.endObject();
      final var result = new RemoteDocumentUpdate(__seq.get(), __seq.get(), context.who, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.AddUserData);
      exception = false;
      return new LivingDocumentChange(result, null, null, false);
    } finally {
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  /** transaction: a person disconnects from the document */
  private LivingDocumentChange __transaction_disconnect(final String request, final CoreRequestContext context) throws ErrorCodeException {
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionDisconnect");
    }
    try {
      __randomizeOutOfBand();
      if (!__clients.containsKey(context.who)) {
        throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_DISCONNECT_DUE_TO_NOT_CONNECTED);
      }
      // disconnect them
      __onDisconnected(context);
      // stop tracking them
      final int id = __clients.remove(context.who);
      __seq.bumpUpPre();
      final var forward = new JsonStreamWriter();
      final var reverse = new JsonStreamWriter();
      forward.beginObject();
      reverse.beginObject();
      __commit(null, forward, reverse);
      __replication.commit(forward, reverse);
      __graph.compute();
      forward.writeObjectFieldIntro("__clients");
      forward.beginObject();
      forward.writeObjectFieldIntro(id);
      forward.writeNull();
      forward.endObject();
      forward.endObject();
      reverse.writeObjectFieldIntro("__clients");
      reverse.beginObject();
      reverse.writeObjectFieldIntro(id);
      reverse.writeNtPrincipal(context.who);
      reverse.endObject();
      reverse.endObject();
      final var result = new RemoteDocumentUpdate(__seq.get(), __seq.get(), context.who, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.AddUserData);
      exception = false;
      return new LivingDocumentChange(result, null, null, false);
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
    return __simple_commit(who, request, null, 0L);
  }

  private LivingDocumentChange __transaction_invalidate_cron(NtPrincipal who, final String request, long timestamp, boolean didWork, boolean againDueToDirtyWebQueue) {
    boolean againAgain = false;
    Integer sleepTime = null;
    boolean againDueToPendingWork = againDueToDirtyWebQueue || __enqueued.size() > 0;
    if (!againDueToDirtyWebQueue) {
      if (__optimisticNextCronCheck <= __time.get()) {
        // We set the cron check to MAX VALUE to find the minimum time of the next event
        __optimisticNextCronCheck = Long.MAX_VALUE;
        __make_cron_progress();
        if (__optimisticNextCronCheck < Long.MAX_VALUE) { // there are no next events if the optimistic next time is max long, and this will disable the function call in future steps
          sleepTime = (int) Math.min(Math.max(10000, (__optimisticNextCronCheck - __time.get())), 12 * 60 * 60 * 1000);
          againAgain = true;
        }
      }
      if (!againAgain && !didWork && __enqueued.readyForTransfer()) {
        return __invalidation_queue_transfer(who, timestamp, request);
      }
    }
    return __invalidate_trailer(who, request, againDueToPendingWork || againAgain, againDueToPendingWork, sleepTime);
  }

  /** transaction: an invalidation is happening on the document (no monitor) */
  private LivingDocumentChange __transaction_invalidate_body(NtPrincipal who, final String request) {
    __preemptedStateOnNextComputeBlocked = null;
    final var seedUsed = Long.parseLong(__entropy.get());
    final long timeBackup = __time.get();
    try {
      __random = new Random(seedUsed);
      boolean workDone = false;
      // channel messages that have blocked the system
      for (final AsyncTask task : __queue) {
        __route(task);
      }
      Runnable taskPerf = __perf.measure("tasks");
      try {
        for (final AsyncTask task : __queue) {
          __time.set(task.timestamp);
          __seq_message = task.docSeq;
          if (task.viewId != null) {
            __currentViewId = task.viewId;
          } else {
            __currentViewId = -1;
          }
          try {
            task.execute();
          } finally {
            __currentViewId = -1;
            __seq_message = null;
          }
          workDone = true;
        }
      } finally {
        taskPerf.run();
      }
      __time.set(timeBackup);
      // execute the state
      if (__state.has() && __next_time.get() <= __time.get()) {
        final var stateToExecute = __state.get();
        __state.set("");
        __invoke_label(stateToExecute);
        workDone = true;
      }
      int dirtyLeft = 0;
      if (!workDone) {
        dirtyLeft = __webQueue.size();
        Iterator<Map.Entry<Integer, WebQueueItem>> it = __webQueue.iterator();
        while (it.hasNext()) {
          dirtyLeft--;
          workDone = true;
          WebQueueItem item = it.next().getValue();
          try {
            __random = new Random(seedUsed);
            if (item.item instanceof WebPut) {
              __currentWebCache = item.cache;
              try {
                WebResponse response = __put_internal(item.context.toCoreRequestContext(new Key(__space, __key)), (WebPut) item.item);
                if (item.future != null) {
                  item.future.send(response);
                }
              } catch (AbortMessageException ame) {
                if (item.future != null) {
                  item.future.abort(ErrorCodes.DOCUMENT_WEB_PUT_ABORT);
                }
              }
            } else if (item.item instanceof WebDelete) {
              __currentWebCache = item.cache;
              try {
                WebResponse response = __delete_internal(item.context.toCoreRequestContext(new Key(__space, __key)), (WebDelete) item.item);
                if (item.future != null) {
                  item.future.send(response);
                }
              } catch (AbortMessageException ame) {
                if (item.future != null) {
                  item.future.abort(ErrorCodes.DOCUMENT_WEB_DELETE_ABORT);
                }
              }
            }
            item.state = WebQueueState.Remove;
            __webQueue.dirty();
            __drive_webget_queue();
            return __transaction_invalidate_cron(who, request, timeBackup, true, dirtyLeft > 0);
          } catch (ComputeBlockedException cbe) {
            __revert();
            __time.set(timeBackup);
            // the web request got blocked, so we let the future delivery invalidate the system so
            // we are not polling until the message arrives. We also signal that work was done (because it was to drive the queue)
            // so we don't perform any other actions. This is why workDone = true
          }
        }
      }
      __drive_webget_queue();
      return __transaction_invalidate_cron(who, request, timeBackup, workDone, false);
    } catch (final ComputeBlockedException blockedOn) {
      if (__preemptedStateOnNextComputeBlocked != null) {
        __state.set(__preemptedStateOnNextComputeBlocked);
        __next_time.set(__time.get());
        __preemptedStateOnNextComputeBlocked = null;
        return __transaction_invalidate_cron(who, request, timeBackup, true, false);
      } else {
        List<LivingDocumentChange.Broadcast> broadcasts = __buildBroadcastListGameMode();
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
        return new LivingDocumentChange(new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), false, 0, 0L, UpdateType.Internal), broadcasts, null, shouldSignalBroadcast(BroadcastPathway.Blocked, who));
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
      return new LivingDocumentChange(new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.Internal), null, null, false);
    } finally {
      __currentViewId = -1;
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
      return new LivingDocumentChange(result, null, null, false);
    } finally {
      if (__monitor != null) {
        __monitor.pop(System.nanoTime() - startedTime, exception);
      }
    }
  }

  private LivingDocumentChange __transaction_send_commit(NtPrincipal sender, final String request, final String dedupeKey, final NtPrincipal who, final String marker, final String channel, final long timestamp, final Object message, final LivingDocumentFactory factory) throws ErrorCodeException {
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
    boolean hasTimeouts = __timeouts.needsInvalidationAndUpdateNext(__next_time);
    __randomizeOutOfBand();
    __proxy_commit(null, forward, reverse);
    forward.endObject();
    reverse.endObject();
    int delay = __deltaTime();
    if (delay <= 0 && factory.appMode) {
      delay = factory.appDelay;
    }
    List<LivingDocumentChange.Broadcast> broadcasts = __buildBroadcastListSend(sender, factory);
    RemoteDocumentUpdate update = new RemoteDocumentUpdate(__seq.get(), __seq.get(), who, request, forward.toString(), reverse.toString(), __again(hasTimeouts) || factory.appMode, delay, 0L, UpdateType.DirectMessageExecute);
    return new LivingDocumentChange(update, broadcasts, null, shouldSignalBroadcast(BroadcastPathway.Send, who));
  }

  private LivingDocumentChange __transaction_send_enqueue(final String request, final int viewId, final String dedupeKey, final CoreRequestContext context, final String marker, final String channel, final long timestamp, final Object message, final LivingDocumentFactory factory) throws ErrorCodeException {
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
    final var task = new AsyncTask(msgId, __seq.get(), context.who, viewId, channel, timestamp, context.origin, context.ip, message);
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
    __proxy_commit(null, forward, reverse);
    forward.endObject();
    reverse.endObject();
    final var result = new RemoteDocumentUpdate(__seq.get(), __seq.get(), context.who, request, forward.toString(), reverse.toString(), true, 0, 0L, UpdateType.AddUserData);
    return new LivingDocumentChange(result, null, null, false);
  }

  /** transaction: a person is sending the document a message */
  private LivingDocumentChange __transaction_send(CoreRequestContext context, final String request, final int viewId, final String marker, final String channel, final long timestamp, final Object message, final LivingDocumentFactory factory) throws ErrorCodeException {
    __resetGoodWill(true);
    final var startedTime = System.nanoTime();
    var exception = true;
    if (__monitor != null) {
      __monitor.push("TransactionSend");
    }
    try {
      String dedupeKey = context.who.agent + "/" + context.who.authority + "/" + marker;
      Runnable perfValidate = __perf.measure("vd_" + channel);
      try {
        // they must be connected OR document allows blind/direct sends OR be overlord
        if (!__open_channel(channel) && !__clients.containsKey(context.who) && !factory.canSendWhileDisconnected(context)) {
          throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NOT_CONNECTED);
        }
        if (marker != null) {
          if (__dedupe.containsKey(dedupeKey)) {
            throw new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_MESSAGE_ALREADY_SENT);
          }
          __dedupe.put(dedupeKey, __time.get());
        }
        __currentViewId = viewId;
      } finally {
        perfValidate.run();
      }
      LivingDocumentChange change;
      if (__is_direct_channel(channel)) {
        Runnable perf = __perf.measure("sd_" + channel);
        try {
          Runnable pd = __perf.measure("pd_" + channel);
          try {
            if (message instanceof NtMessageBase) {
              ((NtMessageBase) message).__parsed();
            } else if (message instanceof NtMessageBase[]) {
              NtMessageBase[] arr = (NtMessageBase[]) message;
              for (int k = 0; k < arr.length; k++) {
                arr[k].__parsed();
              }
            }
          } finally {
            pd.run();
          }
          __random = new Random(Long.parseLong(__entropy.get()) + timestamp);
          Runnable perfExec = __perf.measure("ex_" + channel);
          try {
            __handle_direct(context, channel, message);
          } finally {
            perfExec.run();
          }
          Runnable perfCommit = __perf.measure("cmt_" + channel);
          try {
            change = __transaction_send_commit(context.who, request, dedupeKey, context.who, marker, channel, timestamp, message, factory);
          } finally {
            perfCommit.run();
          }
        } catch (AbortMessageException ame) {
          throw new ErrorCodeException(ame.policyFailure != null ? ErrorCodes.LIVING_DOCUMENT_TRANSACTION_MESSAGE_DIRECT_ABORT_POLICY : ErrorCodes.LIVING_DOCUMENT_TRANSACTION_MESSAGE_DIRECT_ABORT);
        } catch (ComputeBlockedException cbe) {
          __revert();
          change = __transaction_send_enqueue(request, viewId, dedupeKey, context, marker, channel, timestamp, message, factory);
        } finally {
          perf.run();
        }
      } else {
        Runnable perf = __perf.measure("qu_" + channel);
        change = __transaction_send_enqueue(request, viewId, dedupeKey, context, marker, channel, timestamp, message, factory);
        perf.run();
      }
      exception = false;
      return change;
    } finally {
      __currentViewId = -1;
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
    __next_time.set((long) (__time.get() + Math.max(0, timeToTransitionSeconds * 1000.0)));
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

  @Override
  public void __invalidateUp() { // no-op
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

  private class BroadcastTask {
    public NtPrincipal who;
    private final PrivateView pv;

    public BroadcastTask(NtPrincipal who, PrivateView pv) {
      this.who = who;
      this.pv = pv;
    }

    public LivingDocumentChange.Broadcast convert() {
      return __buildBroadcast(who, pv);
    }
  }
}

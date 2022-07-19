import WebSocket from 'isomorphic-ws';

export const Production = "aws-us-east-2.adama-platform.com";

export class Tree {
  tree: object;
  dispatch: object;
  dispatch_count: number;
  queue: Array<any>;
  ondecide: (outstanding: any) => void;

  constructor() {
    this.tree = {};
    this.dispatch = {};
    this.dispatch_count = 0;
    this.queue = [];
    this.ondecide = function (outstanding: any) { };
  }

  // convert the tree to a delta
  toDelta(): any {
    return this.__toDelta(this.tree);
  }

  __toDelta(st: any): any {
    var delta: any = {};
    for (var k in st) {
      if (k[0] == '#' || k == "@o" || k == "__key") continue;
      var v = st[k];
      if ('#' + k in st) {
        var d:any = {};
        if ('@o' in st['#' + k]) {
          var o = [];
          for (var j = 0; j < v.length; j++) {
            d[v[j].id] = this.__toDelta(v[j]);
            o.push(v[j].id);
          }
          d['@o'] = o;
        } else {
          for (var j = 0; j < v.length; j++) {
            d[j] = this.__toDelta(v[j]);
          }
          d['@s'] = v.length;
        }
        delta[k] = d;
      } else if (typeof(v) == 'object') {
        delta[k] = this.__toDelta(v);
      } else {
        delta[k] = v;
      }
    }
    return delta;
  }


  // recursively append a change
  // dispatch is the structural object mirroring the tree
  // callback is the function/object callback tree
  // insert_order is the order to fire events
  __recAppendChange(dispatch: any, callback: any, insert_order: number, auto_delete: boolean, holder: any) {
    if (Array.isArray(callback)) { // callback is an array (expand)
      for (var k = 0; k < callback.length; k++) {
        this.__recAppendChange(dispatch, callback[k], insert_order, auto_delete, holder);
      }
    } else if (typeof (callback) == 'object') { // the callback is an object (recurse)
      // we for each item in the callback
      for (var key in callback) {
        // recurse into that key
        if (key == '@e') {
          this.__recAppendChange(dispatch, callback[key], insert_order, auto_delete, holder);
        } else {
          // make sure it exists
          if (!(key in dispatch)) {
            dispatch[key] = {};
          }
          this.__recAppendChange(dispatch[key], callback[key], insert_order, auto_delete, holder);
        }
      }
    } else if (typeof (callback) == 'function') { // callback is a function (append)
      // we have a function, so let's associate it to the node
      if (!('@e' in dispatch)) {
        dispatch['@e'] = [];
      }
      dispatch['@e'].push({ cb: callback, order: insert_order, auto_delete: auto_delete, holder: holder});
    }
  }

  /** delete events that have auto_delete == true */
  __autoDelete(dispatch: any) {
    if ('@e' in dispatch) {
      var list = dispatch['@e'];
      var next = [];
      for (var k = 0; k < list.length; k++) {
        if (!list[k].auto_delete) {
          next.push(list[k]);
        }
      }
      dispatch['@e'] = next;
    }
  }

  filterHolders(fn: (x: any) => boolean) {
    this.__filterHolders(this.dispatch, fn);
  }

  __filterHolders(dispatch: any, fn: (x: any) => boolean) {
    if (Array.isArray(dispatch)) {
      for (var k = 0; k < dispatch.length; k++) {
        this.__filterHolders(dispatch[k], fn);
      }
    } else if (typeof(dispatch) == 'object') {
      for (var key in dispatch) {
        if (key == '@e') {
          var evts: any = dispatch['@e'];
          var nulls = 0;
          for (var k = 0; k < evts.length; k++) {
            var e = evts[k];
            if (!fn(e.holder)) {
              evts[k] = null;
              nulls++;
            }
          }
          if (nulls > 0) {
            this.__collapse(dispatch);
          }
        } else {
          this.__filterHolders(dispatch[key], fn);
        }
      }
    }
  }

  /** bind an event handler to the tree */
  onTreeChange(callback: any, holder:any) {
    this.__recAppendChange(this.dispatch, callback, this.dispatch_count, false, holder);
    this.dispatch_count++;
  }

  /** supply a delta from Adama; manages data and decisions */
  mergeUpdate(diff: any) {
    if ('data' in diff) {
      // we merge the tree with the data within dispatch
      this.__recMergeAndDispatch(this.tree, this.dispatch, diff.data)
    }
    if ('outstanding' in diff) {
      this.ondecide(diff.outstanding);
    }
    this.__drain();
  }

  /** phase 1.5: recursively delete the tree and fire off events */
  __recDeleteAndDispatch(tree: any, dispatch: any) {
    for (var key in tree) {
      let old = tree[key];
      if (Array.isArray(old)) {
        let elementKey = "#" + key;
        if (elementKey in tree && elementKey in dispatch) {
          this.__recDeleteAndDispatch(tree[elementKey], dispatch[elementKey]);
        }
      } else if (key in dispatch) {
        this.__recDeleteAndDispatch(old, dispatch[key]);
      }

      let deleteChildKey = '-' + key;
      if (dispatch != null && deleteChildKey in dispatch) {
        this.__fire(dispatch[deleteChildKey], {key: key, before: old, value: null});
      }
    }
  }

  /** phase 1: */
  __recMergeAndDispatch(tree: any, dispatch: any, diff: any) {
    // the diff is an object, so let's walk its keys
    for (var key in diff) {
      var child = diff[key];
      if (child === null) {
        let old = tree[key];
        // event: the value is being nuked (maybe value or privacy change)
        let deleteChildKey = '-' + key;
        if (dispatch != null && deleteChildKey in dispatch) {
          this.__fire(dispatch[deleteChildKey], {key: key, before: old, value: null});
        }
        // if the prior value is an array
        if (Array.isArray(old)) {
          // then we recursively delete the children
          let elementKey = "#" + key;
          if (elementKey in tree && elementKey in dispatch) {
            this.__recDeleteAndDispatch(tree[elementKey], dispatch[elementKey]);
          }
          delete tree["#" + key];
        } else if (key in dispatch) {
          this.__recDeleteAndDispatch(old, dispatch[key]);
        }
        delete tree[key];
        // fire off an event for the deletion of the thing
        if (dispatch != null && key in dispatch) {
          this.__fire(dispatch[key], { key: key, before: old, value: null});
        }
        continue;
      }
      // if the child is an object, then..
      var fireNew = !(key in tree);
      if (typeof (child) == 'object') {
        // the child is either an ARRAY or a OBJECT
        var childIsArray = '@o' in child || '@s' in child;
        // the prior version doesn't exist, so we create the empty node so that it does exist
        if (childIsArray) {
          if (!(key in tree)) {
            tree[key] = [];
          }
          if (!(('#' + key) in tree)) {
            tree["#" + key] = {};
            if ('@o' in child) {
              tree["#" + key]['@o'] = true;
            }
          }
        } else {
          if (!(key in tree)) {
            tree[key] = {};
          }
        }
        // now, we check to see if the prior state influences whether or not the diff is an array
        childIsArray = Array.isArray(tree[key]) || childIsArray;
        if (childIsArray) {
          // recurse into the array form
          this.__recMergeAndDispatchArray(tree[key], (dispatch != null && key in dispatch) ? dispatch[key] : null, tree["#" + key], child);
        } else {
          // simply recurse
          this.__recMergeAndDispatch(tree[key], (dispatch != null && key in dispatch) ? dispatch[key] : null, child);
        }
      } else {
        var old = (key in tree) ? tree[key] : null;
        tree[key] = child;
        if (dispatch != null && key in dispatch) {
          this.__fire(dispatch[key], { key: key, before: old, value: child});
        }
      }
      if (fireNew) { // fire new data event
        let newChildKey = '+' + key;
        if (dispatch != null && newChildKey in dispatch) {
          this.__fire(dispatch[newChildKey], {key: key, value:tree[key]});
        }
      }

      // fire that changes happened on this entire branch
      if (dispatch != null && '@e' in dispatch) {
        this.__fire(dispatch, {value: tree});
      }
    }
  }
  /** phase 2: array merging */
  __recMergeAndDispatchArray(prior: any, dispatch: any, tree: any, diff: any) {
    var ordering = null;
    var resize = null;
    for (var key in diff) {
      if (key == "@o") {
        // detect an ordering change
        ordering = diff[key];
      } else if (key == "@s") {
        // detect a resize change
        resize = diff[key];
      } else {
        // compare objects
        if (diff[key] == null) {
          // detect a delete of the object
          if (dispatch && '-' in dispatch) {
            if (key in dispatch) {
              this.__fire(dispatch['-'], {key: key, before: tree[key], value:null, clean: true, clean_on: dispatch[key]});
            } else {
              this.__fire(dispatch['-'], {key: key, before: tree[key], value:null});
            }
          }
          delete tree[key];
        } else {
          // make sure the key exists
          if (!(key in tree)) {
            tree[key] = {};
            tree[key].__key = "" + key;
            // since we are creating an item, let's fire the '+' event right now
            if (dispatch && '+' in dispatch) {
              // ensure the dispatch object exists
              if (!(key in dispatch)) {
                dispatch[key] = {};
              }
              // for each '+' event
              var dispatchAdd = dispatch['+'];
              if ('@e' in dispatchAdd) {
                var evts = dispatchAdd['@e'];
                for (var k = 0; k < evts.length; k++) { // fire it now
                  var evt = evts[k];
                  // __recAppendChange(dispatch: any, callback: any, insert_order: number, auto_delete: boolean, holder: any) {
                  this.__recAppendChange(dispatch[key], evt.cb({key:key}), evt.order, true, evt.order);
                }
              }
            }
          }
          this.__recMergeAndDispatch(tree[key], (dispatch != null && key in dispatch) ? dispatch[key] : null, diff[key]);
          if (dispatch && '!' in dispatch) {
            this.__fire(dispatch['!'], {key: key, value: tree[key]});
          }
        }
      }
    }
    var change = { before: prior, value: prior };
    if (resize !== null) {
      // See DList, but the idea is that I need to trim the list because the above tool care of nulls
      // this is for list of values where we synchronize a list of constants
      change.before = [];
      for (var k = 0; k < prior.length; k++) {
        change.before.push(prior[k]);
      }
      prior.length = resize;
      // TODO: validate this
    }
    if (ordering !== null) {
      var after = [];
      change.before = [];
      var absorder = [];
      for (var k = 0; k < prior.length; k++) {
        change.before.push(prior[k]);
      }
      for (var k = 0; k < ordering.length; k++) {
        var instr = ordering[k];
        var type_instr = typeof (instr);
        if (type_instr == "string" || type_instr == "number") {
          after.push(tree[instr]);
          absorder.push("" + instr);
        } else {
          var start = instr[0];
          var end = instr[1];
          for (var j = start; j <= end; j++) {
            absorder.push(prior[j].__key);
            after.push(prior[j]);
          }
        }
      }
      if (dispatch && '^' in dispatch) {
        this.__fire(dispatch['^'], {new_order: absorder});
      }
      prior.length = after.length;
      for (var k = 0; k < after.length; k++) {
        prior[k] = after[k];
      }
    }
    this.__fire(dispatch, change);
  }

  /** erase dispatches */
  __collapse(dispatch: any) {
    var d = dispatch['@e'];
    var nxt = [];
    for (var k = 0; k < d.length; k++) {
      var evt = d[k];
      if (evt !== null) {
        nxt.push(evt);
      }
    }
    dispatch['@e'] = nxt;
  }

  /** phase: fire events and place them in the queue */
  __fire(dispatch: any, change: any) {
    if (dispatch) {
      if ('@e' in dispatch) {
        var d = dispatch['@e'];
        var nulls = 0;
        for (var k = 0; k < d.length; k++) {
          var evt = d[k];
          if (evt !== null) {
            this.queue.push({cb: evt.cb, order: evt.order, change: change, dispatch_list: d, index: k});
          } else {
            nulls++;
          }
        }
        if (nulls > 0) {
          this.__collapse(dispatch);
        }
      }
    }
  }
  /** when a new event is added dynamically, we fire it directly against the tree */
  __fireDirect(callback: any, tree: any) {
    if (Array.isArray(callback)) { // callback is an array (expand)
      for (var k = 0; k < callback.length; k++) {
        this.__fireDirect(callback[k], tree)
      }
    } else if (typeof (callback) == 'object') {
      for (var key in callback) {
        if (key == '@e') {
          this.__fireDirect(callback['@e'], tree);
        } else {
          if (key in tree) {
            this.__fireDirect(callback[key], tree[key]);
          }
        }
      }
    } else {
      callback({value:tree, before:null});
    }
  }
  /** phase: final phase is draining the queue of events in the order applied by the user */
  __drain() {
    this.queue.sort(function (a: any, b: any) { return a.order - b.order; });
    for (var k = 0; k < this.queue.length; k++) {
      var item = this.queue[k];
      var result = item.cb(item.change);
      if (item.change.clean) {
        this.__autoDelete(item.change.clean_on);
      }
      if (result === 'delete') {
        item.dispatch_list[item.index] = null;
      } // else if item.change
    }
    this.queue = [];
  }
}

/**[BEGIN-EXPORTS]**/
export interface AssetKeyPayload {
  assetKey: string;
}

export interface AssetKeyResponder {
  success(data: AssetKeyPayload): void;
  failure(reason: number): void;
}

export interface AuthorityListingPayload {
  authority: string;
}

export interface AuthorityListingResponder {
  next(data: AuthorityListingPayload): void;
  complete():  void;
  failure(reason: number): void;
}

export interface BillingUsagePayload {
  hour: number;
  cpu: string;
  memory: string;
  connections: number;
  documents: number;
  messages: number;
  storageBytes: string;
}

export interface BillingUsageResponder {
  next(data: BillingUsagePayload): void;
  complete():  void;
  failure(reason: number): void;
}

export interface ClaimResultPayload {
  authority: string;
}

export interface ClaimResultResponder {
  success(data: ClaimResultPayload): void;
  failure(reason: number): void;
}

export interface DataPayload {
  delta: any;
}

export interface DataResponder {
  next(data: DataPayload): void;
  complete():  void;
  failure(reason: number): void;
}

export interface InitiationPayload {
  identity: string;
}

export interface InitiationResponder {
  success(data: InitiationPayload): void;
  failure(reason: number): void;
}

export interface KeyListingPayload {
  key: string;
  created: string;
  updated: string;
  seq: number;
}

export interface KeyListingResponder {
  next(data: KeyListingPayload): void;
  complete():  void;
  failure(reason: number): void;
}

export interface KeyPairPayload {
  keyId: number;
  publicKey: string;
}

export interface KeyPairResponder {
  success(data: KeyPairPayload): void;
  failure(reason: number): void;
}

export interface KeystorePayload {
  keystore: any;
}

export interface KeystoreResponder {
  success(data: KeystorePayload): void;
  failure(reason: number): void;
}

export interface PlanPayload {
  plan: any;
}

export interface PlanResponder {
  success(data: PlanPayload): void;
  failure(reason: number): void;
}

export interface ProgressPayload {
  chunk_request_size: number;
}

export interface ProgressResponder {
  next(data: ProgressPayload): void;
  complete():  void;
  failure(reason: number): void;
}

export interface ReflectionPayload {
  reflection: any;
}

export interface ReflectionResponder {
  success(data: ReflectionPayload): void;
  failure(reason: number): void;
}

export interface SeqPayload {
  seq: number;
}

export interface SeqResponder {
  success(data: SeqPayload): void;
  failure(reason: number): void;
}

export interface SimplePayload {
}

export interface SimpleResponder {
  success(data: SimplePayload): void;
  failure(reason: number): void;
}

export interface SpaceListingPayload {
  space: string;
  role: string;
  created: string;
  enabled: boolean;
  storageBytes: string;
}

export interface SpaceListingResponder {
  next(data: SpaceListingPayload): void;
  complete():  void;
  failure(reason: number): void;
}


  /**[END-EXPORTS]**/

export class ConsoleLogSeqResponder implements SeqResponder {
  prefix: string;
  constructor(prefix: string) {
    this.prefix = prefix;
  }

  failure(reason: number): void {
    console.log(this.prefix + "|error" + reason);
  }

  success(data: SeqPayload): void {
    console.log(this.prefix + "|success;seq=" + data.seq);
  }
}

export class TreePipeDataResponse implements DataResponder {
  tree: Tree;

  constructor(tree: Tree) {
    this.tree = tree;
  }

  next(data: DataPayload): void {
    this.tree.mergeUpdate(data.delta);
  }

  failure(reason: number): void {
    // TODO: figure out how to channel this to the tree
    console.log("tree|failure=" + reason);
  }

  complete(): void {
  }
}

export class Connection {
  // how long between retries
  backoff: number;
  maximum_backoff: number;

  // the websocket address
  url: string;

  // are we connected?
  connected: boolean;

  // are we dead? if we are dead, then we will stop retrying
  dead: boolean;

  // has a retry been scheduled?
  scheduled: boolean;

  // the socket
  socket: WebSocket | null;

  // the callbacks for the various inflight operations
  callbacks: Map<number, (result: object) => void>;

  // event: the status of connection has changed. true => connected & auth, false => not connected
  onstatuschange: (status: boolean) => void;

  // event: a ping from the client
  onping: (seconds: number, latency: number) => void;

  // event: we can connect, but we need auth credentials to make progress
  onauthneeded: (tryagain: () => void) => void;

  nextId: number;

  onreconnect: Map<number, object>;

  sessionId : string;
  sendId : number;

  assets: boolean;
  host: string;

  constructor(host: string) {
    this.backoff = 1;
    this.host = host;
    this.url = "wss://" + host + "/~s";
    this.assets = true;
    this.connected = false;
    this.dead = false;
    this.maximum_backoff = 2500;
    this.socket = null;
    this.onstatuschange = function (status: boolean) {
    };
    this.onping = function (seconds: number, latency: number) {
    };
    this.onauthneeded = function (tryagain: () => void) {
    };
    this.scheduled = false;
    this.callbacks = new Map<number, (result: object) => void>();
    this.nextId = 0;
    this.onreconnect = new Map<number, object>();
    this.sessionId = "";
    this.sendId = 0;
  }

  /** stop the connection */
  stop() {
    this.dead = true;
    if (this.socket !== null) {
      this.socket.close();
    }
  }

  /** private: retry the connection */
  _retry() {
    // null out the socket
    this.socket = null;
    // if we are connected, transition the status
    if (this.connected) {
      this.connected = false;
      this.onstatuschange(false);
    }
    this.callbacks.clear();
    // if we are dead, then don't actually retry
    if (this.dead) {
      return;
    }
    // make sure
    if (!this.scheduled) {
      // schedule a retry; first compute how long to take
      // compute how long we need to wait
      var halveAfterSchedule = false;
      this.backoff += Math.random() * this.backoff;
      if (this.backoff > this.maximum_backoff) {
        this.backoff = this.maximum_backoff;
        halveAfterSchedule = true;
      }

      // schedule it
      this.scheduled = true;
      var self = this;
      setTimeout(function () { self.start(); }, this.backoff);

      // we just scheduled it for the maximum backoff time, let's reduce the backoff so the next one will have some jitter
      if (halveAfterSchedule) {
        this.backoff /= 2.0;
      }
    }
  }

  /** start the connection */
  start() {
    // reset the state
    var self = this;
    this.scheduled = false;
    this.dead = false;
    // create the socket and bind event handlers
    this.socket = new WebSocket(this.url);
    this.socket.onmessage = function (event: WebSocket.MessageEvent) {
      var result = JSON.parse(event.data);
      // a message arrived, is it a connection signal
      if ('ping' in result) {
        self.onping(result.ping, result.latency);
        result.pong = new Date().getTime() / 1000.0;
        self.socket.send(JSON.stringify(result));
        return;
      }
      if ('status' in result) {
        // hey, are we connected?
        if (result.status != 'connected') {
          // nope, OK, let's make this a dead socket
          self.dead = true;
          self.socket.close();
          self.socket = null;
          // inform the client to try again
          self.onauthneeded(function () {
            self.start();
          });
          return;
        }
        // tell the client that we are good!
        self.backoff = 1;
        self.connected = true;
        self.assets = result.assets;
        self.sessionId = result.session_id;
        self.onstatuschange(true);
        self.ConfigureMakeOrGetAssetKey({
          success: function(payload) {
            try {
              var xhttp = new XMLHttpRequest();
              xhttp.open("GET", "https://" + self.host + "/~p" + payload.assetKey, true);
              xhttp.withCredentials = true;
              xhttp.send();
            } catch (ex) {
              console.log(ex);
            }
          },
          failure: function() {
          }
        })
        self._reconnect();
        return;
      }
      // the result was a failure..
      if ('failure' in result) {
        // find the callback, then invoke it (and clean up)
        if (self.callbacks.has(result.failure)) {
          var cb = self.callbacks.get(result.failure);
          if (cb) {
            self.callbacks.delete(result.failure);
            cb(result);
          }
        }
      } else if ('deliver' in result) {
        // otherwise, we have a success, so let's find the callback, and if need be clean up
        if (self.callbacks.has(result.deliver)) {
          var cb = self.callbacks.get(result.deliver);
          if (cb) {
            if (result.done) {
              self.callbacks.delete(result.deliver);
            }
            cb(result);
          }
        }
      }
    };
    this.socket.onclose = function (event: WebSocket.CloseEvent) {
      // let's retry... or should we not
      self._retry();
    };
    this.socket.onerror = function (event: WebSocket.ErrorEvent) {
      // something bad happened, let's retry
      self._retry();
    };
  }

  /** private: send a raw message */
  _write(request: { [k: string]: any }, callback: (result: object) => void) {
    if (!this.connected) {
      // TODO: queue here, and start a timer for timeouts
      callback({ failure: 600, reason: 9999 });
      return;
    }
    this.callbacks.set(request.id, callback);
    this.socket.send(JSON.stringify(request));
  }

  /** api: wait for a connection */
  async wait_connected() {
    if (this.connected) {
      return new Promise(function (good) {
        good(true);
      });
    } else {
      var self = this;
      var prior = this.onstatuschange;
      return new Promise(function (good) {
        self.onstatuschange = function (status) {
          prior(status);
          if (status) {
            good(true);
            self.onstatuschange = prior;
          }
        };
      });
    }
  }

  _reconnect() {
    this.onreconnect.forEach(function (sm: any, id: number) {
      sm.__retry();
    });
  }

  __execute_rr(sm: any) {
    var self = this;
    sm.first = true;
    self._write(sm.request, function (response: { [k: string]: any }) {
      if (sm.first) {
        sm.first = false;
        if ('failure' in response) {
          sm.responder.failure(response.reason);
        } else {
          sm.responder.success(response.response);
        }
      }
      self.onreconnect.delete(sm.id);
    });
    self.onreconnect.set(sm.id, sm);
    sm.__retry = function() {
      self.__execute_rr(sm);
    };
    return sm;
  }

  __execute_stream(sm: any) {
    var self = this;
    self._write(sm.request, function (response: { [k: string]: any }) {
      if ('failure' in response) {
        sm.responder.failure(response.reason);
        self.onreconnect.delete(sm.id);
        return;
      }
      if (response.response) {
        sm.responder.next(response.response);
      }
      if (response.done) {
        sm.responder.complete();
        self.onreconnect.delete(sm.id);
      }
    });
    self.onreconnect.set(sm.id, sm);
    sm.__retry = function() {
      self.__execute_stream(sm);
    };
    return sm;
  }

  /**[BEGIN-INVOKE]**/
  InitSetupAccount(email: string, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"init/setup-account", "id":parId, "email": email}
    });
  }
  InitCompleteAccount(email: string, revoke: boolean, code: string, responder: InitiationResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"init/complete-account", "id":parId, "email": email, "revoke": revoke, "code": code}
    });
  }
  AccountSetPassword(identity: string, password: string, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"account/set-password", "id":parId, "identity": identity, "password": password}
    });
  }
  AccountLogin(email: string, password: string, responder: InitiationResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"account/login", "id":parId, "email": email, "password": password}
    });
  }
  Probe(identity: string, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"probe", "id":parId, "identity": identity}
    });
  }
  AuthorityCreate(identity: string, responder: ClaimResultResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"authority/create", "id":parId, "identity": identity}
    });
  }
  AuthoritySet(identity: string, authority: string, keyStore: any, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"authority/set", "id":parId, "identity": identity, "authority": authority, "key-store": keyStore}
    });
  }
  AuthorityGet(identity: string, authority: string, responder: KeystoreResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"authority/get", "id":parId, "identity": identity, "authority": authority}
    });
  }
  AuthorityList(identity: string, responder: AuthorityListingResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"authority/list", "id":parId, "identity": identity}
    });
  }
  AuthorityDestroy(identity: string, authority: string, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"authority/destroy", "id":parId, "identity": identity, "authority": authority}
    });
  }
  SpaceCreate(identity: string, space: string, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/create", "id":parId, "identity": identity, "space": space}
    });
  }
  SpaceGenerateKey(identity: string, space: string, responder: KeyPairResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/generate-key", "id":parId, "identity": identity, "space": space}
    });
  }
  SpaceUsage(identity: string, space: string, limit: number, responder: BillingUsageResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"space/usage", "id":parId, "identity": identity, "space": space, "limit": limit}
    });
  }
  SpaceGet(identity: string, space: string, responder: PlanResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/get", "id":parId, "identity": identity, "space": space}
    });
  }
  SpaceSet(identity: string, space: string, plan: any, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/set", "id":parId, "identity": identity, "space": space, "plan": plan}
    });
  }
  SpaceDelete(identity: string, space: string, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/delete", "id":parId, "identity": identity, "space": space}
    });
  }
  SpaceSetRole(identity: string, space: string, email: string, role: string, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/set-role", "id":parId, "identity": identity, "space": space, "email": email, "role": role}
    });
  }
  SpaceReflect(identity: string, space: string, key: string, responder: ReflectionResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/reflect", "id":parId, "identity": identity, "space": space, "key": key}
    });
  }
  SpaceList(identity: string, marker: string, limit: number, responder: SpaceListingResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"space/list", "id":parId, "identity": identity, "marker": marker, "limit": limit}
    });
  }
  DocumentCreate(identity: string, space: string, key: string, entropy: string, arg: any, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"document/create", "id":parId, "identity": identity, "space": space, "key": key, "entropy": entropy, "arg": arg}
    });
  }
  DocumentList(identity: string, space: string, marker: string, limit: number, responder: KeyListingResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"document/list", "id":parId, "identity": identity, "space": space, "marker": marker, "limit": limit}
    });
  }
  ConnectionCreate(identity: string, space: string, key: string, viewerState: any, responder: DataResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"connection/create", "id":parId, "identity": identity, "space": space, "key": key, "viewer-state": viewerState},
      send: function(channel: string, message: any, subResponder: SeqResponder) {
        self.nextId++;
        var subId = self.nextId;
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/send", id: subId, "connection":parId, "channel": channel, "message": message}
        });
      },
      update: function(viewerState: any, subResponder: SimpleResponder) {
        self.nextId++;
        var subId = self.nextId;
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/update", id: subId, "connection":parId, "viewer-state": viewerState}
        });
      },
      end: function(subResponder: SimpleResponder) {
        self.nextId++;
        var subId = self.nextId;
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/end", id: subId, "connection":parId}
        });
      }
    });
  }
  ConfigureMakeOrGetAssetKey(responder: AssetKeyResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"configure/make-or-get-asset-key", "id":parId}
    });
  }
  AttachmentStart(identity: string, space: string, key: string, filename: string, contentType: string, responder: ProgressResponder) {
    var self = this;
    self.nextId++;
    var parId = self.nextId;
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"attachment/start", "id":parId, "identity": identity, "space": space, "key": key, "filename": filename, "content-type": contentType},
      append: function(chunkMd5: string, base64Bytes: string, subResponder: SimpleResponder) {
        self.nextId++;
        var subId = self.nextId;
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "attachment/append", id: subId, "upload":parId, "chunk-md5": chunkMd5, "base64-bytes": base64Bytes}
        });
      },
      finish: function(subResponder: SimpleResponder) {
        self.nextId++;
        var subId = self.nextId;
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "attachment/finish", id: subId, "upload":parId}
        });
      }
    });
  }

  /**[END-INVOKE]**/
}

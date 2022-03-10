import WebSocket from 'isomorphic-ws';

export const Production = "wss://aws-us-east-2.adama-platform.com/s";

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

  // recursively append a change
  // dispatch is the structural object mirroring the tree
  // callback is the function/object callback tree
  // insert_order is the order to fire events
  __recAppendChange(dispatch: any, callback: any, insert_order: number) {
    // the callback is an object
    if (typeof (callback) == 'object') {
      // we for each item in the callback
      for (var key in callback) {
        // make sure it exists
        if (!(key in dispatch)) {
          dispatch[key] = {};
        }
        // recurse into that key
        this.__recAppendChange(dispatch[key], callback[key], insert_order);
      }
    } else if (typeof (callback) == 'function') {
      // we have a function, so let's associate it to the node
      if (!('@e' in dispatch)) {
        dispatch['@e'] = [];
      }
      dispatch['@e'].push({ cb: callback, order: insert_order });
    }
  }

  onTreeChange(callback: any) {
    this.__recAppendChange(this.dispatch, callback, this.dispatch_count);
    this.dispatch_count++;
  }

  // the main function
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

  __recDeleteAndDispatch(tree: any, dispatch: any) {
    for (var key in tree) {
      let old = tree[key];

      if (Array.isArray(old)) {
        // need to fire the DELETE
      } else {
        if (key in dispatch) {
          this.__recDeleteAndDispatch(old, dispatch[key]);
        }
      }

      let deleteChildKey = '-' + key;
      if (dispatch != null && deleteChildKey in dispatch) {
        this.__fire(dispatch[deleteChildKey], {key: key, before: old, value: null});
      }
    }
  }

  __recMergeAndDispatch(tree: any, dispatch: any, diff: any) {
    // the diff is an object, so let's walk its keys
    for (var key in diff) {
      var child = diff[key];
      if (child === null) {
        let deleteChildKey = '-' + key;
        let old = tree[key];
        if (dispatch != null && deleteChildKey in dispatch) {
          this.__fire(dispatch[deleteChildKey], {key: key, before: old, value: null});
        }
        if (Array.isArray(old)) {
          // also delete
          let elementKey = "#" + key;
          if (elementKey in tree && elementKey in dispatch) {
            this.__recDeleteAndDispatch(tree[elementKey], dispatch[elementKey]);
          }
          delete tree["#" + key];
        }
        if (key in tree && key in dispatch) {
          this.__recDeleteAndDispatch(tree[key], dispatch[key]);
        }
        delete tree[key];
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
        if (!(key in tree)) {
          if (childIsArray) {
            tree[key] = [];
            tree["#" + key] = {};
          } else {
            tree[key] = {};
          }
        }
        // now, we check to see if the prior state influences whether or not the diff is an array
        childIsArray = Array.isArray(tree[key]) || childIsArray;
        if (childIsArray) {
          this.__recMergeAndDispatchArray(tree[key], (dispatch != null && key in dispatch) ? dispatch[key] : null, tree["#" + key], child);
        } else {
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
      if (dispatch != null && '@e' in dispatch) {
        this.__fire(dispatch, {value: tree});
      }
    }
  }
  __recMergeAndDispatchArray(prior: any, dispatch: any, tree: any, diff: any) {

    // TODO: new item... etc

    var ordering = null;
    var resize = null;
    for (var key in diff) {
      if (key == "@o") {
        ordering = diff[key];
      } else if (key == "@s") {
        resize = diff[key];
      } else {
        if (diff[key] == null) {
          if (dispatch && '-' in dispatch) {
            this.__fire(dispatch['-'], {key: key, before: tree[key], value:null});
          }
          delete tree[key];
        } else {
          var fireNew = false;
          if (!(tree != null && key in tree)) {
            if (dispatch && '+' in dispatch) {
              fireNew = true;
            }
            tree[key] = {};
          }
          this.__recMergeAndDispatch(tree[key], (dispatch != null && '#' in dispatch) ? dispatch['#'] : null, diff[key]);
          if (fireNew) {
            this.__fire(dispatch['+'], {key: key, before: null, value: tree[key]});
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
      var implicitDelete = dispatch ? '-' in dispatch : false;
      for (var k = 0; k < prior.length; k++) {
        change.before.push(prior[k]);
        if (implicitDelete) {
          prior[k].__kill = true;
        }
      }
      for (var k = 0; k < ordering.length; k++) {
        var instr = ordering[k];
        var type_instr = typeof (instr);
        if (type_instr == "string" || type_instr == "number") {
          after.push(tree[instr]);
          if (implicitDelete) {
            tree[instr].__kill = false;
          }
        } else {
          var start = instr[0];
          var end = instr[1];
          for (var j = start; j <= end; j++) {
            if (implicitDelete) {
              prior[j].__kill = false;
            }
            after.push(prior[j]);
          }
        }
      }
      if (implicitDelete) {
        for (key in tree) {
          if (tree[key].__kill) {
            if (key in dispatch) {
              this.__recDeleteAndDispatch(tree[key], dispatch[key]);
            }
            this.__fire(dispatch['-'], {key: key, before: tree[key], value:null});
          }
          delete tree[key].__kill;
        }
      }
      prior.length = after.length;
      for (var k = 0; k < after.length; k++) {
        prior[k] = after[k];
      }
    }
    this.__fire(dispatch, change);
  }
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
          var nxt = [];
          for (var k = 0; k < d.length; k++) {
            if (evt !== null) {
              nxt.push(evt);
            }
          }
          dispatch['@e'] = nxt;
        }
      }
    }
  }
  __drain() {
    this.queue.sort(function (a: any, b: any) { return a.order - b.order; });
    for (var k = 0; k < this.queue.length; k++) {
      var item = this.queue[k];
      if (item.cb(item.change) === 'delete') {
        item.dispatch_list[item.index] = null;
      }
    }
    this.queue = [];
  }
}

/**[BEGIN-EXPORTS]**/
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

  // the unique id for this connection
  rpcid: number;

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

  constructor(url: string) {
    this.backoff = 1;
    this.url = url;
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
    this.rpcid = 1;
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
        self.sessionId = result.session_id;
        self.onstatuschange(true);
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
    var id = this.rpcid;
    this.rpcid++;
    request['id'] = id;
    this.callbacks.set(id, callback);
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
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"init/setup-account", "id":id, "email": email}
    });
  }
  InitCompleteAccount(email: string, revoke: boolean, code: string, responder: InitiationResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"init/complete-account", "id":id, "email": email, "revoke": revoke, "code": code}
    });
  }
  AccountSetPassword(identity: string, password: string, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"account/set-password", "id":id, "identity": identity, "password": password}
    });
  }
  AccountLogin(email: string, password: string, responder: InitiationResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"account/login", "id":id, "email": email, "password": password}
    });
  }
  Probe(identity: string, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"probe", "id":id, "identity": identity}
    });
  }
  AuthorityCreate(identity: string, responder: ClaimResultResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"authority/create", "id":id, "identity": identity}
    });
  }
  AuthoritySet(identity: string, authority: string, keyStore: any, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"authority/set", "id":id, "identity": identity, "authority": authority, "key-store": keyStore}
    });
  }
  AuthorityGet(identity: string, authority: string, responder: KeystoreResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"authority/get", "id":id, "identity": identity, "authority": authority}
    });
  }
  AuthorityList(identity: string, responder: AuthorityListingResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_stream({
      id: id,
      responder: responder,
      request: {"method":"authority/list", "id":id, "identity": identity}
    });
  }
  AuthorityDestroy(identity: string, authority: string, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"authority/destroy", "id":id, "identity": identity, "authority": authority}
    });
  }
  SpaceCreate(identity: string, space: string, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"space/create", "id":id, "identity": identity, "space": space}
    });
  }
  SpaceUsage(identity: string, space: string, limit: number, responder: BillingUsageResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_stream({
      id: id,
      responder: responder,
      request: {"method":"space/usage", "id":id, "identity": identity, "space": space, "limit": limit}
    });
  }
  SpaceGet(identity: string, space: string, responder: PlanResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"space/get", "id":id, "identity": identity, "space": space}
    });
  }
  SpaceSet(identity: string, space: string, plan: any, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"space/set", "id":id, "identity": identity, "space": space, "plan": plan}
    });
  }
  SpaceDelete(identity: string, space: string, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"space/delete", "id":id, "identity": identity, "space": space}
    });
  }
  SpaceSetRole(identity: string, space: string, email: string, role: string, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"space/set-role", "id":id, "identity": identity, "space": space, "email": email, "role": role}
    });
  }
  SpaceReflect(identity: string, space: string, key: string, responder: ReflectionResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"space/reflect", "id":id, "identity": identity, "space": space, "key": key}
    });
  }
  SpaceList(identity: string, marker: string, limit: number, responder: SpaceListingResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_stream({
      id: id,
      responder: responder,
      request: {"method":"space/list", "id":id, "identity": identity, "marker": marker, "limit": limit}
    });
  }
  DocumentCreate(identity: string, space: string, key: string, entropy: string, arg: any, responder: SimpleResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_rr({
      id: id,
      responder: responder,
      request: {"method":"document/create", "id":id, "identity": identity, "space": space, "key": key, "entropy": entropy, "arg": arg}
    });
  }
  DocumentList(identity: string, space: string, marker: string, limit: number, responder: KeyListingResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_stream({
      id: id,
      responder: responder,
      request: {"method":"document/list", "id":id, "identity": identity, "space": space, "marker": marker, "limit": limit}
    });
  }
  ConnectionCreate(identity: string, space: string, key: string, viewerState: any, responder: DataResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_stream({
      id: id,
      responder: responder,
      request: {"method":"connection/create", "id":id, "identity": identity, "space": space, "key": key, "viewer-state": viewerState},
      send: function(channel: string, message: any, subResponder: SeqResponder) {
        self.nextId++;
        var subId = self.nextId;
        var parId = id;
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/send", id: subId, "connection":parId, "channel": channel, "message": message}
        });
      },
      update: function(viewerState: any, subResponder: SimpleResponder) {
        self.nextId++;
        var subId = self.nextId;
        var parId = id;
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/update", id: subId, "connection":parId, "viewer-state": viewerState}
        });
      },
      end: function(subResponder: SimpleResponder) {
        self.nextId++;
        var subId = self.nextId;
        var parId = id;
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/end", id: subId, "connection":parId}
        });
      }
    });
  }
  AttachmentStart(identity: string, space: string, key: string, filename: string, contentType: string, responder: ProgressResponder) {
    var self = this;
    self.nextId++;
    var id = self.nextId;
    return self.__execute_stream({
      id: id,
      responder: responder,
      request: {"method":"attachment/start", "id":id, "identity": identity, "space": space, "key": key, "filename": filename, "content-type": contentType},
      append: function(chunkMd5: string, base64Bytes: string, subResponder: SimpleResponder) {
        self.nextId++;
        var subId = self.nextId;
        var parId = id;
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "attachment/append", id: subId, "upload":parId, "chunk-md5": chunkMd5, "base64-bytes": base64Bytes}
        });
      },
      finish: function(subResponder: SimpleResponder) {
        self.nextId++;
        var subId = self.nextId;
        var parId = id;
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

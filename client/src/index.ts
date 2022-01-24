import WebSocket from 'isomorphic-ws';

export class AdamaTree {
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

export class AdamaConnection {
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

  connectId: number;

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
    this.connectId = 1;
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
    // fail all outstanding operations
    this.callbacks.forEach(function (callback: (result: object) => void, id: number) {
      callback({ failure: id, reason: 77 });
    });
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
            cb(result.response);
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
      return;
    }
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

  /** api: generate a new game */
  /*
  async generate(gs: string) {
    var request = { method: "reserve", space: gs };
    var self = this;
    return new Promise(function (good, bad) {
      self._send(request, function (response: { [k: string]: any }) {
        if ('failure' in response) {
          bad(response.reason)
        } else {
          good(response.key);
        }
      });
    });
  }
  */

  /** api: get the schema for the gamepsace */
  /*
  async reflect(gs: string) {
    var request = { method: "reflect", space: gs, key:'0' };
    var self = this;
    return new Promise(function (good, bad) {
      self._send(request, function (response: { [k: string]: any }) {
        if ('failure' in response) {
          bad(response.reason)
        } else {
          good(response);
        }
      });
    });
  }

  async load_code(gs: string) {
    var request = { method: "load_code", space: gs };
    var self = this;
    return new Promise(function (good, bad) {
      self._send(request, function (response: { [k: string]: any }) {
        if ('failure' in response) {
          bad(response.reason)
        } else {
          good(response);
        }
      });
    });
  }

  async save_code(gs: string, code: string) {
    var request = { method: "save_code", space: gs, code: code};
    var self = this;
    return new Promise(function (good, bad) {
      self._send(request, function (response: { [k: string]: any }) {
        if ('failure' in response) {
          bad(response.reason)
        } else {
          good(response);
        }
      });
    });
  }

  async deploy(gs: string) {
    var request = { method: "deploy", space: gs};
    var self = this;
    return new Promise(function (good, bad) {
      self._send(request, function (response: { [k: string]: any }) {
        if ('failure' in response) {
          bad(response.reason)
        } else {
          good(response);
        }
      });
    });
  }
  */


  /** api: generate a new game */
  /*
  async create(gs: string, id: string, arg: object) {
    var request = { method: "create", space: gs, key: id, arg: arg };
    var self = this;
    return new Promise(function (good, bad) {
      self._send(request, function (response: { [k: string]: any }) {
        if ('failure' in response) {
          bad(response.reason)
        } else {
          good(response);
        }
      });
    });
  }
  */

  /** api: connect to a game */
  /*
  async connect(gs: string, id: string, handler: (data: object) => void) {
    var request = { method: "connect", space: gs, key: id };
    var self = this;
    var first = true;
    return new Promise(function (good, bad) {
      self._send(request, function (response: { [k: string]: any }) {
        if (first) {
          first = false;
          if ('failure' in response) {
            bad(response.reason)
          } else {
            handler(response);
            good(true);
          }
        } else {
          handler(response);
        }
      });
    });
  }
  */

  /** api: send a message */
  /*
  async send(gs: string, id: string, channel: string, msg: any, hack: (request: any) => void) {
    var self = this;
    var request = {method: "send", marker: self.sessionId = "/" + self.sendId, space: gs, key: id, channel: channel, message: msg};
    self.sendId ++;

    if (hack) {
      hack(request);
    }

    // TODO: queue this up? with retry?
    return new Promise(function (good, bad) {
      self._send(request, function (response: { [k: string]: any }) {
        if ('failure' in response) {
          bad(response.reason)
        } else {
          good(response);
        }
      });
    });
  }
  */

  /** api: connect tree */
  /*
  async connectTree(gs: string, id: string, tree: AdamaTree, hack: (request: any) => void) {
    var keyId = this.connectId;
    this.connectId++;
    let sm = {
      request: {method: "connect", space: gs, key: id},
      first: true,
      handler: function (r: any) {
        tree.mergeUpdate(r);
      }
    };
    if (hack) {
      hack(sm.request);
    }
    this.onreconnect.set(keyId, sm);
    return this._execute(sm);
  }
  */

  _reconnect() {
    var self = this;
    this.onreconnect.forEach(function (sm: object, id: number) {
      sm.first = true;
      self._execute(sm);
    });
  }

  async _execute(sm: any) {
    var self = this;
    sm.first = true;
    return new Promise(function (good, bad) {
      self._write(sm.request, function (response: { [k: string]: any }) {
        if (sm.first) {
          sm.first = false;
          if ('failure' in response) {
            bad(response.reason)
          } else {
            sm.handler(response);
            good(true);
          }
          if (sm.remove) {
            self.onreconnect.delete(sm.key);
          }
        } else {
          sm.handler(response);
        }
      });
    });
  }
}

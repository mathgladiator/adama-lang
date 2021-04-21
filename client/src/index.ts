import WebSocket from 'isomorphic-ws';

import { Tree } from './tree';

export function MakeTree(): Tree {
  return new Tree();
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

  connectId: number;

  onreconnect: Map<number, object>;

  sessionId : string;
  sendId : number;

  constructor(url: string) {
    var self = this;
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
      if ('signal' in result) {
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
  _send(request: { [k: string]: any }, callback: (result: object) => void) {
    if (!this.connected) {
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
          good();
          self.onstatuschange = prior;
        }
      };
    });
  }

  /** api: generate a new game */
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

  /** api: get the schema for the gamepsace */
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


  /** api: generate a new game */
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

  /** api: connect to a game */
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

  /** api: send a message */
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

  /** api: connect tree */
  async connectTree(gs: string, id: string, tree: Tree, hack: (request: any) => void) {
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

  _reconnect() {
    var self = this;
    this.onreconnect.forEach(function (sm: object, id: number) {
      self._execute(sm);
    });
  }

  async _execute(sm: any) {
    var self = this;
    return new Promise(function (good, bad) {
      self._send(sm.request, function (response: { [k: string]: any }) {
        if (sm.first) {
          sm.first = false;
          if ('failure' in response) {
            bad(response.reason)
          } else {
            sm.handler(response);
            good(true);
          }
        } else {
          sm.handler(response);
        }
      });
    });
  }
}

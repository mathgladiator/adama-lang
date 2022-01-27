import WebSocket from 'isomorphic-ws';

/**[BEGIN-CODEGEN-EXPORTS]**/
/**[END-CODEGEN-EXPORTS]**/

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
    this.nextId = 1;
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
    var keyId = this.nextId;
    this.nextId++;
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
    this.onreconnect.forEach(function (sm: any, id: number) {
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

  /**[BEGIN-INVOKE]**/
  async initStart(email: string) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"init/start", "id":id, "email":email};
    return {
      InitRevokeAll: function(code: string){
        // var subId = self.nextId;
        self.nextId++;
        // var request = {"method":"init/revoke-all", "id":subId, "connection":id, "code":code};
      },
      InitGenerateIdentity: function(revoke: boolean, code: string){
        // var subId = self.nextId;
        self.nextId++;
        // var request = {"method":"init/generate-identity", "id":subId, "connection":id, "revoke":revoke, "code":code};
      }
    };
  }
  async probe(identity: string) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"probe", "id":id, "identity":identity};
  }
  async authorityCreate(identity: string) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"authority/create", "id":id, "identity":identity};
  }
  async authoritySet(identity: string, authority: string, keyStore: any) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"authority/set", "id":id, "identity":identity, "authority":authority, "key-store":keyStore};
  }
  async authorityGet(identity: string, authority: string) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"authority/get", "id":id, "identity":identity, "authority":authority};
  }
  async authorityList(identity: string) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"authority/list", "id":id, "identity":identity};
  }
  async authorityDestroy(identity: string, authority: string) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"authority/destroy", "id":id, "identity":identity, "authority":authority};
  }
  async spaceCreate(identity: string, space: string) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"space/create", "id":id, "identity":identity, "space":space};
  }
  async spaceGet(identity: string, space: string) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"space/get", "id":id, "identity":identity, "space":space};
  }
  async spaceSet(identity: string, space: string, plan: any) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"space/set", "id":id, "identity":identity, "space":space, "plan":plan};
  }
  async spaceDelete(identity: string, space: string) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"space/delete", "id":id, "identity":identity, "space":space};
  }
  async spaceSetRole(identity: string, space: string, email: string, role: string) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"space/set-role", "id":id, "identity":identity, "space":space, "email":email, "role":role};
  }
  async spaceReflect(identity: string, space: string, key: string) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"space/reflect", "id":id, "identity":identity, "space":space, "key":key};
  }
  async spaceList(identity: string, marker: string, limit: number) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"space/list", "id":id, "identity":identity, "marker":marker, "limit":limit};
  }
  async documentCreate(identity: string, space: string, key: string, entropy: string, arg: any) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"document/create", "id":id, "identity":identity, "space":space, "key":key, "entropy":entropy, "arg":arg};
  }
  async documentList(identity: string, space: string, marker: string, limit: number) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"document/list", "id":id, "identity":identity, "space":space, "marker":marker, "limit":limit};
  }
  async connectionCreate(identity: string, space: string, key: string) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"connection/create", "id":id, "identity":identity, "space":space, "key":key};
    return {
      ConnectionSend: function(channel: string, message: any){
        // var subId = self.nextId;
        self.nextId++;
        // var request = {"method":"connection/send", "id":subId, "connection":id, "channel":channel, "message":message};
      },
      ConnectionEnd: function(){
        // var subId = self.nextId;
        self.nextId++;
        // var request = {"method":"connection/end", "id":subId, "connection":id};
      }
    };
  }
  async attachmentStart(identity: string, space: string, key: string, filename: string, contentType: string) {
    var self = this;
    // var id = self.nextId;
    self.nextId++;
    // var request = {"method":"attachment/start", "id":id, "identity":identity, "space":space, "key":key, "filename":filename, "content-type":contentType};
    return {
      AttachmentAppend: function(chunkMd5: string, base64Bytes: string){
        // var subId = self.nextId;
        self.nextId++;
        // var request = {"method":"attachment/append", "id":subId, "upload":id, "chunk-md5":chunkMd5, "base64-bytes":base64Bytes};
      },
      AttachmentFinish: function(md5: string){
        // var subId = self.nextId;
        self.nextId++;
        // var request = {"method":"attachment/finish", "id":subId, "upload":id, "md5":md5};
      }
    };
  }

  /**[END-INVOKE]**/
}

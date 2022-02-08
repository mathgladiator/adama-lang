import WebSocket from 'isomorphic-ws';

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
  billing: string;
  created: string;
  balance: number;
  storageBytes: string;
}

export interface SpaceListingResponder {
  next(data: SpaceListingPayload): void;
  complete():  void;
  failure(reason: number): void;
}


  /**[END-EXPORTS]**/

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

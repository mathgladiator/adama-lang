/** The WebSocket connection to Adama */
class WebSocketAdamaConnection {
  constructor(host) {
    // on failure, we back-off with this milliseconds
    this.backoff = 1;
    // the host we are connecting to
    this.host = host;
    // form the URL of the host
    this.url = "wss://" + host + "/~s";
    // is the connection connected
    this.connected = false;
    // TODO
    this.assets = true;
    // is the connection dead
    this.dead = false;
    // the maximum period of time between waiting for a retry
    this.maximum_backoff = 5000;
    // when the connection times out from the server, how long to wait until we reconnect
    this.reset_backoff = 1000;
    // the actual websocket
    this.socket = null;
    // what happens when the status changes
    this.onstatuschange = function (status) {};
    // what happens when we learn the latency
    this.onping = function (seconds, latency) {};
    // is a retry scheduled
    this.scheduled = false;
    // callback mapping
    this.callbacks = new Map();
    // id generation
    this.nextId = 0;
    // events to execute on reconnect
    this.onreconnect = new Map();
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
      setTimeout(function () {
        self.start();
      }, this.backoff);

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
    this.socket.onmessage = function (event) {
      var result = JSON.parse(event.data);
      // a message arrived, is it a connection signal
      if ("ping" in result) {
        self.onping(result.ping, result.latency);
        result.pong = new Date().getTime() / 1000.0;
        self.socket.send(JSON.stringify(result));
        return;
      }
      if ("status" in result) {
        // hey, are we connected?
        if (result.status != "connected") {
          // nope, OK, let's make this a dead socket
          self.socket.close();
          self.socket = null;
          self.backoff = self.reset_backoff;
          self._retry();
          return;
        }
        // tell the client that we are good!
        self.backoff = 1;
        self.connected = true;
        self.assets = result.assets;
        self.onstatuschange(true);
        self._reconnect();
        self.ConfigureMakeOrGetAssetKey({
          success: function (payload) {
            try {
              var xhttp = new XMLHttpRequest();
              xhttp.open("GET", "https://" + self.host + "/~p" + payload.assetKey, true);
              xhttp.withCredentials = true;
              xhttp.send();
            } catch (ex) {
              console.log(ex);
            }
          },
          failure: function () {
          }
        });
        return;
      }
      // the result was a failure...
      if ("failure" in result) {
        // find the callback, then invoke it (and clean up)
        if (self.callbacks.has(result.failure)) {
          var cb = self.callbacks.get(result.failure);
          if (cb) {
            self.callbacks.delete(result.failure);
            cb(result);
          }
        }
      } else if ("deliver" in result) {
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
    this.socket.onclose = function (event) {
      // let's retry... or should we not
      self._retry();
    };
    this.socket.onerror = function (event) {
      // something bad happened, let's retry
      self._retry();
    };
  }

  /** private: send a raw message */
  _write(request, callback) {
    if (!this.connected) {
      callback({failure: 600, reason: 9999});
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
    this.onreconnect.forEach(function (sm, id) {
      sm.__retry();
    });
  }

  /** execute a requese response with the given state machine */
  __execute_rr(sm) {
    var self = this;
    sm.first = true;
    self._write(sm.request, function (response) {
      if (sm.first) {
        sm.first = false;
        if ("failure" in response) {
          if ('failure' in sm.responder) {
            sm.responder.failure(response.reason);
          }
        } else {
          if ('success' in sm.responder) {
            sm.responder.success(response.response);
          }
        }
      }
      self.onreconnect.delete(sm.id);
    });
    self.onreconnect.set(sm.id, sm);
    sm.__retry = function () {
      self.__execute_rr(sm);
    };
    return sm;
  }

  /** execute a stream request with the given state machine */
  __execute_stream(sm) {
    var self = this;
    self._write(sm.request, function (response) {
      if ("failure" in response) {
        if ('failure' in sm.responder) {
          sm.responder.failure(response.reason);
        }
        self.onreconnect.delete(sm.id);
        return;
      }
      if (response.response) {
        if ('next' in sm.responder) {
          sm.responder.next(response.response);
        }
      }
      if (response.done) {
        if ('complete' in sm.responder) {
          sm.responder.complete();
        }
        self.onreconnect.delete(sm.id);
      }
    });
    self.onreconnect.set(sm.id, sm);
    sm.__retry = function () {
      self.__execute_stream(sm);
    };
    return sm;
  }

  __id() {
    this.nextId++;
    return this.nextId;
  }

  /**[BEGIN-INVOKE]**/
  InitSetupAccount(email, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"init/setup-account", "id":parId, "email": email}
    });
  }
  InitConvertGoogleUser(accessToken, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"init/convert-google-user", "id":parId, "access-token": accessToken}
    });
  }
  InitCompleteAccount(email, revoke, code, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"init/complete-account", "id":parId, "email": email, "revoke": revoke, "code": code}
    });
  }
  AccountSetPassword(identity, password, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"account/set-password", "id":parId, "identity": identity, "password": password}
    });
  }
  AccountGetPaymentPlan(identity, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"account/get-payment-plan", "id":parId, "identity": identity}
    });
  }
  AccountLogin(email, password, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"account/login", "id":parId, "email": email, "password": password}
    });
  }
  Probe(identity, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"probe", "id":parId, "identity": identity}
    });
  }
  AuthorityCreate(identity, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"authority/create", "id":parId, "identity": identity}
    });
  }
  AuthoritySet(identity, authority, keyStore, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"authority/set", "id":parId, "identity": identity, "authority": authority, "key-store": keyStore}
    });
  }
  AuthorityGet(identity, authority, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"authority/get", "id":parId, "identity": identity, "authority": authority}
    });
  }
  AuthorityList(identity, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"authority/list", "id":parId, "identity": identity}
    });
  }
  AuthorityDestroy(identity, authority, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"authority/destroy", "id":parId, "identity": identity, "authority": authority}
    });
  }
  SpaceCreate(identity, space, template, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/create", "id":parId, "identity": identity, "space": space, "template": template}
    });
  }
  SpaceGenerateKey(identity, space, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/generate-key", "id":parId, "identity": identity, "space": space}
    });
  }
  SpaceUsage(identity, space, limit, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"space/usage", "id":parId, "identity": identity, "space": space, "limit": limit}
    });
  }
  SpaceGet(identity, space, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/get", "id":parId, "identity": identity, "space": space}
    });
  }
  SpaceSet(identity, space, plan, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/set", "id":parId, "identity": identity, "space": space, "plan": plan}
    });
  }
  SpaceRedeployKick(identity, space, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/redeploy-kick", "id":parId, "identity": identity, "space": space}
    });
  }
  SpaceSetRxhtml(identity, space, rxhtml, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/set-rxhtml", "id":parId, "identity": identity, "space": space, "rxhtml": rxhtml}
    });
  }
  SpaceGetRxhtml(identity, space, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/get-rxhtml", "id":parId, "identity": identity, "space": space}
    });
  }
  SpaceDelete(identity, space, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/delete", "id":parId, "identity": identity, "space": space}
    });
  }
  SpaceSetRole(identity, space, email, role, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/set-role", "id":parId, "identity": identity, "space": space, "email": email, "role": role}
    });
  }
  SpaceReflect(identity, space, key, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"space/reflect", "id":parId, "identity": identity, "space": space, "key": key}
    });
  }
  SpaceList(identity, marker, limit, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"space/list", "id":parId, "identity": identity, "marker": marker, "limit": limit}
    });
  }
  DomainMap(identity, domain, space, certificate, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"domain/map", "id":parId, "identity": identity, "domain": domain, "space": space, "certificate": certificate}
    });
  }
  DomainMapDocument(identity, domain, space, key, certificate, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"domain/map-document", "id":parId, "identity": identity, "domain": domain, "space": space, "key": key, "certificate": certificate}
    });
  }
  DomainList(identity, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"domain/list", "id":parId, "identity": identity}
    });
  }
  DomainUnmap(identity, domain, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"domain/unmap", "id":parId, "identity": identity, "domain": domain}
    });
  }
  DomainGet(identity, domain, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"domain/get", "id":parId, "identity": identity, "domain": domain}
    });
  }
  DocumentAuthorize(space, key, username, password, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"document/authorize", "id":parId, "space": space, "key": key, "username": username, "password": password}
    });
  }
  DocumentCreate(identity, space, key, entropy, arg, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"document/create", "id":parId, "identity": identity, "space": space, "key": key, "entropy": entropy, "arg": arg}
    });
  }
  DocumentDelete(identity, space, key, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"document/delete", "id":parId, "identity": identity, "space": space, "key": key}
    });
  }
  DocumentList(identity, space, marker, limit, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"document/list", "id":parId, "identity": identity, "space": space, "marker": marker, "limit": limit}
    });
  }
  MessageDirectSend(identity, space, key, viewerState, channel, message, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"message/direct-send", "id":parId, "identity": identity, "space": space, "key": key, "viewer-state": viewerState, "channel": channel, "message": message}
    });
  }
  MessageDirectSendOnce(identity, space, key, dedupe, channel, message, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"message/direct-send-once", "id":parId, "identity": identity, "space": space, "key": key, "dedupe": dedupe, "channel": channel, "message": message}
    });
  }
  ConnectionCreate(identity, space, key, viewerState, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"connection/create", "id":parId, "identity": identity, "space": space, "key": key, "viewer-state": viewerState},
      send: function(channel, message, subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/send", id: subId, "connection":parId, "channel": channel, "message": message}
        });
      },
      password: function(username, password, new_password, subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/password", id: subId, "connection":parId, "username": username, "password": password, "new_password": new_password}
        });
      },
      sendOnce: function(channel, dedupe, message, subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/send-once", id: subId, "connection":parId, "channel": channel, "dedupe": dedupe, "message": message}
        });
      },
      canAttach: function(subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/can-attach", id: subId, "connection":parId}
        });
      },
      attach: function(assetId, filename, contentType, size, digestMd5, digestSha384, subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/attach", id: subId, "connection":parId, "asset-id": assetId, "filename": filename, "content-type": contentType, "size": size, "digest-md5": digestMd5, "digest-sha384": digestSha384}
        });
      },
      update: function(viewerState, subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/update", id: subId, "connection":parId, "viewer-state": viewerState}
        });
      },
      end: function(subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/end", id: subId, "connection":parId}
        });
      }
    });
  }
  ConnectionCreateViaDomain(identity, domain, viewerState, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"connection/create-via-domain", "id":parId, "identity": identity, "domain": domain, "viewer-state": viewerState},
      send: function(channel, message, subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/send", id: subId, "connection":parId, "channel": channel, "message": message}
        });
      },
      password: function(username, password, new_password, subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/password", id: subId, "connection":parId, "username": username, "password": password, "new_password": new_password}
        });
      },
      sendOnce: function(channel, dedupe, message, subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/send-once", id: subId, "connection":parId, "channel": channel, "dedupe": dedupe, "message": message}
        });
      },
      canAttach: function(subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/can-attach", id: subId, "connection":parId}
        });
      },
      attach: function(assetId, filename, contentType, size, digestMd5, digestSha384, subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/attach", id: subId, "connection":parId, "asset-id": assetId, "filename": filename, "content-type": contentType, "size": size, "digest-md5": digestMd5, "digest-sha384": digestSha384}
        });
      },
      update: function(viewerState, subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/update", id: subId, "connection":parId, "viewer-state": viewerState}
        });
      },
      end: function(subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "connection/end", id: subId, "connection":parId}
        });
      }
    });
  }
  DocumentsHashPassword(password, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"documents/hash-password", "id":parId, "password": password}
    });
  }
  ConfigureMakeOrGetAssetKey(responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"configure/make-or-get-asset-key", "id":parId}
    });
  }
  AttachmentStart(identity, space, key, filename, contentType, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"attachment/start", "id":parId, "identity": identity, "space": space, "key": key, "filename": filename, "content-type": contentType},
      append: function(chunkMd5, base64Bytes, subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "attachment/append", id: subId, "upload":parId, "chunk-md5": chunkMd5, "base64-bytes": base64Bytes}
        });
      },
      finish: function(subResponder) {
        var subId = self.__id();
        self.__execute_rr({
          id: subId,
          responder: subResponder,
          request: { method: "attachment/finish", id: subId, "upload":parId}
        });
      }
    });
  }
  SuperCheckIn(identity, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"super/check-in", "id":parId, "identity": identity}
    });
  }
  SuperListAutomaticDomains(identity, timestamp, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_stream({
      id: parId,
      responder: responder,
      request: {"method":"super/list-automatic-domains", "id":parId, "identity": identity, "timestamp": timestamp}
    });
  }
  SuperSetDomainCertificate(identity, domain, certificate, timestamp, responder) {
    var self = this;
    var parId = self.__id();
    return self.__execute_rr({
      id: parId,
      responder: responder,
      request: {"method":"super/set-domain-certificate", "id":parId, "identity": identity, "domain": domain, "certificate": certificate, "timestamp": timestamp}
    });
  }

  /**[END-INVOKE]**/
}

var Adama = {
  Production: "aws-us-east-2.adama-platform.com",
  Connection: WebSocketAdamaConnection
};

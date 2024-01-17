var RxHTML = (function () {
  var self = {};

  var templates = {};
  var router = {};

  var defaultEndpoint = /*ENDPOINT=[*/Adama.Production/*]*/;

  // override endpoint based on the presence of __md_endpoint
  var mdOverrideEndpoint = localStorage.getItem("mdec"); // multi domain endpoint choice
  if (mdOverrideEndpoint != null && "beta" == mdOverrideEndpoint) {
    defaultEndpoint = Adama.Beta;
  }

  // This strange escaping is for developer mode to proxy to localhost.
  var connection = new Adama.Connection(defaultEndpoint);
  var connections = {};
  self.connection = connection;
  self.bump = function(m) {
    connection.bump(m);
  };

  var connectionMonitorDom = document.createElement("div");
  connection.onstatuschange = function (status) {
    var icon = function (color) { return "<svg xmlns=\"http://www.w3.org/2000/svg\" fill=\"none\" viewBox=\"0 0 24 24\" stroke-width=\"1.5\" stroke=\"" + color + "\" style=\"width:16px; height:16px\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M8.288 15.038a5.25 5.25 0 017.424 0M5.106 11.856c3.807-3.808 9.98-3.808 13.788 0M1.924 8.674c5.565-5.565 14.587-5.565 20.152 0M12.53 18.22l-.53.53-.53-.53a.75.75 0 011.06 0z\" /></svg>\n"; };
    if (!status) {
      connectionMonitorDom.title = "disconnected";
      connectionMonitorDom.innerHTML = icon("red");
      connection.onping = function (_1, _2) { };
    } else {
      connection.onping = function (_, latency) {
        connectionMonitorDom.title = "connected; latency=" + latency + "ms";
        if (latency >= 100) {
          connectionMonitorDom.innerHTML = icon("orange");
        } else {
          connectionMonitorDom.innerHTML = icon("green");
        }
      };
    }
  };
  connectionMonitorDom.style = "position:fixed; bottom:0px; right:0px";
  connectionMonitorDom.className = "adama-debugger"
  if (Adama.Debugger) {
    connectionMonitorDom.onclick = Adama.Debugger.toggle;
  }
  connection.start();
  connection.keepalive();

  window.addEventListener('error', function(event) {
    var payload = {};
    payload.message = event.message;
    payload.filename = event.filename;
    payload.lineno = event.lineno;
    payload.colno = event.colno;
    payload.filename = event.filename;
    connection.log("window.error", JSON.stringify(payload));
    console.error("[error]" + event.message);
  });

  connection.bump("rxhtml");

  var rootReplace = "/";
  var fixPath = function (path) {
    return path;
  };
  if (window.location.hostname.endsWith(".adama-platform.com") && !window.location.hostname.endsWith("ide.adama-platform.com")) {
    // this exists if someone is trying to use the global domain for their site
    var parts = window.location.pathname.split("/");
    rootReplace = [parts[0], parts[1], parts[2], ""].join("/");
    var offset = parts[0].length + parts[1].length + parts[2].length + 2;
    fixPath = function (path) {
      return path.substring(offset);
    };
  }

  var fixHref = function (href) {
    if (!href) {
      return "#";
    }
    if (href.startsWith("/")) {
      return rootReplace + href.substring(1);
    }
    return href;
  };

  var get_connection_obj = function (name) {
    if (name in connections) {
      return connections[name];
    } else {
      var obj = {
        name: name,
        ptr: null,
        tree: new AdamaTree(),
        outstanding: {},
        decisions: {},
        choice_subs: {},
        resets: {},
        connection_events: {},
        sync_events: {},
        id: 0,
        connection_state: false,
        choices: {},
        bound: "",
        viewstate_sent: true,
        viewstate_clone: "",
        has_filter: false,
        synced: false,
        filter: {},
        vsseq: 0,
        debugger: function (delta) { },
        raw: connection
      };
      obj.nuke = function () {
        this.connection_events = {};
        this.sync_events = {};
        this.choice_subs = {};
      }.bind(obj);
      if (Adama.Debugger) {
        obj.debugger = Adama.Debugger.register(obj);
      }
      obj.sync = function () {
        var synced = this.connection_state && this.viewstate_sent;
        if (this.synced == synced) {
          return;
        }
        this.synced = synced;
        var axe = [];
        for (var sub in obj.sync_events) {
          if (!(obj.sync_events[sub](synced))) {
            axe.push(sub);
          }
        }
        for (var k = 0; k < axe.length; k++) {
          delete obj.sync_events[axe[k]];
        }
      }.bind(obj);
      obj.subscribe_sync = function (callback) {
        var s = "-|" + this.id++;
        this.sync_events[s] = callback;
        callback(this.synced);
        return function () {
          delete this.sync_events[s];
        }.bind(this);
      };
      obj.set_connected = function (cs) {
        if (this.connection_state == cs) {
          return;
        }
        this.connection_state = cs;
        var axe = [];
        for (var sub in obj.connection_events) {
          if (!(obj.connection_events[sub](cs))) {
            axe.push(sub);
          }
        }
        for (var k = 0; k < axe.length; k++) {
          delete obj.connection_events[axe[k]];
        }
        this.sync();
      }.bind(obj);
      obj.connected = function (callback) {
        var s = "-|" + this.id++;
        this.connection_events[s] = callback;
        callback(this.connection_state);
        return function () {
          delete this.connection_events[s];
        }.bind(this);
      }.bind(obj);
      obj.subscribe_any = function (callback) {
        var s = "-|" + this.id++;
        this.decisions[s] = callback;
        return function () {
          delete this.decisions[s];
        }.bind(this);
      }.bind(obj);
      obj.subscribe = function (channel, callback) {
        var s = channel + "|" + this.id++;
        this.decisions[s] = callback;
        return function () {
          delete this.decisions[s];
        }.bind(this);
      }.bind(obj);
      obj.subscribe_reset = function (callback) {
        var dr = "reset|" + this.id++;
        this.resets[dr] = callback;
        return function () {
          delete this.resets[dr];
        }.bind(this);
      }.bind(obj);
      obj.subscribe_choice = function (channel, callback) {
        var s = channel + "|" + this.id++;
        this.choice_subs[s] = callback;
        return function () {
          delete this.choice_subs[s];
        }.bind(this);
      }.bind(obj);
      obj.onchoices = function (channel, choice) {
        var axe = [];
        for (var sub in obj.choice_subs) {
          if (sub.startsWith(channel + "|")) {
            if (!obj.choice_subs[sub](choice)) {
              axe.push(sub);
            }
          }
        }
        for (var k = 0; k < axe.length; k++) {
          delete obj.choice_subs[axe[k]];
        }
      };
      obj.ondecide = function (outstanding) {
        var axeReset = [];
        for (var dr in obj.resets) {
          var r = obj.resets[dr];
          if (!(r())) {
            axeReset.push(dr);
          }
        }
        for (var k = 0; k < axeReset.length; k++) {
          delete obj.resets[axeReset[k]];
        }
        for (var ch in obj.outstanding) {
          obj.outstanding[ch] = { options: [] };
        }
        var n = outstanding.length;
        for (var k = 0; k < n; k++) {
          var o = outstanding[k];
          obj.outstanding[o.channel] = o;
        }
        for (var ch in obj.outstanding) {
          var out = obj.outstanding[ch];
          var axe = [];
          for (var sub in obj.decisions) {
            if (sub.startsWith(ch + "|") || sub.startsWith("-|")) {
              if (!obj.decisions[sub](out, ch)) {
                axe.push(sub);
              }
            }
          }
          for (var k = 0; k < axe.length; k++) {
            delete obj.decisions[axe[k]];
          }
        }
      };
      connections[name] = obj;
      return obj;
    }
  };
  self.getConnectionByName = get_connection_obj;

  self.make = function () {
    return new AdamaTree();
  };

  // HELPER | subscribe the given 'sub' to changes within state for the given field named name
  var subscribe = function (state, name, sub) {
    var ss = self.pI(state, name);
    var s = ss[ss.current];
    if (s == null) {
      console.error(ss.current + " is not available; unable to subscribe.");
      return;
    }
    if ("@e" in s.delta) {
      s.delta["@e"].push(sub);
    } else {
      s.delta["@e"] = [sub];
    }
  };
  self.subscribe = subscribe;

  // HELPER | create fresh state
  var fresh = function (where) {
    return {
      tree: new AdamaTree(),
      delta: {},
      parent: null,
      path: null,
      where: where
    };
  };
  self.fresh = fresh;

  // HELPER: remove potentially sensitive information from an object
  var safe_filter = function(o) {
    if ('password' in o) {
      delete o['password'];
    }
    if ('one_time_password' in o) {
      delete o['one_time_password'];
    }
    return o;
  }

  // HELPER | create a new delta copy from the given specific state (i.e. either data or view)
  var new_delta_copy = function (ss) {
    if (ss == null) {
      return null;
    }
    var parent = null;
    if (ss.parent != null) {
      parent = new_delta_copy(ss.parent);
    }
    var new_delta = {};
    if (parent != null) {
      parent.delta[ss.path] = new_delta;
    }
    return { tree: ss.tree, parent: parent, delta: new_delta, path: ss.path };
  };
  self.makeDeltaCopy = new_delta_copy;

  // HELPER | construct a path to
  var path_to = function (ss, obj) {
    if (ss.parent != null) {
      var peel = {};
      peel[ss.path] = obj;
      return path_to(ss.parent, peel);
    } else {
      return obj;
    }
  };
  self.pathTo = path_to;

  // HELPER | get the root of the specific state (i.e. either the data's root or the view's root)
  var root_of = function (ss) {
    var x = ss;
    while (x.parent != null) {
      x = x.parent;
    }
    return x;
  };
  self.rootOf = root_of;

  // HELPER | remove all the children from the given DOM node
  var nuke = function (parent) {
    var last = parent.lastChild;
    while (last) {
      parent.removeChild(last);
      last = parent.lastChild;
    }
  };
  self.nuke = nuke;

  // HELPER: debounce the given functional when rapid function spread is expected
  var debounce = function (ms, foo) {
    var status = { inflight: false, timeout: null };
    status.inflight = false;
    return function () {
      if (!status.inflight) {
        status.inflight = true;
        status.timeout = window.setTimeout(function () {
          status.inflight = false;
          status.timeout = null;
          foo();
        }, ms /* ms */); // debounce the parameters
      }
    };
  };
  self.debounce = debounce;

  var delay = function(ms, foo) {
    var status = { avail: true, again:false, timeout: null };
    status.go = function() {
      if (status.avail) {
        status.avail = false;
        status.timeout = window.setTimeout(function() {
          status.avail = true;
          if (status.again) {
            go();
          }
        });
        foo();
      } else {
        status.again = true;
      }
    };
    return status.go;
  };
  self.delay = delay;

  // HELPER | prepare a free unsubscribe object
  var make_unsub = function () {
    return {
      __data: function () {
      }, __view: function () {
      }
    };
  };

  // HELPER | augment an existing object with unsubscribe data
  var add_unsub = function (obj) {
    obj.__data = function () {
    };
    obj.__view = function () {
    };
  };

  // HELPER | fire the unsubscribe calls within an object
  var fire_unsub = function (unsub) {
    unsub.__data();
    unsub.__view();
  };

  var isEmptyDelta = function(delta) {
    if (typeof(delta) == "function") {
      return false;
    } else if (Array.isArray(delta)) {
      for (var j = 0; j < delta.length; j++) {
        if (!isEmptyDelta(delta[j])) {
          return false;
        }
      }
    } else if (typeof(delta) == "object") {
      for (var k in delta) {
        if (!isEmptyDelta(delta[k])) {
          return false;
        }
      }
    }
    return true;
  }

  // HELPER | subscribe to state and populate unsubscribe object
  var subscribe_state = function (state, unsub) {
    //  && !isEmptyDelta(state.data)
    unsub.__data = function () {
    };
    unsub.__view = function () {
    };

    if (state.data != null) {
      var delta = root_of(state.data).delta;
      if (!isEmptyDelta(delta)) {
        unsub.__data = state.data.tree.subscribe(delta);
      }
    }
    //  && !isEmptyDelta(state.view)
    if (state.view != null) {
      var delta = root_of(state.view).delta;
      if (!isEmptyDelta(delta)) {
        unsub.__view = state.view.tree.subscribe(delta);
      }
    }
  };

  // RUNTIME | Switch to the view object
  self.pV = function (state) {
    return { service: state.service, data: state.data, view: state.view, current: "view" };
  };
  self.newStateViewOf = self.pV;

  // RUNTIME | Switch to the data object
  self.pD = function (state) {
    return { service: state.service, data: state.data, view: state.view, current: "data" };
  };
  self.newStateDataOf = self.pD;

  // RUNTIME | Switch to the root object / (Root)
  self.pR = function (state) {
    var next = { service: state.service, data: state.data, view: state.view, current: state.current };
    var prior = next[state.current];
    while (prior != null) {
      next[state.current] = prior;
      prior = prior.parent;
    }
    return next;
  };
  self.newStateRootOf = self.pR;

  // RUNTIME | ../ (Up)
  self.pU = function (state) {
    var next = { service: state.service, data: state.data, view: state.view, current: state.current };
    var prior = next[state.current];
    if (prior.parent != null) {
      next[state.current] = prior.parent;
    }
    return next;
  };
  self.newStateParentOf = self.pU;

  // RUNTIME | dive one level Into path1/path2/..../pathN
  self.pI = function (state, name) {
    var prior = state[state.current];
    if (prior == null) {
      console.error(state.current + " is not present");
      return state;
    }
    if (!(name in prior.delta)) {
      prior.delta[name] = {};
    }
    var next = { service: state.service, data: state.data, view: state.view, current: state.current };
    next[state.current] = {
      tree: prior.tree,
      delta: prior.delta[name],
      parent: prior,
      path: name
    };
    if (next.current == "data") {
      next.data.connection = prior.connection;
    }
    return next;
  };
  self.newStateDiveInto = self.pI;

  // RUNTIME | extend the given state with the view having a separate child
  self.pEV = function (state, name) {
    if (!(name in state.view.delta)) {
      state.view.delta[name] = {};
    }
    return {
      service: state.service,
      data: state.data,
      view: {
        tree: state.view.tree,
        delta: state.view.delta[name],
        parent: state.view,
        path: name
      },
      current: state.current
    };
  };
  self.newStateCreateViewChild = self.pEV;

  self.pIE = function (state, name, expandView) {
    var next = self.pI(state, name);
    if (expandView) {
      return self.pEV(next, name);
    } else {
      return next;
    }
  };

  var fork = function (priorState) {
    var state = {
      service: priorState.service,
      data: new_delta_copy(priorState.data),
      view: new_delta_copy(priorState.view),
      current: priorState.current
    };
    if (state.data != null) {
      state.data.connection = priorState.data.connection;
    }
    return state;
  };

  // RUNTIME | subscribe between the state and the object.
  // When the member field within state of name changes, copy that value into the obj and run recompute()
  self.Y = function (state, obj, name, recompute) {
    var sub = function (value) {
      obj[name] = value;
      recompute();
    };
    subscribe(state, name, sub);
  };

  self.Y2 = function (state, r, key, name, recompute) {
    var sub = function (value) {
      var obj = r._[key];
      obj[name] = value;
      recompute(obj);
      return true;
    };
    subscribe(state, name, sub);
  };

  // RUNTIME: build an rxobj
  self.RX = function (vars) {
    var o = {};
    o._ = {};
    for (var k = 0; k < vars.length; k++) {
      o._[vars[k]] = {};
    }
    o.__ = function() {};
    return o;
  };

  // RUNTIME | Just subscribee value to the object field of name (no-recompute)
  self.YS = function (state, obj, name) {
    var sub = function (value) {
      obj[name] = value;
      return true;
    };
    subscribe(state, name, sub);
  };

  // RUNTIME | "Text"
  self.T = function (tx) {
    return document.createTextNode(tx);
  };

  // convert a value into a boolean
  self.B = function(val) {
    if (val === true) {
      return true;
    }
    return val == "true";
  };

  // RUNTIME | <view-write name="" value="">
  self.VW = function(state, name, rxObj) {
    rxObj.__ = debounce(50, function() {
      var obj = {};
      var value = rxObj.value;
      if (typeof (value) == "function") {
        obj[name] = value();
      } else {
        obj[name] = value;
      }
      var delta = path_to(state.view, obj);
      state.view.tree.update(delta);
    });
  };

  // RUNTIME | <lookup path=...>
  self.L = function (state, name) {
    var dom = document.createTextNode("");
    var sub = function (value) {
      dom.nodeValue = value;
    };
    subscribe(state, name, sub);
    return dom;
  };

  // RUNTIME | <lookup path=... transform="$transform" />
  self.LT = function (state, name, transform) {
    var dom = document.createTextNode("");
    var sub = function (value) {
      if (value != null) {
        dom.nodeValue = transform(value);
      }
    };
    subscribe(state, name, sub);
    return dom;
  };

  self.Lh = function (state, name) {
    var dom = document.createElement("span");
    var sub = function (value) {
      if (value != null) {
        dom.innerHTML = value;
      }
    };
    subscribe(state, name, sub);
    return dom;
  };

  // RUNTIME | <lookup path=... transform="$transform" />
  self.LTdT = function (state, name, transform, freq) {
    var dom = document.createTextNode("");
    // create the nexus object
    var obj = {};
    obj.freq = freq;
    obj.value = "";

    // update will execute the transform on the value
    obj.update = function() {
      dom.nodeValue = transform(this.value);
    }.bind(obj);

    // subscription will sync the value
    var sub = function (value) {
      if (value != null) {
        this.value = value;
        this.update();
      }
    }.bind(obj);

    // refresh will re-execute the update (and thus the transform)
    obj.refresh = function() {
      this.update();
      if (document.body.contains(dom)) { // will run so long as the DOM contains the node we manipulated
        window.setTimeout(this.refresh, this.freq);
      }
    }.bind(obj);
    // kick off the auto transform
    window.setTimeout(obj.refresh, freq);
    subscribe(state, name, sub);
    return dom;
  };

  // RUNTIME | <tag>
  self.E = function (tag, ns) {
    if (ns == undefined || ns == null) {
      var dom = document.createElement(tag);
      if (tag == "select") {
        dom.addEventListener('change', function() { dom.rxvalue = dom.value; });
      }
      return dom;
    } else {
      var result = document.createElementNS(ns, tag);
      result.setAttribute("xmlns", ns);
      return result;
    }
  };

  self.CSt = function (parent, state, childMakerConnected, childMakerDisconnected) {
    var unsub = make_unsub();
    if (!state.data.connection) {
      return;
    }
    state.data.connection.connected(function(cs) {
      if (this.prior == cs) {
        return;
      }
      nuke(parent);
      fire_unsub(unsub);
      this.prior = cs;
      var newState = fork(state);
      if (cs) {
        childMakerConnected(parent, newState);
      } else {
        childMakerDisconnected(parent, newState);
      }
      subscribe_state(newState, unsub);
      return document.body.contains(parent);
    }.bind({prior:null}));
  };

  // RUNTIME | <local-storage-poll key=$key [ms=$ms]>
  self.LoStPo = function(parent, priorState, rxObj, childMakerFound, childMakerNotFound) {
    var unsub = make_unsub();
    var sm = {};
    sm.key = null;
    sm.ms = 5000;
    sm.last = "!";
    sm.p = function() {
      if (this.key == null) {
        return;
      }
      var value = localStorage.getItem(this.key);
      if (value == this.last) {
        return;
      }
      this.last = value;
      fire_unsub(unsub);
      nuke(parent);
      var tree = new AdamaTree();
      var state = {
        service: priorState.service,
        data: { connection: priorState.connection, tree: tree, delta: {}, parent: null, path: null },
        view: new_delta_copy(priorState.view),
        current: "data"
      };
      if (value == null) {
        childMakerNotFound(parent, state);
        subscribe_state(state, unsub);
      } else {
        childMakerFound(parent, state);
        subscribe_state(state, unsub);
        tree.update(tree.MakeDelta(JSON.parse(value)));
      }

    }.bind(sm);
    rxObj.__ = debounce(50, function () {
      if ('key' in rxObj) {
        this.key = rxObj.key;
      }
      if ('ms' in rxObj) {
        this.ms = parseInt(rxObj.ms);
        if (isNaN(this.ms)) {
          this.ms = 5000;
        }
      }
      this.p();
    }.bind(sm));

    sm.timeout = function() {
      if (document.body.contains(parent)) {
        this.p();
        window.setTimeout(this.timeout, this.ms);
      }
    }.bind(sm);
    window.setTimeout(sm.timeout, 50);
  };

  // RUNTIME | <document-get space="$space" key="$key" url="$path" search:x="..." ...>
  self.DcG = function(parent, priorState, rxObj, childMakerFetched, childMakerFailed) {
    // TODO
  };

  // RUNTIME | <domain-get url="path" search:x="..." ...>
  self.DG = function(parent, priorState, rxObj, childMakerFetched, childMakerFailed, delayLimit) {
    var unsub = make_unsub();
    var delayToUse = typeof(delayLimit) == "number" ? delayLimit : 250;
    rxObj.__ = debounce(50, delay(delayToUse, function () {
      if (!('url' in rxObj)) {
        return;
      }
      var args = [];
      var identity = null;
      for (var arg in rxObj) {
        var val = rxObj[arg];
        switch (arg) {
          case "__":
          case "_":
          case "url":
          case "redirect":
            break;
          case "identity":
            if (typeof(val) == "string") {
              identity = rxObj[arg];
            }
            break;
          default:
            args.push(arg + "=" + encodeURIComponent(val));
        }
      }
      var url = rxObj.url + (args.length > 0 ? "?" + args.join("&") : "");
      this.gen ++;
      var self = this;
      var xhttp = new XMLHttpRequest();
      xhttp.onreadystatechange = function () {
        if (this.xhttp.readyState == 4 && this.gen == self.gen) {
          fire_unsub(unsub);
          nuke(parent);
          var tree = new AdamaTree();
          var state = {
            service: priorState.service,
            data: { connection: priorState.connection, tree: tree, delta: {}, parent: null, path: null },
            view: new_delta_copy(priorState.view),
            current: "data"
          };

          if (this.xhttp.status == 200) {
            var type = this.xhttp.getResponseHeader("Content-Type");
            if (type == "application/json") {
              var toPush = tree.MakeDelta(JSON.parse(this.xhttp.responseText));
              childMakerFetched(parent, state);
              subscribe_state(state, unsub);
              tree.update(toPush);
            } else {
              childMakerFailed(parent, state);
              subscribe_state(state, unsub);
            }
          } else {
            childMakerFailed(parent, state);
            subscribe_state(state, unsub);
          }
        }
      }.bind({gen:self.gen, xhttp:xhttp});
      xhttp.open("GET",  self.base + "/~d" + url, true);
      if (identity != null) {
        xhttp.setRequestHeader("Authorization", "Bearer " + identity);
      }
      xhttp.withCredentials = true;
      xhttp.send();
    }.bind({url:"", gen:0, base:self.protocol + "//" + self.host})));
    rxObj.__();
  };

  // RUNTIME | <pick name=...>
  self.P = function (parent, priorState, rxObj, childMakerConnected, childMakerDisconnected, keepOpen) {
    var unsub = make_unsub();
    rxObj.__ = function () {
      if (!('name' in rxObj)) {
        return;
      }
      if (this.name == rxObj.name) {
        return;
      }
      var co = get_connection_obj(rxObj.name);
      this.name = rxObj.name;
      co.connected(function (cs) {
        if (this.rendered && keepOpen) {
          return;
        }
        nuke(parent);
        fire_unsub(unsub);
        var state = {
          service: priorState.service,
          data: { connection: co, tree: co.tree, delta: {}, parent: null, path: null },
          view: new_delta_copy(priorState.view),
          current: "data"
        };
        if (cs) {
          this.rendered = true;
          childMakerConnected(parent, state);
        } else {
          childMakerDisconnected(parent, state);
        }
        subscribe_state(state, unsub);
        // TODO: return false to unsub
        return true;
      }.bind(this));
    }.bind({ name: "", rendered:false });
  };

  // RUNTIME | <template name="...">
  self.TP = function (name, foo) {
    templates[name] = foo;
  };

  // RUNTIME | <tag rx:template=$name>
  self.UT = function (parent, state, name, child_maker) {
    var foo = templates[name];
    if (typeof(foo) == "function") {
      foo(parent, state, child_maker);
    } else {
      console.error("failed to find template: " + name);
    }
  };

  // RUNTIME | <tag rx:switch=path ..>
  self.SW = function (parent, priorState, name, childrenMaker) {
    var swst = { prior: null };
    add_unsub(swst);
    var sub = function (value) {
      if (value == this.prior) {
        return;
      }
      this.prior = value;
      fire_unsub(this);
      nuke(parent);
      var state = fork(priorState);
      childrenMaker(parent, self.pD(state), "" + value);
      subscribe_state(state, this);
    }.bind(swst);
    subscribe(priorState, name, sub);
  };

  // RUNTIME | <tag rx:iterate=path ...>
  self.IT = function (parentDom, state, name, expandView, maker) {
    var it_state = self.pIE(state, name, expandView);
    var domByKey = {};
    var viewUnSubByKey = {};
    var kill = function () {
      for (var key in viewUnSubByKey) {
        fire_unsub(viewUnSubByKey[key]);
        delete viewUnSubByKey[key];
      }
      for (var key in domByKey) {
        delete domByKey[key];
      }
    };

    var sub = {
      "+": function (key) {
        var new_state = fork(self.pIE(it_state, key, expandView));
        var unsub = make_unsub();
        var dom = maker(new_state);
        domByKey[key] = dom;
        viewUnSubByKey[key] = unsub;
        parentDom.append(dom);
        var item_delta = new_state.data.delta;
        new_state.data.delta = {};
        subscribe_state(new_state, unsub);
        if (expandView) {
          state.view.tree.update(path_to(new_state.view, {"$key": key}));
        };
        return item_delta;
      },
      "-": function (key) {
        if (key in domByKey) {
          parentDom.removeChild(domByKey[key]);
          delete domByKey[key];
        }
        if (key in viewUnSubByKey) {
          fire_unsub(viewUnSubByKey[key]);
          delete viewUnSubByKey[key];
        }
      },
      "~": function (ord) {
        var valueToSet = false;
        // the values are filling up AFTEr the value was set,
        // so detect the absence of a value along with the existence of a value
        if (!parentDom.value && parentDom.rxvalue != "") {
          valueToSet = parentDom.rxvalue;
          // this value, we want to restore once
        }
        nuke(parentDom);
        if (ord == null) {
          kill();
          return;
        }
        for (var k = 0; k < ord.length; k++) {
          parentDom.append(domByKey[ord[k]]);
        }
        if (valueToSet) {
          parentDom.value = valueToSet;
        }
        parentDom.dispatchEvent(new Event("ordered"));
      }
    };
    subscribe(state, name, sub);
  };

  // RUNTIME: <tag ... rx:monitor="state-path" rx:rise="commands..." rx:fall="commands..."
  self.MN = function (dom, state, name, skipFirst, delay) {
    var sub = function (value) {
      window.setTimeout(function() {
        var n = 0;
        try {
          n = typeof (value) == 'number' ? value : parseInt(value);
        } catch (failedParsingN) {
        }
        if (this.first) {
          this.at = n;
          this.first = false;
          return;
        }
        if (this.at < n) {
          var e = new Event("rise");
          dom.dispatchEvent(e);
        } else if (this.at > n) {
          var e = new Event("fall");
          dom.dispatchEvent(e);
        }
        this.at = n;
      }.bind(this), this.delay);
    }.bind({at:-1, first:skipFirst, delay:delay ? delay : 10});
    subscribe(state, name, sub);
  };

  // RUNTIME: <tag ... rx:repeat=$>
  self.RP = function (parentDom, state, name, expandView, maker) {
    var it_state = self.pIE(state, "$" + name, expandView);
    var sub = function (value) {
      var n = 0;
      try {
        n = typeof (value) == 'number' ? value : parseInt(value);
      } catch (failedParsingN) {
      }
      while (this.at < n - 1) {
        this.at ++;
        this.track ++;
        var new_state = fork(self.pIE(it_state, this.track + "", expandView));
        var unsub = make_unsub();
        var dom = maker(self.pD(new_state));
        this.items[this.at] = {unsub:unsub, dom:dom};
        parentDom.append(dom);
        if (expandView) {
          state.view.tree.update(path_to(new_state.view, {"$index": this.track}));
        }
        subscribe_state(new_state, unsub);
      }
      while (this.at >= n && this.at >= 0) {
        fire_unsub(this.items[this.at].unsub);
        parentDom.removeChild(this.items[this.at].dom);
        this.items[this.at] = null;
        delete this.items[this.at];
        this.at--;
      }
    }.bind({items:[], at:-1, track:0});
    subscribe(state, name, sub);
  }
  var find = function (state, channel, key, value) {
    if (channel in state["data"].connection.outstanding) {
      var arr = state["data"].connection.outstanding[channel].options;
      for (var k = 0; k < arr.length; k++) {
        var s = arr[k];
        if (key in s && s[key] == value) {
          return s;
        }
      }
      return null;
    } else {
      return null;
    }
  };

  var customs = {};

  self.PRCUAC = function (name, foo) {
    customs[name] = foo;
  };

  self.exCC = function (dom, type, state, customCommandName) {
    reg_event(state, dom, type, function () {
      if (customCommandName in customs) {
        customs[customCommandName](state, dom);
      }
    });
  };

  // custom event on forms; rx:success="..."
  var fire_success = function (form) {
    form.dispatchEvent(new Event("success"));
    form.dispatchEvent(new Event("submitted"));
  };
  self.fire_success = fire_success;

  // custom event on forms; rx:failed="..."
  var fire_failure = function (dom, msg) {
    var e = new Event("failure");
    e.message = msg;
    console.error(msg);
    dom.dispatchEvent(e);
  };
  self.fire_failure = fire_failure;

  self.aCC = function (form, state, customCommandName) {
    var signal = function (msg) {
      if (signal == null) {
        fire_success(form);
      } else {
        fire_failure(form, msg);
      }
    };
    form.addEventListener('submit', function (evt) {
      if (customCommandName in customs) {
        evt.preventDefault();
        var obj = get_form(form, {});
        customs[customCommandName](obj, state, signal, self);
        fire_success(form);
      } else {
        fire_failure(form, "Failed to find '" + customCommandName + "'");
      }
    }, true);
  };

  var make_choice_array = function (state, channel) {
    var choices = state.data.connection.choices;
    if (!(channel in choices)) {
      choices[channel] = {};
    }
    var choice = choices[channel];
    var arr = [];
    for (var id in choice) {
      arr.push(choice[id]);
    }
    return arr;
  }

  self.exFIN = function (dom, type, state, channel) {
    reg_event(state, dom, type, function () {
      var arr = make_choice_array(state, channel);
      var clear = function () {
        delete state.data.connection.choices[channel];
        state.data.connection.onchoices(channel, {});
      };
      state.data.connection.ptr.send(channel, arr, {
        failure: function (reason) {
          clear();
          console.log("failed:" + reason);
        },
        success: function (payload) {
          clear();
          console.log("Success|" + payload.seq);
        }
      });
    });
  };

  self.FIN = function (parent, priorState, channel, shouldBe, _expand, makerTrue, makerFalse) {
    var finalize = {
      owner: parent,
      shown: false
    };
    add_unsub(finalize);

    var change = function (show) {
      fire_unsub(finalize);
      nuke(parent);
      var state = fork(priorState);
      if (show === shouldBe) {
        makerTrue(parent, state);
      } else {
        makerFalse(parent, state);
      }
      subscribe_state(state, finalize);
    };

    finalize.update = function () {
      var out = priorState.data.connection.outstanding[channel];
      if (!(out)) return;
      var arr = make_choice_array(priorState, channel);
      var result = ('min' in out && 'max' in out) ? (out.min <= arr.length && arr.length <= out.max) : true;
      if (finalize.eval != result) {
        finalize.eval = result;
        change(finalize.eval);
      }
    };

    priorState.data.connection.subscribe_choice(channel, function () {
      finalize.update();
      // TODO: return whether or not this needs to be axed
      return true;
    });
  };

  var form_of = function(dom) {
    var next = dom;
    while (next != null) {
      if (next.tagName.toUpperCase() == "FORM") {
        return next;
      }
      next = next.parentElement;
    }
    return null;
  }

  var reg_event = function (state, dom, type, runnable) {
    // TODO: connected/disconnected as special events to consider
    if (type == "check" || type == "uncheck") {
      dom.addEventListener("change", function() {
        if (this.type == "check" && dom.checked) {
          runnable();
        } else if (this.type == "uncheck" && !dom.checked) {
          runnable();
        }
      }.bind({type:type}));
    } else if (type == "load") {
      window.setTimeout(runnable, 1);
    } else if (type=="submit" || type=="submitted") {
      window.setTimeout(function() {
        var form = form_of(dom);
        if (form) {
          form.addEventListener(type, function () {
            if (document.body.contains(dom)) {
              runnable();
            }
          });
        }
      }, 1);
    } else if (type.startsWith("delay:")) {
      // delay will run after $delayTimeMS IF the dom element is still visible to the document
      var delayTimeMS = parseInt(type.substring(6));
      window.setTimeout(function () {
        if (document.body.contains(dom)) {
          runnable();
        }
      }, delayTimeMS);
    } else if (type == "settle") {
      state.data.tree.add_on_settle(function() {
        // if the DOM has been added to the body, then run the code
        if (document.body.contains(dom)) {
          runnable();
          return true;
        }
        return false;
      });
    } else if (type == "settle-once") {
      state.data.tree.add_on_settle(function() {
        // if the DOM has been added to the body, then run the code
        if (document.body.contains(dom)) {
          runnable();
        }
        return false;
      });
    } else {
      if (type == "click") {
        dom.addEventListener(type, function(event) {
          var fire = true;
          try {
            var el = event.target;
            while (el && fire) {
              if (el.hasAttribute('prevent:click')) {
                fire = false;
              }
              el = el.parentElement;
            }
          } catch (ex) {
          }
          if (fire) {
            runnable();
          }
        });
      } else {
        dom.addEventListener(type, runnable);
      }
    }
  }



  // RUNTIME: <tag .. rx:event="... set:name=value ...">
  self.bS = function (dom, state, name, value) {
    if (typeof (value) == "function") {
      return {
        run: function() {}, // do nothing, this is a simple merge
        merge: function() {
          var obj = {};
          obj[name] = value();
          var delta = path_to(state.view, obj);
          return delta;
        }
      }
    } else {
      var obj = {};
      obj[name] = value;
      var delta = path_to(state.view, obj);
      return {
        run: function() {}, // do nothing, this is a simple merge
        merge: function() { return delta; }
      }
    }
  };

  var merge_delta = function(a, b) {
    if (typeof(a) == "object" && typeof(b) == "object") {
      var result = {};
      for (var k in a) {
        if (k in b) {
          result[k] = merge_delta(a[k], b[k]);
        } else {
          result[k] = a[k];
        }
      }
      for(var k in b) {
        if (!(k in a)) {
          result[k] = b[k];
        }
      }
      return result;
    }
    return a;
  };
  self.MD = merge_delta;

  self.onB = function(dom, type, state, arr) {
    reg_event(state, dom, type, function() {
      var sum = {};
      for (var k = 0; k < arr.length; k++) {
        var cmd = arr[k];
        cmd.run();
        var toMerge = cmd.merge()
        sum = merge_delta(sum, toMerge);
      }
      state.view.tree.update(sum);
    });
  };

  // choose
  self.exCH = function (dom, type, state, name, channel, key) {
    var decide = { value: null };
    reg_event(state, dom, type, function () {
      var result = find(state, channel, key, decide.value);
      if (result != null) {
        var choices = state.data.connection.choices;
        if (!(channel in choices)) {
          choices[channel] = {};
        }
        var choice = choices[channel];
        var valKey = result[key];
        if (valKey in choice) {
          delete choice[valKey];
        } else {
          choice[valKey] = result;
        }
        state.data.connection.onchoices(channel, choice);
      }
    });
    subscribe(state, name, function (value) {
      decide.value = value;
    });
  };

  // decide
  self.exD = function (dom, type, state, name, channel, key) {
    var decide = { value: null };
    reg_event(state, dom, type, function () {
      var result = find(state, channel, key, decide.value);
      if (result != null) {
        let start = performance.now();
        state.data.connection.ptr.send(channel, result, {
          failure: function (reason) {

          },
          success: function (payload) {
            console.log("Success|" + payload.seq + ";latency=" + (performance.now() - start));
          }
        });
      }
    });
    subscribe(state, name, function (value) {
      decide.value = value;
    });
  };

  // RUNTIME: <tag .. rx:event="... fire:channel ..." ...>
  self.onFR = function (state, dom, type, channel) {
    reg_event(state, dom, type, function () {
      state.data.connection.ptr.send(channel, {}, {
        success: function () {
          console.log("SENT '" + channel + "'");
        },
        failure: function (reason) {
          console.log("FAILED TO FIRE '" + channel + "' DUE:" + reason); ''
        }
      });
    });
  }

  // RUNTIME: <tag .. rx:event="... force-auth=name,token ...">
  self.onFORCE_AUTH = function (dom, type, identityName, identity) {
    reg_event(null, dom, type, function () {
      identities[identityName] = identity;
      localStorage.setItem("identity_" + identityName, identity);
    });
  };

  // RUNTIME: <tag .. rx:event="... set:name=value ...">
  self.onS = function (dom, type, state, name, value) {
    reg_event(state, dom, type, function () {
      var obj = {};
      if (typeof (value) == "function") {
        obj[name] = value();
      } else {
        obj[name] = value;
      }
      var delta = path_to(state.view, obj);
      state[state.current].tree.update(delta);
    });
  };

  // RUNTIME: <tag .. rx:event="... te:name ...">
  self.onTM = function (dom, type, state, name, ox, oy) {
    reg_event(state, dom, type, function (event) {
      var obj = {};
      obj[name] = {
        x:event.clientX + window.scrollX + ox,
        y:event.clientY + window.scrollY + oy
      };
      var delta = path_to(state.view, obj);
      state[state.current].tree.update(delta);
    });
  };

  // RUNTIME: <tag .. rx:event="... te:name ...">
  self.onTE = function (dom, type, state, name) {
    reg_event(state, dom, type, function (event) {
      var obj = {};
      obj[name] = event.message;
      if (event.message) {
        var delta = path_to(state.view, obj);
        state[state.current].tree.update(delta);
      }
    });
  };

  // RUNTIME: <tag .. rx:event="... reset ...">
  self.oRST = function (dom, type, state) {
    reg_event(state, dom, type, function (event) {
      var f = form_of(dom);
      if (f != null) {
        f.reset();
      }
    });
  };

  // RUNTIME: <tag .. rx:event="... reset ...">
  self.oRLD = function (dom, type, state) {
    reg_event(state, dom, type, function (event) {
      location.reload();
    });
  };

  // RUNTIME: <tag .. rx:event="... submit ...">
  self.oSBMT = function (dom, type, state) {
    reg_event(state, dom, type, function (event) {
      var f = form_of(dom);
      if (f != null) {
        var e = new SubmitEvent('submit', {
          'bubbles'    : true,
          'cancelable' : true
        });
        f.dispatchEvent(e);
      }
    });
  };

  self.oNK = function (dom, type, state) {
    reg_event(state, dom, type, function (event) {
      var cur = dom;
      while (cur.tagName.toUpperCase() != "NUCLEAR") {
        cur = cur.parentElement;
      }
      if (cur != null) {
        nuke(cur);
      }
    });
  };


  self.oUCK = function (dom, type, state) {
    reg_event(state, dom, type, function (event) {
      dom.checked = false;
    });
  };

  // RUNTIME: <tag .. rx:event="... goto:uri ...">
  self.onGO = function (dom, type, state, value) {
    reg_event(state, dom, type, function () {
      var uri = (typeof (value) == "function") ? value() : value;
      self.goto(uri, false);
    });
  };

  // RUNTIME: <tag .. rx:event="... toggle:name ...">
  self.onT = function (dom, type, state, name) {
    var captured = { value: false };
    reg_event(state, dom, type, function () {
      var obj = {};
      obj[name] = !captured.value;
      var delta = path_to(state.view, obj);
      state.view.tree.update(delta);
    });
    subscribe(state, name, function (value) {
      captured.value = value == true;
    });
  };

  var order_toggle = function(prior, val) {
    var parts = prior.split(",");
    var prefix = '';
    var next = [""];
    for (var k = 0; k < parts.length; k++) {
      if (parts[k] == "") {
        continue;
      } else if (val == parts[k]) {
        prefix = '-';
      } else if ("-" + val ==parts[k]) {
        prefix = '';
      } else {
        next.push(parts[k]);
      }
    }
    next[0] = prefix + val;
    return next.join(",");
  }

  // RUNTIME: <tag .. rx:event="... order-toggle:name=val ...">
  self.onOT = function (dom, type, state, name, val) {
    var captured = { value: "" };
    reg_event(state, dom, type, function () {
      var obj = {};
      obj[name] = order_toggle(captured.value, val);
      var delta = path_to(state.view, obj);
      state[state.current].tree.update(delta);
    });
    subscribe(state, name, function (value) {
      captured.value = value;
    });
  };

  // RUNTIME: <tag .. rx:event="... delta:name=diff" ...">
  self.onD = function (dom, type, state, name, diff) {
    var captured = { value: 0 };
    reg_event(state, dom, type, function () {
      var obj = {};
      obj[name] = captured.value + diff;
      var delta = path_to(state.view, obj);
      state[state.current].tree.update(delta);
    });
    subscribe(state, name, function (value) {
      if (typeof (value) == "number") {
        captured.value = value;
      } else {
        var val = parseFloat(value);
        if (!isNaN(val)) {
          captured.value = val;
        }
      }
    });
  };
  self.CSEN = function (parent, priorState, evalState, channel, key, name, shouldBe, _expand, makerTrue, makerFalse) {
    var chosen = {
      value: "",
      owner: parent,
      shown: false,
      eval: null
    };
    add_unsub(chosen);
    var change = function (show) {
      fire_unsub(chosen);
      nuke(parent);
      var state = fork(priorState);
      if (show === shouldBe) {
        makerTrue(parent, state);
      } else {
        makerFalse(parent, state);
      }
      subscribe_state(state, chosen);
    };

    chosen.update = function () {
      var out = priorState.data.connection.outstanding[channel];
      if (!(out)) return;

      var choices = priorState.data.connection.choices;
      if (!(channel in choices)) {
        choices[channel] = {};
      }
      var choice = choices[channel];
      var result = chosen.value in choice;
      if (chosen.eval != result) {
        chosen.eval = result;
        change(chosen.eval);
      }
    };

    priorState.data.connection.subscribe_choice(channel, function () {
      chosen.update();
      return true;
    });

    subscribe(evalState, name, function (value) {
      chosen.value = value;
      chosen.update();
    });
  };

  self.DE = function (parent, priorState, evalState, channel, key, name, shouldBe, _expand, makerTrue, makerFalse) {
    var decide = {
      value: "",
      owner: parent,
      shown: false,
      eval: null
    };
    add_unsub(decide);
    var change = function (show) {
      fire_unsub(decide);
      nuke(parent);
      var state = fork(priorState);
      if (show === shouldBe) {
        makerTrue(parent, state);
      } else {
        makerFalse(parent, state);
      }
      subscribe_state(state, decide);
    };

    decide.update = function () {
      var result = find(priorState, channel, key, decide.value) != null;
      if (decide.eval != result) {
        decide.eval = result;
        change(decide.eval);
      }
    };

    priorState.data.connection.subscribe(channel, function () {
      decide.update();
      // TODO: return whether or not this needs to be axed
      return true;
    });

    subscribe(evalState, name, function (value) {
      decide.value = value;
      decide.update();
    });
  };

  var commonIf = function(parent, originalState, shouldBe, expandView, makerTrue, makerFalse, forceHiding) {
    var unsub = make_unsub();
    if (forceHiding) {
      parent.style.display = "none";
    }
    return function (value) {
      var show = (value ? true : false) === shouldBe;
      if (this.shown == show) {
        return;
      }
      this.shown = show;
      nuke(parent);
      fire_unsub(unsub);
      var next = self.pD(fork(originalState));
      if (show) {
        if (forceHiding) {
          parent.style.display = "";
        }
        makerTrue(parent, next);
      } else {
        if (forceHiding) {
          parent.style.display = "none";
        } else {
          makerFalse(parent, next);
        }
      }
      subscribe_state(next, unsub);
    }.bind({ shown: 'no' });
  }

  // RUNTIME | rx:if / rx:ifnot = "path1=path2"
  self.IFeq = function (parent, originalState, queryStateLeft, nameLeft, queryStateRight, nameRight, shouldBe, expandView, makerTrue, makerFalse, forceHiding) {
    var common = {left:false, right:true};
    common.s = commonIf(parent, originalState, shouldBe, expandView, makerTrue, makerFalse, forceHiding);
    common.k = debounce(5, function() {
      this.s(this.left == this.right);
    }.bind(common));
    subscribe(queryStateLeft, nameLeft, function (left) {
      this.left = left;
      this.k();
    }.bind(common))
    subscribe(queryStateRight, nameRight, function (right) {
      this.right = right;
      this.k();
    }.bind(common));
  };

  /** the above speaks to a deep pattern. We could take any expression and then linearize it into reactive parts, and then assemble a statement statement on the fly. We should look into using the condition language for this */

  // RUNTIME | rx:if / rx:ifnot = "path"
  self.IF = function (parent, originalState, queryState, name, shouldBe, expandView, makerTrue, makerFalse, forceHiding) {
    subscribe(queryState, name, commonIf(parent, originalState, shouldBe, expandView, makerTrue, makerFalse, forceHiding));
  };

  /// RUNTIME | rx:action=copy:path
  self.aCP = function (form, state, name) {
    form.addEventListener('submit', function (evt) {
      evt.preventDefault();
      var obj = get_form(form, {});
      if (name != "." && name != "") {
        var no = {};
        no[name] = obj;
        obj = no;
      }
      var delta = path_to(state.view, obj);
      state.view.tree.update(delta);
      fire_success(form);
    }, true);
  };

  self.VSP = function(state, vars) {
    var sm = {};
    sm.vars = vars;
    sm.last = {};
    sm.params = "";
    sm.mode = "replace";
    sm.ping = debounce(500, function() {
      var args = [];
      for (var k = 0; k < this.vars.length; k++) {
        var arg = this.vars[k];
        if (arg in this.last) {
          args.push(arg + "=" + this.last[arg]);
        }
      }
      var result = "?" +args.join("&");
      if (result != "?" && window.location.search !== result) {
        var obj = {};
        obj.viewer_search_query = result;
        var delta = path_to(state.view, obj);
        state.view.tree.update(delta);
        try {
          window.history.replaceState({}, "", fixHref(window.location.pathname + result));
        } catch(failedToReplaceState) {

        }
      }
    }.bind(sm));
    state.view.tree.subscribe(function(s) {
      this.last = s;
      this.ping();
    }.bind(sm));
  };

  self.F = function(o,k) {
    if (k in o) {
      return o[k];
    }
    return "";
  };

  // RUNTIME | <input ... rx:sync=path ...>
  self.SY = function (el, state, name, ms) {
    var type = ("type" in el) ? el.type.toUpperCase() : "text";
    var signal = function (value) {
      var obj = {};
      obj[name] = value;
      var delta = path_to(state.view, obj);
      state.view.tree.update(delta);
      if (this.dedupe != value) {
        this.dedupe = value;
        el.dispatchEvent(new Event("aftersync"));
      }
    }.bind({dedupe:""});
    if (type == "CHECKBOX") {
      el.addEventListener('change', debounce(ms, function () {
        signal(el.checked ? true : false);
      }));
      window.setTimeout(function () {
        signal(el.checked ? true : false);
      }, 1);
    } else if (type == "RADIO") {
      el.addEventListener('change', debounce(ms, function () {
        if (el.checked) {
          signal(el.value);
        }
      }));
    } else {
      var f = debounce(ms, function () {
        signal(el.value);
      });
      el.addEventListener('change', f);
      el.addEventListener('keyup', f)
      el.onkeyup = el.onchange;
      window.setTimeout(function () {
        signal(el.value);
      }, 5);
    }
  };

  var firstOccur = function(a, b) {
    if (a > 0 && b > 0) {
      return Math.min(a, b);
    }
    if (a > 0) {
      return a;
    } else {
      return b;
    }
  };
  // HELPER | extract all the inputs from the given element and build an object
  var build_obj = function (el, objToInsertInto, passwords, isRoot) {
    if (el.tagName.toUpperCase() == "FORM" && !isRoot) {
      // don't care about nested forms
      return;
    }
    var upperTag = el.tagName.toUpperCase();
    var justSet = upperTag == "TEXTAREA" || upperTag == "SELECT";
    var isInputBox = upperTag == "INPUT";
    var isFieldSet = upperTag == "FIELDSET";
    var isPull = upperTag == "PULLVALUE";
    var hasName = "name" in el;
    var insertAt = objToInsertInto;
    // the apply function is how we inject the value into the object
    var apply = function (val) { };

    var name = "";
    if (hasName && (justSet || isInputBox || isFieldSet || isPull)) {
      name = el.name;
      if (name == "") { // this is a special name for things that don't get picked up
        return;
      }
      var kDotOrSlash = firstOccur(name.indexOf('.'), name.indexOf('/'));
      while (kDotOrSlash > 0) {
        var par = name.substring(0, kDotOrSlash);
        if (!(par in insertAt)) {
          insertAt[par] = {};
        }
        insertAt = insertAt[par];
        name = name.substring(kDotOrSlash + 1);
        kDotOrSlash = firstOccur(name.indexOf('.'), name.indexOf('/'));
      }

      if (name.endsWith("+")) {
        // here, we push the value into the array
        name = name.substring(0, name.length - 1);
        apply = function (v) {
          if (!(name in insertAt)) {
            insertAt[name] = [v];
          } else {
            insertAt[name].push(v);
          }
        };
      } else {
        // here we set the object
        apply = function (v) {
          insertAt[name] = v;
        };
      }
    }

    if (isPull) {
      apply(el.pull());
      return;
    }

    if (justSet) {
      apply(el.value);
    } else if (isFieldSet) {
      var nextObject = {};
      if ("children" in el) {
        var arr = el.children;
        var n = arr.length;
        for (var k = 0; k < n; k++) {
          var ch = el.children[k];
          build_obj(ch, nextObject, passwords, false);
        }
      }
      apply(nextObject);
    } else if (isInputBox) {
      var type = ("type" in el) ? el.type.toUpperCase() : "TEXT";
      if (type == "SUBMIT" || type == "RESET") return;
      if ((type == "PASSWORD" || name == "password" || name == "confirm-password" || name == "new_password" || name == "confirm-new_password")) {
        passwords[name] = el.value;
        return;
      }
      if (type == "CHECKBOX") {
        apply(el.checked ? true : false);
      } else if (type == "RADIO") {
        if (el.checked) {
          apply(el.value);
        }
      } else {
        apply(el.value);
      }
    } else {
      if ("children" in el) {
        var arr = el.children;
        var n = arr.length;
        for (var k = 0; k < n; k++) {
          var ch = el.children[k];
          build_obj(ch, objToInsertInto, passwords, false);
        }
      }
    }
  };

  self.BuildFormObject = function(form) {
    var obj = {};
    build_obj(form, obj, false, true);
    return obj;
  };

  // HELPER | return an object of all the inputs of the given form element
  var get_form = function (form, passwords) {
    var obj = {};
    build_obj(form, obj, passwords, true);
    return obj;
  };

  // <... rx:wrap=const >
  var customs_components = {};
  self.provideCustomComponent = function(name, foo) {
    customs_components[name] = foo;
  };

  // RUNTIME | <tag ... rx:custom="?" port:$name=$output ... >...</tag> | assemble a writer object
  self.WX = function(instructions) {
    var writer = {};
    for (var k = 0; k + 2 < instructions.length; k++) {
      var obj = {};
      writer[instructions[k]] = function(value) {
        var s = this;
        s.w[s.k] = value;
        s.t.update(s.d);
      }.bind({
        d:path_to(instructions[1].view, obj),
        w: obj,
        k: instructions[2],
        t: instructions[1].view.tree
      });
    }
    return writer;
  };
  self.C = function (dom, state, name, rxobj, writer, childMakerWithCase) {
    var obj = {
      dom: dom,
      state: state,
      inputs: rxobj,
      outputs: writer,
      maker: childMakerWithCase,
      framework: self
    };
    if (name in customs_components) {
      customs_components[name](obj);
    } else {
      console.error("failed to find custom component: '" + name + "'");
    }
  };

  var wrappers = {};
  var wrappers_onload = {}; // TODO: study if this can be removed
  self.PRWP = function (name, foo) {
    wrappers[name] = foo;
    if (name in wrappers_onload) {
      var toload = wrappers_onload[name];
      for (var k = 0; k < toload.length; k++) {
        toload[k]();
      }
    }
  };

  // <... rx:wrap=const >
  self.WP = function (dom, state, name, rxobj, childMakerWithCase) {
    if (name in wrappers) {
      wrappers[name](dom, state, rxobj, childMakerWithCase, self);
    } else {
      var loader = function () {
        wrappers[name](dom, state, rxobj, childMakerWithCase, self);
      };
      if (name in wrappers_onload) {
        wrappers_onload[name].push(loader);
      } else {
        wrappers_onload[name] = [loader];
      }
    }
  };

  var behaviors = {};
  self.defineBehavior = function(name, behavior) {
    if (typeof(behavior) == 'function') {
      behaviors[name] = behavior;
    } else {
      throw new Error("defining behavior '" + name + "' failed due to not a function")
    }
  };

  // RUNTIME | rx:behavior=$name
  self.BHV = function(dom, name) {
    if (name in behaviors) {
      behaviors[name];
    } else {
      console.log("couldn't find behavior:" + name);
    }
  };

  // RUNTIME | register the page for the uri to the given foo().
  self.PG = function (uri, foo) {
    var head = router;
    for (var k = 0; k < uri.length; k++) {
      var part = uri[k];
      if (!(part in head)) {
        head[part] = {};
      }
      head = head[part];
    }
    head["@"] = foo;
  };

  var route = function (parts, at, head, view) {
    if (at < parts.length) {
      if ("number" in head) {
        var neck = head["number"];
        var val = parseFloat(parts[at]);
        if (!isNaN(val)) {
          for (var branch in neck) {
            view[branch] = val;
            var candidate = route(parts, at + 1, neck[branch], view);
            if (candidate !== null) {
              return candidate;
            }
            delete view[branch];
          }
        }
      }
      if ("text" in head) {
        var neck = head["text"];
        var val = parts[at];
        for (var branch in neck) {
          view[branch] = val;
          var candidate = route(parts, at + 1, neck[branch], view);
          if (candidate !== null) {
            return candidate;
          }
          delete view[branch];
        }
      }
      if ("fixed" in head) {
        var neck = head["fixed"];
        for (var branch in neck) {
          if (branch == parts[at]) {
            var candidate = route(parts, at + 1, neck[branch], view);
            if (candidate !== null) {
              return candidate;
            }
          }
        }
      }
    } else {
      if ("@" in head) {
        return head["@"];
      }
    }
    return null;
  };


  self.resume_uri = "/";
  self.gates = [];
  /** RUNTIME | <gate-exit guard="read" set=""> */
  self.IG = function(stateRead, read, stateWrite, write) {
    var sm = {
      value: false,
      run: function() {
        if (this.value) {
          var obj = {};
          obj[write] = true;
          var delta = path_to(stateWrite.view, obj);
          stateWrite.view.tree.update(delta);
          return true;
        }
        return false;
      },
    };
    subscribe(stateRead, read, function(value) {
      sm.value = value;
    });
    self.gates.push(sm);
  };
  self.goto = function (uri, now) {
    self.resume_uri = uri;
    var blocked = false;
    for (var k = 0; k < self.gates.length; k++) {
      if (self.gates[k].run()) {
        blocked = true;
      }
    }
    if (blocked) {
      return;
    }
    if (now) {
      self.goto_now(uri);
    } else {
      window.setTimeout(function () {
        self.goto_now(uri);
      }, 1);
    }
  };
  // RUNTIME: <tag ... rx:event="... resume ...">..
  self.bR = function() {
    return {
      run: function() {
        self.gates = [];
        self.goto(self.resume_uri, false);
      },
      merge: function() {}
    };
  };
  self.goto_now = function(uri) {
    if (uri.startsWith("/")) {
      self.run(document.body, uri, true);
    } else {
      window.location.href = fixHref(uri);
    }
  };

  var urlBase64ToUint8Array = function(base64) {
    var rawData = window.atob(base64);
    var outputArray = new Uint8Array(rawData.length);
    for (var i = 0; i < rawData.length; ++i) {
      outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
  }

  self.currentPushEvent = function() {};

  var getWebPushStatus = function() {
    var result = localStorage.getItem("webpush_status");
    if (result && typeof(result) == "string") {
      return result;
    }
    return "unknown";
  }

  var setPushStatus = function(status) {
    localStorage.setItem("webpush_status", status);
    self.currentPushEvent();
  };
  self.setPushStatus = setPushStatus;

  var setPushEvent = function(foo) {
    self.currentPushEvent = foo;
  };

  var setupSubscription = function (registration, vapidPublicKey, identity, identityName, version) {
    connection.bump("wps");
    var pushKeyLocal = "push_endpoint_" + identityName;
    registration.pushManager
    .getSubscription().then(async function (subscription) {
        if (subscription) {
          return subscription;
        }
        const convertedVapidKey = urlBase64ToUint8Array(vapidPublicKey);
        return registration.pushManager.subscribe({
          userVisibleOnly: true,
          applicationServerKey: convertedVapidKey
        });
    }).then(function (subscription) {
      // make sure we have an endpoint saved related to the right version
      connection.IdentityHash(identity, {
        success: function(result) {
          if (localStorage.getItem("push_worker_version") != version || localStorage.getItem("last_identity_used") != result.identityHash) {
            localStorage.removeItem(pushKeyLocal);
            localStorage.setItem("push_worker_version", version);
            localStorage.setItem("last_identity_used", result.identityHash);
          }
          var sub = subscription.toJSON();
          var val = localStorage.getItem(pushKeyLocal);
          if (val && val == sub.endpoint) {
            connection.bump("wpa");
            setPushStatus("success");
            return;
          }
          sub['@method'] = 'webpush';
          sub['@time'] = new Date().getTime();
          var device = {};
          device.mode = 'web'
          if (window && window.navigator && window.navigator.userAgent) {
            device.ua = window.navigator.userAgent;
          }
          connection.PushRegister(identity, self.domain, sub, device, {
            success: function() {
              localStorage.setItem(pushKeyLocal, sub.endpoint);
              localStorage.setItem("webpush_status", "success");
              setPushStatus("success");
            },
            failure: function() {
              connection.bump("wpf");
            }
          });
        },
        failure: function() {
          connection.bump("wpf");
        }
      });
    }, function(err) {
      setPushStatus("failed");
      localStorage.removeItem(pushKeyLocal);
      localStorage.removeItem("last_identity_used");
      localStorage.removeItem("push_worker_version");
      connection.bump("wpd");
      connection.log("pushsetup", "" + err);
    });
  }

  var setupPush = function(path, vapidPublicKey, identity, identityName, version) {
    if (navigator.serviceWorker) {
      try {
        navigator.serviceWorker.getRegistrations().then(function (registrations) {
          var found = false;
          for (let registration of registrations) {
            if (registration.active) {
              if (!registration.active.scriptURL.endsWith(path)) {
                registration.unregister();
              } else {
                found = true;
                setupSubscription(registration, vapidPublicKey, identity, identityName, version);
              }
            }
          }
          if (!found) {
            navigator.serviceWorker.register(path);
            navigator.serviceWorker.ready.then(function (registration) {
              setupSubscription(registration, vapidPublicKey, identity, identityName, version);
            });
          }
        }, function(ex) {
          connection.bump("wpi3");
          connection.log("worker-find", "" + ex);
        });
      } catch (ex) {
        console.error("failed to register service worker", ex);
        setPushStatus("impossible");
        connection.bump("wpi2");
        connection.log("worker-install", "" + ex);
      }
    } else {
      console.log("no service worker available");
      setPushStatus("impossible");
      connection.bump("wpi1");
    }
  }

  self.worker = function(identityName, path, version) {
    try {
      afterHaveIdentity(identityName, function(identity) {
        connection.DomainGetVapidPublicKey(identity, self.domain, {
          success: function(response) {
            setupPush(path, response.publicKey, identity, identityName, version);
          },
          failure: function (reason) {
            console.error("failed to get public-key:" + reason);
          }
        })
      });
    } catch (ex) {
      console.error("failed to initialize worker", ex);
    }
  };

  self.init = function () {
    self.run(document.body, fixPath(window.location.pathname + window.location.search + window.location.hash), false);
    window.onpopstate = function (p) {
      self.run(document.body, fixPath(window.location.pathname + window.location.search + window.location.hash), false);
    };
  };

  var currentMessageHandlers = {};
  var removeAllMessageHandlers = function() {
    for (var k in currentMessageHandlers) {
      delete currentMessageHandlers[k];
    }
  };

  self.registerMessageHandler = function(channel, handler) {
    if (channel in currentMessageHandlers) {
      currentMessageHandlers[channel].push(handler);
    } else {
      currentMessageHandlers[channel] = [handler];
    }
  };

  var routeMessage = function(event) {
    try {
      if (!('data' in event)) {
        return;
      }
      if (typeof (event.data) != 'object') {
        return;
      }
      if (!('channel' in event.data)) {
        return;
      }
      var channel = event.data.channel;
      var list = currentMessageHandlers[channel];
      if (list !== null) {
        var n = list.length;
        for (var k = 0; k < n; k++) {
          list[k](event.data, event);
        }
      }
    } catch (wellMaybeTheErrorDoesntBelongToMe) {
      // :shrug:
    }
  };

  if (window.addEventListener) {
    window.addEventListener("message", routeMessage, false);
  } else if (window.attachEvent) {
    window.attachEvent("onmessage", routeMessage, false);
  }

  self.currentViewerId = 0;
  // API | Run the page in the given place
  self.run = function (where, rawPath, push) {
    removeAllMessageHandlers();
    var path = rawPath;
    while (path.endsWith("/") && path != "/") {
      path = path.substring(0, path.length - 1);
    }
    for (conKey in connections) {
      connections[conKey].tree.nuke();
      connections[conKey].nuke();
    }
    var search = "";
    var kQuestion = path.indexOf('?');
    if (kQuestion > 0) {
      search = path.substring(kQuestion);
      path = path.substring(0, kQuestion);
    }
    var parts = (path.startsWith("/") ? path.substring(1) : path).split("/");
    var init = {};
    self.currentViewerId++;
    init.viewer_current_page_id = self.currentViewerId;
    try {
      init.viewer_timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
      init.viewer_language = navigator.language;
    } catch (browserBad) {
      // we just don't see the viewstate if we don't have these available
    }
    init.viewer_search_query = "";
    init.viewer_web_push_status = getWebPushStatus();
    if (search != "?") {
      init.viewer_search_query = search;
      const params = new URLSearchParams(search.substring(1));
      var kIt = params.keys();
      var p = kIt.next();
      while (!p.done) {
        init[p.value] = params.get(p.value);
        p = kIt.next();
      }
    }
    var foo = route(parts, 0, router, init);
    nuke(where);
    if (foo != null) {
      window.scrollTo(0, 0);
      var state = { service: connection, data: null, view: fresh(where), current: "view" };
      self.__current = state;

      const viewstate = localStorage.getItem('rxviewstate');
      if (null !== viewstate) {
        localStorage.removeItem('rxviewstate');
        init = Object.assign({}, init, JSON.parse(viewstate));
      }

      state.view.init = init;
      self.gates = [];
      self.resume_uri = path;
      foo(where, state);
      state.view.tree.subscribe(state.view.delta);
      state.view.tree.update(init);
      if (push) {
        window.history.pushState({merge:false}, "", fixHref(path) + search);
      }
      setPushEvent(function() {
        state.view.tree.update({ viewer_web_push_status: getWebPushStatus()});
      });
    } else {
      if (path != "/404") {
        self.run(where, "/404");
      } else {
        // default 404
      }
    }
    where.appendChild(connectionMonitorDom);
  };

  var identities = {};

  self.SIGNOUT = function () {
    identities = {};
    for (var identityName in identities) {
      localStorage.removeItem("identity_" + identityName);
    }
    localStorage.removeItem("identity_default");
    var axe = [];
    for (var cid in connections) {
      var co = connections[cid];
      if (co.ptr != null) {
        co.ptr.end({ success: function () { }, failure: function () { } });
      }
      axe.push(cid);
    }
    for (var k = 0; k < axe.length; k++) {
      delete connections[axe[k]];
    }
  };

  self.GOOGLE_SIGN_ON = function (accessToken) {
    connection.InitConvertGoogleUser(accessToken, {
      success: function (payload) {
        identities["default"] = payload.identity;
        localStorage.setItem("identity_default", payload.identity);
        self.goto("/", false);
      },
      failure: function (reason) {
        console.log("Google failure: " + reason);
      }
    });
  };

  /** RUNTIME | redirect to a page with state */
  self.aRDp = function (state, puller) {
    return function () {
      var uri = puller(state.view.init);
      return uri;
    };
  };

  /** RUNTIME | redirect to a static page */
  self.aRDz = function (raw) {
    return function () {
      return raw;
    }
  };

  var identityEvents = {};
  var afterHaveIdentity = function(identityName, callback) {
    identityEvents[identityName] = callback;
  };
  self.afterHaveIdentity = afterHaveIdentity;

  /** for custom elements to learn of the identity */
  self.ID = function (identityName, redirectToFunc) {
    if (identityName === true) {
      identityName = "default";
    }
    // IF identity contains dots
    var identity = null;
    var cleanup = function () {
    };

    var val = localStorage.getItem("identity_" + identityName);
    if (val) {
      identities[identityName] = val;
    }

    if (identityName.startsWith("direct:")) {
      // Use, as is
      identity = identityName.substring(7);
    } else if (identityName in identities) {
      identity = identities[identityName];
      cleanup = function () {
        delete identities[identityName];
        localStorage.removeItem("identity_" + identityName);
        self.goto(redirectToFunc(), false);
      };
    } else {
      // whatever page we are, needs to die which means we need to nuke everything!
      window.setTimeout(function () {
        self.goto(redirectToFunc(), false);
      }, 10);
      return { abort: true };
    }
    if (identityName in identityEvents) {
      identityEvents[identityName](identity);
      delete identityEvents[identityName];
    }
    return { abort: false, cleanup: cleanup, identity: identity };
  };

  /* For custom callbacks to redirect and invalidate ID failures */
  self.FIDCL = function (callback, lookup) {
    return {
      success: function (result) {
        callback.success(result);
      },
      next: function (item) {
        callback.next(item);
      },
      complete: function () {
        callback.complete();
      },
      failure: function (reason) {
        callback.failure(reason);
        if (reason == 403403 || reason == 403500 || reason == 184333) {
          lookup.cleanup();
        }
      }
    };
  };

  var customDataSources = {};

  /** provide custom data */
  self.PRCUDA = function (name, foo) {
    customDataSources[name] = foo;
  };

  // <customdata src=src (parameter:x=y)* >
  self.CUDA = function (parent, priorState, src, rxobj, redirectTo, childMaker) {
    var unsub = make_unsub();
    rxobj.__ = debounce(10, function () {
      fire_unsub(unsub);
      nuke(parent);
      var customTree = false;
      if (src in customDataSources) {
        var cons = customDataSources[src];
        if (typeof (cons) == "function") {
          customTree = cons(rxobj, priorState, redirectTo, self);
        }
      }
      if (!customTree) {
        customTree = new AdamaTree();
      }
      var state = {
        service: priorState.service,
        data: { connection: priorState.connection, tree: customTree, delta: {}, parent: null, path: null },
        view: new_delta_copy(priorState.view),
        current: "data"
      };
      childMaker(state);
      subscribe_state(state, unsub);
    });
  };

  // <title value="..." />
  self.ST = function (rxobj) {
    if (rxobj.value) {
      document.title = rxobj.value;
    }
    rxobj.__ = debounce(5, function () {
      if (rxobj.value) {
        document.title = rxobj.value;
      }
    });
  };

  // <viewsync>
  self.VSy = function (parent, priorState, avail, notavail) {
    var unsub = make_unsub();
    priorState.data.connection.subscribe_sync(function (synced) {
      nuke(parent);
      fire_unsub(unsub);
      var state = fork(priorState);
      if (synced) {
        avail(parent, state);
      } else {
        notavail(parent, state);
      }
      subscribe_state(state, unsub);
      // TODO: return false to unsub
      return true;
    });
  };

  var bind_responder = function (co, cleanup, retry_sm) {
    retry_sm.unexpected_errors = 0;
    retry_sm.backoff = 1;
    return {
      next: function (payload) {
        co.debugger(payload);
        co.set_connected(true);
        retry_sm.unexpected_errors = 0;
        retry_sm.backoff = 1;
        if ("data" in payload.delta) {
          co.tree.update(payload.delta.data);
        }
        if ("outstanding" in payload.delta) {
          co.ondecide(payload.delta.outstanding);
        }
        if ('viewstate' in payload.delta) {
          co.viewstatetree.update(payload.delta.viewstate);
        }
        if ('log' in payload.delta) {
          console.log(payload.delta.log);
        }
        if ('reload' in payload.delta) {
          if (payload.delta.reload['preserve-view']) {
            localStorage.setItem('rxviewstate', JSON.stringify(co.viewstatetree.raw()));
          }
          window.location.reload();
        }
        if ('viewport' in payload.delta && 'message' in payload.delta) {
          var viewport = payload.delta.viewport;
          if (viewport in co.handlers) {
            co.handlers[viewport](payload.delta.message, co);
          }
        }
        if ('view-state-filter' in payload.delta) {
          var filter = {};
          var keys = payload.delta['view-state-filter'];
          for (var k = 0; k < keys.length; k++) {
            filter[keys[k]] = true;
          }
          co.filter = filter;
          co.has_filter = true;
        }
        if ('goto' in payload.delta) {
          self.goto(payload.delta.goto, false);
        }
      },
      complete: function () {
        // shouldn't realistically happen TODO: validate <- truth
        co.set_connected(false);
        co.ptr = null;
      },
      failure: function (reason) {
        // register the failure
        co.set_connected(false);
        co.ptr = null;
        if (reason == 403403 || reason == 403500 || reason == 184333) {
          cleanup();
          return;
        }
        // is it an expected error that will happen a bunch?
        var expected_error = reason == 999;
        // do we not know about an error that happens a bunch? if so, then let's at least try again a number of finite times.
        var blind_retry = retry_sm.unexpected_errors < 16;
        if (blind_retry) {
          retry_sm.unexpected_errors++;
        }
        // retry on either case
        if (expected_error || blind_retry) {
          retry_sm.backoff = Math.ceil(Math.min(retry_sm.backoff + Math.random() * retry_sm.backoff + (blind_retry ? 125 : 1), 2500));
          if (expected_error) {
            // the expected error is a connection event, so we protect the service and jump back-off right to near max over a nice spread
            retry_sm.backoff = Math.min(1250, retry_sm.backoff) + Math.random() * 1250;
          }
          if (expected_error) {
            console.log("connect-loss|retrying... (" + retry_sm.backoff + "); reason=" + reason + (blind_retry ? " [blind]" : ""));
          } else {
            console.log("connect-failure|retrying... (" + retry_sm.backoff + "); reason=" + reason + (blind_retry ? " [blind]" : ""));
          }
          // TODO: need a different feedback mechanism (maybe a number under the wifi icon?)
          window.setTimeout(function() {
            retry_sm.go();
          }, retry_sm.backoff);
        } else {
          console.log("gave up connection;" + co.label + "; reason=" + reason);
        }
      }
    }
  };

  var setup_co = function(desired, unsub, co, state) {
    co.viewstatetree = state.view.tree;
    var sync_tree = function () {
      // let's copy out the tree from the viewstate
      var new_tree = safe_filter(state.view.tree.copy());

      // if we have got a filter from the server, then let's filter
      if (co.has_filter) {
        // reconstruct the new view based on the intersection of the filter and the new state
        var filtered = {};
        for (var k in new_tree) {
          if (co.filter[k]) {
            filtered[k] = new_tree[k];
          }
        }
        // assume the new tree
        new_tree = filtered;
      }
      // if the clone (i.e. stringified version) is the same as what we last sent, then don't send anything
      var prior_clone = JSON.stringify(new_tree);
      if (prior_clone == co.viewstate_clone) {
        return;
      }
      co.viewstate_sent = false;
      co.vsseq++;
      co.sync();
      var synced = function () {
        if (co.vsseq == this.bound) {
          co.viewstate_sent = true;
          co.sync();
        }
      }.bind({ bound: co.vsseq });
      co.viewstate_clone = JSON.stringify(new_tree);
      co.ptr.update(new_tree, { success: synced, failure: synced });
    };
    var bind = function (sendNow) {
      unsub.view = state.view.tree.subscribe(function () {
        if (co.ptr == null) {
          return;
        }
        sync_tree();
      });
      if (sendNow) {
        sync_tree();
      }
    };
    if (co.ptr != null && co.bound == desired) {
      bind(true);
      return false;
    }
    if (co.ptr != null) {
      co.ptr.end({ success: function () { }, failure: function () { } });
      co.ptr = null;
    }
    return bind;
  };

  // <connection billing ...>
  self.BCONNECT = function (state, rxobj) {
    var unsub = {
      view: function () {
      }
    };
    rxobj.__ = debounce(5, function () {
      if (!('name' in rxobj)) {
        return;
      }
      var idLookup = self.ID(rxobj.identity, function () { return rxobj.redirect; });
      if (idLookup.abort) {
        return;
      }
      var co = get_connection_obj(rxobj.name);
      var desired = ":billing:" + idLookup.identity;
      var bind = setup_co(desired, unsub, co, state);
      if (bind === false) {
        return;
      }
      co.bound = desired;
      var identity = idLookup.identity;
      var cleanup = idLookup.cleanup;
      unsub.view();
      co.identity = identity;
      co.handlers = {};
      co.viewstate_sent = true;
      co.label = "Billing [ " + rxobj.name + "]";
      co.via_billing = true;
      co.via_domain = false;
      var retry_sm = {};
      retry_sm.responder = bind_responder(co, cleanup, retry_sm);
      retry_sm.go = function() {
        // bias to nothing new
        co.viewstate_clone = "{}";
        co.ptr = connection.BillingConnectionCreate(identity, retry_sm.responder);
      };
      retry_sm.go();
      co.tree.update({});
      bind(false);
    });
  };

  // CHECK FOR OVERRIDE SIGNAL HERE
  self.domain = location.hostname;
  self.host = location.host;
  self.protocol = location.protocol;
  self.is_mobile = false;

  var hostOverride = localStorage.getItem("mdo_host"); // multi domain endpoint choice
  if (hostOverride != null) {
    self.host = hostOverride;
    self.domain = hostOverride.split(":")[0];
  }

  self.url_prefix = self.protocol + "//" + self.host;


  self.mobileInit = function(defaultOverrideDomain) {
    self.domain = defaultOverrideDomain;
    self.host = defaultOverrideDomain;
    self.protocol = "https:";
    connection.protocol = "https:";
    self.is_mobile = true;
  };
  self.mobileInitMultiDomain = function(start, betaPrefix, prodPrefix) {
    self.md_betaPrefix = betaPrefix;
    self.md_prodPrefix = prodPrefix;
    self.protocol = "https:";
    connection.protocol = "https:";
    self.is_mobile = true;
    self.run(document.body, fixPath(start), false);
    window.onpopstate = function (p) {
      self.run(document.body, fixPath(start), false);
    };
  };
  var getOrCreateManifests = function() {
    var db = localStorage.getItem("__domain_manifests");
    if (db == null) {
      db = {seq:0, manifests:[]};
    } else {
      db = JSON.parse(db);
    }
    return db;
  };
  var saveManifests = function(db) {
    localStorage.setItem("__domain_manifests", JSON.stringify(db));
  };
  self.registerManifest = function(url) {
    var add = function(manifest) {
      var db = getOrCreateManifests();
      manifest.source = url;
      manifest.id = db.seq;
      db.seq++;
      for (var k = 0; k < db.manifests.length; k++) {
        if (db.manifests[k].source == url) {
          db.manifests[k] = manifest;
          saveManifests(db);
          return;
        }
      }
      db.manifests.push(manifest);
      saveManifests(db);
    };
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
      if (this.readyState == 4) {
        if (this.status == 200) {
          add(JSON.parse(this.responseText));
        }
      }
    };
    xhttp.open("GET", url, true);
    xhttp.send();
  };
  // RUNTIME(mobile) | rx:action="manifest-add:"
  self.MD_a = function(dom, type, value) {
    reg_event(null, dom, type, function() {
      self.registerManifest(typeof(value) == 'function' ? value() : value);
    });
  };
  // RUNTIME(mobile) | rx:action="manifest-use:"
  self.MD_u = function(dom, type, value) {
    reg_event(null, dom, type, function() {
      var id = parseInt(typeof(value) == 'function' ? value() : value);
      var db = getOrCreateManifests();
      for (var k = 0; k < db.manifests.length; k++) {
        var manifest = db.manifests[k];
        if (manifest.id == id) {
          localStorage.setItem("mdo_host", new URL(db.manifests[k].source).host);
          if (manifest.beta) {
            localStorage.setItem("mdec", "beta");
          } else {
            localStorage.setItem("mdec", "prod");
          }
          window.location.href = "/";
        }
      }
    });
  };
  self.mobileLoad = function(url) {
    var parsed = new URL(url);
    localStorage.setItem("mdo_host", parsed.host);
    if (parsed.host.endsWith(self.md_betaPrefix)) {
      localStorage.setItem("mdec", "beta");
    } else {
      localStorage.setItem("mdec", "prod");
    }
    window.location.href = parsed.path;
  };
  // RUNTIME(mobile) | rx:action="manifest-del:"
  self.MD_d = function(dom, type, value) {
    reg_event(null, dom, type, function() {
      var id = parseInt(typeof(value) == 'function' ? value() : value);
      var db = getOrCreateManifests();
      var new_manifests = [];
      for (var k = 0; k < db.manifests.length; k++) {
        if (db.manifests[k].id != id) {
          new_manifests.push(db.manifests[k]);
        }
      }
      db.manifests = new_manifests;
      saveManifests(db);
    });
  };

  // <connection use-domain ...>
  self.DCONNECT = function (state, rxobj) {
    var unsub = {
      view: function () {
      }
    };
    rxobj.__ = debounce(5, function () {
      if (!('name' in rxobj)) {
        return;
      }
      var idLookup = self.ID(rxobj.identity, function () { return rxobj.redirect; });
      if (idLookup.abort) {
        return;
      }
      var co = get_connection_obj(rxobj.name);
      var domain = self.domain;
      var desired = domain + idLookup.identity;
      var bind = setup_co(desired, unsub, co, state);
      if (bind === false) {
        return;
      }
      co.bound = desired;

      var identity = idLookup.identity;
      var cleanup = idLookup.cleanup;
      unsub.view();
      co.identity = identity;
      co.handlers = {};
      co.viewstate_sent = true;
      co.label = "Domain:" + domain + " [ " + rxobj.name + "]";
      co.via_domain = true;
      co.via_billing = false;
      co.domain = domain;
      var retry_sm = {};
      retry_sm.responder = bind_responder(co, cleanup, retry_sm);
      retry_sm.go = function() {
        // bias to nothing new
        co.viewstate_clone = "{}";
        co.ptr = connection.ConnectionCreateViaDomain(identity, domain, safe_filter(state.view.tree.copy()), retry_sm.responder);
      };
      retry_sm.go();
      co.tree.update({});
      bind(false);
    });
  };

  // <connection ...>
  self.CONNECT = function (state, rxobj) {
    var unsub = {
      view: function () {
      }
    };
    rxobj.__ = debounce(5, function () {
      var valid = 'key' in rxobj && 'space' in rxobj && 'name' in rxobj;
      if (!valid) {
        return;
      }
      var idLookup = self.ID(rxobj.identity, function () { return rxobj.redirect; });
      if (idLookup.abort) {
        return;
      }
      var co = get_connection_obj(rxobj.name);
      var desired = rxobj.space + "/" + rxobj.key + "/" + idLookup.identity; // TODO: need some kind of hashing function here
      var bind = setup_co(desired, unsub, co, state);
      if (bind === false) {
        return;
      }
      co.bound = desired;

      var identity = idLookup.identity;
      var cleanup = idLookup.cleanup;
      unsub.view();
      co.space = rxobj.space;
      co.key = rxobj.key;
      co.identity = identity;
      co.handlers = {};
      co.viewstate_sent = true;
      co.label = co.space + "/" + co.key + " [ " + rxobj.name + "]";
      co.via_domain = false;
      co.via_billing = false;
      var retry_sm = {};
      retry_sm.responder = bind_responder(co, cleanup, retry_sm);
      retry_sm.go = function() {
        // bias to nothing new
        co.viewstate_clone = "{}";
        co.ptr = connection.ConnectionCreate(identity, rxobj.space, rxobj.key, safe_filter(state.view.tree.copy()), retry_sm.responder);
      };
      retry_sm.go();
      co.tree.update({});
      bind(false);
    });
  };

  self.INTERNAL = function (priorState) {
    return {
      service: priorState.service,
      data: { connection: null, tree: new AdamaTree(), delta: {}, parent: null, path: null },
      view: new_delta_copy(priorState.view),
      current: "data"
    };
  };

  var recall_email = function (el) {
    if (el.tagName.toUpperCase() == "INPUT") {
      if ("email" == el.type && "email" == el.name) {
        el.value = localStorage.getItem("email_remember");
      }
    } else {
      if ("children" in el) {
        var arr = el.children;
        var n = arr.length;
        for (var k = 0; k < n; k++) {
          recall_email(el.children[k]);
        }
      }
    }
  };

  self.aUP = function (form, state, identityName, rxobj) {
    var idLookup = self.ID(identityName, function () { return rxobj.rx_forward; }); // TODO: make rxvar
    if (idLookup.abort) {
      return;
    }
    form.action = "https://" + Adama.Production + "/~upload";
    form.method = "post";
    form.enctype = "multipart/form-data";
    var identityInput = document.createElement("input");
    identityInput.type = "hidden";
    identityInput.name = "identity";
    identityInput.value = idLookup.identity;
    form.appendChild(identityInput);
    var iframeTarget = document.createElement("iframe");
    iframeTarget.name = "UPLOAD_" + Math.random();
    iframeTarget.width = "1";
    iframeTarget.height = "1";

    form.appendChild(iframeTarget);
    form.target = iframeTarget.name;
  };

  self.aDUP = function (form, state, identityName, rxobj) {
    var idLookup = self.ID(identityName, function () { return rxobj.rx_forward; }); // TODO: make rxvar
    if (idLookup.abort) {
      return;
    }
    form.action = self.protocol + "//" + self.host + "/~upload";
    form.method = "post";
    form.enctype = "multipart/form-data";
    {
      var identityInput = document.createElement("input");
      identityInput.type = "hidden";
      identityInput.name = "identity";
      identityInput.value = idLookup.identity;
      form.appendChild(identityInput);
    }
    {
      var domainInput = document.createElement("input");
      domainInput.type = "hidden";
      domainInput.name = "domain";
      domainInput.value = self.domain;
      form.appendChild(domainInput);
    }
    var iframeTarget = document.createElement("iframe");
    iframeTarget.name = "UPLOAD_" + Math.random();
    iframeTarget.width = "1";
    iframeTarget.height = "1";
    form.appendChild(iframeTarget);
    form.target = iframeTarget.name;
  };

  var stash_identity = function(name, identity, connection) {
    var req = {};
    req['name'] = name;
    req['identity'] = identity;
    req['max-age'] = 5529600;

    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
      if (this.readyState == 4) {
        if (this.status == 200) {
          connection.IdentityStash(identity, name, {
            success: function() {
              // THIS HAS DRAGONS UNTIL A FULL PRODUCTION DEPLOYMENT
              // localStorage.setItem("identity_" + name, "cookie:" + name);
            },
            failure: function(xyz) {

            }
          });
        }
      }
    };
    xhttp.open("PUT", self.protocol + "//" + self.host + "/~stash/" + Date.now(), true);
    xhttp.withCredentials = true;
    xhttp.send(JSON.stringify(req));
  };

  var get_form_and_pw = function(form) {
    var passwords = {};
    var req = get_form(form, passwords);
    if ('password' in passwords) {
      req['password'] = passwords['password'];
    }
    if ('new_password' in passwords) {
      req['new_password'] = passwords['new_password'];
    }
    return req;
  }

  var commonAuthorize = function(form, state, identityName, rxobj, nextStep) {
    rxobj.__ = function () { };
    form.addEventListener('submit', function (evt) {
      evt.preventDefault();
      var sendReq = function(req) {
        var sm = {attempts:0};
        sm.responder = {
          success: function (payload) {
            identities[identityName] = payload.identity;
            localStorage.setItem("identity_" + identityName, payload.identity);
            stash_identity(identityName, payload.identity, connection);
            // TODO: blow away connections
            self.goto(rxobj.rx_forward, false);
            fire_success(form);
          },
          failure: function (reason) {
            fire_failure(form, "Failed signing into document:" + reason);
          }
        };
        nextStep(req, sm.responder);
      }
      var passwords = {};
      var req = get_form(form, passwords);
      if ('password' in passwords) {
        req['password'] = passwords['password'];
      }
      if ('new_password' in passwords) {
        connection.DocumentsHashPassword(passwords['new_password'], {
          success: function (hashed_pw) {
            req['new_password'] = hashed_pw.passwordHash;
            sendReq(req);
          },
          failed: function () {
            fire_failure(form, "Failed to hash password");
          }
        });
      } else {
        sendReq(req);
      }
    }, true);
  }

  // rx:action=document:authorize
  self.aDOC = function (form, state, identityName, rxobj) {
    commonAuthorize(form, state, identityName, rxobj, function(req, responder) {
      connection.DocumentAuthorization(req.space, req.key, req, responder);
    });
  };

  // rx:action=domain:authorize
  self.aDOM = function (form, state, identityName, rxobj) {
    commonAuthorize(form, state, identityName, rxobj, function(req, responder) {
      connection.DocumentAuthorizationDomain(self.domain, req, responder);
    });
  };

  // rx:event=toggle-password
  self.onPWT = function(dom, state, type) {
    reg_event(state, dom, type, function (event) {
      var cur = dom;
      while (cur.tagName.toUpperCase() != "PASSWORD-HOLDER") {
        cur = cur.parentElement;
      }
      var hunt = function(node) {
        if (node.tagName.toUpperCase() == "INPUT" && (node.name == "password" || node.name == "confirm-password" || name == "new_password" || name == "confirm-new_password")) {
          if (node.type == "password") {
            node.type = "text"
          } else {
            node.type = "password"
          }
        }
        if ('children' in node) {
          var ch = node.children;
          var n = ch.length;
          for (var k = 0; k < n; k++) {
            hunt(ch[k]);
          }
        }
      };
      if (cur != null) {
        hunt(cur);
      }
    });
  };

  // RUNTIME | rx:action=domain:sign-in (to deprecate)
  self.adDSO = function (form, state, identityName, rxobj) {
    rxobj.__ = function () { };
    form.addEventListener('submit', function (evt) {
      evt.preventDefault();
      var req = get_form_and_pw(form);
      var sm = {attempts:0};
      sm.responder = {
        success: function (payload) {
          identities[identityName] = payload.identity;
          localStorage.setItem("identity_" + identityName, payload.identity);
          stash_identity(identityName, payload.identity, connection);
          // TODO: blow away connections
          self.goto(rxobj.rx_forward, false);
          fire_success(form);
        },
        failure: function (reason) {
          fire_failure(form, "Failed signing into document:" + reason);
        }
      };
      connection.DocumentAuthorizeDomain(self.domain, req.username, req.password, sm.responder);
    }, true);
  }

  // RUNTIME | rx:action=document:sign-in (to deprecate)
  self.aDSO = function (form, state, identityName, rxobj) { // DEPRECATED
    rxobj.__ = function () { };
    form.addEventListener('submit', function (evt) {
      evt.preventDefault();
      var req = get_form_and_pw(form);
      connection.DocumentAuthorize(req.space, req.key, req.username, req.password, {
        success: function (payload) {
          identities[identityName] = payload.identity;
          localStorage.setItem("identity_" + identityName, payload.identity);
          // TODO: blow away connections
          self.goto(rxobj.rx_forward, false);
          fire_success(form);
        },
        failure: function (reason) {
          fire_failure(form, "Failed signing into document:" + reason);
        }
      });
    }, true);
  };

  // RUNTIME | rx:action=domain:sign-in-reset (to deprecate)
  self.adDSOr = function (form, state, identityName, rxobj) { // DEPRECATED
    rxobj.__ = function () { };
    form.addEventListener('submit', function (evt) {
      evt.preventDefault();
      var req = get_form_and_pw(form);
      connection.DocumentAuthorizeDomainWithReset(self.domain, req.username, req.password, req.new_password,{
        success: function (payload) {
          identities[identityName] = payload.identity;
          localStorage.setItem("identity_" + identityName, payload.identity);
          // TODO: blow away connections
          self.goto(rxobj.rx_forward, false);
          fire_success(form);
        },
        failure: function (reason) {
          fire_failure(form, "Failed signing into document:" + reason);
        }
      });
    }, true);
  }

  // RUNTIME | rx:action=document:sign-in-reset (to deprecate)
  self.aDSOr = function (form, state, identityName, rxobj) { // DEPRECATED
    rxobj.__ = function () { };
    form.addEventListener('submit', function (evt) {
      evt.preventDefault();
      var req = get_form_and_pw(form);
      connection.DocumentAuthorizeWithReset(req.space, req.key, req.username, req.password, req.new_password,{
        success: function (payload) {
          identities[identityName] = payload.identity;
          localStorage.setItem("identity_" + identityName, payload.identity);
          // TODO: blow away connections
          self.goto(rxobj.rx_forward, false);
          fire_success(form);
        },
        failure: function (reason) {
          fire_failure(form, "Failed signing into document:" + reason);
        }
      }, true);
    });
  };

  self.autogenid = 1;
  self.X = function() {
    self.autogenid++;
    return 'x' + self.autogenid;
  };

  var transforms = {};
  transforms['principal.agent'] = function(x) { return x.agent; };
  transforms['principal.authority'] = function(x) { return x.authority; };
  transforms['trim'] = function(x) { return ('' + x).trim(); };
  transforms['upper'] = function(x) { return ('' + x).toUpperCase(); };
  transforms['lower'] = function(x) { return ('' + x).toLowerCase(); };
  transforms['is_empty_str'] = function(x) { return x == ""; };
  transforms['is_not_empty_str'] = function(x) { return x != ""; };
  transforms['jsonify'] = function(x) { return JSON.stringify(x); };
  transforms['time_now'] = function(x) { return Date.now() + ""; };
  transforms['size_bytes'] = function(xraw) {
    var x = 0.0;
    if (typeof(xraw) == 'number') {
      x = xraw;
    } else {
      try {
        x = parseFloat(xraw);
      } catch (ex) {

      }
    }
    var ival = Math.floor(x);
    if (ival < 1024) {
      return ival + " B";
    }
    ival /= 1024;
    if (ival < 1024) {
      return Math.round(ival * 10) / 10.0 + " KB";
    }
    ival /= 1024;
    if (ival < 1024) {
      return Math.round(ival * 10) / 10.0 + " MB";
    }
    ival /= 1024;
    if (ival < 1024) {
      return Math.round(ival * 10) / 10.0 + " GB";
    }
  }
  transforms['vulgar_fraction'] = function(xraw) {
    var x = 0.0;
    if (typeof(xraw) == 'number') {
      x = xraw;
    } else {
      try {
        x = parseFloat(xraw);
      } catch (ex) {

      }
    }
    var ival = Math.floor(x);
    var change = x - ival;
    var suffix = "";
    if (change <= 0.0625) {
      // 0
    } else if (change <= 0.1875) {
      suffix = "⅛";
    } else if (change <= 0.3125) {
      suffix = "¼";
    } else if (change <= 0.4375) {
      suffix = "⅜";
    } else if (change <= 0.5625) {
      suffix = "½";
    } else if (change <= 0.6875) {
      suffix = "⅝";
    } else if (change <= 0.8125) {
      suffix = "¾"; // 3/4
    } else if (change <= 0.9375) {
      suffix = "⅞";
    } // else 1
    return ival + suffix;
  }
  transforms['time_ago'] = function(dt, format) {
    // only transform strings
    if (typeof (dt) == "string") {
      // let's strip out everything after the brackets
      var s = dt;
      var k = s.indexOf('[');
      if (k >= 0) {
        s = s.substring(0, k);
      }
      var d = Date.parse(s);
      if (Number.isNaN(d)) {
        // it isn't a number, so we didn't get a date stamp; default to the input
        return dt;
      }
      // we are interested in producing a string like "X minutes ago", so start with seconds
      var delta = (Date.now() - d)/1000; // seconds;
      if (delta < 60) {
        return "Less than a minute ago";
      }
      // it wasn't 90 seconds ago or less, so let's refine towards minutes
      delta /= 60; // now it is minutes
      if (delta < 60) {
        return Math.floor(delta) + " minutes ago";
      }
      if (delta > 2.628e+7) {
        return "Never";
      }
      // it wasn't 90 minutes ago, so let's just put the time stamp up
      var dthen = new Date(d);
      var dnow = new Date();
      var opts = {};
      if (format){
        format.trim().split("").forEach(char => {
          const keys = {"h": "hour", "m": "minute", "s": "second"}
          if (char in keys){
            opts[keys[char]] = "numeric";
          }
        })
      }
      if (dthen.toLocaleDateString() != dnow.toLocaleDateString()) {
        return dthen.toLocaleDateString() + " " + dthen.toLocaleTimeString([], opts);
      }
      return dthen.toLocaleTimeString([], opts);
    } else {
      // do nothing
      return dt;
    }
  };

  transforms['time'] = function(dt) {
        // only transform strings
    if (typeof (dt) == "string") {
      // let's strip out everything after the brackets
      var s = dt;
      var k = s.indexOf('[');
      if (k >= 0) {
        s = s.substring(0, k);
      }

      // Convert string to time format
      var t = dt.indexOf(":");
      if (t < 0) {
        return dt;
      }
      var h = dt.split(":")[0];
      var hours = h;
      if (h.length > 2) {
        hours = h.substring(h.length - 2, h.length); // trim left in case of datetime
      }
      var AmOrPm = hours >= 12 ? 'pm' : 'am';
      hours = (hours % 12) || 12;
      var m = dt.split(":")[1]
      var minutes = m;
      if (m.length > 2) {
        minutes = m.substring(0,2); // trim right in case of datetime
      }

      return hours + ":" + minutes + " " + AmOrPm; 
    } else {
      // do nothing
      return dt;
    }
  };

  transforms['date-format'] = function(d, format) {
    if (typeof (d) == "string") {
      // if date is not set, return empty string
      if (d == "1-01-01") return ""

      const p = format.toLowerCase().split("/");
      const opts = {};
      p.forEach(f => {
        const val = f.length > 2 ? "numeric" : "2-digit";
        const keys = {"y": "year", "m": "month", "d": "day"}
        opts[keys[f.charAt(0)]] = val;
      })

      // if not datetime, converting to one here to prevent timezone issues
      const datetime = d.includes("T") ? d : d + "T00:00:00";
      return new Date(datetime).toLocaleDateString('en-US', opts);
    } else {
      // do nothing
      return d;
    }
  }
  

  self.RTR = function(name, transform) {
    transforms[name] = transform;
  };
  self.registerTransform = self.RTR;

  // {blah|name}
  self.TR = function(name) {
    if (name in transforms) {
      return transforms[name];
    } 
    // ex: date-format:mm/dd/yy
    if (name.includes(":")){
      const [key, format]  = name.split(":");
      if (key in transforms) {
        return x => transforms[key](x, format);
      }
    }
    
    return function(x) { return x; };
  };

  // TODO
  var validators = {};
  self.RV = function(name, validator) {
    validators[name] = validator;
  };
  self.registerValidator = self.RV;

  var commonPut = function (form, state, identityName, rxobj, urlfactory) {
    // WIP
    rxobj.__ = function () { };
    form.addEventListener('submit', function (evt) {
      evt.preventDefault();
      var passwords = {};
      var req = get_form(form, passwords);

      var next = function () {
        var url = urlfactory(rxobj);
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function () {
          if (this.readyState == 4) {
            if (this.status == 200) {
              var type = this.getResponseHeader("Content-Type");
              if (type == "text/error") {
                fire_failure(form, this.responseText);
                return;
              } else if (type == "application/json") {
                var payload = JSON.parse(this.responseText);
                if ('identity' in payload) {
                  identities[identityName] = payload.identity;
                  localStorage.setItem("identity_" + identityName, payload.identity);
                }
              }
              self.goto(rxobj.rx_forward, false);
              fire_success(form);
              form.dispatchEvent(new Event("submitted"));
            } else {
              fire_failure(form, "Failed to communicate to server");
            }
          }
        };
        xhttp.open("PUT", url, true);
        xhttp.withCredentials = true;
        xhttp.send(JSON.stringify(req));
      };

      if ('password' in passwords) {
        if (!('confirm-password' in passwords) || passwords['password'] == passwords['confirm-password']) {
          connection.DocumentsHashPassword(passwords['password'], {
            success: function (hashed_pw) {
              req['password'] = hashed_pw.passwordHash;
              next();
            },
            failed: function () {
              fire_failure(form, "Failed to hash password");
            }
          });
        } else {
          fire_failure(form, "Passwords don't match");
        }
      } else {
        next();
      }
    }, true);
  }

  // RUNTIME | rx:action=domain:put
  self.adDPUT = function (form, state, identityName, rxobj) {
    commonPut(form, state, identityName, rxobj, function (rxobj) { return self.protocol + "//" + self.host + "/" + rxobj.path; })
  };

  // RUNTIME | rx:action=document:put
  self.aDPUT = function (form, state, identityName, rxobj) {
    commonPut(form, state, identityName, rxobj, function (rxobj) { return self.protocol + "//" + connection.host + "/" + rxobj.space + "/" + rxobj.key + "/" + rxobj.path; })
  };

  // RUNTIME | rx:action=adama:sign-in
  self.aSO = function (form, state, identityName, rxobj) {
    rxobj.__ = function () { };
    window.setTimeout(function () {
      recall_email(form);
    }, 1);
    // TODO: pull email out of thin air
    form.addEventListener('submit', function (evt) {
      evt.preventDefault();
      var req = get_form_and_pw(form);
      if (req.remember) {
        localStorage.setItem("email_remember", req.email);
      } else {
        localStorage.setItem("email_remember", "");
      }
      connection.AccountLogin(req.email, req.password, {
        success: function (payload) {
          identities[identityName] = payload.identity;
          localStorage.setItem("identity_" + identityName, payload.identity);
          self.goto(rxobj.rx_forward, false);
          fire_success(form);
        },
        failure: function (reason) {
          fire_failure(form, "AccountLogin Failed:" + reason);
        }
      });
    }, true);
  };

  // RUNTIME | rx:action=adama:sign-up
  self.aSU = function (form, state, forwardTo) {
    form.addEventListener('submit', function (evt) {
      evt.preventDefault();
      var req = get_form(form, {});
      connection.InitSetupAccount(req.email, {
        success: function (/* payload */) {
          localStorage.setItem("email", req.email);
          self.goto(forwardTo, false);
          fire_success(form);
        },
        failure: function (reason) {
          fire_failure(form, "InitSetupAccount Failed:" + reason);
        }
      });
    }, true);
  };

  // RUNTIME | rx:action=adama:set-password
  self.aSP = function (form, state, forwardTo) {
    form.addEventListener('submit', function (evt) {
      evt.preventDefault();
      var req = get_form_and_pw(form);
      if (!("email" in req)) {
        req.email = localStorage.getItem("email");
      }
      connection.InitCompleteAccount(req.email, false, req.code, {
        success: function (init) {
          var identity = init.identity;
          connection.AccountSetPassword(init.identity, req.password, {
            success: function () {
              localStorage.setItem("identity_default", identity);
              self.goto(forwardTo, false);
              fire_success(form);
            },
            failure: function (reason) {
              fire_failure(form, "Failed AccountSetPassword:" + reason);
            }
          });
        },
        failure: function (reason) {
          fire_failure(form, "Failed InitCompleteAccount:" + reason);
        }
      });
    }, true);
  };


  var hydrate_form = function(el, input) {
    var tag = el.tagName.toUpperCase();
    var isSettable = tag == "SELECT" || tag == "INPUT" || tag == "HIDDEN";
    if ('name' in el && isSettable) {
      var name = el.name;
      var payload = input;
      var kDotOrSlash = firstOccur(name.indexOf('.'), name.indexOf('/'));
      while (kDotOrSlash > 0) {
        var item = name.substring(0, kDotOrSlash);
        name = name.substring(kDotOrSlash + 1);
        if (item in payload) {
          payload = payload[item];
        } else {
          return;
        }
        kDotOrSlash = firstOccur(name.indexOf('.'), name.indexOf('/'));
      }
      if (name in payload) {
        var type = el.type.toUpperCase();
        if (type == "CHECKBOX") {
          el.checked = payload[name] == "true" || payload[name] === true ? true : false;
        } else if (type == "RADIO") {
          if (payload[name] == el.value) {
            el.checked = true;
          }
        } else {
          el.value = payload[name];
        }
      }
    }
    if ("children" in el) {
      var arr = el.children;
      var n = arr.length;
      for (var k = 0; k < n; k++) {
        var ch = el.children[k];
        hydrate_form(ch, input);
      }
    }
  }


  // RUNTIME | rx:action:copy-form:$target-id
  self.aCF = function(form, targetFormId) {
    form.addEventListener('submit', function(evt) {
      evt.preventDefault();
      var input = get_form(form, {});
      var target = document.getElementById(targetFormId);
      if (target) {
        hydrate_form(target, input);
        fire_success(form)
      } else {
        fire_failure(form, "failed to find form by id '" + targetFormId + "'");
      }
    }, true);
  };

  // RUNTIME | rx:action=send:$channel
  self.aSD = function (form, state, channel) {
    form.addEventListener('submit', function (evt) {
      evt.preventDefault();
      var passwords = {};
      var msg = get_form(form, passwords);

      if (evt.submitter) {
        var s = evt.submitter;
        if (s.name && s.value) {
          if (s.value == "true") { s.value = true; }
          else if (s.value == "false") { s.value = false; }
          msg[s.name] = s.value;
        }
      }

      var after = function() {
        var channelToUse = channel;
        if (channel == "__ds") {
          channelToUse = msg['__channel'];
          delete msg['__channel'];
        }
        state.data.connection.ptr.send(channelToUse, msg, {
          success: function (/* payload */) {
            fire_success(form);
          },
          failure: function (reason) {
            fire_failure(form, "Send failed:" + reason);
          }
        });
      };

      var postPassword = function() {
        if ('new_password' in passwords) {
          if (!('confirm-new_password' in passwords) || passwords['new_password'] == passwords['confirm-new_password']) {
            connection.DocumentsHashPassword(passwords['new_password'], {
              success: function (hashed_pw) {
                msg['new_password'] = hashed_pw.passwordHash;
                after();
              },
              failed: function () {
                fire_failure(form, "Failed to hash password");
              }
            });

          } else {
            fire_failure(form, "Passwords mismatch for new password.");
          }
        } else {
          after();
        }
      }

      if ('password' in passwords) {
        if (!('confirm-password' in passwords) || passwords['password'] == passwords['confirm-password']) {
          connection.DocumentsHashPassword(passwords['password'], {
            success: function (hashed_pw) {
              msg['password'] = hashed_pw.passwordHash;
              postPassword();
            },
            failed: function () {
              fire_failure(form, "Failed to hash password");
            }
          });
        } else {
          fire_failure(form, "Passwords mismatch");
        }
      } else {
        postPassword();
      }
    }, true);
  };
  // <todotask>description</todotask>
  self.TASK = function(pdom, id, section, description) {
    var element = document.createElement("div");
    element.innerHTML = "&#9744; " + description + " ( " + section + ") <br />";
    pdom.appendChild(element);
  };
  // RUNTIME: pre-compression for setting an attribute
  // "d.setAttribute(a,v);" -->
  // $.SA(d,a,v);"
  self.SA = function(dom, attr, value) {
    dom.setAttribute(attr, value);
  };
  // RUNTIME: set the label of an option
  self.SL = function(dom, value) {
    dom.label = value;
    dom.innerText = value;
  };

  // for selects
  // d.value=$value;
  // $.SV(d,$value);
  self.SV = function(dom, value) {
    var prior = dom.value;
    dom.value = value;
    dom.rxvalue = value;
    if (prior != value) {
      dom.dispatchEvent(new Event("forced"));
    }
  };
  self.FV = function(dom, field, value) {
    var prior = dom[field];
    dom[field] = value;
    if (prior != value) {
      dom.dispatchEvent(new Event("forced"));
    }
  };
  self.mergeGoto = function(href, state) {
    var parts = (href.startsWith("/") ? href.substring(1) : href).split("/");
    var mergeInit = {};
    if (route(parts, 0, router, mergeInit)) {
      state.view.tree.update(mergeInit);
    }
  };
  // RUNTIME | <... href="" ...>
  self.HREF = function (dom, state, href, merge) {
    dom.setAttribute("href", fixHref(href));
    dom.onclick = function (evt) {
      var trimHref = href;
      if (trimHref.startsWith("/")) {
        trimHref = trimHref.substring(1);
      }
      var kQ = trimHref.indexOf('?');
      if (kQ > 0) {
        trimHref = trimHref.substring(0, kQ);
      }
      var parts = trimHref.split("/");
      var mergeInit = {};
      if (route(parts, 0, router, mergeInit)) {
        evt.preventDefault();
        if (merge) {
          state.view.tree.update(mergeInit);
          window.history.pushState({merge:true}, "", fixHref(href));
        } else {
          self.goto(href, true);
        }
        return false;
      }
      return true;
    };
  };
  // RUNTIME | <... class="" ...>
  self.AC = function (dom, value) {
    dom.setAttribute("class", value);
  };
  self.ACLASS = function (dom, value) { // to deprecate in the future
    dom.setAttribute("class", value);
  };
  // RUNTIME | <... src="" ...>
  self.ASRC = function (dom, value) {
    dom.setAttribute("src", value);
  };

  self.findElementByIdInUnboundDom = function(dom, id) {
    var febid = function (d, id) {
      if ('id' in d) {
        if (d.id == id) {
          return d;
        }
      }
      if ('children' in d) {
        var arr = d.children;
        var n = arr.length;
        for (var k = 0; k < n; k++) {
          var result = febid(arr[k], id);
          if (result !== null) {
            return result;
          }
        }
      }
      return null;
    };
    return febid(dom, id);
  };
  self.pollElement = function(dom, timeout, success, failure) {
    var adm = function (dom, remain) {
      if (remain < 0) {
        failure();
        return;
      }
      if (document.body.contains(dom)) {
        success();
      } else {
        window.setTimeout(function () {
          adm(dom, remain - 25);
        }, 25);
      }
    };
    adm(dom, timeout);
  };
  var libsLoaded = {};
  self.loadLibrary = function(lib, callback) {
    if (lib in libsLoaded) {
      callback(libsLoaded[lib]);
      return;
    }
    libsLoaded[lib] = false;
    var script = document.createElement('script');
    script.setAttribute('src',lib);
    script.onload = function() {
      libsLoaded[lib] = true;
      callback(true);
    };
    script.onerror = function() {
      callback(false);
    };
    document.head.appendChild(script);
  };
  window.rxhtml = self;
  return self;
})();

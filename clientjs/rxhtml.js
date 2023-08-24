var RxHTML = (function () {
  var self = {};

  var templates = {};
  var router = {};
  // This strange escaping is for developer mode to proxy to localhost.
  var connection = new Adama.Connection(/*ENDPOINT=[*/Adama.Production/*]*/);
  var connections = {};

  var connectionMonitorDom = document.createElement("div");
  connection.onstatuschange = function (status) {
    var icon = function (color) { return "<svg xmlns=\"http://www.w3.org/2000/svg\" fill=\"none\" viewBox=\"0 0 24 24\" stroke-width=\"1.5\" stroke=\"" + color + "\" class=\"w-6 h-6\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M8.288 15.038a5.25 5.25 0 017.424 0M5.106 11.856c3.807-3.808 9.98-3.808 13.788 0M1.924 8.674c5.565-5.565 14.587-5.565 20.152 0M12.53 18.22l-.53.53-.53-.53a.75.75 0 011.06 0z\" /></svg>\n"; };
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
  if (Adama.Debugger) {
    connectionMonitorDom.onclick = Adama.Debugger.toggle;
  }
  connection.start();

  var rootReplace = "/";
  var fixPath = function (path) {
    return path;
  };
  if (window.location.hostname.endsWith(".adama-platform.com") && !window.location.hostname.endsWith("ide.adama-platform.com")) {
    var parts = window.location.pathname.split("/");
    rootReplace = [parts[0], parts[1], parts[2], ""].join("/");
    var offset = parts[0].length + parts[1].length + parts[2].length + 2;
    fixPath = function (path) {
      return path.substring(offset);
    };
  }

  var fixHref = function (href) {
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

  // HELPER | subscribe to state and populate unsubscribe object
  var subscribe_state = function (state, unsub) {
    if (state.data != null) {
      unsub.__data = state.data.tree.subscribe(root_of(state.data).delta);
    } else {
      unsub.__data = function () {
      };
    }
    if (state.view != null) {
      unsub.__view = state.view.tree.subscribe(root_of(state.view).delta);
    } else {
      unsub.__view = function () {
      };
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
      next[state.current] = prior;
    }
    return next;
  };
  self.newStateParentOf = self.pU;

  // RUNTIME | dive one level Into path1/path2/..../pathN
  self.pI = function (state, name) {
    var prior = state[state.current];
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
      state[state.current].tree.update(delta);
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

  // RUNTIME | <tag>
  self.E = function (tag, ns) {
    if (ns == undefined || ns == null) {
      return document.createElement(tag);
    } else {
      var result = document.createElementNS(ns, tag);
      result.setAttribute("xmlns", ns);
      return result;
    }
  };

  // RUNTIME | <pick name=...>
  self.P = function (parent, priorState, rxObj, childMakerConnected, childMakerDisconnected) {
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
        nuke(parent);
        fire_unsub(unsub);
        var state = {
          service: priorState.service,
          data: { connection: co, tree: co.tree, delta: {}, parent: null, path: null },
          view: new_delta_copy(priorState.view),
          current: "data"
        };
        if (cs) {
          childMakerConnected(parent, state);
        } else {
          childMakerDisconnected(parent, state);
        }
        subscribe_state(state, unsub);
        // TODO: return false to unsub
        return true;
      });
    }.bind({ name: "" });
  };

  // RUNTIME | <template name="...">
  self.TP = function (name, foo) {
    templates[name] = foo;
  };

  // RUNTIME | <tag rx:template=$name>
  self.UT = function (parent, state, name, child_maker) {
    var foo = templates[name];
    foo(parent, state, child_maker);
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
      childrenMaker(parent, state, "" + value);
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
        nuke(parentDom);
        if (ord == null) {
          kill();
          return;
        }
        for (var k = 0; k < ord.length; k++) {
          parentDom.append(domByKey[ord[k]]);
        }
      }
    };
    subscribe(state, name, sub);
  };

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
  var fire_success = function (dom) {
    dom.dispatchEvent(new Event("success"));
  };
  self.fire_success = fire_success;

  // custom event on forms; rx:failed="..."
  var fire_failure = function (dom, msg) {
    var e = new Event("failure");
    e.message = msg;
    console.log("FAILURE:" + msg);
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
    form.onsubmit = function (evt) {
      if (customCommandName in customs) {
        evt.preventDefault();
        var obj = get_form(form, false);
        customs[customCommandName](obj, state, signal, self);
        fire_success(form);
      } else {
        fire_failure(form, "Failed to find '" + customCommandName + "'");
      }
    };
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
      var eval = ('min' in out && 'max' in out) ? (out.min <= arr.length && arr.length <= out.max) : true;
      if (finalize.eval != eval) {
        finalize.eval = eval;
        change(finalize.eval);
      }
    };

    priorState.data.connection.subscribe_choice(channel, function () {
      finalize.update();
      // TODO: return whether or not this needs to be axed
      return true;
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

  var reg_event = function (state, dom, type, runnable) {
    // TODO: connected/disconnected as special events to consider
    if (type == "load") {
      window.setTimeout(runnable, 1);
    } else {
      dom.addEventListener(type, runnable);
    }
  }

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
      var delta = path_to(state.view, obj);
      state[state.current].tree.update(delta);
    });
  };

  // RUNTIME: <tag .. rx:event="... reset ...">
  self.oRST = function (dom, type, state) {
    reg_event(state, dom, type, function (event) {
      dom.reset();
    });
  };

  // RUNTIME: <tag .. rx:event="... goto:uri ...">
  self.onGO = function (dom, type, state, value) {
    reg_event(state, dom, type, function () {
      var uri = (typeof (value) == "function") ? value() : value;
      self.goto(uri);
    });
  };

  // RUNTIME: <tag .. rx:event="... toggle:name ...">
  self.onT = function (dom, type, state, name) {
    var captured = { value: false };
    reg_event(state, dom, type, function () {
      var obj = {};
      obj[name] = !captured.value;
      var delta = path_to(state.view, obj);
      state[state.current].tree.update(delta);
    });
    subscribe(state, name, function (value) {
      captured.value = value == true;
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
      var eval = chosen.value in choice;
      if (chosen.eval != eval) {
        chosen.eval = eval;
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
      var eval = find(priorState, channel, key, decide.value) != null;
      if (decide.eval != eval) {
        decide.eval = eval;
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

  self.IF = function (parent, priorState, name, shouldBe, expandView, makerTrue, makerFalse) {
    var unsub = make_unsub();
    var set = function (value) {
      var show = (value ? true : false) === shouldBe;
      if (this.shown == show) {
        return;
      }
      this.shown = show;
      nuke(parent);
      fire_unsub(unsub);
      var state = fork(priorState);
      var next = state;
      if (typeof (value) == "object") {
        next = self.pI(next, name);
        if (expandView) {
          next = self.pEV(next, name);
        }
      }
      // we default back to data in the IF case
      next.current = 'data';
      if (show) {
        makerTrue(parent, next);
      } else {
        makerFalse(parent, next);
      }
      subscribe_state(state, unsub);
    }.bind({ shown: 'no' });
    subscribe(priorState, name, set);
  };

  /// RUNTIME | rx:action=copy:path
  self.aCP = function (form, state, name) {
    form.onsubmit = function (evt) {
      evt.preventDefault();
      var obj = get_form(form, false);
      if (name != "." && name != "") {
        var no = {};
        no[name] = obj;
        obj = no;
      }
      var delta = path_to(state.view, obj);
      state.view.tree.update(delta);
      fire_success(form);
    };
  };

  // RUNTIME | <input ... rx:sync=path ...>
  self.SY = function (el, state, name, ms) {
    var type = ("type" in el) ? el.type.toUpperCase() : "text";
    var signal = function (value) {
      var obj = {};
      obj[name] = el.value;
      var delta = path_to(state.view, obj);
      state.view.tree.update(delta);
    };
    if (type == "CHECKBOX") {
      el.onchange = debounce(ms, function (evt) {
        signal(el.checked ? true : false);
      });
    } else if (type == "RADIO") {
      el.onchange = debounce(ms, function (evt) {
        if (el.checked) {
          signal(el.value);
        }
      });
    } else {
      el.onchange = debounce(ms, function (evt) {
        signal(el.value);
      });
      el.onkeyup = el.onchange;
      window.setTimeout(function () {
        signal(el.value);
      }, 1);
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
  var build_obj = function (el, objToInsertInto, allow_passwords) {
    var justSet = el.tagName.toUpperCase() == "TEXTAREA" || el.tagName.toUpperCase() == "SELECT";
    var isInputBox = el.tagName.toUpperCase() == "INPUT";
    var isFieldSet = el.tagName.toUpperCase() == "FIELDSET";
    var hasName = "name" in el;
    var insertAt = objToInsertInto;
    // the apply function is how we inject the value into the object
    var apply = function (val) { };

    if (hasName && (justSet || isInputBox || isFieldSet)) {
      var name = "";
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

    if (justSet) {
      apply(el.value);
    } else if (isFieldSet) {
      var nextObject = {};
      if ("children" in el) {
        var arr = el.children;
        var n = arr.length;
        for (var k = 0; k < n; k++) {
          var ch = el.children[k];
          build_obj(ch, nextObject, allow_passwords);
        }
      }
      apply(nextObject);
    } else if (isInputBox) {
      var type = ("type" in el) ? el.type.toUpperCase() : "TEXT";
      if (type == "SUBMIT" || type == "RESET") return;
      if (type == "PASSWORD" && !allow_passwords) {
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
          build_obj(ch, objToInsertInto, allow_passwords);
        }
      }
    }
  };

  self.BuildFormObject = function(form) {
    var obj = {};
    build_obj(form, obj, false);
    return obj;
  };

  // HELPER | return an object of all the inputs of the given form element
  var get_form = function (form, allow_passwords) {
    var obj = {};
    build_obj(form, obj, allow_passwords);
    return obj;
  };

  // HELPER | return a password from the given form
  var get_password = function (el) {
    if (el.tagName.toUpperCase() == "INPUT" && el.type.toUpperCase() == "PASSWORD") {
      return [el.name, el.value];
    }
    if ("children" in el) {
      var arr = el.children;
      var n = arr.length;
      for (var k = 0; k < n; k++) {
        var ch = el.children[k];
        var result = get_password(ch);
        if (result !== null) {
          return result;
        }
      }
    }
    return null;
  };

  var wrappers = {};
  var wrappers_onload = {};
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
  };

  self.goto = function (uri) {
    console.log("going to:" + uri);
    window.setTimeout(function () {
      if (uri.startsWith("/")) {
        self.run(document.body, uri, true);
      } else {
        window.location.href = fixHref(uri);
      }
    }, 10);
  };

  self.init = function () {
    self.run(document.body, fixPath(window.location.pathname + window.location.hash), false);
    window.onpopstate = function () {
      self.run(document.body, fixPath(window.location.pathname + window.location.hash), false);
    };
  };

  // API | Run the page in the given place
  self.run = function (where, rawPath, push) {
    var path = rawPath;
    while (path.endsWith("/") && path != "/") {
      path = path.substring(0, path.length - 1);
    }
    for (conKey in connections) {
      connections[conKey].tree.nuke();
      connections[conKey].nuke();
    }
    var parts = (path.startsWith("/") ? path.substring(1) : path).split("/");
    var init = {};
    var foo = route(parts, 0, router, init);
    nuke(where);
    if (foo != null) {
      var state = { service: connection, data: null, view: fresh(where), current: "view" };
      state.view.init = init;
      foo(where, state);
      state.view.tree.subscribe(state.view.delta);
      state.view.tree.update(init);
      if (push) {
        window.history.pushState({}, "", fixHref(path));
      }
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

    self.goto("/");
  };

  self.GOOGLE_SIGN_ON = function (accessToken) {
    connection.InitConvertGoogleUser(accessToken, {
      success: function (payload) {
        identities["default"] = payload.identity;
        localStorage.setItem("identity_default", payload.identity);
        self.goto("/");
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
        self.goto(redirectToFunc());
      };
    } else {
      // whatever page we are, needs to die which means we need to nuke everything!
      window.setTimeout(function () {
        self.goto(redirectToFunc());
      }, 10);
      return { abort: true };
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
        if (reason == 403403) {
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
    document.title = rxobj.value;
    rxobj.__ = debounce(1, function () {
      document.title = rxobj.value;
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

  var bind_responder = function (co, state, cleanup, retry_sm) {
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
          state.view.tree.update(payload.delta.viewstate);
        }
        if ('log' in payload.delta) {
          console.log(payload.delta.log);
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
          self.goto(payload.delta.goto);
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
        if (reason == 403403) {
          cleanup();
          return;
        }
        // is it an expected error that will happen a bunch?
        var expected_error = reason == 999;
        // do we not know about an error that happens a bunch? if so, then let's at least try again a number of finite times.
        var blind_retry = retry_sm.unexpected_errors < 10;
        if (blind_retry) {
          retry_sm.unexpected_errors++;
        }
        // retry on either case
        if (expected_error || blind_retry) {
          retry_sm.backoff = Math.ceil(Math.min(retry_sm.backoff + Math.random() * retry_sm.backoff + (blind_retry ? 100 : 1), 1000));
          console.log("connect-failure|retrying... (" + retry_sm.backoff + "); reason=" + reason + (blind_retry ? " [blind]" : ""));
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
    var sync_tree = function () {
      // let's copy out the tree from the viewstate
      var new_tree = state.view.tree.copy();

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
      var domain = location.host.split(":")[0];
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
      co.domain = domain;
      var retry_sm = {};
      retry_sm.responder = bind_responder(co, state, cleanup, retry_sm);
      retry_sm.go = function() {
        // bias to nothing new
        co.viewstate_clone = "{}";
        co.ptr = connection.ConnectionCreateViaDomain(identity, domain, state.view.tree.copy(), retry_sm.responder);
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
      var retry_sm = {};
      retry_sm.responder = bind_responder(co, state, cleanup, retry_sm);
      retry_sm.go = function() {
        // bias to nothing new
        co.viewstate_clone = "{}";
        co.ptr = connection.ConnectionCreate(identity, rxobj.space, rxobj.key, state.view.tree.copy(), retry_sm.responder);
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
    form.action = "https://aws-us-east-2.adama-platform.com/~upload";
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
    form.action = "https://aws-us-east-2.adama-platform.com/~upload";
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
      domainInput.value = location.hostname;
      form.appendChild(domainInput);
    }
    var iframeTarget = document.createElement("iframe");
    iframeTarget.name = "UPLOAD_" + Math.random();
    iframeTarget.width = "1";
    iframeTarget.height = "1";
    form.appendChild(iframeTarget);
    form.target = iframeTarget.name;
  };

  // RUNTIME | rx:action=domain:sign-in
  self.adDSO = function (form, state, identityName, rxobj) {
    rxobj.__ = function () { };
    form.onsubmit = function (evt) {
      evt.preventDefault();
      var req = get_form(form, true);
      connection.DocumentAuthorizeDomain(location.hostname, req.username, req.password, {
        success: function (payload) {
          identities[identityName] = payload.identity;
          localStorage.setItem("identity_" + identityName, payload.identity);
          // TODO: blow away connections
          self.goto(rxobj.rx_forward);
          fire_success(form);
        },
        failure: function (reason) {
          fire_failure(form, "Failed signing into document:" + reason);
        }
      });
    };
  }

  // RUNTIME | rx:action=document:sign-in
  self.aDSO = function (form, state, identityName, rxobj) {
    rxobj.__ = function () { };
    form.onsubmit = function (evt) {
      evt.preventDefault();
      var req = get_form(form, true);
      connection.DocumentAuthorize(req.space, req.key, req.username, req.password, {
        success: function (payload) {
          identities[identityName] = payload.identity;
          localStorage.setItem("identity_" + identityName, payload.identity);
          // TODO: blow away connections
          self.goto(rxobj.rx_forward);
          fire_success(form);
        },
        failure: function (reason) {
          fire_failure(form, "Failed signing into document:" + reason);
        }
      });
    };
  };
  // transform = format_date_usa

  var transforms = {};
  transforms['principal.agent'] = function(x) { return x.agent; };
  transforms['principal.authority'] = function(x) { return x.authority; };
  transforms['trim'] = function(x) { return ('' + x).trim(); };
  transforms['upper'] = function(x) { return ('' + x).toUpperCase(); };
  transforms['lower'] = function(x) { return ('' + x).toLowerCase(); };
  transforms['is_empty_str'] = function(x) { return x == ""; };
  transforms['is_not_empty_str'] = function(x) { return x != ""; };
  transforms['jsonify'] = function(x) { return JSON.stringify(x); };

  self.RTR = function(name, transform) {
    transforms[name] = transform;
  };
  self.registerTransform = self.RTR;

  // {blah|name}
  self.TR = function(name) {
    if (name in transforms) {
      return transforms[name];
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
    form.onsubmit = function (evt) {
      evt.preventDefault();
      var req = get_form(form, false);
      // TODO: this assumes exactly one password in the root message
      var pws = get_password(form);

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
              self.goto(rxobj.rx_forward);
              fire_success(form);
            } else {
              fire_failure(form, "Failed to communicate to server");
            }
          }
        };
        xhttp.open("PUT", url, true);
        xhttp.withCredentials = true;
        xhttp.send(JSON.stringify(req));
      };

      if (pws !== null) {
        connection.DocumentsHashPassword(pws[1], {
          success: function (hashed_pw) {
            req[pws[0]] = hashed_pw.passwordHash;
            next();
          },
          failed: function () {
            fire_failure(form, "Failed to hash password");
          }
        });
      } else {
        next();
      }
    };
  }

  // RUNTIME | rx:action=domain:put
  self.adDPUT = function (form, state, identityName, rxobj) {
    commonPut(form, state, identityName, rxobj, function (rxobj) { return location.protocol + "//" + location.host + "/" + rxobj.path; })
  };

  // RUNTIME | rx:action=document:put
  self.aDPUT = function (form, state, identityName, rxobj) {
    commonPut(form, state, identityName, rxobj, function (rxobj) { return "https://" + connection.host + "/" + rxobj.space + "/" + rxobj.key + "/" + rxobj.path; })
  };

  // RUNTIME | rx:action=adama:sign-in
  self.aSO = function (form, state, identityName, rxobj) {
    rxobj.__ = function () { };
    window.setTimeout(function () {
      recall_email(form);
    }, 1);
    // TODO: pull email out of thin air
    form.onsubmit = function (evt) {
      evt.preventDefault();
      var req = get_form(form, true);
      if (req.remember) {
        localStorage.setItem("email_remember", req.email);
      } else {
        localStorage.setItem("email_remember", "");
      }
      connection.AccountLogin(req.email, req.password, {
        success: function (payload) {
          identities[identityName] = payload.identity;
          localStorage.setItem("identity_" + identityName, payload.identity);
          self.goto(rxobj.rx_forward);
          fire_success(form);
        },
        failure: function (reason) {
          fire_failure(form, "AccountLogin Failed:" + reason);
        }
      });
    };
  };

  // RUNTIME | rx:action=adama:sign-up
  self.aSU = function (form, state, forwardTo) {
    form.onsubmit = function (evt) {
      evt.preventDefault();
      var req = get_form(form);
      connection.InitSetupAccount(req.email, {
        success: function (/* payload */) {
          localStorage.setItem("email", req.email);
          self.goto(forwardTo);
          fire_success(form);
        },
        failure: function (reason) {
          fire_failure(form, "InitSetupAccount Failed:" + reason);
        }
      });
    };
  };

  // RUNTIME | rx:action=adama:set-password
  self.aSP = function (form, state, forwardTo) {
    form.onsubmit = function (evt) {
      evt.preventDefault();
      var req = get_form(form, true);
      if (!("email" in req)) {
        req.email = localStorage.getItem("email");
      }
      connection.InitCompleteAccount(req.email, false, req.code, {
        success: function (init) {
          var identity = init.identity;
          connection.AccountSetPassword(init.identity, req.password, {
            success: function () {
              localStorage.setItem("identity_default", identity);
              self.goto(forwardTo);
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
    };
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
    form.onsubmit = function(evt) {
      evt.preventDefault();
      var input = get_form(form, false);
      var target = document.getElementById(targetFormId);
      if (target) {
        hydrate_form(target, input);
        fire_success(form)
      } else {
        fire_failure(form, "failed to find form by id '" + targetFormId + "'");
      }
    };
  };

  // RUNTIME | rx:action=send:$channel
  self.aSD = function (form, state, channel) {
    form.onsubmit = function (evt) {
      evt.preventDefault();
      state.data.connection.ptr.send(channel, get_form(form, false), {
        success: function (/* payload */) {
          fire_success(form);
        },
        failure: function (reason) {
          fire_failure(form, "Send failed:" + reason);
        }
      });
    };
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
  // RUNTIME | <... href="" ...>
  self.HREF = function (dom, href) {
    dom.setAttribute("href", fixHref(href));
    dom.onclick = function (evt) {
      var parts = (href.startsWith("/") ? href.substring(1) : href).split("/");
      if (route(parts, 0, router, {})) {
        evt.preventDefault();
        self.run(document.body, href, true);
        return false;
      }
      return true;
    };
  };
  // RUNTIME | <... class="" ...>
  self.ACLASS = function (dom, value) {
    dom.setAttribute("class", value);
  };
  // RUNTIME | <... src="" ...>
  self.ASRC = function (dom, value) {
    dom.setAttribute("src", value);
  };
  window.rxhtml = self;
  return self;
})();
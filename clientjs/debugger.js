Adama.Debugger = (function() {
  var self = {};
  self.shown = false;

  var connections = {};
  var current = null;
  var nuke = function (parent) {
    var last = parent.lastChild;
    while (last) {
      parent.removeChild(last);
      last = parent.lastChild;
    }
  };

  var viewJson = document.createElement("div");
  var viewChannels = document.createElement("div");

  var render = function(name) {
    var co = connections[name];
    if (co.rendered == co.bound) {
      return;
    }
    co.rendered = co.bound;
    co.debug_tree.nuke();
    viewJson.innerHTML = co.debug_tree.str();
    co.debug_tree.subscribe(function() {
      viewJson.innerHTML = co.debug_tree.str();
    });

    console.log("RENDER:" + co.space + "/" + co.key);
  };

  var connectionSelector = document.createElement("select");
  var switch_to_connection = function(name) {
    current = name;
    nuke(viewChannels);
    nuke(viewJson);
    var co = connections[name];
    if (co.bound == co.debugging && typeof(co.bound) == "string") {
      render(name);
    }
  };
  connectionSelector.onchange = function() {
    // TODO
  };
  var refresh_select = function() {
    nuke(connectionSelector);
    for (var name in connections) {
      var opt = document.createElement("option");
      opt.value = name;
      opt.innerText = name;
      connectionSelector.appendChild(opt);
      if (current == null) {
        switch_to_connection(name);
        connectionSelector.value = name;
      }
    }
  };

  self.toggle = function() {
    console.log("toggle debugger 123");
    if (self.shown) {
      document.body.removeChild(self.root);
    } else {
      self.root = document.createElement("div");
      self.root.style = "position:absolute; top:0px; left:0px; width:500px; height:500px; background: white; border: 1px solid red; z-index:1000";
      self.root.appendChild(connectionSelector);
      self.root.appendChild(viewJson);
      self.root.appendChild(viewChannels);
      document.body.appendChild(self.root);
    }
    self.shown = !self.shown;
  };

  self.register = function(connection) {
    connections[connection.name] = connection;
    connection.debug_tree = new AdamaTree();
    connection.debugging = "?";
    refresh_select();
    return function(payload) {
      if (connection.bound != connection.debugging && current == connection.name) {
        render(connection.name);
      }
      if ("data" in payload.delta) {
        connection.debug_tree.update(payload.delta.data);
      }
      /*
      if ("outstanding" in payload.delta) {
//        co.ondecide(payload.delta.outstanding);
      }
      if ('viewstate' in payload.delta) {
//        state.view.tree.update(payload.delta.viewstate);
      }
      if ('goto' in payload.delta) {
//        self.goto(payload.delta.goto);
      }
      */
    }
  };
  return self;
})();
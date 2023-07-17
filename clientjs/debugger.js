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

  var turn_json_into_html = function(obj, arr) {
    if (typeof(obj) == "function") {
      return "function...";
    }
    if (typeof(obj) == "undefined") {
      return "";
    }
    if (typeof(obj) != "object") {
      return "" + obj;
    }
    var result = [];
    result.push("<table border='1'>");
    var skip = function(key) {
      return key.startsWith("__") || key.startsWith("#") || key.startsWith("@");
    }
    if (Array.isArray(obj)) {
      var n = obj.length;
      var flatten = n > 0 && typeof(obj[0]) == "object" && !Array.isArray(obj[0]);
      if (flatten) {
        var dom = {};
        for (var k = 0; k < n; k++) {
          for (var j in obj[k]) {
            if (skip(j)) continue;
            dom[j] = true;
          }
        }
        var keys = [];
        for (var k in dom) {
          keys.push(k);
        }
        keys.sort();
        result.push("<tr>");
        for (var k = 0; k < keys.length; k++) {
          result.push("<td>" + keys[k] + "</td>")
        }
        result.push("</tr>");
        for (var k = 0; k < n; k++) {
          result.push("<tr>");
          for (var j = 0; j < keys.length; j++) {
            result.push("<td>" + turn_json_into_html(obj[k][keys[j]]) + "</td>")
          }
          result.push("</tr>");
        }

      } else {
        for (var k = 0; k < n; k++) {
          result.push("<tr><td>" + k + "</td><td>" + turn_json_into_html(obj[k]) + "</td></tr>")
        }
      }
    } else {
      for (var k in obj) {
        if (skip(k)) continue;
        result.push("<tr><td valign='top'>" + k + "</td><td>" + turn_json_into_html(obj[k]) + "</td></tr>")
      }
    }
    result.push("</table>");
    return result.join("");
  }

  var render = function(name) {
    var co = connections[name];
    if (co.rendered == co.bound) {
      return;
    }
    co.rendered = co.bound;
    co.debug_tree.nuke();
    viewJson.innerHTML = co.debug_tree.str();
    co.debug_tree.subscribe(function() {
      viewJson.innerHTML = turn_json_into_html(co.debug_tree.raw());
    });
    // TODO: co.raw.SpaceReflect(co.identity, co.space, co.key, {
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
      self.root.style = "position:absolute; top:0px; left:0px; width:500px; height:500px; background: white; border: 1px solid red; z-index:1000; overflow:scroll";
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
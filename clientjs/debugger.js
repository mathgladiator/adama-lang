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
    result.push("<table style=\"border:1px black solid\">");
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
    if (self.shown) {
      document.body.removeChild(self.root);
    } else {
      self.root = document.createElement("div");

      debuggerPosition = localStorage.getItem("AdamaDebuggerPosition");
      if (!debuggerPosition) {
        debuggerPosition + "top:0px; left:0px;width:640px;height:480px";
      }
      console.log(debuggerPosition);
      self.root.style = "position:absolute;" + debuggerPosition + ";background: white;border:1px solid red;z-index:1000; overflow:scroll";
      var titleBar = document.createElement("div");
      titleBar.innerHTML = "Adama Debugger";
      titleBar.style = "background: #ccc; cursor:move; width:100%; font-variant: small-caps; padding:2px; text-align: center;";
      titleBar.onmousedown = function(ev) {
        ev.preventDefault();
        var rect = self.root.getBoundingClientRect();
        var start = {x: ev.clientX, y: ev.clientY};
        var update = function(nx) {
          self.root.style.left = (rect.left + nx.clientX - start.x) + "px";
          self.root.style.top = (rect.top + nx.clientY - start.y) + "px";
        };
        window.onmousemove = function(nx) {
          update(nx);
          localStorage.setItem("AdamaDebuggerPosition", "top:"+self.root.style.top+"; left:"+self.root.style.left+";width:640px;height:480px");
        };
        window.onmouseup = function(nx) {
          update(nx);
          localStorage.setItem("AdamaDebuggerPosition", "top:"+self.root.style.top+"; left:"+self.root.style.left+";width:640px;height:480px");
          window.onmousemove = function() {};
          window.onmouseup = function() {};
        };
        return false;
      }
      self.root.appendChild(titleBar);
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
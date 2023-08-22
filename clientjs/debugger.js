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
  viewJson.style = "font-family: \"JetBrains Mono\", source-code-pro, Menlo, Monaco, Consolas, monospace; font-size: 14px; color: #fff; background-color: #000; width:100%"
  var viewChannels = document.createElement("div");

  var simple = function(tag, ch) {
    var d = document.createElement(tag);
    d.innerHTML = ch;
    return d;
  }

  var extractRecordType = function(type, types) {
    if (type.nature == "reactive_ref" || type.nature == "native_ref") {
      return extractRecordType(types[type.ref], types);
    }
    if (type.nature == "reactive_record" || type.nature == "native_message") {
      return type;
    }
    return null;
  }
  var buttonStyle = "background:#ccc; border: 1px solid #f00; color:#000;";
  var convertTypeToDomDelta = function(type, types) {
    if (type.nature == "reactive_ref" || type.nature == "native_ref") {
      return convertTypeToDomDelta(types[type.ref], types);
    }
    if (type.nature == "native_maybe" || type.nature == "reactive_maybe") {
      var dom = document.createElement("span");
      var result = convertTypeToDomDelta(type.type, types);
      return {
        dom: dom,
        name: "maybe&lt;" + result.name + "&gt;",
        update: [result.update, function(value) {
          if (value == null) {
            dom.remove(result.dom);
            dom.innerHTML = "<i>null</i>";
          } else {
            dom.innerHTML = "";
            dom.append(result.dom);
          }
        }]
      };
    }
    if (type.nature == "native_list" || type.nature == "native_array") {
      var dummy = convertTypeToDomDelta(type.type, types);
      var recordType = extractRecordType(type.type, types);
      if (recordType != null) {
        var tableControls = document.createElement("span");
        var tableHolder = document.createElement("table");
        tableControls.append(tableHolder);
        var tableHeader = document.createElement("thead");
        tableHolder.append(tableHeader);
        var headerRow = document.createElement("tr");
        tableHolder.append(headerRow);
        var colorStyle = "border: 1px solid #3b54da;";
        for (var fieldName in recordType.fields) {
          var field = recordType.fields[fieldName];
          if ("private" == field.privacy) {
            continue;
          }
          var result = convertTypeToDomDelta(field.type, types);
          var td = document.createElement("td");
          td.innerHTML = result.name + " " + fieldName;
          headerRow.append(td);
          td.style = colorStyle + "padding-left: 0.5em; padding-right: 0.5em;"
        }

        tableHolder.style = colorStyle + "padding: 0.5em; margin:0.5em; display:none"
        headerRow.style = colorStyle;

        var tableBody = document.createElement("tbody");
        tableHolder.append(tableBody);
        var rowsByKey = {};
        var paging = {
          shown:false,
          order:[],
          page: 0,
          page_limit: 10,
        };
        var controller = document.createElement("span");
        tableControls.append(controller);
        paging.reform =function() {
          nuke(tableBody);
          var start = Math.min(this.page * this.page_limit, this.order.length - this.page_limit);
          for (var at = start; at < start + paging.page_limit; at++) {
            var row = rowsByKey[paging.order[at]];
            if (row) {
              tableBody.append(row);
            }
          }
          nuke(controller);
          if (this.shown) {
            var pages = this.order.length / this.page_limit;
            if (pages > 1) {
              for (var k = 0; k < pages; k++) {
                var gotoPage = document.createElement("button");
                gotoPage.style = buttonStyle;
                gotoPage.innerHTML = "[PG:" + k + "]";
                gotoPage.onclick = function () {
                  this.paging.page = this.pg;
                  this.paging.reform();
                }.bind({paging: this, pg: k});
                controller.append(gotoPage);
              }
            }
          }
          var showhide = document.createElement("button");
          showhide.style = buttonStyle;
          showhide.innerHTML = this.shown ? "hide" : "+";
          showhide.onclick = function() {
            this.shown = !this.shown;
            if (this.shown) {
              tableHolder.style.display = "";
            } else {
              tableHolder.style.display = "none";
            }
            this.reform();
          }.bind(this);
          controller.append(showhide);
        }.bind(paging);


        return {
          dom: tableControls,
          name: "list&lt;" + dummy.name + "&gt;",
          update: {
            '+': function (key) {
              var row = document.createElement("tr");
              rowsByKey[key] = row;
              var delta = {};
              for (var fieldName in recordType.fields) {
                var field = recordType.fields[fieldName];
                if ("private" == field.privacy) {
                  continue;
                }
                var result = convertTypeToDomDelta(field.type, types);
                var td = document.createElement("td");
                td.style = colorStyle + "padding: 0.25em; text-align:top";
                td.append(result.dom);
                row.append(td);
                delta[fieldName] = result.update;
              }
              return delta;
            },
            '-': function (key) {
              var dom = rowsByKey[key];
              tableBody.remove(dom);
              delete rowsByKey[key];
            },
            '~': function (order) {
              if (order != null) {
                paging.order = order;
                paging.reform();
              }
            }
          }
        }
      } else {
        var listHolder = document.createElement("div");
        var domByKey = {};
        return {
          dom: listHolder,
          name: "list<" + dummy.name + ">",
          update: {
            '+': function (key) {
              var itemHolder = document.createElement("span");
              var result = convertTypeToDomDelta(type.type, types);
              itemHolder.append(result.dom);
              itemHolder.append(simple("span", ", "));
              domByKey[key] = itemHolder;
              return result.update;
            },
            '-': function (key) {
              var dom = domByKey[key];
              listHolder.remove(dom);
              delete domByKey[key];
            },
            '~': function (order) {
            }
          }
        };
      }
    }
    if (type.nature == "reactive_value" || type.nature == "native_value") {
      var item = document.createElement("span");
      item.innerHTML = "null";
      item.style = "color: #77a5ff;";
      var update = function(value) { item.innerHTML = value; };
      if (type.type == "string") {
        item.style = "color: #77d656";
        update = function(value) { item.innerHTML = "\"" + value + "\""; };
      } else if (type.type == "principal") {
        item.style = "color: #BA83FF;";
        update = function(value) { item.innerHTML = value != null ? (value.agent + "@" + value.authority) : "null"; };
      } else if (type.type == "bool") {
        item.style = "color: #E83B3B;";
      }
      return {
        dom:item,
        name: type.type,
        update: update
      };
    }
    if (type.nature == "reactive_record" || type.nature == "native_message") { // an object
      var obj_holder = document.createElement("span");
      var showhide = document.createElement("button");
      showhide.style = buttonStyle;
      obj_holder.append(showhide);
      obj_holder.showGroup = type.name != "Root";

      var group = document.createElement("div");
      obj_holder.append(group);
      group.append(simple("div", "{"));
      var children = document.createElement("ul");
      group.append(children);
      group.append(simple("div", "}"));
      group.style = "padding-left:10px";
      children.style = "padding-left:10px;";
      showhide.onclick = function() {
        obj_holder.showGroup = ! obj_holder.showGroup;
        if (obj_holder.showGroup) {
          group.style.display = "";
          showhide.innerHTML = "-";
        } else {
          group.style.display = "none";
          showhide.innerHTML = "+";
        }
      };
      showhide.onclick();

      //showhide.onclick = function() {
//        children.shown = !children.shown;
//        children.style.display = children.shown ? "" : "none";
//      };

      var delta = {};
      for (var fieldName in type.fields) {
        var field = type.fields[fieldName];
        if ("private" == field.privacy) {
          continue;
        }
        var result = convertTypeToDomDelta(field.type, types);
        var fieldDom = document.createElement("li");
        fieldDom.append(simple("span",  field.privacy + " " + result.name + " " + fieldName + " = "));
        fieldDom.append(result.dom);
        children.append(fieldDom);
        delta[fieldName] = result.update;
      }
      return {
        dom: obj_holder,
        name: type.name,
        update:delta
      };
    }
    var unknown = document.createElement("div");
    unknown.innerHTML = JSON.stringify(type);
    return {dom: unknown, update: function() {}};
  };

  var render = function(name) {
    var co = connections[name];
    if (co.rendered == co.bound) {
      return;
    }
    co.rendered = co.bound;
    co.debug_tree.nuke();
    /*
    viewJson.innerHTML = co.debug_tree.str();
    co.debug_tree.subscribe(function() {
      viewJson.innerHTML = turn_json_into_html(co.debug_tree.raw());
    });
    */
    var handler = {
      success: function (payload) {
        var reflection = payload.reflection;
        var result = convertTypeToDomDelta(reflection.types["__Root"], reflection.types);
        viewJson.append(result.dom);
        co.debug_tree.subscribe(result.update);
      },
      failure: function(reason) {
        console.log("Failed to get schema:" + reason);
      }
    };
    if (co.via_domain) {
      co.raw.DomainReflect(co.identity, co.domain, handler);
    } else {
      co.raw.SpaceReflect(co.identity, co.space, co.key, handler);
    }
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
      try {
        document.body.removeChild(self.root);
      } catch (notpresent) {
        self.shown = !self.shown;
        document.body.append(self.root);
      }
    }
    else
    {
      self.root = document.createElement("div");
      debuggerPosition = localStorage.getItem("AdamaDebuggerPosition");
      if (!debuggerPosition || debuggerPosition == "") {
        debuggerPosition = "top:0px; left:0px;width:800px;height:600px";
      }
      self.root.style = "position:absolute;" + debuggerPosition + ";background: #000;border:1px solid red;z-index:1000; overflow:scroll";
      var titleBar = document.createElement("div");
      titleBar.innerHTML = "Adama Debugger";
      titleBar.style = "background: #222; color: #0f0; cursor:move; width:100%; font-variant: small-caps; padding:2px; text-align: center;";
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
          localStorage.setItem("AdamaDebuggerPosition", "top:"+self.root.style.top+"; left:"+self.root.style.left+";width:800px;height:600px");
        };
        window.onmouseup = function(nx) {
          update(nx);
          localStorage.setItem("AdamaDebuggerPosition", "top:"+self.root.style.top+"; left:"+self.root.style.left+";width:800px;height:600px");
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
/** The Amazing Internal Debugger for RxHTML Connection */
Adama.Debugger = (function() {
  var self = {};
  self.shown = false;
  var connections = {};
  var current = null;

  /** Helper | remove all the children of a parent */
  var nuke = function (parent) {
    var last = parent.lastChild;
    while (last) {
      parent.removeChild(last);
      last = parent.lastChild;
    }
  };

  /** Helper | create a simple element with the given content as the innerHTML */
  var simple = function(tag, content) {
    var d = document.createElement(tag);
    d.innerHTML = content;
    return d;
  }

  var buttonStyle = "background:#333; border: 1px solid #f00; color:#fff; padding:3px; margin:3px; border-radius: 3px;";
  var inputStyle = "background:#eee; color:#000;";
  var plusSVG = "<svg xmlns=\"http://www.w3.org/2000/svg\" fill=\"none\" viewBox=\"0 0 24 24\" stroke-width=\"1.5\" stroke=\"#77d656\" style=\"width:18px; height:18px;\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M12 9v6m3-3H9m12 0a9 9 0 11-18 0 9 9 0 0118 0z\" /></svg>";
  var minusSVG = "<svg xmlns=\"http://www.w3.org/2000/svg\" fill=\"none\" viewBox=\"0 0 24 24\" stroke-width=\"1.5\" stroke=\"#f00\" style=\"width:18px; height:18px;\"><path stroke-linecap=\"round\" stroke-linejoin=\"round\" d=\"M15 12H9m12 0a9 9 0 11-18 0 9 9 0 0118 0z\" /></svg>";

  // we are going to create a document
  var viewJson = document.createElement("div");
  var viewChannels = document.createElement("div");

  viewJson.style = "font-family: \"JetBrains Mono\", source-code-pro, Menlo, Monaco, Consolas, monospace; font-size: 14px; color: #fff; background-color: #000; width:100%"
  viewChannels.style = "font-family: \"JetBrains Mono\", source-code-pro, Menlo, Monaco, Consolas, monospace; font-size: 14px; color: #fff; background-color: #000; width:100%"

  /** Reflection | given a reflected type, extract the structure type **/
  var extractStructureType = function(type, types) {
    if (type.nature == "reactive_ref" || type.nature == "native_ref") {
      return extractStructureType(types[type.ref], types);
    }
    if (type.nature == "reactive_record" || type.nature == "native_message") {
      return type;
    }
    return null;
  }

  /** TreeView | Given a type (and a type forest) create a 'named' dom element
   *  along with an update delta. The update delta is a functional (or structural functional)
   *  closure that will update the dom element such that it can be attached to an Adama Tree */
  var convertTypeToDomDelta = function(type, types) {
    if (type.nature == "reactive_ref" || type.nature == "native_ref") {
      // go to the forest and turn the ref into a manifested type
      return convertTypeToDomDelta(types[type.ref], types);
    }
    if (type.nature == "native_maybe" || type.nature == "reactive_maybe") {
      // a maybe is just a wrapper around another type that can null out
      var dom = document.createElement("span");
      var result = convertTypeToDomDelta(type.type, types);
      return {
        dom: dom,
        name: "maybe&lt;" + result.name + "&gt;",
        update: [result.update, function(value) {
          nuke(dom);
          if (value == null) {
            dom.innerHTML = "<i>null</i>";
          } else {
            dom.append(result.dom);
          }
        }]
      };
    }
    if (type.nature == "native_list" || type.nature == "native_array") {
      // this is the most complex type as it requires a differential structure.

      // we create a dummy to get the name of the type
      var dummy = convertTypeToDomDelta(type.type, types);

      // so, we have two considerations. First, is this a list of structures OR a list of values.
      var recordType = extractStructureType(type.type, types);
      if (recordType != null) {
        // it is a list of structures, so we want to create a nice looking table with pagination!
        var tableControls = document.createElement("span");
        var showhide = document.createElement("button");
        tableControls.append(showhide);

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
          showhide.innerHTML = this.shown ? minusSVG : plusSVG;
          showhide.onclick = function() {
            this.shown = !this.shown;
            if (this.shown) {
              tableHolder.style.display = "";
            } else {
              tableHolder.style.display = "none";
            }
            this.reform();
          }.bind(this);
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
        // not a record, so we use a simple list
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
      // it's just data, so create a single node to draw it
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
    if (type.nature == "reactive_record" || type.nature == "native_message") {
      // it's a structure, so let's create a record that can show/hide
      var obj_holder = document.createElement("span");
      var showhide = document.createElement("button");
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
          showhide.innerHTML = minusSVG;
        } else {
          group.style.display = "none";
          showhide.innerHTML = plusSVG;
        }
      };
      showhide.onclick();

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

  self.formIdGen = 0;
  var nextFormId = function() {
    self.formIdGen++;
    return self.formIdGen;
  };
  var fillOutFormForType = function(dom, name, type, types) {
    if (type.nature == "native_ref") {
      fillOutFormForType(dom, name, types[type.ref], types);
      return;
    }
    if (type.nature == "native_message") {
      for (var field in type.fields) {
        var fieldType = type.fields[field].type;
        var fieldHolder = document.createElement("div");
        fillOutFormForType(fieldHolder, name == "" ? field : (name + "." + field), fieldType, types);
        dom.append(fieldHolder);
      }
      return;
    }
    if (type.nature == "native_array") {
      // TODO
    }
    if (type.nature == "native_maybe") {
      var maybeHolder = document.createElement("div");
      var label = document.createElement("label");
      label.innerHTML = "enable " + name;
      var checkbox = document.createElement("input");
      checkbox.type = "checkbox";
      checkbox.id = nextFormId();
      checkbox.name = "";
      label.htmlFor = checkbox.id;
      var maybeValueRef = document.createElement("div");
      var maybeValue = document.createElement("div");
      fillOutFormForType(maybeValue, name, type.type, types);
      maybeHolder.append(label);
      maybeHolder.append(checkbox);
      maybeHolder.append(maybeValueRef);
      dom.append(maybeHolder);
      checkbox.onchange = function() {
        nuke(maybeValueRef);
        if (checkbox.checked) {
          maybeValueRef.append(maybeValue);
        }
      };
      return;
    }
    if (type.nature == "native_value") {
      var label = document.createElement("label");
      var input = document.createElement("input");

      input.id = "debugger_form_id_" + nextFormId();
      label.innerHTML = name;
      input.name = name;
      input.style = inputStyle;
      input.type = "text";
      label.forHtml = input.id;
      dom.append(label);
      dom.append(input);
      return;
    }
    dom.innerHTML = name + "-->" + JSON.stringify(type);
  };

  var makeChannelDebugger = function(channels, types, connection) {
    var selector = document.createElement("select");
    selector.style = "background: #333; color:#fff;";
    viewChannels.append(selector);
    var formHolder = document.createElement("div");
    viewChannels.append(formHolder);
    var choose = function(channel) {
      nuke(formHolder);
      var form = document.createElement("form");
      formHolder.append(form);
      var type = types[channels[channel]];
      fillOutFormForType(form, "", type, types);

      var button = document.createElement("button");
      button.style = buttonStyle;
      button.onclick = function(event) {
        event.preventDefault();
        var message = RxHTML.BuildFormObject(form);
        button.innerHTML = "Sending";
        console.log(channel + ":" + JSON.stringify(message));
        connection.send(channel, message, {
          success: function() {
            button.innerHTML = "Success! Send Another!";
          },
          failure: function(reason) {
            button.innerHTML = "[Failed:" + reason + "], Try Again";
          }
        })

      };
      button.innerHTML = "Send";
      form.append(button);
      // BuildFormObject
    };
    var first = null;
    var channelsSorted = [];
    for (var ch in channels) {
      channelsSorted.push(ch);
    }
    channelsSorted.sort();
    for (var k in channelsSorted) {
      var ch = channelsSorted[k];
      if (first == null) {
        first = ch;
      }
      var msgType = channels[ch];
      var opt = document.createElement("option");
      opt.innerHTML = ch + " : " + msgType;
      opt.label = ch + " : " + msgType;
      opt.value = ch;
      selector.append(opt);
      selector.onchange = function() {
        choose(selector.value);
      };
    }

    if (first != null) {
      choose(first);
    }
  };

  var render = function(name) {
    var co = connections[name];
    if (co.rendered == co.bound) {
      return;
    }
    co.rendered = co.bound;
    co.debug_tree.nuke();
    nuke(viewJson);
    nuke(viewChannels);

    var handler = {
      success: function (payload) {
        // handle the data inspector tab
        var reflection = payload.reflection;
        var result = convertTypeToDomDelta(reflection.types["__Root"], reflection.types);
        viewJson.append(result.dom);
        co.debug_tree.subscribe(result.update);
        // handle the channels tab
        makeChannelDebugger(reflection.channels, reflection.types, co.ptr);
      },
      failure: function(reason) {
        viewJson.innerHTML = "[FAILED TO GET SCHEMA:" + reason + "]";
        viewChannels.innerHTML = "[FAILED TO GET SCHEMA:" + reason + "]";
      }
    };
    if (co.via_billing) {
      viewJson.innerHTML = "Bililng document not supported";
      viewChannels.innerHTML = viewJson.innerHTML;
      return;
    }
    if (co.via_domain) {
      co.raw.DomainReflect(co.identity, co.domain, handler);
    } else {
      co.raw.SpaceReflect(co.identity, co.space, co.key, handler);
    }
  };

  var connectionSelector = document.createElement("select");
  connectionSelector.style = "background: #333; color: #fff";
  var switch_to_connection = function(name) {
    current = name;
    nuke(viewChannels);
    nuke(viewJson);
    connections[name].rendered = "";
    render(name);
  };
  connectionSelector.onchange = function() {
    switch_to_connection(connectionSelector.value);
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
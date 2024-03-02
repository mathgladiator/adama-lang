var __graph = {};
var __todo = [];
var __state = {
  execute_form: false,
  form_executing: "",
  idgen: 0
};

function __create(name) {
  return {name: name, visited: false, tests: {}, need_snapshot: true, sub_name: ""};
}

function __getCurrentLocationAndIndex() {
  var current = window.location.pathname + window.location.search;
  if (!(current in __graph)) {
    __graph[current] = __create(current);
  }
  if (!__graph[current].visited) {
    __graph[current].visited = true;
  }

  var links = document.getElementsByTagName("a");
  for (var k = 0; k < links.length; k++) {
    if ("href" in links[k]) {
      var next = links[k].href;
      if (!(next in __graph)) {
        __graph[next] = __create(next);
        __todo.push(next);
      }
    }
  }

  return __graph[current];
}

function __indexCurrent(current) {
  var created = [];
  // TODO: look for all the elements with test:$name:set=
  var index = function (dom) {
    for (const attr of dom.attributes) {
      if (attr.name.startsWith("test:")) {
        var testNameAndCommand = attr.name.substring(5);
        var kColon = testNameAndCommand.indexOf(":");
        if (kColon > 0) {
          var testname = testNameAndCommand.substring(0, kColon);
          var command = testNameAndCommand.substring(kColon + 1);
          var test;
          if (!(testname in current.tests)) {
            test = {name: testname, sets: {}, click: null, ready: false, ran: false};
            current.tests[testname] = test;
            created.push(test);
          } else {
            test = current.tests[testname];
          }
          if (!test.ready) {
            if (command == "click") {
              test.click = dom;
            } else if (command.startsWith("set:")) {
              test.sets[command.substring(4)] = {element: dom, value: attr.value};
            }
          }
        }
      }
    }
    if ("children" in dom) {
      var arr = dom.children;
      var n = arr.length;
      for (var k = 0; k < n; k++) {
        index(dom.children[k]);
      }
    }
  };
  index(document.body);

  for (var k = 0; k < created.length; k++) {
    created[k].ready = true;
  }
  return null;
}

function __startTestAgent() {
  console.log("Test Agent Starting Up");
  return true;
}

function __pollTestAgent() {
  var $ = window.rxhtml;
  if ($._inflight == 0) {
    var current = __getCurrentLocationAndIndex();

    if (current.need_snapshot) {
      current.need_snapshot = false;
      return "snapshot:" + current.name + current.sub_name;
    }

    if (__todo.length > 0) {
      var uri = __todo.pop();
      console.log("going to:" + uri);
      $.goto(uri);
      return "wait:25";
    }

    // index the current
    __indexCurrent(current);

    for (var testname in current.tests) {
      var test = current.tests[testname];
      if (!test.ran) {
        test.ran = true;
        if (test.click) {
          if (!test.click.id) {
            __state.idgen++;
            test.click.id = "__auto_click_id_" + __state.idgen;
          }
          current.need_snapshot = true;
          current.sub_name = "##" + testname;
          return "click:" + test.click.id;
        }
      }
    }
    return "done";
  } else {
    console.log("(instable)");
  }
  return "wait:50";
}
import { Tree } from './../src/tree';

describe("tree updates", function () {
  it("merge in values", function () {
    var tree = new Tree();
    tree.mergeUpdate({ data: { x: 123 } });
    expect(JSON.stringify(tree.tree)).toBe("{\"x\":123}");
  });
  it("learn of new data", function () {
    var tree = new Tree();
    let vals: Array<string> = [];
    tree.onTreeChange({'+x': function(delta: any) {
        vals.push("new:" + delta.key + "-->" + delta.value);
      }
    });
    tree.mergeUpdate({ data: { x: 123 } });
    expect(JSON.stringify(vals)).toBe("[\"new:x-->123\"]");
  });
  it("learn of new data only once in stream", function () {
    var tree = new Tree();
    let vals: Array<string> = [];
    tree.onTreeChange({'+x': function(delta: any) {
        vals.push("new:" + delta.key + "-->" + delta.value);
      }
    });
    tree.mergeUpdate({ data: { x: 123 } });
    tree.mergeUpdate({ data: { x: 52 } });
    tree.mergeUpdate({ data: { x: 32 } });
    expect(JSON.stringify(vals)).toBe("[\"new:x-->123\"]");
  });
  it("learn of delete", function () {
    var tree = new Tree();
    let vals: Array<string> = [];
    tree.onTreeChange({'+x': function(delta: any) {
        vals.push("new:" + delta.key + "-->" + delta.value);
      }
    });
    tree.onTreeChange({'-x': function(delta: any) {
        vals.push("delete:" + delta.key + "-->" + delta.before);
      }
    });
    tree.mergeUpdate({ data: { x: 123 } });
    tree.mergeUpdate({ data: { x: null } });
    expect(JSON.stringify(vals)).toBe("[\"new:x-->123\",\"delete:x-->123\"]");
  });
  it("learn of recreate", function () {
    var tree = new Tree();
    let vals: Array<string> = [];
    tree.onTreeChange({'+x': function(delta: any) {
        vals.push("new:" + delta.key + "-->" + delta.value);
      }
    });
    tree.onTreeChange({'-x': function(delta: any) {
        vals.push("delete:" + delta.key + "-->" + delta.before);
      }
    });
    tree.mergeUpdate({ data: { x: 123 } });
    tree.mergeUpdate({ data: { x: null } });
    tree.mergeUpdate({ data: { x: 42 } });
    expect(JSON.stringify(vals)).toBe("[\"new:x-->123\",\"delete:x-->123\",\"new:x-->42\"]");
  });
  it("learn of value changes", function () {
    var tree = new Tree();
    let vals: Array<string> = [];
    tree.onTreeChange({'x': function(delta: any) {
        vals.push("change:" + delta.key + "-->" + delta.before + "-->" + delta.value);
      }
    });
    tree.mergeUpdate({ data: { x: 123 } });
    tree.mergeUpdate({ data: { x: 52 } });
    tree.mergeUpdate({ data: { x: 32 } });
    tree.mergeUpdate({ data: { x: null } });
    expect(JSON.stringify(vals)).toBe("[\"change:x-->null-->123\",\"change:x-->123-->52\",\"change:x-->52-->32\",\"change:x-->32-->null\"]");
  });
  it("learn of child changes", function () {
    var tree = new Tree();
    tree.mergeUpdate({ data: { x: 123 } });
    var changes: any = [];
    tree.onTreeChange({
      x: function (change: any) {
        changes.push(change);
      }
    });
    expect(changes.length).toBe(0);
    tree.mergeUpdate({ data: { x: 42 } });
    expect(JSON.stringify(tree.tree)).toBe("{\"x\":42}");
    expect(changes.length).toBe(1);
    expect(changes[0].value).toBe(42);
    expect(changes[0].before).toBe(123);
  });
  it("learn of child ordered", function () {
    var tree = new Tree();
    tree.mergeUpdate({ data: { x: 123, y: 100 } });
    var changes: any = [];
    tree.onTreeChange({
      y: function (change: any) {
        changes.push(change);
      }
    });
    tree.onTreeChange({
      x: function (change: any) {
        changes.push(change);
      }
    });
    expect(changes.length).toBe(0);
    tree.mergeUpdate({ data: { x: 42, y: 7 } });
    expect(JSON.stringify(tree.tree)).toBe("{\"x\":42,\"y\":7}");
    expect(changes.length).toBe(2);
    expect(changes[0].value).toBe(7);
    expect(changes[0].before).toBe(100);
    expect(changes[1].value).toBe(42);
    expect(changes[1].before).toBe(123);
  });
  it("combined dispatch", function () {
    var tree = new Tree();
    tree.mergeUpdate({ data: { x: 123, y: 100 } });
    var changes: any = [];
    tree.onTreeChange({
      y: function (change: any) {
        changes.push(change);
      },
      x: function (change: any) {
        changes.push(change);
      }
    });
    expect(changes.length).toBe(0);
    tree.mergeUpdate({ data: { x: 42, y: 7 } });
    expect(JSON.stringify(tree.tree)).toBe("{\"x\":42,\"y\":7}");
    expect(changes.length).toBe(2);
    expect(changes[0].value).toBe(42);
    expect(changes[0].before).toBe(123);
    expect(changes[1].value).toBe(7);
    expect(changes[1].before).toBe(100);
  });
  it("learn of child-child changes", function () {
    var tree = new Tree();
    tree.mergeUpdate({ data: { z: { x: 123 } } });
    var changes: any = [];
    tree.onTreeChange({
      z: {
        x: function (change: any) {
          changes.push(change);
        }
      }
    });
    expect(changes.length).toBe(0);
    tree.mergeUpdate({ data: { z: { x: 42 } } });
    expect(JSON.stringify(tree.tree)).toBe("{\"z\":{\"x\":42}}");
    expect(changes.length).toBe(1);
    expect(changes[0].value).toBe(42);
    expect(changes[0].before).toBe(123);
  });

  it("learn array", function () {
    var tree = new Tree();
    var changes: any = [];
    tree.onTreeChange({
      '+arr': function (add: any) {
        changes.push("add:" + add.key + ":" + JSON.stringify(add.value));
      }
    });
    tree.onTreeChange({
      'arr': function (change: any) {
        changes.push("change:" + JSON.stringify(change.before) + " -> " + JSON.stringify(change.value));
      }
    });
    tree.mergeUpdate({ data: { arr: { "0": {name:"Mr.0"}, "@o":[0], }}});
    expect(JSON.stringify(changes)).toBe("[\"add:arr:[{\\\"name\\\":\\\"Mr.0\\\"}]\",\"change:[] -> [{\\\"name\\\":\\\"Mr.0\\\"}]\"]");
  });

  it("learn of array element by id", function () {
    var tree = new Tree();
    var changes: any = [];
    tree.onTreeChange({
      'arr': {
        "#": function (change: any) { // this will tell me the value of the object AFTER it is built
          changes.push("value:" + JSON.stringify(change.value));
        },
      }
    });
    tree.mergeUpdate({ data: { arr: { "0": {name:"Mr.0"}, "@o":[0], }}});
    expect(JSON.stringify(changes)).toBe("[\"value:{\\\"name\\\":\\\"Mr.0\\\"}\"]");
  });


  it("learn of array element churn", function () {
    var tree = new Tree();
    var changes: any = [];
    tree.onTreeChange({
      'arr': {
        '+': function (change: any) { // this will tell me the value of the object AFTER it is built
          changes.push("new:" + change.key);
        },
        '-': function (change: any) { // this will tell me the value of the object AFTER it is built
          changes.push("delete:" + change.key);
        },
      }
    });
    tree.mergeUpdate({ data: { arr: { "0": {name:"Mr.0"}, "@o":[0], }}});
    tree.mergeUpdate({ data: { arr: { "0": {name:"Mr.0"}, "5": {name:"Mr.Z"}, "@o":[0,5], }}});
    tree.mergeUpdate({ data: { arr: { "0": null, "5": {name:"Mr.Z"}, "@o":[5], }}});
    expect(JSON.stringify(changes)).toBe("[\"new:0\",\"new:5\",\"delete:0\"]");
  });

  it("order change severe executes delete", function () {
    var tree = new Tree();
    var changes: any = [];
    tree.onTreeChange({
      'arr': {
        '+': function (change: any) { // this will tell me the value of the object AFTER it is built
          changes.push("new:" + change.key);
        },
        '-': function (change: any) { // this will tell me the value of the object AFTER it is built
          changes.push("delete:" + change.key);
        },
      }
    });
    tree.mergeUpdate({ data: { arr: { "0": {name:"Mr.0"}, "5": {name:"Mr.Z"}, "@o":[0,5], }}});
    tree.mergeUpdate({ data: { arr: { "@o":[], }}});
    expect(JSON.stringify(changes)).toBe("[\"new:0\",\"new:5\",\"delete:0\",\"delete:5\"]");
  });


  it("clean up of changes", function () {
    var tree = new Tree();
    var changes: any = [];
    tree.onTreeChange({
      'arr': {
        '+': function (change: any) { // this will tell me the value of the object AFTER it is built
          changes.push("new:" + change.key);
          return 'delete';
        },
        '-': function (change: any) { // this will tell me the value of the object AFTER it is built
          changes.push("delete:" + change.key);
          return 'delete';
        },
      }
    });
    tree.mergeUpdate({ data: { arr: { "0": {name:"Mr.0"}, "@o":[0], }}});
    tree.mergeUpdate({ data: { arr: { "5": {name:"Mr.Z"}, "@o":[0,5], }}});
    tree.mergeUpdate({ data: { arr: { "0": null, "@o":[5], }}});
    tree.mergeUpdate({ data: { arr: { "5": null, "@o":[], }}});
    expect(JSON.stringify(changes)).toBe("[\"new:0\",\"delete:0\"]");
  });

  it("recursive delete", function () {
    var tree = new Tree();
    var changes: any = [];
    tree.onTreeChange({ x: {y:{'+z': function(change: any) {
      changes.push("new:" + change.value);
    }}}});
    tree.onTreeChange({ x: {y:{'-z': function(change: any) {
      changes.push("delete:" + change.before);
    }}}});
    tree.mergeUpdate({ data: { x: { y: { z: 42 }  }}});
    tree.mergeUpdate({ data: { x: null }});
    expect(JSON.stringify(changes)).toBe("[\"new:42\",\"delete:42\"]");
  });

});
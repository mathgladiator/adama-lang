import { Tree } from './../src/tree';

describe("tree updates", function () {
  it("merge in values", function () {
    var tree = new Tree();
    tree.mergeUpdate({ data: { x: 123 } });
    expect(JSON.stringify(tree.tree)).toBe("{\"x\":123}");
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
    expect(changes[0].after).toBe(42);
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
    expect(changes[0].after).toBe(7);
    expect(changes[0].before).toBe(100);
    expect(changes[1].after).toBe(42);
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
    expect(changes[0].after).toBe(42);
    expect(changes[0].before).toBe(123);
    expect(changes[1].after).toBe(7);
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
    expect(changes[0].after).toBe(42);
    expect(changes[0].before).toBe(123);
  });
});
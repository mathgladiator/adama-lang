
export class Tree {
  tree: object;
  dispatch: object;
  dispatch_count: number;
  queue: Array<any>;
  onfetch: (channel: string) => void;
  ondecide: (channel: string, options: Array<object>) => void;

  constructor() {
    this.tree = {};
    this.dispatch = {};
    this.dispatch_count = 0;
    this.queue = [];
    this.onfetch = function (channel: string) { };
    this.ondecide = function (channel: string, options: Array<object>) { };
  }

  __recAppendChange(dispatch: any, callback: any, insert_order: number) {
    if (typeof (callback) == 'object') {
      for (var key in callback) {
        if (!(key in dispatch)) {
          dispatch[key] = {};
        }
        this.__recAppendChange(dispatch[key], callback[key], insert_order);
      }
    } else if (typeof (callback) == 'function') {
      if (!('@e' in dispatch)) {
        dispatch['@e'] = [];
      }
      dispatch['@e'].push({ cb: callback, order: insert_order });
    }
  }

  onTreeChange(callback: any) {
    this.__recAppendChange(this.dispatch, callback, this.dispatch_count);
    this.dispatch_count++;
  }

  mergeUpdate(diff: any) {
    if ('data' in diff) {
      this.__recMergeAndDispatch(this.tree, this.dispatch, diff.data)
    }
    // TODO: dispatch decisions
    // TODO: update blockers
    this.__drain();
  }

  __recMergeAndDispatchArray(prior: any, dispatch: any, tree: any, diff: any) {
    var ordering = null;
    var resize = null;
    for (var key in diff) {
      if (key == "@o") {
        ordering = diff[key];
      } else if (key == "@s") {
        resize = diff[key];
      } else {
        if (diff[key] == null) {
          // FIRE_DELETE_ELEMENT
          delete tree[key];
        } else {
          if (!(tree != null && key in tree)) {
            tree[key] = {};
          }
          this.__recMergeAndDispatch(tree[key], (dispatch != null && key in dispatch) ? dispatch[key] : null, diff[key]);
          // this will fire an update for the key
        }
      }
    }
    var change = { before: prior, after: prior };
    if (resize !== null) {
      // reforge the prior
      // TODO: sort this out
    }
    if (ordering !== null) {
      var after = [];
      change.before = [];
      for (var k = 0; k < prior.length; k++) {
        change.before.push(prior[k]);
      }
      for (var k = 0; k < ordering.length; k++) {
        var instr = ordering[k];
        if (typeof (instr) == "string") {
          after.push(tree[instr]);
        } else if (typeof (instr) == "object") {
          if ("@r" in instr) {
            var start = instr["@r"][0];
            var end = instr["@r"][1];
            for (var j = start; j <= end; j++) {
              after.push(prior[j]);
            }
          }
        }
      }
      prior.length = after.length;
      for (var k = 0; k < after.length; k++) {
        prior[k] = after[k];
      }
    }
    this.__fire(dispatch, change);
    // TODO: fire events for the update
  }
  __fire(dispatch: any, change: any) {
    if (dispatch) {
      if ('@e' in dispatch) {
        var d = dispatch['@e'];
        for (var k = 0; k < d.length; k++) {
          var evt = d[k];
          this.queue.push({ cb: evt.cb, order: evt.order, change: change });
        }
      }
    }
  }
  __drain() {
    this.queue.sort(function (a: any, b: any) { return a.order - b.order; });
    for (var k = 0; k < this.queue.length; k++) {
      var item = this.queue[k];
      item.cb(item.change);
    }
    this.queue = [];
  }
  __recMergeAndDispatch(tree: any, dispatch: any, diff: any) {
    // the diff is an object, so let's walk its keys
    for (var key in diff) {
      var child = diff[key];
      if (child === null) {
        if (Array.isArray(tree[key])) {
          delete tree["#" + key];
        }
        // figure out what dispatch means here
        delete tree[key];
        continue;
      }
      // if the child is an object, then..
      if (typeof (child) == 'object') {
        // the child is either an ARRAY or a OBJECT
        var childIsArray = '@o' in child || '@s' in child;
        // the prior version doesn't exist, so we create the empty node so that it does exist
        if (!(key in tree)) {
          if (childIsArray) {
            tree[key] = [];
            tree["#" + key] = {};
          } else {
            tree[key] = {};
          }
        }
        // now, we check to see if the prior state influences whether or not the diff is an array
        childIsArray = Array.isArray(tree[key]) || childIsArray;
        if (childIsArray) {
          this.__recMergeAndDispatchArray(tree[key], (dispatch != null && key in dispatch) ? dispatch[key] : null, tree["#" + key], child);
        } else {
          // we have an object, let's merge recursively... YAY
          this.__recMergeAndDispatch(tree[key], (dispatch != null && key in dispatch) ? dispatch[key] : null, child);
          // NOTE: this will fire events
        }
      } else {
        var change = { before: tree[key], after: child, parent: tree};
        tree[key] = child;
        if (dispatch != null && key in dispatch) {
          this.__fire(dispatch[key], change);
        }
      }
      if (dispatch != null && '@e' in dispatch) {
        this.__fire(dispatch, tree);
      }
    }
  }
}


export class Tree {
  tree: object;
  dispatch: object;
  dispatch_count: number;
  queue: Array<any>;
  ondecide: (outstanding: any) => void;

  constructor() {
    this.tree = {};
    this.dispatch = {};
    this.dispatch_count = 0;
    this.queue = [];
    this.ondecide = function (outstanding: any) { };
  }

  // recursively append a change
  // dispatch is the structural object mirroring the tree
  // callback is the function/object callback tree
  // insert_order is the order to fire events
  __recAppendChange(dispatch: any, callback: any, insert_order: number) {
    // the callback is an object
    if (typeof (callback) == 'object') {
      // we for each item in the callback
      for (var key in callback) {
        // make sure it exists
        if (!(key in dispatch)) {
          dispatch[key] = {};
        }
        // recurse into that key
        this.__recAppendChange(dispatch[key], callback[key], insert_order);
      }
    } else if (typeof (callback) == 'function') {
      // we have a function, so let's associate it to the node
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

  // the main function
  mergeUpdate(diff: any) {
    if ('data' in diff) {
      // we merge the tree with the data within dispatch
      this.__recMergeAndDispatch(this.tree, this.dispatch, diff.data)
    }
    if ('outstanding' in diff) {
      this.ondecide(diff.outstanding);
    }
    this.__drain();
  }

  __recDeleteAndDispatch(tree: any, dispatch: any) {
    for (var key in tree) {
      let old = tree[key];

      if (Array.isArray(old)) {
        // need to fire the DELETE
      } else {
        if (key in dispatch) {
          this.__recDeleteAndDispatch(old, dispatch[key]);
        }
      }

      let deleteChildKey = '-' + key;
      if (dispatch != null && deleteChildKey in dispatch) {
        this.__fire(dispatch[deleteChildKey], {key: key, before: old, value: null});
      }
    }
  }

  __recMergeAndDispatch(tree: any, dispatch: any, diff: any) {
    // the diff is an object, so let's walk its keys
    for (var key in diff) {
      var child = diff[key];
      if (child === null) {
        let deleteChildKey = '-' + key;
        let old = tree[key];
        if (dispatch != null && deleteChildKey in dispatch) {
          this.__fire(dispatch[deleteChildKey], {key: key, before: old, value: null});
        }
        if (Array.isArray(old)) {
          // also delete
          let elementKey = "#" + key;
          if (elementKey in tree && elementKey in dispatch) {
            this.__recDeleteAndDispatch(tree[elementKey], dispatch[elementKey]);
          }
          delete tree["#" + key];
        }
        if (key in tree && key in dispatch) {
          this.__recDeleteAndDispatch(tree[key], dispatch[key]);
        }
        delete tree[key];
        if (dispatch != null && key in dispatch) {
          this.__fire(dispatch[key], { key: key, before: old, value: null});
        }
        continue;
      }
      // if the child is an object, then..
      var fireNew = !(key in tree);
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
          this.__recMergeAndDispatch(tree[key], (dispatch != null && key in dispatch) ? dispatch[key] : null, child);
        }
      } else {
        var old = (key in tree) ? tree[key] : null;
        tree[key] = child;
        if (dispatch != null && key in dispatch) {
          this.__fire(dispatch[key], { key: key, before: old, value: child});
        }
      }
      if (fireNew) { // fire new data event
        let newChildKey = '+' + key;
        if (dispatch != null && newChildKey in dispatch) {
          this.__fire(dispatch[newChildKey], {key: key, value:tree[key]});
        }
      }
      if (dispatch != null && '@e' in dispatch) {
        this.__fire(dispatch, {value: tree});
      }
    }
  }
  __recMergeAndDispatchArray(prior: any, dispatch: any, tree: any, diff: any) {

    // TODO: new item... etc

    var ordering = null;
    var resize = null;
    for (var key in diff) {
      if (key == "@o") {
        ordering = diff[key];
      } else if (key == "@s") {
        resize = diff[key];
      } else {
        if (diff[key] == null) {
          if (dispatch && '-' in dispatch) {
            this.__fire(dispatch['-'], {key: key, before: tree[key], value:null});
          }
          delete tree[key];
        } else {
          var fireNew = false;
          if (!(tree != null && key in tree)) {
            if (dispatch && '+' in dispatch) {
              fireNew = true;
            }
            tree[key] = {};
          }
          this.__recMergeAndDispatch(tree[key], (dispatch != null && '#' in dispatch) ? dispatch['#'] : null, diff[key]);
          if (fireNew) {
            this.__fire(dispatch['+'], {key: key, before: null, value: tree[key]});
          }
        }
      }
    }
    var change = { before: prior, value: prior };
    if (resize !== null) {
      // See DList, but the idea is that I need to trim the list because the above tool care of nulls
      // this is for list of values where we synchronize a list of constants
      change.before = [];
      for (var k = 0; k < prior.length; k++) {
        change.before.push(prior[k]);
      }
      prior.length = resize;
      // TODO: validate this
    }
    if (ordering !== null) {
      var after = [];
      change.before = [];
      var implicitDelete = dispatch ? '-' in dispatch : false;
      for (var k = 0; k < prior.length; k++) {
        change.before.push(prior[k]);
        if (implicitDelete) {
          prior[k].__kill = true;
        }
      }
      for (var k = 0; k < ordering.length; k++) {
        var instr = ordering[k];
        var type_instr = typeof (instr);
        if (type_instr == "string" || type_instr == "number") {
          after.push(tree[instr]);
          if (implicitDelete) {
            tree[instr].__kill = false;
          }
        } else {
            var start = instr[0];
            var end = instr[1];
            for (var j = start; j <= end; j++) {
              if (implicitDelete) {
                prior[j].__kill = false;
              }
              after.push(prior[j]);
            }
        }
      }
      if (implicitDelete) {
        for (key in tree) {
          if (tree[key].__kill) {
            if (key in dispatch) {
              this.__recDeleteAndDispatch(tree[key], dispatch[key]);
            }
            this.__fire(dispatch['-'], {key: key, before: tree[key], value:null});
          }
          delete tree[key].__kill;
        }
      }
      prior.length = after.length;
      for (var k = 0; k < after.length; k++) {
        prior[k] = after[k];
      }
    }
    this.__fire(dispatch, change);
  }
  __fire(dispatch: any, change: any) {
    if (dispatch) {
      if ('@e' in dispatch) {
        var d = dispatch['@e'];
        var nulls = 0;
        for (var k = 0; k < d.length; k++) {
          var evt = d[k];
          if (evt !== null) {
            this.queue.push({cb: evt.cb, order: evt.order, change: change, dispatch_list: d, index: k});
          } else {
            nulls++;
          }
        }
        if (nulls > 0) {
          var nxt = [];
          for (var k = 0; k < d.length; k++) {
            if (evt !== null) {
              nxt.push(evt);
            }
          }
          dispatch['@e'] = nxt;
        }
      }
    }
  }
  __drain() {
    this.queue.sort(function (a: any, b: any) { return a.order - b.order; });
    for (var k = 0; k < this.queue.length; k++) {
      var item = this.queue[k];
      if (item.cb(item.change) === 'delete') {
        item.dispatch_list[item.index] = null;
      }
    }
    this.queue = [];
  }
}

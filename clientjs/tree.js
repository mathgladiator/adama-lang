/** a simplified pub/sub differential tree */
function AdamaTree() {
  var id = 0;

  var root = {};
  var all_subscriptions = {};

  var clone_object = function (obj) {
    if (Array.isArray(obj)) {
      var next = [];
      for (var k = 0; k < obj.length; k++) {
        next.push(clone_object(obj[k]));
      }
      return next;
    } else if (typeof (obj) == "object") {
      var next = {};
      for (key in obj) {
        if (key[0] == "#" || key == "__key" || key == "@o") continue;
        next[key] = clone_object(obj[key]);
      }
      return next;
    } else {
      return obj;
    }
  };

  this.nuke = function () {
    this.all_subscriptions = {};
  };

  this.copy = function () {
    return clone_object(root);
  };

  this.str = function () {
    return JSON.stringify(root);
  };

  // filter the subscriptions to the ones that have the key
  var sub = function (s, key) {
    // if we have nothing, then we return nothing
    if (s.length == 0) {
      return s;
    }
    // make a new array
    var n = [];
    for (var k = 0; k < s.length; k++) { // such that every element
      if (key in s[k]) { // which contains the key
        n.push(s[k][key]); // is added to the new array
      }
    }
    return n;
  };

  // fire events at the current subscription
  var fire = function (s, value) {
    // for each subscription
    for (var j = 0; j < s.length; j++) {
      var v = s[j];
      // if it has events
      if ("@e" in v) {
        // fire each event
        var evts = v["@e"];
        for (var k = 0; k < evts.length; k++) {
          evts[k](value);
        }
      }
    }
  };

  var weave = function (s, callback, key) {
    if (Array.isArray(callback)) {
      for (var k = 0; k < callback.length; k++) {
        weave(s, callback[k], key);
      }
    } else if (typeof (callback) == "function") {
      if (!(key in s)) {
        s[key] = {};
      }
      var sk = s[key];
      if (!("@e" in sk)) {
        sk["@e"] = [callback];
      } else {
        sk["@e"].push(callback);
      }
    } else if (typeof (callback) == "object") {
      if (!(key in s)) {
        s[key] = {};
      }
      var sk = s[key];
      for (var ck in callback) {
        if (ck == "@e") {
          weave(s, callback[ck], key);
        } else {
          weave(sk, callback[ck], ck);
        }
      }
    }
  };

  // we have detected an addition
  var fire_add = function (s, key) {
    for (var j = 0; j < s.length; j++) { // for each subscription
      var v = s[j];
      if ("+" in v) { // if the subscription has the ability to add new objects
        var va = v["+"];
        if ("@e" in va) { // if are events associated to '+'
          var eva = va["@e"];
          for (var k = 0; k < eva.length; k++) { // for each '+' ability
            weave(v, eva[k](key), key);
          }
        }
      }
    }
  };

  // fire a delete message for the given key
  var fire_del = function (s, key) {
    for (var j = 0; j < s.length; j++) { // for each subscription
      var v = s[j];
      if ("-" in v) { // if the subscription has the ability to delete prior keys
        var va = v["-"];
        if ("@e" in va) { // if are events associated to '-'
          var eva = va["@e"];
          for (var k = 0; k < eva.length; k++) { // for each '-' ability
            eva[k](key); // evaluate the '-'
          }
        }
      }
    }
  };

  // a delete of a tree has been detected, let's
  var del = function (s) {
    // if the subscriptions is an array, then delete all children
    if (Array.isArray(s)) {
      var n = s.length;
      for (var k = 0; k < n; k++) {
        del(s[k]);
      }
    } else if (typeof (s) == "object") { // otherwise, iterate over the keys
      for (var key in s) {
        var ch = s[key];
        if (key == "@e") { // we have events, so let's send those events...
          for (var j = 0; j < ch.length; j++) {
            ch[j](null); // a null to indicate deletion
          }
        } else { // recurse into the child field
          del(ch);
        }
      }
    }
  };

  // delete the given key within the subscription
  var del_key = function (s, key) {
    if (Array.isArray(s)) { // if we have an array of subscriptions, then simply repeat
      var n = s.length;
      for (var k = 0; k < n; k++) {
        del_key(s[k], key); // for each element
      }
    } else if (typeof (s) == "object") { // otherwise, we have a subcription callback
      if (key in s) { // if the key is in the subscription callback
        delete s[key]; // delete it
      }
    }
  };

  // merge the given delta into the tree and fire subscriptions (s)
  var merge = function (tree, delta, s) {
    for (var key in delta) { // for each bit of data within the tree
      var eKey = "#" + key; // compute the secret data path
      var prior = tree[key]; // find the prior state within the tree
      var next = delta[key]; // bring the change present for the current key
      if (next == null) { // the item is going poof
        // the prior value is an array, so we may have some special things associate with it
        if (Array.isArray(prior)) { // UNTEST
          if (eKey in tree) {
            del(sub(s, eKey));
          }
          delete tree[eKey];
        }
        del(sub(s, key)); // issue a delete for all the subscriptions for the kid
        delete tree[key]; // delete the data in the tree
      } else {
        if (typeof (next) == "object" && !Array.isArray(next)) { // if the delta is an object
          var arr = Array.isArray(prior) || "@o" in next || "@s" in next; // detect if we need to use array logic
          if (arr) { // we have detected an array
            if (!(key in tree)) { // let's make sure the tree has an empty array
              tree[key] = [];
            }
            if (!(eKey in tree)) { // let's make sure the secret data exists as well
              tree[eKey] = {};
              if ("@o" in next) { // if we have a bunch of keys, then let's make sure to preserve that for make_delta
                tree[eKey]["@o"] = true;
              }
              tree[eKey].__key = key; // annotate the new item with the given key
            }
            var eBase = tree[eKey]; // get the secret (and real) data for the object in question
            var eSub = sub(s, key); // scope the subscription to the key
            var ordering = null; // detection of if we have @o ordering to reorder the values
            var resize = null; // detection of an array without __key
            var delay = {}; // we delay all events such that the array can be built for inline make_delta calls to work
            for (var aKey in next) { // for each field within the difference
              var val = next[aKey];
              if (aKey == "@o") { // the value is a new ordering of the array
                ordering = val;
              } else if (aKey == "@s") { // the value is a resize of the array
                resize = val;
              } else if (val == null) { // delete the element
                if (aKey in eBase) { // if it exists within the secret key
                  delay[aKey] = function (x) {
                    del(sub(eSub, x)); // issue deletes
                    delete eBase[x]; // delete the value
                    del_key(eSub, x); // delete the keys from the object
                    fire_del(eSub, x);
                  };
                }
              } else { // we have data
                if (!(aKey in eBase)) { // the data didn't exist, so fire an addition to extend the subscriptions
                  fire_add(eSub, aKey);
                }
                var nVal = next[aKey]; // get the new value
                if (typeof (nVal) == "object") { // if the new value is an object
                  if (!(aKey in eBase)) { // then make sure the new item is within the secret tree
                    eBase[aKey] = {};
                    eBase[aKey].__key = aKey;
                  }
                  delay[aKey] = function (x) {
                    merge(eBase[x], next[x], sub(eSub, x)); // recurse: merge the difference into the secret tree and publish to all element subscriptions
                    fire(sub(eSub, x), eBase[x]);
                  };
                } else { // otherwise, set the value into the tree
                  eBase[aKey] = nVal;
                  delay[aKey] = function (x) {
                    fire(sub(eSub, x), eBase[x]);
                  };
                }
                // and tell everyone the new value for this
              }
            }
            var before = tree[key]; // stash a pointer to the array
            var delayOrder = [];

            if (ordering != null) { // an ordering change is happening
              var after = []; // we will rebuild the array here
              var msg = []; // and record the keys here
              for (var k = 0; k < ordering.length; k++) { // for each ordering instruction
                var instr = ordering[k];
                var type_instr = typeof (instr);
                if (type_instr == "string" || type_instr == "number") { // if the instruction refers to an item
                  after.push(eBase[instr]); // push the item into the new array
                  msg.push("" + instr); // record the key of the item
                } else { // otherwise, the instruction is a range
                  var start = instr[0];
                  var end = instr[1];
                  for (var j = start; j <= end; j++) { // iterate over the range and copy
                    after.push(before[j]); // the items from the previous copy
                    msg.push(before[j].__key); // and annotate the keys
                  }
                }
              }
              delayOrder.push(function () {
                fire(sub(eSub, "~"), msg); // tell subscriptions about the new ordering
              });
              tree[key] = after;
            } else if (resize != null) { // alternatively, an array without keys is being resized
              var after = [];
              var msg = [];
              for (var k = 0; k < resize; k++) { // we rebuild the array
                after[k] = before[k];
                msg.push("" + k);
              }
              delayOrder.push(function () {
                fire(sub(eSub, "~"), msg); // tell subscriptions about the new ordering
              });
              tree[key] = after;
            }
            for (aKey in delay) {
              delay[aKey](aKey);
            }
            for (var k = 0; k < delayOrder; k++) {
              delayOrder[k]();
            }
            fire(eSub, tree[key]); // tell everyone about the new value
          } else {
            if (!(key in tree) || typeof (tree[key]) != "object") {
              tree[key] = {};
            }
            var e = sub(s, key);
            merge(tree[key], next, e);
            fire(e, tree[key]);
          }
        } else { // not an object, so it is a value
          tree[key] = next; // stash the new value
          fire(sub(s, key), next); // and tell everyone about it
        }
      }
    }
    fire(s, tree);
  };

  this.update = function (delta) {
    var flat = []; // flatten the subcriptions objects
    for (var k in all_subscriptions) {
      flat.push(all_subscriptions[k]);
    }
    merge(root, delta, flat); // execute the merge and fire events
  };

  // fix the callback structure from the user's happy land to the rigor of the format needed
  var transform_callback_into_sub = function (callback, sub) {
    if (Array.isArray(callback)) { // if the callback is an array, then flatten the array into the new object
      for (var k = 0; k < callback.length; k++) {
        transform_callback_into_sub(callback[k], sub);
      }
    } else if (typeof (callback) == "object") { // oh, the callback is an object
      for (var key in callback) { // iterate over each key
        if (key == "@e") { // force the functions to merge together
          transform_callback_into_sub(callback[key], sub);
        } else { // normal flow
          if (!(key in sub)) { // dive into the object and make sure the subscription has the field
            sub[key] = {};
          }
          transform_callback_into_sub(callback[key], sub[key]); // recurse and pair the events to the new subscription
        }
      }
    } else if (typeof (callback) == "function") { // it's a function
      if (!("@e" in sub)) { // let's ensure there is an event list
        sub["@e"] = [callback];
      } else { // otherwise, just add it
        sub["@e"].push(callback);
      }
    }
  };

  // convert the tree to a delta; this tree is assumed to have been merged already
  var make_delta = function (st) {
    var delta = {}; // the delta to construct
    for (var k in st) { // for each key within the object
      if (k[0] == "#" || k == "@o" || k == "__key") continue; // skip these
      var v = st[k];
      if (("#" + k) in st) { // the result is an array
        var d = {};
        if ("@o" in st["#" + k]) { // and we have a specific ordering to memoize
          var o = [];
          for (var j = 0; j < v.length; j++) { // copy the elements from the array
            var vj = v[j];
            d[vj.__key] = make_delta(vj); // using the __key embedded in the object
            o.push(v[j].__key);
          }
          d["@o"] = o;
        } else { // otherwise; record the size as we recurse
          for (var j = 0; j < v.length; j++) {
            d["" + j] = make_delta(v[j]);
          }
          d["@s"] = v.length;
        }
        delta[k] = d;
      } else if (typeof (v) == "object") { // it's just an object, so recurse directly
        delta[k] = make_delta(v);
      } else { // finally, it is a value so just set it
        delta[k] = v;
      }
    }
    return delta;
  };

  // `subscribe` the given structural callback
  this.subscribe = function (callback) {
    var sub = {};
    transform_callback_into_sub(callback, sub);  // transform the callback into a safe callback
    var delta = make_delta(root);
    merge({}, delta, [sub]); // execute the callback now on a callback of all data to fire events and fill the subscription object
    var S = "" + id++; // create an id for the subscription
    all_subscriptions[S] = sub; // record the subscription
    return function () { // return a method to unsubscribe
      delete all_subscriptions[S];
    };
  };
}
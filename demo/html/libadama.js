(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define([], factory);
	else if(typeof exports === 'object')
		exports["Adama"] = factory();
	else
		root["Adama"] = factory();
})(this, function() {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
/******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
/******/ 		}
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// create a fake namespace object
/******/ 	// mode & 1: value is a module id, require it
/******/ 	// mode & 2: merge all properties of value into the ns
/******/ 	// mode & 4: return value when already ns object
/******/ 	// mode & 8|1: behave like require
/******/ 	__webpack_require__.t = function(value, mode) {
/******/ 		if(mode & 1) value = __webpack_require__(value);
/******/ 		if(mode & 8) return value;
/******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
/******/ 		var ns = Object.create(null);
/******/ 		__webpack_require__.r(ns);
/******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
/******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
/******/ 		return ns;
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = "./src/index.ts");
/******/ })
/************************************************************************/
/******/ ({

/***/ "./node_modules/isomorphic-ws/browser.js":
/*!***********************************************!*\
  !*** ./node_modules/isomorphic-ws/browser.js ***!
  \***********************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

/* WEBPACK VAR INJECTION */(function(global) {// https://github.com/maxogden/websocket-stream/blob/48dc3ddf943e5ada668c31ccd94e9186f02fafbd/ws-fallback.js

var ws = null

if (typeof WebSocket !== 'undefined') {
  ws = WebSocket
} else if (typeof MozWebSocket !== 'undefined') {
  ws = MozWebSocket
} else if (typeof global !== 'undefined') {
  ws = global.WebSocket || global.MozWebSocket
} else if (typeof window !== 'undefined') {
  ws = window.WebSocket || window.MozWebSocket
} else if (typeof self !== 'undefined') {
  ws = self.WebSocket || self.MozWebSocket
}

module.exports = ws

/* WEBPACK VAR INJECTION */}.call(this, __webpack_require__(/*! ./../webpack/buildin/global.js */ "./node_modules/webpack/buildin/global.js")))

/***/ }),

/***/ "./node_modules/webpack/buildin/global.js":
/*!***********************************!*\
  !*** (webpack)/buildin/global.js ***!
  \***********************************/
/*! no static exports found */
/***/ (function(module, exports) {

var g;

// This works in non-strict mode
g = (function() {
	return this;
})();

try {
	// This works if eval is allowed (see CSP)
	g = g || new Function("return this")();
} catch (e) {
	// This works if the window reference is available
	if (typeof window === "object") g = window;
}

// g can still be undefined, but nothing to do about it...
// We return undefined, instead of nothing here, so it's
// easier to handle this case. if(!global) { ...}

module.exports = g;


/***/ }),

/***/ "./src/index.ts":
/*!**********************!*\
  !*** ./src/index.ts ***!
  \**********************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.Connection = exports.MakeTree = void 0;
var isomorphic_ws_1 = __importDefault(__webpack_require__(/*! isomorphic-ws */ "./node_modules/isomorphic-ws/browser.js"));
var tree_1 = __webpack_require__(/*! ./tree */ "./src/tree.ts");
function MakeTree() {
    return new tree_1.Tree();
}
exports.MakeTree = MakeTree;
/*
export class TreeStream {
  cancel() {

  }
};
*/
var Connection = /** @class */ (function () {
    function Connection(url) {
        var self = this;
        this.backoff = 1;
        this.url = url;
        this.connected = false;
        this.dead = false;
        this.maximum_backoff = 2500;
        this.socket = null;
        this.onstatuschange = function (status) {
        };
        this.onping = function (seconds, latency) {
        };
        this.onauthneeded = function (tryagain) {
        };
        this.scheduled = false;
        this.callbacks = new Map();
        this.connectId = 1;
        this.onreconnect = new Map();
        this.rpcid = 1;
    }
    /** stop the connection */
    Connection.prototype.stop = function () {
        this.dead = true;
        if (this.socket !== null) {
            this.socket.close();
        }
    };
    /** private: retry the connection */
    Connection.prototype._retry = function () {
        // null out the socket
        this.socket = null;
        // if we are connected, transition the status
        if (this.connected) {
            this.connected = false;
            this.onstatuschange(false);
        }
        // fail all outstanding operations
        this.callbacks.forEach(function (callback, id) {
            callback({ failure: id, reason: 77 });
        });
        this.callbacks.clear();
        // if we are dead, then don't actually retry
        if (this.dead) {
            return;
        }
        // make sure
        if (!this.scheduled) {
            // schedule a retry; first compute how long to take
            // compute how long we need to wait
            var halveAfterSchedule = false;
            this.backoff += Math.random() * this.backoff;
            if (this.backoff > this.maximum_backoff) {
                this.backoff = this.maximum_backoff;
                halveAfterSchedule = true;
            }
            // schedule it
            this.scheduled = true;
            var self = this;
            setTimeout(function () { self.start(); }, this.backoff);
            // we just scheduled it for the maximum backoff time, let's reduce the backoff so the next one will have some jitter
            if (halveAfterSchedule) {
                this.backoff /= 2.0;
            }
        }
    };
    /** start the connection */
    Connection.prototype.start = function () {
        // reset the state
        var self = this;
        this.scheduled = false;
        this.dead = false;
        // create the socket and bind event handlers
        this.socket = new isomorphic_ws_1.default(this.url);
        this.socket.onmessage = function (event) {
            var result = JSON.parse(event.data);
            // a message arrived, is it a connection signal
            if ('ping' in result) {
                self.onping(result.ping, result.latency);
                result.pong = new Date().getTime() / 1000.0;
                self.socket.send(JSON.stringify(result));
                return;
            }
            if ('signal' in result) {
                // hey, are we connected?
                if (result.status != 'connected') {
                    // nope, OK, let's make this a dead socket
                    self.dead = true;
                    self.socket.close();
                    self.socket = null;
                    // inform the client to try again
                    self.onauthneeded(function () {
                        self.start();
                    });
                    return;
                }
                // tell the client that we are good!
                self.backoff = 1;
                self.connected = true;
                self.onstatuschange(true);
                self._reconnect();
                return;
            }
            // the result was a failure..
            if ('failure' in result) {
                // find the callback, then invoke it (and clean up)
                if (self.callbacks.has(result.failure)) {
                    var cb = self.callbacks.get(result.failure);
                    if (cb) {
                        self.callbacks.delete(result.failure);
                        cb(result);
                    }
                }
            }
            else if ('deliver' in result) {
                // otherwise, we have a success, so let's find the callback, and if need be clean up
                if (self.callbacks.has(result.deliver)) {
                    var cb = self.callbacks.get(result.deliver);
                    if (cb) {
                        if (result.done) {
                            self.callbacks.delete(result.deliver);
                        }
                        cb(result.response);
                    }
                }
            }
        };
        this.socket.onclose = function (event) {
            // let's retry... or should we not
            self._retry();
        };
        this.socket.onerror = function (event) {
            // something bad happened, let's retry
            self._retry();
        };
    };
    /** private: send a raw message */
    Connection.prototype._send = function (request, callback) {
        if (!this.connected) {
            callback({ failure: 600, reason: 9999 });
            return;
        }
        var id = this.rpcid;
        this.rpcid++;
        request['id'] = id;
        this.callbacks.set(id, callback);
        this.socket.send(JSON.stringify(request));
    };
    /** api: wait for a connection */
    Connection.prototype.wait_connected = function () {
        return __awaiter(this, void 0, void 0, function () {
            var self, prior;
            return __generator(this, function (_a) {
                if (this.connected) {
                    return [2 /*return*/];
                }
                self = this;
                prior = this.onstatuschange;
                return [2 /*return*/, new Promise(function (good) {
                        self.onstatuschange = function (status) {
                            prior(status);
                            if (status) {
                                good();
                                self.onstatuschange = prior;
                            }
                        };
                    })];
            });
        });
    };
    /** api: generate a new game */
    Connection.prototype.generate = function (gs) {
        return __awaiter(this, void 0, void 0, function () {
            var request, self;
            return __generator(this, function (_a) {
                request = { method: "generate", gamespace: gs };
                self = this;
                return [2 /*return*/, new Promise(function (good, bad) {
                        self._send(request, function (response) {
                            if ('failure' in response) {
                                bad(response.reason);
                            }
                            else {
                                good(response);
                            }
                        });
                    })];
            });
        });
    };
    /** api: get the schema for the gamepsace */
    Connection.prototype.reflect = function (gs) {
        return __awaiter(this, void 0, void 0, function () {
            var request, self;
            return __generator(this, function (_a) {
                request = { method: "reflect", gamespace: gs };
                self = this;
                return [2 /*return*/, new Promise(function (good, bad) {
                        self._send(request, function (response) {
                            if ('failure' in response) {
                                bad(response.reason);
                            }
                            else {
                                good(response);
                            }
                        });
                    })];
            });
        });
    };
    /** api: generate a new game */
    Connection.prototype.create = function (gs, id, arg) {
        return __awaiter(this, void 0, void 0, function () {
            var request, self;
            return __generator(this, function (_a) {
                request = { method: "create", gamespace: gs, game: id, arg: arg };
                self = this;
                return [2 /*return*/, new Promise(function (good, bad) {
                        self._send(request, function (response) {
                            if ('failure' in response) {
                                bad(response.reason);
                            }
                            else {
                                good(response);
                            }
                        });
                    })];
            });
        });
    };
    /** api: connect to a game */
    Connection.prototype.connect = function (gs, id, handler) {
        return __awaiter(this, void 0, void 0, function () {
            var request, self, first;
            return __generator(this, function (_a) {
                request = { method: "connect", gamespace: gs, game: id };
                self = this;
                first = true;
                return [2 /*return*/, new Promise(function (good, bad) {
                        self._send(request, function (response) {
                            if (first) {
                                first = false;
                                if ('failure' in response) {
                                    bad(response.reason);
                                }
                                else {
                                    handler(response);
                                    good(true);
                                }
                            }
                            else {
                                handler(response);
                            }
                        });
                    })];
            });
        });
    };
    /** api: send a message */
    Connection.prototype.send = function (gs, id, channel, msg, hack) {
        return __awaiter(this, void 0, void 0, function () {
            var request, self;
            return __generator(this, function (_a) {
                request = { method: "send", gamespace: gs, game: id, channel: channel, message: msg };
                self = this;
                if (hack) {
                    hack(request);
                }
                // TODO: queue this up? with retry?
                // TODO: generate a marker
                return [2 /*return*/, new Promise(function (good, bad) {
                        self._send(request, function (response) {
                            if ('failure' in response) {
                                bad(response.reason);
                            }
                            else {
                                good(response);
                            }
                        });
                    })];
            });
        });
    };
    /** api: connect tree */
    Connection.prototype.connectTree = function (gs, id, tree, hack) {
        return __awaiter(this, void 0, void 0, function () {
            var keyId, sm;
            return __generator(this, function (_a) {
                keyId = this.connectId;
                this.connectId++;
                sm = {
                    request: { method: "connect", gamespace: gs, game: id },
                    first: true,
                    handler: function (r) {
                        tree.mergeUpdate(r);
                    }
                };
                if (hack) {
                    hack(sm.request);
                }
                this.onreconnect.set(keyId, sm);
                return [2 /*return*/, this._execute(sm)];
            });
        });
    };
    Connection.prototype._reconnect = function () {
        var self = this;
        this.onreconnect.forEach(function (sm, id) {
            self._execute(sm);
        });
    };
    Connection.prototype._execute = function (sm) {
        return __awaiter(this, void 0, void 0, function () {
            var self;
            return __generator(this, function (_a) {
                self = this;
                return [2 /*return*/, new Promise(function (good, bad) {
                        self._send(sm.request, function (response) {
                            if (sm.first) {
                                sm.first = false;
                                if ('failure' in response) {
                                    bad(response.reason);
                                }
                                else {
                                    sm.handler(response);
                                    good(true);
                                }
                            }
                            else {
                                sm.handler(response);
                            }
                        });
                    })];
            });
        });
    };
    return Connection;
}());
exports.Connection = Connection;


/***/ }),

/***/ "./src/tree.ts":
/*!*********************!*\
  !*** ./src/tree.ts ***!
  \*********************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
exports.Tree = void 0;
var Tree = /** @class */ (function () {
    function Tree() {
        this.tree = {};
        this.dispatch = {};
        this.dispatch_count = 0;
        this.queue = [];
        this.ondecide = function (outstanding) { };
    }
    // recursively append a change
    // dispatch is the structural object mirroring the tree
    // callback is the function/object callback tree
    // insert_order is the order to fire events
    Tree.prototype.__recAppendChange = function (dispatch, callback, insert_order) {
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
        }
        else if (typeof (callback) == 'function') {
            // we have a function, so let's associate it to the node
            if (!('@e' in dispatch)) {
                dispatch['@e'] = [];
            }
            dispatch['@e'].push({ cb: callback, order: insert_order });
        }
    };
    Tree.prototype.onTreeChange = function (callback) {
        this.__recAppendChange(this.dispatch, callback, this.dispatch_count);
        this.dispatch_count++;
    };
    // the main function
    Tree.prototype.mergeUpdate = function (diff) {
        if ('data' in diff) {
            // we merge the tree with the data within dispatch
            this.__recMergeAndDispatch(this.tree, this.dispatch, diff.data);
        }
        if ('outstanding' in diff) {
            this.ondecide(diff.outstanding);
        }
        this.__drain();
    };
    Tree.prototype.__recDeleteAndDispatch = function (tree, dispatch) {
        for (var key in tree) {
            var old = tree[key];
            if (Array.isArray(old)) {
                // need to fire the DELETE
            }
            else {
                if (key in dispatch) {
                    this.__recDeleteAndDispatch(old, dispatch[key]);
                }
            }
            var deleteChildKey = '-' + key;
            if (dispatch != null && deleteChildKey in dispatch) {
                this.__fire(dispatch[deleteChildKey], { key: key, before: old, value: null });
            }
        }
    };
    Tree.prototype.__recMergeAndDispatch = function (tree, dispatch, diff) {
        // the diff is an object, so let's walk its keys
        for (var key in diff) {
            var child = diff[key];
            if (child === null) {
                var deleteChildKey = '-' + key;
                var old_1 = tree[key];
                if (dispatch != null && deleteChildKey in dispatch) {
                    this.__fire(dispatch[deleteChildKey], { key: key, before: old_1, value: null });
                }
                if (Array.isArray(old_1)) {
                    // also delete
                    var elementKey = "#" + key;
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
                    this.__fire(dispatch[key], { key: key, before: old_1, value: null });
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
                    }
                    else {
                        tree[key] = {};
                    }
                }
                // now, we check to see if the prior state influences whether or not the diff is an array
                childIsArray = Array.isArray(tree[key]) || childIsArray;
                if (childIsArray) {
                    this.__recMergeAndDispatchArray(tree[key], (dispatch != null && key in dispatch) ? dispatch[key] : null, tree["#" + key], child);
                }
                else {
                    this.__recMergeAndDispatch(tree[key], (dispatch != null && key in dispatch) ? dispatch[key] : null, child);
                }
            }
            else {
                var old = (key in tree) ? tree[key] : null;
                tree[key] = child;
                if (dispatch != null && key in dispatch) {
                    this.__fire(dispatch[key], { key: key, before: old, value: child });
                }
            }
            if (fireNew) { // fire new data event
                var newChildKey = '+' + key;
                if (dispatch != null && newChildKey in dispatch) {
                    this.__fire(dispatch[newChildKey], { key: key, value: tree[key] });
                }
            }
            if (dispatch != null && '@e' in dispatch) {
                this.__fire(dispatch, { value: tree });
            }
        }
    };
    Tree.prototype.__recMergeAndDispatchArray = function (prior, dispatch, tree, diff) {
        // TODO: new item... etc
        var ordering = null;
        var resize = null;
        for (var key in diff) {
            if (key == "@o") {
                ordering = diff[key];
            }
            else if (key == "@s") {
                resize = diff[key];
            }
            else {
                if (diff[key] == null) {
                    if (dispatch && '-' in dispatch) {
                        this.__fire(dispatch['-'], { key: key, before: tree[key], value: null });
                    }
                    delete tree[key];
                }
                else {
                    var fireNew = false;
                    if (!(tree != null && key in tree)) {
                        if (dispatch && '+' in dispatch) {
                            fireNew = true;
                        }
                        tree[key] = {};
                    }
                    this.__recMergeAndDispatch(tree[key], (dispatch != null && '#' in dispatch) ? dispatch['#'] : null, diff[key]);
                    if (fireNew) {
                        this.__fire(dispatch['+'], { key: key, before: null, value: tree[key] });
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
                }
                else {
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
                        this.__fire(dispatch['-'], { key: key, before: tree[key], value: null });
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
    };
    Tree.prototype.__fire = function (dispatch, change) {
        if (dispatch) {
            if ('@e' in dispatch) {
                var d = dispatch['@e'];
                var nulls = 0;
                for (var k = 0; k < d.length; k++) {
                    var evt = d[k];
                    if (evt !== null) {
                        this.queue.push({ cb: evt.cb, order: evt.order, change: change, dispatch_list: d, index: k });
                    }
                    else {
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
    };
    Tree.prototype.__drain = function () {
        this.queue.sort(function (a, b) { return a.order - b.order; });
        for (var k = 0; k < this.queue.length; k++) {
            var item = this.queue[k];
            if (item.cb(item.change) === 'delete') {
                item.dispatch_list[item.index] = null;
            }
        }
        this.queue = [];
    };
    return Tree;
}());
exports.Tree = Tree;


/***/ })

/******/ });
});
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly9BZGFtYS93ZWJwYWNrL3VuaXZlcnNhbE1vZHVsZURlZmluaXRpb24iLCJ3ZWJwYWNrOi8vQWRhbWEvd2VicGFjay9ib290c3RyYXAiLCJ3ZWJwYWNrOi8vQWRhbWEvLi9ub2RlX21vZHVsZXMvaXNvbW9ycGhpYy13cy9icm93c2VyLmpzIiwid2VicGFjazovL0FkYW1hLyh3ZWJwYWNrKS9idWlsZGluL2dsb2JhbC5qcyIsIndlYnBhY2s6Ly9BZGFtYS8uL3NyYy9pbmRleC50cyIsIndlYnBhY2s6Ly9BZGFtYS8uL3NyYy90cmVlLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLENBQUM7QUFDRCxPO1FDVkE7UUFDQTs7UUFFQTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBOztRQUVBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7OztRQUdBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7UUFDQSwwQ0FBMEMsZ0NBQWdDO1FBQzFFO1FBQ0E7O1FBRUE7UUFDQTtRQUNBO1FBQ0Esd0RBQXdELGtCQUFrQjtRQUMxRTtRQUNBLGlEQUFpRCxjQUFjO1FBQy9EOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQSx5Q0FBeUMsaUNBQWlDO1FBQzFFLGdIQUFnSCxtQkFBbUIsRUFBRTtRQUNySTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBLDJCQUEyQiwwQkFBMEIsRUFBRTtRQUN2RCxpQ0FBaUMsZUFBZTtRQUNoRDtRQUNBO1FBQ0E7O1FBRUE7UUFDQSxzREFBc0QsK0RBQStEOztRQUVySDtRQUNBOzs7UUFHQTtRQUNBOzs7Ozs7Ozs7Ozs7QUNsRkE7O0FBRUE7O0FBRUE7QUFDQTtBQUNBLENBQUM7QUFDRDtBQUNBLENB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Q2pEO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EseUNBQXlDLDBCQUEwQixFQUFFO0FBQ3JFLHVCQUF1Qix1QkFBdUI7QUFDOUM7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLENBQUM7QUFDRCIsImZpbGUiOiJsaWJhZGFtYS5qcyIsInNvdXJjZXNDb250ZW50IjpbIihmdW5jdGlvbiB3ZWJwYWNrVW5pdmVyc2FsTW9kdWxlRGVmaW5pdGlvbihyb290LCBmYWN0b3J5KSB7XG5cdGlmKHR5cGVvZiBleHBvcnRzID09PSAnb2JqZWN0JyAmJiB0eXBlb2YgbW9kdWxlID09PSAnb2JqZWN0Jylcblx0XHRtb2R1bGUuZXhwb3J0cyA9IGZhY3RvcnkoKTtcblx0ZWxzZSBpZih0eXBlb2YgZGVmaW5lID09PSAnZnVuY3Rpb24nICYmIGRlZmluZS5hbWQpXG5cdFx0ZGVmaW5lKFtdLCBmYWN0b3J5KTtcblx0ZWxzZSBpZih0eXBlb2YgZXhwb3J0cyA9PT0gJ29iamVjdCcpXG5cdFx0ZXhwb3J0c1tcIkFkYW1hXCJdID0gZmFjdG9yeSgpO1xuXHRlbHNlXG5cdFx0cm9vdFtcIkFkYW1hXCJdID0gZmFjdG9yeSgpO1xufSkodGhpcywgZnVuY3Rpb24oKSB7XG5yZXR1cm4gIiwiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pIHtcbiBcdFx0XHRyZXR1cm4gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0uZXhwb3J0cztcbiBcdFx0fVxuIFx0XHQvLyBDcmVhdGUgYSBuZXcgbW9kdWxlIChhbmQgcHV0IGl0IGludG8gdGhlIGNhY2hlKVxuIFx0XHR2YXIgbW9kdWxlID0gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0gPSB7XG4gXHRcdFx0aTogbW9kdWxlSWQsXG4gXHRcdFx0bDogZmFsc2UsXG4gXHRcdFx0ZXhwb3J0czoge31cbiBcdFx0fTtcblxuIFx0XHQvLyBFeGVjdXRlIHRoZSBtb2R1bGUgZnVuY3Rpb25cbiBcdFx0bW9kdWxlc1ttb2R1bGVJZF0uY2FsbChtb2R1bGUuZXhwb3J0cywgbW9kdWxlLCBtb2R1bGUuZXhwb3J0cywgX193ZWJwYWNrX3JlcXVpcmVfXyk7XG5cbiBcdFx0Ly8gRmxhZyB0aGUgbW9kdWxlIGFzIGxvYWRlZFxuIFx0XHRtb2R1bGUubCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gZGVmaW5lIGdldHRlciBmdW5jdGlvbiBmb3IgaGFybW9ueSBleHBvcnRzXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLmQgPSBmdW5jdGlvbihleHBvcnRzLCBuYW1lLCBnZXR0ZXIpIHtcbiBcdFx0aWYoIV9fd2VicGFja19yZXF1aXJlX18ubyhleHBvcnRzLCBuYW1lKSkge1xuIFx0XHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBuYW1lLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZ2V0dGVyIH0pO1xuIFx0XHR9XG4gXHR9O1xuXG4gXHQvLyBkZWZpbmUgX19lc01vZHVsZSBvbiBleHBvcnRzXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnIgPSBmdW5jdGlvbihleHBvcnRzKSB7XG4gXHRcdGlmKHR5cGVvZiBTeW1ib2wgIT09ICd1bmRlZmluZWQnICYmIFN5bWJvbC50b1N0cmluZ1RhZykge1xuIFx0XHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBTeW1ib2wudG9TdHJpbmdUYWcsIHsgdmFsdWU6ICdNb2R1bGUnIH0pO1xuIFx0XHR9XG4gXHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCAnX19lc01vZHVsZScsIHsgdmFsdWU6IHRydWUgfSk7XG4gXHR9O1xuXG4gXHQvLyBjcmVhdGUgYSBmYWtlIG5hbWVzcGFjZSBvYmplY3RcbiBcdC8vIG1vZGUgJiAxOiB2YWx1ZSBpcyBhIG1vZHVsZSBpZCwgcmVxdWlyZSBpdFxuIFx0Ly8gbW9kZSAmIDI6IG1lcmdlIGFsbCBwcm9wZXJ0aWVzIG9mIHZhbHVlIGludG8gdGhlIG5zXG4gXHQvLyBtb2RlICYgNDogcmV0dXJuIHZhbHVlIHdoZW4gYWxyZWFkeSBucyBvYmplY3RcbiBcdC8vIG1vZGUgJiA4fDE6IGJlaGF2ZSBsaWtlIHJlcXVpcmVcbiBcdF9fd2VicGFja19yZXF1aXJlX18udCA9IGZ1bmN0aW9uKHZhbHVlLCBtb2RlKSB7XG4gXHRcdGlmKG1vZGUgJiAxKSB2YWx1ZSA9IF9fd2VicGFja19yZXF1aXJlX18odmFsdWUpO1xuIFx0XHRpZihtb2RlICYgOCkgcmV0dXJuIHZhbHVlO1xuIFx0XHRpZigobW9kZSAmIDQpICYmIHR5cGVvZiB2YWx1ZSA9PT0gJ29iamVjdCcgJiYgdmFsdWUgJiYgdmFsdWUuX19lc01vZHVsZSkgcmV0dXJuIHZhbHVlO1xuIFx0XHR2YXIgbnMgPSBPYmplY3QuY3JlYXRlKG51bGwpO1xuIFx0XHRfX3dlYnBhY2tfcmVxdWlyZV9fLnIobnMpO1xuIFx0XHRPYmplY3QuZGVmaW5lUHJvcGVydHkobnMsICdkZWZhdWx0JywgeyBlbnVtZXJhYmxlOiB0cnVlLCB2YWx1ZTogdmFsdWUgfSk7XG4gXHRcdGlmKG1vZGUgJiAyICYmIHR5cGVvZiB2YWx1ZSAhPSAnc3RyaW5nJykgZm9yKHZhciBrZXkgaW4gdmFsdWUpIF9fd2VicGFja19yZXF1aXJlX18uZChucywga2V5LCBmdW5jdGlvbihrZXkpIHsgcmV0dXJuIHZhbHVlW2tleV07IH0uYmluZChudWxsLCBrZXkpKTtcbiBcdFx0cmV0dXJuIG5zO1xuIFx0fTtcblxuIFx0Ly8gZ2V0RGVmYXVsdEV4cG9ydCBmdW5jdGlvbiBmb3IgY29tcGF0aWJpbGl0eSB3aXRoIG5vbi1oYXJtb255IG1vZHVsZXNcbiBcdF9fd2VicGFja19yZXF1aXJlX18ubiA9IGZ1bmN0aW9uKG1vZHVsZSkge1xuIFx0XHR2YXIgZ2V0dGVyID0gbW9kdWxlICYmIG1vZHVsZS5fX2VzTW9kdWxlID9cbiBcdFx0XHRmdW5jdGlvbiBnZXREZWZhdWx0KCkgeyByZXR1cm4gbW9kdWxlWydkZWZhdWx0J107IH0gOlxuIFx0XHRcdGZ1bmN0aW9uIGdldE1vZHVsZUV4cG9ydHMoKSB7IHJldHVybiBtb2R1bGU7IH07XG4gXHRcdF9fd2VicGFja19yZXF1aXJlX18uZChnZXR0ZXIsICdhJywgZ2V0dGVyKTtcbiBcdFx0cmV0dXJuIGdldHRlcjtcbiBcdH07XG5cbiBcdC8vIE9iamVjdC5wcm90b3R5cGUuaGFzT3duUHJvcGVydHkuY2FsbFxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5vID0gZnVuY3Rpb24ob2JqZWN0LCBwcm9wZXJ0eSkgeyByZXR1cm4gT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsKG9iamVjdCwgcHJvcGVydHkpOyB9O1xuXG4gXHQvLyBfX3dlYnBhY2tfcHVibGljX3BhdGhfX1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5wID0gXCJcIjtcblxuXG4gXHQvLyBMb2FkIGVudHJ5IG1vZHVsZSBhbmQgcmV0dXJuIGV4cG9ydHNcbiBcdHJldHVybiBfX3dlYnBhY2tfcmVxdWlyZV9fKF9fd2VicGFja19yZXF1aXJlX18ucyA9IFwiLi9zcmMvaW5kZXgudHNcIik7XG4iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vbWF4b2dkZW4vd2Vic29ja2V0LXN0cmVhbS9ibG9iLzQ4ZGMzZGRmOTQzZTVhZGE2NjhjMzFjY2Q5NGU5MTg2ZjAyZmFmYmQvd3MtZmFsbGJhY2suanNcblxudmFyIHdzID0gbnVsbFxuXG5pZiAodHlwZW9mIFdlYlNvY2tldCAhPT0gJ3VuZGVmaW5lZCcpIHtcbiAgd3MgPSBXZWJTb2NrZXRcbn0gZWxzZSBpZiAodHlwZW9mIE1veldlYlNvY2tldCAhPT0gJ3VuZGVmaW5lZCcpIHtcbiAgd3MgPSBNb3pXZWJTb2NrZXRcbn0gZWxzZSBpZiAodHlwZW9mIGdsb2JhbCAhPT0gJ3VuZGVmaW5lZCcpIHtcbiAgd3MgPSBnbG9iYWwuV2ViU29ja2V0IHx8IGdsb2JhbC5Nb3pXZWJTb2NrZXRcbn0gZWxzZSBpZiAodHlwZW9mIHdpbmRvdyAhPT0gJ3VuZGVmaW5lZCcpIHtcbiAgd3MgPSB3aW5kb3cuV2ViU29ja2V0IHx8IHdpbmRvdy5Nb3pXZWJTb2NrZXRcbn0gZWxzZSBpZiAodHlwZW9mIHNlbGYgIT09ICd1bmRlZmluZWQnKSB7XG4gIHdzID0gc2VsZi5XZWJTb2NrZXQgfHwgc2VsZi5Nb3pXZWJTb2NrZXRcbn1cblxubW9kdWxlLmV4cG9ydHMgPSB3c1xuIiwidmFyIGc7XG5cbi8vIFRoaXMgd29ya3MgaW4gbm9uLXN0cmljdCBtb2RlXG5nID0gKGZ1bmN0aW9uKCkge1xuXHRyZXR1cm4gdGhpcztcbn0pKCk7XG5cbnRyeSB7XG5cdC8vIFRoaXMgd29ya3MgaWYgZXZhbCBpcyBhbGxvd2VkIChzZWUgQ1NQKVxuXHRnID0gZyB8fCBuZXcgRnVuY3Rpb24oXCJyZXR1cm4gdGhpc1wiKSgpO1xufSBjYXRjaCAoZSkge1xuXHQvLyBUaGlzIHdvcmtzIGlmIHRoZSB3aW5kb3cgcmVmZXJlbmNlIGlzIGF2YWlsYWJsZVxuXHRpZiAodHlwZW9mIHdpbmRvdyA9PT0gXCJvYmplY3RcIikgZyA9IHdpbmRvdztcbn1cblxuLy8gZyBjYW4gc3RpbGwgYmUgdW5kZWZpbmVkLCBidXQgbm90aGluZyB0byBkbyBhYm91dCBpdC4uLlxuLy8gV2UgcmV0dXJuIHVuZGVmaW5lZCwgaW5zdGVhZCBvZiBub3RoaW5nIGhlcmUsIHNvIGl0J3Ncbi8vIGVhc2llciB0byBoYW5kbGUgdGhpcyBjYXNlLiBpZighZ2xvYmFsKSB7IC4uLn1cblxubW9kdWxlLmV4cG9ydHMgPSBnO1xuIiwiXCJ1c2Ugc3RyaWN0XCI7XG52YXIgX19hd2FpdGVyID0gKHRoaXMgJiYgdGhpcy5fX2F3YWl0ZXIpIHx8IGZ1bmN0aW9uICh0aGlzQXJnLCBfYXJndW1lbnRzLCBQLCBnZW5lcmF0b3IpIHtcbiAgICBmdW5jdGlvbiBhZG9wdCh2YWx1ZSkgeyByZXR1cm4gdmFsdWUgaW5zdGFuY2VvZiBQID8gdmFsdWUgOiBuZXcgUChmdW5jdGlvbiAocmVzb2x2ZSkgeyByZXNvbHZlKHZhbHVlKTsgfSk7IH1cbiAgICByZXR1cm4gbmV3IChQIHx8IChQID0gUHJvbWlzZSkpKGZ1bmN0aW9uIChyZXNvbHZlLCByZWplY3QpIHtcbiAgICAgICAgZnVuY3Rpb24gZnVsZmlsbGVkKHZhbHVlKSB7IHRyeSB7IHN0ZXAoZ2VuZXJhdG9yLm5leHQodmFsdWUpKTsgfSBjYXRjaCAoZSkgeyByZWplY3QoZSk7IH0gfVxuICAgICAgICBmdW5jdGlvbiByZWplY3RlZCh2YWx1ZSkgeyB0cnkgeyBzdGVwKGdlbmVyYXRvcltcInRocm93XCJdKHZhbHVlKSk7IH0gY2F0Y2ggKGUpIHsgcmVqZWN0KGUpOyB9IH1cbiAgICAgICAgZnVuY3Rpb24gc3RlcChyZXN1bHQpIHsgcmVzdWx0LmRvbmUgPyByZXNvbHZlKHJlc3VsdC52YWx1ZSkgOiBhZG9wdChyZXN1bHQudmFsdWUpLnRoZW4oZnVsZmlsbGVkLCByZWplY3RlZCk7IH1cbiAgICAgICAgc3RlcCgoZ2VuZXJhdG9yID0gZ2VuZXJhdG9yLmFwcGx5KHRoaXNBcmcsIF9hcmd1bWVudHMgfHwgW10pKS5uZXh0KCkpO1xuICAgIH0pO1xufTtcbnZhciBfX2dlbmVyYXRvciA9ICh0aGlzICYmIHRoaXMuX19nZW5lcmF0b3IpIHx8IGZ1bmN0aW9uICh0aGlzQXJnLCBib2R5KSB7XG4gICAgdmFyIF8gPSB7IGxhYmVsOiAwLCBzZW50OiBmdW5jdGlvbigpIHsgaWYgKHRbMF0gJiAxKSB0aHJvdyB0WzFdOyByZXR1cm4gdFsxXTsgfSwgdHJ5czogW10sIG9wczogW10gfSwgZiwgeSwgdCwgZztcbiAgICByZXR1cm4gZyA9IHsgbmV4dDogdmVyYigwKSwgXCJ0aHJvd1wiOiB2ZXJiKDEpLCBcInJldHVyblwiOiB2ZXJiKDIpIH0sIHR5cGVvZiBTeW1ib2wgPT09IFwiZnVuY3Rpb25cIiAmJiAoZ1tTeW1ib2wuaXRlcmF0b3JdID0gZnVuY3Rpb24oKSB7IHJldHVybiB0aGlzOyB9KSwgZztcbiAgICBmdW5jdGlvbiB2ZXJiKG4pIHsgcmV0dXJuIGZ1bmN0aW9uICh2KSB7IHJldHVybiBzdGVwKFtuLCB2XSk7IH07IH1cbiAgICBmdW5jdGlvbiBzdGVwKG9wKSB7XG4gICAgICAgIGlmIChmKSB0aHJvdyBuZXcgVHlwZUVycm9yKFwiR2VuZXJhdG9yIGlzIGFscmVhZHkgZXhlY3V0aW5nLlwiKTtcbiAgICAgICAgd2hpbGUgKF8pIHRyeSB7XG4gICAgICAgICAgICBpZiAoZiA9IDEsIHkgJiYgKHQgPSBvcFswXSAmIDIgPyB5W1wicmV0dXJuXCJdIDogb3BbMF0gPyB5W1widGhyb3dcIl0gfHwgKCh0ID0geVtcInJldHVyblwiXSkgJiYgdC5jYWxsKHkpLCAwKSA6IHkubmV4dCkgJiYgISh0ID0gdC5jYWxsKHksIG9wWzFdKSkuZG9uZSkgcmV0dXJuIHQ7XG4gICAgICAgICAgICBpZiAoeSA9IDAsIHQpIG9wID0gW29wWzBdICYgMiwgdC52YWx1ZV07XG4gICAgICAgICAgICBzd2l0Y2ggKG9wWzBdKSB7XG4gICAgICAgICAgICAgICAgY2FzZSAwOiBjYXNlIDE6IHQgPSBvcDsgYnJlYWs7XG4gICAgICAgICAgICAgICAgY2FzZSA0OiBfLmxhYmVsKys7IHJldHVybiB7IHZhbHVlOiBvcFsxXSwgZG9uZTogZmFsc2UgfTtcbiAgICAgICAgICAgICAgICBjYXNlIDU6IF8ubGFiZWwrKzsgeSA9IG9wWzFdOyBvcCA9IFswXTsgY29udGludWU7XG4gICAgICAgICAgICAgICAgY2FzZSA3OiBvcCA9IF8ub3BzLnBvcCgpOyBfLnRyeXMucG9wKCk7IGNvbnRpbnVlO1xuICAgICAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgICAgIGlmICghKHQgPSBfLnRyeXMsIHQgPSB0Lmxlbmd0aCA+IDAgJiYgdFt0Lmxlbmd0aCAtIDFdKSAmJiAob3BbMF0gPT09IDYgfHwgb3BbMF0gPT09IDIpKSB7IF8gPSAwOyBjb250aW51ZTsgfVxuICAgICAgICAgICAgICAgICAgICBpZiAob3BbMF0gPT09IDMgJiYgKCF0IHx8IChvcFsxXSA+IHRbMF0gJiYgb3BbMV0gPCB0WzNdKSkpIHsgXy5sYWJlbCA9IG9wWzFdOyBicmVhazsgfVxuICAgICAgICAgICAgICAgICAgICBpZiAob3BbMF0gPT09IDYgJiYgXy5sYWJlbCA8IHRbMV0pIHsgXy5sYWJlbCA9IHRbMV07IHQgPSBvcDsgYnJlYWs7IH1cbiAgICAgICAgICAgICAgICAgICAgaWYgKHQgJiYgXy5sYWJlbCA8IHRbMl0pIHsgXy5sYWJlbCA9IHRbMl07IF8ub3BzLnB1c2gob3ApOyBicmVhazsgfVxuICAgICAgICAgICAgICAgICAgICBpZiAodFsyXSkgXy5vcHMucG9wKCk7XG4gICAgICAgICAgICAgICAgICAgIF8udHJ5cy5wb3AoKTsgY29udGludWU7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBvcCA9IGJvZHkuY2FsbCh0aGlzQXJnLCBfKTtcbiAgICAgICAgfSBjYXRjaCAoZSkgeyBvcCA9IFs2LCBlXTsgeSA9IDA7IH0gZmluYWxseSB7IGYgPSB0ID0gMDsgfVxuICAgICAgICBpZiAob3BbMF0gJiA1KSB0aHJvdyBvcFsxXTsgcmV0dXJuIHsgdmFsdWU6IG9wWzBdID8gb3BbMV0gOiB2b2lkIDAsIGRvbmU6IHRydWUgfTtcbiAgICB9XG59O1xudmFyIF9faW1wb3J0RGVmYXVsdCA9ICh0aGlzICYmIHRoaXMuX19pbXBvcnREZWZhdWx0KSB8fCBmdW5jdGlvbiAobW9kKSB7XG4gICAgcmV0dXJuIChtb2QgJiYgbW9kLl9fZXNNb2R1bGUpID8gbW9kIDogeyBcImRlZmF1bHRcIjogbW9kIH07XG59O1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xuZXhwb3J0cy5Db25uZWN0aW9uID0gZXhwb3J0cy5NYWtlVHJlZSA9IHZvaWQgMDtcbnZhciBpc29tb3JwaGljX3dzXzEgPSBfX2ltcG9ydERlZmF1bHQocmVxdWlyZShcImlzb21vcnBoaWMtd3NcIikpO1xudmFyIHRyZWVfMSA9IHJlcXVpcmUoXCIuL3RyZWVcIik7XG5mdW5jdGlvbiBNYWtlVHJlZSgpIHtcbiAgICByZXR1cm4gbmV3IHRyZWVfMS5UcmVlKCk7XG59XG5leHBvcnRzLk1ha2VUcmVlID0gTWFrZVRyZWU7XG4vKlxuZXhwb3J0IGNsYXNzIFRyZWVTdHJlYW0ge1xuICBjYW5jZWwoKSB7XG5cbiAgfVxufTtcbiovXG52YXIgQ29ubmVjdGlvbiA9IC8qKiBAY2xhc3MgKi8gKGZ1bmN0aW9uICgpIHtcbiAgICBmdW5jdGlvbiBDb25uZWN0aW9uKHVybCkge1xuICAgICAgICB2YXIgc2VsZiA9IHRoaXM7XG4gICAgICAgIHRoaXMuYmFja29mZiA9IDE7XG4gICAgICAgIHRoaXMudXJsID0gdXJsO1xuICAgICAgICB0aGlzLmNvbm5lY3RlZCA9IGZhbHNlO1xuICAgICAgICB0aGlzLmRlYWQgPSBmYWxzZTtcbiAgICAgICAgdGhpcy5tYXhpbXVtX2JhY2tvZmYgPSAyNTAwO1xuICAgICAgICB0aGlzLnNvY2tldCA9IG51bGw7XG4gICAgICAgIHRoaXMub25zdGF0dXNjaGFuZ2UgPSBmdW5jdGlvbiAoc3RhdHVzKSB7XG4gICAgICAgIH07XG4gICAgICAgIHRoaXMub25waW5nID0gZnVuY3Rpb24gKHNlY29uZHMsIGxhdGVuY3kpIHtcbiAgICAgICAgfTtcbiAgICAgICAgdGhpcy5vbmF1dGhuZWVkZWQgPSBmdW5jdGlvbiAodHJ5YWdhaW4pIHtcbiAgICAgICAgfTtcbiAgICAgICAgdGhpcy5zY2hlZHVsZWQgPSBmYWxzZTtcbiAgICAgICAgdGhpcy5jYWxsYmFja3MgPSBuZXcgTWFwKCk7XG4gICAgICAgIHRoaXMuY29ubmVjdElkID0gMTtcbiAgICAgICAgdGhpcy5vbnJlY29ubmVjdCA9IG5ldyBNYXAoKTtcbiAgICAgICAgdGhpcy5ycGNpZCA9IDE7XG4gICAgfVxuICAgIC8qKiBzdG9wIHRoZSBjb25uZWN0aW9uICovXG4gICAgQ29ubmVjdGlvbi5wcm90b3R5cGUuc3RvcCA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgdGhpcy5kZWFkID0gdHJ1ZTtcbiAgICAgICAgaWYgKHRoaXMuc29ja2V0ICE9PSBudWxsKSB7XG4gICAgICAgICAgICB0aGlzLnNvY2tldC5jbG9zZSgpO1xuICAgICAgICB9XG4gICAgfTtcbiAgICAvKiogcHJpdmF0ZTogcmV0cnkgdGhlIGNvbm5lY3Rpb24gKi9cbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5fcmV0cnkgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIC8vIG51bGwgb3V0IHRoZSBzb2NrZXRcbiAgICAgICAgdGhpcy5zb2NrZXQgPSBudWxsO1xuICAgICAgICAvLyBpZiB3ZSBhcmUgY29ubmVjdGVkLCB0cmFuc2l0aW9uIHRoZSBzdGF0dXNcbiAgICAgICAgaWYgKHRoaXMuY29ubmVjdGVkKSB7XG4gICAgICAgICAgICB0aGlzLmNvbm5lY3RlZCA9IGZhbHNlO1xuICAgICAgICAgICAgdGhpcy5vbnN0YXR1c2NoYW5nZShmYWxzZSk7XG4gICAgICAgIH1cbiAgICAgICAgLy8gZmFpbCBhbGwgb3V0c3RhbmRpbmcgb3BlcmF0aW9uc1xuICAgICAgICB0aGlzLmNhbGxiYWNrcy5mb3JFYWNoKGZ1bmN0aW9uIChjYWxsYmFjaywgaWQpIHtcbiAgICAgICAgICAgIGNhbGxiYWNrKHsgZmFpbHVyZTogaWQsIHJlYXNvbjogNzcgfSk7XG4gICAgICAgIH0pO1xuICAgICAgICB0aGlzLmNhbGxiYWNrcy5jbGVhcigpO1xuICAgICAgICAvLyBpZiB3ZSBhcmUgZGVhZCwgdGhlbiBkb24ndCBhY3R1YWxseSByZXRyeVxuICAgICAgICBpZiAodGhpcy5kZWFkKSB7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cbiAgICAgICAgLy8gbWFrZSBzdXJlXG4gICAgICAgIGlmICghdGhpcy5zY2hlZHVsZWQpIHtcbiAgICAgICAgICAgIC8vIHNjaGVkdWxlIGEgcmV0cnk7IGZpcnN0IGNvbXB1dGUgaG93IGxvbmcgdG8gdGFrZVxuICAgICAgICAgICAgLy8gY29tcHV0ZSBob3cgbG9uZyB3ZSBuZWVkIHRvIHdhaXRcbiAgICAgICAgICAgIHZhciBoYWx2ZUFmdGVyU2NoZWR1bGUgPSBmYWxzZTtcbiAgICAgICAgICAgIHRoaXMuYmFja29mZiArPSBNYXRoLnJhbmRvbSgpICogdGhpcy5iYWNrb2ZmO1xuICAgICAgICAgICAgaWYgKHRoaXMuYmFja29mZiA+IHRoaXMubWF4aW11bV9iYWNrb2ZmKSB7XG4gICAgICAgICAgICAgICAgdGhpcy5iYWNrb2ZmID0gdGhpcy5tYXhpbXVtX2JhY2tvZmY7XG4gICAgICAgICAgICAgICAgaGFsdmVBZnRlclNjaGVkdWxlID0gdHJ1ZTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIC8vIHNjaGVkdWxlIGl0XG4gICAgICAgICAgICB0aGlzLnNjaGVkdWxlZCA9IHRydWU7XG4gICAgICAgICAgICB2YXIgc2VsZiA9IHRoaXM7XG4gICAgICAgICAgICBzZXRUaW1lb3V0KGZ1bmN0aW9uICgpIHsgc2VsZi5zdGFydCgpOyB9LCB0aGlzLmJhY2tvZmYpO1xuICAgICAgICAgICAgLy8gd2UganVzdCBzY2hlZHVsZWQgaXQgZm9yIHRoZSBtYXhpbXVtIGJhY2tvZmYgdGltZSwgbGV0J3MgcmVkdWNlIHRoZSBiYWNrb2ZmIHNvIHRoZSBuZXh0IG9uZSB3aWxsIGhhdmUgc29tZSBqaXR0ZXJcbiAgICAgICAgICAgIGlmIChoYWx2ZUFmdGVyU2NoZWR1bGUpIHtcbiAgICAgICAgICAgICAgICB0aGlzLmJhY2tvZmYgLz0gMi4wO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgfTtcbiAgICAvKiogc3RhcnQgdGhlIGNvbm5lY3Rpb24gKi9cbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5zdGFydCA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgLy8gcmVzZXQgdGhlIHN0YXRlXG4gICAgICAgIHZhciBzZWxmID0gdGhpcztcbiAgICAgICAgdGhpcy5zY2hlZHVsZWQgPSBmYWxzZTtcbiAgICAgICAgdGhpcy5kZWFkID0gZmFsc2U7XG4gICAgICAgIC8vIGNyZWF0ZSB0aGUgc29ja2V0IGFuZCBiaW5kIGV2ZW50IGhhbmRsZXJzXG4gICAgICAgIHRoaXMuc29ja2V0ID0gbmV3IGlzb21vcnBoaWNfd3NfMS5kZWZhdWx0KHRoaXMudXJsKTtcbiAgICAgICAgdGhpcy5zb2NrZXQub25tZXNzYWdlID0gZnVuY3Rpb24gKGV2ZW50KSB7XG4gICAgICAgICAgICB2YXIgcmVzdWx0ID0gSlNPTi5wYXJzZShldmVudC5kYXRhKTtcbiAgICAgICAgICAgIC8vIGEgbWVzc2FnZSBhcnJpdmVkLCBpcyBpdCBhIGNvbm5lY3Rpb24gc2lnbmFsXG4gICAgICAgICAgICBpZiAoJ3BpbmcnIGluIHJlc3VsdCkge1xuICAgICAgICAgICAgICAgIHNlbGYub25waW5nKHJlc3VsdC5waW5nLCByZXN1bHQubGF0ZW5jeSk7XG4gICAgICAgICAgICAgICAgcmVzdWx0LnBvbmcgPSBuZXcgRGF0ZSgpLmdldFRpbWUoKSAvIDEwMDAuMDtcbiAgICAgICAgICAgICAgICBzZWxmLnNvY2tldC5zZW5kKEpTT04uc3RyaW5naWZ5KHJlc3VsdCkpO1xuICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmICgnc2lnbmFsJyBpbiByZXN1bHQpIHtcbiAgICAgICAgICAgICAgICAvLyBoZXksIGFyZSB3ZSBjb25uZWN0ZWQ/XG4gICAgICAgICAgICAgICAgaWYgKHJlc3VsdC5zdGF0dXMgIT0gJ2Nvbm5lY3RlZCcpIHtcbiAgICAgICAgICAgICAgICAgICAgLy8gbm9wZSwgT0ssIGxldCdzIG1ha2UgdGhpcyBhIGRlYWQgc29ja2V0XG4gICAgICAgICAgICAgICAgICAgIHNlbGYuZGVhZCA9IHRydWU7XG4gICAgICAgICAgICAgICAgICAgIHNlbGYuc29ja2V0LmNsb3NlKCk7XG4gICAgICAgICAgICAgICAgICAgIHNlbGYuc29ja2V0ID0gbnVsbDtcbiAgICAgICAgICAgICAgICAgICAgLy8gaW5mb3JtIHRoZSBjbGllbnQgdG8gdHJ5IGFnYWluXG4gICAgICAgICAgICAgICAgICAgIHNlbGYub25hdXRobmVlZGVkKGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuc3RhcnQoKTtcbiAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgLy8gdGVsbCB0aGUgY2xpZW50IHRoYXQgd2UgYXJlIGdvb2QhXG4gICAgICAgICAgICAgICAgc2VsZi5iYWNrb2ZmID0gMTtcbiAgICAgICAgICAgICAgICBzZWxmLmNvbm5lY3RlZCA9IHRydWU7XG4gICAgICAgICAgICAgICAgc2VsZi5vbnN0YXR1c2NoYW5nZSh0cnVlKTtcbiAgICAgICAgICAgICAgICBzZWxmLl9yZWNvbm5lY3QoKTtcbiAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICAvLyB0aGUgcmVzdWx0IHdhcyBhIGZhaWx1cmUuLlxuICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXN1bHQpIHtcbiAgICAgICAgICAgICAgICAvLyBmaW5kIHRoZSBjYWxsYmFjaywgdGhlbiBpbnZva2UgaXQgKGFuZCBjbGVhbiB1cClcbiAgICAgICAgICAgICAgICBpZiAoc2VsZi5jYWxsYmFja3MuaGFzKHJlc3VsdC5mYWlsdXJlKSkge1xuICAgICAgICAgICAgICAgICAgICB2YXIgY2IgPSBzZWxmLmNhbGxiYWNrcy5nZXQocmVzdWx0LmZhaWx1cmUpO1xuICAgICAgICAgICAgICAgICAgICBpZiAoY2IpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuY2FsbGJhY2tzLmRlbGV0ZShyZXN1bHQuZmFpbHVyZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICBjYihyZXN1bHQpO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSBpZiAoJ2RlbGl2ZXInIGluIHJlc3VsdCkge1xuICAgICAgICAgICAgICAgIC8vIG90aGVyd2lzZSwgd2UgaGF2ZSBhIHN1Y2Nlc3MsIHNvIGxldCdzIGZpbmQgdGhlIGNhbGxiYWNrLCBhbmQgaWYgbmVlZCBiZSBjbGVhbiB1cFxuICAgICAgICAgICAgICAgIGlmIChzZWxmLmNhbGxiYWNrcy5oYXMocmVzdWx0LmRlbGl2ZXIpKSB7XG4gICAgICAgICAgICAgICAgICAgIHZhciBjYiA9IHNlbGYuY2FsbGJhY2tzLmdldChyZXN1bHQuZGVsaXZlcik7XG4gICAgICAgICAgICAgICAgICAgIGlmIChjYikge1xuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHJlc3VsdC5kb25lKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5jYWxsYmFja3MuZGVsZXRlKHJlc3VsdC5kZWxpdmVyKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIGNiKHJlc3VsdC5yZXNwb25zZSk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH07XG4gICAgICAgIHRoaXMuc29ja2V0Lm9uY2xvc2UgPSBmdW5jdGlvbiAoZXZlbnQpIHtcbiAgICAgICAgICAgIC8vIGxldCdzIHJldHJ5Li4uIG9yIHNob3VsZCB3ZSBub3RcbiAgICAgICAgICAgIHNlbGYuX3JldHJ5KCk7XG4gICAgICAgIH07XG4gICAgICAgIHRoaXMuc29ja2V0Lm9uZXJyb3IgPSBmdW5jdGlvbiAoZXZlbnQpIHtcbiAgICAgICAgICAgIC8vIHNvbWV0aGluZyBiYWQgaGFwcGVuZWQsIGxldCdzIHJldHJ5XG4gICAgICAgICAgICBzZWxmLl9yZXRyeSgpO1xuICAgICAgICB9O1xuICAgIH07XG4gICAgLyoqIHByaXZhdGU6IHNlbmQgYSByYXcgbWVzc2FnZSAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLl9zZW5kID0gZnVuY3Rpb24gKHJlcXVlc3QsIGNhbGxiYWNrKSB7XG4gICAgICAgIGlmICghdGhpcy5jb25uZWN0ZWQpIHtcbiAgICAgICAgICAgIGNhbGxiYWNrKHsgZmFpbHVyZTogNjAwLCByZWFzb246IDk5OTkgfSk7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cbiAgICAgICAgdmFyIGlkID0gdGhpcy5ycGNpZDtcbiAgICAgICAgdGhpcy5ycGNpZCsrO1xuICAgICAgICByZXF1ZXN0WydpZCddID0gaWQ7XG4gICAgICAgIHRoaXMuY2FsbGJhY2tzLnNldChpZCwgY2FsbGJhY2spO1xuICAgICAgICB0aGlzLnNvY2tldC5zZW5kKEpTT04uc3RyaW5naWZ5KHJlcXVlc3QpKTtcbiAgICB9O1xuICAgIC8qKiBhcGk6IHdhaXQgZm9yIGEgY29ubmVjdGlvbiAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLndhaXRfY29ubmVjdGVkID0gZnVuY3Rpb24gKCkge1xuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICB2YXIgc2VsZiwgcHJpb3I7XG4gICAgICAgICAgICByZXR1cm4gX19nZW5lcmF0b3IodGhpcywgZnVuY3Rpb24gKF9hKSB7XG4gICAgICAgICAgICAgICAgaWYgKHRoaXMuY29ubmVjdGVkKSB7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybiBbMiAvKnJldHVybiovXTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgc2VsZiA9IHRoaXM7XG4gICAgICAgICAgICAgICAgcHJpb3IgPSB0aGlzLm9uc3RhdHVzY2hhbmdlO1xuICAgICAgICAgICAgICAgIHJldHVybiBbMiAvKnJldHVybiovLCBuZXcgUHJvbWlzZShmdW5jdGlvbiAoZ29vZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5vbnN0YXR1c2NoYW5nZSA9IGZ1bmN0aW9uIChzdGF0dXMpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBwcmlvcihzdGF0dXMpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmIChzdGF0dXMpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZ29vZCgpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBzZWxmLm9uc3RhdHVzY2hhbmdlID0gcHJpb3I7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgfTtcbiAgICAgICAgICAgICAgICAgICAgfSldO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH0pO1xuICAgIH07XG4gICAgLyoqIGFwaTogZ2VuZXJhdGUgYSBuZXcgZ2FtZSAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLmdlbmVyYXRlID0gZnVuY3Rpb24gKGdzKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciByZXF1ZXN0LCBzZWxmO1xuICAgICAgICAgICAgcmV0dXJuIF9fZ2VuZXJhdG9yKHRoaXMsIGZ1bmN0aW9uIChfYSkge1xuICAgICAgICAgICAgICAgIHJlcXVlc3QgPSB7IG1ldGhvZDogXCJnZW5lcmF0ZVwiLCBnYW1lc3BhY2U6IGdzIH07XG4gICAgICAgICAgICAgICAgc2VsZiA9IHRoaXM7XG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChnb29kLCBiYWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuX3NlbmQocmVxdWVzdCwgZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBiYWQocmVzcG9uc2UucmVhc29uKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICAvKiogYXBpOiBnZXQgdGhlIHNjaGVtYSBmb3IgdGhlIGdhbWVwc2FjZSAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLnJlZmxlY3QgPSBmdW5jdGlvbiAoZ3MpIHtcbiAgICAgICAgcmV0dXJuIF9fYXdhaXRlcih0aGlzLCB2b2lkIDAsIHZvaWQgMCwgZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgdmFyIHJlcXVlc3QsIHNlbGY7XG4gICAgICAgICAgICByZXR1cm4gX19nZW5lcmF0b3IodGhpcywgZnVuY3Rpb24gKF9hKSB7XG4gICAgICAgICAgICAgICAgcmVxdWVzdCA9IHsgbWV0aG9kOiBcInJlZmxlY3RcIiwgZ2FtZXNwYWNlOiBncyB9O1xuICAgICAgICAgICAgICAgIHNlbGYgPSB0aGlzO1xuICAgICAgICAgICAgICAgIHJldHVybiBbMiAvKnJldHVybiovLCBuZXcgUHJvbWlzZShmdW5jdGlvbiAoZ29vZCwgYmFkKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBzZWxmLl9zZW5kKHJlcXVlc3QsIGZ1bmN0aW9uIChyZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmICgnZmFpbHVyZScgaW4gcmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYmFkKHJlc3BvbnNlLnJlYXNvbik7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBnb29kKHJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICAgICAgfSldO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH0pO1xuICAgIH07XG4gICAgLyoqIGFwaTogZ2VuZXJhdGUgYSBuZXcgZ2FtZSAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLmNyZWF0ZSA9IGZ1bmN0aW9uIChncywgaWQsIGFyZykge1xuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICB2YXIgcmVxdWVzdCwgc2VsZjtcbiAgICAgICAgICAgIHJldHVybiBfX2dlbmVyYXRvcih0aGlzLCBmdW5jdGlvbiAoX2EpIHtcbiAgICAgICAgICAgICAgICByZXF1ZXN0ID0geyBtZXRob2Q6IFwiY3JlYXRlXCIsIGdhbWVzcGFjZTogZ3MsIGdhbWU6IGlkLCBhcmc6IGFyZyB9O1xuICAgICAgICAgICAgICAgIHNlbGYgPSB0aGlzO1xuICAgICAgICAgICAgICAgIHJldHVybiBbMiAvKnJldHVybiovLCBuZXcgUHJvbWlzZShmdW5jdGlvbiAoZ29vZCwgYmFkKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBzZWxmLl9zZW5kKHJlcXVlc3QsIGZ1bmN0aW9uIChyZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmICgnZmFpbHVyZScgaW4gcmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYmFkKHJlc3BvbnNlLnJlYXNvbik7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBnb29kKHJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICAgICAgfSldO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH0pO1xuICAgIH07XG4gICAgLyoqIGFwaTogY29ubmVjdCB0byBhIGdhbWUgKi9cbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5jb25uZWN0ID0gZnVuY3Rpb24gKGdzLCBpZCwgaGFuZGxlcikge1xuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICB2YXIgcmVxdWVzdCwgc2VsZiwgZmlyc3Q7XG4gICAgICAgICAgICByZXR1cm4gX19nZW5lcmF0b3IodGhpcywgZnVuY3Rpb24gKF9hKSB7XG4gICAgICAgICAgICAgICAgcmVxdWVzdCA9IHsgbWV0aG9kOiBcImNvbm5lY3RcIiwgZ2FtZXNwYWNlOiBncywgZ2FtZTogaWQgfTtcbiAgICAgICAgICAgICAgICBzZWxmID0gdGhpcztcbiAgICAgICAgICAgICAgICBmaXJzdCA9IHRydWU7XG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChnb29kLCBiYWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuX3NlbmQocmVxdWVzdCwgZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKGZpcnN0KSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGZpcnN0ID0gZmFsc2U7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmICgnZmFpbHVyZScgaW4gcmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGJhZChyZXNwb25zZS5yZWFzb24pO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgaGFuZGxlcihyZXNwb25zZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBnb29kKHRydWUpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBoYW5kbGVyKHJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICAgICAgfSldO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH0pO1xuICAgIH07XG4gICAgLyoqIGFwaTogc2VuZCBhIG1lc3NhZ2UgKi9cbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5zZW5kID0gZnVuY3Rpb24gKGdzLCBpZCwgY2hhbm5lbCwgbXNnLCBoYWNrKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciByZXF1ZXN0LCBzZWxmO1xuICAgICAgICAgICAgcmV0dXJuIF9fZ2VuZXJhdG9yKHRoaXMsIGZ1bmN0aW9uIChfYSkge1xuICAgICAgICAgICAgICAgIHJlcXVlc3QgPSB7IG1ldGhvZDogXCJzZW5kXCIsIGdhbWVzcGFjZTogZ3MsIGdhbWU6IGlkLCBjaGFubmVsOiBjaGFubmVsLCBtZXNzYWdlOiBtc2cgfTtcbiAgICAgICAgICAgICAgICBzZWxmID0gdGhpcztcbiAgICAgICAgICAgICAgICBpZiAoaGFjaykge1xuICAgICAgICAgICAgICAgICAgICBoYWNrKHJlcXVlc3QpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAvLyBUT0RPOiBxdWV1ZSB0aGlzIHVwPyB3aXRoIHJldHJ5P1xuICAgICAgICAgICAgICAgIC8vIFRPRE86IGdlbmVyYXRlIGEgbWFya2VyXG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChnb29kLCBiYWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuX3NlbmQocmVxdWVzdCwgZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBiYWQocmVzcG9uc2UucmVhc29uKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICAvKiogYXBpOiBjb25uZWN0IHRyZWUgKi9cbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5jb25uZWN0VHJlZSA9IGZ1bmN0aW9uIChncywgaWQsIHRyZWUsIGhhY2spIHtcbiAgICAgICAgcmV0dXJuIF9fYXdhaXRlcih0aGlzLCB2b2lkIDAsIHZvaWQgMCwgZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgdmFyIGtleUlkLCBzbTtcbiAgICAgICAgICAgIHJldHVybiBfX2dlbmVyYXRvcih0aGlzLCBmdW5jdGlvbiAoX2EpIHtcbiAgICAgICAgICAgICAgICBrZXlJZCA9IHRoaXMuY29ubmVjdElkO1xuICAgICAgICAgICAgICAgIHRoaXMuY29ubmVjdElkKys7XG4gICAgICAgICAgICAgICAgc20gPSB7XG4gICAgICAgICAgICAgICAgICAgIHJlcXVlc3Q6IHsgbWV0aG9kOiBcImNvbm5lY3RcIiwgZ2FtZXNwYWNlOiBncywgZ2FtZTogaWQgfSxcbiAgICAgICAgICAgICAgICAgICAgZmlyc3Q6IHRydWUsXG4gICAgICAgICAgICAgICAgICAgIGhhbmRsZXI6IGZ1bmN0aW9uIChyKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICB0cmVlLm1lcmdlVXBkYXRlKHIpO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfTtcbiAgICAgICAgICAgICAgICBpZiAoaGFjaykge1xuICAgICAgICAgICAgICAgICAgICBoYWNrKHNtLnJlcXVlc3QpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB0aGlzLm9ucmVjb25uZWN0LnNldChrZXlJZCwgc20pO1xuICAgICAgICAgICAgICAgIHJldHVybiBbMiAvKnJldHVybiovLCB0aGlzLl9leGVjdXRlKHNtKV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5fcmVjb25uZWN0ID0gZnVuY3Rpb24gKCkge1xuICAgICAgICB2YXIgc2VsZiA9IHRoaXM7XG4gICAgICAgIHRoaXMub25yZWNvbm5lY3QuZm9yRWFjaChmdW5jdGlvbiAoc20sIGlkKSB7XG4gICAgICAgICAgICBzZWxmLl9leGVjdXRlKHNtKTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5fZXhlY3V0ZSA9IGZ1bmN0aW9uIChzbSkge1xuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICB2YXIgc2VsZjtcbiAgICAgICAgICAgIHJldHVybiBfX2dlbmVyYXRvcih0aGlzLCBmdW5jdGlvbiAoX2EpIHtcbiAgICAgICAgICAgICAgICBzZWxmID0gdGhpcztcbiAgICAgICAgICAgICAgICByZXR1cm4gWzIgLypyZXR1cm4qLywgbmV3IFByb21pc2UoZnVuY3Rpb24gKGdvb2QsIGJhZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5fc2VuZChzbS5yZXF1ZXN0LCBmdW5jdGlvbiAocmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoc20uZmlyc3QpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc20uZmlyc3QgPSBmYWxzZTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYmFkKHJlc3BvbnNlLnJlYXNvbik7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBzbS5oYW5kbGVyKHJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QodHJ1ZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHNtLmhhbmRsZXIocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICByZXR1cm4gQ29ubmVjdGlvbjtcbn0oKSk7XG5leHBvcnRzLkNvbm5lY3Rpb24gPSBDb25uZWN0aW9uO1xuIiwiXCJ1c2Ugc3RyaWN0XCI7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJfX2VzTW9kdWxlXCIsIHsgdmFsdWU6IHRydWUgfSk7XG5leHBvcnRzLlRyZWUgPSB2b2lkIDA7XG52YXIgVHJlZSA9IC8qKiBAY2xhc3MgKi8gKGZ1bmN0aW9uICgpIHtcbiAgICBmdW5jdGlvbiBUcmVlKCkge1xuICAgICAgICB0aGlzLnRyZWUgPSB7fTtcbiAgICAgICAgdGhpcy5kaXNwYXRjaCA9IHt9O1xuICAgICAgICB0aGlzLmRpc3BhdGNoX2NvdW50ID0gMDtcbiAgICAgICAgdGhpcy5xdWV1ZSA9IFtdO1xuICAgICAgICB0aGlzLm9uZGVjaWRlID0gZnVuY3Rpb24gKG91dHN0YW5kaW5nKSB7IH07XG4gICAgfVxuICAgIC8vIHJlY3Vyc2l2ZWx5IGFwcGVuZCBhIGNoYW5nZVxuICAgIC8vIGRpc3BhdGNoIGlzIHRoZSBzdHJ1Y3R1cmFsIG9iamVjdCBtaXJyb3JpbmcgdGhlIHRyZWVcbiAgICAvLyBjYWxsYmFjayBpcyB0aGUgZnVuY3Rpb24vb2JqZWN0IGNhbGxiYWNrIHRyZWVcbiAgICAvLyBpbnNlcnRfb3JkZXIgaXMgdGhlIG9yZGVyIHRvIGZpcmUgZXZlbnRzXG4gICAgVHJlZS5wcm90b3R5cGUuX19yZWNBcHBlbmRDaGFuZ2UgPSBmdW5jdGlvbiAoZGlzcGF0Y2gsIGNhbGxiYWNrLCBpbnNlcnRfb3JkZXIpIHtcbiAgICAgICAgLy8gdGhlIGNhbGxiYWNrIGlzIGFuIG9iamVjdFxuICAgICAgICBpZiAodHlwZW9mIChjYWxsYmFjaykgPT0gJ29iamVjdCcpIHtcbiAgICAgICAgICAgIC8vIHdlIGZvciBlYWNoIGl0ZW0gaW4gdGhlIGNhbGxiYWNrXG4gICAgICAgICAgICBmb3IgKHZhciBrZXkgaW4gY2FsbGJhY2spIHtcbiAgICAgICAgICAgICAgICAvLyBtYWtlIHN1cmUgaXQgZXhpc3RzXG4gICAgICAgICAgICAgICAgaWYgKCEoa2V5IGluIGRpc3BhdGNoKSkge1xuICAgICAgICAgICAgICAgICAgICBkaXNwYXRjaFtrZXldID0ge307XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIC8vIHJlY3Vyc2UgaW50byB0aGF0IGtleVxuICAgICAgICAgICAgICAgIHRoaXMuX19yZWNBcHBlbmRDaGFuZ2UoZGlzcGF0Y2hba2V5XSwgY2FsbGJhY2tba2V5XSwgaW5zZXJ0X29yZGVyKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmICh0eXBlb2YgKGNhbGxiYWNrKSA9PSAnZnVuY3Rpb24nKSB7XG4gICAgICAgICAgICAvLyB3ZSBoYXZlIGEgZnVuY3Rpb24sIHNvIGxldCdzIGFzc29jaWF0ZSBpdCB0byB0aGUgbm9kZVxuICAgICAgICAgICAgaWYgKCEoJ0BlJyBpbiBkaXNwYXRjaCkpIHtcbiAgICAgICAgICAgICAgICBkaXNwYXRjaFsnQGUnXSA9IFtdO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZGlzcGF0Y2hbJ0BlJ10ucHVzaCh7IGNiOiBjYWxsYmFjaywgb3JkZXI6IGluc2VydF9vcmRlciB9KTtcbiAgICAgICAgfVxuICAgIH07XG4gICAgVHJlZS5wcm90b3R5cGUub25UcmVlQ2hhbmdlID0gZnVuY3Rpb24gKGNhbGxiYWNrKSB7XG4gICAgICAgIHRoaXMuX19yZWNBcHBlbmRDaGFuZ2UodGhpcy5kaXNwYXRjaCwgY2FsbGJhY2ssIHRoaXMuZGlzcGF0Y2hfY291bnQpO1xuICAgICAgICB0aGlzLmRpc3BhdGNoX2NvdW50Kys7XG4gICAgfTtcbiAgICAvLyB0aGUgbWFpbiBmdW5jdGlvblxuICAgIFRyZWUucHJvdG90eXBlLm1lcmdlVXBkYXRlID0gZnVuY3Rpb24gKGRpZmYpIHtcbiAgICAgICAgaWYgKCdkYXRhJyBpbiBkaWZmKSB7XG4gICAgICAgICAgICAvLyB3ZSBtZXJnZSB0aGUgdHJlZSB3aXRoIHRoZSBkYXRhIHdpdGhpbiBkaXNwYXRjaFxuICAgICAgICAgICAgdGhpcy5fX3JlY01lcmdlQW5kRGlzcGF0Y2godGhpcy50cmVlLCB0aGlzLmRpc3BhdGNoLCBkaWZmLmRhdGEpO1xuICAgICAgICB9XG4gICAgICAgIGlmICgnb3V0c3RhbmRpbmcnIGluIGRpZmYpIHtcbiAgICAgICAgICAgIHRoaXMub25kZWNpZGUoZGlmZi5vdXRzdGFuZGluZyk7XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5fX2RyYWluKCk7XG4gICAgfTtcbiAgICBUcmVlLnByb3RvdHlwZS5fX3JlY0RlbGV0ZUFuZERpc3BhdGNoID0gZnVuY3Rpb24gKHRyZWUsIGRpc3BhdGNoKSB7XG4gICAgICAgIGZvciAodmFyIGtleSBpbiB0cmVlKSB7XG4gICAgICAgICAgICB2YXIgb2xkID0gdHJlZVtrZXldO1xuICAgICAgICAgICAgaWYgKEFycmF5LmlzQXJyYXkob2xkKSkge1xuICAgICAgICAgICAgICAgIC8vIG5lZWQgdG8gZmlyZSB0aGUgREVMRVRFXG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICBpZiAoa2V5IGluIGRpc3BhdGNoKSB7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuX19yZWNEZWxldGVBbmREaXNwYXRjaChvbGQsIGRpc3BhdGNoW2tleV0pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHZhciBkZWxldGVDaGlsZEtleSA9ICctJyArIGtleTtcbiAgICAgICAgICAgIGlmIChkaXNwYXRjaCAhPSBudWxsICYmIGRlbGV0ZUNoaWxkS2V5IGluIGRpc3BhdGNoKSB7XG4gICAgICAgICAgICAgICAgdGhpcy5fX2ZpcmUoZGlzcGF0Y2hbZGVsZXRlQ2hpbGRLZXldLCB7IGtleToga2V5LCBiZWZvcmU6IG9sZCwgdmFsdWU6IG51bGwgfSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9O1xuICAgIFRyZWUucHJvdG90eXBlLl9fcmVjTWVyZ2VBbmREaXNwYXRjaCA9IGZ1bmN0aW9uICh0cmVlLCBkaXNwYXRjaCwgZGlmZikge1xuICAgICAgICAvLyB0aGUgZGlmZiBpcyBhbiBvYmplY3QsIHNvIGxldCdzIHdhbGsgaXRzIGtleXNcbiAgICAgICAgZm9yICh2YXIga2V5IGluIGRpZmYpIHtcbiAgICAgICAgICAgIHZhciBjaGlsZCA9IGRpZmZba2V5XTtcbiAgICAgICAgICAgIGlmIChjaGlsZCA9PT0gbnVsbCkge1xuICAgICAgICAgICAgICAgIHZhciBkZWxldGVDaGlsZEtleSA9ICctJyArIGtleTtcbiAgICAgICAgICAgICAgICB2YXIgb2xkXzEgPSB0cmVlW2tleV07XG4gICAgICAgICAgICAgICAgaWYgKGRpc3BhdGNoICE9IG51bGwgJiYgZGVsZXRlQ2hpbGRLZXkgaW4gZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fX2ZpcmUoZGlzcGF0Y2hbZGVsZXRlQ2hpbGRLZXldLCB7IGtleToga2V5LCBiZWZvcmU6IG9sZF8xLCB2YWx1ZTogbnVsbCB9KTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgaWYgKEFycmF5LmlzQXJyYXkob2xkXzEpKSB7XG4gICAgICAgICAgICAgICAgICAgIC8vIGFsc28gZGVsZXRlXG4gICAgICAgICAgICAgICAgICAgIHZhciBlbGVtZW50S2V5ID0gXCIjXCIgKyBrZXk7XG4gICAgICAgICAgICAgICAgICAgIGlmIChlbGVtZW50S2V5IGluIHRyZWUgJiYgZWxlbWVudEtleSBpbiBkaXNwYXRjaCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgdGhpcy5fX3JlY0RlbGV0ZUFuZERpc3BhdGNoKHRyZWVbZWxlbWVudEtleV0sIGRpc3BhdGNoW2VsZW1lbnRLZXldKTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBkZWxldGUgdHJlZVtcIiNcIiArIGtleV07XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGlmIChrZXkgaW4gdHJlZSAmJiBrZXkgaW4gZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fX3JlY0RlbGV0ZUFuZERpc3BhdGNoKHRyZWVba2V5XSwgZGlzcGF0Y2hba2V5XSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGRlbGV0ZSB0cmVlW2tleV07XG4gICAgICAgICAgICAgICAgaWYgKGRpc3BhdGNoICE9IG51bGwgJiYga2V5IGluIGRpc3BhdGNoKSB7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuX19maXJlKGRpc3BhdGNoW2tleV0sIHsga2V5OiBrZXksIGJlZm9yZTogb2xkXzEsIHZhbHVlOiBudWxsIH0pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBjb250aW51ZTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIC8vIGlmIHRoZSBjaGlsZCBpcyBhbiBvYmplY3QsIHRoZW4uLlxuICAgICAgICAgICAgdmFyIGZpcmVOZXcgPSAhKGtleSBpbiB0cmVlKTtcbiAgICAgICAgICAgIGlmICh0eXBlb2YgKGNoaWxkKSA9PSAnb2JqZWN0Jykge1xuICAgICAgICAgICAgICAgIC8vIHRoZSBjaGlsZCBpcyBlaXRoZXIgYW4gQVJSQVkgb3IgYSBPQkpFQ1RcbiAgICAgICAgICAgICAgICB2YXIgY2hpbGRJc0FycmF5ID0gJ0BvJyBpbiBjaGlsZCB8fCAnQHMnIGluIGNoaWxkO1xuICAgICAgICAgICAgICAgIC8vIHRoZSBwcmlvciB2ZXJzaW9uIGRvZXNuJ3QgZXhpc3QsIHNvIHdlIGNyZWF0ZSB0aGUgZW1wdHkgbm9kZSBzbyB0aGF0IGl0IGRvZXMgZXhpc3RcbiAgICAgICAgICAgICAgICBpZiAoIShrZXkgaW4gdHJlZSkpIHtcbiAgICAgICAgICAgICAgICAgICAgaWYgKGNoaWxkSXNBcnJheSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgdHJlZVtrZXldID0gW107XG4gICAgICAgICAgICAgICAgICAgICAgICB0cmVlW1wiI1wiICsga2V5XSA9IHt9O1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgdHJlZVtrZXldID0ge307XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgLy8gbm93LCB3ZSBjaGVjayB0byBzZWUgaWYgdGhlIHByaW9yIHN0YXRlIGluZmx1ZW5jZXMgd2hldGhlciBvciBub3QgdGhlIGRpZmYgaXMgYW4gYXJyYXlcbiAgICAgICAgICAgICAgICBjaGlsZElzQXJyYXkgPSBBcnJheS5pc0FycmF5KHRyZWVba2V5XSkgfHwgY2hpbGRJc0FycmF5O1xuICAgICAgICAgICAgICAgIGlmIChjaGlsZElzQXJyYXkpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fX3JlY01lcmdlQW5kRGlzcGF0Y2hBcnJheSh0cmVlW2tleV0sIChkaXNwYXRjaCAhPSBudWxsICYmIGtleSBpbiBkaXNwYXRjaCkgPyBkaXNwYXRjaFtrZXldIDogbnVsbCwgdHJlZVtcIiNcIiArIGtleV0sIGNoaWxkKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuX19yZWNNZXJnZUFuZERpc3BhdGNoKHRyZWVba2V5XSwgKGRpc3BhdGNoICE9IG51bGwgJiYga2V5IGluIGRpc3BhdGNoKSA/IGRpc3BhdGNoW2tleV0gOiBudWxsLCBjaGlsZCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgdmFyIG9sZCA9IChrZXkgaW4gdHJlZSkgPyB0cmVlW2tleV0gOiBudWxsO1xuICAgICAgICAgICAgICAgIHRyZWVba2V5XSA9IGNoaWxkO1xuICAgICAgICAgICAgICAgIGlmIChkaXNwYXRjaCAhPSBudWxsICYmIGtleSBpbiBkaXNwYXRjaCkge1xuICAgICAgICAgICAgICAgICAgICB0aGlzLl9fZmlyZShkaXNwYXRjaFtrZXldLCB7IGtleToga2V5LCBiZWZvcmU6IG9sZCwgdmFsdWU6IGNoaWxkIH0pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmIChmaXJlTmV3KSB7IC8vIGZpcmUgbmV3IGRhdGEgZXZlbnRcbiAgICAgICAgICAgICAgICB2YXIgbmV3Q2hpbGRLZXkgPSAnKycgKyBrZXk7XG4gICAgICAgICAgICAgICAgaWYgKGRpc3BhdGNoICE9IG51bGwgJiYgbmV3Q2hpbGRLZXkgaW4gZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fX2ZpcmUoZGlzcGF0Y2hbbmV3Q2hpbGRLZXldLCB7IGtleToga2V5LCB2YWx1ZTogdHJlZVtrZXldIH0pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmIChkaXNwYXRjaCAhPSBudWxsICYmICdAZScgaW4gZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgICAgICB0aGlzLl9fZmlyZShkaXNwYXRjaCwgeyB2YWx1ZTogdHJlZSB9KTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgIH07XG4gICAgVHJlZS5wcm90b3R5cGUuX19yZWNNZXJnZUFuZERpc3BhdGNoQXJyYXkgPSBmdW5jdGlvbiAocHJpb3IsIGRpc3BhdGNoLCB0cmVlLCBkaWZmKSB7XG4gICAgICAgIC8vIFRPRE86IG5ldyBpdGVtLi4uIGV0Y1xuICAgICAgICB2YXIgb3JkZXJpbmcgPSBudWxsO1xuICAgICAgICB2YXIgcmVzaXplID0gbnVsbDtcbiAgICAgICAgZm9yICh2YXIga2V5IGluIGRpZmYpIHtcbiAgICAgICAgICAgIGlmIChrZXkgPT0gXCJAb1wiKSB7XG4gICAgICAgICAgICAgICAgb3JkZXJpbmcgPSBkaWZmW2tleV07XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIGlmIChrZXkgPT0gXCJAc1wiKSB7XG4gICAgICAgICAgICAgICAgcmVzaXplID0gZGlmZltrZXldO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgaWYgKGRpZmZba2V5XSA9PSBudWxsKSB7XG4gICAgICAgICAgICAgICAgICAgIGlmIChkaXNwYXRjaCAmJiAnLScgaW4gZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHRoaXMuX19maXJlKGRpc3BhdGNoWyctJ10sIHsga2V5OiBrZXksIGJlZm9yZTogdHJlZVtrZXldLCB2YWx1ZTogbnVsbCB9KTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBkZWxldGUgdHJlZVtrZXldO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgdmFyIGZpcmVOZXcgPSBmYWxzZTtcbiAgICAgICAgICAgICAgICAgICAgaWYgKCEodHJlZSAhPSBudWxsICYmIGtleSBpbiB0cmVlKSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKGRpc3BhdGNoICYmICcrJyBpbiBkaXNwYXRjaCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGZpcmVOZXcgPSB0cnVlO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgdHJlZVtrZXldID0ge307XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fX3JlY01lcmdlQW5kRGlzcGF0Y2godHJlZVtrZXldLCAoZGlzcGF0Y2ggIT0gbnVsbCAmJiAnIycgaW4gZGlzcGF0Y2gpID8gZGlzcGF0Y2hbJyMnXSA6IG51bGwsIGRpZmZba2V5XSk7XG4gICAgICAgICAgICAgICAgICAgIGlmIChmaXJlTmV3KSB7XG4gICAgICAgICAgICAgICAgICAgICAgICB0aGlzLl9fZmlyZShkaXNwYXRjaFsnKyddLCB7IGtleToga2V5LCBiZWZvcmU6IG51bGwsIHZhbHVlOiB0cmVlW2tleV0gfSk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgdmFyIGNoYW5nZSA9IHsgYmVmb3JlOiBwcmlvciwgdmFsdWU6IHByaW9yIH07XG4gICAgICAgIGlmIChyZXNpemUgIT09IG51bGwpIHtcbiAgICAgICAgICAgIC8vIFNlZSBETGlzdCwgYnV0IHRoZSBpZGVhIGlzIHRoYXQgSSBuZWVkIHRvIHRyaW0gdGhlIGxpc3QgYmVjYXVzZSB0aGUgYWJvdmUgdG9vbCBjYXJlIG9mIG51bGxzXG4gICAgICAgICAgICAvLyB0aGlzIGlzIGZvciBsaXN0IG9mIHZhbHVlcyB3aGVyZSB3ZSBzeW5jaHJvbml6ZSBhIGxpc3Qgb2YgY29uc3RhbnRzXG4gICAgICAgICAgICBjaGFuZ2UuYmVmb3JlID0gW107XG4gICAgICAgICAgICBmb3IgKHZhciBrID0gMDsgayA8IHByaW9yLmxlbmd0aDsgaysrKSB7XG4gICAgICAgICAgICAgICAgY2hhbmdlLmJlZm9yZS5wdXNoKHByaW9yW2tdKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHByaW9yLmxlbmd0aCA9IHJlc2l6ZTtcbiAgICAgICAgICAgIC8vIFRPRE86IHZhbGlkYXRlIHRoaXNcbiAgICAgICAgfVxuICAgICAgICBpZiAob3JkZXJpbmcgIT09IG51bGwpIHtcbiAgICAgICAgICAgIHZhciBhZnRlciA9IFtdO1xuICAgICAgICAgICAgY2hhbmdlLmJlZm9yZSA9IFtdO1xuICAgICAgICAgICAgdmFyIGltcGxpY2l0RGVsZXRlID0gZGlzcGF0Y2ggPyAnLScgaW4gZGlzcGF0Y2ggOiBmYWxzZTtcbiAgICAgICAgICAgIGZvciAodmFyIGsgPSAwOyBrIDwgcHJpb3IubGVuZ3RoOyBrKyspIHtcbiAgICAgICAgICAgICAgICBjaGFuZ2UuYmVmb3JlLnB1c2gocHJpb3Jba10pO1xuICAgICAgICAgICAgICAgIGlmIChpbXBsaWNpdERlbGV0ZSkge1xuICAgICAgICAgICAgICAgICAgICBwcmlvcltrXS5fX2tpbGwgPSB0cnVlO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGZvciAodmFyIGsgPSAwOyBrIDwgb3JkZXJpbmcubGVuZ3RoOyBrKyspIHtcbiAgICAgICAgICAgICAgICB2YXIgaW5zdHIgPSBvcmRlcmluZ1trXTtcbiAgICAgICAgICAgICAgICB2YXIgdHlwZV9pbnN0ciA9IHR5cGVvZiAoaW5zdHIpO1xuICAgICAgICAgICAgICAgIGlmICh0eXBlX2luc3RyID09IFwic3RyaW5nXCIgfHwgdHlwZV9pbnN0ciA9PSBcIm51bWJlclwiKSB7XG4gICAgICAgICAgICAgICAgICAgIGFmdGVyLnB1c2godHJlZVtpbnN0cl0pO1xuICAgICAgICAgICAgICAgICAgICBpZiAoaW1wbGljaXREZWxldGUpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHRyZWVbaW5zdHJdLl9fa2lsbCA9IGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICB2YXIgc3RhcnQgPSBpbnN0clswXTtcbiAgICAgICAgICAgICAgICAgICAgdmFyIGVuZCA9IGluc3RyWzFdO1xuICAgICAgICAgICAgICAgICAgICBmb3IgKHZhciBqID0gc3RhcnQ7IGogPD0gZW5kOyBqKyspIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChpbXBsaWNpdERlbGV0ZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHByaW9yW2pdLl9fa2lsbCA9IGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgYWZ0ZXIucHVzaChwcmlvcltqXSk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBpZiAoaW1wbGljaXREZWxldGUpIHtcbiAgICAgICAgICAgICAgICBmb3IgKGtleSBpbiB0cmVlKSB7XG4gICAgICAgICAgICAgICAgICAgIGlmICh0cmVlW2tleV0uX19raWxsKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAoa2V5IGluIGRpc3BhdGNoKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgdGhpcy5fX3JlY0RlbGV0ZUFuZERpc3BhdGNoKHRyZWVba2V5XSwgZGlzcGF0Y2hba2V5XSk7XG4gICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICB0aGlzLl9fZmlyZShkaXNwYXRjaFsnLSddLCB7IGtleToga2V5LCBiZWZvcmU6IHRyZWVba2V5XSwgdmFsdWU6IG51bGwgfSk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgZGVsZXRlIHRyZWVba2V5XS5fX2tpbGw7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgcHJpb3IubGVuZ3RoID0gYWZ0ZXIubGVuZ3RoO1xuICAgICAgICAgICAgZm9yICh2YXIgayA9IDA7IGsgPCBhZnRlci5sZW5ndGg7IGsrKykge1xuICAgICAgICAgICAgICAgIHByaW9yW2tdID0gYWZ0ZXJba107XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5fX2ZpcmUoZGlzcGF0Y2gsIGNoYW5nZSk7XG4gICAgfTtcbiAgICBUcmVlLnByb3RvdHlwZS5fX2ZpcmUgPSBmdW5jdGlvbiAoZGlzcGF0Y2gsIGNoYW5nZSkge1xuICAgICAgICBpZiAoZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgIGlmICgnQGUnIGluIGRpc3BhdGNoKSB7XG4gICAgICAgICAgICAgICAgdmFyIGQgPSBkaXNwYXRjaFsnQGUnXTtcbiAgICAgICAgICAgICAgICB2YXIgbnVsbHMgPSAwO1xuICAgICAgICAgICAgICAgIGZvciAodmFyIGsgPSAwOyBrIDwgZC5sZW5ndGg7IGsrKykge1xuICAgICAgICAgICAgICAgICAgICB2YXIgZXZ0ID0gZFtrXTtcbiAgICAgICAgICAgICAgICAgICAgaWYgKGV2dCAhPT0gbnVsbCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgdGhpcy5xdWV1ZS5wdXNoKHsgY2I6IGV2dC5jYiwgb3JkZXI6IGV2dC5vcmRlciwgY2hhbmdlOiBjaGFuZ2UsIGRpc3BhdGNoX2xpc3Q6IGQsIGluZGV4OiBrIH0pO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgbnVsbHMrKztcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBpZiAobnVsbHMgPiAwKSB7XG4gICAgICAgICAgICAgICAgICAgIHZhciBueHQgPSBbXTtcbiAgICAgICAgICAgICAgICAgICAgZm9yICh2YXIgayA9IDA7IGsgPCBkLmxlbmd0aDsgaysrKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAoZXZ0ICE9PSBudWxsKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgbnh0LnB1c2goZXZ0KTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBkaXNwYXRjaFsnQGUnXSA9IG54dDtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9O1xuICAgIFRyZWUucHJvdG90eXBlLl9fZHJhaW4gPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHRoaXMucXVldWUuc29ydChmdW5jdGlvbiAoYSwgYikgeyByZXR1cm4gYS5vcmRlciAtIGIub3JkZXI7IH0pO1xuICAgICAgICBmb3IgKHZhciBrID0gMDsgayA8IHRoaXMucXVldWUubGVuZ3RoOyBrKyspIHtcbiAgICAgICAgICAgIHZhciBpdGVtID0gdGhpcy5xdWV1ZVtrXTtcbiAgICAgICAgICAgIGlmIChpdGVtLmNiKGl0ZW0uY2hhbmdlKSA9PT0gJ2RlbGV0ZScpIHtcbiAgICAgICAgICAgICAgICBpdGVtLmRpc3BhdGNoX2xpc3RbaXRlbS5pbmRleF0gPSBudWxsO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIHRoaXMucXVldWUgPSBbXTtcbiAgICB9O1xuICAgIHJldHVybiBUcmVlO1xufSgpKTtcbmV4cG9ydHMuVHJlZSA9IFRyZWU7XG4iXSwic291cmNlUm9vdCI6IiJ9
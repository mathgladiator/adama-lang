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
    Connection.prototype.send = function (gs, id, channel, msg) {
        return __awaiter(this, void 0, void 0, function () {
            var request, self;
            return __generator(this, function (_a) {
                request = { method: "send", gamespace: gs, game: id, channel: channel, message: msg };
                self = this;
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
    Connection.prototype.connectTree = function (gs, id, tree) {
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
        this.onfetch = function (channel) { };
        this.ondecide = function (channel, options) { };
    }
    Tree.prototype.__recAppendChange = function (dispatch, callback, insert_order) {
        if (typeof (callback) == 'object') {
            for (var key in callback) {
                if (!(key in dispatch)) {
                    dispatch[key] = {};
                }
                this.__recAppendChange(dispatch[key], callback[key], insert_order);
            }
        }
        else if (typeof (callback) == 'function') {
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
    Tree.prototype.mergeUpdate = function (diff) {
        if ('data' in diff) {
            this.__recMergeAndDispatch(this.tree, this.dispatch, diff.data);
        }
        // TODO: dispatch decisions
        // TODO: update blockers
        this.__drain();
    };
    Tree.prototype.__recMergeAndDispatchArray = function (prior, dispatch, tree, diff) {
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
                    // FIRE_DELETE_ELEMENT
                    delete tree[key];
                }
                else {
                    if (!(tree != null && key in tree)) {
                        tree[key] = {};
                    }
                    // TODO: does it make sense to track individual keys?
                    this.__recMergeAndDispatch(tree[key], (dispatch != null && key in dispatch) ? dispatch[key] : null, diff[key]);
                    // this will fire an update for the key
                }
            }
        }
        var change = { before: prior, after: prior, parent: tree };
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
            for (var k = 0; k < prior.length; k++) {
                change.before.push(prior[k]);
            }
            for (var k = 0; k < ordering.length; k++) {
                var instr = ordering[k];
                var type_instr = typeof (instr);
                if (type_instr == "string" || type_instr == "number") {
                    after.push(tree[instr]);
                }
                else {
                    var start = instr[0];
                    var end = instr[1];
                    for (var j = start; j <= end; j++) {
                        after.push(prior[j]);
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
    };
    Tree.prototype.__fire = function (dispatch, change) {
        if (dispatch) {
            if ('@e' in dispatch) {
                var d = dispatch['@e'];
                for (var k = 0; k < d.length; k++) {
                    var evt = d[k];
                    this.queue.push({ cb: evt.cb, order: evt.order, change: change });
                }
            }
        }
    };
    Tree.prototype.__drain = function () {
        this.queue.sort(function (a, b) { return a.order - b.order; });
        for (var k = 0; k < this.queue.length; k++) {
            var item = this.queue[k];
            item.cb(item.change);
        }
        this.queue = [];
    };
    Tree.prototype.__recMergeAndDispatch = function (tree, dispatch, diff) {
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
                    // we have an object, let's merge recursively... YAY
                    this.__recMergeAndDispatch(tree[key], (dispatch != null && key in dispatch) ? dispatch[key] : null, child);
                    // NOTE: this will fire events
                }
            }
            else {
                var change = { before: tree[key], after: child, parent: tree };
                tree[key] = child;
                if (dispatch != null && key in dispatch) {
                    this.__fire(dispatch[key], change);
                }
            }
            if (dispatch != null && '@e' in dispatch) {
                this.__fire(dispatch, tree);
            }
        }
    };
    return Tree;
}());
exports.Tree = Tree;


/***/ })

/******/ });
});
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly9BZGFtYS93ZWJwYWNrL3VuaXZlcnNhbE1vZHVsZURlZmluaXRpb24iLCJ3ZWJwYWNrOi8vQWRhbWEvd2VicGFjay9ib290c3RyYXAiLCJ3ZWJwYWNrOi8vQWRhbWEvLi9ub2RlX21vZHVsZXMvaXNvbW9ycGhpYy13cy9icm93c2VyLmpzIiwid2VicGFjazovL0FkYW1hLyh3ZWJwYWNrKS9idWlsZGluL2dsb2JhbC5qcyIsIndlYnBhY2s6Ly9BZGFtYS8uL3NyYy9pbmRleC50cyIsIndlYnBhY2s6Ly9BZGFtYS8uL3NyYy90cmVlLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLENBQUM7QUFDRCxPO1FDVkE7UUFDQTs7UUFFQTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBOztRQUVBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7OztRQUdBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7UUFDQSwwQ0FBMEMsZ0NBQWdDO1FBQzFFO1FBQ0E7O1FBRUE7UUFDQTtRQUNBO1FBQ0Esd0RBQXdELGtCQUFrQjtRQUMxRTtRQUNBLGlEQUFpRCxjQUFjO1FBQy9EOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQSx5Q0FBeUMsaUNBQWlDO1FBQzFFLGdIQUFnSCxtQkFBbUIsRUFBRTtRQUNySTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBLDJCQUEyQiwwQkFBMEIsRUFBRTtRQUN2RCxpQ0FBaUMsZUFBZTtRQUNoRDtRQUNBO1FBQ0E7O1FBRUE7UUFDQSxzREFBc0QsK0RBQStEOztRQUVySDtRQUNBOzs7UUFHQTtRQUNBOzs7Ozs7Ozs7Ozs7QUNsRkE7O0FBRUE7O0FBRUE7QUFDQTtBQUNBLENBQUM7QUFDRDtBQUNBLENBQUM7QUFDRDtBQUNBLENBQUM7QUFDRDtBQUNBLENBQUM7QUFDRDtBQUNBOztBQUVBOzs7Ozs7Ozs7Ozs7O0FDaEJBOztBQUVBO0FBQ0E7QUFDQTtBQUNBLENBQUM7O0FBRUQ7QUFDQTtBQUNBO0FBQ0EsQ0FBQztBQUNEO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0EsNENBQTRDOztBQUU1Qzs7Ozs7Ozs7Ozs7OztBQ25CYTtBQUNiO0FBQ0EsMkJBQTJCLCtEQUErRCxnQkFBZ0IsRUFBRSxFQUFFO0FBQzlHO0FBQ0EsbUNBQW1DLE1BQU0sNkJBQTZCLEVBQUUsWUFBWSxXQUFXLEVBQUU7QUFDakcsa0NBQWtDLE1BQU0saUNBQWlDLEVBQUUsWUFBWSxXQUFXLEVBQUU7QUFDcEcsK0JBQStCLHFGQUFxRjtBQUNwSDtBQUNBLEtBQUs7QUFDTDtBQUNBO0FBQ0EsYUFBYSw2QkFBNkIsMEJBQTBCLGFBQWEsRUFBRSxxQkFBcUI7QUFDeEcsZ0JBQWdCLHFEQUFxRCxvRUFBb0UsYUFBYSxFQUFFO0FBQ3hKLHNCQUFzQixzQkFBc0IscUJBQXFCLEdBQUc7QUFDcEU7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsdUNBQXVDO0FBQ3ZDLGtDQUFrQyxTQUFTO0FBQzNDLGtDQUFrQyxXQUFXLFVBQVU7QUFDdkQseUNBQXlDLGNBQWM7QUFDdkQ7QUFDQSw2R0FBNkcsT0FBTyxVQUFVO0FBQzlILGdGQUFnRixpQkFBaUIsT0FBTztBQUN4Ryx3REFBd0QsZ0JBQWdCLFFBQVEsT0FBTztBQUN2Riw4Q0FBOEMsZ0JBQWdCLGdCQUFnQixPQUFPO0FBQ3JGO0FBQ0EsaUNBQWlDO0FBQ2pDO0FBQ0E7QUFDQSxTQUFTLFlBQVksYUFBYSxPQUFPLEVBQUUsVUFBVSxXQUFXO0FBQ2hFLG1DQUFtQyxTQUFTO0FBQzVDO0FBQ0E7QUFDQTtBQUNBLDRDQUE0QztBQUM1QztBQUNBLDhDQUE4QyxjQUFjO0FBQzVEO0FBQ0Esc0NBQXNDLG1CQUFPLENBQUMsOERBQWU7QUFDN0QsYUFBYSxtQkFBTyxDQUFDLDZCQUFRO0FBQzdCO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0Esc0JBQXNCLDBCQUEwQjtBQUNoRCxTQUFTO0FBQ1Q7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxnQ0FBZ0M7QUFDaEM7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxvQ0FBb0MsY0FBYyxFQUFFO0FBQ3BEO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLHFCQUFxQjtBQUNyQjtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxzQkFBc0IsNkJBQTZCO0FBQ25EO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxxQkFBcUI7QUFDckIsYUFBYTtBQUNiLFNBQVM7QUFDVDtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSwyQkFBMkI7QUFDM0I7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EseUJBQXlCO0FBQ3pCLHFCQUFxQjtBQUNyQixhQUFhO0FBQ2IsU0FBUztBQUNUO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLDJCQUEyQjtBQUMzQjtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSx5QkFBeUI7QUFDekIscUJBQXFCO0FBQ3JCLGFBQWE7QUFDYixTQUFTO0FBQ1Q7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsMkJBQTJCO0FBQzNCO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLHlCQUF5QjtBQUN6QixxQkFBcUI7QUFDckIsYUFBYTtBQUNiLFNBQVM7QUFDVDtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSwyQkFBMkI7QUFDM0I7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLHlCQUF5QjtBQUN6QixxQkFBcUI7QUFDckIsYUFBYTtBQUNiLFNBQVM7QUFDVDtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSwyQkFBMkI7QUFDM0I7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLHlCQUF5QjtBQUN6QixxQkFBcUI7QUFDckIsYUFBYTtBQUNiLFNBQVM7QUFDVDtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSw4QkFBOEIsNkNBQTZDO0FBQzNFO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsYUFBYTtBQUNiLFNBQVM7QUFDVDtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsU0FBUztBQUNUO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLHlCQUF5QjtBQUN6QixxQkFBcUI7QUFDckIsYUFBYTtBQUNiLFNBQVM7QUFDVDtBQUNBO0FBQ0EsQ0FBQztBQUNEOzs7Ozs7Ozs7Ozs7O0FDNVhhO0FBQ2IsOENBQThDLGNBQWM7QUFDNUQ7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSwyQ0FBMkM7QUFDM0MscURBQXFEO0FBQ3JEO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxpQ0FBaUMsb0NBQW9DO0FBQ3JFO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLHNCQUFzQjtBQUN0QjtBQUNBO0FBQ0E7QUFDQTtBQUNBLDJCQUEyQixrQkFBa0I7QUFDN0M7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLDJCQUEyQixrQkFBa0I7QUFDN0M7QUFDQTtBQUNBLDJCQUEyQixxQkFBcUI7QUFDaEQ7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLHVDQUF1QyxVQUFVO0FBQ2pEO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSwyQkFBMkIsa0JBQWtCO0FBQzdDO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsK0JBQStCLGNBQWM7QUFDN0M7QUFDQSxxQ0FBcUMsK0NBQStDO0FBQ3BGO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSx5Q0FBeUMsMEJBQTBCLEVBQUU7QUFDckUsdUJBQXVCLHVCQUF1QjtBQUM5QztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLDhCQUE4QjtBQUM5QjtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsQ0FBQztBQUNEIiwiZmlsZSI6ImxpYmFkYW1hLmpzIiwic291cmNlc0NvbnRlbnQiOlsiKGZ1bmN0aW9uIHdlYnBhY2tVbml2ZXJzYWxNb2R1bGVEZWZpbml0aW9uKHJvb3QsIGZhY3RvcnkpIHtcblx0aWYodHlwZW9mIGV4cG9ydHMgPT09ICdvYmplY3QnICYmIHR5cGVvZiBtb2R1bGUgPT09ICdvYmplY3QnKVxuXHRcdG1vZHVsZS5leHBvcnRzID0gZmFjdG9yeSgpO1xuXHRlbHNlIGlmKHR5cGVvZiBkZWZpbmUgPT09ICdmdW5jdGlvbicgJiYgZGVmaW5lLmFtZClcblx0XHRkZWZpbmUoW10sIGZhY3RvcnkpO1xuXHRlbHNlIGlmKHR5cGVvZiBleHBvcnRzID09PSAnb2JqZWN0Jylcblx0XHRleHBvcnRzW1wiQWRhbWFcIl0gPSBmYWN0b3J5KCk7XG5cdGVsc2Vcblx0XHRyb290W1wiQWRhbWFcIl0gPSBmYWN0b3J5KCk7XG59KSh0aGlzLCBmdW5jdGlvbigpIHtcbnJldHVybiAiLCIgXHQvLyBUaGUgbW9kdWxlIGNhY2hlXG4gXHR2YXIgaW5zdGFsbGVkTW9kdWxlcyA9IHt9O1xuXG4gXHQvLyBUaGUgcmVxdWlyZSBmdW5jdGlvblxuIFx0ZnVuY3Rpb24gX193ZWJwYWNrX3JlcXVpcmVfXyhtb2R1bGVJZCkge1xuXG4gXHRcdC8vIENoZWNrIGlmIG1vZHVsZSBpcyBpbiBjYWNoZVxuIFx0XHRpZihpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSkge1xuIFx0XHRcdHJldHVybiBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXS5leHBvcnRzO1xuIFx0XHR9XG4gXHRcdC8vIENyZWF0ZSBhIG5ldyBtb2R1bGUgKGFuZCBwdXQgaXQgaW50byB0aGUgY2FjaGUpXG4gXHRcdHZhciBtb2R1bGUgPSBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSA9IHtcbiBcdFx0XHRpOiBtb2R1bGVJZCxcbiBcdFx0XHRsOiBmYWxzZSxcbiBcdFx0XHRleHBvcnRzOiB7fVxuIFx0XHR9O1xuXG4gXHRcdC8vIEV4ZWN1dGUgdGhlIG1vZHVsZSBmdW5jdGlvblxuIFx0XHRtb2R1bGVzW21vZHVsZUlkXS5jYWxsKG1vZHVsZS5leHBvcnRzLCBtb2R1bGUsIG1vZHVsZS5leHBvcnRzLCBfX3dlYnBhY2tfcmVxdWlyZV9fKTtcblxuIFx0XHQvLyBGbGFnIHRoZSBtb2R1bGUgYXMgbG9hZGVkXG4gXHRcdG1vZHVsZS5sID0gdHJ1ZTtcblxuIFx0XHQvLyBSZXR1cm4gdGhlIGV4cG9ydHMgb2YgdGhlIG1vZHVsZVxuIFx0XHRyZXR1cm4gbW9kdWxlLmV4cG9ydHM7XG4gXHR9XG5cblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGVzIG9iamVjdCAoX193ZWJwYWNrX21vZHVsZXNfXylcbiBcdF9fd2VicGFja19yZXF1aXJlX18ubSA9IG1vZHVsZXM7XG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlIGNhY2hlXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLmMgPSBpbnN0YWxsZWRNb2R1bGVzO1xuXG4gXHQvLyBkZWZpbmUgZ2V0dGVyIGZ1bmN0aW9uIGZvciBoYXJtb255IGV4cG9ydHNcbiBcdF9fd2VicGFja19yZXF1aXJlX18uZCA9IGZ1bmN0aW9uKGV4cG9ydHMsIG5hbWUsIGdldHRlcikge1xuIFx0XHRpZighX193ZWJwYWNrX3JlcXVpcmVfXy5vKGV4cG9ydHMsIG5hbWUpKSB7XG4gXHRcdFx0T2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIG5hbWUsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBnZXR0ZXIgfSk7XG4gXHRcdH1cbiBcdH07XG5cbiBcdC8vIGRlZmluZSBfX2VzTW9kdWxlIG9uIGV4cG9ydHNcbiBcdF9fd2VicGFja19yZXF1aXJlX18uciA9IGZ1bmN0aW9uKGV4cG9ydHMpIHtcbiBcdFx0aWYodHlwZW9mIFN5bWJvbCAhPT0gJ3VuZGVmaW5lZCcgJiYgU3ltYm9sLnRvU3RyaW5nVGFnKSB7XG4gXHRcdFx0T2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFN5bWJvbC50b1N0cmluZ1RhZywgeyB2YWx1ZTogJ01vZHVsZScgfSk7XG4gXHRcdH1cbiBcdFx0T2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsICdfX2VzTW9kdWxlJywgeyB2YWx1ZTogdHJ1ZSB9KTtcbiBcdH07XG5cbiBcdC8vIGNyZWF0ZSBhIGZha2UgbmFtZXNwYWNlIG9iamVjdFxuIFx0Ly8gbW9kZSAmIDE6IHZhbHVlIGlzIGEgbW9kdWxlIGlkLCByZXF1aXJlIGl0XG4gXHQvLyBtb2RlICYgMjogbWVyZ2UgYWxsIHByb3BlcnRpZXMgb2YgdmFsdWUgaW50byB0aGUgbnNcbiBcdC8vIG1vZGUgJiA0OiByZXR1cm4gdmFsdWUgd2hlbiBhbHJlYWR5IG5zIG9iamVjdFxuIFx0Ly8gbW9kZSAmIDh8MTogYmVoYXZlIGxpa2UgcmVxdWlyZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy50ID0gZnVuY3Rpb24odmFsdWUsIG1vZGUpIHtcbiBcdFx0aWYobW9kZSAmIDEpIHZhbHVlID0gX193ZWJwYWNrX3JlcXVpcmVfXyh2YWx1ZSk7XG4gXHRcdGlmKG1vZGUgJiA4KSByZXR1cm4gdmFsdWU7XG4gXHRcdGlmKChtb2RlICYgNCkgJiYgdHlwZW9mIHZhbHVlID09PSAnb2JqZWN0JyAmJiB2YWx1ZSAmJiB2YWx1ZS5fX2VzTW9kdWxlKSByZXR1cm4gdmFsdWU7XG4gXHRcdHZhciBucyA9IE9iamVjdC5jcmVhdGUobnVsbCk7XG4gXHRcdF9fd2VicGFja19yZXF1aXJlX18ucihucyk7XG4gXHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShucywgJ2RlZmF1bHQnLCB7IGVudW1lcmFibGU6IHRydWUsIHZhbHVlOiB2YWx1ZSB9KTtcbiBcdFx0aWYobW9kZSAmIDIgJiYgdHlwZW9mIHZhbHVlICE9ICdzdHJpbmcnKSBmb3IodmFyIGtleSBpbiB2YWx1ZSkgX193ZWJwYWNrX3JlcXVpcmVfXy5kKG5zLCBrZXksIGZ1bmN0aW9uKGtleSkgeyByZXR1cm4gdmFsdWVba2V5XTsgfS5iaW5kKG51bGwsIGtleSkpO1xuIFx0XHRyZXR1cm4gbnM7XG4gXHR9O1xuXG4gXHQvLyBnZXREZWZhdWx0RXhwb3J0IGZ1bmN0aW9uIGZvciBjb21wYXRpYmlsaXR5IHdpdGggbm9uLWhhcm1vbnkgbW9kdWxlc1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5uID0gZnVuY3Rpb24obW9kdWxlKSB7XG4gXHRcdHZhciBnZXR0ZXIgPSBtb2R1bGUgJiYgbW9kdWxlLl9fZXNNb2R1bGUgP1xuIFx0XHRcdGZ1bmN0aW9uIGdldERlZmF1bHQoKSB7IHJldHVybiBtb2R1bGVbJ2RlZmF1bHQnXTsgfSA6XG4gXHRcdFx0ZnVuY3Rpb24gZ2V0TW9kdWxlRXhwb3J0cygpIHsgcmV0dXJuIG1vZHVsZTsgfTtcbiBcdFx0X193ZWJwYWNrX3JlcXVpcmVfXy5kKGdldHRlciwgJ2EnLCBnZXR0ZXIpO1xuIFx0XHRyZXR1cm4gZ2V0dGVyO1xuIFx0fTtcblxuIFx0Ly8gT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm8gPSBmdW5jdGlvbihvYmplY3QsIHByb3BlcnR5KSB7IHJldHVybiBPYmplY3QucHJvdG90eXBlLmhhc093blByb3BlcnR5LmNhbGwob2JqZWN0LCBwcm9wZXJ0eSk7IH07XG5cbiBcdC8vIF9fd2VicGFja19wdWJsaWNfcGF0aF9fXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnAgPSBcIlwiO1xuXG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oX193ZWJwYWNrX3JlcXVpcmVfXy5zID0gXCIuL3NyYy9pbmRleC50c1wiKTtcbiIsIi8vIGh0dHBzOi8vZ2l0aHViLmNvbS9tYXhvZ2Rlbi93ZWJzb2NrZXQtc3RyZWFtL2Jsb2IvNDhkYzNkZGY5NDNlNWFkYTY2OGMzMWNjZDk0ZTkxODZmMDJmYWZiZC93cy1mYWxsYmFjay5qc1xuXG52YXIgd3MgPSBudWxsXG5cbmlmICh0eXBlb2YgV2ViU29ja2V0ICE9PSAndW5kZWZpbmVkJykge1xuICB3cyA9IFdlYlNvY2tldFxufSBlbHNlIGlmICh0eXBlb2YgTW96V2ViU29ja2V0ICE9PSAndW5kZWZpbmVkJykge1xuICB3cyA9IE1veldlYlNvY2tldFxufSBlbHNlIGlmICh0eXBlb2YgZ2xvYmFsICE9PSAndW5kZWZpbmVkJykge1xuICB3cyA9IGdsb2JhbC5XZWJTb2NrZXQgfHwgZ2xvYmFsLk1veldlYlNvY2tldFxufSBlbHNlIGlmICh0eXBlb2Ygd2luZG93ICE9PSAndW5kZWZpbmVkJykge1xuICB3cyA9IHdpbmRvdy5XZWJTb2NrZXQgfHwgd2luZG93Lk1veldlYlNvY2tldFxufSBlbHNlIGlmICh0eXBlb2Ygc2VsZiAhPT0gJ3VuZGVmaW5lZCcpIHtcbiAgd3MgPSBzZWxmLldlYlNvY2tldCB8fCBzZWxmLk1veldlYlNvY2tldFxufVxuXG5tb2R1bGUuZXhwb3J0cyA9IHdzXG4iLCJ2YXIgZztcblxuLy8gVGhpcyB3b3JrcyBpbiBub24tc3RyaWN0IG1vZGVcbmcgPSAoZnVuY3Rpb24oKSB7XG5cdHJldHVybiB0aGlzO1xufSkoKTtcblxudHJ5IHtcblx0Ly8gVGhpcyB3b3JrcyBpZiBldmFsIGlzIGFsbG93ZWQgKHNlZSBDU1ApXG5cdGcgPSBnIHx8IG5ldyBGdW5jdGlvbihcInJldHVybiB0aGlzXCIpKCk7XG59IGNhdGNoIChlKSB7XG5cdC8vIFRoaXMgd29ya3MgaWYgdGhlIHdpbmRvdyByZWZlcmVuY2UgaXMgYXZhaWxhYmxlXG5cdGlmICh0eXBlb2Ygd2luZG93ID09PSBcIm9iamVjdFwiKSBnID0gd2luZG93O1xufVxuXG4vLyBnIGNhbiBzdGlsbCBiZSB1bmRlZmluZWQsIGJ1dCBub3RoaW5nIHRvIGRvIGFib3V0IGl0Li4uXG4vLyBXZSByZXR1cm4gdW5kZWZpbmVkLCBpbnN0ZWFkIG9mIG5vdGhpbmcgaGVyZSwgc28gaXQnc1xuLy8gZWFzaWVyIHRvIGhhbmRsZSB0aGlzIGNhc2UuIGlmKCFnbG9iYWwpIHsgLi4ufVxuXG5tb2R1bGUuZXhwb3J0cyA9IGc7XG4iLCJcInVzZSBzdHJpY3RcIjtcbnZhciBfX2F3YWl0ZXIgPSAodGhpcyAmJiB0aGlzLl9fYXdhaXRlcikgfHwgZnVuY3Rpb24gKHRoaXNBcmcsIF9hcmd1bWVudHMsIFAsIGdlbmVyYXRvcikge1xuICAgIGZ1bmN0aW9uIGFkb3B0KHZhbHVlKSB7IHJldHVybiB2YWx1ZSBpbnN0YW5jZW9mIFAgPyB2YWx1ZSA6IG5ldyBQKGZ1bmN0aW9uIChyZXNvbHZlKSB7IHJlc29sdmUodmFsdWUpOyB9KTsgfVxuICAgIHJldHVybiBuZXcgKFAgfHwgKFAgPSBQcm9taXNlKSkoZnVuY3Rpb24gKHJlc29sdmUsIHJlamVjdCkge1xuICAgICAgICBmdW5jdGlvbiBmdWxmaWxsZWQodmFsdWUpIHsgdHJ5IHsgc3RlcChnZW5lcmF0b3IubmV4dCh2YWx1ZSkpOyB9IGNhdGNoIChlKSB7IHJlamVjdChlKTsgfSB9XG4gICAgICAgIGZ1bmN0aW9uIHJlamVjdGVkKHZhbHVlKSB7IHRyeSB7IHN0ZXAoZ2VuZXJhdG9yW1widGhyb3dcIl0odmFsdWUpKTsgfSBjYXRjaCAoZSkgeyByZWplY3QoZSk7IH0gfVxuICAgICAgICBmdW5jdGlvbiBzdGVwKHJlc3VsdCkgeyByZXN1bHQuZG9uZSA/IHJlc29sdmUocmVzdWx0LnZhbHVlKSA6IGFkb3B0KHJlc3VsdC52YWx1ZSkudGhlbihmdWxmaWxsZWQsIHJlamVjdGVkKTsgfVxuICAgICAgICBzdGVwKChnZW5lcmF0b3IgPSBnZW5lcmF0b3IuYXBwbHkodGhpc0FyZywgX2FyZ3VtZW50cyB8fCBbXSkpLm5leHQoKSk7XG4gICAgfSk7XG59O1xudmFyIF9fZ2VuZXJhdG9yID0gKHRoaXMgJiYgdGhpcy5fX2dlbmVyYXRvcikgfHwgZnVuY3Rpb24gKHRoaXNBcmcsIGJvZHkpIHtcbiAgICB2YXIgXyA9IHsgbGFiZWw6IDAsIHNlbnQ6IGZ1bmN0aW9uKCkgeyBpZiAodFswXSAmIDEpIHRocm93IHRbMV07IHJldHVybiB0WzFdOyB9LCB0cnlzOiBbXSwgb3BzOiBbXSB9LCBmLCB5LCB0LCBnO1xuICAgIHJldHVybiBnID0geyBuZXh0OiB2ZXJiKDApLCBcInRocm93XCI6IHZlcmIoMSksIFwicmV0dXJuXCI6IHZlcmIoMikgfSwgdHlwZW9mIFN5bWJvbCA9PT0gXCJmdW5jdGlvblwiICYmIChnW1N5bWJvbC5pdGVyYXRvcl0gPSBmdW5jdGlvbigpIHsgcmV0dXJuIHRoaXM7IH0pLCBnO1xuICAgIGZ1bmN0aW9uIHZlcmIobikgeyByZXR1cm4gZnVuY3Rpb24gKHYpIHsgcmV0dXJuIHN0ZXAoW24sIHZdKTsgfTsgfVxuICAgIGZ1bmN0aW9uIHN0ZXAob3ApIHtcbiAgICAgICAgaWYgKGYpIHRocm93IG5ldyBUeXBlRXJyb3IoXCJHZW5lcmF0b3IgaXMgYWxyZWFkeSBleGVjdXRpbmcuXCIpO1xuICAgICAgICB3aGlsZSAoXykgdHJ5IHtcbiAgICAgICAgICAgIGlmIChmID0gMSwgeSAmJiAodCA9IG9wWzBdICYgMiA/IHlbXCJyZXR1cm5cIl0gOiBvcFswXSA/IHlbXCJ0aHJvd1wiXSB8fCAoKHQgPSB5W1wicmV0dXJuXCJdKSAmJiB0LmNhbGwoeSksIDApIDogeS5uZXh0KSAmJiAhKHQgPSB0LmNhbGwoeSwgb3BbMV0pKS5kb25lKSByZXR1cm4gdDtcbiAgICAgICAgICAgIGlmICh5ID0gMCwgdCkgb3AgPSBbb3BbMF0gJiAyLCB0LnZhbHVlXTtcbiAgICAgICAgICAgIHN3aXRjaCAob3BbMF0pIHtcbiAgICAgICAgICAgICAgICBjYXNlIDA6IGNhc2UgMTogdCA9IG9wOyBicmVhaztcbiAgICAgICAgICAgICAgICBjYXNlIDQ6IF8ubGFiZWwrKzsgcmV0dXJuIHsgdmFsdWU6IG9wWzFdLCBkb25lOiBmYWxzZSB9O1xuICAgICAgICAgICAgICAgIGNhc2UgNTogXy5sYWJlbCsrOyB5ID0gb3BbMV07IG9wID0gWzBdOyBjb250aW51ZTtcbiAgICAgICAgICAgICAgICBjYXNlIDc6IG9wID0gXy5vcHMucG9wKCk7IF8udHJ5cy5wb3AoKTsgY29udGludWU7XG4gICAgICAgICAgICAgICAgZGVmYXVsdDpcbiAgICAgICAgICAgICAgICAgICAgaWYgKCEodCA9IF8udHJ5cywgdCA9IHQubGVuZ3RoID4gMCAmJiB0W3QubGVuZ3RoIC0gMV0pICYmIChvcFswXSA9PT0gNiB8fCBvcFswXSA9PT0gMikpIHsgXyA9IDA7IGNvbnRpbnVlOyB9XG4gICAgICAgICAgICAgICAgICAgIGlmIChvcFswXSA9PT0gMyAmJiAoIXQgfHwgKG9wWzFdID4gdFswXSAmJiBvcFsxXSA8IHRbM10pKSkgeyBfLmxhYmVsID0gb3BbMV07IGJyZWFrOyB9XG4gICAgICAgICAgICAgICAgICAgIGlmIChvcFswXSA9PT0gNiAmJiBfLmxhYmVsIDwgdFsxXSkgeyBfLmxhYmVsID0gdFsxXTsgdCA9IG9wOyBicmVhazsgfVxuICAgICAgICAgICAgICAgICAgICBpZiAodCAmJiBfLmxhYmVsIDwgdFsyXSkgeyBfLmxhYmVsID0gdFsyXTsgXy5vcHMucHVzaChvcCk7IGJyZWFrOyB9XG4gICAgICAgICAgICAgICAgICAgIGlmICh0WzJdKSBfLm9wcy5wb3AoKTtcbiAgICAgICAgICAgICAgICAgICAgXy50cnlzLnBvcCgpOyBjb250aW51ZTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIG9wID0gYm9keS5jYWxsKHRoaXNBcmcsIF8pO1xuICAgICAgICB9IGNhdGNoIChlKSB7IG9wID0gWzYsIGVdOyB5ID0gMDsgfSBmaW5hbGx5IHsgZiA9IHQgPSAwOyB9XG4gICAgICAgIGlmIChvcFswXSAmIDUpIHRocm93IG9wWzFdOyByZXR1cm4geyB2YWx1ZTogb3BbMF0gPyBvcFsxXSA6IHZvaWQgMCwgZG9uZTogdHJ1ZSB9O1xuICAgIH1cbn07XG52YXIgX19pbXBvcnREZWZhdWx0ID0gKHRoaXMgJiYgdGhpcy5fX2ltcG9ydERlZmF1bHQpIHx8IGZ1bmN0aW9uIChtb2QpIHtcbiAgICByZXR1cm4gKG1vZCAmJiBtb2QuX19lc01vZHVsZSkgPyBtb2QgOiB7IFwiZGVmYXVsdFwiOiBtb2QgfTtcbn07XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJfX2VzTW9kdWxlXCIsIHsgdmFsdWU6IHRydWUgfSk7XG5leHBvcnRzLkNvbm5lY3Rpb24gPSBleHBvcnRzLk1ha2VUcmVlID0gdm9pZCAwO1xudmFyIGlzb21vcnBoaWNfd3NfMSA9IF9faW1wb3J0RGVmYXVsdChyZXF1aXJlKFwiaXNvbW9ycGhpYy13c1wiKSk7XG52YXIgdHJlZV8xID0gcmVxdWlyZShcIi4vdHJlZVwiKTtcbmZ1bmN0aW9uIE1ha2VUcmVlKCkge1xuICAgIHJldHVybiBuZXcgdHJlZV8xLlRyZWUoKTtcbn1cbmV4cG9ydHMuTWFrZVRyZWUgPSBNYWtlVHJlZTtcbnZhciBDb25uZWN0aW9uID0gLyoqIEBjbGFzcyAqLyAoZnVuY3Rpb24gKCkge1xuICAgIGZ1bmN0aW9uIENvbm5lY3Rpb24odXJsKSB7XG4gICAgICAgIHZhciBzZWxmID0gdGhpcztcbiAgICAgICAgdGhpcy5iYWNrb2ZmID0gMTtcbiAgICAgICAgdGhpcy51cmwgPSB1cmw7XG4gICAgICAgIHRoaXMuY29ubmVjdGVkID0gZmFsc2U7XG4gICAgICAgIHRoaXMuZGVhZCA9IGZhbHNlO1xuICAgICAgICB0aGlzLm1heGltdW1fYmFja29mZiA9IDI1MDA7XG4gICAgICAgIHRoaXMuc29ja2V0ID0gbnVsbDtcbiAgICAgICAgdGhpcy5vbnN0YXR1c2NoYW5nZSA9IGZ1bmN0aW9uIChzdGF0dXMpIHtcbiAgICAgICAgfTtcbiAgICAgICAgdGhpcy5vbnBpbmcgPSBmdW5jdGlvbiAoc2Vjb25kcywgbGF0ZW5jeSkge1xuICAgICAgICB9O1xuICAgICAgICB0aGlzLm9uYXV0aG5lZWRlZCA9IGZ1bmN0aW9uICh0cnlhZ2Fpbikge1xuICAgICAgICB9O1xuICAgICAgICB0aGlzLnNjaGVkdWxlZCA9IGZhbHNlO1xuICAgICAgICB0aGlzLmNhbGxiYWNrcyA9IG5ldyBNYXAoKTtcbiAgICAgICAgdGhpcy5jb25uZWN0SWQgPSAxO1xuICAgICAgICB0aGlzLm9ucmVjb25uZWN0ID0gbmV3IE1hcCgpO1xuICAgICAgICB0aGlzLnJwY2lkID0gMTtcbiAgICB9XG4gICAgLyoqIHN0b3AgdGhlIGNvbm5lY3Rpb24gKi9cbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5zdG9wID0gZnVuY3Rpb24gKCkge1xuICAgICAgICB0aGlzLmRlYWQgPSB0cnVlO1xuICAgICAgICBpZiAodGhpcy5zb2NrZXQgIT09IG51bGwpIHtcbiAgICAgICAgICAgIHRoaXMuc29ja2V0LmNsb3NlKCk7XG4gICAgICAgIH1cbiAgICB9O1xuICAgIC8qKiBwcml2YXRlOiByZXRyeSB0aGUgY29ubmVjdGlvbiAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLl9yZXRyeSA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgLy8gbnVsbCBvdXQgdGhlIHNvY2tldFxuICAgICAgICB0aGlzLnNvY2tldCA9IG51bGw7XG4gICAgICAgIC8vIGlmIHdlIGFyZSBjb25uZWN0ZWQsIHRyYW5zaXRpb24gdGhlIHN0YXR1c1xuICAgICAgICBpZiAodGhpcy5jb25uZWN0ZWQpIHtcbiAgICAgICAgICAgIHRoaXMuY29ubmVjdGVkID0gZmFsc2U7XG4gICAgICAgICAgICB0aGlzLm9uc3RhdHVzY2hhbmdlKGZhbHNlKTtcbiAgICAgICAgfVxuICAgICAgICAvLyBmYWlsIGFsbCBvdXRzdGFuZGluZyBvcGVyYXRpb25zXG4gICAgICAgIHRoaXMuY2FsbGJhY2tzLmZvckVhY2goZnVuY3Rpb24gKGNhbGxiYWNrLCBpZCkge1xuICAgICAgICAgICAgY2FsbGJhY2soeyBmYWlsdXJlOiBpZCwgcmVhc29uOiA3NyB9KTtcbiAgICAgICAgfSk7XG4gICAgICAgIHRoaXMuY2FsbGJhY2tzLmNsZWFyKCk7XG4gICAgICAgIC8vIGlmIHdlIGFyZSBkZWFkLCB0aGVuIGRvbid0IGFjdHVhbGx5IHJldHJ5XG4gICAgICAgIGlmICh0aGlzLmRlYWQpIHtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICAvLyBtYWtlIHN1cmVcbiAgICAgICAgaWYgKCF0aGlzLnNjaGVkdWxlZCkge1xuICAgICAgICAgICAgLy8gc2NoZWR1bGUgYSByZXRyeTsgZmlyc3QgY29tcHV0ZSBob3cgbG9uZyB0byB0YWtlXG4gICAgICAgICAgICAvLyBjb21wdXRlIGhvdyBsb25nIHdlIG5lZWQgdG8gd2FpdFxuICAgICAgICAgICAgdmFyIGhhbHZlQWZ0ZXJTY2hlZHVsZSA9IGZhbHNlO1xuICAgICAgICAgICAgdGhpcy5iYWNrb2ZmICs9IE1hdGgucmFuZG9tKCkgKiB0aGlzLmJhY2tvZmY7XG4gICAgICAgICAgICBpZiAodGhpcy5iYWNrb2ZmID4gdGhpcy5tYXhpbXVtX2JhY2tvZmYpIHtcbiAgICAgICAgICAgICAgICB0aGlzLmJhY2tvZmYgPSB0aGlzLm1heGltdW1fYmFja29mZjtcbiAgICAgICAgICAgICAgICBoYWx2ZUFmdGVyU2NoZWR1bGUgPSB0cnVlO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgLy8gc2NoZWR1bGUgaXRcbiAgICAgICAgICAgIHRoaXMuc2NoZWR1bGVkID0gdHJ1ZTtcbiAgICAgICAgICAgIHZhciBzZWxmID0gdGhpcztcbiAgICAgICAgICAgIHNldFRpbWVvdXQoZnVuY3Rpb24gKCkgeyBzZWxmLnN0YXJ0KCk7IH0sIHRoaXMuYmFja29mZik7XG4gICAgICAgICAgICAvLyB3ZSBqdXN0IHNjaGVkdWxlZCBpdCBmb3IgdGhlIG1heGltdW0gYmFja29mZiB0aW1lLCBsZXQncyByZWR1Y2UgdGhlIGJhY2tvZmYgc28gdGhlIG5leHQgb25lIHdpbGwgaGF2ZSBzb21lIGppdHRlclxuICAgICAgICAgICAgaWYgKGhhbHZlQWZ0ZXJTY2hlZHVsZSkge1xuICAgICAgICAgICAgICAgIHRoaXMuYmFja29mZiAvPSAyLjA7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9O1xuICAgIC8qKiBzdGFydCB0aGUgY29ubmVjdGlvbiAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLnN0YXJ0ID0gZnVuY3Rpb24gKCkge1xuICAgICAgICAvLyByZXNldCB0aGUgc3RhdGVcbiAgICAgICAgdmFyIHNlbGYgPSB0aGlzO1xuICAgICAgICB0aGlzLnNjaGVkdWxlZCA9IGZhbHNlO1xuICAgICAgICB0aGlzLmRlYWQgPSBmYWxzZTtcbiAgICAgICAgLy8gY3JlYXRlIHRoZSBzb2NrZXQgYW5kIGJpbmQgZXZlbnQgaGFuZGxlcnNcbiAgICAgICAgdGhpcy5zb2NrZXQgPSBuZXcgaXNvbW9ycGhpY193c18xLmRlZmF1bHQodGhpcy51cmwpO1xuICAgICAgICB0aGlzLnNvY2tldC5vbm1lc3NhZ2UgPSBmdW5jdGlvbiAoZXZlbnQpIHtcbiAgICAgICAgICAgIHZhciByZXN1bHQgPSBKU09OLnBhcnNlKGV2ZW50LmRhdGEpO1xuICAgICAgICAgICAgLy8gYSBtZXNzYWdlIGFycml2ZWQsIGlzIGl0IGEgY29ubmVjdGlvbiBzaWduYWxcbiAgICAgICAgICAgIGlmICgncGluZycgaW4gcmVzdWx0KSB7XG4gICAgICAgICAgICAgICAgc2VsZi5vbnBpbmcocmVzdWx0LnBpbmcsIHJlc3VsdC5sYXRlbmN5KTtcbiAgICAgICAgICAgICAgICByZXN1bHQucG9uZyA9IG5ldyBEYXRlKCkuZ2V0VGltZSgpIC8gMTAwMC4wO1xuICAgICAgICAgICAgICAgIHNlbGYuc29ja2V0LnNlbmQoSlNPTi5zdHJpbmdpZnkocmVzdWx0KSk7XG4gICAgICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgaWYgKCdzaWduYWwnIGluIHJlc3VsdCkge1xuICAgICAgICAgICAgICAgIC8vIGhleSwgYXJlIHdlIGNvbm5lY3RlZD9cbiAgICAgICAgICAgICAgICBpZiAocmVzdWx0LnN0YXR1cyAhPSAnY29ubmVjdGVkJykge1xuICAgICAgICAgICAgICAgICAgICAvLyBub3BlLCBPSywgbGV0J3MgbWFrZSB0aGlzIGEgZGVhZCBzb2NrZXRcbiAgICAgICAgICAgICAgICAgICAgc2VsZi5kZWFkID0gdHJ1ZTtcbiAgICAgICAgICAgICAgICAgICAgc2VsZi5zb2NrZXQuY2xvc2UoKTtcbiAgICAgICAgICAgICAgICAgICAgc2VsZi5zb2NrZXQgPSBudWxsO1xuICAgICAgICAgICAgICAgICAgICAvLyBpbmZvcm0gdGhlIGNsaWVudCB0byB0cnkgYWdhaW5cbiAgICAgICAgICAgICAgICAgICAgc2VsZi5vbmF1dGhuZWVkZWQoZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5zdGFydCgpO1xuICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICAgICAgcmV0dXJuO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAvLyB0ZWxsIHRoZSBjbGllbnQgdGhhdCB3ZSBhcmUgZ29vZCFcbiAgICAgICAgICAgICAgICBzZWxmLmJhY2tvZmYgPSAxO1xuICAgICAgICAgICAgICAgIHNlbGYuY29ubmVjdGVkID0gdHJ1ZTtcbiAgICAgICAgICAgICAgICBzZWxmLm9uc3RhdHVzY2hhbmdlKHRydWUpO1xuICAgICAgICAgICAgICAgIHNlbGYuX3JlY29ubmVjdCgpO1xuICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIC8vIHRoZSByZXN1bHQgd2FzIGEgZmFpbHVyZS4uXG4gICAgICAgICAgICBpZiAoJ2ZhaWx1cmUnIGluIHJlc3VsdCkge1xuICAgICAgICAgICAgICAgIC8vIGZpbmQgdGhlIGNhbGxiYWNrLCB0aGVuIGludm9rZSBpdCAoYW5kIGNsZWFuIHVwKVxuICAgICAgICAgICAgICAgIGlmIChzZWxmLmNhbGxiYWNrcy5oYXMocmVzdWx0LmZhaWx1cmUpKSB7XG4gICAgICAgICAgICAgICAgICAgIHZhciBjYiA9IHNlbGYuY2FsbGJhY2tzLmdldChyZXN1bHQuZmFpbHVyZSk7XG4gICAgICAgICAgICAgICAgICAgIGlmIChjYikge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5jYWxsYmFja3MuZGVsZXRlKHJlc3VsdC5mYWlsdXJlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIGNiKHJlc3VsdCk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIGlmICgnZGVsaXZlcicgaW4gcmVzdWx0KSB7XG4gICAgICAgICAgICAgICAgLy8gb3RoZXJ3aXNlLCB3ZSBoYXZlIGEgc3VjY2Vzcywgc28gbGV0J3MgZmluZCB0aGUgY2FsbGJhY2ssIGFuZCBpZiBuZWVkIGJlIGNsZWFuIHVwXG4gICAgICAgICAgICAgICAgaWYgKHNlbGYuY2FsbGJhY2tzLmhhcyhyZXN1bHQuZGVsaXZlcikpIHtcbiAgICAgICAgICAgICAgICAgICAgdmFyIGNiID0gc2VsZi5jYWxsYmFja3MuZ2V0KHJlc3VsdC5kZWxpdmVyKTtcbiAgICAgICAgICAgICAgICAgICAgaWYgKGNiKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAocmVzdWx0LmRvbmUpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBzZWxmLmNhbGxiYWNrcy5kZWxldGUocmVzdWx0LmRlbGl2ZXIpO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgY2IocmVzdWx0LnJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgfTtcbiAgICAgICAgdGhpcy5zb2NrZXQub25jbG9zZSA9IGZ1bmN0aW9uIChldmVudCkge1xuICAgICAgICAgICAgLy8gbGV0J3MgcmV0cnkuLi4gb3Igc2hvdWxkIHdlIG5vdFxuICAgICAgICAgICAgc2VsZi5fcmV0cnkoKTtcbiAgICAgICAgfTtcbiAgICAgICAgdGhpcy5zb2NrZXQub25lcnJvciA9IGZ1bmN0aW9uIChldmVudCkge1xuICAgICAgICAgICAgLy8gc29tZXRoaW5nIGJhZCBoYXBwZW5lZCwgbGV0J3MgcmV0cnlcbiAgICAgICAgICAgIHNlbGYuX3JldHJ5KCk7XG4gICAgICAgIH07XG4gICAgfTtcbiAgICAvKiogcHJpdmF0ZTogc2VuZCBhIHJhdyBtZXNzYWdlICovXG4gICAgQ29ubmVjdGlvbi5wcm90b3R5cGUuX3NlbmQgPSBmdW5jdGlvbiAocmVxdWVzdCwgY2FsbGJhY2spIHtcbiAgICAgICAgaWYgKCF0aGlzLmNvbm5lY3RlZCkge1xuICAgICAgICAgICAgY2FsbGJhY2soeyBmYWlsdXJlOiA2MDAsIHJlYXNvbjogOTk5OSB9KTtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICB2YXIgaWQgPSB0aGlzLnJwY2lkO1xuICAgICAgICB0aGlzLnJwY2lkKys7XG4gICAgICAgIHJlcXVlc3RbJ2lkJ10gPSBpZDtcbiAgICAgICAgdGhpcy5jYWxsYmFja3Muc2V0KGlkLCBjYWxsYmFjayk7XG4gICAgICAgIHRoaXMuc29ja2V0LnNlbmQoSlNPTi5zdHJpbmdpZnkocmVxdWVzdCkpO1xuICAgIH07XG4gICAgLyoqIGFwaTogd2FpdCBmb3IgYSBjb25uZWN0aW9uICovXG4gICAgQ29ubmVjdGlvbi5wcm90b3R5cGUud2FpdF9jb25uZWN0ZWQgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciBzZWxmLCBwcmlvcjtcbiAgICAgICAgICAgIHJldHVybiBfX2dlbmVyYXRvcih0aGlzLCBmdW5jdGlvbiAoX2EpIHtcbiAgICAgICAgICAgICAgICBpZiAodGhpcy5jb25uZWN0ZWQpIHtcbiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi9dO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBzZWxmID0gdGhpcztcbiAgICAgICAgICAgICAgICBwcmlvciA9IHRoaXMub25zdGF0dXNjaGFuZ2U7XG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChnb29kKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBzZWxmLm9uc3RhdHVzY2hhbmdlID0gZnVuY3Rpb24gKHN0YXR1cykge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHByaW9yKHN0YXR1cyk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHN0YXR1cykge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBnb29kKCk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYub25zdGF0dXNjaGFuZ2UgPSBwcmlvcjtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICB9O1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICAvKiogYXBpOiBnZW5lcmF0ZSBhIG5ldyBnYW1lICovXG4gICAgQ29ubmVjdGlvbi5wcm90b3R5cGUuZ2VuZXJhdGUgPSBmdW5jdGlvbiAoZ3MpIHtcbiAgICAgICAgcmV0dXJuIF9fYXdhaXRlcih0aGlzLCB2b2lkIDAsIHZvaWQgMCwgZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgdmFyIHJlcXVlc3QsIHNlbGY7XG4gICAgICAgICAgICByZXR1cm4gX19nZW5lcmF0b3IodGhpcywgZnVuY3Rpb24gKF9hKSB7XG4gICAgICAgICAgICAgICAgcmVxdWVzdCA9IHsgbWV0aG9kOiBcImdlbmVyYXRlXCIsIGdhbWVzcGFjZTogZ3MgfTtcbiAgICAgICAgICAgICAgICBzZWxmID0gdGhpcztcbiAgICAgICAgICAgICAgICByZXR1cm4gWzIgLypyZXR1cm4qLywgbmV3IFByb21pc2UoZnVuY3Rpb24gKGdvb2QsIGJhZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5fc2VuZChyZXF1ZXN0LCBmdW5jdGlvbiAocmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoJ2ZhaWx1cmUnIGluIHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGJhZChyZXNwb25zZS5yZWFzb24pO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZ29vZChyZXNwb25zZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgICAgIH0pXTtcbiAgICAgICAgICAgIH0pO1xuICAgICAgICB9KTtcbiAgICB9O1xuICAgIC8qKiBhcGk6IGdldCB0aGUgc2NoZW1hIGZvciB0aGUgZ2FtZXBzYWNlICovXG4gICAgQ29ubmVjdGlvbi5wcm90b3R5cGUucmVmbGVjdCA9IGZ1bmN0aW9uIChncykge1xuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICB2YXIgcmVxdWVzdCwgc2VsZjtcbiAgICAgICAgICAgIHJldHVybiBfX2dlbmVyYXRvcih0aGlzLCBmdW5jdGlvbiAoX2EpIHtcbiAgICAgICAgICAgICAgICByZXF1ZXN0ID0geyBtZXRob2Q6IFwicmVmbGVjdFwiLCBnYW1lc3BhY2U6IGdzIH07XG4gICAgICAgICAgICAgICAgc2VsZiA9IHRoaXM7XG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChnb29kLCBiYWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuX3NlbmQocmVxdWVzdCwgZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBiYWQocmVzcG9uc2UucmVhc29uKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICAvKiogYXBpOiBnZW5lcmF0ZSBhIG5ldyBnYW1lICovXG4gICAgQ29ubmVjdGlvbi5wcm90b3R5cGUuY3JlYXRlID0gZnVuY3Rpb24gKGdzLCBpZCwgYXJnKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciByZXF1ZXN0LCBzZWxmO1xuICAgICAgICAgICAgcmV0dXJuIF9fZ2VuZXJhdG9yKHRoaXMsIGZ1bmN0aW9uIChfYSkge1xuICAgICAgICAgICAgICAgIHJlcXVlc3QgPSB7IG1ldGhvZDogXCJjcmVhdGVcIiwgZ2FtZXNwYWNlOiBncywgZ2FtZTogaWQsIGFyZzogYXJnIH07XG4gICAgICAgICAgICAgICAgc2VsZiA9IHRoaXM7XG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChnb29kLCBiYWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuX3NlbmQocmVxdWVzdCwgZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBiYWQocmVzcG9uc2UucmVhc29uKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICAvKiogYXBpOiBjb25uZWN0IHRvIGEgZ2FtZSAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLmNvbm5lY3QgPSBmdW5jdGlvbiAoZ3MsIGlkLCBoYW5kbGVyKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciByZXF1ZXN0LCBzZWxmLCBmaXJzdDtcbiAgICAgICAgICAgIHJldHVybiBfX2dlbmVyYXRvcih0aGlzLCBmdW5jdGlvbiAoX2EpIHtcbiAgICAgICAgICAgICAgICByZXF1ZXN0ID0geyBtZXRob2Q6IFwiY29ubmVjdFwiLCBnYW1lc3BhY2U6IGdzLCBnYW1lOiBpZCB9O1xuICAgICAgICAgICAgICAgIHNlbGYgPSB0aGlzO1xuICAgICAgICAgICAgICAgIGZpcnN0ID0gdHJ1ZTtcbiAgICAgICAgICAgICAgICByZXR1cm4gWzIgLypyZXR1cm4qLywgbmV3IFByb21pc2UoZnVuY3Rpb24gKGdvb2QsIGJhZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5fc2VuZChyZXF1ZXN0LCBmdW5jdGlvbiAocmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoZmlyc3QpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZmlyc3QgPSBmYWxzZTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYmFkKHJlc3BvbnNlLnJlYXNvbik7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBoYW5kbGVyKHJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QodHJ1ZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGhhbmRsZXIocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICAvKiogYXBpOiBzZW5kIGEgbWVzc2FnZSAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLnNlbmQgPSBmdW5jdGlvbiAoZ3MsIGlkLCBjaGFubmVsLCBtc2cpIHtcbiAgICAgICAgcmV0dXJuIF9fYXdhaXRlcih0aGlzLCB2b2lkIDAsIHZvaWQgMCwgZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgdmFyIHJlcXVlc3QsIHNlbGY7XG4gICAgICAgICAgICByZXR1cm4gX19nZW5lcmF0b3IodGhpcywgZnVuY3Rpb24gKF9hKSB7XG4gICAgICAgICAgICAgICAgcmVxdWVzdCA9IHsgbWV0aG9kOiBcInNlbmRcIiwgZ2FtZXNwYWNlOiBncywgZ2FtZTogaWQsIGNoYW5uZWw6IGNoYW5uZWwsIG1lc3NhZ2U6IG1zZyB9O1xuICAgICAgICAgICAgICAgIHNlbGYgPSB0aGlzO1xuICAgICAgICAgICAgICAgIC8vIFRPRE86IHF1ZXVlIHRoaXMgdXA/IHdpdGggcmV0cnk/XG4gICAgICAgICAgICAgICAgLy8gVE9ETzogZ2VuZXJhdGUgYSBtYXJrZXJcbiAgICAgICAgICAgICAgICByZXR1cm4gWzIgLypyZXR1cm4qLywgbmV3IFByb21pc2UoZnVuY3Rpb24gKGdvb2QsIGJhZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5fc2VuZChyZXF1ZXN0LCBmdW5jdGlvbiAocmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoJ2ZhaWx1cmUnIGluIHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGJhZChyZXNwb25zZS5yZWFzb24pO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZ29vZChyZXNwb25zZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgICAgIH0pXTtcbiAgICAgICAgICAgIH0pO1xuICAgICAgICB9KTtcbiAgICB9O1xuICAgIC8qKiBhcGk6IGNvbm5lY3QgdHJlZSAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLmNvbm5lY3RUcmVlID0gZnVuY3Rpb24gKGdzLCBpZCwgdHJlZSkge1xuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICB2YXIga2V5SWQsIHNtO1xuICAgICAgICAgICAgcmV0dXJuIF9fZ2VuZXJhdG9yKHRoaXMsIGZ1bmN0aW9uIChfYSkge1xuICAgICAgICAgICAgICAgIGtleUlkID0gdGhpcy5jb25uZWN0SWQ7XG4gICAgICAgICAgICAgICAgdGhpcy5jb25uZWN0SWQrKztcbiAgICAgICAgICAgICAgICBzbSA9IHtcbiAgICAgICAgICAgICAgICAgICAgcmVxdWVzdDogeyBtZXRob2Q6IFwiY29ubmVjdFwiLCBnYW1lc3BhY2U6IGdzLCBnYW1lOiBpZCB9LFxuICAgICAgICAgICAgICAgICAgICBmaXJzdDogdHJ1ZSxcbiAgICAgICAgICAgICAgICAgICAgaGFuZGxlcjogZnVuY3Rpb24gKHIpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHRyZWUubWVyZ2VVcGRhdGUocik7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9O1xuICAgICAgICAgICAgICAgIHRoaXMub25yZWNvbm5lY3Quc2V0KGtleUlkLCBzbSk7XG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIHRoaXMuX2V4ZWN1dGUoc20pXTtcbiAgICAgICAgICAgIH0pO1xuICAgICAgICB9KTtcbiAgICB9O1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLl9yZWNvbm5lY3QgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHZhciBzZWxmID0gdGhpcztcbiAgICAgICAgdGhpcy5vbnJlY29ubmVjdC5mb3JFYWNoKGZ1bmN0aW9uIChzbSwgaWQpIHtcbiAgICAgICAgICAgIHNlbGYuX2V4ZWN1dGUoc20pO1xuICAgICAgICB9KTtcbiAgICB9O1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLl9leGVjdXRlID0gZnVuY3Rpb24gKHNtKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciBzZWxmO1xuICAgICAgICAgICAgcmV0dXJuIF9fZ2VuZXJhdG9yKHRoaXMsIGZ1bmN0aW9uIChfYSkge1xuICAgICAgICAgICAgICAgIHNlbGYgPSB0aGlzO1xuICAgICAgICAgICAgICAgIHJldHVybiBbMiAvKnJldHVybiovLCBuZXcgUHJvbWlzZShmdW5jdGlvbiAoZ29vZCwgYmFkKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBzZWxmLl9zZW5kKHNtLnJlcXVlc3QsIGZ1bmN0aW9uIChyZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmIChzbS5maXJzdCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBzbS5maXJzdCA9IGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoJ2ZhaWx1cmUnIGluIHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBiYWQocmVzcG9uc2UucmVhc29uKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHNtLmhhbmRsZXIocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZ29vZCh0cnVlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc20uaGFuZGxlcihyZXNwb25zZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgICAgIH0pXTtcbiAgICAgICAgICAgIH0pO1xuICAgICAgICB9KTtcbiAgICB9O1xuICAgIHJldHVybiBDb25uZWN0aW9uO1xufSgpKTtcbmV4cG9ydHMuQ29ubmVjdGlvbiA9IENvbm5lY3Rpb247XG4iLCJcInVzZSBzdHJpY3RcIjtcbk9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBcIl9fZXNNb2R1bGVcIiwgeyB2YWx1ZTogdHJ1ZSB9KTtcbmV4cG9ydHMuVHJlZSA9IHZvaWQgMDtcbnZhciBUcmVlID0gLyoqIEBjbGFzcyAqLyAoZnVuY3Rpb24gKCkge1xuICAgIGZ1bmN0aW9uIFRyZWUoKSB7XG4gICAgICAgIHRoaXMudHJlZSA9IHt9O1xuICAgICAgICB0aGlzLmRpc3BhdGNoID0ge307XG4gICAgICAgIHRoaXMuZGlzcGF0Y2hfY291bnQgPSAwO1xuICAgICAgICB0aGlzLnF1ZXVlID0gW107XG4gICAgICAgIHRoaXMub25mZXRjaCA9IGZ1bmN0aW9uIChjaGFubmVsKSB7IH07XG4gICAgICAgIHRoaXMub25kZWNpZGUgPSBmdW5jdGlvbiAoY2hhbm5lbCwgb3B0aW9ucykgeyB9O1xuICAgIH1cbiAgICBUcmVlLnByb3RvdHlwZS5fX3JlY0FwcGVuZENoYW5nZSA9IGZ1bmN0aW9uIChkaXNwYXRjaCwgY2FsbGJhY2ssIGluc2VydF9vcmRlcikge1xuICAgICAgICBpZiAodHlwZW9mIChjYWxsYmFjaykgPT0gJ29iamVjdCcpIHtcbiAgICAgICAgICAgIGZvciAodmFyIGtleSBpbiBjYWxsYmFjaykge1xuICAgICAgICAgICAgICAgIGlmICghKGtleSBpbiBkaXNwYXRjaCkpIHtcbiAgICAgICAgICAgICAgICAgICAgZGlzcGF0Y2hba2V5XSA9IHt9O1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB0aGlzLl9fcmVjQXBwZW5kQ2hhbmdlKGRpc3BhdGNoW2tleV0sIGNhbGxiYWNrW2tleV0sIGluc2VydF9vcmRlcik7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgZWxzZSBpZiAodHlwZW9mIChjYWxsYmFjaykgPT0gJ2Z1bmN0aW9uJykge1xuICAgICAgICAgICAgaWYgKCEoJ0BlJyBpbiBkaXNwYXRjaCkpIHtcbiAgICAgICAgICAgICAgICBkaXNwYXRjaFsnQGUnXSA9IFtdO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZGlzcGF0Y2hbJ0BlJ10ucHVzaCh7IGNiOiBjYWxsYmFjaywgb3JkZXI6IGluc2VydF9vcmRlciB9KTtcbiAgICAgICAgfVxuICAgIH07XG4gICAgVHJlZS5wcm90b3R5cGUub25UcmVlQ2hhbmdlID0gZnVuY3Rpb24gKGNhbGxiYWNrKSB7XG4gICAgICAgIHRoaXMuX19yZWNBcHBlbmRDaGFuZ2UodGhpcy5kaXNwYXRjaCwgY2FsbGJhY2ssIHRoaXMuZGlzcGF0Y2hfY291bnQpO1xuICAgICAgICB0aGlzLmRpc3BhdGNoX2NvdW50Kys7XG4gICAgfTtcbiAgICBUcmVlLnByb3RvdHlwZS5tZXJnZVVwZGF0ZSA9IGZ1bmN0aW9uIChkaWZmKSB7XG4gICAgICAgIGlmICgnZGF0YScgaW4gZGlmZikge1xuICAgICAgICAgICAgdGhpcy5fX3JlY01lcmdlQW5kRGlzcGF0Y2godGhpcy50cmVlLCB0aGlzLmRpc3BhdGNoLCBkaWZmLmRhdGEpO1xuICAgICAgICB9XG4gICAgICAgIC8vIFRPRE86IGRpc3BhdGNoIGRlY2lzaW9uc1xuICAgICAgICAvLyBUT0RPOiB1cGRhdGUgYmxvY2tlcnNcbiAgICAgICAgdGhpcy5fX2RyYWluKCk7XG4gICAgfTtcbiAgICBUcmVlLnByb3RvdHlwZS5fX3JlY01lcmdlQW5kRGlzcGF0Y2hBcnJheSA9IGZ1bmN0aW9uIChwcmlvciwgZGlzcGF0Y2gsIHRyZWUsIGRpZmYpIHtcbiAgICAgICAgdmFyIG9yZGVyaW5nID0gbnVsbDtcbiAgICAgICAgdmFyIHJlc2l6ZSA9IG51bGw7XG4gICAgICAgIGZvciAodmFyIGtleSBpbiBkaWZmKSB7XG4gICAgICAgICAgICBpZiAoa2V5ID09IFwiQG9cIikge1xuICAgICAgICAgICAgICAgIG9yZGVyaW5nID0gZGlmZltrZXldO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSBpZiAoa2V5ID09IFwiQHNcIikge1xuICAgICAgICAgICAgICAgIHJlc2l6ZSA9IGRpZmZba2V5XTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgIGlmIChkaWZmW2tleV0gPT0gbnVsbCkge1xuICAgICAgICAgICAgICAgICAgICAvLyBGSVJFX0RFTEVURV9FTEVNRU5UXG4gICAgICAgICAgICAgICAgICAgIGRlbGV0ZSB0cmVlW2tleV07XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICBpZiAoISh0cmVlICE9IG51bGwgJiYga2V5IGluIHRyZWUpKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICB0cmVlW2tleV0gPSB7fTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAvLyBUT0RPOiBkb2VzIGl0IG1ha2Ugc2Vuc2UgdG8gdHJhY2sgaW5kaXZpZHVhbCBrZXlzP1xuICAgICAgICAgICAgICAgICAgICB0aGlzLl9fcmVjTWVyZ2VBbmREaXNwYXRjaCh0cmVlW2tleV0sIChkaXNwYXRjaCAhPSBudWxsICYmIGtleSBpbiBkaXNwYXRjaCkgPyBkaXNwYXRjaFtrZXldIDogbnVsbCwgZGlmZltrZXldKTtcbiAgICAgICAgICAgICAgICAgICAgLy8gdGhpcyB3aWxsIGZpcmUgYW4gdXBkYXRlIGZvciB0aGUga2V5XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIHZhciBjaGFuZ2UgPSB7IGJlZm9yZTogcHJpb3IsIGFmdGVyOiBwcmlvciwgcGFyZW50OiB0cmVlIH07XG4gICAgICAgIGlmIChyZXNpemUgIT09IG51bGwpIHtcbiAgICAgICAgICAgIC8vIFNlZSBETGlzdCwgYnV0IHRoZSBpZGVhIGlzIHRoYXQgSSBuZWVkIHRvIHRyaW0gdGhlIGxpc3QgYmVjYXVzZSB0aGUgYWJvdmUgdG9vbCBjYXJlIG9mIG51bGxzXG4gICAgICAgICAgICAvLyB0aGlzIGlzIGZvciBsaXN0IG9mIHZhbHVlcyB3aGVyZSB3ZSBzeW5jaHJvbml6ZSBhIGxpc3Qgb2YgY29uc3RhbnRzXG4gICAgICAgICAgICBjaGFuZ2UuYmVmb3JlID0gW107XG4gICAgICAgICAgICBmb3IgKHZhciBrID0gMDsgayA8IHByaW9yLmxlbmd0aDsgaysrKSB7XG4gICAgICAgICAgICAgICAgY2hhbmdlLmJlZm9yZS5wdXNoKHByaW9yW2tdKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHByaW9yLmxlbmd0aCA9IHJlc2l6ZTtcbiAgICAgICAgICAgIC8vIFRPRE86IHZhbGlkYXRlIHRoaXNcbiAgICAgICAgfVxuICAgICAgICBpZiAob3JkZXJpbmcgIT09IG51bGwpIHtcbiAgICAgICAgICAgIHZhciBhZnRlciA9IFtdO1xuICAgICAgICAgICAgY2hhbmdlLmJlZm9yZSA9IFtdO1xuICAgICAgICAgICAgZm9yICh2YXIgayA9IDA7IGsgPCBwcmlvci5sZW5ndGg7IGsrKykge1xuICAgICAgICAgICAgICAgIGNoYW5nZS5iZWZvcmUucHVzaChwcmlvcltrXSk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBmb3IgKHZhciBrID0gMDsgayA8IG9yZGVyaW5nLmxlbmd0aDsgaysrKSB7XG4gICAgICAgICAgICAgICAgdmFyIGluc3RyID0gb3JkZXJpbmdba107XG4gICAgICAgICAgICAgICAgdmFyIHR5cGVfaW5zdHIgPSB0eXBlb2YgKGluc3RyKTtcbiAgICAgICAgICAgICAgICBpZiAodHlwZV9pbnN0ciA9PSBcInN0cmluZ1wiIHx8IHR5cGVfaW5zdHIgPT0gXCJudW1iZXJcIikge1xuICAgICAgICAgICAgICAgICAgICBhZnRlci5wdXNoKHRyZWVbaW5zdHJdKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIHZhciBzdGFydCA9IGluc3RyWzBdO1xuICAgICAgICAgICAgICAgICAgICB2YXIgZW5kID0gaW5zdHJbMV07XG4gICAgICAgICAgICAgICAgICAgIGZvciAodmFyIGogPSBzdGFydDsgaiA8PSBlbmQ7IGorKykge1xuICAgICAgICAgICAgICAgICAgICAgICAgYWZ0ZXIucHVzaChwcmlvcltqXSk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBwcmlvci5sZW5ndGggPSBhZnRlci5sZW5ndGg7XG4gICAgICAgICAgICBmb3IgKHZhciBrID0gMDsgayA8IGFmdGVyLmxlbmd0aDsgaysrKSB7XG4gICAgICAgICAgICAgICAgcHJpb3Jba10gPSBhZnRlcltrXTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgICAgICB0aGlzLl9fZmlyZShkaXNwYXRjaCwgY2hhbmdlKTtcbiAgICAgICAgLy8gVE9ETzogZmlyZSBldmVudHMgZm9yIHRoZSB1cGRhdGVcbiAgICB9O1xuICAgIFRyZWUucHJvdG90eXBlLl9fZmlyZSA9IGZ1bmN0aW9uIChkaXNwYXRjaCwgY2hhbmdlKSB7XG4gICAgICAgIGlmIChkaXNwYXRjaCkge1xuICAgICAgICAgICAgaWYgKCdAZScgaW4gZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgICAgICB2YXIgZCA9IGRpc3BhdGNoWydAZSddO1xuICAgICAgICAgICAgICAgIGZvciAodmFyIGsgPSAwOyBrIDwgZC5sZW5ndGg7IGsrKykge1xuICAgICAgICAgICAgICAgICAgICB2YXIgZXZ0ID0gZFtrXTtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5xdWV1ZS5wdXNoKHsgY2I6IGV2dC5jYiwgb3JkZXI6IGV2dC5vcmRlciwgY2hhbmdlOiBjaGFuZ2UgfSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgfTtcbiAgICBUcmVlLnByb3RvdHlwZS5fX2RyYWluID0gZnVuY3Rpb24gKCkge1xuICAgICAgICB0aGlzLnF1ZXVlLnNvcnQoZnVuY3Rpb24gKGEsIGIpIHsgcmV0dXJuIGEub3JkZXIgLSBiLm9yZGVyOyB9KTtcbiAgICAgICAgZm9yICh2YXIgayA9IDA7IGsgPCB0aGlzLnF1ZXVlLmxlbmd0aDsgaysrKSB7XG4gICAgICAgICAgICB2YXIgaXRlbSA9IHRoaXMucXVldWVba107XG4gICAgICAgICAgICBpdGVtLmNiKGl0ZW0uY2hhbmdlKTtcbiAgICAgICAgfVxuICAgICAgICB0aGlzLnF1ZXVlID0gW107XG4gICAgfTtcbiAgICBUcmVlLnByb3RvdHlwZS5fX3JlY01lcmdlQW5kRGlzcGF0Y2ggPSBmdW5jdGlvbiAodHJlZSwgZGlzcGF0Y2gsIGRpZmYpIHtcbiAgICAgICAgLy8gdGhlIGRpZmYgaXMgYW4gb2JqZWN0LCBzbyBsZXQncyB3YWxrIGl0cyBrZXlzXG4gICAgICAgIGZvciAodmFyIGtleSBpbiBkaWZmKSB7XG4gICAgICAgICAgICB2YXIgY2hpbGQgPSBkaWZmW2tleV07XG4gICAgICAgICAgICBpZiAoY2hpbGQgPT09IG51bGwpIHtcbiAgICAgICAgICAgICAgICBpZiAoQXJyYXkuaXNBcnJheSh0cmVlW2tleV0pKSB7XG4gICAgICAgICAgICAgICAgICAgIGRlbGV0ZSB0cmVlW1wiI1wiICsga2V5XTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgLy8gZmlndXJlIG91dCB3aGF0IGRpc3BhdGNoIG1lYW5zIGhlcmVcbiAgICAgICAgICAgICAgICBkZWxldGUgdHJlZVtrZXldO1xuICAgICAgICAgICAgICAgIGNvbnRpbnVlO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgLy8gaWYgdGhlIGNoaWxkIGlzIGFuIG9iamVjdCwgdGhlbi4uXG4gICAgICAgICAgICBpZiAodHlwZW9mIChjaGlsZCkgPT0gJ29iamVjdCcpIHtcbiAgICAgICAgICAgICAgICAvLyB0aGUgY2hpbGQgaXMgZWl0aGVyIGFuIEFSUkFZIG9yIGEgT0JKRUNUXG4gICAgICAgICAgICAgICAgdmFyIGNoaWxkSXNBcnJheSA9ICdAbycgaW4gY2hpbGQgfHwgJ0BzJyBpbiBjaGlsZDtcbiAgICAgICAgICAgICAgICAvLyB0aGUgcHJpb3IgdmVyc2lvbiBkb2Vzbid0IGV4aXN0LCBzbyB3ZSBjcmVhdGUgdGhlIGVtcHR5IG5vZGUgc28gdGhhdCBpdCBkb2VzIGV4aXN0XG4gICAgICAgICAgICAgICAgaWYgKCEoa2V5IGluIHRyZWUpKSB7XG4gICAgICAgICAgICAgICAgICAgIGlmIChjaGlsZElzQXJyYXkpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHRyZWVba2V5XSA9IFtdO1xuICAgICAgICAgICAgICAgICAgICAgICAgdHJlZVtcIiNcIiArIGtleV0gPSB7fTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHRyZWVba2V5XSA9IHt9O1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIC8vIG5vdywgd2UgY2hlY2sgdG8gc2VlIGlmIHRoZSBwcmlvciBzdGF0ZSBpbmZsdWVuY2VzIHdoZXRoZXIgb3Igbm90IHRoZSBkaWZmIGlzIGFuIGFycmF5XG4gICAgICAgICAgICAgICAgY2hpbGRJc0FycmF5ID0gQXJyYXkuaXNBcnJheSh0cmVlW2tleV0pIHx8IGNoaWxkSXNBcnJheTtcbiAgICAgICAgICAgICAgICBpZiAoY2hpbGRJc0FycmF5KSB7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuX19yZWNNZXJnZUFuZERpc3BhdGNoQXJyYXkodHJlZVtrZXldLCAoZGlzcGF0Y2ggIT0gbnVsbCAmJiBrZXkgaW4gZGlzcGF0Y2gpID8gZGlzcGF0Y2hba2V5XSA6IG51bGwsIHRyZWVbXCIjXCIgKyBrZXldLCBjaGlsZCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAvLyB3ZSBoYXZlIGFuIG9iamVjdCwgbGV0J3MgbWVyZ2UgcmVjdXJzaXZlbHkuLi4gWUFZXG4gICAgICAgICAgICAgICAgICAgIHRoaXMuX19yZWNNZXJnZUFuZERpc3BhdGNoKHRyZWVba2V5XSwgKGRpc3BhdGNoICE9IG51bGwgJiYga2V5IGluIGRpc3BhdGNoKSA/IGRpc3BhdGNoW2tleV0gOiBudWxsLCBjaGlsZCk7XG4gICAgICAgICAgICAgICAgICAgIC8vIE5PVEU6IHRoaXMgd2lsbCBmaXJlIGV2ZW50c1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgIHZhciBjaGFuZ2UgPSB7IGJlZm9yZTogdHJlZVtrZXldLCBhZnRlcjogY2hpbGQsIHBhcmVudDogdHJlZSB9O1xuICAgICAgICAgICAgICAgIHRyZWVba2V5XSA9IGNoaWxkO1xuICAgICAgICAgICAgICAgIGlmIChkaXNwYXRjaCAhPSBudWxsICYmIGtleSBpbiBkaXNwYXRjaCkge1xuICAgICAgICAgICAgICAgICAgICB0aGlzLl9fZmlyZShkaXNwYXRjaFtrZXldLCBjaGFuZ2UpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmIChkaXNwYXRjaCAhPSBudWxsICYmICdAZScgaW4gZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgICAgICB0aGlzLl9fZmlyZShkaXNwYXRjaCwgdHJlZSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9O1xuICAgIHJldHVybiBUcmVlO1xufSgpKTtcbmV4cG9ydHMuVHJlZSA9IFRyZWU7XG4iXSwic291cmNlUm9vdCI6IiJ9
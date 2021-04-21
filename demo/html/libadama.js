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
        this.sessionId = "";
        this.sendId = 0;
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
                self.sessionId = result.session_id;
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
                request = { method: "reserve", space: gs };
                self = this;
                return [2 /*return*/, new Promise(function (good, bad) {
                        self._send(request, function (response) {
                            if ('failure' in response) {
                                bad(response.reason);
                            }
                            else {
                                good(response.key);
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
                request = { method: "reflect", space: gs, key: '0' };
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
    Connection.prototype.load_code = function (gs) {
        return __awaiter(this, void 0, void 0, function () {
            var request, self;
            return __generator(this, function (_a) {
                request = { method: "load_code", space: gs };
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
    Connection.prototype.save_code = function (gs, code) {
        return __awaiter(this, void 0, void 0, function () {
            var request, self;
            return __generator(this, function (_a) {
                request = { method: "save_code", space: gs, code: code };
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
    Connection.prototype.deploy = function (gs) {
        return __awaiter(this, void 0, void 0, function () {
            var request, self;
            return __generator(this, function (_a) {
                request = { method: "deploy", space: gs };
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
                request = { method: "create", space: gs, key: id, arg: arg };
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
                request = { method: "connect", space: gs, key: id };
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
            var self, request;
            return __generator(this, function (_a) {
                self = this;
                request = { method: "send", marker: self.sessionId = "/" + self.sendId, space: gs, key: id, channel: channel, message: msg };
                self.sendId++;
                if (hack) {
                    hack(request);
                }
                // TODO: queue this up? with retry?
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
                    request: { method: "connect", space: gs, key: id },
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
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly9BZGFtYS93ZWJwYWNrL3VuaXZlcnNhbE1vZHVsZURlZmluaXRpb24iLCJ3ZWJwYWNrOi8vQWRhbWEvd2VicGFjay9ib290c3RyYXAiLCJ3ZWJwYWNrOi8vQWRhbWEvLi9ub2RlX21vZHVsZXMvaXNvbW9ycGhpYy13cy9icm93c2VyLmpzIiwid2VicGFjazovL0FkYW1hLyh3ZWJwYWNrKS9idWlsZGluL2dsb2JhbC5qcyIsIndlYnBhY2s6Ly9BZGFtYS8uL3NyYy9pbmRleC50cyIsIndlYnBhY2s6Ly9BZGFtYS8uL3NyYy90cmVlLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLENBQUM7QUFDRCxPO1FDVkE7UUFDQTs7UUFFQTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBOztRQUVBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7OztRQUdBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7UUFDQSwwQ0FBMEMsZ0NBQWdDO1FBQzFFO1FBQ0E7O1FBRUE7UUFDQTtRQUNBO1FBQ0Esd0RBQXdELGtCQUFrQjtRQUMxRTtRQUNBLGlEQUFpRCxjQUFjO1FBQy9EOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQSx5Q0FBeUMsaUNBQWlDO1FBQzFFLGdIQUFnSCxtQkFBbUIsRUFBRTtRQUNySTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBLDJCQUEyQiwwQkFBMEIsRUFBRTtRQUN2RCxpQ0FBaUMsZUFBZTtRQUNoRDtRQUNBO1FBQ0E7O1FBRUE7UUFDQSxzREFBc0QsK0RBQStEOztRQUVySDtRQUNBOzs7UUFHQTtRQUNBOzs7Ozs7Ozs7Ozs7QUNsRkE7O0FBRUE7O0FBRUE7QUFDQTtBQUNBLENBQUM7QUFDRDtBQUNBLENBQUM7QUFDRDtBQUNBLENBQUM7QUFDRDtBQUNBLENBQUM7QUFDRDtBQUNBOztBQUVBOzs7Ozs7Ozs7Ozs7O0FDaEJBOztBQUVBO0FBQ0E7QUFDQTtBQUNBLENBQUM7O0FBRUQ7QUFDQTtBQUNBO0FBQ0EsQ0FBQztBQUNEO0FBQ0E7QUFDQTs7QUFFQTtBQUNBO0FBQ0EsNENBQTRDOztBQUU1Qzs7Ozs7Ozs7Ozs7OztBQ25CYTtBQUNiO0FBQ0EsMkJBQTJCLCtEQUErRCxnQkFBZ0IsRUFBRSxFQUFFO0FBQzlHO0FBQ0EsbUNBQW1DLE1BQU0sNkJBQTZCLEVBQUUsWUFBWSxXQUFXLEVBQUU7QUFDakcsa0NBQWtDLE1BQU0saUNBQWlDLEVBQUUsWUFBWSxXQUFXLEVBQUU7QUFDcEcsK0JBQStCLHFGQUFxRjtBQUNwSDtBQUNBLEtBQUs7QUFDTDtBQUNBO0FBQ0EsYUFBYSw2QkFBNkIsMEJBQTBCLGFBQWEsRUFBRSxxQkFBcUI7QUFDeEcsZ0JBQWdCLHFEQUFxRCxvRUFBb0UsYUFBYSxFQUFFO0FBQ3hKLHNCQUFzQixzQkFBc0IscUJBQXFCLEdBQUc7QUFDcEU7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsdUNBQXVDO0FBQ3ZDLGtDQUFrQyxTQUFTO0FBQzNDLGtDQUFrQyxXQUFXLFVBQVU7QUFDdkQseUNBQXlDLGNBQWM7QUFDdkQ7QUFDQSw2R0FBNkcsT0FBTyxVQUFVO0FBQzlILGdGQUFnRixpQkFBaUIsT0FBTztBQUN4Ryx3REFBd0QsZ0JBQWdCLFFBQVEsT0FBTztBQUN2Riw4Q0FBOEMsZ0JBQWdCLGdCQUFnQixPQUFPO0FBQ3JGO0FBQ0EsaUNBQWlDO0FBQ2pDO0FBQ0E7QUFDQSxTQUFTLFlBQVksYUFBYSxPQUFPLEVBQUUsVUFBVSxXQUFXO0FBQ2hFLG1DQUFtQyxTQUFTO0FBQzVDO0FBQ0E7QUFDQTtBQUNBLDRDQUE0QztBQUM1QztBQUNBLDhDQUE4QyxjQUFjO0FBQzVEO0FBQ0Esc0NBQXNDLG1CQUFPLENBQUMsOERBQWU7QUFDN0QsYUFBYSxtQkFBTyxDQUFDLDZCQUFRO0FBQzdCO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLHNCQUFzQiwwQkFBMEI7QUFDaEQsU0FBUztBQUNUO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsZ0NBQWdDO0FBQ2hDO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0Esb0NBQW9DLGNBQWMsRUFBRTtBQUNwRDtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxxQkFBcUI7QUFDckI7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSxzQkFBc0IsNkJBQTZCO0FBQ25EO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFD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LDJCQUEyQixxQkFBcUI7QUFDaEQ7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLHVDQUF1QyxVQUFVO0FBQ2pEO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0Esb0RBQW9ELDJDQUEyQztBQUMvRjtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsMkJBQTJCLGtCQUFrQjtBQUM3QztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLCtCQUErQixjQUFjO0FBQzdDO0FBQ0E7QUFDQSx5Q0FBeUMsMkVBQTJFO0FBQ3BIO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsbUNBQW1DLGNBQWM7QUFDakQ7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSx5Q0FBeUMsMEJBQTBCLEVBQUU7QUFDckUsdUJBQXVCLHVCQUF1QjtBQUM5QztBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsQ0FBQztBQUNEIiwiZmlsZSI6ImxpYmFkYW1hLmpzIiwic291cmNlc0NvbnRlbnQiOlsiKGZ1bmN0aW9uIHdlYnBhY2tVbml2ZXJzYWxNb2R1bGVEZWZpbml0aW9uKHJvb3QsIGZhY3RvcnkpIHtcblx0aWYodHlwZW9mIGV4cG9ydHMgPT09ICdvYmplY3QnICYmIHR5cGVvZiBtb2R1bGUgPT09ICdvYmplY3QnKVxuXHRcdG1vZHVsZS5leHBvcnRzID0gZmFjdG9yeSgpO1xuXHRlbHNlIGlmKHR5cGVvZiBkZWZpbmUgPT09ICdmdW5jdGlvbicgJiYgZGVmaW5lLmFtZClcblx0XHRkZWZpbmUoW10sIGZhY3RvcnkpO1xuXHRlbHNlIGlmKHR5cGVvZiBleHBvcnRzID09PSAnb2JqZWN0Jylcblx0XHRleHBvcnRzW1wiQWRhbWFcIl0gPSBmYWN0b3J5KCk7XG5cdGVsc2Vcblx0XHRyb290W1wiQWRhbWFcIl0gPSBmYWN0b3J5KCk7XG59KSh0aGlzLCBmdW5jdGlvbigpIHtcbnJldHVybiAiLCIgXHQvLyBUaGUgbW9kdWxlIGNhY2hlXG4gXHR2YXIgaW5zdGFsbGVkTW9kdWxlcyA9IHt9O1xuXG4gXHQvLyBUaGUgcmVxdWlyZSBmdW5jdGlvblxuIFx0ZnVuY3Rpb24gX193ZWJwYWNrX3JlcXVpcmVfXyhtb2R1bGVJZCkge1xuXG4gXHRcdC8vIENoZWNrIGlmIG1vZHVsZSBpcyBpbiBjYWNoZVxuIFx0XHRpZihpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSkge1xuIFx0XHRcdHJldHVybiBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXS5leHBvcnRzO1xuIFx0XHR9XG4gXHRcdC8vIENyZWF0ZSBhIG5ldyBtb2R1bGUgKGFuZCBwdXQgaXQgaW50byB0aGUgY2FjaGUpXG4gXHRcdHZhciBtb2R1bGUgPSBpbnN0YWxsZWRNb2R1bGVzW21vZHVsZUlkXSA9IHtcbiBcdFx0XHRpOiBtb2R1bGVJZCxcbiBcdFx0XHRsOiBmYWxzZSxcbiBcdFx0XHRleHBvcnRzOiB7fVxuIFx0XHR9O1xuXG4gXHRcdC8vIEV4ZWN1dGUgdGhlIG1vZHVsZSBmdW5jdGlvblxuIFx0XHRtb2R1bGVzW21vZHVsZUlkXS5jYWxsKG1vZHVsZS5leHBvcnRzLCBtb2R1bGUsIG1vZHVsZS5leHBvcnRzLCBfX3dlYnBhY2tfcmVxdWlyZV9fKTtcblxuIFx0XHQvLyBGbGFnIHRoZSBtb2R1bGUgYXMgbG9hZGVkXG4gXHRcdG1vZHVsZS5sID0gdHJ1ZTtcblxuIFx0XHQvLyBSZXR1cm4gdGhlIGV4cG9ydHMgb2YgdGhlIG1vZHVsZVxuIFx0XHRyZXR1cm4gbW9kdWxlLmV4cG9ydHM7XG4gXHR9XG5cblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGVzIG9iamVjdCAoX193ZWJwYWNrX21vZHVsZXNfXylcbiBcdF9fd2VicGFja19yZXF1aXJlX18ubSA9IG1vZHVsZXM7XG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlIGNhY2hlXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLmMgPSBpbnN0YWxsZWRNb2R1bGVzO1xuXG4gXHQvLyBkZWZpbmUgZ2V0dGVyIGZ1bmN0aW9uIGZvciBoYXJtb255IGV4cG9ydHNcbiBcdF9fd2VicGFja19yZXF1aXJlX18uZCA9IGZ1bmN0aW9uKGV4cG9ydHMsIG5hbWUsIGdldHRlcikge1xuIFx0XHRpZighX193ZWJwYWNrX3JlcXVpcmVfXy5vKGV4cG9ydHMsIG5hbWUpKSB7XG4gXHRcdFx0T2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIG5hbWUsIHsgZW51bWVyYWJsZTogdHJ1ZSwgZ2V0OiBnZXR0ZXIgfSk7XG4gXHRcdH1cbiBcdH07XG5cbiBcdC8vIGRlZmluZSBfX2VzTW9kdWxlIG9uIGV4cG9ydHNcbiBcdF9fd2VicGFja19yZXF1aXJlX18uciA9IGZ1bmN0aW9uKGV4cG9ydHMpIHtcbiBcdFx0aWYodHlwZW9mIFN5bWJvbCAhPT0gJ3VuZGVmaW5lZCcgJiYgU3ltYm9sLnRvU3RyaW5nVGFnKSB7XG4gXHRcdFx0T2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFN5bWJvbC50b1N0cmluZ1RhZywgeyB2YWx1ZTogJ01vZHVsZScgfSk7XG4gXHRcdH1cbiBcdFx0T2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsICdfX2VzTW9kdWxlJywgeyB2YWx1ZTogdHJ1ZSB9KTtcbiBcdH07XG5cbiBcdC8vIGNyZWF0ZSBhIGZha2UgbmFtZXNwYWNlIG9iamVjdFxuIFx0Ly8gbW9kZSAmIDE6IHZhbHVlIGlzIGEgbW9kdWxlIGlkLCByZXF1aXJlIGl0XG4gXHQvLyBtb2RlICYgMjogbWVyZ2UgYWxsIHByb3BlcnRpZXMgb2YgdmFsdWUgaW50byB0aGUgbnNcbiBcdC8vIG1vZGUgJiA0OiByZXR1cm4gdmFsdWUgd2hlbiBhbHJlYWR5IG5zIG9iamVjdFxuIFx0Ly8gbW9kZSAmIDh8MTogYmVoYXZlIGxpa2UgcmVxdWlyZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy50ID0gZnVuY3Rpb24odmFsdWUsIG1vZGUpIHtcbiBcdFx0aWYobW9kZSAmIDEpIHZhbHVlID0gX193ZWJwYWNrX3JlcXVpcmVfXyh2YWx1ZSk7XG4gXHRcdGlmKG1vZGUgJiA4KSByZXR1cm4gdmFsdWU7XG4gXHRcdGlmKChtb2RlICYgNCkgJiYgdHlwZW9mIHZhbHVlID09PSAnb2JqZWN0JyAmJiB2YWx1ZSAmJiB2YWx1ZS5fX2VzTW9kdWxlKSByZXR1cm4gdmFsdWU7XG4gXHRcdHZhciBucyA9IE9iamVjdC5jcmVhdGUobnVsbCk7XG4gXHRcdF9fd2VicGFja19yZXF1aXJlX18ucihucyk7XG4gXHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShucywgJ2RlZmF1bHQnLCB7IGVudW1lcmFibGU6IHRydWUsIHZhbHVlOiB2YWx1ZSB9KTtcbiBcdFx0aWYobW9kZSAmIDIgJiYgdHlwZW9mIHZhbHVlICE9ICdzdHJpbmcnKSBmb3IodmFyIGtleSBpbiB2YWx1ZSkgX193ZWJwYWNrX3JlcXVpcmVfXy5kKG5zLCBrZXksIGZ1bmN0aW9uKGtleSkgeyByZXR1cm4gdmFsdWVba2V5XTsgfS5iaW5kKG51bGwsIGtleSkpO1xuIFx0XHRyZXR1cm4gbnM7XG4gXHR9O1xuXG4gXHQvLyBnZXREZWZhdWx0RXhwb3J0IGZ1bmN0aW9uIGZvciBjb21wYXRpYmlsaXR5IHdpdGggbm9uLWhhcm1vbnkgbW9kdWxlc1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5uID0gZnVuY3Rpb24obW9kdWxlKSB7XG4gXHRcdHZhciBnZXR0ZXIgPSBtb2R1bGUgJiYgbW9kdWxlLl9fZXNNb2R1bGUgP1xuIFx0XHRcdGZ1bmN0aW9uIGdldERlZmF1bHQoKSB7IHJldHVybiBtb2R1bGVbJ2RlZmF1bHQnXTsgfSA6XG4gXHRcdFx0ZnVuY3Rpb24gZ2V0TW9kdWxlRXhwb3J0cygpIHsgcmV0dXJuIG1vZHVsZTsgfTtcbiBcdFx0X193ZWJwYWNrX3JlcXVpcmVfXy5kKGdldHRlciwgJ2EnLCBnZXR0ZXIpO1xuIFx0XHRyZXR1cm4gZ2V0dGVyO1xuIFx0fTtcblxuIFx0Ly8gT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm8gPSBmdW5jdGlvbihvYmplY3QsIHByb3BlcnR5KSB7IHJldHVybiBPYmplY3QucHJvdG90eXBlLmhhc093blByb3BlcnR5LmNhbGwob2JqZWN0LCBwcm9wZXJ0eSk7IH07XG5cbiBcdC8vIF9fd2VicGFja19wdWJsaWNfcGF0aF9fXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnAgPSBcIlwiO1xuXG5cbiBcdC8vIExvYWQgZW50cnkgbW9kdWxlIGFuZCByZXR1cm4gZXhwb3J0c1xuIFx0cmV0dXJuIF9fd2VicGFja19yZXF1aXJlX18oX193ZWJwYWNrX3JlcXVpcmVfXy5zID0gXCIuL3NyYy9pbmRleC50c1wiKTtcbiIsIi8vIGh0dHBzOi8vZ2l0aHViLmNvbS9tYXhvZ2Rlbi93ZWJzb2NrZXQtc3RyZWFtL2Jsb2IvNDhkYzNkZGY5NDNlNWFkYTY2OGMzMWNjZDk0ZTkxODZmMDJmYWZiZC93cy1mYWxsYmFjay5qc1xuXG52YXIgd3MgPSBudWxsXG5cbmlmICh0eXBlb2YgV2ViU29ja2V0ICE9PSAndW5kZWZpbmVkJykge1xuICB3cyA9IFdlYlNvY2tldFxufSBlbHNlIGlmICh0eXBlb2YgTW96V2ViU29ja2V0ICE9PSAndW5kZWZpbmVkJykge1xuICB3cyA9IE1veldlYlNvY2tldFxufSBlbHNlIGlmICh0eXBlb2YgZ2xvYmFsICE9PSAndW5kZWZpbmVkJykge1xuICB3cyA9IGdsb2JhbC5XZWJTb2NrZXQgfHwgZ2xvYmFsLk1veldlYlNvY2tldFxufSBlbHNlIGlmICh0eXBlb2Ygd2luZG93ICE9PSAndW5kZWZpbmVkJykge1xuICB3cyA9IHdpbmRvdy5XZWJTb2NrZXQgfHwgd2luZG93Lk1veldlYlNvY2tldFxufSBlbHNlIGlmICh0eXBlb2Ygc2VsZiAhPT0gJ3VuZGVmaW5lZCcpIHtcbiAgd3MgPSBzZWxmLldlYlNvY2tldCB8fCBzZWxmLk1veldlYlNvY2tldFxufVxuXG5tb2R1bGUuZXhwb3J0cyA9IHdzXG4iLCJ2YXIgZztcblxuLy8gVGhpcyB3b3JrcyBpbiBub24tc3RyaWN0IG1vZGVcbmcgPSAoZnVuY3Rpb24oKSB7XG5cdHJldHVybiB0aGlzO1xufSkoKTtcblxudHJ5IHtcblx0Ly8gVGhpcyB3b3JrcyBpZiBldmFsIGlzIGFsbG93ZWQgKHNlZSBDU1ApXG5cdGcgPSBnIHx8IG5ldyBGdW5jdGlvbihcInJldHVybiB0aGlzXCIpKCk7XG59IGNhdGNoIChlKSB7XG5cdC8vIFRoaXMgd29ya3MgaWYgdGhlIHdpbmRvdyByZWZlcmVuY2UgaXMgYXZhaWxhYmxlXG5cdGlmICh0eXBlb2Ygd2luZG93ID09PSBcIm9iamVjdFwiKSBnID0gd2luZG93O1xufVxuXG4vLyBnIGNhbiBzdGlsbCBiZSB1bmRlZmluZWQsIGJ1dCBub3RoaW5nIHRvIGRvIGFib3V0IGl0Li4uXG4vLyBXZSByZXR1cm4gdW5kZWZpbmVkLCBpbnN0ZWFkIG9mIG5vdGhpbmcgaGVyZSwgc28gaXQnc1xuLy8gZWFzaWVyIHRvIGhhbmRsZSB0aGlzIGNhc2UuIGlmKCFnbG9iYWwpIHsgLi4ufVxuXG5tb2R1bGUuZXhwb3J0cyA9IGc7XG4iLCJcInVzZSBzdHJpY3RcIjtcbnZhciBfX2F3YWl0ZXIgPSAodGhpcyAmJiB0aGlzLl9fYXdhaXRlcikgfHwgZnVuY3Rpb24gKHRoaXNBcmcsIF9hcmd1bWVudHMsIFAsIGdlbmVyYXRvcikge1xuICAgIGZ1bmN0aW9uIGFkb3B0KHZhbHVlKSB7IHJldHVybiB2YWx1ZSBpbnN0YW5jZW9mIFAgPyB2YWx1ZSA6IG5ldyBQKGZ1bmN0aW9uIChyZXNvbHZlKSB7IHJlc29sdmUodmFsdWUpOyB9KTsgfVxuICAgIHJldHVybiBuZXcgKFAgfHwgKFAgPSBQcm9taXNlKSkoZnVuY3Rpb24gKHJlc29sdmUsIHJlamVjdCkge1xuICAgICAgICBmdW5jdGlvbiBmdWxmaWxsZWQodmFsdWUpIHsgdHJ5IHsgc3RlcChnZW5lcmF0b3IubmV4dCh2YWx1ZSkpOyB9IGNhdGNoIChlKSB7IHJlamVjdChlKTsgfSB9XG4gICAgICAgIGZ1bmN0aW9uIHJlamVjdGVkKHZhbHVlKSB7IHRyeSB7IHN0ZXAoZ2VuZXJhdG9yW1widGhyb3dcIl0odmFsdWUpKTsgfSBjYXRjaCAoZSkgeyByZWplY3QoZSk7IH0gfVxuICAgICAgICBmdW5jdGlvbiBzdGVwKHJlc3VsdCkgeyByZXN1bHQuZG9uZSA/IHJlc29sdmUocmVzdWx0LnZhbHVlKSA6IGFkb3B0KHJlc3VsdC52YWx1ZSkudGhlbihmdWxmaWxsZWQsIHJlamVjdGVkKTsgfVxuICAgICAgICBzdGVwKChnZW5lcmF0b3IgPSBnZW5lcmF0b3IuYXBwbHkodGhpc0FyZywgX2FyZ3VtZW50cyB8fCBbXSkpLm5leHQoKSk7XG4gICAgfSk7XG59O1xudmFyIF9fZ2VuZXJhdG9yID0gKHRoaXMgJiYgdGhpcy5fX2dlbmVyYXRvcikgfHwgZnVuY3Rpb24gKHRoaXNBcmcsIGJvZHkpIHtcbiAgICB2YXIgXyA9IHsgbGFiZWw6IDAsIHNlbnQ6IGZ1bmN0aW9uKCkgeyBpZiAodFswXSAmIDEpIHRocm93IHRbMV07IHJldHVybiB0WzFdOyB9LCB0cnlzOiBbXSwgb3BzOiBbXSB9LCBmLCB5LCB0LCBnO1xuICAgIHJldHVybiBnID0geyBuZXh0OiB2ZXJiKDApLCBcInRocm93XCI6IHZlcmIoMSksIFwicmV0dXJuXCI6IHZlcmIoMikgfSwgdHlwZW9mIFN5bWJvbCA9PT0gXCJmdW5jdGlvblwiICYmIChnW1N5bWJvbC5pdGVyYXRvcl0gPSBmdW5jdGlvbigpIHsgcmV0dXJuIHRoaXM7IH0pLCBnO1xuICAgIGZ1bmN0aW9uIHZlcmIobikgeyByZXR1cm4gZnVuY3Rpb24gKHYpIHsgcmV0dXJuIHN0ZXAoW24sIHZdKTsgfTsgfVxuICAgIGZ1bmN0aW9uIHN0ZXAob3ApIHtcbiAgICAgICAgaWYgKGYpIHRocm93IG5ldyBUeXBlRXJyb3IoXCJHZW5lcmF0b3IgaXMgYWxyZWFkeSBleGVjdXRpbmcuXCIpO1xuICAgICAgICB3aGlsZSAoXykgdHJ5IHtcbiAgICAgICAgICAgIGlmIChmID0gMSwgeSAmJiAodCA9IG9wWzBdICYgMiA/IHlbXCJyZXR1cm5cIl0gOiBvcFswXSA/IHlbXCJ0aHJvd1wiXSB8fCAoKHQgPSB5W1wicmV0dXJuXCJdKSAmJiB0LmNhbGwoeSksIDApIDogeS5uZXh0KSAmJiAhKHQgPSB0LmNhbGwoeSwgb3BbMV0pKS5kb25lKSByZXR1cm4gdDtcbiAgICAgICAgICAgIGlmICh5ID0gMCwgdCkgb3AgPSBbb3BbMF0gJiAyLCB0LnZhbHVlXTtcbiAgICAgICAgICAgIHN3aXRjaCAob3BbMF0pIHtcbiAgICAgICAgICAgICAgICBjYXNlIDA6IGNhc2UgMTogdCA9IG9wOyBicmVhaztcbiAgICAgICAgICAgICAgICBjYXNlIDQ6IF8ubGFiZWwrKzsgcmV0dXJuIHsgdmFsdWU6IG9wWzFdLCBkb25lOiBmYWxzZSB9O1xuICAgICAgICAgICAgICAgIGNhc2UgNTogXy5sYWJlbCsrOyB5ID0gb3BbMV07IG9wID0gWzBdOyBjb250aW51ZTtcbiAgICAgICAgICAgICAgICBjYXNlIDc6IG9wID0gXy5vcHMucG9wKCk7IF8udHJ5cy5wb3AoKTsgY29udGludWU7XG4gICAgICAgICAgICAgICAgZGVmYXVsdDpcbiAgICAgICAgICAgICAgICAgICAgaWYgKCEodCA9IF8udHJ5cywgdCA9IHQubGVuZ3RoID4gMCAmJiB0W3QubGVuZ3RoIC0gMV0pICYmIChvcFswXSA9PT0gNiB8fCBvcFswXSA9PT0gMikpIHsgXyA9IDA7IGNvbnRpbnVlOyB9XG4gICAgICAgICAgICAgICAgICAgIGlmIChvcFswXSA9PT0gMyAmJiAoIXQgfHwgKG9wWzFdID4gdFswXSAmJiBvcFsxXSA8IHRbM10pKSkgeyBfLmxhYmVsID0gb3BbMV07IGJyZWFrOyB9XG4gICAgICAgICAgICAgICAgICAgIGlmIChvcFswXSA9PT0gNiAmJiBfLmxhYmVsIDwgdFsxXSkgeyBfLmxhYmVsID0gdFsxXTsgdCA9IG9wOyBicmVhazsgfVxuICAgICAgICAgICAgICAgICAgICBpZiAodCAmJiBfLmxhYmVsIDwgdFsyXSkgeyBfLmxhYmVsID0gdFsyXTsgXy5vcHMucHVzaChvcCk7IGJyZWFrOyB9XG4gICAgICAgICAgICAgICAgICAgIGlmICh0WzJdKSBfLm9wcy5wb3AoKTtcbiAgICAgICAgICAgICAgICAgICAgXy50cnlzLnBvcCgpOyBjb250aW51ZTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIG9wID0gYm9keS5jYWxsKHRoaXNBcmcsIF8pO1xuICAgICAgICB9IGNhdGNoIChlKSB7IG9wID0gWzYsIGVdOyB5ID0gMDsgfSBmaW5hbGx5IHsgZiA9IHQgPSAwOyB9XG4gICAgICAgIGlmIChvcFswXSAmIDUpIHRocm93IG9wWzFdOyByZXR1cm4geyB2YWx1ZTogb3BbMF0gPyBvcFsxXSA6IHZvaWQgMCwgZG9uZTogdHJ1ZSB9O1xuICAgIH1cbn07XG52YXIgX19pbXBvcnREZWZhdWx0ID0gKHRoaXMgJiYgdGhpcy5fX2ltcG9ydERlZmF1bHQpIHx8IGZ1bmN0aW9uIChtb2QpIHtcbiAgICByZXR1cm4gKG1vZCAmJiBtb2QuX19lc01vZHVsZSkgPyBtb2QgOiB7IFwiZGVmYXVsdFwiOiBtb2QgfTtcbn07XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJfX2VzTW9kdWxlXCIsIHsgdmFsdWU6IHRydWUgfSk7XG5leHBvcnRzLkNvbm5lY3Rpb24gPSBleHBvcnRzLk1ha2VUcmVlID0gdm9pZCAwO1xudmFyIGlzb21vcnBoaWNfd3NfMSA9IF9faW1wb3J0RGVmYXVsdChyZXF1aXJlKFwiaXNvbW9ycGhpYy13c1wiKSk7XG52YXIgdHJlZV8xID0gcmVxdWlyZShcIi4vdHJlZVwiKTtcbmZ1bmN0aW9uIE1ha2VUcmVlKCkge1xuICAgIHJldHVybiBuZXcgdHJlZV8xLlRyZWUoKTtcbn1cbmV4cG9ydHMuTWFrZVRyZWUgPSBNYWtlVHJlZTtcbnZhciBDb25uZWN0aW9uID0gLyoqIEBjbGFzcyAqLyAoZnVuY3Rpb24gKCkge1xuICAgIGZ1bmN0aW9uIENvbm5lY3Rpb24odXJsKSB7XG4gICAgICAgIHZhciBzZWxmID0gdGhpcztcbiAgICAgICAgdGhpcy5iYWNrb2ZmID0gMTtcbiAgICAgICAgdGhpcy51cmwgPSB1cmw7XG4gICAgICAgIHRoaXMuY29ubmVjdGVkID0gZmFsc2U7XG4gICAgICAgIHRoaXMuZGVhZCA9IGZhbHNlO1xuICAgICAgICB0aGlzLm1heGltdW1fYmFja29mZiA9IDI1MDA7XG4gICAgICAgIHRoaXMuc29ja2V0ID0gbnVsbDtcbiAgICAgICAgdGhpcy5vbnN0YXR1c2NoYW5nZSA9IGZ1bmN0aW9uIChzdGF0dXMpIHtcbiAgICAgICAgfTtcbiAgICAgICAgdGhpcy5vbnBpbmcgPSBmdW5jdGlvbiAoc2Vjb25kcywgbGF0ZW5jeSkge1xuICAgICAgICB9O1xuICAgICAgICB0aGlzLm9uYXV0aG5lZWRlZCA9IGZ1bmN0aW9uICh0cnlhZ2Fpbikge1xuICAgICAgICB9O1xuICAgICAgICB0aGlzLnNjaGVkdWxlZCA9IGZhbHNlO1xuICAgICAgICB0aGlzLmNhbGxiYWNrcyA9IG5ldyBNYXAoKTtcbiAgICAgICAgdGhpcy5jb25uZWN0SWQgPSAxO1xuICAgICAgICB0aGlzLm9ucmVjb25uZWN0ID0gbmV3IE1hcCgpO1xuICAgICAgICB0aGlzLnJwY2lkID0gMTtcbiAgICAgICAgdGhpcy5zZXNzaW9uSWQgPSBcIlwiO1xuICAgICAgICB0aGlzLnNlbmRJZCA9IDA7XG4gICAgfVxuICAgIC8qKiBzdG9wIHRoZSBjb25uZWN0aW9uICovXG4gICAgQ29ubmVjdGlvbi5wcm90b3R5cGUuc3RvcCA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgdGhpcy5kZWFkID0gdHJ1ZTtcbiAgICAgICAgaWYgKHRoaXMuc29ja2V0ICE9PSBudWxsKSB7XG4gICAgICAgICAgICB0aGlzLnNvY2tldC5jbG9zZSgpO1xuICAgICAgICB9XG4gICAgfTtcbiAgICAvKiogcHJpdmF0ZTogcmV0cnkgdGhlIGNvbm5lY3Rpb24gKi9cbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5fcmV0cnkgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIC8vIG51bGwgb3V0IHRoZSBzb2NrZXRcbiAgICAgICAgdGhpcy5zb2NrZXQgPSBudWxsO1xuICAgICAgICAvLyBpZiB3ZSBhcmUgY29ubmVjdGVkLCB0cmFuc2l0aW9uIHRoZSBzdGF0dXNcbiAgICAgICAgaWYgKHRoaXMuY29ubmVjdGVkKSB7XG4gICAgICAgICAgICB0aGlzLmNvbm5lY3RlZCA9IGZhbHNlO1xuICAgICAgICAgICAgdGhpcy5vbnN0YXR1c2NoYW5nZShmYWxzZSk7XG4gICAgICAgIH1cbiAgICAgICAgLy8gZmFpbCBhbGwgb3V0c3RhbmRpbmcgb3BlcmF0aW9uc1xuICAgICAgICB0aGlzLmNhbGxiYWNrcy5mb3JFYWNoKGZ1bmN0aW9uIChjYWxsYmFjaywgaWQpIHtcbiAgICAgICAgICAgIGNhbGxiYWNrKHsgZmFpbHVyZTogaWQsIHJlYXNvbjogNzcgfSk7XG4gICAgICAgIH0pO1xuICAgICAgICB0aGlzLmNhbGxiYWNrcy5jbGVhcigpO1xuICAgICAgICAvLyBpZiB3ZSBhcmUgZGVhZCwgdGhlbiBkb24ndCBhY3R1YWxseSByZXRyeVxuICAgICAgICBpZiAodGhpcy5kZWFkKSB7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cbiAgICAgICAgLy8gbWFrZSBzdXJlXG4gICAgICAgIGlmICghdGhpcy5zY2hlZHVsZWQpIHtcbiAgICAgICAgICAgIC8vIHNjaGVkdWxlIGEgcmV0cnk7IGZpcnN0IGNvbXB1dGUgaG93IGxvbmcgdG8gdGFrZVxuICAgICAgICAgICAgLy8gY29tcHV0ZSBob3cgbG9uZyB3ZSBuZWVkIHRvIHdhaXRcbiAgICAgICAgICAgIHZhciBoYWx2ZUFmdGVyU2NoZWR1bGUgPSBmYWxzZTtcbiAgICAgICAgICAgIHRoaXMuYmFja29mZiArPSBNYXRoLnJhbmRvbSgpICogdGhpcy5iYWNrb2ZmO1xuICAgICAgICAgICAgaWYgKHRoaXMuYmFja29mZiA+IHRoaXMubWF4aW11bV9iYWNrb2ZmKSB7XG4gICAgICAgICAgICAgICAgdGhpcy5iYWNrb2ZmID0gdGhpcy5tYXhpbXVtX2JhY2tvZmY7XG4gICAgICAgICAgICAgICAgaGFsdmVBZnRlclNjaGVkdWxlID0gdHJ1ZTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIC8vIHNjaGVkdWxlIGl0XG4gICAgICAgICAgICB0aGlzLnNjaGVkdWxlZCA9IHRydWU7XG4gICAgICAgICAgICB2YXIgc2VsZiA9IHRoaXM7XG4gICAgICAgICAgICBzZXRUaW1lb3V0KGZ1bmN0aW9uICgpIHsgc2VsZi5zdGFydCgpOyB9LCB0aGlzLmJhY2tvZmYpO1xuICAgICAgICAgICAgLy8gd2UganVzdCBzY2hlZHVsZWQgaXQgZm9yIHRoZSBtYXhpbXVtIGJhY2tvZmYgdGltZSwgbGV0J3MgcmVkdWNlIHRoZSBiYWNrb2ZmIHNvIHRoZSBuZXh0IG9uZSB3aWxsIGhhdmUgc29tZSBqaXR0ZXJcbiAgICAgICAgICAgIGlmIChoYWx2ZUFmdGVyU2NoZWR1bGUpIHtcbiAgICAgICAgICAgICAgICB0aGlzLmJhY2tvZmYgLz0gMi4wO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgfTtcbiAgICAvKiogc3RhcnQgdGhlIGNvbm5lY3Rpb24gKi9cbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5zdGFydCA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgLy8gcmVzZXQgdGhlIHN0YXRlXG4gICAgICAgIHZhciBzZWxmID0gdGhpcztcbiAgICAgICAgdGhpcy5zY2hlZHVsZWQgPSBmYWxzZTtcbiAgICAgICAgdGhpcy5kZWFkID0gZmFsc2U7XG4gICAgICAgIC8vIGNyZWF0ZSB0aGUgc29ja2V0IGFuZCBiaW5kIGV2ZW50IGhhbmRsZXJzXG4gICAgICAgIHRoaXMuc29ja2V0ID0gbmV3IGlzb21vcnBoaWNfd3NfMS5kZWZhdWx0KHRoaXMudXJsKTtcbiAgICAgICAgdGhpcy5zb2NrZXQub25tZXNzYWdlID0gZnVuY3Rpb24gKGV2ZW50KSB7XG4gICAgICAgICAgICB2YXIgcmVzdWx0ID0gSlNPTi5wYXJzZShldmVudC5kYXRhKTtcbiAgICAgICAgICAgIC8vIGEgbWVzc2FnZSBhcnJpdmVkLCBpcyBpdCBhIGNvbm5lY3Rpb24gc2lnbmFsXG4gICAgICAgICAgICBpZiAoJ3BpbmcnIGluIHJlc3VsdCkge1xuICAgICAgICAgICAgICAgIHNlbGYub25waW5nKHJlc3VsdC5waW5nLCByZXN1bHQubGF0ZW5jeSk7XG4gICAgICAgICAgICAgICAgcmVzdWx0LnBvbmcgPSBuZXcgRGF0ZSgpLmdldFRpbWUoKSAvIDEwMDAuMDtcbiAgICAgICAgICAgICAgICBzZWxmLnNvY2tldC5zZW5kKEpTT04uc3RyaW5naWZ5KHJlc3VsdCkpO1xuICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmICgnc2lnbmFsJyBpbiByZXN1bHQpIHtcbiAgICAgICAgICAgICAgICAvLyBoZXksIGFyZSB3ZSBjb25uZWN0ZWQ/XG4gICAgICAgICAgICAgICAgaWYgKHJlc3VsdC5zdGF0dXMgIT0gJ2Nvbm5lY3RlZCcpIHtcbiAgICAgICAgICAgICAgICAgICAgLy8gbm9wZSwgT0ssIGxldCdzIG1ha2UgdGhpcyBhIGRlYWQgc29ja2V0XG4gICAgICAgICAgICAgICAgICAgIHNlbGYuZGVhZCA9IHRydWU7XG4gICAgICAgICAgICAgICAgICAgIHNlbGYuc29ja2V0LmNsb3NlKCk7XG4gICAgICAgICAgICAgICAgICAgIHNlbGYuc29ja2V0ID0gbnVsbDtcbiAgICAgICAgICAgICAgICAgICAgLy8gaW5mb3JtIHRoZSBjbGllbnQgdG8gdHJ5IGFnYWluXG4gICAgICAgICAgICAgICAgICAgIHNlbGYub25hdXRobmVlZGVkKGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuc3RhcnQoKTtcbiAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgLy8gdGVsbCB0aGUgY2xpZW50IHRoYXQgd2UgYXJlIGdvb2QhXG4gICAgICAgICAgICAgICAgc2VsZi5iYWNrb2ZmID0gMTtcbiAgICAgICAgICAgICAgICBzZWxmLmNvbm5lY3RlZCA9IHRydWU7XG4gICAgICAgICAgICAgICAgc2VsZi5zZXNzaW9uSWQgPSByZXN1bHQuc2Vzc2lvbl9pZDtcbiAgICAgICAgICAgICAgICBzZWxmLm9uc3RhdHVzY2hhbmdlKHRydWUpO1xuICAgICAgICAgICAgICAgIHNlbGYuX3JlY29ubmVjdCgpO1xuICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIC8vIHRoZSByZXN1bHQgd2FzIGEgZmFpbHVyZS4uXG4gICAgICAgICAgICBpZiAoJ2ZhaWx1cmUnIGluIHJlc3VsdCkge1xuICAgICAgICAgICAgICAgIC8vIGZpbmQgdGhlIGNhbGxiYWNrLCB0aGVuIGludm9rZSBpdCAoYW5kIGNsZWFuIHVwKVxuICAgICAgICAgICAgICAgIGlmIChzZWxmLmNhbGxiYWNrcy5oYXMocmVzdWx0LmZhaWx1cmUpKSB7XG4gICAgICAgICAgICAgICAgICAgIHZhciBjYiA9IHNlbGYuY2FsbGJhY2tzLmdldChyZXN1bHQuZmFpbHVyZSk7XG4gICAgICAgICAgICAgICAgICAgIGlmIChjYikge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5jYWxsYmFja3MuZGVsZXRlKHJlc3VsdC5mYWlsdXJlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIGNiKHJlc3VsdCk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIGlmICgnZGVsaXZlcicgaW4gcmVzdWx0KSB7XG4gICAgICAgICAgICAgICAgLy8gb3RoZXJ3aXNlLCB3ZSBoYXZlIGEgc3VjY2Vzcywgc28gbGV0J3MgZmluZCB0aGUgY2FsbGJhY2ssIGFuZCBpZiBuZWVkIGJlIGNsZWFuIHVwXG4gICAgICAgICAgICAgICAgaWYgKHNlbGYuY2FsbGJhY2tzLmhhcyhyZXN1bHQuZGVsaXZlcikpIHtcbiAgICAgICAgICAgICAgICAgICAgdmFyIGNiID0gc2VsZi5jYWxsYmFja3MuZ2V0KHJlc3VsdC5kZWxpdmVyKTtcbiAgICAgICAgICAgICAgICAgICAgaWYgKGNiKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAocmVzdWx0LmRvbmUpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBzZWxmLmNhbGxiYWNrcy5kZWxldGUocmVzdWx0LmRlbGl2ZXIpO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgY2IocmVzdWx0LnJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgfTtcbiAgICAgICAgdGhpcy5zb2NrZXQub25jbG9zZSA9IGZ1bmN0aW9uIChldmVudCkge1xuICAgICAgICAgICAgLy8gbGV0J3MgcmV0cnkuLi4gb3Igc2hvdWxkIHdlIG5vdFxuICAgICAgICAgICAgc2VsZi5fcmV0cnkoKTtcbiAgICAgICAgfTtcbiAgICAgICAgdGhpcy5zb2NrZXQub25lcnJvciA9IGZ1bmN0aW9uIChldmVudCkge1xuICAgICAgICAgICAgLy8gc29tZXRoaW5nIGJhZCBoYXBwZW5lZCwgbGV0J3MgcmV0cnlcbiAgICAgICAgICAgIHNlbGYuX3JldHJ5KCk7XG4gICAgICAgIH07XG4gICAgfTtcbiAgICAvKiogcHJpdmF0ZTogc2VuZCBhIHJhdyBtZXNzYWdlICovXG4gICAgQ29ubmVjdGlvbi5wcm90b3R5cGUuX3NlbmQgPSBmdW5jdGlvbiAocmVxdWVzdCwgY2FsbGJhY2spIHtcbiAgICAgICAgaWYgKCF0aGlzLmNvbm5lY3RlZCkge1xuICAgICAgICAgICAgY2FsbGJhY2soeyBmYWlsdXJlOiA2MDAsIHJlYXNvbjogOTk5OSB9KTtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICB2YXIgaWQgPSB0aGlzLnJwY2lkO1xuICAgICAgICB0aGlzLnJwY2lkKys7XG4gICAgICAgIHJlcXVlc3RbJ2lkJ10gPSBpZDtcbiAgICAgICAgdGhpcy5jYWxsYmFja3Muc2V0KGlkLCBjYWxsYmFjayk7XG4gICAgICAgIHRoaXMuc29ja2V0LnNlbmQoSlNPTi5zdHJpbmdpZnkocmVxdWVzdCkpO1xuICAgIH07XG4gICAgLyoqIGFwaTogd2FpdCBmb3IgYSBjb25uZWN0aW9uICovXG4gICAgQ29ubmVjdGlvbi5wcm90b3R5cGUud2FpdF9jb25uZWN0ZWQgPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciBzZWxmLCBwcmlvcjtcbiAgICAgICAgICAgIHJldHVybiBfX2dlbmVyYXRvcih0aGlzLCBmdW5jdGlvbiAoX2EpIHtcbiAgICAgICAgICAgICAgICBpZiAodGhpcy5jb25uZWN0ZWQpIHtcbiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi9dO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBzZWxmID0gdGhpcztcbiAgICAgICAgICAgICAgICBwcmlvciA9IHRoaXMub25zdGF0dXNjaGFuZ2U7XG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChnb29kKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBzZWxmLm9uc3RhdHVzY2hhbmdlID0gZnVuY3Rpb24gKHN0YXR1cykge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHByaW9yKHN0YXR1cyk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHN0YXR1cykge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBnb29kKCk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYub25zdGF0dXNjaGFuZ2UgPSBwcmlvcjtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICB9O1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICAvKiogYXBpOiBnZW5lcmF0ZSBhIG5ldyBnYW1lICovXG4gICAgQ29ubmVjdGlvbi5wcm90b3R5cGUuZ2VuZXJhdGUgPSBmdW5jdGlvbiAoZ3MpIHtcbiAgICAgICAgcmV0dXJuIF9fYXdhaXRlcih0aGlzLCB2b2lkIDAsIHZvaWQgMCwgZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgdmFyIHJlcXVlc3QsIHNlbGY7XG4gICAgICAgICAgICByZXR1cm4gX19nZW5lcmF0b3IodGhpcywgZnVuY3Rpb24gKF9hKSB7XG4gICAgICAgICAgICAgICAgcmVxdWVzdCA9IHsgbWV0aG9kOiBcInJlc2VydmVcIiwgc3BhY2U6IGdzIH07XG4gICAgICAgICAgICAgICAgc2VsZiA9IHRoaXM7XG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChnb29kLCBiYWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuX3NlbmQocmVxdWVzdCwgZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBiYWQocmVzcG9uc2UucmVhc29uKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QocmVzcG9uc2Uua2V5KTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICAgICAgfSldO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH0pO1xuICAgIH07XG4gICAgLyoqIGFwaTogZ2V0IHRoZSBzY2hlbWEgZm9yIHRoZSBnYW1lcHNhY2UgKi9cbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5yZWZsZWN0ID0gZnVuY3Rpb24gKGdzKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciByZXF1ZXN0LCBzZWxmO1xuICAgICAgICAgICAgcmV0dXJuIF9fZ2VuZXJhdG9yKHRoaXMsIGZ1bmN0aW9uIChfYSkge1xuICAgICAgICAgICAgICAgIHJlcXVlc3QgPSB7IG1ldGhvZDogXCJyZWZsZWN0XCIsIHNwYWNlOiBncywga2V5OiAnMCcgfTtcbiAgICAgICAgICAgICAgICBzZWxmID0gdGhpcztcbiAgICAgICAgICAgICAgICByZXR1cm4gWzIgLypyZXR1cm4qLywgbmV3IFByb21pc2UoZnVuY3Rpb24gKGdvb2QsIGJhZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5fc2VuZChyZXF1ZXN0LCBmdW5jdGlvbiAocmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoJ2ZhaWx1cmUnIGluIHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGJhZChyZXNwb25zZS5yZWFzb24pO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZ29vZChyZXNwb25zZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgICAgIH0pXTtcbiAgICAgICAgICAgIH0pO1xuICAgICAgICB9KTtcbiAgICB9O1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLmxvYWRfY29kZSA9IGZ1bmN0aW9uIChncykge1xuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICB2YXIgcmVxdWVzdCwgc2VsZjtcbiAgICAgICAgICAgIHJldHVybiBfX2dlbmVyYXRvcih0aGlzLCBmdW5jdGlvbiAoX2EpIHtcbiAgICAgICAgICAgICAgICByZXF1ZXN0ID0geyBtZXRob2Q6IFwibG9hZF9jb2RlXCIsIHNwYWNlOiBncyB9O1xuICAgICAgICAgICAgICAgIHNlbGYgPSB0aGlzO1xuICAgICAgICAgICAgICAgIHJldHVybiBbMiAvKnJldHVybiovLCBuZXcgUHJvbWlzZShmdW5jdGlvbiAoZ29vZCwgYmFkKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBzZWxmLl9zZW5kKHJlcXVlc3QsIGZ1bmN0aW9uIChyZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmICgnZmFpbHVyZScgaW4gcmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYmFkKHJlc3BvbnNlLnJlYXNvbik7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBnb29kKHJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICAgICAgfSldO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH0pO1xuICAgIH07XG4gICAgQ29ubmVjdGlvbi5wcm90b3R5cGUuc2F2ZV9jb2RlID0gZnVuY3Rpb24gKGdzLCBjb2RlKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciByZXF1ZXN0LCBzZWxmO1xuICAgICAgICAgICAgcmV0dXJuIF9fZ2VuZXJhdG9yKHRoaXMsIGZ1bmN0aW9uIChfYSkge1xuICAgICAgICAgICAgICAgIHJlcXVlc3QgPSB7IG1ldGhvZDogXCJzYXZlX2NvZGVcIiwgc3BhY2U6IGdzLCBjb2RlOiBjb2RlIH07XG4gICAgICAgICAgICAgICAgc2VsZiA9IHRoaXM7XG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChnb29kLCBiYWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuX3NlbmQocmVxdWVzdCwgZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBiYWQocmVzcG9uc2UucmVhc29uKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5kZXBsb3kgPSBmdW5jdGlvbiAoZ3MpIHtcbiAgICAgICAgcmV0dXJuIF9fYXdhaXRlcih0aGlzLCB2b2lkIDAsIHZvaWQgMCwgZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgdmFyIHJlcXVlc3QsIHNlbGY7XG4gICAgICAgICAgICByZXR1cm4gX19nZW5lcmF0b3IodGhpcywgZnVuY3Rpb24gKF9hKSB7XG4gICAgICAgICAgICAgICAgcmVxdWVzdCA9IHsgbWV0aG9kOiBcImRlcGxveVwiLCBzcGFjZTogZ3MgfTtcbiAgICAgICAgICAgICAgICBzZWxmID0gdGhpcztcbiAgICAgICAgICAgICAgICByZXR1cm4gWzIgLypyZXR1cm4qLywgbmV3IFByb21pc2UoZnVuY3Rpb24gKGdvb2QsIGJhZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5fc2VuZChyZXF1ZXN0LCBmdW5jdGlvbiAocmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoJ2ZhaWx1cmUnIGluIHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGJhZChyZXNwb25zZS5yZWFzb24pO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZ29vZChyZXNwb25zZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgICAgIH0pXTtcbiAgICAgICAgICAgIH0pO1xuICAgICAgICB9KTtcbiAgICB9O1xuICAgIC8qKiBhcGk6IGdlbmVyYXRlIGEgbmV3IGdhbWUgKi9cbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5jcmVhdGUgPSBmdW5jdGlvbiAoZ3MsIGlkLCBhcmcpIHtcbiAgICAgICAgcmV0dXJuIF9fYXdhaXRlcih0aGlzLCB2b2lkIDAsIHZvaWQgMCwgZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgdmFyIHJlcXVlc3QsIHNlbGY7XG4gICAgICAgICAgICByZXR1cm4gX19nZW5lcmF0b3IodGhpcywgZnVuY3Rpb24gKF9hKSB7XG4gICAgICAgICAgICAgICAgcmVxdWVzdCA9IHsgbWV0aG9kOiBcImNyZWF0ZVwiLCBzcGFjZTogZ3MsIGtleTogaWQsIGFyZzogYXJnIH07XG4gICAgICAgICAgICAgICAgc2VsZiA9IHRoaXM7XG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChnb29kLCBiYWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuX3NlbmQocmVxdWVzdCwgZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBiYWQocmVzcG9uc2UucmVhc29uKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICAvKiogYXBpOiBjb25uZWN0IHRvIGEgZ2FtZSAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLmNvbm5lY3QgPSBmdW5jdGlvbiAoZ3MsIGlkLCBoYW5kbGVyKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciByZXF1ZXN0LCBzZWxmLCBmaXJzdDtcbiAgICAgICAgICAgIHJldHVybiBfX2dlbmVyYXRvcih0aGlzLCBmdW5jdGlvbiAoX2EpIHtcbiAgICAgICAgICAgICAgICByZXF1ZXN0ID0geyBtZXRob2Q6IFwiY29ubmVjdFwiLCBzcGFjZTogZ3MsIGtleTogaWQgfTtcbiAgICAgICAgICAgICAgICBzZWxmID0gdGhpcztcbiAgICAgICAgICAgICAgICBmaXJzdCA9IHRydWU7XG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChnb29kLCBiYWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuX3NlbmQocmVxdWVzdCwgZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKGZpcnN0KSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGZpcnN0ID0gZmFsc2U7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmICgnZmFpbHVyZScgaW4gcmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGJhZChyZXNwb25zZS5yZWFzb24pO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgaGFuZGxlcihyZXNwb25zZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBnb29kKHRydWUpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBoYW5kbGVyKHJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICAgICAgfSldO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH0pO1xuICAgIH07XG4gICAgLyoqIGFwaTogc2VuZCBhIG1lc3NhZ2UgKi9cbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5zZW5kID0gZnVuY3Rpb24gKGdzLCBpZCwgY2hhbm5lbCwgbXNnLCBoYWNrKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciBzZWxmLCByZXF1ZXN0O1xuICAgICAgICAgICAgcmV0dXJuIF9fZ2VuZXJhdG9yKHRoaXMsIGZ1bmN0aW9uIChfYSkge1xuICAgICAgICAgICAgICAgIHNlbGYgPSB0aGlzO1xuICAgICAgICAgICAgICAgIHJlcXVlc3QgPSB7IG1ldGhvZDogXCJzZW5kXCIsIG1hcmtlcjogc2VsZi5zZXNzaW9uSWQgPSBcIi9cIiArIHNlbGYuc2VuZElkLCBzcGFjZTogZ3MsIGtleTogaWQsIGNoYW5uZWw6IGNoYW5uZWwsIG1lc3NhZ2U6IG1zZyB9O1xuICAgICAgICAgICAgICAgIHNlbGYuc2VuZElkKys7XG4gICAgICAgICAgICAgICAgaWYgKGhhY2spIHtcbiAgICAgICAgICAgICAgICAgICAgaGFjayhyZXF1ZXN0KTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgLy8gVE9ETzogcXVldWUgdGhpcyB1cD8gd2l0aCByZXRyeT9cbiAgICAgICAgICAgICAgICByZXR1cm4gWzIgLypyZXR1cm4qLywgbmV3IFByb21pc2UoZnVuY3Rpb24gKGdvb2QsIGJhZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5fc2VuZChyZXF1ZXN0LCBmdW5jdGlvbiAocmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoJ2ZhaWx1cmUnIGluIHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGJhZChyZXNwb25zZS5yZWFzb24pO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZ29vZChyZXNwb25zZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgICAgIH0pXTtcbiAgICAgICAgICAgIH0pO1xuICAgICAgICB9KTtcbiAgICB9O1xuICAgIC8qKiBhcGk6IGNvbm5lY3QgdHJlZSAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLmNvbm5lY3RUcmVlID0gZnVuY3Rpb24gKGdzLCBpZCwgdHJlZSwgaGFjaykge1xuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICB2YXIga2V5SWQsIHNtO1xuICAgICAgICAgICAgcmV0dXJuIF9fZ2VuZXJhdG9yKHRoaXMsIGZ1bmN0aW9uIChfYSkge1xuICAgICAgICAgICAgICAgIGtleUlkID0gdGhpcy5jb25uZWN0SWQ7XG4gICAgICAgICAgICAgICAgdGhpcy5jb25uZWN0SWQrKztcbiAgICAgICAgICAgICAgICBzbSA9IHtcbiAgICAgICAgICAgICAgICAgICAgcmVxdWVzdDogeyBtZXRob2Q6IFwiY29ubmVjdFwiLCBzcGFjZTogZ3MsIGtleTogaWQgfSxcbiAgICAgICAgICAgICAgICAgICAgZmlyc3Q6IHRydWUsXG4gICAgICAgICAgICAgICAgICAgIGhhbmRsZXI6IGZ1bmN0aW9uIChyKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICB0cmVlLm1lcmdlVXBkYXRlKHIpO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfTtcbiAgICAgICAgICAgICAgICBpZiAoaGFjaykge1xuICAgICAgICAgICAgICAgICAgICBoYWNrKHNtLnJlcXVlc3QpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB0aGlzLm9ucmVjb25uZWN0LnNldChrZXlJZCwgc20pO1xuICAgICAgICAgICAgICAgIHJldHVybiBbMiAvKnJldHVybiovLCB0aGlzLl9leGVjdXRlKHNtKV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5fcmVjb25uZWN0ID0gZnVuY3Rpb24gKCkge1xuICAgICAgICB2YXIgc2VsZiA9IHRoaXM7XG4gICAgICAgIHRoaXMub25yZWNvbm5lY3QuZm9yRWFjaChmdW5jdGlvbiAoc20sIGlkKSB7XG4gICAgICAgICAgICBzZWxmLl9leGVjdXRlKHNtKTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5fZXhlY3V0ZSA9IGZ1bmN0aW9uIChzbSkge1xuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICB2YXIgc2VsZjtcbiAgICAgICAgICAgIHJldHVybiBfX2dlbmVyYXRvcih0aGlzLCBmdW5jdGlvbiAoX2EpIHtcbiAgICAgICAgICAgICAgICBzZWxmID0gdGhpcztcbiAgICAgICAgICAgICAgICByZXR1cm4gWzIgLypyZXR1cm4qLywgbmV3IFByb21pc2UoZnVuY3Rpb24gKGdvb2QsIGJhZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5fc2VuZChzbS5yZXF1ZXN0LCBmdW5jdGlvbiAocmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoc20uZmlyc3QpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgc20uZmlyc3QgPSBmYWxzZTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYmFkKHJlc3BvbnNlLnJlYXNvbik7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBzbS5oYW5kbGVyKHJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QodHJ1ZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHNtLmhhbmRsZXIocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICByZXR1cm4gQ29ubmVjdGlvbjtcbn0oKSk7XG5leHBvcnRzLkNvbm5lY3Rpb24gPSBDb25uZWN0aW9uO1xuIiwiXCJ1c2Ugc3RyaWN0XCI7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJfX2VzTW9kdWxlXCIsIHsgdmFsdWU6IHRydWUgfSk7XG5leHBvcnRzLlRyZWUgPSB2b2lkIDA7XG52YXIgVHJlZSA9IC8qKiBAY2xhc3MgKi8gKGZ1bmN0aW9uICgpIHtcbiAgICBmdW5jdGlvbiBUcmVlKCkge1xuICAgICAgICB0aGlzLnRyZWUgPSB7fTtcbiAgICAgICAgdGhpcy5kaXNwYXRjaCA9IHt9O1xuICAgICAgICB0aGlzLmRpc3BhdGNoX2NvdW50ID0gMDtcbiAgICAgICAgdGhpcy5xdWV1ZSA9IFtdO1xuICAgICAgICB0aGlzLm9uZGVjaWRlID0gZnVuY3Rpb24gKG91dHN0YW5kaW5nKSB7IH07XG4gICAgfVxuICAgIC8vIHJlY3Vyc2l2ZWx5IGFwcGVuZCBhIGNoYW5nZVxuICAgIC8vIGRpc3BhdGNoIGlzIHRoZSBzdHJ1Y3R1cmFsIG9iamVjdCBtaXJyb3JpbmcgdGhlIHRyZWVcbiAgICAvLyBjYWxsYmFjayBpcyB0aGUgZnVuY3Rpb24vb2JqZWN0IGNhbGxiYWNrIHRyZWVcbiAgICAvLyBpbnNlcnRfb3JkZXIgaXMgdGhlIG9yZGVyIHRvIGZpcmUgZXZlbnRzXG4gICAgVHJlZS5wcm90b3R5cGUuX19yZWNBcHBlbmRDaGFuZ2UgPSBmdW5jdGlvbiAoZGlzcGF0Y2gsIGNhbGxiYWNrLCBpbnNlcnRfb3JkZXIpIHtcbiAgICAgICAgLy8gdGhlIGNhbGxiYWNrIGlzIGFuIG9iamVjdFxuICAgICAgICBpZiAodHlwZW9mIChjYWxsYmFjaykgPT0gJ29iamVjdCcpIHtcbiAgICAgICAgICAgIC8vIHdlIGZvciBlYWNoIGl0ZW0gaW4gdGhlIGNhbGxiYWNrXG4gICAgICAgICAgICBmb3IgKHZhciBrZXkgaW4gY2FsbGJhY2spIHtcbiAgICAgICAgICAgICAgICAvLyBtYWtlIHN1cmUgaXQgZXhpc3RzXG4gICAgICAgICAgICAgICAgaWYgKCEoa2V5IGluIGRpc3BhdGNoKSkge1xuICAgICAgICAgICAgICAgICAgICBkaXNwYXRjaFtrZXldID0ge307XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIC8vIHJlY3Vyc2UgaW50byB0aGF0IGtleVxuICAgICAgICAgICAgICAgIHRoaXMuX19yZWNBcHBlbmRDaGFuZ2UoZGlzcGF0Y2hba2V5XSwgY2FsbGJhY2tba2V5XSwgaW5zZXJ0X29yZGVyKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgICAgICBlbHNlIGlmICh0eXBlb2YgKGNhbGxiYWNrKSA9PSAnZnVuY3Rpb24nKSB7XG4gICAgICAgICAgICAvLyB3ZSBoYXZlIGEgZnVuY3Rpb24sIHNvIGxldCdzIGFzc29jaWF0ZSBpdCB0byB0aGUgbm9kZVxuICAgICAgICAgICAgaWYgKCEoJ0BlJyBpbiBkaXNwYXRjaCkpIHtcbiAgICAgICAgICAgICAgICBkaXNwYXRjaFsnQGUnXSA9IFtdO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZGlzcGF0Y2hbJ0BlJ10ucHVzaCh7IGNiOiBjYWxsYmFjaywgb3JkZXI6IGluc2VydF9vcmRlciB9KTtcbiAgICAgICAgfVxuICAgIH07XG4gICAgVHJlZS5wcm90b3R5cGUub25UcmVlQ2hhbmdlID0gZnVuY3Rpb24gKGNhbGxiYWNrKSB7XG4gICAgICAgIHRoaXMuX19yZWNBcHBlbmRDaGFuZ2UodGhpcy5kaXNwYXRjaCwgY2FsbGJhY2ssIHRoaXMuZGlzcGF0Y2hfY291bnQpO1xuICAgICAgICB0aGlzLmRpc3BhdGNoX2NvdW50Kys7XG4gICAgfTtcbiAgICAvLyB0aGUgbWFpbiBmdW5jdGlvblxuICAgIFRyZWUucHJvdG90eXBlLm1lcmdlVXBkYXRlID0gZnVuY3Rpb24gKGRpZmYpIHtcbiAgICAgICAgaWYgKCdkYXRhJyBpbiBkaWZmKSB7XG4gICAgICAgICAgICAvLyB3ZSBtZXJnZSB0aGUgdHJlZSB3aXRoIHRoZSBkYXRhIHdpdGhpbiBkaXNwYXRjaFxuICAgICAgICAgICAgdGhpcy5fX3JlY01lcmdlQW5kRGlzcGF0Y2godGhpcy50cmVlLCB0aGlzLmRpc3BhdGNoLCBkaWZmLmRhdGEpO1xuICAgICAgICB9XG4gICAgICAgIGlmICgnb3V0c3RhbmRpbmcnIGluIGRpZmYpIHtcbiAgICAgICAgICAgIHRoaXMub25kZWNpZGUoZGlmZi5vdXRzdGFuZGluZyk7XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5fX2RyYWluKCk7XG4gICAgfTtcbiAgICBUcmVlLnByb3RvdHlwZS5fX3JlY0RlbGV0ZUFuZERpc3BhdGNoID0gZnVuY3Rpb24gKHRyZWUsIGRpc3BhdGNoKSB7XG4gICAgICAgIGZvciAodmFyIGtleSBpbiB0cmVlKSB7XG4gICAgICAgICAgICB2YXIgb2xkID0gdHJlZVtrZXldO1xuICAgICAgICAgICAgaWYgKEFycmF5LmlzQXJyYXkob2xkKSkge1xuICAgICAgICAgICAgICAgIC8vIG5lZWQgdG8gZmlyZSB0aGUgREVMRVRFXG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICBpZiAoa2V5IGluIGRpc3BhdGNoKSB7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuX19yZWNEZWxldGVBbmREaXNwYXRjaChvbGQsIGRpc3BhdGNoW2tleV0pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHZhciBkZWxldGVDaGlsZEtleSA9ICctJyArIGtleTtcbiAgICAgICAgICAgIGlmIChkaXNwYXRjaCAhPSBudWxsICYmIGRlbGV0ZUNoaWxkS2V5IGluIGRpc3BhdGNoKSB7XG4gICAgICAgICAgICAgICAgdGhpcy5fX2ZpcmUoZGlzcGF0Y2hbZGVsZXRlQ2hpbGRLZXldLCB7IGtleToga2V5LCBiZWZvcmU6IG9sZCwgdmFsdWU6IG51bGwgfSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9O1xuICAgIFRyZWUucHJvdG90eXBlLl9fcmVjTWVyZ2VBbmREaXNwYXRjaCA9IGZ1bmN0aW9uICh0cmVlLCBkaXNwYXRjaCwgZGlmZikge1xuICAgICAgICAvLyB0aGUgZGlmZiBpcyBhbiBvYmplY3QsIHNvIGxldCdzIHdhbGsgaXRzIGtleXNcbiAgICAgICAgZm9yICh2YXIga2V5IGluIGRpZmYpIHtcbiAgICAgICAgICAgIHZhciBjaGlsZCA9IGRpZmZba2V5XTtcbiAgICAgICAgICAgIGlmIChjaGlsZCA9PT0gbnVsbCkge1xuICAgICAgICAgICAgICAgIHZhciBkZWxldGVDaGlsZEtleSA9ICctJyArIGtleTtcbiAgICAgICAgICAgICAgICB2YXIgb2xkXzEgPSB0cmVlW2tleV07XG4gICAgICAgICAgICAgICAgaWYgKGRpc3BhdGNoICE9IG51bGwgJiYgZGVsZXRlQ2hpbGRLZXkgaW4gZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fX2ZpcmUoZGlzcGF0Y2hbZGVsZXRlQ2hpbGRLZXldLCB7IGtleToga2V5LCBiZWZvcmU6IG9sZF8xLCB2YWx1ZTogbnVsbCB9KTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgaWYgKEFycmF5LmlzQXJyYXkob2xkXzEpKSB7XG4gICAgICAgICAgICAgICAgICAgIC8vIGFsc28gZGVsZXRlXG4gICAgICAgICAgICAgICAgICAgIHZhciBlbGVtZW50S2V5ID0gXCIjXCIgKyBrZXk7XG4gICAgICAgICAgICAgICAgICAgIGlmIChlbGVtZW50S2V5IGluIHRyZWUgJiYgZWxlbWVudEtleSBpbiBkaXNwYXRjaCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgdGhpcy5fX3JlY0RlbGV0ZUFuZERpc3BhdGNoKHRyZWVbZWxlbWVudEtleV0sIGRpc3BhdGNoW2VsZW1lbnRLZXldKTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBkZWxldGUgdHJlZVtcIiNcIiArIGtleV07XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGlmIChrZXkgaW4gdHJlZSAmJiBrZXkgaW4gZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fX3JlY0RlbGV0ZUFuZERpc3BhdGNoKHRyZWVba2V5XSwgZGlzcGF0Y2hba2V5XSk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGRlbGV0ZSB0cmVlW2tleV07XG4gICAgICAgICAgICAgICAgaWYgKGRpc3BhdGNoICE9IG51bGwgJiYga2V5IGluIGRpc3BhdGNoKSB7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuX19maXJlKGRpc3BhdGNoW2tleV0sIHsga2V5OiBrZXksIGJlZm9yZTogb2xkXzEsIHZhbHVlOiBudWxsIH0pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBjb250aW51ZTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIC8vIGlmIHRoZSBjaGlsZCBpcyBhbiBvYmplY3QsIHRoZW4uLlxuICAgICAgICAgICAgdmFyIGZpcmVOZXcgPSAhKGtleSBpbiB0cmVlKTtcbiAgICAgICAgICAgIGlmICh0eXBlb2YgKGNoaWxkKSA9PSAnb2JqZWN0Jykge1xuICAgICAgICAgICAgICAgIC8vIHRoZSBjaGlsZCBpcyBlaXRoZXIgYW4gQVJSQVkgb3IgYSBPQkpFQ1RcbiAgICAgICAgICAgICAgICB2YXIgY2hpbGRJc0FycmF5ID0gJ0BvJyBpbiBjaGlsZCB8fCAnQHMnIGluIGNoaWxkO1xuICAgICAgICAgICAgICAgIC8vIHRoZSBwcmlvciB2ZXJzaW9uIGRvZXNuJ3QgZXhpc3QsIHNvIHdlIGNyZWF0ZSB0aGUgZW1wdHkgbm9kZSBzbyB0aGF0IGl0IGRvZXMgZXhpc3RcbiAgICAgICAgICAgICAgICBpZiAoIShrZXkgaW4gdHJlZSkpIHtcbiAgICAgICAgICAgICAgICAgICAgaWYgKGNoaWxkSXNBcnJheSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgdHJlZVtrZXldID0gW107XG4gICAgICAgICAgICAgICAgICAgICAgICB0cmVlW1wiI1wiICsga2V5XSA9IHt9O1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgdHJlZVtrZXldID0ge307XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgLy8gbm93LCB3ZSBjaGVjayB0byBzZWUgaWYgdGhlIHByaW9yIHN0YXRlIGluZmx1ZW5jZXMgd2hldGhlciBvciBub3QgdGhlIGRpZmYgaXMgYW4gYXJyYXlcbiAgICAgICAgICAgICAgICBjaGlsZElzQXJyYXkgPSBBcnJheS5pc0FycmF5KHRyZWVba2V5XSkgfHwgY2hpbGRJc0FycmF5O1xuICAgICAgICAgICAgICAgIGlmIChjaGlsZElzQXJyYXkpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fX3JlY01lcmdlQW5kRGlzcGF0Y2hBcnJheSh0cmVlW2tleV0sIChkaXNwYXRjaCAhPSBudWxsICYmIGtleSBpbiBkaXNwYXRjaCkgPyBkaXNwYXRjaFtrZXldIDogbnVsbCwgdHJlZVtcIiNcIiArIGtleV0sIGNoaWxkKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuX19yZWNNZXJnZUFuZERpc3BhdGNoKHRyZWVba2V5XSwgKGRpc3BhdGNoICE9IG51bGwgJiYga2V5IGluIGRpc3BhdGNoKSA/IGRpc3BhdGNoW2tleV0gOiBudWxsLCBjaGlsZCk7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgdmFyIG9sZCA9IChrZXkgaW4gdHJlZSkgPyB0cmVlW2tleV0gOiBudWxsO1xuICAgICAgICAgICAgICAgIHRyZWVba2V5XSA9IGNoaWxkO1xuICAgICAgICAgICAgICAgIGlmIChkaXNwYXRjaCAhPSBudWxsICYmIGtleSBpbiBkaXNwYXRjaCkge1xuICAgICAgICAgICAgICAgICAgICB0aGlzLl9fZmlyZShkaXNwYXRjaFtrZXldLCB7IGtleToga2V5LCBiZWZvcmU6IG9sZCwgdmFsdWU6IGNoaWxkIH0pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmIChmaXJlTmV3KSB7IC8vIGZpcmUgbmV3IGRhdGEgZXZlbnRcbiAgICAgICAgICAgICAgICB2YXIgbmV3Q2hpbGRLZXkgPSAnKycgKyBrZXk7XG4gICAgICAgICAgICAgICAgaWYgKGRpc3BhdGNoICE9IG51bGwgJiYgbmV3Q2hpbGRLZXkgaW4gZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fX2ZpcmUoZGlzcGF0Y2hbbmV3Q2hpbGRLZXldLCB7IGtleToga2V5LCB2YWx1ZTogdHJlZVtrZXldIH0pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmIChkaXNwYXRjaCAhPSBudWxsICYmICdAZScgaW4gZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgICAgICB0aGlzLl9fZmlyZShkaXNwYXRjaCwgeyB2YWx1ZTogdHJlZSB9KTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgIH07XG4gICAgVHJlZS5wcm90b3R5cGUuX19yZWNNZXJnZUFuZERpc3BhdGNoQXJyYXkgPSBmdW5jdGlvbiAocHJpb3IsIGRpc3BhdGNoLCB0cmVlLCBkaWZmKSB7XG4gICAgICAgIC8vIFRPRE86IG5ldyBpdGVtLi4uIGV0Y1xuICAgICAgICB2YXIgb3JkZXJpbmcgPSBudWxsO1xuICAgICAgICB2YXIgcmVzaXplID0gbnVsbDtcbiAgICAgICAgZm9yICh2YXIga2V5IGluIGRpZmYpIHtcbiAgICAgICAgICAgIGlmIChrZXkgPT0gXCJAb1wiKSB7XG4gICAgICAgICAgICAgICAgb3JkZXJpbmcgPSBkaWZmW2tleV07XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIGlmIChrZXkgPT0gXCJAc1wiKSB7XG4gICAgICAgICAgICAgICAgcmVzaXplID0gZGlmZltrZXldO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgaWYgKGRpZmZba2V5XSA9PSBudWxsKSB7XG4gICAgICAgICAgICAgICAgICAgIGlmIChkaXNwYXRjaCAmJiAnLScgaW4gZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHRoaXMuX19maXJlKGRpc3BhdGNoWyctJ10sIHsga2V5OiBrZXksIGJlZm9yZTogdHJlZVtrZXldLCB2YWx1ZTogbnVsbCB9KTtcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBkZWxldGUgdHJlZVtrZXldO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICAgICAgdmFyIGZpcmVOZXcgPSBmYWxzZTtcbiAgICAgICAgICAgICAgICAgICAgaWYgKCEodHJlZSAhPSBudWxsICYmIGtleSBpbiB0cmVlKSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKGRpc3BhdGNoICYmICcrJyBpbiBkaXNwYXRjaCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGZpcmVOZXcgPSB0cnVlO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgdHJlZVtrZXldID0ge307XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fX3JlY01lcmdlQW5kRGlzcGF0Y2godHJlZVtrZXldLCAoZGlzcGF0Y2ggIT0gbnVsbCAmJiAnIycgaW4gZGlzcGF0Y2gpID8gZGlzcGF0Y2hbJyMnXSA6IG51bGwsIGRpZmZba2V5XSk7XG4gICAgICAgICAgICAgICAgICAgIGlmIChmaXJlTmV3KSB7XG4gICAgICAgICAgICAgICAgICAgICAgICB0aGlzLl9fZmlyZShkaXNwYXRjaFsnKyddLCB7IGtleToga2V5LCBiZWZvcmU6IG51bGwsIHZhbHVlOiB0cmVlW2tleV0gfSk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgdmFyIGNoYW5nZSA9IHsgYmVmb3JlOiBwcmlvciwgdmFsdWU6IHByaW9yIH07XG4gICAgICAgIGlmIChyZXNpemUgIT09IG51bGwpIHtcbiAgICAgICAgICAgIC8vIFNlZSBETGlzdCwgYnV0IHRoZSBpZGVhIGlzIHRoYXQgSSBuZWVkIHRvIHRyaW0gdGhlIGxpc3QgYmVjYXVzZSB0aGUgYWJvdmUgdG9vbCBjYXJlIG9mIG51bGxzXG4gICAgICAgICAgICAvLyB0aGlzIGlzIGZvciBsaXN0IG9mIHZhbHVlcyB3aGVyZSB3ZSBzeW5jaHJvbml6ZSBhIGxpc3Qgb2YgY29uc3RhbnRzXG4gICAgICAgICAgICBjaGFuZ2UuYmVmb3JlID0gW107XG4gICAgICAgICAgICBmb3IgKHZhciBrID0gMDsgayA8IHByaW9yLmxlbmd0aDsgaysrKSB7XG4gICAgICAgICAgICAgICAgY2hhbmdlLmJlZm9yZS5wdXNoKHByaW9yW2tdKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIHByaW9yLmxlbmd0aCA9IHJlc2l6ZTtcbiAgICAgICAgICAgIC8vIFRPRE86IHZhbGlkYXRlIHRoaXNcbiAgICAgICAgfVxuICAgICAgICBpZiAob3JkZXJpbmcgIT09IG51bGwpIHtcbiAgICAgICAgICAgIHZhciBhZnRlciA9IFtdO1xuICAgICAgICAgICAgY2hhbmdlLmJlZm9yZSA9IFtdO1xuICAgICAgICAgICAgdmFyIGltcGxpY2l0RGVsZXRlID0gZGlzcGF0Y2ggPyAnLScgaW4gZGlzcGF0Y2ggOiBmYWxzZTtcbiAgICAgICAgICAgIGZvciAodmFyIGsgPSAwOyBrIDwgcHJpb3IubGVuZ3RoOyBrKyspIHtcbiAgICAgICAgICAgICAgICBjaGFuZ2UuYmVmb3JlLnB1c2gocHJpb3Jba10pO1xuICAgICAgICAgICAgICAgIGlmIChpbXBsaWNpdERlbGV0ZSkge1xuICAgICAgICAgICAgICAgICAgICBwcmlvcltrXS5fX2tpbGwgPSB0cnVlO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGZvciAodmFyIGsgPSAwOyBrIDwgb3JkZXJpbmcubGVuZ3RoOyBrKyspIHtcbiAgICAgICAgICAgICAgICB2YXIgaW5zdHIgPSBvcmRlcmluZ1trXTtcbiAgICAgICAgICAgICAgICB2YXIgdHlwZV9pbnN0ciA9IHR5cGVvZiAoaW5zdHIpO1xuICAgICAgICAgICAgICAgIGlmICh0eXBlX2luc3RyID09IFwic3RyaW5nXCIgfHwgdHlwZV9pbnN0ciA9PSBcIm51bWJlclwiKSB7XG4gICAgICAgICAgICAgICAgICAgIGFmdGVyLnB1c2godHJlZVtpbnN0cl0pO1xuICAgICAgICAgICAgICAgICAgICBpZiAoaW1wbGljaXREZWxldGUpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHRyZWVbaW5zdHJdLl9fa2lsbCA9IGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICB2YXIgc3RhcnQgPSBpbnN0clswXTtcbiAgICAgICAgICAgICAgICAgICAgdmFyIGVuZCA9IGluc3RyWzFdO1xuICAgICAgICAgICAgICAgICAgICBmb3IgKHZhciBqID0gc3RhcnQ7IGogPD0gZW5kOyBqKyspIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIGlmIChpbXBsaWNpdERlbGV0ZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIHByaW9yW2pdLl9fa2lsbCA9IGZhbHNlO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgYWZ0ZXIucHVzaChwcmlvcltqXSk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBpZiAoaW1wbGljaXREZWxldGUpIHtcbiAgICAgICAgICAgICAgICBmb3IgKGtleSBpbiB0cmVlKSB7XG4gICAgICAgICAgICAgICAgICAgIGlmICh0cmVlW2tleV0uX19raWxsKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAoa2V5IGluIGRpc3BhdGNoKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgdGhpcy5fX3JlY0RlbGV0ZUFuZERpc3BhdGNoKHRyZWVba2V5XSwgZGlzcGF0Y2hba2V5XSk7XG4gICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICB0aGlzLl9fZmlyZShkaXNwYXRjaFsnLSddLCB7IGtleToga2V5LCBiZWZvcmU6IHRyZWVba2V5XSwgdmFsdWU6IG51bGwgfSk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgZGVsZXRlIHRyZWVba2V5XS5fX2tpbGw7XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgcHJpb3IubGVuZ3RoID0gYWZ0ZXIubGVuZ3RoO1xuICAgICAgICAgICAgZm9yICh2YXIgayA9IDA7IGsgPCBhZnRlci5sZW5ndGg7IGsrKykge1xuICAgICAgICAgICAgICAgIHByaW9yW2tdID0gYWZ0ZXJba107XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5fX2ZpcmUoZGlzcGF0Y2gsIGNoYW5nZSk7XG4gICAgfTtcbiAgICBUcmVlLnByb3RvdHlwZS5fX2ZpcmUgPSBmdW5jdGlvbiAoZGlzcGF0Y2gsIGNoYW5nZSkge1xuICAgICAgICBpZiAoZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgIGlmICgnQGUnIGluIGRpc3BhdGNoKSB7XG4gICAgICAgICAgICAgICAgdmFyIGQgPSBkaXNwYXRjaFsnQGUnXTtcbiAgICAgICAgICAgICAgICB2YXIgbnVsbHMgPSAwO1xuICAgICAgICAgICAgICAgIGZvciAodmFyIGsgPSAwOyBrIDwgZC5sZW5ndGg7IGsrKykge1xuICAgICAgICAgICAgICAgICAgICB2YXIgZXZ0ID0gZFtrXTtcbiAgICAgICAgICAgICAgICAgICAgaWYgKGV2dCAhPT0gbnVsbCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgdGhpcy5xdWV1ZS5wdXNoKHsgY2I6IGV2dC5jYiwgb3JkZXI6IGV2dC5vcmRlciwgY2hhbmdlOiBjaGFuZ2UsIGRpc3BhdGNoX2xpc3Q6IGQsIGluZGV4OiBrIH0pO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgbnVsbHMrKztcbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBpZiAobnVsbHMgPiAwKSB7XG4gICAgICAgICAgICAgICAgICAgIHZhciBueHQgPSBbXTtcbiAgICAgICAgICAgICAgICAgICAgZm9yICh2YXIgayA9IDA7IGsgPCBkLmxlbmd0aDsgaysrKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBpZiAoZXZ0ICE9PSBudWxsKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgbnh0LnB1c2goZXZ0KTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICBkaXNwYXRjaFsnQGUnXSA9IG54dDtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9O1xuICAgIFRyZWUucHJvdG90eXBlLl9fZHJhaW4gPSBmdW5jdGlvbiAoKSB7XG4gICAgICAgIHRoaXMucXVldWUuc29ydChmdW5jdGlvbiAoYSwgYikgeyByZXR1cm4gYS5vcmRlciAtIGIub3JkZXI7IH0pO1xuICAgICAgICBmb3IgKHZhciBrID0gMDsgayA8IHRoaXMucXVldWUubGVuZ3RoOyBrKyspIHtcbiAgICAgICAgICAgIHZhciBpdGVtID0gdGhpcy5xdWV1ZVtrXTtcbiAgICAgICAgICAgIGlmIChpdGVtLmNiKGl0ZW0uY2hhbmdlKSA9PT0gJ2RlbGV0ZScpIHtcbiAgICAgICAgICAgICAgICBpdGVtLmRpc3BhdGNoX2xpc3RbaXRlbS5pbmRleF0gPSBudWxsO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIHRoaXMucXVldWUgPSBbXTtcbiAgICB9O1xuICAgIHJldHVybiBUcmVlO1xufSgpKTtcbmV4cG9ydHMuVHJlZSA9IFRyZWU7XG4iXSwic291cmNlUm9vdCI6IiJ9
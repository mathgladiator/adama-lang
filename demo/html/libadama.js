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
        this.onstatuschange = function (status) { };
        this.onauthneeded = function (tryagain) { };
        this.scheduled = false;
        this.callbacks = new Map();
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
                                    tree.mergeUpdate(response);
                                    good(true);
                                }
                            }
                            else {
                                tree.mergeUpdate(response);
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
                    delete tree[key];
                }
                else {
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
                }
                else if (typeof (instr) == "object") {
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
                var change = { before: tree[key], after: child };
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
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly9BZGFtYS93ZWJwYWNrL3VuaXZlcnNhbE1vZHVsZURlZmluaXRpb24iLCJ3ZWJwYWNrOi8vQWRhbWEvd2VicGFjay9ib290c3RyYXAiLCJ3ZWJwYWNrOi8vQWRhbWEvLi9ub2RlX21vZHVsZXMvaXNvbW9ycGhpYy13cy9icm93c2VyLmpzIiwid2VicGFjazovL0FkYW1hLyh3ZWJwYWNrKS9idWlsZGluL2dsb2JhbC5qcyIsIndlYnBhY2s6Ly9BZGFtYS8uL3NyYy9pbmRleC50cyIsIndlYnBhY2s6Ly9BZGFtYS8uL3NyYy90cmVlLnRzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBLENBQUM7QUFDRCxPO1FDVkE7UUFDQTs7UUFFQTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBOztRQUVBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7OztRQUdBO1FBQ0E7O1FBRUE7UUFDQTs7UUFFQTtRQUNBO1FBQ0E7UUFDQSwwQ0FBMEMsZ0NBQWdDO1FBQzFFO1FBQ0E7O1FBRUE7UUFDQTtRQUNBO1FBQ0Esd0RBQXdELGtCQUFrQjtRQUMxRTtRQUNBLGlEQUFpRCxjQUFjO1FBQy9EOztRQUVBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQTtRQUNBO1FBQ0E7UUFDQSx5Q0FBeUMsaUNBQWlDO1FBQzFFLGdIQUFnSCxtQkFBbUIsRUFBRTtRQUNySTtRQUNBOztRQUVBO1FBQ0E7UUFDQTtRQUNBLDJCQUEyQiwwQkFBMEIsRUFBRTtRQUN2RCxpQ0FBaUMsZUFBZTtRQUNoRDtRQUNBO1FBQ0E7O1FBRUE7UUFDQSxzREFBc0QsK0RBQStEOztRQUVySDtRQUNBOzs7UUFHQTtRQUNBOzs7Ozs7Ozs7Ozs7QUNsRkE7O0FBRUE7O0FBRUE7QUFDQTtBQUNBLENBQUM7QUFDRDtBQUNBLENB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QUFDRCIsImZpbGUiOiJsaWJhZGFtYS5qcyIsInNvdXJjZXNDb250ZW50IjpbIihmdW5jdGlvbiB3ZWJwYWNrVW5pdmVyc2FsTW9kdWxlRGVmaW5pdGlvbihyb290LCBmYWN0b3J5KSB7XG5cdGlmKHR5cGVvZiBleHBvcnRzID09PSAnb2JqZWN0JyAmJiB0eXBlb2YgbW9kdWxlID09PSAnb2JqZWN0Jylcblx0XHRtb2R1bGUuZXhwb3J0cyA9IGZhY3RvcnkoKTtcblx0ZWxzZSBpZih0eXBlb2YgZGVmaW5lID09PSAnZnVuY3Rpb24nICYmIGRlZmluZS5hbWQpXG5cdFx0ZGVmaW5lKFtdLCBmYWN0b3J5KTtcblx0ZWxzZSBpZih0eXBlb2YgZXhwb3J0cyA9PT0gJ29iamVjdCcpXG5cdFx0ZXhwb3J0c1tcIkFkYW1hXCJdID0gZmFjdG9yeSgpO1xuXHRlbHNlXG5cdFx0cm9vdFtcIkFkYW1hXCJdID0gZmFjdG9yeSgpO1xufSkodGhpcywgZnVuY3Rpb24oKSB7XG5yZXR1cm4gIiwiIFx0Ly8gVGhlIG1vZHVsZSBjYWNoZVxuIFx0dmFyIGluc3RhbGxlZE1vZHVsZXMgPSB7fTtcblxuIFx0Ly8gVGhlIHJlcXVpcmUgZnVuY3Rpb25cbiBcdGZ1bmN0aW9uIF9fd2VicGFja19yZXF1aXJlX18obW9kdWxlSWQpIHtcblxuIFx0XHQvLyBDaGVjayBpZiBtb2R1bGUgaXMgaW4gY2FjaGVcbiBcdFx0aWYoaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0pIHtcbiBcdFx0XHRyZXR1cm4gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0uZXhwb3J0cztcbiBcdFx0fVxuIFx0XHQvLyBDcmVhdGUgYSBuZXcgbW9kdWxlIChhbmQgcHV0IGl0IGludG8gdGhlIGNhY2hlKVxuIFx0XHR2YXIgbW9kdWxlID0gaW5zdGFsbGVkTW9kdWxlc1ttb2R1bGVJZF0gPSB7XG4gXHRcdFx0aTogbW9kdWxlSWQsXG4gXHRcdFx0bDogZmFsc2UsXG4gXHRcdFx0ZXhwb3J0czoge31cbiBcdFx0fTtcblxuIFx0XHQvLyBFeGVjdXRlIHRoZSBtb2R1bGUgZnVuY3Rpb25cbiBcdFx0bW9kdWxlc1ttb2R1bGVJZF0uY2FsbChtb2R1bGUuZXhwb3J0cywgbW9kdWxlLCBtb2R1bGUuZXhwb3J0cywgX193ZWJwYWNrX3JlcXVpcmVfXyk7XG5cbiBcdFx0Ly8gRmxhZyB0aGUgbW9kdWxlIGFzIGxvYWRlZFxuIFx0XHRtb2R1bGUubCA9IHRydWU7XG5cbiBcdFx0Ly8gUmV0dXJuIHRoZSBleHBvcnRzIG9mIHRoZSBtb2R1bGVcbiBcdFx0cmV0dXJuIG1vZHVsZS5leHBvcnRzO1xuIFx0fVxuXG5cbiBcdC8vIGV4cG9zZSB0aGUgbW9kdWxlcyBvYmplY3QgKF9fd2VicGFja19tb2R1bGVzX18pXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm0gPSBtb2R1bGVzO1xuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZSBjYWNoZVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5jID0gaW5zdGFsbGVkTW9kdWxlcztcblxuIFx0Ly8gZGVmaW5lIGdldHRlciBmdW5jdGlvbiBmb3IgaGFybW9ueSBleHBvcnRzXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLmQgPSBmdW5jdGlvbihleHBvcnRzLCBuYW1lLCBnZXR0ZXIpIHtcbiBcdFx0aWYoIV9fd2VicGFja19yZXF1aXJlX18ubyhleHBvcnRzLCBuYW1lKSkge1xuIFx0XHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBuYW1lLCB7IGVudW1lcmFibGU6IHRydWUsIGdldDogZ2V0dGVyIH0pO1xuIFx0XHR9XG4gXHR9O1xuXG4gXHQvLyBkZWZpbmUgX19lc01vZHVsZSBvbiBleHBvcnRzXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnIgPSBmdW5jdGlvbihleHBvcnRzKSB7XG4gXHRcdGlmKHR5cGVvZiBTeW1ib2wgIT09ICd1bmRlZmluZWQnICYmIFN5bWJvbC50b1N0cmluZ1RhZykge1xuIFx0XHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCBTeW1ib2wudG9TdHJpbmdUYWcsIHsgdmFsdWU6ICdNb2R1bGUnIH0pO1xuIFx0XHR9XG4gXHRcdE9iamVjdC5kZWZpbmVQcm9wZXJ0eShleHBvcnRzLCAnX19lc01vZHVsZScsIHsgdmFsdWU6IHRydWUgfSk7XG4gXHR9O1xuXG4gXHQvLyBjcmVhdGUgYSBmYWtlIG5hbWVzcGFjZSBvYmplY3RcbiBcdC8vIG1vZGUgJiAxOiB2YWx1ZSBpcyBhIG1vZHVsZSBpZCwgcmVxdWlyZSBpdFxuIFx0Ly8gbW9kZSAmIDI6IG1lcmdlIGFsbCBwcm9wZXJ0aWVzIG9mIHZhbHVlIGludG8gdGhlIG5zXG4gXHQvLyBtb2RlICYgNDogcmV0dXJuIHZhbHVlIHdoZW4gYWxyZWFkeSBucyBvYmplY3RcbiBcdC8vIG1vZGUgJiA4fDE6IGJlaGF2ZSBsaWtlIHJlcXVpcmVcbiBcdF9fd2VicGFja19yZXF1aXJlX18udCA9IGZ1bmN0aW9uKHZhbHVlLCBtb2RlKSB7XG4gXHRcdGlmKG1vZGUgJiAxKSB2YWx1ZSA9IF9fd2VicGFja19yZXF1aXJlX18odmFsdWUpO1xuIFx0XHRpZihtb2RlICYgOCkgcmV0dXJuIHZhbHVlO1xuIFx0XHRpZigobW9kZSAmIDQpICYmIHR5cGVvZiB2YWx1ZSA9PT0gJ29iamVjdCcgJiYgdmFsdWUgJiYgdmFsdWUuX19lc01vZHVsZSkgcmV0dXJuIHZhbHVlO1xuIFx0XHR2YXIgbnMgPSBPYmplY3QuY3JlYXRlKG51bGwpO1xuIFx0XHRfX3dlYnBhY2tfcmVxdWlyZV9fLnIobnMpO1xuIFx0XHRPYmplY3QuZGVmaW5lUHJvcGVydHkobnMsICdkZWZhdWx0JywgeyBlbnVtZXJhYmxlOiB0cnVlLCB2YWx1ZTogdmFsdWUgfSk7XG4gXHRcdGlmKG1vZGUgJiAyICYmIHR5cGVvZiB2YWx1ZSAhPSAnc3RyaW5nJykgZm9yKHZhciBrZXkgaW4gdmFsdWUpIF9fd2VicGFja19yZXF1aXJlX18uZChucywga2V5LCBmdW5jdGlvbihrZXkpIHsgcmV0dXJuIHZhbHVlW2tleV07IH0uYmluZChudWxsLCBrZXkpKTtcbiBcdFx0cmV0dXJuIG5zO1xuIFx0fTtcblxuIFx0Ly8gZ2V0RGVmYXVsdEV4cG9ydCBmdW5jdGlvbiBmb3IgY29tcGF0aWJpbGl0eSB3aXRoIG5vbi1oYXJtb255IG1vZHVsZXNcbiBcdF9fd2VicGFja19yZXF1aXJlX18ubiA9IGZ1bmN0aW9uKG1vZHVsZSkge1xuIFx0XHR2YXIgZ2V0dGVyID0gbW9kdWxlICYmIG1vZHVsZS5fX2VzTW9kdWxlID9cbiBcdFx0XHRmdW5jdGlvbiBnZXREZWZhdWx0KCkgeyByZXR1cm4gbW9kdWxlWydkZWZhdWx0J107IH0gOlxuIFx0XHRcdGZ1bmN0aW9uIGdldE1vZHVsZUV4cG9ydHMoKSB7IHJldHVybiBtb2R1bGU7IH07XG4gXHRcdF9fd2VicGFja19yZXF1aXJlX18uZChnZXR0ZXIsICdhJywgZ2V0dGVyKTtcbiBcdFx0cmV0dXJuIGdldHRlcjtcbiBcdH07XG5cbiBcdC8vIE9iamVjdC5wcm90b3R5cGUuaGFzT3duUHJvcGVydHkuY2FsbFxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5vID0gZnVuY3Rpb24ob2JqZWN0LCBwcm9wZXJ0eSkgeyByZXR1cm4gT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsKG9iamVjdCwgcHJvcGVydHkpOyB9O1xuXG4gXHQvLyBfX3dlYnBhY2tfcHVibGljX3BhdGhfX1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5wID0gXCJcIjtcblxuXG4gXHQvLyBMb2FkIGVudHJ5IG1vZHVsZSBhbmQgcmV0dXJuIGV4cG9ydHNcbiBcdHJldHVybiBfX3dlYnBhY2tfcmVxdWlyZV9fKF9fd2VicGFja19yZXF1aXJlX18ucyA9IFwiLi9zcmMvaW5kZXgudHNcIik7XG4iLCIvLyBodHRwczovL2dpdGh1Yi5jb20vbWF4b2dkZW4vd2Vic29ja2V0LXN0cmVhbS9ibG9iLzQ4ZGMzZGRmOTQzZTVhZGE2NjhjMzFjY2Q5NGU5MTg2ZjAyZmFmYmQvd3MtZmFsbGJhY2suanNcblxudmFyIHdzID0gbnVsbFxuXG5pZiAodHlwZW9mIFdlYlNvY2tldCAhPT0gJ3VuZGVmaW5lZCcpIHtcbiAgd3MgPSBXZWJTb2NrZXRcbn0gZWxzZSBpZiAodHlwZW9mIE1veldlYlNvY2tldCAhPT0gJ3VuZGVmaW5lZCcpIHtcbiAgd3MgPSBNb3pXZWJTb2NrZXRcbn0gZWxzZSBpZiAodHlwZW9mIGdsb2JhbCAhPT0gJ3VuZGVmaW5lZCcpIHtcbiAgd3MgPSBnbG9iYWwuV2ViU29ja2V0IHx8IGdsb2JhbC5Nb3pXZWJTb2NrZXRcbn0gZWxzZSBpZiAodHlwZW9mIHdpbmRvdyAhPT0gJ3VuZGVmaW5lZCcpIHtcbiAgd3MgPSB3aW5kb3cuV2ViU29ja2V0IHx8IHdpbmRvdy5Nb3pXZWJTb2NrZXRcbn0gZWxzZSBpZiAodHlwZW9mIHNlbGYgIT09ICd1bmRlZmluZWQnKSB7XG4gIHdzID0gc2VsZi5XZWJTb2NrZXQgfHwgc2VsZi5Nb3pXZWJTb2NrZXRcbn1cblxubW9kdWxlLmV4cG9ydHMgPSB3c1xuIiwidmFyIGc7XG5cbi8vIFRoaXMgd29ya3MgaW4gbm9uLXN0cmljdCBtb2RlXG5nID0gKGZ1bmN0aW9uKCkge1xuXHRyZXR1cm4gdGhpcztcbn0pKCk7XG5cbnRyeSB7XG5cdC8vIFRoaXMgd29ya3MgaWYgZXZhbCBpcyBhbGxvd2VkIChzZWUgQ1NQKVxuXHRnID0gZyB8fCBuZXcgRnVuY3Rpb24oXCJyZXR1cm4gdGhpc1wiKSgpO1xufSBjYXRjaCAoZSkge1xuXHQvLyBUaGlzIHdvcmtzIGlmIHRoZSB3aW5kb3cgcmVmZXJlbmNlIGlzIGF2YWlsYWJsZVxuXHRpZiAodHlwZW9mIHdpbmRvdyA9PT0gXCJvYmplY3RcIikgZyA9IHdpbmRvdztcbn1cblxuLy8gZyBjYW4gc3RpbGwgYmUgdW5kZWZpbmVkLCBidXQgbm90aGluZyB0byBkbyBhYm91dCBpdC4uLlxuLy8gV2UgcmV0dXJuIHVuZGVmaW5lZCwgaW5zdGVhZCBvZiBub3RoaW5nIGhlcmUsIHNvIGl0J3Ncbi8vIGVhc2llciB0byBoYW5kbGUgdGhpcyBjYXNlLiBpZighZ2xvYmFsKSB7IC4uLn1cblxubW9kdWxlLmV4cG9ydHMgPSBnO1xuIiwiXCJ1c2Ugc3RyaWN0XCI7XG52YXIgX19hd2FpdGVyID0gKHRoaXMgJiYgdGhpcy5fX2F3YWl0ZXIpIHx8IGZ1bmN0aW9uICh0aGlzQXJnLCBfYXJndW1lbnRzLCBQLCBnZW5lcmF0b3IpIHtcbiAgICBmdW5jdGlvbiBhZG9wdCh2YWx1ZSkgeyByZXR1cm4gdmFsdWUgaW5zdGFuY2VvZiBQID8gdmFsdWUgOiBuZXcgUChmdW5jdGlvbiAocmVzb2x2ZSkgeyByZXNvbHZlKHZhbHVlKTsgfSk7IH1cbiAgICByZXR1cm4gbmV3IChQIHx8IChQID0gUHJvbWlzZSkpKGZ1bmN0aW9uIChyZXNvbHZlLCByZWplY3QpIHtcbiAgICAgICAgZnVuY3Rpb24gZnVsZmlsbGVkKHZhbHVlKSB7IHRyeSB7IHN0ZXAoZ2VuZXJhdG9yLm5leHQodmFsdWUpKTsgfSBjYXRjaCAoZSkgeyByZWplY3QoZSk7IH0gfVxuICAgICAgICBmdW5jdGlvbiByZWplY3RlZCh2YWx1ZSkgeyB0cnkgeyBzdGVwKGdlbmVyYXRvcltcInRocm93XCJdKHZhbHVlKSk7IH0gY2F0Y2ggKGUpIHsgcmVqZWN0KGUpOyB9IH1cbiAgICAgICAgZnVuY3Rpb24gc3RlcChyZXN1bHQpIHsgcmVzdWx0LmRvbmUgPyByZXNvbHZlKHJlc3VsdC52YWx1ZSkgOiBhZG9wdChyZXN1bHQudmFsdWUpLnRoZW4oZnVsZmlsbGVkLCByZWplY3RlZCk7IH1cbiAgICAgICAgc3RlcCgoZ2VuZXJhdG9yID0gZ2VuZXJhdG9yLmFwcGx5KHRoaXNBcmcsIF9hcmd1bWVudHMgfHwgW10pKS5uZXh0KCkpO1xuICAgIH0pO1xufTtcbnZhciBfX2dlbmVyYXRvciA9ICh0aGlzICYmIHRoaXMuX19nZW5lcmF0b3IpIHx8IGZ1bmN0aW9uICh0aGlzQXJnLCBib2R5KSB7XG4gICAgdmFyIF8gPSB7IGxhYmVsOiAwLCBzZW50OiBmdW5jdGlvbigpIHsgaWYgKHRbMF0gJiAxKSB0aHJvdyB0WzFdOyByZXR1cm4gdFsxXTsgfSwgdHJ5czogW10sIG9wczogW10gfSwgZiwgeSwgdCwgZztcbiAgICByZXR1cm4gZyA9IHsgbmV4dDogdmVyYigwKSwgXCJ0aHJvd1wiOiB2ZXJiKDEpLCBcInJldHVyblwiOiB2ZXJiKDIpIH0sIHR5cGVvZiBTeW1ib2wgPT09IFwiZnVuY3Rpb25cIiAmJiAoZ1tTeW1ib2wuaXRlcmF0b3JdID0gZnVuY3Rpb24oKSB7IHJldHVybiB0aGlzOyB9KSwgZztcbiAgICBmdW5jdGlvbiB2ZXJiKG4pIHsgcmV0dXJuIGZ1bmN0aW9uICh2KSB7IHJldHVybiBzdGVwKFtuLCB2XSk7IH07IH1cbiAgICBmdW5jdGlvbiBzdGVwKG9wKSB7XG4gICAgICAgIGlmIChmKSB0aHJvdyBuZXcgVHlwZUVycm9yKFwiR2VuZXJhdG9yIGlzIGFscmVhZHkgZXhlY3V0aW5nLlwiKTtcbiAgICAgICAgd2hpbGUgKF8pIHRyeSB7XG4gICAgICAgICAgICBpZiAoZiA9IDEsIHkgJiYgKHQgPSBvcFswXSAmIDIgPyB5W1wicmV0dXJuXCJdIDogb3BbMF0gPyB5W1widGhyb3dcIl0gfHwgKCh0ID0geVtcInJldHVyblwiXSkgJiYgdC5jYWxsKHkpLCAwKSA6IHkubmV4dCkgJiYgISh0ID0gdC5jYWxsKHksIG9wWzFdKSkuZG9uZSkgcmV0dXJuIHQ7XG4gICAgICAgICAgICBpZiAoeSA9IDAsIHQpIG9wID0gW29wWzBdICYgMiwgdC52YWx1ZV07XG4gICAgICAgICAgICBzd2l0Y2ggKG9wWzBdKSB7XG4gICAgICAgICAgICAgICAgY2FzZSAwOiBjYXNlIDE6IHQgPSBvcDsgYnJlYWs7XG4gICAgICAgICAgICAgICAgY2FzZSA0OiBfLmxhYmVsKys7IHJldHVybiB7IHZhbHVlOiBvcFsxXSwgZG9uZTogZmFsc2UgfTtcbiAgICAgICAgICAgICAgICBjYXNlIDU6IF8ubGFiZWwrKzsgeSA9IG9wWzFdOyBvcCA9IFswXTsgY29udGludWU7XG4gICAgICAgICAgICAgICAgY2FzZSA3OiBvcCA9IF8ub3BzLnBvcCgpOyBfLnRyeXMucG9wKCk7IGNvbnRpbnVlO1xuICAgICAgICAgICAgICAgIGRlZmF1bHQ6XG4gICAgICAgICAgICAgICAgICAgIGlmICghKHQgPSBfLnRyeXMsIHQgPSB0Lmxlbmd0aCA+IDAgJiYgdFt0Lmxlbmd0aCAtIDFdKSAmJiAob3BbMF0gPT09IDYgfHwgb3BbMF0gPT09IDIpKSB7IF8gPSAwOyBjb250aW51ZTsgfVxuICAgICAgICAgICAgICAgICAgICBpZiAob3BbMF0gPT09IDMgJiYgKCF0IHx8IChvcFsxXSA+IHRbMF0gJiYgb3BbMV0gPCB0WzNdKSkpIHsgXy5sYWJlbCA9IG9wWzFdOyBicmVhazsgfVxuICAgICAgICAgICAgICAgICAgICBpZiAob3BbMF0gPT09IDYgJiYgXy5sYWJlbCA8IHRbMV0pIHsgXy5sYWJlbCA9IHRbMV07IHQgPSBvcDsgYnJlYWs7IH1cbiAgICAgICAgICAgICAgICAgICAgaWYgKHQgJiYgXy5sYWJlbCA8IHRbMl0pIHsgXy5sYWJlbCA9IHRbMl07IF8ub3BzLnB1c2gob3ApOyBicmVhazsgfVxuICAgICAgICAgICAgICAgICAgICBpZiAodFsyXSkgXy5vcHMucG9wKCk7XG4gICAgICAgICAgICAgICAgICAgIF8udHJ5cy5wb3AoKTsgY29udGludWU7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBvcCA9IGJvZHkuY2FsbCh0aGlzQXJnLCBfKTtcbiAgICAgICAgfSBjYXRjaCAoZSkgeyBvcCA9IFs2LCBlXTsgeSA9IDA7IH0gZmluYWxseSB7IGYgPSB0ID0gMDsgfVxuICAgICAgICBpZiAob3BbMF0gJiA1KSB0aHJvdyBvcFsxXTsgcmV0dXJuIHsgdmFsdWU6IG9wWzBdID8gb3BbMV0gOiB2b2lkIDAsIGRvbmU6IHRydWUgfTtcbiAgICB9XG59O1xudmFyIF9faW1wb3J0RGVmYXVsdCA9ICh0aGlzICYmIHRoaXMuX19pbXBvcnREZWZhdWx0KSB8fCBmdW5jdGlvbiAobW9kKSB7XG4gICAgcmV0dXJuIChtb2QgJiYgbW9kLl9fZXNNb2R1bGUpID8gbW9kIDogeyBcImRlZmF1bHRcIjogbW9kIH07XG59O1xuT2JqZWN0LmRlZmluZVByb3BlcnR5KGV4cG9ydHMsIFwiX19lc01vZHVsZVwiLCB7IHZhbHVlOiB0cnVlIH0pO1xuZXhwb3J0cy5Db25uZWN0aW9uID0gZXhwb3J0cy5NYWtlVHJlZSA9IHZvaWQgMDtcbnZhciBpc29tb3JwaGljX3dzXzEgPSBfX2ltcG9ydERlZmF1bHQocmVxdWlyZShcImlzb21vcnBoaWMtd3NcIikpO1xudmFyIHRyZWVfMSA9IHJlcXVpcmUoXCIuL3RyZWVcIik7XG5mdW5jdGlvbiBNYWtlVHJlZSgpIHtcbiAgICByZXR1cm4gbmV3IHRyZWVfMS5UcmVlKCk7XG59XG5leHBvcnRzLk1ha2VUcmVlID0gTWFrZVRyZWU7XG52YXIgQ29ubmVjdGlvbiA9IC8qKiBAY2xhc3MgKi8gKGZ1bmN0aW9uICgpIHtcbiAgICBmdW5jdGlvbiBDb25uZWN0aW9uKHVybCkge1xuICAgICAgICB2YXIgc2VsZiA9IHRoaXM7XG4gICAgICAgIHRoaXMuYmFja29mZiA9IDE7XG4gICAgICAgIHRoaXMudXJsID0gdXJsO1xuICAgICAgICB0aGlzLmNvbm5lY3RlZCA9IGZhbHNlO1xuICAgICAgICB0aGlzLmRlYWQgPSBmYWxzZTtcbiAgICAgICAgdGhpcy5tYXhpbXVtX2JhY2tvZmYgPSAyNTAwO1xuICAgICAgICB0aGlzLnNvY2tldCA9IG51bGw7XG4gICAgICAgIHRoaXMub25zdGF0dXNjaGFuZ2UgPSBmdW5jdGlvbiAoc3RhdHVzKSB7IH07XG4gICAgICAgIHRoaXMub25hdXRobmVlZGVkID0gZnVuY3Rpb24gKHRyeWFnYWluKSB7IH07XG4gICAgICAgIHRoaXMuc2NoZWR1bGVkID0gZmFsc2U7XG4gICAgICAgIHRoaXMuY2FsbGJhY2tzID0gbmV3IE1hcCgpO1xuICAgICAgICB0aGlzLnJwY2lkID0gMTtcbiAgICB9XG4gICAgLyoqIHN0b3AgdGhlIGNvbm5lY3Rpb24gKi9cbiAgICBDb25uZWN0aW9uLnByb3RvdHlwZS5zdG9wID0gZnVuY3Rpb24gKCkge1xuICAgICAgICB0aGlzLmRlYWQgPSB0cnVlO1xuICAgICAgICBpZiAodGhpcy5zb2NrZXQgIT09IG51bGwpIHtcbiAgICAgICAgICAgIHRoaXMuc29ja2V0LmNsb3NlKCk7XG4gICAgICAgIH1cbiAgICB9O1xuICAgIC8qKiBwcml2YXRlOiByZXRyeSB0aGUgY29ubmVjdGlvbiAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLl9yZXRyeSA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgLy8gbnVsbCBvdXQgdGhlIHNvY2tldFxuICAgICAgICB0aGlzLnNvY2tldCA9IG51bGw7XG4gICAgICAgIC8vIGlmIHdlIGFyZSBjb25uZWN0ZWQsIHRyYW5zaXRpb24gdGhlIHN0YXR1c1xuICAgICAgICBpZiAodGhpcy5jb25uZWN0ZWQpIHtcbiAgICAgICAgICAgIHRoaXMuY29ubmVjdGVkID0gZmFsc2U7XG4gICAgICAgICAgICB0aGlzLm9uc3RhdHVzY2hhbmdlKGZhbHNlKTtcbiAgICAgICAgfVxuICAgICAgICAvLyBmYWlsIGFsbCBvdXRzdGFuZGluZyBvcGVyYXRpb25zXG4gICAgICAgIHRoaXMuY2FsbGJhY2tzLmZvckVhY2goZnVuY3Rpb24gKGNhbGxiYWNrLCBpZCkge1xuICAgICAgICAgICAgY2FsbGJhY2soeyBmYWlsdXJlOiBpZCwgcmVhc29uOiA3NyB9KTtcbiAgICAgICAgfSk7XG4gICAgICAgIHRoaXMuY2FsbGJhY2tzLmNsZWFyKCk7XG4gICAgICAgIC8vIGlmIHdlIGFyZSBkZWFkLCB0aGVuIGRvbid0IGFjdHVhbGx5IHJldHJ5XG4gICAgICAgIGlmICh0aGlzLmRlYWQpIHtcbiAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgfVxuICAgICAgICAvLyBtYWtlIHN1cmVcbiAgICAgICAgaWYgKCF0aGlzLnNjaGVkdWxlZCkge1xuICAgICAgICAgICAgLy8gc2NoZWR1bGUgYSByZXRyeTsgZmlyc3QgY29tcHV0ZSBob3cgbG9uZyB0byB0YWtlXG4gICAgICAgICAgICAvLyBjb21wdXRlIGhvdyBsb25nIHdlIG5lZWQgdG8gd2FpdFxuICAgICAgICAgICAgdmFyIGhhbHZlQWZ0ZXJTY2hlZHVsZSA9IGZhbHNlO1xuICAgICAgICAgICAgdGhpcy5iYWNrb2ZmICs9IE1hdGgucmFuZG9tKCkgKiB0aGlzLmJhY2tvZmY7XG4gICAgICAgICAgICBpZiAodGhpcy5iYWNrb2ZmID4gdGhpcy5tYXhpbXVtX2JhY2tvZmYpIHtcbiAgICAgICAgICAgICAgICB0aGlzLmJhY2tvZmYgPSB0aGlzLm1heGltdW1fYmFja29mZjtcbiAgICAgICAgICAgICAgICBoYWx2ZUFmdGVyU2NoZWR1bGUgPSB0cnVlO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgLy8gc2NoZWR1bGUgaXRcbiAgICAgICAgICAgIHRoaXMuc2NoZWR1bGVkID0gdHJ1ZTtcbiAgICAgICAgICAgIHZhciBzZWxmID0gdGhpcztcbiAgICAgICAgICAgIHNldFRpbWVvdXQoZnVuY3Rpb24gKCkgeyBzZWxmLnN0YXJ0KCk7IH0sIHRoaXMuYmFja29mZik7XG4gICAgICAgICAgICAvLyB3ZSBqdXN0IHNjaGVkdWxlZCBpdCBmb3IgdGhlIG1heGltdW0gYmFja29mZiB0aW1lLCBsZXQncyByZWR1Y2UgdGhlIGJhY2tvZmYgc28gdGhlIG5leHQgb25lIHdpbGwgaGF2ZSBzb21lIGppdHRlclxuICAgICAgICAgICAgaWYgKGhhbHZlQWZ0ZXJTY2hlZHVsZSkge1xuICAgICAgICAgICAgICAgIHRoaXMuYmFja29mZiAvPSAyLjA7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9O1xuICAgIC8qKiBzdGFydCB0aGUgY29ubmVjdGlvbiAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLnN0YXJ0ID0gZnVuY3Rpb24gKCkge1xuICAgICAgICAvLyByZXNldCB0aGUgc3RhdGVcbiAgICAgICAgdmFyIHNlbGYgPSB0aGlzO1xuICAgICAgICB0aGlzLnNjaGVkdWxlZCA9IGZhbHNlO1xuICAgICAgICB0aGlzLmRlYWQgPSBmYWxzZTtcbiAgICAgICAgLy8gY3JlYXRlIHRoZSBzb2NrZXQgYW5kIGJpbmQgZXZlbnQgaGFuZGxlcnNcbiAgICAgICAgdGhpcy5zb2NrZXQgPSBuZXcgaXNvbW9ycGhpY193c18xLmRlZmF1bHQodGhpcy51cmwpO1xuICAgICAgICB0aGlzLnNvY2tldC5vbm1lc3NhZ2UgPSBmdW5jdGlvbiAoZXZlbnQpIHtcbiAgICAgICAgICAgIHZhciByZXN1bHQgPSBKU09OLnBhcnNlKGV2ZW50LmRhdGEpO1xuICAgICAgICAgICAgLy8gYSBtZXNzYWdlIGFycml2ZWQsIGlzIGl0IGEgY29ubmVjdGlvbiBzaWduYWxcbiAgICAgICAgICAgIGlmICgnc2lnbmFsJyBpbiByZXN1bHQpIHtcbiAgICAgICAgICAgICAgICAvLyBoZXksIGFyZSB3ZSBjb25uZWN0ZWQ/XG4gICAgICAgICAgICAgICAgaWYgKHJlc3VsdC5zdGF0dXMgIT0gJ2Nvbm5lY3RlZCcpIHtcbiAgICAgICAgICAgICAgICAgICAgLy8gbm9wZSwgT0ssIGxldCdzIG1ha2UgdGhpcyBhIGRlYWQgc29ja2V0XG4gICAgICAgICAgICAgICAgICAgIHNlbGYuZGVhZCA9IHRydWU7XG4gICAgICAgICAgICAgICAgICAgIHNlbGYuc29ja2V0LmNsb3NlKCk7XG4gICAgICAgICAgICAgICAgICAgIHNlbGYuc29ja2V0ID0gbnVsbDtcbiAgICAgICAgICAgICAgICAgICAgLy8gaW5mb3JtIHRoZSBjbGllbnQgdG8gdHJ5IGFnYWluXG4gICAgICAgICAgICAgICAgICAgIHNlbGYub25hdXRobmVlZGVkKGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuc3RhcnQoKTtcbiAgICAgICAgICAgICAgICAgICAgfSk7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybjtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgLy8gdGVsbCB0aGUgY2xpZW50IHRoYXQgd2UgYXJlIGdvb2QhXG4gICAgICAgICAgICAgICAgc2VsZi5iYWNrb2ZmID0gMTtcbiAgICAgICAgICAgICAgICBzZWxmLmNvbm5lY3RlZCA9IHRydWU7XG4gICAgICAgICAgICAgICAgc2VsZi5vbnN0YXR1c2NoYW5nZSh0cnVlKTtcbiAgICAgICAgICAgICAgICByZXR1cm47XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICAvLyB0aGUgcmVzdWx0IHdhcyBhIGZhaWx1cmUuLlxuICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXN1bHQpIHtcbiAgICAgICAgICAgICAgICAvLyBmaW5kIHRoZSBjYWxsYmFjaywgdGhlbiBpbnZva2UgaXQgKGFuZCBjbGVhbiB1cClcbiAgICAgICAgICAgICAgICBpZiAoc2VsZi5jYWxsYmFja3MuaGFzKHJlc3VsdC5mYWlsdXJlKSkge1xuICAgICAgICAgICAgICAgICAgICB2YXIgY2IgPSBzZWxmLmNhbGxiYWNrcy5nZXQocmVzdWx0LmZhaWx1cmUpO1xuICAgICAgICAgICAgICAgICAgICBpZiAoY2IpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuY2FsbGJhY2tzLmRlbGV0ZShyZXN1bHQuZmFpbHVyZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICBjYihyZXN1bHQpO1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSBpZiAoJ2RlbGl2ZXInIGluIHJlc3VsdCkge1xuICAgICAgICAgICAgICAgIC8vIG90aGVyd2lzZSwgd2UgaGF2ZSBhIHN1Y2Nlc3MsIHNvIGxldCdzIGZpbmQgdGhlIGNhbGxiYWNrLCBhbmQgaWYgbmVlZCBiZSBjbGVhbiB1cFxuICAgICAgICAgICAgICAgIGlmIChzZWxmLmNhbGxiYWNrcy5oYXMocmVzdWx0LmRlbGl2ZXIpKSB7XG4gICAgICAgICAgICAgICAgICAgIHZhciBjYiA9IHNlbGYuY2FsbGJhY2tzLmdldChyZXN1bHQuZGVsaXZlcik7XG4gICAgICAgICAgICAgICAgICAgIGlmIChjYikge1xuICAgICAgICAgICAgICAgICAgICAgICAgaWYgKHJlc3VsdC5kb25lKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5jYWxsYmFja3MuZGVsZXRlKHJlc3VsdC5kZWxpdmVyKTtcbiAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIGNiKHJlc3VsdC5yZXNwb25zZSk7XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH07XG4gICAgICAgIHRoaXMuc29ja2V0Lm9uY2xvc2UgPSBmdW5jdGlvbiAoZXZlbnQpIHtcbiAgICAgICAgICAgIC8vIGxldCdzIHJldHJ5Li4uIG9yIHNob3VsZCB3ZSBub3RcbiAgICAgICAgICAgIHNlbGYuX3JldHJ5KCk7XG4gICAgICAgIH07XG4gICAgICAgIHRoaXMuc29ja2V0Lm9uZXJyb3IgPSBmdW5jdGlvbiAoZXZlbnQpIHtcbiAgICAgICAgICAgIC8vIHNvbWV0aGluZyBiYWQgaGFwcGVuZWQsIGxldCdzIHJldHJ5XG4gICAgICAgICAgICBzZWxmLl9yZXRyeSgpO1xuICAgICAgICB9O1xuICAgIH07XG4gICAgLyoqIHByaXZhdGU6IHNlbmQgYSByYXcgbWVzc2FnZSAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLl9zZW5kID0gZnVuY3Rpb24gKHJlcXVlc3QsIGNhbGxiYWNrKSB7XG4gICAgICAgIGlmICghdGhpcy5jb25uZWN0ZWQpIHtcbiAgICAgICAgICAgIGNhbGxiYWNrKHsgZmFpbHVyZTogNjAwLCByZWFzb246IDk5OTkgfSk7XG4gICAgICAgICAgICByZXR1cm47XG4gICAgICAgIH1cbiAgICAgICAgdmFyIGlkID0gdGhpcy5ycGNpZDtcbiAgICAgICAgdGhpcy5ycGNpZCsrO1xuICAgICAgICByZXF1ZXN0WydpZCddID0gaWQ7XG4gICAgICAgIHRoaXMuY2FsbGJhY2tzLnNldChpZCwgY2FsbGJhY2spO1xuICAgICAgICB0aGlzLnNvY2tldC5zZW5kKEpTT04uc3RyaW5naWZ5KHJlcXVlc3QpKTtcbiAgICB9O1xuICAgIC8qKiBhcGk6IHdhaXQgZm9yIGEgY29ubmVjdGlvbiAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLndhaXRfY29ubmVjdGVkID0gZnVuY3Rpb24gKCkge1xuICAgICAgICByZXR1cm4gX19hd2FpdGVyKHRoaXMsIHZvaWQgMCwgdm9pZCAwLCBmdW5jdGlvbiAoKSB7XG4gICAgICAgICAgICB2YXIgc2VsZiwgcHJpb3I7XG4gICAgICAgICAgICByZXR1cm4gX19nZW5lcmF0b3IodGhpcywgZnVuY3Rpb24gKF9hKSB7XG4gICAgICAgICAgICAgICAgaWYgKHRoaXMuY29ubmVjdGVkKSB7XG4gICAgICAgICAgICAgICAgICAgIHJldHVybiBbMiAvKnJldHVybiovXTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgc2VsZiA9IHRoaXM7XG4gICAgICAgICAgICAgICAgcHJpb3IgPSB0aGlzLm9uc3RhdHVzY2hhbmdlO1xuICAgICAgICAgICAgICAgIHJldHVybiBbMiAvKnJldHVybiovLCBuZXcgUHJvbWlzZShmdW5jdGlvbiAoZ29vZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5vbnN0YXR1c2NoYW5nZSA9IGZ1bmN0aW9uIChzdGF0dXMpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBwcmlvcihzdGF0dXMpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmIChzdGF0dXMpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZ29vZCgpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBzZWxmLm9uc3RhdHVzY2hhbmdlID0gcHJpb3I7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgfTtcbiAgICAgICAgICAgICAgICAgICAgfSldO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH0pO1xuICAgIH07XG4gICAgLyoqIGFwaTogZ2VuZXJhdGUgYSBuZXcgZ2FtZSAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLmdlbmVyYXRlID0gZnVuY3Rpb24gKGdzKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciByZXF1ZXN0LCBzZWxmO1xuICAgICAgICAgICAgcmV0dXJuIF9fZ2VuZXJhdG9yKHRoaXMsIGZ1bmN0aW9uIChfYSkge1xuICAgICAgICAgICAgICAgIHJlcXVlc3QgPSB7IG1ldGhvZDogXCJnZW5lcmF0ZVwiLCBnYW1lc3BhY2U6IGdzIH07XG4gICAgICAgICAgICAgICAgc2VsZiA9IHRoaXM7XG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChnb29kLCBiYWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuX3NlbmQocmVxdWVzdCwgZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBiYWQocmVzcG9uc2UucmVhc29uKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICAvKiogYXBpOiBnZW5lcmF0ZSBhIG5ldyBnYW1lICovXG4gICAgQ29ubmVjdGlvbi5wcm90b3R5cGUuY3JlYXRlID0gZnVuY3Rpb24gKGdzLCBpZCwgYXJnKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciByZXF1ZXN0LCBzZWxmO1xuICAgICAgICAgICAgcmV0dXJuIF9fZ2VuZXJhdG9yKHRoaXMsIGZ1bmN0aW9uIChfYSkge1xuICAgICAgICAgICAgICAgIHJlcXVlc3QgPSB7IG1ldGhvZDogXCJjcmVhdGVcIiwgZ2FtZXNwYWNlOiBncywgZ2FtZTogaWQsIGFyZzogYXJnIH07XG4gICAgICAgICAgICAgICAgc2VsZiA9IHRoaXM7XG4gICAgICAgICAgICAgICAgcmV0dXJuIFsyIC8qcmV0dXJuKi8sIG5ldyBQcm9taXNlKGZ1bmN0aW9uIChnb29kLCBiYWQpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHNlbGYuX3NlbmQocmVxdWVzdCwgZnVuY3Rpb24gKHJlc3BvbnNlKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBiYWQocmVzcG9uc2UucmVhc29uKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICAvKiogYXBpOiBjb25uZWN0IHRvIGEgZ2FtZSAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLmNvbm5lY3QgPSBmdW5jdGlvbiAoZ3MsIGlkLCBoYW5kbGVyKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciByZXF1ZXN0LCBzZWxmLCBmaXJzdDtcbiAgICAgICAgICAgIHJldHVybiBfX2dlbmVyYXRvcih0aGlzLCBmdW5jdGlvbiAoX2EpIHtcbiAgICAgICAgICAgICAgICByZXF1ZXN0ID0geyBtZXRob2Q6IFwiY29ubmVjdFwiLCBnYW1lc3BhY2U6IGdzLCBnYW1lOiBpZCB9O1xuICAgICAgICAgICAgICAgIHNlbGYgPSB0aGlzO1xuICAgICAgICAgICAgICAgIGZpcnN0ID0gdHJ1ZTtcbiAgICAgICAgICAgICAgICByZXR1cm4gWzIgLypyZXR1cm4qLywgbmV3IFByb21pc2UoZnVuY3Rpb24gKGdvb2QsIGJhZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5fc2VuZChyZXF1ZXN0LCBmdW5jdGlvbiAocmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoZmlyc3QpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZmlyc3QgPSBmYWxzZTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYmFkKHJlc3BvbnNlLnJlYXNvbik7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBoYW5kbGVyKHJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QodHJ1ZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGhhbmRsZXIocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICAvKiogYXBpOiBzZW5kIGEgbWVzc2FnZSAqL1xuICAgIENvbm5lY3Rpb24ucHJvdG90eXBlLnNlbmQgPSBmdW5jdGlvbiAoZ3MsIGlkLCBjaGFubmVsLCBtc2cpIHtcbiAgICAgICAgcmV0dXJuIF9fYXdhaXRlcih0aGlzLCB2b2lkIDAsIHZvaWQgMCwgZnVuY3Rpb24gKCkge1xuICAgICAgICAgICAgdmFyIHJlcXVlc3QsIHNlbGY7XG4gICAgICAgICAgICByZXR1cm4gX19nZW5lcmF0b3IodGhpcywgZnVuY3Rpb24gKF9hKSB7XG4gICAgICAgICAgICAgICAgcmVxdWVzdCA9IHsgbWV0aG9kOiBcInNlbmRcIiwgZ2FtZXNwYWNlOiBncywgZ2FtZTogaWQsIGNoYW5uZWw6IGNoYW5uZWwsIG1lc3NhZ2U6IG1zZyB9O1xuICAgICAgICAgICAgICAgIHNlbGYgPSB0aGlzO1xuICAgICAgICAgICAgICAgIHJldHVybiBbMiAvKnJldHVybiovLCBuZXcgUHJvbWlzZShmdW5jdGlvbiAoZ29vZCwgYmFkKSB7XG4gICAgICAgICAgICAgICAgICAgICAgICBzZWxmLl9zZW5kKHJlcXVlc3QsIGZ1bmN0aW9uIChyZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGlmICgnZmFpbHVyZScgaW4gcmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYmFkKHJlc3BvbnNlLnJlYXNvbik7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBnb29kKHJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICB9KTtcbiAgICAgICAgICAgICAgICAgICAgfSldO1xuICAgICAgICAgICAgfSk7XG4gICAgICAgIH0pO1xuICAgIH07XG4gICAgLyoqIGFwaTogY29ubmVjdCB0cmVlICovXG4gICAgQ29ubmVjdGlvbi5wcm90b3R5cGUuY29ubmVjdFRyZWUgPSBmdW5jdGlvbiAoZ3MsIGlkLCB0cmVlKSB7XG4gICAgICAgIHJldHVybiBfX2F3YWl0ZXIodGhpcywgdm9pZCAwLCB2b2lkIDAsIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgICAgIHZhciByZXF1ZXN0LCBzZWxmLCBmaXJzdDtcbiAgICAgICAgICAgIHJldHVybiBfX2dlbmVyYXRvcih0aGlzLCBmdW5jdGlvbiAoX2EpIHtcbiAgICAgICAgICAgICAgICByZXF1ZXN0ID0geyBtZXRob2Q6IFwiY29ubmVjdFwiLCBnYW1lc3BhY2U6IGdzLCBnYW1lOiBpZCB9O1xuICAgICAgICAgICAgICAgIHNlbGYgPSB0aGlzO1xuICAgICAgICAgICAgICAgIGZpcnN0ID0gdHJ1ZTtcbiAgICAgICAgICAgICAgICByZXR1cm4gWzIgLypyZXR1cm4qLywgbmV3IFByb21pc2UoZnVuY3Rpb24gKGdvb2QsIGJhZCkge1xuICAgICAgICAgICAgICAgICAgICAgICAgc2VsZi5fc2VuZChyZXF1ZXN0LCBmdW5jdGlvbiAocmVzcG9uc2UpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICBpZiAoZmlyc3QpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZmlyc3QgPSBmYWxzZTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgaWYgKCdmYWlsdXJlJyBpbiByZXNwb25zZSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgYmFkKHJlc3BvbnNlLnJlYXNvbik7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICB0cmVlLm1lcmdlVXBkYXRlKHJlc3BvbnNlKTtcbiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGdvb2QodHJ1ZSk7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIHRyZWUubWVyZ2VVcGRhdGUocmVzcG9uc2UpO1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICAgICAgICAgIH0pO1xuICAgICAgICAgICAgICAgICAgICB9KV07XG4gICAgICAgICAgICB9KTtcbiAgICAgICAgfSk7XG4gICAgfTtcbiAgICByZXR1cm4gQ29ubmVjdGlvbjtcbn0oKSk7XG5leHBvcnRzLkNvbm5lY3Rpb24gPSBDb25uZWN0aW9uO1xuIiwiXCJ1c2Ugc3RyaWN0XCI7XG5PYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgXCJfX2VzTW9kdWxlXCIsIHsgdmFsdWU6IHRydWUgfSk7XG5leHBvcnRzLlRyZWUgPSB2b2lkIDA7XG52YXIgVHJlZSA9IC8qKiBAY2xhc3MgKi8gKGZ1bmN0aW9uICgpIHtcbiAgICBmdW5jdGlvbiBUcmVlKCkge1xuICAgICAgICB0aGlzLnRyZWUgPSB7fTtcbiAgICAgICAgdGhpcy5kaXNwYXRjaCA9IHt9O1xuICAgICAgICB0aGlzLmRpc3BhdGNoX2NvdW50ID0gMDtcbiAgICAgICAgdGhpcy5xdWV1ZSA9IFtdO1xuICAgICAgICB0aGlzLm9uZmV0Y2ggPSBmdW5jdGlvbiAoY2hhbm5lbCkgeyB9O1xuICAgICAgICB0aGlzLm9uZGVjaWRlID0gZnVuY3Rpb24gKGNoYW5uZWwsIG9wdGlvbnMpIHsgfTtcbiAgICB9XG4gICAgVHJlZS5wcm90b3R5cGUuX19yZWNBcHBlbmRDaGFuZ2UgPSBmdW5jdGlvbiAoZGlzcGF0Y2gsIGNhbGxiYWNrLCBpbnNlcnRfb3JkZXIpIHtcbiAgICAgICAgaWYgKHR5cGVvZiAoY2FsbGJhY2spID09ICdvYmplY3QnKSB7XG4gICAgICAgICAgICBmb3IgKHZhciBrZXkgaW4gY2FsbGJhY2spIHtcbiAgICAgICAgICAgICAgICBpZiAoIShrZXkgaW4gZGlzcGF0Y2gpKSB7XG4gICAgICAgICAgICAgICAgICAgIGRpc3BhdGNoW2tleV0gPSB7fTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgdGhpcy5fX3JlY0FwcGVuZENoYW5nZShkaXNwYXRjaFtrZXldLCBjYWxsYmFja1trZXldLCBpbnNlcnRfb3JkZXIpO1xuICAgICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICAgIGVsc2UgaWYgKHR5cGVvZiAoY2FsbGJhY2spID09ICdmdW5jdGlvbicpIHtcbiAgICAgICAgICAgIGlmICghKCdAZScgaW4gZGlzcGF0Y2gpKSB7XG4gICAgICAgICAgICAgICAgZGlzcGF0Y2hbJ0BlJ10gPSBbXTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGRpc3BhdGNoWydAZSddLnB1c2goeyBjYjogY2FsbGJhY2ssIG9yZGVyOiBpbnNlcnRfb3JkZXIgfSk7XG4gICAgICAgIH1cbiAgICB9O1xuICAgIFRyZWUucHJvdG90eXBlLm9uVHJlZUNoYW5nZSA9IGZ1bmN0aW9uIChjYWxsYmFjaykge1xuICAgICAgICB0aGlzLl9fcmVjQXBwZW5kQ2hhbmdlKHRoaXMuZGlzcGF0Y2gsIGNhbGxiYWNrLCB0aGlzLmRpc3BhdGNoX2NvdW50KTtcbiAgICAgICAgdGhpcy5kaXNwYXRjaF9jb3VudCsrO1xuICAgIH07XG4gICAgVHJlZS5wcm90b3R5cGUubWVyZ2VVcGRhdGUgPSBmdW5jdGlvbiAoZGlmZikge1xuICAgICAgICBpZiAoJ2RhdGEnIGluIGRpZmYpIHtcbiAgICAgICAgICAgIHRoaXMuX19yZWNNZXJnZUFuZERpc3BhdGNoKHRoaXMudHJlZSwgdGhpcy5kaXNwYXRjaCwgZGlmZi5kYXRhKTtcbiAgICAgICAgfVxuICAgICAgICAvLyBUT0RPOiBkaXNwYXRjaCBkZWNpc2lvbnNcbiAgICAgICAgLy8gVE9ETzogdXBkYXRlIGJsb2NrZXJzXG4gICAgICAgIHRoaXMuX19kcmFpbigpO1xuICAgIH07XG4gICAgVHJlZS5wcm90b3R5cGUuX19yZWNNZXJnZUFuZERpc3BhdGNoQXJyYXkgPSBmdW5jdGlvbiAocHJpb3IsIGRpc3BhdGNoLCB0cmVlLCBkaWZmKSB7XG4gICAgICAgIHZhciBvcmRlcmluZyA9IG51bGw7XG4gICAgICAgIHZhciByZXNpemUgPSBudWxsO1xuICAgICAgICBmb3IgKHZhciBrZXkgaW4gZGlmZikge1xuICAgICAgICAgICAgaWYgKGtleSA9PSBcIkBvXCIpIHtcbiAgICAgICAgICAgICAgICBvcmRlcmluZyA9IGRpZmZba2V5XTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGVsc2UgaWYgKGtleSA9PSBcIkBzXCIpIHtcbiAgICAgICAgICAgICAgICByZXNpemUgPSBkaWZmW2tleV07XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICBlbHNlIHtcbiAgICAgICAgICAgICAgICBpZiAoZGlmZltrZXldID09IG51bGwpIHtcbiAgICAgICAgICAgICAgICAgICAgZGVsZXRlIHRyZWVba2V5XTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIGlmICghKHRyZWUgIT0gbnVsbCAmJiBrZXkgaW4gdHJlZSkpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHRyZWVba2V5XSA9IHt9O1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIHRoaXMuX19yZWNNZXJnZUFuZERpc3BhdGNoKHRyZWVba2V5XSwgKGRpc3BhdGNoICE9IG51bGwgJiYga2V5IGluIGRpc3BhdGNoKSA/IGRpc3BhdGNoW2tleV0gOiBudWxsLCBkaWZmW2tleV0pO1xuICAgICAgICAgICAgICAgICAgICAvLyB0aGlzIHdpbGwgZmlyZSBhbiB1cGRhdGUgZm9yIHRoZSBrZXlcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgdmFyIGNoYW5nZSA9IHsgYmVmb3JlOiBwcmlvciwgYWZ0ZXI6IHByaW9yIH07XG4gICAgICAgIGlmIChyZXNpemUgIT09IG51bGwpIHtcbiAgICAgICAgICAgIC8vIHJlZm9yZ2UgdGhlIHByaW9yXG4gICAgICAgICAgICAvLyBUT0RPOiBzb3J0IHRoaXMgb3V0XG4gICAgICAgIH1cbiAgICAgICAgaWYgKG9yZGVyaW5nICE9PSBudWxsKSB7XG4gICAgICAgICAgICB2YXIgYWZ0ZXIgPSBbXTtcbiAgICAgICAgICAgIGNoYW5nZS5iZWZvcmUgPSBbXTtcbiAgICAgICAgICAgIGZvciAodmFyIGsgPSAwOyBrIDwgcHJpb3IubGVuZ3RoOyBrKyspIHtcbiAgICAgICAgICAgICAgICBjaGFuZ2UuYmVmb3JlLnB1c2gocHJpb3Jba10pO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZm9yICh2YXIgayA9IDA7IGsgPCBvcmRlcmluZy5sZW5ndGg7IGsrKykge1xuICAgICAgICAgICAgICAgIHZhciBpbnN0ciA9IG9yZGVyaW5nW2tdO1xuICAgICAgICAgICAgICAgIGlmICh0eXBlb2YgKGluc3RyKSA9PSBcInN0cmluZ1wiKSB7XG4gICAgICAgICAgICAgICAgICAgIGFmdGVyLnB1c2godHJlZVtpbnN0cl0pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICBlbHNlIGlmICh0eXBlb2YgKGluc3RyKSA9PSBcIm9iamVjdFwiKSB7XG4gICAgICAgICAgICAgICAgICAgIGlmIChcIkByXCIgaW4gaW5zdHIpIHtcbiAgICAgICAgICAgICAgICAgICAgICAgIHZhciBzdGFydCA9IGluc3RyW1wiQHJcIl1bMF07XG4gICAgICAgICAgICAgICAgICAgICAgICB2YXIgZW5kID0gaW5zdHJbXCJAclwiXVsxXTtcbiAgICAgICAgICAgICAgICAgICAgICAgIGZvciAodmFyIGogPSBzdGFydDsgaiA8PSBlbmQ7IGorKykge1xuICAgICAgICAgICAgICAgICAgICAgICAgICAgIGFmdGVyLnB1c2gocHJpb3Jbal0pO1xuICAgICAgICAgICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgcHJpb3IubGVuZ3RoID0gYWZ0ZXIubGVuZ3RoO1xuICAgICAgICAgICAgZm9yICh2YXIgayA9IDA7IGsgPCBhZnRlci5sZW5ndGg7IGsrKykge1xuICAgICAgICAgICAgICAgIHByaW9yW2tdID0gYWZ0ZXJba107XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5fX2ZpcmUoZGlzcGF0Y2gsIGNoYW5nZSk7XG4gICAgICAgIC8vIFRPRE86IGZpcmUgZXZlbnRzIGZvciB0aGUgdXBkYXRlXG4gICAgfTtcbiAgICBUcmVlLnByb3RvdHlwZS5fX2ZpcmUgPSBmdW5jdGlvbiAoZGlzcGF0Y2gsIGNoYW5nZSkge1xuICAgICAgICBpZiAoZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgIGlmICgnQGUnIGluIGRpc3BhdGNoKSB7XG4gICAgICAgICAgICAgICAgdmFyIGQgPSBkaXNwYXRjaFsnQGUnXTtcbiAgICAgICAgICAgICAgICBmb3IgKHZhciBrID0gMDsgayA8IGQubGVuZ3RoOyBrKyspIHtcbiAgICAgICAgICAgICAgICAgICAgdmFyIGV2dCA9IGRba107XG4gICAgICAgICAgICAgICAgICAgIHRoaXMucXVldWUucHVzaCh7IGNiOiBldnQuY2IsIG9yZGVyOiBldnQub3JkZXIsIGNoYW5nZTogY2hhbmdlIH0pO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgfVxuICAgIH07XG4gICAgVHJlZS5wcm90b3R5cGUuX19kcmFpbiA9IGZ1bmN0aW9uICgpIHtcbiAgICAgICAgdGhpcy5xdWV1ZS5zb3J0KGZ1bmN0aW9uIChhLCBiKSB7IHJldHVybiBhLm9yZGVyIC0gYi5vcmRlcjsgfSk7XG4gICAgICAgIGZvciAodmFyIGsgPSAwOyBrIDwgdGhpcy5xdWV1ZS5sZW5ndGg7IGsrKykge1xuICAgICAgICAgICAgdmFyIGl0ZW0gPSB0aGlzLnF1ZXVlW2tdO1xuICAgICAgICAgICAgaXRlbS5jYihpdGVtLmNoYW5nZSk7XG4gICAgICAgIH1cbiAgICAgICAgdGhpcy5xdWV1ZSA9IFtdO1xuICAgIH07XG4gICAgVHJlZS5wcm90b3R5cGUuX19yZWNNZXJnZUFuZERpc3BhdGNoID0gZnVuY3Rpb24gKHRyZWUsIGRpc3BhdGNoLCBkaWZmKSB7XG4gICAgICAgIC8vIHRoZSBkaWZmIGlzIGFuIG9iamVjdCwgc28gbGV0J3Mgd2FsayBpdHMga2V5c1xuICAgICAgICBmb3IgKHZhciBrZXkgaW4gZGlmZikge1xuICAgICAgICAgICAgdmFyIGNoaWxkID0gZGlmZltrZXldO1xuICAgICAgICAgICAgaWYgKGNoaWxkID09PSBudWxsKSB7XG4gICAgICAgICAgICAgICAgaWYgKEFycmF5LmlzQXJyYXkodHJlZVtrZXldKSkge1xuICAgICAgICAgICAgICAgICAgICBkZWxldGUgdHJlZVtcIiNcIiArIGtleV07XG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICAgIGRlbGV0ZSB0cmVlW2tleV07XG4gICAgICAgICAgICAgICAgY29udGludWU7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICAvLyBpZiB0aGUgY2hpbGQgaXMgYW4gb2JqZWN0LCB0aGVuLi5cbiAgICAgICAgICAgIGlmICh0eXBlb2YgKGNoaWxkKSA9PSAnb2JqZWN0Jykge1xuICAgICAgICAgICAgICAgIC8vIHRoZSBjaGlsZCBpcyBlaXRoZXIgYW4gQVJSQVkgb3IgYSBPQkpFQ1RcbiAgICAgICAgICAgICAgICB2YXIgY2hpbGRJc0FycmF5ID0gJ0BvJyBpbiBjaGlsZCB8fCAnQHMnIGluIGNoaWxkO1xuICAgICAgICAgICAgICAgIC8vIHRoZSBwcmlvciB2ZXJzaW9uIGRvZXNuJ3QgZXhpc3QsIHNvIHdlIGNyZWF0ZSB0aGUgZW1wdHkgbm9kZSBzbyB0aGF0IGl0IGRvZXMgZXhpc3RcbiAgICAgICAgICAgICAgICBpZiAoIShrZXkgaW4gdHJlZSkpIHtcbiAgICAgICAgICAgICAgICAgICAgaWYgKGNoaWxkSXNBcnJheSkge1xuICAgICAgICAgICAgICAgICAgICAgICAgdHJlZVtrZXldID0gW107XG4gICAgICAgICAgICAgICAgICAgICAgICB0cmVlW1wiI1wiICsga2V5XSA9IHt9O1xuICAgICAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgICAgIGVsc2Uge1xuICAgICAgICAgICAgICAgICAgICAgICAgdHJlZVtrZXldID0ge307XG4gICAgICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgLy8gbm93LCB3ZSBjaGVjayB0byBzZWUgaWYgdGhlIHByaW9yIHN0YXRlIGluZmx1ZW5jZXMgd2hldGhlciBvciBub3QgdGhlIGRpZmYgaXMgYW4gYXJyYXlcbiAgICAgICAgICAgICAgICBjaGlsZElzQXJyYXkgPSBBcnJheS5pc0FycmF5KHRyZWVba2V5XSkgfHwgY2hpbGRJc0FycmF5O1xuICAgICAgICAgICAgICAgIGlmIChjaGlsZElzQXJyYXkpIHtcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fX3JlY01lcmdlQW5kRGlzcGF0Y2hBcnJheSh0cmVlW2tleV0sIChkaXNwYXRjaCAhPSBudWxsICYmIGtleSBpbiBkaXNwYXRjaCkgPyBkaXNwYXRjaFtrZXldIDogbnVsbCwgdHJlZVtcIiNcIiArIGtleV0sIGNoaWxkKTtcbiAgICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgICAgIC8vIHdlIGhhdmUgYW4gb2JqZWN0LCBsZXQncyBtZXJnZSByZWN1cnNpdmVseS4uLiBZQVlcbiAgICAgICAgICAgICAgICAgICAgdGhpcy5fX3JlY01lcmdlQW5kRGlzcGF0Y2godHJlZVtrZXldLCAoZGlzcGF0Y2ggIT0gbnVsbCAmJiBrZXkgaW4gZGlzcGF0Y2gpID8gZGlzcGF0Y2hba2V5XSA6IG51bGwsIGNoaWxkKTtcbiAgICAgICAgICAgICAgICAgICAgLy8gTk9URTogdGhpcyB3aWxsIGZpcmUgZXZlbnRzXG4gICAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgfVxuICAgICAgICAgICAgZWxzZSB7XG4gICAgICAgICAgICAgICAgdmFyIGNoYW5nZSA9IHsgYmVmb3JlOiB0cmVlW2tleV0sIGFmdGVyOiBjaGlsZCB9O1xuICAgICAgICAgICAgICAgIHRyZWVba2V5XSA9IGNoaWxkO1xuICAgICAgICAgICAgICAgIGlmIChkaXNwYXRjaCAhPSBudWxsICYmIGtleSBpbiBkaXNwYXRjaCkge1xuICAgICAgICAgICAgICAgICAgICB0aGlzLl9fZmlyZShkaXNwYXRjaFtrZXldLCBjaGFuZ2UpO1xuICAgICAgICAgICAgICAgIH1cbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmIChkaXNwYXRjaCAhPSBudWxsICYmICdAZScgaW4gZGlzcGF0Y2gpIHtcbiAgICAgICAgICAgICAgICB0aGlzLl9fZmlyZShkaXNwYXRjaCwgdHJlZSk7XG4gICAgICAgICAgICB9XG4gICAgICAgIH1cbiAgICB9O1xuICAgIHJldHVybiBUcmVlO1xufSgpKTtcbmV4cG9ydHMuVHJlZSA9IFRyZWU7XG4iXSwic291cmNlUm9vdCI6IiJ9
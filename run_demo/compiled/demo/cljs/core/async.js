// Compiled by ClojureScript 0.0-2280
goog.provide('cljs.core.async');
goog.require('cljs.core');
goog.require('cljs.core.async.impl.channels');
goog.require('cljs.core.async.impl.dispatch');
goog.require('cljs.core.async.impl.ioc_helpers');
goog.require('cljs.core.async.impl.protocols');
goog.require('cljs.core.async.impl.channels');
goog.require('cljs.core.async.impl.buffers');
goog.require('cljs.core.async.impl.protocols');
goog.require('cljs.core.async.impl.timers');
goog.require('cljs.core.async.impl.dispatch');
goog.require('cljs.core.async.impl.ioc_helpers');
goog.require('cljs.core.async.impl.buffers');
goog.require('cljs.core.async.impl.timers');
cljs.core.async.fn_handler = (function fn_handler(f){if(typeof cljs.core.async.t10535 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10535 = (function (f,fn_handler,meta10536){
this.f = f;
this.fn_handler = fn_handler;
this.meta10536 = meta10536;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10535.cljs$lang$type = true;
cljs.core.async.t10535.cljs$lang$ctorStr = "cljs.core.async/t10535";
cljs.core.async.t10535.cljs$lang$ctorPrWriter = (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10535");
});
cljs.core.async.t10535.prototype.cljs$core$async$impl$protocols$Handler$ = true;
cljs.core.async.t10535.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return true;
});
cljs.core.async.t10535.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return self__.f;
});
cljs.core.async.t10535.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_10537){var self__ = this;
var _10537__$1 = this;return self__.meta10536;
});
cljs.core.async.t10535.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_10537,meta10536__$1){var self__ = this;
var _10537__$1 = this;return (new cljs.core.async.t10535(self__.f,self__.fn_handler,meta10536__$1));
});
cljs.core.async.__GT_t10535 = (function __GT_t10535(f__$1,fn_handler__$1,meta10536){return (new cljs.core.async.t10535(f__$1,fn_handler__$1,meta10536));
});
}
return (new cljs.core.async.t10535(f,fn_handler,null));
});
/**
* Returns a fixed buffer of size n. When full, puts will block/park.
*/
cljs.core.async.buffer = (function buffer(n){return cljs.core.async.impl.buffers.fixed_buffer.call(null,n);
});
/**
* Returns a buffer of size n. When full, puts will complete but
* val will be dropped (no transfer).
*/
cljs.core.async.dropping_buffer = (function dropping_buffer(n){return cljs.core.async.impl.buffers.dropping_buffer.call(null,n);
});
/**
* Returns a buffer of size n. When full, puts will complete, and be
* buffered, but oldest elements in buffer will be dropped (not
* transferred).
*/
cljs.core.async.sliding_buffer = (function sliding_buffer(n){return cljs.core.async.impl.buffers.sliding_buffer.call(null,n);
});
/**
* Returns true if a channel created with buff will never block. That is to say,
* puts into this buffer will never cause the buffer to be full.
*/
cljs.core.async.unblocking_buffer_QMARK_ = (function unblocking_buffer_QMARK_(buff){var G__10539 = buff;if(G__10539)
{var bit__4192__auto__ = null;if(cljs.core.truth_((function (){var or__3542__auto__ = bit__4192__auto__;if(cljs.core.truth_(or__3542__auto__))
{return or__3542__auto__;
} else
{return G__10539.cljs$core$async$impl$protocols$UnblockingBuffer$;
}
})()))
{return true;
} else
{if((!G__10539.cljs$lang$protocol_mask$partition$))
{return cljs.core.native_satisfies_QMARK_.call(null,cljs.core.async.impl.protocols.UnblockingBuffer,G__10539);
} else
{return false;
}
}
} else
{return cljs.core.native_satisfies_QMARK_.call(null,cljs.core.async.impl.protocols.UnblockingBuffer,G__10539);
}
});
/**
* Creates a channel with an optional buffer. If buf-or-n is a number,
* will create and use a fixed buffer of that size.
*/
cljs.core.async.chan = (function() {
var chan = null;
var chan__0 = (function (){return chan.call(null,null);
});
var chan__1 = (function (buf_or_n){var buf_or_n__$1 = ((cljs.core._EQ_.call(null,buf_or_n,(0)))?null:buf_or_n);return cljs.core.async.impl.channels.chan.call(null,((typeof buf_or_n__$1 === 'number')?cljs.core.async.buffer.call(null,buf_or_n__$1):buf_or_n__$1));
});
chan = function(buf_or_n){
switch(arguments.length){
case 0:
return chan__0.call(this);
case 1:
return chan__1.call(this,buf_or_n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
chan.cljs$core$IFn$_invoke$arity$0 = chan__0;
chan.cljs$core$IFn$_invoke$arity$1 = chan__1;
return chan;
})()
;
/**
* Returns a channel that will close after msecs
*/
cljs.core.async.timeout = (function timeout(msecs){return cljs.core.async.impl.timers.timeout.call(null,msecs);
});
/**
* takes a val from port. Must be called inside a (go ...) block. Will
* return nil if closed. Will park if nothing is available.
* Returns true unless port is already closed
*/
cljs.core.async._LT__BANG_ = (function _LT__BANG_(port){if(null)
{return null;
} else
{throw (new Error(("Assert failed: <! used not in (go ...) block\n"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,null)))));
}
});
/**
* Asynchronously takes a val from port, passing to fn1. Will pass nil
* if closed. If on-caller? (default true) is true, and value is
* immediately available, will call fn1 on calling thread.
* Returns nil.
*/
cljs.core.async.take_BANG_ = (function() {
var take_BANG_ = null;
var take_BANG___2 = (function (port,fn1){return take_BANG_.call(null,port,fn1,true);
});
var take_BANG___3 = (function (port,fn1,on_caller_QMARK_){var ret = cljs.core.async.impl.protocols.take_BANG_.call(null,port,cljs.core.async.fn_handler.call(null,fn1));if(cljs.core.truth_(ret))
{var val_10540 = cljs.core.deref.call(null,ret);if(cljs.core.truth_(on_caller_QMARK_))
{fn1.call(null,val_10540);
} else
{cljs.core.async.impl.dispatch.run.call(null,((function (val_10540,ret){
return (function (){return fn1.call(null,val_10540);
});})(val_10540,ret))
);
}
} else
{}
return null;
});
take_BANG_ = function(port,fn1,on_caller_QMARK_){
switch(arguments.length){
case 2:
return take_BANG___2.call(this,port,fn1);
case 3:
return take_BANG___3.call(this,port,fn1,on_caller_QMARK_);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
take_BANG_.cljs$core$IFn$_invoke$arity$2 = take_BANG___2;
take_BANG_.cljs$core$IFn$_invoke$arity$3 = take_BANG___3;
return take_BANG_;
})()
;
cljs.core.async.nop = (function nop(_){return null;
});
cljs.core.async.fhnop = cljs.core.async.fn_handler.call(null,cljs.core.async.nop);
/**
* puts a val into port. nil values are not allowed. Must be called
* inside a (go ...) block. Will park if no buffer space is available.
* Returns true unless port is already closed.
*/
cljs.core.async._GT__BANG_ = (function _GT__BANG_(port,val){if(null)
{return null;
} else
{throw (new Error(("Assert failed: >! used not in (go ...) block\n"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,null)))));
}
});
/**
* Asynchronously puts a val into port, calling fn0 (if supplied) when
* complete. nil values are not allowed. Will throw if closed. If
* on-caller? (default true) is true, and the put is immediately
* accepted, will call fn0 on calling thread.  Returns nil.
*/
cljs.core.async.put_BANG_ = (function() {
var put_BANG_ = null;
var put_BANG___2 = (function (port,val){var temp__4124__auto__ = cljs.core.async.impl.protocols.put_BANG_.call(null,port,val,cljs.core.async.fhnop);if(cljs.core.truth_(temp__4124__auto__))
{var ret = temp__4124__auto__;return cljs.core.deref.call(null,ret);
} else
{return true;
}
});
var put_BANG___3 = (function (port,val,fn1){return put_BANG_.call(null,port,val,fn1,true);
});
var put_BANG___4 = (function (port,val,fn1,on_caller_QMARK_){var temp__4124__auto__ = cljs.core.async.impl.protocols.put_BANG_.call(null,port,val,cljs.core.async.fn_handler.call(null,fn1));if(cljs.core.truth_(temp__4124__auto__))
{var retb = temp__4124__auto__;var ret = cljs.core.deref.call(null,retb);if(cljs.core.truth_(on_caller_QMARK_))
{fn1.call(null,ret);
} else
{cljs.core.async.impl.dispatch.run.call(null,((function (ret,retb,temp__4124__auto__){
return (function (){return fn1.call(null,ret);
});})(ret,retb,temp__4124__auto__))
);
}
return ret;
} else
{return true;
}
});
put_BANG_ = function(port,val,fn1,on_caller_QMARK_){
switch(arguments.length){
case 2:
return put_BANG___2.call(this,port,val);
case 3:
return put_BANG___3.call(this,port,val,fn1);
case 4:
return put_BANG___4.call(this,port,val,fn1,on_caller_QMARK_);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
put_BANG_.cljs$core$IFn$_invoke$arity$2 = put_BANG___2;
put_BANG_.cljs$core$IFn$_invoke$arity$3 = put_BANG___3;
put_BANG_.cljs$core$IFn$_invoke$arity$4 = put_BANG___4;
return put_BANG_;
})()
;
cljs.core.async.close_BANG_ = (function close_BANG_(port){return cljs.core.async.impl.protocols.close_BANG_.call(null,port);
});
cljs.core.async.random_array = (function random_array(n){var a = (new Array(n));var n__4398__auto___10541 = n;var x_10542 = (0);while(true){
if((x_10542 < n__4398__auto___10541))
{(a[x_10542] = (0));
{
var G__10543 = (x_10542 + (1));
x_10542 = G__10543;
continue;
}
} else
{}
break;
}
var i = (1);while(true){
if(cljs.core._EQ_.call(null,i,n))
{return a;
} else
{var j = cljs.core.rand_int.call(null,i);(a[i] = (a[j]));
(a[j] = i);
{
var G__10544 = (i + (1));
i = G__10544;
continue;
}
}
break;
}
});
cljs.core.async.alt_flag = (function alt_flag(){var flag = cljs.core.atom.call(null,true);if(typeof cljs.core.async.t10548 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10548 = (function (flag,alt_flag,meta10549){
this.flag = flag;
this.alt_flag = alt_flag;
this.meta10549 = meta10549;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10548.cljs$lang$type = true;
cljs.core.async.t10548.cljs$lang$ctorStr = "cljs.core.async/t10548";
cljs.core.async.t10548.cljs$lang$ctorPrWriter = ((function (flag){
return (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10548");
});})(flag))
;
cljs.core.async.t10548.prototype.cljs$core$async$impl$protocols$Handler$ = true;
cljs.core.async.t10548.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = ((function (flag){
return (function (_){var self__ = this;
var ___$1 = this;return cljs.core.deref.call(null,self__.flag);
});})(flag))
;
cljs.core.async.t10548.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = ((function (flag){
return (function (_){var self__ = this;
var ___$1 = this;cljs.core.reset_BANG_.call(null,self__.flag,null);
return true;
});})(flag))
;
cljs.core.async.t10548.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (flag){
return (function (_10550){var self__ = this;
var _10550__$1 = this;return self__.meta10549;
});})(flag))
;
cljs.core.async.t10548.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (flag){
return (function (_10550,meta10549__$1){var self__ = this;
var _10550__$1 = this;return (new cljs.core.async.t10548(self__.flag,self__.alt_flag,meta10549__$1));
});})(flag))
;
cljs.core.async.__GT_t10548 = ((function (flag){
return (function __GT_t10548(flag__$1,alt_flag__$1,meta10549){return (new cljs.core.async.t10548(flag__$1,alt_flag__$1,meta10549));
});})(flag))
;
}
return (new cljs.core.async.t10548(flag,alt_flag,null));
});
cljs.core.async.alt_handler = (function alt_handler(flag,cb){if(typeof cljs.core.async.t10554 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10554 = (function (cb,flag,alt_handler,meta10555){
this.cb = cb;
this.flag = flag;
this.alt_handler = alt_handler;
this.meta10555 = meta10555;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10554.cljs$lang$type = true;
cljs.core.async.t10554.cljs$lang$ctorStr = "cljs.core.async/t10554";
cljs.core.async.t10554.cljs$lang$ctorPrWriter = (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10554");
});
cljs.core.async.t10554.prototype.cljs$core$async$impl$protocols$Handler$ = true;
cljs.core.async.t10554.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.active_QMARK_.call(null,self__.flag);
});
cljs.core.async.t10554.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){var self__ = this;
var ___$1 = this;cljs.core.async.impl.protocols.commit.call(null,self__.flag);
return self__.cb;
});
cljs.core.async.t10554.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_10556){var self__ = this;
var _10556__$1 = this;return self__.meta10555;
});
cljs.core.async.t10554.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_10556,meta10555__$1){var self__ = this;
var _10556__$1 = this;return (new cljs.core.async.t10554(self__.cb,self__.flag,self__.alt_handler,meta10555__$1));
});
cljs.core.async.__GT_t10554 = (function __GT_t10554(cb__$1,flag__$1,alt_handler__$1,meta10555){return (new cljs.core.async.t10554(cb__$1,flag__$1,alt_handler__$1,meta10555));
});
}
return (new cljs.core.async.t10554(cb,flag,alt_handler,null));
});
/**
* returns derefable [val port] if immediate, nil if enqueued
*/
cljs.core.async.do_alts = (function do_alts(fret,ports,opts){var flag = cljs.core.async.alt_flag.call(null);var n = cljs.core.count.call(null,ports);var idxs = cljs.core.async.random_array.call(null,n);var priority = new cljs.core.Keyword(null,"priority","priority",1431093715).cljs$core$IFn$_invoke$arity$1(opts);var ret = (function (){var i = (0);while(true){
if((i < n))
{var idx = (cljs.core.truth_(priority)?i:(idxs[i]));var port = cljs.core.nth.call(null,ports,idx);var wport = ((cljs.core.vector_QMARK_.call(null,port))?port.call(null,(0)):null);var vbox = (cljs.core.truth_(wport)?(function (){var val = port.call(null,(1));return cljs.core.async.impl.protocols.put_BANG_.call(null,wport,val,cljs.core.async.alt_handler.call(null,flag,((function (i,val,idx,port,wport,flag,n,idxs,priority){
return (function (p1__10557_SHARP_){return fret.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__10557_SHARP_,wport], null));
});})(i,val,idx,port,wport,flag,n,idxs,priority))
));
})():cljs.core.async.impl.protocols.take_BANG_.call(null,port,cljs.core.async.alt_handler.call(null,flag,((function (i,idx,port,wport,flag,n,idxs,priority){
return (function (p1__10558_SHARP_){return fret.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__10558_SHARP_,port], null));
});})(i,idx,port,wport,flag,n,idxs,priority))
)));if(cljs.core.truth_(vbox))
{return cljs.core.async.impl.channels.box.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.deref.call(null,vbox),(function (){var or__3542__auto__ = wport;if(cljs.core.truth_(or__3542__auto__))
{return or__3542__auto__;
} else
{return port;
}
})()], null));
} else
{{
var G__10559 = (i + (1));
i = G__10559;
continue;
}
}
} else
{return null;
}
break;
}
})();var or__3542__auto__ = ret;if(cljs.core.truth_(or__3542__auto__))
{return or__3542__auto__;
} else
{if(cljs.core.contains_QMARK_.call(null,opts,new cljs.core.Keyword(null,"default","default",-1987822328)))
{var temp__4126__auto__ = (function (){var and__3530__auto__ = cljs.core.async.impl.protocols.active_QMARK_.call(null,flag);if(cljs.core.truth_(and__3530__auto__))
{return cljs.core.async.impl.protocols.commit.call(null,flag);
} else
{return and__3530__auto__;
}
})();if(cljs.core.truth_(temp__4126__auto__))
{var got = temp__4126__auto__;return cljs.core.async.impl.channels.box.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"default","default",-1987822328).cljs$core$IFn$_invoke$arity$1(opts),new cljs.core.Keyword(null,"default","default",-1987822328)], null));
} else
{return null;
}
} else
{return null;
}
}
});
/**
* Completes at most one of several channel operations. Must be called
* inside a (go ...) block. ports is a vector of channel endpoints,
* which can be either a channel to take from or a vector of
* [channel-to-put-to val-to-put], in any combination. Takes will be
* made as if by <!, and puts will be made as if by >!. Unless
* the :priority option is true, if more than one port operation is
* ready a non-deterministic choice will be made. If no operation is
* ready and a :default value is supplied, [default-val :default] will
* be returned, otherwise alts! will park until the first operation to
* become ready completes. Returns [val port] of the completed
* operation, where val is the value taken for takes, and a
* boolean (true unless already closed, as per put!) for puts.
* 
* opts are passed as :key val ... Supported options:
* 
* :default val - the value to use if none of the operations are immediately ready
* :priority true - (default nil) when true, the operations will be tried in order.
* 
* Note: there is no guarantee that the port exps or val exprs will be
* used, nor in what order should they be, so they should not be
* depended upon for side effects.
* @param {...*} var_args
*/
cljs.core.async.alts_BANG_ = (function() { 
var alts_BANG___delegate = function (ports,p__10560){var map__10562 = p__10560;var map__10562__$1 = ((cljs.core.seq_QMARK_.call(null,map__10562))?cljs.core.apply.call(null,cljs.core.hash_map,map__10562):map__10562);var opts = map__10562__$1;if(null)
{return null;
} else
{throw (new Error(("Assert failed: alts! used not in (go ...) block\n"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,null)))));
}
};
var alts_BANG_ = function (ports,var_args){
var p__10560 = null;if (arguments.length > 1) {
  p__10560 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 1),0);} 
return alts_BANG___delegate.call(this,ports,p__10560);};
alts_BANG_.cljs$lang$maxFixedArity = 1;
alts_BANG_.cljs$lang$applyTo = (function (arglist__10563){
var ports = cljs.core.first(arglist__10563);
var p__10560 = cljs.core.rest(arglist__10563);
return alts_BANG___delegate(ports,p__10560);
});
alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = alts_BANG___delegate;
return alts_BANG_;
})()
;
/**
* Takes a function and a source channel, and returns a channel which
* contains the values produced by applying f to each value taken from
* the source channel
*/
cljs.core.async.map_LT_ = (function map_LT_(f,ch){if(typeof cljs.core.async.t10571 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10571 = (function (ch,f,map_LT_,meta10572){
this.ch = ch;
this.f = f;
this.map_LT_ = map_LT_;
this.meta10572 = meta10572;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10571.cljs$lang$type = true;
cljs.core.async.t10571.cljs$lang$ctorStr = "cljs.core.async/t10571";
cljs.core.async.t10571.cljs$lang$ctorPrWriter = (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10571");
});
cljs.core.async.t10571.prototype.cljs$core$async$impl$protocols$WritePort$ = true;
cljs.core.async.t10571.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,val,fn1);
});
cljs.core.async.t10571.prototype.cljs$core$async$impl$protocols$ReadPort$ = true;
cljs.core.async.t10571.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){var self__ = this;
var ___$1 = this;var ret = cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,(function (){if(typeof cljs.core.async.t10574 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10574 = (function (fn1,_,meta10572,ch,f,map_LT_,meta10575){
this.fn1 = fn1;
this._ = _;
this.meta10572 = meta10572;
this.ch = ch;
this.f = f;
this.map_LT_ = map_LT_;
this.meta10575 = meta10575;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10574.cljs$lang$type = true;
cljs.core.async.t10574.cljs$lang$ctorStr = "cljs.core.async/t10574";
cljs.core.async.t10574.cljs$lang$ctorPrWriter = ((function (___$1){
return (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10574");
});})(___$1))
;
cljs.core.async.t10574.prototype.cljs$core$async$impl$protocols$Handler$ = true;
cljs.core.async.t10574.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = ((function (___$1){
return (function (___$3){var self__ = this;
var ___$4 = this;return cljs.core.async.impl.protocols.active_QMARK_.call(null,self__.fn1);
});})(___$1))
;
cljs.core.async.t10574.prototype.cljs$core$async$impl$protocols$Handler$lock_id$arity$1 = ((function (___$1){
return (function (___$3){var self__ = this;
var ___$4 = this;return cljs.core.async.impl.protocols.lock_id.call(null,self__.fn1);
});})(___$1))
;
cljs.core.async.t10574.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = ((function (___$1){
return (function (___$3){var self__ = this;
var ___$4 = this;var f1 = cljs.core.async.impl.protocols.commit.call(null,self__.fn1);return ((function (f1,___$4,___$1){
return (function (p1__10564_SHARP_){return f1.call(null,(((p1__10564_SHARP_ == null))?null:self__.f.call(null,p1__10564_SHARP_)));
});
;})(f1,___$4,___$1))
});})(___$1))
;
cljs.core.async.t10574.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (___$1){
return (function (_10576){var self__ = this;
var _10576__$1 = this;return self__.meta10575;
});})(___$1))
;
cljs.core.async.t10574.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (___$1){
return (function (_10576,meta10575__$1){var self__ = this;
var _10576__$1 = this;return (new cljs.core.async.t10574(self__.fn1,self__._,self__.meta10572,self__.ch,self__.f,self__.map_LT_,meta10575__$1));
});})(___$1))
;
cljs.core.async.__GT_t10574 = ((function (___$1){
return (function __GT_t10574(fn1__$1,___$2,meta10572__$1,ch__$2,f__$2,map_LT___$2,meta10575){return (new cljs.core.async.t10574(fn1__$1,___$2,meta10572__$1,ch__$2,f__$2,map_LT___$2,meta10575));
});})(___$1))
;
}
return (new cljs.core.async.t10574(fn1,___$1,self__.meta10572,self__.ch,self__.f,self__.map_LT_,null));
})());if(cljs.core.truth_((function (){var and__3530__auto__ = ret;if(cljs.core.truth_(and__3530__auto__))
{return !((cljs.core.deref.call(null,ret) == null));
} else
{return and__3530__auto__;
}
})()))
{return cljs.core.async.impl.channels.box.call(null,self__.f.call(null,cljs.core.deref.call(null,ret)));
} else
{return ret;
}
});
cljs.core.async.t10571.prototype.cljs$core$async$impl$protocols$Channel$ = true;
cljs.core.async.t10571.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});
cljs.core.async.t10571.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch);
});
cljs.core.async.t10571.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_10573){var self__ = this;
var _10573__$1 = this;return self__.meta10572;
});
cljs.core.async.t10571.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_10573,meta10572__$1){var self__ = this;
var _10573__$1 = this;return (new cljs.core.async.t10571(self__.ch,self__.f,self__.map_LT_,meta10572__$1));
});
cljs.core.async.__GT_t10571 = (function __GT_t10571(ch__$1,f__$1,map_LT___$1,meta10572){return (new cljs.core.async.t10571(ch__$1,f__$1,map_LT___$1,meta10572));
});
}
return (new cljs.core.async.t10571(ch,f,map_LT_,null));
});
/**
* Takes a function and a target channel, and returns a channel which
* applies f to each value before supplying it to the target channel.
*/
cljs.core.async.map_GT_ = (function map_GT_(f,ch){if(typeof cljs.core.async.t10580 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10580 = (function (ch,f,map_GT_,meta10581){
this.ch = ch;
this.f = f;
this.map_GT_ = map_GT_;
this.meta10581 = meta10581;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10580.cljs$lang$type = true;
cljs.core.async.t10580.cljs$lang$ctorStr = "cljs.core.async/t10580";
cljs.core.async.t10580.cljs$lang$ctorPrWriter = (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10580");
});
cljs.core.async.t10580.prototype.cljs$core$async$impl$protocols$WritePort$ = true;
cljs.core.async.t10580.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,self__.f.call(null,val),fn1);
});
cljs.core.async.t10580.prototype.cljs$core$async$impl$protocols$ReadPort$ = true;
cljs.core.async.t10580.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,fn1);
});
cljs.core.async.t10580.prototype.cljs$core$async$impl$protocols$Channel$ = true;
cljs.core.async.t10580.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});
cljs.core.async.t10580.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_10582){var self__ = this;
var _10582__$1 = this;return self__.meta10581;
});
cljs.core.async.t10580.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_10582,meta10581__$1){var self__ = this;
var _10582__$1 = this;return (new cljs.core.async.t10580(self__.ch,self__.f,self__.map_GT_,meta10581__$1));
});
cljs.core.async.__GT_t10580 = (function __GT_t10580(ch__$1,f__$1,map_GT___$1,meta10581){return (new cljs.core.async.t10580(ch__$1,f__$1,map_GT___$1,meta10581));
});
}
return (new cljs.core.async.t10580(ch,f,map_GT_,null));
});
/**
* Takes a predicate and a target channel, and returns a channel which
* supplies only the values for which the predicate returns true to the
* target channel.
*/
cljs.core.async.filter_GT_ = (function filter_GT_(p,ch){if(typeof cljs.core.async.t10586 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10586 = (function (ch,p,filter_GT_,meta10587){
this.ch = ch;
this.p = p;
this.filter_GT_ = filter_GT_;
this.meta10587 = meta10587;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10586.cljs$lang$type = true;
cljs.core.async.t10586.cljs$lang$ctorStr = "cljs.core.async/t10586";
cljs.core.async.t10586.cljs$lang$ctorPrWriter = (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10586");
});
cljs.core.async.t10586.prototype.cljs$core$async$impl$protocols$WritePort$ = true;
cljs.core.async.t10586.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){var self__ = this;
var ___$1 = this;if(cljs.core.truth_(self__.p.call(null,val)))
{return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,val,fn1);
} else
{return cljs.core.async.impl.channels.box.call(null,cljs.core.not.call(null,cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch)));
}
});
cljs.core.async.t10586.prototype.cljs$core$async$impl$protocols$ReadPort$ = true;
cljs.core.async.t10586.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,fn1);
});
cljs.core.async.t10586.prototype.cljs$core$async$impl$protocols$Channel$ = true;
cljs.core.async.t10586.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});
cljs.core.async.t10586.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch);
});
cljs.core.async.t10586.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_10588){var self__ = this;
var _10588__$1 = this;return self__.meta10587;
});
cljs.core.async.t10586.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_10588,meta10587__$1){var self__ = this;
var _10588__$1 = this;return (new cljs.core.async.t10586(self__.ch,self__.p,self__.filter_GT_,meta10587__$1));
});
cljs.core.async.__GT_t10586 = (function __GT_t10586(ch__$1,p__$1,filter_GT___$1,meta10587){return (new cljs.core.async.t10586(ch__$1,p__$1,filter_GT___$1,meta10587));
});
}
return (new cljs.core.async.t10586(ch,p,filter_GT_,null));
});
/**
* Takes a predicate and a target channel, and returns a channel which
* supplies only the values for which the predicate returns false to the
* target channel.
*/
cljs.core.async.remove_GT_ = (function remove_GT_(p,ch){return cljs.core.async.filter_GT_.call(null,cljs.core.complement.call(null,p),ch);
});
/**
* Takes a predicate and a source channel, and returns a channel which
* contains only the values taken from the source channel for which the
* predicate returns true. The returned channel will be unbuffered by
* default, or a buf-or-n can be supplied. The channel will close
* when the source channel closes.
*/
cljs.core.async.filter_LT_ = (function() {
var filter_LT_ = null;
var filter_LT___2 = (function (p,ch){return filter_LT_.call(null,p,ch,null);
});
var filter_LT___3 = (function (p,ch,buf_or_n){var out = cljs.core.async.chan.call(null,buf_or_n);var c__6352__auto___10671 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___10671,out){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___10671,out){
return (function (state_10650){var state_val_10651 = (state_10650[(1)]);if((state_val_10651 === (7)))
{var inst_10646 = (state_10650[(2)]);var state_10650__$1 = state_10650;var statearr_10652_10672 = state_10650__$1;(statearr_10652_10672[(2)] = inst_10646);
(statearr_10652_10672[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10651 === (1)))
{var state_10650__$1 = state_10650;var statearr_10653_10673 = state_10650__$1;(statearr_10653_10673[(2)] = null);
(statearr_10653_10673[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10651 === (4)))
{var inst_10632 = (state_10650[(7)]);var inst_10632__$1 = (state_10650[(2)]);var inst_10633 = (inst_10632__$1 == null);var state_10650__$1 = (function (){var statearr_10654 = state_10650;(statearr_10654[(7)] = inst_10632__$1);
return statearr_10654;
})();if(cljs.core.truth_(inst_10633))
{var statearr_10655_10674 = state_10650__$1;(statearr_10655_10674[(1)] = (5));
} else
{var statearr_10656_10675 = state_10650__$1;(statearr_10656_10675[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10651 === (6)))
{var inst_10632 = (state_10650[(7)]);var inst_10637 = p.call(null,inst_10632);var state_10650__$1 = state_10650;if(cljs.core.truth_(inst_10637))
{var statearr_10657_10676 = state_10650__$1;(statearr_10657_10676[(1)] = (8));
} else
{var statearr_10658_10677 = state_10650__$1;(statearr_10658_10677[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10651 === (3)))
{var inst_10648 = (state_10650[(2)]);var state_10650__$1 = state_10650;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10650__$1,inst_10648);
} else
{if((state_val_10651 === (2)))
{var state_10650__$1 = state_10650;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_10650__$1,(4),ch);
} else
{if((state_val_10651 === (11)))
{var inst_10640 = (state_10650[(2)]);var state_10650__$1 = state_10650;var statearr_10659_10678 = state_10650__$1;(statearr_10659_10678[(2)] = inst_10640);
(statearr_10659_10678[(1)] = (10));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10651 === (9)))
{var state_10650__$1 = state_10650;var statearr_10660_10679 = state_10650__$1;(statearr_10660_10679[(2)] = null);
(statearr_10660_10679[(1)] = (10));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10651 === (5)))
{var inst_10635 = cljs.core.async.close_BANG_.call(null,out);var state_10650__$1 = state_10650;var statearr_10661_10680 = state_10650__$1;(statearr_10661_10680[(2)] = inst_10635);
(statearr_10661_10680[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10651 === (10)))
{var inst_10643 = (state_10650[(2)]);var state_10650__$1 = (function (){var statearr_10662 = state_10650;(statearr_10662[(8)] = inst_10643);
return statearr_10662;
})();var statearr_10663_10681 = state_10650__$1;(statearr_10663_10681[(2)] = null);
(statearr_10663_10681[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10651 === (8)))
{var inst_10632 = (state_10650[(7)]);var state_10650__$1 = state_10650;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10650__$1,(11),out,inst_10632);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto___10671,out))
;return ((function (switch__6337__auto__,c__6352__auto___10671,out){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_10667 = [null,null,null,null,null,null,null,null,null];(statearr_10667[(0)] = state_machine__6338__auto__);
(statearr_10667[(1)] = (1));
return statearr_10667;
});
var state_machine__6338__auto____1 = (function (state_10650){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_10650);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e10668){if((e10668 instanceof Object))
{var ex__6341__auto__ = e10668;var statearr_10669_10682 = state_10650;(statearr_10669_10682[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10650);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e10668;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__10683 = state_10650;
state_10650 = G__10683;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_10650){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_10650);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___10671,out))
})();var state__6354__auto__ = (function (){var statearr_10670 = f__6353__auto__.call(null);(statearr_10670[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___10671);
return statearr_10670;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___10671,out))
);
return out;
});
filter_LT_ = function(p,ch,buf_or_n){
switch(arguments.length){
case 2:
return filter_LT___2.call(this,p,ch);
case 3:
return filter_LT___3.call(this,p,ch,buf_or_n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
filter_LT_.cljs$core$IFn$_invoke$arity$2 = filter_LT___2;
filter_LT_.cljs$core$IFn$_invoke$arity$3 = filter_LT___3;
return filter_LT_;
})()
;
/**
* Takes a predicate and a source channel, and returns a channel which
* contains only the values taken from the source channel for which the
* predicate returns false. The returned channel will be unbuffered by
* default, or a buf-or-n can be supplied. The channel will close
* when the source channel closes.
*/
cljs.core.async.remove_LT_ = (function() {
var remove_LT_ = null;
var remove_LT___2 = (function (p,ch){return remove_LT_.call(null,p,ch,null);
});
var remove_LT___3 = (function (p,ch,buf_or_n){return cljs.core.async.filter_LT_.call(null,cljs.core.complement.call(null,p),ch,buf_or_n);
});
remove_LT_ = function(p,ch,buf_or_n){
switch(arguments.length){
case 2:
return remove_LT___2.call(this,p,ch);
case 3:
return remove_LT___3.call(this,p,ch,buf_or_n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
remove_LT_.cljs$core$IFn$_invoke$arity$2 = remove_LT___2;
remove_LT_.cljs$core$IFn$_invoke$arity$3 = remove_LT___3;
return remove_LT_;
})()
;
cljs.core.async.mapcat_STAR_ = (function mapcat_STAR_(f,in$,out){var c__6352__auto__ = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto__){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto__){
return (function (state_10849){var state_val_10850 = (state_10849[(1)]);if((state_val_10850 === (7)))
{var inst_10845 = (state_10849[(2)]);var state_10849__$1 = state_10849;var statearr_10851_10892 = state_10849__$1;(statearr_10851_10892[(2)] = inst_10845);
(statearr_10851_10892[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (20)))
{var inst_10815 = (state_10849[(7)]);var inst_10826 = (state_10849[(2)]);var inst_10827 = cljs.core.next.call(null,inst_10815);var inst_10801 = inst_10827;var inst_10802 = null;var inst_10803 = (0);var inst_10804 = (0);var state_10849__$1 = (function (){var statearr_10852 = state_10849;(statearr_10852[(8)] = inst_10803);
(statearr_10852[(9)] = inst_10801);
(statearr_10852[(10)] = inst_10804);
(statearr_10852[(11)] = inst_10826);
(statearr_10852[(12)] = inst_10802);
return statearr_10852;
})();var statearr_10853_10893 = state_10849__$1;(statearr_10853_10893[(2)] = null);
(statearr_10853_10893[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (1)))
{var state_10849__$1 = state_10849;var statearr_10854_10894 = state_10849__$1;(statearr_10854_10894[(2)] = null);
(statearr_10854_10894[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (4)))
{var inst_10790 = (state_10849[(13)]);var inst_10790__$1 = (state_10849[(2)]);var inst_10791 = (inst_10790__$1 == null);var state_10849__$1 = (function (){var statearr_10855 = state_10849;(statearr_10855[(13)] = inst_10790__$1);
return statearr_10855;
})();if(cljs.core.truth_(inst_10791))
{var statearr_10856_10895 = state_10849__$1;(statearr_10856_10895[(1)] = (5));
} else
{var statearr_10857_10896 = state_10849__$1;(statearr_10857_10896[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (15)))
{var state_10849__$1 = state_10849;var statearr_10861_10897 = state_10849__$1;(statearr_10861_10897[(2)] = null);
(statearr_10861_10897[(1)] = (16));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (21)))
{var state_10849__$1 = state_10849;var statearr_10862_10898 = state_10849__$1;(statearr_10862_10898[(2)] = null);
(statearr_10862_10898[(1)] = (23));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (13)))
{var inst_10803 = (state_10849[(8)]);var inst_10801 = (state_10849[(9)]);var inst_10804 = (state_10849[(10)]);var inst_10802 = (state_10849[(12)]);var inst_10811 = (state_10849[(2)]);var inst_10812 = (inst_10804 + (1));var tmp10858 = inst_10803;var tmp10859 = inst_10801;var tmp10860 = inst_10802;var inst_10801__$1 = tmp10859;var inst_10802__$1 = tmp10860;var inst_10803__$1 = tmp10858;var inst_10804__$1 = inst_10812;var state_10849__$1 = (function (){var statearr_10863 = state_10849;(statearr_10863[(8)] = inst_10803__$1);
(statearr_10863[(9)] = inst_10801__$1);
(statearr_10863[(10)] = inst_10804__$1);
(statearr_10863[(12)] = inst_10802__$1);
(statearr_10863[(14)] = inst_10811);
return statearr_10863;
})();var statearr_10864_10899 = state_10849__$1;(statearr_10864_10899[(2)] = null);
(statearr_10864_10899[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (22)))
{var state_10849__$1 = state_10849;var statearr_10865_10900 = state_10849__$1;(statearr_10865_10900[(2)] = null);
(statearr_10865_10900[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (6)))
{var inst_10790 = (state_10849[(13)]);var inst_10799 = f.call(null,inst_10790);var inst_10800 = cljs.core.seq.call(null,inst_10799);var inst_10801 = inst_10800;var inst_10802 = null;var inst_10803 = (0);var inst_10804 = (0);var state_10849__$1 = (function (){var statearr_10866 = state_10849;(statearr_10866[(8)] = inst_10803);
(statearr_10866[(9)] = inst_10801);
(statearr_10866[(10)] = inst_10804);
(statearr_10866[(12)] = inst_10802);
return statearr_10866;
})();var statearr_10867_10901 = state_10849__$1;(statearr_10867_10901[(2)] = null);
(statearr_10867_10901[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (17)))
{var inst_10815 = (state_10849[(7)]);var inst_10819 = cljs.core.chunk_first.call(null,inst_10815);var inst_10820 = cljs.core.chunk_rest.call(null,inst_10815);var inst_10821 = cljs.core.count.call(null,inst_10819);var inst_10801 = inst_10820;var inst_10802 = inst_10819;var inst_10803 = inst_10821;var inst_10804 = (0);var state_10849__$1 = (function (){var statearr_10868 = state_10849;(statearr_10868[(8)] = inst_10803);
(statearr_10868[(9)] = inst_10801);
(statearr_10868[(10)] = inst_10804);
(statearr_10868[(12)] = inst_10802);
return statearr_10868;
})();var statearr_10869_10902 = state_10849__$1;(statearr_10869_10902[(2)] = null);
(statearr_10869_10902[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (3)))
{var inst_10847 = (state_10849[(2)]);var state_10849__$1 = state_10849;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10849__$1,inst_10847);
} else
{if((state_val_10850 === (12)))
{var inst_10835 = (state_10849[(2)]);var state_10849__$1 = state_10849;var statearr_10870_10903 = state_10849__$1;(statearr_10870_10903[(2)] = inst_10835);
(statearr_10870_10903[(1)] = (9));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (2)))
{var state_10849__$1 = state_10849;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_10849__$1,(4),in$);
} else
{if((state_val_10850 === (23)))
{var inst_10843 = (state_10849[(2)]);var state_10849__$1 = state_10849;var statearr_10871_10904 = state_10849__$1;(statearr_10871_10904[(2)] = inst_10843);
(statearr_10871_10904[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (19)))
{var inst_10830 = (state_10849[(2)]);var state_10849__$1 = state_10849;var statearr_10872_10905 = state_10849__$1;(statearr_10872_10905[(2)] = inst_10830);
(statearr_10872_10905[(1)] = (16));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (11)))
{var inst_10801 = (state_10849[(9)]);var inst_10815 = (state_10849[(7)]);var inst_10815__$1 = cljs.core.seq.call(null,inst_10801);var state_10849__$1 = (function (){var statearr_10873 = state_10849;(statearr_10873[(7)] = inst_10815__$1);
return statearr_10873;
})();if(inst_10815__$1)
{var statearr_10874_10906 = state_10849__$1;(statearr_10874_10906[(1)] = (14));
} else
{var statearr_10875_10907 = state_10849__$1;(statearr_10875_10907[(1)] = (15));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (9)))
{var inst_10837 = (state_10849[(2)]);var inst_10838 = cljs.core.async.impl.protocols.closed_QMARK_.call(null,out);var state_10849__$1 = (function (){var statearr_10876 = state_10849;(statearr_10876[(15)] = inst_10837);
return statearr_10876;
})();if(cljs.core.truth_(inst_10838))
{var statearr_10877_10908 = state_10849__$1;(statearr_10877_10908[(1)] = (21));
} else
{var statearr_10878_10909 = state_10849__$1;(statearr_10878_10909[(1)] = (22));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (5)))
{var inst_10793 = cljs.core.async.close_BANG_.call(null,out);var state_10849__$1 = state_10849;var statearr_10879_10910 = state_10849__$1;(statearr_10879_10910[(2)] = inst_10793);
(statearr_10879_10910[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (14)))
{var inst_10815 = (state_10849[(7)]);var inst_10817 = cljs.core.chunked_seq_QMARK_.call(null,inst_10815);var state_10849__$1 = state_10849;if(inst_10817)
{var statearr_10880_10911 = state_10849__$1;(statearr_10880_10911[(1)] = (17));
} else
{var statearr_10881_10912 = state_10849__$1;(statearr_10881_10912[(1)] = (18));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (16)))
{var inst_10833 = (state_10849[(2)]);var state_10849__$1 = state_10849;var statearr_10882_10913 = state_10849__$1;(statearr_10882_10913[(2)] = inst_10833);
(statearr_10882_10913[(1)] = (12));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10850 === (10)))
{var inst_10804 = (state_10849[(10)]);var inst_10802 = (state_10849[(12)]);var inst_10809 = cljs.core._nth.call(null,inst_10802,inst_10804);var state_10849__$1 = state_10849;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10849__$1,(13),out,inst_10809);
} else
{if((state_val_10850 === (18)))
{var inst_10815 = (state_10849[(7)]);var inst_10824 = cljs.core.first.call(null,inst_10815);var state_10849__$1 = state_10849;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10849__$1,(20),out,inst_10824);
} else
{if((state_val_10850 === (8)))
{var inst_10803 = (state_10849[(8)]);var inst_10804 = (state_10849[(10)]);var inst_10806 = (inst_10804 < inst_10803);var inst_10807 = inst_10806;var state_10849__$1 = state_10849;if(cljs.core.truth_(inst_10807))
{var statearr_10883_10914 = state_10849__$1;(statearr_10883_10914[(1)] = (10));
} else
{var statearr_10884_10915 = state_10849__$1;(statearr_10884_10915[(1)] = (11));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto__))
;return ((function (switch__6337__auto__,c__6352__auto__){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_10888 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_10888[(0)] = state_machine__6338__auto__);
(statearr_10888[(1)] = (1));
return statearr_10888;
});
var state_machine__6338__auto____1 = (function (state_10849){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_10849);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e10889){if((e10889 instanceof Object))
{var ex__6341__auto__ = e10889;var statearr_10890_10916 = state_10849;(statearr_10890_10916[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10849);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e10889;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__10917 = state_10849;
state_10849 = G__10917;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_10849){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_10849);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto__))
})();var state__6354__auto__ = (function (){var statearr_10891 = f__6353__auto__.call(null);(statearr_10891[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto__);
return statearr_10891;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto__))
);
return c__6352__auto__;
});
/**
* Takes a function and a source channel, and returns a channel which
* contains the values in each collection produced by applying f to
* each value taken from the source channel. f must return a
* collection.
* 
* The returned channel will be unbuffered by default, or a buf-or-n
* can be supplied. The channel will close when the source channel
* closes.
*/
cljs.core.async.mapcat_LT_ = (function() {
var mapcat_LT_ = null;
var mapcat_LT___2 = (function (f,in$){return mapcat_LT_.call(null,f,in$,null);
});
var mapcat_LT___3 = (function (f,in$,buf_or_n){var out = cljs.core.async.chan.call(null,buf_or_n);cljs.core.async.mapcat_STAR_.call(null,f,in$,out);
return out;
});
mapcat_LT_ = function(f,in$,buf_or_n){
switch(arguments.length){
case 2:
return mapcat_LT___2.call(this,f,in$);
case 3:
return mapcat_LT___3.call(this,f,in$,buf_or_n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
mapcat_LT_.cljs$core$IFn$_invoke$arity$2 = mapcat_LT___2;
mapcat_LT_.cljs$core$IFn$_invoke$arity$3 = mapcat_LT___3;
return mapcat_LT_;
})()
;
/**
* Takes a function and a target channel, and returns a channel which
* applies f to each value put, then supplies each element of the result
* to the target channel. f must return a collection.
* 
* The returned channel will be unbuffered by default, or a buf-or-n
* can be supplied. The target channel will be closed when the source
* channel closes.
*/
cljs.core.async.mapcat_GT_ = (function() {
var mapcat_GT_ = null;
var mapcat_GT___2 = (function (f,out){return mapcat_GT_.call(null,f,out,null);
});
var mapcat_GT___3 = (function (f,out,buf_or_n){var in$ = cljs.core.async.chan.call(null,buf_or_n);cljs.core.async.mapcat_STAR_.call(null,f,in$,out);
return in$;
});
mapcat_GT_ = function(f,out,buf_or_n){
switch(arguments.length){
case 2:
return mapcat_GT___2.call(this,f,out);
case 3:
return mapcat_GT___3.call(this,f,out,buf_or_n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
mapcat_GT_.cljs$core$IFn$_invoke$arity$2 = mapcat_GT___2;
mapcat_GT_.cljs$core$IFn$_invoke$arity$3 = mapcat_GT___3;
return mapcat_GT_;
})()
;
/**
* Takes elements from the from channel and supplies them to the to
* channel. By default, the to channel will be closed when the from
* channel closes, but can be determined by the close?  parameter. Will
* stop consuming the from channel if the to channel closes
*/
cljs.core.async.pipe = (function() {
var pipe = null;
var pipe__2 = (function (from,to){return pipe.call(null,from,to,true);
});
var pipe__3 = (function (from,to,close_QMARK_){var c__6352__auto___11012 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___11012){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___11012){
return (function (state_10988){var state_val_10989 = (state_10988[(1)]);if((state_val_10989 === (7)))
{var inst_10984 = (state_10988[(2)]);var state_10988__$1 = state_10988;var statearr_10990_11013 = state_10988__$1;(statearr_10990_11013[(2)] = inst_10984);
(statearr_10990_11013[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10989 === (1)))
{var state_10988__$1 = state_10988;var statearr_10991_11014 = state_10988__$1;(statearr_10991_11014[(2)] = null);
(statearr_10991_11014[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10989 === (4)))
{var inst_10967 = (state_10988[(7)]);var inst_10967__$1 = (state_10988[(2)]);var inst_10968 = (inst_10967__$1 == null);var state_10988__$1 = (function (){var statearr_10992 = state_10988;(statearr_10992[(7)] = inst_10967__$1);
return statearr_10992;
})();if(cljs.core.truth_(inst_10968))
{var statearr_10993_11015 = state_10988__$1;(statearr_10993_11015[(1)] = (5));
} else
{var statearr_10994_11016 = state_10988__$1;(statearr_10994_11016[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10989 === (13)))
{var state_10988__$1 = state_10988;var statearr_10995_11017 = state_10988__$1;(statearr_10995_11017[(2)] = null);
(statearr_10995_11017[(1)] = (14));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10989 === (6)))
{var inst_10967 = (state_10988[(7)]);var state_10988__$1 = state_10988;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10988__$1,(11),to,inst_10967);
} else
{if((state_val_10989 === (3)))
{var inst_10986 = (state_10988[(2)]);var state_10988__$1 = state_10988;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10988__$1,inst_10986);
} else
{if((state_val_10989 === (12)))
{var state_10988__$1 = state_10988;var statearr_10996_11018 = state_10988__$1;(statearr_10996_11018[(2)] = null);
(statearr_10996_11018[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10989 === (2)))
{var state_10988__$1 = state_10988;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_10988__$1,(4),from);
} else
{if((state_val_10989 === (11)))
{var inst_10977 = (state_10988[(2)]);var state_10988__$1 = state_10988;if(cljs.core.truth_(inst_10977))
{var statearr_10997_11019 = state_10988__$1;(statearr_10997_11019[(1)] = (12));
} else
{var statearr_10998_11020 = state_10988__$1;(statearr_10998_11020[(1)] = (13));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10989 === (9)))
{var state_10988__$1 = state_10988;var statearr_10999_11021 = state_10988__$1;(statearr_10999_11021[(2)] = null);
(statearr_10999_11021[(1)] = (10));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10989 === (5)))
{var state_10988__$1 = state_10988;if(cljs.core.truth_(close_QMARK_))
{var statearr_11000_11022 = state_10988__$1;(statearr_11000_11022[(1)] = (8));
} else
{var statearr_11001_11023 = state_10988__$1;(statearr_11001_11023[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10989 === (14)))
{var inst_10982 = (state_10988[(2)]);var state_10988__$1 = state_10988;var statearr_11002_11024 = state_10988__$1;(statearr_11002_11024[(2)] = inst_10982);
(statearr_11002_11024[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10989 === (10)))
{var inst_10974 = (state_10988[(2)]);var state_10988__$1 = state_10988;var statearr_11003_11025 = state_10988__$1;(statearr_11003_11025[(2)] = inst_10974);
(statearr_11003_11025[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10989 === (8)))
{var inst_10971 = cljs.core.async.close_BANG_.call(null,to);var state_10988__$1 = state_10988;var statearr_11004_11026 = state_10988__$1;(statearr_11004_11026[(2)] = inst_10971);
(statearr_11004_11026[(1)] = (10));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto___11012))
;return ((function (switch__6337__auto__,c__6352__auto___11012){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_11008 = [null,null,null,null,null,null,null,null];(statearr_11008[(0)] = state_machine__6338__auto__);
(statearr_11008[(1)] = (1));
return statearr_11008;
});
var state_machine__6338__auto____1 = (function (state_10988){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_10988);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e11009){if((e11009 instanceof Object))
{var ex__6341__auto__ = e11009;var statearr_11010_11027 = state_10988;(statearr_11010_11027[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10988);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e11009;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__11028 = state_10988;
state_10988 = G__11028;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_10988){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_10988);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___11012))
})();var state__6354__auto__ = (function (){var statearr_11011 = f__6353__auto__.call(null);(statearr_11011[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___11012);
return statearr_11011;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___11012))
);
return to;
});
pipe = function(from,to,close_QMARK_){
switch(arguments.length){
case 2:
return pipe__2.call(this,from,to);
case 3:
return pipe__3.call(this,from,to,close_QMARK_);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
pipe.cljs$core$IFn$_invoke$arity$2 = pipe__2;
pipe.cljs$core$IFn$_invoke$arity$3 = pipe__3;
return pipe;
})()
;
/**
* Takes a predicate and a source channel and returns a vector of two
* channels, the first of which will contain the values for which the
* predicate returned true, the second those for which it returned
* false.
* 
* The out channels will be unbuffered by default, or two buf-or-ns can
* be supplied. The channels will close after the source channel has
* closed.
*/
cljs.core.async.split = (function() {
var split = null;
var split__2 = (function (p,ch){return split.call(null,p,ch,null,null);
});
var split__4 = (function (p,ch,t_buf_or_n,f_buf_or_n){var tc = cljs.core.async.chan.call(null,t_buf_or_n);var fc = cljs.core.async.chan.call(null,f_buf_or_n);var c__6352__auto___11129 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___11129,tc,fc){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___11129,tc,fc){
return (function (state_11104){var state_val_11105 = (state_11104[(1)]);if((state_val_11105 === (7)))
{var inst_11100 = (state_11104[(2)]);var state_11104__$1 = state_11104;var statearr_11106_11130 = state_11104__$1;(statearr_11106_11130[(2)] = inst_11100);
(statearr_11106_11130[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11105 === (1)))
{var state_11104__$1 = state_11104;var statearr_11107_11131 = state_11104__$1;(statearr_11107_11131[(2)] = null);
(statearr_11107_11131[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11105 === (4)))
{var inst_11081 = (state_11104[(7)]);var inst_11081__$1 = (state_11104[(2)]);var inst_11082 = (inst_11081__$1 == null);var state_11104__$1 = (function (){var statearr_11108 = state_11104;(statearr_11108[(7)] = inst_11081__$1);
return statearr_11108;
})();if(cljs.core.truth_(inst_11082))
{var statearr_11109_11132 = state_11104__$1;(statearr_11109_11132[(1)] = (5));
} else
{var statearr_11110_11133 = state_11104__$1;(statearr_11110_11133[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11105 === (13)))
{var state_11104__$1 = state_11104;var statearr_11111_11134 = state_11104__$1;(statearr_11111_11134[(2)] = null);
(statearr_11111_11134[(1)] = (14));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11105 === (6)))
{var inst_11081 = (state_11104[(7)]);var inst_11087 = p.call(null,inst_11081);var state_11104__$1 = state_11104;if(cljs.core.truth_(inst_11087))
{var statearr_11112_11135 = state_11104__$1;(statearr_11112_11135[(1)] = (9));
} else
{var statearr_11113_11136 = state_11104__$1;(statearr_11113_11136[(1)] = (10));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11105 === (3)))
{var inst_11102 = (state_11104[(2)]);var state_11104__$1 = state_11104;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_11104__$1,inst_11102);
} else
{if((state_val_11105 === (12)))
{var state_11104__$1 = state_11104;var statearr_11114_11137 = state_11104__$1;(statearr_11114_11137[(2)] = null);
(statearr_11114_11137[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11105 === (2)))
{var state_11104__$1 = state_11104;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_11104__$1,(4),ch);
} else
{if((state_val_11105 === (11)))
{var inst_11081 = (state_11104[(7)]);var inst_11091 = (state_11104[(2)]);var state_11104__$1 = state_11104;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_11104__$1,(8),inst_11091,inst_11081);
} else
{if((state_val_11105 === (9)))
{var state_11104__$1 = state_11104;var statearr_11115_11138 = state_11104__$1;(statearr_11115_11138[(2)] = tc);
(statearr_11115_11138[(1)] = (11));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11105 === (5)))
{var inst_11084 = cljs.core.async.close_BANG_.call(null,tc);var inst_11085 = cljs.core.async.close_BANG_.call(null,fc);var state_11104__$1 = (function (){var statearr_11116 = state_11104;(statearr_11116[(8)] = inst_11084);
return statearr_11116;
})();var statearr_11117_11139 = state_11104__$1;(statearr_11117_11139[(2)] = inst_11085);
(statearr_11117_11139[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11105 === (14)))
{var inst_11098 = (state_11104[(2)]);var state_11104__$1 = state_11104;var statearr_11118_11140 = state_11104__$1;(statearr_11118_11140[(2)] = inst_11098);
(statearr_11118_11140[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11105 === (10)))
{var state_11104__$1 = state_11104;var statearr_11119_11141 = state_11104__$1;(statearr_11119_11141[(2)] = fc);
(statearr_11119_11141[(1)] = (11));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11105 === (8)))
{var inst_11093 = (state_11104[(2)]);var state_11104__$1 = state_11104;if(cljs.core.truth_(inst_11093))
{var statearr_11120_11142 = state_11104__$1;(statearr_11120_11142[(1)] = (12));
} else
{var statearr_11121_11143 = state_11104__$1;(statearr_11121_11143[(1)] = (13));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto___11129,tc,fc))
;return ((function (switch__6337__auto__,c__6352__auto___11129,tc,fc){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_11125 = [null,null,null,null,null,null,null,null,null];(statearr_11125[(0)] = state_machine__6338__auto__);
(statearr_11125[(1)] = (1));
return statearr_11125;
});
var state_machine__6338__auto____1 = (function (state_11104){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_11104);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e11126){if((e11126 instanceof Object))
{var ex__6341__auto__ = e11126;var statearr_11127_11144 = state_11104;(statearr_11127_11144[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_11104);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e11126;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__11145 = state_11104;
state_11104 = G__11145;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_11104){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_11104);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___11129,tc,fc))
})();var state__6354__auto__ = (function (){var statearr_11128 = f__6353__auto__.call(null);(statearr_11128[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___11129);
return statearr_11128;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___11129,tc,fc))
);
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [tc,fc], null);
});
split = function(p,ch,t_buf_or_n,f_buf_or_n){
switch(arguments.length){
case 2:
return split__2.call(this,p,ch);
case 4:
return split__4.call(this,p,ch,t_buf_or_n,f_buf_or_n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
split.cljs$core$IFn$_invoke$arity$2 = split__2;
split.cljs$core$IFn$_invoke$arity$4 = split__4;
return split;
})()
;
/**
* f should be a function of 2 arguments. Returns a channel containing
* the single result of applying f to init and the first item from the
* channel, then applying f to that result and the 2nd item, etc. If
* the channel closes without yielding items, returns init and f is not
* called. ch must close before reduce produces a result.
*/
cljs.core.async.reduce = (function reduce(f,init,ch){var c__6352__auto__ = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto__){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto__){
return (function (state_11192){var state_val_11193 = (state_11192[(1)]);if((state_val_11193 === (7)))
{var inst_11188 = (state_11192[(2)]);var state_11192__$1 = state_11192;var statearr_11194_11210 = state_11192__$1;(statearr_11194_11210[(2)] = inst_11188);
(statearr_11194_11210[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11193 === (6)))
{var inst_11178 = (state_11192[(7)]);var inst_11181 = (state_11192[(8)]);var inst_11185 = f.call(null,inst_11178,inst_11181);var inst_11178__$1 = inst_11185;var state_11192__$1 = (function (){var statearr_11195 = state_11192;(statearr_11195[(7)] = inst_11178__$1);
return statearr_11195;
})();var statearr_11196_11211 = state_11192__$1;(statearr_11196_11211[(2)] = null);
(statearr_11196_11211[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11193 === (5)))
{var inst_11178 = (state_11192[(7)]);var state_11192__$1 = state_11192;var statearr_11197_11212 = state_11192__$1;(statearr_11197_11212[(2)] = inst_11178);
(statearr_11197_11212[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11193 === (4)))
{var inst_11181 = (state_11192[(8)]);var inst_11181__$1 = (state_11192[(2)]);var inst_11182 = (inst_11181__$1 == null);var state_11192__$1 = (function (){var statearr_11198 = state_11192;(statearr_11198[(8)] = inst_11181__$1);
return statearr_11198;
})();if(cljs.core.truth_(inst_11182))
{var statearr_11199_11213 = state_11192__$1;(statearr_11199_11213[(1)] = (5));
} else
{var statearr_11200_11214 = state_11192__$1;(statearr_11200_11214[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11193 === (3)))
{var inst_11190 = (state_11192[(2)]);var state_11192__$1 = state_11192;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_11192__$1,inst_11190);
} else
{if((state_val_11193 === (2)))
{var state_11192__$1 = state_11192;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_11192__$1,(4),ch);
} else
{if((state_val_11193 === (1)))
{var inst_11178 = init;var state_11192__$1 = (function (){var statearr_11201 = state_11192;(statearr_11201[(7)] = inst_11178);
return statearr_11201;
})();var statearr_11202_11215 = state_11192__$1;(statearr_11202_11215[(2)] = null);
(statearr_11202_11215[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{return null;
}
}
}
}
}
}
}
});})(c__6352__auto__))
;return ((function (switch__6337__auto__,c__6352__auto__){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_11206 = [null,null,null,null,null,null,null,null,null];(statearr_11206[(0)] = state_machine__6338__auto__);
(statearr_11206[(1)] = (1));
return statearr_11206;
});
var state_machine__6338__auto____1 = (function (state_11192){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_11192);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e11207){if((e11207 instanceof Object))
{var ex__6341__auto__ = e11207;var statearr_11208_11216 = state_11192;(statearr_11208_11216[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_11192);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e11207;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__11217 = state_11192;
state_11192 = G__11217;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_11192){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_11192);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto__))
})();var state__6354__auto__ = (function (){var statearr_11209 = f__6353__auto__.call(null);(statearr_11209[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto__);
return statearr_11209;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto__))
);
return c__6352__auto__;
});
/**
* Puts the contents of coll into the supplied channel.
* 
* By default the channel will be closed after the items are copied,
* but can be determined by the close? parameter.
* 
* Returns a channel which will close after the items are copied.
*/
cljs.core.async.onto_chan = (function() {
var onto_chan = null;
var onto_chan__2 = (function (ch,coll){return onto_chan.call(null,ch,coll,true);
});
var onto_chan__3 = (function (ch,coll,close_QMARK_){var c__6352__auto__ = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto__){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto__){
return (function (state_11291){var state_val_11292 = (state_11291[(1)]);if((state_val_11292 === (7)))
{var inst_11273 = (state_11291[(2)]);var state_11291__$1 = state_11291;var statearr_11293_11316 = state_11291__$1;(statearr_11293_11316[(2)] = inst_11273);
(statearr_11293_11316[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11292 === (1)))
{var inst_11267 = cljs.core.seq.call(null,coll);var inst_11268 = inst_11267;var state_11291__$1 = (function (){var statearr_11294 = state_11291;(statearr_11294[(7)] = inst_11268);
return statearr_11294;
})();var statearr_11295_11317 = state_11291__$1;(statearr_11295_11317[(2)] = null);
(statearr_11295_11317[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11292 === (4)))
{var inst_11268 = (state_11291[(7)]);var inst_11271 = cljs.core.first.call(null,inst_11268);var state_11291__$1 = state_11291;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_11291__$1,(7),ch,inst_11271);
} else
{if((state_val_11292 === (13)))
{var inst_11285 = (state_11291[(2)]);var state_11291__$1 = state_11291;var statearr_11296_11318 = state_11291__$1;(statearr_11296_11318[(2)] = inst_11285);
(statearr_11296_11318[(1)] = (10));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11292 === (6)))
{var inst_11276 = (state_11291[(2)]);var state_11291__$1 = state_11291;if(cljs.core.truth_(inst_11276))
{var statearr_11297_11319 = state_11291__$1;(statearr_11297_11319[(1)] = (8));
} else
{var statearr_11298_11320 = state_11291__$1;(statearr_11298_11320[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11292 === (3)))
{var inst_11289 = (state_11291[(2)]);var state_11291__$1 = state_11291;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_11291__$1,inst_11289);
} else
{if((state_val_11292 === (12)))
{var state_11291__$1 = state_11291;var statearr_11299_11321 = state_11291__$1;(statearr_11299_11321[(2)] = null);
(statearr_11299_11321[(1)] = (13));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11292 === (2)))
{var inst_11268 = (state_11291[(7)]);var state_11291__$1 = state_11291;if(cljs.core.truth_(inst_11268))
{var statearr_11300_11322 = state_11291__$1;(statearr_11300_11322[(1)] = (4));
} else
{var statearr_11301_11323 = state_11291__$1;(statearr_11301_11323[(1)] = (5));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11292 === (11)))
{var inst_11282 = cljs.core.async.close_BANG_.call(null,ch);var state_11291__$1 = state_11291;var statearr_11302_11324 = state_11291__$1;(statearr_11302_11324[(2)] = inst_11282);
(statearr_11302_11324[(1)] = (13));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11292 === (9)))
{var state_11291__$1 = state_11291;if(cljs.core.truth_(close_QMARK_))
{var statearr_11303_11325 = state_11291__$1;(statearr_11303_11325[(1)] = (11));
} else
{var statearr_11304_11326 = state_11291__$1;(statearr_11304_11326[(1)] = (12));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11292 === (5)))
{var inst_11268 = (state_11291[(7)]);var state_11291__$1 = state_11291;var statearr_11305_11327 = state_11291__$1;(statearr_11305_11327[(2)] = inst_11268);
(statearr_11305_11327[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11292 === (10)))
{var inst_11287 = (state_11291[(2)]);var state_11291__$1 = state_11291;var statearr_11306_11328 = state_11291__$1;(statearr_11306_11328[(2)] = inst_11287);
(statearr_11306_11328[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11292 === (8)))
{var inst_11268 = (state_11291[(7)]);var inst_11278 = cljs.core.next.call(null,inst_11268);var inst_11268__$1 = inst_11278;var state_11291__$1 = (function (){var statearr_11307 = state_11291;(statearr_11307[(7)] = inst_11268__$1);
return statearr_11307;
})();var statearr_11308_11329 = state_11291__$1;(statearr_11308_11329[(2)] = null);
(statearr_11308_11329[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto__))
;return ((function (switch__6337__auto__,c__6352__auto__){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_11312 = [null,null,null,null,null,null,null,null];(statearr_11312[(0)] = state_machine__6338__auto__);
(statearr_11312[(1)] = (1));
return statearr_11312;
});
var state_machine__6338__auto____1 = (function (state_11291){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_11291);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e11313){if((e11313 instanceof Object))
{var ex__6341__auto__ = e11313;var statearr_11314_11330 = state_11291;(statearr_11314_11330[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_11291);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e11313;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__11331 = state_11291;
state_11291 = G__11331;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_11291){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_11291);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto__))
})();var state__6354__auto__ = (function (){var statearr_11315 = f__6353__auto__.call(null);(statearr_11315[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto__);
return statearr_11315;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto__))
);
return c__6352__auto__;
});
onto_chan = function(ch,coll,close_QMARK_){
switch(arguments.length){
case 2:
return onto_chan__2.call(this,ch,coll);
case 3:
return onto_chan__3.call(this,ch,coll,close_QMARK_);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
onto_chan.cljs$core$IFn$_invoke$arity$2 = onto_chan__2;
onto_chan.cljs$core$IFn$_invoke$arity$3 = onto_chan__3;
return onto_chan;
})()
;
/**
* Creates and returns a channel which contains the contents of coll,
* closing when exhausted.
*/
cljs.core.async.to_chan = (function to_chan(coll){var ch = cljs.core.async.chan.call(null,cljs.core.bounded_count.call(null,(100),coll));cljs.core.async.onto_chan.call(null,ch,coll);
return ch;
});
cljs.core.async.Mux = (function (){var obj11333 = {};return obj11333;
})();
cljs.core.async.muxch_STAR_ = (function muxch_STAR_(_){if((function (){var and__3530__auto__ = _;if(and__3530__auto__)
{return _.cljs$core$async$Mux$muxch_STAR_$arity$1;
} else
{return and__3530__auto__;
}
})())
{return _.cljs$core$async$Mux$muxch_STAR_$arity$1(_);
} else
{var x__4169__auto__ = (((_ == null))?null:_);return (function (){var or__3542__auto__ = (cljs.core.async.muxch_STAR_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (cljs.core.async.muxch_STAR_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Mux.muxch*",_);
}
}
})().call(null,_);
}
});
cljs.core.async.Mult = (function (){var obj11335 = {};return obj11335;
})();
cljs.core.async.tap_STAR_ = (function tap_STAR_(m,ch,close_QMARK_){if((function (){var and__3530__auto__ = m;if(and__3530__auto__)
{return m.cljs$core$async$Mult$tap_STAR_$arity$3;
} else
{return and__3530__auto__;
}
})())
{return m.cljs$core$async$Mult$tap_STAR_$arity$3(m,ch,close_QMARK_);
} else
{var x__4169__auto__ = (((m == null))?null:m);return (function (){var or__3542__auto__ = (cljs.core.async.tap_STAR_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (cljs.core.async.tap_STAR_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Mult.tap*",m);
}
}
})().call(null,m,ch,close_QMARK_);
}
});
cljs.core.async.untap_STAR_ = (function untap_STAR_(m,ch){if((function (){var and__3530__auto__ = m;if(and__3530__auto__)
{return m.cljs$core$async$Mult$untap_STAR_$arity$2;
} else
{return and__3530__auto__;
}
})())
{return m.cljs$core$async$Mult$untap_STAR_$arity$2(m,ch);
} else
{var x__4169__auto__ = (((m == null))?null:m);return (function (){var or__3542__auto__ = (cljs.core.async.untap_STAR_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (cljs.core.async.untap_STAR_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Mult.untap*",m);
}
}
})().call(null,m,ch);
}
});
cljs.core.async.untap_all_STAR_ = (function untap_all_STAR_(m){if((function (){var and__3530__auto__ = m;if(and__3530__auto__)
{return m.cljs$core$async$Mult$untap_all_STAR_$arity$1;
} else
{return and__3530__auto__;
}
})())
{return m.cljs$core$async$Mult$untap_all_STAR_$arity$1(m);
} else
{var x__4169__auto__ = (((m == null))?null:m);return (function (){var or__3542__auto__ = (cljs.core.async.untap_all_STAR_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (cljs.core.async.untap_all_STAR_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Mult.untap-all*",m);
}
}
})().call(null,m);
}
});
/**
* Creates and returns a mult(iple) of the supplied channel. Channels
* containing copies of the channel can be created with 'tap', and
* detached with 'untap'.
* 
* Each item is distributed to all taps in parallel and synchronously,
* i.e. each tap must accept before the next item is distributed. Use
* buffering/windowing to prevent slow taps from holding up the mult.
* 
* Items received when there are no taps get dropped.
* 
* If a tap puts to a closed channel, it will be removed from the mult.
*/
cljs.core.async.mult = (function mult(ch){var cs = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);var m = (function (){if(typeof cljs.core.async.t11557 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t11557 = (function (cs,ch,mult,meta11558){
this.cs = cs;
this.ch = ch;
this.mult = mult;
this.meta11558 = meta11558;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t11557.cljs$lang$type = true;
cljs.core.async.t11557.cljs$lang$ctorStr = "cljs.core.async/t11557";
cljs.core.async.t11557.cljs$lang$ctorPrWriter = ((function (cs){
return (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t11557");
});})(cs))
;
cljs.core.async.t11557.prototype.cljs$core$async$Mult$ = true;
cljs.core.async.t11557.prototype.cljs$core$async$Mult$tap_STAR_$arity$3 = ((function (cs){
return (function (_,ch__$2,close_QMARK_){var self__ = this;
var ___$1 = this;cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.assoc,ch__$2,close_QMARK_);
return null;
});})(cs))
;
cljs.core.async.t11557.prototype.cljs$core$async$Mult$untap_STAR_$arity$2 = ((function (cs){
return (function (_,ch__$2){var self__ = this;
var ___$1 = this;cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.dissoc,ch__$2);
return null;
});})(cs))
;
cljs.core.async.t11557.prototype.cljs$core$async$Mult$untap_all_STAR_$arity$1 = ((function (cs){
return (function (_){var self__ = this;
var ___$1 = this;cljs.core.reset_BANG_.call(null,self__.cs,cljs.core.PersistentArrayMap.EMPTY);
return null;
});})(cs))
;
cljs.core.async.t11557.prototype.cljs$core$async$Mux$ = true;
cljs.core.async.t11557.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (cs){
return (function (_){var self__ = this;
var ___$1 = this;return self__.ch;
});})(cs))
;
cljs.core.async.t11557.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (cs){
return (function (_11559){var self__ = this;
var _11559__$1 = this;return self__.meta11558;
});})(cs))
;
cljs.core.async.t11557.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (cs){
return (function (_11559,meta11558__$1){var self__ = this;
var _11559__$1 = this;return (new cljs.core.async.t11557(self__.cs,self__.ch,self__.mult,meta11558__$1));
});})(cs))
;
cljs.core.async.__GT_t11557 = ((function (cs){
return (function __GT_t11557(cs__$1,ch__$1,mult__$1,meta11558){return (new cljs.core.async.t11557(cs__$1,ch__$1,mult__$1,meta11558));
});})(cs))
;
}
return (new cljs.core.async.t11557(cs,ch,mult,null));
})();var dchan = cljs.core.async.chan.call(null,(1));var dctr = cljs.core.atom.call(null,null);var done = ((function (cs,m,dchan,dctr){
return (function (_){if((cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec) === (0)))
{return cljs.core.async.put_BANG_.call(null,dchan,true);
} else
{return null;
}
});})(cs,m,dchan,dctr))
;var c__6352__auto___11778 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___11778,cs,m,dchan,dctr,done){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___11778,cs,m,dchan,dctr,done){
return (function (state_11690){var state_val_11691 = (state_11690[(1)]);if((state_val_11691 === (7)))
{var inst_11686 = (state_11690[(2)]);var state_11690__$1 = state_11690;var statearr_11692_11779 = state_11690__$1;(statearr_11692_11779[(2)] = inst_11686);
(statearr_11692_11779[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (20)))
{var inst_11591 = (state_11690[(7)]);var inst_11601 = cljs.core.first.call(null,inst_11591);var inst_11602 = cljs.core.nth.call(null,inst_11601,(0),null);var inst_11603 = cljs.core.nth.call(null,inst_11601,(1),null);var state_11690__$1 = (function (){var statearr_11693 = state_11690;(statearr_11693[(8)] = inst_11602);
return statearr_11693;
})();if(cljs.core.truth_(inst_11603))
{var statearr_11694_11780 = state_11690__$1;(statearr_11694_11780[(1)] = (22));
} else
{var statearr_11695_11781 = state_11690__$1;(statearr_11695_11781[(1)] = (23));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (27)))
{var inst_11633 = (state_11690[(9)]);var inst_11631 = (state_11690[(10)]);var inst_11562 = (state_11690[(11)]);var inst_11638 = (state_11690[(12)]);var inst_11638__$1 = cljs.core._nth.call(null,inst_11631,inst_11633);var inst_11639 = cljs.core.async.put_BANG_.call(null,inst_11638__$1,inst_11562,done);var state_11690__$1 = (function (){var statearr_11696 = state_11690;(statearr_11696[(12)] = inst_11638__$1);
return statearr_11696;
})();if(cljs.core.truth_(inst_11639))
{var statearr_11697_11782 = state_11690__$1;(statearr_11697_11782[(1)] = (30));
} else
{var statearr_11698_11783 = state_11690__$1;(statearr_11698_11783[(1)] = (31));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (1)))
{var state_11690__$1 = state_11690;var statearr_11699_11784 = state_11690__$1;(statearr_11699_11784[(2)] = null);
(statearr_11699_11784[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (24)))
{var inst_11591 = (state_11690[(7)]);var inst_11608 = (state_11690[(2)]);var inst_11609 = cljs.core.next.call(null,inst_11591);var inst_11571 = inst_11609;var inst_11572 = null;var inst_11573 = (0);var inst_11574 = (0);var state_11690__$1 = (function (){var statearr_11700 = state_11690;(statearr_11700[(13)] = inst_11572);
(statearr_11700[(14)] = inst_11571);
(statearr_11700[(15)] = inst_11608);
(statearr_11700[(16)] = inst_11574);
(statearr_11700[(17)] = inst_11573);
return statearr_11700;
})();var statearr_11701_11785 = state_11690__$1;(statearr_11701_11785[(2)] = null);
(statearr_11701_11785[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (39)))
{var state_11690__$1 = state_11690;var statearr_11705_11786 = state_11690__$1;(statearr_11705_11786[(2)] = null);
(statearr_11705_11786[(1)] = (41));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (4)))
{var inst_11562 = (state_11690[(11)]);var inst_11562__$1 = (state_11690[(2)]);var inst_11563 = (inst_11562__$1 == null);var state_11690__$1 = (function (){var statearr_11706 = state_11690;(statearr_11706[(11)] = inst_11562__$1);
return statearr_11706;
})();if(cljs.core.truth_(inst_11563))
{var statearr_11707_11787 = state_11690__$1;(statearr_11707_11787[(1)] = (5));
} else
{var statearr_11708_11788 = state_11690__$1;(statearr_11708_11788[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (15)))
{var inst_11572 = (state_11690[(13)]);var inst_11571 = (state_11690[(14)]);var inst_11574 = (state_11690[(16)]);var inst_11573 = (state_11690[(17)]);var inst_11587 = (state_11690[(2)]);var inst_11588 = (inst_11574 + (1));var tmp11702 = inst_11572;var tmp11703 = inst_11571;var tmp11704 = inst_11573;var inst_11571__$1 = tmp11703;var inst_11572__$1 = tmp11702;var inst_11573__$1 = tmp11704;var inst_11574__$1 = inst_11588;var state_11690__$1 = (function (){var statearr_11709 = state_11690;(statearr_11709[(13)] = inst_11572__$1);
(statearr_11709[(14)] = inst_11571__$1);
(statearr_11709[(16)] = inst_11574__$1);
(statearr_11709[(17)] = inst_11573__$1);
(statearr_11709[(18)] = inst_11587);
return statearr_11709;
})();var statearr_11710_11789 = state_11690__$1;(statearr_11710_11789[(2)] = null);
(statearr_11710_11789[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (21)))
{var inst_11612 = (state_11690[(2)]);var state_11690__$1 = state_11690;var statearr_11714_11790 = state_11690__$1;(statearr_11714_11790[(2)] = inst_11612);
(statearr_11714_11790[(1)] = (18));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (31)))
{var inst_11638 = (state_11690[(12)]);var inst_11642 = cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec);var inst_11643 = cljs.core.async.untap_STAR_.call(null,m,inst_11638);var state_11690__$1 = (function (){var statearr_11715 = state_11690;(statearr_11715[(19)] = inst_11642);
return statearr_11715;
})();var statearr_11716_11791 = state_11690__$1;(statearr_11716_11791[(2)] = inst_11643);
(statearr_11716_11791[(1)] = (32));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (32)))
{var inst_11630 = (state_11690[(20)]);var inst_11633 = (state_11690[(9)]);var inst_11631 = (state_11690[(10)]);var inst_11632 = (state_11690[(21)]);var inst_11645 = (state_11690[(2)]);var inst_11646 = (inst_11633 + (1));var tmp11711 = inst_11630;var tmp11712 = inst_11631;var tmp11713 = inst_11632;var inst_11630__$1 = tmp11711;var inst_11631__$1 = tmp11712;var inst_11632__$1 = tmp11713;var inst_11633__$1 = inst_11646;var state_11690__$1 = (function (){var statearr_11717 = state_11690;(statearr_11717[(20)] = inst_11630__$1);
(statearr_11717[(9)] = inst_11633__$1);
(statearr_11717[(22)] = inst_11645);
(statearr_11717[(10)] = inst_11631__$1);
(statearr_11717[(21)] = inst_11632__$1);
return statearr_11717;
})();var statearr_11718_11792 = state_11690__$1;(statearr_11718_11792[(2)] = null);
(statearr_11718_11792[(1)] = (25));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (40)))
{var inst_11658 = (state_11690[(23)]);var inst_11662 = cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec);var inst_11663 = cljs.core.async.untap_STAR_.call(null,m,inst_11658);var state_11690__$1 = (function (){var statearr_11719 = state_11690;(statearr_11719[(24)] = inst_11662);
return statearr_11719;
})();var statearr_11720_11793 = state_11690__$1;(statearr_11720_11793[(2)] = inst_11663);
(statearr_11720_11793[(1)] = (41));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (33)))
{var inst_11649 = (state_11690[(25)]);var inst_11651 = cljs.core.chunked_seq_QMARK_.call(null,inst_11649);var state_11690__$1 = state_11690;if(inst_11651)
{var statearr_11721_11794 = state_11690__$1;(statearr_11721_11794[(1)] = (36));
} else
{var statearr_11722_11795 = state_11690__$1;(statearr_11722_11795[(1)] = (37));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (13)))
{var inst_11581 = (state_11690[(26)]);var inst_11584 = cljs.core.async.close_BANG_.call(null,inst_11581);var state_11690__$1 = state_11690;var statearr_11723_11796 = state_11690__$1;(statearr_11723_11796[(2)] = inst_11584);
(statearr_11723_11796[(1)] = (15));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (22)))
{var inst_11602 = (state_11690[(8)]);var inst_11605 = cljs.core.async.close_BANG_.call(null,inst_11602);var state_11690__$1 = state_11690;var statearr_11724_11797 = state_11690__$1;(statearr_11724_11797[(2)] = inst_11605);
(statearr_11724_11797[(1)] = (24));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (36)))
{var inst_11649 = (state_11690[(25)]);var inst_11653 = cljs.core.chunk_first.call(null,inst_11649);var inst_11654 = cljs.core.chunk_rest.call(null,inst_11649);var inst_11655 = cljs.core.count.call(null,inst_11653);var inst_11630 = inst_11654;var inst_11631 = inst_11653;var inst_11632 = inst_11655;var inst_11633 = (0);var state_11690__$1 = (function (){var statearr_11725 = state_11690;(statearr_11725[(20)] = inst_11630);
(statearr_11725[(9)] = inst_11633);
(statearr_11725[(10)] = inst_11631);
(statearr_11725[(21)] = inst_11632);
return statearr_11725;
})();var statearr_11726_11798 = state_11690__$1;(statearr_11726_11798[(2)] = null);
(statearr_11726_11798[(1)] = (25));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (41)))
{var inst_11649 = (state_11690[(25)]);var inst_11665 = (state_11690[(2)]);var inst_11666 = cljs.core.next.call(null,inst_11649);var inst_11630 = inst_11666;var inst_11631 = null;var inst_11632 = (0);var inst_11633 = (0);var state_11690__$1 = (function (){var statearr_11727 = state_11690;(statearr_11727[(20)] = inst_11630);
(statearr_11727[(9)] = inst_11633);
(statearr_11727[(10)] = inst_11631);
(statearr_11727[(21)] = inst_11632);
(statearr_11727[(27)] = inst_11665);
return statearr_11727;
})();var statearr_11728_11799 = state_11690__$1;(statearr_11728_11799[(2)] = null);
(statearr_11728_11799[(1)] = (25));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (43)))
{var state_11690__$1 = state_11690;var statearr_11729_11800 = state_11690__$1;(statearr_11729_11800[(2)] = null);
(statearr_11729_11800[(1)] = (44));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (29)))
{var inst_11674 = (state_11690[(2)]);var state_11690__$1 = state_11690;var statearr_11730_11801 = state_11690__$1;(statearr_11730_11801[(2)] = inst_11674);
(statearr_11730_11801[(1)] = (26));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (44)))
{var inst_11683 = (state_11690[(2)]);var state_11690__$1 = (function (){var statearr_11731 = state_11690;(statearr_11731[(28)] = inst_11683);
return statearr_11731;
})();var statearr_11732_11802 = state_11690__$1;(statearr_11732_11802[(2)] = null);
(statearr_11732_11802[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (6)))
{var inst_11622 = (state_11690[(29)]);var inst_11621 = cljs.core.deref.call(null,cs);var inst_11622__$1 = cljs.core.keys.call(null,inst_11621);var inst_11623 = cljs.core.count.call(null,inst_11622__$1);var inst_11624 = cljs.core.reset_BANG_.call(null,dctr,inst_11623);var inst_11629 = cljs.core.seq.call(null,inst_11622__$1);var inst_11630 = inst_11629;var inst_11631 = null;var inst_11632 = (0);var inst_11633 = (0);var state_11690__$1 = (function (){var statearr_11733 = state_11690;(statearr_11733[(20)] = inst_11630);
(statearr_11733[(9)] = inst_11633);
(statearr_11733[(30)] = inst_11624);
(statearr_11733[(10)] = inst_11631);
(statearr_11733[(29)] = inst_11622__$1);
(statearr_11733[(21)] = inst_11632);
return statearr_11733;
})();var statearr_11734_11803 = state_11690__$1;(statearr_11734_11803[(2)] = null);
(statearr_11734_11803[(1)] = (25));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (28)))
{var inst_11630 = (state_11690[(20)]);var inst_11649 = (state_11690[(25)]);var inst_11649__$1 = cljs.core.seq.call(null,inst_11630);var state_11690__$1 = (function (){var statearr_11735 = state_11690;(statearr_11735[(25)] = inst_11649__$1);
return statearr_11735;
})();if(inst_11649__$1)
{var statearr_11736_11804 = state_11690__$1;(statearr_11736_11804[(1)] = (33));
} else
{var statearr_11737_11805 = state_11690__$1;(statearr_11737_11805[(1)] = (34));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (25)))
{var inst_11633 = (state_11690[(9)]);var inst_11632 = (state_11690[(21)]);var inst_11635 = (inst_11633 < inst_11632);var inst_11636 = inst_11635;var state_11690__$1 = state_11690;if(cljs.core.truth_(inst_11636))
{var statearr_11738_11806 = state_11690__$1;(statearr_11738_11806[(1)] = (27));
} else
{var statearr_11739_11807 = state_11690__$1;(statearr_11739_11807[(1)] = (28));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (34)))
{var state_11690__$1 = state_11690;var statearr_11740_11808 = state_11690__$1;(statearr_11740_11808[(2)] = null);
(statearr_11740_11808[(1)] = (35));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (17)))
{var state_11690__$1 = state_11690;var statearr_11741_11809 = state_11690__$1;(statearr_11741_11809[(2)] = null);
(statearr_11741_11809[(1)] = (18));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (3)))
{var inst_11688 = (state_11690[(2)]);var state_11690__$1 = state_11690;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_11690__$1,inst_11688);
} else
{if((state_val_11691 === (12)))
{var inst_11617 = (state_11690[(2)]);var state_11690__$1 = state_11690;var statearr_11742_11810 = state_11690__$1;(statearr_11742_11810[(2)] = inst_11617);
(statearr_11742_11810[(1)] = (9));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (2)))
{var state_11690__$1 = state_11690;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_11690__$1,(4),ch);
} else
{if((state_val_11691 === (23)))
{var state_11690__$1 = state_11690;var statearr_11743_11811 = state_11690__$1;(statearr_11743_11811[(2)] = null);
(statearr_11743_11811[(1)] = (24));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (35)))
{var inst_11672 = (state_11690[(2)]);var state_11690__$1 = state_11690;var statearr_11744_11812 = state_11690__$1;(statearr_11744_11812[(2)] = inst_11672);
(statearr_11744_11812[(1)] = (29));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (19)))
{var inst_11591 = (state_11690[(7)]);var inst_11595 = cljs.core.chunk_first.call(null,inst_11591);var inst_11596 = cljs.core.chunk_rest.call(null,inst_11591);var inst_11597 = cljs.core.count.call(null,inst_11595);var inst_11571 = inst_11596;var inst_11572 = inst_11595;var inst_11573 = inst_11597;var inst_11574 = (0);var state_11690__$1 = (function (){var statearr_11745 = state_11690;(statearr_11745[(13)] = inst_11572);
(statearr_11745[(14)] = inst_11571);
(statearr_11745[(16)] = inst_11574);
(statearr_11745[(17)] = inst_11573);
return statearr_11745;
})();var statearr_11746_11813 = state_11690__$1;(statearr_11746_11813[(2)] = null);
(statearr_11746_11813[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (11)))
{var inst_11591 = (state_11690[(7)]);var inst_11571 = (state_11690[(14)]);var inst_11591__$1 = cljs.core.seq.call(null,inst_11571);var state_11690__$1 = (function (){var statearr_11747 = state_11690;(statearr_11747[(7)] = inst_11591__$1);
return statearr_11747;
})();if(inst_11591__$1)
{var statearr_11748_11814 = state_11690__$1;(statearr_11748_11814[(1)] = (16));
} else
{var statearr_11749_11815 = state_11690__$1;(statearr_11749_11815[(1)] = (17));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (9)))
{var inst_11619 = (state_11690[(2)]);var state_11690__$1 = state_11690;var statearr_11750_11816 = state_11690__$1;(statearr_11750_11816[(2)] = inst_11619);
(statearr_11750_11816[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (5)))
{var inst_11569 = cljs.core.deref.call(null,cs);var inst_11570 = cljs.core.seq.call(null,inst_11569);var inst_11571 = inst_11570;var inst_11572 = null;var inst_11573 = (0);var inst_11574 = (0);var state_11690__$1 = (function (){var statearr_11751 = state_11690;(statearr_11751[(13)] = inst_11572);
(statearr_11751[(14)] = inst_11571);
(statearr_11751[(16)] = inst_11574);
(statearr_11751[(17)] = inst_11573);
return statearr_11751;
})();var statearr_11752_11817 = state_11690__$1;(statearr_11752_11817[(2)] = null);
(statearr_11752_11817[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (14)))
{var state_11690__$1 = state_11690;var statearr_11753_11818 = state_11690__$1;(statearr_11753_11818[(2)] = null);
(statearr_11753_11818[(1)] = (15));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (45)))
{var inst_11680 = (state_11690[(2)]);var state_11690__$1 = state_11690;var statearr_11754_11819 = state_11690__$1;(statearr_11754_11819[(2)] = inst_11680);
(statearr_11754_11819[(1)] = (44));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (26)))
{var inst_11622 = (state_11690[(29)]);var inst_11676 = (state_11690[(2)]);var inst_11677 = cljs.core.seq.call(null,inst_11622);var state_11690__$1 = (function (){var statearr_11755 = state_11690;(statearr_11755[(31)] = inst_11676);
return statearr_11755;
})();if(inst_11677)
{var statearr_11756_11820 = state_11690__$1;(statearr_11756_11820[(1)] = (42));
} else
{var statearr_11757_11821 = state_11690__$1;(statearr_11757_11821[(1)] = (43));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (16)))
{var inst_11591 = (state_11690[(7)]);var inst_11593 = cljs.core.chunked_seq_QMARK_.call(null,inst_11591);var state_11690__$1 = state_11690;if(inst_11593)
{var statearr_11758_11822 = state_11690__$1;(statearr_11758_11822[(1)] = (19));
} else
{var statearr_11759_11823 = state_11690__$1;(statearr_11759_11823[(1)] = (20));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (38)))
{var inst_11669 = (state_11690[(2)]);var state_11690__$1 = state_11690;var statearr_11760_11824 = state_11690__$1;(statearr_11760_11824[(2)] = inst_11669);
(statearr_11760_11824[(1)] = (35));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (30)))
{var state_11690__$1 = state_11690;var statearr_11761_11825 = state_11690__$1;(statearr_11761_11825[(2)] = null);
(statearr_11761_11825[(1)] = (32));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (10)))
{var inst_11572 = (state_11690[(13)]);var inst_11574 = (state_11690[(16)]);var inst_11580 = cljs.core._nth.call(null,inst_11572,inst_11574);var inst_11581 = cljs.core.nth.call(null,inst_11580,(0),null);var inst_11582 = cljs.core.nth.call(null,inst_11580,(1),null);var state_11690__$1 = (function (){var statearr_11762 = state_11690;(statearr_11762[(26)] = inst_11581);
return statearr_11762;
})();if(cljs.core.truth_(inst_11582))
{var statearr_11763_11826 = state_11690__$1;(statearr_11763_11826[(1)] = (13));
} else
{var statearr_11764_11827 = state_11690__$1;(statearr_11764_11827[(1)] = (14));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (18)))
{var inst_11615 = (state_11690[(2)]);var state_11690__$1 = state_11690;var statearr_11765_11828 = state_11690__$1;(statearr_11765_11828[(2)] = inst_11615);
(statearr_11765_11828[(1)] = (12));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (42)))
{var state_11690__$1 = state_11690;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_11690__$1,(45),dchan);
} else
{if((state_val_11691 === (37)))
{var inst_11658 = (state_11690[(23)]);var inst_11562 = (state_11690[(11)]);var inst_11649 = (state_11690[(25)]);var inst_11658__$1 = cljs.core.first.call(null,inst_11649);var inst_11659 = cljs.core.async.put_BANG_.call(null,inst_11658__$1,inst_11562,done);var state_11690__$1 = (function (){var statearr_11766 = state_11690;(statearr_11766[(23)] = inst_11658__$1);
return statearr_11766;
})();if(cljs.core.truth_(inst_11659))
{var statearr_11767_11829 = state_11690__$1;(statearr_11767_11829[(1)] = (39));
} else
{var statearr_11768_11830 = state_11690__$1;(statearr_11768_11830[(1)] = (40));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11691 === (8)))
{var inst_11574 = (state_11690[(16)]);var inst_11573 = (state_11690[(17)]);var inst_11576 = (inst_11574 < inst_11573);var inst_11577 = inst_11576;var state_11690__$1 = state_11690;if(cljs.core.truth_(inst_11577))
{var statearr_11769_11831 = state_11690__$1;(statearr_11769_11831[(1)] = (10));
} else
{var statearr_11770_11832 = state_11690__$1;(statearr_11770_11832[(1)] = (11));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto___11778,cs,m,dchan,dctr,done))
;return ((function (switch__6337__auto__,c__6352__auto___11778,cs,m,dchan,dctr,done){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_11774 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_11774[(0)] = state_machine__6338__auto__);
(statearr_11774[(1)] = (1));
return statearr_11774;
});
var state_machine__6338__auto____1 = (function (state_11690){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_11690);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e11775){if((e11775 instanceof Object))
{var ex__6341__auto__ = e11775;var statearr_11776_11833 = state_11690;(statearr_11776_11833[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_11690);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e11775;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__11834 = state_11690;
state_11690 = G__11834;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_11690){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_11690);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___11778,cs,m,dchan,dctr,done))
})();var state__6354__auto__ = (function (){var statearr_11777 = f__6353__auto__.call(null);(statearr_11777[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___11778);
return statearr_11777;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___11778,cs,m,dchan,dctr,done))
);
return m;
});
/**
* Copies the mult source onto the supplied channel.
* 
* By default the channel will be closed when the source closes,
* but can be determined by the close? parameter.
*/
cljs.core.async.tap = (function() {
var tap = null;
var tap__2 = (function (mult,ch){return tap.call(null,mult,ch,true);
});
var tap__3 = (function (mult,ch,close_QMARK_){cljs.core.async.tap_STAR_.call(null,mult,ch,close_QMARK_);
return ch;
});
tap = function(mult,ch,close_QMARK_){
switch(arguments.length){
case 2:
return tap__2.call(this,mult,ch);
case 3:
return tap__3.call(this,mult,ch,close_QMARK_);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
tap.cljs$core$IFn$_invoke$arity$2 = tap__2;
tap.cljs$core$IFn$_invoke$arity$3 = tap__3;
return tap;
})()
;
/**
* Disconnects a target channel from a mult
*/
cljs.core.async.untap = (function untap(mult,ch){return cljs.core.async.untap_STAR_.call(null,mult,ch);
});
/**
* Disconnects all target channels from a mult
*/
cljs.core.async.untap_all = (function untap_all(mult){return cljs.core.async.untap_all_STAR_.call(null,mult);
});
cljs.core.async.Mix = (function (){var obj11836 = {};return obj11836;
})();
cljs.core.async.admix_STAR_ = (function admix_STAR_(m,ch){if((function (){var and__3530__auto__ = m;if(and__3530__auto__)
{return m.cljs$core$async$Mix$admix_STAR_$arity$2;
} else
{return and__3530__auto__;
}
})())
{return m.cljs$core$async$Mix$admix_STAR_$arity$2(m,ch);
} else
{var x__4169__auto__ = (((m == null))?null:m);return (function (){var or__3542__auto__ = (cljs.core.async.admix_STAR_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (cljs.core.async.admix_STAR_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Mix.admix*",m);
}
}
})().call(null,m,ch);
}
});
cljs.core.async.unmix_STAR_ = (function unmix_STAR_(m,ch){if((function (){var and__3530__auto__ = m;if(and__3530__auto__)
{return m.cljs$core$async$Mix$unmix_STAR_$arity$2;
} else
{return and__3530__auto__;
}
})())
{return m.cljs$core$async$Mix$unmix_STAR_$arity$2(m,ch);
} else
{var x__4169__auto__ = (((m == null))?null:m);return (function (){var or__3542__auto__ = (cljs.core.async.unmix_STAR_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (cljs.core.async.unmix_STAR_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Mix.unmix*",m);
}
}
})().call(null,m,ch);
}
});
cljs.core.async.unmix_all_STAR_ = (function unmix_all_STAR_(m){if((function (){var and__3530__auto__ = m;if(and__3530__auto__)
{return m.cljs$core$async$Mix$unmix_all_STAR_$arity$1;
} else
{return and__3530__auto__;
}
})())
{return m.cljs$core$async$Mix$unmix_all_STAR_$arity$1(m);
} else
{var x__4169__auto__ = (((m == null))?null:m);return (function (){var or__3542__auto__ = (cljs.core.async.unmix_all_STAR_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (cljs.core.async.unmix_all_STAR_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Mix.unmix-all*",m);
}
}
})().call(null,m);
}
});
cljs.core.async.toggle_STAR_ = (function toggle_STAR_(m,state_map){if((function (){var and__3530__auto__ = m;if(and__3530__auto__)
{return m.cljs$core$async$Mix$toggle_STAR_$arity$2;
} else
{return and__3530__auto__;
}
})())
{return m.cljs$core$async$Mix$toggle_STAR_$arity$2(m,state_map);
} else
{var x__4169__auto__ = (((m == null))?null:m);return (function (){var or__3542__auto__ = (cljs.core.async.toggle_STAR_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (cljs.core.async.toggle_STAR_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Mix.toggle*",m);
}
}
})().call(null,m,state_map);
}
});
cljs.core.async.solo_mode_STAR_ = (function solo_mode_STAR_(m,mode){if((function (){var and__3530__auto__ = m;if(and__3530__auto__)
{return m.cljs$core$async$Mix$solo_mode_STAR_$arity$2;
} else
{return and__3530__auto__;
}
})())
{return m.cljs$core$async$Mix$solo_mode_STAR_$arity$2(m,mode);
} else
{var x__4169__auto__ = (((m == null))?null:m);return (function (){var or__3542__auto__ = (cljs.core.async.solo_mode_STAR_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (cljs.core.async.solo_mode_STAR_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Mix.solo-mode*",m);
}
}
})().call(null,m,mode);
}
});
/**
* Creates and returns a mix of one or more input channels which will
* be put on the supplied out channel. Input sources can be added to
* the mix with 'admix', and removed with 'unmix'. A mix supports
* soloing, muting and pausing multiple inputs atomically using
* 'toggle', and can solo using either muting or pausing as determined
* by 'solo-mode'.
* 
* Each channel can have zero or more boolean modes set via 'toggle':
* 
* :solo - when true, only this (ond other soloed) channel(s) will appear
* in the mix output channel. :mute and :pause states of soloed
* channels are ignored. If solo-mode is :mute, non-soloed
* channels are muted, if :pause, non-soloed channels are
* paused.
* 
* :mute - muted channels will have their contents consumed but not included in the mix
* :pause - paused channels will not have their contents consumed (and thus also not included in the mix)
*/
cljs.core.async.mix = (function mix(out){var cs = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);var solo_modes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"pause","pause",-2095325672),null,new cljs.core.Keyword(null,"mute","mute",1151223646),null], null), null);var attrs = cljs.core.conj.call(null,solo_modes,new cljs.core.Keyword(null,"solo","solo",-316350075));var solo_mode = cljs.core.atom.call(null,new cljs.core.Keyword(null,"mute","mute",1151223646));var change = cljs.core.async.chan.call(null);var changed = ((function (cs,solo_modes,attrs,solo_mode,change){
return (function (){return cljs.core.async.put_BANG_.call(null,change,true);
});})(cs,solo_modes,attrs,solo_mode,change))
;var pick = ((function (cs,solo_modes,attrs,solo_mode,change,changed){
return (function (attr,chs){return cljs.core.reduce_kv.call(null,((function (cs,solo_modes,attrs,solo_mode,change,changed){
return (function (ret,c,v){if(cljs.core.truth_(attr.call(null,v)))
{return cljs.core.conj.call(null,ret,c);
} else
{return ret;
}
});})(cs,solo_modes,attrs,solo_mode,change,changed))
,cljs.core.PersistentHashSet.EMPTY,chs);
});})(cs,solo_modes,attrs,solo_mode,change,changed))
;var calc_state = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick){
return (function (){var chs = cljs.core.deref.call(null,cs);var mode = cljs.core.deref.call(null,solo_mode);var solos = pick.call(null,new cljs.core.Keyword(null,"solo","solo",-316350075),chs);var pauses = pick.call(null,new cljs.core.Keyword(null,"pause","pause",-2095325672),chs);return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"solos","solos",1441458643),solos,new cljs.core.Keyword(null,"mutes","mutes",1068806309),pick.call(null,new cljs.core.Keyword(null,"mute","mute",1151223646),chs),new cljs.core.Keyword(null,"reads","reads",-1215067361),cljs.core.conj.call(null,(((cljs.core._EQ_.call(null,mode,new cljs.core.Keyword(null,"pause","pause",-2095325672))) && (!(cljs.core.empty_QMARK_.call(null,solos))))?cljs.core.vec.call(null,solos):cljs.core.vec.call(null,cljs.core.remove.call(null,pauses,cljs.core.keys.call(null,chs)))),change)], null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick))
;var m = (function (){if(typeof cljs.core.async.t11956 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t11956 = (function (change,mix,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta11957){
this.change = change;
this.mix = mix;
this.solo_mode = solo_mode;
this.pick = pick;
this.cs = cs;
this.calc_state = calc_state;
this.out = out;
this.changed = changed;
this.solo_modes = solo_modes;
this.attrs = attrs;
this.meta11957 = meta11957;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t11956.cljs$lang$type = true;
cljs.core.async.t11956.cljs$lang$ctorStr = "cljs.core.async/t11956";
cljs.core.async.t11956.cljs$lang$ctorPrWriter = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t11956");
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11956.prototype.cljs$core$async$Mix$ = true;
cljs.core.async.t11956.prototype.cljs$core$async$Mix$admix_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,ch){var self__ = this;
var ___$1 = this;cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.assoc,ch,cljs.core.PersistentArrayMap.EMPTY);
return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11956.prototype.cljs$core$async$Mix$unmix_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,ch){var self__ = this;
var ___$1 = this;cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.dissoc,ch);
return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11956.prototype.cljs$core$async$Mix$unmix_all_STAR_$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_){var self__ = this;
var ___$1 = this;cljs.core.reset_BANG_.call(null,self__.cs,cljs.core.PersistentArrayMap.EMPTY);
return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11956.prototype.cljs$core$async$Mix$toggle_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,state_map){var self__ = this;
var ___$1 = this;cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.partial.call(null,cljs.core.merge_with,cljs.core.merge),state_map);
return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11956.prototype.cljs$core$async$Mix$solo_mode_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,mode){var self__ = this;
var ___$1 = this;if(cljs.core.truth_(self__.solo_modes.call(null,mode)))
{} else
{throw (new Error(("Assert failed: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(("mode must be one of: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(self__.solo_modes)))+"\n"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,cljs.core.list(new cljs.core.Symbol(null,"solo-modes","solo-modes",882180540,null),new cljs.core.Symbol(null,"mode","mode",-2000032078,null)))))));
}
cljs.core.reset_BANG_.call(null,self__.solo_mode,mode);
return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11956.prototype.cljs$core$async$Mux$ = true;
cljs.core.async.t11956.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_){var self__ = this;
var ___$1 = this;return self__.out;
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11956.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_11958){var self__ = this;
var _11958__$1 = this;return self__.meta11957;
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11956.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_11958,meta11957__$1){var self__ = this;
var _11958__$1 = this;return (new cljs.core.async.t11956(self__.change,self__.mix,self__.solo_mode,self__.pick,self__.cs,self__.calc_state,self__.out,self__.changed,self__.solo_modes,self__.attrs,meta11957__$1));
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.__GT_t11956 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function __GT_t11956(change__$1,mix__$1,solo_mode__$1,pick__$1,cs__$1,calc_state__$1,out__$1,changed__$1,solo_modes__$1,attrs__$1,meta11957){return (new cljs.core.async.t11956(change__$1,mix__$1,solo_mode__$1,pick__$1,cs__$1,calc_state__$1,out__$1,changed__$1,solo_modes__$1,attrs__$1,meta11957));
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
}
return (new cljs.core.async.t11956(change,mix,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,null));
})();var c__6352__auto___12075 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___12075,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___12075,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function (state_12028){var state_val_12029 = (state_12028[(1)]);if((state_val_12029 === (7)))
{var inst_11972 = (state_12028[(7)]);var inst_11977 = cljs.core.apply.call(null,cljs.core.hash_map,inst_11972);var state_12028__$1 = state_12028;var statearr_12030_12076 = state_12028__$1;(statearr_12030_12076[(2)] = inst_11977);
(statearr_12030_12076[(1)] = (9));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (20)))
{var inst_11987 = (state_12028[(8)]);var state_12028__$1 = state_12028;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12028__$1,(23),out,inst_11987);
} else
{if((state_val_12029 === (1)))
{var inst_11962 = (state_12028[(9)]);var inst_11962__$1 = calc_state.call(null);var inst_11963 = cljs.core.seq_QMARK_.call(null,inst_11962__$1);var state_12028__$1 = (function (){var statearr_12031 = state_12028;(statearr_12031[(9)] = inst_11962__$1);
return statearr_12031;
})();if(inst_11963)
{var statearr_12032_12077 = state_12028__$1;(statearr_12032_12077[(1)] = (2));
} else
{var statearr_12033_12078 = state_12028__$1;(statearr_12033_12078[(1)] = (3));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (24)))
{var inst_11980 = (state_12028[(10)]);var inst_11972 = inst_11980;var state_12028__$1 = (function (){var statearr_12034 = state_12028;(statearr_12034[(7)] = inst_11972);
return statearr_12034;
})();var statearr_12035_12079 = state_12028__$1;(statearr_12035_12079[(2)] = null);
(statearr_12035_12079[(1)] = (5));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (4)))
{var inst_11962 = (state_12028[(9)]);var inst_11968 = (state_12028[(2)]);var inst_11969 = cljs.core.get.call(null,inst_11968,new cljs.core.Keyword(null,"reads","reads",-1215067361));var inst_11970 = cljs.core.get.call(null,inst_11968,new cljs.core.Keyword(null,"mutes","mutes",1068806309));var inst_11971 = cljs.core.get.call(null,inst_11968,new cljs.core.Keyword(null,"solos","solos",1441458643));var inst_11972 = inst_11962;var state_12028__$1 = (function (){var statearr_12036 = state_12028;(statearr_12036[(7)] = inst_11972);
(statearr_12036[(11)] = inst_11971);
(statearr_12036[(12)] = inst_11970);
(statearr_12036[(13)] = inst_11969);
return statearr_12036;
})();var statearr_12037_12080 = state_12028__$1;(statearr_12037_12080[(2)] = null);
(statearr_12037_12080[(1)] = (5));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (15)))
{var state_12028__$1 = state_12028;var statearr_12038_12081 = state_12028__$1;(statearr_12038_12081[(2)] = null);
(statearr_12038_12081[(1)] = (16));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (21)))
{var inst_11980 = (state_12028[(10)]);var inst_11972 = inst_11980;var state_12028__$1 = (function (){var statearr_12039 = state_12028;(statearr_12039[(7)] = inst_11972);
return statearr_12039;
})();var statearr_12040_12082 = state_12028__$1;(statearr_12040_12082[(2)] = null);
(statearr_12040_12082[(1)] = (5));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (13)))
{var inst_12024 = (state_12028[(2)]);var state_12028__$1 = state_12028;var statearr_12041_12083 = state_12028__$1;(statearr_12041_12083[(2)] = inst_12024);
(statearr_12041_12083[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (22)))
{var inst_12022 = (state_12028[(2)]);var state_12028__$1 = state_12028;var statearr_12042_12084 = state_12028__$1;(statearr_12042_12084[(2)] = inst_12022);
(statearr_12042_12084[(1)] = (13));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (6)))
{var inst_12026 = (state_12028[(2)]);var state_12028__$1 = state_12028;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12028__$1,inst_12026);
} else
{if((state_val_12029 === (25)))
{var state_12028__$1 = state_12028;var statearr_12043_12085 = state_12028__$1;(statearr_12043_12085[(2)] = null);
(statearr_12043_12085[(1)] = (26));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (17)))
{var inst_12002 = (state_12028[(14)]);var state_12028__$1 = state_12028;var statearr_12044_12086 = state_12028__$1;(statearr_12044_12086[(2)] = inst_12002);
(statearr_12044_12086[(1)] = (19));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (3)))
{var inst_11962 = (state_12028[(9)]);var state_12028__$1 = state_12028;var statearr_12045_12087 = state_12028__$1;(statearr_12045_12087[(2)] = inst_11962);
(statearr_12045_12087[(1)] = (4));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (12)))
{var inst_12002 = (state_12028[(14)]);var inst_11988 = (state_12028[(15)]);var inst_11983 = (state_12028[(16)]);var inst_12002__$1 = inst_11983.call(null,inst_11988);var state_12028__$1 = (function (){var statearr_12046 = state_12028;(statearr_12046[(14)] = inst_12002__$1);
return statearr_12046;
})();if(cljs.core.truth_(inst_12002__$1))
{var statearr_12047_12088 = state_12028__$1;(statearr_12047_12088[(1)] = (17));
} else
{var statearr_12048_12089 = state_12028__$1;(statearr_12048_12089[(1)] = (18));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (2)))
{var inst_11962 = (state_12028[(9)]);var inst_11965 = cljs.core.apply.call(null,cljs.core.hash_map,inst_11962);var state_12028__$1 = state_12028;var statearr_12049_12090 = state_12028__$1;(statearr_12049_12090[(2)] = inst_11965);
(statearr_12049_12090[(1)] = (4));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (23)))
{var inst_12013 = (state_12028[(2)]);var state_12028__$1 = state_12028;if(cljs.core.truth_(inst_12013))
{var statearr_12050_12091 = state_12028__$1;(statearr_12050_12091[(1)] = (24));
} else
{var statearr_12051_12092 = state_12028__$1;(statearr_12051_12092[(1)] = (25));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (19)))
{var inst_12010 = (state_12028[(2)]);var state_12028__$1 = state_12028;if(cljs.core.truth_(inst_12010))
{var statearr_12052_12093 = state_12028__$1;(statearr_12052_12093[(1)] = (20));
} else
{var statearr_12053_12094 = state_12028__$1;(statearr_12053_12094[(1)] = (21));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (11)))
{var inst_11987 = (state_12028[(8)]);var inst_11993 = (inst_11987 == null);var state_12028__$1 = state_12028;if(cljs.core.truth_(inst_11993))
{var statearr_12054_12095 = state_12028__$1;(statearr_12054_12095[(1)] = (14));
} else
{var statearr_12055_12096 = state_12028__$1;(statearr_12055_12096[(1)] = (15));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (9)))
{var inst_11980 = (state_12028[(10)]);var inst_11980__$1 = (state_12028[(2)]);var inst_11981 = cljs.core.get.call(null,inst_11980__$1,new cljs.core.Keyword(null,"reads","reads",-1215067361));var inst_11982 = cljs.core.get.call(null,inst_11980__$1,new cljs.core.Keyword(null,"mutes","mutes",1068806309));var inst_11983 = cljs.core.get.call(null,inst_11980__$1,new cljs.core.Keyword(null,"solos","solos",1441458643));var state_12028__$1 = (function (){var statearr_12056 = state_12028;(statearr_12056[(17)] = inst_11982);
(statearr_12056[(10)] = inst_11980__$1);
(statearr_12056[(16)] = inst_11983);
return statearr_12056;
})();return cljs.core.async.impl.ioc_helpers.ioc_alts_BANG_.call(null,state_12028__$1,(10),inst_11981);
} else
{if((state_val_12029 === (5)))
{var inst_11972 = (state_12028[(7)]);var inst_11975 = cljs.core.seq_QMARK_.call(null,inst_11972);var state_12028__$1 = state_12028;if(inst_11975)
{var statearr_12057_12097 = state_12028__$1;(statearr_12057_12097[(1)] = (7));
} else
{var statearr_12058_12098 = state_12028__$1;(statearr_12058_12098[(1)] = (8));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (14)))
{var inst_11988 = (state_12028[(15)]);var inst_11995 = cljs.core.swap_BANG_.call(null,cs,cljs.core.dissoc,inst_11988);var state_12028__$1 = state_12028;var statearr_12059_12099 = state_12028__$1;(statearr_12059_12099[(2)] = inst_11995);
(statearr_12059_12099[(1)] = (16));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (26)))
{var inst_12018 = (state_12028[(2)]);var state_12028__$1 = state_12028;var statearr_12060_12100 = state_12028__$1;(statearr_12060_12100[(2)] = inst_12018);
(statearr_12060_12100[(1)] = (22));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (16)))
{var inst_11998 = (state_12028[(2)]);var inst_11999 = calc_state.call(null);var inst_11972 = inst_11999;var state_12028__$1 = (function (){var statearr_12061 = state_12028;(statearr_12061[(7)] = inst_11972);
(statearr_12061[(18)] = inst_11998);
return statearr_12061;
})();var statearr_12062_12101 = state_12028__$1;(statearr_12062_12101[(2)] = null);
(statearr_12062_12101[(1)] = (5));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (10)))
{var inst_11987 = (state_12028[(8)]);var inst_11988 = (state_12028[(15)]);var inst_11986 = (state_12028[(2)]);var inst_11987__$1 = cljs.core.nth.call(null,inst_11986,(0),null);var inst_11988__$1 = cljs.core.nth.call(null,inst_11986,(1),null);var inst_11989 = (inst_11987__$1 == null);var inst_11990 = cljs.core._EQ_.call(null,inst_11988__$1,change);var inst_11991 = (inst_11989) || (inst_11990);var state_12028__$1 = (function (){var statearr_12063 = state_12028;(statearr_12063[(8)] = inst_11987__$1);
(statearr_12063[(15)] = inst_11988__$1);
return statearr_12063;
})();if(cljs.core.truth_(inst_11991))
{var statearr_12064_12102 = state_12028__$1;(statearr_12064_12102[(1)] = (11));
} else
{var statearr_12065_12103 = state_12028__$1;(statearr_12065_12103[(1)] = (12));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (18)))
{var inst_11982 = (state_12028[(17)]);var inst_11988 = (state_12028[(15)]);var inst_11983 = (state_12028[(16)]);var inst_12005 = cljs.core.empty_QMARK_.call(null,inst_11983);var inst_12006 = inst_11982.call(null,inst_11988);var inst_12007 = cljs.core.not.call(null,inst_12006);var inst_12008 = (inst_12005) && (inst_12007);var state_12028__$1 = state_12028;var statearr_12066_12104 = state_12028__$1;(statearr_12066_12104[(2)] = inst_12008);
(statearr_12066_12104[(1)] = (19));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12029 === (8)))
{var inst_11972 = (state_12028[(7)]);var state_12028__$1 = state_12028;var statearr_12067_12105 = state_12028__$1;(statearr_12067_12105[(2)] = inst_11972);
(statearr_12067_12105[(1)] = (9));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto___12075,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
;return ((function (switch__6337__auto__,c__6352__auto___12075,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_12071 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_12071[(0)] = state_machine__6338__auto__);
(statearr_12071[(1)] = (1));
return statearr_12071;
});
var state_machine__6338__auto____1 = (function (state_12028){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12028);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e12072){if((e12072 instanceof Object))
{var ex__6341__auto__ = e12072;var statearr_12073_12106 = state_12028;(statearr_12073_12106[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12028);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e12072;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__12107 = state_12028;
state_12028 = G__12107;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12028){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12028);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___12075,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
})();var state__6354__auto__ = (function (){var statearr_12074 = f__6353__auto__.call(null);(statearr_12074[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___12075);
return statearr_12074;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___12075,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
);
return m;
});
/**
* Adds ch as an input to the mix
*/
cljs.core.async.admix = (function admix(mix,ch){return cljs.core.async.admix_STAR_.call(null,mix,ch);
});
/**
* Removes ch as an input to the mix
*/
cljs.core.async.unmix = (function unmix(mix,ch){return cljs.core.async.unmix_STAR_.call(null,mix,ch);
});
/**
* removes all inputs from the mix
*/
cljs.core.async.unmix_all = (function unmix_all(mix){return cljs.core.async.unmix_all_STAR_.call(null,mix);
});
/**
* Atomically sets the state(s) of one or more channels in a mix. The
* state map is a map of channels -> channel-state-map. A
* channel-state-map is a map of attrs -> boolean, where attr is one or
* more of :mute, :pause or :solo. Any states supplied are merged with
* the current state.
* 
* Note that channels can be added to a mix via toggle, which can be
* used to add channels in a particular (e.g. paused) state.
*/
cljs.core.async.toggle = (function toggle(mix,state_map){return cljs.core.async.toggle_STAR_.call(null,mix,state_map);
});
/**
* Sets the solo mode of the mix. mode must be one of :mute or :pause
*/
cljs.core.async.solo_mode = (function solo_mode(mix,mode){return cljs.core.async.solo_mode_STAR_.call(null,mix,mode);
});
cljs.core.async.Pub = (function (){var obj12109 = {};return obj12109;
})();
cljs.core.async.sub_STAR_ = (function sub_STAR_(p,v,ch,close_QMARK_){if((function (){var and__3530__auto__ = p;if(and__3530__auto__)
{return p.cljs$core$async$Pub$sub_STAR_$arity$4;
} else
{return and__3530__auto__;
}
})())
{return p.cljs$core$async$Pub$sub_STAR_$arity$4(p,v,ch,close_QMARK_);
} else
{var x__4169__auto__ = (((p == null))?null:p);return (function (){var or__3542__auto__ = (cljs.core.async.sub_STAR_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (cljs.core.async.sub_STAR_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Pub.sub*",p);
}
}
})().call(null,p,v,ch,close_QMARK_);
}
});
cljs.core.async.unsub_STAR_ = (function unsub_STAR_(p,v,ch){if((function (){var and__3530__auto__ = p;if(and__3530__auto__)
{return p.cljs$core$async$Pub$unsub_STAR_$arity$3;
} else
{return and__3530__auto__;
}
})())
{return p.cljs$core$async$Pub$unsub_STAR_$arity$3(p,v,ch);
} else
{var x__4169__auto__ = (((p == null))?null:p);return (function (){var or__3542__auto__ = (cljs.core.async.unsub_STAR_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (cljs.core.async.unsub_STAR_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Pub.unsub*",p);
}
}
})().call(null,p,v,ch);
}
});
cljs.core.async.unsub_all_STAR_ = (function() {
var unsub_all_STAR_ = null;
var unsub_all_STAR___1 = (function (p){if((function (){var and__3530__auto__ = p;if(and__3530__auto__)
{return p.cljs$core$async$Pub$unsub_all_STAR_$arity$1;
} else
{return and__3530__auto__;
}
})())
{return p.cljs$core$async$Pub$unsub_all_STAR_$arity$1(p);
} else
{var x__4169__auto__ = (((p == null))?null:p);return (function (){var or__3542__auto__ = (cljs.core.async.unsub_all_STAR_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (cljs.core.async.unsub_all_STAR_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Pub.unsub-all*",p);
}
}
})().call(null,p);
}
});
var unsub_all_STAR___2 = (function (p,v){if((function (){var and__3530__auto__ = p;if(and__3530__auto__)
{return p.cljs$core$async$Pub$unsub_all_STAR_$arity$2;
} else
{return and__3530__auto__;
}
})())
{return p.cljs$core$async$Pub$unsub_all_STAR_$arity$2(p,v);
} else
{var x__4169__auto__ = (((p == null))?null:p);return (function (){var or__3542__auto__ = (cljs.core.async.unsub_all_STAR_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (cljs.core.async.unsub_all_STAR_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"Pub.unsub-all*",p);
}
}
})().call(null,p,v);
}
});
unsub_all_STAR_ = function(p,v){
switch(arguments.length){
case 1:
return unsub_all_STAR___1.call(this,p);
case 2:
return unsub_all_STAR___2.call(this,p,v);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
unsub_all_STAR_.cljs$core$IFn$_invoke$arity$1 = unsub_all_STAR___1;
unsub_all_STAR_.cljs$core$IFn$_invoke$arity$2 = unsub_all_STAR___2;
return unsub_all_STAR_;
})()
;
/**
* Creates and returns a pub(lication) of the supplied channel,
* partitioned into topics by the topic-fn. topic-fn will be applied to
* each value on the channel and the result will determine the 'topic'
* on which that value will be put. Channels can be subscribed to
* receive copies of topics using 'sub', and unsubscribed using
* 'unsub'. Each topic will be handled by an internal mult on a
* dedicated channel. By default these internal channels are
* unbuffered, but a buf-fn can be supplied which, given a topic,
* creates a buffer with desired properties.
* 
* Each item is distributed to all subs in parallel and synchronously,
* i.e. each sub must accept before the next item is distributed. Use
* buffering/windowing to prevent slow subs from holding up the pub.
* 
* Items received when there are no matching subs get dropped.
* 
* Note that if buf-fns are used then each topic is handled
* asynchronously, i.e. if a channel is subscribed to more than one
* topic it should not expect them to be interleaved identically with
* the source.
*/
cljs.core.async.pub = (function() {
var pub = null;
var pub__2 = (function (ch,topic_fn){return pub.call(null,ch,topic_fn,cljs.core.constantly.call(null,null));
});
var pub__3 = (function (ch,topic_fn,buf_fn){var mults = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);var ensure_mult = ((function (mults){
return (function (topic){var or__3542__auto__ = cljs.core.get.call(null,cljs.core.deref.call(null,mults),topic);if(cljs.core.truth_(or__3542__auto__))
{return or__3542__auto__;
} else
{return cljs.core.get.call(null,cljs.core.swap_BANG_.call(null,mults,((function (or__3542__auto__,mults){
return (function (p1__12110_SHARP_){if(cljs.core.truth_(p1__12110_SHARP_.call(null,topic)))
{return p1__12110_SHARP_;
} else
{return cljs.core.assoc.call(null,p1__12110_SHARP_,topic,cljs.core.async.mult.call(null,cljs.core.async.chan.call(null,buf_fn.call(null,topic))));
}
});})(or__3542__auto__,mults))
),topic);
}
});})(mults))
;var p = (function (){if(typeof cljs.core.async.t12233 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t12233 = (function (ensure_mult,mults,buf_fn,topic_fn,ch,pub,meta12234){
this.ensure_mult = ensure_mult;
this.mults = mults;
this.buf_fn = buf_fn;
this.topic_fn = topic_fn;
this.ch = ch;
this.pub = pub;
this.meta12234 = meta12234;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t12233.cljs$lang$type = true;
cljs.core.async.t12233.cljs$lang$ctorStr = "cljs.core.async/t12233";
cljs.core.async.t12233.cljs$lang$ctorPrWriter = ((function (mults,ensure_mult){
return (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t12233");
});})(mults,ensure_mult))
;
cljs.core.async.t12233.prototype.cljs$core$async$Pub$ = true;
cljs.core.async.t12233.prototype.cljs$core$async$Pub$sub_STAR_$arity$4 = ((function (mults,ensure_mult){
return (function (p,topic,ch__$2,close_QMARK_){var self__ = this;
var p__$1 = this;var m = self__.ensure_mult.call(null,topic);return cljs.core.async.tap.call(null,m,ch__$2,close_QMARK_);
});})(mults,ensure_mult))
;
cljs.core.async.t12233.prototype.cljs$core$async$Pub$unsub_STAR_$arity$3 = ((function (mults,ensure_mult){
return (function (p,topic,ch__$2){var self__ = this;
var p__$1 = this;var temp__4126__auto__ = cljs.core.get.call(null,cljs.core.deref.call(null,self__.mults),topic);if(cljs.core.truth_(temp__4126__auto__))
{var m = temp__4126__auto__;return cljs.core.async.untap.call(null,m,ch__$2);
} else
{return null;
}
});})(mults,ensure_mult))
;
cljs.core.async.t12233.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$1 = ((function (mults,ensure_mult){
return (function (_){var self__ = this;
var ___$1 = this;return cljs.core.reset_BANG_.call(null,self__.mults,cljs.core.PersistentArrayMap.EMPTY);
});})(mults,ensure_mult))
;
cljs.core.async.t12233.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$2 = ((function (mults,ensure_mult){
return (function (_,topic){var self__ = this;
var ___$1 = this;return cljs.core.swap_BANG_.call(null,self__.mults,cljs.core.dissoc,topic);
});})(mults,ensure_mult))
;
cljs.core.async.t12233.prototype.cljs$core$async$Mux$ = true;
cljs.core.async.t12233.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (mults,ensure_mult){
return (function (_){var self__ = this;
var ___$1 = this;return self__.ch;
});})(mults,ensure_mult))
;
cljs.core.async.t12233.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (mults,ensure_mult){
return (function (_12235){var self__ = this;
var _12235__$1 = this;return self__.meta12234;
});})(mults,ensure_mult))
;
cljs.core.async.t12233.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (mults,ensure_mult){
return (function (_12235,meta12234__$1){var self__ = this;
var _12235__$1 = this;return (new cljs.core.async.t12233(self__.ensure_mult,self__.mults,self__.buf_fn,self__.topic_fn,self__.ch,self__.pub,meta12234__$1));
});})(mults,ensure_mult))
;
cljs.core.async.__GT_t12233 = ((function (mults,ensure_mult){
return (function __GT_t12233(ensure_mult__$1,mults__$1,buf_fn__$1,topic_fn__$1,ch__$1,pub__$1,meta12234){return (new cljs.core.async.t12233(ensure_mult__$1,mults__$1,buf_fn__$1,topic_fn__$1,ch__$1,pub__$1,meta12234));
});})(mults,ensure_mult))
;
}
return (new cljs.core.async.t12233(ensure_mult,mults,buf_fn,topic_fn,ch,pub,null));
})();var c__6352__auto___12355 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___12355,mults,ensure_mult,p){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___12355,mults,ensure_mult,p){
return (function (state_12307){var state_val_12308 = (state_12307[(1)]);if((state_val_12308 === (7)))
{var inst_12303 = (state_12307[(2)]);var state_12307__$1 = state_12307;var statearr_12309_12356 = state_12307__$1;(statearr_12309_12356[(2)] = inst_12303);
(statearr_12309_12356[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (20)))
{var state_12307__$1 = state_12307;var statearr_12310_12357 = state_12307__$1;(statearr_12310_12357[(2)] = null);
(statearr_12310_12357[(1)] = (21));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (1)))
{var state_12307__$1 = state_12307;var statearr_12311_12358 = state_12307__$1;(statearr_12311_12358[(2)] = null);
(statearr_12311_12358[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (24)))
{var inst_12286 = (state_12307[(7)]);var inst_12295 = cljs.core.swap_BANG_.call(null,mults,cljs.core.dissoc,inst_12286);var state_12307__$1 = state_12307;var statearr_12312_12359 = state_12307__$1;(statearr_12312_12359[(2)] = inst_12295);
(statearr_12312_12359[(1)] = (25));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (4)))
{var inst_12238 = (state_12307[(8)]);var inst_12238__$1 = (state_12307[(2)]);var inst_12239 = (inst_12238__$1 == null);var state_12307__$1 = (function (){var statearr_12313 = state_12307;(statearr_12313[(8)] = inst_12238__$1);
return statearr_12313;
})();if(cljs.core.truth_(inst_12239))
{var statearr_12314_12360 = state_12307__$1;(statearr_12314_12360[(1)] = (5));
} else
{var statearr_12315_12361 = state_12307__$1;(statearr_12315_12361[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (15)))
{var inst_12280 = (state_12307[(2)]);var state_12307__$1 = state_12307;var statearr_12316_12362 = state_12307__$1;(statearr_12316_12362[(2)] = inst_12280);
(statearr_12316_12362[(1)] = (12));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (21)))
{var inst_12300 = (state_12307[(2)]);var state_12307__$1 = (function (){var statearr_12317 = state_12307;(statearr_12317[(9)] = inst_12300);
return statearr_12317;
})();var statearr_12318_12363 = state_12307__$1;(statearr_12318_12363[(2)] = null);
(statearr_12318_12363[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (13)))
{var inst_12262 = (state_12307[(10)]);var inst_12264 = cljs.core.chunked_seq_QMARK_.call(null,inst_12262);var state_12307__$1 = state_12307;if(inst_12264)
{var statearr_12319_12364 = state_12307__$1;(statearr_12319_12364[(1)] = (16));
} else
{var statearr_12320_12365 = state_12307__$1;(statearr_12320_12365[(1)] = (17));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (22)))
{var inst_12292 = (state_12307[(2)]);var state_12307__$1 = state_12307;if(cljs.core.truth_(inst_12292))
{var statearr_12321_12366 = state_12307__$1;(statearr_12321_12366[(1)] = (23));
} else
{var statearr_12322_12367 = state_12307__$1;(statearr_12322_12367[(1)] = (24));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (6)))
{var inst_12288 = (state_12307[(11)]);var inst_12286 = (state_12307[(7)]);var inst_12238 = (state_12307[(8)]);var inst_12286__$1 = topic_fn.call(null,inst_12238);var inst_12287 = cljs.core.deref.call(null,mults);var inst_12288__$1 = cljs.core.get.call(null,inst_12287,inst_12286__$1);var state_12307__$1 = (function (){var statearr_12323 = state_12307;(statearr_12323[(11)] = inst_12288__$1);
(statearr_12323[(7)] = inst_12286__$1);
return statearr_12323;
})();if(cljs.core.truth_(inst_12288__$1))
{var statearr_12324_12368 = state_12307__$1;(statearr_12324_12368[(1)] = (19));
} else
{var statearr_12325_12369 = state_12307__$1;(statearr_12325_12369[(1)] = (20));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (25)))
{var inst_12297 = (state_12307[(2)]);var state_12307__$1 = state_12307;var statearr_12326_12370 = state_12307__$1;(statearr_12326_12370[(2)] = inst_12297);
(statearr_12326_12370[(1)] = (21));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (17)))
{var inst_12262 = (state_12307[(10)]);var inst_12271 = cljs.core.first.call(null,inst_12262);var inst_12272 = cljs.core.async.muxch_STAR_.call(null,inst_12271);var inst_12273 = cljs.core.async.close_BANG_.call(null,inst_12272);var inst_12274 = cljs.core.next.call(null,inst_12262);var inst_12248 = inst_12274;var inst_12249 = null;var inst_12250 = (0);var inst_12251 = (0);var state_12307__$1 = (function (){var statearr_12327 = state_12307;(statearr_12327[(12)] = inst_12251);
(statearr_12327[(13)] = inst_12273);
(statearr_12327[(14)] = inst_12248);
(statearr_12327[(15)] = inst_12249);
(statearr_12327[(16)] = inst_12250);
return statearr_12327;
})();var statearr_12328_12371 = state_12307__$1;(statearr_12328_12371[(2)] = null);
(statearr_12328_12371[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (3)))
{var inst_12305 = (state_12307[(2)]);var state_12307__$1 = state_12307;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12307__$1,inst_12305);
} else
{if((state_val_12308 === (12)))
{var inst_12282 = (state_12307[(2)]);var state_12307__$1 = state_12307;var statearr_12329_12372 = state_12307__$1;(statearr_12329_12372[(2)] = inst_12282);
(statearr_12329_12372[(1)] = (9));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (2)))
{var state_12307__$1 = state_12307;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_12307__$1,(4),ch);
} else
{if((state_val_12308 === (23)))
{var state_12307__$1 = state_12307;var statearr_12330_12373 = state_12307__$1;(statearr_12330_12373[(2)] = null);
(statearr_12330_12373[(1)] = (25));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (19)))
{var inst_12288 = (state_12307[(11)]);var inst_12238 = (state_12307[(8)]);var inst_12290 = cljs.core.async.muxch_STAR_.call(null,inst_12288);var state_12307__$1 = state_12307;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12307__$1,(22),inst_12290,inst_12238);
} else
{if((state_val_12308 === (11)))
{var inst_12262 = (state_12307[(10)]);var inst_12248 = (state_12307[(14)]);var inst_12262__$1 = cljs.core.seq.call(null,inst_12248);var state_12307__$1 = (function (){var statearr_12331 = state_12307;(statearr_12331[(10)] = inst_12262__$1);
return statearr_12331;
})();if(inst_12262__$1)
{var statearr_12332_12374 = state_12307__$1;(statearr_12332_12374[(1)] = (13));
} else
{var statearr_12333_12375 = state_12307__$1;(statearr_12333_12375[(1)] = (14));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (9)))
{var inst_12284 = (state_12307[(2)]);var state_12307__$1 = state_12307;var statearr_12334_12376 = state_12307__$1;(statearr_12334_12376[(2)] = inst_12284);
(statearr_12334_12376[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (5)))
{var inst_12245 = cljs.core.deref.call(null,mults);var inst_12246 = cljs.core.vals.call(null,inst_12245);var inst_12247 = cljs.core.seq.call(null,inst_12246);var inst_12248 = inst_12247;var inst_12249 = null;var inst_12250 = (0);var inst_12251 = (0);var state_12307__$1 = (function (){var statearr_12335 = state_12307;(statearr_12335[(12)] = inst_12251);
(statearr_12335[(14)] = inst_12248);
(statearr_12335[(15)] = inst_12249);
(statearr_12335[(16)] = inst_12250);
return statearr_12335;
})();var statearr_12336_12377 = state_12307__$1;(statearr_12336_12377[(2)] = null);
(statearr_12336_12377[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (14)))
{var state_12307__$1 = state_12307;var statearr_12340_12378 = state_12307__$1;(statearr_12340_12378[(2)] = null);
(statearr_12340_12378[(1)] = (15));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (16)))
{var inst_12262 = (state_12307[(10)]);var inst_12266 = cljs.core.chunk_first.call(null,inst_12262);var inst_12267 = cljs.core.chunk_rest.call(null,inst_12262);var inst_12268 = cljs.core.count.call(null,inst_12266);var inst_12248 = inst_12267;var inst_12249 = inst_12266;var inst_12250 = inst_12268;var inst_12251 = (0);var state_12307__$1 = (function (){var statearr_12341 = state_12307;(statearr_12341[(12)] = inst_12251);
(statearr_12341[(14)] = inst_12248);
(statearr_12341[(15)] = inst_12249);
(statearr_12341[(16)] = inst_12250);
return statearr_12341;
})();var statearr_12342_12379 = state_12307__$1;(statearr_12342_12379[(2)] = null);
(statearr_12342_12379[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (10)))
{var inst_12251 = (state_12307[(12)]);var inst_12248 = (state_12307[(14)]);var inst_12249 = (state_12307[(15)]);var inst_12250 = (state_12307[(16)]);var inst_12256 = cljs.core._nth.call(null,inst_12249,inst_12251);var inst_12257 = cljs.core.async.muxch_STAR_.call(null,inst_12256);var inst_12258 = cljs.core.async.close_BANG_.call(null,inst_12257);var inst_12259 = (inst_12251 + (1));var tmp12337 = inst_12248;var tmp12338 = inst_12249;var tmp12339 = inst_12250;var inst_12248__$1 = tmp12337;var inst_12249__$1 = tmp12338;var inst_12250__$1 = tmp12339;var inst_12251__$1 = inst_12259;var state_12307__$1 = (function (){var statearr_12343 = state_12307;(statearr_12343[(12)] = inst_12251__$1);
(statearr_12343[(14)] = inst_12248__$1);
(statearr_12343[(15)] = inst_12249__$1);
(statearr_12343[(16)] = inst_12250__$1);
(statearr_12343[(17)] = inst_12258);
return statearr_12343;
})();var statearr_12344_12380 = state_12307__$1;(statearr_12344_12380[(2)] = null);
(statearr_12344_12380[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (18)))
{var inst_12277 = (state_12307[(2)]);var state_12307__$1 = state_12307;var statearr_12345_12381 = state_12307__$1;(statearr_12345_12381[(2)] = inst_12277);
(statearr_12345_12381[(1)] = (15));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12308 === (8)))
{var inst_12251 = (state_12307[(12)]);var inst_12250 = (state_12307[(16)]);var inst_12253 = (inst_12251 < inst_12250);var inst_12254 = inst_12253;var state_12307__$1 = state_12307;if(cljs.core.truth_(inst_12254))
{var statearr_12346_12382 = state_12307__$1;(statearr_12346_12382[(1)] = (10));
} else
{var statearr_12347_12383 = state_12307__$1;(statearr_12347_12383[(1)] = (11));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto___12355,mults,ensure_mult,p))
;return ((function (switch__6337__auto__,c__6352__auto___12355,mults,ensure_mult,p){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_12351 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_12351[(0)] = state_machine__6338__auto__);
(statearr_12351[(1)] = (1));
return statearr_12351;
});
var state_machine__6338__auto____1 = (function (state_12307){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12307);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e12352){if((e12352 instanceof Object))
{var ex__6341__auto__ = e12352;var statearr_12353_12384 = state_12307;(statearr_12353_12384[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12307);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e12352;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__12385 = state_12307;
state_12307 = G__12385;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12307){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12307);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___12355,mults,ensure_mult,p))
})();var state__6354__auto__ = (function (){var statearr_12354 = f__6353__auto__.call(null);(statearr_12354[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___12355);
return statearr_12354;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___12355,mults,ensure_mult,p))
);
return p;
});
pub = function(ch,topic_fn,buf_fn){
switch(arguments.length){
case 2:
return pub__2.call(this,ch,topic_fn);
case 3:
return pub__3.call(this,ch,topic_fn,buf_fn);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
pub.cljs$core$IFn$_invoke$arity$2 = pub__2;
pub.cljs$core$IFn$_invoke$arity$3 = pub__3;
return pub;
})()
;
/**
* Subscribes a channel to a topic of a pub.
* 
* By default the channel will be closed when the source closes,
* but can be determined by the close? parameter.
*/
cljs.core.async.sub = (function() {
var sub = null;
var sub__3 = (function (p,topic,ch){return sub.call(null,p,topic,ch,true);
});
var sub__4 = (function (p,topic,ch,close_QMARK_){return cljs.core.async.sub_STAR_.call(null,p,topic,ch,close_QMARK_);
});
sub = function(p,topic,ch,close_QMARK_){
switch(arguments.length){
case 3:
return sub__3.call(this,p,topic,ch);
case 4:
return sub__4.call(this,p,topic,ch,close_QMARK_);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
sub.cljs$core$IFn$_invoke$arity$3 = sub__3;
sub.cljs$core$IFn$_invoke$arity$4 = sub__4;
return sub;
})()
;
/**
* Unsubscribes a channel from a topic of a pub
*/
cljs.core.async.unsub = (function unsub(p,topic,ch){return cljs.core.async.unsub_STAR_.call(null,p,topic,ch);
});
/**
* Unsubscribes all channels from a pub, or a topic of a pub
*/
cljs.core.async.unsub_all = (function() {
var unsub_all = null;
var unsub_all__1 = (function (p){return cljs.core.async.unsub_all_STAR_.call(null,p);
});
var unsub_all__2 = (function (p,topic){return cljs.core.async.unsub_all_STAR_.call(null,p,topic);
});
unsub_all = function(p,topic){
switch(arguments.length){
case 1:
return unsub_all__1.call(this,p);
case 2:
return unsub_all__2.call(this,p,topic);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
unsub_all.cljs$core$IFn$_invoke$arity$1 = unsub_all__1;
unsub_all.cljs$core$IFn$_invoke$arity$2 = unsub_all__2;
return unsub_all;
})()
;
/**
* Takes a function and a collection of source channels, and returns a
* channel which contains the values produced by applying f to the set
* of first items taken from each source channel, followed by applying
* f to the set of second items from each channel, until any one of the
* channels is closed, at which point the output channel will be
* closed. The returned channel will be unbuffered by default, or a
* buf-or-n can be supplied
*/
cljs.core.async.map = (function() {
var map = null;
var map__2 = (function (f,chs){return map.call(null,f,chs,null);
});
var map__3 = (function (f,chs,buf_or_n){var chs__$1 = cljs.core.vec.call(null,chs);var out = cljs.core.async.chan.call(null,buf_or_n);var cnt = cljs.core.count.call(null,chs__$1);var rets = cljs.core.object_array.call(null,cnt);var dchan = cljs.core.async.chan.call(null,(1));var dctr = cljs.core.atom.call(null,null);var done = cljs.core.mapv.call(null,((function (chs__$1,out,cnt,rets,dchan,dctr){
return (function (i){return ((function (chs__$1,out,cnt,rets,dchan,dctr){
return (function (ret){(rets[i] = ret);
if((cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec) === (0)))
{return cljs.core.async.put_BANG_.call(null,dchan,rets.slice((0)));
} else
{return null;
}
});
;})(chs__$1,out,cnt,rets,dchan,dctr))
});})(chs__$1,out,cnt,rets,dchan,dctr))
,cljs.core.range.call(null,cnt));var c__6352__auto___12522 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___12522,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___12522,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function (state_12492){var state_val_12493 = (state_12492[(1)]);if((state_val_12493 === (7)))
{var state_12492__$1 = state_12492;var statearr_12494_12523 = state_12492__$1;(statearr_12494_12523[(2)] = null);
(statearr_12494_12523[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12493 === (1)))
{var state_12492__$1 = state_12492;var statearr_12495_12524 = state_12492__$1;(statearr_12495_12524[(2)] = null);
(statearr_12495_12524[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12493 === (4)))
{var inst_12456 = (state_12492[(7)]);var inst_12458 = (inst_12456 < cnt);var state_12492__$1 = state_12492;if(cljs.core.truth_(inst_12458))
{var statearr_12496_12525 = state_12492__$1;(statearr_12496_12525[(1)] = (6));
} else
{var statearr_12497_12526 = state_12492__$1;(statearr_12497_12526[(1)] = (7));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12493 === (15)))
{var inst_12488 = (state_12492[(2)]);var state_12492__$1 = state_12492;var statearr_12498_12527 = state_12492__$1;(statearr_12498_12527[(2)] = inst_12488);
(statearr_12498_12527[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12493 === (13)))
{var inst_12481 = cljs.core.async.close_BANG_.call(null,out);var state_12492__$1 = state_12492;var statearr_12499_12528 = state_12492__$1;(statearr_12499_12528[(2)] = inst_12481);
(statearr_12499_12528[(1)] = (15));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12493 === (6)))
{var state_12492__$1 = state_12492;var statearr_12500_12529 = state_12492__$1;(statearr_12500_12529[(2)] = null);
(statearr_12500_12529[(1)] = (11));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12493 === (3)))
{var inst_12490 = (state_12492[(2)]);var state_12492__$1 = state_12492;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12492__$1,inst_12490);
} else
{if((state_val_12493 === (12)))
{var inst_12478 = (state_12492[(8)]);var inst_12478__$1 = (state_12492[(2)]);var inst_12479 = cljs.core.some.call(null,cljs.core.nil_QMARK_,inst_12478__$1);var state_12492__$1 = (function (){var statearr_12501 = state_12492;(statearr_12501[(8)] = inst_12478__$1);
return statearr_12501;
})();if(cljs.core.truth_(inst_12479))
{var statearr_12502_12530 = state_12492__$1;(statearr_12502_12530[(1)] = (13));
} else
{var statearr_12503_12531 = state_12492__$1;(statearr_12503_12531[(1)] = (14));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12493 === (2)))
{var inst_12455 = cljs.core.reset_BANG_.call(null,dctr,cnt);var inst_12456 = (0);var state_12492__$1 = (function (){var statearr_12504 = state_12492;(statearr_12504[(9)] = inst_12455);
(statearr_12504[(7)] = inst_12456);
return statearr_12504;
})();var statearr_12505_12532 = state_12492__$1;(statearr_12505_12532[(2)] = null);
(statearr_12505_12532[(1)] = (4));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12493 === (11)))
{var inst_12456 = (state_12492[(7)]);var _ = cljs.core.async.impl.ioc_helpers.add_exception_frame.call(null,state_12492,(10),Object,null,(9));var inst_12465 = chs__$1.call(null,inst_12456);var inst_12466 = done.call(null,inst_12456);var inst_12467 = cljs.core.async.take_BANG_.call(null,inst_12465,inst_12466);var state_12492__$1 = state_12492;var statearr_12506_12533 = state_12492__$1;(statearr_12506_12533[(2)] = inst_12467);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12492__$1);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12493 === (9)))
{var inst_12456 = (state_12492[(7)]);var inst_12469 = (state_12492[(2)]);var inst_12470 = (inst_12456 + (1));var inst_12456__$1 = inst_12470;var state_12492__$1 = (function (){var statearr_12507 = state_12492;(statearr_12507[(7)] = inst_12456__$1);
(statearr_12507[(10)] = inst_12469);
return statearr_12507;
})();var statearr_12508_12534 = state_12492__$1;(statearr_12508_12534[(2)] = null);
(statearr_12508_12534[(1)] = (4));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12493 === (5)))
{var inst_12476 = (state_12492[(2)]);var state_12492__$1 = (function (){var statearr_12509 = state_12492;(statearr_12509[(11)] = inst_12476);
return statearr_12509;
})();return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_12492__$1,(12),dchan);
} else
{if((state_val_12493 === (14)))
{var inst_12478 = (state_12492[(8)]);var inst_12483 = cljs.core.apply.call(null,f,inst_12478);var state_12492__$1 = state_12492;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12492__$1,(16),out,inst_12483);
} else
{if((state_val_12493 === (16)))
{var inst_12485 = (state_12492[(2)]);var state_12492__$1 = (function (){var statearr_12510 = state_12492;(statearr_12510[(12)] = inst_12485);
return statearr_12510;
})();var statearr_12511_12535 = state_12492__$1;(statearr_12511_12535[(2)] = null);
(statearr_12511_12535[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12493 === (10)))
{var inst_12460 = (state_12492[(2)]);var inst_12461 = cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec);var state_12492__$1 = (function (){var statearr_12512 = state_12492;(statearr_12512[(13)] = inst_12460);
return statearr_12512;
})();var statearr_12513_12536 = state_12492__$1;(statearr_12513_12536[(2)] = inst_12461);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12492__$1);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12493 === (8)))
{var inst_12474 = (state_12492[(2)]);var state_12492__$1 = state_12492;var statearr_12514_12537 = state_12492__$1;(statearr_12514_12537[(2)] = inst_12474);
(statearr_12514_12537[(1)] = (5));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto___12522,chs__$1,out,cnt,rets,dchan,dctr,done))
;return ((function (switch__6337__auto__,c__6352__auto___12522,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_12518 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_12518[(0)] = state_machine__6338__auto__);
(statearr_12518[(1)] = (1));
return statearr_12518;
});
var state_machine__6338__auto____1 = (function (state_12492){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12492);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e12519){if((e12519 instanceof Object))
{var ex__6341__auto__ = e12519;var statearr_12520_12538 = state_12492;(statearr_12520_12538[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12492);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e12519;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__12539 = state_12492;
state_12492 = G__12539;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12492){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12492);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___12522,chs__$1,out,cnt,rets,dchan,dctr,done))
})();var state__6354__auto__ = (function (){var statearr_12521 = f__6353__auto__.call(null);(statearr_12521[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___12522);
return statearr_12521;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___12522,chs__$1,out,cnt,rets,dchan,dctr,done))
);
return out;
});
map = function(f,chs,buf_or_n){
switch(arguments.length){
case 2:
return map__2.call(this,f,chs);
case 3:
return map__3.call(this,f,chs,buf_or_n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
map.cljs$core$IFn$_invoke$arity$2 = map__2;
map.cljs$core$IFn$_invoke$arity$3 = map__3;
return map;
})()
;
/**
* Takes a collection of source channels and returns a channel which
* contains all values taken from them. The returned channel will be
* unbuffered by default, or a buf-or-n can be supplied. The channel
* will close after all the source channels have closed.
*/
cljs.core.async.merge = (function() {
var merge = null;
var merge__1 = (function (chs){return merge.call(null,chs,null);
});
var merge__2 = (function (chs,buf_or_n){var out = cljs.core.async.chan.call(null,buf_or_n);var c__6352__auto___12647 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___12647,out){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___12647,out){
return (function (state_12623){var state_val_12624 = (state_12623[(1)]);if((state_val_12624 === (7)))
{var inst_12602 = (state_12623[(7)]);var inst_12603 = (state_12623[(8)]);var inst_12602__$1 = (state_12623[(2)]);var inst_12603__$1 = cljs.core.nth.call(null,inst_12602__$1,(0),null);var inst_12604 = cljs.core.nth.call(null,inst_12602__$1,(1),null);var inst_12605 = (inst_12603__$1 == null);var state_12623__$1 = (function (){var statearr_12625 = state_12623;(statearr_12625[(7)] = inst_12602__$1);
(statearr_12625[(8)] = inst_12603__$1);
(statearr_12625[(9)] = inst_12604);
return statearr_12625;
})();if(cljs.core.truth_(inst_12605))
{var statearr_12626_12648 = state_12623__$1;(statearr_12626_12648[(1)] = (8));
} else
{var statearr_12627_12649 = state_12623__$1;(statearr_12627_12649[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12624 === (1)))
{var inst_12594 = cljs.core.vec.call(null,chs);var inst_12595 = inst_12594;var state_12623__$1 = (function (){var statearr_12628 = state_12623;(statearr_12628[(10)] = inst_12595);
return statearr_12628;
})();var statearr_12629_12650 = state_12623__$1;(statearr_12629_12650[(2)] = null);
(statearr_12629_12650[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12624 === (4)))
{var inst_12595 = (state_12623[(10)]);var state_12623__$1 = state_12623;return cljs.core.async.impl.ioc_helpers.ioc_alts_BANG_.call(null,state_12623__$1,(7),inst_12595);
} else
{if((state_val_12624 === (6)))
{var inst_12619 = (state_12623[(2)]);var state_12623__$1 = state_12623;var statearr_12630_12651 = state_12623__$1;(statearr_12630_12651[(2)] = inst_12619);
(statearr_12630_12651[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12624 === (3)))
{var inst_12621 = (state_12623[(2)]);var state_12623__$1 = state_12623;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12623__$1,inst_12621);
} else
{if((state_val_12624 === (2)))
{var inst_12595 = (state_12623[(10)]);var inst_12597 = cljs.core.count.call(null,inst_12595);var inst_12598 = (inst_12597 > (0));var state_12623__$1 = state_12623;if(cljs.core.truth_(inst_12598))
{var statearr_12632_12652 = state_12623__$1;(statearr_12632_12652[(1)] = (4));
} else
{var statearr_12633_12653 = state_12623__$1;(statearr_12633_12653[(1)] = (5));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12624 === (11)))
{var inst_12595 = (state_12623[(10)]);var inst_12612 = (state_12623[(2)]);var tmp12631 = inst_12595;var inst_12595__$1 = tmp12631;var state_12623__$1 = (function (){var statearr_12634 = state_12623;(statearr_12634[(10)] = inst_12595__$1);
(statearr_12634[(11)] = inst_12612);
return statearr_12634;
})();var statearr_12635_12654 = state_12623__$1;(statearr_12635_12654[(2)] = null);
(statearr_12635_12654[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12624 === (9)))
{var inst_12603 = (state_12623[(8)]);var state_12623__$1 = state_12623;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12623__$1,(11),out,inst_12603);
} else
{if((state_val_12624 === (5)))
{var inst_12617 = cljs.core.async.close_BANG_.call(null,out);var state_12623__$1 = state_12623;var statearr_12636_12655 = state_12623__$1;(statearr_12636_12655[(2)] = inst_12617);
(statearr_12636_12655[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12624 === (10)))
{var inst_12615 = (state_12623[(2)]);var state_12623__$1 = state_12623;var statearr_12637_12656 = state_12623__$1;(statearr_12637_12656[(2)] = inst_12615);
(statearr_12637_12656[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12624 === (8)))
{var inst_12595 = (state_12623[(10)]);var inst_12602 = (state_12623[(7)]);var inst_12603 = (state_12623[(8)]);var inst_12604 = (state_12623[(9)]);var inst_12607 = (function (){var c = inst_12604;var v = inst_12603;var vec__12600 = inst_12602;var cs = inst_12595;return ((function (c,v,vec__12600,cs,inst_12595,inst_12602,inst_12603,inst_12604,state_val_12624,c__6352__auto___12647,out){
return (function (p1__12540_SHARP_){return cljs.core.not_EQ_.call(null,c,p1__12540_SHARP_);
});
;})(c,v,vec__12600,cs,inst_12595,inst_12602,inst_12603,inst_12604,state_val_12624,c__6352__auto___12647,out))
})();var inst_12608 = cljs.core.filterv.call(null,inst_12607,inst_12595);var inst_12595__$1 = inst_12608;var state_12623__$1 = (function (){var statearr_12638 = state_12623;(statearr_12638[(10)] = inst_12595__$1);
return statearr_12638;
})();var statearr_12639_12657 = state_12623__$1;(statearr_12639_12657[(2)] = null);
(statearr_12639_12657[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto___12647,out))
;return ((function (switch__6337__auto__,c__6352__auto___12647,out){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_12643 = [null,null,null,null,null,null,null,null,null,null,null,null];(statearr_12643[(0)] = state_machine__6338__auto__);
(statearr_12643[(1)] = (1));
return statearr_12643;
});
var state_machine__6338__auto____1 = (function (state_12623){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12623);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e12644){if((e12644 instanceof Object))
{var ex__6341__auto__ = e12644;var statearr_12645_12658 = state_12623;(statearr_12645_12658[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12623);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e12644;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__12659 = state_12623;
state_12623 = G__12659;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12623){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12623);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___12647,out))
})();var state__6354__auto__ = (function (){var statearr_12646 = f__6353__auto__.call(null);(statearr_12646[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___12647);
return statearr_12646;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___12647,out))
);
return out;
});
merge = function(chs,buf_or_n){
switch(arguments.length){
case 1:
return merge__1.call(this,chs);
case 2:
return merge__2.call(this,chs,buf_or_n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
merge.cljs$core$IFn$_invoke$arity$1 = merge__1;
merge.cljs$core$IFn$_invoke$arity$2 = merge__2;
return merge;
})()
;
/**
* Returns a channel containing the single (collection) result of the
* items taken from the channel conjoined to the supplied
* collection. ch must close before into produces a result.
*/
cljs.core.async.into = (function into(coll,ch){return cljs.core.async.reduce.call(null,cljs.core.conj,coll,ch);
});
/**
* Returns a channel that will return, at most, n items from ch. After n items
* have been returned, or ch has been closed, the return chanel will close.
* 
* The output channel is unbuffered by default, unless buf-or-n is given.
*/
cljs.core.async.take = (function() {
var take = null;
var take__2 = (function (n,ch){return take.call(null,n,ch,null);
});
var take__3 = (function (n,ch,buf_or_n){var out = cljs.core.async.chan.call(null,buf_or_n);var c__6352__auto___12752 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___12752,out){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___12752,out){
return (function (state_12729){var state_val_12730 = (state_12729[(1)]);if((state_val_12730 === (7)))
{var inst_12711 = (state_12729[(7)]);var inst_12711__$1 = (state_12729[(2)]);var inst_12712 = (inst_12711__$1 == null);var inst_12713 = cljs.core.not.call(null,inst_12712);var state_12729__$1 = (function (){var statearr_12731 = state_12729;(statearr_12731[(7)] = inst_12711__$1);
return statearr_12731;
})();if(inst_12713)
{var statearr_12732_12753 = state_12729__$1;(statearr_12732_12753[(1)] = (8));
} else
{var statearr_12733_12754 = state_12729__$1;(statearr_12733_12754[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12730 === (1)))
{var inst_12706 = (0);var state_12729__$1 = (function (){var statearr_12734 = state_12729;(statearr_12734[(8)] = inst_12706);
return statearr_12734;
})();var statearr_12735_12755 = state_12729__$1;(statearr_12735_12755[(2)] = null);
(statearr_12735_12755[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12730 === (4)))
{var state_12729__$1 = state_12729;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_12729__$1,(7),ch);
} else
{if((state_val_12730 === (6)))
{var inst_12724 = (state_12729[(2)]);var state_12729__$1 = state_12729;var statearr_12736_12756 = state_12729__$1;(statearr_12736_12756[(2)] = inst_12724);
(statearr_12736_12756[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12730 === (3)))
{var inst_12726 = (state_12729[(2)]);var inst_12727 = cljs.core.async.close_BANG_.call(null,out);var state_12729__$1 = (function (){var statearr_12737 = state_12729;(statearr_12737[(9)] = inst_12726);
return statearr_12737;
})();return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12729__$1,inst_12727);
} else
{if((state_val_12730 === (2)))
{var inst_12706 = (state_12729[(8)]);var inst_12708 = (inst_12706 < n);var state_12729__$1 = state_12729;if(cljs.core.truth_(inst_12708))
{var statearr_12738_12757 = state_12729__$1;(statearr_12738_12757[(1)] = (4));
} else
{var statearr_12739_12758 = state_12729__$1;(statearr_12739_12758[(1)] = (5));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12730 === (11)))
{var inst_12706 = (state_12729[(8)]);var inst_12716 = (state_12729[(2)]);var inst_12717 = (inst_12706 + (1));var inst_12706__$1 = inst_12717;var state_12729__$1 = (function (){var statearr_12740 = state_12729;(statearr_12740[(10)] = inst_12716);
(statearr_12740[(8)] = inst_12706__$1);
return statearr_12740;
})();var statearr_12741_12759 = state_12729__$1;(statearr_12741_12759[(2)] = null);
(statearr_12741_12759[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12730 === (9)))
{var state_12729__$1 = state_12729;var statearr_12742_12760 = state_12729__$1;(statearr_12742_12760[(2)] = null);
(statearr_12742_12760[(1)] = (10));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12730 === (5)))
{var state_12729__$1 = state_12729;var statearr_12743_12761 = state_12729__$1;(statearr_12743_12761[(2)] = null);
(statearr_12743_12761[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12730 === (10)))
{var inst_12721 = (state_12729[(2)]);var state_12729__$1 = state_12729;var statearr_12744_12762 = state_12729__$1;(statearr_12744_12762[(2)] = inst_12721);
(statearr_12744_12762[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12730 === (8)))
{var inst_12711 = (state_12729[(7)]);var state_12729__$1 = state_12729;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12729__$1,(11),out,inst_12711);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto___12752,out))
;return ((function (switch__6337__auto__,c__6352__auto___12752,out){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_12748 = [null,null,null,null,null,null,null,null,null,null,null];(statearr_12748[(0)] = state_machine__6338__auto__);
(statearr_12748[(1)] = (1));
return statearr_12748;
});
var state_machine__6338__auto____1 = (function (state_12729){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12729);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e12749){if((e12749 instanceof Object))
{var ex__6341__auto__ = e12749;var statearr_12750_12763 = state_12729;(statearr_12750_12763[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12729);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e12749;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__12764 = state_12729;
state_12729 = G__12764;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12729){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12729);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___12752,out))
})();var state__6354__auto__ = (function (){var statearr_12751 = f__6353__auto__.call(null);(statearr_12751[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___12752);
return statearr_12751;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___12752,out))
);
return out;
});
take = function(n,ch,buf_or_n){
switch(arguments.length){
case 2:
return take__2.call(this,n,ch);
case 3:
return take__3.call(this,n,ch,buf_or_n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
take.cljs$core$IFn$_invoke$arity$2 = take__2;
take.cljs$core$IFn$_invoke$arity$3 = take__3;
return take;
})()
;
/**
* Returns a channel that will contain values from ch. Consecutive duplicate
* values will be dropped.
* 
* The output channel is unbuffered by default, unless buf-or-n is given.
*/
cljs.core.async.unique = (function() {
var unique = null;
var unique__1 = (function (ch){return unique.call(null,ch,null);
});
var unique__2 = (function (ch,buf_or_n){var out = cljs.core.async.chan.call(null,buf_or_n);var c__6352__auto___12861 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___12861,out){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___12861,out){
return (function (state_12836){var state_val_12837 = (state_12836[(1)]);if((state_val_12837 === (7)))
{var inst_12831 = (state_12836[(2)]);var state_12836__$1 = state_12836;var statearr_12838_12862 = state_12836__$1;(statearr_12838_12862[(2)] = inst_12831);
(statearr_12838_12862[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12837 === (1)))
{var inst_12813 = null;var state_12836__$1 = (function (){var statearr_12839 = state_12836;(statearr_12839[(7)] = inst_12813);
return statearr_12839;
})();var statearr_12840_12863 = state_12836__$1;(statearr_12840_12863[(2)] = null);
(statearr_12840_12863[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12837 === (4)))
{var inst_12816 = (state_12836[(8)]);var inst_12816__$1 = (state_12836[(2)]);var inst_12817 = (inst_12816__$1 == null);var inst_12818 = cljs.core.not.call(null,inst_12817);var state_12836__$1 = (function (){var statearr_12841 = state_12836;(statearr_12841[(8)] = inst_12816__$1);
return statearr_12841;
})();if(inst_12818)
{var statearr_12842_12864 = state_12836__$1;(statearr_12842_12864[(1)] = (5));
} else
{var statearr_12843_12865 = state_12836__$1;(statearr_12843_12865[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12837 === (6)))
{var state_12836__$1 = state_12836;var statearr_12844_12866 = state_12836__$1;(statearr_12844_12866[(2)] = null);
(statearr_12844_12866[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12837 === (3)))
{var inst_12833 = (state_12836[(2)]);var inst_12834 = cljs.core.async.close_BANG_.call(null,out);var state_12836__$1 = (function (){var statearr_12845 = state_12836;(statearr_12845[(9)] = inst_12833);
return statearr_12845;
})();return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12836__$1,inst_12834);
} else
{if((state_val_12837 === (2)))
{var state_12836__$1 = state_12836;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_12836__$1,(4),ch);
} else
{if((state_val_12837 === (11)))
{var inst_12816 = (state_12836[(8)]);var inst_12825 = (state_12836[(2)]);var inst_12813 = inst_12816;var state_12836__$1 = (function (){var statearr_12846 = state_12836;(statearr_12846[(7)] = inst_12813);
(statearr_12846[(10)] = inst_12825);
return statearr_12846;
})();var statearr_12847_12867 = state_12836__$1;(statearr_12847_12867[(2)] = null);
(statearr_12847_12867[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12837 === (9)))
{var inst_12816 = (state_12836[(8)]);var state_12836__$1 = state_12836;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12836__$1,(11),out,inst_12816);
} else
{if((state_val_12837 === (5)))
{var inst_12813 = (state_12836[(7)]);var inst_12816 = (state_12836[(8)]);var inst_12820 = cljs.core._EQ_.call(null,inst_12816,inst_12813);var state_12836__$1 = state_12836;if(inst_12820)
{var statearr_12849_12868 = state_12836__$1;(statearr_12849_12868[(1)] = (8));
} else
{var statearr_12850_12869 = state_12836__$1;(statearr_12850_12869[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12837 === (10)))
{var inst_12828 = (state_12836[(2)]);var state_12836__$1 = state_12836;var statearr_12851_12870 = state_12836__$1;(statearr_12851_12870[(2)] = inst_12828);
(statearr_12851_12870[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12837 === (8)))
{var inst_12813 = (state_12836[(7)]);var tmp12848 = inst_12813;var inst_12813__$1 = tmp12848;var state_12836__$1 = (function (){var statearr_12852 = state_12836;(statearr_12852[(7)] = inst_12813__$1);
return statearr_12852;
})();var statearr_12853_12871 = state_12836__$1;(statearr_12853_12871[(2)] = null);
(statearr_12853_12871[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto___12861,out))
;return ((function (switch__6337__auto__,c__6352__auto___12861,out){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_12857 = [null,null,null,null,null,null,null,null,null,null,null];(statearr_12857[(0)] = state_machine__6338__auto__);
(statearr_12857[(1)] = (1));
return statearr_12857;
});
var state_machine__6338__auto____1 = (function (state_12836){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12836);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e12858){if((e12858 instanceof Object))
{var ex__6341__auto__ = e12858;var statearr_12859_12872 = state_12836;(statearr_12859_12872[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12836);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e12858;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__12873 = state_12836;
state_12836 = G__12873;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12836){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12836);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___12861,out))
})();var state__6354__auto__ = (function (){var statearr_12860 = f__6353__auto__.call(null);(statearr_12860[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___12861);
return statearr_12860;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___12861,out))
);
return out;
});
unique = function(ch,buf_or_n){
switch(arguments.length){
case 1:
return unique__1.call(this,ch);
case 2:
return unique__2.call(this,ch,buf_or_n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
unique.cljs$core$IFn$_invoke$arity$1 = unique__1;
unique.cljs$core$IFn$_invoke$arity$2 = unique__2;
return unique;
})()
;
/**
* Returns a channel that will contain vectors of n items taken from ch. The
* final vector in the return channel may be smaller than n if ch closed before
* the vector could be completely filled.
* 
* The output channel is unbuffered by default, unless buf-or-n is given
*/
cljs.core.async.partition = (function() {
var partition = null;
var partition__2 = (function (n,ch){return partition.call(null,n,ch,null);
});
var partition__3 = (function (n,ch,buf_or_n){var out = cljs.core.async.chan.call(null,buf_or_n);var c__6352__auto___13008 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___13008,out){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___13008,out){
return (function (state_12978){var state_val_12979 = (state_12978[(1)]);if((state_val_12979 === (7)))
{var inst_12974 = (state_12978[(2)]);var state_12978__$1 = state_12978;var statearr_12980_13009 = state_12978__$1;(statearr_12980_13009[(2)] = inst_12974);
(statearr_12980_13009[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12979 === (1)))
{var inst_12941 = (new Array(n));var inst_12942 = inst_12941;var inst_12943 = (0);var state_12978__$1 = (function (){var statearr_12981 = state_12978;(statearr_12981[(7)] = inst_12942);
(statearr_12981[(8)] = inst_12943);
return statearr_12981;
})();var statearr_12982_13010 = state_12978__$1;(statearr_12982_13010[(2)] = null);
(statearr_12982_13010[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12979 === (4)))
{var inst_12946 = (state_12978[(9)]);var inst_12946__$1 = (state_12978[(2)]);var inst_12947 = (inst_12946__$1 == null);var inst_12948 = cljs.core.not.call(null,inst_12947);var state_12978__$1 = (function (){var statearr_12983 = state_12978;(statearr_12983[(9)] = inst_12946__$1);
return statearr_12983;
})();if(inst_12948)
{var statearr_12984_13011 = state_12978__$1;(statearr_12984_13011[(1)] = (5));
} else
{var statearr_12985_13012 = state_12978__$1;(statearr_12985_13012[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12979 === (15)))
{var inst_12968 = (state_12978[(2)]);var state_12978__$1 = state_12978;var statearr_12986_13013 = state_12978__$1;(statearr_12986_13013[(2)] = inst_12968);
(statearr_12986_13013[(1)] = (14));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12979 === (13)))
{var state_12978__$1 = state_12978;var statearr_12987_13014 = state_12978__$1;(statearr_12987_13014[(2)] = null);
(statearr_12987_13014[(1)] = (14));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12979 === (6)))
{var inst_12943 = (state_12978[(8)]);var inst_12964 = (inst_12943 > (0));var state_12978__$1 = state_12978;if(cljs.core.truth_(inst_12964))
{var statearr_12988_13015 = state_12978__$1;(statearr_12988_13015[(1)] = (12));
} else
{var statearr_12989_13016 = state_12978__$1;(statearr_12989_13016[(1)] = (13));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12979 === (3)))
{var inst_12976 = (state_12978[(2)]);var state_12978__$1 = state_12978;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12978__$1,inst_12976);
} else
{if((state_val_12979 === (12)))
{var inst_12942 = (state_12978[(7)]);var inst_12966 = cljs.core.vec.call(null,inst_12942);var state_12978__$1 = state_12978;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12978__$1,(15),out,inst_12966);
} else
{if((state_val_12979 === (2)))
{var state_12978__$1 = state_12978;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_12978__$1,(4),ch);
} else
{if((state_val_12979 === (11)))
{var inst_12958 = (state_12978[(2)]);var inst_12959 = (new Array(n));var inst_12942 = inst_12959;var inst_12943 = (0);var state_12978__$1 = (function (){var statearr_12990 = state_12978;(statearr_12990[(7)] = inst_12942);
(statearr_12990[(8)] = inst_12943);
(statearr_12990[(10)] = inst_12958);
return statearr_12990;
})();var statearr_12991_13017 = state_12978__$1;(statearr_12991_13017[(2)] = null);
(statearr_12991_13017[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12979 === (9)))
{var inst_12942 = (state_12978[(7)]);var inst_12956 = cljs.core.vec.call(null,inst_12942);var state_12978__$1 = state_12978;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12978__$1,(11),out,inst_12956);
} else
{if((state_val_12979 === (5)))
{var inst_12946 = (state_12978[(9)]);var inst_12942 = (state_12978[(7)]);var inst_12943 = (state_12978[(8)]);var inst_12951 = (state_12978[(11)]);var inst_12950 = (inst_12942[inst_12943] = inst_12946);var inst_12951__$1 = (inst_12943 + (1));var inst_12952 = (inst_12951__$1 < n);var state_12978__$1 = (function (){var statearr_12992 = state_12978;(statearr_12992[(12)] = inst_12950);
(statearr_12992[(11)] = inst_12951__$1);
return statearr_12992;
})();if(cljs.core.truth_(inst_12952))
{var statearr_12993_13018 = state_12978__$1;(statearr_12993_13018[(1)] = (8));
} else
{var statearr_12994_13019 = state_12978__$1;(statearr_12994_13019[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12979 === (14)))
{var inst_12971 = (state_12978[(2)]);var inst_12972 = cljs.core.async.close_BANG_.call(null,out);var state_12978__$1 = (function (){var statearr_12996 = state_12978;(statearr_12996[(13)] = inst_12971);
return statearr_12996;
})();var statearr_12997_13020 = state_12978__$1;(statearr_12997_13020[(2)] = inst_12972);
(statearr_12997_13020[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12979 === (10)))
{var inst_12962 = (state_12978[(2)]);var state_12978__$1 = state_12978;var statearr_12998_13021 = state_12978__$1;(statearr_12998_13021[(2)] = inst_12962);
(statearr_12998_13021[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12979 === (8)))
{var inst_12942 = (state_12978[(7)]);var inst_12951 = (state_12978[(11)]);var tmp12995 = inst_12942;var inst_12942__$1 = tmp12995;var inst_12943 = inst_12951;var state_12978__$1 = (function (){var statearr_12999 = state_12978;(statearr_12999[(7)] = inst_12942__$1);
(statearr_12999[(8)] = inst_12943);
return statearr_12999;
})();var statearr_13000_13022 = state_12978__$1;(statearr_13000_13022[(2)] = null);
(statearr_13000_13022[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto___13008,out))
;return ((function (switch__6337__auto__,c__6352__auto___13008,out){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_13004 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_13004[(0)] = state_machine__6338__auto__);
(statearr_13004[(1)] = (1));
return statearr_13004;
});
var state_machine__6338__auto____1 = (function (state_12978){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12978);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e13005){if((e13005 instanceof Object))
{var ex__6341__auto__ = e13005;var statearr_13006_13023 = state_12978;(statearr_13006_13023[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12978);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e13005;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__13024 = state_12978;
state_12978 = G__13024;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12978){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12978);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___13008,out))
})();var state__6354__auto__ = (function (){var statearr_13007 = f__6353__auto__.call(null);(statearr_13007[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___13008);
return statearr_13007;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___13008,out))
);
return out;
});
partition = function(n,ch,buf_or_n){
switch(arguments.length){
case 2:
return partition__2.call(this,n,ch);
case 3:
return partition__3.call(this,n,ch,buf_or_n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
partition.cljs$core$IFn$_invoke$arity$2 = partition__2;
partition.cljs$core$IFn$_invoke$arity$3 = partition__3;
return partition;
})()
;
/**
* Returns a channel that will contain vectors of items taken from ch. New
* vectors will be created whenever (f itm) returns a value that differs from
* the previous item's (f itm).
* 
* The output channel is unbuffered, unless buf-or-n is given
*/
cljs.core.async.partition_by = (function() {
var partition_by = null;
var partition_by__2 = (function (f,ch){return partition_by.call(null,f,ch,null);
});
var partition_by__3 = (function (f,ch,buf_or_n){var out = cljs.core.async.chan.call(null,buf_or_n);var c__6352__auto___13167 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___13167,out){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___13167,out){
return (function (state_13137){var state_val_13138 = (state_13137[(1)]);if((state_val_13138 === (7)))
{var inst_13133 = (state_13137[(2)]);var state_13137__$1 = state_13137;var statearr_13139_13168 = state_13137__$1;(statearr_13139_13168[(2)] = inst_13133);
(statearr_13139_13168[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13138 === (1)))
{var inst_13096 = [];var inst_13097 = inst_13096;var inst_13098 = new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123);var state_13137__$1 = (function (){var statearr_13140 = state_13137;(statearr_13140[(7)] = inst_13097);
(statearr_13140[(8)] = inst_13098);
return statearr_13140;
})();var statearr_13141_13169 = state_13137__$1;(statearr_13141_13169[(2)] = null);
(statearr_13141_13169[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13138 === (4)))
{var inst_13101 = (state_13137[(9)]);var inst_13101__$1 = (state_13137[(2)]);var inst_13102 = (inst_13101__$1 == null);var inst_13103 = cljs.core.not.call(null,inst_13102);var state_13137__$1 = (function (){var statearr_13142 = state_13137;(statearr_13142[(9)] = inst_13101__$1);
return statearr_13142;
})();if(inst_13103)
{var statearr_13143_13170 = state_13137__$1;(statearr_13143_13170[(1)] = (5));
} else
{var statearr_13144_13171 = state_13137__$1;(statearr_13144_13171[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13138 === (15)))
{var inst_13127 = (state_13137[(2)]);var state_13137__$1 = state_13137;var statearr_13145_13172 = state_13137__$1;(statearr_13145_13172[(2)] = inst_13127);
(statearr_13145_13172[(1)] = (14));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13138 === (13)))
{var state_13137__$1 = state_13137;var statearr_13146_13173 = state_13137__$1;(statearr_13146_13173[(2)] = null);
(statearr_13146_13173[(1)] = (14));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13138 === (6)))
{var inst_13097 = (state_13137[(7)]);var inst_13122 = inst_13097.length;var inst_13123 = (inst_13122 > (0));var state_13137__$1 = state_13137;if(cljs.core.truth_(inst_13123))
{var statearr_13147_13174 = state_13137__$1;(statearr_13147_13174[(1)] = (12));
} else
{var statearr_13148_13175 = state_13137__$1;(statearr_13148_13175[(1)] = (13));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13138 === (3)))
{var inst_13135 = (state_13137[(2)]);var state_13137__$1 = state_13137;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_13137__$1,inst_13135);
} else
{if((state_val_13138 === (12)))
{var inst_13097 = (state_13137[(7)]);var inst_13125 = cljs.core.vec.call(null,inst_13097);var state_13137__$1 = state_13137;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_13137__$1,(15),out,inst_13125);
} else
{if((state_val_13138 === (2)))
{var state_13137__$1 = state_13137;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_13137__$1,(4),ch);
} else
{if((state_val_13138 === (11)))
{var inst_13101 = (state_13137[(9)]);var inst_13105 = (state_13137[(10)]);var inst_13115 = (state_13137[(2)]);var inst_13116 = [];var inst_13117 = inst_13116.push(inst_13101);var inst_13097 = inst_13116;var inst_13098 = inst_13105;var state_13137__$1 = (function (){var statearr_13149 = state_13137;(statearr_13149[(7)] = inst_13097);
(statearr_13149[(8)] = inst_13098);
(statearr_13149[(11)] = inst_13115);
(statearr_13149[(12)] = inst_13117);
return statearr_13149;
})();var statearr_13150_13176 = state_13137__$1;(statearr_13150_13176[(2)] = null);
(statearr_13150_13176[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13138 === (9)))
{var inst_13097 = (state_13137[(7)]);var inst_13113 = cljs.core.vec.call(null,inst_13097);var state_13137__$1 = state_13137;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_13137__$1,(11),out,inst_13113);
} else
{if((state_val_13138 === (5)))
{var inst_13098 = (state_13137[(8)]);var inst_13101 = (state_13137[(9)]);var inst_13105 = (state_13137[(10)]);var inst_13105__$1 = f.call(null,inst_13101);var inst_13106 = cljs.core._EQ_.call(null,inst_13105__$1,inst_13098);var inst_13107 = cljs.core.keyword_identical_QMARK_.call(null,inst_13098,new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123));var inst_13108 = (inst_13106) || (inst_13107);var state_13137__$1 = (function (){var statearr_13151 = state_13137;(statearr_13151[(10)] = inst_13105__$1);
return statearr_13151;
})();if(cljs.core.truth_(inst_13108))
{var statearr_13152_13177 = state_13137__$1;(statearr_13152_13177[(1)] = (8));
} else
{var statearr_13153_13178 = state_13137__$1;(statearr_13153_13178[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13138 === (14)))
{var inst_13130 = (state_13137[(2)]);var inst_13131 = cljs.core.async.close_BANG_.call(null,out);var state_13137__$1 = (function (){var statearr_13155 = state_13137;(statearr_13155[(13)] = inst_13130);
return statearr_13155;
})();var statearr_13156_13179 = state_13137__$1;(statearr_13156_13179[(2)] = inst_13131);
(statearr_13156_13179[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13138 === (10)))
{var inst_13120 = (state_13137[(2)]);var state_13137__$1 = state_13137;var statearr_13157_13180 = state_13137__$1;(statearr_13157_13180[(2)] = inst_13120);
(statearr_13157_13180[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13138 === (8)))
{var inst_13097 = (state_13137[(7)]);var inst_13101 = (state_13137[(9)]);var inst_13105 = (state_13137[(10)]);var inst_13110 = inst_13097.push(inst_13101);var tmp13154 = inst_13097;var inst_13097__$1 = tmp13154;var inst_13098 = inst_13105;var state_13137__$1 = (function (){var statearr_13158 = state_13137;(statearr_13158[(7)] = inst_13097__$1);
(statearr_13158[(8)] = inst_13098);
(statearr_13158[(14)] = inst_13110);
return statearr_13158;
})();var statearr_13159_13181 = state_13137__$1;(statearr_13159_13181[(2)] = null);
(statearr_13159_13181[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__6352__auto___13167,out))
;return ((function (switch__6337__auto__,c__6352__auto___13167,out){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_13163 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_13163[(0)] = state_machine__6338__auto__);
(statearr_13163[(1)] = (1));
return statearr_13163;
});
var state_machine__6338__auto____1 = (function (state_13137){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_13137);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e13164){if((e13164 instanceof Object))
{var ex__6341__auto__ = e13164;var statearr_13165_13182 = state_13137;(statearr_13165_13182[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_13137);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e13164;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__13183 = state_13137;
state_13137 = G__13183;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_13137){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_13137);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___13167,out))
})();var state__6354__auto__ = (function (){var statearr_13166 = f__6353__auto__.call(null);(statearr_13166[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___13167);
return statearr_13166;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___13167,out))
);
return out;
});
partition_by = function(f,ch,buf_or_n){
switch(arguments.length){
case 2:
return partition_by__2.call(this,f,ch);
case 3:
return partition_by__3.call(this,f,ch,buf_or_n);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
partition_by.cljs$core$IFn$_invoke$arity$2 = partition_by__2;
partition_by.cljs$core$IFn$_invoke$arity$3 = partition_by__3;
return partition_by;
})()
;

//# sourceMappingURL=async.js.map
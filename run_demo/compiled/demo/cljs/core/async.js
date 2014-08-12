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
cljs.core.async.fn_handler = (function fn_handler(f){if(typeof cljs.core.async.t10521 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10521 = (function (f,fn_handler,meta10522){
this.f = f;
this.fn_handler = fn_handler;
this.meta10522 = meta10522;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10521.cljs$lang$type = true;
cljs.core.async.t10521.cljs$lang$ctorStr = "cljs.core.async/t10521";
cljs.core.async.t10521.cljs$lang$ctorPrWriter = (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10521");
});
cljs.core.async.t10521.prototype.cljs$core$async$impl$protocols$Handler$ = true;
cljs.core.async.t10521.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return true;
});
cljs.core.async.t10521.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return self__.f;
});
cljs.core.async.t10521.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_10523){var self__ = this;
var _10523__$1 = this;return self__.meta10522;
});
cljs.core.async.t10521.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_10523,meta10522__$1){var self__ = this;
var _10523__$1 = this;return (new cljs.core.async.t10521(self__.f,self__.fn_handler,meta10522__$1));
});
cljs.core.async.__GT_t10521 = (function __GT_t10521(f__$1,fn_handler__$1,meta10522){return (new cljs.core.async.t10521(f__$1,fn_handler__$1,meta10522));
});
}
return (new cljs.core.async.t10521(f,fn_handler,null));
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
cljs.core.async.unblocking_buffer_QMARK_ = (function unblocking_buffer_QMARK_(buff){var G__10525 = buff;if(G__10525)
{var bit__4192__auto__ = null;if(cljs.core.truth_((function (){var or__3542__auto__ = bit__4192__auto__;if(cljs.core.truth_(or__3542__auto__))
{return or__3542__auto__;
} else
{return G__10525.cljs$core$async$impl$protocols$UnblockingBuffer$;
}
})()))
{return true;
} else
{if((!G__10525.cljs$lang$protocol_mask$partition$))
{return cljs.core.native_satisfies_QMARK_.call(null,cljs.core.async.impl.protocols.UnblockingBuffer,G__10525);
} else
{return false;
}
}
} else
{return cljs.core.native_satisfies_QMARK_.call(null,cljs.core.async.impl.protocols.UnblockingBuffer,G__10525);
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
{var val_10526 = cljs.core.deref.call(null,ret);if(cljs.core.truth_(on_caller_QMARK_))
{fn1.call(null,val_10526);
} else
{cljs.core.async.impl.dispatch.run.call(null,((function (val_10526,ret){
return (function (){return fn1.call(null,val_10526);
});})(val_10526,ret))
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
cljs.core.async.random_array = (function random_array(n){var a = (new Array(n));var n__4398__auto___10527 = n;var x_10528 = (0);while(true){
if((x_10528 < n__4398__auto___10527))
{(a[x_10528] = (0));
{
var G__10529 = (x_10528 + (1));
x_10528 = G__10529;
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
var G__10530 = (i + (1));
i = G__10530;
continue;
}
}
break;
}
});
cljs.core.async.alt_flag = (function alt_flag(){var flag = cljs.core.atom.call(null,true);if(typeof cljs.core.async.t10534 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10534 = (function (flag,alt_flag,meta10535){
this.flag = flag;
this.alt_flag = alt_flag;
this.meta10535 = meta10535;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10534.cljs$lang$type = true;
cljs.core.async.t10534.cljs$lang$ctorStr = "cljs.core.async/t10534";
cljs.core.async.t10534.cljs$lang$ctorPrWriter = ((function (flag){
return (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10534");
});})(flag))
;
cljs.core.async.t10534.prototype.cljs$core$async$impl$protocols$Handler$ = true;
cljs.core.async.t10534.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = ((function (flag){
return (function (_){var self__ = this;
var ___$1 = this;return cljs.core.deref.call(null,self__.flag);
});})(flag))
;
cljs.core.async.t10534.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = ((function (flag){
return (function (_){var self__ = this;
var ___$1 = this;cljs.core.reset_BANG_.call(null,self__.flag,null);
return true;
});})(flag))
;
cljs.core.async.t10534.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (flag){
return (function (_10536){var self__ = this;
var _10536__$1 = this;return self__.meta10535;
});})(flag))
;
cljs.core.async.t10534.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (flag){
return (function (_10536,meta10535__$1){var self__ = this;
var _10536__$1 = this;return (new cljs.core.async.t10534(self__.flag,self__.alt_flag,meta10535__$1));
});})(flag))
;
cljs.core.async.__GT_t10534 = ((function (flag){
return (function __GT_t10534(flag__$1,alt_flag__$1,meta10535){return (new cljs.core.async.t10534(flag__$1,alt_flag__$1,meta10535));
});})(flag))
;
}
return (new cljs.core.async.t10534(flag,alt_flag,null));
});
cljs.core.async.alt_handler = (function alt_handler(flag,cb){if(typeof cljs.core.async.t10540 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10540 = (function (cb,flag,alt_handler,meta10541){
this.cb = cb;
this.flag = flag;
this.alt_handler = alt_handler;
this.meta10541 = meta10541;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10540.cljs$lang$type = true;
cljs.core.async.t10540.cljs$lang$ctorStr = "cljs.core.async/t10540";
cljs.core.async.t10540.cljs$lang$ctorPrWriter = (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10540");
});
cljs.core.async.t10540.prototype.cljs$core$async$impl$protocols$Handler$ = true;
cljs.core.async.t10540.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.active_QMARK_.call(null,self__.flag);
});
cljs.core.async.t10540.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){var self__ = this;
var ___$1 = this;cljs.core.async.impl.protocols.commit.call(null,self__.flag);
return self__.cb;
});
cljs.core.async.t10540.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_10542){var self__ = this;
var _10542__$1 = this;return self__.meta10541;
});
cljs.core.async.t10540.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_10542,meta10541__$1){var self__ = this;
var _10542__$1 = this;return (new cljs.core.async.t10540(self__.cb,self__.flag,self__.alt_handler,meta10541__$1));
});
cljs.core.async.__GT_t10540 = (function __GT_t10540(cb__$1,flag__$1,alt_handler__$1,meta10541){return (new cljs.core.async.t10540(cb__$1,flag__$1,alt_handler__$1,meta10541));
});
}
return (new cljs.core.async.t10540(cb,flag,alt_handler,null));
});
/**
* returns derefable [val port] if immediate, nil if enqueued
*/
cljs.core.async.do_alts = (function do_alts(fret,ports,opts){var flag = cljs.core.async.alt_flag.call(null);var n = cljs.core.count.call(null,ports);var idxs = cljs.core.async.random_array.call(null,n);var priority = new cljs.core.Keyword(null,"priority","priority",1431093715).cljs$core$IFn$_invoke$arity$1(opts);var ret = (function (){var i = (0);while(true){
if((i < n))
{var idx = (cljs.core.truth_(priority)?i:(idxs[i]));var port = cljs.core.nth.call(null,ports,idx);var wport = ((cljs.core.vector_QMARK_.call(null,port))?port.call(null,(0)):null);var vbox = (cljs.core.truth_(wport)?(function (){var val = port.call(null,(1));return cljs.core.async.impl.protocols.put_BANG_.call(null,wport,val,cljs.core.async.alt_handler.call(null,flag,((function (i,val,idx,port,wport,flag,n,idxs,priority){
return (function (p1__10543_SHARP_){return fret.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__10543_SHARP_,wport], null));
});})(i,val,idx,port,wport,flag,n,idxs,priority))
));
})():cljs.core.async.impl.protocols.take_BANG_.call(null,port,cljs.core.async.alt_handler.call(null,flag,((function (i,idx,port,wport,flag,n,idxs,priority){
return (function (p1__10544_SHARP_){return fret.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__10544_SHARP_,port], null));
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
var G__10545 = (i + (1));
i = G__10545;
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
var alts_BANG___delegate = function (ports,p__10546){var map__10548 = p__10546;var map__10548__$1 = ((cljs.core.seq_QMARK_.call(null,map__10548))?cljs.core.apply.call(null,cljs.core.hash_map,map__10548):map__10548);var opts = map__10548__$1;if(null)
{return null;
} else
{throw (new Error(("Assert failed: alts! used not in (go ...) block\n"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,null)))));
}
};
var alts_BANG_ = function (ports,var_args){
var p__10546 = null;if (arguments.length > 1) {
  p__10546 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 1),0);} 
return alts_BANG___delegate.call(this,ports,p__10546);};
alts_BANG_.cljs$lang$maxFixedArity = 1;
alts_BANG_.cljs$lang$applyTo = (function (arglist__10549){
var ports = cljs.core.first(arglist__10549);
var p__10546 = cljs.core.rest(arglist__10549);
return alts_BANG___delegate(ports,p__10546);
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
cljs.core.async.map_LT_ = (function map_LT_(f,ch){if(typeof cljs.core.async.t10557 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10557 = (function (ch,f,map_LT_,meta10558){
this.ch = ch;
this.f = f;
this.map_LT_ = map_LT_;
this.meta10558 = meta10558;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10557.cljs$lang$type = true;
cljs.core.async.t10557.cljs$lang$ctorStr = "cljs.core.async/t10557";
cljs.core.async.t10557.cljs$lang$ctorPrWriter = (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10557");
});
cljs.core.async.t10557.prototype.cljs$core$async$impl$protocols$WritePort$ = true;
cljs.core.async.t10557.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,val,fn1);
});
cljs.core.async.t10557.prototype.cljs$core$async$impl$protocols$ReadPort$ = true;
cljs.core.async.t10557.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){var self__ = this;
var ___$1 = this;var ret = cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,(function (){if(typeof cljs.core.async.t10560 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10560 = (function (fn1,_,meta10558,ch,f,map_LT_,meta10561){
this.fn1 = fn1;
this._ = _;
this.meta10558 = meta10558;
this.ch = ch;
this.f = f;
this.map_LT_ = map_LT_;
this.meta10561 = meta10561;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10560.cljs$lang$type = true;
cljs.core.async.t10560.cljs$lang$ctorStr = "cljs.core.async/t10560";
cljs.core.async.t10560.cljs$lang$ctorPrWriter = ((function (___$1){
return (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10560");
});})(___$1))
;
cljs.core.async.t10560.prototype.cljs$core$async$impl$protocols$Handler$ = true;
cljs.core.async.t10560.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = ((function (___$1){
return (function (___$3){var self__ = this;
var ___$4 = this;return cljs.core.async.impl.protocols.active_QMARK_.call(null,self__.fn1);
});})(___$1))
;
cljs.core.async.t10560.prototype.cljs$core$async$impl$protocols$Handler$lock_id$arity$1 = ((function (___$1){
return (function (___$3){var self__ = this;
var ___$4 = this;return cljs.core.async.impl.protocols.lock_id.call(null,self__.fn1);
});})(___$1))
;
cljs.core.async.t10560.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = ((function (___$1){
return (function (___$3){var self__ = this;
var ___$4 = this;var f1 = cljs.core.async.impl.protocols.commit.call(null,self__.fn1);return ((function (f1,___$4,___$1){
return (function (p1__10550_SHARP_){return f1.call(null,(((p1__10550_SHARP_ == null))?null:self__.f.call(null,p1__10550_SHARP_)));
});
;})(f1,___$4,___$1))
});})(___$1))
;
cljs.core.async.t10560.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (___$1){
return (function (_10562){var self__ = this;
var _10562__$1 = this;return self__.meta10561;
});})(___$1))
;
cljs.core.async.t10560.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (___$1){
return (function (_10562,meta10561__$1){var self__ = this;
var _10562__$1 = this;return (new cljs.core.async.t10560(self__.fn1,self__._,self__.meta10558,self__.ch,self__.f,self__.map_LT_,meta10561__$1));
});})(___$1))
;
cljs.core.async.__GT_t10560 = ((function (___$1){
return (function __GT_t10560(fn1__$1,___$2,meta10558__$1,ch__$2,f__$2,map_LT___$2,meta10561){return (new cljs.core.async.t10560(fn1__$1,___$2,meta10558__$1,ch__$2,f__$2,map_LT___$2,meta10561));
});})(___$1))
;
}
return (new cljs.core.async.t10560(fn1,___$1,self__.meta10558,self__.ch,self__.f,self__.map_LT_,null));
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
cljs.core.async.t10557.prototype.cljs$core$async$impl$protocols$Channel$ = true;
cljs.core.async.t10557.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});
cljs.core.async.t10557.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch);
});
cljs.core.async.t10557.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_10559){var self__ = this;
var _10559__$1 = this;return self__.meta10558;
});
cljs.core.async.t10557.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_10559,meta10558__$1){var self__ = this;
var _10559__$1 = this;return (new cljs.core.async.t10557(self__.ch,self__.f,self__.map_LT_,meta10558__$1));
});
cljs.core.async.__GT_t10557 = (function __GT_t10557(ch__$1,f__$1,map_LT___$1,meta10558){return (new cljs.core.async.t10557(ch__$1,f__$1,map_LT___$1,meta10558));
});
}
return (new cljs.core.async.t10557(ch,f,map_LT_,null));
});
/**
* Takes a function and a target channel, and returns a channel which
* applies f to each value before supplying it to the target channel.
*/
cljs.core.async.map_GT_ = (function map_GT_(f,ch){if(typeof cljs.core.async.t10566 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10566 = (function (ch,f,map_GT_,meta10567){
this.ch = ch;
this.f = f;
this.map_GT_ = map_GT_;
this.meta10567 = meta10567;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10566.cljs$lang$type = true;
cljs.core.async.t10566.cljs$lang$ctorStr = "cljs.core.async/t10566";
cljs.core.async.t10566.cljs$lang$ctorPrWriter = (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10566");
});
cljs.core.async.t10566.prototype.cljs$core$async$impl$protocols$WritePort$ = true;
cljs.core.async.t10566.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,self__.f.call(null,val),fn1);
});
cljs.core.async.t10566.prototype.cljs$core$async$impl$protocols$ReadPort$ = true;
cljs.core.async.t10566.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,fn1);
});
cljs.core.async.t10566.prototype.cljs$core$async$impl$protocols$Channel$ = true;
cljs.core.async.t10566.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});
cljs.core.async.t10566.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_10568){var self__ = this;
var _10568__$1 = this;return self__.meta10567;
});
cljs.core.async.t10566.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_10568,meta10567__$1){var self__ = this;
var _10568__$1 = this;return (new cljs.core.async.t10566(self__.ch,self__.f,self__.map_GT_,meta10567__$1));
});
cljs.core.async.__GT_t10566 = (function __GT_t10566(ch__$1,f__$1,map_GT___$1,meta10567){return (new cljs.core.async.t10566(ch__$1,f__$1,map_GT___$1,meta10567));
});
}
return (new cljs.core.async.t10566(ch,f,map_GT_,null));
});
/**
* Takes a predicate and a target channel, and returns a channel which
* supplies only the values for which the predicate returns true to the
* target channel.
*/
cljs.core.async.filter_GT_ = (function filter_GT_(p,ch){if(typeof cljs.core.async.t10572 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t10572 = (function (ch,p,filter_GT_,meta10573){
this.ch = ch;
this.p = p;
this.filter_GT_ = filter_GT_;
this.meta10573 = meta10573;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t10572.cljs$lang$type = true;
cljs.core.async.t10572.cljs$lang$ctorStr = "cljs.core.async/t10572";
cljs.core.async.t10572.cljs$lang$ctorPrWriter = (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t10572");
});
cljs.core.async.t10572.prototype.cljs$core$async$impl$protocols$WritePort$ = true;
cljs.core.async.t10572.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){var self__ = this;
var ___$1 = this;if(cljs.core.truth_(self__.p.call(null,val)))
{return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,val,fn1);
} else
{return cljs.core.async.impl.channels.box.call(null,cljs.core.not.call(null,cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch)));
}
});
cljs.core.async.t10572.prototype.cljs$core$async$impl$protocols$ReadPort$ = true;
cljs.core.async.t10572.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,fn1);
});
cljs.core.async.t10572.prototype.cljs$core$async$impl$protocols$Channel$ = true;
cljs.core.async.t10572.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});
cljs.core.async.t10572.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch);
});
cljs.core.async.t10572.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_10574){var self__ = this;
var _10574__$1 = this;return self__.meta10573;
});
cljs.core.async.t10572.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_10574,meta10573__$1){var self__ = this;
var _10574__$1 = this;return (new cljs.core.async.t10572(self__.ch,self__.p,self__.filter_GT_,meta10573__$1));
});
cljs.core.async.__GT_t10572 = (function __GT_t10572(ch__$1,p__$1,filter_GT___$1,meta10573){return (new cljs.core.async.t10572(ch__$1,p__$1,filter_GT___$1,meta10573));
});
}
return (new cljs.core.async.t10572(ch,p,filter_GT_,null));
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
var filter_LT___3 = (function (p,ch,buf_or_n){var out = cljs.core.async.chan.call(null,buf_or_n);var c__6352__auto___10657 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___10657,out){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___10657,out){
return (function (state_10636){var state_val_10637 = (state_10636[(1)]);if((state_val_10637 === (7)))
{var inst_10632 = (state_10636[(2)]);var state_10636__$1 = state_10636;var statearr_10638_10658 = state_10636__$1;(statearr_10638_10658[(2)] = inst_10632);
(statearr_10638_10658[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10637 === (1)))
{var state_10636__$1 = state_10636;var statearr_10639_10659 = state_10636__$1;(statearr_10639_10659[(2)] = null);
(statearr_10639_10659[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10637 === (4)))
{var inst_10618 = (state_10636[(7)]);var inst_10618__$1 = (state_10636[(2)]);var inst_10619 = (inst_10618__$1 == null);var state_10636__$1 = (function (){var statearr_10640 = state_10636;(statearr_10640[(7)] = inst_10618__$1);
return statearr_10640;
})();if(cljs.core.truth_(inst_10619))
{var statearr_10641_10660 = state_10636__$1;(statearr_10641_10660[(1)] = (5));
} else
{var statearr_10642_10661 = state_10636__$1;(statearr_10642_10661[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10637 === (6)))
{var inst_10618 = (state_10636[(7)]);var inst_10623 = p.call(null,inst_10618);var state_10636__$1 = state_10636;if(cljs.core.truth_(inst_10623))
{var statearr_10643_10662 = state_10636__$1;(statearr_10643_10662[(1)] = (8));
} else
{var statearr_10644_10663 = state_10636__$1;(statearr_10644_10663[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10637 === (3)))
{var inst_10634 = (state_10636[(2)]);var state_10636__$1 = state_10636;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10636__$1,inst_10634);
} else
{if((state_val_10637 === (2)))
{var state_10636__$1 = state_10636;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_10636__$1,(4),ch);
} else
{if((state_val_10637 === (11)))
{var inst_10626 = (state_10636[(2)]);var state_10636__$1 = state_10636;var statearr_10645_10664 = state_10636__$1;(statearr_10645_10664[(2)] = inst_10626);
(statearr_10645_10664[(1)] = (10));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10637 === (9)))
{var state_10636__$1 = state_10636;var statearr_10646_10665 = state_10636__$1;(statearr_10646_10665[(2)] = null);
(statearr_10646_10665[(1)] = (10));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10637 === (5)))
{var inst_10621 = cljs.core.async.close_BANG_.call(null,out);var state_10636__$1 = state_10636;var statearr_10647_10666 = state_10636__$1;(statearr_10647_10666[(2)] = inst_10621);
(statearr_10647_10666[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10637 === (10)))
{var inst_10629 = (state_10636[(2)]);var state_10636__$1 = (function (){var statearr_10648 = state_10636;(statearr_10648[(8)] = inst_10629);
return statearr_10648;
})();var statearr_10649_10667 = state_10636__$1;(statearr_10649_10667[(2)] = null);
(statearr_10649_10667[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10637 === (8)))
{var inst_10618 = (state_10636[(7)]);var state_10636__$1 = state_10636;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10636__$1,(11),out,inst_10618);
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
});})(c__6352__auto___10657,out))
;return ((function (switch__6337__auto__,c__6352__auto___10657,out){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_10653 = [null,null,null,null,null,null,null,null,null];(statearr_10653[(0)] = state_machine__6338__auto__);
(statearr_10653[(1)] = (1));
return statearr_10653;
});
var state_machine__6338__auto____1 = (function (state_10636){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_10636);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e10654){if((e10654 instanceof Object))
{var ex__6341__auto__ = e10654;var statearr_10655_10668 = state_10636;(statearr_10655_10668[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10636);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e10654;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__10669 = state_10636;
state_10636 = G__10669;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_10636){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_10636);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___10657,out))
})();var state__6354__auto__ = (function (){var statearr_10656 = f__6353__auto__.call(null);(statearr_10656[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___10657);
return statearr_10656;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___10657,out))
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
return (function (state_10835){var state_val_10836 = (state_10835[(1)]);if((state_val_10836 === (7)))
{var inst_10831 = (state_10835[(2)]);var state_10835__$1 = state_10835;var statearr_10837_10878 = state_10835__$1;(statearr_10837_10878[(2)] = inst_10831);
(statearr_10837_10878[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (20)))
{var inst_10801 = (state_10835[(7)]);var inst_10812 = (state_10835[(2)]);var inst_10813 = cljs.core.next.call(null,inst_10801);var inst_10787 = inst_10813;var inst_10788 = null;var inst_10789 = (0);var inst_10790 = (0);var state_10835__$1 = (function (){var statearr_10838 = state_10835;(statearr_10838[(8)] = inst_10812);
(statearr_10838[(9)] = inst_10788);
(statearr_10838[(10)] = inst_10790);
(statearr_10838[(11)] = inst_10787);
(statearr_10838[(12)] = inst_10789);
return statearr_10838;
})();var statearr_10839_10879 = state_10835__$1;(statearr_10839_10879[(2)] = null);
(statearr_10839_10879[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (1)))
{var state_10835__$1 = state_10835;var statearr_10840_10880 = state_10835__$1;(statearr_10840_10880[(2)] = null);
(statearr_10840_10880[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (4)))
{var inst_10776 = (state_10835[(13)]);var inst_10776__$1 = (state_10835[(2)]);var inst_10777 = (inst_10776__$1 == null);var state_10835__$1 = (function (){var statearr_10841 = state_10835;(statearr_10841[(13)] = inst_10776__$1);
return statearr_10841;
})();if(cljs.core.truth_(inst_10777))
{var statearr_10842_10881 = state_10835__$1;(statearr_10842_10881[(1)] = (5));
} else
{var statearr_10843_10882 = state_10835__$1;(statearr_10843_10882[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (15)))
{var state_10835__$1 = state_10835;var statearr_10847_10883 = state_10835__$1;(statearr_10847_10883[(2)] = null);
(statearr_10847_10883[(1)] = (16));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (21)))
{var state_10835__$1 = state_10835;var statearr_10848_10884 = state_10835__$1;(statearr_10848_10884[(2)] = null);
(statearr_10848_10884[(1)] = (23));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (13)))
{var inst_10788 = (state_10835[(9)]);var inst_10790 = (state_10835[(10)]);var inst_10787 = (state_10835[(11)]);var inst_10789 = (state_10835[(12)]);var inst_10797 = (state_10835[(2)]);var inst_10798 = (inst_10790 + (1));var tmp10844 = inst_10788;var tmp10845 = inst_10787;var tmp10846 = inst_10789;var inst_10787__$1 = tmp10845;var inst_10788__$1 = tmp10844;var inst_10789__$1 = tmp10846;var inst_10790__$1 = inst_10798;var state_10835__$1 = (function (){var statearr_10849 = state_10835;(statearr_10849[(9)] = inst_10788__$1);
(statearr_10849[(14)] = inst_10797);
(statearr_10849[(10)] = inst_10790__$1);
(statearr_10849[(11)] = inst_10787__$1);
(statearr_10849[(12)] = inst_10789__$1);
return statearr_10849;
})();var statearr_10850_10885 = state_10835__$1;(statearr_10850_10885[(2)] = null);
(statearr_10850_10885[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (22)))
{var state_10835__$1 = state_10835;var statearr_10851_10886 = state_10835__$1;(statearr_10851_10886[(2)] = null);
(statearr_10851_10886[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (6)))
{var inst_10776 = (state_10835[(13)]);var inst_10785 = f.call(null,inst_10776);var inst_10786 = cljs.core.seq.call(null,inst_10785);var inst_10787 = inst_10786;var inst_10788 = null;var inst_10789 = (0);var inst_10790 = (0);var state_10835__$1 = (function (){var statearr_10852 = state_10835;(statearr_10852[(9)] = inst_10788);
(statearr_10852[(10)] = inst_10790);
(statearr_10852[(11)] = inst_10787);
(statearr_10852[(12)] = inst_10789);
return statearr_10852;
})();var statearr_10853_10887 = state_10835__$1;(statearr_10853_10887[(2)] = null);
(statearr_10853_10887[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (17)))
{var inst_10801 = (state_10835[(7)]);var inst_10805 = cljs.core.chunk_first.call(null,inst_10801);var inst_10806 = cljs.core.chunk_rest.call(null,inst_10801);var inst_10807 = cljs.core.count.call(null,inst_10805);var inst_10787 = inst_10806;var inst_10788 = inst_10805;var inst_10789 = inst_10807;var inst_10790 = (0);var state_10835__$1 = (function (){var statearr_10854 = state_10835;(statearr_10854[(9)] = inst_10788);
(statearr_10854[(10)] = inst_10790);
(statearr_10854[(11)] = inst_10787);
(statearr_10854[(12)] = inst_10789);
return statearr_10854;
})();var statearr_10855_10888 = state_10835__$1;(statearr_10855_10888[(2)] = null);
(statearr_10855_10888[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (3)))
{var inst_10833 = (state_10835[(2)]);var state_10835__$1 = state_10835;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10835__$1,inst_10833);
} else
{if((state_val_10836 === (12)))
{var inst_10821 = (state_10835[(2)]);var state_10835__$1 = state_10835;var statearr_10856_10889 = state_10835__$1;(statearr_10856_10889[(2)] = inst_10821);
(statearr_10856_10889[(1)] = (9));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (2)))
{var state_10835__$1 = state_10835;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_10835__$1,(4),in$);
} else
{if((state_val_10836 === (23)))
{var inst_10829 = (state_10835[(2)]);var state_10835__$1 = state_10835;var statearr_10857_10890 = state_10835__$1;(statearr_10857_10890[(2)] = inst_10829);
(statearr_10857_10890[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (19)))
{var inst_10816 = (state_10835[(2)]);var state_10835__$1 = state_10835;var statearr_10858_10891 = state_10835__$1;(statearr_10858_10891[(2)] = inst_10816);
(statearr_10858_10891[(1)] = (16));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (11)))
{var inst_10801 = (state_10835[(7)]);var inst_10787 = (state_10835[(11)]);var inst_10801__$1 = cljs.core.seq.call(null,inst_10787);var state_10835__$1 = (function (){var statearr_10859 = state_10835;(statearr_10859[(7)] = inst_10801__$1);
return statearr_10859;
})();if(inst_10801__$1)
{var statearr_10860_10892 = state_10835__$1;(statearr_10860_10892[(1)] = (14));
} else
{var statearr_10861_10893 = state_10835__$1;(statearr_10861_10893[(1)] = (15));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (9)))
{var inst_10823 = (state_10835[(2)]);var inst_10824 = cljs.core.async.impl.protocols.closed_QMARK_.call(null,out);var state_10835__$1 = (function (){var statearr_10862 = state_10835;(statearr_10862[(15)] = inst_10823);
return statearr_10862;
})();if(cljs.core.truth_(inst_10824))
{var statearr_10863_10894 = state_10835__$1;(statearr_10863_10894[(1)] = (21));
} else
{var statearr_10864_10895 = state_10835__$1;(statearr_10864_10895[(1)] = (22));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (5)))
{var inst_10779 = cljs.core.async.close_BANG_.call(null,out);var state_10835__$1 = state_10835;var statearr_10865_10896 = state_10835__$1;(statearr_10865_10896[(2)] = inst_10779);
(statearr_10865_10896[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (14)))
{var inst_10801 = (state_10835[(7)]);var inst_10803 = cljs.core.chunked_seq_QMARK_.call(null,inst_10801);var state_10835__$1 = state_10835;if(inst_10803)
{var statearr_10866_10897 = state_10835__$1;(statearr_10866_10897[(1)] = (17));
} else
{var statearr_10867_10898 = state_10835__$1;(statearr_10867_10898[(1)] = (18));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (16)))
{var inst_10819 = (state_10835[(2)]);var state_10835__$1 = state_10835;var statearr_10868_10899 = state_10835__$1;(statearr_10868_10899[(2)] = inst_10819);
(statearr_10868_10899[(1)] = (12));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10836 === (10)))
{var inst_10788 = (state_10835[(9)]);var inst_10790 = (state_10835[(10)]);var inst_10795 = cljs.core._nth.call(null,inst_10788,inst_10790);var state_10835__$1 = state_10835;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10835__$1,(13),out,inst_10795);
} else
{if((state_val_10836 === (18)))
{var inst_10801 = (state_10835[(7)]);var inst_10810 = cljs.core.first.call(null,inst_10801);var state_10835__$1 = state_10835;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10835__$1,(20),out,inst_10810);
} else
{if((state_val_10836 === (8)))
{var inst_10790 = (state_10835[(10)]);var inst_10789 = (state_10835[(12)]);var inst_10792 = (inst_10790 < inst_10789);var inst_10793 = inst_10792;var state_10835__$1 = state_10835;if(cljs.core.truth_(inst_10793))
{var statearr_10869_10900 = state_10835__$1;(statearr_10869_10900[(1)] = (10));
} else
{var statearr_10870_10901 = state_10835__$1;(statearr_10870_10901[(1)] = (11));
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
var state_machine__6338__auto____0 = (function (){var statearr_10874 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_10874[(0)] = state_machine__6338__auto__);
(statearr_10874[(1)] = (1));
return statearr_10874;
});
var state_machine__6338__auto____1 = (function (state_10835){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_10835);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e10875){if((e10875 instanceof Object))
{var ex__6341__auto__ = e10875;var statearr_10876_10902 = state_10835;(statearr_10876_10902[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10835);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e10875;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__10903 = state_10835;
state_10835 = G__10903;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_10835){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_10835);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto__))
})();var state__6354__auto__ = (function (){var statearr_10877 = f__6353__auto__.call(null);(statearr_10877[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto__);
return statearr_10877;
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
var pipe__3 = (function (from,to,close_QMARK_){var c__6352__auto___10998 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___10998){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___10998){
return (function (state_10974){var state_val_10975 = (state_10974[(1)]);if((state_val_10975 === (7)))
{var inst_10970 = (state_10974[(2)]);var state_10974__$1 = state_10974;var statearr_10976_10999 = state_10974__$1;(statearr_10976_10999[(2)] = inst_10970);
(statearr_10976_10999[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10975 === (1)))
{var state_10974__$1 = state_10974;var statearr_10977_11000 = state_10974__$1;(statearr_10977_11000[(2)] = null);
(statearr_10977_11000[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10975 === (4)))
{var inst_10953 = (state_10974[(7)]);var inst_10953__$1 = (state_10974[(2)]);var inst_10954 = (inst_10953__$1 == null);var state_10974__$1 = (function (){var statearr_10978 = state_10974;(statearr_10978[(7)] = inst_10953__$1);
return statearr_10978;
})();if(cljs.core.truth_(inst_10954))
{var statearr_10979_11001 = state_10974__$1;(statearr_10979_11001[(1)] = (5));
} else
{var statearr_10980_11002 = state_10974__$1;(statearr_10980_11002[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10975 === (13)))
{var state_10974__$1 = state_10974;var statearr_10981_11003 = state_10974__$1;(statearr_10981_11003[(2)] = null);
(statearr_10981_11003[(1)] = (14));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10975 === (6)))
{var inst_10953 = (state_10974[(7)]);var state_10974__$1 = state_10974;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_10974__$1,(11),to,inst_10953);
} else
{if((state_val_10975 === (3)))
{var inst_10972 = (state_10974[(2)]);var state_10974__$1 = state_10974;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_10974__$1,inst_10972);
} else
{if((state_val_10975 === (12)))
{var state_10974__$1 = state_10974;var statearr_10982_11004 = state_10974__$1;(statearr_10982_11004[(2)] = null);
(statearr_10982_11004[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10975 === (2)))
{var state_10974__$1 = state_10974;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_10974__$1,(4),from);
} else
{if((state_val_10975 === (11)))
{var inst_10963 = (state_10974[(2)]);var state_10974__$1 = state_10974;if(cljs.core.truth_(inst_10963))
{var statearr_10983_11005 = state_10974__$1;(statearr_10983_11005[(1)] = (12));
} else
{var statearr_10984_11006 = state_10974__$1;(statearr_10984_11006[(1)] = (13));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10975 === (9)))
{var state_10974__$1 = state_10974;var statearr_10985_11007 = state_10974__$1;(statearr_10985_11007[(2)] = null);
(statearr_10985_11007[(1)] = (10));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10975 === (5)))
{var state_10974__$1 = state_10974;if(cljs.core.truth_(close_QMARK_))
{var statearr_10986_11008 = state_10974__$1;(statearr_10986_11008[(1)] = (8));
} else
{var statearr_10987_11009 = state_10974__$1;(statearr_10987_11009[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10975 === (14)))
{var inst_10968 = (state_10974[(2)]);var state_10974__$1 = state_10974;var statearr_10988_11010 = state_10974__$1;(statearr_10988_11010[(2)] = inst_10968);
(statearr_10988_11010[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10975 === (10)))
{var inst_10960 = (state_10974[(2)]);var state_10974__$1 = state_10974;var statearr_10989_11011 = state_10974__$1;(statearr_10989_11011[(2)] = inst_10960);
(statearr_10989_11011[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_10975 === (8)))
{var inst_10957 = cljs.core.async.close_BANG_.call(null,to);var state_10974__$1 = state_10974;var statearr_10990_11012 = state_10974__$1;(statearr_10990_11012[(2)] = inst_10957);
(statearr_10990_11012[(1)] = (10));
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
});})(c__6352__auto___10998))
;return ((function (switch__6337__auto__,c__6352__auto___10998){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_10994 = [null,null,null,null,null,null,null,null];(statearr_10994[(0)] = state_machine__6338__auto__);
(statearr_10994[(1)] = (1));
return statearr_10994;
});
var state_machine__6338__auto____1 = (function (state_10974){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_10974);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e10995){if((e10995 instanceof Object))
{var ex__6341__auto__ = e10995;var statearr_10996_11013 = state_10974;(statearr_10996_11013[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_10974);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e10995;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__11014 = state_10974;
state_10974 = G__11014;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_10974){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_10974);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___10998))
})();var state__6354__auto__ = (function (){var statearr_10997 = f__6353__auto__.call(null);(statearr_10997[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___10998);
return statearr_10997;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___10998))
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
var split__4 = (function (p,ch,t_buf_or_n,f_buf_or_n){var tc = cljs.core.async.chan.call(null,t_buf_or_n);var fc = cljs.core.async.chan.call(null,f_buf_or_n);var c__6352__auto___11115 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___11115,tc,fc){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___11115,tc,fc){
return (function (state_11090){var state_val_11091 = (state_11090[(1)]);if((state_val_11091 === (7)))
{var inst_11086 = (state_11090[(2)]);var state_11090__$1 = state_11090;var statearr_11092_11116 = state_11090__$1;(statearr_11092_11116[(2)] = inst_11086);
(statearr_11092_11116[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11091 === (1)))
{var state_11090__$1 = state_11090;var statearr_11093_11117 = state_11090__$1;(statearr_11093_11117[(2)] = null);
(statearr_11093_11117[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11091 === (4)))
{var inst_11067 = (state_11090[(7)]);var inst_11067__$1 = (state_11090[(2)]);var inst_11068 = (inst_11067__$1 == null);var state_11090__$1 = (function (){var statearr_11094 = state_11090;(statearr_11094[(7)] = inst_11067__$1);
return statearr_11094;
})();if(cljs.core.truth_(inst_11068))
{var statearr_11095_11118 = state_11090__$1;(statearr_11095_11118[(1)] = (5));
} else
{var statearr_11096_11119 = state_11090__$1;(statearr_11096_11119[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11091 === (13)))
{var state_11090__$1 = state_11090;var statearr_11097_11120 = state_11090__$1;(statearr_11097_11120[(2)] = null);
(statearr_11097_11120[(1)] = (14));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11091 === (6)))
{var inst_11067 = (state_11090[(7)]);var inst_11073 = p.call(null,inst_11067);var state_11090__$1 = state_11090;if(cljs.core.truth_(inst_11073))
{var statearr_11098_11121 = state_11090__$1;(statearr_11098_11121[(1)] = (9));
} else
{var statearr_11099_11122 = state_11090__$1;(statearr_11099_11122[(1)] = (10));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11091 === (3)))
{var inst_11088 = (state_11090[(2)]);var state_11090__$1 = state_11090;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_11090__$1,inst_11088);
} else
{if((state_val_11091 === (12)))
{var state_11090__$1 = state_11090;var statearr_11100_11123 = state_11090__$1;(statearr_11100_11123[(2)] = null);
(statearr_11100_11123[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11091 === (2)))
{var state_11090__$1 = state_11090;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_11090__$1,(4),ch);
} else
{if((state_val_11091 === (11)))
{var inst_11067 = (state_11090[(7)]);var inst_11077 = (state_11090[(2)]);var state_11090__$1 = state_11090;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_11090__$1,(8),inst_11077,inst_11067);
} else
{if((state_val_11091 === (9)))
{var state_11090__$1 = state_11090;var statearr_11101_11124 = state_11090__$1;(statearr_11101_11124[(2)] = tc);
(statearr_11101_11124[(1)] = (11));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11091 === (5)))
{var inst_11070 = cljs.core.async.close_BANG_.call(null,tc);var inst_11071 = cljs.core.async.close_BANG_.call(null,fc);var state_11090__$1 = (function (){var statearr_11102 = state_11090;(statearr_11102[(8)] = inst_11070);
return statearr_11102;
})();var statearr_11103_11125 = state_11090__$1;(statearr_11103_11125[(2)] = inst_11071);
(statearr_11103_11125[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11091 === (14)))
{var inst_11084 = (state_11090[(2)]);var state_11090__$1 = state_11090;var statearr_11104_11126 = state_11090__$1;(statearr_11104_11126[(2)] = inst_11084);
(statearr_11104_11126[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11091 === (10)))
{var state_11090__$1 = state_11090;var statearr_11105_11127 = state_11090__$1;(statearr_11105_11127[(2)] = fc);
(statearr_11105_11127[(1)] = (11));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11091 === (8)))
{var inst_11079 = (state_11090[(2)]);var state_11090__$1 = state_11090;if(cljs.core.truth_(inst_11079))
{var statearr_11106_11128 = state_11090__$1;(statearr_11106_11128[(1)] = (12));
} else
{var statearr_11107_11129 = state_11090__$1;(statearr_11107_11129[(1)] = (13));
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
});})(c__6352__auto___11115,tc,fc))
;return ((function (switch__6337__auto__,c__6352__auto___11115,tc,fc){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_11111 = [null,null,null,null,null,null,null,null,null];(statearr_11111[(0)] = state_machine__6338__auto__);
(statearr_11111[(1)] = (1));
return statearr_11111;
});
var state_machine__6338__auto____1 = (function (state_11090){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_11090);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e11112){if((e11112 instanceof Object))
{var ex__6341__auto__ = e11112;var statearr_11113_11130 = state_11090;(statearr_11113_11130[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_11090);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e11112;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__11131 = state_11090;
state_11090 = G__11131;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_11090){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_11090);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___11115,tc,fc))
})();var state__6354__auto__ = (function (){var statearr_11114 = f__6353__auto__.call(null);(statearr_11114[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___11115);
return statearr_11114;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___11115,tc,fc))
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
return (function (state_11178){var state_val_11179 = (state_11178[(1)]);if((state_val_11179 === (7)))
{var inst_11174 = (state_11178[(2)]);var state_11178__$1 = state_11178;var statearr_11180_11196 = state_11178__$1;(statearr_11180_11196[(2)] = inst_11174);
(statearr_11180_11196[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11179 === (6)))
{var inst_11164 = (state_11178[(7)]);var inst_11167 = (state_11178[(8)]);var inst_11171 = f.call(null,inst_11164,inst_11167);var inst_11164__$1 = inst_11171;var state_11178__$1 = (function (){var statearr_11181 = state_11178;(statearr_11181[(7)] = inst_11164__$1);
return statearr_11181;
})();var statearr_11182_11197 = state_11178__$1;(statearr_11182_11197[(2)] = null);
(statearr_11182_11197[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11179 === (5)))
{var inst_11164 = (state_11178[(7)]);var state_11178__$1 = state_11178;var statearr_11183_11198 = state_11178__$1;(statearr_11183_11198[(2)] = inst_11164);
(statearr_11183_11198[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11179 === (4)))
{var inst_11167 = (state_11178[(8)]);var inst_11167__$1 = (state_11178[(2)]);var inst_11168 = (inst_11167__$1 == null);var state_11178__$1 = (function (){var statearr_11184 = state_11178;(statearr_11184[(8)] = inst_11167__$1);
return statearr_11184;
})();if(cljs.core.truth_(inst_11168))
{var statearr_11185_11199 = state_11178__$1;(statearr_11185_11199[(1)] = (5));
} else
{var statearr_11186_11200 = state_11178__$1;(statearr_11186_11200[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11179 === (3)))
{var inst_11176 = (state_11178[(2)]);var state_11178__$1 = state_11178;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_11178__$1,inst_11176);
} else
{if((state_val_11179 === (2)))
{var state_11178__$1 = state_11178;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_11178__$1,(4),ch);
} else
{if((state_val_11179 === (1)))
{var inst_11164 = init;var state_11178__$1 = (function (){var statearr_11187 = state_11178;(statearr_11187[(7)] = inst_11164);
return statearr_11187;
})();var statearr_11188_11201 = state_11178__$1;(statearr_11188_11201[(2)] = null);
(statearr_11188_11201[(1)] = (2));
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
var state_machine__6338__auto____0 = (function (){var statearr_11192 = [null,null,null,null,null,null,null,null,null];(statearr_11192[(0)] = state_machine__6338__auto__);
(statearr_11192[(1)] = (1));
return statearr_11192;
});
var state_machine__6338__auto____1 = (function (state_11178){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_11178);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e11193){if((e11193 instanceof Object))
{var ex__6341__auto__ = e11193;var statearr_11194_11202 = state_11178;(statearr_11194_11202[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_11178);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e11193;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__11203 = state_11178;
state_11178 = G__11203;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_11178){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_11178);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto__))
})();var state__6354__auto__ = (function (){var statearr_11195 = f__6353__auto__.call(null);(statearr_11195[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto__);
return statearr_11195;
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
return (function (state_11277){var state_val_11278 = (state_11277[(1)]);if((state_val_11278 === (7)))
{var inst_11259 = (state_11277[(2)]);var state_11277__$1 = state_11277;var statearr_11279_11302 = state_11277__$1;(statearr_11279_11302[(2)] = inst_11259);
(statearr_11279_11302[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11278 === (1)))
{var inst_11253 = cljs.core.seq.call(null,coll);var inst_11254 = inst_11253;var state_11277__$1 = (function (){var statearr_11280 = state_11277;(statearr_11280[(7)] = inst_11254);
return statearr_11280;
})();var statearr_11281_11303 = state_11277__$1;(statearr_11281_11303[(2)] = null);
(statearr_11281_11303[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11278 === (4)))
{var inst_11254 = (state_11277[(7)]);var inst_11257 = cljs.core.first.call(null,inst_11254);var state_11277__$1 = state_11277;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_11277__$1,(7),ch,inst_11257);
} else
{if((state_val_11278 === (13)))
{var inst_11271 = (state_11277[(2)]);var state_11277__$1 = state_11277;var statearr_11282_11304 = state_11277__$1;(statearr_11282_11304[(2)] = inst_11271);
(statearr_11282_11304[(1)] = (10));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11278 === (6)))
{var inst_11262 = (state_11277[(2)]);var state_11277__$1 = state_11277;if(cljs.core.truth_(inst_11262))
{var statearr_11283_11305 = state_11277__$1;(statearr_11283_11305[(1)] = (8));
} else
{var statearr_11284_11306 = state_11277__$1;(statearr_11284_11306[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11278 === (3)))
{var inst_11275 = (state_11277[(2)]);var state_11277__$1 = state_11277;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_11277__$1,inst_11275);
} else
{if((state_val_11278 === (12)))
{var state_11277__$1 = state_11277;var statearr_11285_11307 = state_11277__$1;(statearr_11285_11307[(2)] = null);
(statearr_11285_11307[(1)] = (13));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11278 === (2)))
{var inst_11254 = (state_11277[(7)]);var state_11277__$1 = state_11277;if(cljs.core.truth_(inst_11254))
{var statearr_11286_11308 = state_11277__$1;(statearr_11286_11308[(1)] = (4));
} else
{var statearr_11287_11309 = state_11277__$1;(statearr_11287_11309[(1)] = (5));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11278 === (11)))
{var inst_11268 = cljs.core.async.close_BANG_.call(null,ch);var state_11277__$1 = state_11277;var statearr_11288_11310 = state_11277__$1;(statearr_11288_11310[(2)] = inst_11268);
(statearr_11288_11310[(1)] = (13));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11278 === (9)))
{var state_11277__$1 = state_11277;if(cljs.core.truth_(close_QMARK_))
{var statearr_11289_11311 = state_11277__$1;(statearr_11289_11311[(1)] = (11));
} else
{var statearr_11290_11312 = state_11277__$1;(statearr_11290_11312[(1)] = (12));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11278 === (5)))
{var inst_11254 = (state_11277[(7)]);var state_11277__$1 = state_11277;var statearr_11291_11313 = state_11277__$1;(statearr_11291_11313[(2)] = inst_11254);
(statearr_11291_11313[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11278 === (10)))
{var inst_11273 = (state_11277[(2)]);var state_11277__$1 = state_11277;var statearr_11292_11314 = state_11277__$1;(statearr_11292_11314[(2)] = inst_11273);
(statearr_11292_11314[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11278 === (8)))
{var inst_11254 = (state_11277[(7)]);var inst_11264 = cljs.core.next.call(null,inst_11254);var inst_11254__$1 = inst_11264;var state_11277__$1 = (function (){var statearr_11293 = state_11277;(statearr_11293[(7)] = inst_11254__$1);
return statearr_11293;
})();var statearr_11294_11315 = state_11277__$1;(statearr_11294_11315[(2)] = null);
(statearr_11294_11315[(1)] = (2));
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
var state_machine__6338__auto____0 = (function (){var statearr_11298 = [null,null,null,null,null,null,null,null];(statearr_11298[(0)] = state_machine__6338__auto__);
(statearr_11298[(1)] = (1));
return statearr_11298;
});
var state_machine__6338__auto____1 = (function (state_11277){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_11277);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e11299){if((e11299 instanceof Object))
{var ex__6341__auto__ = e11299;var statearr_11300_11316 = state_11277;(statearr_11300_11316[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_11277);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e11299;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__11317 = state_11277;
state_11277 = G__11317;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_11277){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_11277);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto__))
})();var state__6354__auto__ = (function (){var statearr_11301 = f__6353__auto__.call(null);(statearr_11301[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto__);
return statearr_11301;
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
cljs.core.async.Mux = (function (){var obj11319 = {};return obj11319;
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
cljs.core.async.Mult = (function (){var obj11321 = {};return obj11321;
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
cljs.core.async.mult = (function mult(ch){var cs = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);var m = (function (){if(typeof cljs.core.async.t11543 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t11543 = (function (cs,ch,mult,meta11544){
this.cs = cs;
this.ch = ch;
this.mult = mult;
this.meta11544 = meta11544;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t11543.cljs$lang$type = true;
cljs.core.async.t11543.cljs$lang$ctorStr = "cljs.core.async/t11543";
cljs.core.async.t11543.cljs$lang$ctorPrWriter = ((function (cs){
return (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t11543");
});})(cs))
;
cljs.core.async.t11543.prototype.cljs$core$async$Mult$ = true;
cljs.core.async.t11543.prototype.cljs$core$async$Mult$tap_STAR_$arity$3 = ((function (cs){
return (function (_,ch__$2,close_QMARK_){var self__ = this;
var ___$1 = this;cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.assoc,ch__$2,close_QMARK_);
return null;
});})(cs))
;
cljs.core.async.t11543.prototype.cljs$core$async$Mult$untap_STAR_$arity$2 = ((function (cs){
return (function (_,ch__$2){var self__ = this;
var ___$1 = this;cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.dissoc,ch__$2);
return null;
});})(cs))
;
cljs.core.async.t11543.prototype.cljs$core$async$Mult$untap_all_STAR_$arity$1 = ((function (cs){
return (function (_){var self__ = this;
var ___$1 = this;cljs.core.reset_BANG_.call(null,self__.cs,cljs.core.PersistentArrayMap.EMPTY);
return null;
});})(cs))
;
cljs.core.async.t11543.prototype.cljs$core$async$Mux$ = true;
cljs.core.async.t11543.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (cs){
return (function (_){var self__ = this;
var ___$1 = this;return self__.ch;
});})(cs))
;
cljs.core.async.t11543.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (cs){
return (function (_11545){var self__ = this;
var _11545__$1 = this;return self__.meta11544;
});})(cs))
;
cljs.core.async.t11543.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (cs){
return (function (_11545,meta11544__$1){var self__ = this;
var _11545__$1 = this;return (new cljs.core.async.t11543(self__.cs,self__.ch,self__.mult,meta11544__$1));
});})(cs))
;
cljs.core.async.__GT_t11543 = ((function (cs){
return (function __GT_t11543(cs__$1,ch__$1,mult__$1,meta11544){return (new cljs.core.async.t11543(cs__$1,ch__$1,mult__$1,meta11544));
});})(cs))
;
}
return (new cljs.core.async.t11543(cs,ch,mult,null));
})();var dchan = cljs.core.async.chan.call(null,(1));var dctr = cljs.core.atom.call(null,null);var done = ((function (cs,m,dchan,dctr){
return (function (_){if((cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec) === (0)))
{return cljs.core.async.put_BANG_.call(null,dchan,true);
} else
{return null;
}
});})(cs,m,dchan,dctr))
;var c__6352__auto___11764 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___11764,cs,m,dchan,dctr,done){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___11764,cs,m,dchan,dctr,done){
return (function (state_11676){var state_val_11677 = (state_11676[(1)]);if((state_val_11677 === (7)))
{var inst_11672 = (state_11676[(2)]);var state_11676__$1 = state_11676;var statearr_11678_11765 = state_11676__$1;(statearr_11678_11765[(2)] = inst_11672);
(statearr_11678_11765[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (20)))
{var inst_11577 = (state_11676[(7)]);var inst_11587 = cljs.core.first.call(null,inst_11577);var inst_11588 = cljs.core.nth.call(null,inst_11587,(0),null);var inst_11589 = cljs.core.nth.call(null,inst_11587,(1),null);var state_11676__$1 = (function (){var statearr_11679 = state_11676;(statearr_11679[(8)] = inst_11588);
return statearr_11679;
})();if(cljs.core.truth_(inst_11589))
{var statearr_11680_11766 = state_11676__$1;(statearr_11680_11766[(1)] = (22));
} else
{var statearr_11681_11767 = state_11676__$1;(statearr_11681_11767[(1)] = (23));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (27)))
{var inst_11617 = (state_11676[(9)]);var inst_11619 = (state_11676[(10)]);var inst_11624 = (state_11676[(11)]);var inst_11548 = (state_11676[(12)]);var inst_11624__$1 = cljs.core._nth.call(null,inst_11617,inst_11619);var inst_11625 = cljs.core.async.put_BANG_.call(null,inst_11624__$1,inst_11548,done);var state_11676__$1 = (function (){var statearr_11682 = state_11676;(statearr_11682[(11)] = inst_11624__$1);
return statearr_11682;
})();if(cljs.core.truth_(inst_11625))
{var statearr_11683_11768 = state_11676__$1;(statearr_11683_11768[(1)] = (30));
} else
{var statearr_11684_11769 = state_11676__$1;(statearr_11684_11769[(1)] = (31));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (1)))
{var state_11676__$1 = state_11676;var statearr_11685_11770 = state_11676__$1;(statearr_11685_11770[(2)] = null);
(statearr_11685_11770[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (24)))
{var inst_11577 = (state_11676[(7)]);var inst_11594 = (state_11676[(2)]);var inst_11595 = cljs.core.next.call(null,inst_11577);var inst_11557 = inst_11595;var inst_11558 = null;var inst_11559 = (0);var inst_11560 = (0);var state_11676__$1 = (function (){var statearr_11686 = state_11676;(statearr_11686[(13)] = inst_11557);
(statearr_11686[(14)] = inst_11560);
(statearr_11686[(15)] = inst_11559);
(statearr_11686[(16)] = inst_11594);
(statearr_11686[(17)] = inst_11558);
return statearr_11686;
})();var statearr_11687_11771 = state_11676__$1;(statearr_11687_11771[(2)] = null);
(statearr_11687_11771[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (39)))
{var state_11676__$1 = state_11676;var statearr_11691_11772 = state_11676__$1;(statearr_11691_11772[(2)] = null);
(statearr_11691_11772[(1)] = (41));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (4)))
{var inst_11548 = (state_11676[(12)]);var inst_11548__$1 = (state_11676[(2)]);var inst_11549 = (inst_11548__$1 == null);var state_11676__$1 = (function (){var statearr_11692 = state_11676;(statearr_11692[(12)] = inst_11548__$1);
return statearr_11692;
})();if(cljs.core.truth_(inst_11549))
{var statearr_11693_11773 = state_11676__$1;(statearr_11693_11773[(1)] = (5));
} else
{var statearr_11694_11774 = state_11676__$1;(statearr_11694_11774[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (15)))
{var inst_11557 = (state_11676[(13)]);var inst_11560 = (state_11676[(14)]);var inst_11559 = (state_11676[(15)]);var inst_11558 = (state_11676[(17)]);var inst_11573 = (state_11676[(2)]);var inst_11574 = (inst_11560 + (1));var tmp11688 = inst_11557;var tmp11689 = inst_11559;var tmp11690 = inst_11558;var inst_11557__$1 = tmp11688;var inst_11558__$1 = tmp11690;var inst_11559__$1 = tmp11689;var inst_11560__$1 = inst_11574;var state_11676__$1 = (function (){var statearr_11695 = state_11676;(statearr_11695[(13)] = inst_11557__$1);
(statearr_11695[(14)] = inst_11560__$1);
(statearr_11695[(15)] = inst_11559__$1);
(statearr_11695[(17)] = inst_11558__$1);
(statearr_11695[(18)] = inst_11573);
return statearr_11695;
})();var statearr_11696_11775 = state_11676__$1;(statearr_11696_11775[(2)] = null);
(statearr_11696_11775[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (21)))
{var inst_11598 = (state_11676[(2)]);var state_11676__$1 = state_11676;var statearr_11700_11776 = state_11676__$1;(statearr_11700_11776[(2)] = inst_11598);
(statearr_11700_11776[(1)] = (18));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (31)))
{var inst_11624 = (state_11676[(11)]);var inst_11628 = cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec);var inst_11629 = cljs.core.async.untap_STAR_.call(null,m,inst_11624);var state_11676__$1 = (function (){var statearr_11701 = state_11676;(statearr_11701[(19)] = inst_11628);
return statearr_11701;
})();var statearr_11702_11777 = state_11676__$1;(statearr_11702_11777[(2)] = inst_11629);
(statearr_11702_11777[(1)] = (32));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (32)))
{var inst_11617 = (state_11676[(9)]);var inst_11619 = (state_11676[(10)]);var inst_11616 = (state_11676[(20)]);var inst_11618 = (state_11676[(21)]);var inst_11631 = (state_11676[(2)]);var inst_11632 = (inst_11619 + (1));var tmp11697 = inst_11617;var tmp11698 = inst_11616;var tmp11699 = inst_11618;var inst_11616__$1 = tmp11698;var inst_11617__$1 = tmp11697;var inst_11618__$1 = tmp11699;var inst_11619__$1 = inst_11632;var state_11676__$1 = (function (){var statearr_11703 = state_11676;(statearr_11703[(9)] = inst_11617__$1);
(statearr_11703[(10)] = inst_11619__$1);
(statearr_11703[(20)] = inst_11616__$1);
(statearr_11703[(22)] = inst_11631);
(statearr_11703[(21)] = inst_11618__$1);
return statearr_11703;
})();var statearr_11704_11778 = state_11676__$1;(statearr_11704_11778[(2)] = null);
(statearr_11704_11778[(1)] = (25));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (40)))
{var inst_11644 = (state_11676[(23)]);var inst_11648 = cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec);var inst_11649 = cljs.core.async.untap_STAR_.call(null,m,inst_11644);var state_11676__$1 = (function (){var statearr_11705 = state_11676;(statearr_11705[(24)] = inst_11648);
return statearr_11705;
})();var statearr_11706_11779 = state_11676__$1;(statearr_11706_11779[(2)] = inst_11649);
(statearr_11706_11779[(1)] = (41));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (33)))
{var inst_11635 = (state_11676[(25)]);var inst_11637 = cljs.core.chunked_seq_QMARK_.call(null,inst_11635);var state_11676__$1 = state_11676;if(inst_11637)
{var statearr_11707_11780 = state_11676__$1;(statearr_11707_11780[(1)] = (36));
} else
{var statearr_11708_11781 = state_11676__$1;(statearr_11708_11781[(1)] = (37));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (13)))
{var inst_11567 = (state_11676[(26)]);var inst_11570 = cljs.core.async.close_BANG_.call(null,inst_11567);var state_11676__$1 = state_11676;var statearr_11709_11782 = state_11676__$1;(statearr_11709_11782[(2)] = inst_11570);
(statearr_11709_11782[(1)] = (15));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (22)))
{var inst_11588 = (state_11676[(8)]);var inst_11591 = cljs.core.async.close_BANG_.call(null,inst_11588);var state_11676__$1 = state_11676;var statearr_11710_11783 = state_11676__$1;(statearr_11710_11783[(2)] = inst_11591);
(statearr_11710_11783[(1)] = (24));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (36)))
{var inst_11635 = (state_11676[(25)]);var inst_11639 = cljs.core.chunk_first.call(null,inst_11635);var inst_11640 = cljs.core.chunk_rest.call(null,inst_11635);var inst_11641 = cljs.core.count.call(null,inst_11639);var inst_11616 = inst_11640;var inst_11617 = inst_11639;var inst_11618 = inst_11641;var inst_11619 = (0);var state_11676__$1 = (function (){var statearr_11711 = state_11676;(statearr_11711[(9)] = inst_11617);
(statearr_11711[(10)] = inst_11619);
(statearr_11711[(20)] = inst_11616);
(statearr_11711[(21)] = inst_11618);
return statearr_11711;
})();var statearr_11712_11784 = state_11676__$1;(statearr_11712_11784[(2)] = null);
(statearr_11712_11784[(1)] = (25));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (41)))
{var inst_11635 = (state_11676[(25)]);var inst_11651 = (state_11676[(2)]);var inst_11652 = cljs.core.next.call(null,inst_11635);var inst_11616 = inst_11652;var inst_11617 = null;var inst_11618 = (0);var inst_11619 = (0);var state_11676__$1 = (function (){var statearr_11713 = state_11676;(statearr_11713[(9)] = inst_11617);
(statearr_11713[(10)] = inst_11619);
(statearr_11713[(20)] = inst_11616);
(statearr_11713[(21)] = inst_11618);
(statearr_11713[(27)] = inst_11651);
return statearr_11713;
})();var statearr_11714_11785 = state_11676__$1;(statearr_11714_11785[(2)] = null);
(statearr_11714_11785[(1)] = (25));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (43)))
{var state_11676__$1 = state_11676;var statearr_11715_11786 = state_11676__$1;(statearr_11715_11786[(2)] = null);
(statearr_11715_11786[(1)] = (44));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (29)))
{var inst_11660 = (state_11676[(2)]);var state_11676__$1 = state_11676;var statearr_11716_11787 = state_11676__$1;(statearr_11716_11787[(2)] = inst_11660);
(statearr_11716_11787[(1)] = (26));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (44)))
{var inst_11669 = (state_11676[(2)]);var state_11676__$1 = (function (){var statearr_11717 = state_11676;(statearr_11717[(28)] = inst_11669);
return statearr_11717;
})();var statearr_11718_11788 = state_11676__$1;(statearr_11718_11788[(2)] = null);
(statearr_11718_11788[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (6)))
{var inst_11608 = (state_11676[(29)]);var inst_11607 = cljs.core.deref.call(null,cs);var inst_11608__$1 = cljs.core.keys.call(null,inst_11607);var inst_11609 = cljs.core.count.call(null,inst_11608__$1);var inst_11610 = cljs.core.reset_BANG_.call(null,dctr,inst_11609);var inst_11615 = cljs.core.seq.call(null,inst_11608__$1);var inst_11616 = inst_11615;var inst_11617 = null;var inst_11618 = (0);var inst_11619 = (0);var state_11676__$1 = (function (){var statearr_11719 = state_11676;(statearr_11719[(9)] = inst_11617);
(statearr_11719[(10)] = inst_11619);
(statearr_11719[(20)] = inst_11616);
(statearr_11719[(30)] = inst_11610);
(statearr_11719[(21)] = inst_11618);
(statearr_11719[(29)] = inst_11608__$1);
return statearr_11719;
})();var statearr_11720_11789 = state_11676__$1;(statearr_11720_11789[(2)] = null);
(statearr_11720_11789[(1)] = (25));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (28)))
{var inst_11616 = (state_11676[(20)]);var inst_11635 = (state_11676[(25)]);var inst_11635__$1 = cljs.core.seq.call(null,inst_11616);var state_11676__$1 = (function (){var statearr_11721 = state_11676;(statearr_11721[(25)] = inst_11635__$1);
return statearr_11721;
})();if(inst_11635__$1)
{var statearr_11722_11790 = state_11676__$1;(statearr_11722_11790[(1)] = (33));
} else
{var statearr_11723_11791 = state_11676__$1;(statearr_11723_11791[(1)] = (34));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (25)))
{var inst_11619 = (state_11676[(10)]);var inst_11618 = (state_11676[(21)]);var inst_11621 = (inst_11619 < inst_11618);var inst_11622 = inst_11621;var state_11676__$1 = state_11676;if(cljs.core.truth_(inst_11622))
{var statearr_11724_11792 = state_11676__$1;(statearr_11724_11792[(1)] = (27));
} else
{var statearr_11725_11793 = state_11676__$1;(statearr_11725_11793[(1)] = (28));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (34)))
{var state_11676__$1 = state_11676;var statearr_11726_11794 = state_11676__$1;(statearr_11726_11794[(2)] = null);
(statearr_11726_11794[(1)] = (35));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (17)))
{var state_11676__$1 = state_11676;var statearr_11727_11795 = state_11676__$1;(statearr_11727_11795[(2)] = null);
(statearr_11727_11795[(1)] = (18));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (3)))
{var inst_11674 = (state_11676[(2)]);var state_11676__$1 = state_11676;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_11676__$1,inst_11674);
} else
{if((state_val_11677 === (12)))
{var inst_11603 = (state_11676[(2)]);var state_11676__$1 = state_11676;var statearr_11728_11796 = state_11676__$1;(statearr_11728_11796[(2)] = inst_11603);
(statearr_11728_11796[(1)] = (9));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (2)))
{var state_11676__$1 = state_11676;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_11676__$1,(4),ch);
} else
{if((state_val_11677 === (23)))
{var state_11676__$1 = state_11676;var statearr_11729_11797 = state_11676__$1;(statearr_11729_11797[(2)] = null);
(statearr_11729_11797[(1)] = (24));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (35)))
{var inst_11658 = (state_11676[(2)]);var state_11676__$1 = state_11676;var statearr_11730_11798 = state_11676__$1;(statearr_11730_11798[(2)] = inst_11658);
(statearr_11730_11798[(1)] = (29));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (19)))
{var inst_11577 = (state_11676[(7)]);var inst_11581 = cljs.core.chunk_first.call(null,inst_11577);var inst_11582 = cljs.core.chunk_rest.call(null,inst_11577);var inst_11583 = cljs.core.count.call(null,inst_11581);var inst_11557 = inst_11582;var inst_11558 = inst_11581;var inst_11559 = inst_11583;var inst_11560 = (0);var state_11676__$1 = (function (){var statearr_11731 = state_11676;(statearr_11731[(13)] = inst_11557);
(statearr_11731[(14)] = inst_11560);
(statearr_11731[(15)] = inst_11559);
(statearr_11731[(17)] = inst_11558);
return statearr_11731;
})();var statearr_11732_11799 = state_11676__$1;(statearr_11732_11799[(2)] = null);
(statearr_11732_11799[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (11)))
{var inst_11557 = (state_11676[(13)]);var inst_11577 = (state_11676[(7)]);var inst_11577__$1 = cljs.core.seq.call(null,inst_11557);var state_11676__$1 = (function (){var statearr_11733 = state_11676;(statearr_11733[(7)] = inst_11577__$1);
return statearr_11733;
})();if(inst_11577__$1)
{var statearr_11734_11800 = state_11676__$1;(statearr_11734_11800[(1)] = (16));
} else
{var statearr_11735_11801 = state_11676__$1;(statearr_11735_11801[(1)] = (17));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (9)))
{var inst_11605 = (state_11676[(2)]);var state_11676__$1 = state_11676;var statearr_11736_11802 = state_11676__$1;(statearr_11736_11802[(2)] = inst_11605);
(statearr_11736_11802[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (5)))
{var inst_11555 = cljs.core.deref.call(null,cs);var inst_11556 = cljs.core.seq.call(null,inst_11555);var inst_11557 = inst_11556;var inst_11558 = null;var inst_11559 = (0);var inst_11560 = (0);var state_11676__$1 = (function (){var statearr_11737 = state_11676;(statearr_11737[(13)] = inst_11557);
(statearr_11737[(14)] = inst_11560);
(statearr_11737[(15)] = inst_11559);
(statearr_11737[(17)] = inst_11558);
return statearr_11737;
})();var statearr_11738_11803 = state_11676__$1;(statearr_11738_11803[(2)] = null);
(statearr_11738_11803[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (14)))
{var state_11676__$1 = state_11676;var statearr_11739_11804 = state_11676__$1;(statearr_11739_11804[(2)] = null);
(statearr_11739_11804[(1)] = (15));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (45)))
{var inst_11666 = (state_11676[(2)]);var state_11676__$1 = state_11676;var statearr_11740_11805 = state_11676__$1;(statearr_11740_11805[(2)] = inst_11666);
(statearr_11740_11805[(1)] = (44));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (26)))
{var inst_11608 = (state_11676[(29)]);var inst_11662 = (state_11676[(2)]);var inst_11663 = cljs.core.seq.call(null,inst_11608);var state_11676__$1 = (function (){var statearr_11741 = state_11676;(statearr_11741[(31)] = inst_11662);
return statearr_11741;
})();if(inst_11663)
{var statearr_11742_11806 = state_11676__$1;(statearr_11742_11806[(1)] = (42));
} else
{var statearr_11743_11807 = state_11676__$1;(statearr_11743_11807[(1)] = (43));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (16)))
{var inst_11577 = (state_11676[(7)]);var inst_11579 = cljs.core.chunked_seq_QMARK_.call(null,inst_11577);var state_11676__$1 = state_11676;if(inst_11579)
{var statearr_11744_11808 = state_11676__$1;(statearr_11744_11808[(1)] = (19));
} else
{var statearr_11745_11809 = state_11676__$1;(statearr_11745_11809[(1)] = (20));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (38)))
{var inst_11655 = (state_11676[(2)]);var state_11676__$1 = state_11676;var statearr_11746_11810 = state_11676__$1;(statearr_11746_11810[(2)] = inst_11655);
(statearr_11746_11810[(1)] = (35));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (30)))
{var state_11676__$1 = state_11676;var statearr_11747_11811 = state_11676__$1;(statearr_11747_11811[(2)] = null);
(statearr_11747_11811[(1)] = (32));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (10)))
{var inst_11560 = (state_11676[(14)]);var inst_11558 = (state_11676[(17)]);var inst_11566 = cljs.core._nth.call(null,inst_11558,inst_11560);var inst_11567 = cljs.core.nth.call(null,inst_11566,(0),null);var inst_11568 = cljs.core.nth.call(null,inst_11566,(1),null);var state_11676__$1 = (function (){var statearr_11748 = state_11676;(statearr_11748[(26)] = inst_11567);
return statearr_11748;
})();if(cljs.core.truth_(inst_11568))
{var statearr_11749_11812 = state_11676__$1;(statearr_11749_11812[(1)] = (13));
} else
{var statearr_11750_11813 = state_11676__$1;(statearr_11750_11813[(1)] = (14));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (18)))
{var inst_11601 = (state_11676[(2)]);var state_11676__$1 = state_11676;var statearr_11751_11814 = state_11676__$1;(statearr_11751_11814[(2)] = inst_11601);
(statearr_11751_11814[(1)] = (12));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (42)))
{var state_11676__$1 = state_11676;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_11676__$1,(45),dchan);
} else
{if((state_val_11677 === (37)))
{var inst_11548 = (state_11676[(12)]);var inst_11644 = (state_11676[(23)]);var inst_11635 = (state_11676[(25)]);var inst_11644__$1 = cljs.core.first.call(null,inst_11635);var inst_11645 = cljs.core.async.put_BANG_.call(null,inst_11644__$1,inst_11548,done);var state_11676__$1 = (function (){var statearr_11752 = state_11676;(statearr_11752[(23)] = inst_11644__$1);
return statearr_11752;
})();if(cljs.core.truth_(inst_11645))
{var statearr_11753_11815 = state_11676__$1;(statearr_11753_11815[(1)] = (39));
} else
{var statearr_11754_11816 = state_11676__$1;(statearr_11754_11816[(1)] = (40));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_11677 === (8)))
{var inst_11560 = (state_11676[(14)]);var inst_11559 = (state_11676[(15)]);var inst_11562 = (inst_11560 < inst_11559);var inst_11563 = inst_11562;var state_11676__$1 = state_11676;if(cljs.core.truth_(inst_11563))
{var statearr_11755_11817 = state_11676__$1;(statearr_11755_11817[(1)] = (10));
} else
{var statearr_11756_11818 = state_11676__$1;(statearr_11756_11818[(1)] = (11));
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
});})(c__6352__auto___11764,cs,m,dchan,dctr,done))
;return ((function (switch__6337__auto__,c__6352__auto___11764,cs,m,dchan,dctr,done){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_11760 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_11760[(0)] = state_machine__6338__auto__);
(statearr_11760[(1)] = (1));
return statearr_11760;
});
var state_machine__6338__auto____1 = (function (state_11676){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_11676);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e11761){if((e11761 instanceof Object))
{var ex__6341__auto__ = e11761;var statearr_11762_11819 = state_11676;(statearr_11762_11819[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_11676);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e11761;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__11820 = state_11676;
state_11676 = G__11820;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_11676){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_11676);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___11764,cs,m,dchan,dctr,done))
})();var state__6354__auto__ = (function (){var statearr_11763 = f__6353__auto__.call(null);(statearr_11763[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___11764);
return statearr_11763;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___11764,cs,m,dchan,dctr,done))
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
cljs.core.async.Mix = (function (){var obj11822 = {};return obj11822;
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
;var m = (function (){if(typeof cljs.core.async.t11942 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t11942 = (function (change,mix,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,meta11943){
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
this.meta11943 = meta11943;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t11942.cljs$lang$type = true;
cljs.core.async.t11942.cljs$lang$ctorStr = "cljs.core.async/t11942";
cljs.core.async.t11942.cljs$lang$ctorPrWriter = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t11942");
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11942.prototype.cljs$core$async$Mix$ = true;
cljs.core.async.t11942.prototype.cljs$core$async$Mix$admix_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,ch){var self__ = this;
var ___$1 = this;cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.assoc,ch,cljs.core.PersistentArrayMap.EMPTY);
return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11942.prototype.cljs$core$async$Mix$unmix_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,ch){var self__ = this;
var ___$1 = this;cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.dissoc,ch);
return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11942.prototype.cljs$core$async$Mix$unmix_all_STAR_$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_){var self__ = this;
var ___$1 = this;cljs.core.reset_BANG_.call(null,self__.cs,cljs.core.PersistentArrayMap.EMPTY);
return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11942.prototype.cljs$core$async$Mix$toggle_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,state_map){var self__ = this;
var ___$1 = this;cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.partial.call(null,cljs.core.merge_with,cljs.core.merge),state_map);
return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11942.prototype.cljs$core$async$Mix$solo_mode_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,mode){var self__ = this;
var ___$1 = this;if(cljs.core.truth_(self__.solo_modes.call(null,mode)))
{} else
{throw (new Error(("Assert failed: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(("mode must be one of: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(self__.solo_modes)))+"\n"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,cljs.core.list(new cljs.core.Symbol(null,"solo-modes","solo-modes",882180540,null),new cljs.core.Symbol(null,"mode","mode",-2000032078,null)))))));
}
cljs.core.reset_BANG_.call(null,self__.solo_mode,mode);
return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11942.prototype.cljs$core$async$Mux$ = true;
cljs.core.async.t11942.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_){var self__ = this;
var ___$1 = this;return self__.out;
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11942.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_11944){var self__ = this;
var _11944__$1 = this;return self__.meta11943;
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.t11942.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_11944,meta11943__$1){var self__ = this;
var _11944__$1 = this;return (new cljs.core.async.t11942(self__.change,self__.mix,self__.solo_mode,self__.pick,self__.cs,self__.calc_state,self__.out,self__.changed,self__.solo_modes,self__.attrs,meta11943__$1));
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
cljs.core.async.__GT_t11942 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function __GT_t11942(change__$1,mix__$1,solo_mode__$1,pick__$1,cs__$1,calc_state__$1,out__$1,changed__$1,solo_modes__$1,attrs__$1,meta11943){return (new cljs.core.async.t11942(change__$1,mix__$1,solo_mode__$1,pick__$1,cs__$1,calc_state__$1,out__$1,changed__$1,solo_modes__$1,attrs__$1,meta11943));
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;
}
return (new cljs.core.async.t11942(change,mix,solo_mode,pick,cs,calc_state,out,changed,solo_modes,attrs,null));
})();var c__6352__auto___12061 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___12061,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___12061,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function (state_12014){var state_val_12015 = (state_12014[(1)]);if((state_val_12015 === (7)))
{var inst_11958 = (state_12014[(7)]);var inst_11963 = cljs.core.apply.call(null,cljs.core.hash_map,inst_11958);var state_12014__$1 = state_12014;var statearr_12016_12062 = state_12014__$1;(statearr_12016_12062[(2)] = inst_11963);
(statearr_12016_12062[(1)] = (9));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (20)))
{var inst_11973 = (state_12014[(8)]);var state_12014__$1 = state_12014;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12014__$1,(23),out,inst_11973);
} else
{if((state_val_12015 === (1)))
{var inst_11948 = (state_12014[(9)]);var inst_11948__$1 = calc_state.call(null);var inst_11949 = cljs.core.seq_QMARK_.call(null,inst_11948__$1);var state_12014__$1 = (function (){var statearr_12017 = state_12014;(statearr_12017[(9)] = inst_11948__$1);
return statearr_12017;
})();if(inst_11949)
{var statearr_12018_12063 = state_12014__$1;(statearr_12018_12063[(1)] = (2));
} else
{var statearr_12019_12064 = state_12014__$1;(statearr_12019_12064[(1)] = (3));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (24)))
{var inst_11966 = (state_12014[(10)]);var inst_11958 = inst_11966;var state_12014__$1 = (function (){var statearr_12020 = state_12014;(statearr_12020[(7)] = inst_11958);
return statearr_12020;
})();var statearr_12021_12065 = state_12014__$1;(statearr_12021_12065[(2)] = null);
(statearr_12021_12065[(1)] = (5));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (4)))
{var inst_11948 = (state_12014[(9)]);var inst_11954 = (state_12014[(2)]);var inst_11955 = cljs.core.get.call(null,inst_11954,new cljs.core.Keyword(null,"reads","reads",-1215067361));var inst_11956 = cljs.core.get.call(null,inst_11954,new cljs.core.Keyword(null,"mutes","mutes",1068806309));var inst_11957 = cljs.core.get.call(null,inst_11954,new cljs.core.Keyword(null,"solos","solos",1441458643));var inst_11958 = inst_11948;var state_12014__$1 = (function (){var statearr_12022 = state_12014;(statearr_12022[(11)] = inst_11957);
(statearr_12022[(12)] = inst_11955);
(statearr_12022[(13)] = inst_11956);
(statearr_12022[(7)] = inst_11958);
return statearr_12022;
})();var statearr_12023_12066 = state_12014__$1;(statearr_12023_12066[(2)] = null);
(statearr_12023_12066[(1)] = (5));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (15)))
{var state_12014__$1 = state_12014;var statearr_12024_12067 = state_12014__$1;(statearr_12024_12067[(2)] = null);
(statearr_12024_12067[(1)] = (16));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (21)))
{var inst_11966 = (state_12014[(10)]);var inst_11958 = inst_11966;var state_12014__$1 = (function (){var statearr_12025 = state_12014;(statearr_12025[(7)] = inst_11958);
return statearr_12025;
})();var statearr_12026_12068 = state_12014__$1;(statearr_12026_12068[(2)] = null);
(statearr_12026_12068[(1)] = (5));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (13)))
{var inst_12010 = (state_12014[(2)]);var state_12014__$1 = state_12014;var statearr_12027_12069 = state_12014__$1;(statearr_12027_12069[(2)] = inst_12010);
(statearr_12027_12069[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (22)))
{var inst_12008 = (state_12014[(2)]);var state_12014__$1 = state_12014;var statearr_12028_12070 = state_12014__$1;(statearr_12028_12070[(2)] = inst_12008);
(statearr_12028_12070[(1)] = (13));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (6)))
{var inst_12012 = (state_12014[(2)]);var state_12014__$1 = state_12014;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12014__$1,inst_12012);
} else
{if((state_val_12015 === (25)))
{var state_12014__$1 = state_12014;var statearr_12029_12071 = state_12014__$1;(statearr_12029_12071[(2)] = null);
(statearr_12029_12071[(1)] = (26));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (17)))
{var inst_11988 = (state_12014[(14)]);var state_12014__$1 = state_12014;var statearr_12030_12072 = state_12014__$1;(statearr_12030_12072[(2)] = inst_11988);
(statearr_12030_12072[(1)] = (19));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (3)))
{var inst_11948 = (state_12014[(9)]);var state_12014__$1 = state_12014;var statearr_12031_12073 = state_12014__$1;(statearr_12031_12073[(2)] = inst_11948);
(statearr_12031_12073[(1)] = (4));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (12)))
{var inst_11969 = (state_12014[(15)]);var inst_11974 = (state_12014[(16)]);var inst_11988 = (state_12014[(14)]);var inst_11988__$1 = inst_11969.call(null,inst_11974);var state_12014__$1 = (function (){var statearr_12032 = state_12014;(statearr_12032[(14)] = inst_11988__$1);
return statearr_12032;
})();if(cljs.core.truth_(inst_11988__$1))
{var statearr_12033_12074 = state_12014__$1;(statearr_12033_12074[(1)] = (17));
} else
{var statearr_12034_12075 = state_12014__$1;(statearr_12034_12075[(1)] = (18));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (2)))
{var inst_11948 = (state_12014[(9)]);var inst_11951 = cljs.core.apply.call(null,cljs.core.hash_map,inst_11948);var state_12014__$1 = state_12014;var statearr_12035_12076 = state_12014__$1;(statearr_12035_12076[(2)] = inst_11951);
(statearr_12035_12076[(1)] = (4));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (23)))
{var inst_11999 = (state_12014[(2)]);var state_12014__$1 = state_12014;if(cljs.core.truth_(inst_11999))
{var statearr_12036_12077 = state_12014__$1;(statearr_12036_12077[(1)] = (24));
} else
{var statearr_12037_12078 = state_12014__$1;(statearr_12037_12078[(1)] = (25));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (19)))
{var inst_11996 = (state_12014[(2)]);var state_12014__$1 = state_12014;if(cljs.core.truth_(inst_11996))
{var statearr_12038_12079 = state_12014__$1;(statearr_12038_12079[(1)] = (20));
} else
{var statearr_12039_12080 = state_12014__$1;(statearr_12039_12080[(1)] = (21));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (11)))
{var inst_11973 = (state_12014[(8)]);var inst_11979 = (inst_11973 == null);var state_12014__$1 = state_12014;if(cljs.core.truth_(inst_11979))
{var statearr_12040_12081 = state_12014__$1;(statearr_12040_12081[(1)] = (14));
} else
{var statearr_12041_12082 = state_12014__$1;(statearr_12041_12082[(1)] = (15));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (9)))
{var inst_11966 = (state_12014[(10)]);var inst_11966__$1 = (state_12014[(2)]);var inst_11967 = cljs.core.get.call(null,inst_11966__$1,new cljs.core.Keyword(null,"reads","reads",-1215067361));var inst_11968 = cljs.core.get.call(null,inst_11966__$1,new cljs.core.Keyword(null,"mutes","mutes",1068806309));var inst_11969 = cljs.core.get.call(null,inst_11966__$1,new cljs.core.Keyword(null,"solos","solos",1441458643));var state_12014__$1 = (function (){var statearr_12042 = state_12014;(statearr_12042[(17)] = inst_11968);
(statearr_12042[(15)] = inst_11969);
(statearr_12042[(10)] = inst_11966__$1);
return statearr_12042;
})();return cljs.core.async.impl.ioc_helpers.ioc_alts_BANG_.call(null,state_12014__$1,(10),inst_11967);
} else
{if((state_val_12015 === (5)))
{var inst_11958 = (state_12014[(7)]);var inst_11961 = cljs.core.seq_QMARK_.call(null,inst_11958);var state_12014__$1 = state_12014;if(inst_11961)
{var statearr_12043_12083 = state_12014__$1;(statearr_12043_12083[(1)] = (7));
} else
{var statearr_12044_12084 = state_12014__$1;(statearr_12044_12084[(1)] = (8));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (14)))
{var inst_11974 = (state_12014[(16)]);var inst_11981 = cljs.core.swap_BANG_.call(null,cs,cljs.core.dissoc,inst_11974);var state_12014__$1 = state_12014;var statearr_12045_12085 = state_12014__$1;(statearr_12045_12085[(2)] = inst_11981);
(statearr_12045_12085[(1)] = (16));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (26)))
{var inst_12004 = (state_12014[(2)]);var state_12014__$1 = state_12014;var statearr_12046_12086 = state_12014__$1;(statearr_12046_12086[(2)] = inst_12004);
(statearr_12046_12086[(1)] = (22));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (16)))
{var inst_11984 = (state_12014[(2)]);var inst_11985 = calc_state.call(null);var inst_11958 = inst_11985;var state_12014__$1 = (function (){var statearr_12047 = state_12014;(statearr_12047[(18)] = inst_11984);
(statearr_12047[(7)] = inst_11958);
return statearr_12047;
})();var statearr_12048_12087 = state_12014__$1;(statearr_12048_12087[(2)] = null);
(statearr_12048_12087[(1)] = (5));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (10)))
{var inst_11974 = (state_12014[(16)]);var inst_11973 = (state_12014[(8)]);var inst_11972 = (state_12014[(2)]);var inst_11973__$1 = cljs.core.nth.call(null,inst_11972,(0),null);var inst_11974__$1 = cljs.core.nth.call(null,inst_11972,(1),null);var inst_11975 = (inst_11973__$1 == null);var inst_11976 = cljs.core._EQ_.call(null,inst_11974__$1,change);var inst_11977 = (inst_11975) || (inst_11976);var state_12014__$1 = (function (){var statearr_12049 = state_12014;(statearr_12049[(16)] = inst_11974__$1);
(statearr_12049[(8)] = inst_11973__$1);
return statearr_12049;
})();if(cljs.core.truth_(inst_11977))
{var statearr_12050_12088 = state_12014__$1;(statearr_12050_12088[(1)] = (11));
} else
{var statearr_12051_12089 = state_12014__$1;(statearr_12051_12089[(1)] = (12));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (18)))
{var inst_11968 = (state_12014[(17)]);var inst_11969 = (state_12014[(15)]);var inst_11974 = (state_12014[(16)]);var inst_11991 = cljs.core.empty_QMARK_.call(null,inst_11969);var inst_11992 = inst_11968.call(null,inst_11974);var inst_11993 = cljs.core.not.call(null,inst_11992);var inst_11994 = (inst_11991) && (inst_11993);var state_12014__$1 = state_12014;var statearr_12052_12090 = state_12014__$1;(statearr_12052_12090[(2)] = inst_11994);
(statearr_12052_12090[(1)] = (19));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12015 === (8)))
{var inst_11958 = (state_12014[(7)]);var state_12014__$1 = state_12014;var statearr_12053_12091 = state_12014__$1;(statearr_12053_12091[(2)] = inst_11958);
(statearr_12053_12091[(1)] = (9));
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
});})(c__6352__auto___12061,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
;return ((function (switch__6337__auto__,c__6352__auto___12061,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_12057 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_12057[(0)] = state_machine__6338__auto__);
(statearr_12057[(1)] = (1));
return statearr_12057;
});
var state_machine__6338__auto____1 = (function (state_12014){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12014);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e12058){if((e12058 instanceof Object))
{var ex__6341__auto__ = e12058;var statearr_12059_12092 = state_12014;(statearr_12059_12092[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12014);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e12058;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__12093 = state_12014;
state_12014 = G__12093;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12014){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12014);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___12061,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
})();var state__6354__auto__ = (function (){var statearr_12060 = f__6353__auto__.call(null);(statearr_12060[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___12061);
return statearr_12060;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___12061,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
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
cljs.core.async.Pub = (function (){var obj12095 = {};return obj12095;
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
return (function (p1__12096_SHARP_){if(cljs.core.truth_(p1__12096_SHARP_.call(null,topic)))
{return p1__12096_SHARP_;
} else
{return cljs.core.assoc.call(null,p1__12096_SHARP_,topic,cljs.core.async.mult.call(null,cljs.core.async.chan.call(null,buf_fn.call(null,topic))));
}
});})(or__3542__auto__,mults))
),topic);
}
});})(mults))
;var p = (function (){if(typeof cljs.core.async.t12219 !== 'undefined')
{} else
{
/**
* @constructor
*/
cljs.core.async.t12219 = (function (ensure_mult,mults,buf_fn,topic_fn,ch,pub,meta12220){
this.ensure_mult = ensure_mult;
this.mults = mults;
this.buf_fn = buf_fn;
this.topic_fn = topic_fn;
this.ch = ch;
this.pub = pub;
this.meta12220 = meta12220;
this.cljs$lang$protocol_mask$partition1$ = 0;
this.cljs$lang$protocol_mask$partition0$ = 393216;
})
cljs.core.async.t12219.cljs$lang$type = true;
cljs.core.async.t12219.cljs$lang$ctorStr = "cljs.core.async/t12219";
cljs.core.async.t12219.cljs$lang$ctorPrWriter = ((function (mults,ensure_mult){
return (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"cljs.core.async/t12219");
});})(mults,ensure_mult))
;
cljs.core.async.t12219.prototype.cljs$core$async$Pub$ = true;
cljs.core.async.t12219.prototype.cljs$core$async$Pub$sub_STAR_$arity$4 = ((function (mults,ensure_mult){
return (function (p,topic,ch__$2,close_QMARK_){var self__ = this;
var p__$1 = this;var m = self__.ensure_mult.call(null,topic);return cljs.core.async.tap.call(null,m,ch__$2,close_QMARK_);
});})(mults,ensure_mult))
;
cljs.core.async.t12219.prototype.cljs$core$async$Pub$unsub_STAR_$arity$3 = ((function (mults,ensure_mult){
return (function (p,topic,ch__$2){var self__ = this;
var p__$1 = this;var temp__4126__auto__ = cljs.core.get.call(null,cljs.core.deref.call(null,self__.mults),topic);if(cljs.core.truth_(temp__4126__auto__))
{var m = temp__4126__auto__;return cljs.core.async.untap.call(null,m,ch__$2);
} else
{return null;
}
});})(mults,ensure_mult))
;
cljs.core.async.t12219.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$1 = ((function (mults,ensure_mult){
return (function (_){var self__ = this;
var ___$1 = this;return cljs.core.reset_BANG_.call(null,self__.mults,cljs.core.PersistentArrayMap.EMPTY);
});})(mults,ensure_mult))
;
cljs.core.async.t12219.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$2 = ((function (mults,ensure_mult){
return (function (_,topic){var self__ = this;
var ___$1 = this;return cljs.core.swap_BANG_.call(null,self__.mults,cljs.core.dissoc,topic);
});})(mults,ensure_mult))
;
cljs.core.async.t12219.prototype.cljs$core$async$Mux$ = true;
cljs.core.async.t12219.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (mults,ensure_mult){
return (function (_){var self__ = this;
var ___$1 = this;return self__.ch;
});})(mults,ensure_mult))
;
cljs.core.async.t12219.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (mults,ensure_mult){
return (function (_12221){var self__ = this;
var _12221__$1 = this;return self__.meta12220;
});})(mults,ensure_mult))
;
cljs.core.async.t12219.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (mults,ensure_mult){
return (function (_12221,meta12220__$1){var self__ = this;
var _12221__$1 = this;return (new cljs.core.async.t12219(self__.ensure_mult,self__.mults,self__.buf_fn,self__.topic_fn,self__.ch,self__.pub,meta12220__$1));
});})(mults,ensure_mult))
;
cljs.core.async.__GT_t12219 = ((function (mults,ensure_mult){
return (function __GT_t12219(ensure_mult__$1,mults__$1,buf_fn__$1,topic_fn__$1,ch__$1,pub__$1,meta12220){return (new cljs.core.async.t12219(ensure_mult__$1,mults__$1,buf_fn__$1,topic_fn__$1,ch__$1,pub__$1,meta12220));
});})(mults,ensure_mult))
;
}
return (new cljs.core.async.t12219(ensure_mult,mults,buf_fn,topic_fn,ch,pub,null));
})();var c__6352__auto___12341 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___12341,mults,ensure_mult,p){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___12341,mults,ensure_mult,p){
return (function (state_12293){var state_val_12294 = (state_12293[(1)]);if((state_val_12294 === (7)))
{var inst_12289 = (state_12293[(2)]);var state_12293__$1 = state_12293;var statearr_12295_12342 = state_12293__$1;(statearr_12295_12342[(2)] = inst_12289);
(statearr_12295_12342[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (20)))
{var state_12293__$1 = state_12293;var statearr_12296_12343 = state_12293__$1;(statearr_12296_12343[(2)] = null);
(statearr_12296_12343[(1)] = (21));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (1)))
{var state_12293__$1 = state_12293;var statearr_12297_12344 = state_12293__$1;(statearr_12297_12344[(2)] = null);
(statearr_12297_12344[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (24)))
{var inst_12272 = (state_12293[(7)]);var inst_12281 = cljs.core.swap_BANG_.call(null,mults,cljs.core.dissoc,inst_12272);var state_12293__$1 = state_12293;var statearr_12298_12345 = state_12293__$1;(statearr_12298_12345[(2)] = inst_12281);
(statearr_12298_12345[(1)] = (25));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (4)))
{var inst_12224 = (state_12293[(8)]);var inst_12224__$1 = (state_12293[(2)]);var inst_12225 = (inst_12224__$1 == null);var state_12293__$1 = (function (){var statearr_12299 = state_12293;(statearr_12299[(8)] = inst_12224__$1);
return statearr_12299;
})();if(cljs.core.truth_(inst_12225))
{var statearr_12300_12346 = state_12293__$1;(statearr_12300_12346[(1)] = (5));
} else
{var statearr_12301_12347 = state_12293__$1;(statearr_12301_12347[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (15)))
{var inst_12266 = (state_12293[(2)]);var state_12293__$1 = state_12293;var statearr_12302_12348 = state_12293__$1;(statearr_12302_12348[(2)] = inst_12266);
(statearr_12302_12348[(1)] = (12));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (21)))
{var inst_12286 = (state_12293[(2)]);var state_12293__$1 = (function (){var statearr_12303 = state_12293;(statearr_12303[(9)] = inst_12286);
return statearr_12303;
})();var statearr_12304_12349 = state_12293__$1;(statearr_12304_12349[(2)] = null);
(statearr_12304_12349[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (13)))
{var inst_12248 = (state_12293[(10)]);var inst_12250 = cljs.core.chunked_seq_QMARK_.call(null,inst_12248);var state_12293__$1 = state_12293;if(inst_12250)
{var statearr_12305_12350 = state_12293__$1;(statearr_12305_12350[(1)] = (16));
} else
{var statearr_12306_12351 = state_12293__$1;(statearr_12306_12351[(1)] = (17));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (22)))
{var inst_12278 = (state_12293[(2)]);var state_12293__$1 = state_12293;if(cljs.core.truth_(inst_12278))
{var statearr_12307_12352 = state_12293__$1;(statearr_12307_12352[(1)] = (23));
} else
{var statearr_12308_12353 = state_12293__$1;(statearr_12308_12353[(1)] = (24));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (6)))
{var inst_12274 = (state_12293[(11)]);var inst_12224 = (state_12293[(8)]);var inst_12272 = (state_12293[(7)]);var inst_12272__$1 = topic_fn.call(null,inst_12224);var inst_12273 = cljs.core.deref.call(null,mults);var inst_12274__$1 = cljs.core.get.call(null,inst_12273,inst_12272__$1);var state_12293__$1 = (function (){var statearr_12309 = state_12293;(statearr_12309[(11)] = inst_12274__$1);
(statearr_12309[(7)] = inst_12272__$1);
return statearr_12309;
})();if(cljs.core.truth_(inst_12274__$1))
{var statearr_12310_12354 = state_12293__$1;(statearr_12310_12354[(1)] = (19));
} else
{var statearr_12311_12355 = state_12293__$1;(statearr_12311_12355[(1)] = (20));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (25)))
{var inst_12283 = (state_12293[(2)]);var state_12293__$1 = state_12293;var statearr_12312_12356 = state_12293__$1;(statearr_12312_12356[(2)] = inst_12283);
(statearr_12312_12356[(1)] = (21));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (17)))
{var inst_12248 = (state_12293[(10)]);var inst_12257 = cljs.core.first.call(null,inst_12248);var inst_12258 = cljs.core.async.muxch_STAR_.call(null,inst_12257);var inst_12259 = cljs.core.async.close_BANG_.call(null,inst_12258);var inst_12260 = cljs.core.next.call(null,inst_12248);var inst_12234 = inst_12260;var inst_12235 = null;var inst_12236 = (0);var inst_12237 = (0);var state_12293__$1 = (function (){var statearr_12313 = state_12293;(statearr_12313[(12)] = inst_12259);
(statearr_12313[(13)] = inst_12236);
(statearr_12313[(14)] = inst_12235);
(statearr_12313[(15)] = inst_12234);
(statearr_12313[(16)] = inst_12237);
return statearr_12313;
})();var statearr_12314_12357 = state_12293__$1;(statearr_12314_12357[(2)] = null);
(statearr_12314_12357[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (3)))
{var inst_12291 = (state_12293[(2)]);var state_12293__$1 = state_12293;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12293__$1,inst_12291);
} else
{if((state_val_12294 === (12)))
{var inst_12268 = (state_12293[(2)]);var state_12293__$1 = state_12293;var statearr_12315_12358 = state_12293__$1;(statearr_12315_12358[(2)] = inst_12268);
(statearr_12315_12358[(1)] = (9));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (2)))
{var state_12293__$1 = state_12293;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_12293__$1,(4),ch);
} else
{if((state_val_12294 === (23)))
{var state_12293__$1 = state_12293;var statearr_12316_12359 = state_12293__$1;(statearr_12316_12359[(2)] = null);
(statearr_12316_12359[(1)] = (25));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (19)))
{var inst_12274 = (state_12293[(11)]);var inst_12224 = (state_12293[(8)]);var inst_12276 = cljs.core.async.muxch_STAR_.call(null,inst_12274);var state_12293__$1 = state_12293;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12293__$1,(22),inst_12276,inst_12224);
} else
{if((state_val_12294 === (11)))
{var inst_12248 = (state_12293[(10)]);var inst_12234 = (state_12293[(15)]);var inst_12248__$1 = cljs.core.seq.call(null,inst_12234);var state_12293__$1 = (function (){var statearr_12317 = state_12293;(statearr_12317[(10)] = inst_12248__$1);
return statearr_12317;
})();if(inst_12248__$1)
{var statearr_12318_12360 = state_12293__$1;(statearr_12318_12360[(1)] = (13));
} else
{var statearr_12319_12361 = state_12293__$1;(statearr_12319_12361[(1)] = (14));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (9)))
{var inst_12270 = (state_12293[(2)]);var state_12293__$1 = state_12293;var statearr_12320_12362 = state_12293__$1;(statearr_12320_12362[(2)] = inst_12270);
(statearr_12320_12362[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (5)))
{var inst_12231 = cljs.core.deref.call(null,mults);var inst_12232 = cljs.core.vals.call(null,inst_12231);var inst_12233 = cljs.core.seq.call(null,inst_12232);var inst_12234 = inst_12233;var inst_12235 = null;var inst_12236 = (0);var inst_12237 = (0);var state_12293__$1 = (function (){var statearr_12321 = state_12293;(statearr_12321[(13)] = inst_12236);
(statearr_12321[(14)] = inst_12235);
(statearr_12321[(15)] = inst_12234);
(statearr_12321[(16)] = inst_12237);
return statearr_12321;
})();var statearr_12322_12363 = state_12293__$1;(statearr_12322_12363[(2)] = null);
(statearr_12322_12363[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (14)))
{var state_12293__$1 = state_12293;var statearr_12326_12364 = state_12293__$1;(statearr_12326_12364[(2)] = null);
(statearr_12326_12364[(1)] = (15));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (16)))
{var inst_12248 = (state_12293[(10)]);var inst_12252 = cljs.core.chunk_first.call(null,inst_12248);var inst_12253 = cljs.core.chunk_rest.call(null,inst_12248);var inst_12254 = cljs.core.count.call(null,inst_12252);var inst_12234 = inst_12253;var inst_12235 = inst_12252;var inst_12236 = inst_12254;var inst_12237 = (0);var state_12293__$1 = (function (){var statearr_12327 = state_12293;(statearr_12327[(13)] = inst_12236);
(statearr_12327[(14)] = inst_12235);
(statearr_12327[(15)] = inst_12234);
(statearr_12327[(16)] = inst_12237);
return statearr_12327;
})();var statearr_12328_12365 = state_12293__$1;(statearr_12328_12365[(2)] = null);
(statearr_12328_12365[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (10)))
{var inst_12236 = (state_12293[(13)]);var inst_12235 = (state_12293[(14)]);var inst_12234 = (state_12293[(15)]);var inst_12237 = (state_12293[(16)]);var inst_12242 = cljs.core._nth.call(null,inst_12235,inst_12237);var inst_12243 = cljs.core.async.muxch_STAR_.call(null,inst_12242);var inst_12244 = cljs.core.async.close_BANG_.call(null,inst_12243);var inst_12245 = (inst_12237 + (1));var tmp12323 = inst_12236;var tmp12324 = inst_12235;var tmp12325 = inst_12234;var inst_12234__$1 = tmp12325;var inst_12235__$1 = tmp12324;var inst_12236__$1 = tmp12323;var inst_12237__$1 = inst_12245;var state_12293__$1 = (function (){var statearr_12329 = state_12293;(statearr_12329[(13)] = inst_12236__$1);
(statearr_12329[(14)] = inst_12235__$1);
(statearr_12329[(15)] = inst_12234__$1);
(statearr_12329[(16)] = inst_12237__$1);
(statearr_12329[(17)] = inst_12244);
return statearr_12329;
})();var statearr_12330_12366 = state_12293__$1;(statearr_12330_12366[(2)] = null);
(statearr_12330_12366[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (18)))
{var inst_12263 = (state_12293[(2)]);var state_12293__$1 = state_12293;var statearr_12331_12367 = state_12293__$1;(statearr_12331_12367[(2)] = inst_12263);
(statearr_12331_12367[(1)] = (15));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12294 === (8)))
{var inst_12236 = (state_12293[(13)]);var inst_12237 = (state_12293[(16)]);var inst_12239 = (inst_12237 < inst_12236);var inst_12240 = inst_12239;var state_12293__$1 = state_12293;if(cljs.core.truth_(inst_12240))
{var statearr_12332_12368 = state_12293__$1;(statearr_12332_12368[(1)] = (10));
} else
{var statearr_12333_12369 = state_12293__$1;(statearr_12333_12369[(1)] = (11));
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
});})(c__6352__auto___12341,mults,ensure_mult,p))
;return ((function (switch__6337__auto__,c__6352__auto___12341,mults,ensure_mult,p){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_12337 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_12337[(0)] = state_machine__6338__auto__);
(statearr_12337[(1)] = (1));
return statearr_12337;
});
var state_machine__6338__auto____1 = (function (state_12293){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12293);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e12338){if((e12338 instanceof Object))
{var ex__6341__auto__ = e12338;var statearr_12339_12370 = state_12293;(statearr_12339_12370[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12293);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e12338;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__12371 = state_12293;
state_12293 = G__12371;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12293){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12293);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___12341,mults,ensure_mult,p))
})();var state__6354__auto__ = (function (){var statearr_12340 = f__6353__auto__.call(null);(statearr_12340[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___12341);
return statearr_12340;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___12341,mults,ensure_mult,p))
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
,cljs.core.range.call(null,cnt));var c__6352__auto___12508 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___12508,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___12508,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function (state_12478){var state_val_12479 = (state_12478[(1)]);if((state_val_12479 === (7)))
{var state_12478__$1 = state_12478;var statearr_12480_12509 = state_12478__$1;(statearr_12480_12509[(2)] = null);
(statearr_12480_12509[(1)] = (8));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12479 === (1)))
{var state_12478__$1 = state_12478;var statearr_12481_12510 = state_12478__$1;(statearr_12481_12510[(2)] = null);
(statearr_12481_12510[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12479 === (4)))
{var inst_12442 = (state_12478[(7)]);var inst_12444 = (inst_12442 < cnt);var state_12478__$1 = state_12478;if(cljs.core.truth_(inst_12444))
{var statearr_12482_12511 = state_12478__$1;(statearr_12482_12511[(1)] = (6));
} else
{var statearr_12483_12512 = state_12478__$1;(statearr_12483_12512[(1)] = (7));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12479 === (15)))
{var inst_12474 = (state_12478[(2)]);var state_12478__$1 = state_12478;var statearr_12484_12513 = state_12478__$1;(statearr_12484_12513[(2)] = inst_12474);
(statearr_12484_12513[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12479 === (13)))
{var inst_12467 = cljs.core.async.close_BANG_.call(null,out);var state_12478__$1 = state_12478;var statearr_12485_12514 = state_12478__$1;(statearr_12485_12514[(2)] = inst_12467);
(statearr_12485_12514[(1)] = (15));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12479 === (6)))
{var state_12478__$1 = state_12478;var statearr_12486_12515 = state_12478__$1;(statearr_12486_12515[(2)] = null);
(statearr_12486_12515[(1)] = (11));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12479 === (3)))
{var inst_12476 = (state_12478[(2)]);var state_12478__$1 = state_12478;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12478__$1,inst_12476);
} else
{if((state_val_12479 === (12)))
{var inst_12464 = (state_12478[(8)]);var inst_12464__$1 = (state_12478[(2)]);var inst_12465 = cljs.core.some.call(null,cljs.core.nil_QMARK_,inst_12464__$1);var state_12478__$1 = (function (){var statearr_12487 = state_12478;(statearr_12487[(8)] = inst_12464__$1);
return statearr_12487;
})();if(cljs.core.truth_(inst_12465))
{var statearr_12488_12516 = state_12478__$1;(statearr_12488_12516[(1)] = (13));
} else
{var statearr_12489_12517 = state_12478__$1;(statearr_12489_12517[(1)] = (14));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12479 === (2)))
{var inst_12441 = cljs.core.reset_BANG_.call(null,dctr,cnt);var inst_12442 = (0);var state_12478__$1 = (function (){var statearr_12490 = state_12478;(statearr_12490[(7)] = inst_12442);
(statearr_12490[(9)] = inst_12441);
return statearr_12490;
})();var statearr_12491_12518 = state_12478__$1;(statearr_12491_12518[(2)] = null);
(statearr_12491_12518[(1)] = (4));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12479 === (11)))
{var inst_12442 = (state_12478[(7)]);var _ = cljs.core.async.impl.ioc_helpers.add_exception_frame.call(null,state_12478,(10),Object,null,(9));var inst_12451 = chs__$1.call(null,inst_12442);var inst_12452 = done.call(null,inst_12442);var inst_12453 = cljs.core.async.take_BANG_.call(null,inst_12451,inst_12452);var state_12478__$1 = state_12478;var statearr_12492_12519 = state_12478__$1;(statearr_12492_12519[(2)] = inst_12453);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12478__$1);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12479 === (9)))
{var inst_12442 = (state_12478[(7)]);var inst_12455 = (state_12478[(2)]);var inst_12456 = (inst_12442 + (1));var inst_12442__$1 = inst_12456;var state_12478__$1 = (function (){var statearr_12493 = state_12478;(statearr_12493[(10)] = inst_12455);
(statearr_12493[(7)] = inst_12442__$1);
return statearr_12493;
})();var statearr_12494_12520 = state_12478__$1;(statearr_12494_12520[(2)] = null);
(statearr_12494_12520[(1)] = (4));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12479 === (5)))
{var inst_12462 = (state_12478[(2)]);var state_12478__$1 = (function (){var statearr_12495 = state_12478;(statearr_12495[(11)] = inst_12462);
return statearr_12495;
})();return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_12478__$1,(12),dchan);
} else
{if((state_val_12479 === (14)))
{var inst_12464 = (state_12478[(8)]);var inst_12469 = cljs.core.apply.call(null,f,inst_12464);var state_12478__$1 = state_12478;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12478__$1,(16),out,inst_12469);
} else
{if((state_val_12479 === (16)))
{var inst_12471 = (state_12478[(2)]);var state_12478__$1 = (function (){var statearr_12496 = state_12478;(statearr_12496[(12)] = inst_12471);
return statearr_12496;
})();var statearr_12497_12521 = state_12478__$1;(statearr_12497_12521[(2)] = null);
(statearr_12497_12521[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12479 === (10)))
{var inst_12446 = (state_12478[(2)]);var inst_12447 = cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec);var state_12478__$1 = (function (){var statearr_12498 = state_12478;(statearr_12498[(13)] = inst_12446);
return statearr_12498;
})();var statearr_12499_12522 = state_12478__$1;(statearr_12499_12522[(2)] = inst_12447);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12478__$1);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12479 === (8)))
{var inst_12460 = (state_12478[(2)]);var state_12478__$1 = state_12478;var statearr_12500_12523 = state_12478__$1;(statearr_12500_12523[(2)] = inst_12460);
(statearr_12500_12523[(1)] = (5));
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
});})(c__6352__auto___12508,chs__$1,out,cnt,rets,dchan,dctr,done))
;return ((function (switch__6337__auto__,c__6352__auto___12508,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_12504 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_12504[(0)] = state_machine__6338__auto__);
(statearr_12504[(1)] = (1));
return statearr_12504;
});
var state_machine__6338__auto____1 = (function (state_12478){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12478);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e12505){if((e12505 instanceof Object))
{var ex__6341__auto__ = e12505;var statearr_12506_12524 = state_12478;(statearr_12506_12524[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12478);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e12505;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__12525 = state_12478;
state_12478 = G__12525;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12478){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12478);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___12508,chs__$1,out,cnt,rets,dchan,dctr,done))
})();var state__6354__auto__ = (function (){var statearr_12507 = f__6353__auto__.call(null);(statearr_12507[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___12508);
return statearr_12507;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___12508,chs__$1,out,cnt,rets,dchan,dctr,done))
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
var merge__2 = (function (chs,buf_or_n){var out = cljs.core.async.chan.call(null,buf_or_n);var c__6352__auto___12633 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___12633,out){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___12633,out){
return (function (state_12609){var state_val_12610 = (state_12609[(1)]);if((state_val_12610 === (7)))
{var inst_12588 = (state_12609[(7)]);var inst_12589 = (state_12609[(8)]);var inst_12588__$1 = (state_12609[(2)]);var inst_12589__$1 = cljs.core.nth.call(null,inst_12588__$1,(0),null);var inst_12590 = cljs.core.nth.call(null,inst_12588__$1,(1),null);var inst_12591 = (inst_12589__$1 == null);var state_12609__$1 = (function (){var statearr_12611 = state_12609;(statearr_12611[(9)] = inst_12590);
(statearr_12611[(7)] = inst_12588__$1);
(statearr_12611[(8)] = inst_12589__$1);
return statearr_12611;
})();if(cljs.core.truth_(inst_12591))
{var statearr_12612_12634 = state_12609__$1;(statearr_12612_12634[(1)] = (8));
} else
{var statearr_12613_12635 = state_12609__$1;(statearr_12613_12635[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12610 === (1)))
{var inst_12580 = cljs.core.vec.call(null,chs);var inst_12581 = inst_12580;var state_12609__$1 = (function (){var statearr_12614 = state_12609;(statearr_12614[(10)] = inst_12581);
return statearr_12614;
})();var statearr_12615_12636 = state_12609__$1;(statearr_12615_12636[(2)] = null);
(statearr_12615_12636[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12610 === (4)))
{var inst_12581 = (state_12609[(10)]);var state_12609__$1 = state_12609;return cljs.core.async.impl.ioc_helpers.ioc_alts_BANG_.call(null,state_12609__$1,(7),inst_12581);
} else
{if((state_val_12610 === (6)))
{var inst_12605 = (state_12609[(2)]);var state_12609__$1 = state_12609;var statearr_12616_12637 = state_12609__$1;(statearr_12616_12637[(2)] = inst_12605);
(statearr_12616_12637[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12610 === (3)))
{var inst_12607 = (state_12609[(2)]);var state_12609__$1 = state_12609;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12609__$1,inst_12607);
} else
{if((state_val_12610 === (2)))
{var inst_12581 = (state_12609[(10)]);var inst_12583 = cljs.core.count.call(null,inst_12581);var inst_12584 = (inst_12583 > (0));var state_12609__$1 = state_12609;if(cljs.core.truth_(inst_12584))
{var statearr_12618_12638 = state_12609__$1;(statearr_12618_12638[(1)] = (4));
} else
{var statearr_12619_12639 = state_12609__$1;(statearr_12619_12639[(1)] = (5));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12610 === (11)))
{var inst_12581 = (state_12609[(10)]);var inst_12598 = (state_12609[(2)]);var tmp12617 = inst_12581;var inst_12581__$1 = tmp12617;var state_12609__$1 = (function (){var statearr_12620 = state_12609;(statearr_12620[(10)] = inst_12581__$1);
(statearr_12620[(11)] = inst_12598);
return statearr_12620;
})();var statearr_12621_12640 = state_12609__$1;(statearr_12621_12640[(2)] = null);
(statearr_12621_12640[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12610 === (9)))
{var inst_12589 = (state_12609[(8)]);var state_12609__$1 = state_12609;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12609__$1,(11),out,inst_12589);
} else
{if((state_val_12610 === (5)))
{var inst_12603 = cljs.core.async.close_BANG_.call(null,out);var state_12609__$1 = state_12609;var statearr_12622_12641 = state_12609__$1;(statearr_12622_12641[(2)] = inst_12603);
(statearr_12622_12641[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12610 === (10)))
{var inst_12601 = (state_12609[(2)]);var state_12609__$1 = state_12609;var statearr_12623_12642 = state_12609__$1;(statearr_12623_12642[(2)] = inst_12601);
(statearr_12623_12642[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12610 === (8)))
{var inst_12581 = (state_12609[(10)]);var inst_12590 = (state_12609[(9)]);var inst_12588 = (state_12609[(7)]);var inst_12589 = (state_12609[(8)]);var inst_12593 = (function (){var c = inst_12590;var v = inst_12589;var vec__12586 = inst_12588;var cs = inst_12581;return ((function (c,v,vec__12586,cs,inst_12581,inst_12590,inst_12588,inst_12589,state_val_12610,c__6352__auto___12633,out){
return (function (p1__12526_SHARP_){return cljs.core.not_EQ_.call(null,c,p1__12526_SHARP_);
});
;})(c,v,vec__12586,cs,inst_12581,inst_12590,inst_12588,inst_12589,state_val_12610,c__6352__auto___12633,out))
})();var inst_12594 = cljs.core.filterv.call(null,inst_12593,inst_12581);var inst_12581__$1 = inst_12594;var state_12609__$1 = (function (){var statearr_12624 = state_12609;(statearr_12624[(10)] = inst_12581__$1);
return statearr_12624;
})();var statearr_12625_12643 = state_12609__$1;(statearr_12625_12643[(2)] = null);
(statearr_12625_12643[(1)] = (2));
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
});})(c__6352__auto___12633,out))
;return ((function (switch__6337__auto__,c__6352__auto___12633,out){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_12629 = [null,null,null,null,null,null,null,null,null,null,null,null];(statearr_12629[(0)] = state_machine__6338__auto__);
(statearr_12629[(1)] = (1));
return statearr_12629;
});
var state_machine__6338__auto____1 = (function (state_12609){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12609);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e12630){if((e12630 instanceof Object))
{var ex__6341__auto__ = e12630;var statearr_12631_12644 = state_12609;(statearr_12631_12644[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12609);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e12630;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__12645 = state_12609;
state_12609 = G__12645;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12609){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12609);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___12633,out))
})();var state__6354__auto__ = (function (){var statearr_12632 = f__6353__auto__.call(null);(statearr_12632[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___12633);
return statearr_12632;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___12633,out))
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
var take__3 = (function (n,ch,buf_or_n){var out = cljs.core.async.chan.call(null,buf_or_n);var c__6352__auto___12738 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___12738,out){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___12738,out){
return (function (state_12715){var state_val_12716 = (state_12715[(1)]);if((state_val_12716 === (7)))
{var inst_12697 = (state_12715[(7)]);var inst_12697__$1 = (state_12715[(2)]);var inst_12698 = (inst_12697__$1 == null);var inst_12699 = cljs.core.not.call(null,inst_12698);var state_12715__$1 = (function (){var statearr_12717 = state_12715;(statearr_12717[(7)] = inst_12697__$1);
return statearr_12717;
})();if(inst_12699)
{var statearr_12718_12739 = state_12715__$1;(statearr_12718_12739[(1)] = (8));
} else
{var statearr_12719_12740 = state_12715__$1;(statearr_12719_12740[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12716 === (1)))
{var inst_12692 = (0);var state_12715__$1 = (function (){var statearr_12720 = state_12715;(statearr_12720[(8)] = inst_12692);
return statearr_12720;
})();var statearr_12721_12741 = state_12715__$1;(statearr_12721_12741[(2)] = null);
(statearr_12721_12741[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12716 === (4)))
{var state_12715__$1 = state_12715;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_12715__$1,(7),ch);
} else
{if((state_val_12716 === (6)))
{var inst_12710 = (state_12715[(2)]);var state_12715__$1 = state_12715;var statearr_12722_12742 = state_12715__$1;(statearr_12722_12742[(2)] = inst_12710);
(statearr_12722_12742[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12716 === (3)))
{var inst_12712 = (state_12715[(2)]);var inst_12713 = cljs.core.async.close_BANG_.call(null,out);var state_12715__$1 = (function (){var statearr_12723 = state_12715;(statearr_12723[(9)] = inst_12712);
return statearr_12723;
})();return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12715__$1,inst_12713);
} else
{if((state_val_12716 === (2)))
{var inst_12692 = (state_12715[(8)]);var inst_12694 = (inst_12692 < n);var state_12715__$1 = state_12715;if(cljs.core.truth_(inst_12694))
{var statearr_12724_12743 = state_12715__$1;(statearr_12724_12743[(1)] = (4));
} else
{var statearr_12725_12744 = state_12715__$1;(statearr_12725_12744[(1)] = (5));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12716 === (11)))
{var inst_12692 = (state_12715[(8)]);var inst_12702 = (state_12715[(2)]);var inst_12703 = (inst_12692 + (1));var inst_12692__$1 = inst_12703;var state_12715__$1 = (function (){var statearr_12726 = state_12715;(statearr_12726[(10)] = inst_12702);
(statearr_12726[(8)] = inst_12692__$1);
return statearr_12726;
})();var statearr_12727_12745 = state_12715__$1;(statearr_12727_12745[(2)] = null);
(statearr_12727_12745[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12716 === (9)))
{var state_12715__$1 = state_12715;var statearr_12728_12746 = state_12715__$1;(statearr_12728_12746[(2)] = null);
(statearr_12728_12746[(1)] = (10));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12716 === (5)))
{var state_12715__$1 = state_12715;var statearr_12729_12747 = state_12715__$1;(statearr_12729_12747[(2)] = null);
(statearr_12729_12747[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12716 === (10)))
{var inst_12707 = (state_12715[(2)]);var state_12715__$1 = state_12715;var statearr_12730_12748 = state_12715__$1;(statearr_12730_12748[(2)] = inst_12707);
(statearr_12730_12748[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12716 === (8)))
{var inst_12697 = (state_12715[(7)]);var state_12715__$1 = state_12715;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12715__$1,(11),out,inst_12697);
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
});})(c__6352__auto___12738,out))
;return ((function (switch__6337__auto__,c__6352__auto___12738,out){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_12734 = [null,null,null,null,null,null,null,null,null,null,null];(statearr_12734[(0)] = state_machine__6338__auto__);
(statearr_12734[(1)] = (1));
return statearr_12734;
});
var state_machine__6338__auto____1 = (function (state_12715){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12715);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e12735){if((e12735 instanceof Object))
{var ex__6341__auto__ = e12735;var statearr_12736_12749 = state_12715;(statearr_12736_12749[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12715);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e12735;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__12750 = state_12715;
state_12715 = G__12750;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12715){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12715);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___12738,out))
})();var state__6354__auto__ = (function (){var statearr_12737 = f__6353__auto__.call(null);(statearr_12737[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___12738);
return statearr_12737;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___12738,out))
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
var unique__2 = (function (ch,buf_or_n){var out = cljs.core.async.chan.call(null,buf_or_n);var c__6352__auto___12847 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___12847,out){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___12847,out){
return (function (state_12822){var state_val_12823 = (state_12822[(1)]);if((state_val_12823 === (7)))
{var inst_12817 = (state_12822[(2)]);var state_12822__$1 = state_12822;var statearr_12824_12848 = state_12822__$1;(statearr_12824_12848[(2)] = inst_12817);
(statearr_12824_12848[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12823 === (1)))
{var inst_12799 = null;var state_12822__$1 = (function (){var statearr_12825 = state_12822;(statearr_12825[(7)] = inst_12799);
return statearr_12825;
})();var statearr_12826_12849 = state_12822__$1;(statearr_12826_12849[(2)] = null);
(statearr_12826_12849[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12823 === (4)))
{var inst_12802 = (state_12822[(8)]);var inst_12802__$1 = (state_12822[(2)]);var inst_12803 = (inst_12802__$1 == null);var inst_12804 = cljs.core.not.call(null,inst_12803);var state_12822__$1 = (function (){var statearr_12827 = state_12822;(statearr_12827[(8)] = inst_12802__$1);
return statearr_12827;
})();if(inst_12804)
{var statearr_12828_12850 = state_12822__$1;(statearr_12828_12850[(1)] = (5));
} else
{var statearr_12829_12851 = state_12822__$1;(statearr_12829_12851[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12823 === (6)))
{var state_12822__$1 = state_12822;var statearr_12830_12852 = state_12822__$1;(statearr_12830_12852[(2)] = null);
(statearr_12830_12852[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12823 === (3)))
{var inst_12819 = (state_12822[(2)]);var inst_12820 = cljs.core.async.close_BANG_.call(null,out);var state_12822__$1 = (function (){var statearr_12831 = state_12822;(statearr_12831[(9)] = inst_12819);
return statearr_12831;
})();return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12822__$1,inst_12820);
} else
{if((state_val_12823 === (2)))
{var state_12822__$1 = state_12822;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_12822__$1,(4),ch);
} else
{if((state_val_12823 === (11)))
{var inst_12802 = (state_12822[(8)]);var inst_12811 = (state_12822[(2)]);var inst_12799 = inst_12802;var state_12822__$1 = (function (){var statearr_12832 = state_12822;(statearr_12832[(10)] = inst_12811);
(statearr_12832[(7)] = inst_12799);
return statearr_12832;
})();var statearr_12833_12853 = state_12822__$1;(statearr_12833_12853[(2)] = null);
(statearr_12833_12853[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12823 === (9)))
{var inst_12802 = (state_12822[(8)]);var state_12822__$1 = state_12822;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12822__$1,(11),out,inst_12802);
} else
{if((state_val_12823 === (5)))
{var inst_12799 = (state_12822[(7)]);var inst_12802 = (state_12822[(8)]);var inst_12806 = cljs.core._EQ_.call(null,inst_12802,inst_12799);var state_12822__$1 = state_12822;if(inst_12806)
{var statearr_12835_12854 = state_12822__$1;(statearr_12835_12854[(1)] = (8));
} else
{var statearr_12836_12855 = state_12822__$1;(statearr_12836_12855[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12823 === (10)))
{var inst_12814 = (state_12822[(2)]);var state_12822__$1 = state_12822;var statearr_12837_12856 = state_12822__$1;(statearr_12837_12856[(2)] = inst_12814);
(statearr_12837_12856[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12823 === (8)))
{var inst_12799 = (state_12822[(7)]);var tmp12834 = inst_12799;var inst_12799__$1 = tmp12834;var state_12822__$1 = (function (){var statearr_12838 = state_12822;(statearr_12838[(7)] = inst_12799__$1);
return statearr_12838;
})();var statearr_12839_12857 = state_12822__$1;(statearr_12839_12857[(2)] = null);
(statearr_12839_12857[(1)] = (2));
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
});})(c__6352__auto___12847,out))
;return ((function (switch__6337__auto__,c__6352__auto___12847,out){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_12843 = [null,null,null,null,null,null,null,null,null,null,null];(statearr_12843[(0)] = state_machine__6338__auto__);
(statearr_12843[(1)] = (1));
return statearr_12843;
});
var state_machine__6338__auto____1 = (function (state_12822){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12822);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e12844){if((e12844 instanceof Object))
{var ex__6341__auto__ = e12844;var statearr_12845_12858 = state_12822;(statearr_12845_12858[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12822);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e12844;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__12859 = state_12822;
state_12822 = G__12859;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12822){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12822);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___12847,out))
})();var state__6354__auto__ = (function (){var statearr_12846 = f__6353__auto__.call(null);(statearr_12846[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___12847);
return statearr_12846;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___12847,out))
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
var partition__3 = (function (n,ch,buf_or_n){var out = cljs.core.async.chan.call(null,buf_or_n);var c__6352__auto___12994 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___12994,out){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___12994,out){
return (function (state_12964){var state_val_12965 = (state_12964[(1)]);if((state_val_12965 === (7)))
{var inst_12960 = (state_12964[(2)]);var state_12964__$1 = state_12964;var statearr_12966_12995 = state_12964__$1;(statearr_12966_12995[(2)] = inst_12960);
(statearr_12966_12995[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12965 === (1)))
{var inst_12927 = (new Array(n));var inst_12928 = inst_12927;var inst_12929 = (0);var state_12964__$1 = (function (){var statearr_12967 = state_12964;(statearr_12967[(7)] = inst_12929);
(statearr_12967[(8)] = inst_12928);
return statearr_12967;
})();var statearr_12968_12996 = state_12964__$1;(statearr_12968_12996[(2)] = null);
(statearr_12968_12996[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12965 === (4)))
{var inst_12932 = (state_12964[(9)]);var inst_12932__$1 = (state_12964[(2)]);var inst_12933 = (inst_12932__$1 == null);var inst_12934 = cljs.core.not.call(null,inst_12933);var state_12964__$1 = (function (){var statearr_12969 = state_12964;(statearr_12969[(9)] = inst_12932__$1);
return statearr_12969;
})();if(inst_12934)
{var statearr_12970_12997 = state_12964__$1;(statearr_12970_12997[(1)] = (5));
} else
{var statearr_12971_12998 = state_12964__$1;(statearr_12971_12998[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12965 === (15)))
{var inst_12954 = (state_12964[(2)]);var state_12964__$1 = state_12964;var statearr_12972_12999 = state_12964__$1;(statearr_12972_12999[(2)] = inst_12954);
(statearr_12972_12999[(1)] = (14));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12965 === (13)))
{var state_12964__$1 = state_12964;var statearr_12973_13000 = state_12964__$1;(statearr_12973_13000[(2)] = null);
(statearr_12973_13000[(1)] = (14));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12965 === (6)))
{var inst_12929 = (state_12964[(7)]);var inst_12950 = (inst_12929 > (0));var state_12964__$1 = state_12964;if(cljs.core.truth_(inst_12950))
{var statearr_12974_13001 = state_12964__$1;(statearr_12974_13001[(1)] = (12));
} else
{var statearr_12975_13002 = state_12964__$1;(statearr_12975_13002[(1)] = (13));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12965 === (3)))
{var inst_12962 = (state_12964[(2)]);var state_12964__$1 = state_12964;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_12964__$1,inst_12962);
} else
{if((state_val_12965 === (12)))
{var inst_12928 = (state_12964[(8)]);var inst_12952 = cljs.core.vec.call(null,inst_12928);var state_12964__$1 = state_12964;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12964__$1,(15),out,inst_12952);
} else
{if((state_val_12965 === (2)))
{var state_12964__$1 = state_12964;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_12964__$1,(4),ch);
} else
{if((state_val_12965 === (11)))
{var inst_12944 = (state_12964[(2)]);var inst_12945 = (new Array(n));var inst_12928 = inst_12945;var inst_12929 = (0);var state_12964__$1 = (function (){var statearr_12976 = state_12964;(statearr_12976[(7)] = inst_12929);
(statearr_12976[(8)] = inst_12928);
(statearr_12976[(10)] = inst_12944);
return statearr_12976;
})();var statearr_12977_13003 = state_12964__$1;(statearr_12977_13003[(2)] = null);
(statearr_12977_13003[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12965 === (9)))
{var inst_12928 = (state_12964[(8)]);var inst_12942 = cljs.core.vec.call(null,inst_12928);var state_12964__$1 = state_12964;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_12964__$1,(11),out,inst_12942);
} else
{if((state_val_12965 === (5)))
{var inst_12929 = (state_12964[(7)]);var inst_12928 = (state_12964[(8)]);var inst_12937 = (state_12964[(11)]);var inst_12932 = (state_12964[(9)]);var inst_12936 = (inst_12928[inst_12929] = inst_12932);var inst_12937__$1 = (inst_12929 + (1));var inst_12938 = (inst_12937__$1 < n);var state_12964__$1 = (function (){var statearr_12978 = state_12964;(statearr_12978[(11)] = inst_12937__$1);
(statearr_12978[(12)] = inst_12936);
return statearr_12978;
})();if(cljs.core.truth_(inst_12938))
{var statearr_12979_13004 = state_12964__$1;(statearr_12979_13004[(1)] = (8));
} else
{var statearr_12980_13005 = state_12964__$1;(statearr_12980_13005[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12965 === (14)))
{var inst_12957 = (state_12964[(2)]);var inst_12958 = cljs.core.async.close_BANG_.call(null,out);var state_12964__$1 = (function (){var statearr_12982 = state_12964;(statearr_12982[(13)] = inst_12957);
return statearr_12982;
})();var statearr_12983_13006 = state_12964__$1;(statearr_12983_13006[(2)] = inst_12958);
(statearr_12983_13006[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12965 === (10)))
{var inst_12948 = (state_12964[(2)]);var state_12964__$1 = state_12964;var statearr_12984_13007 = state_12964__$1;(statearr_12984_13007[(2)] = inst_12948);
(statearr_12984_13007[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_12965 === (8)))
{var inst_12928 = (state_12964[(8)]);var inst_12937 = (state_12964[(11)]);var tmp12981 = inst_12928;var inst_12928__$1 = tmp12981;var inst_12929 = inst_12937;var state_12964__$1 = (function (){var statearr_12985 = state_12964;(statearr_12985[(7)] = inst_12929);
(statearr_12985[(8)] = inst_12928__$1);
return statearr_12985;
})();var statearr_12986_13008 = state_12964__$1;(statearr_12986_13008[(2)] = null);
(statearr_12986_13008[(1)] = (2));
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
});})(c__6352__auto___12994,out))
;return ((function (switch__6337__auto__,c__6352__auto___12994,out){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_12990 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_12990[(0)] = state_machine__6338__auto__);
(statearr_12990[(1)] = (1));
return statearr_12990;
});
var state_machine__6338__auto____1 = (function (state_12964){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_12964);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e12991){if((e12991 instanceof Object))
{var ex__6341__auto__ = e12991;var statearr_12992_13009 = state_12964;(statearr_12992_13009[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_12964);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e12991;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__13010 = state_12964;
state_12964 = G__13010;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_12964){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_12964);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___12994,out))
})();var state__6354__auto__ = (function (){var statearr_12993 = f__6353__auto__.call(null);(statearr_12993[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___12994);
return statearr_12993;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___12994,out))
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
var partition_by__3 = (function (f,ch,buf_or_n){var out = cljs.core.async.chan.call(null,buf_or_n);var c__6352__auto___13153 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___13153,out){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___13153,out){
return (function (state_13123){var state_val_13124 = (state_13123[(1)]);if((state_val_13124 === (7)))
{var inst_13119 = (state_13123[(2)]);var state_13123__$1 = state_13123;var statearr_13125_13154 = state_13123__$1;(statearr_13125_13154[(2)] = inst_13119);
(statearr_13125_13154[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13124 === (1)))
{var inst_13082 = [];var inst_13083 = inst_13082;var inst_13084 = new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123);var state_13123__$1 = (function (){var statearr_13126 = state_13123;(statearr_13126[(7)] = inst_13083);
(statearr_13126[(8)] = inst_13084);
return statearr_13126;
})();var statearr_13127_13155 = state_13123__$1;(statearr_13127_13155[(2)] = null);
(statearr_13127_13155[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13124 === (4)))
{var inst_13087 = (state_13123[(9)]);var inst_13087__$1 = (state_13123[(2)]);var inst_13088 = (inst_13087__$1 == null);var inst_13089 = cljs.core.not.call(null,inst_13088);var state_13123__$1 = (function (){var statearr_13128 = state_13123;(statearr_13128[(9)] = inst_13087__$1);
return statearr_13128;
})();if(inst_13089)
{var statearr_13129_13156 = state_13123__$1;(statearr_13129_13156[(1)] = (5));
} else
{var statearr_13130_13157 = state_13123__$1;(statearr_13130_13157[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13124 === (15)))
{var inst_13113 = (state_13123[(2)]);var state_13123__$1 = state_13123;var statearr_13131_13158 = state_13123__$1;(statearr_13131_13158[(2)] = inst_13113);
(statearr_13131_13158[(1)] = (14));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13124 === (13)))
{var state_13123__$1 = state_13123;var statearr_13132_13159 = state_13123__$1;(statearr_13132_13159[(2)] = null);
(statearr_13132_13159[(1)] = (14));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13124 === (6)))
{var inst_13083 = (state_13123[(7)]);var inst_13108 = inst_13083.length;var inst_13109 = (inst_13108 > (0));var state_13123__$1 = state_13123;if(cljs.core.truth_(inst_13109))
{var statearr_13133_13160 = state_13123__$1;(statearr_13133_13160[(1)] = (12));
} else
{var statearr_13134_13161 = state_13123__$1;(statearr_13134_13161[(1)] = (13));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13124 === (3)))
{var inst_13121 = (state_13123[(2)]);var state_13123__$1 = state_13123;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_13123__$1,inst_13121);
} else
{if((state_val_13124 === (12)))
{var inst_13083 = (state_13123[(7)]);var inst_13111 = cljs.core.vec.call(null,inst_13083);var state_13123__$1 = state_13123;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_13123__$1,(15),out,inst_13111);
} else
{if((state_val_13124 === (2)))
{var state_13123__$1 = state_13123;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_13123__$1,(4),ch);
} else
{if((state_val_13124 === (11)))
{var inst_13091 = (state_13123[(10)]);var inst_13087 = (state_13123[(9)]);var inst_13101 = (state_13123[(2)]);var inst_13102 = [];var inst_13103 = inst_13102.push(inst_13087);var inst_13083 = inst_13102;var inst_13084 = inst_13091;var state_13123__$1 = (function (){var statearr_13135 = state_13123;(statearr_13135[(11)] = inst_13103);
(statearr_13135[(12)] = inst_13101);
(statearr_13135[(7)] = inst_13083);
(statearr_13135[(8)] = inst_13084);
return statearr_13135;
})();var statearr_13136_13162 = state_13123__$1;(statearr_13136_13162[(2)] = null);
(statearr_13136_13162[(1)] = (2));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13124 === (9)))
{var inst_13083 = (state_13123[(7)]);var inst_13099 = cljs.core.vec.call(null,inst_13083);var state_13123__$1 = state_13123;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_13123__$1,(11),out,inst_13099);
} else
{if((state_val_13124 === (5)))
{var inst_13084 = (state_13123[(8)]);var inst_13091 = (state_13123[(10)]);var inst_13087 = (state_13123[(9)]);var inst_13091__$1 = f.call(null,inst_13087);var inst_13092 = cljs.core._EQ_.call(null,inst_13091__$1,inst_13084);var inst_13093 = cljs.core.keyword_identical_QMARK_.call(null,inst_13084,new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123));var inst_13094 = (inst_13092) || (inst_13093);var state_13123__$1 = (function (){var statearr_13137 = state_13123;(statearr_13137[(10)] = inst_13091__$1);
return statearr_13137;
})();if(cljs.core.truth_(inst_13094))
{var statearr_13138_13163 = state_13123__$1;(statearr_13138_13163[(1)] = (8));
} else
{var statearr_13139_13164 = state_13123__$1;(statearr_13139_13164[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13124 === (14)))
{var inst_13116 = (state_13123[(2)]);var inst_13117 = cljs.core.async.close_BANG_.call(null,out);var state_13123__$1 = (function (){var statearr_13141 = state_13123;(statearr_13141[(13)] = inst_13116);
return statearr_13141;
})();var statearr_13142_13165 = state_13123__$1;(statearr_13142_13165[(2)] = inst_13117);
(statearr_13142_13165[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13124 === (10)))
{var inst_13106 = (state_13123[(2)]);var state_13123__$1 = state_13123;var statearr_13143_13166 = state_13123__$1;(statearr_13143_13166[(2)] = inst_13106);
(statearr_13143_13166[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_13124 === (8)))
{var inst_13083 = (state_13123[(7)]);var inst_13091 = (state_13123[(10)]);var inst_13087 = (state_13123[(9)]);var inst_13096 = inst_13083.push(inst_13087);var tmp13140 = inst_13083;var inst_13083__$1 = tmp13140;var inst_13084 = inst_13091;var state_13123__$1 = (function (){var statearr_13144 = state_13123;(statearr_13144[(14)] = inst_13096);
(statearr_13144[(7)] = inst_13083__$1);
(statearr_13144[(8)] = inst_13084);
return statearr_13144;
})();var statearr_13145_13167 = state_13123__$1;(statearr_13145_13167[(2)] = null);
(statearr_13145_13167[(1)] = (2));
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
});})(c__6352__auto___13153,out))
;return ((function (switch__6337__auto__,c__6352__auto___13153,out){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_13149 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];(statearr_13149[(0)] = state_machine__6338__auto__);
(statearr_13149[(1)] = (1));
return statearr_13149;
});
var state_machine__6338__auto____1 = (function (state_13123){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_13123);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e13150){if((e13150 instanceof Object))
{var ex__6341__auto__ = e13150;var statearr_13151_13168 = state_13123;(statearr_13151_13168[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_13123);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e13150;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__13169 = state_13123;
state_13123 = G__13169;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_13123){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_13123);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___13153,out))
})();var state__6354__auto__ = (function (){var statearr_13152 = f__6353__auto__.call(null);(statearr_13152[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___13153);
return statearr_13152;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___13153,out))
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
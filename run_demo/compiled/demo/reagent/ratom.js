// Compiled by ClojureScript 0.0-2280
goog.provide('reagent.ratom');
goog.require('cljs.core');
reagent.ratom.debug = false;
reagent.ratom._running = cljs.core.atom.call(null,(0));
reagent.ratom.running = (function running(){return cljs.core.deref.call(null,reagent.ratom._running);
});
reagent.ratom.capture_derefed = (function capture_derefed(f,obj){obj.cljsCaptured = null;
var _STAR_ratom_context_STAR_9268 = reagent.ratom._STAR_ratom_context_STAR_;try{reagent.ratom._STAR_ratom_context_STAR_ = obj;
return f.call(null);
}finally {reagent.ratom._STAR_ratom_context_STAR_ = _STAR_ratom_context_STAR_9268;
}});
reagent.ratom.captured = (function captured(obj){var c = obj.cljsCaptured;obj.cljsCaptured = null;
return c;
});
reagent.ratom.notify_deref_watcher_BANG_ = (function notify_deref_watcher_BANG_(derefable){var obj = reagent.ratom._STAR_ratom_context_STAR_;if((obj == null))
{return null;
} else
{var captured = obj.cljsCaptured;return obj.cljsCaptured = cljs.core.conj.call(null,(((captured == null))?cljs.core.PersistentHashSet.EMPTY:captured),derefable);
}
});

/**
* @constructor
*/
reagent.ratom.RAtom = (function (state,meta,validator,watches){
this.state = state;
this.meta = meta;
this.validator = validator;
this.watches = watches;
this.cljs$lang$protocol_mask$partition0$ = 2153938944;
this.cljs$lang$protocol_mask$partition1$ = 114690;
})
reagent.ratom.RAtom.cljs$lang$type = true;
reagent.ratom.RAtom.cljs$lang$ctorStr = "reagent.ratom/RAtom";
reagent.ratom.RAtom.cljs$lang$ctorPrWriter = (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"reagent.ratom/RAtom");
});
reagent.ratom.RAtom.prototype.cljs$core$IHash$_hash$arity$1 = (function (this$){var self__ = this;
var this$__$1 = this;return goog.getUid(this$__$1);
});
reagent.ratom.RAtom.prototype.cljs$core$IWatchable$_notify_watches$arity$3 = (function (this$,oldval,newval){var self__ = this;
var this$__$1 = this;return cljs.core.reduce_kv.call(null,((function (this$__$1){
return (function (_,key,f){f.call(null,key,this$__$1,oldval,newval);
return null;
});})(this$__$1))
,null,self__.watches);
});
reagent.ratom.RAtom.prototype.cljs$core$IWatchable$_add_watch$arity$3 = (function (this$,key,f){var self__ = this;
var this$__$1 = this;return self__.watches = cljs.core.assoc.call(null,self__.watches,key,f);
});
reagent.ratom.RAtom.prototype.cljs$core$IWatchable$_remove_watch$arity$2 = (function (this$,key){var self__ = this;
var this$__$1 = this;return self__.watches = cljs.core.dissoc.call(null,self__.watches,key);
});
reagent.ratom.RAtom.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (a,writer,opts){var self__ = this;
var a__$1 = this;cljs.core._write.call(null,writer,"#<Atom: ");
cljs.core.pr_writer.call(null,self__.state,writer,opts);
return cljs.core._write.call(null,writer,">");
});
reagent.ratom.RAtom.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_){var self__ = this;
var ___$1 = this;return self__.meta;
});
reagent.ratom.RAtom.prototype.cljs$core$ISwap$_swap_BANG_$arity$2 = (function (a,f){var self__ = this;
var a__$1 = this;return cljs.core._reset_BANG_.call(null,a__$1,f.call(null,self__.state));
});
reagent.ratom.RAtom.prototype.cljs$core$ISwap$_swap_BANG_$arity$3 = (function (a,f,x){var self__ = this;
var a__$1 = this;return cljs.core._reset_BANG_.call(null,a__$1,f.call(null,self__.state,x));
});
reagent.ratom.RAtom.prototype.cljs$core$ISwap$_swap_BANG_$arity$4 = (function (a,f,x,y){var self__ = this;
var a__$1 = this;return cljs.core._reset_BANG_.call(null,a__$1,f.call(null,self__.state,x,y));
});
reagent.ratom.RAtom.prototype.cljs$core$ISwap$_swap_BANG_$arity$5 = (function (a,f,x,y,more){var self__ = this;
var a__$1 = this;return cljs.core._reset_BANG_.call(null,a__$1,cljs.core.apply.call(null,f,self__.state,x,y,more));
});
reagent.ratom.RAtom.prototype.cljs$core$IReset$_reset_BANG_$arity$2 = (function (a,new_value){var self__ = this;
var a__$1 = this;if((self__.validator == null))
{} else
{if(cljs.core.truth_(self__.validator.call(null,new_value)))
{} else
{throw (new Error(("Assert failed: Validator rejected reference state\n"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,cljs.core.list(new cljs.core.Symbol(null,"validator","validator",-325659154,null),new cljs.core.Symbol(null,"new-value","new-value",-1567397401,null)))))));
}
}
var old_value = self__.state;self__.state = new_value;
if((self__.watches == null))
{} else
{cljs.core._notify_watches.call(null,a__$1,old_value,new_value);
}
return new_value;
});
reagent.ratom.RAtom.prototype.cljs$core$IDeref$_deref$arity$1 = (function (this$){var self__ = this;
var this$__$1 = this;reagent.ratom.notify_deref_watcher_BANG_.call(null,this$__$1);
return self__.state;
});
reagent.ratom.RAtom.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (o,other){var self__ = this;
var o__$1 = this;return (o__$1 === other);
});
reagent.ratom.__GT_RAtom = (function __GT_RAtom(state,meta,validator,watches){return (new reagent.ratom.RAtom(state,meta,validator,watches));
});
/**
* Like clojure.core/atom, except that it keeps track of derefs.
* @param {...*} var_args
*/
reagent.ratom.atom = (function() {
var atom = null;
var atom__1 = (function (x){return (new reagent.ratom.RAtom(x,null,null,null));
});
var atom__2 = (function() { 
var G__9272__delegate = function (x,p__9269){var map__9271 = p__9269;var map__9271__$1 = ((cljs.core.seq_QMARK_.call(null,map__9271))?cljs.core.apply.call(null,cljs.core.hash_map,map__9271):map__9271);var validator = cljs.core.get.call(null,map__9271__$1,new cljs.core.Keyword(null,"validator","validator",-1966190681));var meta = cljs.core.get.call(null,map__9271__$1,new cljs.core.Keyword(null,"meta","meta",1499536964));return (new reagent.ratom.RAtom(x,meta,validator,null));
};
var G__9272 = function (x,var_args){
var p__9269 = null;if (arguments.length > 1) {
  p__9269 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 1),0);} 
return G__9272__delegate.call(this,x,p__9269);};
G__9272.cljs$lang$maxFixedArity = 1;
G__9272.cljs$lang$applyTo = (function (arglist__9273){
var x = cljs.core.first(arglist__9273);
var p__9269 = cljs.core.rest(arglist__9273);
return G__9272__delegate(x,p__9269);
});
G__9272.cljs$core$IFn$_invoke$arity$variadic = G__9272__delegate;
return G__9272;
})()
;
atom = function(x,var_args){
var p__9269 = var_args;
switch(arguments.length){
case 1:
return atom__1.call(this,x);
default:
return atom__2.cljs$core$IFn$_invoke$arity$variadic(x, cljs.core.array_seq(arguments, 1));
}
throw(new Error('Invalid arity: ' + arguments.length));
};
atom.cljs$lang$maxFixedArity = 1;
atom.cljs$lang$applyTo = atom__2.cljs$lang$applyTo;
atom.cljs$core$IFn$_invoke$arity$1 = atom__1;
atom.cljs$core$IFn$_invoke$arity$variadic = atom__2.cljs$core$IFn$_invoke$arity$variadic;
return atom;
})()
;
reagent.ratom.IDisposable = (function (){var obj9275 = {};return obj9275;
})();
reagent.ratom.dispose_BANG_ = (function dispose_BANG_(this$){if((function (){var and__3530__auto__ = this$;if(and__3530__auto__)
{return this$.reagent$ratom$IDisposable$dispose_BANG_$arity$1;
} else
{return and__3530__auto__;
}
})())
{return this$.reagent$ratom$IDisposable$dispose_BANG_$arity$1(this$);
} else
{var x__4169__auto__ = (((this$ == null))?null:this$);return (function (){var or__3542__auto__ = (reagent.ratom.dispose_BANG_[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (reagent.ratom.dispose_BANG_["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"IDisposable.dispose!",this$);
}
}
})().call(null,this$);
}
});
reagent.ratom.IRunnable = (function (){var obj9277 = {};return obj9277;
})();
reagent.ratom.run = (function run(this$){if((function (){var and__3530__auto__ = this$;if(and__3530__auto__)
{return this$.reagent$ratom$IRunnable$run$arity$1;
} else
{return and__3530__auto__;
}
})())
{return this$.reagent$ratom$IRunnable$run$arity$1(this$);
} else
{var x__4169__auto__ = (((this$ == null))?null:this$);return (function (){var or__3542__auto__ = (reagent.ratom.run[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (reagent.ratom.run["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"IRunnable.run",this$);
}
}
})().call(null,this$);
}
});
reagent.ratom.IComputedImpl = (function (){var obj9279 = {};return obj9279;
})();
reagent.ratom._update_watching = (function _update_watching(this$,derefed){if((function (){var and__3530__auto__ = this$;if(and__3530__auto__)
{return this$.reagent$ratom$IComputedImpl$_update_watching$arity$2;
} else
{return and__3530__auto__;
}
})())
{return this$.reagent$ratom$IComputedImpl$_update_watching$arity$2(this$,derefed);
} else
{var x__4169__auto__ = (((this$ == null))?null:this$);return (function (){var or__3542__auto__ = (reagent.ratom._update_watching[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (reagent.ratom._update_watching["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"IComputedImpl.-update-watching",this$);
}
}
})().call(null,this$,derefed);
}
});
reagent.ratom._handle_change = (function _handle_change(k,sender,oldval,newval){if((function (){var and__3530__auto__ = k;if(and__3530__auto__)
{return k.reagent$ratom$IComputedImpl$_handle_change$arity$4;
} else
{return and__3530__auto__;
}
})())
{return k.reagent$ratom$IComputedImpl$_handle_change$arity$4(k,sender,oldval,newval);
} else
{var x__4169__auto__ = (((k == null))?null:k);return (function (){var or__3542__auto__ = (reagent.ratom._handle_change[goog.typeOf(x__4169__auto__)]);if(or__3542__auto__)
{return or__3542__auto__;
} else
{var or__3542__auto____$1 = (reagent.ratom._handle_change["_"]);if(or__3542__auto____$1)
{return or__3542__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"IComputedImpl.-handle-change",k);
}
}
})().call(null,k,sender,oldval,newval);
}
});
reagent.ratom.call_watches = (function call_watches(obs,watches,oldval,newval){return cljs.core.reduce_kv.call(null,(function (_,key,f){f.call(null,key,obs,oldval,newval);
return null;
}),null,watches);
});

/**
* @constructor
*/
reagent.ratom.Reaction = (function (f,state,dirty_QMARK_,active_QMARK_,watching,watches,auto_run,on_set,on_dispose){
this.f = f;
this.state = state;
this.dirty_QMARK_ = dirty_QMARK_;
this.active_QMARK_ = active_QMARK_;
this.watching = watching;
this.watches = watches;
this.auto_run = auto_run;
this.on_set = on_set;
this.on_dispose = on_dispose;
this.cljs$lang$protocol_mask$partition0$ = 2153807872;
this.cljs$lang$protocol_mask$partition1$ = 114690;
})
reagent.ratom.Reaction.cljs$lang$type = true;
reagent.ratom.Reaction.cljs$lang$ctorStr = "reagent.ratom/Reaction";
reagent.ratom.Reaction.cljs$lang$ctorPrWriter = (function (this__4109__auto__,writer__4110__auto__,opt__4111__auto__){return cljs.core._write.call(null,writer__4110__auto__,"reagent.ratom/Reaction");
});
reagent.ratom.Reaction.prototype.reagent$ratom$IComputedImpl$ = true;
reagent.ratom.Reaction.prototype.reagent$ratom$IComputedImpl$_handle_change$arity$4 = (function (this$,sender,oldval,newval){var self__ = this;
var this$__$1 = this;if(cljs.core.truth_((function (){var and__3530__auto__ = self__.active_QMARK_;if(cljs.core.truth_(and__3530__auto__))
{return (cljs.core.not.call(null,self__.dirty_QMARK_)) && (!((oldval === newval)));
} else
{return and__3530__auto__;
}
})()))
{self__.dirty_QMARK_ = true;
return (function (){var or__3542__auto__ = self__.auto_run;if(cljs.core.truth_(or__3542__auto__))
{return or__3542__auto__;
} else
{return reagent.ratom.run;
}
})().call(null,this$__$1);
} else
{return null;
}
});
reagent.ratom.Reaction.prototype.reagent$ratom$IComputedImpl$_update_watching$arity$2 = (function (this$,derefed){var self__ = this;
var this$__$1 = this;var seq__9280_9292 = cljs.core.seq.call(null,derefed);var chunk__9281_9293 = null;var count__9282_9294 = (0);var i__9283_9295 = (0);while(true){
if((i__9283_9295 < count__9282_9294))
{var w_9296 = cljs.core._nth.call(null,chunk__9281_9293,i__9283_9295);if(cljs.core.contains_QMARK_.call(null,self__.watching,w_9296))
{} else
{cljs.core.add_watch.call(null,w_9296,this$__$1,reagent.ratom._handle_change);
}
{
var G__9297 = seq__9280_9292;
var G__9298 = chunk__9281_9293;
var G__9299 = count__9282_9294;
var G__9300 = (i__9283_9295 + (1));
seq__9280_9292 = G__9297;
chunk__9281_9293 = G__9298;
count__9282_9294 = G__9299;
i__9283_9295 = G__9300;
continue;
}
} else
{var temp__4126__auto___9301 = cljs.core.seq.call(null,seq__9280_9292);if(temp__4126__auto___9301)
{var seq__9280_9302__$1 = temp__4126__auto___9301;if(cljs.core.chunked_seq_QMARK_.call(null,seq__9280_9302__$1))
{var c__4298__auto___9303 = cljs.core.chunk_first.call(null,seq__9280_9302__$1);{
var G__9304 = cljs.core.chunk_rest.call(null,seq__9280_9302__$1);
var G__9305 = c__4298__auto___9303;
var G__9306 = cljs.core.count.call(null,c__4298__auto___9303);
var G__9307 = (0);
seq__9280_9292 = G__9304;
chunk__9281_9293 = G__9305;
count__9282_9294 = G__9306;
i__9283_9295 = G__9307;
continue;
}
} else
{var w_9308 = cljs.core.first.call(null,seq__9280_9302__$1);if(cljs.core.contains_QMARK_.call(null,self__.watching,w_9308))
{} else
{cljs.core.add_watch.call(null,w_9308,this$__$1,reagent.ratom._handle_change);
}
{
var G__9309 = cljs.core.next.call(null,seq__9280_9302__$1);
var G__9310 = null;
var G__9311 = (0);
var G__9312 = (0);
seq__9280_9292 = G__9309;
chunk__9281_9293 = G__9310;
count__9282_9294 = G__9311;
i__9283_9295 = G__9312;
continue;
}
}
} else
{}
}
break;
}
var seq__9284_9313 = cljs.core.seq.call(null,self__.watching);var chunk__9285_9314 = null;var count__9286_9315 = (0);var i__9287_9316 = (0);while(true){
if((i__9287_9316 < count__9286_9315))
{var w_9317 = cljs.core._nth.call(null,chunk__9285_9314,i__9287_9316);if(cljs.core.contains_QMARK_.call(null,derefed,w_9317))
{} else
{cljs.core.remove_watch.call(null,w_9317,this$__$1);
}
{
var G__9318 = seq__9284_9313;
var G__9319 = chunk__9285_9314;
var G__9320 = count__9286_9315;
var G__9321 = (i__9287_9316 + (1));
seq__9284_9313 = G__9318;
chunk__9285_9314 = G__9319;
count__9286_9315 = G__9320;
i__9287_9316 = G__9321;
continue;
}
} else
{var temp__4126__auto___9322 = cljs.core.seq.call(null,seq__9284_9313);if(temp__4126__auto___9322)
{var seq__9284_9323__$1 = temp__4126__auto___9322;if(cljs.core.chunked_seq_QMARK_.call(null,seq__9284_9323__$1))
{var c__4298__auto___9324 = cljs.core.chunk_first.call(null,seq__9284_9323__$1);{
var G__9325 = cljs.core.chunk_rest.call(null,seq__9284_9323__$1);
var G__9326 = c__4298__auto___9324;
var G__9327 = cljs.core.count.call(null,c__4298__auto___9324);
var G__9328 = (0);
seq__9284_9313 = G__9325;
chunk__9285_9314 = G__9326;
count__9286_9315 = G__9327;
i__9287_9316 = G__9328;
continue;
}
} else
{var w_9329 = cljs.core.first.call(null,seq__9284_9323__$1);if(cljs.core.contains_QMARK_.call(null,derefed,w_9329))
{} else
{cljs.core.remove_watch.call(null,w_9329,this$__$1);
}
{
var G__9330 = cljs.core.next.call(null,seq__9284_9323__$1);
var G__9331 = null;
var G__9332 = (0);
var G__9333 = (0);
seq__9284_9313 = G__9330;
chunk__9285_9314 = G__9331;
count__9286_9315 = G__9332;
i__9287_9316 = G__9333;
continue;
}
}
} else
{}
}
break;
}
return self__.watching = derefed;
});
reagent.ratom.Reaction.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this$,writer,opts){var self__ = this;
var this$__$1 = this;cljs.core._write.call(null,writer,("#<Reaction "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.hash.call(null,this$__$1))+": "));
cljs.core.pr_writer.call(null,self__.state,writer,opts);
return cljs.core._write.call(null,writer,">");
});
reagent.ratom.Reaction.prototype.cljs$core$IHash$_hash$arity$1 = (function (this$){var self__ = this;
var this$__$1 = this;return goog.getUid(this$__$1);
});
reagent.ratom.Reaction.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (o,other){var self__ = this;
var o__$1 = this;return (o__$1 === other);
});
reagent.ratom.Reaction.prototype.reagent$ratom$IDisposable$ = true;
reagent.ratom.Reaction.prototype.reagent$ratom$IDisposable$dispose_BANG_$arity$1 = (function (this$){var self__ = this;
var this$__$1 = this;var seq__9288_9334 = cljs.core.seq.call(null,self__.watching);var chunk__9289_9335 = null;var count__9290_9336 = (0);var i__9291_9337 = (0);while(true){
if((i__9291_9337 < count__9290_9336))
{var w_9338 = cljs.core._nth.call(null,chunk__9289_9335,i__9291_9337);cljs.core.remove_watch.call(null,w_9338,this$__$1);
{
var G__9339 = seq__9288_9334;
var G__9340 = chunk__9289_9335;
var G__9341 = count__9290_9336;
var G__9342 = (i__9291_9337 + (1));
seq__9288_9334 = G__9339;
chunk__9289_9335 = G__9340;
count__9290_9336 = G__9341;
i__9291_9337 = G__9342;
continue;
}
} else
{var temp__4126__auto___9343 = cljs.core.seq.call(null,seq__9288_9334);if(temp__4126__auto___9343)
{var seq__9288_9344__$1 = temp__4126__auto___9343;if(cljs.core.chunked_seq_QMARK_.call(null,seq__9288_9344__$1))
{var c__4298__auto___9345 = cljs.core.chunk_first.call(null,seq__9288_9344__$1);{
var G__9346 = cljs.core.chunk_rest.call(null,seq__9288_9344__$1);
var G__9347 = c__4298__auto___9345;
var G__9348 = cljs.core.count.call(null,c__4298__auto___9345);
var G__9349 = (0);
seq__9288_9334 = G__9346;
chunk__9289_9335 = G__9347;
count__9290_9336 = G__9348;
i__9291_9337 = G__9349;
continue;
}
} else
{var w_9350 = cljs.core.first.call(null,seq__9288_9344__$1);cljs.core.remove_watch.call(null,w_9350,this$__$1);
{
var G__9351 = cljs.core.next.call(null,seq__9288_9344__$1);
var G__9352 = null;
var G__9353 = (0);
var G__9354 = (0);
seq__9288_9334 = G__9351;
chunk__9289_9335 = G__9352;
count__9290_9336 = G__9353;
i__9291_9337 = G__9354;
continue;
}
}
} else
{}
}
break;
}
self__.watching = cljs.core.PersistentHashSet.EMPTY;
self__.state = null;
self__.dirty_QMARK_ = true;
if(cljs.core.truth_(self__.active_QMARK_))
{if(cljs.core.truth_(reagent.ratom.debug))
{cljs.core.swap_BANG_.call(null,reagent.ratom._running,cljs.core.dec);
} else
{}
self__.active_QMARK_ = false;
} else
{}
if(cljs.core.truth_(self__.on_dispose))
{return self__.on_dispose.call(null);
} else
{return null;
}
});
reagent.ratom.Reaction.prototype.cljs$core$IReset$_reset_BANG_$arity$2 = (function (a,new_value){var self__ = this;
var a__$1 = this;var old_value = self__.state;self__.state = new_value;
cljs.core._notify_watches.call(null,a__$1,old_value,new_value);
return new_value;
});
reagent.ratom.Reaction.prototype.cljs$core$ISwap$_swap_BANG_$arity$2 = (function (a,f__$1){var self__ = this;
var a__$1 = this;return cljs.core._reset_BANG_.call(null,a__$1,f__$1.call(null,self__.state));
});
reagent.ratom.Reaction.prototype.cljs$core$ISwap$_swap_BANG_$arity$3 = (function (a,f__$1,x){var self__ = this;
var a__$1 = this;return cljs.core._reset_BANG_.call(null,a__$1,f__$1.call(null,self__.state,x));
});
reagent.ratom.Reaction.prototype.cljs$core$ISwap$_swap_BANG_$arity$4 = (function (a,f__$1,x,y){var self__ = this;
var a__$1 = this;return cljs.core._reset_BANG_.call(null,a__$1,f__$1.call(null,self__.state,x,y));
});
reagent.ratom.Reaction.prototype.cljs$core$ISwap$_swap_BANG_$arity$5 = (function (a,f__$1,x,y,more){var self__ = this;
var a__$1 = this;return cljs.core._reset_BANG_.call(null,a__$1,cljs.core.apply.call(null,f__$1,self__.state,x,y,more));
});
reagent.ratom.Reaction.prototype.reagent$ratom$IRunnable$ = true;
reagent.ratom.Reaction.prototype.reagent$ratom$IRunnable$run$arity$1 = (function (this$){var self__ = this;
var this$__$1 = this;var oldstate = self__.state;var res = reagent.ratom.capture_derefed.call(null,self__.f,this$__$1);var derefed = reagent.ratom.captured.call(null,this$__$1);if(cljs.core.not_EQ_.call(null,derefed,self__.watching))
{reagent.ratom._update_watching.call(null,this$__$1,derefed);
} else
{}
if(cljs.core.truth_(self__.active_QMARK_))
{} else
{if(cljs.core.truth_(reagent.ratom.debug))
{cljs.core.swap_BANG_.call(null,reagent.ratom._running,cljs.core.inc);
} else
{}
self__.active_QMARK_ = true;
}
self__.dirty_QMARK_ = false;
self__.state = res;
reagent.ratom.call_watches.call(null,this$__$1,self__.watches,oldstate,self__.state);
return res;
});
reagent.ratom.Reaction.prototype.cljs$core$IWatchable$_notify_watches$arity$3 = (function (this$,oldval,newval){var self__ = this;
var this$__$1 = this;if(cljs.core.truth_(self__.on_set))
{self__.on_set.call(null,oldval,newval);
} else
{}
return reagent.ratom.call_watches.call(null,this$__$1,self__.watches,oldval,newval);
});
reagent.ratom.Reaction.prototype.cljs$core$IWatchable$_add_watch$arity$3 = (function (this$,k,wf){var self__ = this;
var this$__$1 = this;return self__.watches = cljs.core.assoc.call(null,self__.watches,k,wf);
});
reagent.ratom.Reaction.prototype.cljs$core$IWatchable$_remove_watch$arity$2 = (function (this$,k){var self__ = this;
var this$__$1 = this;self__.watches = cljs.core.dissoc.call(null,self__.watches,k);
if(cljs.core.empty_QMARK_.call(null,self__.watches))
{return reagent.ratom.dispose_BANG_.call(null,this$__$1);
} else
{return null;
}
});
reagent.ratom.Reaction.prototype.cljs$core$IDeref$_deref$arity$1 = (function (this$){var self__ = this;
var this$__$1 = this;if(cljs.core.not.call(null,(function (){var or__3542__auto__ = self__.auto_run;if(cljs.core.truth_(or__3542__auto__))
{return or__3542__auto__;
} else
{return reagent.ratom._STAR_ratom_context_STAR_;
}
})()))
{var x__4998__auto___9355 = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [self__.auto_run,reagent.ratom._STAR_ratom_context_STAR_], null);if(!((console.log == null)))
{console.log((''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(("dbg reagent.ratom:"+177+": [auto-run *ratom-context*]: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,x__4998__auto___9355))))));
} else
{}
} else
{}
if(cljs.core.truth_((function (){var or__3542__auto__ = self__.auto_run;if(cljs.core.truth_(or__3542__auto__))
{return or__3542__auto__;
} else
{return reagent.ratom._STAR_ratom_context_STAR_;
}
})()))
{} else
{throw (new Error(("Assert failed: Reaction derefed outside auto-running context\n"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,cljs.core.list(new cljs.core.Symbol(null,"or","or",1876275696,null),new cljs.core.Symbol(null,"auto-run","auto-run",-696035332,null),new cljs.core.Symbol(null,"*ratom-context*","*ratom-context*",-1557728360,null)))))));
}
reagent.ratom.notify_deref_watcher_BANG_.call(null,this$__$1);
if(cljs.core.truth_(self__.dirty_QMARK_))
{return reagent.ratom.run.call(null,this$__$1);
} else
{return self__.state;
}
});
reagent.ratom.__GT_Reaction = (function __GT_Reaction(f,state,dirty_QMARK_,active_QMARK_,watching,watches,auto_run,on_set,on_dispose){return (new reagent.ratom.Reaction(f,state,dirty_QMARK_,active_QMARK_,watching,watches,auto_run,on_set,on_dispose));
});
/**
* @param {...*} var_args
*/
reagent.ratom.make_reaction = (function() { 
var make_reaction__delegate = function (f,p__9356){var map__9358 = p__9356;var map__9358__$1 = ((cljs.core.seq_QMARK_.call(null,map__9358))?cljs.core.apply.call(null,cljs.core.hash_map,map__9358):map__9358);var derefed = cljs.core.get.call(null,map__9358__$1,new cljs.core.Keyword(null,"derefed","derefed",590684583));var on_dispose = cljs.core.get.call(null,map__9358__$1,new cljs.core.Keyword(null,"on-dispose","on-dispose",2105306360));var on_set = cljs.core.get.call(null,map__9358__$1,new cljs.core.Keyword(null,"on-set","on-set",-140953470));var auto_run = cljs.core.get.call(null,map__9358__$1,new cljs.core.Keyword(null,"auto-run","auto-run",1958400437));var runner = ((cljs.core._EQ_.call(null,auto_run,true))?reagent.ratom.run:auto_run);var active = !((derefed == null));var dirty = !(active);var reaction = (new reagent.ratom.Reaction(f,null,dirty,active,null,cljs.core.PersistentArrayMap.EMPTY,runner,on_set,on_dispose));if((derefed == null))
{} else
{if(cljs.core.truth_(reagent.ratom.debug))
{cljs.core.swap_BANG_.call(null,reagent.ratom._running,cljs.core.inc);
} else
{}
reagent.ratom._update_watching.call(null,reaction,derefed);
}
return reaction;
};
var make_reaction = function (f,var_args){
var p__9356 = null;if (arguments.length > 1) {
  p__9356 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 1),0);} 
return make_reaction__delegate.call(this,f,p__9356);};
make_reaction.cljs$lang$maxFixedArity = 1;
make_reaction.cljs$lang$applyTo = (function (arglist__9359){
var f = cljs.core.first(arglist__9359);
var p__9356 = cljs.core.rest(arglist__9359);
return make_reaction__delegate(f,p__9356);
});
make_reaction.cljs$core$IFn$_invoke$arity$variadic = make_reaction__delegate;
return make_reaction;
})()
;

//# sourceMappingURL=ratom.js.map
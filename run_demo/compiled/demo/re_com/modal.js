// Compiled by ClojureScript 0.0-2280
goog.provide('re_com.modal');
goog.require('cljs.core');
goog.require('re_com.core');
goog.require('cljs.core.async');
goog.require('goog.events');
goog.require('goog.events');
goog.require('reagent.core');
goog.require('reagent.core');
goog.require('re_com.core');
goog.require('re_com.util');
goog.require('re_com.util');
goog.require('cljs.core.async');
goog.require('cljs.core.async');
re_com.modal.cancel_button = (function cancel_button(callback){return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"display","display",242065432),"flex"], null)], null),new cljs.core.PersistentVector(null, 7, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.core.button,"Cancel",callback,new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"margin","margin",-995903681),"auto"], null),new cljs.core.Keyword(null,"class","class",-2030961996),"btn-info"], null)], null);
});
/**
* @param {...*} var_args
*/
re_com.modal.modal_window = (function() { 
var modal_window__delegate = function (p__8793){var map__8795 = p__8793;var map__8795__$1 = ((cljs.core.seq_QMARK_.call(null,map__8795))?cljs.core.apply.call(null,cljs.core.hash_map,map__8795):map__8795);var markup = cljs.core.get.call(null,map__8795__$1,new cljs.core.Keyword(null,"markup","markup",2143234544));return ((function (map__8795,map__8795__$1,markup){
return (function (){return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"top","top",-1856271961),new cljs.core.Keyword(null,"background-color","background-color",570434026),new cljs.core.Keyword(null,"width","width",-384071477),new cljs.core.Keyword(null,"on-click","on-click",1632826543),new cljs.core.Keyword(null,"z-index","z-index",1892827090),new cljs.core.Keyword(null,"display","display",242065432),new cljs.core.Keyword(null,"position","position",-2011731912),new cljs.core.Keyword(null,"height","height",1025178622),new cljs.core.Keyword(null,"left","left",-399115937)],["0px","rgba(0,0,0,0.85)","100%",((function (map__8795,map__8795__$1,markup){
return (function (){return re_com.util.console_log.call(null,"clicked backdrop");
});})(map__8795,map__8795__$1,markup))
,(1020),"flex","fixed","100%","0px"])], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"margin","margin",-995903681),"auto",new cljs.core.Keyword(null,"background-color","background-color",570434026),"white",new cljs.core.Keyword(null,"padding","padding",1660304693),"16px",new cljs.core.Keyword(null,"border-radius","border-radius",419594011),"6px",new cljs.core.Keyword(null,"z-index","z-index",1892827090),(1020)], null)], null),markup], null)], null);
});
;})(map__8795,map__8795__$1,markup))
};
var modal_window = function (var_args){
var p__8793 = null;if (arguments.length > 0) {
  p__8793 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);} 
return modal_window__delegate.call(this,p__8793);};
modal_window.cljs$lang$maxFixedArity = 0;
modal_window.cljs$lang$applyTo = (function (arglist__8796){
var p__8793 = cljs.core.seq(arglist__8796);
return modal_window__delegate(p__8793);
});
modal_window.cljs$core$IFn$_invoke$arity$variadic = modal_window__delegate;
return modal_window;
})()
;
/**
* @param {...*} var_args
*/
re_com.modal.looper = (function() { 
var looper__delegate = function (p__8797){var map__8839 = p__8797;var map__8839__$1 = ((cljs.core.seq_QMARK_.call(null,map__8839))?cljs.core.apply.call(null,cljs.core.hash_map,map__8839):map__8839);var when_done = cljs.core.get.call(null,map__8839__$1,new cljs.core.Keyword(null,"when-done","when-done",889619364));var func = cljs.core.get.call(null,map__8839__$1,new cljs.core.Keyword(null,"func","func",-238706040));var initial_value = cljs.core.get.call(null,map__8839__$1,new cljs.core.Keyword(null,"initial-value","initial-value",470619381));var c__6352__auto__ = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto__,map__8839,map__8839__$1,when_done,func,initial_value){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto__,map__8839,map__8839__$1,when_done,func,initial_value){
return (function (state_8862){var state_val_8863 = (state_8862[(1)]);if((state_val_8863 === (8)))
{var inst_8849 = (state_8862[(7)]);var inst_8853 = (state_8862[(2)]);var inst_8843 = inst_8853;var inst_8844 = inst_8849;var state_8862__$1 = (function (){var statearr_8864 = state_8862;(statearr_8864[(8)] = inst_8844);
(statearr_8864[(9)] = inst_8843);
return statearr_8864;
})();var statearr_8865_8880 = state_8862__$1;(statearr_8865_8880[(2)] = null);
(statearr_8865_8880[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_8863 === (7)))
{var inst_8858 = (state_8862[(2)]);var state_8862__$1 = state_8862;var statearr_8866_8881 = state_8862__$1;(statearr_8866_8881[(2)] = inst_8858);
(statearr_8866_8881[(1)] = (4));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_8863 === (6)))
{var inst_8849 = (state_8862[(7)]);var inst_8856 = when_done.call(null,inst_8849);var state_8862__$1 = state_8862;var statearr_8867_8882 = state_8862__$1;(statearr_8867_8882[(2)] = inst_8856);
(statearr_8867_8882[(1)] = (7));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_8863 === (5)))
{var inst_8851 = cljs.core.async.timeout.call(null,(20));var state_8862__$1 = state_8862;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_8862__$1,(8),inst_8851);
} else
{if((state_val_8863 === (4)))
{var inst_8860 = (state_8862[(2)]);var state_8862__$1 = state_8862;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_8862__$1,inst_8860);
} else
{if((state_val_8863 === (3)))
{var inst_8844 = (state_8862[(8)]);var inst_8847 = func.call(null,inst_8844);var inst_8848 = cljs.core.nth.call(null,inst_8847,(0),null);var inst_8849 = cljs.core.nth.call(null,inst_8847,(1),null);var state_8862__$1 = (function (){var statearr_8868 = state_8862;(statearr_8868[(7)] = inst_8849);
return statearr_8868;
})();if(cljs.core.truth_(inst_8848))
{var statearr_8869_8883 = state_8862__$1;(statearr_8869_8883[(1)] = (5));
} else
{var statearr_8870_8884 = state_8862__$1;(statearr_8870_8884[(1)] = (6));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_8863 === (2)))
{var inst_8842 = (state_8862[(2)]);var inst_8843 = inst_8842;var inst_8844 = initial_value;var state_8862__$1 = (function (){var statearr_8871 = state_8862;(statearr_8871[(8)] = inst_8844);
(statearr_8871[(9)] = inst_8843);
return statearr_8871;
})();var statearr_8872_8885 = state_8862__$1;(statearr_8872_8885[(2)] = null);
(statearr_8872_8885[(1)] = (3));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_8863 === (1)))
{var inst_8840 = cljs.core.async.timeout.call(null,(20));var state_8862__$1 = state_8862;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_8862__$1,(2),inst_8840);
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
});})(c__6352__auto__,map__8839,map__8839__$1,when_done,func,initial_value))
;return ((function (switch__6337__auto__,c__6352__auto__,map__8839,map__8839__$1,when_done,func,initial_value){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_8876 = [null,null,null,null,null,null,null,null,null,null];(statearr_8876[(0)] = state_machine__6338__auto__);
(statearr_8876[(1)] = (1));
return statearr_8876;
});
var state_machine__6338__auto____1 = (function (state_8862){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_8862);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e8877){if((e8877 instanceof Object))
{var ex__6341__auto__ = e8877;var statearr_8878_8886 = state_8862;(statearr_8878_8886[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_8862);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e8877;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__8887 = state_8862;
state_8862 = G__8887;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_8862){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_8862);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto__,map__8839,map__8839__$1,when_done,func,initial_value))
})();var state__6354__auto__ = (function (){var statearr_8879 = f__6353__auto__.call(null);(statearr_8879[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto__);
return statearr_8879;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto__,map__8839,map__8839__$1,when_done,func,initial_value))
);
return c__6352__auto__;
};
var looper = function (var_args){
var p__8797 = null;if (arguments.length > 0) {
  p__8797 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);} 
return looper__delegate.call(this,p__8797);};
looper.cljs$lang$maxFixedArity = 0;
looper.cljs$lang$applyTo = (function (arglist__8888){
var p__8797 = cljs.core.seq(arglist__8888);
return looper__delegate(p__8797);
});
looper.cljs$core$IFn$_invoke$arity$variadic = looper__delegate;
return looper;
})()
;
re_com.modal.domino_step = (function domino_step(continue_fn_QMARK_,in_chan,func){var out_chan = cljs.core.async.chan.call(null);var c__6352__auto___8967 = cljs.core.async.chan.call(null,(1));cljs.core.async.impl.dispatch.run.call(null,((function (c__6352__auto___8967,out_chan){
return (function (){var f__6353__auto__ = (function (){var switch__6337__auto__ = ((function (c__6352__auto___8967,out_chan){
return (function (state_8947){var state_val_8948 = (state_8947[(1)]);if((state_val_8948 === (7)))
{var inst_8945 = (state_8947[(2)]);var state_8947__$1 = state_8947;return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_8947__$1,inst_8945);
} else
{if((state_val_8948 === (1)))
{var state_8947__$1 = state_8947;return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_8947__$1,(2),in_chan);
} else
{if((state_val_8948 === (4)))
{var inst_8929 = (state_8947[(7)]);var inst_8935 = func.call(null,inst_8929);var state_8947__$1 = state_8947;var statearr_8949_8968 = state_8947__$1;(statearr_8949_8968[(2)] = inst_8935);
(statearr_8949_8968[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_8948 === (6)))
{var inst_8938 = (state_8947[(8)]);var inst_8938__$1 = (state_8947[(2)]);var inst_8939 = (inst_8938__$1 == null);var state_8947__$1 = (function (){var statearr_8950 = state_8947;(statearr_8950[(8)] = inst_8938__$1);
return statearr_8950;
})();if(cljs.core.truth_(inst_8939))
{var statearr_8951_8969 = state_8947__$1;(statearr_8951_8969[(1)] = (8));
} else
{var statearr_8952_8970 = state_8947__$1;(statearr_8952_8970[(1)] = (9));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_8948 === (3)))
{var inst_8932 = (state_8947[(2)]);var inst_8933 = continue_fn_QMARK_.call(null);var state_8947__$1 = (function (){var statearr_8953 = state_8947;(statearr_8953[(9)] = inst_8932);
return statearr_8953;
})();if(cljs.core.truth_(inst_8933))
{var statearr_8954_8971 = state_8947__$1;(statearr_8954_8971[(1)] = (4));
} else
{var statearr_8955_8972 = state_8947__$1;(statearr_8955_8972[(1)] = (5));
}
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_8948 === (2)))
{var inst_8929 = (state_8947[(2)]);var inst_8930 = cljs.core.async.timeout.call(null,(20));var state_8947__$1 = (function (){var statearr_8956 = state_8947;(statearr_8956[(7)] = inst_8929);
return statearr_8956;
})();return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_8947__$1,(3),inst_8930);
} else
{if((state_val_8948 === (9)))
{var inst_8938 = (state_8947[(8)]);var state_8947__$1 = state_8947;var statearr_8957_8973 = state_8947__$1;(statearr_8957_8973[(2)] = inst_8938);
(statearr_8957_8973[(1)] = (10));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_8948 === (5)))
{var inst_8929 = (state_8947[(7)]);var state_8947__$1 = state_8947;var statearr_8958_8974 = state_8947__$1;(statearr_8958_8974[(2)] = inst_8929);
(statearr_8958_8974[(1)] = (6));
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if((state_val_8948 === (10)))
{var inst_8943 = (state_8947[(2)]);var state_8947__$1 = state_8947;return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_8947__$1,(7),out_chan,inst_8943);
} else
{if((state_val_8948 === (8)))
{var inst_8929 = (state_8947[(7)]);var state_8947__$1 = state_8947;var statearr_8959_8975 = state_8947__$1;(statearr_8959_8975[(2)] = inst_8929);
(statearr_8959_8975[(1)] = (10));
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
});})(c__6352__auto___8967,out_chan))
;return ((function (switch__6337__auto__,c__6352__auto___8967,out_chan){
return (function() {
var state_machine__6338__auto__ = null;
var state_machine__6338__auto____0 = (function (){var statearr_8963 = [null,null,null,null,null,null,null,null,null,null];(statearr_8963[(0)] = state_machine__6338__auto__);
(statearr_8963[(1)] = (1));
return statearr_8963;
});
var state_machine__6338__auto____1 = (function (state_8947){while(true){
var ret_value__6339__auto__ = (function (){try{while(true){
var result__6340__auto__ = switch__6337__auto__.call(null,state_8947);if(cljs.core.keyword_identical_QMARK_.call(null,result__6340__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
continue;
}
} else
{return result__6340__auto__;
}
break;
}
}catch (e8964){if((e8964 instanceof Object))
{var ex__6341__auto__ = e8964;var statearr_8965_8976 = state_8947;(statearr_8965_8976[(5)] = ex__6341__auto__);
cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_8947);
return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{throw e8964;
} else
{return null;
}
}
}})();if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__6339__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268)))
{{
var G__8977 = state_8947;
state_8947 = G__8977;
continue;
}
} else
{return ret_value__6339__auto__;
}
break;
}
});
state_machine__6338__auto__ = function(state_8947){
switch(arguments.length){
case 0:
return state_machine__6338__auto____0.call(this);
case 1:
return state_machine__6338__auto____1.call(this,state_8947);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$0 = state_machine__6338__auto____0;
state_machine__6338__auto__.cljs$core$IFn$_invoke$arity$1 = state_machine__6338__auto____1;
return state_machine__6338__auto__;
})()
;})(switch__6337__auto__,c__6352__auto___8967,out_chan))
})();var state__6354__auto__ = (function (){var statearr_8966 = f__6353__auto__.call(null);(statearr_8966[cljs.core.async.impl.ioc_helpers.USER_START_IDX] = c__6352__auto___8967);
return statearr_8966;
})();return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__6354__auto__);
});})(c__6352__auto___8967,out_chan))
);
return out_chan;
});
re_com.modal.domino_process = (function() {
var domino_process = null;
var domino_process__2 = (function (initial_value,funcs){return domino_process.call(null,initial_value,cljs.core.atom.call(null,true),funcs);
});
var domino_process__3 = (function (initial_value,continue_QMARK_,funcs){if(cljs.core.truth_(cljs.core.complement.call(null,cljs.core.nil_QMARK_).call(null,initial_value)))
{} else
{throw (new Error(("Assert failed: Initial value can't be nil because that causes channel problems\n"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,cljs.core.list(cljs.core.list(new cljs.core.Symbol(null,"complement","complement",-913606051,null),new cljs.core.Symbol(null,"nil?","nil?",1612038930,null)),new cljs.core.Symbol(null,"initial-value","initial-value",2111150908,null)))))));
}
var continue_fn_QMARK_ = (function (){return cljs.core.deref.call(null,continue_QMARK_);
});var in_chan = cljs.core.async.chan.call(null);var out_chan = cljs.core.reduce.call(null,cljs.core.partial.call(null,re_com.modal.domino_step,continue_fn_QMARK_),in_chan,funcs);cljs.core.async.put_BANG_.call(null,in_chan,initial_value);
return out_chan;
});
domino_process = function(initial_value,continue_QMARK_,funcs){
switch(arguments.length){
case 2:
return domino_process__2.call(this,initial_value,continue_QMARK_);
case 3:
return domino_process__3.call(this,initial_value,continue_QMARK_,funcs);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
domino_process.cljs$core$IFn$_invoke$arity$2 = domino_process__2;
domino_process.cljs$core$IFn$_invoke$arity$3 = domino_process__3;
return domino_process;
})()
;

//# sourceMappingURL=modal.js.map
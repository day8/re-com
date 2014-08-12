// Compiled by ClojureScript 0.0-2280
goog.provide('re_com.core');
goog.require('cljs.core');
goog.require('re_com.util');
goog.require('re_com.util');
goog.require('reagent.core');
goog.require('reagent.core');
/**
* @param {...*} var_args
*/
re_com.core.label = (function() { 
var label__delegate = function (text,p__5106){var map__5108 = p__5106;var map__5108__$1 = ((cljs.core.seq_QMARK_.call(null,map__5108))?cljs.core.apply.call(null,cljs.core.hash_map,map__5108):map__5108);var class$ = cljs.core.get.call(null,map__5108__$1,new cljs.core.Keyword(null,"class","class",-2030961996));var style = cljs.core.get.call(null,map__5108__$1,new cljs.core.Keyword(null,"style","style",-496642736));return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"class","class",-2030961996),class$,new cljs.core.Keyword(null,"style","style",-496642736),style], null),text], null);
};
var label = function (text,var_args){
var p__5106 = null;if (arguments.length > 1) {
  p__5106 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 1),0);} 
return label__delegate.call(this,text,p__5106);};
label.cljs$lang$maxFixedArity = 1;
label.cljs$lang$applyTo = (function (arglist__5109){
var text = cljs.core.first(arglist__5109);
var p__5106 = cljs.core.rest(arglist__5109);
return label__delegate(text,p__5106);
});
label.cljs$core$IFn$_invoke$arity$variadic = label__delegate;
return label;
})()
;
/**
* @param {...*} var_args
*/
re_com.core.input_text = (function() { 
var input_text__delegate = function (text,callback,p__5110){var map__5112 = p__5110;var map__5112__$1 = ((cljs.core.seq_QMARK_.call(null,map__5112))?cljs.core.apply.call(null,cljs.core.hash_map,map__5112):map__5112);var class$ = cljs.core.get.call(null,map__5112__$1,new cljs.core.Keyword(null,"class","class",-2030961996));var style = cljs.core.get.call(null,map__5112__$1,new cljs.core.Keyword(null,"style","style",-496642736));return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"type","type",1174270348),"text",new cljs.core.Keyword(null,"class","class",-2030961996),class$,new cljs.core.Keyword(null,"style","style",-496642736),style,new cljs.core.Keyword(null,"value","value",305978217),text,new cljs.core.Keyword(null,"on-change","on-change",-732046149),((function (map__5112,map__5112__$1,class$,style){
return (function (){return callback.call(null);
});})(map__5112,map__5112__$1,class$,style))
], null)], null);
};
var input_text = function (text,callback,var_args){
var p__5110 = null;if (arguments.length > 2) {
  p__5110 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 2),0);} 
return input_text__delegate.call(this,text,callback,p__5110);};
input_text.cljs$lang$maxFixedArity = 2;
input_text.cljs$lang$applyTo = (function (arglist__5113){
var text = cljs.core.first(arglist__5113);
arglist__5113 = cljs.core.next(arglist__5113);
var callback = cljs.core.first(arglist__5113);
var p__5110 = cljs.core.rest(arglist__5113);
return input_text__delegate(text,callback,p__5110);
});
input_text.cljs$core$IFn$_invoke$arity$variadic = input_text__delegate;
return input_text;
})()
;
/**
* @param {...*} var_args
*/
re_com.core.button = (function() { 
var button__delegate = function (text,callback,p__5114){var map__5116 = p__5114;var map__5116__$1 = ((cljs.core.seq_QMARK_.call(null,map__5116))?cljs.core.apply.call(null,cljs.core.hash_map,map__5116):map__5116);var class$ = cljs.core.get.call(null,map__5116__$1,new cljs.core.Keyword(null,"class","class",-2030961996),"btn-default");var style = cljs.core.get.call(null,map__5116__$1,new cljs.core.Keyword(null,"style","style",-496642736));return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"button","button",1456579943),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"class","class",-2030961996),("btn "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(class$)),new cljs.core.Keyword(null,"style","style",-496642736),style,new cljs.core.Keyword(null,"on-click","on-click",1632826543),((function (map__5116,map__5116__$1,class$,style){
return (function (){return callback.call(null);
});})(map__5116,map__5116__$1,class$,style))
], null),text], null);
};
var button = function (text,callback,var_args){
var p__5114 = null;if (arguments.length > 2) {
  p__5114 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 2),0);} 
return button__delegate.call(this,text,callback,p__5114);};
button.cljs$lang$maxFixedArity = 2;
button.cljs$lang$applyTo = (function (arglist__5117){
var text = cljs.core.first(arglist__5117);
arglist__5117 = cljs.core.next(arglist__5117);
var callback = cljs.core.first(arglist__5117);
var p__5114 = cljs.core.rest(arglist__5117);
return button__delegate(text,callback,p__5114);
});
button.cljs$core$IFn$_invoke$arity$variadic = button__delegate;
return button;
})()
;
/**
* @param {...*} var_args
*/
re_com.core.checkbox = (function() { 
var checkbox__delegate = function (text,callback,p__5119){var map__5121 = p__5119;var map__5121__$1 = ((cljs.core.seq_QMARK_.call(null,map__5121))?cljs.core.apply.call(null,cljs.core.hash_map,map__5121):map__5121);var class$ = cljs.core.get.call(null,map__5121__$1,new cljs.core.Keyword(null,"class","class",-2030961996));var style = cljs.core.get.call(null,map__5121__$1,new cljs.core.Keyword(null,"style","style",-496642736));return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"type","type",1174270348),"checkbox",new cljs.core.Keyword(null,"class","class",-2030961996),("btn "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(class$)),new cljs.core.Keyword(null,"style","style",-496642736),style,new cljs.core.Keyword(null,"value","value",305978217),text,new cljs.core.Keyword(null,"on-click","on-click",1632826543),((function (map__5121,map__5121__$1,class$,style){
return (function (){return callback.call(null);
});})(map__5121,map__5121__$1,class$,style))
], null)], null);
};
var checkbox = function (text,callback,var_args){
var p__5119 = null;if (arguments.length > 2) {
  p__5119 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 2),0);} 
return checkbox__delegate.call(this,text,callback,p__5119);};
checkbox.cljs$lang$maxFixedArity = 2;
checkbox.cljs$lang$applyTo = (function (arglist__5122){
var text = cljs.core.first(arglist__5122);
arglist__5122 = cljs.core.next(arglist__5122);
var callback = cljs.core.first(arglist__5122);
var p__5119 = cljs.core.rest(arglist__5122);
return checkbox__delegate(text,callback,p__5119);
});
checkbox.cljs$core$IFn$_invoke$arity$variadic = checkbox__delegate;
return checkbox;
})()
;
/**
* @param {...*} var_args
*/
re_com.core.radio_button = (function() { 
var radio_button__delegate = function (text,callback,p__5124){var map__5126 = p__5124;var map__5126__$1 = ((cljs.core.seq_QMARK_.call(null,map__5126))?cljs.core.apply.call(null,cljs.core.hash_map,map__5126):map__5126);var class$ = cljs.core.get.call(null,map__5126__$1,new cljs.core.Keyword(null,"class","class",-2030961996));var style = cljs.core.get.call(null,map__5126__$1,new cljs.core.Keyword(null,"style","style",-496642736));return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"type","type",1174270348),"radio",new cljs.core.Keyword(null,"class","class",-2030961996),("btn "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(class$)),new cljs.core.Keyword(null,"style","style",-496642736),style,new cljs.core.Keyword(null,"value","value",305978217),text,new cljs.core.Keyword(null,"on-click","on-click",1632826543),((function (map__5126,map__5126__$1,class$,style){
return (function (){return callback.call(null);
});})(map__5126,map__5126__$1,class$,style))
], null)], null);
};
var radio_button = function (text,callback,var_args){
var p__5124 = null;if (arguments.length > 2) {
  p__5124 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 2),0);} 
return radio_button__delegate.call(this,text,callback,p__5124);};
radio_button.cljs$lang$maxFixedArity = 2;
radio_button.cljs$lang$applyTo = (function (arglist__5127){
var text = cljs.core.first(arglist__5127);
arglist__5127 = cljs.core.next(arglist__5127);
var callback = cljs.core.first(arglist__5127);
var p__5124 = cljs.core.rest(arglist__5127);
return radio_button__delegate(text,callback,p__5124);
});
radio_button.cljs$core$IFn$_invoke$arity$variadic = radio_button__delegate;
return radio_button;
})()
;
re_com.core.spinner = (function spinner(){return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"display","display",242065432),"flex",new cljs.core.Keyword(null,"margin","margin",-995903681),"10px"], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"img","img",1442687358),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"src","src",-1651076051),"img/spinner.gif",new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"margin","margin",-995903681),"auto"], null)], null)], null)], null);
});
re_com.core.progress_bar = (function progress_bar(progress_percent){return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.progress","div.progress",169531213),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.progress-bar","div.progress-bar",929518721),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"role","role",-736691072),"progressbar",new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"width","width",-384071477),(''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,progress_percent))+"%"),new cljs.core.Keyword(null,"transition","transition",765692007),"none"], null)], null),(''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,progress_percent))+"%")], null)], null);
});

//# sourceMappingURL=core.js.map
// Compiled by ClojureScript 0.0-2280
goog.provide('reagent.impl.template');
goog.require('cljs.core');
goog.require('reagent.impl.util');
goog.require('reagent.impl.util');
goog.require('reagent.impl.component');
goog.require('reagent.ratom');
goog.require('reagent.impl.batching');
goog.require('reagent.impl.component');
goog.require('reagent.impl.util');
goog.require('reagent.ratom');
goog.require('clojure.string');
goog.require('reagent.impl.batching');
goog.require('reagent.debug');
goog.require('clojure.string');
/**
* Regular expression that parses a CSS-style id and class
* from a tag name.
*/
reagent.impl.template.re_tag = /([^\s\.#]+)(?:#([^\s\.#]+))?(?:\.([^\s#]+))?/;
reagent.impl.template.DOM = (reagent.impl.util.React["DOM"]);
reagent.impl.template.attr_aliases = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"class","class",-2030961996),"className",new cljs.core.Keyword(null,"for","for",-1323786319),"htmlFor",new cljs.core.Keyword(null,"charset","charset",-1063822193),"charSet"], null);
reagent.impl.template.hiccup_tag_QMARK_ = (function hiccup_tag_QMARK_(x){return ((x instanceof cljs.core.Keyword)) || ((x instanceof cljs.core.Symbol)) || (typeof x === 'string');
});
reagent.impl.template.valid_tag_QMARK_ = (function valid_tag_QMARK_(x){return (reagent.impl.template.hiccup_tag_QMARK_.call(null,x)) || (reagent.impl.util.clj_ifn_QMARK_.call(null,x));
});
reagent.impl.template.to_js_val = (function to_js_val(v){if(!(cljs.core.ifn_QMARK_.call(null,v)))
{return v;
} else
{if((v instanceof cljs.core.Keyword))
{return cljs.core.name.call(null,v);
} else
{if((v instanceof cljs.core.Symbol))
{return (''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(v));
} else
{if(cljs.core.coll_QMARK_.call(null,v))
{return cljs.core.clj__GT_js.call(null,v);
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{return (function() { 
var G__9250__delegate = function (args){return cljs.core.apply.call(null,v,args);
};
var G__9250 = function (var_args){
var args = null;if (arguments.length > 0) {
  args = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);} 
return G__9250__delegate.call(this,args);};
G__9250.cljs$lang$maxFixedArity = 0;
G__9250.cljs$lang$applyTo = (function (arglist__9251){
var args = cljs.core.seq(arglist__9251);
return G__9250__delegate(args);
});
G__9250.cljs$core$IFn$_invoke$arity$variadic = G__9250__delegate;
return G__9250;
})()
;
} else
{return null;
}
}
}
}
}
});
reagent.impl.template.undash_prop_name = (function undash_prop_name(n){var or__3542__auto__ = reagent.impl.template.attr_aliases.call(null,n);if(cljs.core.truth_(or__3542__auto__))
{return or__3542__auto__;
} else
{return reagent.impl.util.dash_to_camel.call(null,n);
}
});
reagent.impl.template.cached_prop_name = reagent.impl.util.memoize_1.call(null,reagent.impl.template.undash_prop_name);
reagent.impl.template.cached_style_name = reagent.impl.util.memoize_1.call(null,reagent.impl.util.dash_to_camel);
reagent.impl.template.convert_prop_value = (function convert_prop_value(val){if(cljs.core.map_QMARK_.call(null,val))
{return cljs.core.reduce_kv.call(null,(function (res,k,v){var G__9253 = res;(G__9253[reagent.impl.template.cached_prop_name.call(null,k)] = reagent.impl.template.to_js_val.call(null,v));
return G__9253;
}),{},val);
} else
{return reagent.impl.template.to_js_val.call(null,val);
}
});
reagent.impl.template.set_id_class = (function set_id_class(props,p__9254){var vec__9256 = p__9254;var id = cljs.core.nth.call(null,vec__9256,(0),null);var class$ = cljs.core.nth.call(null,vec__9256,(1),null);var pid = (props["id"]);(props["id"] = ((!((pid == null)))?pid:id));
if((class$ == null))
{return null;
} else
{return (props["className"] = (function (){var old = (props["className"]);if(!((old == null)))
{return (''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(class$)+" "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(old));
} else
{return class$;
}
})());
}
});
reagent.impl.template.convert_props = (function convert_props(props,id_class){if((cljs.core.empty_QMARK_.call(null,props)) && ((id_class == null)))
{return null;
} else
{if((cljs.core.type.call(null,props) === Object))
{return props;
} else
{if(new cljs.core.Keyword(null,"else","else",-1508377146))
{var objprops = cljs.core.reduce_kv.call(null,(function (o,k,v){var pname_9257 = reagent.impl.template.cached_prop_name.call(null,k);if(!((pname_9257 === "key")))
{(o[pname_9257] = reagent.impl.template.convert_prop_value.call(null,v));
} else
{}
return o;
}),{},props);if((id_class == null))
{} else
{reagent.impl.template.set_id_class.call(null,objprops,id_class);
}
return objprops;
} else
{return null;
}
}
}
});
reagent.impl.template.input_handle_change = (function input_handle_change(this$,on_change,e){var res = on_change.call(null,e);reagent.impl.batching.queue_render.call(null,this$);
return res;
});
reagent.impl.template.input_did_update = (function input_did_update(this$){var value = this$.cljsInputValue;if((value == null))
{return null;
} else
{var node = this$.getDOMNode();if(cljs.core.not_EQ_.call(null,value,node.value))
{return node.value = value;
} else
{return null;
}
}
});
reagent.impl.template.input_render_setup = (function input_render_setup(this$,jsprops){var on_change = (jsprops["onChange"]);var value = (((on_change == null))?null:(jsprops["value"]));this$.cljsInputValue = value;
if((value == null))
{return null;
} else
{reagent.impl.batching.mark_rendered.call(null,this$);
var G__9260 = jsprops;(G__9260["defaultValue"] = value);
(G__9260["value"] = null);
(G__9260["onChange"] = ((function (G__9260,on_change,value){
return (function (p1__9258_SHARP_){return reagent.impl.template.input_handle_change.call(null,this$,on_change,p1__9258_SHARP_);
});})(G__9260,on_change,value))
);
return G__9260;
}
});
reagent.impl.template.input_components = cljs.core.PersistentHashSet.fromArray([(reagent.impl.template.DOM["input"]),(reagent.impl.template.DOM["textarea"])], true);
reagent.impl.template.wrapped_render = (function wrapped_render(this$,comp,id_class,input_setup){var inprops = reagent.impl.util.js_props.call(null,this$);var argv = (inprops[reagent.impl.util.cljs_argv]);var props = cljs.core.nth.call(null,argv,(1),null);var hasprops = ((props == null)) || (cljs.core.map_QMARK_.call(null,props));var jsargs = reagent.impl.template.convert_args.call(null,argv,((hasprops)?(2):(1)),((inprops[reagent.impl.util.cljs_level]) + (1)));var jsprops = reagent.impl.template.convert_props.call(null,((hasprops)?props:null),id_class);if((input_setup == null))
{} else
{input_setup.call(null,this$,jsprops);
}
(jsargs[(0)] = jsprops);
return comp.apply(null,jsargs);
});
reagent.impl.template.wrapped_should_update = (function wrapped_should_update(C,nextprops,nextstate){var inprops = reagent.impl.util.js_props.call(null,C);var a1 = (inprops[reagent.impl.util.cljs_argv]);var a2 = (nextprops[reagent.impl.util.cljs_argv]);return cljs.core.not.call(null,reagent.impl.util.equal_args.call(null,a1,a2));
});
reagent.impl.template.add_input_methods = (function add_input_methods(spec){var G__9262 = spec;(G__9262["componentDidUpdate"] = ((function (G__9262){
return (function (){var C = this;return reagent.impl.template.input_did_update.call(null,C);
});})(G__9262))
);
(G__9262["componentWillUnmount"] = ((function (G__9262){
return (function (){var C = this;return reagent.impl.batching.dispose.call(null,C);
});})(G__9262))
);
return G__9262;
});
reagent.impl.template.wrap_component = (function wrap_component(comp,extras,name){var input_QMARK_ = reagent.impl.template.input_components.call(null,comp);var input_setup = (cljs.core.truth_(input_QMARK_)?reagent.impl.template.input_render_setup:null);var spec = {"displayName": (function (){var or__3542__auto__ = name;if(cljs.core.truth_(or__3542__auto__))
{return or__3542__auto__;
} else
{return "ComponentWrapper";
}
})(), "shouldComponentUpdate": ((function (input_QMARK_,input_setup){
return (function (p1__9263_SHARP_,p2__9264_SHARP_){var C = this;return reagent.impl.template.wrapped_should_update.call(null,C,p1__9263_SHARP_,p2__9264_SHARP_);
});})(input_QMARK_,input_setup))
, "render": ((function (input_QMARK_,input_setup){
return (function (){var C = this;return reagent.impl.template.wrapped_render.call(null,C,comp,extras,input_setup);
});})(input_QMARK_,input_setup))
};if(cljs.core.truth_(input_QMARK_))
{reagent.impl.template.add_input_methods.call(null,spec);
} else
{}
return reagent.impl.util.React.createClass(spec);
});
reagent.impl.template.parse_tag = (function parse_tag(hiccup_tag){var vec__9266 = cljs.core.next.call(null,cljs.core.re_matches.call(null,reagent.impl.template.re_tag,cljs.core.name.call(null,hiccup_tag)));var tag = cljs.core.nth.call(null,vec__9266,(0),null);var id = cljs.core.nth.call(null,vec__9266,(1),null);var class$ = cljs.core.nth.call(null,vec__9266,(2),null);var comp = (reagent.impl.template.DOM[tag]);var class_SINGLEQUOTE_ = (cljs.core.truth_(class$)?clojure.string.replace.call(null,class$,/\./," "):null);if(cljs.core.truth_(comp))
{} else
{throw (new Error(("Assert failed: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(("Unknown tag: '"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(hiccup_tag)+"'"))+"\n"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,new cljs.core.Symbol(null,"comp","comp",-1462482139,null))))));
}
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [comp,(cljs.core.truth_((function (){var or__3542__auto__ = id;if(cljs.core.truth_(or__3542__auto__))
{return or__3542__auto__;
} else
{return class_SINGLEQUOTE_;
}
})())?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [id,class_SINGLEQUOTE_], null):null)], null);
});
reagent.impl.template.get_wrapper = (function get_wrapper(tag){var vec__9268 = reagent.impl.template.parse_tag.call(null,tag);var comp = cljs.core.nth.call(null,vec__9268,(0),null);var id_class = cljs.core.nth.call(null,vec__9268,(1),null);return reagent.impl.template.wrap_component.call(null,comp,id_class,(''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(tag)));
});
reagent.impl.template.cached_wrapper = reagent.impl.util.memoize_1.call(null,reagent.impl.template.get_wrapper);
reagent.impl.template.fn_to_class = (function fn_to_class(f){var spec = cljs.core.meta.call(null,f);var withrender = cljs.core.assoc.call(null,spec,new cljs.core.Keyword(null,"component-function","component-function",654728922),f);var res = reagent.impl.template.create_class.call(null,withrender);var wrapf = res.cljsReactClass;f.cljsReactClass = wrapf;
return wrapf;
});
reagent.impl.template.as_class = (function as_class(tag){if(reagent.impl.template.hiccup_tag_QMARK_.call(null,tag))
{return reagent.impl.template.cached_wrapper.call(null,tag);
} else
{var cached_class = tag.cljsReactClass;if(!((cached_class == null)))
{return cached_class;
} else
{if(cljs.core.truth_(reagent.impl.util.React.isValidClass(tag)))
{return tag.cljsReactClass = reagent.impl.template.wrap_component.call(null,tag,null,null);
} else
{return reagent.impl.template.fn_to_class.call(null,tag);
}
}
}
});
reagent.impl.template.get_key = (function get_key(x){if(cljs.core.map_QMARK_.call(null,x))
{return cljs.core.get.call(null,x,new cljs.core.Keyword(null,"key","key",-1516042587));
} else
{return null;
}
});
reagent.impl.template.vec_to_comp = (function vec_to_comp(v,level){if((cljs.core.count.call(null,v) > (0)))
{} else
{throw (new Error(("Assert failed: Hiccup form should not be empty\n"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,cljs.core.list(new cljs.core.Symbol(null,"pos?","pos?",-244377722,null),cljs.core.list(new cljs.core.Symbol(null,"count","count",-514511684,null),new cljs.core.Symbol(null,"v","v",1661996586,null))))))));
}
if(reagent.impl.template.valid_tag_QMARK_.call(null,cljs.core.nth.call(null,v,(0))))
{} else
{throw (new Error(("Assert failed: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(("Invalid Hiccup form: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,v))))+"\n"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,cljs.core.list(new cljs.core.Symbol(null,"valid-tag?","valid-tag?",1243064160,null),cljs.core.list(new cljs.core.Symbol(null,"nth","nth",1529209554,null),new cljs.core.Symbol(null,"v","v",1661996586,null),(0))))))));
}
var c = reagent.impl.template.as_class.call(null,cljs.core.nth.call(null,v,(0)));var jsprops = (function (){var obj9272 = {};(obj9272[reagent.impl.util.cljs_argv] = v);
(obj9272[reagent.impl.util.cljs_level] = level);
return obj9272;
})();var k_9273 = reagent.impl.template.get_key.call(null,cljs.core.meta.call(null,v));var k_SINGLEQUOTE__9274 = (((k_9273 == null))?reagent.impl.template.get_key.call(null,cljs.core.nth.call(null,v,(1),null)):k_9273);if((k_SINGLEQUOTE__9274 == null))
{} else
{(jsprops["key"] = k_SINGLEQUOTE__9274);
}
return c.call(null,jsprops);
});
reagent.impl.template.tmp = {};
reagent.impl.template.warn_on_deref = (function warn_on_deref(x){if(cljs.core.truth_(reagent.impl.template.tmp.warned))
{return null;
} else
{if(!((console.log == null)))
{console.log("Warning: Reactive deref not supported in seq in ",cljs.core.pr_str.call(null,x));
} else
{}
return reagent.impl.template.tmp.warned = true;
}
});
reagent.impl.template.as_component = (function() {
var as_component = null;
var as_component__1 = (function (x){return as_component.call(null,x,(0));
});
var as_component__2 = (function (x,level){if(cljs.core.vector_QMARK_.call(null,x))
{return reagent.impl.template.vec_to_comp.call(null,x,level);
} else
{if(cljs.core.seq_QMARK_.call(null,x))
{if(!((true) && ((reagent.ratom._STAR_ratom_context_STAR_ == null))))
{return reagent.impl.template.expand_seq.call(null,x,level);
} else
{var s = reagent.ratom.capture_derefed.call(null,(function (){return reagent.impl.template.expand_seq.call(null,x,level);
}),reagent.impl.template.tmp);if(cljs.core.truth_(reagent.ratom.captured.call(null,reagent.impl.template.tmp)))
{reagent.impl.template.warn_on_deref.call(null,x);
} else
{}
return s;
}
} else
{if(true)
{return x;
} else
{return null;
}
}
}
});
as_component = function(x,level){
switch(arguments.length){
case 1:
return as_component__1.call(this,x);
case 2:
return as_component__2.call(this,x,level);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
as_component.cljs$core$IFn$_invoke$arity$1 = as_component__1;
as_component.cljs$core$IFn$_invoke$arity$2 = as_component__2;
return as_component;
})()
;
reagent.impl.template.create_class = (function create_class(spec){return reagent.impl.component.create_class.call(null,spec,reagent.impl.template.as_component);
});
reagent.impl.template.expand_seq = (function expand_seq(s,level){var a = cljs.core.into_array.call(null,s);var level_SINGLEQUOTE_ = (level + (1));var n__4398__auto___9275 = a.length;var i_9276 = (0);while(true){
if((i_9276 < n__4398__auto___9275))
{(a[i_9276] = reagent.impl.template.as_component.call(null,(a[i_9276]),level_SINGLEQUOTE_));
{
var G__9277 = (i_9276 + (1));
i_9276 = G__9277;
continue;
}
} else
{}
break;
}
return a;
});
reagent.impl.template.convert_args = (function convert_args(argv,first_child,level){var a = cljs.core.into_array.call(null,argv);var n__4398__auto___9278 = a.length;var i_9279 = (0);while(true){
if((i_9279 < n__4398__auto___9278))
{if((i_9279 >= first_child))
{(a[i_9279] = reagent.impl.template.as_component.call(null,(a[i_9279]),level));
} else
{}
{
var G__9280 = (i_9279 + (1));
i_9279 = G__9280;
continue;
}
} else
{}
break;
}
if((first_child === (2)))
{a.shift();
} else
{}
return a;
});

//# sourceMappingURL=template.js.map
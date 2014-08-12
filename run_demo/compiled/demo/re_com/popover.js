// Compiled by ClojureScript 0.0-2280
goog.provide('re_com.popover');
goog.require('cljs.core');
goog.require('re_com.core');
goog.require('reagent.core');
goog.require('reagent.core');
goog.require('re_com.core');
goog.require('re_com.util');
goog.require('re_com.util');
re_com.popover.point = (function point(x,y){return (''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(x)+","+cljs.core.str.cljs$core$IFn$_invoke$arity$1(y)+" ");
});
/**
* @param {...*} var_args
*/
re_com.popover.px = (function() { 
var px__delegate = function (val,negative){return (''+cljs.core.str.cljs$core$IFn$_invoke$arity$1((cljs.core.truth_(negative)?"-":null))+cljs.core.str.cljs$core$IFn$_invoke$arity$1(val)+"px");
};
var px = function (val,var_args){
var negative = null;if (arguments.length > 1) {
  negative = cljs.core.array_seq(Array.prototype.slice.call(arguments, 1),0);} 
return px__delegate.call(this,val,negative);};
px.cljs$lang$maxFixedArity = 1;
px.cljs$lang$applyTo = (function (arglist__8978){
var val = cljs.core.first(arglist__8978);
var negative = cljs.core.rest(arglist__8978);
return px__delegate(val,negative);
});
px.cljs$core$IFn$_invoke$arity$variadic = px__delegate;
return px;
})()
;
re_com.popover.split_keyword = (function split_keyword(kw,delimiter){var keywords = clojure.string.split.call(null,(''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(kw)),cljs.core.re_pattern.call(null,("["+cljs.core.str.cljs$core$IFn$_invoke$arity$1(delimiter)+":]")));return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.keyword.call(null,keywords.call(null,(1))),cljs.core.keyword.call(null,keywords.call(null,(2)))], null);
});
re_com.popover.make_close_button = (function make_close_button(popover_to_close_QMARK_){return new cljs.core.PersistentVector(null, 7, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.core.button,"\u00D7",(function (){return cljs.core.reset_BANG_.call(null,popover_to_close_QMARK_,false);
}),new cljs.core.Keyword(null,"class","class",-2030961996),"close",new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"font-size","font-size",-1847940346),"36px",new cljs.core.Keyword(null,"margin-top","margin-top",392161226),"-8px"], null)], null);
});
re_com.popover.calc_popover_pos = (function calc_popover_pos(pop_id,pop_orient,pop_offset){var temp__4124__auto__ = re_com.util.get_element_by_id.call(null,pop_id);if(cljs.core.truth_(temp__4124__auto__))
{var popover_elem = temp__4124__auto__;var p_width = popover_elem.clientWidth;var p_height = popover_elem.clientHeight;var popover_left = (function (){var G__8983 = (((pop_orient instanceof cljs.core.Keyword))?pop_orient.fqn:null);switch (G__8983) {
case "below":
return re_com.popover.px.call(null,(cljs.core.truth_(pop_offset)?pop_offset:(p_width / (2))),new cljs.core.Keyword(null,"negative","negative",-1562068438));

break;
case "above":
return re_com.popover.px.call(null,(cljs.core.truth_(pop_offset)?pop_offset:(p_width / (2))),new cljs.core.Keyword(null,"negative","negative",-1562068438));

break;
case "right":
return "100%";

break;
case "left":
return "initial";

break;
default:
throw (new Error(("No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(pop_orient))));

}
})();var popover_top = (function (){var G__8984 = (((pop_orient instanceof cljs.core.Keyword))?pop_orient.fqn:null);switch (G__8984) {
case "below":
return "100%";

break;
case "above":
return "initial";

break;
case "right":
return re_com.popover.px.call(null,(cljs.core.truth_(pop_offset)?pop_offset:(p_height / (2))),new cljs.core.Keyword(null,"negative","negative",-1562068438));

break;
case "left":
return re_com.popover.px.call(null,(cljs.core.truth_(pop_offset)?pop_offset:(p_height / (2))),new cljs.core.Keyword(null,"negative","negative",-1562068438));

break;
default:
throw (new Error(("No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(pop_orient))));

}
})();var popover_right = (function (){var G__8985 = (((pop_orient instanceof cljs.core.Keyword))?pop_orient.fqn:null);switch (G__8985) {
case "below":
return null;

break;
case "above":
return null;

break;
case "right":
return null;

break;
case "left":
return re_com.popover.px.call(null,(10));

break;
default:
throw (new Error(("No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(pop_orient))));

}
})();var popover_bottom = (function (){var G__8986 = (((pop_orient instanceof cljs.core.Keyword))?pop_orient.fqn:null);switch (G__8986) {
case "below":
return null;

break;
case "above":
return re_com.popover.px.call(null,(10));

break;
case "right":
return null;

break;
case "left":
return null;

break;
default:
throw (new Error(("No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(pop_orient))));

}
})();return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"left","left",-399115937),popover_left,new cljs.core.Keyword(null,"top","top",-1856271961),popover_top,new cljs.core.Keyword(null,"right","right",-452581833),popover_right,new cljs.core.Keyword(null,"bottom","bottom",-1550509018),popover_bottom], null);
} else
{return null;
}
});
re_com.popover.make_popover_arrow = (function make_popover_arrow(orientation,pop_offset,arrow_length,arrow_width,grey_arrow){var half_arrow_width = (arrow_width / (2));var arrow_shape = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"left","left",-399115937),(''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(re_com.popover.point.call(null,(0),(0)))+cljs.core.str.cljs$core$IFn$_invoke$arity$1(re_com.popover.point.call(null,arrow_length,half_arrow_width))+cljs.core.str.cljs$core$IFn$_invoke$arity$1(re_com.popover.point.call(null,(0),arrow_width))),new cljs.core.Keyword(null,"right","right",-452581833),(''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(re_com.popover.point.call(null,arrow_length,(0)))+cljs.core.str.cljs$core$IFn$_invoke$arity$1(re_com.popover.point.call(null,(0),half_arrow_width))+cljs.core.str.cljs$core$IFn$_invoke$arity$1(re_com.popover.point.call(null,arrow_length,arrow_width))),new cljs.core.Keyword(null,"above","above",-1286866470),(''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(re_com.popover.point.call(null,(0),(0)))+cljs.core.str.cljs$core$IFn$_invoke$arity$1(re_com.popover.point.call(null,half_arrow_width,arrow_length))+cljs.core.str.cljs$core$IFn$_invoke$arity$1(re_com.popover.point.call(null,arrow_width,(0)))),new cljs.core.Keyword(null,"below","below",-926774883),(''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(re_com.popover.point.call(null,(0),arrow_length))+cljs.core.str.cljs$core$IFn$_invoke$arity$1(re_com.popover.point.call(null,half_arrow_width,(0)))+cljs.core.str.cljs$core$IFn$_invoke$arity$1(re_com.popover.point.call(null,arrow_width,arrow_length)))], null);return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"svg","svg",856789142),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap.fromArray([new cljs.core.Keyword(null,"position","position",-2011731912),"absolute",(function (){var G__8996 = (((orientation instanceof cljs.core.Keyword))?orientation.fqn:null);switch (G__8996) {
case "below":
return new cljs.core.Keyword(null,"top","top",-1856271961);

break;
case "above":
return new cljs.core.Keyword(null,"bottom","bottom",-1550509018);

break;
case "right":
return new cljs.core.Keyword(null,"left","left",-399115937);

break;
case "left":
return new cljs.core.Keyword(null,"right","right",-452581833);

break;
default:
throw (new Error(("No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(orientation))));

}
})(),re_com.popover.px.call(null,arrow_length,new cljs.core.Keyword(null,"negative","negative",-1562068438)),(function (){var G__8997 = (((orientation instanceof cljs.core.Keyword))?orientation.fqn:null);switch (G__8997) {
case "below":
return new cljs.core.Keyword(null,"left","left",-399115937);

break;
case "above":
return new cljs.core.Keyword(null,"left","left",-399115937);

break;
case "right":
return new cljs.core.Keyword(null,"top","top",-1856271961);

break;
case "left":
return new cljs.core.Keyword(null,"top","top",-1856271961);

break;
default:
throw (new Error(("No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(orientation))));

}
})(),(((pop_offset == null))?"50%":re_com.popover.px.call(null,pop_offset)),(function (){var G__8998 = (((orientation instanceof cljs.core.Keyword))?orientation.fqn:null);switch (G__8998) {
case "below":
return new cljs.core.Keyword(null,"margin-left","margin-left",2015598377);

break;
case "above":
return new cljs.core.Keyword(null,"margin-left","margin-left",2015598377);

break;
case "right":
return new cljs.core.Keyword(null,"margin-top","margin-top",392161226);

break;
case "left":
return new cljs.core.Keyword(null,"margin-top","margin-top",392161226);

break;
default:
throw (new Error(("No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(orientation))));

}
})(),re_com.popover.px.call(null,half_arrow_width,new cljs.core.Keyword(null,"negative","negative",-1562068438)),new cljs.core.Keyword(null,"width","width",-384071477),re_com.popover.px.call(null,(function (){var G__8999 = (((orientation instanceof cljs.core.Keyword))?orientation.fqn:null);switch (G__8999) {
case "below":
return arrow_width;

break;
case "above":
return arrow_width;

break;
case "right":
return arrow_length;

break;
case "left":
return arrow_length;

break;
default:
throw (new Error(("No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(orientation))));

}
})()),new cljs.core.Keyword(null,"height","height",1025178622),re_com.popover.px.call(null,(function (){var G__9000 = (((orientation instanceof cljs.core.Keyword))?orientation.fqn:null);switch (G__9000) {
case "below":
return arrow_length;

break;
case "above":
return arrow_length;

break;
case "right":
return arrow_width;

break;
case "left":
return arrow_width;

break;
default:
throw (new Error(("No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(orientation))));

}
})())], true, false)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"polyline","polyline",-1731551044),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"points","points",-1486596883),arrow_shape.call(null,orientation),new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"fill","fill",883462889),(cljs.core.truth_(grey_arrow)?"#f7f7f7":"white"),new cljs.core.Keyword(null,"stroke","stroke",1741823555),"rgba(0, 0, 0, .2)",new cljs.core.Keyword(null,"stroke-width","stroke-width",716836435),"1"], null)], null)], null)], null);
});
re_com.popover.make_popover = (function make_popover(p__9006){var map__9013 = p__9006;var map__9013__$1 = ((cljs.core.seq_QMARK_.call(null,map__9013))?cljs.core.apply.call(null,cljs.core.hash_map,map__9013):map__9013);var body = cljs.core.get.call(null,map__9013__$1,new cljs.core.Keyword(null,"body","body",-2049205669),"{empty body}");var height = cljs.core.get.call(null,map__9013__$1,new cljs.core.Keyword(null,"height","height",1025178622));var show_popover_QMARK_ = cljs.core.get.call(null,map__9013__$1,new cljs.core.Keyword(null,"show-popover?","show-popover?",-282591553));var backdrop_transparency = cljs.core.get.call(null,map__9013__$1,new cljs.core.Keyword(null,"backdrop-transparency","backdrop-transparency",-1674491937),0.1);var arrow_length = cljs.core.get.call(null,map__9013__$1,new cljs.core.Keyword(null,"arrow-length","arrow-length",934916707),(11));var backdrop_callback = cljs.core.get.call(null,map__9013__$1,new cljs.core.Keyword(null,"backdrop-callback","backdrop-callback",1772830664));var close_button_QMARK_ = cljs.core.get.call(null,map__9013__$1,new cljs.core.Keyword(null,"close-button?","close-button?",-1030817687),false);var arrow_width = cljs.core.get.call(null,map__9013__$1,new cljs.core.Keyword(null,"arrow-width","arrow-width",1926673833),(22));var width = cljs.core.get.call(null,map__9013__$1,new cljs.core.Keyword(null,"width","width",-384071477),(250));var title = cljs.core.get.call(null,map__9013__$1,new cljs.core.Keyword(null,"title","title",636505583));var position = cljs.core.get.call(null,map__9013__$1,new cljs.core.Keyword(null,"position","position",-2011731912),new cljs.core.Keyword(null,"right-below","right-below",586981827));new cljs.core.PersistentArrayMap.fromArray([cljs.core.empty,body], true, false);
var rendered_once = reagent.core.atom.call(null,false);var pop_id = cljs.core.gensym.call(null,"popover-");var vec__9014 = re_com.popover.split_keyword.call(null,position,"-");var orientation = cljs.core.nth.call(null,vec__9014,(0),null);var arrow_pos = cljs.core.nth.call(null,vec__9014,(1),null);var grey_arrow = (function (){var and__3530__auto__ = title;if(cljs.core.truth_(and__3530__auto__))
{return (cljs.core._EQ_.call(null,orientation,new cljs.core.Keyword(null,"below","below",-926774883))) || (cljs.core._EQ_.call(null,arrow_pos,new cljs.core.Keyword(null,"below","below",-926774883)));
} else
{return and__3530__auto__;
}
})();return reagent.core.create_class.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"component-did-mount","component-did-mount",-1126910518),((function (rendered_once,pop_id,vec__9014,orientation,arrow_pos,grey_arrow,map__9013,map__9013__$1,body,height,show_popover_QMARK_,backdrop_transparency,arrow_length,backdrop_callback,close_button_QMARK_,arrow_width,width,title,position){
return (function (){return cljs.core.reset_BANG_.call(null,rendered_once,true);
});})(rendered_once,pop_id,vec__9014,orientation,arrow_pos,grey_arrow,map__9013,map__9013__$1,body,height,show_popover_QMARK_,backdrop_transparency,arrow_length,backdrop_callback,close_button_QMARK_,arrow_width,width,title,position))
,new cljs.core.Keyword(null,"render","render",-1408033454),((function (rendered_once,pop_id,vec__9014,orientation,arrow_pos,grey_arrow,map__9013,map__9013__$1,body,height,show_popover_QMARK_,backdrop_transparency,arrow_length,backdrop_callback,close_button_QMARK_,arrow_width,width,title,position){
return (function (){var popover_elem = re_com.util.get_element_by_id.call(null,pop_id);var p_height = (cljs.core.truth_(popover_elem)?popover_elem.clientHeight:(0));var pop_offset = (function (){var G__9015 = (((arrow_pos instanceof cljs.core.Keyword))?arrow_pos.fqn:null);switch (G__9015) {
case "above":
if(cljs.core.truth_(p_height))
{return (p_height - (25));
} else
{return p_height;
}

break;
case "left":
if(cljs.core.truth_(width))
{return (width - (25));
} else
{return width;
}

break;
case "below":
return (20);

break;
case "right":
return (20);

break;
case "center":
return null;

break;
default:
throw (new Error(("No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(arrow_pos))));

}
})();return new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.popover.fade.in","div.popover.fade.in",-106226512),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"id","id",-1388402092),pop_id,new cljs.core.Keyword(null,"class","class",-2030961996),(function (){var G__9016 = (((orientation instanceof cljs.core.Keyword))?orientation.fqn:null);switch (G__9016) {
case "below":
return "bottom";

break;
case "above":
return "top";

break;
case "right":
return "right";

break;
case "left":
return "left";

break;
default:
throw (new Error(("No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(orientation))));

}
})(),new cljs.core.Keyword(null,"style","style",-496642736),cljs.core.merge.call(null,(cljs.core.truth_(cljs.core.deref.call(null,rendered_once))?re_com.popover.calc_popover_pos.call(null,pop_id,orientation,pop_offset):new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"top","top",-1856271961),re_com.popover.px.call(null,(-10000)),new cljs.core.Keyword(null,"left","left",-399115937),re_com.popover.px.call(null,(-10000))], null)),(cljs.core.truth_(width)?new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"width","width",-384071477),width], null):null),(cljs.core.truth_(height)?new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"height","height",1025178622),height], null):null),new cljs.core.PersistentArrayMap.fromArray([(function (){var G__9017 = (((orientation instanceof cljs.core.Keyword))?orientation.fqn:null);switch (G__9017) {
case "below":
return new cljs.core.Keyword(null,"margin-top","margin-top",392161226);

break;
case "above":
return new cljs.core.Keyword(null,"margin-top","margin-top",392161226);

break;
case "right":
return new cljs.core.Keyword(null,"margin-left","margin-left",2015598377);

break;
case "left":
return new cljs.core.Keyword(null,"margin-left","margin-left",2015598377);

break;
default:
throw (new Error(("No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(orientation))));

}
})(),re_com.popover.px.call(null,(function (){var G__9018 = (((orientation instanceof cljs.core.Keyword))?orientation.fqn:null);switch (G__9018) {
case "below":
return arrow_length;

break;
case "right":
return arrow_length;

break;
case "above":
return ("-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1((arrow_length + p_height)));

break;
case "left":
return ("-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1((arrow_length + width)));

break;
default:
throw (new Error(("No matching clause: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(orientation))));

}
})())], true, false),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"display","display",242065432),"block",new cljs.core.Keyword(null,"max-width","max-width",-1939924051),"none",new cljs.core.Keyword(null,"padding","padding",1660304693),re_com.popover.px.call(null,(0))], null))], null),new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.popover.make_popover_arrow,orientation,pop_offset,arrow_length,arrow_width,grey_arrow], null),(cljs.core.truth_(title)?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3.popover-title","h3.popover-title",126205197),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),title,(cljs.core.truth_(close_button_QMARK_)?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.popover.make_close_button,show_popover_QMARK_], null):null)], null)], null):null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.popover-content","div.popover-content",1045719989),body], null)], null);
});})(rendered_once,pop_id,vec__9014,orientation,arrow_pos,grey_arrow,map__9013,map__9013__$1,body,height,show_popover_QMARK_,backdrop_transparency,arrow_length,backdrop_callback,close_button_QMARK_,arrow_width,width,title,position))
], null));
});
re_com.popover.popover = (function popover(position,show_popover_QMARK_,anchor,popover_content,popover_options){var vec__9026 = re_com.popover.split_keyword.call(null,position,"-");var orientation = cljs.core.nth.call(null,vec__9026,(0),null);var arrow_pos = cljs.core.nth.call(null,vec__9026,(1),null);var place_anchor_before_QMARK_ = (function (){var G__9027 = (((orientation instanceof cljs.core.Keyword))?orientation.fqn:null);switch (G__9027) {
case "above":
return false;

break;
case "left":
return false;

break;
default:
return true;

}
})();var flex_flow = (function (){var G__9028 = (((orientation instanceof cljs.core.Keyword))?orientation.fqn:null);switch (G__9028) {
case "right":
return "row";

break;
case "left":
return "row";

break;
default:
return "column";

}
})();var popover_params = cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"position","position",-2011731912),position,new cljs.core.Keyword(null,"show-popover?","show-popover?",-282591553),show_popover_QMARK_], null),popover_content,popover_options);var backdrop_callback = new cljs.core.Keyword(null,"backdrop-callback","backdrop-callback",1772830664).cljs$core$IFn$_invoke$arity$1(popover_params);var backdrop_opacity = new cljs.core.Keyword(null,"backdrop-opacity","backdrop-opacity",1467395653).cljs$core$IFn$_invoke$arity$1(popover_params);return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"display","display",242065432),"inline-block"], null)], null),(cljs.core.truth_((function (){var and__3530__auto__ = cljs.core.deref.call(null,show_popover_QMARK_);if(cljs.core.truth_(and__3530__auto__))
{return backdrop_callback;
} else
{return and__3530__auto__;
}
})())?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"position","position",-2011731912),"fixed",new cljs.core.Keyword(null,"left","left",-399115937),"0px",new cljs.core.Keyword(null,"top","top",-1856271961),"0px",new cljs.core.Keyword(null,"width","width",-384071477),"100%",new cljs.core.Keyword(null,"height","height",1025178622),"100%",new cljs.core.Keyword(null,"background-color","background-color",570434026),"black",new cljs.core.Keyword(null,"opacity","opacity",397153780),backdrop_opacity], null),new cljs.core.Keyword(null,"on-click","on-click",1632826543),backdrop_callback], null)], null):null),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"display","display",242065432),"inline-flex",new cljs.core.Keyword(null,"flex-flow","flex-flow",544537375),flex_flow,new cljs.core.Keyword(null,"align-items","align-items",-267946462),"center"], null)], null),(cljs.core.truth_(place_anchor_before_QMARK_)?anchor:null),(cljs.core.truth_(cljs.core.deref.call(null,show_popover_QMARK_))?new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"position","position",-2011731912),"relative",new cljs.core.Keyword(null,"display","display",242065432),"inline-block"], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.popover.make_popover,popover_params], null)], null):null),(cljs.core.truth_(place_anchor_before_QMARK_)?null:anchor)], null)], null);
});
re_com.popover.make_button = (function make_button(show_popover_QMARK_,type,text){return new cljs.core.PersistentVector(null, 7, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.core.button,text,(function (){return cljs.core.reset_BANG_.call(null,show_popover_QMARK_,cljs.core.not.call(null,cljs.core.deref.call(null,show_popover_QMARK_)));
}),new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"margin-left","margin-left",2015598377),"2px"], null),new cljs.core.Keyword(null,"class","class",-2030961996),("btn-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(type))], null);
});
re_com.popover.make_link = (function make_link(show_popover_QMARK_,toggle_on,text){var show = (function (){return cljs.core.reset_BANG_.call(null,show_popover_QMARK_,true);
});var hide = ((function (show){
return (function (){return cljs.core.reset_BANG_.call(null,show_popover_QMARK_,false);
});})(show))
;var toggle = ((function (show,hide){
return (function (){return cljs.core.reset_BANG_.call(null,show_popover_QMARK_,cljs.core.not.call(null,cljs.core.deref.call(null,show_popover_QMARK_)));
});})(show,hide))
;return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a","a",-2123407586),cljs.core.merge.call(null,cljs.core.PersistentArrayMap.EMPTY,((cljs.core._EQ_.call(null,toggle_on,new cljs.core.Keyword(null,"mouse","mouse",478628972)))?new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"on-mouse-over","on-mouse-over",-858472552),show,new cljs.core.Keyword(null,"on-mouse-out","on-mouse-out",643448647),hide], null):new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-click","on-click",1632826543),toggle], null))),text], null);
});

//# sourceMappingURL=popover.js.map
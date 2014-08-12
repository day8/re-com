// Compiled by ClojureScript 0.0-2280
goog.provide('re_com.v_layout');
goog.require('cljs.core');
goog.require('reagent.core');
goog.require('reagent.core');
re_com.v_layout.v_layout = (function v_layout(top_panel,bottom_panel){var container_id = cljs.core.gensym.call(null,"v-layout-");var this$ = reagent.core.current_component.call(null);var margin = "8px";var split_perc = reagent.core.atom.call(null,(50));var dragging_QMARK_ = reagent.core.atom.call(null,false);var stop_drag = ((function (container_id,this$,margin,split_perc,dragging_QMARK_){
return (function (){return cljs.core.reset_BANG_.call(null,dragging_QMARK_,false);
});})(container_id,this$,margin,split_perc,dragging_QMARK_))
;var calc_perc = ((function (container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag){
return (function (mouse_y){var container = document.getElementById(container_id);var c_height = container.clientHeight;var c_top_y = container.offsetTop;var relative_y = (mouse_y - c_top_y);return (100.0 * (relative_y / c_height));
});})(container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag))
;var _LT_html_GT__QMARK_ = ((function (container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc){
return (function (p1__9213_SHARP_){return cljs.core._EQ_.call(null,p1__9213_SHARP_,document.documentElement);
});})(container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc))
;var mouseout = ((function (container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_){
return (function (event){if(_LT_html_GT__QMARK_.call(null,event.relatedTarget))
{return stop_drag.call(null);
} else
{return null;
}
});})(container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_))
;var mousemove = ((function (container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_,mouseout){
return (function (event){return cljs.core.reset_BANG_.call(null,split_perc,calc_perc.call(null,event.clientY));
});})(container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_,mouseout))
;var mousedown = ((function (container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_,mouseout,mousemove){
return (function (event){event.preventDefault();
return cljs.core.reset_BANG_.call(null,dragging_QMARK_,true);
});})(container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_,mouseout,mousemove))
;var make_container_style = ((function (container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_,mouseout,mousemove,mousedown){
return (function (in_drag_QMARK_){return cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"id","id",-1388402092),container_id,new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"display","display",242065432),"flex",new cljs.core.Keyword(null,"flex-flow","flex-flow",544537375),"column",new cljs.core.Keyword(null,"height","height",1025178622),"100%",new cljs.core.Keyword(null,"margin","margin",-995903681),margin], null)], null),(cljs.core.truth_(in_drag_QMARK_)?new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"on-mouse-up","on-mouse-up",-1340533320),stop_drag,new cljs.core.Keyword(null,"on-mouse-move","on-mouse-move",-1386320874),mousemove,new cljs.core.Keyword(null,"on-mouse-out","on-mouse-out",643448647),mouseout], null):null));
});})(container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_,mouseout,mousemove,mousedown))
;var make_panel_style = ((function (container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_,mouseout,mousemove,mousedown,make_container_style){
return (function (in_drag_QMARK_,percentage){return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"display","display",242065432),"flex",new cljs.core.Keyword(null,"flex-grow","flex-grow",1865160747),percentage,new cljs.core.Keyword(null,"flex-shrink","flex-shrink",1481146383),"1",new cljs.core.Keyword(null,"flex-basis","flex-basis",983188475),"0",new cljs.core.Keyword(null,"overflow","overflow",2058931880),"hidden"], null),(cljs.core.truth_(in_drag_QMARK_)?new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"pointer-events","pointer-events",-1053858853),"none"], null):null))], null);
});})(container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_,mouseout,mousemove,mousedown,make_container_style))
;var make_splitter_style = ((function (container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_,mouseout,mousemove,mousedown,make_container_style,make_panel_style){
return (function (){return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"on-mouse-down","on-mouse-down",1147755470),mousedown,new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"height","height",1025178622),margin,new cljs.core.Keyword(null,"cursor","cursor",1011937484),"ns-resize"], null)], null);
});})(container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_,mouseout,mousemove,mousedown,make_container_style,make_panel_style))
;return ((function (container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_,mouseout,mousemove,mousedown,make_container_style,make_panel_style,make_splitter_style){
return (function (){return new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.v-layout-container","div.v-layout-container",793375591),make_container_style.call(null,cljs.core.deref.call(null,dragging_QMARK_)),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.v-layout-top","div.v-layout-top",-1898767661),make_panel_style.call(null,cljs.core.deref.call(null,dragging_QMARK_),cljs.core.deref.call(null,split_perc)),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [top_panel], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.v-layout-splitter","div.v-layout-splitter",1717002279),make_splitter_style.call(null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.v-layout-bottom","div.v-layout-bottom",1239900377),make_panel_style.call(null,cljs.core.deref.call(null,dragging_QMARK_),((100) - cljs.core.deref.call(null,split_perc))),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [bottom_panel], null)], null)], null);
});
;})(container_id,this$,margin,split_perc,dragging_QMARK_,stop_drag,calc_perc,_LT_html_GT__QMARK_,mouseout,mousemove,mousedown,make_container_style,make_panel_style,make_splitter_style))
});

//# sourceMappingURL=v_layout.js.map
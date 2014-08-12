// Compiled by ClojureScript 0.0-2280
goog.provide('re_com.alert');
goog.require('cljs.core');
goog.require('re_com.core');
goog.require('re_com.util');
goog.require('re_com.util');
goog.require('re_com.core');
goog.require('reagent.core');
goog.require('reagent.core');
re_com.alert.closeable_alert = (function closeable_alert(alert_item,close_callback){var map__5093 = alert_item;var map__5093__$1 = ((cljs.core.seq_QMARK_.call(null,map__5093))?cljs.core.apply.call(null,cljs.core.hash_map,map__5093):map__5093);var body = cljs.core.get.call(null,map__5093__$1,new cljs.core.Keyword(null,"body","body",-2049205669));var heading = cljs.core.get.call(null,map__5093__$1,new cljs.core.Keyword(null,"heading","heading",-1312171873));var alert_type = cljs.core.get.call(null,map__5093__$1,new cljs.core.Keyword(null,"alert-type","alert-type",405751817));var id = cljs.core.get.call(null,map__5093__$1,new cljs.core.Keyword(null,"id","id",-1388402092));return ((function (map__5093,map__5093__$1,body,heading,alert_type,id){
return (function (){return new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.alert.fade.in","div.alert.fade.in",-270961921),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"class","class",-2030961996),("alert-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(alert_type))], null),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.core.button,"\u00D7",((function (map__5093,map__5093__$1,body,heading,alert_type,id){
return (function (){return close_callback.call(null,id);
});})(map__5093,map__5093__$1,body,heading,alert_type,id))
,new cljs.core.Keyword(null,"class","class",-2030961996),"close"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h4","h4",2004862993),heading], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),body], null)], null);
});
;})(map__5093,map__5093__$1,body,heading,alert_type,id))
});
re_com.alert.alerts = reagent.core.atom.call(null,cljs.core.sorted_map_by.call(null,cljs.core._GT_));
re_com.alert.alerts_count = reagent.core.atom.call(null,(0));
re_com.alert.close_alert = (function close_alert(id){return cljs.core.swap_BANG_.call(null,re_com.alert.alerts,cljs.core.dissoc,id);
});
re_com.alert.add_alert = (function add_alert(alert_type,p__5094){var map__5096 = p__5094;var map__5096__$1 = ((cljs.core.seq_QMARK_.call(null,map__5096))?cljs.core.apply.call(null,cljs.core.hash_map,map__5096):map__5096);var body = cljs.core.get.call(null,map__5096__$1,new cljs.core.Keyword(null,"body","body",-2049205669));var heading = cljs.core.get.call(null,map__5096__$1,new cljs.core.Keyword(null,"heading","heading",-1312171873));var id = cljs.core.swap_BANG_.call(null,re_com.alert.alerts_count,cljs.core.inc);return cljs.core.swap_BANG_.call(null,re_com.alert.alerts,cljs.core.assoc,id,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"id","id",-1388402092),id,new cljs.core.Keyword(null,"alert-type","alert-type",405751817),alert_type,new cljs.core.Keyword(null,"heading","heading",-1312171873),heading,new cljs.core.Keyword(null,"body","body",-2049205669),body], null));
});
re_com.alert.alert_list = (function alert_list(alert_items,close_callback){return (function (){return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"border","border",1444987323),"1px dashed lightgrey"], null)], null),(function (){var iter__4267__auto__ = (function iter__5101(s__5102){return (new cljs.core.LazySeq(null,(function (){var s__5102__$1 = s__5102;while(true){
var temp__4126__auto__ = cljs.core.seq.call(null,s__5102__$1);if(temp__4126__auto__)
{var s__5102__$2 = temp__4126__auto__;if(cljs.core.chunked_seq_QMARK_.call(null,s__5102__$2))
{var c__4265__auto__ = cljs.core.chunk_first.call(null,s__5102__$2);var size__4266__auto__ = cljs.core.count.call(null,c__4265__auto__);var b__5104 = cljs.core.chunk_buffer.call(null,size__4266__auto__);if((function (){var i__5103 = (0);while(true){
if((i__5103 < size__4266__auto__))
{var alert = cljs.core._nth.call(null,c__4265__auto__,i__5103);cljs.core.chunk_append.call(null,b__5104,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.alert.closeable_alert,cljs.core.last.call(null,alert),re_com.alert.close_alert], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(cljs.core.last.call(null,alert))], null)));
{
var G__5105 = (i__5103 + (1));
i__5103 = G__5105;
continue;
}
} else
{return true;
}
break;
}
})())
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__5104),iter__5101.call(null,cljs.core.chunk_rest.call(null,s__5102__$2)));
} else
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__5104),null);
}
} else
{var alert = cljs.core.first.call(null,s__5102__$2);return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.alert.closeable_alert,cljs.core.last.call(null,alert),re_com.alert.close_alert], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(cljs.core.last.call(null,alert))], null)),iter__5101.call(null,cljs.core.rest.call(null,s__5102__$2)));
}
} else
{return null;
}
break;
}
}),null,null));
});return iter__4267__auto__.call(null,cljs.core.deref.call(null,re_com.alert.alerts));
})()], null);
});
});

//# sourceMappingURL=alert.js.map
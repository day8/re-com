// Compiled by ClojureScript 0.0-2280
goog.provide('re_com.tour');
goog.require('cljs.core');
goog.require('re_com.popover');
goog.require('re_com.popover');
goog.require('re_com.util');
goog.require('re_com.util');
goog.require('reagent.core');
goog.require('reagent.core');
/**
* Returns a map containing
* - A reagent atom for each tour step controlling popover show/hide (boolean)
* - A standard atom holding the current step (integer)
* - A copy of the steps parameter passed in, to determine the order for prev/next functions
* It sets the first step atom to true so that it will be initially shown
* Sample return value:
* {:steps [:step1 :step2 :step3]
* :current-step (atom 0)
* :step1 (reagent/atom true)
* :step2 (reagent/atom false)
* :step3 (reagent/atom false)}
*/
re_com.tour.make_tour = (function make_tour(tour_spec){var tour_map = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"current-step","current-step",-2023410137),cljs.core.atom.call(null,(0)),new cljs.core.Keyword(null,"steps","steps",-128433302),tour_spec], null);return cljs.core.reduce.call(null,((function (tour_map){
return (function (p1__9202_SHARP_,p2__9203_SHARP_){return cljs.core.assoc.call(null,p1__9202_SHARP_,p2__9203_SHARP_,reagent.core.atom.call(null,false));
});})(tour_map))
,tour_map,tour_spec);
});
/**
* Resets all poover atoms to false.
*/
re_com.tour.initialise_tour = (function initialise_tour(tour){return cljs.core.doall.call(null,(function (){var iter__4267__auto__ = (function iter__9208(s__9209){return (new cljs.core.LazySeq(null,(function (){var s__9209__$1 = s__9209;while(true){
var temp__4126__auto__ = cljs.core.seq.call(null,s__9209__$1);if(temp__4126__auto__)
{var s__9209__$2 = temp__4126__auto__;if(cljs.core.chunked_seq_QMARK_.call(null,s__9209__$2))
{var c__4265__auto__ = cljs.core.chunk_first.call(null,s__9209__$2);var size__4266__auto__ = cljs.core.count.call(null,c__4265__auto__);var b__9211 = cljs.core.chunk_buffer.call(null,size__4266__auto__);if((function (){var i__9210 = (0);while(true){
if((i__9210 < size__4266__auto__))
{var step = cljs.core._nth.call(null,c__4265__auto__,i__9210);cljs.core.chunk_append.call(null,b__9211,cljs.core.reset_BANG_.call(null,step.call(null,tour),false));
{
var G__9212 = (i__9210 + (1));
i__9210 = G__9212;
continue;
}
} else
{return true;
}
break;
}
})())
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9211),iter__9208.call(null,cljs.core.chunk_rest.call(null,s__9209__$2)));
} else
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9211),null);
}
} else
{var step = cljs.core.first.call(null,s__9209__$2);return cljs.core.cons.call(null,cljs.core.reset_BANG_.call(null,step.call(null,tour),false),iter__9208.call(null,cljs.core.rest.call(null,s__9209__$2)));
}
} else
{return null;
}
break;
}
}),null,null));
});return iter__4267__auto__.call(null,new cljs.core.Keyword(null,"steps","steps",-128433302).cljs$core$IFn$_invoke$arity$1(tour));
})());
});
/**
* Sets the first popover atom in the tour to true.
*/
re_com.tour.start_tour = (function start_tour(tour){re_com.tour.initialise_tour.call(null,tour);
cljs.core.reset_BANG_.call(null,new cljs.core.Keyword(null,"current-step","current-step",-2023410137).cljs$core$IFn$_invoke$arity$1(tour),(0));
return cljs.core.reset_BANG_.call(null,cljs.core.first.call(null,new cljs.core.Keyword(null,"steps","steps",-128433302).cljs$core$IFn$_invoke$arity$1(tour)).call(null,tour),true);
});
/**
* Closes all tour popovers.
*/
re_com.tour.finish_tour = (function finish_tour(tour){return re_com.tour.initialise_tour.call(null,tour);
});
re_com.tour.next_tour_step = (function next_tour_step(tour){var steps = new cljs.core.Keyword(null,"steps","steps",-128433302).cljs$core$IFn$_invoke$arity$1(tour);var old_step = cljs.core.deref.call(null,new cljs.core.Keyword(null,"current-step","current-step",-2023410137).cljs$core$IFn$_invoke$arity$1(tour));var new_step = (old_step + (1));if((new_step < cljs.core.count.call(null,new cljs.core.Keyword(null,"steps","steps",-128433302).cljs$core$IFn$_invoke$arity$1(tour))))
{cljs.core.reset_BANG_.call(null,new cljs.core.Keyword(null,"current-step","current-step",-2023410137).cljs$core$IFn$_invoke$arity$1(tour),new_step);
cljs.core.reset_BANG_.call(null,cljs.core.nth.call(null,steps,old_step).call(null,tour),false);
return cljs.core.reset_BANG_.call(null,cljs.core.nth.call(null,steps,new_step).call(null,tour),true);
} else
{return null;
}
});
re_com.tour.prev_tour_step = (function prev_tour_step(tour){var steps = new cljs.core.Keyword(null,"steps","steps",-128433302).cljs$core$IFn$_invoke$arity$1(tour);var old_step = cljs.core.deref.call(null,new cljs.core.Keyword(null,"current-step","current-step",-2023410137).cljs$core$IFn$_invoke$arity$1(tour));var new_step = (old_step - (1));if((new_step >= (0)))
{cljs.core.reset_BANG_.call(null,new cljs.core.Keyword(null,"current-step","current-step",-2023410137).cljs$core$IFn$_invoke$arity$1(tour),new_step);
cljs.core.reset_BANG_.call(null,cljs.core.nth.call(null,steps,old_step).call(null,tour),false);
return cljs.core.reset_BANG_.call(null,cljs.core.nth.call(null,steps,new_step).call(null,tour),true);
} else
{return null;
}
});
/**
* Generate the hr and previous/next buttons markup.
* If first button in tour, don't generate a Previous button.
* If last button in tour, change Next button to a Finish button.
*/
re_com.tour.make_tour_nav = (function make_tour_nav(tour){var on_first_button = cljs.core._EQ_.call(null,cljs.core.deref.call(null,new cljs.core.Keyword(null,"current-step","current-step",-2023410137).cljs$core$IFn$_invoke$arity$1(tour)),(0));var on_last_button = cljs.core._EQ_.call(null,cljs.core.deref.call(null,new cljs.core.Keyword(null,"current-step","current-step",-2023410137).cljs$core$IFn$_invoke$arity$1(tour)),(cljs.core.count.call(null,new cljs.core.Keyword(null,"steps","steps",-128433302).cljs$core$IFn$_invoke$arity$1(tour)) - (1)));return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"hr","hr",1377740067),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"margin","margin",-995903681),"10px 0 10px"], null)], null)], null),((on_first_button)?null:new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input.btn.btn-default","input.btn.btn-default",184364484),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"type","type",1174270348),"button",new cljs.core.Keyword(null,"value","value",305978217),"Previous",new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"margin-right","margin-right",809689658),"15px"], null),new cljs.core.Keyword(null,"on-click","on-click",1632826543),((function (on_first_button,on_last_button){
return (function (){return re_com.tour.prev_tour_step.call(null,tour);
});})(on_first_button,on_last_button))
], null)], null)),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input.btn.btn-default","input.btn.btn-default",184364484),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"type","type",1174270348),"button",new cljs.core.Keyword(null,"value","value",305978217),((on_last_button)?"Finish":"Next"),new cljs.core.Keyword(null,"on-click","on-click",1632826543),((function (on_first_button,on_last_button){
return (function (){if(on_last_button)
{return re_com.tour.finish_tour.call(null,tour);
} else
{return re_com.tour.next_tour_step.call(null,tour);
}
});})(on_first_button,on_last_button))
], null)], null)], null);
});

//# sourceMappingURL=tour.js.map
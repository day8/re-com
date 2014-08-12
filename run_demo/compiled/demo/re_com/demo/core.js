// Compiled by ClojureScript 0.0-2280
goog.provide('re_com.demo.core');
goog.require('cljs.core');
goog.require('reagent.core');
goog.require('re_com.util');
goog.require('re_com.demo.welcome');
goog.require('re_com.demo.modals');
goog.require('re_com.demo.popovers');
goog.require('re_com.demo.basics');
goog.require('reagent.core');
goog.require('re_com.demo.tour');
goog.require('re_com.demo.welcome');
goog.require('re_com.tabs');
goog.require('re_com.util');
goog.require('re_com.tabs');
goog.require('re_com.demo.basics');
goog.require('re_com.demo.popovers');
goog.require('re_com.demo.modals');
goog.require('re_com.demo.tour');
re_com.demo.core.tabs_definition = new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword("re-com.demo.core","welcome","re-com.demo.core/welcome",587922372),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"label","label",1718410804),"Welcome",new cljs.core.Keyword(null,"panel","panel",-558637456),re_com.demo.welcome.panel], null),new cljs.core.Keyword("re-com.demo.core","basics","re-com.demo.core/basics",1575655001),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"label","label",1718410804),"Basics",new cljs.core.Keyword(null,"panel","panel",-558637456),re_com.demo.basics.panel], null),new cljs.core.Keyword("re-com.demo.core","popovers","re-com.demo.core/popovers",966295134),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"label","label",1718410804),"Popovers",new cljs.core.Keyword(null,"panel","panel",-558637456),re_com.demo.popovers.panel], null),new cljs.core.Keyword("re-com.demo.core","tour","re-com.demo.core/tour",1930775737),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"label","label",1718410804),"Tour",new cljs.core.Keyword(null,"panel","panel",-558637456),re_com.demo.tour.panel], null),new cljs.core.Keyword("re-com.demo.core","modals","re-com.demo.core/modals",-251420055),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"label","label",1718410804),"Modals",new cljs.core.Keyword(null,"panel","panel",-558637456),re_com.demo.modals.panel], null)], null);
re_com.demo.core.main = (function main(){var selected_tab = reagent.core.atom.call(null,cljs.core.ffirst.call(null,re_com.demo.core.tabs_definition));return ((function (selected_tab){
return (function _main(){return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"margin","margin",-995903681),"15px"], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.tabs.horizontal_pills,selected_tab,re_com.demo.core.tabs_definition], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"panel","panel",-558637456).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,selected_tab).call(null,re_com.demo.core.tabs_definition))], null)], null);
});
;})(selected_tab))
});
re_com.demo.core.init = (function init(){return reagent.core.render_component.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.demo.core.main], null),re_com.util.get_element_by_id.call(null,"app"));
});

//# sourceMappingURL=core.js.map
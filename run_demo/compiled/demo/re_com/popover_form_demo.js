// Compiled by ClojureScript 0.0-2280
goog.provide('re_com.popover_form_demo');
goog.require('cljs.core');
goog.require('re_com.alert');
goog.require('re_com.popover');
goog.require('re_com.core');
goog.require('re_com.popover');
goog.require('re_com.alert');
goog.require('re_com.core');
goog.require('re_com.util');
goog.require('re_com.util');
goog.require('reagent.core');
goog.require('reagent.core');
re_com.popover_form_demo.img1_wink_QMARK_ = reagent.core.atom.call(null,false);
re_com.popover_form_demo.img2_wink_QMARK_ = reagent.core.atom.call(null,false);
re_com.popover_form_demo.show_this_popover_QMARK_ = reagent.core.atom.call(null,false);
re_com.popover_form_demo.initial_form_data = reagent.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
re_com.popover_form_demo.form_data = reagent.core.atom.call(null,cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"description","description",-1428560544),new cljs.core.Keyword(null,"drop-down","drop-down",-545329823),new cljs.core.Keyword(null,"radio-group","radio-group",1791520002),new cljs.core.Keyword(null,"email","email",1415816706),new cljs.core.Keyword(null,"password","password",417022471),new cljs.core.Keyword(null,"file","file",-1269645878),new cljs.core.Keyword(null,"listbox-single","listbox-single",1839853292),new cljs.core.Keyword(null,"checkbox3","checkbox3",1295838188),new cljs.core.Keyword(null,"checkbox1","checkbox1",546992302),new cljs.core.Keyword(null,"listbox-multi","listbox-multi",-106650218),new cljs.core.Keyword(null,"datetime","datetime",494675702),new cljs.core.Keyword(null,"checkbox2","checkbox2",-974371525),new cljs.core.Keyword(null,"range","range",1639692286)],["Description text area",null,"2","gregg.ramsey@gmail.com","abc123","",null,false,false,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Item 2","Item 4","Item 7"], null),"2014-07-10T22:40",true,(45)]));
re_com.popover_form_demo.pform_initialise = (function pform_initialise(){cljs.core.reset_BANG_.call(null,re_com.popover_form_demo.initial_form_data,cljs.core.deref.call(null,re_com.popover_form_demo.form_data));
cljs.core.reset_BANG_.call(null,re_com.popover_form_demo.show_this_popover_QMARK_,true);
return re_com.util.console_log_prstr.call(null,"Initialised PRIMARY form: form-data",re_com.popover_form_demo.form_data);
});
re_com.popover_form_demo.pform_submit = (function pform_submit(event){var selected_file = "";cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"file","file",-1269645878),selected_file.value);
cljs.core.reset_BANG_.call(null,re_com.popover_form_demo.show_this_popover_QMARK_,false);
re_com.util.console_log_prstr.call(null,"Submitted PRIMARY form: form-data",re_com.popover_form_demo.form_data);
re_com.alert.add_alert.call(null,"info",new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"heading","heading",-1312171873),"Submitted PRIMARY form",new cljs.core.Keyword(null,"body","body",-2049205669),("Form data submitted: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str(cljs.core.deref.call(null,re_com.popover_form_demo.form_data))))], null));
return false;
});
re_com.popover_form_demo.pform_cancel = (function pform_cancel(){cljs.core.reset_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.deref.call(null,re_com.popover_form_demo.initial_form_data));
cljs.core.reset_BANG_.call(null,re_com.popover_form_demo.show_this_popover_QMARK_,false);
re_com.util.console_log_prstr.call(null,"Cancelled PRIMARY form: form-data",re_com.popover_form_demo.form_data);
re_com.alert.add_alert.call(null,"warning",new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"heading","heading",-1312171873),"Cancelled PRIMARY form",new cljs.core.Keyword(null,"body","body",-2049205669),("Form data reset to original values: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str(cljs.core.deref.call(null,re_com.popover_form_demo.form_data))))], null));
return false;
});
re_com.popover_form_demo.primary_form = (function primary_form(){return new cljs.core.PersistentVector(null, 16, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"padding","padding",1660304693),"5px",new cljs.core.Keyword(null,"background-color","background-color",570434026),"cornsilk",new cljs.core.Keyword(null,"border","border",1444987323),"1px solid #eee"], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-group","div.form-group",-1721134770),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"for","for",-1323786319),"pf-email"], null),"Email address"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input#pf-email.form-control","input#pf-email.form-control",829261857),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"name","name",1843675177),"email",new cljs.core.Keyword(null,"type","type",1174270348),"text",new cljs.core.Keyword(null,"placeholder","placeholder",-104873083),"Type email",new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"email","email",1415816706).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9031_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"email","email",1415816706),p1__9031_SHARP_.target.value);
})], null)], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-group","div.form-group",-1721134770),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"for","for",-1323786319),"pf-password"], null),"Password"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input#pf-password.form-control","input#pf-password.form-control",-641793189),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"name","name",1843675177),"password",new cljs.core.Keyword(null,"type","type",1174270348),"password",new cljs.core.Keyword(null,"placeholder","placeholder",-104873083),"Type password",new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"password","password",417022471).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9032_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"password","password",417022471),p1__9032_SHARP_.target.value);
})], null)], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-group","div.form-group",-1721134770),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"for","for",-1323786319),"pf-desc"], null),"Description"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"textarea#pf-desc.form-control","textarea#pf-desc.form-control",-360855170),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"name","name",1843675177),"description",new cljs.core.Keyword(null,"placeholder","placeholder",-104873083),"Write a description here",new cljs.core.Keyword(null,"rows","rows",850049680),(5),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"description","description",-1428560544).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9033_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"description","description",-1428560544),p1__9033_SHARP_.target.value);
})], null)], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-group","div.form-group",-1721134770),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"for","for",-1323786319),"pf-range"], null),"Range (0-100)"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input#pf-range","input#pf-range",-1341420111),new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"name","name",1843675177),"range",new cljs.core.Keyword(null,"type","type",1174270348),"range",new cljs.core.Keyword(null,"min","min",444991522),"0",new cljs.core.Keyword(null,"max","max",61366548),"100",new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"range","range",1639692286).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9034_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"range","range",1639692286),p1__9034_SHARP_.target.value);
})], null)], null)], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-group","div.form-group",-1721134770),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"for","for",-1323786319),"pf-datetime"], null),"Date/Time"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991)," "], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input#pf-datetime","input#pf-datetime",1098654249),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"name","name",1843675177),"datetime",new cljs.core.Keyword(null,"type","type",1174270348),"datetime-local",new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"font-family","font-family",-667419874),"Consolas"], null),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"datetime","datetime",494675702).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9035_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"datetime","datetime",494675702),p1__9035_SHARP_.target.value);
})], null)], null)], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-group","div.form-group",-1721134770),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"for","for",-1323786319),"pf-file"], null),"File input"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input#pf-file","input#pf-file",1882173671),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),"file",new cljs.core.Keyword(null,"type","type",1174270348),"file"], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p.help-block","p.help-block",-933389757),"This is the Bootstrap help text style."], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h4","h4",2004862993),"Checkboxes and Radio buttons"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.container-fluid","div.container-fluid",3929737),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.row","div.row",133678515),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.col-lg-4","div.col-lg-4",-519713955),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.checkbox","div.checkbox",389009838),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"type","type",1174270348),"checkbox",new cljs.core.Keyword(null,"name","name",1843675177),"cb1",new cljs.core.Keyword(null,"checked","checked",-50955819),new cljs.core.Keyword(null,"checkbox1","checkbox1",546992302).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9036_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"checkbox1","checkbox1",546992302),p1__9036_SHARP_.target.checked);
})], null),"Red (toggle disabled)"], null)], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.checkbox","div.checkbox",389009838),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"type","type",1174270348),"checkbox",new cljs.core.Keyword(null,"name","name",1843675177),"cb2",new cljs.core.Keyword(null,"checked","checked",-50955819),new cljs.core.Keyword(null,"checkbox2","checkbox2",-974371525).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9037_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"checkbox2","checkbox2",-974371525),p1__9037_SHARP_.target.checked);
})], null),"Green (initially checked)"], null)], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.checkbox","div.checkbox",389009838),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"type","type",1174270348),"checkbox",new cljs.core.Keyword(null,"name","name",1843675177),"cb3",new cljs.core.Keyword(null,"disabled","disabled",-1529784218),cljs.core.not.call(null,new cljs.core.Keyword(null,"checkbox1","checkbox1",546992302).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data))),new cljs.core.Keyword(null,"checked","checked",-50955819),new cljs.core.Keyword(null,"checkbox3","checkbox3",1295838188).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9038_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"checkbox3","checkbox3",1295838188),p1__9038_SHARP_.target.checked);
})], null),(cljs.core.truth_(new cljs.core.Keyword(null,"checkbox1","checkbox1",546992302).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)))?"Blue":"Blue (disabled)")], null)], null)], null)], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.col-lg-4","div.col-lg-4",-519713955),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.radio","div.radio",1435221667),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"for","for",-1323786319),"pf-radio1"], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input#pf-radio1","input#pf-radio1",1014180815),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"type","type",1174270348),"radio",new cljs.core.Keyword(null,"name","name",1843675177),"rgroup",new cljs.core.Keyword(null,"value","value",305978217),"1",new cljs.core.Keyword(null,"checked","checked",-50955819),cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"radio-group","radio-group",1791520002).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),"1"),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9039_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"radio-group","radio-group",1791520002),p1__9039_SHARP_.target.value);
})], null),"Hue"], null)], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.radio","div.radio",1435221667),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"for","for",-1323786319),"pf-radio2"], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input#pf-radio2","input#pf-radio2",1777225069),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"type","type",1174270348),"radio",new cljs.core.Keyword(null,"name","name",1843675177),"rgroup",new cljs.core.Keyword(null,"value","value",305978217),"2",new cljs.core.Keyword(null,"checked","checked",-50955819),cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"radio-group","radio-group",1791520002).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),"2"),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9040_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"radio-group","radio-group",1791520002),p1__9040_SHARP_.target.value);
})], null),"Saturation (initially checked)"], null)], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.radio","div.radio",1435221667),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"for","for",-1323786319),"pf-radio3"], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input#pf-radio3","input#pf-radio3",860179950),new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"type","type",1174270348),"radio",new cljs.core.Keyword(null,"name","name",1843675177),"rgroup",new cljs.core.Keyword(null,"value","value",305978217),"3",new cljs.core.Keyword(null,"disabled","disabled",-1529784218),cljs.core.not.call(null,new cljs.core.Keyword(null,"checkbox1","checkbox1",546992302).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data))),new cljs.core.Keyword(null,"checked","checked",-50955819),cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"radio-group","radio-group",1791520002).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),"3"),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9041_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"radio-group","radio-group",1791520002),p1__9041_SHARP_.target.value);
})], null),("Luminance"+cljs.core.str.cljs$core$IFn$_invoke$arity$1((cljs.core.truth_(new cljs.core.Keyword(null,"checkbox1","checkbox1",546992302).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)))?null:" (disabled)")))], null)], null)], null)], null)], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h4","h4",2004862993),"Selects"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.container-fluid","div.container-fluid",3929737),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.row","div.row",133678515),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.col-lg-4","div.col-lg-4",-519713955),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"Drop-down List (Combo Box)"], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"select.form-control","select.form-control",696610397),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"drop-down","drop-down",-545329823).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9042_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"drop-down","drop-down",-545329823),p1__9042_SHARP_.target.value);
})], null),cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"option","option",65132272),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"value","value",305978217),""], null),"-- Select an option --"], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),(0)], null)),(function (){var iter__4267__auto__ = (function iter__9061(s__9062){return (new cljs.core.LazySeq(null,(function (){var s__9062__$1 = s__9062;while(true){
var temp__4126__auto__ = cljs.core.seq.call(null,s__9062__$1);if(temp__4126__auto__)
{var s__9062__$2 = temp__4126__auto__;if(cljs.core.chunked_seq_QMARK_.call(null,s__9062__$2))
{var c__4265__auto__ = cljs.core.chunk_first.call(null,s__9062__$2);var size__4266__auto__ = cljs.core.count.call(null,c__4265__auto__);var b__9064 = cljs.core.chunk_buffer.call(null,size__4266__auto__);if((function (){var i__9063 = (0);while(true){
if((i__9063 < size__4266__auto__))
{var line = cljs.core._nth.call(null,c__4265__auto__,i__9063);cljs.core.chunk_append.call(null,b__9064,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"option","option",65132272),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"value","value",305978217),line], null),("Item "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(line))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),line], null)));
{
var G__9077 = (i__9063 + (1));
i__9063 = G__9077;
continue;
}
} else
{return true;
}
break;
}
})())
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9064),iter__9061.call(null,cljs.core.chunk_rest.call(null,s__9062__$2)));
} else
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9064),null);
}
} else
{var line = cljs.core.first.call(null,s__9062__$2);return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"option","option",65132272),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"value","value",305978217),line], null),("Item "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(line))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),line], null)),iter__9061.call(null,cljs.core.rest.call(null,s__9062__$2)));
}
} else
{return null;
}
break;
}
}),null,null));
});return iter__4267__auto__.call(null,cljs.core.range.call(null,(1),(21)));
})()], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.col-lg-4","div.col-lg-4",-519713955),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"Single-select List Box"], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"select.form-control","select.form-control",696610397),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"size","size",1098693007),(8),new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"listbox-single","listbox-single",1839853292).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9043_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"listbox-single","listbox-single",1839853292),p1__9043_SHARP_.target.value);
})], null),(function (){var iter__4267__auto__ = (function iter__9065(s__9066){return (new cljs.core.LazySeq(null,(function (){var s__9066__$1 = s__9066;while(true){
var temp__4126__auto__ = cljs.core.seq.call(null,s__9066__$1);if(temp__4126__auto__)
{var s__9066__$2 = temp__4126__auto__;if(cljs.core.chunked_seq_QMARK_.call(null,s__9066__$2))
{var c__4265__auto__ = cljs.core.chunk_first.call(null,s__9066__$2);var size__4266__auto__ = cljs.core.count.call(null,c__4265__auto__);var b__9068 = cljs.core.chunk_buffer.call(null,size__4266__auto__);if((function (){var i__9067 = (0);while(true){
if((i__9067 < size__4266__auto__))
{var line = cljs.core._nth.call(null,c__4265__auto__,i__9067);cljs.core.chunk_append.call(null,b__9068,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"option","option",65132272),("Item "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(line))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),line], null)));
{
var G__9078 = (i__9067 + (1));
i__9067 = G__9078;
continue;
}
} else
{return true;
}
break;
}
})())
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9068),iter__9065.call(null,cljs.core.chunk_rest.call(null,s__9066__$2)));
} else
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9068),null);
}
} else
{var line = cljs.core.first.call(null,s__9066__$2);return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"option","option",65132272),("Item "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(line))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),line], null)),iter__9065.call(null,cljs.core.rest.call(null,s__9066__$2)));
}
} else
{return null;
}
break;
}
}),null,null));
});return iter__4267__auto__.call(null,cljs.core.range.call(null,(1),(21)));
})()], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.col-lg-4","div.col-lg-4",-519713955),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"Multi-select List Box"], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"select.form-control","select.form-control",696610397),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"size","size",1098693007),(8),new cljs.core.Keyword(null,"multiple","multiple",1244445549),true,new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"listbox-multi","listbox-multi",-106650218).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.form_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9044_SHARP_){var selected_nodes = p1__9044_SHARP_.target.selectedOptions;var count = selected_nodes.length;var selected_values = (function (){var iter__4267__auto__ = ((function (selected_nodes,count){
return (function iter__9069(s__9070){return (new cljs.core.LazySeq(null,((function (selected_nodes,count){
return (function (){var s__9070__$1 = s__9070;while(true){
var temp__4126__auto__ = cljs.core.seq.call(null,s__9070__$1);if(temp__4126__auto__)
{var s__9070__$2 = temp__4126__auto__;if(cljs.core.chunked_seq_QMARK_.call(null,s__9070__$2))
{var c__4265__auto__ = cljs.core.chunk_first.call(null,s__9070__$2);var size__4266__auto__ = cljs.core.count.call(null,c__4265__auto__);var b__9072 = cljs.core.chunk_buffer.call(null,size__4266__auto__);if((function (){var i__9071 = (0);while(true){
if((i__9071 < size__4266__auto__))
{var index = cljs.core._nth.call(null,c__4265__auto__,i__9071);var item = (selected_nodes[index]).value;cljs.core.chunk_append.call(null,b__9072,item);
{
var G__9079 = (i__9071 + (1));
i__9071 = G__9079;
continue;
}
} else
{return true;
}
break;
}
})())
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9072),iter__9069.call(null,cljs.core.chunk_rest.call(null,s__9070__$2)));
} else
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9072),null);
}
} else
{var index = cljs.core.first.call(null,s__9070__$2);var item = (selected_nodes[index]).value;return cljs.core.cons.call(null,item,iter__9069.call(null,cljs.core.rest.call(null,s__9070__$2)));
}
} else
{return null;
}
break;
}
});})(selected_nodes,count))
,null,null));
});})(selected_nodes,count))
;return iter__4267__auto__.call(null,cljs.core.range.call(null,count));
})();return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.form_data,cljs.core.assoc,new cljs.core.Keyword(null,"listbox-multi","listbox-multi",-106650218),cljs.core.vec.call(null,selected_values));
})], null),(function (){var iter__4267__auto__ = (function iter__9073(s__9074){return (new cljs.core.LazySeq(null,(function (){var s__9074__$1 = s__9074;while(true){
var temp__4126__auto__ = cljs.core.seq.call(null,s__9074__$1);if(temp__4126__auto__)
{var s__9074__$2 = temp__4126__auto__;if(cljs.core.chunked_seq_QMARK_.call(null,s__9074__$2))
{var c__4265__auto__ = cljs.core.chunk_first.call(null,s__9074__$2);var size__4266__auto__ = cljs.core.count.call(null,c__4265__auto__);var b__9076 = cljs.core.chunk_buffer.call(null,size__4266__auto__);if((function (){var i__9075 = (0);while(true){
if((i__9075 < size__4266__auto__))
{var line = cljs.core._nth.call(null,c__4265__auto__,i__9075);cljs.core.chunk_append.call(null,b__9076,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"option","option",65132272),("Item "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(line))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),line], null)));
{
var G__9080 = (i__9075 + (1));
i__9075 = G__9080;
continue;
}
} else
{return true;
}
break;
}
})())
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9076),iter__9073.call(null,cljs.core.chunk_rest.call(null,s__9074__$2)));
} else
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9076),null);
}
} else
{var line = cljs.core.first.call(null,s__9074__$2);return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"option","option",65132272),("Item "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(line))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),line], null)),iter__9073.call(null,cljs.core.rest.call(null,s__9074__$2)));
}
} else
{return null;
}
break;
}
}),null,null));
});return iter__4267__auto__.call(null,cljs.core.range.call(null,(1),(21)));
})()], null)], null)], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"hr","hr",1377740067),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"margin","margin",-995903681),"10px 0 10px"], null)], null)], null),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.core.button,"Apply",re_com.popover_form_demo.pform_submit,new cljs.core.Keyword(null,"class","class",-2030961996),"btn-primary"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991)," "], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.core.button,"Cancel",re_com.popover_form_demo.pform_cancel], null)], null);
});
re_com.popover_form_demo.sform_data = reagent.core.atom.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"email","email",1415816706),"",new cljs.core.Keyword(null,"password","password",417022471),"",new cljs.core.Keyword(null,"remember-me","remember-me",-1046194083),false], null));
re_com.popover_form_demo.sform_submit = (function sform_submit(){re_com.util.console_log_prstr.call(null,"Submitted SECONDARY form: sform-data",re_com.popover_form_demo.sform_data);
re_com.alert.add_alert.call(null,"info",new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"heading","heading",-1312171873),"Submitted SECONDARY form",new cljs.core.Keyword(null,"body","body",-2049205669),("Form data submitted: "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str(cljs.core.deref.call(null,re_com.popover_form_demo.sform_data))))], null));
return false;
});
re_com.popover_form_demo.secondary_form = (function secondary_form(){return new cljs.core.PersistentVector(null, 9, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-inline","div.form-inline",-557536095),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"padding","padding",1660304693),"5px",new cljs.core.Keyword(null,"background-color","background-color",570434026),"cornsilk",new cljs.core.Keyword(null,"border","border",1444987323),"1px solid #eee"], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-group","div.form-group",-1721134770),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label.sr-only","label.sr-only",-964684571),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"for","for",-1323786319),"sf-email"], null),"Email"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input#sf-email.form-control","input#sf-email.form-control",2121076009),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"name","name",1843675177),"email",new cljs.core.Keyword(null,"type","type",1174270348),"text",new cljs.core.Keyword(null,"placeholder","placeholder",-104873083),"Type email",new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"email","email",1415816706).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.sform_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9081_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.sform_data,cljs.core.assoc,new cljs.core.Keyword(null,"email","email",1415816706),p1__9081_SHARP_.target.value);
})], null)], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991)," "], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-group","div.form-group",-1721134770),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label.sr-only","label.sr-only",-964684571),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"for","for",-1323786319),"sf-passord"], null),"Password"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input#sf-passord.form-control","input#sf-passord.form-control",-1367170833),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"name","name",1843675177),"password",new cljs.core.Keyword(null,"type","type",1174270348),"password",new cljs.core.Keyword(null,"placeholder","placeholder",-104873083),"Type password",new cljs.core.Keyword(null,"value","value",305978217),new cljs.core.Keyword(null,"password","password",417022471).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.sform_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9082_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.sform_data,cljs.core.assoc,new cljs.core.Keyword(null,"password","password",417022471),p1__9082_SHARP_.target.value);
})], null)], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991)," "], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.checkbox","div.checkbox",389009838),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input","input",556931961),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"name","name",1843675177),"remember-me",new cljs.core.Keyword(null,"type","type",1174270348),"checkbox",new cljs.core.Keyword(null,"checked","checked",-50955819),new cljs.core.Keyword(null,"checkbox","checkbox",1612615655).cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.sform_data)),new cljs.core.Keyword(null,"on-change","on-change",-732046149),(function (p1__9083_SHARP_){return cljs.core.swap_BANG_.call(null,re_com.popover_form_demo.sform_data,cljs.core.assoc,new cljs.core.Keyword(null,"remember-me","remember-me",-1046194083),p1__9083_SHARP_.target.checked);
})], null),"Remember me"], null)], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991)," "], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.core.button,"Sign in",re_com.popover_form_demo.sform_submit], null)], null);
});
re_com.popover_form_demo.popover_form = (function popover_form(){return new cljs.core.PersistentVector(null, 14, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),"Primary Form"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"p","p",151049309),"Here is a form which has some events"], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.popover_form_demo.primary_form], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"hr","hr",1377740067)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),"Secondary Form - Inline"], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.popover_form_demo.secondary_form], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"hr","hr",1377740067)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),"Image clipping ",new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"color","color",1011675173),"red"], null)], null),"(click left image)"], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"img.img-rounded.smooth","img.img-rounded.smooth",-1998718157),new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"src","src",-1651076051),"img/magdeburg-water-bridge.jpg",new cljs.core.Keyword(null,"alt","alt",-3214426),"Here is the tooltip for this image",new cljs.core.Keyword(null,"style","style",-496642736),cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"width","width",-384071477),"49%",new cljs.core.Keyword(null,"margin-right","margin-right",809689658),"10px",new cljs.core.Keyword(null,"cursor","cursor",1011937484),"hand"], null),(cljs.core.truth_(cljs.core.deref.call(null,re_com.popover_form_demo.img1_wink_QMARK_))?new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"border","border",1444987323),"5px solid purple"], null):null)),new cljs.core.Keyword(null,"on-mouse-over","on-mouse-over",-858472552),(function (){return cljs.core.reset_BANG_.call(null,re_com.popover_form_demo.img1_wink_QMARK_,true);
}),new cljs.core.Keyword(null,"on-mouse-out","on-mouse-out",643448647),(function (){return cljs.core.reset_BANG_.call(null,re_com.popover_form_demo.img1_wink_QMARK_,false);
}),new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){cljs.core.reset_BANG_.call(null,re_com.popover_form_demo.img2_wink_QMARK_,cljs.core.not.call(null,cljs.core.deref.call(null,re_com.popover_form_demo.img2_wink_QMARK_)));
return re_com.util.console_log.call(null,("CLICK-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,re_com.popover_form_demo.img2_wink_QMARK_))));
})], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"img.img-rounded.smooth","img.img-rounded.smooth",-1998718157),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"src","src",-1651076051),"img/Guru.jpg",new cljs.core.Keyword(null,"alt","alt",-3214426),"Here is the tooltip for this image",new cljs.core.Keyword(null,"style","style",-496642736),cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"width","width",-384071477),"49%"], null),(cljs.core.truth_(cljs.core.deref.call(null,re_com.popover_form_demo.img2_wink_QMARK_))?new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"border","border",1444987323),"10px solid green",new cljs.core.Keyword(null,"-webkit-transform","-webkit-transform",-624763371),"rotate(180deg)"], null):null))], null)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"hr","hr",1377740067)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),"Table"], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"table.table.table-bordered.table-striped","table.table.table-bordered.table-striped",1413526814),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"margin-top","margin-top",392161226),"20px"], null)], null),(function (){var iter__4267__auto__ = (function iter__9104(s__9105){return (new cljs.core.LazySeq(null,(function (){var s__9105__$1 = s__9105;while(true){
var temp__4126__auto__ = cljs.core.seq.call(null,s__9105__$1);if(temp__4126__auto__)
{var s__9105__$2 = temp__4126__auto__;if(cljs.core.chunked_seq_QMARK_.call(null,s__9105__$2))
{var c__4265__auto__ = cljs.core.chunk_first.call(null,s__9105__$2);var size__4266__auto__ = cljs.core.count.call(null,c__4265__auto__);var b__9107 = cljs.core.chunk_buffer.call(null,size__4266__auto__);if((function (){var i__9106 = (0);while(true){
if((i__9106 < size__4266__auto__))
{var row = cljs.core._nth.call(null,c__4265__auto__,i__9106);cljs.core.chunk_append.call(null,b__9107,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"tr","tr",-1424774646),(function (){var iter__4267__auto__ = ((function (i__9106,row,c__4265__auto__,size__4266__auto__,b__9107,s__9105__$2,temp__4126__auto__){
return (function iter__9116(s__9117){return (new cljs.core.LazySeq(null,((function (i__9106,row,c__4265__auto__,size__4266__auto__,b__9107,s__9105__$2,temp__4126__auto__){
return (function (){var s__9117__$1 = s__9117;while(true){
var temp__4126__auto____$1 = cljs.core.seq.call(null,s__9117__$1);if(temp__4126__auto____$1)
{var s__9117__$2 = temp__4126__auto____$1;if(cljs.core.chunked_seq_QMARK_.call(null,s__9117__$2))
{var c__4265__auto____$1 = cljs.core.chunk_first.call(null,s__9117__$2);var size__4266__auto____$1 = cljs.core.count.call(null,c__4265__auto____$1);var b__9119 = cljs.core.chunk_buffer.call(null,size__4266__auto____$1);if((function (){var i__9118 = (0);while(true){
if((i__9118 < size__4266__auto____$1))
{var col = cljs.core._nth.call(null,c__4265__auto____$1,i__9118);cljs.core.chunk_append.call(null,b__9119,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),("row-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(row)+" col-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(col))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),col], null)));
{
var G__9124 = (i__9118 + (1));
i__9118 = G__9124;
continue;
}
} else
{return true;
}
break;
}
})())
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9119),iter__9116.call(null,cljs.core.chunk_rest.call(null,s__9117__$2)));
} else
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9119),null);
}
} else
{var col = cljs.core.first.call(null,s__9117__$2);return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),("row-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(row)+" col-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(col))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),col], null)),iter__9116.call(null,cljs.core.rest.call(null,s__9117__$2)));
}
} else
{return null;
}
break;
}
});})(i__9106,row,c__4265__auto__,size__4266__auto__,b__9107,s__9105__$2,temp__4126__auto__))
,null,null));
});})(i__9106,row,c__4265__auto__,size__4266__auto__,b__9107,s__9105__$2,temp__4126__auto__))
;return iter__4267__auto__.call(null,cljs.core.range.call(null,(1),(6)));
})()], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),row], null)));
{
var G__9125 = (i__9106 + (1));
i__9106 = G__9125;
continue;
}
} else
{return true;
}
break;
}
})())
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9107),iter__9104.call(null,cljs.core.chunk_rest.call(null,s__9105__$2)));
} else
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9107),null);
}
} else
{var row = cljs.core.first.call(null,s__9105__$2);return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"tr","tr",-1424774646),(function (){var iter__4267__auto__ = ((function (row,s__9105__$2,temp__4126__auto__){
return (function iter__9120(s__9121){return (new cljs.core.LazySeq(null,((function (row,s__9105__$2,temp__4126__auto__){
return (function (){var s__9121__$1 = s__9121;while(true){
var temp__4126__auto____$1 = cljs.core.seq.call(null,s__9121__$1);if(temp__4126__auto____$1)
{var s__9121__$2 = temp__4126__auto____$1;if(cljs.core.chunked_seq_QMARK_.call(null,s__9121__$2))
{var c__4265__auto__ = cljs.core.chunk_first.call(null,s__9121__$2);var size__4266__auto__ = cljs.core.count.call(null,c__4265__auto__);var b__9123 = cljs.core.chunk_buffer.call(null,size__4266__auto__);if((function (){var i__9122 = (0);while(true){
if((i__9122 < size__4266__auto__))
{var col = cljs.core._nth.call(null,c__4265__auto__,i__9122);cljs.core.chunk_append.call(null,b__9123,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),("row-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(row)+" col-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(col))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),col], null)));
{
var G__9126 = (i__9122 + (1));
i__9122 = G__9126;
continue;
}
} else
{return true;
}
break;
}
})())
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9123),iter__9120.call(null,cljs.core.chunk_rest.call(null,s__9121__$2)));
} else
{return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__9123),null);
}
} else
{var col = cljs.core.first.call(null,s__9121__$2);return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),("row-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(row)+" col-"+cljs.core.str.cljs$core$IFn$_invoke$arity$1(col))], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),col], null)),iter__9120.call(null,cljs.core.rest.call(null,s__9121__$2)));
}
} else
{return null;
}
break;
}
});})(row,s__9105__$2,temp__4126__auto__))
,null,null));
});})(row,s__9105__$2,temp__4126__auto__))
;return iter__4267__auto__.call(null,cljs.core.range.call(null,(1),(6)));
})()], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),row], null)),iter__9104.call(null,cljs.core.rest.call(null,s__9105__$2)));
}
} else
{return null;
}
break;
}
}),null,null));
});return iter__4267__auto__.call(null,cljs.core.range.call(null,(1),(11)));
})()], null)], null);
});
re_com.popover_form_demo.popover_title = (function popover_title(){return new cljs.core.PersistentVector(null, 7, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),"Arbitrary ",new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"strong","strong",269529000),"markup"], null)," example (",new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"color","color",1011675173),"red"], null)], null),"red text"], null),")",new cljs.core.PersistentVector(null, 7, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.core.button,"\u00D7",re_com.popover_form_demo.pform_cancel,new cljs.core.Keyword(null,"class","class",-2030961996),"close",new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"font-size","font-size",-1847940346),"36px",new cljs.core.Keyword(null,"margin-top","margin-top",392161226),"-8px"], null)], null)], null);
});
re_com.popover_form_demo.red_button = (function red_button(){return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"input.btn.btn-danger","input.btn.btn-danger",1981559989),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"type","type",1174270348),"button",new cljs.core.Keyword(null,"value","value",305978217),":right-below",new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"flex-grow","flex-grow",1865160747),(0),new cljs.core.Keyword(null,"flex-shrink","flex-shrink",1481146383),(1),new cljs.core.Keyword(null,"flex-basis","flex-basis",983188475),"auto"], null),new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){if(cljs.core.truth_(cljs.core.deref.call(null,re_com.popover_form_demo.show_this_popover_QMARK_)))
{return re_com.popover_form_demo.pform_cancel.call(null);
} else
{return re_com.popover_form_demo.pform_initialise.call(null);
}
})], null)], null);
});
re_com.popover_form_demo.show = (function show(){var popover_content = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"width","width",-384071477),(800),new cljs.core.Keyword(null,"title","title",636505583),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.popover_form_demo.popover_title], null),new cljs.core.Keyword(null,"close-button?","close-button?",-1030817687),false,new cljs.core.Keyword(null,"body","body",-2049205669),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.popover_form_demo.popover_form], null)], null);var popover_options = new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"arrow-length","arrow-length",934916707),(80),new cljs.core.Keyword(null,"arrow-width","arrow-width",1926673833),(10),new cljs.core.Keyword(null,"backdrop-callback","backdrop-callback",1772830664),re_com.popover_form_demo.pform_cancel,new cljs.core.Keyword(null,"backdrop-opacity","backdrop-opacity",1467395653),.3], null);return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [re_com.popover.popover,new cljs.core.Keyword(null,"right-below","right-below",586981827),re_com.popover_form_demo.show_this_popover_QMARK_,re_com.popover_form_demo.red_button.call(null),popover_content,popover_options], null);
});

//# sourceMappingURL=popover_form_demo.js.map
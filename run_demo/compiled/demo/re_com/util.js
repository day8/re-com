// Compiled by ClojureScript 0.0-2280
goog.provide('re_com.util');
goog.require('cljs.core');
goog.require('clojure.string');
goog.require('clojure.string');
re_com.util.console_log = (function console_log(msg){return console.log(msg);
});
re_com.util.console_log_stringify = (function console_log_stringify(msg,obj){return console.log((''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg)+": "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(JSON.stringify(obj))));
});
re_com.util.console_log_prstr = (function console_log_prstr(msg,obj){return console.log((''+cljs.core.str.cljs$core$IFn$_invoke$arity$1(msg)+": "+cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str(obj))));
});
re_com.util.get_element_by_id = (function get_element_by_id(id){return document.getElementById(id);
});

//# sourceMappingURL=util.js.map
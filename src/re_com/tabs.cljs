(ns re-com.tabs
  (:require
   [re-com.core :as rc]
   [re-com.horizontal-tabs :as horizontal-tabs]
   [re-com.bar-tabs        :as bar-tabs]
   [re-com.pill-tabs       :as pill-tabs]))

(def horizontal-tabs      horizontal-tabs/horizontal-tabs)
(def bar-tabs             bar-tabs/bar-tabs)
(def horizontal-bar-tabs  bar-tabs/horizontal-bar-tabs)
(def vertical-bar-tabs    bar-tabs/vertical-bar-tabs)
(def pill-tabs            pill-tabs/pill-tabs)
(def horizontal-pill-tabs pill-tabs/horizontal-pill-tabs)
(def vertical-pill-tabs   pill-tabs/vertical-pill-tabs)

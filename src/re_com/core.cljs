(ns re-com.core
  (:require-macros
    [re-com.core])
  (:require
    [re-com.alert          :as alert]
    [re-com.box            :as box]
    [re-com.buttons        :as buttons]
    [re-com.checkbox       :as checkbox]
    [re-com.close-button   :as close-button]
    [re-com.datepicker     :as datepicker]
    [re-com.debug          :as debug]
    [re-com.dropdown       :as dropdown]
    [re-com.input-text     :as input-text]
    [re-com.typeahead      :as typeahead]
    [re-com.input-time     :as input-time]
    [re-com.splits         :as splits]
    [re-com.modal-panel    :as modal-panel]
    [re-com.multi-select   :as multi-select]
    [re-com.popover        :as popover]
    [re-com.progress-bar   :as progress-bar]
    [re-com.radio-button   :as radio-button]
    [re-com.selection-list :as selection-list]
    [re-com.slider         :as slider]
    [re-com.tabs           :as tabs]
    [re-com.tag-dropdown   :as tag-dropdown]
    [re-com.text           :as text]
    [re-com.throbber       :as throbber]
    [re-com.tour           :as tour]
    [re-com.v-table        :as v-table]
    [re-com.simple-v-table :as simple-v-table]))

;; -----------------------------------------------------------------------------
;; re-com public API (see also re-com.util)
;; -----------------------------------------------------------------------------

(def alert-box                  alert/alert-box)
(def alert-list                 alert/alert-list)

(def flex-child-style           box/flex-child-style)
(def flex-flow-style            box/flex-flow-style)
(def justify-style              box/justify-style)
(def align-style                box/align-style)
(def scroll-style               box/scroll-style)

(def h-box                      box/h-box)
(def v-box                      box/v-box)
(def box                        box/box)
(def line                       box/line)
(def gap                        box/gap)
(def scroller                   box/scroller)
(def border                     box/border)

(def v-table                    v-table/v-table)
(def show-row-data-on-alt-click v-table/show-row-data-on-alt-click)
(def simple-v-table             simple-v-table/simple-v-table)

(def button                     buttons/button)
(def md-circle-icon-button      buttons/md-circle-icon-button)
(def md-icon-button             buttons/md-icon-button)
(def info-button                buttons/info-button)
(def row-button                 buttons/row-button)
(def hyperlink                  buttons/hyperlink)
(def hyperlink-href             buttons/hyperlink-href)

(def close-button               close-button/close-button)

(def datepicker                 datepicker/datepicker)
(def datepicker-dropdown        datepicker/datepicker-dropdown)

(def single-dropdown            dropdown/single-dropdown)

(def typeahead                  typeahead/typeahead)

(def input-time                 input-time/input-time)

(def h-split                    splits/h-split)
(def v-split                    splits/v-split)

(def input-text                 input-text/input-text)
(def input-password             input-text/input-password)
(def input-textarea             input-text/input-textarea)
(def checkbox                   checkbox/checkbox)
(def radio-button               radio-button/radio-button)
(def slider                     slider/slider)
(def progress-bar               progress-bar/progress-bar)
(def throbber                   throbber/throbber)

(def modal-panel                modal-panel/modal-panel)

(def popover-content-wrapper    popover/popover-content-wrapper)
(def popover-anchor-wrapper     popover/popover-anchor-wrapper)
(def popover-border             popover/popover-border)
(def popover-tooltip            popover/popover-tooltip)

(def selection-list             selection-list/selection-list)
(def multi-select               multi-select/multi-select)
(def tag-dropdown               tag-dropdown/tag-dropdown)

(def horizontal-tabs            tabs/horizontal-tabs)
(def horizontal-bar-tabs        tabs/horizontal-bar-tabs)
(def vertical-bar-tabs          tabs/vertical-bar-tabs)
(def horizontal-pill-tabs       tabs/horizontal-pill-tabs)
(def vertical-pill-tabs         tabs/vertical-pill-tabs)

(def label                      text/label)
(def p                          text/p)
(def p-span                     text/p-span)
(def title                      text/title)

(def make-tour                  tour/make-tour)
(def start-tour                 tour/start-tour)
(def make-tour-nav              tour/make-tour-nav)

(def stack-spy                  debug/stack-spy)
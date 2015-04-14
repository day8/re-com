(ns re-com.core
  (:require [re-com.alert          :as alert]
            [re-com.box            :as box]
            [re-com.buttons        :as buttons]
            [re-com.datepicker     :as datepicker]
            [re-com.dropdown       :as dropdown]
            [re-com.input-time     :as input-time]
            [re-com.splits         :as splits]
            [re-com.misc           :as misc]
            [re-com.modal-panel    :as modal-panel]
            [re-com.popover        :as popover]
            [re-com.selection-list :as selection-list]
            [re-com.tabs           :as tabs]
            [re-com.text           :as text]
            [re-com.tour           :as tour]))

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

(def button                     buttons/button)
(def md-circle-icon-button      buttons/md-circle-icon-button)
(def md-icon-button             buttons/md-icon-button)
(def info-button                buttons/info-button)
(def row-button                 buttons/row-button)
(def hyperlink                  buttons/hyperlink)
(def hyperlink-href             buttons/hyperlink-href)

(def datepicker                 datepicker/datepicker)
(def datepicker-dropdown        datepicker/datepicker-dropdown)

(def single-dropdown            dropdown/single-dropdown)

(def input-time                 input-time/input-time)

(def h-split                    splits/h-split)
(def v-split                    splits/v-split)

(def input-text                 misc/input-text)
(def input-textarea             misc/input-textarea)
(def checkbox                   misc/checkbox)
(def radio-button               misc/radio-button)
(def slider                     misc/slider)
(def progress-bar               misc/progress-bar)
(def throbber                   misc/throbber)

(def modal-panel                modal-panel/modal-panel)

(def popover-content-wrapper    popover/popover-content-wrapper)
(def popover-anchor-wrapper     popover/popover-anchor-wrapper)
(def popover-border             popover/popover-border)
(def popover-tooltip            popover/popover-tooltip)

(def selection-list             selection-list/selection-list)

(def horizontal-tabs            tabs/horizontal-tabs)
(def horizontal-bar-tabs        tabs/horizontal-bar-tabs)
(def vertical-bar-tabs          tabs/vertical-bar-tabs)
(def horizontal-pill-tabs       tabs/horizontal-pill-tabs)
(def vertical-pill-tabs         tabs/vertical-pill-tabs)

(def label                      text/label)
(def title                      text/title)
(def p                          text/p)

(def make-tour                  tour/make-tour)
(def start-tour                 tour/start-tour)
(def make-tour-nav              tour/make-tour-nav)

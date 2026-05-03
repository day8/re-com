(ns re-com.theme.modern
  "Opt-in modern visual theme for re-com.

   Activate from app code with:

     (re-com.core/reg-theme re-com.theme.modern/theme)

   The visual language is Bootstrap-5-flavoured for colours, spacing,
   borders, radii, shadows, and focus rings. Typography (font-family,
   font-size, font-weight, line-height) is intentionally inherited from
   the classic look so apps adopting modern incrementally don't see a
   sudden text re-flow. The `typography` token map is defined for
   reference but not applied by `theme` — apps that also want modern
   fonts can opt in by registering their own thin theme.

   Implementation strategy: the `theme` function (a) merges modern tokens
   into the :variables layer, then (b) dispatches a `styles` multimethod
   on `:part`. Each defmethod adds inline styles (and occasionally classes)
   to override the classic look that the BS3-derived rules in re-com.css
   provide. Inline styles win over class styles, so we don't have to
   strip Bootstrap classes — we just paint on top.

   Token reads use `(:variables (:re-com props))` rather than re-requiring
   `static-variables`, so apps can override individual tokens via their own
   :variables theme layer."
  (:require
   [clojure.string :as str]
   [re-com.theme.util :as tu]
   [re-com.alert-box :as-alias ab]
   [re-com.bar-tabs :as-alias bt]
   [re-com.button :as-alias btn]
   [re-com.checkbox :as-alias cb]
   [re-com.close-button :as-alias clb]
   [re-com.datepicker :as-alias dp]
   [re-com.daterange :as-alias dr]
   [re-com.dropdown :as-alias dd]
   [re-com.h-split :as-alias hs]
   [re-com.horizontal-tabs :as-alias ht]
   [re-com.hyperlink :as-alias hl]
   [re-com.input-text :as-alias it]
   [re-com.input-time :as-alias itime]
   [re-com.modal-panel :as-alias mp]
   [re-com.pill-tabs :as-alias pt]
   [re-com.popover-border :as-alias popb]
   [re-com.popover-title :as-alias popt]
   [re-com.progress-bar :as-alias progress-bar]
   [re-com.radio-button :as-alias rb]
   [re-com.selection-list :as-alias sl]
   [re-com.slider :as-alias slider]
   [re-com.v-split :as-alias vs]))

;; ---------------------------------------------------------------------------
;; Palette — Bootstrap 5 derived hues. Status colours kept close to BS5 so
;; semantic meaning is preserved (success=green, danger=red, etc.).

(def colors
  {:primary             "#0d6efd"
   :primary-hover       "#0b5ed7"
   :primary-active      "#0a58ca"
   :primary-soft        "#cfe2ff"

   :secondary           "#6c757d"
   :secondary-hover     "#5c636a"
   :secondary-soft      "#e2e3e5"

   :success             "#198754"
   :success-hover       "#157347"
   :success-soft        "#d1e7dd"

   :info                "#0dcaf0"
   :info-hover          "#31d2f2"
   :info-soft           "#cff4fc"

   :warning             "#ffc107"
   :warning-hover       "#ffca2c"
   :warning-soft        "#fff3cd"

   :danger              "#dc3545"
   :danger-hover        "#bb2d3b"
   :danger-soft         "#f8d7da"

   :light               "#f8f9fa"
   :dark                "#212529"
   :white               "#ffffff"
   :black               "#000000"

   :body-bg             "#ffffff"
   :body-color          "#212529"
   :text-muted          "#6c757d"
   :border              "#dee2e6"
   :border-strong       "#adb5bd"
   :background-disabled "#e9ecef"
   :shadow              "rgba(0, 0, 0, 0.15)"})

;; ---------------------------------------------------------------------------
;; Linear 4px spacing scale (replaces classic's golden-section).

(def spacing
  {:space-0 "0"
   :space-1 "0.25rem"
   :space-2 "0.5rem"
   :space-3 "0.75rem"
   :space-4 "1rem"
   :space-5 "1.5rem"
   :space-6 "2rem"
   :space-7 "3rem"
   :space-8 "4rem"})

;; ---------------------------------------------------------------------------
;; Typography — defined for reference. NOT applied by `theme` (modern
;; inherits classic typography by default — see ns docstring).

(def typography
  {:font-family-sans     "system-ui, -apple-system, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, sans-serif"
   :font-family-mono     "SFMono-Regular, Menlo, Monaco, Consolas, \"Liberation Mono\", \"Courier New\", monospace"
   :font-size-base       "1rem"
   :font-size-sm         "0.875rem"
   :font-size-lg         "1.125rem"
   :font-size-xs         "0.75rem"
   :font-weight-normal   "400"
   :font-weight-medium   "500"
   :font-weight-semibold "600"
   :font-weight-bold     "700"
   :line-height-base     "1.5"
   :line-height-sm       "1.25"
   :line-height-lg       "1.75"})

(def radius
  {:radius-sm   "0.25rem"
   :radius      "0.375rem"
   :radius-lg   "0.5rem"
   :radius-xl   "1rem"
   :radius-pill "50rem"})

(def shadows
  {:shadow-sm "0 .125rem .25rem rgba(0,0,0,.075)"
   :shadow    "0 .5rem 1rem rgba(0,0,0,.15)"
   :shadow-lg "0 1rem 3rem rgba(0,0,0,.175)"})

(def focus
  {:focus-ring-color "rgba(13,110,253,.25)"
   :focus-ring-width "0.25rem"})

(def static-variables
  (merge colors spacing radius shadows focus))

;; ---------------------------------------------------------------------------
;; Component styling. Multimethod dispatches on :part. Each method adds
;; inline styles to override classic look. Inline wins over class, so
;; classic Bootstrap classes still apply for typography and structural
;; defaults — modern just paints colour/border/spacing/radius on top.

(defmulti styles :part)

(defmethod styles :default [props] props)

;; --- Buttons ---------------------------------------------------------------

(def ^:private bootstrap-variant-classes
  "Bootstrap colour-variant button classes whose styling Modern must not override —
   users picking btn-danger want red, not Modern's neutral surface."
  #{"btn-primary" "btn-success" "btn-info" "btn-warning" "btn-danger" "btn-link"})

(defn- bootstrap-variant?
  [class-prop]
  (let [s (cond-> class-prop (coll? class-prop) (->> (str/join " ")))]
    (boolean (and s (some #(re-find (re-pattern (str "\\b" % "\\b")) s) bootstrap-variant-classes)))))

(defmethod styles ::btn/button [props]
  (let [v        (get-in props [:re-com :variables])
        variant? (bootstrap-variant? (:class props))]
    (tu/style props
              (cond-> {:padding       "0.375rem 0.75rem"
                       :border-radius (:radius v)
                       :transition    "background-color .15s ease, border-color .15s ease, box-shadow .15s ease"}
                (not variant?) (assoc :border     (str "1px solid " (:border v))
                                      :background-color (:white v)
                                      :color      (:body-color v))))))

;; --- Alert box -------------------------------------------------------------

(defn- alert-palette [v alert-type]
  (case alert-type
    :info    [(:info v)    (:info-soft v)    "#055160"]
    :warning [(:warning v) (:warning-soft v) "#664d03"]
    :danger  [(:danger v)  (:danger-soft v)  "#58151c"]
    [(:success v) (:success-soft v) "#0f5132"]))

(defmethod styles ::ab/wrapper
  [{{{:keys [alert-type]} :state} :re-com :as props}]
  (let [v              (get-in props [:re-com :variables])
        [accent bg fg] (alert-palette v alert-type)]
    (tu/style props
              {:background-color bg
               :color         fg
               :border        (str "1px solid " accent)
               :border-left   (str "4px solid " accent)
               :border-radius (:radius v)
               :padding       (:space-4 v)
               :box-shadow    (:shadow-sm v)})))

(defmethod styles ::ab/header [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:margin-bottom (:space-2 v)})))

(defmethod styles ::ab/close-button [props]
  (tu/style props
            {:opacity 0.6}))

;; --- Progress bar ----------------------------------------------------------

(defmethod styles ::progress-bar/container [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:background-color (:light v)
               :border        :none
               :border-radius (:radius-pill v)
               :height        "0.5rem"
               :box-shadow    :none})))

(defmethod styles ::progress-bar/portion [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:background-color (:primary v)
               :border-radius (:radius-pill v)
               :box-shadow    :none
               :transition    "width .3s ease"})))

;; --- Input text ------------------------------------------------------------

(defmethod styles ::it/field
  [{{{:keys [status]} :state} :re-com :as props}]
  (let [v      (get-in props [:re-com :variables])
        border (case status
                 :success (:success v)
                 :warning (:warning v)
                 :error   (:danger v)
                 (:border v))]
    (tu/style props
              {:padding       "0.375rem 0.75rem"
               :height        "auto"
               :border        (str "1px solid " border)
               :border-radius (:radius v)
               :background-color (:white v)
               :color         (:body-color v)
               :box-shadow    :none
               :transition    "border-color .15s ease, box-shadow .15s ease"})))

;; --- Checkbox & radio ------------------------------------------------------

(defmethod styles ::cb/wrapper [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:gap (:space-2 v)})))

(defmethod styles ::cb/input [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:width        "1rem"
               :height       "1rem"
               :margin-top   "0.25rem"
               :accent-color (:primary v)
               :cursor       :pointer})))

;; Note: no ::rb/wrapper override. radio-button's classic theme spaces the
;; label via `:padding-left "8px"` on ::rb/label rather than via a wrapper
;; `:gap`. Adding a wrapper gap here would stack on top of that padding and
;; double the visible spacing under Modern. Checkbox uses the gap mechanism
;; instead (no padding on label), so ::cb/wrapper does need the override
;; below.

(defmethod styles ::rb/input [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:width        "1rem"
               :height       "1rem"
               :margin-top   "0.25rem"
               :accent-color (:primary v)
               :cursor       :pointer})))

;; --- Tabs (horizontal, pill, bar) -----------------------------------------

(defmethod styles ::ht/wrapper [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:border-bottom (str "1px solid " (:border v))
               :gap           (:space-1 v)})))

(defmethod styles ::ht/anchor
  [{{{:keys [selectable]} :state} :re-com :as props}]
  (let [v        (get-in props [:re-com :variables])
        selected (= :selected selectable)]
    (tu/style props
              {:padding       "0.5rem 1rem"
               :border        "1px solid transparent"
               :border-bottom :none
               :border-radius (str (:radius v) " " (:radius v) " 0 0")
               :background-color (if selected (:white v) :transparent)
               :color         (if selected (:body-color v) (:text-muted v))
               :margin-bottom "-1px"
               :transition    "color .15s ease, background-color .15s ease"})))

(defmethod styles ::pt/wrapper [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:gap (:space-1 v)})))

(defmethod styles ::pt/anchor
  [{{{:keys [selectable]} :state} :re-com :as props}]
  (let [v        (get-in props [:re-com :variables])
        selected (= :selected selectable)]
    (tu/style props
              {:padding       "0.5rem 1rem"
               :border-radius (:radius v)
               :background-color (if selected (:primary v) :transparent)
               :color         (if selected (:white v) (:body-color v))
               :transition    "background-color .15s ease, color .15s ease"})))

(defmethod styles ::bt/wrapper [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:border        (str "1px solid " (:border v))
               :border-radius (:radius v)
               :overflow      :hidden})))

(defmethod styles ::bt/button
  [{{{:keys [selectable]} :state} :re-com :as props}]
  (let [v        (get-in props [:re-com :variables])
        selected (= :selected selectable)]
    (tu/style props
              {:padding       "0.375rem 0.75rem"
               :border        :none
               :border-radius "0"
               :background-color (if selected (:primary v) (:white v))
               :color         (if selected (:white v) (:body-color v))
               :transition    "background-color .15s ease"})))

;; --- Dropdown --------------------------------------------------------------

(defmethod styles ::dd/anchor-wrapper [props]
  (let [v                                  (get-in props [:re-com :variables])
        {{{:keys [enable openable]} :state} :re-com} props
        disabled?                          (= :disabled enable)
        open?                              (= :open openable)]
    (tu/style props
              {:background-color (if disabled? (:background-disabled v) (:white v))
               :border        (str "1px solid " (cond disabled? (:border v)
                                                      open?     (:primary v)
                                                      :else     (:border v)))
               :border-radius (:radius v)
               :box-shadow    (if open?
                                (str "0 0 0 " (:focus-ring-width v) " " (:focus-ring-color v))
                                :none)
               :color         (if disabled? (:text-muted v) (:body-color v))
               :height        "auto"
               :padding       "0.375rem 0.75rem"
               :transition    "border-color .15s ease, box-shadow .15s ease"})))

(defmethod styles ::dd/body-wrapper [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:background-color (:white v)
               :border        (str "1px solid " (:border v))
               :border-radius (:radius v)
               :padding       (:space-2 v)
               :box-shadow    (:shadow v)})))

(defmethod styles ::dd/anchor [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:color (:body-color v)})))

;; --- Selection list --------------------------------------------------------

(defmethod styles ::sl/wrapper [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:border        (str "1px solid " (:border v))
               :border-radius (:radius v)
               :background-color (:white v)})))

(defmethod styles ::sl/list-group-item [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:padding    "0.5rem 0.75rem"
               :border     :none
               :background-color :transparent
               :color      (:body-color v)})))

;; --- Modal panel -----------------------------------------------------------

(defmethod styles ::mp/backdrop [props]
  (tu/style props
            {:background-color "rgba(0, 0, 0, 0.5)"}))

(defmethod styles ::mp/child-container
  [{{{:keys [wrap]} :state} :re-com :as props}]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              (when (= wrap :nicely)
                {:background-color (:white v)
                 :padding       (:space-5 v)
                 :border-radius (:radius-lg v)
                 :box-shadow    (:shadow-lg v)
                 :border        :none}))))

;; --- Popover ---------------------------------------------------------------
;; Tooltip-style popovers (re-com `popover-tooltip`) intentionally retain the
;; classic dark-on-light look — they're a distinct visual element. Modern only
;; restyles the regular dialog/info popover surface and inner title.

(defmethod styles ::popb/wrapper
  [{{{:keys [tooltip-style?]} :state} :re-com :as props}]
  (if tooltip-style?
    props
    (let [v (get-in props [:re-com :variables])]
      (tu/style props
                {:border        (str "1px solid " (:border v))
                 :border-radius (:radius-lg v)
                 :box-shadow    (:shadow v)}))))

(defmethod styles ::popb/content
  [{{{:keys [tooltip-style?]} :state} :re-com :as props}]
  (if tooltip-style?
    props
    (let [v (get-in props [:re-com :variables])]
      (tu/style props
                {:padding (:space-3 v)}))))

(defmethod styles ::popt/wrapper [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:padding-bottom (:space-2 v)
               :margin-bottom  (:space-2 v)
               :border-bottom  (str "1px solid " (:border v))})))

;; --- Hyperlink colour ------------------------------------------------------

(defmethod styles ::hl/link
  [{{{:keys [disabled?]} :state} :re-com :as props}]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:color           (if disabled? (:text-muted v) (:primary v))
               :text-decoration :none
               :cursor          (if disabled? :default :pointer)})))

;; --- Close button ----------------------------------------------------------

(defmethod styles ::clb/button [props]
  (let [v                        (get-in props [:re-com :variables])
        {{:keys [hover?]} :state} (:re-com props)]
    (tu/style props
              {:color      (if hover? (:body-color v) (:text-muted v))
               :opacity    (if hover? 1 0.6)
               :transition "color .15s ease, opacity .15s ease"})))

;; --- Datepicker ------------------------------------------------------------

(defmethod styles ::dp/border [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:border        (str "1px solid " (:border v))
               :border-radius (:radius v)
               :box-shadow    (:shadow-sm v)})))

(defmethod styles ::dp/container [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:background-color (:white v)
               :color            (:body-color v)
               :padding          (:space-2 v)})))

(defmethod styles ::dp/header [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:border-bottom (str "1px solid " (:border v))})))

(defmethod styles ::dp/month [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:color       (:body-color v)
               :font-weight "600"
               :padding     (str (:space-1 v) " " (:space-2 v))})))

(defmethod styles ::dp/day [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:color       (:text-muted v)
               :font-weight "500"
               :padding     (:space-1 v)})))

(defmethod styles ::dp/date
  [{{{:keys [selectable? disabled? selected? today?]} :state} :re-com :as props}]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              (cond-> {:border-radius (:radius v)
                       :transition    "background-color .12s ease, color .12s ease"}
                today?                     (assoc :background-color (:primary-soft v)
                                                  :color            (:primary-active v)
                                                  :border-color     :transparent)
                selected?                  (assoc :background-color (:primary v)
                                                  :color            (:white v)
                                                  :border-color     (:primary v))
                (or disabled?
                    (not selectable?))     (assoc :color (:text-muted v))))))

(defn- dp-nav-style [v enabled?]
  {:color       (if enabled? (:primary v) (:text-muted v))
   :cursor      (if enabled? :pointer :default)
   :padding     (:space-1 v)
   :transition  "color .12s ease"})

(defmethod styles ::dp/prev-year
  [{{{:keys [enabled?]} :state} :re-com :as props}]
  (tu/style props (dp-nav-style (get-in props [:re-com :variables]) enabled?)))

(defmethod styles ::dp/prev-month
  [{{{:keys [enabled?]} :state} :re-com :as props}]
  (tu/style props (dp-nav-style (get-in props [:re-com :variables]) enabled?)))

(defmethod styles ::dp/next-month
  [{{{:keys [enabled?]} :state} :re-com :as props}]
  (tu/style props (dp-nav-style (get-in props [:re-com :variables]) enabled?)))

(defmethod styles ::dp/next-year
  [{{{:keys [enabled?]} :state} :re-com :as props}]
  (tu/style props (dp-nav-style (get-in props [:re-com :variables]) enabled?)))

;; --- Daterange -------------------------------------------------------------

(defmethod styles ::dr/border [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:border        (str "1px solid " (:border v))
               :border-radius (:radius v)
               :box-shadow    (:shadow-sm v)
               :background-color (:white v)})))

(defmethod styles ::dr/month-title [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:color       (:body-color v)
               :font-weight "600"})))

(defmethod styles ::dr/year-title [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:color       (:text-muted v)
               :font-weight "500"})))

(defmethod styles ::dr/day-title [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:color       (:text-muted v)
               :font-weight "500"})))

(defmethod styles ::dr/date [props]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:border-radius (:radius v)
               :transition    "background-color .12s ease, color .12s ease"})))

(defn- dr-nav-style [v]
  {:color      (:primary v)
   :cursor     :pointer
   :transition "color .12s ease"})

(defmethod styles ::dr/prev-year [props]
  (tu/style props (dr-nav-style (get-in props [:re-com :variables]))))

(defmethod styles ::dr/prev-month [props]
  (tu/style props (dr-nav-style (get-in props [:re-com :variables]))))

(defmethod styles ::dr/next-month [props]
  (tu/style props (dr-nav-style (get-in props [:re-com :variables]))))

(defmethod styles ::dr/next-year [props]
  (tu/style props (dr-nav-style (get-in props [:re-com :variables]))))

;; --- Slider ----------------------------------------------------------------

(defmethod styles ::slider/input
  [{{{:keys [disabled?]} :state} :re-com :as props}]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:accent-color (if disabled? (:text-muted v) (:primary v))
               :height       "1.25rem"})))

;; --- Input time ------------------------------------------------------------

(defmethod styles ::itime/time-entry
  [{{{:keys [border input-state]} :state} :re-com :as props}]
  (let [v        (get-in props [:re-com :variables])
        disabled? (= input-state :disabled)]
    (tu/style props
              (cond-> {:padding       "0.375rem 0.75rem"
                       :height        "auto"
                       :background-color (if disabled? (:background-disabled v) (:white v))
                       :color         (:body-color v)
                       :transition    "border-color .15s ease, box-shadow .15s ease"}
                (not= border :hidden)
                (assoc :border        (str "1px solid " (:border v))
                       :border-radius (str (:radius v) " 0 0 " (:radius v)))))))

(defmethod styles ::itime/icon-container
  [{{{:keys [border]} :state} :re-com :as props}]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              (cond-> {:background-color (:light v)
                       :color            (:text-muted v)
                       :padding          "0 0.5rem"}
                (not= border :hidden)
                (assoc :border        (str "1px solid " (:border v))
                       :border-left   :none
                       :border-radius (str "0 " (:radius v) " " (:radius v) " 0"))))))

;; --- Splits ----------------------------------------------------------------

(defmethod styles ::hs/splitter
  [{{{:keys [hover]} :state} :re-com :as props}]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              (when (= hover :active)
                {:background-color (:light v)}))))

(defmethod styles ::vs/splitter
  [{{{:keys [hover]} :state} :re-com :as props}]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              (when (= hover :active)
                {:background-color (:light v)}))))

(defn- split-bar-color [v hover]
  (if (= hover :active) (:secondary v) (:border-strong v)))

(defmethod styles ::hs/handle-bar-1
  [{{{:keys [hover]} :state} :re-com :as props}]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:border-right (str "solid 1px " (split-bar-color v hover))})))

(defmethod styles ::hs/handle-bar-2
  [{{{:keys [hover]} :state} :re-com :as props}]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:border-right (str "solid 1px " (split-bar-color v hover))})))

(defmethod styles ::vs/handle-bar-1
  [{{{:keys [hover]} :state} :re-com :as props}]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:border-bottom (str "solid 1px " (split-bar-color v hover))})))

(defmethod styles ::vs/handle-bar-2
  [{{{:keys [hover]} :state} :re-com :as props}]
  (let [v (get-in props [:re-com :variables])]
    (tu/style props
              {:border-bottom (str "solid 1px " (split-bar-color v hover))})))

;; ---------------------------------------------------------------------------
;; Public entrypoint.

(defn theme
  "Modern theme entrypoint. Installs modern design tokens at :variables,
   then applies modern component-level styling via the styles multimethod."
  [props]
  (-> props
      (update-in [:re-com :variables] merge static-variables)
      styles))

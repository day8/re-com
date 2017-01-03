(ns re-com.validate
  (:require
    [clojure.set           :refer [superset?]]
    [re-com.util           :refer [deref-or-value-peek]]
    [reagent.core          :as    reagent]
    [reagent.impl.template :refer [valid-tag?]]
    [goog.string           :as    gstring]
    [goog.date.UtcDateTime]))


;; -- Helpers -----------------------------------------------------------------

(defn left-string
  "Converts obj to a string and truncates it to max-len chars if necessary.
   When truncation is necessary, adds an elipsis to the end"
  [obj max-len]
  (gstring/truncate (str obj) max-len))

(defn log-error
  "Sends a message to the DeV Tools console as an error. Returns false to indicate 'error' condition"
  [& args]
  (.error js/console (apply str args))
  false)

(defn log-warning
  "Sends a message to the DeV Tools console as an warning. Returns true to indicate 'not and error' condition"
  [& args]
  (.warn js/console (apply str args))
  true)


(defn hash-map-with-name-keys
  [v]
  (zipmap (map :name v) v))


(defn extract-arg-data
  "Package up all the relevant data for validation purposes from the xxx-args-desc map into a new map"
  [args-desc]
    {:arg-names      (set (map :name args-desc))
     :required-args  (->> args-desc
                          (filter :required)
                          (map :name)
                          set)
     :validated-args (->> (filter :validate-fn args-desc)
                          vec
                          (hash-map-with-name-keys))})

;; ----------------------------------------------------------------------------
;; Primary validation functions
;; ----------------------------------------------------------------------------

(defn arg-names-valid?
  "returns true if every passed-args is value. Otherwise log the problem and return false"
  [defined-args passed-args]
  (or (superset? defined-args passed-args)
      (let [missing-args (remove defined-args passed-args)]
        (log-error "Invalid argument(s): " missing-args)))) ;; Regent will show the component-path

(defn required-args-passed?
  "returns true if all the required args are supplied. Otherwise log the error and return false"
  [required-args passed-args]
  (or (superset? passed-args required-args)
      (let [missing-args (remove passed-args required-args)]
        (log-error "Missing required argument(s): " missing-args)))) ;; Regent will show the component-path


(defn validate-fns-pass?
  "Gathers together a list of args that have a validator and...
   returns true if all argument values are valid OR are just warnings (log warning to the console).
   Otherwise log an error to the console and return false.
   Validation functions can return:
         - true:   validation success
         - false:  validation failed - use standard error message
         - map:    validation failed - includes two keys:
                                         :status  - :error:   log to console as error
                                                    :warning: log to console as warning
                                         :message - use this string in the message of the warning/error"
  [args-with-validators passed-args component-name]
  (let [validate-arg (fn [[_ v-arg-def]]
                       (let [arg-name        (:name v-arg-def)
                             arg-val         (deref-or-value-peek (arg-name passed-args)) ;; Automatically extract value if it's in an atom
                             required?       (:required v-arg-def)
                             validate-result ((:validate-fn v-arg-def) arg-val)
                             log-msg-base    (str "Validation failed for argument '" arg-name "' in component '" component-name "': ")
                             comp-path       (str " at " (reagent/component-path (reagent/current-component)))
                             warning?        (= (:status validate-result) :warning)]
                         ;(println (str "[" component-name "] " arg-name " = '" (if (nil? arg-val) "nil" (left-string arg-val 200)) "' => " validate-result))
                         (cond
                           (or (true? validate-result)
                               (and (nil? arg-val)          ;; Allow nil values through if the arg is NOT required
                                    (not required?))) true
                           (false? validate-result)  (log-error log-msg-base "Expected '" (:type v-arg-def) "'. Got '" (if (nil? arg-val) "nil" (left-string arg-val 60)) "'" comp-path)
                           (map?   validate-result)  ((if warning? log-warning log-error)
                                                       log-msg-base
                                                       (:message validate-result)
                                                       (when warning? comp-path))
                           :else                      (log-error "Invalid return from validate-fn: " validate-result comp-path))))]
    (->> (select-keys args-with-validators (vec (keys passed-args)))
         (map validate-arg)
         (every? true?))))

(defn validate-args
  "Calls three validation tests:
    - Are arg names valid?
    - Have all required args been passed?
    - Specific valiadation function calls to check arg values if specified
   If they all pass, returns true.
   Normally used for a call to the {:pre...} at the beginning of a function"
  [arg-defs passed-args & component-name]
  (if-not ^boolean js/goog.DEBUG
    true
    (let [passed-arg-keys (set (keys passed-args))]
      (and (arg-names-valid?      (:arg-names      arg-defs) passed-arg-keys)
           (required-args-passed? (:required-args  arg-defs) passed-arg-keys)
           (validate-fns-pass?    (:validated-args arg-defs) passed-args (first component-name))))))


;; ----------------------------------------------------------------------------
;; Custom :validate-fn functions based on (validate-arg-against-set)
;; ----------------------------------------------------------------------------

(def justify-options      [:start :end :center :between :around])
(def align-options        [:start :end :center :baseline :stretch])
(def scroll-options       [:auto :off :on :spill])
(def alert-types          [:none :info :warning :danger])
(def button-sizes         [:regular :smaller :larger])
(def throbber-sizes       [:regular :smaller :small :large])
(def input-status-types   [:success :warning :error :validating])
(def popover-status-types [:success :warning :error :validating :info])
(def title-levels         [:level1 :level2 :level3 :level4])
(def position-options     [:above-left  :above-center :above-right
                           :below-left  :below-center :below-right
                           :left-above  :left-center  :left-below
                           :right-above :right-center :right-below])

(defn validate-arg-against-set
  "Validates the passed argument against the expected set"
  [arg arg-name valid-set]
  (let [arg (deref-or-value-peek arg)]
    (or (not= (some (hash-set arg) valid-set) nil)
        (str "Invalid " arg-name ". Expected one of " valid-set ". Got '" (left-string arg 40) "'"))))

(defn justify-style?       [arg] (validate-arg-against-set arg ":justify-style" justify-options))
(defn align-style?         [arg] (validate-arg-against-set arg ":align-style"   align-options))
(defn scroll-style?        [arg] (validate-arg-against-set arg ":scroll-style"  scroll-options))
(defn alert-type?          [arg] (validate-arg-against-set arg ":alert-type"    alert-types))
(defn button-size?         [arg] (validate-arg-against-set arg ":size"          button-sizes))
(defn throbber-size?       [arg] (validate-arg-against-set arg ":size"          throbber-sizes))
(defn input-status-type?   [arg] (validate-arg-against-set arg ":status"        input-status-types))
(defn popover-status-type? [arg] (validate-arg-against-set arg ":status"        popover-status-types))
(defn title-level-type?    [arg] (validate-arg-against-set arg ":level"         title-levels))
(defn position?            [arg] (validate-arg-against-set arg ":position"      position-options))

;; ----------------------------------------------------------------------------
;; Predefined hiccup lists for streamlined consumption in arg documentation
;; ----------------------------------------------------------------------------

(defn make-code-list
  "Given a vector or list of codes, create a [:span] hiccup vector containing a comma separated list of the codes"
  [codes]
  (into [:span] (interpose ", " (map #(vector :code (str %)) codes))))

(def justify-options-list      (make-code-list justify-options))
(def align-options-list        (make-code-list align-options))
(def scroll-options-list       (make-code-list scroll-options))
(def alert-types-list          (make-code-list alert-types))
(def button-sizes-list         (make-code-list button-sizes))
(def throbber-sizes-list       (make-code-list throbber-sizes))
(def input-status-types-list   (make-code-list input-status-types))
(def popover-status-types-list (make-code-list popover-status-types))
(def title-levels-list         (make-code-list title-levels))
(def position-options-list     (make-code-list position-options))


;; ----------------------------------------------------------------------------
;; Custom :validate-fn functions
;; ----------------------------------------------------------------------------

(def html-attrs #{; ----- HTML attributes (:class and :style commented out as they are not valid in re-com)
                  ; ----- Reference: https://facebook.github.io/react/docs/dom-elements.html#all-supported-html-attributes
                  :accept :accept-charset :access-key :action :allow-full-screen :allow-transparency :alt :async :auto-complete :auto-focus :auto-play :capture
                  :cell-padding :cell-spacing :challenge :char-set :checked :cite #_:class :class-name :cols :col-span :content :content-editable :context-menu :controls
                  :coords :cross-origin :data :date-time :default :defer :dir :disabled :download :draggable :enc-type :form :form-action :form-enc-type :form-method
                  :form-no-validate :form-target :frame-border :headers :height :hidden :high :href :href-lang :html-for :http-equiv :icon :id :input-mode :integrity
                  :is :key-params :key-type :kind :label :lang :list :loop :low :manifest
                  :margin-height :margin-width :max :max-length :media :media-group :method :min :min-length :multiple :muted :name :no-validate :nonce :open :optimum :pattern :placeholder
                  :poster :preload :profile :radio-group :read-only :rel :required :reversed :role :rows :row-span :sandbox :scope :scoped :scrolling :seamless :selected :shape :size :sizes
                  :span :spell-check :src :src-doc :src-lang :src-set :start :step #_:style :summary :tab-index :target :title :type :use-map :value :width :wmode :wrap
                  ; ----- SVG attributes
                  ; ----- Reference: https://facebook.github.io/react/docs/dom-elements.html#all-supported-svg-attributes
                  :accentheight :accumulate :additive :alignment-baseline :allow-reorder :alphabetic :amplitude :arabic-form :ascent :attribute-name :attribute-type
                  :auto-reverse :azimuth :base-frequency :base-profile :baseline-shift :bbox :begin :bias :by :calc-mode :cap-height :clip :clip-path :clip-path-units
                  :clip-rule :color-interpolation :color-interpolation-filters :color-profile :color-rendering :content-script-type :content-style-type :cursor :cx :cy :d
                  :decelerate :descent :diffuse-constant :direction :display :divisor :dominant-baseline :dur :dx :dy :edge-mode :elevation :enable-background :end :exponent
                  :external-resources-required :fill :fill-opacity :fill-rule :filter :filter-res :filter-units :flood-color :flood-opacity :focusable :font-family :font-size
                  :font-size-adjust :font-stretch :font-style :font-variant :font-weight :format :from :fx :fy :g1 :g2 :glyph-name :glyph-orientation-horizontal :glyph-orientation-vertical
                  :glyph-ref :gradient-transform :gradient-units :hanging :horiz-adv-x :horiz-origin-x :ideographic :image-rendering :in :in2 :intercept :k :k1 :k2 :k3 :k4
                  :kernel-matrix :kernel-unit-length :kerning :key-points :key-splines :key-times :length-adjust :letter-spacing :lighting-color :limiting-cone-angle :local
                  :marker-end :marker-height :marker-mid :marker-start :marker-units :marker-width :mask :mask-content-units :mask-units :mathematical :mode :num-octaves
                  :offset :opacity :operator :order :orient :orientation :origin :overflow :overline-position :overline-thickness :paint-order :panose1 :path-length
                  :pattern-content-units :pattern-transform :pattern-units :pointer-events :points :points-at-x :points-at-y :points-at-z :preserve-alpha :preserve-aspect-ratio
                  :primitive-units :r :radius :ref-x :ref-y :rendering-intent :repeat-count :repeat-dur :required-extensions :required-features :restart :result :rotate :rx :ry
                  :scale :seed :shape-rendering :slope :spacing :specular-constant :specular-exponent :speed :spread-method :start-offset :std-deviation :stemh :stemv :stitch-tiles
                  :stop-color :stop-opacity :strikethrough-position :strikethrough-thickness :string :stroke :stroke-dasharray :stroke-dashoffset :stroke-linecap :stroke-linejoin
                  :stroke-miterlimit :stroke-opacity :stroke-width :surface-scale :system-language :table-values :target-x :target-y :text-anchor :text-decoration :text-length
                  :text-rendering :to :transform :u1 :u2 :underline-position :underline-thickness :unicode :unicode-bidi :unicode-range :units-per-em :v-alphabetic :v-hanging
                  :v-ideographic :v-mathematical :values :vector-effect :version :vert-adv-y :vert-origin-x :vert-origin-y :view-box :view-target :visibility :widths :word-spacing
                  :writing-mode :x :x1 :x2 :x-channel-selector :x-height :xlink-actuate :xlink-arcrole :xlink-href :xlink-role :xlink-show :xlink-title :xlink-type :xml-base
                  :xml-lang :xml-space :y :y1 :y2 :y-channel-selector :z :zoom-and-pan
                  ; ----- Event attributes
                  ; ----- Reference: https://facebook.github.io/react/docs/events.html#supported-events
                  :on-copy :on-cut :on-paste :on-composition-end :on-composition-start :on-composition-update :on-key-down
                  :on-key-press :on-key-up :on-focus :on-blur :on-change :on-input :on-submit :on-click
                  :on-context-menu :on-double-click :on-drag :on-drag-end :on-drag-enter :on-drag-exit :on-drag-leave
                  :on-drag-over :on-drag-start :on-drop :on-mouse-down :on-mouse-enter :on-mouse-leave :on-mouse-move
                  :on-mouse-out :on-mouse-over :on-mouse-up :on-select :on-touch-cancel :on-touch-end :on-touch-move
                  :on-touch-start :on-scroll :on-wheel :on-abort :on-can-play :on-can-play-through :on-duration-change
                  :on-emptied :on-encrypted :on-ended :on-error :on-loaded-data :on-loaded-metadata :on-load-start
                  :on-pause :on-play :on-playing :on-progress :on-rate-change :on-seeked :on-seeking :on-stalled
                  :on-suspend :on-time-update :on-volume-change :on-waiting :on-load #_:on-error :on-animation-start
                  :on-animation-end :on-animation-iteration :on-transition-end
                  ; ----- '--capture' versions of the above events
                  :on-copy-capture :on-cut-capture :on-paste-capture :on-composition-end-capture :on-composition-start-capture :on-composition-update-capture :on-key-down-capture
                  :on-key-press-capture :on-key-up-capture :on-focus-capture :on-blur-capture :on-change-capture :on-input-capture :on-submit-capture :on-click-capture
                  :on-context-menu-capture :on-double-click-capture :on-drag-capture :on-drag-end-capture :on-drag-enter-capture :on-drag-exit-capture :on-drag-leave-capture
                  :on-drag-over-capture :on-drag-start-capture :on-drop-capture :on-mouse-down-capture :on-mouse-enter-capture :on-mouse-leave-capture :on-mouse-move-capture
                  :on-mouse-out-capture :on-mouse-over-capture :on-mouse-up-capture :on-select-capture :on-touch-cancel-capture :on-touch-end-capture :on-touch-move-capture
                  :on-touch-start-capture :on-scroll-capture :on-wheel-capture :on-abort-capture :on-can-play-capture :on-can-play-through-capture :on-duration-change-capture
                  :on-emptied-capture :on-encrypted-capture :on-ended-capture :on-error-capture :on-loaded-data-capture :on-loaded-metadata-capture :on-load-start-capture
                  :on-pause-capture :on-play-capture :on-playing-capture :on-progress-capture :on-rate-change-capture :on-seeked-capture :on-seeking-capture :on-stalled-capture
                  :on-suspend-capture :on-time-update-capture :on-volume-change-capture :on-waiting-capture :on-load-capture #_:on-error-capture :on-animation-start-capture
                  :on-animation-end-capture :on-animation-iteration-capture :on-transition-end-capture})

; ----- Reference: https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/data-*
; -----            https://developer.mozilla.org/en-US/docs/Web/Accessibility/ARIA
(def extension-attrs #{:data :aria})

(def css-styles #{; ----- Standard CSS styles
                  ; ----- Reference: https://developer.mozilla.org/en-US/docs/Web/CSS/Reference
                  :align-content :align-items :align-self :all :animation :animation-delay :animation-direction :animation-duration :animation-fill-mode
                  :animation-iteration-count :animation-name :animation-play-state :animation-timing-function :backface-visibility :background
                  :background-attachment :background-blend-mode :background-clip :background-color :background-image :background-origin :background-position
                  :background-repeat :background-size :block-size :border :border-block-end :border-block-end-color :border-block-end-style :border-block-end-width
                  :border-block-start :border-block-start-color :border-block-start-style :border-block-start-width :border-bottom :border-bottom-color
                  :border-bottom-left-radius :border-bottom-right-radius :border-bottom-style :border-bottom-width :border-collapse :border-color :border-image
                  :border-image-outset :border-image-repeat :border-image-slice :border-image-source :border-image-width :border-inline-end :border-inline-end-color
                  :border-inline-end-style :border-inline-end-width :border-inline-start :border-inline-start-color :border-inline-start-style :border-inline-start-width
                  :border-left :border-left-color :border-left-style :border-left-width :border-radius :border-right :border-right-color :border-right-style
                  :border-right-width :border-spacing :border-style :border-top :border-top-color :border-top-left-radius :border-top-right-radius :border-top-style
                  :border-top-width :border-width :bottom :box-decoration-break :box-shadow :box-sizing :break-after :break-before :break-inside :caption-side :ch :clear
                  :clip :clip-path :cm :color :column-count :column-fill :column-gap :column-rule :column-rule-color :column-rule-style :column-rule-width :columns
                  :column-span :column-width :content :counter-increment :counter-reset :cursor :deg :direction :display :dpcm :dpi :dppx :em :empty-cells :ex :filter
                  :flex :flex-basis :flex-direction :flex-flow :flex-grow :flex-shrink :flex-wrap :float :font :font-family :font-feature-settings :font-kerning
                  :font-language-override :font-size :font-size-adjust :font-stretch :font-style :font-synthesis :font-variant :font-variant-alternates :font-variant-caps
                  :font-variant-east-asian :font-variant-ligatures :font-variant-numeric :font-variant-position :font-weight :fr :grad :grid :grid-area :grid-auto-columns
                  :grid-auto-flow :grid-auto-position :grid-auto-rows :grid-column :grid-column-end :grid-column-gap :grid-column-start :grid-gap :grid-row :grid-row-end
                  :grid-row-gap :grid-row-start :grid-template :grid-template-areas :grid-template-columns :grid-template-rows :height :hyphens :hz :image-orientation
                  :image-rendering :image-resolution :ime-mode :in :inherit :initial :inline-size :isolation :justify-content :khz :left :letter-spacing :line-break
                  :line-height :list-style :list-style-image :list-style-position :list-style-type :margin :margin-block-end :margin-block-start :margin-bottom
                  :margin-inline-end :margin-inline-start :margin-left :margin-right :margin-top :marks :mask :mask-clip :mask-composite :mask-image :mask-mode
                  :mask-origin :mask-position :mask-repeat :mask-size :mask-type :max-block-size :max-height :max-inline-size :max-width :min-block-size :min-height
                  :min-inline-size :min-width :mix-blend-mode :mm :ms :object-fit :object-position :offset-block-end :offset-block-start :offset-inline-end
                  :offset-inline-start :opacity :order :orphans :outline :outline-color :outline-offset :outline-style :outline-width :overflow :overflow-wrap
                  :overflow-x :overflow-y :padding :padding-block-end :padding-block-start :padding-bottom :padding-inline-end :padding-inline-start :padding-left
                  :padding-right :padding-top :page-break-after :page-break-before :page-break-inside :pc :perspective :perspective-origin :pointer-events :position
                  :pt :px :quotes :rad :rem :resize :revert :right :ruby-align :ruby-merge :ruby-position :s :scroll-behavior :scroll-snap-coordinate :scroll-snap-destination
                  :scroll-snap-type :shape-image-threshold :shape-margin :shape-outside :table-layout :tab-size :text-align :text-align-last :text-combine-upright
                  :text-decoration :text-decoration-color :text-decoration-line :text-decoration-style :text-emphasis :text-emphasis-color :text-emphasis-position
                  :text-emphasis-style :text-indent :text-orientation :text-overflow :text-rendering :text-shadow :text-transform :text-underline-position :top
                  :touch-action :transform :transform-box :transform-origin :transform-style :transition :transition-delay :transition-duration :transition-property
                  :transition-timing-function :turn :unicode-bidi :unicode-range :unset :vertical-align :vh :visibility :vmax :vmin :vw :white-space :widows :width
                  :will-change :word-break :word-spacing :word-wrap :writing-mode :z-index
                  ; ----- Browser specific styles
                  :-webkit-user-select :-moz-user-select :-ms-user-select :user-select
                  :-webkit-flex-flow :-webkit-flex-direction :-webkit-flex-wrap :-webkit-justify-content :-webkit-align-items :-webkit-align-content
                  :-webkit-flex :-webkit-flex-grow :-webkit-flex-shrink :-webkit-flex-basis :-webkit-order :-webkit-align-self})

(defn string-or-hiccup?
  "Returns true if the passed argument is either valid hiccup or a string, otherwise false/error"
  [arg]
  (valid-tag? (deref-or-value-peek arg)))

(defn vector-of-maps?
  "Returns true if the passed argument is a vector of maps (either directly or contained in an atom), otherwise false/error
   Notes:
    - actually it also accepts a list of maps (should we rename this? Potential long/ugly names: sequential-of-maps?, vector-or-list-of-maps?)
    - vector/list can be empty
    - only checks the first element in the vector/list"
  [arg]
  (let [arg (deref-or-value-peek arg)]
    (and (sequential? arg) ;; Allows lists as well
         (or (empty? arg)
             (map? (first arg))))))

(defn css-style?
  "Returns true if the passed argument is a valid CSS style.
   Otherwise returns a warning map"
  [arg]
  (if-not ^boolean js/goog.DEBUG
    true
    (let [arg (deref-or-value-peek arg)]
      (and (map? arg)
           (let [arg-keys (keys arg)]
             (or (superset? css-styles arg-keys)
                 {:status  :warning
                  :message (str "Unknown CSS style(s): " (remove css-styles arg-keys))}))))))

(defn extension-attribute?
  "Returns truthy if the attribute name is an extension attribute, that is data-* or aria-*, otherwise falsey."
  ([attr]
   (let [attr (name attr)
         ext? #(and (= (.indexOf attr %) 0)
                       (> (count attr) (count %)))]
     (some (comp ext? #(str % "-") name) extension-attrs))))

(defn invalid-html-attrs
  "Returns the subset of HTML attributes contained in the passed argument that are not valid HTML attributes."
  [attrs]
  (remove #(or (html-attrs %)
               (extension-attribute? %))
          attrs))

(defn html-attr?
  "Returns true if the passed argument is a valid HTML, SVG or event attribute.
   Otherwise returns a warning map.
   Notes:
    - Prevents :class and :style attributes"
  [arg]
  (if-not ^boolean js/goog.DEBUG
    true
    (let [arg (deref-or-value-peek arg)]
      (and (map? arg)
           (let [arg-keys        (set (keys arg))
                 contains-class? (contains? arg-keys :class)
                 contains-style? (contains? arg-keys :style)
                 result   (cond
                            contains-class? ":class not allowed in :attr argument"
                            contains-style? ":style not allowed in :attr argument"
                            :else           (when-let [invalid (not-empty (invalid-html-attrs arg-keys))]
                                              (str "Unknown HTML attribute(s): " invalid)))]
             (or (nil? result)
                 {:status  (if (or contains-class? contains-style?) :error :warning)
                  :message result}))))))

(defn goog-date?
  "Returns true if the passed argument is a valid goog.date.UtcDateTime, otherwise false/error"
  [arg]
  (let [arg (deref-or-value-peek arg)]
    (instance? js/goog.date.UtcDateTime arg)))

(defn regex?
  "Returns true if the passed argument is a valid regular expression, otherwise false/error"
  [arg]
  (let [arg (deref-or-value-peek arg)]
    (instance? js/RegExp arg)))

(defn number-or-string?
  "Returns true if the passed argument is a number or a string, otherwise false/error"
  [arg]
  (let [arg (deref-or-value-peek arg)]
    (or (number? arg) (string? arg))))

(defn string-or-atom?
  "Returns true if the passed argument is a string (or a string within an atom), otherwise false/error"
  [arg]
  (string? (deref-or-value-peek arg)))

(defn set-or-atom?
  "Returns true if the passed argument is a set (or a set within an atom), otherwise false/error"
  [arg]
  (set? (deref-or-value-peek arg)))

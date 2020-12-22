(ns com.example.ui.video-tag-forms
  (:require
    ;[com.example.model.item :as item]
    [com.example.model.session :as session]
    [com.fulcrologic.rad.picker-options :as picker-options]
    #?(:cljs [com.fulcrologic.semantic-ui.collections.form.ui-form-text-area :refer [ui-form-text-area]])
    #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom]
       :cljs [com.fulcrologic.fulcro.dom :as dom])
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.rad.control :as control]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.form-options :as fo]
    [com.fulcrologic.rad.report :as report]
    [com.fulcrologic.rad.report-options :as ro]
    [taoensso.timbre :as log]
    [com.example.model.category :as category]
    [com.example.model.youtube-video :as youtube]
    [com.example.model.video-tag :as video-tag]
    [com.example.model.session-tag :as session-tag]
    [cljs.pprint :as pp]))

(defsc TagsQuery [_ _]
  {:query [:session-tag/id :session-tag/session :session-tag/session-id-2 :session-tag/tag-id-2]
   :ident :session-tag/id})


; this is to edit the video tags
(form/defsc-form VideoTagForm [this props]
  {fo/id           video-tag/id
   fo/attributes   [video-tag/tag-name]
   fo/route-prefix "video-tag"
   fo/title        "Edit Video Tag"})

;(dom/div :.ui.container.grid
;  "Hello!")

(defn create-tag-names [{:session-tag/keys [session] :as tags}]
  (println "create-tag-names: " tags)
  (assoc tags :session/tag-name "abc"))

; this is to associate tags with sessions
(form/defsc-form SessionTagsSubForm [this props]
  {fo/id           session-tag/id
   fo/attributes   [session-tag/tag-id-2]
   fo/triggers     {:derive-fields (fn [new-form-tree]
                                     (println "derived")
                                     (create-tag-names new-form-tree))}
   ;fo/field-styles  {:session-tag/tag-name :pick-one}
   ;fo/field-options {:session-tag/tag-name
   ;                  {::picker-options/query-key       :session-tag/all-session-tags
   ;                   ::picker-options/query-component TagsQuery
   ;                                                   ;(fn [_ options]
   ;                                                   ;  (mapv
   ;                                                   ;    (fn [{:category/keys [id label]}]
   ;                                                   ;      {:text (str label) :value [:category/id id]}
   ;                                                   ;      (sort-by :category/label options))))
   ;                   ::picker-options/options-xform
   ;                       (fn [_ options]
   ;                         (println "SessionTagsSubForm: ::picker-options/options-xform: " options)
   ;                         (mapv
   ;                           (fn [{:video-tag/keys [id name]}]
   ;                             (println "video-tag: ::picker-options/options-xform: " id name)
   ;                             {:text (str name) :value [:video-tag/id id]})
   ;                           (sort-by :video-tag/name options)))
   ;                   ::picker-options/cache-time-ms   30000}}
   fo/route-prefix "session-video-tag"
   fo/title        "Edit Session Tags"})
  ;(do
  ;  ;(println this)
  ;  (js/console.log "this")
  ;  (pp/pprint this)
  ;  (js/console.log "props")
  ;  (pp/pprint props)))

      ;(js/console.log this)
      ;(js/console.log props)))


(report/defsc-report VideoTagReport [this props]
  {ro/title               "Tags Report"
   ro/source-attribute    :video-tag/all-tags
   ro/row-pk              video-tag/id
   ro/columns             [video-tag/tag-name]

   ro/paginate?           true
   ro/page-size           20

   ;session/speakers session/stype session/title session/venue session/start-time-utc]

   ;ro/row-visible?        (fn [filter-parameters row] (let [{::keys [category]} filter-parameters
   ;                                                         row-category (get row :category/label)]
   ;                                                     (or (= "" category) (= category row-category))))
   ;
   ;;; A sample server-query based picker that sets a local parameter that we use to filter rows.
   ;ro/controls            {::category {:type                          :picker
   ;                                    :local?                        true
   ;                                    :label                         "Category"
   ;                                    :default-value                 ""
   ;                                    :action                        (fn [this] (report/filter-rows! this))
   ;                                    picker-options/cache-time-ms   30000
   ;                                    picker-options/cache-key       :all-category-options
   ;                                    picker-options/query-key       :category/all-categories
   ;                                    picker-options/query-component category/Category
   ;                                    picker-options/options-xform   (fn [_ categories]
   ;                                                                     (into [{:text "All" :value ""}]
   ;                                                                           (map
   ;                                                                             (fn [{:category/keys [label]}]
   ;                                                                               {:text label :value label}))
   ;                                                                           categories))}}

   ; If defined: sort is applied to rows after filtering (client-side)
   ro/initial-sort-params {:sort-by          :video-tag/name
                           ;:sortable-columns #{}
                           ;:session/title :session/speakers :session/venue}
                           ; :session/stype
                           :ascending?       true}

   ro/controls            {::new-tag   {:type   :button
                                        :local? true
                                        :label  "New Tag"
                                        :action (fn [this _] (form/create! this VideoTagForm))}}


   ro/form-links          {video-tag/tag-name VideoTagForm}

   ;ro/links               {:category/label (fn [this {:category/keys [label]}]
   ;                                          (control/set-parameter! this ::category label)
   ;                                          (report/filter-rows! this))}

   ro/run-on-mount?       true
   ro/route               "video-tag"})


(comment
  (comp/get-query VideoTagReport)
  (comp/get-query YouTubeForm))


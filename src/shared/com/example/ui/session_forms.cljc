(ns com.example.ui.session-forms
  (:require
    ;[com.example.model.item :as item]
    [com.example.model.session :as session]
    [com.example.model.video-tag :as video-tag]
    [com.example.ui.video-tag-forms :as video-tag-form]
    [com.fulcrologic.rad.picker-options :as picker-options]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.rad.control :as control]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.form-options :as fo]
    [com.fulcrologic.rad.report :as report]
    [com.fulcrologic.rad.report-options :as ro]
    [taoensso.timbre :as log]
    [com.example.model.category :as category]
    [clojure.pprint :as pp]))


(form/defsc-form SessionForm [this props]
  {fo/id           session/id
   fo/attributes   [
                    ;session/id
                    session/title
                    ;session/venue
                    session/speakers
                    ;session/stype
                    session/start-time-utc
                    session/tags]

   fo/subforms     {:session/tags {fo/ui          video-tag-form/SessionTagsSubForm
                                   fo/can-delete? (fn [_ _] true)
                                   fo/can-add?    (fn [this v]
                                                    (println "can add? v: " (clojure.pprint/pprint v))
                                                    true)}}
   ;fo/field-styles  {:item/category :pick-one}
   ;fo/field-options {:item/category {::picker-options/query-key       :category/all-categories
   ;                                  ::picker-options/query-component CategoryQuery
   ;                                  ::picker-options/options-xform   (fn [_ options] (mapv
   ;                                                                                     (fn [{:category/keys [id label]}]
   ;                                                                                       {:text (str label) :value [:category/id id]})
   ;                                                                                     (sort-by :category/label options)))
   ;                                  ::picker-options/cache-time-ms   30000}}

   ; Jakub: why is layout use symbols, where row options use attrs?
   ;fo/layout         [[:session/speakers :session/title :session/stype :session/venue
   ;                    :session/start-time-utc]
   ;                   [:session/tags]]

   fo/route-prefix "session"
   fo/title        "Edit Session"})

(report/defsc-report SessionReport [this props]
  {ro/title               "Session Report"
   ro/source-attribute    :session/all-sessions
   ro/row-pk              session/id
   ro/columns             [session/speakers session/title session/stype session/venue
                           session/start-time-utc session/tags]


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
   ro/initial-sort-params {:sort-by          :session/title
                           :sortable-columns #{:session/title :session/speakers :session/venue}
                           ; :session/stype
                           :ascending?       true}


   ; TODO: add link to add tags here
   ;ro/row-actions
   ; ro/row-actions              [{:label     "Select"
   ;                                                    :action    (fn [report-instance {:tem-organization/keys [organization-number]}]
   ;                                                                 (comp/transact!
   ;                                                                   report-instance
   ;                                                                   [(mutations/set-selected-org {:orgnr organization-number})]))}]
   ;ro/row-actions              [{:label     "Select"
   ;                                                    :action    (fn [report-instance row]
   ;                                                                 (println "from session-row-actions: " row)
   ;                                                                 (js/console.log row)
   ;                                                                 (comp/transact!
   ;                                                                   report-instance
   ;                                                                   '[(mutations/set-selected-org {:orgnr organization-number})]))}]
   ro/form-links          {session/speakers SessionForm}

   ;ro/links               {:category/label (fn [this {:category/keys [label]}]
   ;                                          (control/set-parameter! this ::category label)
   ;                                          (report/filter-rows! this))}

   ro/run-on-mount?       true
   ro/route               "session-report"})


(comment
  (comp/get-query SessionReport)
  (comp/get-query SessionForm))
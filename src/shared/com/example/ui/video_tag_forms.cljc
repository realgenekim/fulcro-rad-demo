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
    [com.example.model.video-tag :as video-tag]))


(form/defsc-form VideoTagForm [this props]
  {fo/id           video-tag/id
   fo/attributes   [
                    ;session/id
                    video-tag/tag-name]
                    ;youtube/video-id
                    ;youtube/description ; <===
                    ;youtube/playlist-id]
                    ;youtube/url]
   ;youtube/playlist-id
   ;session/title
   ;;session/venue
   ;session/speakers
   ;;session/stype
   ;session/start-time-utc]
   ;fo/field-style  :pick-one
   ;fo/field-styles  {:item/category :pick-one}
   ;fo/field-options {:item/category {::picker-options/query-key       :category/all-categories
   ;                                  ::picker-options/query-component CategoryQuery
   ;                                  ::picker-options/options-xform   (fn [_ options] (mapv
   ;                                                                                     (fn [{:category/keys [id label]}]
   ;                                                                                       {:text (str label) :value [:category/id id]})
   ;                                                                                     (sort-by :category/label options)))
   ;                                  ::picker-options/cache-time-ms   30000}}
   fo/route-prefix "video-tag"
   fo/title        "Edit Video Tag"})
(dom/div :.ui.container.grid
  "Hello!")


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


   ro/form-links          {video-tag/tag-name VideoTagForm}

   ;ro/links               {:category/label (fn [this {:category/keys [label]}]
   ;                                          (control/set-parameter! this ::category label)
   ;                                          (report/filter-rows! this))}

   ro/run-on-mount?       true
   ro/route               "video-tag"})


(comment
  (comp/get-query VideoTagReport)
  (comp/get-query YouTubeForm))


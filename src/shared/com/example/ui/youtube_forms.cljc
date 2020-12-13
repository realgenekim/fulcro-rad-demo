(ns com.example.ui.youtube-forms
  (:require
    ;[com.example.model.item :as item]
    [com.example.model.session :as session]
    [com.fulcrologic.rad.picker-options :as picker-options]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.rad.control :as control]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.form-options :as fo]
    [com.fulcrologic.rad.report :as report]
    [com.fulcrologic.rad.report-options :as ro]
    [taoensso.timbre :as log]
    [com.example.model.category :as category]
    [com.example.model.youtube-video :as youtube]))

;(defsc CategoryQuery [_ _]
;  {:query [:category/id :category/label]
;   :ident :category/id})

;(form/defsc-form YouTubeForm [this props]
;  {fo/id            youtube/id
;   fo/attributes    [
;                     ;session/id
;                     youtube/title
;                     youtube/description
;                     youtube/playlist-id
;                     youtube/video-id]
;                     ;session/title
;                     ;;session/venue
;                     ;session/speakers
;                     ;;session/stype
;                     ;session/start-time-utc]
;   ;fo/field-styles  {:item/category :pick-one}
;   ;fo/field-options {:item/category {::picker-options/query-key       :category/all-categories
;   ;                                  ::picker-options/query-component CategoryQuery
;   ;                                  ::picker-options/options-xform   (fn [_ options] (mapv
;   ;                                                                                     (fn [{:category/keys [id label]}]
;   ;                                                                                       {:text (str label) :value [:category/id id]})
;   ;                                                                                     (sort-by :category/label options)))
;   ;                                  ::picker-options/cache-time-ms   30000}}
;   fo/route-prefix  "youtube"
;   fo/title         "Edit Session"})

(report/defsc-report YouTubeReport [this props]
  {ro/title               "YouTube Report"
   ro/source-attribute    :youtube-video/all-videos
   ro/row-pk              youtube/id
   ro/columns             [youtube/position youtube/title
                           youtube/description youtube/playlist-id  youtube/video-id]

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
   ro/initial-sort-params {:sort-by          :youtube-video/position
                           :sortable-columns #{:youtube-video/playlist-id
                                               :youtube-video/title
                                               :youtube-video/position}
                                               ;:session/title :session/speakers :session/venue}
                           ; :session/stype
                           :ascending?       true}


   ;ro/form-links          {youtube/title YouTubeForm}

   ;ro/links               {:category/label (fn [this {:category/keys [label]}]
   ;                                          (control/set-parameter! this ::category label)
   ;                                          (report/filter-rows! this))}

   ro/run-on-mount?       true
   ro/route               "youtube-report"})


(comment
  (comp/get-query SessionReport)
  (comp/get-query SessionForm))
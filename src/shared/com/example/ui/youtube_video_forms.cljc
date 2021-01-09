(ns com.example.ui.youtube-video-forms
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
    [com.example.model.mutations :as mymutations]
    [clojure.pprint :as pp]))
    ;[com.fulcrologic.fulcro.dom-server :as dom]))

;(defsc CategoryQuery [_ _]
;  {:query [:category/id :category/label]
;   :ident :category/id})

(form/defsc-form YouTubeForm [this props]
  {fo/id           youtube/id
   fo/attributes   [
                    ;session/id
                    youtube/title
                    youtube/video-id
                    youtube/description ; <===
                    ;youtube/playlist-id
                    youtube/url]
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
   fo/route-prefix "youtube"
   fo/title        "Edit YouTube Video"})
  ;(dom/div :.ui.container.grid
  ;  "Hello!"))


(report/defsc-report YouTubeReportAll [this props]
  {ro/title               "All YouTube Report"
   ro/source-attribute    :youtube-video/all-videos
   ro/row-pk              youtube/id
   ro/columns             [youtube/position youtube/title
                           youtube/description youtube/playlist-id youtube/video-id
                           youtube/url]

   ;ro/paginate?           true
   ;ro/page-size           20

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

   ro/row-actions         [{:label  "Select/Download"
                            :action (fn [report-instance row]
                                      (println "from youtube-row-actions: " row)
                                      ;#?{:cljs (js/console.log row)}
                                      (comp/transact!
                                        report-instance
                                        [(mymutations/fetch-vimeo-entry (select-keys row
                                                                                     [:youtube-video/id]))]))}]
   ;(comp/transact!
   ;  report-instance
   ;  '[(mutations/set-selected-org {:orgnr organization-number})]))}]


   ro/form-links          {youtube/title YouTubeForm}

   ;ro/links               {:category/label (fn [this {:category/keys [label]}]
   ;                                          (control/set-parameter! this ::category label)
   ;                                          (report/filter-rows! this))}

   ro/run-on-mount?       true
   ro/route               "youtube-report-all"})

(def by-playlist
  {[:youtube-video/playlist-id "PLvk9Yh_MWYuwXC0iU5EAB1ryI62YpPHR9"]
   #:youtube-video{:by-playlist [{:db/id                     1011550698537772,
                                  :youtube-video/description "DOES19 Las Vegas
                                                                                                                              DOES 2019 US
                                                                                                                              DevOps Enterprise Summit
                                                                                                                              https://events.itrevolution.com/us/",
                                  :youtube-video/id          "UEx2azlZaF9NV1l1d1hDMGlVNUVBQjFyeUk2MllwUEhSOS5GM0Q3M0MzMzY5NTJFNTdE",
                                  :youtube-video/playlist-id #:db{:id 47476912088351523},
                                  :youtube-video/title       "Tuesday Opening Remarks - Gene Kim and Jeff Gallimore",
                                  :youtube-video/url         "https://www.youtube.com/watch?v=UrpQURdh2kk",
                                  :youtube-video/video-id    "UrpQURdh2kk",
                                  :youtube-video/position    8}
                                 {:db/id                     2898312651801381,
                                  :youtube-video/description "DOES19 Las Vegas
                                                                                                                              DOES 2019 US
                                                                                                                              DevOps Enterprise Summit
                                                                                                                              https://events.itrevolution.com/us/",
                                  :youtube-video/id          "UEx2azlZaF9NV1l1d1hDMGlVNUVBQjFyeUk2MllwUEhSOS4xMkVGQjNCMUM1N0RFNEUx",
                                  :youtube-video/playlist-id #:db{:id 47476912088351523},
                                  :youtube-video/title       "Opening Remarks -  Gene Kim",
                                  :youtube-video/url         "https://www.youtube.com/watch?v=az3BOHoiiS0",
                                  :youtube-video/video-id    "az3BOHoiiS0",
                                  :youtube-video/position    1}]}})

(report/defsc-report YouTubeReportByPlaylist [this props]
  {ro/title               "YouTube Report By Playlist"
   ro/source-attribute    :youtube-video/by-playlist
   ;ro/source-attribute    :youtube-video/all-videos
   ro/row-pk              youtube/id
   ro/columns             [;youtube/id
                           youtube/title
                           youtube/description
                           youtube/url]
   ; youtube/position youtube/playlist-id youtube/video-id
   ;                           youtube/url

   ; input: :youtube-playlist/id
   ro/controls {:youtube-playlist/id {:type   :string
                                      :local? true
                                      :label  "Playlist ID"}}

   ro/row-actions      [{:label  "Download Video"
                         :action (fn [report-instance row]
                                   (println "from youtube-row-actions: " row))}]
                                   ; [this form-class entity-id]
                                   ;(form/edit! report-instance YouTubePlaylistForm
                                   ;            (:youtube-playlist/id row)))}]

   ; If defined: sort is applied to rows after filtering (client-side)
   ;ro/initial-sort-params {:sort-by          :youtube-video/position
   ;                        :sortable-columns #{:youtube-video/playlist-id
   ;                                            :youtube-video/title
   ;                                            :youtube-video/position}
   ;                        ;:session/title :session/speakers :session/venue}
   ;                        ; :session/stype
   ;                        :ascending?       true}
   ;
   ;ro/row-actions         [{:label  "Select"
   ;                         :action (fn [report-instance row]
   ;                                   (println "from youtube-row-actions: " row)
   ;                                   ;#?{:cljs (js/console.log row)}
   ;                                   (comp/transact!
   ;                                     report-instance
   ;                                     [(mymutations/fetch-vimeo-entry (select-keys row
   ;                                                                                  [:youtube-video/id]))]))}]

   ro/form-links          {youtube/title YouTubeForm}

   ;ro/links               {:category/label (fn [this {:category/keys [label]}]
   ;                                          (control/set-parameter! this ::category label)
   ;                                          (report/filter-rows! this))}

   ro/run-on-mount?       true
   ro/route               "youtube-report-by-playlist"})
  ;(dom/div
  ;  (dom/pre (with-out-str (pp/pprint props)))))


(comment
  (comp/get-query YouTubeReportAll)
  (comp/get-query YouTubeForm))
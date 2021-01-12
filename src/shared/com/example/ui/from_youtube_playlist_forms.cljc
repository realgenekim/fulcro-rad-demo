(ns com.example.ui.from-youtube-playlist-forms
  (:require
    [com.fulcrologic.rad.picker-options :as picker-options]
    #?(:cljs [com.fulcrologic.semantic-ui.collections.form.ui-form-text-area :refer [ui-form-text-area]])
    #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom]
       :cljs [com.fulcrologic.fulcro.dom :as dom])
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.rad.control :as control]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.form-options :as fo]
    [com.fulcrologic.rad.report :as report]
    [com.fulcrologic.rad.report-options :as ro]
    [taoensso.timbre :as log]
    [com.example.model.category :as category]
    [com.example.model.youtube-video :as youtube]
    [com.example.model.from-youtube-playlist :as yt-playlist]
    [com.example.ui.from-youtube-video-forms :as yt-video-forms]
    [com.fulcrologic.rad.routing :as rroute]
    [com.example.model.mutations :as mymutations]
    [clojure.pprint :as pp]))

(declare FromYouTube-PlaylistReport FromYouTube-PlaylistReport-Row)

(defn- get-all-report-rows
  " gets report rows from app state "
  []
  #?(:cljs
     (let [current-state   (app/current-state com.example.client/app)
           path            (comp/get-ident FromYouTube-PlaylistReport {})
           ; => [:com.fulcrologic.rad.report/id :com.example.ui.from-youtube-video-forms/FromYouTubeVideoReport]
           starting-entity (get-in current-state path)
           ; this is exactly what shows up in Fulcro Inspect: at the path ^^
           ; => #:ui{:controls [],
           ;     :current-rows [[:from-youtube-video/id "UEx2azlZaF9NV1l1eXNFa0M4bFFDbV85dnBFRmgyZUNyai41NkI0NEY2RDEwNTU3Q0M2"]
           ;                    [:from-youtube-video/id "UEx2azlZaF9NV1l1eXNFa0M4bFFDbV85dnBFRmgyZUNyai4yODlGNEE0NkRGMEEzMEQy"]
           ;                    [:from-youtube-video/id "UEx2azlZaF9NV1l1eXNFa0M4bFFDbV85dnBFRmgyZUNyai4wMTcyMDhGQUE4NTIzM0Y5"]
           ;                    [:from-youtube-video/id "UEx2azlZaF9NV1l1eXNFa0M4bFFDbV85dnBFRmgyZUNyai4wOTA3OTZBNzVEMTUzOTMy"]],
           ;     :busy? false,
           ;     :parameters {:com.fulcrologic.rad.report/sort {:ascending? true},
           ;                  :from-youtube-playlist/id "PLvk9Yh_MWYuysEkC8lQCm_9vpEFh2eCrj"},
           ;     :page-count 1,
           query           [{:ui/loaded-data (comp/get-query FromYouTube-PlaylistReport-Row)}]
           ; reminder: (comp/get-query FromYouTubeVideoReport-Row) returns
           ; => [:from-youtube-video/title :from-youtube-video/url :from-youtube-video/id]

           ;_ (println current-state)
           ;_ (println path)
           ;_ (println starting-entity)

           retval          (com.fulcrologic.fulcro.algorithms.denormalize/db->tree query starting-entity current-state)]
       ; => :ui{:loaded-data [#:from-youtube-video{:title "Bryan Finster on Andy Patton's Antipatterns",
       ;                                        :url "https://www.youtube.com/watch?v=IZt8PqGWmCY",
       ;                                        :id "UEx2azlZaF9NV1l1eXNFa0M4bFFDbV85dnBFRmgyZUNyai41NkI0NEY2RDEwNTU3Q0M2"}
       ;                   #:from-youtube-video{:title "Dominica DeGrandis on Andy Patton's Antipatterns",
       ;                                        :url "https://www.youtube.com/watch?v=qIatlcomXwQ",
       ;                                        :id "UEx2azlZaF9NV1l1eXNFa0M4bFFDbV85dnBFRmgyZUNyai4yODlGNEE0NkRGMEEzMEQy"}
       (-> retval
           :ui/loaded-data))))

(comment
  (def rows (get-all-report-rows))
  (comp/transact!
    (app/current-state com.example.client/app)
    [(mymutations/save-youtube-playlist-to-database
       {:from-youtube/videos (get-all-report-rows)})])
  ,)


(report/defsc-report FromYouTube-PlaylistReport [this props]
  {ro/title            "From YouTube: All Playlists"
   ro/source-attribute :from-youtube-playlist/all-playlists
   ;ro/source-attribute    :youtube-video/all-videos
   ro/row-pk           yt-playlist/id
   ro/columns          [yt-playlist/id
                        yt-playlist/published-at
                        yt-playlist/title
                        yt-playlist/item-count
                        yt-playlist/description]

   ; youtube/position youtube/playlist-id youtube/video-id
   ;                           youtube/url


   ro/controls         {; input: query-parameter: :youtube-playlist/id
                        :youtube-playlist/id {:type   :string
                                              :local? true
                                              :label  "Playlist ID"}}

   ro/links            {:from-youtube-playlist/title
                        (fn [this row]
                          (rroute/route-to! this
                                            yt-video-forms/FromYouTubeVideoReport
                                            ; {:youtube-video/by-playlist [:youtube-video/id]}
                                            ;:from-youtube-playlist/id
                                            ;:from-youtube-video/from-playlist
                                            {:from-youtube-playlist/id (:from-youtube-playlist/id row)}))}

   ro/row-actions      [{:label  "Create Database Entry"
                         :action (fn [this row]
                                   (println "from youtube-row-actions: "
                                            (with-out-str (pp/pprint row)))
                                   (comp/transact!
                                     this
                                     [(mymutations/save-youtube-playlist-to-database
                                        (select-keys row
                                                     [:from-youtube-playlist/title
                                                      :from-youtube-playlist/id
                                                      :from-youtube-playlist/description]))]))}
                        ;(rroute/route-to! this
                        ;                  yt-video-forms/FromYouTubeVideoReport
                        ;                  ; {:youtube-video/by-playlist [:youtube-video/id]}
                        ;                  ;:from-youtube-playlist/id
                        ;                  ;:from-youtube-video/from-playlist
                        ;                  {:from-youtube-playlist/id (:from-youtube-playlist/id row)}))}
                        {:label  "Go to Playlist"
                         :action (fn [this row]
                                   (println "from youtube-row-actions: " row)
                                   (rroute/route-to! this
                                                     yt-video-forms/FromYouTubeVideoReport
                                                     ; {:youtube-video/by-playlist [:youtube-video/id]}
                                                     ;:from-youtube-playlist/id
                                                     ;:from-youtube-video/from-playlist
                                                     {:from-youtube-playlist/id (:from-youtube-playlist/id row)}))}]
   ;{:label  "Save Playlist to Database"
   ; :action (fn [report-instance row]
   ;           (println "from youtube-row-actions: " row)
   ;           (comp/transact!
   ;             report-instance
   ;             [(mymutations/save-youtube-playlist-to-database-given-playlist-id
   ;                {:from-youtube-playlist/id (:from-youtube-playlist/id row)})]))}]
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

   ;ro/form-links          {youtube/title YouTubeForm}



   ;ro/links               {:category/label (fn [this {:category/keys [label]}]
   ;                                          (control/set-parameter! this ::category label)
   ;                                          (report/filter-rows! this))}

   ro/run-on-mount?    true
   ro/route            "from-youtube-report-by-playlist"})
;(dom/div
;  (dom/pre (with-out-str (pp/pprint props)))))


;(comment
;  (comp/get-query YouTubeReportAll)
;  (comp/get-query YouTubeForm))
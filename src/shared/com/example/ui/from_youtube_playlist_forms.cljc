(ns com.example.ui.from-youtube-playlist-forms
  (:require
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
    [com.example.model.from-youtube-playlist :as yt-playlist]
    [com.example.ui.from-youtube-video-forms :as yt-video-forms]
    [com.fulcrologic.rad.routing :as rroute]
    [com.example.model.mutations :as mymutations]
    [clojure.pprint :as pp]))
;
(report/defsc-report FromYouTube-PlaylistReport [this props]
  {ro/title               "YouTube Report By Playlist"
   ro/source-attribute    :from-youtube-playlist/all-playlists
   ;ro/source-attribute    :youtube-video/all-videos
   ro/row-pk              yt-playlist/id
   ro/columns             [;youtube/id
                           yt-playlist/published-at
                           yt-playlist/title
                           yt-playlist/item-count
                           yt-playlist/description]

   ; youtube/position youtube/playlist-id youtube/video-id
   ;                           youtube/url


   ro/controls {; input: query-parameter: :youtube-playlist/id
                :youtube-playlist/id {:type   :string
                                      :local? true
                                      :label  "Playlist ID"}
                ::upload-to-database {:type   :button
                                      :local? true
                                      :label  "Save to Database"
                                      :action (fn [this]
                                                (println "save-to-database: " this))}}
                                                ;(comp/transact!
                                                ;  this
                                                ;  [(mymutations/save-youtube-playlist-to-database
                                                ;     rows)]))}}
                                                    ;(select-keys rows [:youtube-video/id]))]))}}

   ro/row-actions      [{:label  "Go to Playlist"
                         :action (fn [this row]
                                   (println "from youtube-row-actions: " row)
                                   (rroute/route-to! this
                                                     yt-video-forms/FromYouTubeVideoReport
                                                     ; {:youtube-video/by-playlist [:youtube-video/id]}
                                                     ;:from-youtube-playlist/id
                                                     ;:from-youtube-video/from-playlist
                                                     {:from-youtube-playlist/id (:from-youtube-playlist/id row)}))}
                        {:label  "Save Playlist to Database"
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

   ;ro/form-links          {youtube/title YouTubeForm}

   ;ro/links               {:category/label (fn [this {:category/keys [label]}]
   ;                                          (control/set-parameter! this ::category label)
   ;                                          (report/filter-rows! this))}

   ro/run-on-mount?       true
   ro/route               "from-youtube-report-by-playlist"})
;(dom/div
;  (dom/pre (with-out-str (pp/pprint props)))))


;(comment
;  (comp/get-query YouTubeReportAll)
;  (comp/get-query YouTubeForm))
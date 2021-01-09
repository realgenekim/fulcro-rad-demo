(ns com.example.ui.from-youtube-video-forms
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
    [com.example.model.conference :as conference]
    [com.example.ui.youtube-video-forms :as youtube-forms]
    [com.example.model.youtube-playlist :as youtube-playlist]
    [com.example.model.from-youtube-video :as yt-video]
    [com.example.model.mutations :as mymutations]
    [com.fulcrologic.rad.routing :as rroute]
    [clojure.pprint :as pp]))

; this is an example of generating a report, with a query
;   input: :conference/youtube-playlists2
;   query parameter:

(report/defsc-report FromYouTubeVideoReport [this props]
  {ro/title            "From YouTube Videos Report 2"
   ro/source-attribute :from-youtube-video/from-playlist
   ro/row-pk           yt-video/id
   ro/columns          [yt-video/title
                        yt-video/url]

   ro/controls {:from-youtube-playlist/id {:type   :string
                                           :local? true
                                           :label  "Playlist"}}



   ;ro/row-actions      [{:label  "Edit Playlist"
   ;                      :action (fn [report-instance row]
   ;                                (println "from youtube-row-actions: " row)
   ;                                ; [this form-class entity-id]
   ;                                (form/edit! report-instance YouTubePlaylistForm
   ;                                            (:youtube-playlist/id row)))}
   ;                     {:label  "Goto Playlist"
   ;                      :action (fn [this row]
   ;                                ; [app-or-component RouteTarget route-params
   ;                                (println "Go to Playlist" row)
   ;                                ; :youtube-playlist/id
   ;                                (rroute/route-to! this
   ;                                                  youtube-forms/YouTubeReportByPlaylist
   ;                                                  ; {:youtube-video/by-playlist [:youtube-video/id]}
   ;                                                  {:youtube-playlist/id (:youtube-playlist/id row)}))}
   ;
   ;                     {:label  "Select"
   ;                      :action (fn [report-instance row]
   ;                                (println "from youtube-row-actions: " row)
   ;                                ;#?{:cljs (js/console.log row)}
   ;                                (comp/transact!
   ;                                  report-instance
   ;                                  [(mymutations/fetch-vimeo-entry (select-keys row
   ;                                                                               [:youtube-video/id]))]))}]
   ;(comp/transact!
   ;  report-instance
   ;  '[(mutations/set-selected-org {:orgnr organization-number})]))}]

   ;ro/column-formatters {:youtube-playlist/title
   ;                      (fn [this row]
   ;                        (println "column formatter: " row)
   ;                        (println "column formatter: " this)
   ;                        (dom/a {:onClick (fn []
   ;                                           (rroute/route-to! this
   ;                                                             youtube-forms/YouTubeReportByPlaylist
   ;                                                             ; {:youtube-video/by-playlist [:youtube-video/id]}
   ;                                                             {:youtube-playlist/id (:youtube-playlist/id row)}))}
   ;                          (str row)))}
   ;(dom/a {:onClick #(form/edit! this AccountForm (-> this comp/props :account/id)} (str v)))}}

   ;ro/form-links       {youtube-playlist/title YouTubePlaylistForm}

   ;ro/links               {:category/label (fn [this {:category/keys [label]}]
   ;                                          (control/set-parameter! this ::category label)
   ;                                          (report/filter-rows! this))}

   ro/run-on-mount?    true
   ro/route            "from-youtube-playlist-report"})
  ;(dom/div
  ;  (dom/h4
  ;    (str "Accounts for: " (-> props :ui/controls first
  ;                              :com.fulcrologic.rad.control/value)))
  ;  (dom/pre
  ;    (with-out-str (pp/pprint props)))))


(comment
  (comp/get-query YouTubePlaylistReport)
  (comp/get-query YouTubeForm))

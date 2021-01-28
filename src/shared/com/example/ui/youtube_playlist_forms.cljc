(ns com.example.ui.youtube-playlist-forms
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
    [com.example.model.mutations :as mymutations]
    [com.fulcrologic.rad.routing :as rroute]
    [clojure.pprint :as pp]))

(form/defsc-form YouTubePlaylistForm [this props]
  {fo/id           youtube-playlist/id
   fo/attributes   [
                    youtube-playlist/title
                    youtube-playlist/year-city-type
                    youtube-playlist/description]

   fo/route-prefix "youtube-playlist-edit"
   fo/title        "Edit YouTube Playlist"})
;(dom/div :.ui.container.grid
;  "Hello!"))

; this is an example of generating a report, with a query
;   input: :conference/youtube-playlists2

(report/defsc-report AllYouTubePlaylists [this props]
  {ro/title            "Database: YouTube Playlists (All)"
   ro/source-attribute :youtube-playlist/all-playlists
   ro/row-pk           youtube-playlist/id
   ro/columns          [youtube-playlist/id
                        youtube-playlist/title
                        youtube-playlist/year-city-type]

   ro/row-actions      [{:label  "Edit Playlist"
                         :action (fn [report-instance row]
                                   (println "from youtube-row-actions: " row)
                                   ; [this form-class entity-id]
                                   (form/edit! report-instance YouTubePlaylistForm
                                               (:youtube-playlist/id row)))}
                        {:label  "Goto Playlist"
                         :action (fn [this row]
                                   ; [app-or-component RouteTarget route-params
                                   (println "Go to Playlist" row)
                                   ; :youtube-playlist/id
                                   (rroute/route-to! this
                                                     youtube-forms/YouTubeReportByPlaylist
                                                     ; {:youtube-video/by-playlist [:youtube-video/id]}
                                                     {:youtube-playlist/id (:youtube-playlist/id row)}))}]

   ro/links               {:youtube-playlist/id
                           (fn [this row]
                             (rroute/route-to! this
                                               youtube-forms/YouTubeReportByPlaylist
                                               ; {:youtube-video/by-playlist [:youtube-video/id]}
                                               {:youtube-playlist/id (:youtube-playlist/id row)}))}

   ro/run-on-mount?    true
   ro/route            "youtube-playlist-all"})

(report/defsc-report YouTubeVideoReport [this props]
  {ro/title            "Database: YouTube Playlists (query)"
   ;ro/source-attribute :youtube-playlist/all-playlists
   ro/source-attribute :conference/youtube-playlists2
   ro/row-pk           youtube-playlist/id
   ro/columns          [youtube-playlist/id
                        youtube-playlist/title]
                        ;youtube-playlist/description]
                        ; youtube-playlist/conf-uuid]

   ;ro/column-formatters {:youtube-playlist/conf-uuid (fn [this value]
   ;                                                   (println "youtubeplaylistreport: column formatter: " value)
   ;                                                   (or (str value)
   ;                                                       "-"))}

   ro/controls {:conference/uuid {:type   :uuid
                                  :local? true
                                  :label  "Account"}}


   ;ro/row-visible?        (fn [filter-parameters row] (let [{::keys [category]} filter-parameters
   ;                                                         row-category (get row :category/label)]
   ;                                                     (or (= "" category) (= category row-category))))
   ;
   ;;; A sample server-query based picker that sets a local parameter that we use to filter rows.

   ; If defined: sort is applied to rows after filtering (client-side)
   ;ro/initial-sort-params {:sort-by          :youtube-video/position
   ;                        :sortable-columns #{:youtube-video/playlist-id
   ;                                            :youtube-video/title
   ;                                            :youtube-video/position}
   ;                        ;:session/title :session/speakers :session/venue}
   ;                        ; :session/stype
   ;                        :ascending?       true}

   ro/row-actions      [{:label  "Edit Playlist"
                         :action (fn [report-instance row]
                                   (println "from youtube-row-actions: " row)
                                   ; [this form-class entity-id]
                                   (form/edit! report-instance YouTubePlaylistForm
                                               (:youtube-playlist/id row)))}
                        {:label  "Goto Playlist"
                         :action (fn [this row]
                                   ; [app-or-component RouteTarget route-params
                                   (println "Go to Playlist" row)
                                   ; :youtube-playlist/id
                                   (rroute/route-to! this
                                                     youtube-forms/YouTubeReportByPlaylist
                                                     ; {:youtube-video/by-playlist [:youtube-video/id]}
                                                     {:youtube-playlist/id (:youtube-playlist/id row)}))}]

                        ;{:label  "Select"
                        ; :action (fn [report-instance row]
                        ;           (println "from youtube-row-actions: " row)
                        ;           ;#?{:cljs (js/console.log row)}
                        ;           (comp/transact!
                        ;             report-instance
                        ;             [(mymutations/fetch-vimeo-entry (select-keys row
                        ;                                                          [:youtube-video/id]))]))}]
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

   ro/links               {:youtube-playlist/id
                           (fn [this row]
                             (rroute/route-to! this
                                               youtube-forms/YouTubeReportByPlaylist
                                               ; {:youtube-video/by-playlist [:youtube-video/id]}
                                               {:youtube-playlist/id (:youtube-playlist/id row)}))}

   ro/run-on-mount?    true
   ro/route            "youtube-playlist-report2"})
  ;(dom/div
  ;  (dom/h4
  ;    (str "Accounts for: " (-> props :ui/controls first
  ;                              :com.fulcrologic.rad.control/value)))
  ;  (dom/pre
  ;    (with-out-str (pp/pprint props)))))


(comment
  (comp/get-query YouTubePlaylistReport)
  (comp/get-query YouTubeForm))

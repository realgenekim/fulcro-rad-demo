(ns com.example.ui.from-youtube-video-forms
  (:require
    [clojure.pprint :as pp]
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
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.rad.routing :as rroute]

    ;[com.example.model.item :as item]
    [com.example.model.session :as session]
    [com.example.model.category :as category]
    [com.example.model.youtube-video :as youtube]
    [com.example.model.conference :as conference]
    [com.example.ui.youtube-video-forms :as youtube-forms]
    [com.example.model.youtube-playlist :as youtube-playlist]
    [com.example.model.from-youtube-video :as yt-video]
    [com.example.model.mutations :as mymutations]))



; this is an example of generating a report, with a query
;   input: :conference/youtube-playlists2
;   query parameter:

(defsc MyRow
  [this props]
  {:query [:foo]
   :ident :foo})

(comment
    (comp/get-query FromYouTubeVideoReport)
    (comp/get-query FromYouTubeVideoReport-Row)
    (app/current-state com.example.client/app)
    (com.fulcrologic.fulcro.algorithms.denormalize/db->tree [{:authenticator ['*]}]
                                                            (app/current-state com.example.client/app)
                                                            (app/current-state com.example.client/app))

    (let [current-state   (app/current-state com.example.client/app)
          path            (comp/get-ident FromYouTubeVideoReport {})
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
          query           [{:ui/loaded-data (comp/get-query FromYouTubeVideoReport-Row)}]
          ; reminder: (comp/get-query FromYouTubeVideoReport-Row) returns
          ; => [:from-youtube-video/title :from-youtube-video/url :from-youtube-video/id]

          retval          (com.fulcrologic.fulcro.algorithms.denormalize/db->tree query starting-entity current-state)]
      ; => :ui{:loaded-data [#:from-youtube-video{:title "Bryan Finster on Andy Patton's Antipatterns",
      ;                                        :url "https://www.youtube.com/watch?v=IZt8PqGWmCY",
      ;                                        :id "UEx2azlZaF9NV1l1eXNFa0M4bFFDbV85dnBFRmgyZUNyai41NkI0NEY2RDEwNTU3Q0M2"}
      ;                   #:from-youtube-video{:title "Dominica DeGrandis on Andy Patton's Antipatterns",
      ;                                        :url "https://www.youtube.com/watch?v=qIatlcomXwQ",
      ;                                        :id "UEx2azlZaF9NV1l1eXNFa0M4bFFDbV85dnBFRmgyZUNyai4yODlGNEE0NkRGMEEzMEQy"}
      retval)

    (get-in (app/current-state com.example.client/app)
            (comp/get-ident FromYouTubeVideoReport {}))


      ;(comp/transact!))
      ; or store sessions in namespace that isn't being reloades)

    (->> com.example.client/app
         :com.fulcrologic.fulcro.application/state-atom
         deref)

         ;:com.fulcrologic.rad.report/id
         ;:com.example.ui.from-youtube-playlist-forms/FromYouTube-PlaylistReport)
    ,)

(declare FromYouTubeVideoReport FromYouTubeVideoReport-Row)

(defn- get-all-report-rows
  " gets report rows from app state "
  []
  #?(:cljs
      (let [current-state   (app/current-state com.example.client/app)
            path            (comp/get-ident FromYouTubeVideoReport {})
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
            query           [{:ui/loaded-data (comp/get-query FromYouTubeVideoReport-Row)}]
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

(report/defsc-report FromYouTubeVideoReport [this props]
  {ro/title            "From YouTube: Videos"
   ro/source-attribute :from-youtube-video/from-playlist
   ro/row-pk           yt-video/id
   ;ro/BodyItem MyRow
   ro/columns          [yt-video/title
                        yt-video/published-at
                        yt-video/description
                        yt-video/url
                        yt-video/video-id
                        yt-video/position
                        yt-video/playlist-id]


   ;[id title published-at description url video-id position playlist-id
   ; from-playlist]

   ro/controls {:from-youtube-playlist/id {:type   :string
                                           :local? true
                                           :label  "Playlist"}
                ::upload-to-database {:type   :button
                                      :local? true
                                      :label  "Save All Videos To Database"
                                      :action (fn [this]
                                                (let [rows (get-all-report-rows)]
                                                  (println "button: save-to-database: " rows)
                                                  (comp/transact!
                                                    this
                                                    [(mymutations/save-all-videos-to-database
                                                       {:from-youtube/videos rows})])))}}

   ;



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

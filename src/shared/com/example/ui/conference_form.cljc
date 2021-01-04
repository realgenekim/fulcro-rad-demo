(ns com.example.ui.conference-form
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
    [com.example.ui.youtube-forms :as youtube-forms]
    [com.example.model.conference :as conference]
    [com.example.model.youtube-playlist :as youtube-playlist]
    [com.example.model.mutations :as mymutations]
    [com.fulcrologic.rad.routing :as rroute]))

;(form/defsc-form YouTubePlaylistForm [this props]
;  {fo/id           youtube-playlist/id
;   fo/attributes   [
;                    youtube-playlist/title
;                    youtube-playlist/description]
;   fo/route-prefix "youtube-playlist-edit"
;   fo/title        "Edit YouTube Playlist"})
;;(dom/div :.ui.container.grid
;;  "Hello!"))


(report/defsc-report ConferenceReport [this props]
  {ro/title            "Conference Report"
   ro/source-attribute :conference/all-conferences
   ro/row-pk           conference/id
   ro/columns          [conference/id conference/nm conference/youtube-playlists]


   ;ro/form-links       {youtube-playlist/title YouTubePlaylistForm}

   ;ro/links               {:category/label (fn [this {:category/keys [label]}]
   ;                                          (control/set-parameter! this ::category label)
   ;                                          (report/filter-rows! this))}

   ro/run-on-mount?    true
   ro/route            "conferences"})


(comment
  (comp/get-query YouTubeReport)
  (comp/get-query YouTubeForm))
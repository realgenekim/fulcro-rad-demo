(ns com.example.ui.vimeo-video-forms
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

    [com.example.model.vimeo-video :as vimeo]

    [com.example.model.mutations :as mymutations]
    [com.fulcrologic.rad.routing :as rroute]
    [clojure.pprint :as pp]))

;(form/defsc-form YouTubePlaylistForm [this props]
;  {fo/id           youtube-playlist/id
;   fo/attributes   [
;                    youtube-playlist/title
;                    youtube-playlist/description]
;   fo/route-prefix "youtube-playlist-edit"
;   fo/title        "Edit YouTube Playlist"})
;(dom/div :.ui.container.grid
;  "Hello!"))

; this is an example of generating a report, with a query
;   input: :conference/youtube-playlists2

(report/defsc-report AllVimeoVideos [this props]
  {ro/title            "Vimeo: All Videos"
   ro/source-attribute :vimeo-video/all-videos
   ro/row-pk           vimeo/uri
   ro/columns          [vimeo/uri vimeo/nm vimeo/description] ;vimeo/transcode]
   ro/run-on-mount?    true
   ro/route            "vimeo-video"})

  ;(dom/div
  ;  (dom/h4
  ;    (str "Accounts for: " (-> props :ui/controls first
  ;                              :com.fulcrologic.rad.control/value)))
  ;  (dom/pre
  ;    (with-out-str (pp/pprint props)))))


(comment
  (comp/get-query YouTubePlaylistReport)
  (comp/get-query YouTubeForm))

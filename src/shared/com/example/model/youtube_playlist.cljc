(ns com.example.model.youtube-playlist
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.fulcrologic.rad.report-options :as ro]
    [com.wsscode.pathom.connect :as pc]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom]
       :cljs [com.fulcrologic.fulcro.dom :as dom])
    #?(:clj [com.example.components.database-queries :as queries])
    [taoensso.timbre :as log]))

(defattr id :youtube-playlist/id :string
  {ao/identity? true?
   ao/schema :video})

(defattr description :youtube-playlist/description :string
  {ao/cardinality :one
   ao/identities #{:youtube-playlist/id}
   ao/style       :multi-line
   ao/schema      :video})

(defattr title :youtube-playlist/title :string
  {ao/cardinality :one
   ao/identities #{:youtube-playlist/id}
   ao/schema      :video})

(defattr conf-uuid :youtube-playlist/conf-uuid :uuid
  {ao/cardinality :one
   ao/identities #{:youtube-playlist/id}
   ao/schema      :video
   ro/column-formatter (fn [_ value]
                         (println "conf-uuid: column-formatter: " value)
                         (or (str value)
                             "-"))})


;(defsc YouTubePlaylist [_ _]
;  {:query [:youtube-playlist/id :youtube-playlist/title]
;   :ident :youtube-playlist/id})

; who cares about the attrs: no one except for you
; it's just a map: you use them in reports and forms, to tell the forms/reports what to display

(defattr all-playlists :youtube-playlist/all-playlists :ref
  {ao/target     :youtube-playlist/id
   ao/pc-output  [{:youtube-playlist/all-playlists [:youtube-playlist/id]}]
   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
                   (println "defattr all-playlists: " env)
                   #?(:clj
                      ;{:youtube-video/all-videos [{:youtube-video/video-id "123" :youtube-video/id "123 "}]}))})
                      {:youtube-playlist/all-playlists (queries/get-all-youtube-playlists env query-params)}))})



;(pc/defresolver youtube-video-by-id [{:keys [db] :as env} {:youtube-video/keys [id] :as input}]
;  {::pc/input #{:youtube-video/id}
;   ::pc/output [:youtube-video/description :youtube-video/playlist-id :youtube-video/title
;                :youtube-video/position :youtube-video/url :youtube-video/video-id]}
;  ;(println "defresolver: input: " env input)
;  (println "defresolver: youtube-video-by-id: id: " id)
;  #?(:clj
;     ;{:session/uuid "abc"
;     ; :session/venue "abc"}))
;     ;{:youtube-video/all-videos [{:youtube-video/video-id "123" :youtube-video/id "123 "}]}))
;     (queries/youtube-video-by-id env id)))


; WARNING: make sure to add all model attributes here!

(def attributes [id description title conf-uuid all-playlists])
;item-name category description price in-stock all-items])

#?(:clj
   (def resolvers []))


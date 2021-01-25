(ns com.example.model.vimeo-video
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.fulcrologic.rad.report-options :as ro]
    [com.wsscode.pathom.connect :as pc]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom]
       :cljs [com.fulcrologic.fulcro.dom :as dom])
    #?(:clj [vimeo.main :as vimeo])
    [taoensso.timbre :as log]

    [com.example.utils :as utils]))

; {:name "az3BOHoiiS0---1920x1080---Opening Remarks -  Gene Kim",
; :uri "/videos/485168751",
; :description nil,
; :transcode {:status "complete"}}

(defattr uri :vimeo-video/uri :string
  {ao/identity? true?})

(defattr description :vimeo-video/description :string
  {ao/identities #{:vimeo-video/uri}
   ao/style       :multi-line})

(defattr nm :vimeo-video/name :string
  {ao/identities #{:vimeo-video/uri}})


(defattr all-videos :vimeo-video/all-videos :ref
  {ao/target     :vimeo-video/uri
   ao/pc-output  [{:vimeo-video/all-videos [:youtube-playlist/id]}]
   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
                   (println "defattr all-videos: " query-params)
                   #?(:clj
                      ;{:youtube-video/all-videos [{:youtube-video/video-id "123" :youtube-video/id "123 "}]}))})
                      {:vimeo-video/all-videos (->> (vimeo/fetch-all-videos-parsed!)
                                                    (map #(utils/map->nsmap % "vimeo-video")))}))})




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

(def attributes [uri nm description all-videos])
;item-name category description price in-stock all-items])

#?(:clj
   (def resolvers []))

(comment
  (comp/get-query all-playlists))



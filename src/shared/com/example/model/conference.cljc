(ns com.example.model.conference
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

(def confs
  [[{:db/id 5348024557502565,
     :conference/name "Vegas-Virtual 2020",
     :conference/uuid #uuid"d6a8d160-4af5-4d7a-b796-306f8f404754"}]
   [{:db/id 24171663626011426,
     :conference/name "Vegas 2019",
     :conference/youtube-playlists [#:youtube-playlist{:id "PLvk9Yh_MWYuwXC0iU5EAB1ryI62YpPHR9",
                                                       :title "Keynotes - DevOps Enterprise Summit: Las Vegas 2019"}],
     :conference/uuid #uuid"2e24aa89-48ef-4a4c-879f-f1900ada35ea"}]])


(defattr id :conference/uuid :uuid
  {ao/identity? true?
   ao/schema :video})

(defattr nm :conference/name :string
  {ao/cardinality :one
   ao/identities #{:conference/uuid}
   ao/schema      :video})

(defattr youtube-playlists :conference/youtube-playlists :ref
  {ao/cardinality :many
   ao/identities #{:conference/uuid}
   ao/schema      :video})


;(defsc YouTubePlaylist [_ _]
;  {:query [:youtube-playlist/id :youtube-playlist/title]
;   :ident :youtube-playlist/id})

; who cares about the attrs: no one except for you
; it's just a map: you use them in reports and forms, to tell the forms/reports what to display

(defattr all-conferences :conference/all-conferences :ref
  {ao/target     :conference/uuid
   ao/pc-output  [{:conference/all-conferences [:conference/uuid]}]
   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
                   (println "defattr all-conferences: " env)
                   #?(:clj
                      ;{:conference/all-conferences [{:conference/uuid #uuid"d6a8d160-4af5-4d7a-b796-306f8f404754"}]}))})
                      {:conference/all-conferences (queries/get-all-conferences env query-params)}))})



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

(def attributes [id nm youtube-playlists all-conferences])
;item-name category description price in-stock all-items])

#?(:clj
   (def resolvers []))



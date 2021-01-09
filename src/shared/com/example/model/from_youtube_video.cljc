(ns com.example.model.from-youtube-video
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.fulcrologic.rad.report-options :as ro]
    [com.wsscode.pathom.connect :as pc]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.example.utils :as utils]
    #?(:clj [youtube.main :as yt])
    ;#?(:clj  [com.fulcrologic.fulcro.dom-server :as dom]
    ;   :cljs [com.fulcrologic.fulcro.dom :as dom])
    ;#?(:clj [com.example.components.database-queries :as queries])
    [taoensso.timbre :as log]))


(def example {:description   "Gene Kim, author, researcher, founder of IT Revolution\nPaul Muller, VP Software Marketing, HP Software",
              :publishedAt   "2016-07-02T04:17:18Z",
              :title         "DOES16 London -  Gene Kim & Paul Muller - Closing Remarks",
              :id            "UEx2azlZaF9NV1l1ekFmYXpBZTZtOHVFLV9sVnZzaGpYNi41NkI0NEY2RDEwNTU3Q0M2",
              :url           "https://www.youtube.com/watch?v=eLrTqi9AjX8",
              :videoId       "eLrTqi9AjX8",
              :position      8,
              :privacyStatus "unlisted",
              :playlistId    "PLvk9Yh_MWYuzAfazAe6m8uE-_lVvshjX6"})

(defattr id :from-youtube-video/id :string
  {ao/identity? true})

(defattr published-at :from-youtube-video/published-at :string
  {ao/identities #{:from-youtube-video/id}})

(defattr title :from-youtube-video/title :string
  {ao/identities #{:from-youtube-video/id}})

(defattr url :from-youtube-video/url :string
  {ao/identities #{:from-youtube-video/id}})

(defattr description :from-youtube-video/description :string
  {ao/identities #{:from-youtube-video/id}})

(defattr video-id :from-youtube-video/video-id :string
  {ao/identities #{:from-youtube-video/id}})

(defattr position :from-youtube-video/position :string
  {ao/identities #{:from-youtube-video/id}})

(defattr playlist-id :from-youtube-video/playlist-id :string
  {ao/identities #{:from-youtube-video/id}})

; this is an example of something that does a query, using passed in
; query-params:
;      input: :from-youtube-playlist/id


#?(:clj
    (defn fetch-playlist-parsed! [{:from-youtube-playlist/keys [id] :as params}]
      (println "fetch-playlist-parsed: params: " params)
      (println "fetch-playlist-parsed: id: " id)
      (let [retval (yt/fetch-playlists-items-parsed! id)
            parsed (->> retval
                        (map #(clojure.set/rename-keys % {:publishedAt :published-at
                                                          :playlistId :playlist-id
                                                          :videoId :video-id}))
                        (map #(utils/map->nsmap % "from-youtube-video")))]
        ;(println "fetch-playlist-parsed: retval: " retval)
        parsed)))

(comment
  (yt/fetch-playlists-items-parsed! "PLvk9Yh_MWYuzAfazAe6m8uE-_lVvshjX6")
  (fetch-playlist-parsed! #:from-youtube-playlist{:id "PLvk9Yh_MWYuzAfazAe6m8uE-_lVvshjX6"})
  ,)

(defattr from-playlist :from-youtube-video/from-playlist :ref
  {ao/target      :from-youtube-video/id
   ao/cardinality :many
   ao/identities #{:from-youtube-playlist/id}  ; <-- input?

   ao/pc-output   [{:from-youtube-video/from-playlist [:from-youtube-video/id]}]
   ao/pc-resolve  (fn [{:keys [query-params] :as env} _]
                    (println "defattr :from-youtube-video/from-playlist: " query-params)
                    #?(:clj
                       {:from-youtube-video/from-playlist
                        (fetch-playlist-parsed! query-params)}))})
                        ;{:from-youtube-video/id "abc"}}))})
                        ;(queries/get-conference-playlists env query-params)}))})


(def attributes [id title published-at description url video-id position playlist-id
                 from-playlist])

#?(:clj
   (def resolvers []))
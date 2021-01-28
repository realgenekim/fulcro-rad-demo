(ns com.example.model.from-youtube-playlist
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

(defattr id :from-youtube-playlist/id :string
  {ao/identity? true})

(defattr title :from-youtube-playlist/title :string
  {ao/cardinality :one
   ao/identities #{:from-youtube-playlist/id}})

(defattr published-at :from-youtube-playlist/published-at :string
  {ao/cardinality :one
   ao/identities #{:from-youtube-playlist/id}})

(defattr item-count :from-youtube-playlist/item-count :string
  {ao/cardinality :one
   ao/identities #{:from-youtube-playlist/id}})

(defattr description :from-youtube-playlist/description :string
  {ao/cardinality :one
   ao/identities #{:from-youtube-playlist/id}})


(defattr all-playlists :from-youtube-playlist/all-playlists :ref
  {ao/target     :from-youtube-playlist/id
   ; TODO: can I turn :from-youtube-playlist/id => :youtube-playlist/id?
   ao/pc-output  [{:from-youtube-playlist/all-playlists [:from-youtube-playlist/id]}]
   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
                   (println "defattr from-youtube-playlists: " query-params)
                   #?(:clj
                      (let [ytchannel (:main yt/itrev-channels)
                            retval (->> (yt/fetch-channel-playlists-parsed!)
                                        ;(take 15)
                                        (map #(clojure.set/rename-keys % {:publishedAt :published-at
                                                                          :itemCount :item-count
                                                                          :channelId :channel-id}))
                                        ;(map #(select-keys % [:id :title :published-a :itemCount]))
                                        ; make sure everything is namespaced: :youtube-playlist/id, ...
                                        (map #(utils/map->nsmap % "from-youtube-playlist")))]
                        (println retval)
                        {:from-youtube-playlist/all-playlists retval})))})


(def attributes [id title published-at item-count description all-playlists])

#?(:clj
   (def resolvers []))
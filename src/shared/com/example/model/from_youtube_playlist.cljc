(ns com.example.model.from-youtube-playlist
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.fulcrologic.rad.report-options :as ro]
    [com.wsscode.pathom.connect :as pc]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    #?(:clj [youtube.main :as yt])
    ;#?(:clj  [com.fulcrologic.fulcro.dom-server :as dom]
    ;   :cljs [com.fulcrologic.fulcro.dom :as dom])
    ;#?(:clj [com.example.components.database-queries :as queries])
    [taoensso.timbre :as log]))

;(defsc YouTubePlaylistItem [this {:from-youtube-playlist/keys [id title] :as props}]
;  {:query [:from-youtube-playlist/id
;           :from-youtube-playlist/title
;           :from-youtube-playlist/published-at
;           :from-youtube-playlist/item-count
;           :from-youtube-playlist/channel-id]
;   :ident :from-youtube-playlist/id})
;
;(defsc FromYouTube-PlaylistReturn [_ _]
;  {:query [{:ui.from-youtube/playlists (comp/get-query YouTubePlaylistItem)}]
;   :ident (fn [] [:component/id :ui.from-youtube/playlists])})

; {:youtube-playlist/id "PLvk9Yh_MWYuysEkC8lQCm_9vpEFh2eCrj",
;    :youtube-playlist/title "Andy Patton's Antipatterns",
;    :youtube-playlist/published-at "2020-10-14T17:21:56Z",
;    :youtube-playlist/item-count 4,
;    :youtube-playlist/channel-id "UCkAQCw5_sIZmj2IkSrNy00A"}

(defattr id :from-youtube-playlist/id :string
  {ao/identity? true
   ao/schema :youtube})

(defattr title :from-youtube-playlist/title :string
  {ao/cardinality :one
   ao/identities #{:from-youtube-playlist/id}
   ao/schema :youtube})


(defattr published-at :from-youtube-playlist/published-at :string
  {ao/cardinality :one
   ao/identities #{:from-youtube-playlist/id}
   ao/schema :youtube})

(defattr item-count :from-youtube-playlist/item-count :string
  {ao/cardinality :one
   ao/identities #{:from-youtube-playlist/id}
   ao/schema :youtube})

;(defattr all-videos :youtube-video/all-videos :ref
;  {ao/target     :youtube-video/id
;   ao/pc-output  [{:youtube-video/all-videos [:youtube-video/id]}]
;   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
;                   (println "defattr all-videos: " env)
;                   #?(:clj
;                      ;{:youtube-video/all-videos [{:youtube-video/video-id "123" :youtube-video/id "123 "}]}))})
;                      {:youtube-video/all-videos (queries/get-all-youtube-videos env query-params)}))})

(defn map->nsmap
  " make all keys namespaced to namespace n "
  [m n]
  (reduce-kv (fn [acc k v]
               (let [new-kw (if (and (keyword? k)
                                     (not (qualified-keyword? k)))
                              (keyword (str n) (name k))
                              k)]
                 (assoc acc new-kw v)))
             {} m))

;(pc/defmutation fetch-from-youtube-playlists
;  [env _]
;  {::pc/output {:ui.from-youtube/playlists [:youtube-playlist/id
;                                            :youtube-playlist/title
;                                            :youtube-playlist/published-at
;                                            :youtube-playlist/item-count
;                                            :youtube-playlist/channel-id]}}
;  (println "fetch-from-youtube-playlists: ")
;  (let [ytchannel (:main yt/itrev-channels)
;        retval (->> (yt/fetch-channel-playlists-parsed! ytchannel)
;                    ;(take 15)
;                    (map #(clojure.set/rename-keys % {:publishedAt :published-at
;                                                      :itemCount :item-count
;                                                      :channelId :channel-id}))
;                    ;(map #(select-keys % [:id :title :published-a :itemCount]))
;                    ; make sure everything is namespaced: :youtube-playlist/id, ...
;                    (map #(map->nsmap % "youtube-playlist")))]
;    (println retval)
;    {:ui.from-youtube/playlists retval}))

;(defattr all-videos :youtube-video/all-videos :ref
;  {ao/target     :youtube-video/id
;   ao/pc-output  [{:youtube-video/all-videos [:youtube-video/id]}]
;   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
;                   (println "defattr all-videos: " env)
;                   #?(:clj
;                      ;{:youtube-video/all-videos [{:youtube-video/video-id "123" :youtube-video/id "123 "}]}))})
;                      {:youtube-video/all-videos (queries/get-all-youtube-videos env query-params)}))})

(defattr all-playlists :from-youtube-playlist/all-playlists :ref
  {ao/target     :from-youtube-playlist/id
   ; TODO: can I turn :from-youtube-playlist/id => :youtube-playlist/id?
   ao/pc-output  [{:from-youtube-playlist/all-playlists [:from-youtube-playlist/id]}]
                                               ;:youtube-playlist/title
                                               ;:youtube-playlist/published-at
                                               ;:youtube-playlist/item-count
                                               ;:youtube-playlist/channel-id]}]
   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
                   (println "defattr from-youtube-playlists: " query-params)
                   #?(:clj
                      (let [ytchannel (:main yt/itrev-channels)
                            retval (->> (yt/fetch-channel-playlists-parsed! ytchannel)
                                        ;(take 15)
                                        (map #(clojure.set/rename-keys % {:publishedAt :published-at
                                                                          :itemCount :item-count
                                                                          :channelId :channel-id}))
                                        ;(map #(select-keys % [:id :title :published-a :itemCount]))
                                        ; make sure everything is namespaced: :youtube-playlist/id, ...
                                        (map #(map->nsmap % "from-youtube-playlist")))]
                        (println retval)
                        {:from-youtube-playlist/all-playlists retval})))})
                      ;{:youtube-video/all-videos [{:youtube-video/video-id "123" :youtube-video/id "123 "}]}))})
                      ;{:youtube-video/all-videos (queries/get-all-youtube-videos env query-params)}))})


(def attributes [id title published-at item-count all-playlists])
;item-name category description price in-stock all-items]) all-videos-by-playlist

#?(:clj
   (def resolvers []))
(ns com.example.model.mutations
  (:require
    [com.wsscode.pathom.connect :as pc]
    [com.example.utils :as utils]
    [clojure.pprint :as pp]
    [youtube.main :as yt]
    [datomic.datomic :as mydatomic]))


;
; server-side
;

(pc/defmutation fetch-vimeo-entry
  [env row]
  {::pc/output [:youtube-playlist/id :returned-url]}
  (println "defmutation: fetch-vimeo-entry: " row)
  {:youtube-video/id 111
   :returned-url "abcdefdef"})


; #:youtube-playlist{:id "PLvk9Yh_MWYuzAfazAe6m8uE-_lVvshjX6",
;                    :publishedAt "2016-07-02T04:16:53Z",
;                    :title "DevOps Enterprise Summit: London 2016 - Keynotes",
;                    :description "Keynotes from DevOps Enterprise Summit London 2016.\n\nDOES16 UK",
;                    :channelId "UCkAQCw5_sIZmj2IkSrNy00A",
;                    :itemCount 9}

(pc/defmutation fetch-from-youtube-playlists
   [env _]
   {::pc/output {:ui.from-youtube/playlists [:youtube-playlist/id
                                             :youtube-playlist/title
                                             :youtube-playlist/published-at
                                             :youtube-playlist/item-count
                                             :youtube-playlist/channel-id]}}
   (println "fetch-from-youtube-playlists: ")
   (let [ytchannel (:main yt/itrev-channels)
         retval (->> (yt/fetch-channel-playlists-parsed! ytchannel)
                     ;(take 15)
                     (map #(clojure.set/rename-keys % {:publishedAt :published-at
                                                       :itemCount :item-count
                                                       :channelId :channel-id}))
                     ;(map #(select-keys % [:id :title :published-a :itemCount]))
                     ; make sure everything is namespaced: :youtube-playlist/id, ...
                     (map #(utils/map->nsmap % "youtube-playlist")))]
     ;(println retval)
     ;; TASK: remove ui element from namespace
     ; :com.youtube/playlists
     {:ui.from-youtube/playlists retval}))
   ;{:ui.from-youtube/playlists
   ; [{:youtube-playlist/id "xyz"
   ;   :youtube-playlist/title "abc"}
   ;  {:youtube-playlist/id "xxx"
   ;   :youtube-playlist/title "def"}]})

(comment
  (def ytchannel (:main yt/itrev-channels))

  (def playlists (yt/fetch-channel-playlists-parsed! ytchannel))
  (->> playlists
       ;(map #(select-keys % [:id :title]))
       (map #(clojure.set/rename-keys % {:publishedAt :published-at
                                         :itemCount :item-count
                                         :channelId :channel-id}))
       (map #(utils/map->nsmap % "youtube-playlist")))
  (identity playlists))

;
; save-youtube-playlist-to-database
;
; com.example.model.mutations/save-youtube-playlist-to-database

;(pc/defmutation save-youtube-playlist-to-database
;  [env rows]
;  {::pc/output [:youtube-playlist/id :returned-url]}
;  (println "defmutation: save-youtube-playlist-to-database: "
;           (with-out-str (pp/pprint rows)))
;  {:youtube-video/id 111
;   :returned-url "abcdefdef"})

(comment
  (def x {:from-youtube-playlist/title "Keynotes - DevOps Enterprise Summit: Las Vegas 2019",
          :from-youtube-playlist/id "PLvk9Yh_MWYuwXC0iU5EAB1ryI62YpPHR9",
          :from-youtube-playlist/description ""})

  (utils/nsmap->map x)

  (mydatomic/tx!
    (mydatomic/create-youtube-playlist-no-conf (utils/nsmap->map x)))
  ,)


(pc/defmutation save-youtube-playlist-to-database
  [env row]
  {::pc/output [:from-youtube/videos :returned-url]}
  ; TASK: maybe return the new playlists, and return it?
  (do
    (println "defmutation: save-youtube-playlist-to-database: \n"
             (with-out-str (pp/pprint row)))
    (let [m (utils/nsmap->map row)
          retval (mydatomic/tx!
                   (mydatomic/create-youtube-playlist-no-conf m))]
      (println retval)
  ;(mydatomic/create-youtube-playlist)
      {:youtube-video/id 111
       :returned-url retval})))

(comment
  (def x {:from-youtube/videos
          [{:from-youtube-video/title
                                             "DOES19 Las Vegas - Lightning Talks presented by Sonatype (Full)",
            :from-youtube-video/published-at "2019-11-02T20:21:14Z",
            :from-youtube-video/description
                                             "DOES19 Las Vegas\nDOES 2019 US\nDevOps Enterprise Summit\nhttps://events.itrevolution.com/us/",
            :from-youtube-video/url
                                             "https://www.youtube.com/watch?v=ISxcNdc0gLg",
            :from-youtube-video/video-id     "ISxcNdc0gLg",
            :from-youtube-video/position     0,
            :from-youtube-video/playlist-id
                                             "PLvk9Yh_MWYuwRnn_W242n-AdYJtnflEOR",
            :from-youtube-video/id
                                             "UEx2azlZaF9NV1l1d1Jubl9XMjQybi1BZFlKdG5mbEVPUi41NkI0NEY2RDEwNTU3Q0M2"}
           {:from-youtube-video/title        "Lightning Talks - DJ Schleen",
            :from-youtube-video/published-at "2019-11-02T20:22:41Z",
            :from-youtube-video/description
                                             "DOES19 Las Vegas\nDOES 2019 US\nDevOps Enterprise Summit\nhttps://events.itrevolution.com/us/",
            :from-youtube-video/url
                                             "https://www.youtube.com/watch?v=LK99X3jLhYI",
            :from-youtube-video/video-id     "LK99X3jLhYI",
            :from-youtube-video/position     1,
            :from-youtube-video/playlist-id
                                             "PLvk9Yh_MWYuwRnn_W242n-AdYJtnflEOR",
            :from-youtube-video/id
                                             "UEx2azlZaF9NV1l1d1Jubl9XMjQybi1BZFlKdG5mbEVPUi4yODlGNEE0NkRGMEEzMEQy"}]})


  (def playlist-id (-> x :from-youtube/videos first :from-youtube-video/playlist-id))

  (def txs (->> x
                :from-youtube/videos
                (map utils/nsmap->map)
                (take 10)
                ;(map #(utils/map->nsmap % "youtube-video"))
                (map #(mydatomic/create-youtube-video-from-youtube-api % playlist-id))))

  (map mydatomic/tx! txs)

  (mydatomic/tx! #:youtube-video{:id "UEx2azlZaF9NV1l1d1Jubl9XMjQybi1BZFlKdG5mbEVPUi41NkI0NEY2RDEwNTU3Q0M2",
                                 :playlist-id [:youtube-playlist/id "PLvk9Yh_MWYuwRnn_W242n-AdYJtnflEOR"],
                                 :position 0,
                                 :description "DOES19 Las Vegas
                               DOES 2019 US
                               DevOps Enterprise Summit
                               https://events.itrevolution.com/us/",
                                 :title "DOES19 Las Vegas - Lightning Talks presented by Sonatype (Full)",
                                 :url "https://www.youtube.com/watch?v=ISxcNdc0gLg",
                                 :video-id "ISxcNdc0gLg"})

  ,)

(pc/defmutation save-all-videos-to-database
  [env videos]
  {::pc/output [:from-youtube/videos :returned-url]}
  ; TASK: maybe return the new playlists, and return it?
  (do
    (println "defmutation: save-all-videos-to-database: \n"
             (with-out-str (pp/pprint videos)))
    (let [playlist-id (-> videos :from-youtube/videos first :from-youtube-video/playlist-id)
          ms          (->> videos
                           :from-youtube/videos
                           ;(take 10)
                           (map utils/nsmap->map)
                           ;(map #(utils/map->nsmap % "youtube-video"))
                           (map #(mydatomic/create-youtube-video-from-youtube-api % playlist-id)))
          _           (println "write maps: \n" (with-out-str (pp/pprint ms)))
          retval      (doall (map mydatomic/tx! ms))]
      (println "=== return from tx!")
      (println retval)
      ;(println (with-out-str (pp/pprint retval)))
      ;(mydatomic/create-youtube-playlist)
      {:youtube-video/id 111
       :returned-url     "retval"})))

;(pc/defmutation save-youtube-playlist-to-database-given-playlist-id
;  [env rows]
;  {::pc/output [:youtube-playlist/id :returned-url]}
;  (println "defmutation: save-youtube-playlist-to-database-given-playlist-id: "
;           (with-out-str (pp/pprint rows)))
;  {:from-youtube-playlist/id 333
;   :returned-url "xxxyyyzzz"})




;(pc/defmutation simulate-bill-run [env {:keys [orgnr]}]
;  {;::pc/params [:orgnr]
;   ::pc/output [:tem-organization/organization-number :bill-run/logs]}
;  (assert orgnr "orgnr is required")
;  (let [logs
;        (:logs (kostnadsdeling-data/simulate-bill-run
;                 (doto (get-in env [:com.fulcrologic.rad.database-adapters.sql/connection-pools :minbedrift]) (assert "Missing MB DB"))
;                 orgnr))]
;    {:tem-organization/organization-number orgnr
;     :bill-run/logs (vec logs)}))


;#?(:cljs
;   (defmutation simulate-bill-run [{:keys [orgnr]}]
;               (action [{:keys [app state ref]}
;                        (df/set-load-marker! app :simulate-bill-run :loading)])
;               (remote [env] (m/returning env (doto (comp/registry-key->class :minbedrift.ui.kostnadsdeling.ui/SimulateBillRun) (assert))))
;               (refresh [_] [:tem-organization/organization-number orgnr])
;               (ok-action [{:keys [app state]}] (df/remove-load-marker! app :simulate-bill-run))
;               (error-action [{:keys [app state]}](df/set-load-marker! app :simulate-bill-run :failed)))
;   :clj
;   (pc/defmutation simulate-bill-run [env {:keys [orgnr]}]
;     {;::pc/params [:orgnr]
;      ::pc/output [:tem-organization/organization-number :bill-run/logs]}
;     (assert orgnr "orgnr is required")
;     (let [logs
;           (:logs (kostnadsdeling-data/simulate-bill-run
;                    (doto (get-in env [:com.fulcrologic.rad.database-adapters.sql/connection-pools :minbedrift]) (assert "Missing MB DB"))
;                    orgnr))]
;       {:tem-organization/organization-number orgnr
;        :bill-run/logs (vec logs)})))


(def resolvers [fetch-vimeo-entry fetch-from-youtube-playlists
                save-youtube-playlist-to-database
                save-all-videos-to-database])

; save-youtube-playlist-to-database-given-playlist-id
(ns com.example.model.mutations
  (:require
    [com.wsscode.pathom.connect :as pc]
    [youtube.main :as yt]))


;
; server-side
;

(pc/defmutation fetch-vimeo-entry
  [env row]
  {::pc/output [:youtube-playlist/id :returned-url]}
  (println "defmutation: " row)
  {:youtube-video/id 111
   :returned-url "abcdefdef"})


; https://stackoverflow.com/questions/43722091/clojure-programmatically-namespace-map-keys

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

(pc/defmutation fetch-from-youtube-playlists
   [env _]
   {::pc/output {:ui.from-youtube/playlists [:youtube-playlist/id
                                             :youtube-playlist/title]}}
   (println "fetch-from-youtube-playlists: ")
   (let [ytchannel (:main yt/itrev-channels)
         retval (->> (yt/fetch-channel-playlists-parsed! ytchannel)
                     ;(take 15)
                     (map #(select-keys % [:id :title]))
                     (map #(map->nsmap % "youtube-playlist")))]
     (println retval)
     {:ui.from-youtube/playlists retval}))
   ;{:ui.from-youtube/playlists
   ; [{:youtube-playlist/id "xyz"
   ;   :youtube-playlist/title "abc"}
   ;  {:youtube-playlist/id "xxx"
   ;   :youtube-playlist/title "def"}]})

; ({:id "PLvk9Yh_MWYuysEkC8lQCm_9vpEFh2eCrj",
;  :publishedAt "2020-10-14T17:21:56Z",
;  :title "Andy Patton's Antipatterns",
;  :description "",
;  :channelId "UCkAQCw5_sIZmj2IkSrNy00A",
;  :itemCount 4}


(comment
  (def ytchannel (:main yt/itrev-channels))

  (def playlists (yt/fetch-channel-playlists-parsed! ytchannel))
  (->> playlists
       (map #(select-keys % [:id :title]))
       (map #(map->nsmap % "youtube-playlist")))
  (identity playlists)),



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


(def resolvers [fetch-vimeo-entry fetch-from-youtube-playlists])

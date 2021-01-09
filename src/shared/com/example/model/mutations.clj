(ns com.example.model.mutations
  (:require
    [com.wsscode.pathom.connect :as pc]))

;
; server-side
;

(pc/defmutation fetch-vimeo-entry
  [env row]
  {::pc/output [:youtube-playlist/id :returned-url]}
  (println "defmutation: " row)
  {:youtube-video/id 111
   :returned-url "abcdefdef"})

(pc/defmutation fetch-from-youtube-playlists
   [env _]
   {::pc/output {:ui.from-youtube/playlists [:youtube-playlist/id
                                             :youtube-playlist/title]}}
   (println "fetch-from-youtube-playlists: ")
   {:ui.from-youtube/playlists
    [{:youtube-playlist/id "xyz"
      :youtube-playlist/title "abc"}
     {:youtube-playlist/id "xxx"
      :youtube-playlist/title "def"}]})



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

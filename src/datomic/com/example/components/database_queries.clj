(ns com.example.components.database-queries
  (:require
    [com.fulcrologic.rad.database-adapters.datomic-cloud :as datomic]
    [com.fulcrologic.rad.database-adapters.datomic-options :as do]
    [datomic.client.api :as d]
    [taoensso.timbre :as log]
    [taoensso.encore :as enc]))

(defn get-all-accounts
  [env query-params]
  (if-let [db (some-> (get-in env [do/databases :production]) deref)]
    (let [ids (if (:show-inactive? query-params)
                (d/q '[:find ?uuid
                       :where
                       [?dbid :account/id ?uuid]] db)
                (d/q '[:find ?uuid
                       :where
                       [?dbid :account/active? true]
                       [?dbid :account/id ?uuid]] db))]
      (->> ids
        flatten
        (mapv (fn [id] {:account/id id}))))
    (log/error "No database atom for production schema!")))

(defn get-all-items
  [env {:category/keys [id]}]
  (if-let [db (some-> (get-in env [do/databases :production]) deref)]
    (let [ids (if id
                (d/q '[:find ?uuid
                       :in $ ?catid
                       :where
                       [?c :category/id ?catid]
                       [?i :item/category ?c]
                       [?i :item/id ?uuid]] db id)
                (d/q '[:find ?uuid
                       :where
                       [_ :item/id ?uuid]] db))]
      (->> ids
        flatten
        (mapv (fn [id] {:item/id id}))))
    (log/error "No database atom for production schema!")))

(defn get-customer-invoices [env {:account/keys [id]}]
  (if-let [db (some-> (get-in env [do/databases :production]) deref)]
    (let [ids (d/q '[:find ?uuid
                     :in $ ?cid
                     :where
                     [?dbid :invoice/id ?uuid]
                     [?dbid :invoice/customer ?c]
                     [?c :account/id ?cid]] db id)]
      (->> ids
        flatten
        (mapv (fn [id] {:invoice/id id}))))
    (log/error "No database atom for production schema!")))

(defn get-all-invoices
  [env query-params]
  (if-let [db (some-> (get-in env [do/databases :production]) deref)]
    (let [ids (d/q '[:find ?uuid
                     :where
                     [?dbid :invoice/id ?uuid]] db)]
      (->> ids
        flatten
        (mapv (fn [id] {:invoice/id id}))))
    (log/error "No database atom for production schema!")))

(defn get-invoice-customer-id
  [env invoice-id]
  (if-let [db (some-> (get-in env [do/databases :production]) deref)]
    (let [ids (d/q '[:find ?account-uuid
                     :in $ ?invoice-uuid
                     :where
                     [?i :invoice/id ?invoice-uuid]
                     [?i :invoice/customer ?c]
                     [?c :account/id ?account-uuid]] db invoice-id)]
      (first (flatten ids)))
    (log/error "No database atom for production schema!")))

(defn get-all-categories
  [env query-params]
  (if-let [db (some-> (get-in env [do/databases :production]) deref)]
    (let [ids (d/q '[:find ?id
                     :where
                     [?e :category/label]
                     [?e :category/id ?id]] db)]
      (->> ids
        flatten
        (mapv (fn [id] {:category/id id}))))
    (log/error "No database atom for production schema!")))

(defn get-line-item-category [env line-item-id]
  (if-let [db (some-> (get-in env [do/databases :production]) deref)]
    (first (d/q '[:find ?cid
                  :in $ ?line-item-id
                  :where
                  [?e :line-item/id ?line-item-id]
                  [?e :line-item/item ?item]
                  [?item :item/category ?c]
                  [?c :category/id ?cid]] db line-item-id))
    (log/error "No database atom for production schema!")))

(defn get-all-sessions
  [env query-params]
  (println "get-all-sessions...")
  ;(tap> env)
  ; :com.fulcrologic.rad.database-adapters.datomic/databases
  ;(println "env: ")
  ;(clojure.pprint/pprint env)
  ;(println "db production: " (get-in env [do/databases :production]))
  ;(println "db all: " (get-in env [do/databases]))
  ;(if-let [db (some-> (get-in env [do/databases :main #_:video]) deref)])
  ;(if-let [db (some-> (get-in env [do/databases :production]) deref)]
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (let [ids (d/q '[:find (pull ?s [* {:session/conf-id [:db/id :conference/name]
                                        :session/tags-2  [:session-tag-2/id
                                                          {:session-tag-2/video-tag
                                                           [:video-tag/id
                                                            :video-tag/name]}]
                                        :session/tags    [:db/id
                                                          :session-tag/id
                                                          :session-tag/tag-id-2
                                                          :session-tag/session-id
                                                          {:session-tag/session-eid-2 [:db/id :session/uuid
                                                                                       :session/title]}
                                                          {:session-tag/tag-eid-2 [:db/id :video-tag/id
                                                                                   :video-tag/name]}]}])
                     ;(let [ids (d/q '[:find ?s
                     :where
                     [?s :session/title _]] db)]
      ;(println "db: " db)
      ;(println "ids: " ids)
      (->> ids
           ;(take 5)
           flatten
           (#(apply vector %))))
      ;(->> ids
      ;     (take 5)
      ;     flatten
      ;     (mapv (fn [id] {:db/id id}))))
    (log/error "No database atom for production schema!")))

; must return a map; Jakub noted that it returned a empty vector
; can return a nil, but not a vector
(defn get-session-from-uuid
  [env uuid]
  (log/error "db-query: get-session-from-uuid: " uuid)
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (do
      ;(tap> uuid)
      ;(log/info "get-session-from-uuid: id: " uuid)
      ;(log/info "get-session-from-uuid: dbs: " (get-in env [do/databases]))
      ;(log/info "get-session-from-uuid: env: " env)
      (let [session (d/q '[:find (pull ?e [* {:session/conf-id [:db/id :conference/name]
                                              :session/tags-2  [:session-tag-2/id
                                                                {:session-tag-2/video-tag
                                                                 [:video-tag/id
                                                                  :video-tag/name]}]
                                              :session/tags    [:db/id
                                                                :session-tag/id
                                                                :session-tag/tag-id-2
                                                                :session-tag/session-id
                                                                {:session-tag/session-eid-2 [:db/id :session/uuid
                                                                                             :session/title]}
                                                                {:session-tag/tag-eid-2 [:db/id :video-tag/id
                                                                                               :video-tag/name]}]}])
                           :in $ ?uuid
                           ;(let [ids (d/q '[:find ?s
                           :where
                           [?e :session/uuid ?uuid]] db uuid)]
        ;(println "db: " db)
        ;(println "session: " session)
        ;(tap> session)
        (ffirst session)))

    ;(->> ids
    ;     flatten
    ;     (mapv (fn [id] {:db/id id}))))
    (log/error "No database atom for production schema!")))

(defn get-all-youtube-videos
  [env query-params]
  (println "get-all-youtube-videos...")
  ;(tap> env)
  ; :com.fulcrologic.rad.database-adapters.datomic/databases
  ;(println "env: ")
  ;(clojure.pprint/pprint env)
  ;(println "db production: " (get-in env [do/databases :production]))
  ;(println "db all: " (get-in env [do/databases]))
  ;(if-let [db (some-> (get-in env [do/databases :main #_:video]) deref)])
  ;(if-let [db (some-> (get-in env [do/databases :production]) deref)]
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (let [ids (d/q '[:find (pull ?s [* {:youtube-video/playlist-id  [:db/id
                                                                     :youtube-playlist/id
                                                                     :youtube-playlist/title]}])
                     ;(let [ids (d/q '[:find ?s
                     :where
                     [?s :youtube-video/id _]] db)]
      ;(println "db: " db)
      ;(println "ids: " ids)
      (->> ids
           ;(take 5)
           flatten
           (#(apply vector %))))
    ;(->> ids
    ;     (take 5)
    ;     flatten
    ;     (mapv (fn [id] {:db/id id}))))
    (log/error "No database atom for production schema!")))



(defn youtube-video-by-id
  [env id]
  (log/error "youtube-video-by-id: " id)
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (do
      (log/info "youtube-video-by-id: id: " id)
      ;(log/info "get-session-from-uuid: dbs: " (get-in env [do/databases]))
      ;(log/info "youtube-video-by-id: env: " env)
      (let [session (d/q '[:find (pull ?e [*])
                           :in $ ?id
                           ;(let [ids (d/q '[:find ?s
                           :where
                           [?e :youtube-video/id ?id]] db id)]
        ;(println "db: " db)
        (println "youtube-video: " session)
        (ffirst session)))
    ;(->> ids
    ;     flatten
    ;     (mapv (fn [id] {:db/id id}))))
    (log/error "No database atom for production schema!")))

(defn get-video-playlist [env id]
  (log/info "get-video-playlist: id, playlist-id: " id)
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (first (d/q '[:find ?playlist-title
                  :in $ ?id
                  :where
                  [?e :youtube-video/id ?id]
                  [?e :youtube-video/playlist-id ?ep]
                  [?ep :youtube-playlist/title ?playlist-title]]
                db id))
    (log/error "No database atom for production schema!")))

(defn youtube-video-by-playlist-id [env playlist-id]
  (log/info "youtube-video-by-playlist-id: playlist-id: " playlist-id)
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (let [retval (d/q '[:find (pull ?e [*])
                        :in $ ?playlist-id
                        :where
                        [?e :youtube-video/id ?id]
                        [?e :youtube-video/playlist-id ?ep]
                        [?ep :youtube-playlist/id ?playlist-id]]
                      db playlist-id)]
      (println "youtube-video-by-playlist-id: " retval)
      ;retval
      (->> retval
           ;(take 5)
           flatten
           (#(apply vector %))))
    (log/error "No database atom for production schema!")))

;
; video tags
;

(defn get-all-video-tags
  [env query-params]
  (println "get-all-video-tags...")
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (let [tags (d/q '[:find (pull ?e [*])
                      :where
                      [?e :video-tag/id _]] db)]
      ;(println "db: " db)
      ;(println "tags: " tags)
      (tap> tags)
      (->> tags
           ;(take 5)
           flatten
           (#(apply vector %))))
    (log/error "No database atom for production schema!")))

(defn fetch-video-tag-by-uuid
  [env uuid]
  (log/error "fetch-video-tag-by-uuid: " uuid)
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (do
      ;(tap> uuid)
      ;(log/info "get-session-from-uuid: id: " uuid)
      ;(log/info "get-session-from-uuid: dbs: " (get-in env [do/databases]))
      ;(log/info "get-session-from-uuid: env: " env)
      (let [tags (d/q '[:find (pull ?e [*])
                        :in $ ?uuid
                        ;(let [ids (d/q '[:find ?s
                        :where
                        [?e :video-tag/id ?uuid]] db uuid)]
        ;(println "db: " db)
        (println "session: " tags)
        (tap> tags)
        (ffirst tags)))
    ;(->> ids
    ;     flatten
    ;     (mapv (fn [id] {:db/id id}))))
    (log/error "No database atom for production schema!")))

;
; session video tags
;

(defn get-all-session-tags
  [env query-params]
  (println "dbquery: get-all-session-tags...")
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (let [tags (d/q '[:find (pull ?e [*
                                      :session-tag/id
                                      {:session-tag/session-eid-2 [:db/id :session/uuid
                                                                   :session/title]}
                                      {:session-tag/tag-eid-2 [:db/id :video-tag/id
                                                               :video-tag/name]}])
                      :where
                      [?e :session-tag/id _]] db)]
      ;(println "db: " db)
      ;(println "tags: " tags)
      (tap> tags)
      (->> tags
           ;(take 5)
           flatten
           (#(apply vector %))))
    (log/error "No database atom for production schema!")))

(defn get-all-session-tags-2
  [env query-params]
  (println "dbquery: get-all-session-tags...")
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (let [tags (d/q '[:find (pull ?e [* {:session-tag-2/video-tag
                                         [:video-tag/id
                                          :video-tag/name]}])
                      :where
                      [?e :session-tag-2/id _]] db)]
      ;(println "db: " db)
      ;(println "tags: " tags)
      (tap> tags)
      (->> tags
           ;(take 5)
           flatten
           (#(apply vector %))))
    (log/error "No database atom for production schema!")))

;
; video playlists
;

(defn get-all-youtube-playlists
  [env query-params]
  (println "dbquery: get-all-youtube-playlists...")
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (let [playlists (d/q '[:find (pull ?s [* {:youtube-playlist/conf-id [:conference/uuid]}])
                           :where
                           [?s :youtube-playlist/id _]] db)]
      (tap> playlists)
      (->> playlists
           ;(take 5)
           flatten
           (#(apply vector %))))
    (log/error "No database atom for production schema!")))

;
; conferences
;

(defn get-all-conferences
  [env query-params]
  (println "dbquery: get-all-conferences...")
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (let [confs (d/q '[:find (pull ?e [* {:conference/youtube-playlists
                                          [:youtube-playlist/id :youtube-playlist/title
                                           :db/id]}])
                       :where
                       [?e :conference/name _]] db)]
      (tap> confs)
      (->> confs
           ;(take 5)
           flatten
           (#(apply vector %))))
    (log/error "No database atom for production schema!")))

;(defn get-conference-playlists [env {:conference/keys [id]}]
;  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
;    (let [ids (d/q '[:find ?uuid
;                     :in $ ?cid
;                     :where
;                     [?dbid :invoice/id ?uuid]
;                     [?dbid :invoice/customer ?c]
;                     [?c :account/id ?cid]] db id)]
;      (->> ids
;           flatten
;           (mapv (fn [id] {:invoice/id id}))))
;    (log/error "No database atom for production schema!")))

(defn get-conference-playlists [env {:conference/keys [uuid]}]
  (println "get-conference-playlists: id: " uuid)
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (let [ids (d/q '[:find ?playlistid
                     :in $ ?confuuid
                     :where
                     [?playlisteid :youtube-playlist/id ?playlistid]
                     [?confeid :conference/youtube-playlists ?playlisteid]
                     [?confeid :conference/uuid ?confuuid]] db uuid)]
      (println "retval: " ids)
      (->> ids
           flatten
           (mapv (fn [id] {:youtube-playlist/id id}))))
    (log/error "No database atom for production schema!")))



;
;
;

(defn get-login-info
  "Get the account name, time zone, and password info via a username (email)."
  [{::datomic/keys [databases] :as env} username]
  (if-let [db (some-> (get-in env [do/databases :production]) deref)]
    (d/pull db [:account/name
                {:time-zone/zone-id [:db/ident]}
                :password/hashed-value
                :password/salt
                :password/iterations]
      [:account/email username])))


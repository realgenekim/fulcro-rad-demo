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
  ; :com.fulcrologic.rad.database-adapters.datomic/databases
  ;(println "env: ")
  ;(clojure.pprint/pprint env)
  ;(println "db production: " (get-in env [do/databases :production]))
  ;(println "db all: " (get-in env [do/databases]))
  ;(if-let [db (some-> (get-in env [do/databases :main #_:video]) deref)])
  ;(if-let [db (some-> (get-in env [do/databases :production]) deref)]
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (let [ids (d/q '[:find (pull ?s [*])
                     ;(let [ids (d/q '[:find ?s
                     :where
                     [?s :session/title _]] db)]
      (println "db: " db)
      (println "ids: " ids)
      (->> ids
           (take 5)
           flatten
           (#(apply vector %))))
      ;(->> ids
      ;     (take 5)
      ;     flatten
      ;     (mapv (fn [id] {:db/id id}))))
    (log/error "No database atom for production schema!")))

(defn get-session-from-eid
  [env session-id]
  (log/error "get-session-from-eid: " session-id)
  (if-let [db (some-> (get-in env [do/databases :video]) deref)]
    (do
      (log/info "get-session-from-eid: id: " session-id)
      (let [session (d/q '[:find (pull ?id [*])
                           :in $ ?id
                           ;(let [ids (d/q '[:find ?s
                           :where
                           [?id :session/speakers _]] db session-id)]
        ;(println "db: " db)
        (println "session: " session)
        session))
      ;(->> ids
      ;     flatten
      ;     (mapv (fn [id] {:db/id id}))))
    (log/error "No database atom for production schema!")))




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

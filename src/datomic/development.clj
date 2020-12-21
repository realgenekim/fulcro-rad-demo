(ns development
  (:require
    [clojure.pprint :refer [pprint]]
    [clojure.repl :refer [doc source]]
    [clojure.tools.namespace.repl :as tools-ns :refer [disable-reload! refresh clear set-refresh-dirs]]
    [com.example.components.datomic :refer [datomic-connections]]
    [com.example.components.ring-middleware]
    [com.example.components.server]
    [com.example.model.seed :as seed]
    [com.example.model.account :as account]
    [com.example.model.address :as address]
    [com.example.model.session :as session]
    [com.fulcrologic.rad.ids :refer [new-uuid]]
    [com.fulcrologic.rad.database-adapters.datomic-cloud :as datomic]
    [com.fulcrologic.rad.database-adapters.datomic-common :as dc]
    [com.fulcrologic.rad.resolvers :as res]
    [mount.core :as mount]
    [taoensso.timbre :as log]
    [datomic.client.api :as d]
    [com.fulcrologic.rad.attributes :as attr]
    [com.fulcrologic.rad.type-support.date-time :as dt]
    [com.fulcrologic.rad.database-adapters.datomic-options :as do]
    [com.fulcrologic.fulcro.algorithms.denormalize :as fdn]))

(set-refresh-dirs "src/main" "src/datomic" "src/dev" "src/shared")

(comment
  (clojure.core/require 'development)
  (development/go)
  (restart)
  (development/reset)

  (mount/find-all-states)
  ; ("#'com.example.components.parser/parser"
  ; "#'com.example.components.blob-store/temporary-blob-store"
  ; "#'com.example.components.ring-middleware/middleware"
  ; "#'com.example.components.server/http-server"
  ; "#'com.example.components.auto-resolvers/automatic-resolvers"
  ; "#'com.example.components.config/config"
  ; "#'com.example.components.blob-store/image-blob-store"
  ; "#'com.example.components.datomic/datomic-connections"
  ; "#'com.example.components.blob-store/file-blob-store")
  (mount/current-state "#'com.example.components.config/config")
  (mount/current-state "#'com.example.components.parser/parser"))



(comment
  (let [db (d/db (:main datomic-connections))]
    (d/pull db '[*] [:account/id (new-uuid 100)]))

  ; OMG, it works!

  (let [db (d/db (:video datomic-connections))]
    (d/q '[:find (pull ?s [*])
           :where
           [?s :session/title _]] db))

  (let [db (d/db (:video datomic-connections))]
      (d/q '[:find (pull ?e [*])
             :where
             [?e :session/title _]] db 70368744177664139))

      

  (com.example.components.parser/parser com.example.components.config/config
                                        {:address/id 1})

  ; works
  (com.example.components.parser/parser com.example.components.config/config
                                       [:account/all-accounts])



  ; works ^^^
  ; now working on getting joins working
  (restart)

  (com.example.components.parser/parser com.example.components.config/config
                                        [:session/all-sessions])
  
  (com.example.components.parser/parser com.example.components.config/config
                                        [{:session/all-sessions
                                          [:session/uuid :session/title :session/speakers
                                           :session/tags]}])

  (restart)

  (com.example.components.parser/parser com.example.components.config/config
                                        [{:youtube-video/all-videos
                                          [:youtube-video/id :youtube-video/video-id
                                           :youtube-video/description :youtube-video/url
                                           :youtube-playlist/title
                                           :youtube-video/playlist-id]}])

  ;(com.example.components.parser/parser com.example.components.config/config
  ;                                        [{session/:db/id 70368744177664139
  ;                                          [:session/speakers]}])

  ; tags

  (restart)

  (com.example.components.parser/parser com.example.components.config/config
                                        [{:video-tag/all-tags
                                          [:video-tag/id :video-tag/name]}])

  ; XXX: Jakub: I'm trying to get this to work...
  (restart)

  (com.example.components.parser/parser com.example.components.config/config
                                            [{[:session/uuid #uuid"8a481331-eb1d-4e5b-9d19-759da23cb674"]
                                              [:session/venue :session/speakers :session/start-time-utc]}])

  ; {:session/conf-sched-id "5348024557502565-140"
  ;                            :session/conf-id {:db/id 5348024557502565}
  ;                            :session/venue "Track 2"
  ;                            :session/title "Fast Product Development in Digital Banking Without Sacrificing Security (Bancolombia)"
  ;                            :session/sched-id 140
  ;                            :session/uuid #uuid "1b03d496-ce5e-4da5-baa9-a5b3a92555df"
  ;                            :session/start-time-utc #inst "2020-10-15T18:40:00.000-00:00"
  ;                            :session/type {:db/id 15942918602752074
  ;                                           :db/ident :session-type/track}
  ;                            :db/id 71683760084484231
  ;                            :session/speakers "Rafael Alvarez; Camilo Piedrahita"}

  (com.example.components.parser/parser com.example.components.config/config
                                        [{[:youtube-video/id "UEx2azlZaF9NV1l1d1hDMGlVNUVBQjFyeUk2MllwUEhSOS43MTI1NDIwOTMwQjIxMzNG"]
                                          [:youtube-video/id :youtube-video/video-id :youtube-video/playlist-id
                                           :youtube-video/description :youtube-video/url]}])


  ; ￼12:38PM IT WORKS!!  THANK YOU JAKUB!

  (dc/delta->txn com.example.components.config/config #{:video}
                 {[:session/uuid #uuid "60dd0f5e-7215-40ba-a110-3af30c40b7bf"]
                  {:session/speakers
                   {:before "Shaaron A Alvares", :after "Shaaron A Alvares SSS"}}})




  ; :account/all-accounts

  (com.example.components.parser/parser com.example.components.config/config
                                        {:account/email "tony@example.com"})

  (com.example.components.parser/parser com.example.components.config/config
                                        [{:account/email "tony@example.com"}
                                         :password/hashed-value
                                         :password/salt,
                                         :password/iterations]))
(comment

  (identity com.example.components.config/config)

  (get-in com.example.components.config/config
          [com.fulcrologic.rad.database-adapters.datomic-options/databases])

  (get-in com.example.components.config/config
          [do/databases])

  (let [env com.example.components.config/config]
    (some-> (get-in env [com.fulcrologic.rad.database-adapters.datomic-options/databases :video]) deref))

  (com.fulcrologic.rad.database-adapters.datomic-options/databases
    com.example.components.config/config)

  (com.example.components.database-queries/get-all-sessions
    ;datomic-connections
    com.example.components.config/config
    {})

  (com.example.components.database-queries/get-session-from-eid
           datomic-connections
           39762738506891394)

  (let [db (d/db (:video datomic-connections))])

       ;(d/q '[:find (pull ?s [*])
       ;       :where
       ;       [?s :session/title _]] db))

  (com.example.components.database-queries/get-session-from-eid
      datomic-connections
      ;com.example.components.config/config,
      47872736273367215))



(defn seed! []
  (dt/set-timezone! "America/Los_Angeles")
  (let [connection (:main datomic-connections)
        date-1     (dt/html-datetime-string->inst "2020-01-01T12:00")
        date-2     (dt/html-datetime-string->inst "2020-01-05T12:00")
        date-3     (dt/html-datetime-string->inst "2020-02-01T12:00")
        date-4     (dt/html-datetime-string->inst "2020-03-10T12:00")
        date-5     (dt/html-datetime-string->inst "2020-03-21T12:00")]
    (when connection
      (log/info "SEEDING data.")
      (d/transact connection [(seed/new-address (new-uuid 1) "111 Main St.")
                              (seed/new-account (new-uuid 100) "Tony" "tony@example.com" "letmein"
                                                :account/addresses ["111 Main St."]
                                                :account/primary-address (seed/new-address (new-uuid 300) "222 Other")
                                                :time-zone/zone-id :time-zone.zone-id/America-Los_Angeles)
                              (seed/new-account (new-uuid 101) "Sam" "sam@example.com" "letmein")
                              (seed/new-account (new-uuid 102) "Sally" "sally@example.com" "letmein")
                              (seed/new-account (new-uuid 103) "Barbara" "barb@example.com" "letmein")
                              (seed/new-category (new-uuid 1000) "Tools")
                              (seed/new-category (new-uuid 1002) "Toys")
                              (seed/new-category (new-uuid 1003) "Misc")
                              (seed/new-item (new-uuid 200) "Widget" 33.99
                                             :item/category "Misc")
                              (seed/new-item (new-uuid 201) "Screwdriver" 4.99
                                             :item/category "Tools")
                              (seed/new-item (new-uuid 202) "Wrench" 14.99
                                             :item/category "Tools")
                              (seed/new-item (new-uuid 203) "Hammer" 14.99
                                             :item/category "Tools")
                              (seed/new-item (new-uuid 204) "Doll" 4.99
                                             :item/category "Toys")
                              (seed/new-item (new-uuid 205) "Robot" 94.99
                                             :item/category "Toys")
                              (seed/new-item (new-uuid 206) "Building Blocks" 24.99
                                             :item/category "Toys")
                              (seed/new-invoice "invoice-1" date-1 "Tony"
                                                [(seed/new-line-item "Doll" 1 5.0M)
                                                 (seed/new-line-item "Hammer" 1 14.99M)])
                              (seed/new-invoice "invoice-2" date-2 "Sally"
                                                [(seed/new-line-item "Wrench" 1 12.50M)
                                                 (seed/new-line-item "Widget" 2 32.0M)])
                              (seed/new-invoice "invoice-3" date-3 "Sam"
                                                [(seed/new-line-item "Wrench" 2 12.50M)
                                                 (seed/new-line-item "Hammer" 2 12.50M)])
                              (seed/new-invoice "invoice-4" date-4 "Sally"
                                                [(seed/new-line-item "Robot" 6 89.99M)])
                              (seed/new-invoice "invoice-5" date-5 "Barbara"
                                                [(seed/new-line-item "Building Blocks" 10 20.0M)])]))))

(defn start []
  (mount/start-with-args {:config "config/dev.edn"})
  (seed!)
  :ok)

(defn stop
  "Stop the server."
  []
  (mount/stop))

(def go start)

(defn restart
  "Stop, refresh, and restart the server."
  []
  (stop)
  (tools-ns/refresh :after 'development/start))

(def reset #'restart)


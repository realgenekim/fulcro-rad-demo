(ns com.example.components.parser
  (:require
    [com.example.components.auto-resolvers :refer [automatic-resolvers]]
    [com.example.components.blob-store :as bs]
    [com.example.components.config :refer [config]]
    [com.example.components.datomic :refer [datomic-connections]]
    [com.example.components.delete-middleware :as delete]
    [com.example.components.save-middleware :as save]
    [com.example.model :refer [all-attributes]]
    [com.example.model.account :as account]
    [com.example.model.invoice :as invoice]
    [com.example.model.timezone :as timezone]
    [com.fulcrologic.rad.attributes :as attr]
    [com.fulcrologic.rad.blob :as blob]
    [com.fulcrologic.rad.database-adapters.datomic-cloud :as datomic]
    [com.fulcrologic.rad.database-adapters.datomic-common :as dc]
    [com.fulcrologic.rad.database-adapters.datomic-options :as do]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.pathom :as pathom]
    [mount.core :refer [defstate]]
    [com.example.model.sales :as sales]
    [com.example.model.item :as item]
    [com.wsscode.pathom.core :as p]
    [com.fulcrologic.rad.type-support.date-time :as dt]
    [com.example.model.session :as session]
    [com.example.model.youtube-video :as youtube]
    [com.example.model.video-tag :as video-tag]
    [com.example.model.mutations :as mymutations]))

(defstate parser
  :start
  (pathom/new-parser config
    [(attr/pathom-plugin all-attributes)
     (form/pathom-plugin save/middleware delete/middleware)
     ;(datomic/pathom-plugin (fn [env] {:production (:main datomic-connections)}))
     ; put both databases into pathom
     ;   and specify during the query
     (datomic/pathom-plugin (fn [env]
                              ;(tap> env)
                              {:production (:main datomic-connections)
                               :video (:video datomic-connections)}))
     ; (fn [env]
     ;                              (let [dbs (get-in env [do/databases])]
     ;                                (println "pathom-plugin: dbs: " dbs)
     ;                                {:production (:main dbs)
     ;                                 :video      (:video dbs)}))
     ;
     ;(datomic/pathom-plugin (fn [env] {:production (:video datomic-connections)}))
     ;(datomic/pathom-plugin (fn [env] {:main (:main datomic-connections)
     ;                                  :video (:video datomic-connections)}))
     (blob/pathom-plugin bs/temporary-blob-store {:files         bs/file-blob-store
                                                  :avatar-images bs/image-blob-store})
     {::p/wrap-parser
      (fn transform-parser-out-plugin-external [parser]
        (fn transform-parser-out-plugin-internal [env tx]
          ;; TASK: This should be taken from account-based setting
          (dt/with-timezone "America/Los_Angeles"
            (if (and (map? env) (seq tx))
              (parser env tx)
              {}))))}]
    [automatic-resolvers
     form/resolvers
     (blob/resolvers all-attributes)
     account/resolvers
     session/resolvers
     youtube/resolvers
     video-tag/resolvers
     invoice/resolvers
     item/resolvers
     sales/resolvers
     timezone/resolvers
     mymutations/resolvers]))




(ns com.example.components.parser
  (:require
    [com.example.components.auto-resolvers :refer [automatic-resolvers]]
    [com.example.components.config :refer [config]]
    [com.example.components.connection-pools :as pools]
    [com.fulcrologic.rad.database-adapters.sql.plugin :as sql]
    [com.fulcrologic.rad.pathom :as pathom]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.blob :as blob]
    [com.example.components.blob-store :as bs]
    [com.example.components.save-middleware :as save]
    [com.example.components.delete-middleware :as delete]
    [mount.core :refer [defstate]]
    [com.example.model :refer [all-attributes]]
    [com.example.model.account :as account]
    [com.example.model.timezone :as timezone]
    [com.fulcrologic.rad.attributes :as rad.attr]
    [com.example.model.invoice :as invoice]))

(defstate parser
  :start
  (pathom/new-parser config
    [(rad.attr/pathom-plugin all-attributes)
     (form/pathom-plugin save/middleware delete/middleware)
     (sql/pathom-plugin (fn [_] {:production (:main pools/connection-pools)}))
     (sql/pathom-plugin (fn [_] {:production (:video pools/connection-pools)}))
     (blob/pathom-plugin bs/temporary-blob-store {:files         bs/file-blob-store
                                                  :avatar-images bs/image-blob-store})]
    [automatic-resolvers
     form/resolvers
     (blob/resolvers all-attributes)
     account/resolvers
     invoice/resolvers
     timezone/resolvers]))

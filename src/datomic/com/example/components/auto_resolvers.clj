(ns com.example.components.auto-resolvers
  (:require
    ;[com.example.model :refer [all-attributes all-attributes2]]
    [com.example.model :refer [all-attributes]]
    [mount.core :refer [defstate]]
    [com.fulcrologic.rad.resolvers :as res]
    [com.fulcrologic.rad.database-adapters.datomic-cloud :as datomic]
    [taoensso.timbre :as log]))

(defstate automatic-resolvers
  :start
  (vec
    (concat
      (res/generate-resolvers all-attributes)
      (datomic/generate-resolvers all-attributes :production))))
      ;(datomic/generate-resolvers all-attributes2 :production2))))

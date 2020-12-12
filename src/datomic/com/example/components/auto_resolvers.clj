(ns com.example.components.auto-resolvers
  (:require
    ;[com.example.model :refer [all-attributes all-attributes2]]
    [com.example.model :refer [all-attributes all-video-attributes]]
    [mount.core :refer [defstate]]
    [com.fulcrologic.rad.resolvers :as res]
    [com.fulcrologic.rad.database-adapters.datomic-cloud :as datomic]
    [com.fulcrologic.rad.attributes :as attr]
    [taoensso.timbre :as log]))

(defstate automatic-resolvers
  :start
  (vec
    (concat
      (res/generate-resolvers all-attributes)
      (datomic/generate-resolvers all-attributes :production)
      (datomic/generate-resolvers all-video-attributes :video))))
      ;(datomic/generate-resolvers all-attributes2 :production2))))


(comment

  (datomic/generate-resolvers all-attributes :production)
  (datomic/generate-resolvers all-video-attributes :video)
  (filter #(= schema (::attr/schema %)) all-video-attributes)

  ,)


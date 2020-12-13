(ns com.example.components.save-middleware
  (:require
    [com.fulcrologic.rad.middleware.save-middleware :as r.s.middleware]
    [com.fulcrologic.rad.database-adapters.datomic-cloud :as datomic]
    [com.example.components.datomic :refer [datomic-connections]]
    [com.fulcrologic.rad.blob :as blob]
    [com.example.model :as model]))

(def middleware
  (->
    (datomic/wrap-datomic-save
      (fn [env]
        ;(tap> env)
        (tap> "NEW: save/middleware: ")
        ;(tap> env)
        ;(tap> (:video datomic-connections))
        ;(clojure.pprint/pprint env)
        ; do we need to use the values in the env, instead of this
        ; global var?
        {:production (:main datomic-connections)
         :video (:video datomic-connections)}))
    (blob/wrap-persist-images model/all-attributes)
    (r.s.middleware/wrap-rewrite-values)))

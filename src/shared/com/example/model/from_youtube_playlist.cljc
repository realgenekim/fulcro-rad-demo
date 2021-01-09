(ns com.example.from-youtube-playlist
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.fulcrologic.rad.report-options :as ro]
    [com.wsscode.pathom.connect :as pc]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    ;#?(:clj  [com.fulcrologic.fulcro.dom-server :as dom]
    ;   :cljs [com.fulcrologic.fulcro.dom :as dom])
    ;#?(:clj [com.example.components.database-queries :as queries])
    [taoensso.timbre :as log]))

(defattr id :youtube-video/id :string
  {ao/identity? true
   ao/schema    :video})

(defattr description :youtube-video/description :string
  {ao/cardinality :one
   ao/identities #{:youtube-video/id}
   ;ao/style       :multi-line
   ao/schema      :video})
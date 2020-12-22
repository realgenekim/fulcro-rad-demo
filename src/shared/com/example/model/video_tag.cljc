(ns com.example.model.video-tag
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.fulcrologic.rad.report-options :as ro]
    [com.wsscode.pathom.connect :as pc]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    #?(:clj [com.example.components.database-queries :as queries])
    #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom]
       :cljs [com.fulcrologic.fulcro.dom :as dom])
    [taoensso.timbre :as log]))

; who cares about the attrs: no one except for you
; it's just a map: you use them in reports and forms, to tell the forms/reports what to display

(defattr id :video-tag/id :uuid
  {ao/identity? true
   ao/schema    :video})
   ;ro/column-formatter (fn [this v]
   ;                      (str v))})

(defattr tag-name :video-tag/name :string
  {ao/cardinality :one
   ao/identities #{:video-tag/id}
   ao/schema      :video})


(comment
  {:db/id 20248606138109519,
   :video-tag/id #uuid"13dffbc7-7f3f-49c5-919f-854369c700fe",
   :video-tag/name "Leadership"}
  ,)


(defattr all-tags :video-tag/all-tags :ref
  {ao/target     :video-tag/id
   ao/pc-output  [{:video-tag/all-tags [:video-tag/id :video-tag/name]}]
   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
                   (println "defattr all-tags2: " env)
                   #?(:clj
                      ;{:video-tag/all-videos [{:video-tag/id #uuid"13dffbc7-7f3f-49c5-919f-854369c700fe"
                      ;                         :video-tag/name "abc123"}]}
                      {:video-tag/all-tags
                       (queries/get-all-video-tags env query-params)}))})

(pc/defresolver video-tag-by-id [{:keys [db] :as env} {:video-tag/keys [id] :as input}]
  {::pc/input #{:video-tag/id}
   ::pc/output [:video-tag/id :video-tag/name]}
  ;(println "defresolver: input: " env input)
  (println "defresolver: video-tag-by-id: id: " id)
  (tap> "in defresolver: video-tag-by-id")
  #?(:clj
     ;{:session/uuid "abc"
     ; :session/venue "abc"}))
     ;{:youtube-video/all-videos [{:youtube-video/video-id "123" :youtube-video/id "123 "}]}))
     (do
       ;{:video-tag/id 123
       ; :video-tag/name "abc"}
       (queries/fetch-video-tag-by-uuid env id))))


; WARNING: make sure to add all model attributes here!
; Jakub:  I'm leery and worred that "name" var overwrites clojure.core/name

(def attributes [id tag-name all-tags])
;item-name category description price in-stock all-items])

#?(:clj
   (def resolvers [video-tag-by-id]))


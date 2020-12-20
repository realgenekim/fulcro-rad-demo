(ns com.example.model.session
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.fulcrologic.rad.report-options :as ro]
    [com.wsscode.pathom.connect :as pc]
    #?(:clj [com.example.components.database-queries :as queries])
    #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom]
       :cljs [com.fulcrologic.fulcro.dom :as dom])
    [taoensso.timbre :as log]))

; to enable Fulcro Inspector

(pc/defresolver index-explorer [env _]
  {::pc/input  #{:com.wsscode.pathom.viz.index-explorer/id}
   ::pc/output [:com.wsscode.pathom.viz.index-explorer/index]}
  {:com.wsscode.pathom.viz.index-explorer/index
   (get env ::pc/indexes)})

; who cares about the attrs: no one except for you
; it's just a map: you use them in reports and forms, to tell the forms/reports what to display

(defattr id :session/uuid :uuid
  {ao/identity? true
   ao/schema    :video})

(defattr conf-sched-id :session/conf-sched-id :string
  {ao/cardinality :one
   ao/identities #{:session/id}
   ao/schema      :video})

(defattr venue :session/venue :string
  {ao/cardinality :one
   ao/identities #{:session/uuid}
   ao/schema      :video})

(defattr title :session/title :string
  {ao/cardinality :one
   ao/identities #{:session/uuid}
   ao/schema      :video})

(defattr sched-id :session/sched-id :long
  {ao/cardinality :one
   ao/identities #{:session/uuid}
   ao/schema      :video})

(defattr start-time-utc :session/start-time-utc :instant
  {ao/cardinality :one
   ao/identities #{:session/uuid}
   ao/schema      :video})

(defattr stype :session/type :ref
  {ao/cardinality :one
   ao/identities #{:session/uuid}
   ao/schema      :video})

(defattr speakers :session/speakers :string
  {ao/cardinality :one
   ao/identities #{:session/uuid}
   ao/schema      :video})

(defattr tags :session/tags :ref
  {ao/target           :video-tag/id
   ao/cardinality      :many
   ao/identities       #{:session/uuid}
   ao/schema           :video
   ro/column-formatter (fn [this tags]
                         (println "session/tags: column-formatter: " tags)
                         (clojure.string/join ", "
                                              (for [t tags]
                                                (str (:video-tag/name t)))))})

(comment

  {:session/conf-sched-id "5348024557502565-119",
   :session/conf-id {:db/id 5348024557502565, :conference/name "Vegas-Virtual 2020"},
   :session/venue "Track 3",
   :session/title "#Culture Stole My OKR's! (Nationwide Building Society)",
   :session/sched-id 119,
   :session/tags [{:db/id 12947848929677702
                   :video-tag/id #uuid "febb25e2-f270-46e7-8355-03f552e84962"
                   :video-tag/name "Leadership"}
                  {:db/id 55111920831631254
                   :video-tag/id #uuid "5c2fed3a-a649-4b0f-ae02-bf2764c96df8"
                   :video-tag/name "Experience Report"}]
   :session/uuid #uuid"63827c18-5960-408f-8421-66d121a175b2",
   :session/start-time-utc #inst"2020-10-14T18:35:00.000-00:00",
   :session/type #:db{:id 15942918602752074, :ident :session-type/track},
   :db/id 2291382232285303,
   :session/speakers "Peter Lear; Kimberley Wilson"})


(defattr all-sessions :session/all-sessions :ref
  {ao/target    :session/uuid
   ao/pc-output  [{:session/all-sessions [:session/uuid]}]
   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
                   ;(println "defattr all-sessions: " env)
                   #?(:clj
                      {:session/all-sessions (queries/get-all-sessions env query-params)}))})


(pc/defresolver session-by-uuid [{:keys [db] :as env} {:session/keys [uuid] :as input}]
  {::pc/input #{:session/uuid}
   ::pc/output [:session/title
                :session/venue :session/start-time-utc :session/speakers :session/sched-id]}
  ;(println "defresolver: input: " env input)
  (println "defresolver: uuid: " uuid)
  #?(:clj
     ;{:session/uuid "abc"
     ; :session/venue "abc"}))
     (queries/get-session-from-uuid env uuid)))


; WARNING: make sure to add all model attributes here!

(def attributes [id title speakers start-time-utc all-sessions tags])
                 ;item-name category description price in-stock all-items])

#?(:clj
   (def resolvers [session-by-uuid]))


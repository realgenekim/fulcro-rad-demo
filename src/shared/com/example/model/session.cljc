(ns com.example.model.session
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.wsscode.pathom.connect :as pc]
    #?(:clj [com.example.components.database-queries :as queries])
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

(comment

  #:db{:id {:session/conf-sched-id "5348024557502565-30",
            :session/conf-id #:db{:id 5348024557502565},
            :session/venue "All Tracks",
            :session/title "Closing Remarks",
            :session/sched-id 30,
            :session/start-time-utc #inst"2020-10-15T00:15:00.000-00:00",
            :session/type #:db{:id 9649314045362249, :ident :session-type/plenary},
            :db/id 71521032363573428,
            :session/speakers "Gene Kim; Jeff Gallimore"}})


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

(def attributes [id title speakers start-time-utc all-sessions])
                 ;item-name category description price in-stock all-items])

#?(:clj
   (def resolvers [session-by-uuid]))


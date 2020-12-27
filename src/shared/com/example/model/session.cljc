(ns com.example.model.session
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.fulcrologic.rad.report-options :as ro]
    [com.wsscode.pathom.connect :as pc]
    #?(:clj [com.example.components.database-queries :as queries])
    #?(:clj [com.fulcrologic.rad.database-adapters.datomic-cloud :as datomic])
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
   ao/identities #{:session/uuid}
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
  {ao/cardinality      :one
   ao/identities       #{:session/uuid}
   ao/schema           :video
   ro/column-formatter (fn [_ value]
                         ;(println value)
                         (or (str (:db/ident value))
                             "-"))})


(defattr speakers :session/speakers :string
  {ao/cardinality :one
   ao/identities #{:session/uuid}
   ao/schema      :video})

;(defattr tags :session/tags :ref
;  {ao/target           :session-tag/id
;   ao/cardinality      :many
;   ao/identities       #{:session/uuid}
;   ao/schema           :video
;   ro/column-formatter (fn [this tags]
;                         ;(println "session/tags: column-formatter: " tags)
;                         (clojure.string/join ", "
;                                              (for [t tags]
;                                                (str (-> t
;                                                         :session-tag/tag-eid-2
;                                                         :video-tag/name)))))})

; session/tags: column-formatter:
; [{:session-tag-2/id #uuid "95ec4b65-a7e1-4a94-91d5-5a3196b0b388",
;   :session-tag-2/video-tag {:video-tag/id #uuid "5d81f6f7-a5a8-4196-aef6-4ba3ae125777",
;                             :video-tag/name Leadership}}
;  {:session-tag-2/id #uuid "be1f1687-4700-4143-9274-001d3cfd506e",
;   :session-tag-2/video-tag {:video-tag/id #uuid "1993c94e-5fe6-4aea-8eec-51c046a98b47",
;                             :video-tag/name Structure and Dynamics}}]

(defattr tags-2 :session/tags-2 :ref
  {ao/target            :session-tag-2/id
   ao/cardinality       :many
   ao/identities        #{:session/uuid}
   :com.fulcrologic.rad.database-adapters.datomic/entity-ids #{:session/uuid}
   ao/schema            :video
   ro/column-formatter  (fn [this tags]
                          (println "session/tags: column-formatter: " tags)
                          ;"---")})
                          (clojure.string/join ", "
                                               (for [t tags]
                                                 (str (-> t
                                                          :session-tag-2/video-tag
                                                          :video-tag/name)))))})

;(defattr tagrefs :sessino/tagrefs :ref
;  {ao/target})

(comment

  {:session/conf-sched-id "5348024557502565-119"
   :session/conf-id {:db/id 5348024557502565
                     :conference/name "Vegas-Virtual 2020"}
   :session/venue "Track 3"
   :session/title "#Culture Stole My OKR's! (Nationwide Building Society)"
   :session/tags-2 [{:session-tag-2/id #uuid "95ec4b65-a7e1-4a94-91d5-5a3196b0b388"
                     :session-tag-2/video-tag {:video-tag/id #uuid "5d81f6f7-a5a8-4196-aef6-4ba3ae125777"
                                               :video-tag/name "Leadership"}}
                    {:session-tag-2/id #uuid "be1f1687-4700-4143-9274-001d3cfd506e"
                     :session-tag-2/video-tag {:video-tag/id #uuid "1993c94e-5fe6-4aea-8eec-51c046a98b47"
                                               :video-tag/name "Structure and Dynamics"}}]
   :session/sched-id 119
   :session/tags [{:db/id 2713594698338703
                   :session-tag/id #uuid "a6d956fb-a6c7-4922-9e8b-2a05d000dc1a"
                   :session-tag/tag-id-2 #uuid "5d81f6f7-a5a8-4196-aef6-4ba3ae125777"
                   :session-tag/session-eid-2 {:db/id 2291382232285303
                                               :session/uuid #uuid "63827c18-5960-408f-8421-66d121a175b2"
                                               :session/title "#Culture Stole My OKR's! (Nationwide Building Society)"}
                   :session-tag/tag-eid-2 {:db/id 34199209671332235
                                           :video-tag/id #uuid "5d81f6f7-a5a8-4196-aef6-4ba3ae125777"
                                           :video-tag/name "Leadership"}}
                  {:db/id 42282819158741392
                   :session-tag/id #uuid "8f916b65-9346-4119-9dc7-8239f83f8597"
                   :session-tag/tag-id-2 #uuid "1993c94e-5fe6-4aea-8eec-51c046a98b47"
                   :session-tag/session-eid-2 {:db/id 2291382232285303
                                               :session/uuid #uuid "63827c18-5960-408f-8421-66d121a175b2"
                                               :session/title "#Culture Stole My OKR's! (Nationwide Building Society)"}
                   :session-tag/tag-eid-2 {:db/id 42282819158741389
                                           :video-tag/id #uuid "1993c94e-5fe6-4aea-8eec-51c046a98b47"
                                           :video-tag/name "Structure and Dynamics"}}
                  {:db/id 62848084644663699}]
   :session/uuid #uuid "63827c18-5960-408f-8421-66d121a175b2"
   :session/start-time-utc #inst "2020-10-14T18:35:00.000-00:00"
   :session/type {:db/id 15942918602752074
                  :db/ident :session-type/track}
   :db/id 2291382232285303
   :session/speakers "Peter Lear; Kimberley Wilson"})


(defattr all-sessions :session/all-sessions :ref
  {ao/target     :session/uuid
   ao/pc-output  [{:session/all-sessions [:session/uuid :session/title :session/venue :session/start-time-utc :session/speakers :session/sched-id
                                          :session/tags
                                          {:session/tags-2
                                           [:session-tag-2/id
                                            {:session-tag-2/video-tag
                                             [:video-tag/id
                                              :video-tag/name]}]}]}]
   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
                   ;(println "defattr all-sessions: " env)
                   #?(:clj
                      {:session/all-sessions (queries/get-all-sessions env query-params)}))})


;(pc/defresolver session-by-uuid [{:keys [db] :as env} {:session/keys [uuid] :as input}]
;  {::pc/input #{:session/uuid}
;   ::pc/output [:session/title
;                :session/venue :session/start-time-utc :session/speakers :session/sched-id
;                :session/tags {:session/tags-2
;                               {:session-tag-2/video-tag
;                                [:video-tag/id
;                                 :video-tag/name]}}]}
;  ;(println "defresolver: input: " env input)
;  (println "defresolver: session-by-uuid: " uuid)
;  #?(:clj
;     ;{:session/uuid "abc"
;     ; :session/venue "abc"}))
;     (queries/get-session-from-uuid env uuid)))


; WARNING: make sure to add all model attributes here!

(def attributes [id title speakers start-time-utc all-sessions
                  tags-2])
                 ;item-name category description price in-stock all-items])

#?(:clj
   (def resolvers []))


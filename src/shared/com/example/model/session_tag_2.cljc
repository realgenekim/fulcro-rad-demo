(ns com.example.model.session-tag-2
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.authorization :as auth]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.wsscode.pathom.connect :as pc]
    #?(:clj [com.example.components.database-queries :as queries])))

(def sample-session-tag
  {:session/conf-sched-id "5348024557502565-119",
   :session/conf-id {:db/id 5348024557502565, :conference/name "Vegas-Virtual 2020"},
   :session/venue "Track 3",
   :session/title "#Culture Stole My OKR's! (Nationwide Building Society)",
   :session/tags-2 [#:session-tag-2{:id #uuid"95ec4b65-a7e1-4a94-91d5-5a3196b0b388"}
                    #:session-tag-2{:id #uuid"be1f1687-4700-4143-9274-001d3cfd506e"}],
   :session/sched-id 119,
   :session/tags [{:db/id 2713594698338703,
                   :session-tag/tag-eid-2 {:db/id 34199209671332235,
                                           :video-tag/id #uuid"5d81f6f7-a5a8-4196-aef6-4ba3ae125777",
                                           :video-tag/name "Leadership"}}
                  {:db/id 42282819158741392,
                   :session-tag/tag-eid-2 {:db/id 42282819158741389,
                                           :video-tag/id #uuid"1993c94e-5fe6-4aea-8eec-51c046a98b47",
                                           :video-tag/name "Structure and Dynamics"}}
                  #:db{:id 62848084644663699}],
   :session/uuid #uuid"63827c18-5960-408f-8421-66d121a175b2",
   :session/start-time-utc #inst"2020-10-14T18:35:00.000-00:00",
   :session/type #:db{:id 15942918602752074, :ident :session-type/track},
   :db/id 2291382232285303,
   :session/speakers "Peter Lear; Kimberley Wilson"})


(defattr id :session-tag-2/id :uuid
  {ao/identity? true
   ao/schema    :video})

(defattr video-tag :session-tag-2/video-tag :ref
  {ao/target      :video-tag/id
   ao/cardinality :one
   ao/required?   true
   ao/identities  #{:session-tag-2/id}
   ao/schema      :video})


(defattr all-session-tags :session-tag-2/all-session-tags :ref
  {ao/target     :session-tag-2/id
   ; [{:video-tag/all-tags [:video-tag/id :video-tag/name]}]
   ao/pc-output  [{:session-tag-2/all-session-tags [:session-tag-2/id
                                                    ;:session-tag-2/video-tag]}]
                                                    {:session-tag-2/video-tag
                                                     [:db/id
                                                      :video-tag/id
                                                      :video-tag/name]}]}]
   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
                   (println ":session-tag-2/all-session-tags: here!" query-params)
                   (tap> query-params)
                   #?(:clj
                      {:session-tag-2/all-session-tags
                       ;[{:session-tag/id #uuid"63827c18-5960-408f-8421-66d121a175b2"
                       ;  :session-tag/session-id-2 #uuid"63827c18-5960-408f-8421-66d121a175b2"
                       ;  :session-tag/tag-id-2 #uuid"63827c18-5960-408f-8421-66d121a175b2"}]}))})
                       (queries/get-all-session-tags-2 env query-params)}))})


;(def alias-video-tag (pc/alias-resolver :session-tag-2/video-tag :video-tag/id))



(def attributes [id video-tag all-session-tags])

#?(:clj
   (def resolvers [])) ;alias-video-tag

; session-tag-by-id

; (defattr id :category/id :uuid
;  {ao/identity? true
;   ao/schema    :production})
;
;(defattr label :category/label :string
;  {ao/required?                                      true
;   ao/identities                                     #{:category/id}
;   :com.fulcrologic.rad.database-adapters.sql/max-length 120
;   ao/schema                                         :production})
;
;(defattr all-categories :category/all-categories :ref
;  {ao/target :category/id
;   ao/pc-output   [{:category/all-categories [:category/id]}]
;   ao/pc-resolve  (fn [{:keys [query-params] :as env} _]
;                    #?(:clj
;                       {:category/all-categories (queries/get-all-categories env query-params)}))})
;


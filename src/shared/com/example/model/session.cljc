(ns com.example.model.session
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.wsscode.pathom.connect :as pc]
    #?(:clj [com.example.components.database-queries :as queries])
    [taoensso.timbre :as log]))

(defattr id :db/id :long
  {ao/identity? true
   ao/schema    :production})

(defattr conf-sched-id :session/conf-sched-id :string
  {ao/cardinality :one
   ao/identities #{:db/id}
   ao/schema      :production})

;(defattr conf-id :session/conf-id :ref
;  {ao/cardinality :one
;   ao/identities #{:db/id}
;   ao/target :session-type
;   ao/schema      :production})

(defattr venue :session/venue :string
  {ao/cardinality :one
   ao/identities #{:db/id}
   ao/schema      :production})

(defattr title :session/title :string
  {ao/cardinality :one
   ao/identities #{:db/id}
   ao/schema      :production})

(defattr sched-id :session/sched-id :long
  {ao/cardinality :one
   ao/identities #{:db/id}
   ao/schema      :production})

(defattr start-time-utc :session/start-time-utc :instant
  {ao/cardinality :one
   ao/identities #{:db/id}
   ao/schema      :production})

(defattr stype :session/type :ref
  {ao/cardinality :one
   ao/identities #{:db/id}
   ao/schema      :production})

(defattr speakers :session/speakers :string
  {ao/cardinality :one
   ao/identities #{:db/id}
   ao/schema      :production})

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

;
;(defattr category :item/category :ref
;  {ao/target      :category/id
;   ao/cardinality :one
;   ao/identities  #{:item/id}
;   ao/schema      :production})
;
;(defattr item-name :item/name :string
;  {ao/identities #{:item/id}
;   ao/schema     :production})
;
;(defattr description :item/description :string
;  {ao/identities #{:item/id}
;   ao/schema     :production})
;
;(defattr price :item/price :decimal
;  {ao/identities #{:item/id}
;   ao/schema     :production})
;
;(defattr in-stock :item/in-stock :int
;  {ao/identities #{:item/id}
;   ao/schema     :production})

;(defattr all-sessions :session/all-sessions :ref
;  {ao/target    :session/id
;   ::pc/output  [{:session/all-sessions [:session/id]}]
;   ::pc/resolve (fn [{:keys [query-params] :as env} _]
;                  #?(:clj
;                     {:session/all-session (queries/get-all-sessions env (log/spy :info query-params))}))})

(defattr all-sessions :session/all-sessions :ref
  {ao/target     :session/id
   ao/pc-output  [{:session/all-sessions [:db/id]}]
   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
                   #?(:clj
                      {:session/all-sessions (queries/get-all-sessions env query-params)}))})

(defattr session-by-eid :session/id :ref
  {ao/target :session/id
   ao/pc-output [:session/title :session/venue :session/start-time-utc :session/speakers :session/sched-id]
   ;ao/pc-input :session/id
   ao/pc-resolve (fn [{:keys [db query-params] :as env} {:keys [db/id]}]
                   #?(:clj
                      (queries/get-session-from-eid db id)))})
                      ;{:account/all-accounts (queries/get-all-accounts env query-params)}))})



;(pc/defresolver session-by-eid [{:keys [db] :as env} input]
;  {::pc/input #{:db/id}
;   ::pc/output [:session/title
;                :session/venue :session/start-time-utc :session/speakers :session/sched-id]}
;  #?(:clj
;     (queries/get-session-from-eid db input)))


 ;(pc/defresolver session-by-speakers [{:keys [db] :as env} input]
 ;  {::pc/input #{:session/speakers}
 ;   ::pc/output [:session/id :session/title
 ;                :session/venue :session/start-time-utc :session/speakers :session/sched-id]}
 ;  (queries/get-session-from-speakers db input)))

; #?(:clj
;   (pc/defresolver item-category-resolver [{:keys [parser] :as env} {:item/keys [id]}]
;     {::pc/input  #{:item/id}
;      ::pc/output [:category/id :category/label]}
;     (let [result (parser env [{[:item/id id] [{:item/category [:category/id :category/label]}]}])]
;       (get-in (log/spy :info result) [[:item/id id] :item/category]))))

;(pc/defresolver session-by-eid [{:keys [db] :as env} {:keys [db/id]}]
;  {::pc/input #{:session/id}
;   ::pc/output [:session/id :session/full-name :club/id]}
;  (let [res (d/pull db [:person/id :person/full-name {:club/_manager [:club/id]}] id)]
;    (-> res
;        (assoc :club/id (get-in res [:club/_manager :club/id])))))

;(pc/defresolver session-by-eid [{:keys [db] :as env} {:keys [db/id]}]
;  {::pc/input #{:db/id}
;   ::pc/output [:session/title]}
;  (d/pull db [:session/title] id))

;(pc/defresolver person-by-eid [{:keys [db] :as env} {:keys [db/id]}]
;  {::pc/input #{:person/id}
;   ::pc/output [:person/id :person/full-name :club/id]}
;  (let [res (d/pull db [:person/id :person/full-name {:club/_manager [:club/id]}] id)]
;    (-> res
;        (assoc :club/id (get-in res [:club/_manager :club/id])))))

;#?(:clj
;   (pc/defresolver item-category-resolver [{:keys [parser] :as env} {:item/keys [id]}]
;     {::pc/input  #{:item/id}
;      ::pc/output [:category/id :category/label]}
;     (let [result (parser env [{[:item/id id] [{:item/category [:category/id :category/label]}]}])]
;       (get-in (log/spy :info result) [[:item/id id] :item/category]))))

(def attributes [id title all-sessions session-by-eid])
                 ;item-name category description price in-stock all-items])

;#?(:clj
;   (def resolvers [item-category-resolver]))


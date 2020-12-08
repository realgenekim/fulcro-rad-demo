(ns com.example.model.session
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.wsscode.pathom.connect :as pc]
    #?(:clj [com.example.components.database-queries :as queries])
    [taoensso.timbre :as log]))

(defattr id :session/id :long
  {ao/identity? true
   ao/schema    :production})

(defattr title :session/title :string
  {ao/cardinality :one
   ao/identities #{:db/id}
   ao/schema      :production})

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

(def attributes [id title all-sessions])
                 ;item-name category description price in-stock all-items])

;#?(:clj
;   (def resolvers [item-category-resolver]))


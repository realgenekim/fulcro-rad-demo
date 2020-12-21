(ns com.example.model.session-tag
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.authorization :as auth]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.wsscode.pathom.connect :as pc]
    #?(:clj [com.example.components.database-queries :as queries])))



(defattr id :session-tag/id :uuid
  {ao/identity? true
   ao/schema    :video})

(defattr session :session-tag/session-id-2 :uuid
  {ao/target      :session/uuid
   ao/cardinality :one
   ao/required?   true
   ao/identities  #{:session-tag/id}
   ao/schema     :video})

(defattr tag :session-tag/tag-id-2 :uuid
  {ao/target      :video-tag/id
   ao/cardinality :one
   ao/required?   true
   ao/identities  #{:session-tag/id}
   ao/schema     :video})

(defattr all-session-tags :session-tag/all-session-tags :ref
  {ao/target     :session-tag/id
   ao/pc-output  [{:session-tag/all-session-tags [:session-tag/id
                                                  :session-tag/session-id-2
                                                  :session-tag/tag-id-2]}]
   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
                   (println ":session-tag/all-session-tags: here!" query-params)
                   (tap> query-params)
                   #?(:clj
                      {:session-tag/all-session-tags
                       ;[{:session-tag/id #uuid"63827c18-5960-408f-8421-66d121a175b2"
                       ;  :session-tag/session-id-2 #uuid"63827c18-5960-408f-8421-66d121a175b2"
                       ;  :session-tag/tag-id-2 #uuid"63827c18-5960-408f-8421-66d121a175b2"}]}))})
                        (queries/get-all-session-tags env query-params)}))})

(def attributes [id session tag all-session-tags])

#?(:clj
   (def resolvers []))

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

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

(defattr session-id-2 :session-tag/session-id-2 :uuid
  {;ao/target      :session/uuid
   ao/cardinality :one
   ao/required?   true
   ao/identities  #{:session-tag/id}
   ao/schema     :video})

(defattr tag-id-2 :session-tag/tag-id-2 :uuid
  {
   ;ao/identity? true
   ;ao/target      :video-tag/id
   ao/cardinality :one
   ao/required?   true
   ao/identities  #{:session-tag/id}
   ao/schema     :video})

; computed in subform
(defattr tag-name :session-tag/tag-name :string
  {ao/read-only? true
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


; {::pc/input #{:video-tag/id}
; ::pc/output [:session-tag/tag-id-2]}

;(pc/defresolver session-tag-to-video-tag [{:keys [db] :as env} {:session-tag/keys [tag-id-2] :as input}]
;  {::pc/input #{:session-tag/tag-id-2}
;   ::pc/output [:video-tag/id]}
;  ;(println "defresolver: input: " env input)
;  (println "defresolver: session-tag-to-video-tag: tag-id-2: " tag-id-2)
;  (tap> ["defresolver: session-tag-to-video-tag: tag-id-2: " tag-id-2])
;  #?(:clj
;     {;:session-tag/tag-id-2 tag-id-2
;      :video-tag/id tag-id-2}))
     ;{:session/uuid "abc"
     ; :session/venue "abc"}))
     ;{:youtube-video/all-videos [{:youtube-video/video-id "123" :youtube-video/id "123 "}]}))

;(pc/defresolver session-tag-to-video-tag [env {:session-tag/keys [tag-id-2] :as input}]
;  {::pc/input #{:session-tag/tag-id-2}
;   ::pc/output [:video-tag/id]}
;  ;(println "defresolver: input: " env input)
;  (println "defresolver: session-tag-to-video-tag: tag-id-2: " tag-id-2)
;  (tap> ["defresolver: session-tag-to-video-tag: tag-id-2: " tag-id-2])
;  #?(:clj
;     {;:session-tag/tag-id-2 tag-id-2
;      :video-tag/id tag-id-2}))

(def alias-video-tag (pc/alias-resolver :session-tag/tag-id-2 :video-tag/id))

(def alias-session-tag (pc/alias-resolver :session-tag/session-id-2 :session/id))


;(pc/defresolver session-tag-by-id [{:keys [db] :as env} {:session-tag/keys [id] :as input}]
;  {::pc/input #{:session-tag/id}
;   ::pc/output [:session-tag/id :session-tag/tag-id-2 :session-tag/session-id-2]}
;  ;(println "defresolver: input: " env input)
;  (println "defresolver: session-tag-by-id: id: " id)
;  (tap> "in defresolver: session-tag-by-id")
;  #?(:clj
;     ;{:session/uuid "abc"
;     ; :session/venue "abc"}))
;     ;{:youtube-video/all-videos [{:youtube-video/video-id "123" :youtube-video/id "123 "}]}))
;     (do
;       ;{:session-tag/id #uuid "5d81f6f7-a5a8-4196-aef6-4ba3ae125777"
;       ; :session-tag/tag-id-2 #uuid "5d81f6f7-a5a8-4196-aef6-4ba3ae125777"})))
;       (queries/fetch-video-tag-by-uuid env id))))



(def attributes [id session-id-2 tag-id-2 all-session-tags])

#?(:clj
   (def resolvers [ alias-video-tag alias-session-tag]))

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

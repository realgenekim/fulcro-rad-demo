(ns com.example.model.mutations
  (:require
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.components :refer [defsc]]))


; POST to Vimeo (send server the Session entry)
; Upload video to Vimeo
; Download YouTube video

; GET Vimeo entry (send server Session entry)

; def stateful component

(defsc MyComponent
  [_ _]
  {:query [:youtube-video/id :returned-url]
   :ident :youtube-video/id})

(defmutation fetch-vimeo-entry
  [row]
  (action [{:keys [app state]}]
          (println "hello from mutation" row))
  ;(remote [env] true) ; see client/app definitioons for remotes
  (remote [env] (m/returning env MyComponent)) ; see client/app definitioons for remotes
  #_ (my-custom-remote [env] (do-whatever)))


;(m/defmutation fix-route
;  "Mutation. Called after auth startup. Looks at the session. If the user is not logged in, it triggers authentication"
;  [_]
;  (action [{:keys [app]}]
;          (let [logged-in (auth/verified-authorities app)]
;            (if (empty? logged-in)
;              (routing/route-to! app ui/LandingPage {})
;              (hist5/restore-route! app ui/LandingPage {})))))
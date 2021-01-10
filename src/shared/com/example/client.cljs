(ns com.example.client
  (:require
    [com.example.ui :as ui :refer [Root]]
    [com.example.ui.login-dialog :refer [LoginForm]]
    [com.example.ui.session-forms :refer [SessionListManual SessionList SessionListItem]]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp]
    [com.fulcrologic.fulcro.mutations :as m]
    [com.fulcrologic.rad.application :as rad-app]
    [com.fulcrologic.rad.report :as report]
    [com.fulcrologic.rad.authorization :as auth]
    [com.fulcrologic.rad.rendering.semantic-ui.semantic-ui-controls :as sui]
    [com.fulcrologic.fulcro.algorithms.timbre-support :refer [console-appender prefix-output-fn]]
    [taoensso.timbre :as log]
    [taoensso.tufte :as tufte :refer [profile]]
    [com.fulcrologic.rad.type-support.date-time :as datetime]
    [com.fulcrologic.fulcro.algorithms.tx-processing.synchronous-tx-processing :as stx]
    [com.fulcrologic.rad.routing.html5-history :as hist5 :refer [html5-history]]
    [com.fulcrologic.rad.routing.history :as history]
    [com.fulcrologic.rad.routing :as routing]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [com.fulcrologic.fulcro.data-fetch :as df]))

(defonce stats-accumulator
         (tufte/add-accumulating-handler! {:ns-pattern "*"}))

(m/defmutation fix-route
  "Mutation. Called after auth startup. Looks at the session. If the user is not logged in, it triggers authentication"
  [_]
  (action [{:keys [app]}]
          (let [logged-in (auth/verified-authorities app)]
            (if (empty? logged-in)
              (routing/route-to! app ui/LandingPage {})
              (hist5/restore-route! app ui/LandingPage {})))))

(defn setup-RAD [app]
  (rad-app/install-ui-controls! app sui/all-controls)
  (report/install-formatter! app :boolean :affirmation
                             (fn [_ value] (if value "yes" "no")))
  ; instead of here, we use ro/column-formatter in the model files
  ;(report/install-formatter! app :ref :youtube-playlist-id
  ;                           (fn [_ value] (or (:youtube-playlist/title value) "-")))
  ;(report/install-formatter! app :ref :video-tag/id
  ;                           (fn [_ value] (or (:video-tag/name value) "-")))

  ,)

(defonce app (rad-app/fulcro-rad-app
               {:client-did-mount
                (fn [app]
                  (df/load! app :session/all-sessions SessionListItem
                                    {:target [:component/id :session-list :session-list/sessions]}))}))

(comment
  (keys app)
  @(:com.fulcrologic.fulcro.application/state-atom app)
  (keys @(:com.fulcrologic.fulcro.application/state-atom app))
  ; => (:item/id
  ; :com.fulcrologic.fulcro.ui-state-machines/asm-id
  ; :youtube-video/id
  ; :router
  ; :account/id
  ; :com.fulcrologic.rad.report/id
  ; :fulcro.inspect.core/app-id
  ; :com.fulcrologic.rad.authorization/authorization
  ; :com.fulcrologic.rad.container/id
  ; :fulcro.inspect.core/app-uuid
  ; :ui.fulcro.client.data-fetch.load-markers/by-id
  ; :session/uuid
  ; :com.fulcrologic.fulcro.routing.dynamic-routing/id
  ; :com.fulcrologic.rad.control/id
  ; :ui/password
  ; :com.fulcrologic.rad.authorization/id
  ; :category/id
  ; :com.fulcrologic.rad.picker-options/options-cache
  ; :ui/username
  ; :component/id
  ; :com.fulcrologic.fulcro.components/queries
  ; :com.fulcrologic.fulcro.application/active-remotes
  ; :authenticator)
  (:component/id @(:com.fulcrologic.fulcro.application/state-atom app))
  (:com.fulcrologic.rad.report/id @(:com.fulcrologic.fulcro.application/state-atom app))
  (->> app)



  (->>

       :com.fulcrologic.fulcro.application/state-atom
       deref
       :from-youtube-playlist/id)

  ; => {nil #:com.fulcrologic.rad.pathom{:errors {:message "Mutation not found",
  ;                                           :data {:mutation com.example.model.mutations/save-youtube-playlist-to-database}}},
  ; "PLvk9Yh_MWYuxwfRj8I8Y5Eo0-V4KH4lal" #:from-youtube-playlist{:published-at "2020-09-10T21:04:44Z",
  ;                                                              :title "Shaaron Alvares: DevOps Enterprise Summit Las Vegas - Virtual 2020",
  ;                                                              :item-count 1,
  ;                                                              :description "",
  ;                                                              :id "PLvk9Yh_MWYuxwfRj8I8Y5Eo0-V4KH4lal"},
  ;

  (as-> app $
        (:com.fulcrologic.fulcro.application/state-atom $)
        (deref $)
        (:from-youtube-playlist/id $)
        (take 3 $)
        (rest $))

  (->> app
       (:com.fulcrologic.fulcro.application/state-atom)
       (deref)
       (:from-youtube-playlist/id)
       ;(take 3 $)
       (rest)
       (map second))

  ; :com.fulcrologic.rad.report/id :com.example.ui.from-youtube-playlist-forms/FromYouTube-PlaylistReport

  (->> app
       (:com.fulcrologic.fulcro.application/state-atom)
       (deref)
       :com.fulcrologic.rad.report/id
       :com.example.ui.from-youtube-playlist-forms/FromYouTube-PlaylistReport
       :ui/current-rows)
       ;(take 3 $)
       ;(rest)
       ;(map second))


  ,)


(defn refresh []
  ;; hot code reload of installed controls
  (log/info "Reinstalling controls")
  (setup-RAD app)
  (comp/refresh-dynamic-queries! app)
  (app/mount! app Root "app"))

(defn init []
  (log/merge-config! {:output-fn prefix-output-fn
                      :appenders {:console (console-appender)}})
  (log/info "Starting App")
  ;; default time zone (should be changed at login for given user)
  (datetime/set-timezone! "America/Los_Angeles")
  ;; Avoid startup async timing issues by pre-initializing things before mount
  (app/set-root! app Root {:initialize-state? true})
  (dr/initialize! app)
  (setup-RAD app)
  (dr/change-route! app ["landing-page"])
  (history/install-route-history! app (html5-history))
  (auth/start! app [LoginForm] {:after-session-check `fix-route})
  (app/mount! app Root "app" {:initialize-state? false}))

(comment)



(defonce performance-stats (tufte/add-accumulating-handler! {}))

(defn pperf
  "Dump the currently-collected performance stats"
  []
  (let [stats (not-empty @performance-stats)]
    (println (tufte/format-grouped-pstats stats))))
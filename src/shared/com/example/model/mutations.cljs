(ns com.example.model.mutations
  (:require
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]))


; POST to Vimeo (send server the Session entry)
; Upload video to Vimeo
; Download YouTube video

; GET Vimeo entry (send server Session entry)

; def stateful component

(defsc VimeoComponent
  [_ _]
  {:query [:youtube-video/id :returned-url]
   :ident :youtube-video/id})

(defmutation fetch-vimeo-entry
  [row]
  (action [{:keys [app state]}]
          (println "mutation: fetch-vimeo-entry: " row))
  ;(remote [env] true) ; see client/app definitioons for remotes
  (remote [env] (m/returning env VimeoComponent)) ; see client/app definitioons for remotes
  #_ (my-custom-remote [env] (do-whatever)))

;
; save-youtube-playlist-to-database
;

(defsc SaveYouTubePlaylistComponent
  [_ _]
  {:query [:youtube-playlist/id :returned-url]
   :ident :youtube-playlist/id})

(defmutation save-youtube-playlist-to-database
  [videos]
  (action [{:keys [app state]}]
          (println "mutation: save-youtube-playlist-to-database: " videos))
  ;(remote [env] true) ; see client/app definitioons for remotes
  (remote [env] (m/returning env VimeoComponent)) ; see client/app definitioons for remotes
  #_ (my-custom-remote [env] (do-whatever)))

;
; fetch-from-youtube-playlists
;


;(defsc FromYouTubePlaylist [_ _]
;  {:query [:ui.from-youtube/playlists]
;   :ident :ui.from-youtube/playlists})

; (defsc SessionListItem [this {:session/keys [uuid speakers venue] :as props}]
;  {:query [:session/uuid :session/speakers :session/venue]
;   :ident :session/uuid}
;
; (comp/get-query SessionListItem)
; => [:session/uuid :session/speakers :session/venue]

; (defsc SessionList2 [this {:session-list/keys [sessions]}]
;  {:query         [{:session-list/sessions (comp/get-query SessionListItem)}]
;   :ident         (fn [] [:component/id :session-list])
;   :initial-state {:session-list/sessions []}}

; (defsc SessionListItem [this {:session/keys [uuid speakers venue] :as props}]
;  {:query [:session/uuid :session/speakers :session/venue]
;   :ident :session/uuid}

;(pc/defmutation fetch-from-youtube-playlists
;                [env _]
;                {::pc/output [{:ui.from-youtube/playlists [:youtube-playlist/id]}]}
;                (println "fetch-from-youtube-playlists: ")
;                {:ui.from-youtube/playlists
;                 [{:id "xyz"
;                   :title "abc"}
;                  {:id "xxx"
;                   :title "def"}]})

(defsc YouTubePlaylistItem [this {:youtube-playlist/keys [id title] :as props}]
  {:query [:youtube-playlist/id
           :youtube-playlist/title
           :youtube-playlist/published-at
           :youtube-playlist/item-count
           :youtube-playlist/channel-id]
   :ident :youtube-playlist/id})

(defsc FromYouTube-PlaylistReturn [_ _]
  {:query [{:ui.from-youtube/playlists (comp/get-query YouTubePlaylistItem)}]
   :ident (fn [] [:component/id :ui.from-youtube/playlists])})

(defmutation fetch-from-youtube-playlists
  [params]
  (action [{:keys [app state]}]
          (println "fetch-from-youtube-playlists: " params))
  ;(remote [env] true) ; see client/app definitioons for remotes
  (remote [env] (m/returning env FromYouTube-PlaylistReturn)) ; see client/app definitioons for remotes
  #_ (my-custom-remote [env] (do-whatever)))



;(m/defmutation fix-route
;  "Mutation. Called after auth startup. Looks at the session. If the user is not logged in, it triggers authentication"
;  [_]
;  (action [{:keys [app]}]
;          (let [logged-in (auth/verified-authorities app)]
;            (if (empty? logged-in)
;              (routing/route-to! app ui/LandingPage {})
;              (hist5/restore-route! app ui/LandingPage {})))))
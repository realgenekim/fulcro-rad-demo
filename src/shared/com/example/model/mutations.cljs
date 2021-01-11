(ns com.example.model.mutations
  (:require
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [clojure.pprint :as pp]))


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
  (remote [env] (m/returning env VimeoComponent))
          ;(let [retval (m/returning env VimeoComponent)]
          ;  (println "remote: retval: " retval)))              ; see client/app definitioons for remotes
  #_ (my-custom-remote [env] (do-whatever)))

;
; save-youtube-playlist-to-database
;

(defsc SaveYouTubePlaylistComponent
  [_ _]
  {:query [:from-youtube/videos :returned-url]
   :ident :from-youtube/videos})

;(myparse ['({:youtube-video/by-playlist [:youtube-video/id :youtube-video/title]}
;            {:youtube-playlist/id "PLvk9Yh_MWYuwXC0iU5EAB1ryI62YpPHR9"})])

(defmutation save-youtube-playlist-to-database
  [params]
  (action [{:keys [app state]}]
          (println "mutation: save-youtube-playlist-to-database: params:\n"
                   (with-out-str (pp/pprint params))))
  ;(remote [env] true) ; see client/app definitions for remotes
  (remote [env] (m/returning env SaveYouTubePlaylistComponent))
          ;(let [retval (m/returning env SaveYouTubePlaylistComponent)]
          ;  (println "mutation: retval: " retval)
          ;  retval)) ; see client/app definitioons for remotes
  #_ (my-custom-remote [env] (do-whatever)))

;
; fetch-from-youtube-playlists
;

(comment

  ,)

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
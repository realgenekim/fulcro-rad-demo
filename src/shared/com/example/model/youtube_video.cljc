(ns com.example.model.youtube-video
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.fulcrologic.rad.report-options :as ro]
    [com.wsscode.pathom.connect :as pc]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom]
       :cljs [com.fulcrologic.fulcro.dom :as dom])
    #?(:clj [com.example.components.database-queries :as queries])
    [taoensso.timbre :as log]))


; who cares about the attrs: no one except for you
; it's just a map: you use them in reports and forms, to tell the forms/reports what to display

(defattr id :youtube-video/id :string
  {ao/identity? true
   ao/schema    :video})

(defattr description :youtube-video/description :string
  {ao/cardinality :one
   ao/identities #{:youtube-video/id}
   ao/style       :multi-line
   ao/schema      :video})

;(defattr playlist-id :youtube-video/playlist-id :ref
;  {ao/cardinality :one
;   ao/identities #{:youtube-video/id}
;   ao/schema      :video})

(defattr playlist-id :youtube-video/playlist-id :ref
  {ao/target      :youtube-playlist/id
   ao/cardinality :one
   ao/style       :youtube-playlist-id
   ao/schema      :video
   ro/column-formatter (fn [_ value]
                         (or (:youtube-playlist/title value) "-"))})

   ;ao/pc-resolve  (fn [env {:youtube-video/keys [id] :as input}]
   ;                 ;(println "defattr3: id playlist-id: " id)
   ;                 (tap> (str "defattr6: id playlist-id: " id))
   ;                 (tap> input)
   ;                 ;{:youtube-video/playlist-id [{:youtube-playlist/title "abc"}]}
   ;                 #?(:clj
   ;                    (when-let [cid (queries/get-video-playlist env id)]
   ;                      (tap> cid)
   ;                      {:youtube-video/playlist-id [{:youtube-playlist/title cid}]}))),})

; (defattr playlist :line-item/category :ref
;  {ao/target      :category/id
;   ao/pc-input    #{:line-item/id}
;   ao/pc-output   [{:line-item/category [:category/id]}]
;   ao/pc-resolve  (fn [env {:line-item/keys [id]}]
;                    #?(:clj
;                       (when-let [cid (queries/get-line-item-category env id)]
;                         {:line-item/category {:category/id cid}})))
;   ao/cardinality :one})

(defattr title :youtube-video/title :string
  {ao/cardinality :one
   ao/identities #{:youtube-video/id}
   ao/schema      :video})

(defattr url :youtube-video/url :string
  {ao/cardinality :one
   ao/identities #{:youtube-video/id}
   ao/style       :url
   ao/schema      :video
   ro/column-formatter (fn [this v] (dom/a
                                      {:href v :target "_blank"}
                                      (str v)))})


; (fn [this v] (dom/a {:onClick #(form/edit! this AccountForm (-> this comp/props :account/id)} (str v)))}

(defattr video-id :youtube-video/video-id :string
  {ao/cardinality :one
   ao/identities #{:youtube-video/id}
   ao/schema      :video})

(defattr position :youtube-video/position :long
  {ao/cardinality :one
   ao/identities #{:youtube-video/id}
   ao/schema      :video})


(comment

  {:db/id 28802806602203956,
   :youtube-video/description "DOES19 Las Vegas — This talk will describe the \"why\" and the \"way\" to 100% Agile @ BMW Group IT - a holistic approach with 4 focus areas: Process, Structure, Technology and People&Culture. Ralf will give a deep dive into the transformation from \"Projects\" to \"Products\" defined last year.

                              Our Journey to 100% Agile and a BizDevOps Product Portfolio - Dr. Frank Ramsak and Ralf Waltram

                              Dr. Frank Ramsak, IT Governance, BMW Group
                              Ralf Waltram, Head of IT Systems Research & Development, BMW Group

                              Frank Ramsak started his career within the BMW Group in 2003 and is presently responsible for Architecture, Innovation and Technology in the BMW Group IT Governance. With his team and the community of architects, he defines and drives the innovative, competitive IT solution space for the feature teams.\r

                              Before his time in the IT-Governance, he managed international IT projects and was responsible for enterprise architecture management for the R&D, quality and production areas.

                              Frank st\rudied computer science at the Technical University of Munich (TUM) and the University of Illinois at \r
                              Urbana-Champaign. He received his PhD from TUM for his work on multi-dimensional indexing in database systems.

                              Ralf Waltram has been with the BMW Group since 1996 and is responsible for IT systems in vehicle development since 2015. He and his team focus on the possibilities of digitalization in the R&D process, with an agile collaboration model and a focus on a BizDevOps structure. Prior to this, he managed international IT projects, e.g. in China, in the area of R&D, sales and marketing and was responsible in different line functions. Ralf Waltram studied computer science at the Munich University of Applied Sciences, specializing in computer vision and neural networks.

                              DOES19 Las Vegas
                              DOES 2019 US
                              DevOps Enterprise Summit
                              https://events.itrevolution.com/us/",
   :youtube-video/id "UEx2azlZaF9NV1l1d1hDMGlVNUVBQjFyeUk2MllwUEhSOS44Mjc5REFBRUE2MTdFRDU0",
   :youtube-video/playlist-id #:db{:id 47476912088351523},
   :youtube-video/title "Our Journey to 100% Agile and a BizDevOps Product Portfolio - BMW",
   :youtube-video/url "https://www.youtube.com/watch?v=f50e5YGuFG4",
   :youtube-video/video-id "f50e5YGuFG4",
   :youtube-video/position 16}

  ,)


(defattr all-videos :youtube-video/all-videos :ref
  {ao/target     :youtube-video/id
   ao/pc-output  [{:youtube-video/all-videos [:youtube-video/id]}]
   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
                   (println "defattr all-videos: " env)
                   #?(:clj
                      ;{:youtube-video/all-videos [{:youtube-video/video-id "123" :youtube-video/id "123 "}]}))})
                      {:youtube-video/all-videos (queries/get-all-youtube-videos env query-params)}))})

; (defattr account-invoices :account/invoices :ref
;  {ao/target     :account/id
;   ao/pc-output  [{:account/invoices [:invoice/id]}]
;   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
;                   #?(:clj
;                      {:account/invoices (queries/get-customer-invoices env query-params)}))})

;(defattr all-videos-by-playlist :youtube-video/by-playlist :ref
;  {ao/target     :youtube-video/id
;   ao/pc-output  [{:youtube-video/by-playlist [:youtube-video/id]}]
;   ao/pc-resolve (fn [{:keys [query-params] :as env} _]
;                   (println "defattr all-videos-by-playlist: " query-params)
;                   #?(:clj
;                      {:youtube-video/by-playlist [{:youtube-video/id "123 "}]}))})
;                      ;{:youtube-video/all-videos (queries/get-all-youtube-videos env query-params)}))})

; TODO: delete this?

;(pc/defresolver youtube-video-by-id [{:keys [db] :as env} {:youtube-video/keys [id] :as input}]
;  {::pc/input #{:youtube-video/id}
;   ::pc/output [:youtube-video/description :youtube-video/playlist-id :youtube-video/title
;                :youtube-video/position :youtube-video/url :youtube-video/video-id]}
;  ;(println "defresolver: input: " env input)
;  (println "defresolver: youtube-video-by-id: id: " id)
;  #?(:clj
;     ;{:session/uuid "abc"
;     ; :session/venue "abc"}))
;     ;{:youtube-video/all-videos [{:youtube-video/video-id "123" :youtube-video/id "123 "}]}))
;     (queries/youtube-video-by-id env id)))

;(defattr youtube-playlists2 :conference/youtube-playlists2 :ref
;  {ao/target      :youtube-playlist/id
;   ao/cardinality :many
;   ao/identities #{:conference/uuid}
;   ao/schema      :video
;
;   ao/pc-output   [{:conference/youtube-playlists2 [:youtube-playlist/id]}]
;   ao/pc-resolve  (fn [{:keys [query-params] :as env} _]
;                    ;(println "defattr conference/youtube-playlists: " env)
;                    (println "defattr conference/youtube-playlists2: " query-params)
;                    #?(:clj
;                       {:conference/youtube-playlists2 (queries/get-conference-playlists env query-params)}))})

(defattr youtube-playlists2 :conference/youtube-playlists2 :ref
  {ao/target      :youtube-playlist/id
   ao/cardinality :many
   ao/identities #{:conference/uuid}
   ao/schema      :video

   ao/pc-output   [{:conference/youtube-playlists2 [:youtube-playlist/id]}]
   ao/pc-resolve  (fn [{:keys [query-params] :as env} _]
                    ;(println "defattr conference/youtube-playlists: " env)
                    (println "defattr conference/youtube-playlists2: " query-params)
                    #?(:clj
                       {:conference/youtube-playlists2 (queries/get-conference-playlists env query-params)}))})

;
; (myparse ['({:youtube-video/by-playlist [:youtube-video/id :youtube-video/title]}
;              {:youtube-playlist/id "PLvk9Yh_MWYuwXC0iU5EAB1ryI62YpPHR9"})])

(defattr youtube-video-by-id :youtube-video/videos-list :ref
  {ao/target      :youtube-video/id
   ao/cardinality :many
   ao/identities  #{:youtube-playlist/id}  ; <-- input?
   ao/schema      :video
   ; {:youtube-video/by-playlist (:youtube-playlist/id row)}
   :com.fulcrologic.rad.control/controls {:youtube-playlist/id {:type   :string
                                                                :local? true
                                                                :label  "Playlist ID"}}
   ao/pc-output   [{:youtube-video/by-playlist [:youtube-video/id]}]
   ao/pc-resolve  (fn [{:keys [query-params] :as env} _]
                    (println "defresolver: youtube-video-by-playlist-id: playlist-id: " query-params)
                    #?(:clj
                       (let [r (queries/youtube-video-by-playlist-id env query-params)]
                         ;(println "retval: " r)
                         {:youtube-video/by-playlist r})))})


;(pc/defresolver youtube-video-by-playlist-id [{:keys [db] :as env} {:youtube-video/keys [playlist-id] :as input}]
;  {
;   ;::pc/input #{:youtube-video/playlist-id}
;   ::pc/output [{:youtube-video/by-playlist [:youtube-video/id]}]}
;              ;[:youtube-video/description :youtube-video/playlist-id :youtube-video/title
;              ; :youtube-video/position :youtube-video/url :youtube-video/video-id]}
;  ;(println "defresolver: input: " env input)
;  (println "defresolver: youtube-video-by-playlist-id: playlist-id: " playlist-id)
;  #?(:clj
;     ;{:session/uuid "abc"
;     ; :session/venue "abc"}))
;     ;{:youtube-video/by-playlist [{:youtube-video/id "123 "}]}))
;     (fn [{:keys [query-params] :as env} _]
;       (let [r (queries/youtube-video-by-playlist-id env query-params)]
;         ;(println "retval: " r)
;         {:youtube-video/by-playlist r}))))



; WARNING: make sure to add all model attributes here!

(def attributes [id description title playlist-id position url video-id all-videos
                 youtube-video-by-id])
;item-name category description price in-stock all-items]) all-videos-by-playlist

#?(:clj
   (def resolvers [youtube-video-by-id]))


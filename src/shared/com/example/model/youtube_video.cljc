(ns com.example.model.youtube-video
  (:require
    [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
    [com.fulcrologic.rad.attributes-options :as ao]
    [com.wsscode.pathom.connect :as pc]
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
   ao/schema      :video})

(defattr playlist-id :youtube-video/playlist-id :ref
  {ao/cardinality :one
   ao/identities #{:youtube-video/id}
   ao/schema      :video})

(defattr title :youtube-video/title :string
  {ao/cardinality :one
   ao/identities #{:youtube-video/id}
   ao/schema      :video})

(defattr url :youtube-video/url :string
  {ao/cardinality :one
   ao/identities #{:youtube-video/id}
   ao/schema      :video})

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



;(pc/defresolver youtube-video-by-id [{:keys [db] :as env} {:session/keys [id] :as input}]
;  {::pc/input #{:youtube-video/id}
;   ::pc/output [:youtube-video/description :youtube-video/playlist-id :youtube-video/title
;                :youtube-video/position :youtube-video/url :youtube-video/video-id]}
;  ;(println "defresolver: input: " env input)
;  (println "defresolver: youtube-video-by-id: id: " id)
;  #?(:clj
;     ;{:session/uuid "abc"
;     ; :session/venue "abc"}))
;     (queries/youtube-video-by-id env id)))


; WARNING: make sure to add all model attributes here!

(def attributes [id description title playlist-id position url video-id all-videos])
;item-name category description price in-stock all-items])

#?(:clj
   (def resolvers []));youtube-video-by-id]))


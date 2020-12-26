(ns com.example.model.gene-test
  (:require
    [clojure.test :refer :all]
    [datomic.client.api :as d]
    [mount.core :as mount]
    [com.example.components.datomic :refer [datomic-connections]]
    [clojure.pprint :as pp]
    [com.example.components.parser :as parser]
    [com.example.components.config :as config]))

(def myparse
  (partial com.example.components.parser/parser com.example.components.config/config))


(println "hello!")
(println (mount/find-all-states))

(defn start []
  (mount/start-with-args {:config "config/test.edn"})
  ;(seed!)
  :ok)

(defn stop
  "Stop the server."
  []
  (mount/stop))




;(println
;  ; this doesn't work -- no server running
;  (let [db (d/db (:video datomic-connections))]
;    (d/q '[:find (pull ?e [*])
;           :where
;           [?e :session/title _]] db 70368744177664139)))

(deftest reset-tests
  (do
    (println "*** stop")
    (stop)
    (println "*** start")
    (start))
  (is (= 1 1)))

(deftest a-test
  (testing "FIXME, I fail."
    (println "hello!")
    (is (= 1 1))))

(deftest schema
  (testing "get all tags"
    (let [r (myparse [:video-tag/all-tags])]
      (is (= #:video-tag{:all-tags
                         [{:db/id 17935233673269646, :video-tag/id #uuid "75008b25-975a-45b6-bf68-cb96e0d1fa7a", :video-tag/name "Continuous Integration and Delivery (CI/CD)"}
                          {:db/id 25970464649056652, :video-tag/id #uuid "e519e1a5-189f-4803-83fa-80aba88ec6d0", :video-tag/name "Business Leadership"}
                          {:db/id 34199209671332235, :video-tag/id #uuid "5d81f6f7-a5a8-4196-aef6-4ba3ae125777", :video-tag/name "Leadership"}
                          {:db/id 42282819158741389, :video-tag/id #uuid "1993c94e-5fe6-4aea-8eec-51c046a98b47", :video-tag/name "Structure and Dynamics"}]}
             r))))

  (testing "get all sessions"
    (let [r (myparse [:session/all-sessions])]
      (is (= 107
             (-> r
                 :session/all-sessions
                 count)))
      (is (= "Dr. J. Goosby Smith"
             (-> r
                 :session/all-sessions
                 first
                 :session/speakers)))))

  (def leartalk-uuid #uuid"63827c18-5960-408f-8421-66d121a175b2")
  (def leadership-uuid #uuid"5d81f6f7-a5a8-4196-aef6-4ba3ae125777")

  (testing "lookup lear talk tag by uuid; return two session-tags"
    (let [r (myparse [{[:session/uuid leartalk-uuid]
                       [:session/tags-2]}])]
      (is (= {[:session/uuid #uuid "63827c18-5960-408f-8421-66d121a175b2"]
              {:session/tags-2 [{:session-tag-2/id #uuid "95ec4b65-a7e1-4a94-91d5-5a3196b0b388"}
                                {:session-tag-2/id #uuid "be1f1687-4700-4143-9274-001d3cfd506e"}]}}
             r))
      ; should return two session-tag/ids
      (is (= [[:session-tag-2/id #uuid "95ec4b65-a7e1-4a94-91d5-5a3196b0b388"]
              [:session-tag-2/id #uuid "be1f1687-4700-4143-9274-001d3cfd506e"]]
             (-> r
                 (get [:session/uuid #uuid"63827c18-5960-408f-8421-66d121a175b2"])
                 :session/tags-2
                 (#(map (fn [x] (-> x
                                    first)) %)))))))

  (testing "get lear talk by uuid: get video-tag/name "
    (let [r (myparse [{[:session/uuid leartalk-uuid]
                       [:session/speakers :session/tags-2]}])]

      (is (= {[:session/uuid #uuid "63827c18-5960-408f-8421-66d121a175b2"]
              {:session/speakers "Peter Lear; Kimberley Wilson"
               :session/tags-2 [{:session-tag-2/id #uuid "95ec4b65-a7e1-4a94-91d5-5a3196b0b388"}
                                {:session-tag-2/id #uuid "be1f1687-4700-4143-9274-001d3cfd506e"}]}}
             r))

      (pp/pprint r)
      ; should return speaker
      (is (= "Peter Lear; Kimberley Wilson"
             (-> r
                 (get [:session/uuid #uuid"63827c18-5960-408f-8421-66d121a175b2"])
                 :session/speakers)))
      ; should return two session-tag/ids
      (is (= [[:session-tag-2/id #uuid "95ec4b65-a7e1-4a94-91d5-5a3196b0b388"]
              [:session-tag-2/id #uuid "be1f1687-4700-4143-9274-001d3cfd506e"]]
             (-> r
                 (get [:session/uuid #uuid"63827c18-5960-408f-8421-66d121a175b2"])
                 :session/tags-2
                 (#(map (fn [x] (-> x
                                    first)) %)))))))

  (testing "get lear by uuid: get all the video-tags"
    (let [r (myparse [{[:session/uuid #uuid "63827c18-5960-408f-8421-66d121a175b2"]
                       [:session/speakers
                        {:session/tags-2
                         [:db/id
                          :session-tag-2/id
                          {:session-tag-2/video-tag
                           [:db/id
                            :video-tag/id
                            :video-tag/name]}]}]}])]
      (pp/pprint r)
      (is (= {[:session/uuid #uuid "63827c18-5960-408f-8421-66d121a175b2"]
              {:session/speakers "Peter Lear; Kimberley Wilson"
               :session/tags-2 [{:session-tag-2/id #uuid "95ec4b65-a7e1-4a94-91d5-5a3196b0b388"
                                 :session-tag-2/video-tag {:video-tag/id #uuid "5d81f6f7-a5a8-4196-aef6-4ba3ae125777"
                                                           :video-tag/name "Leadership"}}
                                {:session-tag-2/id #uuid "be1f1687-4700-4143-9274-001d3cfd506e"
                                 :session-tag-2/video-tag {:video-tag/id #uuid "1993c94e-5fe6-4aea-8eec-51c046a98b47"
                                                           :video-tag/name "Structure and Dynamics"}}]}}
             r))))







  ,)


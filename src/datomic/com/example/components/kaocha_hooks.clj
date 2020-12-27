(ns com.example.components.kaocha-hooks
  (:require
    [clojure.tools.namespace.repl :as tools-ns :refer [disable-reload! refresh clear set-refresh-dirs]]
    [mount.core :as mount]))

; https://cljdoc.org/d/lambdaisland/kaocha/0.0-549/doc/plugin-hooks

(defn start []
  (mount/start-with-args {:config "config/test.edn"})
  ;(seed!)
  :ok)

(defn stop
  "Stop the server."
  []
  (mount/stop))

(defn restart
  "Stop, refresh, and restart the server."
  []
  (stop)
  (tools-ns/refresh :after 'development/start))


(defn mount-hook
  [test]
  (println "***")
  (println "*** kaocha: mount-hook: ")
  (println "***")
  (start)
  (println (mount/find-all-states))
  ;(stop)
  (start)
  ;(restart)
  test)
  ;(if (re-find #"fail" (str (:kaocha.testable/id test)))
  ;  (assoc test :kaocha.testable/pending true)
  ;  test))

(defn unmount-hook
  [test]
  (println "***")
  (println "*** kaocha: post-run:")
  (println "***")
  ;(restart)
  test)
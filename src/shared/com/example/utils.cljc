(ns com.example.utils)

(defn map->nsmap
  " make all keys namespaced to namespace n "
  [m n]
  (reduce-kv (fn [acc k v]
               (let [new-kw (if (and (keyword? k)
                                     (not (qualified-keyword? k)))
                              (keyword (str n) (name k))
                              k)]
                 (assoc acc new-kw v)))
             {} m))

(defn nsmap->map
  " strip off namespaces from keys in map "
  [m]
  (reduce-kv (fn [acc k v]
               (let [new-kw (keyword (name k))]
                 (assoc acc new-kw v)))
             {} m))
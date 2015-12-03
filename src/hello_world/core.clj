(ns hello-world.core
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(s/defschema Total {:total Long})

(defn operation [name]
 (cond 
  (= "sum" name) {:name name :operation +}
  (= "sub" name) {:name name :operation -}
  (= "pro" name) {:name name :operation *}
  :else {:name name :operation str}))


(defn range-including [lower-bound higher-bound]
	(range lower-bound (inc higher-bound)))

(defapi app
  (context* "/math" []
    :tags ["math"]

    (GET* "/plus/:y" []
      :query-params [x :- [Long]]
      :path-params [y :- Long]
      :summary "x+y with query-parameters"
      (ok {:total (apply + x) :y y :params x}))

    (GET* "/range/:start/:end" []
      :path-params [start :- Long end :- Long]
      :query-params [op :- [String]]
      :summary "x+y with query-parameters"
      (let [operands (range-including start end)
            operators (map operation op)
            results (map #(-> {(:name %) (apply (:operation %) operands)}) operators)]
      (ok {:total operands :results results :op op})))
))


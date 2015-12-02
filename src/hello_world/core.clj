(ns hello-world.core
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(s/defschema Total {:total Long})

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
      :query-params [op]
      :summary "x+y with query-parameters"
      (let [operands (range start (inc end))
            operators (map (fn [op] (cond (= "sum" op) + (= "sub" op) -)) op)
            results (map #(apply % operands) operators)]
      (ok {:total operands :op results})))
))


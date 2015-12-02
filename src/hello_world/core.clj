(ns hello-world.core
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(s/defschema Total {:total Long})

(defapi app
  (context* "/math" []
    :tags ["math"]

    (GET* "/plus" []
      :return Total
      :query-params [x :- Long, y :- Long]
      :summary "x+y with query-parameters"
      (ok {:total (+ x y)}))))
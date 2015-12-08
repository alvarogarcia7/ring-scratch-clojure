(ns hello-world.core-test
  (:require [compojure.api.sweet :refer :all]
                [cheshire.core :as cheshire]
                [midje.sweet :refer :all]
                [peridot.core :as p]
                [hello-world.core :as hw]
)
  (:import [java.io InputStream]))

(defn json [x] (cheshire/generate-string x))

(defn read-body [body]
  (if (instance? InputStream body)
    (slurp body)
    body))

(defn follow-redirect [state]
  (if (some-> state :response :headers (get "Location"))
    (p/follow-redirect state)
    state))

(defn raw-get* [app uri & [params headers]]
  (let [{{:keys [status body headers]} :response}
        (-> (p/session app)
            (p/request uri
                       :request-method :get
                       :params (or params {})
                       :headers (or headers {}))
            follow-redirect)]
    [status (read-body body) headers]))

(defn parse-body [body]
  (let [body (read-body body)
        body (if (instance? String body)
               (cheshire/parse-string body true)
               body)]
    body))

(defn get* [app uri & [params headers]]
  (let [[status body headers]
        (raw-get* app uri params headers)]
    [status (parse-body body) headers]))


(facts "math context"
(fact "generates ranges"
  (let [app hw/app]
    (fact "with the sum operation"
      (let [[status body] (get* app "/math/range/1/3?op=sum")]
        status => 200
        body =>{:total [1,2,3] :results [{:sum 6}], :op ["sum"]}
        (:results body) =>[{:sum 6}])

      (let [[status body] (get* app "/math/range/1/3?op=unknown")]
        status => 200
        (:results body) =>[{:unknown "123"}]
        (:op body) => ["unknown"])

      ))))

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
(fact "generates ranges between 1 and 3"
  (let [app hw/app
         range-1-3 "/math/range/1/3"
         request (fn [operation-desc] (get* app range-1-3 {:op operation-desc}))]
    (fact "with the sum operation"

      (defn op-matches-description? [body operation-desc]
        (:op body) => [operation-desc])

      (defn ok? [status]
        status => 200)

      (defn matches-range? [body]
        (:total body) => [1,2,3])

      (let [operation :sum
             operation-desc (name operation)]
        (let [[status body] (request operation-desc)]
              (ok? status)
              (op-matches-description? body operation-desc)
              (matches-range? body)
              (:results body) =>[{operation 6}])))

    (fact "with an unknown operation"
      (let [operation :unknown
             operation-desc (name operation)]
        (let [[status body] (request operation-desc)]
          (ok? status)
          (matches-range? body)
          (op-matches-description? body operation-desc)
          (:results body) =>[{operation "123"}])))

    (fact "with the subtraction operation"
      (let [operation :sub
               operation-desc (name operation)]
          (let [[status body] (request operation-desc)]
                (ok? status)
                (op-matches-description? body operation-desc)
                (matches-range? body)
                (:results body) => [{operation -4}])))

      )))

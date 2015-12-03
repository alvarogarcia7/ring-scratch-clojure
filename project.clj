(defproject ring-scratch "0.0.1-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                           [metosin/compojure-api "0.23.0"]]
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler hello-world.core/app}
  :profiles {:uberjar { :resource-paths ["swagger-ui"]
                                :aot :all}
             :dev {
                    :dependencies [[javax.servlet/servlet-api "2.5"]]
                    :plugins [[lein-ring "0.9.6"]]}})
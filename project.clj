(defproject eureka-client "0.4.0-SNAPSHOT"
  :description "A client for Netflix Eureka service discovery servers"
  :url "http://github.com/codebrickie/eureka-client"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "3.1.0"]
                 [cheshire "5.6.3"]
                 [org.clojure/core.cache "0.6.5"]]
  :profiles {:dev {:dependencies [[midje "1.8.3"]]
                   :plugins [[lein-midje "3.2"]
                             [codox "0.9.6"]
                             [lein-ancient "0.6.10"]]}})

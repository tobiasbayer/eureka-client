(defproject eureka-client "0.3.0-SNAPSHOT"
  :description "A client for Netflix Eureka service discovery servers"
  :url "http://github.com/codebrickie/eureka-client"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-http "1.0.1" :exclusions [com.fasterxml.jackson.core/jackson-core]]
                 [org.clojure/core.cache "0.6.4"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]
                             [codox "0.8.10"]]}})

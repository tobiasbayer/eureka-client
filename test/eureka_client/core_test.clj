(ns eureka-client.core-test
  (:require [midje.sweet :refer :all]
            [eureka-client.core :refer :all]
            [clj-http.client :as http]))


(fact "it registers the app"
  (register "localhost" 8761 "myId" 1234) => anything
    (provided
      (http/post
        "http://localhost:8761/eureka/apps/myId"
        (contains {:form-params {:instance {:hostName (.getHostAddress (java.net.InetAddress/getLocalHost))
                                  :app "myId"
                                  :ipAddr (.getHostAddress (java.net.InetAddress/getLocalHost))
                                  :vipAddress "myId"
                                  :status "UP"
                                  :port 1234
                                  :securePort 443
                                  :dataCenterInfo {:name "MyOwn"}}}})) => anything))

(fact "it sends heartbeats")



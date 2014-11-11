(ns eureka-client.core-test
  (:require [midje.sweet :refer :all]
            [eureka-client.core :refer :all]
            [clj-http.client :as http]))


(fact "it registers the app"
  (register "localhost" 8761 "myId" 1234) => anything
    (provided
      (rand-int 10000) => 1
      (http/post
        "http://localhost:8761/eureka/apps/myId"
        (contains {:form-params {:instance
                                  {:hostName (str (.getHostAddress (java.net.InetAddress/getLocalHost)) "-" 1)
                                   :app "myId"
                                   :ipAddr (.getHostAddress (java.net.InetAddress/getLocalHost))
                                   :vipAddress "myId"
                                  :status "UP"
                                  :port 1234
                                  :securePort 443
                                  :dataCenterInfo {:name "MyOwn"}}}})) => anything))



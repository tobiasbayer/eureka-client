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

(fact "it finds all instances of an app"
  (find-instances "localhost" 8761 "myId") => '({:ip "192.168.178.38" :port 10100}
                                                {:ip "192.168.178.38" :port 11100})
    (provided
      (http/get "http://localhost:8761/eureka/apps/myId" anything) =>
                                                             {:orig-content-encoding nil
                                                              :request-time 8
                                                              :status 200
                                                              :headers
                                                               {"Connection" "close"
                                                                "Date" "Tue, 11 Nov 2014 16:10:52 GMT"
                                                                "Transfer-Encoding" "chunked"
                                                                "Content-Type" "application/json"
                                                                "Server" "Apache-Coyote/1.1"}
                                                               :body
                                                               {:application
                                                                {:name "MYID"
                                                                 :instance
                                                                 [{:leaseInfo
                                                                   {:renewalIntervalInSecs 30
                                                                    :durationInSecs 90
                                                                    :registrationTimestamp 1415720281005
                                                                    :lastRenewalTimestamp 1415722261874
                                                                    :evictionTimestamp 0
                                                                    :serviceUpTimestamp 1415720280954}
                                                                   :isCoordinatingDiscoveryServer false
                                                                   :lastDirtyTimestamp 1415720280954
                                                                   :securePort {:enabled "false" :$ "443"}
                                                                   :dataCenterInfo
                                                                   {:class "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo"
                                                                    :name "MyOwn"}
                                                                   :hostName "192.168.178.38-8107"
                                                                   :ipAddr "192.168.178.38"
                                                                   :port {:enabled "true" :$ "10100"}
                                                                   :overriddenstatus "UNKNOWN"
                                                                   :lastUpdatedTimestamp 1415720281005
                                                                   :vipAddress "myid"
                                                                   :status "UP"
                                                                   :actionType "ADDED"
                                                                   :countryId 1
                                                                   :app "MYID"
                                                                   :metadata {:class "java.util.Collections$EmptyMap"}}
                                                                  {:leaseInfo
                                                                   {:renewalIntervalInSecs 30
                                                                    :durationInSecs 90
                                                                    :registrationTimestamp 1415720235036
                                                                    :lastRenewalTimestamp 1415722275398
                                                                    :evictionTimestamp 0
                                                                    :serviceUpTimestamp 1415720234504}
                                                                   :isCoordinatingDiscoveryServer false
                                                                   :lastDirtyTimestamp 1415720234500
                                                                   :securePort {:enabled "false" :$ "443"}
                                                                   :dataCenterInfo
                                                                   {:name "MyOwn"}
                                                                   :hostName "192.168.178.38-2325"
                                                                   :ipAddr "192.168.178.38"
                                                                   :port {:enabled "true" :$ "11100"}
                                                                   :overriddenstatus "UNKNOWN"
                                                                   :lastUpdatedTimestamp 1415720235037
                                                                   :vipAddress "myid"
                                                                   :status "UP"
                                                                   :actionType "ADDED"
                                                                   :countryId 1
                                                                   :app "MYID"}]}}}))

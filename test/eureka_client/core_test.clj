(ns eureka-client.core-test
  (:require [midje.sweet :refer :all]
            [eureka-client.core :refer :all]
            [clj-http.client :as http]))

(def example-instance-response
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
                                    :securePort {:enabled "false" :$ 443}
                                    :dataCenterInfo
                                                                   {:class "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo"
                                                                    :name "MyOwn"}
                                    :hostName "192.168.178.38-8107"
                                    :ipAddr "192.168.178.38"
                                    :port {:enabled "true" :$ 10100}
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
                                    :securePort {:enabled "false" :$ 443}
                                    :dataCenterInfo
                                                                   {:name "MyOwn"}
                                    :hostName "192.168.178.38-2325"
                                    :ipAddr "192.168.178.38"
                                    :port {:enabled "true" :$ 11100}
                                    :overriddenstatus "UNKNOWN"
                                    :lastUpdatedTimestamp 1415720235037
                                    :vipAddress "myid"
                                    :status "UP"
                                    :actionType "ADDED"
                                    :countryId 1
                                    :app "MYID"}]}}})

(def example-delete-response
  {:request-time 13,
   :repeatable? false,
   :streaming? false,
   :chunked? false,
   :headers {"Server" "Apache-Coyote/1.1",
             "Vary" "Accept-Encoding",
             "Content-Type" "application/xml",
             "Content-Length" "0",
             "Date" "Tue, 16 Aug 2016 12:19:21 GMT",
             "Connection" "close"},
   :orig-content-encoding nil,
   :status 200,
   :length 0,
   :body "",
   :trace-redirects ["http://localhost:8080/eureka/v2/apps/blubber/127.0.1.1-2874"]})

(def example-request
  {:form-params {:instance
                 {:hostName (str (.getHostAddress (java.net.InetAddress/getLocalHost)) "-" 1)
                  :app "myId"
                  :ipAddr (.getHostAddress (java.net.InetAddress/getLocalHost))
                  :vipAddress "myId"
                  :status "UP"
                  :port {:$ 1234, "@enabled" "true"}
                  :securePort {:$ 443, "@enabled" "true"}
                  :dataCenterInfo {"@class" "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo", :name "MyOwn"}}}})


(fact "it registers the app"
  (register "localhost" 8761 "myId" 1234) => anything
    (provided
      (rand-int 10000) => 1
      (http/post
        "http://localhost:8761/eureka/v2/apps/myId"
        (contains example-request)) => anything))

(fact "it finds all instances of an app (second call cached)"
  (find-instances "localhost" 8761 "myId") => '({:ip "192.168.178.38" :port 10100}
                                                {:ip "192.168.178.38" :port 11100})
    (provided
      (http/get "http://localhost:8761/eureka/v2/apps/myId" anything) => example-instance-response)

  (find-instances "localhost" 8761 "myId") => '({:ip "192.168.178.38" :port 10100}
                                                {:ip "192.168.178.38" :port 11100})
    (provided
      (http/get "http://localhost:8761/eureka/v2/apps/myId" anything) => anything :times 0))

(fact "it deletes an instance from eureka"
      (delete-instance "localhost" 8761 "myId" "127.0.1.1-4242") => true
      (provided (http/delete "http://localhost:8761/eureka/v2/apps/myId/127.0.1.1-4242")
                => example-delete-response))


(with-state-changes [(before :facts (alter-server-path "/eureka/apps"))]
                    (fact "with changed url it registers the app"
                          (register "localhost" 8761 "myId" 1234) => anything
                          (provided
                            (rand-int 10000) => 1
                            (http/post
                              "http://localhost:8761/eureka/apps/myId"
                              (contains example-request)) => anything))
                    (fact "with changed url it finds all instances of an app, second call cached"
                          fact "it finds all instances of an app (second call cached)"
                          (find-instances "localhost" 8761 "otherId") => '({:ip "192.168.178.38" :port 10100}
                                                                         {:ip "192.168.178.38" :port 11100})
                          (provided
                            (http/get "http://localhost:8761/eureka/apps/otherId" anything) => example-instance-response)

                          (find-instances "localhost" 8761 "otherId") => '({:ip "192.168.178.38" :port 10100}
                                                                         {:ip "192.168.178.38" :port 11100})
                          (provided
                            (http/get "http://localhost:8761/eureka/apps/otherId" anything) => anything :times 0)))
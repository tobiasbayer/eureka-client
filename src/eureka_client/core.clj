(ns eureka-client.core
  (:require [clj-http.client :as http]))

(def request-opts {:content-type :json
                   :socket-timeout 1000
                   :conn-timeout 1000
                   :accept :json})

(defn- tick [ms f & args]
  (future
    (loop []
      (do
        (apply f args)
        (Thread/sleep ms)
        (recur)))))

(defn- app-path [host port app-id]
  (str "http://" host ":" port "/eureka/apps/" app-id))

(defn- instance-path [host port app-id instance-id]
  (str (app-path host port app-id) "/" instance-id))

(defn- send-heartbeat [host port app-id]
  (http/put (instance-path host port app-id (.getHostAddress (java.net.InetAddress/getLocalHost)))
            request-opts))

(defn register
  "Registers the app-id at the Eureka server and sends a heartbeat every 20 seconds"
  [eureka-host eureka-port app-id own-port]
  (do
    (http/post (app-path eureka-host eureka-port app-id)
               (assoc
                 request-opts
                 :form-params
                 {:instance {:hostName (.getHostAddress (java.net.InetAddress/getLocalHost))
                             :app app-id
                             :ipAddr (.getHostAddress (java.net.InetAddress/getLocalHost))
                             :vipAddress app-id
                             :status "UP"
                             :port own-port
                             :securePort 443
                             :dataCenterInfo {:name "MyOwn"}}}))
    (tick 20000 send-heartbeat eureka-host eureka-port app-id)))

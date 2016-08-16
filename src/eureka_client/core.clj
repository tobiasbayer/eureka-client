(ns eureka-client.core
  (:require [clj-http.client :as http]
            [cheshire.core :refer :all]
            [clojure.set :refer [rename-keys]]
            [clojure.core.cache :as cache]))

(def ^:private request-opts {:content-type :json
                             :socket-timeout 1000
                             :conn-timeout 1000
                             :accept :json})

(def ^:private instances-cache (atom (cache/ttl-cache-factory {} :ttl 30000)))

(def server-path "/eureka/v2/apps")

(defn- tick [ms f & args]
  (future
    (loop []
      (apply f args)
      (Thread/sleep ms)
      (recur))))

(defn- eureka-path [host port]
  (str "http://" host ":" port server-path))

(defn- app-path [host port app-id]
  (str (eureka-path host port) "/" app-id))

(defn- instance-path [host port app-id instance-id]
  (str (app-path host port app-id) "/" instance-id))

(defn- send-heartbeat [host port app-id host-id]
  (http/put (instance-path host port app-id host-id)
            request-opts))

(defn- fetch-instances-from-server
  [eureka-host eureka-port app-id]
  (->> (http/get (app-path eureka-host eureka-port  app-id) (assoc request-opts :as :json))
       :body
       :application
       :instance
       (map #(select-keys % [:ipAddr :port]))
       (map #(rename-keys % {:ipAddr :ip}))
       (map #(assoc % :port  (:$ (:port %))))))

(defn- find-instances-cached
  [eureka-host eureka-port app-id]
  (if (cache/has? @instances-cache app-id)
    (swap! instances-cache #(cache/hit % app-id))
    (swap! instances-cache
           #(cache/miss %
                        app-id
                        (fetch-instances-from-server eureka-host eureka-port app-id)))))

(defn register
  "Registers the app-id at the Eureka server and sends a heartbeat every 30
   seconds."
  [eureka-host eureka-port app-id own-port]
  (let [host-id (str (.getHostAddress (java.net.InetAddress/getLocalHost)) "-" (rand-int 10000))]
    (http/post (app-path eureka-host eureka-port app-id)
                 (assoc
                   request-opts
                   :form-params
                   {:instance {:hostName host-id
                               :app app-id
                               :ipAddr (.getHostAddress (java.net.InetAddress/getLocalHost))
                               :vipAddress app-id
                               :status "UP"
                               :port {:$ own-port, "@enabled" "true"}
                               :securePort {:$ 443, "@enabled" "true"}
                               :dataCenterInfo {"@class" "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo", :name "MyOwn"}}}))
      (tick 30000 send-heartbeat eureka-host eureka-port app-id host-id)))

(defn find-instances
  "Finds all instances for a given app id registered with the Eureka server.
  Results are cached for 30 seconds."
  [eureka-host eureka-port app-id]
  (get (find-instances-cached eureka-host eureka-port app-id) app-id))

(defn alter-server-path
  "Modify the server path. Standard is /eureka/apps"
  [new-server-path]
  (alter-var-root (var server-path)
                  (constantly new-server-path)))
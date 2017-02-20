(ns clojure-catalog-service.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.json]
            [ring.util.response :as ring-resp]))

(defn get-uri []
  (String. (or (System/getenv "MONGO_CONNECTION") "mongodb://localhost:27017/clojure-catalog-service")))

(defn db-get-project [project-name]
  (let [uri (get-uri) {:keys [conn db]} (mg/connect-via-uri uri)]
    (mc/find-maps db "projects" {:name project-name})))

(defn token-checker [request]
  (let [token (get-in request [:headers "x-access-token"])]
    (if (= (type token) nil)
      (assoc (http/json-response {:body "forbidden"}) :status 403))))

;; Routes Functions

;; GET /projects
(defn get-projects
  [request]
  (let [uri (get-uri) {:keys [conn db]} (mg/connect-via-uri uri)]
    (http/json-response (mc/find-maps db "projects"))))

;; POST /projects
(defn add-project
  [request]
  (let [incoming (:json-params request)
        uri (get-uri) {:keys [conn db]} (mg/connect-via-uri uri)]
    (http/json-response (mc/insert-and-return db "projects" incoming))))

;; GET /projects/:name
(defn get-project
  [request]
  (let [projectname (get-in request [:path-params :name])] ;; Get-in will follow an sequence of keys
    (http/json-response (db-get-project projectname))))


;; Routes

(def routes
  `[[["/api" {}
      ^:interceptors [(body-params/body-params) http/html-body token-checker]
      ["/v1" {}
       ["/projects" {:get get-projects
              :post add-project}]
        ["/projects/:name" {:get get-project}]]]]])

(def service {:env :prod
              ::http/routes routes
              ::http/allowed-origins {:creds true :allowed-origins (constantly true)}
              ::http/resource-path "/public"
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port (Integer. (or (System/getenv "PORT") 8080))
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})


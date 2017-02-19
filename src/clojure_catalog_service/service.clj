(ns clojure-catalog-service.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.json]
            [ring.util.response :as ring-resp]))

;; Mocked Json Response
(def mocked-json { :project1 { :name "João Marcus" } :project2 { :name "Hellow World" } })

(defn get-uri []
  (String. (or (System/getenv "MONGO_CONNECTION") "mongodb://localhost:27017/clojure-catalog-service")))

(defn db-get-project [project-name]
  (let [uri (get-uri) {:keys [conn db]} (mg/connect-via-uri uri)]))

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
    (http/json-response ((keyword projectname) mocked-json))))

;; Interceptors

(def common-interceptors [(body-params/body-params) http/html-body])


;; Routes

(def routes
  `[[["/api" {}
      ^:interceptors [(body-params/body-params) http/html-body]
      ["/v1" {}
       ["/projects" {:get get-projects
              :post add-project}]
        ["/projects/:name" {:get get-project}]]]]])

(def service {:env :prod
              ::http/routes routes
              ::http/allowed-origins ["*"]
              ::http/resource-path "/public"
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port (Integer. (or (System/getenv "PORT") 8080))
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})


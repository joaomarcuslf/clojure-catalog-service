(ns clojure-catalog-service.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]))

;; Mocked Json Response
(def mocked-json { :project1 { :name "João Marcus" } :project2 { :name "Hellow World" } })

;; Routes Functions

;; GET /about
(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s - served from %s"
                              (clojure-version)
                              (route/url-for ::about-page))))

;; GET /
(defn home-page
  [request]
  (ring-resp/response "Hello World!"))

;; GET /projects
(defn get-projects
  [request]
  (http/json-response mocked-json))

;; POST /projects
(defn add-project
  [request]
  (http/json-response mocked-json))

;; GET /projects/:name
(defn get-project
  [request]
  (let [projectname (get-in request [:path-params :name])] ;; Get-in will follow an sequence of keys
    (http/json-response ((keyword projectname) mocked-json))))

;; Interceptors

(def common-interceptors [(body-params/body-params) http/html-body])


;; Routes

(def routes
  `[[["/" {:get home-page}
      ^:interceptors [(body-params/body-params) http/html-body]
      ["/projects" {:get get-projects
                    :post add-project}]
      ["/projects/:name" {:get get-project}]
      ["/about" {:get about-page}]]]])



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


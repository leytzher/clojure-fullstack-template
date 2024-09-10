(ns backend.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.util.response :refer [response]]
            [ring.middleware.cors :refer [wrap-cors]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [dotenv :refer [env app-env]]
            [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.core.async :refer [go <! timeout]]))

(def fmp-key (env "FMP_KEY"))

;; we will make requests to FMP, handle the response and send back to the use via our own API
;; this is to avoid exposing our credentials in the Frontend
(def fmp-url "https://financialmodelingprep.com/api/v4/")

(defn fetch-fmp-news []
  (try
    (let [response (http/get
                    (str fmp-url "general_news?page=0&apikey=" fmp-key)
                    {:as :json})]
      (:body response))
    (catch Exception e
      (println "Error fetching data: " (.getMessage e)) [])))

(defn process-news-item [item]
  (select-keys item [:publishedDate :title :image :site :text :url]))


(defn get-latest-news-handler [request]
  (let [raw-data (fetch-fmp-news)
        processed-data (map process-news-item raw-data)]
    (response {:latest-news processed-data})))

(defroutes app-routes 
  (GET "/api/latest-news" [] get-latest-news-handler)
  (route/not-found "Not found"))

(def app 
  (-> app-routes 
      wrap-json-body
      wrap-json-response
      (wrap-cors :access-control-allow-origin #"http://localhost:8080"
                 :access-control-allow-methods [:get :post])))

;;; start server in port 3000
(defn -main [& args]
  (jetty/run-jetty app {:port 3000 :join? true}))





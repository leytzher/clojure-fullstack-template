(ns frontend.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [ajax.core :refer [GET POST]]
   ))


(def news-data (reagent/atom {:items []}))

(defn fetch-news []
    (GET "http://localhost:3000/api/latest-news" {:handler (fn [response]
                                                             (swap! news-data assoc :items (:latest-news response)))
                                                  :response-format :json
                                                  :keywords? true}))


(defn news-component []
  (let [{:keys [items]} @news-data]
    [:div
     [:h2 "Latest News"]
       [:ul
        (for [item items]
          ^{:key (:url item)}
          [:li
           [:a {:href (:url item) :target "_blank"}
            (:title item)]
           [:span (str " (" (:symbol item) " - " (:publishedDate item) ")")]
           ])]]))


;;;
(defn app []
  (reagent/create-class
   {:component-did-mount (fn [_] (fetch-news))
    :reagent-render (fn [] [news-component])}))

(defn mount-app-element []
  (rdom/render [app] (gdom/getElement "app")))

(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))

(ns frontend.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]))



(defn app []
  [:div
   [:h1 "Welcome to my Clojurescript App"]
   [:p "This is a basic Reagent component"]])

(defn mount-app-element []
  (rdom/render [app] (gdom/getElement "app")))

(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))

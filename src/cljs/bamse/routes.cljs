(ns bamse.routes
  (:require
   [re-frame.core :as re-frame]
   [bidi.bidi :as bidi]
   [bamse.events :as events]
   [bamse.users.events :as users]))

(def routes ["/"     {""              :home
                      "about"         :about
                      "users/new"     :user-add
                      "users"         :users
                      ["user/"  :id]  :user
                      ["user/"  :id "/edit"] :user-edit
                      true            :not-found}])

(defn parse-url [url]
 (bidi/match-route routes url))

(defn dispatch-route [matched-route]
  (let [panel-name (:handler matched-route)
        user-id    (get-in matched-route [:route-params :id])]
    (case panel-name
      :home  (do
               (re-frame/dispatch [::events/get-readme])
               (re-frame/dispatch [:set-title (name panel-name)]))
      :about (do
               (re-frame/dispatch [::events/get-url])
               (re-frame/dispatch [::events/get-poe])
               (re-frame/dispatch [:set-title (name panel-name)]))
      :users (do
               (re-frame/dispatch [::users/get-users])
               (re-frame/dispatch [:set-title (name panel-name)]))
      :user  (do
               (re-frame/dispatch [::users/get-user (js/parseInt user-id)]))
      :user-edit  (do
                   (re-frame/dispatch [::users/get-user (js/parseInt user-id)]))
      :not-found (do
                   (re-frame/dispatch [:set-title (name panel-name)]))
      "")
    (re-frame/dispatch [:set-active-route matched-route])))

(def url-for (partial bidi/path-for routes))

(defn navigate-to [path]
 (dispatch-route (parse-url path)))

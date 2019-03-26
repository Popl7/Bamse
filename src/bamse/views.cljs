(ns bamse.views
   (:require
    [reagent.core :as r]
    [re-frame.core :as re-frame]
    [bamse.events :as events]
    [bamse.config :as config]
    [bamse.subs :as subs]
    [bamse.helpers :refer [spinner tr trn]]
    [bamse.users.views :as user-views]
    [bamse.routes :refer [url-for]]))

(defn navigation [panel mobile-menu-open]
  [:nav.navbar.navbar-dark.bg-dark.navbar-expand-md.fixed-top
   [:div.container
    [:a.navbar-brand {:href (url-for :home)} [:img {:src   "/img/home.svg"
                                                    :style {:max-width 30}
                                                    :alt   "home icon"}]]
    [:button.navbar-toggler {:type "button"
                             :on-click #(re-frame/dispatch [::events/toggle-mobile-menu])
                             :aria-controls "navbarToggler" :aria-expanded "false" :aria-label "Toggle navigation"}
     [:span.navbar-toggler-icon]]
    [:div#navbarToggler.collapse.navbar-collapse {:class (when mobile-menu-open "show")}
     [:ul.navbar-nav.mr-auto.mt-2.mt-md-0 {:on-click (when mobile-menu-open
                                                         #(re-frame/dispatch [::events/close-mobile-menu]))}
      [:li.nav-item {:class (when (= panel :home) "active")}
       [:a.nav-link {:href (url-for :home)} "Home"]]
      [:li.nav-item {:class (when (= panel :translations) "active")}
       [:a.nav-link {:href (url-for :translations)} "Translations"]]
      [:li.nav-item {:class (when (= panel :about) "active")}
       [:a.nav-link {:href (url-for :about)} "About"]]
      [:li.nav-item {:class (when (= panel :users) "active")}
       [:a.nav-link {:href (url-for :users)} "Users"]]
      [:li.nav-item {:class (when (= panel :not-found) "active")}
       [:a.nav-link {:href "/fake"} "Not found"]]]]]])
     ;; [:form {:class "form-inline my-2 my-lg-0"}
     ;;  [:input {:class "form-control mr-sm-2", :type "search", :placeholder "Search"}]
     ;;  [:button {:class "btn btn-outline-success my-2 my-sm-0", :type "submit"} "Search"]]


(defn not-found-page []
  [:main.container
   [:h1 "404 - Page not found"]
   [:p "The requested page cannot be found :-("]])


(defn home-page []
  (let [readme (re-frame/subscribe [::subs/readme])
        readme-loading (re-frame/subscribe [::subs/readme-loading])]
    (fn []
      [:main.container
       [:div
        (when @readme-loading
          [spinner])
        [:div {:dangerouslySetInnerHTML {:__html @readme}}]]])))


(defn translate-button [lang lang-code lang-string]
  [:button.btn.m-2 {:type :button
                    :class (if (= @lang lang-code) "btn-primary" "btn-secondary")
                    :on-click #(re-frame/dispatch [::events/set-language lang-code])}
         lang-string])


(defn translations-page []
  (let [readme (re-frame/subscribe [::subs/readme])
        readme-loading (re-frame/subscribe [::subs/readme-loading])
        lang (re-frame/subscribe [::subs/language])]
    (fn []
      [:main.container
       [:h1 "Translations"]
       [:h4 "language: " (get config/languages @lang)]
       [:div
        (for [[lang-code lang-string] config/languages]
          ^{:key lang-code}
          [translate-button lang lang-code lang-string])]
       [:p
        ^{:notes "Name of the selected language"}
        (tr @lang "Language")]
       [:p (tr @lang "Translation test")]
       [:p (tr @lang "Greetings")]
       [:p (tr @lang "Please confirm your email")]
       [:p (tr @lang "Welcome, %s!" "John")]
       [:p (trn @lang ["product" "%s products"] 3)]
       [:p (trn @lang ["product" "%s products"] 1)]])))


(defn about-page []
  (let [url (re-frame/subscribe [::subs/url])
        url-loading (re-frame/subscribe [::subs/url-loading])
        poe (re-frame/subscribe [::subs/poe])
        poe-loading (re-frame/subscribe [::subs/poe-loading])]
    (fn []
      [:main.container
       [:h1 "About Page"]
       [:h4 "Server rendered"]
       [:div
        (when @url-loading
          [spinner])
        [:div {:dangerouslySetInnerHTML {:__html @url}}]
        (when config/debug?
          [:div.my-2
           [:button.btn.btn-secondary {:type     :button
                                       :on-click #(re-frame/dispatch [::events/reget-url])} "reload"]])]
       [:h4 "Client rendered"]
       [:div
        (when @poe-loading
          [spinner])
        [:div @poe]
        (when config/debug?
          [:div.my-2
           [:button.btn.btn-secondary {:type     :button
                                       :on-click #(re-frame/dispatch [::events/reget-poe])} "reload"]])]])))


;; main
(defn- panels [route]
  (case (:handler route)
    :home        [home-page]
    :about       [about-page]
    :translations [translations-page]
    :user-add    [user-views/user-add]
    :users       [user-views/users]
    :user        [user-views/user]
    :user-edit   [user-views/edit-user]
    :not-found   [not-found-page]
    [:div]))

(defn page []
  (let [active-route (re-frame/subscribe [::subs/active-route])
        mobile-menu-open (re-frame/subscribe [::subs/mobile-menu-open])]
    (fn []
      [:div
       [navigation (:handler @active-route) @mobile-menu-open]
       [panels @active-route]])))

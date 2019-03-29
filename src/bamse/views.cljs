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

(defn language-chooser [lang open]
  [:li.nav-item.dropdown {:class (when open "show")}
   [:a.nav-link.dropdown-toggle {:href "#"
                                 :on-click #(re-frame/dispatch [::events/toggle-language-menu])
                                 :data-toggle "dropdown"
                                 :aria-haspopup "true"
                                 :aria-expanded "false"}
    (get config/languages lang)]
   [:div.dropdown-menu {:aria-labelledby "navbarDropdownMenuLink"
                        :on-click #(re-frame/dispatch [::events/close-language-menu])
                        :class (when open "show")}
    (for [[lang-code lang-string] config/languages]
      ^{:key lang-code}
      [:a.dropdown-item {:href "#"
                         :class (when (= lang lang-code) "active")
                         :on-click #(re-frame/dispatch [::events/set-language lang-code])}
       lang-string])]])

(defn navigation [panel lang mobile-menu-open language-menu-open]
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
      [:li.nav-item {:class (when (= panel :about) "active")}
       [:a.nav-link {:href (url-for :about)} (tr "About")]]
      [:li.nav-item {:class (when (= panel :users) "active")}
       [:a.nav-link {:href (url-for :users)} (tr "Users")]]
      [:li.nav-item {:class (when (= panel :not-found) "active")}
       [:a.nav-link {:href "/fake"} (tr "Not found")]]
      [language-chooser lang language-menu-open]]]]])

(defn not-found-page []
  [:main.container
   [:h1 (tr "404 - Page not found")]
   [:p (tr "The requested page cannot be found :-(")]])


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


(defn about-page []
  (let [url (re-frame/subscribe [::subs/url])
        url-loading (re-frame/subscribe [::subs/url-loading])
        poe (re-frame/subscribe [::subs/poe])
        poe-loading (re-frame/subscribe [::subs/poe-loading])]
    (fn []
      [:main.container
       [:h1 (tr "About Page")]
       [:h4 (tr "Server rendered")]
       [:div
        (when @url-loading
          [spinner])
        [:div {:dangerouslySetInnerHTML {:__html @url}}]
        (when config/debug?
          [:div.my-2
           [:button.btn.btn-secondary {:type     :button
                                       :on-click #(re-frame/dispatch [::events/reget-url])} (tr "Reload")]])]
       [:h4 (tr "Client rendered")]
       [:div
        (when @poe-loading
          [spinner])
        [:div @poe]
        (when config/debug?
          [:div.my-2
           [:button.btn.btn-secondary {:type     :button
                                       :on-click #(re-frame/dispatch [::events/reget-poe])} (tr "Reload")]])]])))


;; main
(defn- panels [route]
  (case (:handler route)
    :home        [home-page]
    :about       [about-page]
    :user-add    [user-views/user-add]
    :users       [user-views/users]
    :user        [user-views/user]
    :user-edit   [user-views/edit-user]
    :not-found   [not-found-page]
    [:div]))

(defn page []
  (let [active-route (re-frame/subscribe [::subs/active-route])
        lang (re-frame/subscribe [::subs/language])
        mobile-menu-open (re-frame/subscribe [::subs/mobile-menu-open])
        language-menu-open (re-frame/subscribe [::subs/language-menu-open])]
    (fn []
      [:div
       [navigation (:handler @active-route) @lang @mobile-menu-open @language-menu-open]
       [panels @active-route]])))

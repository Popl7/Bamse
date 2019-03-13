(ns bamse.users.views
 (:require [re-frame.core :as re-frame]
           [reagent.core :as r]
           [bamse.helpers :refer [spinner user-avatar]]
           [bamse.routes :refer [url-for navigate-to]]
           [bamse.config :as config]
           [bamse.subs :as subs]
           [bamse.users.subs :as user-subs]
           [bamse.users.events :as user-events]))

(defn full-name [user]
 (apply str (interpose " " [(:first-name user) (:last-name user)])))

(defn valid-name [name]
 (and (not-empty name)
      (< 1 (.-length name))))

(defn valid-user [user]
 (and (valid-name (:first-name user))
      (valid-name (:last-name user))))

(defn field [{:keys [user type label id class]}]
 [:div.form-group
  [:label {:for id} label]
  (if (= type :textarea)
   [:textarea.form-control {:id       id
                            :name     id
                            :value    (get @user id)
                            :rows     4
                            :class    class
                            :onChange #(swap! user assoc id (-> % .-target .-value))}]
   [:input.form-control {:type     type
                         :id       id
                         :name     id
                         :value    (get @user id)
                         :class    class
                         :onChange #(swap! user assoc id (-> % .-target .-value))}])])

(defn user-form [user]
 [:div
  [field {:user user
          :type :text
          :label "First name"
          :id :first-name
          :class (if (valid-name (get @user :first-name)) "is-valid" "is-invalid")}]

  [field {:user user
          :type :text
          :label "Last name"
          :id :last-name
          :class (if (valid-name (get @user :last-name)) "is-valid" "is-invalid")}]

  [field {:user user
          :type :text
          :label "Title"
          :id :title}]

  [field {:user user
          :type :textarea
          :label "Description"
          :id :description}]])

(defn user-add-form [user]
 [:form.col-12.col-md-6 {:action "/api/users"
                         :method :post
                         :on-submit #(do (.preventDefault %)
                                      (re-frame/dispatch [::user-events/add-user @user]))}
  [user-form user]
  [:div
   [:a.btn.btn-outline-secondary.mr-2 {:href (url-for :users)}
    "Cancel"]
   [:button.btn.btn-primary.mr-2 {:type     :submit
                                  :disabled (not (valid-user @user))}
    "Add"]]])


(defn user-edit-form [user]
 [:form.col-12.col-md-6 {:on-submit #(do (.preventDefault %)
                                         (re-frame/dispatch [::user-events/update-user @user]))}
  [user-form user]
  [:div
   [:a.btn.btn-outline-secondary.mr-2 {:href (url-for :user :id (:id @user))}
    "Cancel"]
   [:button.btn.btn-primary.mr-2 {:type     :submit
                                  :disabled (not (valid-user @user))}
    "Save"]]])

(defn user-detail [user]
  [:div
   [:div [user-avatar user]]
   [:div.mb-4 [:h3 (full-name user)]]
   [:div.row.my-2
    [:div.col-12.col-md-2.font-weight-bold [:label "Title"]]
    [:div.col-12.col-md-10 (:title user)]]
   [:div.row.my-2
    [:div.col-12.col-md-2.font-weight-bold [:label "Description"]]
    [:div.col-12.col-md-10 (:description user)]]
   [:div.my-2
    [:a.btn.btn-outline-secondary.mr-2 {:href (url-for :users)}
     "Back"]
    [:a.btn.btn-primary.mr-2 {:href (url-for :user-edit :id (:id user))}
     "Edit"]
    [:button.btn.btn-danger.mr-2 {:type     :button
                                  :on-click #(if (.confirm js/window "Are you sure?")
                                               (re-frame/dispatch [::user-events/delete-user (:id user)])
                                               (.preventDefault %))}
                            "Delete"]]])

(defn users-list [users]
 [:table.table.table-hover
  [:thead.thead-light
   [:tr
    [:th {:scope :col :style {:width 20}} "#"]
    [:th {:scope :col :style {:width 60}} "avatar"]
    [:th {:scope :col} "first name"]
    [:th {:scope :col} "last name"]]]
  [:tbody
   (for [user users]
     ^{:key user}
     [:tr
      [:th {:scope :row} (:id user)]
      [:td [user-avatar user 40]]
      [:td [:a {:href (url-for :user :id (:id user))} (:first-name user)]]
      [:td [:a {:href (url-for :user :id (:id user))} (:last-name user)]]])]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn users []
 (let [users (re-frame/subscribe [::user-subs/users])
       users-loading (re-frame/subscribe [::user-subs/users-loading])]
  (r/create-class {:display-name           "users-component"
                   :reagent-render (fn []
                                       [:main.container
                                        [:h1 "Users"]
                                        [:a.btn.btn-outline-secondary.my-2 {:href (url-for :user-add)} "Add"]
                                        [:div
                                         (if @users-loading
                                          [spinner]
                                          (if (seq @users)
                                            [users-list @users]
                                            [:div "no users"]))
                                         (when config/debug?
                                          [:div
                                           [:button.btn.btn-secondary {:type     :button
                                                                       :on-click #(re-frame/dispatch [::user-events/reget-users])} "reload"]])]])})))

(defn user []
 (let [user (re-frame/subscribe [::user-subs/user])
       route-id (re-frame/subscribe [::subs/active-route-id])
       loading (re-frame/subscribe [::user-subs/user-loading])]
  (r/create-class
   {:component-will-unmount #(re-frame/dispatch [::user-events/reset-user])
    :display-name           "user-component"
    :reagent-render         (fn []
                              [:main.container
                               [:h1 "User"]
                               [:div
                                (if @loading
                                  [spinner]
                                  (if @user
                                    [user-detail @user]
                                    [:div "no user"]))
                                (when config/debug?
                                 [:div
                                  [:button.btn.btn-secondary {:type     :button
                                                              :on-click #(re-frame/dispatch [::user-events/reget-user @route-id])} "reload"]])]])})))

(defn user-add []
 (let [user (r/atom {})
       loading (re-frame/subscribe [::user-subs/add-user-loading])]
   (fn []
     [:main.container
      [:h1 "New user"]
      (when @loading [spinner])
      [user-add-form user]])))

(defn edit-user [id]
  (let [user (re-frame/subscribe [::user-subs/user])
        loading (re-frame/subscribe [::user-subs/update-user-loading])]
    (r/create-class
     {:component-will-unmount #(re-frame/dispatch [::user-events/reset-user])
      :display-name           "edit-user-component"
      :reagent-render         (fn []
                                [:main.container
                                 [:h1 "Edit user"]
                                 (if @loading
                                   [spinner]
                                   (if @user
                                     [user-edit-form (r/atom @user)]
                                     [:div "no user"]))])})))
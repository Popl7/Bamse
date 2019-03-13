(ns bamse.user-add
 (:require [reagent.core :as reagent]
           [bamse.users.views :refer [user-add-form]])
 (:require-macros [devcards.core :as dc :refer [defcard defcard-doc deftest dom-node]]))

(def user-data (reagent/atom {:name "Steven"}))

(defcard user-add-card
 (dc/reagent user-add-form)
 user-data
 {:inspect-data true
  :history      true})

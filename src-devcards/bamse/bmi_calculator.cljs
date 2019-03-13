(ns bamse.bmi-calculator
 (:require [reagent.core :as reagent]
           [goog.string :as gstring])
 (:require-macros [devcards.core :as dc :refer [defcard defcard-doc deftest dom-node]]))

(defn round [nr & [formula]]
  (gstring/format (or formula "%.1f") nr))

(defonce bmi-data (reagent/atom {:height 182 :weight 83.7}))

(defn slider [param value min max]
  [:input.form-control-range {:type "range" :value value :min min :max max
                              :step 0.1
                              :on-change (fn [e]
                                           (swap! bmi-data assoc param (.-target.value e))
                                          (when (not= param :bmi)
                                            (swap! bmi-data assoc :bmi nil)))}])


(defn calc-bmi [bmi-data]
 (let [{:keys [height weight bmi] :as data} bmi-data
       h (/ height 100)]
   (if (nil? bmi)
     (assoc data :bmi (/ weight (* h h)))
     (assoc data :weight (* bmi h h)))))

(defn bmi-component []
 (let [{:keys [weight height bmi]} (calc-bmi @bmi-data)
       [color diagnose] (cond
                         (< bmi 18.5) ["orange" "underweight"]
                         (< bmi 25) ["green" "normal"]
                         (< bmi 30) ["orange" "overweight"]
                         :else ["red" "obese"])]
   [:div
    [:h3 "BMI calculator"]
    [:div.form-group
     [:label "Height: " (int height) " cm"]
     [slider :height height 100 220]]
    [:div.form-group
     [:label "Weight: " (round weight) " kg"]
     [slider :weight weight 30 150]]
    [:div.form-group
     [:label "BMI: " (round bmi) " "
      [:span {:style {:color color}} diagnose]]
     [slider :bmi bmi 10 50]]]))


(defcard bmi-data
  "# BMI Data structure
### Main data structure"
 @bmi-data)


(defcard bmi-calculator
  (dc/reagent bmi-component)
  bmi-data
  {:inspect-data true
   :history      true})


(devcards.core/start-devcard-ui!)
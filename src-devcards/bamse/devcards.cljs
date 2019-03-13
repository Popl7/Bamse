(ns bamse.devcards
 (:require [bamse.bmi-calculator]
           [bamse.user-add])
 (:require-macros
  [devcards.core :refer [defcard defcard-doc deftest dom-node]]))

(defcard demo-card
  "# Demo devcard
### empty devcard; include all other devcards")

(devcards.core/start-devcard-ui!)

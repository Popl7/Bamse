(ns bamse.redux
  (:require [re-frame.core :as re-frame]
            [bamse.config :as config]
            [cljs.reader :refer [read-string]]
            [cljs.spec.alpha :as s]))

(def ignored-events [])

(def with-dev-tools
  (try
    (and
      (exists? js/window)
      (.-__REDUX_DEVTOOLS_EXTENSION__ js/window))
    (catch :default _
      false)))

(defn setup []
  (when-let [devTools with-dev-tools]
    (.subscribe (.connect devTools)
                (fn [msg]
                  (let [msg (js->clj msg :keywordize-keys true)]
                    (when (and (= (:type msg) "DISPATCH")
                               (= (get-in msg [:payload :type]) "JUMP_TO_STATE"))
                      (let [new-state (-> (:state msg)
                                          (js/JSON.parse)
                                          (js->clj :keywordize-keys true)
                                          (:cljs)
                                          (read-string))]
                        (re-frame/dispatch [:load-db new-state]))))))))

(defn dispatch [action state]
  (when config/debug?
    (when-let [devTools with-dev-tools]
      (.send devTools (clj->js action) (clj->js state)))))

(def redux-debug
  (re-frame/->interceptor
    :id    :bm-debug
    :after (fn [{:keys [coeffects effects] :as context}]
             (let [[name & args] (:event coeffects)
                   state          (or (:db effects)
                                      (:db coeffects))
                   state (assoc state :cljs (pr-str state))]
               (when (not-any? #(= % name) ignored-events)
                 (let [action {:type (str name)}
                       action (if (map? args)
                                (merge action args)
                                (assoc action :values args))]
                   (dispatch action state))))
             context)))

(re-frame/reg-event-db
  :load-db
  (fn [db [_ new-db]]
    (let [valid-db (s/valid? :bamse.db/db new-db)]

      (when-not valid-db
        (.warn js/console "[Redux]: NOT setting db, invalid: " (s/explain-data :bamse.db/db new-db)))

      (if (or (= new-db {})      ;; prevent going back to before initialize-db
              (not valid-db))    ;; only use a valid new-db
        db
        (do
          (.warn js/console "[Redux]: setting db")
          new-db)))))

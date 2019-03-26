(defproject gettext "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [brightin/pottery "0.0.1"]]
  :main gettext.core
  :source-paths ["resources" "src"]
  :plugins [[lein-auto "0.1.3"]]
  :auto {:default {:file-pattern #"\.(clj|cljs|po)$"}})


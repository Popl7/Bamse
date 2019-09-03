(defproject bamse "0.1.0-SNAPSHOT"
  :description "Universal Re-frame setup running on Node.js"
  :url "https://gitlab.com/StevenT/bamse.git"
  :license {:name "MIT License"
            :url  "https://opensource.org/licenses/MIT"
            :key  "mit"
            :year 2015}

  :min-lein-version "2.5.3"

  :clean-targets ^{:protect false} ["target" "resources/public/js" "resources/server"]

  :source-paths ["src/clj" "src/cljs"]
  :resource-paths ["target" "resources"]

  :plugins [[lein-auto "0.1.3"]]
  ; :main gettext.core
  :auto {:default {:file-pattern #"\.(clj|cljs|po)$"}}

  :aliases
  {"fig"   ["trampoline" "run" "-m" "figwheel.main"]
   "client" ["trampoline" "run" "-m" "figwheel.main" "-b" "client" "-r"]
   "server" ["trampoline" "run" "-m" "figwheel.main" "-b" "server" "-r"]}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [reagent "0.9.0-SNAPSHOT"]
                 [re-frame "0.10.9"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [bidi "2.1.6"]
                 [kibu/pushy "0.3.8"]
                 [com.cemerick/url "0.1.1"]]

  :profiles
  {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                        [cider/piggieback "0.4.1"]
                        [com.bhauman/figwheel-main "0.2.3"]
                        [com.bhauman/rebel-readline-cljs "0.1.4"]
                        [figwheel-sidecar "0.5.19"]
                        [brightin/pottery "0.0.1"]
                        [org.clojars.unrealistic/re-frame-redux "0.1.0-SNAPSHOT"]]}})

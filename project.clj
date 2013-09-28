(defproject passw "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.5"]
                 [org.clojure/data.csv "0.1.2"]
                 [com.taoensso/timbre "1.5.2"]
                 [de.ubercode.clostache/clostache "1.3.1"]]
  :plugins [[lein-ring "0.8.5"]]
  :ring {:handler passw.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]]}})

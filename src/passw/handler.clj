(ns passw.handler
  (:use [compojure.core]
        [clostache.parser]
        [clojure.pprint])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [taoensso.timbre :as logger :refer (trace debug info warn error report)]))

(def eng (partial clojure.pprint/cl-format nil "~@(~@[~R~]~^ ~A.~)"))

(defn take-csv
  "Takes file name and reads data."
  [fname]
  (with-open [in-file (io/reader fname)]
    (doall
      (csv/read-csv in-file :quote \'))))

(def cheers
  '("Yay!" "Have yourself a donut!" "Awesome!" "Cool!" "FTW!" "Tip top!"
           ))

(def connectors '(
                  "-" "+" "=" "_" "." "/"
                  ))

(def dictionary (flatten (into '() (take-csv "resources/public/words.csv"))))
(def dictionary-size (count dictionary))

(defn exp-s [x n]
  (let [square (fn[x] (* x x))]
    (cond (zero? n) 1
          (even? n) (square (exp-s x (/ n 2)))
          :else (* x (exp-s x (dec n))))))

(defn get-pw "doc-string" []
  (let [       
        part-1  (nth dictionary (rand-int dictionary-size))
        part-2  (nth dictionary (rand-int dictionary-size))
        part-3   (nth dictionary (rand-int dictionary-size))
        password  (str part-1 (nth connectors (rand-int  (count connectors))) part-2 )
        ]
    password
    )

  )

My home has been invaded by a gaggle of ex-fashion models so I 

(defn get-index "doc-string" []

  ; (debug dictionary-size)
  (let [

        template  (slurp "resources/public/template.html")

        password (get-pw)

        countr (* (count (.getBytes password)) 1N)
        per-sec 5000000.0
        secs-in-year 31536000
        per-year (* secs-in-year per-sec)
        y (/ (exp-s countr 24N) per-year  )
        years (eng y)
        cheer (nth cheers (rand-int (count cheers)))

        mmm {:password password, :years years, :cheer cheer }
        ]

    (render template mmm )

    )

  )









(defroutes app-routes
  (GET "/" [] (get-index))

  (GET "/pw" [] (get-pw))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))


;for deployment purposes
(defn -main
  [& [port]]
  (let [port (Integer. (or port
                           (System/getenv "PORT")
                           80))]
    (jetty/run-jetty #'app {:port  port
                            :join? false})))

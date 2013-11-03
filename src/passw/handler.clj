(ns passw.handler
  (:use [compojure.core]
        [clostache.parser]
        [clojure.pprint])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
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
  '("Yay!" "Have yourself a doughnut!" "Awesome!" "Cool!" "FTW!" "Tip top!"
           ))

(def connectors '(
                  "-" "+" "=" "_" "." "/"
                  ))


(def replacements {
                   :a ["a" "A" "@"]
                   :b ["b" "B" "ß" "8" "6"]
                   :c ["c" "C"  ] 
                   :d ["d" "D"]
                   :e ["e" "E" "£" "3"]
                   :f ["f" "F"]
                   :g ["g" "G" ]
                   :h ["h" "H" ]
                   :i ["i" "I" "1"]
                   :j ["j" "J" ]
                   :k ["k" "K" ]
                   :l ["l" "L" "2"]
                   :m ["m" "M" ]
                   :n ["n" "N"]
                   :o ["o" "O" ]
                   :p ["p" "P"]
                   :q ["q" "Q" "9"]
                   :r ["r" "R"]
                   :s ["s" "S"  "5"]
                   :t ["t" "T"]
                   :u ["u" "U"]
                   :v ["v" "V" "7"]
                   :w ["w" "W"]
                   :x ["x" "X"]
                   :y ["y" "Y"]
                   :z ["z" "Z" "2"]
                   })

(def x-replacements {
                   :a ["a" "A" "@" "å" "Å" "∆"]
                   :b ["b" "B" "ß" "8" "6"]
                   :c ["c" "C" "ç" ] 
                   :d ["d" "D"]
                   :e ["e" "E" "£" "€" "3"]
                   :f ["f" "F"]
                   :g ["g" "G" "©"]
                   :h ["h" "H" ]
                   :i ["i" "I" "î" "1"]
                   :j ["j" "J" ]
                   :k ["k" "K" ]
                   :l ["l" "L" "¬" "2"]
                   :m ["m" "M" "µ"]
                   :n ["n" "N" "~"]
                   :o ["o" "O" "ø" "Ø" "0"]
                   :p ["p" "P"]
                   :q ["q" "Q" "9"]
                   :r ["r" "R"]
                   :s ["s" "S"  "5"]
                   :t ["t" "T"]
                   :u ["u" "U" "ü" "Ü" "Ω"]
                   :v ["v" "V" "7"]
                   :w ["w" "W"]
                   :x ["x" "X"]
                   :y ["y" "Y" "¥"]
                   :z ["z" "Z" "2"]
                   })



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

    password))



(defn complexify-pw "doc-string" [simple-pw]
  (def hardz "")
  (let [simplez (seq simple-pw)]
    (doseq [c simplez]
      (if (some #{(str c)} connectors)
        (def hardz (str hardz (str c)))
        (let [
              letter (str c)
              k (keyword letter)
              alts (replacements k)
              newc  (nth alts (rand-int (count alts)))
              ]
          (def hardz (str hardz newc)))))
    hardz))




(defn fiendishly-hardify-pw "doc-string" [simple-pw]
  (def hardz "")
  (let [simplez (seq simple-pw)]
    (doseq [c simplez]
      (if (some #{(str c)} connectors)
        (def hardz (str hardz (str c)))
        (let [
              letter (str c)
              k (keyword letter)
              alts (x-replacements k)
              newc  (nth alts (rand-int (count alts)))
              ]
          (def hardz (str hardz newc)))))
    hardz))





  ;My home has been invaded by a gaggle of ex-fashion models so I wrote this

  (defn get-index "doc-string" []

    ; (debug dictionary-size)
    (let [

          template  (slurp "resources/public/template.html")

          password (get-pw)
          complexified (complexify-pw password)
          fiendishly-hard (fiendishly-hardify-pw password)

          countr (* (count (.getBytes password)) 1N)
          per-sec 5000000.0
          secs-in-year 31536000
          per-year (* secs-in-year per-sec)
          y (/ (exp-s countr 24N) per-year  )
          years (eng y)
          cheer (nth cheers (rand-int (count cheers)))

          mmm {:password password, :complexified complexified, :fiendishlyhard fiendishly-hard :years years, :cheer cheer }
          ]
      (render template mmm )))









  (defroutes app-routes
    (GET "/" [] (get-index))
    (GET "/cpw" [] (complexify-pw (get-pw)))
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




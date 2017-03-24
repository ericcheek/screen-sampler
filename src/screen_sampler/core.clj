(ns screen-sampler.core
  (:gen-class)
  (:require [joda-time])
  (:import java.awt.Toolkit
           java.awt.GraphicsEnvironment
           java.awt.Rectangle
           java.awt.Robot
           java.awt.image.BufferedImage
           javax.imageio.ImageIO
           java.io.File))

(defn snapshot-all-screens [filename]
  (let
      [ge (GraphicsEnvironment/getLocalGraphicsEnvironment)
       screens (->>
                (.getScreenDevices ge)
                (map #(-> %
                          (.getDefaultConfiguration)
                          (.getBounds))))
       combined-bounds (new Rectangle
                        (->> screens (map #(.x %)) (apply min))
                        (->> screens (map #(.y %)) (apply min))
                        (->> screens (map #(+ (.x %) (.width %))) (apply max))
                        (->> screens (map #(+ (.y %) (.height %))) (apply max)))
       robot (new Robot)
       image (.createScreenCapture robot combined-bounds)]
    (ImageIO/write image "png" (new File filename))))

(defn sample-exponential [avg]
  (/
   (Math/log (- 1.0 (Math/random)))
   (/ -1.0 avg)))

(defn as-unix [instant]
  (->
   instant
   (.getMillis)
   (/ 1000.0)
   int))

(defn now-timestamp []
  (as-unix (joda-time/instant)))

(defn offset-time [offset-seconds]
  (->
   (joda-time/instant)
   (joda-time/plus (joda-time/seconds offset-seconds))
   as-unix))

(defn sample-randomly [file-prefix interval sample-count]
  (dotimes [i sample-count]
    (let
        [wakeup-time (->
                      interval
                      sample-exponential
                      offset-time)]
      (while (< (now-timestamp) wakeup-time)
        (try (Thread/sleep 500) (catch Throwable e)))
      (snapshot-all-screens
       (str file-prefix "-" i  "-" (now-timestamp) ".png")))))

(defn -main
  [& [path-prefix interval sample-count :as args]]
  (if (< (count args) 3)
    (do
      (println "Usage: screen-sampler <path-andprefix> <interval-seconds> <sample-size>")
      (println "e.x. screen-sampler /tmp/screenshots 300 30")
      (println args))
    (sample-randomly
     path-prefix
     (clojure.edn/read-string interval)
     (clojure.edn/read-string sample-count))))

(defproject screen-sampler "0.1.0-SNAPSHOT"
  :description "screen-sampler"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clojure.joda-time "0.6.0"]]
  :main ^:skip-aot screen-sampler.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

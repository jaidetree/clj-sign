(defproject clj-sign "0.1.1"
  :description "A clojure library for signing and verifying openssl signatures."
  :url "https://github.com/jayzawrotny/clj-sign"
  :license {:name "BSD-3-Clause"
            :url "https://github.com/jayzawrotny/clj-sign/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [xsc/pem-reader "0.1.1"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

(ns clj-sign.core
  (:gen-class))

(ns deploy.client.core
  (:require [clj-http.client :as client]
            [clojure.string :refer [join]]
            [clojure.walk :refer [keywordize-keys]]
            [pem-reader.core :as pem]
            [clojure.pprint :refer [pprint]])
  (:import [java.security SecureRandom Signature]
           [java.util Base64]))

(defn encode64
  "Takes a bytes stream and returns a base64 encoded string."
  [bytes]
  (.encodeToString (java.util.Base64/getEncoder) bytes))

(defn decode64
  "Takes a base64 string a returns a regular string."
  [str]
  (.decode (java.util.Base64/getDecoder) str))

(defn get-private-key!
  "Create a Java private key instance from a filename.
  Takes a filename string.
  Returns a PrivateKey class instance."
  [filename]
  (->> filename
       (pem/read)
       (pem/private-key)))

(defn get-public-key!
  "Create a Java public key instance from a filename.
  Takes a filename string.
  Returns a PrivateKey class instance."
  [filename]
  (->> filename
       (pem/read)
       (pem/public-key)))

(defn sign
  "RSA private key signing of a hash. Takes hash as string and private key.
  Returns signature string."
  [hash private-key-file]
  (encode64
    (let [msg-data (.getBytes hash)
          sig (doto (Signature/getInstance "SHA256withRSA")
                    (.initSign (get-private-key! private-key-file) (SecureRandom.))
                    (.update msg-data))]
      (.sign sig))))

(defn verify [encoded-sig hash public-key-filename]
  "RSA public key verification of a Base64-encoded signature and an
   assumed source hash.
  Returns true/false if signature is valid."
  (let [msg-data (.getBytes hash)
        signature (decode64 encoded-sig)
        sig (doto (java.security.Signature/getInstance "SHA256withRSA")
              (.initVerify (get-public-key! public-key-filename))
              (.update msg-data))]
    (.verify sig signature)))

(defn now
  "Get the current time in unix seconds. Used to set the timeout.
  Returns a number."
  []
  (quot (System/currentTimeMillis) 1000))

(defn create-hash
  "Create a hash-string to sign with a private key
  Takes a repo string, branch, and timeout.
  Returns a string."
  [repo branch timeout]
  (join "|" [repo branch timeout]))

(defn create-post-body
  "Create the post body params to send to deploy server.
  Takes a map dest-dir, src-dir, branch, repo, private-key.
  Returns a hash-map."
  [{:keys [dest-dir src-dir branch repo private-key]}]
  (let [timeout (+ (now) 5)
        hash (create-hash repo branch timeout)]
    {:dest_dir dest-dir
     :src_dir src-dir
     :branch branch
     :repo repo
     :timeout timeout
     :hash hash
     :signature (sign hash private-key)}))

(defn -main
  "Testing function to test direct calls.
  Takes a server-url string a private-key filepath string and additional
  deploy params.
  Returns the clj-http request body."
  [server-url private-key & {:as args}]
  (->> args
       keywordize-keys
       (deploy! server-url private-key)))

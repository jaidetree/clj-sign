(ns clj-sign.core
  (:require [pem-reader.core :as pem])
  (:import [java.security SecureRandom Signature]
           [java.util Base64])
  (:gen-class))

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
  "RSA private key signing of a message.
  Takes a message string and private key filename string.
  Returns signature string."
  [message private-key-file]
  (encode64
    (let [msg-data (.getBytes message)
          sig (doto (Signature/getInstance "SHA256withRSA")
                    (.initSign (get-private-key! private-key-file) (SecureRandom.))
                    (.update msg-data))]
      (.sign sig))))

(defn verify
  "RSA public key verification of a Base64-encoded signature and an
  assumed source message.
  Takes an encoded signature string, a message string, and a public key
  filename string.
  Returns true/false if signature is valid."
  [encoded-sig message public-key-filename]
  (let [msg-data (.getBytes message)
        signature (decode64 encoded-sig)
        sig (doto (java.security.Signature/getInstance "SHA256withRSA")
              (.initVerify (get-public-key! public-key-filename))
              (.update msg-data))]
    (.verify sig signature)))

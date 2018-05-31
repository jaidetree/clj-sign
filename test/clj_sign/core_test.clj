(ns clj-sign.core-test
  (:require [clojure.test :refer :all]
            [clj-sign.core :refer :all])
  (:import [sun.security.rsa RSAPrivateKeyImpl RSAPublicKeyImpl]
           [java.security PrivateKey PublicKey]))

(deftest base64-encode-test
  (testing "Base64 encoder and decoder"
    (is (= (encode64 (.getBytes "Hello World")) "SGVsbG8gV29ybGQ="))
    (is (= (String. (decode64 (encode64 (.getBytes "Hello World")))) "Hello World"))))

(deftest get-private-key-test
  (testing "Returns a private key"
    (is (instance? PrivateKey (get-private-key! "test/fixtures/private_key.pem")))))

(deftest get-public-key-test
  (testing "Returns a public key"
    (is (instance? PublicKey (get-public-key! "test/fixtures/public_key.pub")))))

(deftest sign-test
  (testing "sign returns a signature signed with private key file"
    (is (not (= (sign "hello world 123" "test/fixtures/private_key.pem")
                "hello world 123")))))

(deftest verify-test
  (testing "verify returns a boolean"
    (let [signature (sign "hello world 345" "test/fixtures/private_key.pem")]
      (is (verify signature "hello world 345" "test/fixtures/public_key.pub")))))

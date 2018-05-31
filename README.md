[![Clojars Project](https://img.shields.io/clojars/v/clj-sign.svg)](https://clojars.org/clj-sign)

# clj-sign

A simple library for signing data and verifying signatures with an openssl RSA keypair.

## Installation
Leiningen/Boot

```
[clj-sign "0.1.0-SNAPSHOT"]
```

Clojure CLI/deps.edn

```
clj-sign {:mvn/version "0.1.0-SNAPSHOT"}
```

Gradle

```
compile 'clj-sign:clj-sign:0.1.0-SNAPSHOT'
```

Maven

```
<dependency>
  <groupId>clj-sign</groupId>
  <artifactId>clj-sign</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Usage

In Clojure:

```clojure
(ns my-app
  (:require [clj-sign.core :refer [sign verify]]))

(sign "my-data" "path/to/private_key.pem")
(verify signature "my-data" "path/to/public_key.pem")
```

Generate RSA private and public key pair:

```
openssl genrsa -out resources/deploy.secret.pem 1024
openssl rsa -in resources/deploy.secret.pem -pubout -out resources/deploy.secret.pub
```

### Bugs

Should any bugs or feature requests come up please report them in [https://github.com/jayzawrotny/clj-sign/issues] repo.

## License

Copyright © 2018 Jay

Distributed under the BSD 3-Clause "New" or "Revised" License.

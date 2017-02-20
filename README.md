# clojure-catalog-service

Clojure Microservice using Pedestal module

## Getting Started

1. Start the application: `lein run`
2. Go to [localhost:8080](http://localhost:8080/)

## Depencencies

- [lein](https://leiningen.org/)
- [MongoDB](https://www.mongodb.com/) or set Env Variable

## Env Variables

MONGO_CONNECTION: URL For mongoDB connection **Default: mongodb://localhost:27017/clojure-catalog-service**
PORT: Port for service startup **Default: 8080**

## REPL

```
  lein repl

  (defn my-service [] (run-dev))

  (my-service)
```

> Made by: [@joaomarcuslf](http://joaomarcuslf.github.io/)

# eureka-client

This library aims to be a Clojure client for Netflix' Eureka service registry.
At the moment registering an app and sending heartbeats is supported.

[![Build Status](https://travis-ci.org/codebrickie/eureka-client.svg?branch=master)](https://travis-ci.org/codebrickie/eureka-client)

Latest release version:
[![Clojars Project](http://clojars.org/org.clojars.codebrickie/eureka-client/latest-version.svg)](http://clojars.org/org.clojars.codebrickie/eureka-client)

## Usage

Include eureka-client as a dependency in your ```project.clj```:
```
[org.clojars.codebrickie/eureka-client "0.1.0"]
```

To build from source, run the following commands:
```
lein deps
lein install
```

Register your service "my-service-name" with port 10100 at the Eureka server at localhost:8761 like this:
```
(ns your.name.space
  (:require [eureka-client.core :as eureka]))

  (eureka/register "localhost" 8761 "my-service-name" 10100)
```

After registering, a heartbeat will automatically be sent every 20 seconds.

## Contributing
Contributions are welcome!
Please make sure you have added Midje tests for your contribution and ```lein midje``` runs fine before sending a pull request.

## License

Copyright © 2014 Tobias Bayer

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

# akka-fsm-throttler

Suppose that an application is receiving lots of request at a time. It is not feasable to process all requests in parallel.
To configure the parallelism on the basis of number of request we need throttler. With a throttler, we can ensure that calls we make do not cross the threshold rate.

*** To run application run following commands ***
```
sbt clean compile
sbt run
```

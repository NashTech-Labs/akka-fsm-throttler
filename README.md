# akka-fsm-throttler

Suppose that an application is receiving lots of request at a time. It is not feasable to process all requests in parallel.
To configure the parallelism on the basis of number of request we need throttler. With a throttler, we can ensure that calls we make do not cross the threshold rate.

# Clone the repo
```
git clone https://github.com/knoldus/akka-fsm-throttler.git
cd akka-fsm-throttler
```
# Build the code
If this is your first time running SBT, you have to download the used dependencies.
```
cd akka-fsm-throttler
sbt clean compile
```
# To run application run following command.
```
sbt run
```
# To test the application run following command.
```
sbt clean test
```

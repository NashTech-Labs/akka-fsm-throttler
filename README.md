# akka-fsm-throttler

Suppose that an application is receiving lots of request at a time. It is not feasable to process all request in parallel.
To configure the parallelism on the basis of number of request we need throttler. With a throttler, you can ensure that calls you make do not cross the threshold rate.


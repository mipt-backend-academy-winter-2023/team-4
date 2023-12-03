package routing.clients

import core.config.JamClientConfig
import nl.vroste.rezilience.{CircuitBreaker, TrippingStrategy}
import nl.vroste.rezilience.CircuitBreaker.{CircuitBreakerCallError, State}
import zio._

class MyCircuitBreakerImpl(circuitBreaker: CircuitBreaker[Any]) extends MyCircuitBreaker {
  override def run[R, E, A](effect: ZIO[R, E, A])
      : ZIO[R with MyCircuitBreaker with JamClientConfig, CircuitBreakerCallError[E], A] =
    circuitBreaker(effect)
}

object MyCircuitBreakerImpl {
  val live: ZLayer[Scope with JamClientConfig, Nothing, MyCircuitBreaker] =
    ZLayer {
      for {
        fallbackConfig <- ZIO.service[JamClientConfig]
        cb <- CircuitBreaker.make(
          TrippingStrategy.failureCount(fallbackConfig.fallbackPolicy.failuresThreshold),
          zio.Schedule.exponential(
            Duration.fromSeconds(fallbackConfig.fallbackPolicy.expBackoffSeconds)
          ),
          onStateChange = (s: State) => ZIO.logInfo(s"State change to $s").ignore
        )
      } yield new MyCircuitBreakerImpl(cb)
    }
}

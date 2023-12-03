package core.config

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

final case class FallbackConfig(
    failuresThreshold: Int,
    expBackoffSeconds: Int
)

object FallbackConfig {
  implicit val configReader: ConfigReader[FallbackConfig] = deriveReader[FallbackConfig]
}

final case class JamClientConfig(
    fallbackPolicy: FallbackConfig,
    url: String
)

object JamClientConfig {
  implicit val configReader: ConfigReader[JamClientConfig] = deriveReader[JamClientConfig]
}

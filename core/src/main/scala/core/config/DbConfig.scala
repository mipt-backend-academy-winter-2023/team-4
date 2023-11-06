package core.config

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader

final case class DbConfig(
    url: String,
    user: String,
    password: String
)

object DbConfig {
  implicit val configReader: ConfigReader[DbConfig] = deriveReader[DbConfig]
}

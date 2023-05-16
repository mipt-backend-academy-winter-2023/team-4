package auth.config

import pureconfig.ConfigSource
import pureconfig.generic.auto.exportReader
import zio.ZLayer
import zio.http.ServerConfig

case class ServiceConfig(host: String, port: Int)

object ServiceConfig {

  private val source: ConfigSource = ConfigSource.default.at("app").at("service-config")
  private val appConfig: ServiceConfig = source.loadOrThrow[ServiceConfig]

  val live: ZLayer[Any, Nothing, ServerConfig] = ServerConfig.live {
    ServerConfig.default
      .binding(
        hostname = appConfig.host,
        port = appConfig.port
      )
  }

}

package auth.config

import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.generic.auto.exportReader
import pureconfig.generic.semiauto.deriveReader
import zio.{ULayer, ZIO, ZLayer}
import zio.http.ServerConfig
import zio.sql.ConnectionPoolConfig

import java.util.Properties

final case class ServiceConfig(host: String, port: Int)

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

  val dbLive: ULayer[DbConfig] = {
    import ConfigImpl._
    ZLayer.fromZIO(
      ZIO.attempt(source.loadOrThrow[ConfigImpl].dbConfig).orDie
    )
  }

  val connectionPoolConfigLive
  : ZLayer[DbConfig, Throwable, ConnectionPoolConfig] =
    ZLayer(
      for {
        serverConfig <- ZIO.service[DbConfig]
      } yield (ConnectionPoolConfig(
        serverConfig.url,
        connProperties(serverConfig.user, serverConfig.password)
      ))
    )

  private def connProperties(user: String, password: String): Properties = {
    val props = new Properties
    props.setProperty("user", user)
    props.setProperty("password", password)
    props
  }

}

case class ConfigImpl(
                       dbConfig: DbConfig,
                       httpServiceConfig: HttpServerConfig
                     )
case class DbConfig(
                     url: String,
                     user: String,
                     password: String
                   )
case class HttpServerConfig(
                             host: String,
                             port: Int
                           )

object ConfigImpl {
  implicit val configReader: ConfigReader[ConfigImpl] = deriveReader[ConfigImpl]
  implicit val configReaderHttpServerConfig: ConfigReader[HttpServerConfig] =
    deriveReader[HttpServerConfig]
  implicit val configReaderDbConfig: ConfigReader[DbConfig] =
    deriveReader[DbConfig]
}

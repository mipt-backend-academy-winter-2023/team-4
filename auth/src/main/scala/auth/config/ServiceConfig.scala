package auth.config

import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.generic.auto.exportReader
import pureconfig.generic.semiauto.deriveReader
import zio.{ULayer, ZIO, ZLayer}
import zio.http.ServerConfig
import zio.sql.ConnectionPoolConfig

import java.util.Properties

final case class ServiceConfig(host: String, port: Int)
final case class DbConfig(url: String, user: String, password: String)

object ServiceConfig {

  private val source: ConfigSource = ConfigSource.default.at("app")
  private val appConfig: ServiceConfig = source.at("service-config").loadOrThrow[ServiceConfig]
  private val dbConfig: DbConfig = source.at("db-config").loadOrThrow[DbConfig]

  val live: ZLayer[Any, Nothing, ServerConfig] = ServerConfig.live {
    ServerConfig.default
      .binding(
        hostname = appConfig.host,
        port = appConfig.port
      )
  }

  val dbLive: ULayer[DbConfig] = {
    ZLayer.fromZIO(
      ZIO.attempt(dbConfig).orDie
    )
  }

  val connectionPoolConfigLive: ZLayer[DbConfig, Throwable, ConnectionPoolConfig] =
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

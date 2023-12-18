package core.config

import core.flyway.FlywayAdapter
import pureconfig.generic.semiauto.deriveReader
import pureconfig.{ConfigReader, ConfigSource}
import zio.sql.ConnectionPoolConfig
import zio.{ULayer, ZIO, ZLayer}

import java.util.Properties

final case class AppConfig(
    dbConfig: DbConfig,
    jamClientConfig: JamClientConfig
)

object AppConfig {
  private val basePath = "app"
  private val source = ConfigSource.default.at(basePath)

  val dbLive: ULayer[DbConfig] = {
    ZLayer.fromZIO(
      ZIO.attempt(source.loadOrThrow[AppConfig].dbConfig).orDie
    )
  }

  val jamLive: ULayer[JamClientConfig] =
    ZLayer.fromZIO(
      ZIO.attempt(source.loadOrThrow[AppConfig].jamClientConfig).orDie
    )

  val connectionPoolLive
      : ZLayer[DbConfig with FlywayAdapter.Service, Throwable, ConnectionPoolConfig] =
    ZLayer(
      for {
        serverConfig <- ZIO.service[DbConfig]
        _ <- ZIO.serviceWithZIO[FlywayAdapter.Service](_.migration)
      } yield ConnectionPoolConfig(
        serverConfig.url,
        connProperties(serverConfig.user, serverConfig.password)
      )
    )

  private def connProperties(user: String, password: String): Properties = {
    val props = new Properties
    props.setProperty("user", user)
    props.setProperty("password", password)
    props
  }

  implicit val configReader: ConfigReader[AppConfig] = deriveReader[AppConfig]
}

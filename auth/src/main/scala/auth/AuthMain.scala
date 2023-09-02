package auth

import auth.api.AuthRoutes
import auth.config.{Config, ServiceConfig}
import repo.UserRepositoryImpl
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio.sql.ConnectionPool
import flyway.FlywayAdapter

object AuthMain extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val server =
      for {
        _ <- ZIO.logInfo("Starting Auth service")
        flyway <- ZIO.service[FlywayAdapter.Service]
        _ <- flyway.migration
        server <- zio.http.Server.serve(AuthRoutes.app)
      } yield ()
    server.provide(
      Server.live,
      ServiceConfig.live,
      Config.dbLive,
      FlywayAdapter.live,
      Config.connectionPoolLive,
      ConnectionPool.live,
      UserRepositoryImpl.live
    )
  }
}
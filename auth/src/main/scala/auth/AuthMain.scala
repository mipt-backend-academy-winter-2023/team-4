package auth

import auth.api.AuthRoutes
import auth.config.ServiceConfig
import auth.repo.UserRepositoryImpl
import core.config.AppConfig
import core.flyway.FlywayAdapter
import zio.http.Server
import zio.sql.ConnectionPool
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object AuthMain extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val server =
      for {
        _ <- ZIO.logInfo("Starting Auth service")
        server <- zio.http.Server.serve(AuthRoutes.app)
      } yield ()
    server.provide(
      Server.live,
      ServiceConfig.live,
      AppConfig.dbLive,
      FlywayAdapter.live,
      AppConfig.connectionPoolLive,
      ConnectionPool.live,
      UserRepositoryImpl.live
    )
  }

}

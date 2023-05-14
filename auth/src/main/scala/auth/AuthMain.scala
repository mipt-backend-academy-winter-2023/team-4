package auth

import auth.api.AuthRoutes
import auth.config.ServiceConfig
import flyway.FlywayAdapter
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object AuthMain extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val server = for {
      _ <- ZIO.logInfo("Starting Auth service")
      flyway <- ZIO.service[FlywayAdapter.Service]
      _ <- flyway.migration
      _ <- Server
        .serve(AuthRoutes.app)
        .provide(
          Server.live,
          ServiceConfig.live
        )
    } yield ()

    server.provide(
      ServiceConfig.dbLive,
      FlywayAdapter.live,
      Server.live,
      ServiceConfig.connectionPoolConfigLive
    )
  }

}

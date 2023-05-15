package auth

import auth.api.AuthRoutes
import auth.config.ServiceConfig
import flyway.FlywayAdapter
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object AuthMain extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val server = for {
      flyway <- ZIO.service[FlywayAdapter.Service]
      _ <- flyway.migration
      server <- Server.serve(AuthRoutes.app)
    } yield server

    server.provide(
      Server.live,
      ServiceConfig.live,
      ServiceConfig.dbLive,
      FlywayAdapter.live
    )
  }

}

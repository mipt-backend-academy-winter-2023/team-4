package auth

import auth.api.AuthRoutes
import auth.config.ServiceConfig
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import config.Config
import flyway.FlywayAdapter

object AuthMain extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val server =
      for {
        flyway <- ZIO.service[FlywayAdapter.Service]
        _ <- flyway.migration
        server <- zio.http.Server.serve(AuthRoutes.app)
      } yield ()
//    for {
//      _ <- ZIO.logInfo("Starting Auth service")
//      _ <- Server.serve(AuthRoutes.app)
//        .provide(
//          Server.live,
//          ServiceConfig.live
//        )
//    } yield ()
  }

}

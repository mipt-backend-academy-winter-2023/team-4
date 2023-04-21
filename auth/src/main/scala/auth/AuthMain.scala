package auth

import auth.api.AuthRoutes
import auth.config.ServiceConfig
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object AuthMain extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      _ <- ZIO.logInfo("Starting Auth service")
      _ <- Server.serve(AuthRoutes.app)
        .provide(
          Server.live,
          ServiceConfig.live
        )
    } yield ()

}

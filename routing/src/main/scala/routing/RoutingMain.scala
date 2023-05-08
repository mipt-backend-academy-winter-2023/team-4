package routing

import routing.api.RoutingRoutes
import routing.config.ServiceConfig
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object RoutingMain extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      _ <- ZIO.logInfo("Starting Routing service")
      _ <- Server.serve(RoutingRoutes.app)
        .provide(
          Server.live,
          ServiceConfig.live
        )
    } yield ()

}

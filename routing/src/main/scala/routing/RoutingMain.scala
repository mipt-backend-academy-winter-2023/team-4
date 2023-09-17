package routing

import routing.api.RoutingRoutes
import routing.config.ServiceConfig
import routing.repo.VertexRepositoryImpl
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import config.Config
import zio.sql.ConnectionPool
import flyway.FlywayAdapter

object RoutingMain extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val server =
      for {
        _ <- ZIO.logInfo("Starting Routing service")
        flyway <- ZIO.service[FlywayAdapter.Service]
        _ <- flyway.migration
        server <- zio.http.Server.serve(RoutingRoutes.app)
      } yield ()
    server.provide(
      Server.live,
      ServiceConfig.live,
      Config.dbLive,
      FlywayAdapter.live,
      Config.connectionPoolLive,
      ConnectionPool.live,
      VertexRepositoryImpl.live
    )
  }

}

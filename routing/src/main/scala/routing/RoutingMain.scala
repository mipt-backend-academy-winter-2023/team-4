package routing

import core.config.AppConfig
import core.flyway.FlywayAdapter
import routing.api.RoutingRoutes
import routing.db.GraphRepositoryImpl
import routing.models.graph.Graph
import zio.http.Server
import zio.sql.ConnectionPool
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object RoutingMain extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val server = for {
      _ <- ZIO.logInfo("Starting Routing service")
      server <- ZIO.serviceWithZIO[RoutingRoutes](_.app)
    } yield ()

    server.provide(
      AppConfig.dbLive,
      FlywayAdapter.live,
      AppConfig.connectionPoolLive,
      ConnectionPool.live,
      GraphRepositoryImpl.live,
      Graph.graphLayer,
      RoutingRoutes.layer,
      Server.default
    )
  }

}

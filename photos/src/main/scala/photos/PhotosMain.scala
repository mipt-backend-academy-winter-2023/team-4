package photos

import photos.config.ServiceConfig
import photos.api.PhotosRoutes
import zio.http.Server
import zio.{ZIO, ZIOAppDefault, ZIOAppArgs, Scope}

object PhotosMain extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val server =
      for {
        _ <- ZIO.logInfo("Starting Photos service")
        server <- zio.http.Server.serve(PhotosRoutes.app)
      } yield ()
    server.provide(
      Server.live,
      ServiceConfig.live
    )
  }
}

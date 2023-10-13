package images

import images.api.ImagesRoutes
import images.config.ServiceConfig
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object ImagesMain extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    ZIO.logInfo("Starting Images service")
    zio.http.Server.serve(ImagesRoutes.app).provide(
      Server.live,
      ServiceConfig.live
    )
  }

}

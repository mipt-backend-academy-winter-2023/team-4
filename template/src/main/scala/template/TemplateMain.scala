package template

import template.api.TemplateRoutes
import template.config.ServiceConfig
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object TemplateMain extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      _ <- ZIO.logInfo("Starting TemplateMain")
      _ <- Server.serve(TemplateRoutes.app)
        .provide(
          Server.live,
          ServiceConfig.live
        )
    } yield ()

}

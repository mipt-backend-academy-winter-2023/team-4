package images.api

import zio._
import zio.ZIO
import zio.http._
import zio.http.model.Method
import zio.http.model.Status
import zio.stream.ZSink

import java.nio.file
import java.nio.file.{Files, Paths}

object ImagesRoutes {
  private def getImagesDir: file.Path = {
    Paths.get(".", "images/resources/images")
  }

  private def getImagesFilePath(place_id: String): file.Path = {
    getImagesDir.resolve(place_id + ".png")
  }

  val app: HttpApp[Any, Nothing] =
    Http.collectZIO[Request] {
      case Method.GET -> !! =>
        ZIO.succeed(Response.text("The Images service is working"))
    }
}

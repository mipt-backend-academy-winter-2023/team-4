package photos.api

import zio._
import zio.ZIO
import zio.http._
import zio.http.model.Method
import zio.http.model.Status
import zio.stream.{ZSink, ZStream, ZPipeline}
import java.nio.file.{Files, Paths}
import photos.utils.JpegValidation

object PhotosRoutes {

  def getPath(id: String) = Paths.get(s"/photos/${id}_image.png")
  private val kMaxSize: Int = 10 * 1024 * 1024

  val app: HttpApp[Any, Response] =
    Http.collectZIO[Request] {
      case req @ Method.PUT -> !! / "photo" / id =>
        (for {
          _ <- ZIO.logInfo("PUT photo/{id}")
          path <- ZIO.attempt(Files.createFile(getPath(id)))
          _ <- req.body.asStream.via(JpegValidation.pipeline).run(ZSink.drain)
          size <- req.body.asStream.run(ZSink.fromPath(path))
        } yield size).either.map {
          case Right(size) if size <= kMaxSize => Response.status(Status.Ok)
          case _ =>
            Files.deleteIfExists(getPath(id))
            Response.status(Status.BadRequest)
        }

      case req @ Method.GET -> !! / "photo" / id =>
        for {
          _ <- ZIO.logInfo("GET photo/{id}")
          result <- if (Files.exists(getPath(id))) {
            ZIO.succeed(Response(
              status = Status.Ok,
              body = Body.fromStream(ZStream.fromFile(getPath(id).toFile))
            ))
          } else {
            ZIO.succeed(Response.status(Status.NotFound))
          }
        } yield result

    }
}

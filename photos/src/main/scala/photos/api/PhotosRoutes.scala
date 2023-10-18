package photos.api

import zio._
import zio.ZIO
import zio.http._
import zio.http.model.Method
import zio.http.model.Status
import zio.stream.{ZPipeline, ZSink, ZStream}

import java.nio.file.{FileAlreadyExistsException, Files, Paths}
import photos.utils.JpegValidation
import photos.utils.JpegValidation.Error

object PhotosRoutes {

  def getPath(id: String) = Paths.get(s"/${id}_image.png")
  private val kMaxSize: Int = 10 * 1024 * 1024

  val app: HttpApp[Any, Response] =
    Http.collectZIO[Request] {
      case req @ Method.PUT -> !! / "photo" / id =>
        val path = Files.createTempFile(null, null)
        (for {
          _ <- req.body.asStream.via(JpegValidation.pipeline).run(ZSink.drain)
          size <- req.body.asStream.run(ZSink.fromPath(path))
        } yield size).either.map {
          case Right(size) if size <= kMaxSize =>
            try {
              Files.move(path, getPath(id), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
            } catch {
              case _: Throwable => Response.status(Status.Forbidden)
            }
            Response.status(Status.Ok)
          case _ =>
            try {
              Files.deleteIfExists(path)
            } catch {
              case _: Throwable => Response.status(Status.Forbidden)
            }
            Response.status(Status.BadRequest)
        }

      case req @ Method.GET -> !! / "photo" / id =>
        if (Files.exists(getPath(id))) {
          ZIO.succeed(Response(
            status = Status.Ok,
            body = Body.fromStream(ZStream.fromFile(getPath(id).toFile))
          ))
        } else {
          ZIO.succeed(Response.status(Status.NotFound))
        }

    }
}

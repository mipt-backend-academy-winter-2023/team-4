package images.api

import images.utils.JpegValidation
import zio.ZIO
import zio.http._
import zio.http.model.Method
import zio.http.model.Status
import zio.stream.{ZPipeline, ZSink, ZStream}

import java.nio.file
import java.nio.file.{Files, Paths}

object ImagesRoutes {
  private val fileSizeMb = 10 * 1000 * 1000
  private def getImagesDir: file.Path = {
    Paths.get(".", "images/resources/images")
  }

  private def getImagesFilePath(placeId: String): file.Path = {
    getImagesDir.resolve(placeId + ".jpeg")
  }

  val app: HttpApp[Any, Nothing] =
    Http.collectZIO[Request] {

      case Method.GET -> !! / "images" / placeId =>
        val filePath = getImagesFilePath(placeId)
        if (Files.exists(filePath)) {
            ZIO.succeed(
              Response(
                body = Body.fromStream(
                  ZStream
                    .fromPath(filePath)
                    .via(ZPipeline.inflate())
                )
              )
            )
        } else {
          ZIO.succeed(Response.status(Status.NotFound))
        }
      case request@Method.POST -> !! / "images" / placeId =>
        val dirPath = getImagesDir
        if (!Files.exists(dirPath))
          Files.createDirectories(dirPath)
        val filePath = getImagesFilePath(placeId)
        if (Files.exists(filePath)) {
          ZIO.succeed(Response.status(Status.BadRequest))
        } else {
          val result = for {
            path <- ZIO.attempt(Files.createFile(filePath))
            bytesCount <- request
              .body
              .asStream
              .via(JpegValidation.pipeline)
              .run(ZSink.fromPath(path))
          } yield bytesCount
          result.either.map {
            case Left(e) =>
              Response(
                status = Status.InternalServerError,
                body = Body.fromString(e.getMessage)
              )
            case Right(bytesCount) if bytesCount >= fileSizeMb =>
              Response(Status.BadRequest)
            case Right(bytesCount) =>
              println(bytesCount.toString)
              Response(status = Status.Created)
          }
        }
    }
}

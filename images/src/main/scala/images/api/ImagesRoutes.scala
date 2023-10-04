package images.api

import zio.ZIO
import zio.http._
import zio.http.model.Method
import zio.http.model.Status
import zio.stream.{ZPipeline, ZSink, ZStream}

import java.nio.file
import java.nio.file.{Files, Paths}

object ImagesRoutes {
  private def getImagesDir: file.Path = {
    Paths.get(".", "images/resources/images")
  }

  private def getImagesFilePath(place_id: String): file.Path = {
    getImagesDir.resolve(place_id + ".jpeg")
  }

  val app: HttpApp[Any, Nothing] =
    Http.collectZIO[Request] {

      case Method.GET -> !! / "images" / place_id =>
        val filePath = getImagesFilePath(place_id)
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
      case request@Method.POST -> !! / "images" / place_id =>
        val dirPath = getImagesDir
        if (!Files.exists(dirPath))
          Files.createDirectories(dirPath)
        val filePath = getImagesFilePath(place_id)
        if (Files.exists(filePath)) {
          ZIO.succeed(Response.status(Status.BadRequest))
        } else {
          val result = for {
            path <- ZIO.attempt(Files.createFile(filePath))
            bytesCount <- request
              .body
              .asStream
              .via(ZPipeline.deflate())
              .run(ZSink.fromPath(path))
          } yield bytesCount
          result.either.map {
            case Left(e) =>
              Response(
                status = Status.InternalServerError,
                body = Body.fromString(e.toString)
              )
            case Right(bytesCount) if bytesCount >= 10000000 =>
              Response(Status.BadRequest)
            case Right(bytesCount) =>
              println(bytesCount.toString)
              Response(status = Status.Created)
          }
        }
    }
}

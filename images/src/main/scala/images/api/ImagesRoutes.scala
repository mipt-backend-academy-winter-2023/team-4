package images.api

import zio._
import zio.ZIO
import zio.http._
import zio.http.model.{Header, Method, Status}
import zio.stream.{ZSink, ZStream}

import java.nio.file.{Files, Paths, Path => JPath}
import images.helpers.{AuthHelper, ErrorWithResponse}

object ImagesRoutes {
  val app: HttpApp[Any, Nothing] =
    Http.collectZIO[Request] {
      case request @ Method.POST -> !! / "images" / uuid =>
        try {
          AuthHelper.validateHeaderOrFinish(request.headers.get(kAuthHeader))
          val contentLength = request.headers.get(kContentLengthHeader).getOrElse("0").toInt

          val outputPath = Paths.get(".", "src", uuid + "_image.jpeg")
          if (!Files.exists(outputPath.getParent)) {
            Files.createDirectories(outputPath.getParent)
          }
          val path =
            try {
              Files.createFile(outputPath)
            } catch {
              case _: Throwable => throw ErrorWithResponse(Response.status(Status.Conflict))
            }
          request.body.asStream.take(Math.min(contentLength, kMaxSize)).run(
            ZSink.fromPath(path)
          ).catchAll(_ =>
            throw ErrorWithResponse(Response.status(Status.Conflict))
          )
          ZIO.succeed(Response.ok)
        } catch {
          case ErrorWithResponse(response) => ZIO.succeed(response)
          case ex: Throwable               => throw ex
        }
      case request @ Method.GET -> !! / "images" / uuid =>
        try {
          AuthHelper.validateHeaderOrFinish(request.headers.get(kAuthHeader))

          val outputPath = Paths.get(".", "src", uuid + "_image.png")
          if (!Files.exists(outputPath)) {
            throw ErrorWithResponse(Response.status(Status.NotFound))
          }
          ZIO.succeed(Response(
            status = Status.Ok,
            body = Body.fromStream(ZStream.fromFileName(outputPath.toAbsolutePath.toString))
          ))
        } catch {
          case ErrorWithResponse(response) => ZIO.succeed(response)
          case ex: Throwable               => throw ex
        }
    }
  private val kAuthHeader = "Authorization"
  private val kContentLengthHeader = "Content-Length"
  private val kMaxSize = 10 * 1024 * 1024
}

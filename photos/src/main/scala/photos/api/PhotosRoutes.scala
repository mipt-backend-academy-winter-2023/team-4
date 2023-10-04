package photos.api

import zio._
import zio.ZIO
import zio.http._
import zio.http.model.Method
import zio.http.model.Status
import zio.stream.{ZSink, ZStream, ZPipeline}
import java.nio.file.{Files, Paths}

object PhotosRoutes {

    def getPath(id: String) = Paths.get(s"/${id}_image.png")

    val app: HttpApp[Any, Response] = 
        Http.collectZIO[Request] {
            case req@Method.PUT -> !! / "photo" / id => 
                (for {
                    path <- ZIO.attempt(Files.createFile(getPath(id)))
                    size <- req.body.asStream.via(ZPipeline.deflate()).run(ZSink.fromPath(path))
                } yield size).either.map {
                    case Left(_) => Response.status(Status.BadRequest)
                    case Right(_) => Response.status(Status.Ok)
                }
            
            case req@Method.GET -> !! / "photo" / id =>
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
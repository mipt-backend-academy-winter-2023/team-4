package auth.api

import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.syntax.EncoderOps
import repo.UserRepository
import zio.ZIO
import zio.http._
import zio.http.model.{Method, Status}

object AuthRoutes {

  val app: Http[UserRepository, Response, Request, Response] =
    Http.collectZIO[Request] {

      case Method.GET -> !! / "auth" / "users" => UserRepository
          .findAll()
          .runCollect
          .map(chunk => chunk.toArray)
          .tapError(e => ZIO.logError(e.getMessage))
          .either
          .map {
            case Right(customers) => Response.json(customers.asJson.spaces2)
            case Left(e)          => Response.status(Status.InternalServerError)
          }

      case Method.POST -> !! / "auth" / "register" => {
        ZIO.succeed(Response.ok)
      }

      case Method.POST -> !! / "auth" / "login" =>
        ZIO.succeed(Response.json("""{"token": "string"}"""))

    }

}

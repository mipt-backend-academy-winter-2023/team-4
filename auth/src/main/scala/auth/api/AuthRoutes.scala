package auth.api

import zio.ZIO
import zio.http._
import zio.http.model.{Method, Status}

object AuthRoutes {
  val app: Http[Any, Response, Request, Response] = Http.collectZIO[Request] {
    //  Method GET for testing
    case Method.GET -> !! =>
      ZIO.succeed(Response.text("The Auth service is working"))
    case request@Method.POST -> !! / "auth" / "register" =>
      val response = for {
        username <- ZIO.fromOption(
          request
            .url
            .queryParams
            .get("username")
            .flatMap(_.headOption)
        ).tapError(
          _ => ZIO.logError("There is not field username in the request body")
        )
        password <- ZIO.fromOption(
          request
            .url
            .queryParams
            .get("password")
            .flatMap(_.headOption)
        ).tapError(
          _ => ZIO.logError("There is not field password in the request body")
        )
      } yield Response.text(s"Successful user registration $username with password $password")
      response.orElseFail(Response.status(Status.BadRequest))
    case request@Method.POST -> !! / "auth" / "login" =>
      val response = for {
        username <- ZIO.fromOption(
          request
            .url
            .queryParams
            .get("username")
            .flatMap(_.headOption)
        ).tapError(
          _ => ZIO.logError("There is not field username in the request body")
        )
        password <- ZIO.fromOption(
          request
            .url
            .queryParams
            .get("password")
            .flatMap(_.headOption)
        ).tapError(
          _ => ZIO.logError("There is not field password in the request body")
        )
      } yield Response.text(s"Successful authorisation for user $username with password $password")
      response.orElseFail(Response.status(Status.BadRequest))
  }
}

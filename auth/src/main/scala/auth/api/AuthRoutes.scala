package auth.api

import zio.ZIO
import zio.http._
import zio.http.model.{Method, Status}

object AuthRoutes {

  val app: Http[Any, Response, Request, Response] = Http.collectZIO[Request] {
    case request@Method.POST -> !! / "auth" / "register" =>
      val response =
        for {
          username <- ZIO.fromOption(
            request
              .url
              .queryParams
              .get("username")
              .flatMap(_.headOption)
          ).tapError(
            _ => ZIO.logError("username not provided")
          )

          password <- ZIO.fromOption(
            request
              .url
              .queryParams
              .get("password")
              .flatMap(_.headOption)
          ).tapError(
            _ => ZIO.logError("password not provided")
          )
      } yield Response.text(s"Hello from registration, user: $username, password: $password").setStatus(Status.Created)

      response.orElseFail(Response.status(Status.BadRequest))

    case request@Method.POST -> !! / "auth" / "login" =>
      val response =
        for {
          username <- ZIO.fromOption(
            request
              .url
              .queryParams
              .get("username")
              .flatMap(_.headOption)
          ).tapError(
            _ => ZIO.logError("username not provided")
          )

          password <- ZIO.fromOption(
            request
              .url
              .queryParams
              .get("password")
              .flatMap(_.headOption)
          ).tapError(
            _ => ZIO.logError("password not provided")
          )
        } yield Response.text(s"Hello from login, $username, your token is ${Math.abs((username + "$" + password).hashCode)}").setStatus(Status.Ok)

      response.orElseFail(Response.status(Status.BadRequest))
  }

}

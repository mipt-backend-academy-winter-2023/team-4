package routing.api

import zio.ZIO
import zio.http._
import zio.http.model.{Method, Status}

object RoutingRoutes {

  val app: Http[Any, Response, Request, Response] = Http.collectZIO[Request] {
    case request@Method.POST -> !! / "route" =>
      val response =
        for {
          authToken <- ZIO.fromOption(
            request
              .url
              .queryParams
              .get("auth_token")
              .flatMap(_.headOption)
          ).tapError(
            _ => ZIO.logError("auth_token not provided")
          )

          points <- ZIO.fromOption(
            request
              .url
              .queryParams
              .get("points")
              .flatMap(_.headOption)
          ).tapError(
            _ => ZIO.logError("points not provided")
          )

        } yield Response.text(s"Hello from route, token: $authToken, points: $points").setStatus(Status.Ok)

      response.orElseFail(Response.status(Status.BadRequest))

  }

}

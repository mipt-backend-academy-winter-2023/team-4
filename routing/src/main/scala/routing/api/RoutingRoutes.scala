package routing.api

import zio.ZIO
import zio.http._
import zio.http.model.{Method, Status}

object RoutingRoutes {

  val app: Http[Any, Response, Request, Response] =
    Http.collectZIO[Request] {

      case request@Method.POST -> !! / "route" =>
        val response =
          for {
            start_point <- ZIO.fromOption(
              request
                .url
                .queryParams
                .get("start_point")
                .flatMap(_.headOption)
            ).tapError(
              _ => ZIO.logError("bad request")
            )
            end_point <- ZIO.fromOption(
              request
                .url
                .queryParams
                .get("start_point")
                .flatMap(_.headOption)
            ).tapError(
              _ => ZIO.logError("bad request")
            )
          } yield Response.text(s"$start_point --- $end_point")

        response.orElseFail(Response.status(Status.BadRequest))

    }

}

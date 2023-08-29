package routing.api

import zio.ZIO
import zio.http._
import zio.http.model.{Method, Status}

object RoutingRoutes {

  val app: Http[Any, Response, Request, Response] = Http.collectZIO[Request] {
    //  Method GET for testing
    case Method.GET -> !! =>
      ZIO.succeed(Response.text("The Routing service is working"))
    case request@Method.POST -> !! / "auth" / "components" / "schemas" / "points" =>
    val response = for {
      point_ids <- ZIO.fromOption(
          request
            .url
            .queryParams
            .get("points")
            .flatMap(_.headOption)
        ).tapError(
          _ => ZIO.logError("There is not field points in the request body")
        )
      } yield Response.text(s"Routing request for points $point_ids")
      response.orElseFail(Response.status(Status.BadRequest))
  }
}

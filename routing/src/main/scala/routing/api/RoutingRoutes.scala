package routing.api

import zio.ZIO
import zio.http._
import zio.http.model.Method

object RoutingRoutes {

  val app: Http[Any, Response, Request, Response] =
    Http.collectZIO[Request] {

      case Method.POST -> !! / "route" =>
        ZIO.succeed(Response.json("""[{"id": 0}]"""))

    }

}

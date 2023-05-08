package auth.api

import zio.ZIO
import zio.http._
import zio.http.model.Method

object AuthRoutes {

  val app: Http[Any, Response, Request, Response] =
    Http.collectZIO[Request] {

      case Method.POST -> !! / "auth" / "register" =>
        ZIO.succeed(Response.ok)

      case Method.POST -> !! / "auth" / "login" =>
        ZIO.succeed(Response.json("""{"token": "string"}"""))

    }

}

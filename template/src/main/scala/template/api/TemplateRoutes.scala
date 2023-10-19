package template.api

import zio.ZIO
import zio.http._
import zio.http.model.{Method, Status}

object TemplateRoutes {

  val app: Http[Any, Response, Request, Response] =
    Http.collectZIO[Request] {

      case Method.GET -> !! / "hello" =>
        ZIO.succeed(Response.text("Hello world!"))

      case request @ Method.POST -> !! / "echo" =>
        val response =
          for {
            input <- ZIO.fromOption(
              request
                .url
                .queryParams
                .get("input")
                .flatMap(_.headOption)
            ).tapError(_ => ZIO.logError("bad request"))
          } yield Response.text(input)

        response.orElseFail(Response.status(Status.BadRequest))

    }

}

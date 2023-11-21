package routing.clients

import cats.implicits.{toFlatMapOps, toFunctorOps}
import sttp.client4.testing.SyncBackendStub.send
import sttp.client4.{UriContext, asString, basicRequest}
import zio._

import io.circe.parser._
import io.circe.generic.auto._

class JamClient(baseUrl: String) {
  def getJam(id: Int): ZIO[Any, Throwable, Int] = {
    val request = basicRequest
      .get(uri"$baseUrl/jam/$id")
      .response(asString)

    send(request).flatMap { response =>
      val jsonResponse = response.body.getOrElse("")
      val jamValue = for {
        json <- parse(jsonResponse)
        jam <- json.hcursor.downField("jam_value").as[Int]
      } yield jam

      ZIO.fromEither(jamValue)
    }

  }

}

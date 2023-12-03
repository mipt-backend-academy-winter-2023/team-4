package routing.clients

import cats.implicits.toFlatMapOps
import core.config.JamClientConfig
import sttp.client4._
import zio._
import io.circe.parser._
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class JamResponse(jam_value: Int)

object JamResponse {
  // Generate the decoder instance using Circe's automatic derivation
  implicit val decoder: Decoder[JamResponse] = deriveDecoder
}

class JamClient(baseUrl: String) {
  def getJam(id: Int): ZIO[Any, Throwable, Int] = {
    DefaultSyncBackend().send(basicRequest
      .get(uri"$baseUrl/jam/$id")
      .response(asStringAlways)).flatMap { response =>
      ZIO.fromEither(for {
        json <- parse(response.body)
        resp <- json.as[JamResponse]
      } yield resp.jam_value)
    }
  }
}

object JamClient {
  val live: ZLayer[JamClientConfig, Nothing, JamClient] =
    ZLayer.fromZIO(
      for {
        config <- ZIO.service[JamClientConfig]
        client = new JamClient(config.url)
      } yield client
    )
}

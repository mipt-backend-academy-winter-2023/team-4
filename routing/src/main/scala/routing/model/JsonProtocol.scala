package routing.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import zio.schema.{DeriveSchema, Schema}

object JsonProtocol {
  implicit val customerDecoder: Decoder[Vertex] = deriveDecoder
  implicit val customerEncoder: Encoder[Vertex] = deriveEncoder

  implicit val customerSchema: Schema[Vertex] = DeriveSchema.gen[Vertex]
}
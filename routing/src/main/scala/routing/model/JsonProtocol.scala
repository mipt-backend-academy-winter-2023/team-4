package routing.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import zio.schema.{DeriveSchema, Schema}

object JsonProtocol {
  implicit val customerNodeDecoder: Decoder[NodeRow] = deriveDecoder
  implicit val customerNodeEncoder: Encoder[NodeRow] = deriveEncoder
  implicit val customerEdgeDecoder: Decoder[EdgeRow] = deriveDecoder
  implicit val customerEdgeEncoder: Encoder[EdgeRow] = deriveEncoder

  implicit val customerNodeSchema: Schema[NodeRow] = DeriveSchema.gen[NodeRow]
  implicit val customerEdgeSchema: Schema[EdgeRow] = DeriveSchema.gen[EdgeRow]
}
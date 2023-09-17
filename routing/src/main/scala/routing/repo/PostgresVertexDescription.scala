package routing.repo

import routing.model.Vertex
import zio.schema.DeriveSchema
import zio.sql.postgresql.PostgresJdbcModule

trait PostgresVertexDescription extends PostgresJdbcModule {

  implicit val customerSchema = DeriveSchema.gen[Vertex]

  val vertexes = defineTable[Vertex]

  val (id, address) = vertexes.columns
}
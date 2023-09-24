package routing.repo

import routing.model.EdgeRow
import zio.schema.{DeriveSchema, Schema}
import zio.sql.macros.TableSchema
import zio.sql.postgresql.PostgresJdbcModule

trait EdgesTableDescription extends PostgresJdbcModule {

  implicit val customerSchema = DeriveSchema.gen[EdgeRow]

  val edges = defineTable[EdgeRow]

  val (fId, fName, fFirstNode, fSecondNode) = edges.columns
}
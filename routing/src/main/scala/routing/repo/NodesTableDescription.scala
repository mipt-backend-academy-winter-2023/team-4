package routing.repo

import routing.model.NodeRow
import zio.schema.{DeriveSchema, Schema}
import zio.sql.macros.TableSchema
import zio.sql.postgresql.PostgresJdbcModule

trait NodesTableDescription extends PostgresJdbcModule {

  implicit val customerSchema = DeriveSchema.gen[NodeRow]

  val nodes = defineTable[NodeRow]

  val (fId, fName, fLat, fLon) = nodes.columns
}
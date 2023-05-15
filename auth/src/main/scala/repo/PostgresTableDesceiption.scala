package repo

import model.User
import zio.schema.{DeriveSchema, Schema}
import zio.sql.macros.TableSchema
import zio.sql.postgresql.PostgresJdbcModule

trait PostgresTableDescription extends PostgresJdbcModule {

  implicit val userSchema = DeriveSchema.gen[User]

  val users = defineTable[User]

  val (userId, login, passwordHash) = users.columns
}

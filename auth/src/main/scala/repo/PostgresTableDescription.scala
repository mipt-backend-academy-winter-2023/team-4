package repo

import model.User
import zio.schema.DeriveSchema
import zio.sql.postgresql.PostgresJdbcModule

trait PostgresTableDescription extends PostgresJdbcModule {
  implicit val userSchema = DeriveSchema.gen[User]

  val users = defineTable[User]("users")

  val (username, password) = users.columns
}
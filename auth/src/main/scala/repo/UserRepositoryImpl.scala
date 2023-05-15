package repo

import model.User
import zio.{ZIO, ZLayer}
import zio.sql.ConnectionPool
import zio.stream.ZStream

final class UserRepositoryImpl(
    pool: ConnectionPool
) extends UserRepository
    with PostgresTableDescription {

  val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer
      .make[SqlDriver](
        SqlDriver.live,
        ZLayer.succeed(pool)
      )

  override def findAll(): ZStream[Any, Throwable, User] = {
    val selectAll =
      select(userId, login, passwordHash).from(users)

    ZStream.fromZIO(
      ZIO.logInfo(s"Query to execute findAll is ${renderRead(selectAll)}")
    ) *>
      execute(selectAll.to((User.apply _).tupled))
        .provideSomeLayer(driverLayer)
  }

  override def add(customer: User): ZIO[Any, Throwable, Unit] = {
    val query =
      insertInto(users)(userId, login, passwordHash)
        .values(
          (
            customer.id,
            customer.login,
            customer.pwdhash
          )
        )

    ZIO.logInfo(s"Query to insert customer is ${renderInsert(query)}") *>
      execute(query)
        .provideSomeLayer(driverLayer)
        .unit
  }

  override def updateCustomer(
      customer: User
  ): ZIO[Any, Throwable, Unit] = {
    val query =
      update(users)
        .set(login, customer.login)
        .set(passwordHash, customer.pwdhash)
        .where(userId === customer.id)
    // .where(Expr.Relational(customerId, customer.id, RelationalOp.Equals))

    ZIO.logInfo(s"Query to update customer is ${renderUpdate(query)}") *>
      execute(query)
        .provideSomeLayer(driverLayer)
        .unit
  }

  override def delete(id: Int): ZIO[Any, Throwable, Unit] = {
    val delete = deleteFrom(users).where(userId === id)
    execute(delete)
      .provideSomeLayer(driverLayer)
      .unit
  }
}

object UserRepositoryImpl {
  val live: ZLayer[ConnectionPool, Throwable, UserRepository] =
    ZLayer.fromFunction(new UserRepositoryImpl(_))
}

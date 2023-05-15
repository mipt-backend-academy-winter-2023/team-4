package auth.repo

import auth.model.User
import zio.{ZIO, ZLayer}
import zio.sql.ConnectionPool
import zio.stream.ZStream
import java.security.MessageDigest
import java.util.Base64.getEncoder

final class UserRepositoryImpl(pool: ConnectionPool) extends PostgresTableDescription with UserRepository {
  val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer.make[SqlDriver](SqlDriver.live, ZLayer.succeed(pool))

  private def encodePassword(password: String): String = {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(password.getBytes("UTF-8"))
    getEncoder.encodeToString(bytes)
  }

  override def findUser(user: User): ZStream[Any, Throwable, User] = {
    val selectUser = select(fUsername, fPassword)
      .from(users)
      .where(fUsername === user.username && fPassword === encodePassword(user.password))
    ZStream.fromZIO(
      ZIO.logInfo(s"Query to execute findUser is ${renderRead(selectUser)}")
    ) *> execute(selectUser.to((User.apply _).tupled)).provideSomeLayer(driverLayer)
  }

  override def add(user: User): ZIO[UserRepository, Throwable, Unit] = {
    val query =
      insertInto(users)(fUsername, fPassword)
        .values(
          (
            user.username,
            encodePassword(user.password)
          )
        )

    ZIO.logInfo(s"Query to insert user is ${renderInsert(query)}") *>
      execute(query)
        .provideSomeLayer(driverLayer)
        .unit
  }
}

object UserRepositoryImpl {
  val live: ZLayer[ConnectionPool, Throwable, UserRepository] = ZLayer.fromFunction(new UserRepositoryImpl(_))
}
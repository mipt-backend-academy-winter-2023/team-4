package routing.repo

import zio.sql.ConnectionPool
import zio.stream.ZStream
import zio.{ZIO, ZLayer}

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

  private def checkUserByName(username: String): ZStream[Any, Throwable, User] = {
    val selectUser = select(fUsername, fPassword)
      .from(users)
      .where(fUsername === username)
    ZStream.fromZIO(
      ZIO.logInfo(s"Query to execute checkUserByName is ${renderRead(selectUser)}")
    ) *> execute(selectUser.to((User.apply _).tupled)).provideSomeLayer(driverLayer)
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
    checkUserByName(user.username).runCollect.map(_.toArray).either.flatMap {
      case Right(arr) => arr match {
        case Array() => {
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
        case _ => ZIO.fail(new IllegalAccessException("User exists"))
      }
      case Left(_) => ZIO.fail(new Exception("Error"))
    }
  }
}

object UserRepositoryImpl {
  val live: ZLayer[ConnectionPool, Throwable, UserRepository] = ZLayer.fromFunction(new UserRepositoryImpl(_))
}
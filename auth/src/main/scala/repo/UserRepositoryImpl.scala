package repo

import model.User
import java.security.MessageDigest
import zio.{ZIO, ZLayer}
import zio.sql.ConnectionPool
import zio.stream.ZStream

final class UserRepositoryImpl(pool: ConnectionPool) extends UserRepository
  with PostgresTableDescription {
  private def hash(password: String): String = {
    val md = MessageDigest.getInstance("SHA-256")
    val hash = md.digest(password.getBytes("UTF-8"))
    hash.map("%02x".format(_)).mkString
  }

  val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer
      .make[SqlDriver](
        SqlDriver.live,
        ZLayer.succeed(pool)
      )

  override def addUser(user: User): zio.ZIO[repo.UserRepository, Throwable, Unit] = {
    getByUsername(user).runCollect.map(_.toArray).either.flatMap {
      case Right(arr) =>
        if (arr.length > 0) ZIO.fail(new Exception("User exists"))
        else {
          val query =
            insertInto(users)(username, password)
              .values(
                (
                  user.username,
                  hash(user.password)
                )
              )
          ZIO.logInfo(s"Query to addUser is ${renderInsert(query)}") *>
            execute(query)
              .provideSomeLayer(driverLayer)
              .unit
        }
      case Left(_) => ZIO.fail(new Exception("Query error"))
    }
  }

  override def getByUsername(user: User): zio.stream.ZStream[UserRepository, Throwable, model.User] = {
    val selectAll = select(username, password)
      .from(users)
      .where(username === user.username)
    ZStream.fromZIO(
      ZIO.logInfo(s"Query to getByUsername is ${renderRead(selectAll)}")
    ) *> execute(selectAll.to((User.apply _).tupled)).provideSomeLayer(driverLayer)
  }

  override def login(user: User): zio.ZIO[repo.UserRepository, Exception, Unit] = {
    val selectAll = select(username, password)
      .from(users)
      .where(username === user.username && password === hash(user.password))
    val result = ZStream.fromZIO(
      ZIO.logInfo(s"Query to getUser is ${renderRead(selectAll)}")
    ) *> execute(selectAll.to((User.apply _).tupled)).provideSomeLayer(driverLayer)
    result.runCollect.map(_.toArray).either.flatMap {
      case Right(arr) =>
        if (arr.length > 0) ZIO.succeed()
        else ZIO.fail(new IllegalAccessException("Access denied"))
      case Left(_) => ZIO.fail(new Exception("Query error"))
    }
  }
}

object UserRepositoryImpl {
  val live: ZLayer[ConnectionPool, Throwable, UserRepository] =
    ZLayer.fromFunction(new UserRepositoryImpl(_))
}

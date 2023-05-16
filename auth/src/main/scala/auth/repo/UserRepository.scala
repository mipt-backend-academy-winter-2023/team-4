package auth.repo

import auth.model.User
import zio.ZIO
import zio.stream.ZStream

trait UserRepository {
  def findUser(user: User): ZStream[UserRepository, Throwable, User]

  def add(user: User): ZIO[UserRepository, Throwable, Unit]
}

object UserRepository {
  def findUser(user: User): ZStream[UserRepository, Throwable, User] =
    ZStream.serviceWithStream[UserRepository](_.findUser(user))

  def add(user: User): ZIO[UserRepository, Throwable, Unit] =
    ZIO.serviceWithZIO[UserRepository](_.add(user))
}

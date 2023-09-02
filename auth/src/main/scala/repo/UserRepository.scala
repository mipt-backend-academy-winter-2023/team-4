package repo

import model.User
import zio.ZIO
import zio.stream.ZStream

trait UserRepository {
  def addUser(user: User): zio.ZIO[repo.UserRepository,Throwable,Unit]

  def getByUsername(user: User): zio.stream.ZStream[UserRepository, Throwable, model.User]

  def login(user: User): zio.ZIO[repo.UserRepository,Exception,Unit]
}


object UserRepository {
  def addUser(user: User): zio.ZIO[repo.UserRepository,Throwable,Unit] =
    ZIO.serviceWithZIO[UserRepository](_.addUser(user))

  def getByUsername(user: User): zio.stream.ZStream[UserRepository, Throwable, model.User] =
    ZStream.serviceWithStream[UserRepository](_.getByUsername(user))

  def login(user: User): zio.ZIO[repo.UserRepository,Exception,Unit] =
    ZIO.serviceWithZIO[UserRepository](_.login(user))
}

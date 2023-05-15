package repo

import model.User
import zio.ZIO
import zio.stream.ZStream

trait UserRepository {

  def findAll(): ZStream[Any, Throwable, User]

  def add(customer: User): ZIO[Any, Throwable, Unit]

  def updateCustomer(customer: User): ZIO[Any, Throwable, Unit]

  def delete(id: Int): ZIO[Any, Throwable, Unit]
}

object UserRepository {
  def findAll(): ZStream[UserRepository, Throwable, User] =
    ZStream.serviceWithStream[UserRepository](_.findAll())

  def add(customer: User): ZIO[UserRepository, Throwable, Unit] =
    ZIO.serviceWithZIO[UserRepository](_.add(customer))

  def update(customer: User): ZIO[UserRepository, Throwable, Unit] =
    ZIO.serviceWithZIO[UserRepository](_.updateCustomer(customer))

  def delete(id: Int): ZIO[UserRepository, Throwable, Unit] =
    ZIO.serviceWithZIO[UserRepository](_.delete(id))
}

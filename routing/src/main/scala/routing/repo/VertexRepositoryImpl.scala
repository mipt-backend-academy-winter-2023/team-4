package routing.repo

import routing.model.Vertex
import zio.sql.ConnectionPool
import zio.stream.ZStream
import zio.{ZIO, ZLayer}

import java.security.MessageDigest
import java.util.Base64.getEncoder

final class VertexRepositoryImpl(pool: ConnectionPool) extends PostgresVertexDescription with VertexRepository {
  val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer.make[SqlDriver](SqlDriver.live, ZLayer.succeed(pool))

  override def findAll():  ZStream[VertexRepository, Throwable, Vertex] = {
    ZStream.empty
  }

  override def add(vertex: Vertex): ZIO[VertexRepository, Throwable, Unit] = {
    ZIO.succeed(())
  }
}

object VertexRepositoryImpl {
  val live: ZLayer[ConnectionPool, Throwable, VertexRepository] = ZLayer.fromFunction(new VertexRepositoryImpl(_))
}
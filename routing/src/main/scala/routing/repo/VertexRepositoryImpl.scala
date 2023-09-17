package routing.repo

import routing.model.Vertex
import zio.sql.ConnectionPool
import zio.stream.ZStream
import zio.{ZIO, ZLayer}

final class VertexRepositoryImpl(pool: ConnectionPool) extends PostgresVertexDescription with VertexRepository {
  val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer.make[SqlDriver](SqlDriver.live, ZLayer.succeed(pool))

  override def getAll():  ZStream[VertexRepository, Throwable, Vertex] = {
    val selectVertexes = select(id, address)
      .from(vertexes)
    ZStream.fromZIO(
      ZIO.logInfo(s"Query to execute findUser is ${renderRead(selectVertexes)}")
    ) *> execute(selectVertexes.to((Vertex.apply _).tupled)).provideSomeLayer(driverLayer)
  }

  override def add(vertex: Vertex): ZIO[VertexRepository, Throwable, Unit] = {
    ZIO.succeed(())
  }
}

object VertexRepositoryImpl {
  val live: ZLayer[ConnectionPool, Throwable, VertexRepository] = ZLayer.fromFunction(new VertexRepositoryImpl(_))
}
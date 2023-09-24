package routing.repo

import routing.model.EdgeRow
import routing.repo.EdgesTableDescription
import zio.{ZIO, ZLayer}
import zio.sql.ConnectionPool
import zio.stream.ZStream
import java.security.MessageDigest
import java.util.Base64.getEncoder

final class EdgeImpl(pool: ConnectionPool) extends EdgesTableDescription with Edges {
  val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer.make[SqlDriver](SqlDriver.live, ZLayer.succeed(pool))

  override def getAll(): ZStream[Any, Throwable, EdgeRow] = {
    val selectAll = select(fId, fName, fFirstNode, fSecondNode).from(edges)
    ZStream.fromZIO(
      ZIO.logInfo(s"Query to execute getAll is ${renderRead(selectAll)}")
    ) *> execute(selectAll.to((EdgeRow.apply _).tupled)).provideSomeLayer(driverLayer)
  }
}

object EdgeImpl {
  val live: ZLayer[ConnectionPool, Throwable, Edges] = ZLayer.fromFunction(new EdgeImpl(_))
}
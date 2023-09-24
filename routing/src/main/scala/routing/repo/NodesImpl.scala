package routing.repo

import routing.model.NodeRow
import routing.repo.NodesTableDescription
import zio.{ZIO, ZLayer}
import zio.sql.ConnectionPool
import zio.stream.ZStream
import java.security.MessageDigest
import java.util.Base64.getEncoder

final class NodeImpl(pool: ConnectionPool) extends NodesTableDescription with Nodes {
  val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer.make[SqlDriver](SqlDriver.live, ZLayer.succeed(pool))

  override def getAll(): ZStream[Nodes, Throwable, NodeRow] = {
    val selectAll = select(fId, fName, fLat, fLon)
      .from(nodes)
    ZStream.fromZIO(
      ZIO.logInfo(s"Query to execute getAll is ${renderRead(selectAll)}")
    ) *> execute(selectAll.to((NodeRow.apply _).tupled)).provideSomeLayer(driverLayer)
  }
}

object NodeImpl {
  val live: ZLayer[ConnectionPool, Throwable, Nodes] = ZLayer.fromFunction(new NodeImpl(_))
}
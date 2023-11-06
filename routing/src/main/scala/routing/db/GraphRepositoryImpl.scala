package routing.db

import routing.models.graph.Edge.Street
import routing.models.graph.Node.{Building, Intersection}
import zio.ZLayer
import zio.sql.ConnectionPool
import zio.stream.ZStream

final class GraphRepositoryImpl(pool: ConnectionPool) extends PostgresTableDescription
    with GraphRepository {

  private val driverLayer: ZLayer[Any, Nothing, SqlDriver] =
    ZLayer.make[SqlDriver](SqlDriver.live, ZLayer.succeed(pool))

  override def selectBuildings: ZStream[GraphRepository, Throwable, Building] = {
    val selectQuery =
      select(buildingId, buildingName, buildingLat, buildingLong)
        .from(buildings)

    execute(selectQuery.to((Building.apply _).tupled)).provideSomeLayer(driverLayer)
  }

  override def selectIntersections: ZStream[GraphRepository, Throwable, Intersection] = {
    val selectQuery =
      select(intersectionId, intersectionLat, intersectionLong)
        .from(intersections)

    execute(selectQuery.to((Intersection.apply _).tupled)).provideSomeLayer(driverLayer)
  }

  override def selectStreets: ZStream[GraphRepository, Throwable, Street] = {
    val selectQuery =
      select(streetId, streetName, source, destination)
        .from(streets)

    execute(selectQuery.to((Street.apply _).tupled)).provideSomeLayer(driverLayer)
  }
}

object GraphRepositoryImpl {

  val live: ZLayer[ConnectionPool, Throwable, GraphRepository] =
    ZLayer.fromFunction(new GraphRepositoryImpl(_))

}

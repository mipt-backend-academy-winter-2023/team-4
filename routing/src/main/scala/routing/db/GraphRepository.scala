package routing.db

import routing.models.graph.Edge.Street
import routing.models.graph.Node.{Building, Intersection}
import zio.stream.ZStream

trait GraphRepository {
  def selectBuildings: ZStream[GraphRepository, Throwable, Building]
  def selectIntersections: ZStream[GraphRepository, Throwable, Intersection]
  def selectStreets: ZStream[GraphRepository, Throwable, Street]
}

object GraphRepository {

  def selectBuildings: ZStream[GraphRepository, Throwable, Building] =
    ZStream.serviceWithStream[GraphRepository](_.selectBuildings)

  def selectIntersections: ZStream[GraphRepository, Throwable, Intersection] =
    ZStream.serviceWithStream[GraphRepository](_.selectIntersections)

  def selectStreets: ZStream[GraphRepository, Throwable, Street] =
    ZStream.serviceWithStream[GraphRepository](_.selectStreets)
}

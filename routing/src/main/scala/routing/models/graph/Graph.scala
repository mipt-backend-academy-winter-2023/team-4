package routing.models.graph

import routing.db.GraphRepository
import zio.{ZIO, ZLayer}
import zio.stream.ZStream

case class Graph(nodes: Vector[Node], edges: Vector[Edge]) {

  def successors(node: Node): Vector[Node] = {
    // assume ids are distinct for all nodes
    val successorDestination = edges.filter(_.source == node.id).map(_.destination)
    val successorSource = edges.filter(_.destination == node.id).map(_.source)

    val neighbourIds = successorDestination ++ successorSource

    nodes.filter(node => neighbourIds.contains(node.id))
  }

  def findEdge(node1: Node, node2: Node): Option[Edge] = {
    edges.find(e =>
      e.source == node1.id && e.destination == node2.id ||
        e.destination == node1.id && e.source == node2.id
    )
  }

}

object Graph {

  private def loadGraph: ZIO[GraphRepository, Throwable, Graph] = {

    def toVector[A](stream: ZStream[GraphRepository, Throwable, A])
        : ZIO[GraphRepository, Throwable, Vector[A]] = {
      stream.runCollect.map(_.toVector)
    }

    for {
      buildings <- toVector(GraphRepository.selectBuildings)
      intersections <- toVector(GraphRepository.selectIntersections)
      streets <- toVector(GraphRepository.selectStreets)
    } yield Graph(buildings ++ intersections, streets)
  }

  def graphLayer: ZLayer[GraphRepository, Throwable, Graph] =
    ZLayer.fromZIO {
      for {
        _ <- ZIO.logInfo("Preloading graph")
        graph <- loadGraph
      } yield graph
    }

}

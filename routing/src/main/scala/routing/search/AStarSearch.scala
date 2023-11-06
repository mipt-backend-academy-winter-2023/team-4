package routing.search

import routing.models.graph.{Edge, Graph, Node}
import routing.services.Converter

import scala.annotation.tailrec
import scala.collection.immutable.Queue

final case class NodeWrapper(node: Node, f: Double, g: Double)

final case class GraphWrapper(
    graph: Graph,
    nodes: Vector[NodeWrapper],
    edges: Vector[Edge]
) {

  def successors(node: Node): Vector[NodeWrapper] = {
    val successorDestination = edges.filter(_.source == node.id).map(_.destination)
    val successorSource = edges.filter(_.destination == node.id).map(_.source)

    val neighbourIds = successorDestination ++ successorSource

    nodes.filter(wrapper => neighbourIds.contains(wrapper.node.id))
  }
}

trait AStarSearch {
  def search(start: Node, finish: Node): Vector[Edge]
}

final class AStarSearchImpl(val graphWrapper: GraphWrapper, val converter: Converter)
    extends AStarSearch {

  private def graph: Graph = graphWrapper.graph

  override def search(start: Node, finish: Node): Vector[Edge] = {
    val openSet = Queue(NodeWrapper(start, f = 0, g = 0))

    @tailrec
    def loop(
        openSet: Queue[NodeWrapper],
        parentMap: Map[NodeWrapper, NodeWrapper]
    ): Map[NodeWrapper, NodeWrapper] = {
      if (openSet.isEmpty) {
        Map.empty
      } else {
        val (current, openSetTail) = openSet.sortBy(_.f).dequeue

        if (current.node == finish) {
          parentMap
        } else {

          val successors = graphWrapper.successors(current.node)

          val (newOpenSet, newParentMap) =
            loopOverSuccessors(current, successors.head, successors.tail, openSetTail, parentMap)

          loop(newOpenSet, newParentMap)
        }
      }
    }

    def loopOverSuccessors(
        current: NodeWrapper,
        successor: NodeWrapper,
        successors: Vector[NodeWrapper],
        openSet: Queue[NodeWrapper],
        parentMap: Map[NodeWrapper, NodeWrapper]
    ): (Queue[NodeWrapper], Map[NodeWrapper, NodeWrapper]) = {
      val successorG = current.g + edgeCost(current.node, successor.node)

      if (successorG < successor.g) {
        val newSuccessor = successor.copy(
          f = successorG + h(successor.node, finish),
          g = successorG
        )

        val newOpenSet =
          if (openSet.contains(successor))
            openSet
          else
            openSet.enqueue(newSuccessor)

        val newPath = parentMap + (successor -> current)

        loopOverNextSuccessor(current, successors, newOpenSet, newPath)
      } else {
        loopOverNextSuccessor(current, successors, openSet, parentMap)
      }
    }

    def loopOverNextSuccessor(
        current: NodeWrapper,
        successors: Vector[NodeWrapper],
        openSet: Queue[NodeWrapper],
        parentMap: Map[NodeWrapper, NodeWrapper]
    ): (Queue[NodeWrapper], Map[NodeWrapper, NodeWrapper]) = {
      if (successors.isEmpty) {
        (openSet, parentMap)
      } else {
        val (newSuccessor, successorsTail) = (successors.head, successors.tail)

        loopOverSuccessors(current, newSuccessor, successorsTail, openSet, parentMap)
      }
    }

    reconstructPath(
      loop(openSet, Map.empty),
      start,
      finish
    )
  }

  private def reconstructPath(
      parentMap: Map[NodeWrapper, NodeWrapper],
      start: Node,
      finish: Node
  ): Vector[Edge] = {

    @tailrec
    def loop(path: Vector[Edge], current: NodeWrapper): Vector[Edge] = {
      if (parentMap.isEmpty) {
        Vector.empty
      } else {
        parentMap.find(_._1.node == current.node) match {
          case None => path
          case Some((_, node)) =>
            println(s"parentMap.get(${current.node.id}): ${node.node.id}")
            val edge = graph.findEdge(current.node, node.node)
            val newPath = path ++ edge

            if (node.node == start) {
              newPath
            } else {
              loop(newPath, node)
            }
        }
      }
    }

    loop(Vector.empty, NodeWrapper(finish, 0, 0)).reverse
  }

  /** cost to get to the node */
  private def edgeCost(current: Node, node: Node): Double =
    converter.calcDistance(current.geoPosition, node.geoPosition)

  /** estimated cost to get from the node to the finish */
  private def h(node: Node, finish: Node): Double =
    converter.calcDistance(node.geoPosition, finish.geoPosition)
}

object AStarSearch {
  def apply(graph: Graph, converter: Converter): AStarSearch = {
    val graphWrapper = GraphWrapper(
      graph,
      nodes = graph.nodes.map { n =>
        NodeWrapper(n, Double.PositiveInfinity, Double.PositiveInfinity)
      },
      edges = graph.edges
    )

    new AStarSearchImpl(graphWrapper, converter)
  }
}

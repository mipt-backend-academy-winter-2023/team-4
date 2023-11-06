package routing.models.graph

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import routing.models.graph.Edge.Street
import routing.models.graph.Node.Building

class GraphSpec extends AnyFlatSpec with Matchers {

  "successors" should "return list of node's neighbours" in {
    val nodes = Vector(
      Building(id = 1, name = None, lat = 1, long = 1),
      Building(id = 2, name = None, lat = 1, long = 1),
      Building(id = 3, name = None, lat = 1, long = 1)
    )
    val edges = Vector(
      Street(id = 1, name = "a", source = 1, destination = 3),
      Street(id = 2, name = "b", source = 1, destination = 2)
    )

    val graph = Graph(nodes, edges)

    graph.successors(nodes(0)) shouldBe Vector(nodes(1), nodes(2))
    graph.successors(nodes(1)) shouldBe Vector(nodes(0))
  }

  "findEdge" should "return the edge between two nodes" in {
    val nodes = Vector(
      Building(id = 1, name = None, lat = 1, long = 1),
      Building(id = 2, name = None, lat = 1, long = 1),
      Building(id = 3, name = None, lat = 1, long = 1)
    )
    val edges = Vector(
      Street(id = 1, name = "a", source = 1, destination = 3),
      Street(id = 2, name = "b", source = 1, destination = 2)
    )

    val graph = Graph(nodes, edges)

    graph.findEdge(nodes(0), nodes(1)) shouldBe Some(edges(1))
    graph.findEdge(nodes(0), nodes(2)) shouldBe Some(edges(0))
    graph.findEdge(nodes(1), nodes(2)) shouldBe None
  }

}

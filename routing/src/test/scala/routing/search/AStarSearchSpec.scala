package routing.search

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import routing.models.graph.Edge.Street
import routing.models.graph.Node.Building
import routing.models.graph.{Edge, Graph}
import routing.services.ConverterImpl

class AStarSearchSpec extends AnyFlatSpec with Matchers {

  "search" should "find shortest path from start to finish" in {
    val nodes = Vector(
      Building(1, Some("main building"), 55.929456, 37.518310),
      Building(2, Some("buffet"), 55.930683, 37.520558),
      Building(3, Some("stadium"), 55.927514, 37.524811)
    )
    val edges = Vector(
      Street(1, "institutskiy per.", 1, 2),
      Street(2, "pervomaiskaya", 3, 2)
    )

    val graph = Graph(
      nodes = nodes,
      edges = edges
    )

    val converter = new ConverterImpl
    val search = AStarSearch(graph, converter)

    (1 < Double.PositiveInfinity) shouldBe true
    search.search(nodes.head, nodes.last) shouldBe Vector(edges(0), edges(1))
  }

}

package routing.api

import io.circe.{Json, jawn}
import nl.vroste.rezilience.CircuitBreaker.{CircuitBreakerOpen, WrappedError}
import routing.clients.{JamCache, JamClient, MyCircuitBreaker}
import routing.db.GraphRepository
import routing.models.errors.RouteFindError
import routing.models.graph.{Edge, Graph}
import routing.search.AStarSearch
import routing.services.ConverterImpl
import zio._
import zio.http._
import zio.http.model.{Method, Status}

case class RoutingRoutes(graph: Graph) {

  def app: URIO[GraphRepository with MyCircuitBreaker with Server, Nothing] = Server.serve(routes)

  val jamClient = new JamClient("http://jams:8080")
  val jamCache = new JamCache()

  private def routes: HttpApp[GraphRepository with MyCircuitBreaker, Response] =
    Http.collectZIO[Request] {
      case request @ Method.POST -> !! / "route" / "find" =>
        (for {
          bodyStr <- request.body.asString
          path <- ZIO.fromEither(jawn.decode[Vector[Long]](bodyStr)).tapError(logError)
          _ <- logInfo(path.toString())

          jamKey = path.length
          jam <- MyCircuitBreaker.run(jamClient.getJam(jamKey)).map(value => {
            jamCache.update(jamKey, value)
            value
          }).catchAll {
            case CircuitBreakerOpen => jamCache.getByKey(jamKey)
            case WrappedError(error) =>
              ZIO.logError(s"Got error from jam client ${error.toString}")
              ZIO.fail(error)
          }
          _ <- logInfo(s"Finding route from nodeId ${path.headOption} to ${path.lastOption}")
          edges <- calculatePath(graph, path)
            .tap(e => logInfo(e.toString()))
        } yield (jam, edges)).either.map(handle)
    }

  private def calculatePath(graph: Graph, path: Vector[Long]): Task[Vector[Edge]] = {
    val converter = new ConverterImpl
    val searchAlgorithm = AStarSearch(graph, converter)

    for {
      firstNode <- ZIO.getOrFailWith(RouteFindError.InvalidInput)(path.headOption)
      lastNode <- ZIO.getOrFailWith(RouteFindError.InvalidInput)(path.lastOption)
      _ <- ZIO.logDebug((firstNode, lastNode).toString())

      start <- ZIO.getOrFailWith(RouteFindError.NoSuchNode)(graph.nodes.find(_.id == firstNode))
      finish <- ZIO.getOrFailWith(RouteFindError.NoSuchNode)(graph.nodes.find(_.id == lastNode))
      _ <- ZIO.logDebug((start, finish).toString())
    } yield searchAlgorithm.search(start, finish)
  }

  private def handle(response: Either[Throwable, (Int, Vector[Edge])]): Response =
    response match {
      case Right((jam, edges)) => Response.json(
          Json.obj(
            "jamValue" -> Json.fromInt(jam),
            "route" -> Json.fromString(showPath(edges))
          ).toString()
        )
      case Left(_) => Response.status(Status.InternalServerError)
    }

  private def showPath(edges: Vector[Edge]): String = {
    val list = edges.map(e => s"${e.id}: ${e.source} -> ${e.destination}")

    list.toString()
  }

  private def logInfo(message: String): UIO[Unit] = ZIO.logInfo(message)
  private def logError(throwable: Throwable): UIO[Unit] = ZIO.logError(throwable.getMessage)

}

object RoutingRoutes {
  val layer: ZLayer[Graph, Nothing, RoutingRoutes] = ZLayer.fromFunction(RoutingRoutes(_))
}

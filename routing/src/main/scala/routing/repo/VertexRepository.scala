package routing.repo

import routing.model.Vertex
import zio.ZIO
import zio.stream.ZStream

trait VertexRepository {
  def getAll(): ZStream[VertexRepository, Throwable, Vertex]

  def add(vertex: Vertex): ZIO[VertexRepository, Throwable, Unit]
}

object VertexRepository {
  def getAll(): ZStream[VertexRepository, Throwable, Vertex] =
    ZStream.serviceWithStream[VertexRepository](_.getAll())

  def add(vertex: Vertex): ZIO[VertexRepository, Throwable, Unit] =
    ZIO.serviceWithZIO[VertexRepository](_.add(vertex))
}

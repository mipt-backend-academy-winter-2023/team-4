package routing.repo

import routing.model.EdgeRow
import zio.ZIO
import zio.stream.ZStream

trait Edges {
  def getAll(): ZStream[Edges, Throwable, EdgeRow]
}

object Edges {
  def getAll(): ZStream[Edges, Throwable, EdgeRow] = ZStream.serviceWithStream[Edges](_.getAll())
}
package routing.repo

import routing.model.NodeRow
import zio.ZIO
import zio.stream.ZStream

trait Nodes {
  def getAll(): ZStream[Nodes, Throwable, NodeRow]
}

object Nodes {
  def getAll(): ZStream[Nodes, Throwable, NodeRow] = ZStream.serviceWithStream[Nodes](_.getAll())
}
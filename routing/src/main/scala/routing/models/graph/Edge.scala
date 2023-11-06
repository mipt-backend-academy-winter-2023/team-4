package routing.models.graph

sealed trait Edge {
  def id: Long
  def source: Long
  def destination: Long
}

object Edge {

  final case class Street(id: Long, name: String, source: Long, destination: Long) extends Edge

}

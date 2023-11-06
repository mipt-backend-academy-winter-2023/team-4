package routing.models.graph

sealed trait Node {
  def id: Long
  def lat: Double
  def long: Double

  def geoPosition: Coordinates = Coordinates(lat, long)
}

object Node {

  final case class Building(
      id: Long,
      name: Option[String],
      lat: Double,
      long: Double
  ) extends Node

  final case class Intersection(
      id: Long,
      lat: Double,
      long: Double
  ) extends Node

}

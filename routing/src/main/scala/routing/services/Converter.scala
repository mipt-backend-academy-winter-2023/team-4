package routing.services

import routing.models.graph.Coordinates

trait Converter {
  def calcDistance(point1: Coordinates, point2: Coordinates): Double
}

final class ConverterImpl extends Converter {
  override def calcDistance(point1: Coordinates, point2: Coordinates): Double = {
    val (lat1, long1) = (point1.lat, point1.lat)
    val (lat2, long2) = (point2.lat, point2.lat)

    val latDiff = degreeToRadians(lat1 - lat2)
    val longDiff = degreeToRadians(long1 - long2)

    val a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
      Math.cos(degreeToRadians(lat1)) * Math.cos(degreeToRadians(lat2)) *
      Math.sin(longDiff / 2) * Math.sin(longDiff / 2)

    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    earthRadiusKm * c
  }

  private def degreeToRadians(degree: Double): Double = degree * (Math.PI / 180)

  private val earthRadiusKm: Double = 6371
}

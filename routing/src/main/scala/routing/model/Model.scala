package routing.model

case class NodeRow(id: Int, name: String, lat: Float, lon: Float)

case class EdgeRow(id: Int, name: String, firstNode: Int, secondNode: Int)
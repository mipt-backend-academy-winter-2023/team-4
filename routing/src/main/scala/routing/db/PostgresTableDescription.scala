package routing.db

import routing.models.graph.Edge.Street
import routing.models.graph.Node.{Building, Intersection}
import zio.schema.DeriveSchema
import zio.sql.postgresql.PostgresJdbcModule

trait PostgresTableDescription extends PostgresJdbcModule {

  implicit val buildingSchema = DeriveSchema.gen[Building]
  val buildings = defineTable[Building]("buildings")
  val (buildingId, buildingName, buildingLat, buildingLong) = buildings.columns

  implicit val intersectionSchema = DeriveSchema.gen[Intersection]
  val intersections = defineTable[Intersection]("intersections")
  val (intersectionId, intersectionLat, intersectionLong) = intersections.columns

  implicit val streetSchema = DeriveSchema.gen[Street]
  val streets = defineTable[Street]("streets")
  val (streetId, streetName, source, destination) = streets.columns
}

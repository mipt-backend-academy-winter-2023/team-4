import Dependencies.{Auth, Routing, Template}

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "team-4"
  )
  .aggregate(
    template,
    auth,
    routing
  )

lazy val template = (project in file("template"))
  .settings(
    name := "team-4-template",
    libraryDependencies ++= Template.dependencies
  )

lazy val auth = (project in file("auth"))
  .settings(
    name := "team-4-auth",
    libraryDependencies ++= Auth.dependencies
  )

lazy val routing = (project in file("routing"))
  .settings(
    name := "team-4-routing",
    libraryDependencies ++= Routing.dependencies
  )
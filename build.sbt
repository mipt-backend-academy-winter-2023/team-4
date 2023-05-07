import Dependencies.{Auth, Routing, Template}

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}

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
    assembly / mainClass := Some("auth.AuthMain"),
    assembly / assemblyJarName := "auth.jar",
    assembly / assemblyOutputPath := baseDirectory.value / "target" / (assembly / assemblyJarName).value,
    libraryDependencies ++= Auth.dependencies
  )

lazy val routing = (project in file("routing"))
  .settings(
    name := "team-4-routing",
    assembly / mainClass := Some("routing.RoutingMain"),
    assembly / assemblyJarName := "routing.jar",
    assembly / assemblyOutputPath := baseDirectory.value / "target" / (assembly / assemblyJarName).value,
    libraryDependencies ++= Routing.dependencies
  )

import Dependencies.{Auth, Core, Photos, Routing, Template}

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / assemblyMergeStrategy := {
  case x if Assembly.isConfigFile(x) =>
    MergeStrategy.concat
  case PathList(ps @ _*) if Assembly.isReadme(ps.last) || Assembly.isLicenseFile(ps.last) =>
    MergeStrategy.rename
  case PathList("META-INF", xs @ _*) =>
    xs map { _.toLowerCase } match {
      case "manifest.mf" :: Nil | "index.list" :: Nil | "dependencies" :: Nil =>
        MergeStrategy.discard
      case ps @ x :: xs if ps.last.endsWith(".sf") || ps.last.endsWith(".dsa") =>
        MergeStrategy.discard
      case "plexus" :: xs =>
        MergeStrategy.discard
      case "services" :: xs =>
        MergeStrategy.filterDistinctLines
      case "spring.schemas" :: Nil | "spring.handlers" :: Nil =>
        MergeStrategy.filterDistinctLines
      case _ => MergeStrategy.first
    }
  case _ => MergeStrategy.first
}

lazy val root = (project in file("."))
  .settings(
    name := "team-4"
  )
  .aggregate(
    template,
    auth,
    routing,
    photos
  )

lazy val template = (project in file("template"))
  .settings(
    name := "team-4-template",
    libraryDependencies ++= Template.dependencies
  )

lazy val auth = (project in file("auth"))
  .settings(
    name := "team-4-auth",
    libraryDependencies ++= Auth.dependencies,
    assembly / mainClass := Some("auth.AuthMain"),
    assembly / assemblyJarName := "auth.jar"
  )
  .dependsOn(core)

lazy val routing = (project in file("routing"))
  .settings(
    name := "team-4-routing",
    libraryDependencies ++= Routing.dependencies,
    assembly / mainClass := Some("routing.RoutingMain"),
    assembly / assemblyJarName := "routing.jar"
  )
  .dependsOn(core)

lazy val photos = (project in file("photos"))
  .settings(
    name := "team-4-photos",
    libraryDependencies ++= Photos.dependencies,
    assembly / mainClass := Some("photos.PhotosMain"),
    assembly / assemblyJarName := "photos.jar"
  )

lazy val core = (project in file("core"))
  .settings(
    name := "team-4-core",
    libraryDependencies ++= Core.dependencies
  )

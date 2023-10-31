import sbt._

object Libs {

  private object V {
    val zio = "2.0.13"
    val zioHttp = "0.0.5"
    val zioPostgres = "0.1.2"

    val pureconfig = "0.17.3"

    val flyway = "9.17.0"

    val circe = "0.14.5"

    val jwt = "9.2.0"

    val test = "3.2.15"

    val core = "4.0.0-M6"
  }

  val zio: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio" % V.zio,
    "dev.zio" %% "zio-http" % V.zioHttp,
    "dev.zio" %% "zio-sql-postgres" % V.zioPostgres
  )

  val pureconfig: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig" % V.pureconfig
  )

  val flyway: Seq[ModuleID] = Seq(
    "org.flywaydb" % "flyway-core" % V.flyway
  )

  val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core" % V.circe,
    "io.circe" %% "circe-generic" % V.circe,
    "io.circe" %% "circe-parser" % V.circe
  )

  val jwt: Seq[ModuleID] = Seq(
    "com.github.jwt-scala" %% "jwt-json4s-native" % V.jwt
  )

  val test: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.client4" %% "core" % V.core,
    "org.scalatest" %% "scalatest" % V.test % Test
  )
}

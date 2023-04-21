import sbt._

object Libs {

  private object V {
    val zio = "2.0.13"
    val zioHttp = "0.0.5"

    val pureconfig = "0.17.3"
  }

  val zio: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio" % V.zio,
    "dev.zio" %% "zio-http" % V.zioHttp
  )

  val pureconfig: Seq[ModuleID] = Seq(
    "com.github.pureconfig" %% "pureconfig" % V.pureconfig
  )

}

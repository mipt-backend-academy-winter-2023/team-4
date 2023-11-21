import Libs._
import sbt._

trait Dependencies {
  def dependencies: Seq[ModuleID]
}

object Dependencies {

  object Template extends Dependencies {
    override def dependencies: Seq[ModuleID] = Seq(
      zio,
      pureconfig
    ).flatten
  }

  object Auth extends Dependencies {
    override def dependencies: Seq[ModuleID] = Seq(
      zio,
      pureconfig,
      flyway,
      circe,
      jwt
    ).flatten
  }

  object Routing extends Dependencies {
    override def dependencies: Seq[ModuleID] = Seq(
      zio,
      pureconfig,
      test,
      rezilience,
      sttp
    ).flatten
  }

  object Photos extends Dependencies {
    override def dependencies: Seq[ModuleID] = Seq(
      zio,
      pureconfig,
      circe,
      jwt,
      test
    ).flatten
  }

  object Core extends Dependencies {
    override def dependencies: Seq[ModuleID] = Seq(
      zio,
      flyway,
      pureconfig,
      circe
    ).flatten
  }

}

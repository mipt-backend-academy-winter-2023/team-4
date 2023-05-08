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
      circe
    ).flatten
  }

  object Routing extends Dependencies {
    override def dependencies: Seq[ModuleID] = Seq(
      zio,
      pureconfig
    ).flatten
  }

}

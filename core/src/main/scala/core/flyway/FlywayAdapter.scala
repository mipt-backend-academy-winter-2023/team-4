package core.flyway

import core.config.DbConfig
import org.flywaydb.core.Flyway
import zio.{IO, ZIO, ZLayer}

import java.io.IOException

object FlywayAdapter {
  trait Service {
    def migration: IO[IOException, Unit]
  }

  val live: ZLayer[DbConfig, Nothing, FlywayAdapter.Service] =
    ZLayer.fromFunction(new FlywayAdapterImpl(_))
}

class FlywayAdapterImpl(dbConfig: DbConfig) extends FlywayAdapter.Service {
  override def migration: IO[IOException, Unit] = {
    for {
      _ <- ZIO.logInfo("Starting db migration")
      _ <- ZIO.attemptBlockingIO {
        Flyway
          .configure()
          .dataSource(dbConfig.url, dbConfig.user, dbConfig.password)
          .load()
          .migrate()
      }
    } yield ()
  }
}

package auth.api

import model.User
import io.circe.jawn.decode
import zio.ZIO
import zio.http._
import zio.http.model.Method
import zio.http.model.Status.{BadRequest, Created, Unauthorized}
import io.circe.generic.auto.exportDecoder
import repo.UserRepository

import java.security.MessageDigest

object AuthRoutes {

  private def getToken(str: String): String = {
    val md = MessageDigest.getInstance("SHA-256")
    val hash = md.digest(str.getBytes("UTF-8"))
    hash.map("%02x".format(_)).mkString
  }

  val app: HttpApp[UserRepository, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> !! =>
        ZIO.succeed(Response.text("The Auth service is working"))

      case req@Method.POST -> !! / "auth" / "register" =>
        (for {
          bodyStr <- req.body.asString
          user <- ZIO.fromEither(decode[User](bodyStr)).tapError(e => ZIO.logError(e.getMessage))
          _ <- UserRepository.addUser(user)
          _ <- ZIO.logInfo(s"Register $user")
        } yield ()).either.map {
          case Right(_) => Response.status(Created)
          case Left(_) => Response.status(BadRequest)
        }

      case req@Method.POST -> !! / "auth" / "login" =>
        (for {
          bodyStr <- req.body.asString
          user <- ZIO.fromEither(decode[User](bodyStr)).tapError(e => ZIO.logError(e.getMessage))
          entry <- UserRepository.login(user)
        } yield (user, entry)).either.map {
          case Right(result) => Response.text(s"{\"token\": \"${getToken(result._1.username)}\"}")
          case Left(_) => Response.status(Unauthorized)
        }
    }

}

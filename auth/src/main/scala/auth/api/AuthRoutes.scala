package auth.api

import auth.model.User
import auth.repo.UserRepository
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import io.circe.jawn.decode
import zio.ZIO
import zio.http._
import zio.http.model.Method
import zio.http.model.Status.{BadRequest, Conflict, Created, Unauthorized}
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

import java.time.Clock

object AuthRoutes {
  private val salt = "493194919"

  private def generateJWT(username: String): String = {
    val claim = JwtClaim(subject = Some(username))
    Jwt.encode(claim, salt, JwtAlgorithm.HS256)
  }

  val app: HttpApp[UserRepository, Response] =
    Http.collectZIO[Request] {

      case req @ Method.POST -> !! / "auth" / "register" =>
        (for {
          bodyStr <- req.body.asString
          user <- ZIO.fromEither(decode[User](bodyStr)).tapError(e => ZIO.logError(e.getMessage))
          _ <- UserRepository.add(user)
          _ <- ZIO.logInfo(s"Registered user: $user")
        } yield ()).either.map {
          case Right(_) => Response.status(Created)
          case Left(ex) =>
            ex match {
              case _: IllegalAccessException => Response.status(Conflict)
              case _                         => Response.status(BadRequest)
            }
        }

      case req @ Method.POST -> !! / "auth" / "login" =>
        (for {
          bodyStr <- req.body.asString
          user <- ZIO.fromEither(decode[User](bodyStr)).tapError(e => ZIO.logError(e.getMessage))
          entry <- UserRepository.findUser(user).runCollect.map(_.toArray)
        } yield entry).either.map {
          case Right(users) =>
            users match {
              case Array() => Response.status(Unauthorized)
              case arr =>
                ZIO.logInfo(s"Login was successful for ${arr.head}")
                Response.text(s"{\"token\": \"${generateJWT(arr.head.username)}\"}")
            }
          case Left(_) => Response.status(BadRequest)
        }

    }

}

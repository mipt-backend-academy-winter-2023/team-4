package images.helpers

import io.jsonwebtoken.Jwts
import images.helpers.ErrorWithResponse
import zio.http.Response
import zio.http.model.Status

object AuthHelper {
  def validateHeaderOrFinish(header: Option[String]): Any = header match {
    case None => throw ErrorWithResponse(Response.status(Status.BadRequest))
    case Some(value) => validateJwtToken(value) match {
        case false => throw ErrorWithResponse(Response.status(Status.Unauthorized))
        case _     => {}
      }
  }
  def validateJwtToken(token: String): Boolean = {
    val key: String = "secret-key"
    try {
      Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody
      true
    } catch {
      case _: Exception => false
    }
  }
}

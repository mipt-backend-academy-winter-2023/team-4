package images.helpers

import zio.http.Response

case class ErrorWithResponse(msg: Response) extends Exception(msg.toString)

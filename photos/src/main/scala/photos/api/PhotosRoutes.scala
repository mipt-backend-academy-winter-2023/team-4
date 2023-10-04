package photos.api

import zio._
import zio.ZIO
import zio.http._
import zio.http.model.Method
import zio.http.model.Status
import zio.stream.{ZSink, ZStream}

object PhotosRoutes {
    val app: HttpApp[Any, Response] = 
        Http.CollectZIO[Request] {
            case req@Method.POST -> !! / "photo" / id => {
                
            }
        }
}
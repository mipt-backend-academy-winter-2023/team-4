package photos

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client4._
import sttp.client4.httpurlconnection.HttpURLConnectionBackend
import sttp.model._

class PhotosTest extends AnyFlatSpec with Matchers {
  private val msgJPEG: Array[Byte] = Array[Byte](
    0xff.toByte,
    0xd8.toByte,
    0xff.toByte,
    0x31.toByte,
    0x41.toByte,
    0x59.toByte
  )
  implicit val backend: SyncBackend = HttpURLConnectionBackend()

  it should "Uploading of small photo" in {
    val request = basicRequest
      .body(msgJPEG)
      .contentType("image/jpeg")
      .put(uri"http://localhost:7070/photo/1")
    val response = request.send(backend)
    response.code shouldBe StatusCode.Ok
  }

  it should "Get of existed photo" in {
    val request = basicRequest
      .get(uri"http://localhost:7070/photo/1")
    val response = request.send(backend)
    response.code shouldBe StatusCode.Ok
    response.body.isRight shouldBe true
  }

  it should "Long message should be rejected" in {
    val longMsg = msgJPEG ++ Array.fill[Byte](10 * 1024 * 1024)(0)
    val request = basicRequest
      .body(longMsg)
      .contentType("image/jpeg")
      .put(uri"http://localhost:7070/photo/2")
    val response = request.send(backend)
    response.code shouldBe StatusCode.PayloadTooLarge
  }

  it should "Just random bytes should be rejected" in {
    val request = basicRequest
      .body(Array.fill[Byte](1024)(0))
      .contentType("image/jpeg")
      .put(uri"http://localhost:7070/photo/3")
    val response = request.send(backend)
    response.code shouldBe StatusCode.BadRequest
  }

  it should "Get of not existed photo" in {
    val request = basicRequest
      .get(uri"http://localhost:7070/photo/4")
    val response = request.send(backend)
    response.code shouldBe StatusCode.NotFound
  }
}

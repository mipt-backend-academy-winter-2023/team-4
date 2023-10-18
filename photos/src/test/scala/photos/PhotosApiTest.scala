import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import sttp.client3._
import sttp.model._

class PhotosApiTest extends AnyFlatSpec with Matchers {

  implicit val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()

  val testUri = uri"http://localhost:7070/photo/testId"
  val invalidUri = uri"http://localhost:7070/photo/invalidId"

  "Uploading a photo" should "return 200 for successful upload" in {
    val testRequest = basicRequest
      .body(Array[Byte](0xff.toByte, 0xd8.toByte, 0xff.toByte))
      .contentType(MediaType.ImagePng)
      .put(testUri)

    val response = testRequest.send(backend)
    response.code shouldBe StatusCode.Ok
  }

  "Uploading a photo" should "return 400 for not jpeg" in {
    val testRequest = basicRequest
      .body(Array[Byte](1, 2, 3, 4))
      .contentType(MediaType.ImagePng)
      .put(testUri)

    val response = testRequest.send(backend)
    response.code shouldBe StatusCode.BadRequest
  }

  "Downloading a photo" should "return 200 and photo for a valid id" in {
    val testRequest = basicRequest.get(testUri)

    val response = testRequest.send(backend)
    response.code shouldBe StatusCode.Ok
    response.body.isRight shouldBe true
  }

  "Downloading a photo" should "return 404 and photo for an invalid id" in {
    val testRequest = basicRequest.get(invalidUri)

    val response = testRequest.send(backend)
    response.code shouldBe StatusCode.NotFound
  }
}

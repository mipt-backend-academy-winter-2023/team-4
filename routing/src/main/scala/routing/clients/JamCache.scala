package routing.clients

import scala.collection.concurrent.TrieMap
import zio._

class JamCache {
  private val map = new TrieMap[Int, Int]()

  def getByKey(key: Int): ZIO[Any, NoSuchElementException, Int] =
    ZIO.fromOption(map.get(key)).orElseFail(
      new NoSuchElementException(s"No value found for key: $key")
    )

  def update(key: Int, value: Int): Unit =
    map.update(key, value)
}

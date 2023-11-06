package routing.models.errors

case class RouteFindError(message: String) extends RuntimeException(message)

object RouteFindError {
  val InvalidInput: RouteFindError = RouteFindError("Неверный формат ввода")
  val NoSuchNode: RouteFindError = RouteFindError("Введенного узла графа не существует")
}

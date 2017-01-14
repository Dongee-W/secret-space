import scala.io.StdIn
import scala.util.{Failure, Success, Try}

val input = "jewfee ww"
val validInput = Try {
  val action = input.split(" ").head
  val text = input.split(" ")(1)
  (action, text)
}

validInput match {
  case Success(v) => println(v)
  case Failure(f) => println(f)
}
package lib

import org.specs2.Specification
import Strings._

class RichStringSpec extends Specification {
  def is = "trimSpace trims non-breaking space from end of string" ! {
    "hello\u00A0".trimSpace mustEqual "hello"
  } ^ "trimSpace trims non-breaking space from beginning of string" ! {
    "\u00A0hello".trimSpace mustEqual "hello"
  }
}

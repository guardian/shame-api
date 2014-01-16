package lib

object Strings {
  implicit class RichString(val string: String) {
    /** quick (to write) & shitty, inefficient method for trimming non-breaking spaces */
    def trimSpace = string.dropWhile(_.isSpaceChar).reverse.dropWhile(_.isSpaceChar).reverse
  }
}

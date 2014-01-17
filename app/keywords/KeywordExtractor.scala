package keywords

import scala.annotation.tailrec
import lib.SidebarElement

trait KeywordExtractor {

  def apply(title: String, altText: String): List[String] = {
    val words = toWords(title) ::: toWords(altText)
    extractNames(words)
  }

  def apply(sidebar: SidebarElement): List[String] = {
    val words = toWords(sidebar.linkText) ::: toWords(sidebar.altText)
    extractNames(words)
  }

  protected def extractNames(words: List[String]): List[String] = {
    @tailrec
    def chunkNames(chunk: List[String], words: List[String], names: List[List[String]]): List[List[String]] = words match {
      case Nil => names

      case head :: tail if head.headOption exists {_.isUpper} =>
        chunkNames(chunk :+ head, tail, names)

      case head :: tail =>
        val newNames = if(chunk.isEmpty) names else chunk :: names
        chunkNames(Nil, tail, newNames)
    }

    chunkNames(Nil, words, Nil).sortBy(_.size) map {
      names =>
        names mkString " "
    }
  }

  protected def toWords(s: String): List[String] = {
    val words = s split """\W""" // matches on Not Word characters
    words.toList
  }

}
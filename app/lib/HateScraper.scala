package lib

import scala.collection.JavaConversions._
import org.jsoup.Jsoup
import play.api.libs.ws.WS
import scala.concurrent.{ExecutionContext, Future}
import Strings._

case class SidebarElement(linkText: String, altText: String)

object HateScraper {
  val DailyMailFrontPage = "http://www.dailymail.co.uk/home/index.html"

  def scrape(implicit executionContext: ExecutionContext): Future[List[SidebarElement]] =
    WS.url(DailyMailFrontPage).get().map(response => extract(response.body))

  def extract(html: String): List[SidebarElement] = {
    val doc = Jsoup.parse(html)

    (doc.select(".femail.item ul.linkro-wocc li").iterator() map { el =>
      SidebarElement(el.select("span.pufftext").text().trimSpace, el.select("img").attr("alt").trimSpace)
    }).toList
  }
}

package lib

import com.gu.openplatform.contentapi.{FutureAsyncApi, Api}

import com.gu.openplatform.contentapi.connection.HttpResponse

import play.api.libs.ws.WS
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import conf.ShameApiConfig
import com.gu.openplatform.contentapi.model.Content

case class Shame(webTitle:String, webUrl: String, standfirst: Option[String], image: Option[String])


object FetchContent {

  def getShameWall : Future[List[Shame]] = {
    getContent(Agent.keywordsFromDM.take(10))

  }

  def getContent(keywords: List[String]): Future[List[Shame]] = {
    val results = Future.sequence(keywords.map { keyword =>
      println(keyword)
      ApiClient.tags.q(keyword).response flatMap { tagsResponse =>
        val tags = tagsResponse.results
        val content = if(tags.nonEmpty) {
          ApiClient.item.itemId(tags(0).id).showElements("image").showFields("all").response map { itemResponse =>
            itemResponse.results.headOption.map(createShame)
          }
        }
        else {
          ApiClient.search.q(keyword).showElements("image").showFields("all").response map { searchResponse =>
            searchResponse.results.headOption.map(createShame)
          }
        }
        content

      }
    })
    results map { _.flatten }
  }

  private def createShame(c: Content): Shame = {
    val element = c.elements.flatMap(_.headOption)
    val imageUrl = element.flatMap(_.assets.head.file)

    Shame(c.webTitle, c.webUrl, c.fields.flatMap(_.get("standfirst")), imageUrl)
  }

}

object ApiClient extends FutureAsyncApi {

  apiKey = ShameApiConfig.apiKey
  override val targetUrl = ShameApiConfig.targetUrl.getOrElse("http://content.guardianapis.com")

  override protected def fetch(url: String, parameters: scala.collection.immutable.Map[String, String]) =
    super.fetch(url, parameters + ("user-tier" -> ShameApiConfig.userTier.getOrElse("free")))


  def GET(urlString: String, headers: Iterable[(String, String)] = Nil): Future[HttpResponse] =
    WS.url(urlString).withHeaders(headers.toSeq: _*).get().map(r => HttpResponse(r.body, r.status, r.statusText))

  implicit def executionContext: ExecutionContext = ExecutionContext.Implicits.global

}
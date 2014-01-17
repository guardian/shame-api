package lib

import com.gu.openplatform.contentapi.{FutureAsyncApi, Api}

import com.gu.openplatform.contentapi.connection.HttpResponse

import play.api.libs.ws.WS
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import conf.ShameApiConfig
import com.gu.openplatform.contentapi.model.Content

case class Shame(id: String, webTitle:String, webUrl: String, standfirst: String, image: String, dmKeyword: String)


object FetchContent {

  def getShameWall : Future[List[Shame]] = {
    getContent(Agent.keywordsFromDM.take(30))

  }

  def getContent(keywords: List[String]): Future[List[Shame]] = {
    val results = Future.sequence(keywords.map { keyword =>
      ApiClient.tags.q(keyword).response flatMap { tagsResponse =>
        val tags = tagsResponse.results
        val content = if(tags.nonEmpty) {
          ApiClient.item.itemId(tags(0).id).showElements("image").showFields("all").response map { itemResponse =>
            itemResponse.results.headOption.flatMap(createShame(_, keyword))
          }
        }
        else {
          ApiClient.search.q(keyword).showElements("image").showFields("all").response map { searchResponse =>
            searchResponse.results.headOption.flatMap(createShame(_, keyword))
          }
        }
        content

      }
    })
    results map { _.flatten.distinct }
  }

  private def createShame(c: Content, keyword: String): Option[Shame] = {
    for {
      standfirst <- c.safeFields.get("standfirst")
      imageUrl <- c.elements.flatMap(_.headOption).flatMap(_.assets.head.file)
    } yield Shame(c.id, c.webTitle, c.webUrl, standfirst, imageUrl, keyword)
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
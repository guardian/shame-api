package lib

import com.gu.openplatform.contentapi.{FutureAsyncApi, Api}

import com.gu.openplatform.contentapi.connection.HttpResponse

import play.api.libs.ws.WS
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

case class Shame(webTitle:String, webUrl: String, standfirst: String, thumbnail: String)


object FetchContent {

  def getShameWall : Future[List[Shame]] = {
    //get keywords -- todo
    val testKeywords = List("harry styles")
    getContent(testKeywords)

  }

  def getContent(keywords: List[String]): Future[List[Shame]] = {
    val results = Future.sequence(keywords.map { keyword =>
      ApiClient.tags.q(keyword).response flatMap { tagsResponse =>
        val tags = tagsResponse.results
        val content = if(tags.nonEmpty) {
          ApiClient.item.itemId(tags(0).id).showElements("image").showFields("standfirst,thumbnail").response map { itemResponse =>
            itemResponse.results.headOption.map(r =>
              Shame(r.webTitle, r.webUrl, r.fields.get("standfirst"), r.fields.get("thumbnail")))
          }
        }
        else {
          ApiClient.search.q(keyword).showElements("image").showFields("all").response map { searchResponse =>
            searchResponse.results.headOption.map(r =>
              Shame(r.webTitle, r.webUrl, r.fields.get("standfirst"), r.fields.get("thumbnail")))
          }
        }
        content

      }
    })
    results map { _.flatten }
  }

}

object ApiClient extends FutureAsyncApi {

  override protected def fetch(url: String, parameters: scala.collection.immutable.Map[String, String]) =
    super.fetch(url, parameters + ("api-key" -> "jenny"))


  def GET(urlString: String, headers: Iterable[(String, String)] = Nil): Future[HttpResponse] =
    WS.url(urlString).withHeaders(headers.toSeq: _*).get().map(r => HttpResponse(r.body, r.status, r.statusText))

  implicit def executionContext: ExecutionContext = ExecutionContext.Implicits.global

}
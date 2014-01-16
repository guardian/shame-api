package lib

import com.gu.openplatform.contentapi.{FutureAsyncApi, Api}

import com.gu.openplatform.contentapi.connection.HttpResponse

import play.api.libs.ws.WS
import scala.concurrent.{ExecutionContext, Future}

case class Shame(webTitle:String, standfirst: String, thumbnail: String)


class FetchContent {


  def getShameWall(keywords: List[String])(implicit executionContext: ExecutionContext): Future[List[Shame]] = {

    val testKeywords = List("harry styles", "fifty shades of grey")

    val results = Future.sequence(testKeywords.map { keyword =>
      ApiClient.tags.q(keyword).response flatMap { tagsResponse =>
        val tags = tagsResponse.results
        val content = if(tags.nonEmpty) {
          ApiClient.item.itemId(tags(0).id).showElements("image").response map { itemResponse =>
            itemResponse.results.headOption.map(r => Shame(r.webTitle, r.fields.get("standfirst"), r.fields.get("thumbnail")))
          }
        }
        else {
          ApiClient.search.q(keyword).response map { searchResponse =>
            searchResponse.results.headOption.map(r => Shame(r.webTitle, r.fields.get("standfirst"), r.fields.get("thumbnail")))
          }
        }
        content

      }
    })
    results map { _.flatten }
  }

}

object ApiClient extends FutureAsyncApi {

  implicit val executionContext = scala.concurrent.ExecutionContext.global


  override protected def fetch(url: String, parameters: scala.collection.immutable.Map[String, String]) =
    super.fetch(url, parameters + ("api-key" -> "jenny"))


  def GET(urlString: String, headers: Iterable[(String, String)] = Nil): Future[HttpResponse] =
    WS.url(urlString).withHeaders(headers.toSeq: _*).get().map(r => HttpResponse(r.body, r.status, r.statusText))

}
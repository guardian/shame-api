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
    results map { _.flatten.distinctBy(_.id).toList }
  }

  implicit class RichSeq[A](seq: Seq[A]) {
    def distinctBy[B](f: A => B) = seq.groupBy(f).values.toSeq.map(_.head)
  }

  private def createShame(c: Content, keyword: String): Option[Shame] = {
    val element = c.elements.flatMap(_.find(_.relation == "main"))

    val filteredAssets = element.map(e => e.assets.filter( a => a.typeData.get("width").isDefined))
    val asset = filteredAssets.flatMap(a => a.find(_.typeData("width") == "140")).orElse(filteredAssets.map(_.head))

    val sortedAssets = filteredAssets.map(_.sortBy( a => a.typeData("width").toInt))
    sortedAssets.map(asset => asset.find( a=> a.typeData("width").toInt >= 140))

    for {
      standfirst <- c.safeFields.get("standfirst")
      imageUrl <- asset.flatMap(_.file)
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
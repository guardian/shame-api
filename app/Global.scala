import keywords.KeywordExtractor
import lib.{Agent, HateScraper}
import management.ShameApiManagementServer
import play._
import play.api.libs.concurrent.Akka
import scala.concurrent.duration._
import play.api.Play.current
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global


class Global extends GlobalSettings {


  override def onStart(app: Application) {
    val keywordExtract = new KeywordExtractor {}
    Akka.system.scheduler.schedule(0 seconds, 30 seconds) {
      val futureKeywords = HateScraper.scrape.map(_.map(keywordExtract.apply).flatten)
      futureKeywords.map( x => Agent.keywordsFromDM = x)
    }
    ShameApiManagementServer.start()
  }

  override def onStop(app: Application) {
    ShameApiManagementServer.stop()
  }
}
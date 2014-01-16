package management

import com.gu.management.internal.{ManagementServer, ManagementHandler}
import com.gu.management.{HealthcheckManagementPage, ManifestPage, ManagementPage}
import com.gu.management.logback.LogbackLevelPage

object ShameApiManagementServer {
  private val handler = new ManagementHandler {
    val applicationName: String = "shame-api"

    def pages: List[ManagementPage] = List(
      new ManifestPage(),
      new LogbackLevelPage(applicationName),
      new HealthcheckManagementPage()
    )
  }

  def start() = ManagementServer.start(handler)

  def stop() = ManagementServer.shutdown()
}
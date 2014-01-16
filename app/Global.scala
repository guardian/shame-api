import management.ShameApiManagementServer
import play._

class Global extends GlobalSettings {
  override def onStart(app: Application) {
    ShameApiManagementServer.start()
  }

  override def onStop(app: Application) {
    ShameApiManagementServer.stop()
  }
}

package controllers

import play.api.mvc._
import lib.{Shame, FetchContent}
import play.api.libs.json._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import play.api.libs.Jsonp


object Application extends Controller {

  implicit val shame = Json.format[Shame]

  def index = Action.async { implicit request =>
    FetchContent.getShameWall.map { shames =>
      request.getQueryString("callback").map { callback =>
        Ok(Jsonp(callback, Json.toJson(shames)))
      }.getOrElse {
        Ok(Json.toJson(shames))
      }
    }
  }
}

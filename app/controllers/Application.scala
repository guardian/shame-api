package controllers

import play.api.mvc._
import lib.{Shame, FetchContent}
import play.api.libs.json._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global


object Application extends Controller {

  implicit val shame = Json.format[Shame]

  def index = Action.async {
    FetchContent.getShameWall.map { shames =>
      Ok(Json.toJson(shames))

    }
  }



}
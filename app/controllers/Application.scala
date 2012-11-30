package controllers

import play.api._
import play.api.mvc._
import models._
import play.api.libs.json.Json

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def randomWords(count: Int = 10) = Action { implicit request =>
    import models.Words._

    Ok(Json.toJson(Words.defaultWords))
  }

}
package controllers

import play.api._
import play.api.mvc._

import models._
import play.api.libs.json.Json
import utils._

import play.api.libs.concurrent.Execution.Implicits._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def randomWords(lang: String = "en") = Action { implicit request =>

    import models.Words._

    Async {
      Words.findWords("ordinateur" :: "carte" :: "avion" :: "bottes" :: "culturisme" :: "pince" :: "loutre" :: "choucroute" :: "abeille" :: "cravate" :: Nil, lang).map { words =>
        Ok(Json.toJson(words))
      }
    }
  }

  def translate(lang: String, word: String) = Action {
    Async {
      GoogleTranslator.find(lang, word).map{ json =>
        Ok(json)
      }
    }
  }

  def tts(lang: String, word: String) = Action {
    Ok(GoogleTranslator.textToSpeech(lang, word))
  }

  def searchWikipediaPage(lang: String, word: String) = Action {
    Async {
      Wikipedia.find(lang, word).map{ json =>
        Ok(json)
      }
    }
  }

  def searchWikipediaImages(lang: String, title: String) = Action {
    Async {
      Wikipedia.findImage(lang, title).map{ json =>
        Ok(json)
      }
    }
  }

  def searchWiktionaryPage(lang: String, title: String) = Action {
    Async {
      Wiktionary.find(lang, title).map{ json =>
        Ok(json)
      }
    }
  }
}
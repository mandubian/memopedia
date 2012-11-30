package utils

import play.api.libs.ws.WS
import play.api.libs.json._
import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits._

object GoogleTranslator {
  
  def find(lang: String, word: String): Future[JsValue] = {
    val GOOGLEKEY = "AIzaSyCPTBRnI4eauBgUvFcNdbr_z3-9IcTUzyc"
    WS.url(
      s"https://www.googleapis.com/language/translate/v2?key=$GOOGLEKEY&target=$lang&q=$word"
    ).get().map(_.json)
  }

  def textToSpeech(lang: String, word: String): String = {
    s"http://translate.google.com/translate_tts?tl=$lang&q=$word"
  }
}

object Wikipedia {
  def find(lang: String, word: String): Future[JsValue] = {
    WS.url(
      s"http://$lang.wikipedia.org/w/api.php?format=json&action=query&list=search&srsearch=$word"
    ).get().map( resp =>
      ((resp.json \ "query" \ "search").as[JsArray]).apply(0)
    )
  }

  def findImage(lang: String, title: String): Future[JsArray] = {
    WS.url(
      s"http://$lang.wikipedia.org/w/api.php?format=json&action=query&titles=$title&prop=images"
    ).get().map{ resp =>
      val obj = (resp.json \ "query" \ "pages").as[JsObject]

      (obj.fields(0)._2.as[JsObject] \ "images").as[JsArray]
    }
  }
}

object Wiktionary {
  def find(lang: String, word: String): Future[JsValue] = {
    WS.url(
      s"http://$lang.wiktionary.org/w/api.php?format=json&action=query&list=search&srsearch=$word"
    ).get().map( resp =>
      (resp.json \ "query" \ "search").as[JsArray]      
    )
  }
}
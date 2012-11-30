package utils

import play.api.libs.ws.WS
import play.api.libs.json._
import scala.concurrent.Future

import java.net.URLEncoder

import play.api.libs.concurrent.Execution.Implicits._

object GoogleTranslator {

  def find(lang: String, word: String): Future[String] = {
    val GOOGLEKEY = "AIzaSyCPTBRnI4eauBgUvFcNdbr_z3-9IcTUzyc"
    WS.url(
      s"https://www.googleapis.com/language/translate/v2?key=$GOOGLEKEY&target=$lang&q=$word"
    ).get().map( first =>
      ((first.json \ "data" \ "translations").as[JsArray].apply(0) \ "translatedText").as[String]
      )
  }

  def textToSpeech(lang: String, word: String): String = {
    s"http://translate.google.com/translate_tts?tl=$lang&q=$word"
  }
}

object Wikipedia {
  def find(lang: String, word: String): Future[JsValue] = {
    val url = s"""http://$lang.wikipedia.org/w/api.php?format=json&action=query&list=search&srsearch=${URLEncoder.encode(word, "UTF-8")}"""
    WS.url(url)
    .get().map( resp =>
      ((resp.json \ "query" \ "search").as[JsArray]).apply(0)
    )
  }

  def findImage(lang: String, title: String): Future[JsValue] = {
    WS.url(
      s"""http://$lang.wikipedia.org/w/api.php?format=json&action=query&titles=${URLEncoder.encode(title, "UTF-8")}&prop=images"""
    ).get().flatMap{ resp =>
      val obj = (resp.json \ "query" \ "pages").as[JsObject]
      println(title)
      val img = URLEncoder.encode((obj.fields(0)._2.as[JsObject] \ "images" \\ "title")(0).as[String], "UTF-8")
      println(img)
      WS.url(
        s"http://$lang.wikipedia.org/w/api.php?format=json&action=query&titles=$img&prop=imageinfo&iiprop=url"
        ).get().map { resp =>
          val obj = (resp.json \ "query" \ "pages").as[JsObject]
          val link = (obj.fields(0)._2 \ "imageinfo" \\ "url").last
          //println(link)
          link
        }
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

object Wordnik {

  val wordnikApi = "a2e27ac86edd2ca27400c043ef70491740229dd554e0e6dc6"

  def randomWords(count: Int = 10) : Future[List[String]] = {
    WS.url(s"http://api.wordnik.com/v4/words.json/randomWords?limit=$count&minDictionaryCount=20")
      .withHeaders("api_key" -> wordnikApi)
      .get.map { resp =>
        (Json.parse(resp.body) \\ "word").toList.map(_.as[String])
      }
  }
}
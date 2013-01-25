package utils

import play.api.libs.ws.WS
import play.api.libs.json._
import scala.concurrent.Future

import java.net.URLEncoder

import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play
import java.lang.RuntimeException
import scala.RuntimeException

object GoogleTranslator {

  def find(lang: String, word: String): Future[String] = {
    val GOOGLEKEY = "AIzaSyCPTBRnI4eauBgUvFcNdbr_z3-9IcTUzyc"
    WS.url(
      s"https://www.googleapis.com/language/translate/v2?key=$GOOGLEKEY&target=$lang&q=$word"
    ).get().map{ response =>
      if(response.status != 200) throw new RuntimeException(response.body)
      ((response.json \ "data" \ "translations").as[JsArray].apply(0) \ "translatedText").as[String]
    }
  }

  def textToSpeech(lang: String, word: String): String = {
    s"http://translate.google.com/translate_tts?tl=$lang&q=$word"
  }
}

object WordReference {
  val apiVersion = "0.8"
  val wordReferenceApiKey = Play.current.configuration.getString("wordreferenceApiKey").get
  def find(word: String, langFrom: String, langTo: String): Future[String] = {
    val dictionary = langFrom+langTo
    println(s"http://api.wordreference.com/$apiVersion/$wordReferenceApiKey/json/$dictionary/$word")
    WS.url(
      s"http://api.wordreference.com/$apiVersion/$wordReferenceApiKey/json/$dictionary/$word"
    ).get().map{ response =>
      if(response.status != 200) throw new RuntimeException(response.body)
      (response.json \ "term0" \ "PrincipalTranslations" \ "0" \ "FirstTranslation" \ "term").as[String]
    }
  }
}

object Wikipedia {
  def find(lang: String, word: String): Future[JsValue] = {
    val url = s"""http://$lang.wikipedia.org/w/api.php?format=json&action=query&list=search&srsearch=${URLEncoder.encode(word, "UTF-8")}"""
    println(url)
    WS.url(url)
    .get().map{ resp =>
      if(resp.status != 200) throw new RuntimeException(resp.body)
      ((resp.json \ "query" \ "search").as[JsArray]).apply(0)
    }
  }

  def findImage(lang: String, title: String): Future[JsValue] = {
    println( s"""http://$lang.wikipedia.org/w/api.php?format=json&action=query&titles=${URLEncoder.encode(title, "UTF-8")}&prop=images""")
    WS.url(
      s"""http://$lang.wikipedia.org/w/api.php?format=json&action=query&titles=${URLEncoder.encode(title, "UTF-8")}&prop=images"""
    ).get().flatMap{ resp =>
      val obj = (resp.json \ "query" \ "pages").as[JsObject]
      val img = URLEncoder.encode((obj.fields(0)._2.as[JsObject] \ "images" \\ "title")(0).as[String], "UTF-8")
      println(s"http://$lang.wikipedia.org/w/api.php?format=json&action=query&titles=$img&prop=imageinfo&iiprop=url")
      WS.url(
        s"http://$lang.wikipedia.org/w/api.php?format=json&action=query&titles=$img&prop=imageinfo&iiprop=url"
        ).get().map { resp =>
        if(resp.status != 200) throw new RuntimeException(resp.body)
        val obj = (resp.json \ "query" \ "pages").as[JsObject]
          (obj.fields(0)._2 \ "imageinfo" \\ "url").last
        }
    }
  }
}

object Wiktionary {
  def find(lang: String, word: String): Future[JsValue] = {
    WS.url(
      s"http://$lang.wiktionary.org/w/api.php?format=json&action=query&list=search&srsearch=$word"
    ).get().map{ resp =>
      if(resp.status != 200) throw new RuntimeException(resp.body)
      (resp.json \ "query" \ "search").as[JsArray]
    }
  }
}

object Wordnik {

  val wordnikApi = "a2e27ac86edd2ca27400c043ef70491740229dd554e0e6dc6"

  def randomWords(count: Int = 10) : Future[List[String]] = {
    WS.url(s"http://api.wordnik.com/v4/words.json/randomWords?limit=$count&minDictionaryCount=20")
      .withHeaders("api_key" -> wordnikApi)
      .get.map { resp =>
      if(resp.status != 200) throw new RuntimeException(resp.body)
      (Json.parse(resp.body) \\ "word").toList.map(_.as[String])
      }
  }
}
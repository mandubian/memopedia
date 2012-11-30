package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.ws.WS
import utils._
import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits._

object Words {

  implicit val wordsFormat = Json.format[Word]


  def findWords(words: List[String], lang: String = "fr") : Future[List[Word]] = {
    val all = words.map { wordStr =>
        //println(wordStr)
        val rdy : List[Future[String]] = List(

          GoogleTranslator.find(lang, wordStr).flatMap { trad =>
            //println("trad : "+((trad \ "data" \ "translations").as[JsArray].apply(0) \ "translatedText").as[String])
            GoogleTranslator.find(lang, trad)
          },

          Wikipedia.find("fr", wordStr).flatMap { article =>
            println(article \ "title")
            Wikipedia.findImage("fr", (article \ "title").as[String]).map(_.as[String])
          },

          GoogleTranslator.find(lang, wordStr).map {
            GoogleTranslator.textToSpeech(lang, _)
          }
          )

        Future.sequence(rdy).map { res =>
          Word(wordStr, res(0), res(1), res(2))
        }
      }
      Future.sequence(all)
  }

  def defaultWords =
    Word("briquet", "lighter", "lighter.jpeg") ::
    Word("ruban", "ribbon", "ribbon.jpeg") ::
    Word("boîte aux lettres", "post box", "postbox.jpeg") ::
    Word("cerf", "deer", "deer.jpeg") ::
    Word("menton", "chin", "chin.jpeg") ::
    Nil
}

case class Word(word: String, trad: String, url: String, speech: String = "")



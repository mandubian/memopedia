package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.ws.WS
import utils._
import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits._

object Words {

  implicit val wordsFormat = Json.format[Word]


  def findWords(words: List[String], langFrom: String, langTo: String) : Future[List[Word]] = {
    val all = words.map { wordStr =>

        val rdy : List[Future[String]] = List(
            WordReference.find(wordStr, langFrom, langTo),
            Wikipedia.find(langFrom, wordStr).flatMap { article =>
              Wikipedia.findImage(langFrom, (article \ "title").as[String]).map(_.as[String])
            }
        )

        Future.sequence(rdy).map { res =>
          Word(wordStr, res(0), res(1))
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



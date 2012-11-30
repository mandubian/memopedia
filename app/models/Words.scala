package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.ws.WS

import play.api.libs.concurrent.Execution.Implicits._

object Words {

  implicit val wordsFormat = Json.format[Word]

  val wordnikApi = "a2e27ac86edd2ca27400c043ef70491740229dd554e0e6dc6"

  def randomWords(count: Int = 10) = {
    WS.url(s"http://api.wordnik.com/v4/words.json/randomWords?limit=$count&minCorpusCount=100")
      .withHeaders("api_key" -> wordnikApi)
      .get.map { resp =>
        Json.parse(resp.body) \\ "word"
      }
  }

  def defaultWords =
    Word("briquet", "lighter", "lighter.jpeg") ::
    Word("ruban", "ribbon", "ribbon.jpeg") ::
    Word("boîte aux lettres", "post box", "postbox.jpeg") ::
    Word("cerf", "deer", "deer.jpeg") ::
    Word("menton", "chin", "chin.jpeg") ::
    Nil
}

case class Word(word: String, trad: String, url: String)



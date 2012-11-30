package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

object Words {

  implicit val wordsFormat = Json.format[Word]


  def defaultWords =
    Word("briquet", "lighter", "lighter.jpeg") ::
    Word("ruban", "ribbon", "ribbon.jpeg") ::
    Word("boîte aux lettres", "post box", "postbox.jpeg") ::
    Word("cerf", "deer", "deer.jpeg") ::
    Word("menton", "chin", "chin.jpeg") ::
    Nil
}

case class Word(word: String, trad: String, url: String)


